import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class TeamOffsetResolver {

    private Map<Integer, Integer> primaryOffsets;
    private Map<Integer, Integer> mirrorOffsets;

    public TeamOffsetResolver() {
        primaryOffsets = new HashMap<Integer, Integer>();
        mirrorOffsets = new HashMap<Integer, Integer>();

        loadDefaults();
        loadExternalConfig();
    }

    private void loadDefaults() {
        // Fallback defaults only.
        primaryOffsets.put(1,  0x0A1080);
        primaryOffsets.put(2,  0x0A10C0);
        primaryOffsets.put(3,  0x0A1100);
        primaryOffsets.put(4,  0x0A1140);
        primaryOffsets.put(5,  0x0A1180);

        mirrorOffsets.put(1,  0x0A3080);
        mirrorOffsets.put(2,  0x0A30C0);
        mirrorOffsets.put(3,  0x0A3100);
        mirrorOffsets.put(4,  0x0A3140);
        mirrorOffsets.put(5,  0x0A3180);
    }

    private void loadExternalConfig() {
        try {
            File configFile = resolveConfigFile();
            if (configFile == null || !configFile.exists()) {
                System.out.println("TeamOffsetResolver: external config not found, using defaults.");
                return;
            }

            System.out.println("TeamOffsetResolver: loading config from " + configFile.getAbsolutePath());

            BufferedReader br = new BufferedReader(new FileReader(configFile));
            String line;

            while ((line = br.readLine()) != null) {
                line = line.trim();

                if (line.length() == 0) {
                    continue;
                }

                if (line.startsWith("#")) {
                    continue;
                }

                int eqPos = line.indexOf('=');
                if (eqPos == -1) {
                    continue;
                }

                String teamPart = line.substring(0, eqPos).trim();
                String valuePart = line.substring(eqPos + 1).trim();

                String[] parts = valuePart.split(",");
                if (parts.length != 2) {
                    System.out.println("Skipping invalid mapping line: " + line);
                    continue;
                }

                int teamId = parseInt(teamPart);
                int primaryOffset = parseInt(parts[0].trim());
                int mirrorOffset = parseInt(parts[1].trim());

                setMapping(teamId, primaryOffset, mirrorOffset);
            }

            br.close();

        } catch (Exception e) {
            System.out.println("TeamOffsetResolver: failed loading external config: " + e.getMessage());
        }
    }

    private File resolveConfigFile() {
        String[] candidates = new String[] {
            "builder/config/team-offsets.properties",
            "../config/team-offsets.properties",
            "../../config/team-offsets.properties"
        };

        for (int i = 0; i < candidates.length; i++) {
            File f = new File(candidates[i]);
            if (f.exists()) {
                return f;
            }
        }

        return null;
    }

    private int parseInt(String value) {
        value = value.trim().toLowerCase();

        if (value.startsWith("0x")) {
            return Integer.parseInt(value.substring(2), 16);
        }

        return Integer.parseInt(value);
    }

    public int getPrimaryOffset(int teamId) {
        Integer offset = primaryOffsets.get(teamId);
        if (offset == null) {
            throw new RuntimeException("No primary offset configured for teamId=" + teamId);
        }
        return offset.intValue();
    }

    public int getMirrorOffset(int teamId) {
        Integer offset = mirrorOffsets.get(teamId);
        if (offset == null) {
            throw new RuntimeException("No mirror offset configured for teamId=" + teamId);
        }
        return offset.intValue();
    }

    public boolean hasTeam(int teamId) {
        return primaryOffsets.containsKey(teamId) && mirrorOffsets.containsKey(teamId);
    }

    public void setMapping(int teamId, int primaryOffset, int mirrorOffset) {
        primaryOffsets.put(teamId, primaryOffset);
        mirrorOffsets.put(teamId, mirrorOffset);
    }
}
