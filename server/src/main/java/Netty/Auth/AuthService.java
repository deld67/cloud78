package Netty.Auth;

public interface AuthService {
    String getUsernameByLoginAndPassword(String login, String password);
    String getUserFolderByUsername(String username);

    void start();
    void stop();

}
