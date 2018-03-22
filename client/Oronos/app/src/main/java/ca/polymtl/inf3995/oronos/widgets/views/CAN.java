package ca.polymtl.inf3995.oronos.widgets.views;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import org.parceler.Parcels;

import ca.polymtl.inf3995.oronos.services.BroadcastMessage;

/**
 * Created by Felix on 15/févr./2018.
 */

public class CAN implements ContainableWidget {
    private final String id;
    private final String name;
    private final String display;
    private final String minAcceptable;
    private final String maxAcceptable;
    private final String chiffresSign;
    private final String specificSource;
    private final String serialNb;
    private final String customUpdate;
    private final String updateEach;
    private BroadcastReceiver broadcastReceiver;
    private String unit;
    private String dataToDisplay;
    private DisplayState state;
    private boolean hasChanged = false;

    public CAN(String id, @Nullable String name, @Nullable String display,
               @Nullable String minAcceptable, @Nullable String maxAcceptable,
               @Nullable String chiffresSign, @Nullable String specificSource,
               @Nullable String serialNb, @Nullable String customUpdate,
               @Nullable String updateEach) {

        this.id = id;
        this.name = name;
        this.display = display;
        this.minAcceptable = minAcceptable;
        this.maxAcceptable = maxAcceptable;
        this.chiffresSign = chiffresSign;
        this.specificSource = specificSource;
        this.serialNb = serialNb;
        this.customUpdate = customUpdate;
        this.updateEach = updateEach;
        dataToDisplay = "";
        if (display != null) {
            String[] dataSplit = this.display.split(" ");
            if (dataSplit.length == 2) {
                unit = dataSplit[1];
            } else {
                unit = "";
            }
        } else {
            unit = "CST";
        }
        state = DisplayState.NONE;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDisplay() {
        return display;
    }

    public String getMinAcceptable() {
        return minAcceptable;
    }

    public String getMaxAcceptable() {
        return maxAcceptable;
    }

    public String getChiffresSign() {
        return chiffresSign;
    }

    public String getSpecificSource() {
        return specificSource;
    }

    public String getSerialNb() {
        return serialNb;
    }

    public String getCustomUpdate() {
        return customUpdate;
    }

    public String getUpdateEach() {
        return updateEach;
    }

    public String getDataToDisplay() {
        return dataToDisplay;
    }

    public String getUnit() {
        return unit;
    }

    public DisplayState getState() {
        return state;
    }

    public boolean updatesAreEnabled() {
        return broadcastReceiver != null;
    }

    public void enableDataDisplayerUpdates(Context context) {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, final Intent intent) {

                BroadcastMessage msg = Parcels.unwrap(intent.getParcelableExtra("data"));
                double newData = 0.0;
                String formattedData = "";

                if (display != null) {

                    switch (display) {
                        case "__DATA1__":
                            newData = msg.getData1().doubleValue();
                            break;
                        case "__DATA2__":
                            newData = msg.getData2().doubleValue();
                            break;
                    }

                    if (chiffresSign != null) {
                        String signFormat = "%." + chiffresSign + "f";
                        formattedData = String.format(signFormat, newData);
                    } else {
                        formattedData = String.format("%f", newData);
                    }

                } else {
                    // TODO: Use customUpdate to generate the data to display.
                    if (chiffresSign != null) {
                        String signFormat = "%." + chiffresSign + "f";
                        formattedData = String.format(signFormat, 0.000000);
                    }
                }

                if (!formattedData.equals(dataToDisplay)) {
                    dataToDisplay = formattedData;
                    hasChanged = true;
                }

                // TODO: Change holder appearance according to minAcceptable and maxAcceptable.
                try {
                    if (minAcceptable != null && maxAcceptable != null) {
                        if (newData < Double.parseDouble(minAcceptable)
                                || newData > Double.parseDouble(maxAcceptable)) {
                            state = DisplayState.RED;
                            //view.setBackgroundResource(R.drawable.can_data_large_border_red);
                            //data.setTextColor(0xFFCC0000);
                        } else {
                            state = DisplayState.GREEN;
                            //view.setBackgroundResource(R.drawable.can_data_large_border_green);
                            //data.setTextColor(Color.BLACK);
                        }
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(id);
        if (specificSource != null) {
            intentFilter.addCategory(specificSource);
        }
        if (serialNb != null) {
            intentFilter.addCategory(serialNb);
        }
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver, intentFilter);

    }

    public void disableDataDisplayerUpdates(Context context) {
        if (broadcastReceiver != null) {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }
    }

    public boolean isChanged() {
        return hasChanged;
    }

    public void notifyReset() {
        this.hasChanged = false;
    }

    public enum DisplayState {
        NONE, GREEN, RED
    }


}
