import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
        File baseFile = new File(basePath);
        File outputFile = new File(outputPath);

        if (!baseFile.exists()) {
            throw new Exception("Base Option File not found: " + basePath);
        }

        System.out.println("==========================================");
        System.out.println("Applying squads (placeholder)");
        System.out.println("==========================================");

        for (Map.Entry<Integer, List<Integer>> entry : teams.entrySet()) {
            Integer teamId = entry.getKey();
            List<Integer> players = entry.getValue();

            System.out.println("Applying team " + teamId + " with " + players.size() + " players");
        }

        // ==========================================
        // TEMPORAL: copiar archivo base
        // ==========================================
        copyFile(baseFile, outputFile);

        // ==========================================
        // FUTURO:
        // - cargar OF binario
        // - ubicar squads
        // - reemplazar jugadores
        // - recalcular checksum
        // ==========================================
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
