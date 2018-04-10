package ca.polymtl.inf3995.oronos.widgets.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.polymtl.inf3995.oronos.R;
import ca.polymtl.inf3995.oronos.activities.MainActivity;

/**
 * <h1>Grid Selector Adapter</h1>
 * This Adapter allows to display a grid containing small cards representing tags available for
 * navigating through the application.
 *
 * @author Félix Boulet
 * @version 0.0
 * @since 2018-04-12
 */
public class GridSelectorAdapter extends RecyclerView.Adapter<GridSelectorAdapter.OronosViewCard> {
    private Context mContext;
    private List<OronosViewCardContents> gridNames;
    private final HashMap<String, Integer> imageAssociations;

    /**
     * Constructor requesting the activity context and the names of the tags the menu will have to
     * display.
     *
     * @param c         context of the activity.
     * @param gridNames a list of all the names of the tags.
     */
    public GridSelectorAdapter(Context c, List<OronosViewCardContents> gridNames) {
        mContext = c;
        this.gridNames = gridNames;
        imageAssociations = new HashMap<>();
        imageAssociations.put("map", R.drawable.map_preview);
        imageAssociations.put("find", R.drawable.map_preview);
        imageAssociations.put("module", R.drawable.module_preview);
        imageAssociations.put("data", R.drawable.module_preview);
        imageAssociations.put("log", R.drawable.data_preview);
        imageAssociations.put("gps", R.drawable.gps_preview);
        imageAssociations.put("plot", R.drawable.plot_preview);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OronosViewCard onCreateViewHolder(ViewGroup parent, int viewType) {
        return new OronosViewCard(LayoutInflater.from(parent.getContext()).inflate(R.layout.preview_card, parent, false));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBindViewHolder(final OronosViewCard holder, int position) {
        String title = gridNames.get(position).title;
        holder.gridName.setText(title);
        StringBuilder subtitle = new StringBuilder();
        for (String sub : gridNames.get(position).subtitles) {
            subtitle.append(sub).append("\n");
        }
        if (subtitle.toString().isEmpty()) {
            holder.subElements.setVisibility(View.GONE);
        } else {
            holder.subElements.setVisibility(View.VISIBLE);
            holder.subElements.setText(subtitle);
        }

        holder.previewImage.setVisibility(View.INVISIBLE);

        for (Map.Entry<String, Integer> entry : imageAssociations.entrySet()) {
            if (title.toLowerCase().contains(entry.getKey()) || subtitle.toString().toLowerCase().contains(entry.getKey())) {
                Glide.with(mContext).load(entry.getValue()).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.loadingSpinner.setVisibility(View.GONE);
                        holder.previewImage.setVisibility(View.VISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.loadingSpinner.setVisibility(View.GONE);
                        holder.previewImage.setVisibility(View.VISIBLE);
                        return false;
                    }
                }).into(holder.previewImage);
                break;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getItemCount() {
        return gridNames.size();
    }

    /**
     * An instance of this class represent the content of an OronosViewCard.
     */
    public static class OronosViewCardContents {
        private String title;
        private List<String> subtitles;

        public OronosViewCardContents(String title, List<String> subtitles) {
            this.title = title;
            this.subtitles = subtitles;

        }
    }

    /**
     * An instance of this class represent a card indicating the views under a section of the app;
     * small buttons allows to navigate to these views.
     */
    class OronosViewCard extends RecyclerView.ViewHolder {
        TextView gridName;
        TextView subElements;
        ProgressBar loadingSpinner;
        ImageView previewImage;
        Button viewButton;

        OronosViewCard(View itemView) {
            super(itemView);
            gridName = itemView.findViewById(R.id.grid_name);
            subElements = itemView.findViewById(R.id.sub_elements);
            loadingSpinner = itemView.findViewById(R.id.loadingSpinner);
            previewImage = itemView.findViewById(R.id.preview_image);
            viewButton = itemView.findViewById(R.id.view_button);
            viewButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity) mContext).changeStateOfDataLayout(getAdapterPosition());
                }
            });
        }
    }
}
