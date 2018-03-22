package ca.polymtl.inf3995.oronos.services;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import org.parceler.Parcels;

import java.util.List;
import java.util.Map;

import ca.polymtl.inf3995.oronos.utils.GlobalParameters;

/**
 * Created by prst on 2018-03-01.
 */

public class DataDispatcher {

    static private Context context;

    public static void setContext(Context context) {
        // Important for avoiding memory leaks
        DataDispatcher.context = context.getApplicationContext();
    }

    public static void dataToDispatch(List<Object> data) {

        if (GlobalParameters.canSid == null
                || GlobalParameters.canDataTypes == null
                || GlobalParameters.canMsgDataTypes == null
                || GlobalParameters.canModuleTypes == null) {
            return;
        }

        for (int i = 0; i < data.size(); i += 6) {
            String canSid = GlobalParameters.canSid.get((Integer) data.get(i));
            Number data1 = (Number) data.get(i + 1);
            Number data2 = (Number) data.get(i + 2);
            String srcModule = "";
            for (Map.Entry<String, Integer> entry : GlobalParameters.canModuleTypes.entrySet()) {
                if (entry.getValue() == data.get(i + 3)) {
                    srcModule = entry.getKey();
                    break;
                }
            }
            Integer serialNb = (Integer) data.get(i + 4);
            Integer counter = (Integer) data.get(i + 5);

            BroadcastMessage broadcastMessage = new BroadcastMessage(canSid, data1, data2, srcModule, serialNb, counter);

            Intent intent = new Intent(canSid);
            intent.addCategory(srcModule);
            intent.addCategory(serialNb.toString());
            intent.putExtra("data", Parcels.wrap(broadcastMessage));
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }

    }

    public static void logToDispatch(List<Object> data) {

        if (GlobalParameters.canSid == null
                || GlobalParameters.canDataTypes == null
                || GlobalParameters.canMsgDataTypes == null) {
            return;
        }

        for (int i = 0; i < data.size(); i += 6) {
            String canSid = GlobalParameters.canSid.get((Integer) data.get(i));
            Number data1 = (Number) data.get(i + 1);
            Number data2 = (Number) data.get(i + 2);
            ModuleType moduleSource = ModuleType.getValue((Integer) data.get(i + 3));
            Integer noSerieSource = (Integer) data.get(i + 4);
            Integer counter = (Integer) data.get(i + 5);

            BroadcastMessage broadcastMessage = new BroadcastMessage(canSid, data1, data2, moduleSource, noSerieSource, counter);

            Intent intent = new Intent(GlobalParameters.CATEGORY_FOR_DISPATCH);
            intent.addCategory(GlobalParameters.CATEGORY_FOR_DISPATCH);
            intent.putExtra("data", Parcels.wrap(broadcastMessage));
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }

    }

    public static void moduleToDispatch(List<Object> data) {

        if (GlobalParameters.canModuleTypes == null) {
            return;
        }


        for (int i = 0; i < data.size(); i += 2) {
            String srcModule = "";
            for (Map.Entry<String, Integer> entry : GlobalParameters.canModuleTypes.entrySet()) {
                if (entry.getValue() == ((Integer)data.get(i) >> 16)) {
                    srcModule = entry.getKey();
                    break;
                }
            }
            Integer serialNb = (Integer) data.get(i) & 0x0000FFFF;
            Integer counter = (Integer) data.get(i + 1);

            ModuleMessage moduleMessage = new ModuleMessage(srcModule, serialNb, counter);

            Intent intent = new Intent(srcModule);
            intent.putExtra("data", Parcels.wrap(moduleMessage));
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }


    }

}
