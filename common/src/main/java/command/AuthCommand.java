package command;

import java.io.Serializable;
import java.util.List;

public class AuthCommand implements Serializable {

    private final String login;
    private final String password;
    private String username;
    private String userFolder;
    private String Result;


    public AuthCommand(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserFolder() {
        return userFolder;
    }

    public void setUserFolder(String userFolder) {
        this.userFolder = userFolder;
    }

    public String getResult() {
        return Result;
    }

    public void setResult(String result) {
        Result = result;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}
