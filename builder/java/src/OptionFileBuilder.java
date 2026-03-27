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

        System.out.println("==========================================");
        System.out.println("Option File loaded");
        System.out.println("==========================================");
        System.out.println("File size: " + of.length() + " bytes");

        int first4 = of.readUInt32LE(0);
        System.out.println("First 4 bytes as UInt32LE: " + first4);

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

        of.save(outputPath);

        System.out.println("==========================================");
        System.out.println("Option File saved to: " + outputPath);
        System.out.println("==========================================");
    }
}
