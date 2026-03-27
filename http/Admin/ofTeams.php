<?php

$page = "ofTeams";

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
// GUARDAR ASIGNACIÓN
// =========================

if (isset($_POST['sixTeamId']) && isset($_POST['profile_id'])) {

    $sixTeamId = intval($_POST['sixTeamId']);
    $profileId = intval($_POST['profile_id']);

    if ($sixTeamId > 0 && $profileId > 0) {
        $ok = ofAssignTeam($sixTeamId, $profileId);
        $msg = $ok ? "Assignment saved." : "Error saving assignment.";
    } else {
        $msg = "Invalid data.";
    }
}

// =========================
// OBTENER LISTAS
// =========================

// Equipos
$teams = array();
$resTeams = mysql_query("
    SELECT sixTeamId, name
    FROM six_teams
    ORDER BY name ASC
");

while ($row = mysql_fetch_assoc($resTeams)) {
    $teams[] = $row;
}

// Usuarios
$players = array();
$resPlayers = mysql_query("
    SELECT sp.id AS profile_id, wp.name
    FROM six_profiles sp
    LEFT JOIN weblm_players wp ON wp.player_id = sp.user_id
    ORDER BY wp.name ASC
");

while ($row = mysql_fetch_assoc($resPlayers)) {
    $players[] = $row;
}

?>
<html>
<head>
    <title>OF Teams</title>
</head>
<body>

<?php include("menu.php"); ?>

<h2>Option File - Team Assignments</h2>

<?php if (!empty($msg)) { ?>
    <p><b><?php echo htmlspecialchars($msg); ?></b></p>
<?php } ?>

<h3>Assign team to user</h3>

<form method="post">
<table border="0" cellpadding="5">

<tr>
    <td>Team</td>
    <td>
        <select name="sixTeamId">
            <option value="0">-- Select Team --</option>
            <?php foreach ($teams as $t) { ?>
                <option value="<?php echo intval($t['sixTeamId']); ?>">
                    <?php echo htmlspecialchars($t['name']); ?> (ID: <?php echo intval($t['sixTeamId']); ?>)
                </option>
            <?php } ?>
        </select>
    </td>
</tr>

<tr>
    <td>User</td>
    <td>
        <select name="profile_id">
            <option value="0">-- Select User --</option>
            <?php foreach ($players as $p) { ?>
                <option value="<?php echo intval($p['profile_id']); ?>">
                    <?php echo htmlspecialchars($p['name']); ?> (Profile ID: <?php echo intval($p['profile_id']); ?>)
                </option>
            <?php } ?>
        </select>
    </td>
</tr>

<tr>
    <td colspan="2">
        <input type="submit" value="Assign Team" />
    </td>
</tr>

</table>
</form>

<hr>

<h3>Current Assignments</h3>

<table border="1" cellpadding="5">
<tr>
    <th>Team ID</th>
    <th>Team Name</th>
    <th>Profile ID</th>
    <th>User Name</th>
    <th>Created At</th>
</tr>

<?php
$res = ofGetAssignments();
while ($row = mysql_fetch_assoc($res)) {
?>
<tr>
    <td><?php echo intval($row['sixTeamId']); ?></td>
    <td><?php echo htmlspecialchars($row['team_name']); ?></td>
    <td><?php echo intval($row['profile_id']); ?></td>
    <td><?php echo htmlspecialchars($row['player_name']); ?></td>
    <td><?php echo htmlspecialchars($row['created_at']); ?></td>
</tr>
<?php } ?>

</table>

</body>
</html>
