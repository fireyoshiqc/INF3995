package ca.polymtl.inf3995.oronos.widgets.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import ca.polymtl.inf3995.oronos.R;
import ca.polymtl.inf3995.oronos.activities.MainActivity;


public class GridSelectorAdapter extends RecyclerView.Adapter<GridSelectorAdapter.OronosViewCard> {
    private Context mContext;
    private List<OronosViewCardContents> gridNames;

    /**
     * Adapter that can display small images in the Grid View for the MainActivity menu.
     * <p>
     * Eventually, we will be able to adapt this to take View instead of ImageView, and thus display
     * miniatures of all the data received by the client.
     *
     * @param c Context of the app.
     */
    public GridSelectorAdapter(Context c, List<OronosViewCardContents> gridNames) {
        mContext = c;
        this.gridNames = gridNames;
    }

    @Override
    public OronosViewCard onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cardView;
        cardView = (CardView) LayoutInflater.from(mContext).inflate(R.layout.preview_card, parent, false);
        return new OronosViewCard(cardView);
    }

    @Override
    public void onBindViewHolder(OronosViewCard holder, int position) {
        holder.gridName.setText(gridNames.get(position).title);
        StringBuilder subtitle = new StringBuilder();
        for (String sub : gridNames.get(position).subtitles) {
            subtitle.append(sub).append("\n");
        }
        holder.subElements.setText(subtitle);
    }

    @Override
    public int getItemCount() {
        return gridNames.size();
    }

    class OronosViewCard extends RecyclerView.ViewHolder {
        TextView gridName;
        TextView subElements;
        Button viewButton;

        OronosViewCard(View itemView) {
            super(itemView);
            gridName = itemView.findViewById(R.id.grid_name);
            subElements = itemView.findViewById(R.id.sub_elements);
            viewButton = itemView.findViewById(R.id.view_button);
            viewButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity) mContext).changeStateOfDataLayout(getAdapterPosition());
                }
            });
        }
    }

    public static class OronosViewCardContents {
        private String title;
        private List<String> subtitles;

        public OronosViewCardContents(String title, List<String> subtitles) {
            this.title = title;
            this.subtitles = subtitles;

        }
    }
}
