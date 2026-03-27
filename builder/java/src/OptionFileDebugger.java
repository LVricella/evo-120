public class OptionFileDebugger {

    public void printFileSummary(OptionFile of) {
        System.out.println("==========================================");
        System.out.println("Option File Debug Summary");
        System.out.println("==========================================");
        System.out.println("Length: " + of.length() + " bytes");
        System.out.println("First 4 bytes (UInt32LE): " + of.readUInt32LE(0));
        System.out.println("Checksum area (UInt32LE): " + of.readUInt32LE(OptionFileConstants.CHECKSUM_OFFSET));
        System.out.println("==========================================");
    }

    public void dumpBytes(OptionFile of, int startOffset, int length) {
        if (length <= 0) {
            System.out.println("dumpBytes: invalid length");
            return;
        }

        System.out.println("==========================================");
        System.out.println("Byte dump");
        System.out.println("Start offset: " + startOffset);
        System.out.println("Length: " + length);
        System.out.println("==========================================");

        int end = startOffset + length;
        int lineStart = startOffset;

        while (lineStart < end) {
            StringBuilder hexPart = new StringBuilder();
            StringBuilder asciiPart = new StringBuilder();

            for (int i = 0; i < 16; i++) {
                int currentOffset = lineStart + i;

                if (currentOffset < end) {
                    int value = of.readByte(currentOffset);
                    hexPart.append(toHex(value, 2)).append(" ");
                    asciiPart.append(toPrintableAscii(value));
                } else {
                    hexPart.append("     ");
                    asciiPart.append(" ");
                }
            }

            System.out.println(
                toHex(lineStart, 8) + ": " +
                hexPart.toString() +
                " | " +
                asciiPart.toString()
            );

            lineStart += 16;
        }

        System.out.println("==========================================");
    }

    public void dumpUInt16LE(OptionFile of, int startOffset, int count) {
        if (count <= 0) {
            System.out.println("dumpUInt16LE: invalid count");
            return;
        }

        System.out.println("==========================================");
        System.out.println("UInt16LE dump");
        System.out.println("Start offset: " + startOffset);
        System.out.println("Count: " + count);
        System.out.println("==========================================");

        for (int i = 0; i < count; i++) {
            int offset = startOffset + (i * 2);
            int value = of.readUInt16LE(offset);

            System.out.println(
                "[" + i + "] " +
                "offset=" + offset +
                " hex=" + toHex(offset, 8) +
                " value=" + value +
                " valueHex=" + toHex(value, 4)
            );
        }

        System.out.println("==========================================");
    }

    public void compareRegions(OptionFile of, int offsetA, int offsetB, int length) {
        if (length <= 0) {
            System.out.println("compareRegions: invalid length");
            return;
        }

        System.out.println("==========================================");
        System.out.println("Compare regions");
        System.out.println("A: " + offsetA + " (" + toHex(offsetA, 8) + ")");
        System.out.println("B: " + offsetB + " (" + toHex(offsetB, 8) + ")");
        System.out.println("Length: " + length);
        System.out.println("==========================================");

        int diffCount = 0;

        for (int i = 0; i < length; i++) {
            int a = of.readByte(offsetA + i);
            int b = of.readByte(offsetB + i);

            if (a != b) {
                diffCount++;
                System.out.println(
                    "diff @ +" + i +
                    " | A=" + toHex(a, 2) +
                    " | B=" + toHex(b, 2)
                );
            }
        }

        if (diffCount == 0) {
            System.out.println("No differences found.");
        } else {
            System.out.println("Total differences: " + diffCount);
        }

        System.out.println("==========================================");
    }

    public void findUInt16Value(OptionFile of, int startOffset, int length, int targetValue) {
        findUInt16Value(of, startOffset, length, targetValue, 2);
    }

    public void findUInt16Value(OptionFile of, int startOffset, int length, int targetValue, int stride) {
        if (length <= 0) {
            System.out.println("findUInt16Value: invalid length");
            return;
        }

        if (stride <= 0) {
            System.out.println("findUInt16Value: invalid stride");
            return;
        }

        System.out.println("==========================================");
        System.out.println("Find UInt16LE value");
        System.out.println("Start offset: " + startOffset + " (" + toHex(startOffset, 8) + ")");
        System.out.println("Length: " + length);
        System.out.println("Stride: " + stride);
        System.out.println("Target value: " + targetValue + " (" + toHex(targetValue, 4) + ")");
        System.out.println("==========================================");

        int end = startOffset + length;
        int found = 0;

        for (int offset = startOffset; offset + 1 < end; offset += stride) {
            int value = of.readUInt16LE(offset);
            if (value == targetValue) {
                found++;
                System.out.println(
                    "match #" + found +
                    " at offset=" + offset +
                    " (" + toHex(offset, 8) + ")"
                );
            }
        }

        if (found == 0) {
            System.out.println("No matches found.");
        } else {
            System.out.println("Total matches: " + found);
        }

        System.out.println("==========================================");
    }

    public void findUInt16Sequence(OptionFile of, int startOffset, int length, int[] sequence) {
        findUInt16Sequence(of, startOffset, length, sequence, 2);
    }

    public void findUInt16Sequence(OptionFile of, int startOffset, int length, int[] sequence, int stride) {
        if (length <= 0) {
            System.out.println("findUInt16Sequence: invalid length");
            return;
        }

        if (sequence == null || sequence.length == 0) {
            System.out.println("findUInt16Sequence: empty sequence");
            return;
        }

        if (stride <= 0) {
            System.out.println("findUInt16Sequence: invalid stride");
            return;
        }

        System.out.println("==========================================");
        System.out.println("Find UInt16LE sequence");
        System.out.println("Start offset: " + startOffset + " (" + toHex(startOffset, 8) + ")");
        System.out.println("Length: " + length);
        System.out.println("Stride: " + stride);
        System.out.print("Sequence: ");
        for (int i = 0; i < sequence.length; i++) {
            if (i > 0) System.out.print(", ");
            System.out.print(sequence[i] + " (" + toHex(sequence[i], 4) + ")");
        }
        System.out.println();
        System.out.println("==========================================");

        int end = startOffset + length;
        int found = 0;
        int bytesNeeded = ((sequence.length - 1) * stride) + 2;

        for (int offset = startOffset; offset + bytesNeeded - 1 < end; offset += stride) {
            boolean match = true;

            for (int i = 0; i < sequence.length; i++) {
                int value = of.readUInt16LE(offset + (i * stride));
                if (value != sequence[i]) {
                    match = false;
                    break;
                }
            }

            if (match) {
                found++;
                System.out.println(
                    "sequence match #" + found +
                    " at offset=" + offset +
                    " (" + toHex(offset, 8) + ")"
                );
            }
        }

        if (found == 0) {
            System.out.println("No sequence matches found.");
        } else {
            System.out.println("Total sequence matches: " + found);
        }

        System.out.println("==========================================");
    }

    public void printKnownAreas(OptionFile of) {
        System.out.println("==========================================");
        System.out.println("Known / suspected areas");
        System.out.println("==========================================");

        System.out.println(
            "CHECKSUM: " +
            OptionFileConstants.CHECKSUM_OFFSET + "-" +
            (OptionFileConstants.CHECKSUM_OFFSET + OptionFileConstants.CHECKSUM_LENGTH - 1)
        );

        System.out.println(
            "SQUAD_BLOCK_1: " +
            OptionFileConstants.SQUAD_BLOCK_1_START + "-" +
            OptionFileConstants.SQUAD_BLOCK_1_END
        );

        System.out.println(
            "SQUAD_BLOCK_2: " +
            OptionFileConstants.SQUAD_BLOCK_2_START + "-" +
            OptionFileConstants.SQUAD_BLOCK_2_END
        );

        System.out.println(
            "SQUAD_BLOCK_3: " +
            OptionFileConstants.SQUAD_BLOCK_3_START + "-" +
            OptionFileConstants.SQUAD_BLOCK_3_END
        );

        System.out.println(
            "OBSERVED_DIFF_A: " +
            OptionFileConstants.OBSERVED_DIFF_BLOCK_A_START + "-" +
            OptionFileConstants.OBSERVED_DIFF_BLOCK_A_END
        );

        System.out.println(
            "OBSERVED_DIFF_B: " +
            OptionFileConstants.OBSERVED_DIFF_BLOCK_B_START + "-" +
            OptionFileConstants.OBSERVED_DIFF_BLOCK_B_END
        );

        System.out.println("Current checksum UInt32LE: " + of.readUInt32LE(OptionFileConstants.CHECKSUM_OFFSET));

        System.out.println("==========================================");
    }

    private String toHex(int value, int digits) {
        String hex = Integer.toHexString(value).toUpperCase();

        while (hex.length() < digits) {
            hex = "0" + hex;
        }

        if (hex.length() > digits) {
            hex = hex.substring(hex.length() - digits);
        }

        return "0x" + hex;
    }

    private char toPrintableAscii(int value) {
        if (value >= 32 && value <= 126) {
            return (char) value;
        }
        return '.';
    }
}
