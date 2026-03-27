import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        String basePath = null;
        String snapshotPath = null;
        String outputPath = null;
        String reportPath = null;

        boolean debugOnly = false;

        Integer dumpBytesOffset = null;
        Integer dumpBytesLength = null;

        Integer dumpU16Offset = null;
        Integer dumpU16Count = null;

        Integer compareOffsetA = null;
        Integer compareOffsetB = null;
        Integer compareLength = null;

        Integer findU16Offset = null;
        Integer findU16Length = null;
        Integer findU16Value = null;
        Integer findU16Stride = 2;

        Integer findSeqOffset = null;
        Integer findSeqLength = null;
        int[] findSequence = null;
        Integer findSeqStride = 2;

        Integer findWildcardOffset = null;
        Integer findWildcardLength = null;
        Integer[] findWildcardSequence = null;
        Integer findWildcardStride = 2;

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
            } else if ("--find-u16".equals(args[i]) && i + 3 < args.length) {
                findU16Offset = parseIntArg(args[++i]);
                findU16Length = parseIntArg(args[++i]);
                findU16Value = parseIntArg(args[++i]);
            } else if ("--find-u16-stride".equals(args[i]) && i + 4 < args.length) {
                findU16Offset = parseIntArg(args[++i]);
                findU16Length = parseIntArg(args[++i]);
                findU16Value = parseIntArg(args[++i]);
                findU16Stride = parseIntArg(args[++i]);
            } else if ("--find-seq".equals(args[i]) && i + 3 < args.length) {
                findSeqOffset = parseIntArg(args[++i]);
                findSeqLength = parseIntArg(args[++i]);
                findSequence = parseSequenceArg(args[++i]);
            } else if ("--find-seq-stride".equals(args[i]) && i + 4 < args.length) {
                findSeqOffset = parseIntArg(args[++i]);
                findSeqLength = parseIntArg(args[++i]);
                findSequence = parseSequenceArg(args[++i]);
                findSeqStride = parseIntArg(args[++i]);
            } else if ("--find-wildcard".equals(args[i]) && i + 3 < args.length) {
                findWildcardOffset = parseIntArg(args[++i]);
                findWildcardLength = parseIntArg(args[++i]);
                findWildcardSequence = parseWildcardSequenceArg(args[++i]);
            } else if ("--find-wildcard-stride".equals(args[i]) && i + 4 < args.length) {
                findWildcardOffset = parseIntArg(args[++i]);
                findWildcardLength = parseIntArg(args[++i]);
                findWildcardSequence = parseWildcardSequenceArg(args[++i]);
                findWildcardStride = parseIntArg(args[++i]);
            } else if ("--report".equals(args[i]) && i + 1 < args.length) {
                reportPath = args[++i];
            }
        }

        PrintStream originalOut = System.out;
        PrintStream reportOut = null;

        try {
            if (reportPath != null) {
                File reportFile = new File(reportPath);
                File parent = reportFile.getParentFile();
                if (parent != null && !parent.exists()) {
                    parent.mkdirs();
                }
                reportOut = new PrintStream(reportFile);
                System.setOut(reportOut);
            }

            if (debugOnly) {
                runDebugOnly(
                    basePath,
                    dumpBytesOffset,
                    dumpBytesLength,
                    dumpU16Offset,
                    dumpU16Count,
                    compareOffsetA,
                    compareOffsetB,
                    compareLength,
                    findU16Offset,
                    findU16Length,
                    findU16Value,
                    findU16Stride,
                    findSeqOffset,
                    findSeqLength,
                    findSequence,
                    findSeqStride,
                    findWildcardOffset,
                    findWildcardLength,
                    findWildcardSequence,
                    findWildcardStride
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
        } finally {
            if (reportOut != null) {
                reportOut.flush();
                reportOut.close();
                System.setOut(originalOut);
                System.out.println("Report written to: " + reportPath);
            }
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
        Integer compareLength,
        Integer findU16Offset,
        Integer findU16Length,
        Integer findU16Value,
        Integer findU16Stride,
        Integer findSeqOffset,
        Integer findSeqLength,
        int[] findSequence,
        Integer findSeqStride,
        Integer findWildcardOffset,
        Integer findWildcardLength,
        Integer[] findWildcardSequence,
        Integer findWildcardStride
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

        if (findU16Offset != null && findU16Length != null && findU16Value != null) {
            debugger.findUInt16Value(
                of,
                findU16Offset.intValue(),
                findU16Length.intValue(),
                findU16Value.intValue(),
                findU16Stride.intValue()
            );
        }

        if (findSeqOffset != null && findSeqLength != null && findSequence != null) {
            debugger.findUInt16Sequence(
                of,
                findSeqOffset.intValue(),
                findSeqLength.intValue(),
                findSequence,
                findSeqStride.intValue()
            );
        }

        if (findWildcardOffset != null && findWildcardLength != null && findWildcardSequence != null) {
            debugger.findUInt16SequenceWildcard(
                of,
                findWildcardOffset.intValue(),
                findWildcardLength.intValue(),
                findWildcardSequence,
                findWildcardStride.intValue()
            );
        }

        if (dumpBytesOffset == null && dumpU16Offset == null && compareOffsetA == null && findU16Offset == null && findSeqOffset == null && findWildcardOffset == null) {
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

    private static int[] parseSequenceArg(String value) {
        String[] parts = value.split(",");
        List<Integer> values = new ArrayList<Integer>();

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i].trim();
            if (part.length() > 0) {
                values.add(parseIntArg(part));
            }
        }

        int[] result = new int[values.size()];
        for (int i = 0; i < values.size(); i++) {
            result[i] = values.get(i).intValue();
        }

        return result;
    }

    private static Integer[] parseWildcardSequenceArg(String value) {
        String[] parts = value.split(",");
        List<Integer> values = new ArrayList<Integer>();

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i].trim();
            if (part.equals("*")) {
                values.add(null);
            } else if (part.length() > 0) {
                values.add(Integer.valueOf(parseIntArg(part)));
            }
        }

        Integer[] result = new Integer[values.size()];
        for (int i = 0; i < values.size(); i++) {
            result[i] = values.get(i);
        }

        return result;
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
        System.out.println("  Debug mode find UInt16LE value:");
        System.out.println("    java Main --debug --base <base_opt> --find-u16 <offset> <length> <value>");
        System.out.println("");
        System.out.println("  Debug mode find UInt16LE value with stride:");
        System.out.println("    java Main --debug --base <base_opt> --find-u16-stride <offset> <length> <value> <stride>");
        System.out.println("");
        System.out.println("  Debug mode find UInt16LE sequence:");
        System.out.println("    java Main --debug --base <base_opt> --find-seq <offset> <length> <v1,v2,v3>");
        System.out.println("");
        System.out.println("  Debug mode find UInt16LE sequence with stride:");
        System.out.println("    java Main --debug --base <base_opt> --find-seq-stride <offset> <length> <v1,v2,v3> <stride>");
        System.out.println("");
        System.out.println("  Debug mode find wildcard sequence:");
        System.out.println("    java Main --debug --base <base_opt> --find-wildcard <offset> <length> <v1,*,v3>");
        System.out.println("");
        System.out.println("  Debug mode find wildcard sequence with stride:");
        System.out.println("    java Main --debug --base <base_opt> --find-wildcard-stride <offset> <length> <v1,*,v3> <stride>");
        System.out.println("");
        System.out.println("  Optional report output:");
        System.out.println("    --report <path_to_txt>");
        System.out.println("");
        System.out.println("Offsets and values can be decimal or hexadecimal, e.g. 657956 or 0x0A0A24");
    }
}
