package ca.polymtl.inf3995.oronos.utils;

import java.util.List;
import java.util.Map;

/**
 * <h1>Global Parameters</h1>
 * Constants that are shared between many classes
 *
 * @author Everyone
 * @version 0.0
 * @since 2018-04-12
 */
public class GlobalParameters {

    //Theme
    public final static boolean DEFAULT_THEME_IS_DARK = false;

    // LogTree
    public final static String LOG_DIR_NAME = "OronosLogs";

    // ModuleStatus
    public final static int ONLINE_TO_DELAY = 2000; // in milliseconds.
    public final static int DELAY_TO_OFFLINE = 4000; // in milliseconds.

    // SocketClient
    public static final String CLIENT_ADDRESS = "0.0.0.0";

    public static boolean hasRetardedErrorMessages = false;
    public static String serverAddress = null;
    public static int udpPort = 5005;
    public static Map<Integer, String> canSid;
    public static Map<String, String> canDataTypes;
    public static Map<String, List<String>> canMsgDataTypes;
    public static Map<String, Integer> canModuleTypes;
    public static String layoutName;
    public static String mapName;
    public static double serverTimeout = 0.0;

    // DisplayLogWidget
    public final static int DATA_UPDATE_PERIOD = 1000;

    GlobalParameters() {
    }

}
