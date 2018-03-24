package ca.polymtl.inf3995.oronos.widgets.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import ca.polymtl.inf3995.oronos.utils.GlobalParameters;

/**
 * This Adapter allows to display a grid containing small cards representing PCBs.
 * Each card must indicate the PCB name, the PCB serial number and the PCB status (between ONLINE,
 * DELAY and OFFLINE).
 * If the PCB status is ONLINE, the background of the card must be GREEN.
 * If the PCB status is DELAY, the background of the card must be ORANGE.
 * If the PCB status is OFFLINE, the background of the card must be RED.
 */
public class ModuleStatusAdapter extends BaseAdapter {
    private Context mContext;
    private int nGrid;
    private int nColumns;
    private List<PCBPair> PCBList;

    /**
     * Constructor for this class; supposedly called by ModuleStatus.java
     *
     * @param c        Context of the activity.
     * @param nGrid    int number of lines in the grid.
     * @param nColumns int number of columns in the grid.
     */
    public ModuleStatusAdapter(Context c, int nGrid, int nColumns) {
        mContext = c;
        this.nGrid = nGrid;
        this.nColumns = nColumns;
        PCBList = new ArrayList<>();
    }

    public int getCount() {
        return this.nGrid * this.nColumns;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    /**
     * Access point to this class; when new message received from server, please call this method.
     * Provide the following :
     *
     * @param PCBname  String Name of PCB.
     * @param noSerial int Serial number of PCB.
     * @param noMsg    int number of messages received from this PCB.
     */
    public void receiveItem(String PCBname, int noSerial, int noMsg) {
        int position = findPositionOfPCB(PCBname, noSerial, noMsg);
        if (position < getCount()) {
            PCBPair pcb = PCBList.get(position);
            StatusOfPCB oldStatus = pcb.getStatus();
            if (pcb.getLastNoMsg() != noMsg) {
                pcb.setLastNoMsg(noMsg);
                pcb.setLastTimeSeen(DateTime.now());
                pcb.setStatus(StatusOfPCB.ONLINE);
            } else {
                if (DateTime.now().getMillis() - pcb.getLastTimeSeen().getMillis()
                        < GlobalParameters.ONLINE_TO_DELAY) {
                    pcb.setStatus(StatusOfPCB.ONLINE);
                } else if (DateTime.now().getMillis() - pcb.getLastTimeSeen().getMillis()
                        < GlobalParameters.DELAY_TO_OFFLINE) {
                    pcb.setStatus(StatusOfPCB.DELAY);
                } else {
                    pcb.setStatus(StatusOfPCB.OFFLINE);
                }
            }
            if (oldStatus != pcb.getStatus()) {
                this.notifyDataSetChanged();
            }
        }
    }

    /**
     * create a new ImageView for each item referenced by the Adapter
     *
     * @param position    int Position of the item in the grid viewed as 1D array.
     * @param convertView View The view of the item itself, if already created.
     * @param parent      ViewGroup parent of this item.
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        CardView cardView;
        TextView textView;

        if (convertView == null) {
            textView = new TextView(mContext);
            cardView = new CardView(mContext);
            textView.setPadding(8, 8, 8, 8);
            setPCBText(textView, position);
            cardView.addView(textView);
        } else {
            cardView = (CardView) convertView;
            setPCBText((TextView) cardView.getChildAt(0), position);
        }
        if (PCBList.size() > position) {
            cardView.setCardBackgroundColor(PCBList.get(position).getStatus().toColor());
        } else {
            cardView.setCardBackgroundColor(Color.TRANSPARENT);
        }
        return cardView;
    }

    private void addItem(String PCBname, int noSerial, int noMsg) {
        if (PCBList.size() < getCount()) {
            PCBPair pcbPair = new PCBPair(PCBname, noSerial, noMsg);
            PCBList.add(pcbPair);
            this.notifyDataSetChanged();
        }
    }

    private int findPositionOfPCB(String PCBname, int noSerial, int noMsg) {
        int index;
        for (index = 0; index < PCBList.size(); index++) {
            if (PCBList.get(index).getNoSerial() == noSerial
                    && PCBList.get(index).getPCBName().equals(PCBname)) {
                break;
            }
        }
        if (index == PCBList.size()) {
            addItem(PCBname, noSerial, noMsg);
        }
        return index;
    }

    private void setPCBText(TextView tv, int position) {
        if (PCBList.size() > position) {
            String toDisplay = PCBList.get(position).getPCBName()
                    + " ("
                    + Integer.toString(PCBList.get(position).getNoSerial())
                    + ")\n"
                    + PCBList.get(position).getStatus().toString();
            tv.setText(toDisplay);
        }
    }

    /**
     * StatusOfPCB represent each possible state with a color and a string.
     */
    private enum StatusOfPCB {
        ONLINE("ONLINE", GlobalParameters.GREEN_STATUS),
        DELAY("DELAY", GlobalParameters.ORANGE_STATUS),
        OFFLINE("OFFLINE", GlobalParameters.RED_STATUS);

        private final String text;
        private final int color;

        StatusOfPCB(final String text, final int color) {
            this.text = text;
            this.color = color;
        }

        @Override
        public String toString() {
            return text;
        }

        public int toColor() {
            return color;
        }
    }

    /**
     * PCBPair stocks all data regarding a PCB and its state.
     */
    private class PCBPair {
        private final String name;
        private final int noSerial;
        private StatusOfPCB status;
        private int lastNoMsg;
        private DateTime lastTimeSeen;

        private PCBPair(String aName, int aNoSerial, int aNoMsg) {
            name = aName;
            noSerial = aNoSerial;
            status = StatusOfPCB.ONLINE;
            lastNoMsg = aNoMsg;
            lastTimeSeen = new DateTime();
        }

        private String getPCBName() {
            return name;
        }

        private int getNoSerial() {
            return noSerial;
        }

        private StatusOfPCB getStatus() {
            return status;
        }

        private void setStatus(StatusOfPCB newStatus) {
            status = newStatus;
        }

        private int getLastNoMsg() {
            return lastNoMsg;
        }

        private void setLastNoMsg(int newNoMsg) {
            lastNoMsg = newNoMsg;
        }

        private DateTime getLastTimeSeen() {
            return lastTimeSeen;
        }

        private void setLastTimeSeen(DateTime newDate) {
            lastTimeSeen = newDate;
        }
    }

}

