<?php

$page = "ofMarket";

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
// CREAR ORDEN MANUAL
// =========================

if (isset($_POST['seller_profile_id']) && isset($_POST['pesPlayerId']) && isset($_POST['price'])) {
    $sellerId = intval($_POST['seller_profile_id']);
    $playerId = intval($_POST['pesPlayerId']);
    $price = intval($_POST['price']);

    if ($sellerId > 0 && $playerId > 0 && $price > 0) {
        $ok = ofCreateOrder($sellerId, $playerId, $price);
        $msg = $ok ? "Market order created." : "Error creating market order.";
    } else {
        $msg = "Invalid data.";
    }
}

// =========================
// USUARIOS
// =========================

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
    <title>OF Market</title>
</head>
<body>

<?php include("menu.php"); ?>

<h2>Option File - Market</h2>

<?php if (!empty($msg)) { ?>
    <p><b><?php echo htmlspecialchars($msg); ?></b></p>
<?php } ?>

<h3>Create Manual Market Order</h3>

<form method="post">
<table border="0" cellpadding="5">

<tr>
    <td>Seller</td>
    <td>
        <select name="seller_profile_id">
            <option value="0">-- Select Seller --</option>
            <?php foreach ($players as $p) { ?>
                <option value="<?php echo intval($p['profile_id']); ?>">
                    <?php echo htmlspecialchars($p['name']); ?>
                </option>
            <?php } ?>
        </select>
    </td>
</tr>

<tr>
    <td>PES Player ID</td>
    <td>
        <input type="text" name="pesPlayerId" value="" />
    </td>
</tr>

<tr>
    <td>Price</td>
    <td>
        <input type="text" name="price" value="" />
    </td>
</tr>

<tr>
    <td colspan="2">
        <input type="submit" value="Create Order" />
    </td>
</tr>

</table>
</form>

<hr>

<h3>Open Market Orders</h3>

<table border="1" cellpadding="5">
<tr>
    <th>ID</th>
    <th>Seller Profile ID</th>
    <th>Buyer Profile ID</th>
    <th>PES Player ID</th>
    <th>Price</th>
    <th>Status</th>
    <th>Created At</th>
    <th>Completed At</th>
</tr>

<?php
$res = ofGetMarket();
while ($row = mysql_fetch_assoc($res)) {
?>
<tr>
    <td><?php echo intval($row['id']); ?></td>
    <td><?php echo intval($row['seller_profile_id']); ?></td>
    <td><?php echo intval($row['buyer_profile_id']); ?></td>
    <td><?php echo intval($row['pesPlayerId']); ?></td>
    <td><?php echo intval($row['price']); ?></td>
    <td><?php echo htmlspecialchars($row['status']); ?></td>
    <td><?php echo htmlspecialchars($row['created_at']); ?></td>
    <td><?php echo htmlspecialchars($row['completed_at']); ?></td>
</tr>
<?php } ?>

</table>

</body>
</html>
