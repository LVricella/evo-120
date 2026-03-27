<?php

$page = "ofRoster";

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
// SAVE / UPDATE PLAYER SLOT
// =========================

if (
    isset($_POST['sixTeamId']) &&
    isset($_POST['pesPlayerId']) &&
    isset($_POST['squad_order'])
) {
    $sixTeamId = intval($_POST['sixTeamId']);
    $pesPlayerId = intval($_POST['pesPlayerId']);
    $squadOrder = intval($_POST['squad_order']);

    if ($sixTeamId > 0 && $pesPlayerId > 0 && $squadOrder > 0) {
        $ok = ofSetPlayer($sixTeamId, $pesPlayerId, $squadOrder);
        $msg = $ok ? "Roster slot saved." : "Error saving roster slot.";
    } else {
        $msg = "Invalid data.";
    }
}

// =========================
// CURRENT TEAM FILTER
// =========================

$currentTeamId = 0;

if (isset($_GET['sixTeamId'])) {
    $currentTeamId = intval($_GET['sixTeamId']);
} elseif (isset($_POST['sixTeamId'])) {
    $currentTeamId = intval($_POST['sixTeamId']);
}

// =========================
// LOAD TEAMS
// =========================

$teams = array();
$resTeams = mysql_query("
    SELECT sixTeamId, name
    FROM six_teams
    ORDER BY name ASC
");

while ($row = mysql_fetch_assoc($resTeams)) {
    $teams[] = $row;
}

// =========================
// LOAD CURRENT ROSTER
// =========================

$currentRoster = array();

if ($currentTeamId > 0) {
    $resRoster = ofGetRoster($currentTeamId);
    while ($row = mysql_fetch_assoc($resRoster)) {
        $currentRoster[] = $row;
    }
}

// =========================
// LOAD ASSIGNMENT INFO
// =========================

$assignmentInfo = null;

if ($currentTeamId > 0) {
    $resAssign = mysql_query("
        SELECT
            ota.*,
            st.name AS team_name,
            wp.name AS player_name
        FROM of_team_assignments ota
        LEFT JOIN six_teams st ON st.sixTeamId = ota.sixTeamId
        LEFT JOIN six_profiles sp ON sp.id = ota.profile_id
        LEFT JOIN weblm_players wp ON wp.player_id = sp.user_id
        WHERE ota.is_active = 1
          AND ota.sixTeamId = " . intval($currentTeamId) . "
        LIMIT 1
    ");

    if ($resAssign && mysql_num_rows($resAssign) > 0) {
        $assignmentInfo = mysql_fetch_assoc($resAssign);
    }
}

?>
<html>
<head>
    <title>OF Roster</title>
</head>
<body>

<?php include("menu.php"); ?>

<h2>Option File - Roster</h2>

<?php if (!empty($msg)) { ?>
    <p><b><?php echo htmlspecialchars($msg); ?></b></p>
<?php } ?>

<h3>Select Team</h3>

<form method="get">
    <select name="sixTeamId">
        <option value="0">-- Select Team --</option>
        <?php foreach ($teams as $t) { ?>
            <option value="<?php echo intval($t['sixTeamId']); ?>" <?php if ($currentTeamId == intval($t['sixTeamId'])) echo 'selected="selected"'; ?>>
                <?php echo htmlspecialchars($t['name']); ?> (ID: <?php echo intval($t['sixTeamId']); ?>)
            </option>
        <?php } ?>
    </select>
    <input type="submit" value="Load Team" />
</form>

<?php if ($currentTeamId > 0) { ?>

<hr>

<h3>Team Info</h3>

<?php if ($assignmentInfo) { ?>
    <p>
        <b>Team:</b> <?php echo htmlspecialchars($assignmentInfo['team_name']); ?>
        <br>
        <b>Owner:</b> <?php echo htmlspecialchars($assignmentInfo['player_name']); ?>
        <br>
        <b>Profile ID:</b> <?php echo intval($assignmentInfo['profile_id']); ?>
    </p>
<?php } else { ?>
    <p><i>No active assignment found for this team.</i></p>
<?php } ?>

<hr>

<h3>Add / Update Roster Slot</h3>

<form method="post">
    <input type="hidden" name="sixTeamId" value="<?php echo intval($currentTeamId); ?>" />

    <table border="0" cellpadding="5">
        <tr>
            <td>Team ID</td>
            <td><?php echo intval($currentTeamId); ?></td>
        </tr>
        <tr>
            <td>PES Player ID</td>
            <td><input type="text" name="pesPlayerId" value="" /></td>
        </tr>
        <tr>
            <td>Squad Order</td>
            <td><input type="text" name="squad_order" value="" /></td>
        </tr>
        <tr>
            <td colspan="2">
                <input type="submit" value="Save Roster Slot" />
            </td>
        </tr>
    </table>
</form>

<hr>

<h3>Current Roster</h3>

<table border="1" cellpadding="5">
<tr>
    <th>Squad Order</th>
    <th>PES Player ID</th>
    <th>Slot Type</th>
    <th>Updated At</th>
</tr>

<?php if (count($currentRoster) > 0) { ?>
    <?php foreach ($currentRoster as $row) { ?>
    <tr>
        <td><?php echo intval($row['squad_order']); ?></td>
        <td><?php echo intval($row['pesPlayerId']); ?></td>
        <td><?php echo htmlspecialchars($row['slot_type']); ?></td>
        <td><?php echo htmlspecialchars($row['updated_at']); ?></td>
    </tr>
    <?php } ?>
<?php } else { ?>
    <tr>
        <td colspan="4">No roster data for this team yet.</td>
    </tr>
<?php } ?>

</table>

<?php } ?>

</body>
</html>
