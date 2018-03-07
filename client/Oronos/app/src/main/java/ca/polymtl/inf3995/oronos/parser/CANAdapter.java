package ca.polymtl.inf3995.oronos.parser;

import android.content.Context;
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

    public CANAdapter (Context context, List<CAN> canTags) {
        this.context = context;
        this.canTags = canTags;
    }

    @Override
    public CANContainer onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.can_data_large, parent, false);
        return new CANContainer(itemView);
    }

    @Override
    public void onBindViewHolder(CANContainer holder, int position) {
        CAN can = canTags.get(position);
        holder.name.setText(can.getName());
        holder.canid.setText(can.getId());
        // TODO: Use notify event to send formatted data into the holder.
        if (can.getDisplay() != null) {
            holder.data.setText(can.getDisplay());
            String[] dataSplit = can.getDisplay().split(" ");
            if (dataSplit.length == 2) {
                holder.unit.setText(dataSplit[1]);
            } else {
                holder.unit.setText("");
            }
        } else {
            // TODO: Use customUpdate to generate the data to display.
            holder.data.setText("CUSTOM");
            holder.unit.setText("CUST");
        }

        // TODO: Change holder appearance according to minAcceptable and maxAcceptable.


    }

    @Override
    public int getItemCount() {
        return canTags.size();
    }


}
