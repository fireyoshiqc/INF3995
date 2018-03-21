package ca.polymtl.inf3995.oronos.parser;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import ca.polymtl.inf3995.oronos.BroadcastMessage;
import ca.polymtl.inf3995.oronos.ModuleType;

public class DisplayLogWidget extends AbstractWidgetContainer<CAN> implements ContainableWidget {
    RecyclerView recycler;
    DisplayLogAdapter adapter;
    List<MSGPair> lastMsgsReceived;

    /**
     * MSGPair stocks all necessary data to distinguish 2 logs and register the newest.
     * */
    private class MSGPair
    {
        private final String     cansid;
        private final ModuleType moduleType;
        private final Integer    noSerial;
        private int              lastNoMsg;

        private MSGPair(String acansid, ModuleType amodule, Integer aNoSerial, int aNoMsg)
        {
            cansid     = acansid;
            moduleType = amodule;
            noSerial   = aNoSerial;
            lastNoMsg  = aNoMsg;
        }

        private String      getCansid()                { return cansid; }
        private ModuleType  getModuleType()            { return moduleType; }
        private Integer     getNoSerial()              { return noSerial; }
        private int         getLastNoMsg()             {return lastNoMsg; }
        private void        setLastNoMsg(int newNoMsg) { lastNoMsg = newNoMsg; }
    }

    DisplayLogWidget(Context context, List<CAN> list) {
        super(context, list);
        lastMsgsReceived = new ArrayList<>();
        recycler = new RecyclerView(context);
        adapter = new DisplayLogAdapter(context);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);

        recycler.setLayoutManager(linearLayoutManager);
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.setAdapter(adapter);
        recycler.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT));
        recycler.setNestedScrollingEnabled(false);
        addView(recycler);

        IntentFilter intentFilter = new IntentFilter();
        if (list.size() > 0) {
            for (CAN can : list) {
                intentFilter.addAction(can.getId());
                if (can.getSpecificSource() != null) {
                    intentFilter.addCategory(can.getSpecificSource());
                }
                if (can.getSerialNb() != null) {
                    intentFilter.addCategory(can.getSerialNb());
                }
            }
        } else {
            //Todo : add all actions related to all possible can ids.
        }
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver, intentFilter);

    }

    public void receiveMsgToLog(BroadcastMessage msg) {
        String     canSID   = msg.getCanSid();
        String     newData1 = Double.toString(msg.getData1().doubleValue());
        String     newData2 = Double.toString(msg.getData2().doubleValue());
        String     module   = msg.getModuleSource().name();
        String     noSerie  = Integer.toString(msg.getNoSerieSource());
        adapter.addCSVMsg(
                module + ";" + noSerie + ";" + canSID + ";" + newData1 + ";" + newData2 + ";");
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BroadcastMessage msg = Parcels.unwrap(intent.getParcelableExtra("data"));
            String     canSID   = msg.getCanSid();
            ModuleType module   = msg.getModuleSource();
            Integer    noSerie  = msg.getNoSerieSource();
            Integer    counter  = msg.getCounter();
            Boolean isNewMsgToLog = true;
            Boolean isNewMsgReceived = true;

            for (MSGPair msgPair: lastMsgsReceived) {
                if (canSID == msgPair.getCansid()
                        && module == msgPair.getModuleType()
                        && noSerie == msgPair.getNoSerial()) {
                    isNewMsgReceived = false;
                    if (counter > msgPair.getLastNoMsg()) {
                        msgPair.setLastNoMsg(counter);
                        break;
                    } else {
                        isNewMsgToLog = false;
                        break;
                    }
                }
            }

            if (isNewMsgReceived) {
                lastMsgsReceived.add(new MSGPair(canSID, module, noSerie, counter));
            }
            if (isNewMsgToLog) {
                receiveMsgToLog(msg);
            }
        }
    };
}
