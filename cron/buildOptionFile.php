<?php

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

$projectRoot = realpath(dirname(__FILE__) . '/../');
$buildBaseDir = $projectRoot . '/storage/of_builds';
$builderScript = $projectRoot . '/builder/scripts/run-builder.sh';
$baseOptPath = $projectRoot . '/builder/base/KONAMI-WIN32PES6OPT';

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
// RUN BUILDER PLACEHOLDER
// =========================

$builderOk = false;

if (file_exists($builderScript) && file_exists($baseOptPath)) {
    $cmd =
        'bash ' . escapeshellarg($builderScript) . ' ' .
        escapeshellarg($baseOptPath) . ' ' .
        escapeshellarg($jsonPath) . ' ' .
        escapeshellarg($buildDir) . ' 2>&1';

    exec($cmd, $output, $returnCode);

    echo implode("\n", $output) . "\n";

    if ($returnCode === 0 && file_exists($optPath)) {
        $builderOk = true;
    }
}

// =========================
// CREATE PLACEHOLDER DB ZIP
// =========================

if (!file_exists($dbPath)) {
    file_put_contents($dbPath, "");
}

// =========================
// FINAL STATUS
// =========================

$optPathDb = mysql_real_escape_string($optPath);
$dbPathDb = mysql_real_escape_string($dbPath);
$manifestPathDb = mysql_real_escape_string($manifestPath);

if ($builderOk) {
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
} else {
    mysql_query("
        UPDATE of_builds
        SET status = 'failed',
            finished_at = NOW()
        WHERE id = $buildId
    ");

    echo "Build #".$buildId." failed.\n";
}
