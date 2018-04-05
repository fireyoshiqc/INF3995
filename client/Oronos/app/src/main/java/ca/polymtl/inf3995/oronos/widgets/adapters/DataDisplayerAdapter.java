package ca.polymtl.inf3995.oronos.widgets.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ca.polymtl.inf3995.oronos.R;
import ca.polymtl.inf3995.oronos.widgets.views.CAN;

/**
 * Created by Felix on 07/mars/2018.
 */

public class DataDisplayerAdapter extends RecyclerView.Adapter<DataDisplayerAdapter.DataContainer> {

    private final int maxLargeItems;
    private Context context;
    private List<CAN> canTags;

    public DataDisplayerAdapter(Context context, List<CAN> canTags, int maxLargeItems) {
        this.context = context;
        this.canTags = canTags;
        this.maxLargeItems = maxLargeItems;
    }

    @Override
    public DataContainer onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (this.getItemCount() > maxLargeItems) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.can_data_small, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.can_data_large, parent, false);
        }
        return new DataContainer(itemView);
    }

    @Override
    public void onBindViewHolder(DataContainer holder, int position) {
        CAN can = canTags.get(position);
        holder.name.setText(can.getName());
        holder.canid.setText(can.getId());
        holder.data.setText(can.getDataToDisplay());
        holder.unit.setText(can.getUnit());
        TypedValue outValue;
        switch (can.getState()) {
            case NONE:
                break;
            case RED:
                holder.itemView.setBackgroundResource(R.drawable.can_data_large_border_red);
                outValue = new TypedValue();
                context.getTheme().resolveAttribute(R.attr.redCanTag, outValue, true);
                holder.data.setTextColor(outValue.data);
                break;
            case GREEN:
                holder.itemView.setBackgroundResource(R.drawable.can_data_large_border_green);
                outValue = new TypedValue();
                context.getTheme().resolveAttribute(R.attr.greenCanTag, outValue, true);
                holder.data.setTextColor(outValue.data);
                break;
        }
    }

    @Override
    public void onViewRecycled(DataContainer holder) {
        super.onViewRecycled(holder);
        holder.itemView.setBackgroundResource(R.drawable.can_data_large_border_black);
        holder.data.setTextColor(Color.BLACK);
    }

    @Override
    public int getItemCount() {
        return canTags.size();
    }

    class DataContainer extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView canid;
        private TextView data;
        private TextView unit;

        DataContainer(final View view) {
            super(view);
            name = view.findViewById(R.id.name);
            canid = view.findViewById(R.id.canid);
            data = view.findViewById(R.id.data);
            unit = view.findViewById(R.id.unit_and_warning);
        }

    }


}
