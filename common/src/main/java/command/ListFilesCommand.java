package command;

import java.io.Serializable;
import java.util.List;

public class ListFilesCommand implements Serializable {
    private  List<String> list;
    private final String filePath;

    public ListFilesCommand(String filePath) {
        this.filePath = filePath;
    }

    public List<String> getList() {
        return list;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setList(List<String> list) {
        this.list = list;
    }
}
