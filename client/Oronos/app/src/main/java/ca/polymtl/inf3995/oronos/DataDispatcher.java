package ca.polymtl.inf3995.oronos;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import org.parceler.Parcels;

import java.util.List;

/**
 * Created by prst on 2018-03-01.
 */

public class DataDispatcher {

    static private Context context;
    static private final ModuleType[] ModuleTypeValues = ModuleType.values();

    public static void setContext(Context context) {
        DataDispatcher.context = context.getApplicationContext();
    }

    public static void dataToDispatch(List<Object> data) {

        if (GlobalParameters.canSid == null
                || GlobalParameters.canDataTypes == null
                || GlobalParameters.canMsgDataTypes == null) {
            return;
        }

        for (int i = 0; i < data.size(); i += 6) {
            String canSid = GlobalParameters.canSid.get((Integer) data.get(i));
            Number data1 = (Number) data.get(i + 1);
            Number data2 = (Number) data.get(i + 2);
            //ModuleType moduleSource = ModuleTypeValues[(Integer) data.get(i + 3)];
            ModuleType moduleSource = ModuleType.UNKNOWN_MODULE;
            Integer noSerieSource = (Integer) data.get(i + 4);
            Integer counter = (Integer) data.get(i + 5);

            BroadcastMessage broadcastMessage = new BroadcastMessage(canSid, data1, data2, moduleSource, noSerieSource, counter);

            Intent intent = new Intent(canSid);
            intent.putExtra("data", Parcels.wrap(broadcastMessage));
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);


        }

    }

}
