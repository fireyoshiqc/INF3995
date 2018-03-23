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

    public static void dataToDispatch(List<Object> data, boolean isModulePing) {

        if (GlobalParameters.canSid == null
                || GlobalParameters.canDataTypes == null
                || GlobalParameters.canMsgDataTypes == null
                || GlobalParameters.canModuleTypes == null) {
            return;
        }

        for (int i = 0; i < data.size(); i += (isModulePing ? 3 : 6)) {
            String canSid = isModulePing ? null : GlobalParameters.canSid.get((Integer) data.get(i));
            Number data1 = isModulePing ? null : (Number) data.get(i + 1);
            Number data2 = isModulePing ? null : (Number) data.get(i + 2);
            String moduleSource = "";
            for (Map.Entry<String, Integer> entry : GlobalParameters.canModuleTypes.entrySet()) {
                if (entry.getValue() == (isModulePing ? data.get(i) : data.get(i + 3))) {
                    moduleSource = entry.getKey();
                    break;
                }
            }
            Integer noSerieSource = isModulePing ? (Integer) data.get(i + 1) : (Integer) data.get(i + 4);
            Integer counter = isModulePing ? (Integer) data.get(i + 2) : (Integer) data.get(i + 5);

            BroadcastMessage broadcastMessage = new BroadcastMessage(canSid, data1, data2, moduleSource, noSerieSource, counter);

            Intent intent;
            if (!isModulePing) {
                intent = new Intent(canSid);
                intent.addCategory(moduleSource);
                intent.addCategory(noSerieSource.toString());

            } else {
                intent = new Intent(moduleSource);
            }
            intent.putExtra("data", Parcels.wrap(broadcastMessage));
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }

    }
}
