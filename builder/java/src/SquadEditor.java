import java.util.List;

public class SquadEditor {

    private TeamOffsetResolver offsetResolver;

    public SquadEditor() {
        this.offsetResolver = new TeamOffsetResolver();
    }

    public void applyTeamPlayers(OptionFile of, int teamId, List<Integer> players) {
        System.out.println("------------------------------------------");
        System.out.println("SquadEditor.applyTeamPlayers()");
        System.out.println("Team ID: " + teamId);
        System.out.println("Players count: " + players.size());
        System.out.println("------------------------------------------");

        validatePlayers(players);

        if (!offsetResolver.hasTeam(teamId)) {
            System.out.println("No offset mapping for team " + teamId + ". Skipping real write.");
            debugKnownAreas(of, teamId, players);
            return;
        }

        int primaryOffset = offsetResolver.getPrimaryOffset(teamId);
        int mirrorOffset = offsetResolver.getMirrorOffset(teamId);

        System.out.println("Resolved offsets:");
        System.out.println("  primary = " + primaryOffset);
        System.out.println("  mirror  = " + mirrorOffset);

        // ==================================================
        // FIRST REAL WRITE STEP
        // ==================================================
        //
        // We write ordered 16-bit player IDs into:
        // - primary offset
        // - mirror offset
        //
        // This is still experimental and depends on correct
        // team mappings / real squad block structure.
        // ==================================================

        writePlayersArray(of, primaryOffset, players);
        writePlayersArray(of, mirrorOffset, players);

        System.out.println("Players written (experimental) for team " + teamId);
    }

    private void writePlayersArray(OptionFile of, int offset, List<Integer> players) {
        for (int i = 0; i < players.size(); i++) {
            int playerId = players.get(i);
            int writeOffset = offset + (i * OptionFileConstants.PLAYER_ID_SIZE);
            of.writeUInt16LE(writeOffset, playerId);
        }
    }

    private void validatePlayers(List<Integer> players) {
        if (players == null) {
            throw new RuntimeException("Players list is null");
        }

        if (players.size() == 0) {
            System.out.println("Warning: team has 0 players");
        }

        if (players.size() > OptionFileConstants.DEFAULT_MAX_SQUAD_PLAYERS) {
            throw new RuntimeException(
                "Too many players for one team: " + players.size() +
                " (max assumed " + OptionFileConstants.DEFAULT_MAX_SQUAD_PLAYERS + ")"
            );
        }

        for (int i = 0; i < players.size(); i++) {
            int playerId = players.get(i);
            if (playerId < 0 || playerId > 65535) {
                throw new RuntimeException(
                    "Invalid player ID at position " + i + ": " + playerId
                );
            }
        }
    }

    private void debugKnownAreas(OptionFile of, int teamId, List<Integer> players) {
        System.out.println("Known checksum/control area:");
        System.out.println(
            "  offset=" + OptionFileConstants.CHECKSUM_OFFSET +
            " length=" + OptionFileConstants.CHECKSUM_LENGTH
        );

        int checksumValue = of.readUInt32LE(OptionFileConstants.CHECKSUM_OFFSET);
        System.out.println("  current UInt32LE value=" + checksumValue);

        System.out.println("Known squad-related blocks:");
        System.out.println(
            "  block1=" + OptionFileConstants.SQUAD_BLOCK_1_START +
            "-" + OptionFileConstants.SQUAD_BLOCK_1_END
        );
        System.out.println(
            "  block2=" + OptionFileConstants.SQUAD_BLOCK_2_START +
            "-" + OptionFileConstants.SQUAD_BLOCK_2_END
        );
        System.out.println(
            "  block3=" + OptionFileConstants.SQUAD_BLOCK_3_START +
            "-" + OptionFileConstants.SQUAD_BLOCK_3_END
        );

        System.out.println("Observed diff regions:");
        System.out.println(
            "  A=" + OptionFileConstants.OBSERVED_DIFF_BLOCK_A_START +
            "-" + OptionFileConstants.OBSERVED_DIFF_BLOCK_A_END
        );
        System.out.println(
            "  B=" + OptionFileConstants.OBSERVED_DIFF_BLOCK_B_START +
            "-" + OptionFileConstants.OBSERVED_DIFF_BLOCK_B_END
        );

        if (!players.isEmpty()) {
            System.out.println("First player to apply: " + players.get(0));
            System.out.println("Last player to apply: " + players.get(players.size() - 1));
        }

        int aSample = of.readByte(OptionFileConstants.OBSERVED_DIFF_BLOCK_A_START);
        int bSample = of.readByte(OptionFileConstants.OBSERVED_DIFF_BLOCK_B_START);

        System.out.println("Observed block samples:");
        System.out.println("  A first byte=" + aSample);
        System.out.println("  B first byte=" + bSample);

        System.out.println("No real squad write performed yet for team " + teamId + ".");
    }
}
