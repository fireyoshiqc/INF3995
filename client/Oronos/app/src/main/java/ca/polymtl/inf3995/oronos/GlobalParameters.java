package ca.polymtl.inf3995.oronos;

import android.graphics.Color;

public class GlobalParameters {
    // SocketClient
    public static String CLIENT_ADDRESS = "127.0.0.1";
    public static int CLIENT_PORT = 3000;

    // ModuleStatus
    public final static int ONLINE_TO_DELAY  = 120000; // in milliseconds.
    public final static int DELAY_TO_OFFLINE = 240000; // in milliseconds.
    public final static int GREEN_STATUS  = Color.rgb(
            Integer.parseInt("43", 16),
            Integer.parseInt("a0", 16),
            Integer.parseInt("47", 16)
    );
    public final static int ORANGE_STATUS = Color.rgb(
            Integer.parseInt("ff", 16),
            Integer.parseInt("57", 16),
            Integer.parseInt("22", 16)
    );
    public final static int RED_STATUS    = Color.rgb(
            Integer.parseInt("d3", 16),
            Integer.parseInt("2f", 16),
            Integer.parseInt("2f", 16)
    );

    GlobalParameters() {}
}
