<?php

// ========================================
// buildOptionFile.php
// ========================================
// This script prepares a build snapshot for
// the PES 6 global Option File generator.
//
// Current version:
// - reads roster data from DB
// - exports JSON snapshot
// - creates build folder
// - updates build status
//
// Future version:
// - call Java OF builder
// - generate final KONAMI-WIN32PES6OPT
// - generate DB package / manifest
// ========================================

error_reporting(E_ALL);
ini_set('display_errors', 1);

$appRoot = realpath(dirname(__FILE__)) . '/../http/';

include($appRoot . 'config.php');
include($appRoot . 'variables.php');
include($appRoot . 'variablesdb.php');
include($appRoot . 'functions.php');
include($appRoot . 'inc/oflib.php');

// =========================
// CONFIG
// =========================

$buildBaseDir = realpath(dirname(__FILE__) . '/../') . '/storage/of_builds';

if (!is_dir($buildBaseDir)) {
    mkdir($buildBaseDir, 0777, true);
}

// =========================
// GET NEXT PENDING BUILD
// =========================

$sqlBuild = "
    SELECT *
    FROM of_builds
    WHERE status = 'pending'
    ORDER BY id ASC
    LIMIT 1
";

$resBuild = mysql_query($sqlBuild);

if (!$resBuild) {
    die("Error querying builds: " . mysql_error() . "\n");
}

$build = mysql_fetch_assoc($resBuild);

if (!$build) {
    echo "No pending builds found.\n";
    exit;
}

$buildId = intval($build['id']);
$version = $build['version'];

echo "Processing build #".$buildId." (".$version.")\n";

// =========================
// MARK AS PROCESSING
// =========================

mysql_query("
    UPDATE of_builds
    SET status = 'processing'
    WHERE id = $buildId
");

// =========================
// PREPARE FOLDER
// =========================

$buildDir = $buildBaseDir . '/' . $version;

if (!is_dir($buildDir)) {
    mkdir($buildDir, 0777, true);
}

$jsonPath = $buildDir . '/snapshot.json';
$manifestPath = $buildDir . '/manifest.json';
$optPath = $buildDir . '/KONAMI-WIN32PES6OPT';
$dbPath = $buildDir . '/pes6-db.zip';

// =========================
// EXPORT SNAPSHOT
// =========================

$snapshotJson = ofExportSnapshot();

if (!$snapshotJson) {
    mysql_query("
        UPDATE of_builds
        SET status = 'failed',
            finished_at = NOW()
        WHERE id = $buildId
    ");

    die("Failed exporting snapshot.\n");
}

file_put_contents($jsonPath, $snapshotJson);

// =========================
// CREATE MANIFEST
// =========================

$manifest = array(
    "build_id" => $buildId,
    "version" => $version,
    "created_at" => date("Y-m-d H:i:s"),
    "snapshot_file" => "snapshot.json",
    "opt_file" => "KONAMI-WIN32PES6OPT",
    "db_file" => "pes6-db.zip",
    "status" => "prepared"
);

file_put_contents($manifestPath, json_encode($manifest, JSON_PRETTY_PRINT));

// =========================
// PLACEHOLDER OUTPUT FILES
// =========================
// These files will later be replaced by the
// real Java builder output.

if (!file_exists($optPath)) {
    file_put_contents($optPath, "");
}

if (!file_exists($dbPath)) {
    file_put_contents($dbPath, "");
}

// =========================
// MARK AS READY
// =========================

$optPathDb = mysql_real_escape_string($optPath);
$dbPathDb = mysql_real_escape_string($dbPath);
$manifestPathDb = mysql_real_escape_string($manifestPath);

mysql_query("
    UPDATE of_builds
    SET status = 'ready',
        opt_path = '$optPathDb',
        db_path = '$dbPathDb',
        manifest_path = '$manifestPathDb',
        finished_at = NOW()
    WHERE id = $buildId
");

echo "Build #".$buildId." prepared successfully.\n";
echo "Snapshot: ".$jsonPath."\n";
echo "Manifest: ".$manifestPath."\n";
echo "OPT path: ".$optPath."\n";
echo "DB path: ".$dbPath."\n";
