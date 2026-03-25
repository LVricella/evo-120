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
$teams = [];
$resTeams = mysql_query("SELECT sixTeamId, name FROM six_teams ORDER BY name ASC");
while ($row = mysql_fetch_assoc($resTeams)) {
    $teams[] = $row;
}

// Usuarios
$players = [];
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
    <p><b><?php echo $msg; ?></b></p>
<?php } ?>

<!-- ========================= -->
<!-- FORMULARIO -->
<!-- ========================= -->

<form method="post">
<table border="0" cellpadding="5">

<tr>
    <td>Team</td>
    <td>
        <select name="sixTeamId">
            <option value="0">-- Select Team --</option>
            <?php foreach ($teams as $t) { ?>
                <option value="<?php echo $t['sixTeamId']; ?>">
                    <?php echo htmlspecialchars($t['name']); ?>
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
                <option value="<?php echo $p['profile_id']; ?>">
                    <?php echo htmlspecialchars($p['name']); ?>
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

<!-- ========================= -->
<!-- LISTADO ACTUAL -->
<!-- ========================= -->

<h3>Current Assignments</h3>

<table border="1" cellpadding="5">
<tr>
    <th>Team ID</th>
    <th>Profile ID</th>
</tr>

<?php
$res = ofGetAssignments();
while ($row = mysql_fetch_assoc($res)) {
?>
<tr>
    <td><?php echo $row['sixTeamId']; ?></td>
    <td><?php echo $row['profile_id']; ?></td>
</tr>
<?php } ?>

</table>

</body>
</html>
