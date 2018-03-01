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

    public static void setContext(Context context) {
        DataDispatcher.context = context.getApplicationContext();
    }

    static public void dataToDispatch(List<Object> data) {
        Intent intent = new Intent((String) data.get(0));
        intent.putExtra("data", Parcels.wrap(data));
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

}
