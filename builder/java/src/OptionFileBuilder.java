import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class OptionFileBuilder {

    public void build(File baseFile, File snapshotFile, File outputFile, Map<Integer, List<Integer>> teams) throws Exception {
        validateInputs(baseFile, snapshotFile, outputFile, teams);

        logBuildStart(baseFile, snapshotFile, outputFile, teams);

        // ==========================================
        // Placeholder implementation
        // ==========================================
        // Current behavior:
        // - validates snapshot structure already parsed by Main
        // - copies base OPT to output
        //
        // Future behavior:
        // - load base Option File bytes
        // - locate squad data blocks
        // - replace each team roster according to snapshot
        // - recalculate checksums
        // - write final OPT
        // ==========================================

        copyFile(baseFile, outputFile);

        System.out.println("OptionFileBuilder: placeholder copy completed.");
    }

    private void validateInputs(File baseFile, File snapshotFile, File outputFile, Map<Integer, List<Integer>> teams) throws Exception {
        if (baseFile == null || !baseFile.exists()) {
            throw new Exception("Base Option File does not exist.");
        }

        if (snapshotFile == null || !snapshotFile.exists()) {
            throw new Exception("Snapshot file does not exist.");
        }

        if (outputFile == null) {
            throw new Exception("Output file is null.");
        }

        if (teams == null) {
            throw new Exception("Teams map is null.");
        }
    }

    private void logBuildStart(File baseFile, File snapshotFile, File outputFile, Map<Integer, List<Integer>> teams) {
        System.out.println("==========================================");
        System.out.println("OptionFileBuilder");
        System.out.println("==========================================");
        System.out.println("Base OPT:   " + baseFile.getAbsolutePath());
        System.out.println("Snapshot:   " + snapshotFile.getAbsolutePath());
        System.out.println("Output OPT: " + outputFile.getAbsolutePath());
        System.out.println("Teams:      " + teams.size());
        System.out.println("==========================================");

        for (Map.Entry<Integer, List<Integer>> entry : teams.entrySet()) {
            Integer teamId = entry.getKey();
            List<Integer> players = entry.getValue();

            System.out.println(
                "Team " + teamId +
                " | players: " + players.size() +
                (players.isEmpty() ? "" : " | first: " + players.get(0))
            );
        }

        System.out.println("==========================================");
    }

    private void copyFile(File source, File target) throws IOException {
        File parent = target.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        FileInputStream in = new FileInputStream(source);
        FileOutputStream out = new FileOutputStream(target);

        byte[] buffer = new byte[8192];
        int len;

        while ((len = in.read(buffer)) > 0) {
            out.write(buffer, 0, len);
        }

        in.close();
        out.close();
    }
}
