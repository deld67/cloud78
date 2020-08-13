package command;

import java.io.Serializable;
import java.util.List;

public class GetFileCommand implements Serializable {
    private final String filename;
    private final String filePath;
    private byte[] bytes;
    private int path;
    private int paths;

    public GetFileCommand(String filename, String filePath) {
        this.filename = filename;
        this.filePath = filePath;
    }

    public String getFilename() {
        return filename;
    }

    public String getFilePath() {
        return filePath;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBites(byte[] bytes) {
        this.bytes = bytes;
    }

    public int getPath() {
        return path;
    }

    public void setPath(int path) {
        this.path = path;
    }

    public int getPaths() {
        return paths;
    }

    public void setPaths(int paths) {
        this.paths = paths;
    }
}
