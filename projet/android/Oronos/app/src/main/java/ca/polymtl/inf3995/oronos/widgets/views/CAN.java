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
 * <h1>CAN</h1>
 * This class represents the content of a CAN message as received from the server.
 *
 * @author Félix Boulet
 * @version 0.0
 * @since 2018-04-12
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

    /**
     * Constructor that needs any field that can be found in the CAN message as a
     * string given as an argument.
     *
     * @param id the id of the can message.
     * @param name the name of the can message.
     * @param display display of the message.
     * @param minAcceptable the minimal value the CAN message can have before turning red.
     * @param maxAcceptable the maximal value the CAN message can have before turning green.
     * @param chiffresSign the number of decimal number the value of the CAN message is going
     *                     to be displayed.
     * @param specificSource the specific source name of the emitting pcb.
     * @param serialNb the serial number of the pcb emitting the value.
     * */
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

    /**
     * Accessor for the CAN id.
     * */
    public String getId() {
        return id;
    }

    /**
     * Accessor for the CAN name.
     * */
    public String getName() {
        return name;
    }

    /**
     * Accessor for the CAN data.
     * */
    public String getDataToDisplay() {
        return dataToDisplay;
    }

    /**
     * Accessor for the CAN data unit.
     * */
    public String getUnit() {
        return unit;
    }

    /**
     * Accessor for the CAN state (RED if under min value or over max value,
     * GREEN if between those two and NULL --> grey if no value have been received
     * yet).
     * */
    public DisplayState getState() {
        return state;
    }

    /**
     * This method return true if the value of the CAN data has changed compared to the
     * last displayed value.
     * */
    public boolean isChanged() {
        return hasChanged;
    }

    /**
     * This method is called when the displayed value has been replaced with the new value to
     * notify that the change has been made.
     * */
    public void notifyReset() {
        this.hasChanged = false;
    }

    /**
     * This method is taking care of the reception of a broadcast message. It ensures the
     * data is valid and it chooses a display state for it.
     *
     * @param msg the broadcast message to display.
     * */
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

    /**
     * Accessor for the CAN sid list.
     * */
    @Override
    public List<String> getCANSidList() {
        return new ArrayList<>(Arrays.asList(id));
    }

    /**
     * Accessor for the emitting pcb name.
     * */
    @Override
    public String getSourceModule() {
        return specificSource;
    }

    /**
     * Accessor for the emitting pcb serial number.
     * */
    @Override
    public String getSerialNumber() {
        return serialNb;
    }

    /**
     * Enum that is all the possible display states a CAN message can have.
     * RED : data value is not between the permitted range.
     * GREEN : data value is between the permitted range.
     * NONE : no data value received yet. Default state.
     * */
    public enum DisplayState {
        NONE, GREEN, RED
    }


}
