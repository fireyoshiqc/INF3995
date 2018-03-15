package ca.polymtl.inf3995.oronos;

public class GlobalParameters {
    // SocketClient
    public static String CLIENT_ADDRESS = "127.0.0.1";
    public static int CLIENT_PORT = 3000;

    // ModuleStatus
    public final static int ONLINE_TO_DELAY  = 2000; // in milliseconds.
    public final static int DELAY_TO_OFFLINE = 4000; // in milliseconds.

    GlobalParameters() {}
}
