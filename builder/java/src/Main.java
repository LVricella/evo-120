import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;
import java.util.Map;

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

        try {
            String json = readAll(new File(snapshotPath));
            Map<Integer, List<Integer>> teams = JsonParser.parseSnapshot(json);

            System.out.println("Teams parsed: " + teams.size());

            OptionFileBuilder builder = new OptionFileBuilder(basePath, outputPath);
            builder.build(teams);

            System.out.println("Build completed.");

        } catch (Exception e) {
            System.out.println("Builder failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static String readAll(File file) throws Exception {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;

        while ((line = br.readLine()) != null) {
            sb.append(line);
        }

        br.close();
        return sb.toString();
    }
}
