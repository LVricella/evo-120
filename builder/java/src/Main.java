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

        boolean debugOnly = false;

        Integer dumpBytesOffset = null;
        Integer dumpBytesLength = null;

        Integer dumpU16Offset = null;
        Integer dumpU16Count = null;

        Integer compareOffsetA = null;
        Integer compareOffsetB = null;
        Integer compareLength = null;

        for (int i = 0; i < args.length; i++) {
            if ("--base".equals(args[i]) && i + 1 < args.length) {
                basePath = args[++i];
            } else if ("--snapshot".equals(args[i]) && i + 1 < args.length) {
                snapshotPath = args[++i];
            } else if ("--output".equals(args[i]) && i + 1 < args.length) {
                outputPath = args[++i];
            } else if ("--debug".equals(args[i])) {
                debugOnly = true;
            } else if ("--dump-bytes".equals(args[i]) && i + 2 < args.length) {
                dumpBytesOffset = parseIntArg(args[++i]);
                dumpBytesLength = parseIntArg(args[++i]);
            } else if ("--dump-u16".equals(args[i]) && i + 2 < args.length) {
                dumpU16Offset = parseIntArg(args[++i]);
                dumpU16Count = parseIntArg(args[++i]);
            } else if ("--compare".equals(args[i]) && i + 3 < args.length) {
                compareOffsetA = parseIntArg(args[++i]);
                compareOffsetB = parseIntArg(args[++i]);
                compareLength = parseIntArg(args[++i]);
            }
        }

        try {
            if (debugOnly) {
                runDebugOnly(
                    basePath,
                    dumpBytesOffset,
                    dumpBytesLength,
                    dumpU16Offset,
                    dumpU16Count,
                    compareOffsetA,
                    compareOffsetB,
                    compareLength
                );
                return;
            }

            if (basePath == null || snapshotPath == null || outputPath == null) {
                printUsage();
                System.exit(1);
            }

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

    private static void runDebugOnly(
        String basePath,
        Integer dumpBytesOffset,
        Integer dumpBytesLength,
        Integer dumpU16Offset,
        Integer dumpU16Count,
        Integer compareOffsetA,
        Integer compareOffsetB,
        Integer compareLength
    ) throws Exception {
        if (basePath == null) {
            System.out.println("Debug mode requires --base <base_opt>");
            printUsage();
            System.exit(1);
        }

        OptionFile of = new OptionFile(basePath);
        OptionFileDebugger debugger = new OptionFileDebugger();

        System.out.println("==========================================");
        System.out.println("DEBUG ONLY MODE");
        System.out.println("==========================================");

        debugger.printFileSummary(of);
        debugger.printKnownAreas(of);

        if (dumpBytesOffset != null && dumpBytesLength != null) {
            debugger.dumpBytes(of, dumpBytesOffset.intValue(), dumpBytesLength.intValue());
        }

        if (dumpU16Offset != null && dumpU16Count != null) {
            debugger.dumpUInt16LE(of, dumpU16Offset.intValue(), dumpU16Count.intValue());
        }

        if (compareOffsetA != null && compareOffsetB != null && compareLength != null) {
            debugger.compareRegions(
                of,
                compareOffsetA.intValue(),
                compareOffsetB.intValue(),
                compareLength.intValue()
            );
        }

        if (dumpBytesOffset == null && dumpU16Offset == null && compareOffsetA == null) {
            System.out.println("Dumping observed diff region A as UInt16LE...");
            debugger.dumpUInt16LE(of, OptionFileConstants.OBSERVED_DIFF_BLOCK_A_START, 24);

            System.out.println("Dumping observed diff region B as UInt16LE...");
            debugger.dumpUInt16LE(of, OptionFileConstants.OBSERVED_DIFF_BLOCK_B_START, 24);

            System.out.println("Comparing observed diff regions A and B...");
            debugger.compareRegions(
                of,
                OptionFileConstants.OBSERVED_DIFF_BLOCK_A_START,
                OptionFileConstants.OBSERVED_DIFF_BLOCK_B_START,
                64
            );
        }

        System.out.println("Debug completed.");
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

    private static int parseIntArg(String value) {
        String v = value.trim().toLowerCase();

        if (v.startsWith("0x")) {
            return Integer.parseInt(v.substring(2), 16);
        }

        return Integer.parseInt(v);
    }

    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("  Build mode:");
        System.out.println("    java Main --base <base_opt> --snapshot <snapshot_json> --output <output_opt>");
        System.out.println("");
        System.out.println("  Debug mode:");
        System.out.println("    java Main --debug --base <base_opt>");
        System.out.println("");
        System.out.println("  Debug mode with byte dump:");
        System.out.println("    java Main --debug --base <base_opt> --dump-bytes <offset> <length>");
        System.out.println("");
        System.out.println("  Debug mode with UInt16LE dump:");
        System.out.println("    java Main --debug --base <base_opt> --dump-u16 <offset> <count>");
        System.out.println("");
        System.out.println("  Debug mode with region compare:");
        System.out.println("    java Main --debug --base <base_opt> --compare <offsetA> <offsetB> <length>");
        System.out.println("");
        System.out.println("Offsets can be decimal or hexadecimal, e.g. 657956 or 0x0A0A24");
    }
}
