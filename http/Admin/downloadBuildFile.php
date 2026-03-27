<?php

$page = "ofBuilds";

include("../config.php");
include("../variables.php");
include("../variablesdb.php");
include("../functions.php");
include("../inc/oflib.php");

include("functions.php");

if (!$isAdminFull) {
    die("Access denied.");
}

if (!isset($_GET['id']) || !isset($_GET['type'])) {
    die("Missing parameters.");
}

$buildId = intval($_GET['id']);
$type = trim($_GET['type']);

if ($buildId <= 0) {
    die("Invalid build id.");
}

$allowedTypes = array(
    'opt' => 'opt_path',
    'db' => 'db_path',
    'manifest' => 'manifest_path'
);

if (!isset($allowedTypes[$type])) {
    die("Invalid file type.");
}

$field = $allowedTypes[$type];

$sql = "
    SELECT id, version, status, opt_path, db_path, manifest_path
    FROM of_builds
    WHERE id = $buildId
    LIMIT 1
";

$res = mysql_query($sql);

if (!$res) {
    die("Database error.");
}

$build = mysql_fetch_assoc($res);

if (!$build) {
    die("Build not found.");
}

$filePath = $build[$field];

if (!$filePath || !file_exists($filePath)) {
    die("File not found.");
}

// seguridad básica: asegurar que esté dentro del proyecto
$realFile = realpath($filePath);
$projectRoot = realpath(dirname(__FILE__) . '/../..');

if ($realFile === false || strpos($realFile, $projectRoot) !== 0) {
    die("Access denied.");
}

$fileName = basename($realFile);

$contentType = "application/octet-stream";

if ($type == "manifest") {
    $contentType = "application/json";
} else if ($type == "opt") {
    $contentType = "application/octet-stream";
} else if ($type == "db") {
    $contentType = "application/zip";
}

header('Content-Description: File Transfer');
header('Content-Type: ' . $contentType);
header('Content-Disposition: attachment; filename="' . $fileName . '"');
header('Content-Length: ' . filesize($realFile));
header('Cache-Control: no-cache, must-revalidate');
header('Pragma: public');
header('Expires: 0');

readfile($realFile);
exit;
