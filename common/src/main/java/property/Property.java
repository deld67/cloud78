package property;

public class Property {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8189;
    private static final String SERVER_ROOT_PATH = "C://GeekBrains/cloud/server/client";
    private static final String CLIENTS_ROOT_PATH = "C://GeekBrains/cloud/client";
    private static final int BUFER_SIZE = 1;

    public static String getServerHost() {
        return SERVER_HOST;
    }

    public static int getServerPort() {
        return SERVER_PORT;
    }

    public static String getServerRootPath() {
        return SERVER_ROOT_PATH;
    }

    public static String getClientsRootPath() {
        return CLIENTS_ROOT_PATH;
    }

    public static int getBuferSize() {
        return BUFER_SIZE;
    }
}
