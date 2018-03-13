package ca.polymtl.inf3995.oronos.parser;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ca.polymtl.inf3995.oronos.R;

/**
 * Created by Felix on 07/mars/2018.
 */

public class CANAdapter extends RecyclerView.Adapter<CANAdapter.CANContainer> {

    private Context context;
    private List<CAN> canTags;
    private final int maxLargeItems;

    public CANAdapter(Context context, List<CAN> canTags, int maxLargeItems) {
        this.context = context;
        this.canTags = canTags;
        this.maxLargeItems = maxLargeItems;
    }

    @Override
    public CANContainer onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        if (this.getItemCount() > maxLargeItems) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.can_data_small, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.can_data_large, parent, false);
        }

        return new CANContainer(itemView);
    }

    @Override
    public void onBindViewHolder(CANContainer holder, int position) {
        CAN can = canTags.get(position);
        holder.name.setText(can.getName());
        holder.canid.setText(can.getId());
        double dummyData = 0.000000;
        // TODO: Use notify event to send formatted data into the holder.
        String toDisplay = "" + dummyData;
        if (can.getDisplay() != null) {

            if (can.getChiffresSign() != null) {
                String signFormat = "%." + can.getChiffresSign() + "f";
                toDisplay = String.format(signFormat, dummyData);
            }
            holder.data.setText(toDisplay);
            String[] dataSplit = can.getDisplay().split(" ");
            if (dataSplit.length == 2) {
                holder.unit.setText(dataSplit[1]);
            } else {
                holder.unit.setText("");
            }
        } else {
            // TODO: Use customUpdate to generate the data to display.
            if (can.getChiffresSign() != null) {
                String signFormat = "%." + can.getChiffresSign() + "f";
                toDisplay = String.format(signFormat, dummyData);
            }
            holder.data.setText(toDisplay);
            holder.unit.setText("CST");
        }

        // TODO: Change holder appearance according to minAcceptable and maxAcceptable.
        try {
            if (can.getMinAcceptable() != null && can.getMaxAcceptable() != null) {
                if (dummyData < Double.parseDouble(can.getMinAcceptable())
                        || dummyData > Double.parseDouble(can.getMaxAcceptable())) {
                    holder.itemView.setBackgroundResource(R.drawable.can_data_large_border_red);
                    holder.data.setTextColor(0xFFCC0000);
                } else {
                    holder.itemView.setBackgroundResource(R.drawable.can_data_large_border_green);
                    holder.data.setTextColor(Color.BLACK);
                }
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return canTags.size();
    }

    class CANContainer extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView canid;
        public TextView data;
        public TextView unit;

        public CANContainer(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            canid = view.findViewById(R.id.canid);
            data = view.findViewById(R.id.data);
            unit = view.findViewById(R.id.unit_and_warning);
        }

    }


}
