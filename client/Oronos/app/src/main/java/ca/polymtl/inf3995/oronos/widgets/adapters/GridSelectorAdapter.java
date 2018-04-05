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

/**
 * <h1>Grid Selector Adapter</h1>
 * This Adapter allows to display a grid containing small cards representing tags available for
 * navigating through the application.
 *
 *
 * @author  Félix Boulet
 * @version 0.0
 * @since   2018-04-12
 */
public class GridSelectorAdapter extends RecyclerView.Adapter<GridSelectorAdapter.OronosViewCard> {
    private Context mContext;
    private List<OronosViewCardContents> gridNames;

    /**
     * Constructor requesting the activity context and the names of the tags the menu will have to
     * display.
     *
     * @param c context of the activity.
     * @param gridNames a list of all the names of the tags.
     * */
    public GridSelectorAdapter(Context c, List<OronosViewCardContents> gridNames) {
        mContext = c;
        this.gridNames = gridNames;
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public OronosViewCard onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cardView;
        cardView = (CardView) LayoutInflater.from(mContext).inflate(R.layout.preview_card, parent, false);
        return new OronosViewCard(cardView);
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public void onBindViewHolder(OronosViewCard holder, int position) {
        holder.gridName.setText(gridNames.get(position).title);
        StringBuilder subtitle = new StringBuilder();
        for (String sub : gridNames.get(position).subtitles) {
            subtitle.append(sub).append("\n");
        }
        holder.subElements.setText(subtitle);
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public int getItemCount() {
        return gridNames.size();
    }

    /**
     * An instance of this class represent a card indicating the views under a section of the app;
     * small buttons allows to navigate to these views.
     * */
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

    /**
     * An instance of this class represent the content of an OronosViewCard.
     * */
    public static class OronosViewCardContents {
        private String title;
        private List<String> subtitles;

        public OronosViewCardContents(String title, List<String> subtitles) {
            this.title = title;
            this.subtitles = subtitles;

        }
    }
}
