import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        String basePath = null;
        String snapshotPath = null;
        String outputPath = null;

        for (int i = 0; i < args.length; i++) {
            if ("--base".equals(args[i]) && i + 1 < args.length) {
                basePath = args[++i];
            } else if ("--snapshot".equals(args[i]) && i + 1 < args.length) {
                snapshotPath = args[++i];
            } else if ("--output".equals(args[i]) && i + 1 < args.length) {
                outputPath = args[++i];
            }
        }

        if (basePath == null || snapshotPath == null || outputPath == null) {
            System.out.println("Usage:");
            System.out.println("java Main --base <base_opt> --snapshot <snapshot_json> --output <output_opt>");
            System.exit(1);
        }

        File baseFile = new File(basePath);
        File snapshotFile = new File(snapshotPath);
        File outputFile = new File(outputPath);

        if (!baseFile.exists()) {
            System.out.println("Base Option File not found: " + basePath);
            System.exit(1);
        }

        if (!snapshotFile.exists()) {
            System.out.println("Snapshot JSON not found: " + snapshotPath);
            System.exit(1);
        }

        try {
            copyFile(baseFile, outputFile);

            System.out.println("==========================================");
            System.out.println("Java Option File Builder");
            System.out.println("==========================================");
            System.out.println("Base OPT: " + basePath);
            System.out.println("Snapshot: " + snapshotPath);
            System.out.println("Output:   " + outputPath);
            System.out.println("Status: placeholder copy completed");
            System.out.println("==========================================");
        } catch (Exception e) {
            System.out.println("Builder failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void copyFile(File source, File target) throws IOException {
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
