import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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
            String json = readAll(snapshotFile);
            Map<Integer, List<Integer>> teams = parseSnapshot(json);

            System.out.println("==========================================");
            System.out.println("Java Option File Builder");
            System.out.println("==========================================");
            System.out.println("Base OPT: " + basePath);
            System.out.println("Snapshot: " + snapshotPath);
            System.out.println("Output:   " + outputPath);
            System.out.println("==========================================");
            System.out.println("Parsed teams: " + teams.size());

            for (Map.Entry<Integer, List<Integer>> entry : teams.entrySet()) {
                Integer teamId = entry.getKey();
                List<Integer> players = entry.getValue();

                System.out.println(
                    "Team " + teamId + " -> " + players.size() + " players" +
                    (players.isEmpty() ? "" : " | first player: " + players.get(0))
                );
            }

            // Placeholder: for now, still copy the base Option File.
            // Next step: replace squad data inside the OPT.
            copyFile(baseFile, outputFile);

            System.out.println("==========================================");
            System.out.println("Status: snapshot parsed successfully");
            System.out.println("Status: base OPT copied as placeholder");
            System.out.println("==========================================");
        } catch (Exception e) {
            System.out.println("Builder failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static String readAll(File file) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;

        while ((line = br.readLine()) != null) {
            sb.append(line);
        }

        br.close();
        return sb.toString();
    }

    private static Map<Integer, List<Integer>> parseSnapshot(String json) {
        Map<Integer, List<Integer>> teams = new LinkedHashMap<Integer, List<Integer>>();

        String compact = json.replace("\n", "").replace("\r", "").replace("\t", "").trim();

        int teamsKey = compact.indexOf("\"teams\"");
        if (teamsKey == -1) {
            throw new RuntimeException("Invalid snapshot: missing 'teams' key");
        }

        int firstBrace = compact.indexOf("{", teamsKey);
        if (firstBrace == -1) {
            throw new RuntimeException("Invalid snapshot: missing teams object start");
        }

        int endBrace = findMatchingBrace(compact, firstBrace);
        if (endBrace == -1) {
            throw new RuntimeException("Invalid snapshot: missing teams object end");
        }

        String teamsBody = compact.substring(firstBrace + 1, endBrace).trim();
        if (teamsBody.length() == 0) {
            return teams;
        }

        int pos = 0;
        while (pos < teamsBody.length()) {
            while (pos < teamsBody.length() && (teamsBody.charAt(pos) == ',' || teamsBody.charAt(pos) == ' ')) {
                pos++;
            }

            if (pos >= teamsBody.length()) {
                break;
            }

            if (teamsBody.charAt(pos) != '"') {
                throw new RuntimeException("Invalid snapshot: expected team key quote at position " + pos);
            }

            int keyEnd = teamsBody.indexOf('"', pos + 1);
            if (keyEnd == -1) {
                throw new RuntimeException("Invalid snapshot: unterminated team key");
            }

            String teamKey = teamsBody.substring(pos + 1, keyEnd);
            int teamId = Integer.parseInt(teamKey);

            int colonPos = teamsBody.indexOf(':', keyEnd);
            if (colonPos == -1) {
                throw new RuntimeException("Invalid snapshot: missing ':' after team key");
            }

            int arrayStart = teamsBody.indexOf('[', colonPos);
            if (arrayStart == -1) {
                throw new RuntimeException("Invalid snapshot: missing '[' for team " + teamId);
            }

            int arrayEnd = findMatchingBracket(teamsBody, arrayStart);
            if (arrayEnd == -1) {
                throw new RuntimeException("Invalid snapshot: missing ']' for team " + teamId);
            }

            String arrayBody = teamsBody.substring(arrayStart + 1, arrayEnd).trim();
            List<Integer> players = parseIntArray(arrayBody);

            teams.put(teamId, players);

            pos = arrayEnd + 1;
        }

        return teams;
    }

    private static List<Integer> parseIntArray(String arrayBody) {
        List<Integer> values = new ArrayList<Integer>();

        if (arrayBody.length() == 0) {
            return values;
        }

        String[] parts = arrayBody.split(",");
        for (String part : parts) {
            String value = part.trim();
            if (value.length() > 0) {
                values.add(Integer.parseInt(value));
            }
        }

        return values;
    }

    private static int findMatchingBrace(String s, int startPos) {
        int depth = 0;

        for (int i = startPos; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c == '{') {
                depth++;
            } else if (c == '}') {
                depth--;
                if (depth == 0) {
                    return i;
                }
            }
        }

        return -1;
    }

    private static int findMatchingBracket(String s, int startPos) {
        int depth = 0;

        for (int i = startPos; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c == '[') {
                depth++;
            } else if (c == ']') {
                depth--;
                if (depth == 0) {
                    return i;
                }
            }
        }

        return -1;
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
