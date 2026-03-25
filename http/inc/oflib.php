<?php

// =========================
// Option File Library (MLO)
// =========================

// =========================
// Helpers
// =========================

function ofEsc($v) {
    return mysql_real_escape_string($v);
}

// =========================
// TEAM ASSIGNMENTS
// =========================

function ofAssignTeam($sixTeamId, $profileId) {
    $sixTeamId = intval($sixTeamId);
    $profileId = intval($profileId);

    // Desactivar asignaciones previas
    mysql_query("UPDATE of_team_assignments SET is_active = 0 WHERE sixTeamId = $sixTeamId");
    mysql_query("UPDATE of_team_assignments SET is_active = 0 WHERE profile_id = $profileId");

    // Insertar nueva asignación
    return mysql_query("
        INSERT INTO of_team_assignments (sixTeamId, profile_id, is_active)
        VALUES ($sixTeamId, $profileId, 1)
    ");
}

function ofGetAssignments() {
    return mysql_query("
        SELECT *
        FROM of_team_assignments
        WHERE is_active = 1
        ORDER BY sixTeamId ASC
    ");
}

// =========================
// ROSTER
// =========================

function ofGetRoster($sixTeamId) {
    $sixTeamId = intval($sixTeamId);

    return mysql_query("
        SELECT *
        FROM of_team_roster
        WHERE sixTeamId = $sixTeamId
        ORDER BY squad_order ASC
    ");
}

function ofSetPlayer($sixTeamId, $pesPlayerId, $order) {
    $sixTeamId = intval($sixTeamId);
    $pesPlayerId = intval($pesPlayerId);
    $order = intval($order);

    return mysql_query("
        REPLACE INTO of_team_roster (sixTeamId, pesPlayerId, squad_order)
        VALUES ($sixTeamId, $pesPlayerId, $order)
    ");
}

// =========================
// MARKET (base)
// =========================

function ofCreateOrder($sellerId, $playerId, $price) {
    $sellerId = intval($sellerId);
    $playerId = intval($playerId);
    $price = intval($price);

    return mysql_query("
        INSERT INTO of_market_orders (seller_profile_id, pesPlayerId, price)
        VALUES ($sellerId, $playerId, $price)
    ");
}

function ofGetMarket() {
    return mysql_query("
        SELECT *
        FROM of_market_orders
        WHERE status = 'open'
        ORDER BY created_at DESC
    ");
}

// =========================
// BUILDS
// =========================

function ofQueueBuild($requestedBy) {
    $requestedBy = intval($requestedBy);
    $version = date("Ymd_His");

    mysql_query("
        INSERT INTO of_builds (version, status, requested_by)
        VALUES ('" . ofEsc($version) . "', 'pending', $requestedBy)
    ");

    return mysql_insert_id();
}

function ofGetBuilds() {
    return mysql_query("
        SELECT *
        FROM of_builds
        ORDER BY id DESC
    ");
}

// =========================
// EXPORT SNAPSHOT (CLAVE)
// =========================

function ofExportSnapshot() {
    $teams = [];

    $res = mysql_query("
        SELECT sixTeamId, pesPlayerId, squad_order
        FROM of_team_roster
        ORDER BY sixTeamId, squad_order
    ");

    while ($row = mysql_fetch_assoc($res)) {
        $teamId = $row['sixTeamId'];

        if (!isset($teams[$teamId])) {
            $teams[$teamId] = [];
        }

        $teams[$teamId][] = intval($row['pesPlayerId']);
    }

    return json_encode([
        "teams" => $teams
    ]);
}
