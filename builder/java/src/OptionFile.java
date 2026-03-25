import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class OptionFile {

    private byte[] data;

    public OptionFile(String path) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            throw new IOException("Option File not found: " + path);
        }

        FileInputStream in = new FileInputStream(file);
        data = new byte[(int) file.length()];

        int totalRead = 0;
        while (totalRead < data.length) {
            int read = in.read(data, totalRead, data.length - totalRead);
            if (read < 0) {
                break;
            }
            totalRead += read;
        }

        in.close();

        if (totalRead != data.length) {
            throw new IOException("Could not read full Option File. Read " + totalRead + " of " + data.length + " bytes.");
        }
    }

    public int length() {
        return data.length;
    }

    public int readByte(int offset) {
        checkOffset(offset, 1);
        return data[offset] & 0xFF;
    }

    public int readUInt16LE(int offset) {
        checkOffset(offset, 2);
        int b1 = data[offset] & 0xFF;
        int b2 = data[offset + 1] & 0xFF;
        return b1 | (b2 << 8);
    }

    public int readUInt32LE(int offset) {
        checkOffset(offset, 4);
        int b1 = data[offset] & 0xFF;
        int b2 = data[offset + 1] & 0xFF;
        int b3 = data[offset + 2] & 0xFF;
        int b4 = data[offset + 3] & 0xFF;
        return b1 | (b2 << 8) | (b3 << 16) | (b4 << 24);
    }

    public void writeByte(int offset, int value) {
        checkOffset(offset, 1);
        data[offset] = (byte) (value & 0xFF);
    }

    public void writeUInt16LE(int offset, int value) {
        checkOffset(offset, 2);
        data[offset] = (byte) (value & 0xFF);
        data[offset + 1] = (byte) ((value >> 8) & 0xFF);
    }

    public void writeUInt32LE(int offset, int value) {
        checkOffset(offset, 4);
        data[offset] = (byte) (value & 0xFF);
        data[offset + 1] = (byte) ((value >> 8) & 0xFF);
        data[offset + 2] = (byte) ((value >> 16) & 0xFF);
        data[offset + 3] = (byte) ((value >> 24) & 0xFF);
    }

    public byte[] getBytes(int offset, int length) {
        checkOffset(offset, length);
        byte[] out = new byte[length];
        System.arraycopy(data, offset, out, 0, length);
        return out;
    }

    public void setBytes(int offset, byte[] bytes) {
        checkOffset(offset, bytes.length);
        System.arraycopy(bytes, 0, data, offset, bytes.length);
    }

    public void save(String path) throws IOException {
        File outFile = new File(path);
        File parent = outFile.getParentFile();

        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        FileOutputStream out = new FileOutputStream(outFile);
        out.write(data);
        out.flush();
        out.close();
    }

    private void checkOffset(int offset, int size) {
        if (offset < 0 || offset + size > data.length) {
            throw new RuntimeException(
                "Invalid offset access. Offset=" + offset +
                ", size=" + size +
                ", fileLength=" + data.length
            );
        }
    }
}
