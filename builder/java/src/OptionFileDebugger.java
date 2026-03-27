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
            StringBuilder sb = new StringBuilder();

            sb.append(toHex(lineStart, 8)).append(": ");

            for (int i = 0; i < 16 && lineStart + i < end; i++) {
                int value = of.readByte(lineStart + i);
                sb.append(toHex(value, 2)).append(" ");
            }

            System.out.println(sb.toString());
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
}
