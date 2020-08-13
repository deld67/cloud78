package command;

import java.io.Serializable;

public class ResultCommand implements Serializable {
    private final String result;

    public ResultCommand(String result) {
        this.result = result;
    }
}
