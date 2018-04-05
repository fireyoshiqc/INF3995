package ca.polymtl.inf3995.oronos.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import ca.polymtl.inf3995.oronos.utils.GlobalParameters;
import timber.log.Timber;

/**
 * <h1>Data Dispatcher</h1>
 * The Data Dispatcher receives raw OSC data from the Socket Client and manage the listener for each
 * message type. It must create the associated message type (either Broadcast Message for can data
 * or Module Message for module data).
 *
 *
 * @author  Félix Boulet, Justine Pepin, Patrick Richer St-Onge
 * @version 0.0
 * @since   2018-04-12
 **/
public class DataDispatcher {

    static private List<CANDataListener> canDataListeners = new CopyOnWriteArrayList<>();
    static private List<ModuleDataListener> moduleDataListeners = new CopyOnWriteArrayList<>();

    /**
     * This method adds a listener to the list of CAN Data Listeners.
     *
     * @param listener the listener to register to the list of CAN Data Listeners.
     * */
    public static void registerCANDataListener(CANDataListener listener) {
        if (!canDataListeners.contains(listener)) {
            canDataListeners.add(listener);
        } else {
            Timber.w("Trying to add the same listener twice to DataDispatcher.");
        }

    }

    /**
     * This method removes a listener from the list of CAN Data Listeners.
     *
     * @param listener the listener to unregister from the list of CAN Data Listeners.
     * */
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

    /**
     * This method removes all listeners from the list of CAN Data Listeners and the list of Module
     * Data Listeners.
     * */
    public static void clearAllListeners() {
        canDataListeners.clear();
        moduleDataListeners.clear();
    }

    /**
     * This method receives as an argument data to send to every listener on the list of CAN Data
     * Listeners.
     *
     * @param data the data of a CAN message in list form.
     * */
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

    /**
     * This method receives as an argument data to send to every listener on the list of Module Data
     * Listeners.
     *
     * @param data the data of a module message in list form.
     * */
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

    /**
     * CAN Data Listener interface that every tag that wants to listen for CAN messages should
     * implement.
     * */
    public interface CANDataListener {
        void onCANDataReceived(BroadcastMessage msg);

        List<String> getCANSidList();

        String getSourceModule();

        String getSerialNumber();
    }

    /**
     * Module Data Listener interface that every tag that wants to listen for module messages should
     * implement.
     * */
    public interface ModuleDataListener {
        void onModuleDataReceived(ModuleMessage msg);
    }

}
