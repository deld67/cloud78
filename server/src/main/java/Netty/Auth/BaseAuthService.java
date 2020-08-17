package Netty.Auth;

import java.util.LinkedList;
import java.util.List;

public class BaseAuthService implements AuthService {

    private static class UserData {
        private String login;
        private String password;
        private String username;
        private String userFolger;

        public UserData(String login, String password, String username, String userfolder) {
            this.login = login;
            this.password = password;
            this.username = username;
            this.userFolger = userfolder;

        }
    }

    private static final List<UserData> USER_DATA = new LinkedList<>();

    @Override
    public String getUsernameByLoginAndPassword(String login, String password) {
        System.out.println("Get "+login+" "+password);
        for (UserData userDatum : USER_DATA) {
            if (userDatum.login.equals(login) && userDatum.password.equals(password)) {
                System.out.println("return "+userDatum.username);
                return userDatum.username;
            }
        }
        return null;
    }

    @Override
    public String getUserFolderByUsername(String username) {
        for (UserData userDatum : USER_DATA) {
            if (userDatum.username.equals(username) ) {
                return userDatum.userFolger;
            }
        }
        return null;
    }

    @Override
    public void start() {
        System.out.println("Сервис аутентификации запущен");
        USER_DATA.clear();
        USER_DATA.add( new UserData("login1", "pass1", "username1", "1") );
        USER_DATA.add(new UserData("login2", "pass2", "username2", "2")  );
        USER_DATA.add( new UserData("login3", "pass3", "username3", "3") );
        System.out.println(USER_DATA.size());
    }

    @Override
    public void stop() {
        System.out.println("Сервис аутентификации оставлен");
    }

    
}
