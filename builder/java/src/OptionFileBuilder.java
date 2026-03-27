import java.util.List;
import java.util.Map;

public class OptionFileBuilder {

    private String basePath;
    private String outputPath;

    public OptionFileBuilder(String basePath, String outputPath) {
        this.basePath = basePath;
        this.outputPath = outputPath;
    }

    public void build(Map<Integer, List<Integer>> teams) throws Exception {
        OptionFile of = new OptionFile(basePath);
        SquadEditor squadEditor = new SquadEditor();
        ChecksumUpdater checksumUpdater = new ChecksumUpdater();
        OptionFileDebugger debugger = new OptionFileDebugger();

        System.out.println("==========================================");
        System.out.println("Option File loaded");
        System.out.println("==========================================");

        debugger.printFileSummary(of);
        debugger.printKnownAreas(of);

        System.out.println("==========================================");
        System.out.println("Applying teams");
        System.out.println("==========================================");

        for (Map.Entry<Integer, List<Integer>> entry : teams.entrySet()) {
            Integer teamId = entry.getKey();
            List<Integer> players = entry.getValue();

            System.out.println("Applying team " + teamId + " with " + players.size() + " players");

            squadEditor.applyTeamPlayers(of, teamId, players);
        }

        checksumUpdater.update(of);

        System.out.println("==========================================");
        System.out.println("Debugging observed regions after writes");
        System.out.println("==========================================");

        debugger.dumpUInt16LE(of, OptionFileConstants.OBSERVED_DIFF_BLOCK_A_START, 16);
        debugger.dumpUInt16LE(of, OptionFileConstants.OBSERVED_DIFF_BLOCK_B_START, 16);

        of.save(outputPath);

        System.out.println("==========================================");
        System.out.println("Option File saved to: " + outputPath);
        System.out.println("==========================================");
    }
}
