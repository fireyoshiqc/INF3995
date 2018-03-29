package ca.polymtl.inf3995.oronos.widgets.views;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;

import ca.polymtl.inf3995.oronos.R;
import ca.polymtl.inf3995.oronos.services.BroadcastMessage;
import ca.polymtl.inf3995.oronos.utils.GlobalParameters;
import ca.polymtl.inf3995.oronos.widgets.containers.AbstractWidgetContainer;

public class DisplayLogWidget extends AbstractWidgetContainer<CAN> implements ContainableWidget {
    private Timer listUpdater;
    private List<MSGPair> lastMsgsReceived;
    private Context context;
    private Queue<String> msgQueue;
    private TextView textView;
    private ScrollView scrollView;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BroadcastMessage msg = Parcels.unwrap(intent.getParcelableExtra("data"));
            String canSID = msg.getCanSid();
            String module = msg.getSourceModule();
            Integer noSerie = msg.getSerialNb();
            Integer counter = msg.getCounter();
            boolean isDesiredCanMsg = false;

            for (CAN can : list) {
                if (canSID.equals(can.getId())) {
                    isDesiredCanMsg = true;
                    break;
                }
            }

            if (list.isEmpty() || isDesiredCanMsg) {
                boolean isNewMsgToLog = true;
                boolean isNewMsgReceived = true;

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

    public DisplayLogWidget(Context context, List<CAN> list) {
        super(context, list);

        this.context = context;
        lastMsgsReceived = new ArrayList<>();
        msgQueue = new LinkedBlockingQueue<>(GlobalParameters.LIMIT_OF_N_MSG);
        textView = new TextView(context);
        scrollView = new ScrollView(context);
        scrollView.addView(textView);
        this.addView(scrollView);
        setUpBroadcast();
    }

    private void setUpBroadcast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(GlobalParameters.CATEGORY_FOR_DISPATCH);
        intentFilter.addCategory(GlobalParameters.CATEGORY_FOR_DISPATCH);
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver, intentFilter);
    }

    public void receiveMsgToLog(BroadcastMessage msg) {
        String canSID = msg.getCanSid();
        String newData1 = Double.toString(msg.getData1().doubleValue());
        String newData2 = Double.toString(msg.getData2().doubleValue());
        String module = msg.getSourceModule();
        String noSerie = Integer.toString(msg.getSerialNb());

        if (GlobalParameters.LIMIT_OF_N_MSG - msgQueue.size() == 0) {
            msgQueue.poll();
        }
        String currentTime = Calendar.getInstance().getTime().toString();
        msgQueue.add(String.format("%s;%s;%s;%s;%s;%s;",currentTime, module, noSerie, canSID, newData1, newData2));
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
                final StringBuilder display = new StringBuilder();
                for (String msg : msgQueue) {
                    display.append(msg).append("\n");
                }
                ((Activity) getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (getVisibility() == View.VISIBLE) {
                            textView.setText(display);
                            if (textView.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()) <= 50) {
                                autoscroll();
                            }
                        }
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

    /**
     * MSGPair stocks all necessary data to distinguish 2 logs and register the newest.
     */
    private class MSGPair {
        private final String cansid;
        private final String moduleType;
        private final Integer noSerial;
        private int lastNoMsg;

        private MSGPair(String acansid, String amodule, Integer aNoSerial, int aNoMsg) {
            cansid = acansid;
            moduleType = amodule;
            noSerial = aNoSerial;
            lastNoMsg = aNoMsg;
        }

        private String getCansid() {
            return cansid;
        }

        private String getModuleType() {
            return moduleType;
        }

        private Integer getNoSerial() {
            return noSerial;
        }

        private int getLastNoMsg() {
            return lastNoMsg;
        }

        private void setLastNoMsg(int newNoMsg) {
            lastNoMsg = newNoMsg;
        }
    }
}
