package ca.polymtl.inf3995.oronos.utils;

import android.graphics.Color;

import java.util.List;
import java.util.Map;


public class GlobalParameters {
    // ModuleStatus
    public final static int ONLINE_TO_DELAY = 120000; // in milliseconds.
    public final static int DELAY_TO_OFFLINE = 240000; // in milliseconds.
    public final static int GREEN_STATUS = Color.rgb(
            Integer.parseInt("43", 16),
            Integer.parseInt("a0", 16),
            Integer.parseInt("47", 16)
    );
    public final static int ORANGE_STATUS = Color.rgb(
            Integer.parseInt("ff", 16),
            Integer.parseInt("57", 16),
            Integer.parseInt("22", 16)
    );
    public final static int RED_STATUS = Color.rgb(
            Integer.parseInt("d3", 16),
            Integer.parseInt("2f", 16),
            Integer.parseInt("2f", 16)
    );
    // SocketClient
    public static String CLIENT_ADDRESS = "0.0.0.0";
    public static int CLIENT_PORT = 5005;
    public static Map<Integer, String> canSid;
    public static Map<String, String> canDataTypes;
    public static Map<String, List<String>> canMsgDataTypes;
    GlobalParameters() {
    }

}