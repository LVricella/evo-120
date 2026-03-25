import java.util.HashMap;
import java.util.Map;

public class TeamOffsetResolver {

    // =========================================================
    // TEMPORARY / MANUAL TEAM OFFSET MAPPING
    // =========================================================
    //
    // This is the first step toward real squad writing.
    //
    // For now:
    // - we support manual team -> offset mapping
    // - we keep one primary offset and one mirrored offset
    //
    // Later:
    // - replace with real mapping logic from PES 6 editor analysis
    // - or load mapping from config / generated data
    // =========================================================

    private Map<Integer, Integer> primaryOffsets;
    private Map<Integer, Integer> mirrorOffsets;

    public TeamOffsetResolver() {
        primaryOffsets = new HashMap<Integer, Integer>();
        mirrorOffsets = new HashMap<Integer, Integer>();

        loadDefaults();
    }

    private void loadDefaults() {
        // =====================================================
        // IMPORTANT:
        // These are placeholder mappings.
        //
        // They are NOT guaranteed to match real PES 6 teams yet.
        // They only provide structure so the codebase can move
        // from "diagnostic mode" to "write-capable mode".
        //
        // Replace these with real values as soon as you map them.
        // =====================================================

        // Example dummy mappings:
        // teamId -> primary block offset
        primaryOffsets.put(1,  0x0A1080);
        primaryOffsets.put(2,  0x0A10C0);
        primaryOffsets.put(3,  0x0A1100);
        primaryOffsets.put(4,  0x0A1140);
        primaryOffsets.put(5,  0x0A1180);

        // teamId -> mirror block offset
        mirrorOffsets.put(1,  0x0A3080);
        mirrorOffsets.put(2,  0x0A30C0);
        mirrorOffsets.put(3,  0x0A3100);
        mirrorOffsets.put(4,  0x0A3140);
        mirrorOffsets.put(5,  0x0A3180);
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
