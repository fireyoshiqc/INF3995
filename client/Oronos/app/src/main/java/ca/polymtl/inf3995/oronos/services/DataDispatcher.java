package ca.polymtl.inf3995.oronos.services;

import android.provider.Settings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import ca.polymtl.inf3995.oronos.utils.GlobalParameters;
import timber.log.Timber;

/**
 * Created by prst on 2018-03-01.
 */

public class DataDispatcher {

    static private List<CANDataListener> canDataListeners = new CopyOnWriteArrayList<>();
    static private List<ModuleDataListener> moduleDataListeners = new CopyOnWriteArrayList<>();

    public static void registerCANDataListener(CANDataListener listener) {
        if (!canDataListeners.contains(listener)) {
            canDataListeners.add(listener);
        } else {
            Timber.w("Trying to add the same listener twice to DataDispatcher.");
        }

    }

    public static void unregisterCANDataListener(CANDataListener listener) {
        canDataListeners.remove(listener);
    }

    public static void registerModuleDataListener(ModuleDataListener listener) {
        if (!moduleDataListeners.contains(listener)) {
            moduleDataListeners.add(listener);
        } else {
            Timber.w("Trying to add the same listener twice to DataDispatcher.");
        }
    }

    public static void unregisterModuleDataListener(ModuleDataListener listener) {
        moduleDataListeners.remove(listener);
    }

    public static void clearAllListeners() {
        canDataListeners.clear();
        moduleDataListeners.clear();
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
            for (CANDataListener listener : canDataListeners) {
                String givenModule = listener.getSourceModule();
                List<String> compatibleModules = new ArrayList<>();
                if (GlobalParameters.canModuleTypes != null && givenModule != null) {
                    Integer moduleValue = GlobalParameters.canModuleTypes.get(givenModule);
                    if (moduleValue != null) {
                        for (Map.Entry<String, Integer> entry : GlobalParameters.canModuleTypes.entrySet()) {
                            if (entry.getValue().equals(moduleValue)) {
                                compatibleModules.add(entry.getKey());
                            }
                        }
                    } else {
                        Timber.w(String.format("Incompatible source module type present in listener : %s", givenModule));
                        compatibleModules.add(givenModule);
                    }
                } else if (givenModule != null) {
                    compatibleModules.add(givenModule);
                }

                if (listener.getCANSidList() == null || listener.getCANSidList().contains(canSid)) {
                    if (givenModule == null || compatibleModules.contains(srcModule)) {
                        if (listener.getSerialNumber() == null || listener.getSerialNumber().equals(serialNb.toString())) {
                            listener.onCANDataReceived(broadcastMessage);
                        }
                    }
                }
            }
        }
    }

    public static void moduleToDispatch(List<Object> data) {

        if (GlobalParameters.canModuleTypes == null) {
            return;
        }
        for (int i = 0; i < data.size(); i += 2) {
            String srcModule = "";
            for (Map.Entry<String, Integer> entry : GlobalParameters.canModuleTypes.entrySet()) {
                if (entry.getValue() == ((Integer) data.get(i) >> 16)) {
                    srcModule = entry.getKey();
                    break;
                }
            }
            Integer serialNb = (Integer) data.get(i) & 0x0000FFFF;
            Integer counter = (Integer) data.get(i + 1);

            ModuleMessage moduleMessage = new ModuleMessage(srcModule, serialNb, counter);
            for (ModuleDataListener listener : moduleDataListeners) {
                listener.onModuleDataReceived(moduleMessage);
            }
        }


    }

    public interface CANDataListener {
        void onCANDataReceived(BroadcastMessage msg);

        List<String> getCANSidList();

        String getSourceModule();

        String getSerialNumber();
    }

    public interface ModuleDataListener {
        void onModuleDataReceived(ModuleMessage msg);
    }

}
