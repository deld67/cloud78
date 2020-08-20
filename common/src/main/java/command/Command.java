package command;

import java.io.Serializable;

public class Command implements Serializable {
    private CommandType type;
    private Object data;

    public CommandType getType() {
        return type;
    }

    public Object getData() {
        return data;
    }

    public void setType(CommandType type) {
        this.type = type;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
