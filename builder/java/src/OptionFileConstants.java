public class OptionFileConstants {

    // ==========================================
    // Known / suspected PES 6 Option File areas
    // ==========================================
    //
    // These values come from:
    // - binary diffs between edited OF files
    // - analysis of existing PES 6 editor classes
    //
    // They are still "working constants", not final truth.
    // We will refine them as we validate squad writes.

    // File control / checksum-like area
    public static final int CHECKSUM_OFFSET = 0x0A0A1C;
    public static final int CHECKSUM_LENGTH = 4;

    // First squad-related block observed in diffs / editor analysis
    public static final int SQUAD_BLOCK_1_START = 657956;   // 0x0A0A24
    public static final int SQUAD_BLOCK_1_END   = 751472;

    // Additional internal blocks used by the editor
    public static final int SQUAD_BLOCK_2_START = 763804;
    public static final int SQUAD_BLOCK_2_END   = 911144;

    public static final int SQUAD_BLOCK_3_START = 911144;
    public static final int SQUAD_BLOCK_3_END   = 1170520;

    // Diff-observed zones that changed when squads changed
    public static final int OBSERVED_DIFF_BLOCK_A_START = 0x0A10B3;
    public static final int OBSERVED_DIFF_BLOCK_A_END   = 0x0A10EE;

    public static final int OBSERVED_DIFF_BLOCK_B_START = 0x0A3080;
    public static final int OBSERVED_DIFF_BLOCK_B_END   = 0x0A30C9;

    // General assumptions
    public static final int PLAYER_ID_SIZE = 2; // 16-bit LE
    public static final int DEFAULT_MAX_SQUAD_PLAYERS = 32;

    private OptionFileConstants() {
        // utility class
    }
}
