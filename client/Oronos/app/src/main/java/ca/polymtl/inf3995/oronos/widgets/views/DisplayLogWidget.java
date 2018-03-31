package ca.polymtl.inf3995.oronos.widgets.views;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import ca.polymtl.inf3995.oronos.R;
import ca.polymtl.inf3995.oronos.services.BroadcastMessage;
import ca.polymtl.inf3995.oronos.services.DataDispatcher;
import ca.polymtl.inf3995.oronos.utils.GlobalParameters;
import ca.polymtl.inf3995.oronos.widgets.containers.AbstractWidgetContainer;
import timber.log.Timber;

public class DisplayLogWidget extends AbstractWidgetContainer<CAN> implements ContainableWidget, DataDispatcher.CANDataListener {
    private final int MAX_LINES = 5000;
    private final ConcurrentHashMap<String, Integer> lastMsgsReceived;
    private Timer listUpdater;

    private ListView listView;
    private LinkedBlockingQueue<String> msgQueue;
    private ArrayAdapter<String> listAdapter;

    public DisplayLogWidget(Context context, List<CAN> list) {
        super(context, list);
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        CoordinatorLayout coordinatorLayout = new CoordinatorLayout(getContext());
        coordinatorLayout.setLayoutParams(new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        lastMsgsReceived = new ConcurrentHashMap<>();
        msgQueue = new LinkedBlockingQueue<>(MAX_LINES);
        listView = new ListView(getContext());
        listAdapter = new ArrayAdapter<>(getContext(), R.layout.display_log_textview);
        listView.setAdapter(listAdapter);
        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
        listView.setLayoutParams(new ListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        coordinatorLayout.addView(listView);

        LayoutInflater.from(getContext()).inflate(R.layout.scroll_down_fab, coordinatorLayout, true);
        coordinatorLayout.findViewById(R.id.scroll_fab).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listView.setSelection(listAdapter.getCount()-1);
            }
        });

        addView(coordinatorLayout);
        DataDispatcher.registerCANDataListener(this);
    }


    public void receiveMsgToLog(BroadcastMessage msg) {
        final String canSID = msg.getCanSid();
        final String newData1 = Double.toString(msg.getData1().doubleValue());
        final String newData2 = Double.toString(msg.getData2().doubleValue());
        final String module = msg.getSourceModule();
        final String noSerie = Integer.toString(msg.getSerialNb());
        final String currentTime = Calendar.getInstance().getTime().toString();
        if (msgQueue.remainingCapacity() == 0) {
            msgQueue.poll();
        }
        try {
            msgQueue.put(String.format("%s\n%s;%s;%s;%s;%s;", currentTime, module, noSerie, canSID, newData1, newData2));
        } catch (InterruptedException e) {
            Timber.e("Message queue was interrupted in DisplayLogWidget");
        }
    }

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
                        listAdapter.clear();
                        listAdapter.addAll(msgQueue);
                        listAdapter.notifyDataSetChanged();
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

    @Override
    public void onCANDataReceived(BroadcastMessage msg) {
        String canSID = msg.getCanSid();
        String module = msg.getSourceModule();
        Integer noSerie = msg.getSerialNb();
        Integer counter = msg.getCounter();

        Integer oldCounter = lastMsgsReceived.get(canSID + module + noSerie);
        if (oldCounter == null || !oldCounter.equals(counter)) {
            receiveMsgToLog(msg);
            lastMsgsReceived.put(canSID + module + noSerie, counter);
        }
    }

    @Override
    public List<String> getCANSidList() {
        if (list.isEmpty()) {
            return null;
        }
        ArrayList<String> sidList = new ArrayList<>();
        for (CAN can : list) {
            sidList.add(can.getId());
        }
        return sidList;
    }

    @Override
    public String getSourceModule() {
        return null;
    }

    @Override
    public String getSerialNumber() {
        return null;
    }
}
