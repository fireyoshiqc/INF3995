package ca.polymtl.inf3995.oronos.widgets.views;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import ca.polymtl.inf3995.oronos.services.BroadcastMessage;
import ca.polymtl.inf3995.oronos.services.DataDispatcher;
import ca.polymtl.inf3995.oronos.utils.GlobalParameters;
import ca.polymtl.inf3995.oronos.widgets.containers.AbstractWidgetContainer;
import timber.log.Timber;

public class DisplayLogWidget extends AbstractWidgetContainer<CAN> implements ContainableWidget, DataDispatcher.CANDataListener {
    private final int MAX_LINES = 5000;
    private final ConcurrentHashMap<String, Integer> lastMsgsReceived;
    private Timer listUpdater;
    private TextView textView;
    private ScrollView scrollView;
    private final StringBuilder display = new StringBuilder();

    public DisplayLogWidget(Context context, List<CAN> list) {
        super(context, list);
        lastMsgsReceived = new ConcurrentHashMap<>();
        textView = new TextView(context);
        scrollView = new ScrollView(context);
        scrollView.addView(textView);
        this.addView(scrollView);
        DataDispatcher.registerCANDataListener(this);
    }


    public void receiveMsgToLog(BroadcastMessage msg) {
        String canSID = msg.getCanSid();
        String newData1 = Double.toString(msg.getData1().doubleValue());
        String newData2 = Double.toString(msg.getData2().doubleValue());
        String module = msg.getSourceModule();
        String noSerie = Integer.toString(msg.getSerialNb());

        String currentTime = Calendar.getInstance().getTime().toString();
        synchronized (display) {
            display.append(String.format("%s;%s;%s;%s;%s;%s;", currentTime, module, noSerie, canSID, newData1, newData2)).append("\n");
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startUpdateTask();
        autoscroll();
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
                textView.post(new Runnable() {
                    @Override
                    public void run() {
                        textView.append(display);
                        if (textView.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()) <= 100) {
                            autoscroll();
                        }
                        display.setLength(0);
                    }
                });
            }
        };
        listUpdater.scheduleAtFixedRate(sensorTask, 0, GlobalParameters.DATA_UPDATE_PERIOD);
    }

    private void autoscroll() {
        scrollView.post(new Runnable() {
            public void run() {
                scrollView.smoothScrollTo(0, textView.getBottom());
            }
        });
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
