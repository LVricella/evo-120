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
// CURRENT TEAM FILTER
// =========================

$currentTeamId = 0;

if (isset($_GET['sixTeamId'])) {
    $currentTeamId = intval($_GET['sixTeamId']);
} elseif (isset($_POST['sixTeamId'])) {
    $currentTeamId = intval($_POST['sixTeamId']);
}

// =========================
// DELETE SINGLE SLOT
// =========================

if (isset($_POST['delete_slot']) && isset($_POST['delete_order']) && isset($_POST['sixTeamId'])) {
    $sixTeamId = intval($_POST['sixTeamId']);
    $deleteOrder = intval($_POST['delete_order']);

    if ($sixTeamId > 0 && $deleteOrder > 0) {
        $ok = ofDeleteRosterSlot($sixTeamId, $deleteOrder);
        $msg = $ok ? "Roster slot deleted." : "Error deleting roster slot.";
        $currentTeamId = $sixTeamId;
    } else {
        $msg = "Invalid delete request.";
    }
}

// =========================
// CLEAR FULL ROSTER
// =========================

if (isset($_POST['clear_roster']) && isset($_POST['sixTeamId'])) {
    $sixTeamId = intval($_POST['sixTeamId']);

    if ($sixTeamId > 0) {
        $ok = ofClearRoster($sixTeamId);
        $msg = $ok ? "Roster cleared." : "Error clearing roster.";
        $currentTeamId = $sixTeamId;
    } else {
        $msg = "Invalid clear request.";
    }
}

// =========================
// SAVE / UPDATE PLAYER SLOT
// =========================

if (
    isset($_POST['save_slot']) &&
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
        $currentTeamId = $sixTeamId;
    } else {
        $msg = "Invalid data.";
    }
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
    <input type="hidden" name="save_slot" value="1" />
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

<form method="post" onsubmit="return confirm('Are you sure you want to clear the full roster for this team?');">
    <input type="hidden" name="clear_roster" value="1" />
    <input type="hidden" name="sixTeamId" value="<?php echo intval($currentTeamId); ?>" />
    <input type="submit" value="Clear Full Roster" />
</form>

<br>

<table border="1" cellpadding="5">
<tr>
    <th>Squad Order</th>
    <th>PES Player ID</th>
    <th>Slot Type</th>
    <th>Updated At</th>
    <th>Action</th>
</tr>

<?php if (count($currentRoster) > 0) { ?>
    <?php foreach ($currentRoster as $row) { ?>
    <tr>
        <td><?php echo intval($row['squad_order']); ?></td>
        <td><?php echo intval($row['pesPlayerId']); ?></td>
        <td><?php echo htmlspecialchars($row['slot_type']); ?></td>
        <td><?php echo htmlspecialchars($row['updated_at']); ?></td>
        <td>
            <form method="post" style="margin:0;" onsubmit="return confirm('Delete this roster slot?');">
                <input type="hidden" name="delete_slot" value="1" />
                <input type="hidden" name="sixTeamId" value="<?php echo intval($currentTeamId); ?>" />
                <input type="hidden" name="delete_order" value="<?php echo intval($row['squad_order']); ?>" />
                <input type="submit" value="Delete" />
            </form>
        </td>
    </tr>
    <?php } ?>
<?php } else { ?>
    <tr>
        <td colspan="5">No roster data for this team yet.</td>
    </tr>
<?php } ?>

</table>

<?php } ?>

</body>
</html>
