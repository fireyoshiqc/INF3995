package ca.polymtl.inf3995.oronos.widgets.views;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ca.polymtl.inf3995.oronos.widgets.adapters.DisplayLogAdapter;
import ca.polymtl.inf3995.oronos.widgets.containers.AbstractWidgetContainer;

import ca.polymtl.inf3995.oronos.services.BroadcastMessage;
import ca.polymtl.inf3995.oronos.utils.GlobalParameters;

public class DisplayLogWidget extends AbstractWidgetContainer<CAN> implements ContainableWidget {
    private RecyclerView recycler;
    private Timer listUpdater;
    private DisplayLogAdapter adapter;
    private List<MSGPair> lastMsgsReceived;
    private Context context;

    /**
     * MSGPair stocks all necessary data to distinguish 2 logs and register the newest.
     */
    private class MSGPair {
        private final String  cansid;
        private final String  moduleType;
        private final Integer noSerial;
        private int           lastNoMsg;

        private MSGPair(String acansid, String amodule, Integer aNoSerial, int aNoMsg) {
            cansid     = acansid;
            moduleType = amodule;
            noSerial   = aNoSerial;
            lastNoMsg  = aNoMsg;
        }

            private String      getCansid()                { return cansid; }
            private String      getModuleType()            { return moduleType; }
            private Integer     getNoSerial()              { return noSerial; }
            private int         getLastNoMsg()             { return lastNoMsg; }
            private void        setLastNoMsg(int newNoMsg) { lastNoMsg = newNoMsg; }
    }

    public DisplayLogWidget(Context context, List<CAN> list) {
        super(context, list);

        this.context     = context;
        lastMsgsReceived = new ArrayList<>();
        recycler         = new RecyclerView(context);
        adapter          = new DisplayLogAdapter(context);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recycler.setLayoutManager(linearLayoutManager);
        recycler.setAdapter(adapter);
        recycler.setLayoutParams(new RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT));
        recycler.setNestedScrollingEnabled(false);
        addView(recycler);
        setUpBroadcast();
    }

        private void setUpBroadcast() {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(GlobalParameters.CATEGORY_FOR_DISPATCH);
            intentFilter.addCategory(GlobalParameters.CATEGORY_FOR_DISPATCH);
            LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver, intentFilter);
        }

        public void receiveMsgToLog(BroadcastMessage msg) {
            String     canSID   = msg.getCanSid();
            String     newData1 = Double.toString(msg.getData1().doubleValue());
            String     newData2 = Double.toString(msg.getData2().doubleValue());
            String     module   = msg.getSourceModule();
            String     noSerie  = Integer.toString(msg.getSerialNb());

            adapter.addCSVMsg(
                module + ";" + noSerie + ";" + canSID + ";" + newData1 + ";" + newData2 + ";\n");
        }

        private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                BroadcastMessage msg = Parcels.unwrap(intent.getParcelableExtra("data"));
                String     canSID   = msg.getCanSid();
                String module   = msg.getSourceModule();
                Integer    noSerie  = msg.getSerialNb();
                Integer    counter  = msg.getCounter();
                boolean isDesiredCanMsg = false;

                for (CAN can : list) {
                    if (canSID.equals(can.getId())) {
                        isDesiredCanMsg = true;
                    }
                }

                if (list.size() == 0 || isDesiredCanMsg) {
                    Boolean isNewMsgToLog = true;
                    Boolean isNewMsgReceived = true;

                    for (MSGPair msgPair : lastMsgsReceived) {
                        if (canSID.equals(msgPair.getCansid())
                            && module.equals(msgPair.getModuleType())
                            && noSerie.equals(msgPair.getNoSerial())) {
                            isNewMsgReceived = false;
                            if (counter != msgPair.getLastNoMsg()) {
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
            }
        };

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startUpdateTask();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopUpdateTask();
    }

    private void startUpdateTask() {
        listUpdater = new Timer(true);
        TimerTask sensorTask = new TimerTask() {
            @Override
            public void run() {
                ((Activity) getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        };
        listUpdater.scheduleAtFixedRate(sensorTask, 0, GlobalParameters.DATA_UPDATE_PERIOD);
    }

    /**
     * This method stops the task started by the method above.
     */
     private void stopUpdateTask() {
         if (listUpdater != null) {
             listUpdater.cancel();
             listUpdater.purge();
         }
    }
}
