package ca.polymtl.inf3995.oronos.widgets.views;

import android.annotation.SuppressLint;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ca.polymtl.inf3995.oronos.services.BroadcastMessage;
import ca.polymtl.inf3995.oronos.services.DataDispatcher;
import ca.polymtl.inf3995.oronos.utils.CANCustomUpdate;
import timber.log.Timber;

/**
 * Created by Felix on 15/févr./2018.
 */

public class CAN implements ContainableWidget, DataDispatcher.CANDataListener {
    private final String id;
    private final String name;
    private final String display;
    private final String minAcceptable;
    private final String maxAcceptable;
    private final String chiffresSign;
    private final String specificSource;
    private final String serialNb;
    private final String customUpdate;
    private final String customUpdateParam;
    private final String updateEach;
    private final String customAcceptable;
    private String unit;
    private String dataToDisplay;
    private DisplayState state;
    private boolean hasChanged = false;

    public CAN(String id, @Nullable String name, @Nullable String display,
               @Nullable String minAcceptable, @Nullable String maxAcceptable,
               @Nullable String chiffresSign, @Nullable String specificSource,
               @Nullable String serialNb, @Nullable String customUpdate,
               @Nullable String customUpdateParam, @Nullable String updateEach,
               @Nullable String customAcceptable) {

        this.id = id;
        this.name = name;
        this.display = display;
        this.minAcceptable = minAcceptable;
        this.maxAcceptable = maxAcceptable;
        this.chiffresSign = chiffresSign;
        this.specificSource = specificSource;
        this.serialNb = serialNb;
        this.customUpdate = customUpdate;
        this.customUpdateParam = customUpdateParam;
        this.updateEach = updateEach;
        this.customAcceptable = customAcceptable;
        dataToDisplay = "-";
        if (display != null) {
            String[] dataSplit = this.display.split(" ");
            if (dataSplit.length == 2) {
                unit = dataSplit[1];
            } else {
                unit = "";
            }
        } else {
            unit = "";
        }
        state = DisplayState.NONE;
        DataDispatcher.registerCANDataListener(this);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
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

    public boolean isChanged() {
        return hasChanged;
    }

    public void notifyReset() {
        this.hasChanged = false;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onCANDataReceived(BroadcastMessage msg) {
        double newData = 0.0;
        String formattedData;

        if (customUpdate != null) {

            newData = msg.getData1().doubleValue();
            String[] dataAndUnit;
            String updated;

            if (customUpdateParam != null) {
                updated = CANCustomUpdate.updateWithParam(customUpdate, customUpdateParam, msg);
            } else {
                updated = CANCustomUpdate.update(customUpdate, msg);
            }
            if (updated != null) {
                dataAndUnit = updated.split(" ");
                formattedData = dataAndUnit[0];
                if (dataAndUnit.length == 2) {
                    unit = dataAndUnit[1];
                }
            } else {
                formattedData = dataToDisplay;
            }

        } else if (display != null) {
            if (display.startsWith("__DATA1__")) {
                newData = msg.getData1().doubleValue();
            } else if (display.startsWith("__DATA2__")) {
                newData = msg.getData2().doubleValue();
            }

            if (chiffresSign != null) {
                String signFormat = "%." + chiffresSign + "f";
                formattedData = String.format(signFormat, newData);
            } else {
                formattedData = String.format("%f", newData);
            }

        } else {
            formattedData = "N/A";
        }

        if (!formattedData.equals(dataToDisplay)) {
            dataToDisplay = formattedData;
            hasChanged = true;
        }

        try {
            if (customAcceptable != null) {
                if (!CANCustomUpdate.acceptable(customAcceptable, msg)) {
                    state = DisplayState.RED;
                } else {
                    state = DisplayState.GREEN;
                }
            } else if ((minAcceptable != null && newData < Double.parseDouble(minAcceptable))
                    || (maxAcceptable != null && newData > Double.parseDouble(maxAcceptable))) {
                state = DisplayState.RED;
            } else {
                state = DisplayState.GREEN;
            }

        } catch (NumberFormatException e) {
            Timber.e("Error: Could not update CAN tag state due to NumberFormatException.");
        }
    }

    @Override
    public List<String> getCANSidList() {
        return new ArrayList<>(Arrays.asList(id));
    }

    @Override
    public String getSourceModule() {
        return specificSource;
    }

    @Override
    public String getSerialNumber() {
        return serialNb;
    }

    public enum DisplayState {
        NONE, GREEN, RED
    }


}
