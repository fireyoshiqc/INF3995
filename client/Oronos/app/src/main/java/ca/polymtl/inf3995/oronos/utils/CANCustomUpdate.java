package ca.polymtl.inf3995.oronos.utils;

import android.content.BroadcastReceiver;

import ca.polymtl.inf3995.oronos.services.BroadcastMessage;

/**
 * Created by Felix on 22/mars/2018.
 */

// TODO: FINISH THIS USING CustomUpdate.py as a base

public class CANCustomUpdate {
    public static String armingStatusUpdate(BroadcastMessage msg) {
        int armingStatus = msg.getData1().intValue();
        if (armingStatus == 0) {
            return "DISARMED";
        }
        else if (armingStatus == 1) {
            return "ARMED";
        }
        else {
            return "UNKNOWN";
        }
    }

    public static String admStateUpdate(BroadcastMessage msg) {
        int admState = msg.getData1().intValue();

        switch(admState) {
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

    public static String pressToAlt(BroadcastMessage msg) {
        pascalToFeet(msg.getData1().doubleValue());
        return null;

    }

    private static double pascalToFeet(double pascal) {
        // Sea level pressure : 1013.25 kPa
        // Sea level temp : 288.15 K
        double meters = (((Math.log10(pascal/1013.25)/Math.log(5.255))*288.15)-1)/-0.0065;

        // 1 meter = 3.28084 ft
        return meters * 3.28084;
    }
}
