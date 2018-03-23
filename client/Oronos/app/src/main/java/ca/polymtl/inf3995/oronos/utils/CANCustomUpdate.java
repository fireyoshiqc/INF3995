package ca.polymtl.inf3995.oronos.utils;

import android.annotation.SuppressLint;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import ca.polymtl.inf3995.oronos.services.BroadcastMessage;

/**
 * Created by Felix on 22/mars/2018.
 */

// TODO: FINISH THIS USING CustomUpdate.py as a base

@SuppressLint("DefaultLocale")
public class CANCustomUpdate {

    private static HashMap<String, Double> altMax = new HashMap<>();
    private static HashMap<String, Double> lastRampAlt = new HashMap<>();


    // REFLECTION WOOOOOO
    public static String update(String customUpdate, BroadcastMessage msg) {

        try {
            Method method = CANCustomUpdate.class.getDeclaredMethod(customUpdate, BroadcastMessage.class);
            method.setAccessible(true);
            return (String) method.invoke(null, msg);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            return "WRONG_UPDATE";
        }
    }

    public static boolean acceptable(String customAcceptable, BroadcastMessage msg) {
        try {
            Method method = CANCustomUpdate.class.getDeclaredMethod(customAcceptable, BroadcastMessage.class);
            method.setAccessible(true);
            return (boolean) method.invoke(null, msg);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            return false;
        }
    }

    public static String updateWithParam(String customUpdate, String param, BroadcastMessage msg) {
        try {
            Method method = CANCustomUpdate.class.getDeclaredMethod(customUpdate, BroadcastMessage.class, String.class);
            method.setAccessible(true);
            return (String) method.invoke(null, msg, param);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            return "WRONG_UPDATE";
        }
    }

    private static String oneWire(BroadcastMessage msg, String param) {
        try {
            if (Integer.toHexString(msg.getData1().intValue()).equalsIgnoreCase(param.split("x")[1])) {
                return String.format("%.2f", msg.getData2().doubleValue()) + " C";
            } else {
                return null;
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            return "WRONG_UPDATE";
        }
    }

    private static String armingStatusUpdate(BroadcastMessage msg) {
        int armingStatus = msg.getData1().intValue();
        if (armingStatus == 0) {
            return "DISARMED";
        } else if (armingStatus == 1) {
            return "ARMED";
        } else {
            return "UNKNOWN";
        }
    }

    private static String admStateUpdate(BroadcastMessage msg) {
        int admState = msg.getData1().intValue();

        switch (admState) {
            case 0:
                return "INIT";
            case 1:
                return "ARMING";
            case 2:
                return "LAUNCH_DETECT";
            case 3:
                return "MACH_DELAY";
            case 4:
                return "APOGEE";
            case 5:
                return "MAIN_CHUTE_ALTITUDE";
            case 6:
                return "SAFE_MODE";
            default:
                return "UNKNOWN";
        }
    }

    private static String pressToAlt(BroadcastMessage msg) {
        double altFeet;
        if (lastRampAlt.containsKey(msg.getModuleSource().name() + msg.getNoSerieSource())) {
            altFeet = pascalToFeet(msg.getData1().doubleValue()) -
                    lastRampAlt.get(msg.getModuleSource().name() + msg.getNoSerieSource());
        } else {
            altFeet = 0;
        }
        return String.format("%.0f", altFeet) + " ft";

    }

    private static String rampAlt(BroadcastMessage msg) {
        double rampAltFt = metersToFeet(msg.getData1().doubleValue());
        lastRampAlt.put(msg.getModuleSource().name() + msg.getNoSerieSource(), rampAltFt);
        if (msg.getModuleSource() == ModuleType.ADM && msg.getNoSerieSource() == 1) {
            lastRampAlt.put(ModuleType.ADIRM.name() + 0, rampAltFt);
        }
        return String.format("%.0f", rampAltFt) + " ft";
    }

    private static String apogeeDetect(BroadcastMessage msg) {
        String key = msg.getModuleSource().name() + msg.getNoSerieSource();
        if (!altMax.containsKey(key)) {
            altMax.put(msg.getModuleSource().name() + msg.getNoSerieSource(), Double.NEGATIVE_INFINITY);
        }

        double altFeet = Double.NEGATIVE_INFINITY;

        if (lastRampAlt.containsKey(key)) {
            altFeet = pascalToFeet(msg.getData1().doubleValue()) - lastRampAlt.get(key);
        }

        if (altFeet > altMax.get(key)) {
            altMax.put(key, altFeet);
        }
        return String.format("%.0f", altMax.get(key)) + " ft";
    }


    private static String admBWVoltsToOhmsString(BroadcastMessage msg) {
        double conv = admBWVoltsToOhms(msg.getData1().doubleValue());
        return String.format("%.1f", conv) + " Ω";
    }

    private static String meterToFoot(BroadcastMessage msg) {
        return String.format("%.0f", msg.getData1().doubleValue() * 3.28084) + " ft";
    }

    private static String footToMeter(BroadcastMessage msg) {
        return String.format("%.0f", msg.getData1().doubleValue() * 0.3048) + " m";
    }

    private static String SDSpaceLeft(BroadcastMessage msg) {
        // Remaining space is sent as kB
        double data = msg.getData1().doubleValue();
        if (data < 0) {
            return "ERROR";
        } else if (data < 10 * 1024) {
            return String.format("%.0f", data) + " kB";
        } else if (data < 1024 * 1024) {
            return String.format("%.2f", data / 1024) + " MB";
        } else {
            return String.format("%.2f", data / (1024 * 1024)) + " GB";
        }
    }

    private static String SDBytesWritten(BroadcastMessage msg) {
        // Written space is sent as bytes
        double data = msg.getData1().doubleValue();
        if (data < 0) {
            return "ERROR";
        } else if (data < 1024 * 1024) {
            return String.format("%.0f", data / 1024) + " kB";
        } else if (data < 1024 * 1024 * 1024) {
            return String.format("%.2f", data / (1024 * 1024)) + " MB";
        } else {
            return String.format("%.2f", data / (1024 * 1024 * 1024)) + " GB";
        }
    }

    private static boolean isBWOhmAcceptable(BroadcastMessage msg) {
        double conv = admBWVoltsToOhms(msg.getData1().doubleValue());
        return 4.0 < conv && conv < 6.5;
    }

    private static boolean oneWireAcceptable(BroadcastMessage msg) {
        double wire = msg.getData2().doubleValue();
        return 15.0 < wire && wire < 65.0;
    }

    private static double pascalToFeet(double pascal) {
        // Sea level pressure : 1013.25 kPa
        // Sea level temp : 288.15 K
        double meters = (((Math.log10(pascal / 101325) / Math.log(5.255)) * 28815) - 1) / (-0.65);

        // 1 meter = 3.28084 ft
        return metersToFeet(meters);
    }

    private static double metersToFeet(double meters) {
        return meters * 3.28084;
    }

    private static double admBWVoltsToOhms(double volts) {
        double conv = volts * 3.5 / 1000.0;
        if (conv < 11.0) {
            conv = Double.POSITIVE_INFINITY;
        }
        return conv < 11.0 ? conv : Double.POSITIVE_INFINITY;
    }
}
