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
    <th>OPT Path</th>
    <th>DB Path</th>
    <th>Manifest Path</th>
    <th>Requested By</th>
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
    <td><?php echo htmlspecialchars($row['status']); ?></td>
    <td><?php echo htmlspecialchars($row['opt_path']); ?></td>
    <td><?php echo htmlspecialchars($row['db_path']); ?></td>
    <td><?php echo htmlspecialchars($row['manifest_path']); ?></td>
    <td><?php echo intval($row['requested_by']); ?></td>
    <td><?php echo htmlspecialchars($row['created_at']); ?></td>
    <td><?php echo htmlspecialchars($row['finished_at']); ?></td>
</tr>
<?php } ?>

</table>

</body>
</html>
