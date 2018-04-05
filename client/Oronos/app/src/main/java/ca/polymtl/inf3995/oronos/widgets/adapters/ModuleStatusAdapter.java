package ca.polymtl.inf3995.oronos.widgets.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import ca.polymtl.inf3995.oronos.R;
import ca.polymtl.inf3995.oronos.utils.GlobalParameters;

/**
 * This Adapter allows to display a grid containing small cards representing PCBs.
 * Each card must indicate the PCB name, the PCB serial number and the PCB status (between ONLINE,
 * DELAY and OFFLINE).
 * If the PCB status is ONLINE, the background of the card must be GREEN.
 * If the PCB status is DELAY, the background of the card must be ORANGE.
 * If the PCB status is OFFLINE, the background of the card must be RED.
 */
public class ModuleStatusAdapter extends RecyclerView.Adapter<ModuleStatusAdapter.PCBContainer> {
    private Context mContext;
    private int nGrid;
    private int nColumns;
    private List<PCBPair> PCBList;

    class PCBContainer extends RecyclerView.ViewHolder {
        private TextView PCBName;
        private TextView PCBSerialNb;
        private TextView PCBStatusDisplay;

        PCBContainer(final View view) {
            super(view);
            PCBName = view.findViewById(R.id.pcb_name);
            PCBSerialNb = view.findViewById(R.id.pcb_serial);
            PCBStatusDisplay = view.findViewById(R.id.pcb_status);
        }
    }

    /**
     * Constructor for this class; supposedly called by ModuleStatus.java
     *
     * @param context        Context of the activity.
     */
    public ModuleStatusAdapter(Context context, int nGrid, int nColumns) {
        mContext = context;
        this.nGrid = nGrid;
        this.nColumns = nColumns;
        PCBList = new ArrayList<>();
    }


    @Override
    public PCBContainer onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PCBContainer(LayoutInflater.from(parent.getContext()).inflate(R.layout.module_status_card, parent, false));
    }

    @Override
    public void onBindViewHolder(PCBContainer holder, int position) {
        PCBPair pcb = PCBList.get(position);
        holder.PCBName.setText(pcb.name);
        holder.PCBSerialNb.setText(String.format("%d", pcb.serialNb));
        holder.PCBStatusDisplay.setText(pcb.status.toString());
        holder.itemView.setBackgroundColor(pcb.status.toColor(mContext));
    }

    @Override
    public int getItemCount() {
        return PCBList.size();
    }

    public List<PCBPair> getPCBList() {
        return PCBList;
    }

    /**
     * Access point to this class; when new message received from server, please call this method.
     * Provide the following :
     *
     * @param PCBname  String Name of PCB.
     * @param serialNb int Serial number of PCB.
     * @param msgNb    int number of messages received from this PCB.
     */
    public void receiveItem(String PCBname, int serialNb, int msgNb) {
        int position = findPCBPosition(PCBname, serialNb, msgNb);
        if (position < nGrid) {
            PCBPair pcb = PCBList.get(position);
            PCBStatus oldStatus = pcb.status;
            if (pcb.lastMsgNb != msgNb) {
                pcb.lastMsgNb = msgNb;
                pcb.lastTimeSeen = DateTime.now();
                pcb.status = PCBStatus.ONLINE;
            } else {
                if (DateTime.now().getMillis() - pcb.lastTimeSeen.getMillis()
                        < GlobalParameters.ONLINE_TO_DELAY) {
                    pcb.status = PCBStatus.ONLINE;
                } else if (DateTime.now().getMillis() - pcb.lastTimeSeen.getMillis()
                        < GlobalParameters.DELAY_TO_OFFLINE) {
                    pcb.status = PCBStatus.DELAY;
                } else {
                    pcb.status = PCBStatus.OFFLINE;
                }
            }
            if (oldStatus != pcb.status) {
                this.notifyItemChanged(position);
            }
        }
    }

    private void addItem(String PCBname, int noSerial, int noMsg) {
        if (PCBList.size() < nGrid) {
            PCBPair pcbPair = new PCBPair(PCBname, noSerial, noMsg);
            PCBList.add(pcbPair);
            this.notifyItemInserted(PCBList.size()-1);
        }
    }

    private int findPCBPosition(String PCBname, int noSerial, int noMsg) {
        int index;
        for (index = 0; index < PCBList.size(); index++) {
            if (PCBList.get(index).serialNb == noSerial
                    && PCBList.get(index).name.equals(PCBname)) {
                break;
            }
        }
        if (index == PCBList.size()) {
            addItem(PCBname, noSerial, noMsg);
        }
        return index;
    }

    /**
     * PCBStatusDisplay represent each possible state with a color and a string.
     */
    private enum PCBStatus {
        ONLINE("ONLINE", R.color.greenA700dark),
        DELAY("DELAY", R.color.orangeA700),
        OFFLINE("OFFLINE", R.color.redA700);

        private final String text;
        private final int color;

        PCBStatus(final String text, final int color) {
            this.text = text;
            this.color = color;
        }

        @Override
        public String toString() {
            return text;
        }

        public int toColor(Context context) {
            return ContextCompat.getColor(context, this.color);
        }
    }

    /**
     * PCBPair stocks all data regarding a PCB and its state.
     */
    private class PCBPair {
        private final String name;
        private final int serialNb;
        private PCBStatus status;
        private int lastMsgNb;
        private DateTime lastTimeSeen;

        PCBPair(String aName, int aNoSerial, int aNoMsg) {
            name = aName;
            serialNb = aNoSerial;
            status = PCBStatus.ONLINE;
            lastMsgNb = aNoMsg;
            lastTimeSeen = new DateTime();
        }
    }

}

