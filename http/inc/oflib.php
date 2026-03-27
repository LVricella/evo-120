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

    mysql_query("UPDATE of_team_assignments SET is_active = 0 WHERE sixTeamId = $sixTeamId");
    mysql_query("UPDATE of_team_assignments SET is_active = 0 WHERE profile_id = $profileId");

    return mysql_query("
        INSERT INTO of_team_assignments (sixTeamId, profile_id, is_active)
        VALUES ($sixTeamId, $profileId, 1)
    ");
}

function ofGetAssignments() {
    return mysql_query("
        SELECT
            ota.*,
            st.name AS team_name,
            wp.name AS player_name
        FROM of_team_assignments ota
        LEFT JOIN six_teams st ON st.sixTeamId = ota.sixTeamId
        LEFT JOIN six_profiles sp ON sp.id = ota.profile_id
        LEFT JOIN weblm_players wp ON wp.player_id = sp.user_id
        WHERE ota.is_active = 1
        ORDER BY ota.sixTeamId ASC
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
// MARKET
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
// SNAPSHOT HELPERS
// =========================

function ofGetAssignmentsMap() {
    $map = array();

    $res = mysql_query("
        SELECT
            ota.sixTeamId,
            ota.profile_id,
            st.name AS team_name,
            wp.name AS player_name
        FROM of_team_assignments ota
        LEFT JOIN six_teams st ON st.sixTeamId = ota.sixTeamId
        LEFT JOIN six_profiles sp ON sp.id = ota.profile_id
        LEFT JOIN weblm_players wp ON wp.player_id = sp.user_id
        WHERE ota.is_active = 1
        ORDER BY ota.sixTeamId ASC
    ");

    while ($row = mysql_fetch_assoc($res)) {
        $teamId = intval($row['sixTeamId']);

        $map[$teamId] = array(
            "profile_id" => intval($row['profile_id']),
            "team_name" => $row['team_name'],
            "player_name" => $row['player_name']
        );
    }

    return $map;
}

function ofGetRosterMap() {
    $teams = array();

    $res = mysql_query("
        SELECT sixTeamId, pesPlayerId, squad_order, slot_type
        FROM of_team_roster
        ORDER BY sixTeamId ASC, squad_order ASC
    ");

    while ($row = mysql_fetch_assoc($res)) {
        $teamId = intval($row['sixTeamId']);

        if (!isset($teams[$teamId])) {
            $teams[$teamId] = array();
        }

        $teams[$teamId][] = array(
            "pesPlayerId" => intval($row['pesPlayerId']),
            "squad_order" => intval($row['squad_order']),
            "slot_type" => $row['slot_type']
        );
    }

    return $teams;
}

function ofGetSimpleTeamsArray() {
    $teams = array();

    $res = mysql_query("
        SELECT sixTeamId, pesPlayerId, squad_order
        FROM of_team_roster
        ORDER BY sixTeamId ASC, squad_order ASC
    ");

    while ($row = mysql_fetch_assoc($res)) {
        $teamId = intval($row['sixTeamId']);

        if (!isset($teams[$teamId])) {
            $teams[$teamId] = array();
        }

        $teams[$teamId][] = intval($row['pesPlayerId']);
    }

    return $teams;
}

// =========================
// EXPORT SNAPSHOT
// =========================

function ofExportSnapshotArray() {
    $teams = ofGetSimpleTeamsArray();
    $assignments = ofGetAssignmentsMap();
    $detailedRoster = ofGetRosterMap();

    return array(
        "meta" => array(
            "generated_at" => date("Y-m-d H:i:s"),
            "generated_unix" => time(),
            "format_version" => 1,
            "generator" => "evo-120"
        ),
        "teams" => $teams,
        "assignments" => $assignments,
        "roster_detail" => $detailedRoster
    );
}

function ofExportSnapshot() {
    return json_encode(ofExportSnapshotArray(), JSON_PRETTY_PRINT);
}
