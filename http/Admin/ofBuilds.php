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

$msg = "";

// =========================
// ENCOLAR BUILD
// =========================

if (isset($_POST['queueBuild'])) {
    $requestedBy = 0;

    if (isset($cookie_name) && $cookie_name != "") {
        $safeCookieName = mysql_real_escape_string($cookie_name);
        $sqlUser = "SELECT player_id FROM weblm_players WHERE name='$safeCookieName' LIMIT 1";
        $resUser = mysql_query($sqlUser);

        if ($rowUser = mysql_fetch_assoc($resUser)) {
            $requestedBy = intval($rowUser['player_id']);
        }
    }

    $buildId = ofQueueBuild($requestedBy);

    if ($buildId) {
        $msg = "Build queued successfully. Build ID: " . $buildId;
    } else {
        $msg = "Error queueing build.";
    }
}

// =========================
// HELPERS
// =========================

function ofBuildStatusLabel($status) {
    $status = trim(strtolower($status));

    if ($status == "pending") return "Pending";
    if ($status == "processing") return "Processing";
    if ($status == "ready") return "Ready";
    if ($status == "failed") return "Failed";

    return ucfirst($status);
}

function ofRenderDownloadLink($buildId, $type, $label, $absolutePath) {
    if (!$absolutePath) {
        return "-";
    }

    if (!file_exists($absolutePath)) {
        return htmlspecialchars($label) . " (missing)";
    }

    $url = "downloadBuildFile.php?id=" . intval($buildId) . "&type=" . urlencode($type);

    return '<a href="' . htmlspecialchars($url) . '" target="_blank">' . htmlspecialchars($label) . '</a>';
}

?>
<html>
<head>
    <title>OF Builds</title>
</head>
<body>

<?php include("menu.php"); ?>

<h2>Option File - Builds</h2>

<?php if (!empty($msg)) { ?>
    <p><b><?php echo htmlspecialchars($msg); ?></b></p>
<?php } ?>

<h3>Queue new build</h3>

<form method="post">
    <input type="hidden" name="queueBuild" value="1" />
    <input type="submit" value="Queue New Build" />
</form>

<hr>

<h3>Build History</h3>

<table border="1" cellpadding="5">
<tr>
    <th>ID</th>
    <th>Version</th>
    <th>Status</th>
    <th>Requested By</th>
    <th>OPT</th>
    <th>DB</th>
    <th>Manifest</th>
    <th>OPT Path</th>
    <th>Created At</th>
    <th>Finished At</th>
</tr>

<?php
$res = ofGetBuilds();
while ($row = mysql_fetch_assoc($res)) {
?>
<tr>
    <td><?php echo intval($row['id']); ?></td>
    <td><?php echo htmlspecialchars($row['version']); ?></td>
    <td><?php echo htmlspecialchars(ofBuildStatusLabel($row['status'])); ?></td>
    <td><?php echo intval($row['requested_by']); ?></td>
    <td><?php echo ofRenderDownloadLink($row['id'], 'opt', 'Download OPT', $row['opt_path']); ?></td>
    <td><?php echo ofRenderDownloadLink($row['id'], 'db', 'Download DB', $row['db_path']); ?></td>
    <td><?php echo ofRenderDownloadLink($row['id'], 'manifest', 'View Manifest', $row['manifest_path']); ?></td>
    <td><?php echo htmlspecialchars($row['opt_path']); ?></td>
    <td><?php echo htmlspecialchars($row['created_at']); ?></td>
    <td><?php echo htmlspecialchars($row['finished_at']); ?></td>
</tr>
<?php } ?>

</table>

</body>
</html>
