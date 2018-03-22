package ca.polymtl.inf3995.oronos.widgets.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ca.polymtl.inf3995.oronos.R;
import ca.polymtl.inf3995.oronos.activities.MainActivity;


public class ImageAdapter extends BaseAdapter {
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
    public ImageAdapter(Context c, List<OronosViewCardContents> gridNames) {
        mContext = c;
        this.gridNames = gridNames;
    }

    @Override
    public int getCount() {
        return gridNames.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        CardView cardView;
        if (convertView == null) {
            cardView = (CardView) LayoutInflater.from(mContext).inflate(R.layout.preview_card, parent, false);
            ((TextView) cardView.findViewById(R.id.grid_name)).setText(gridNames.get(position).title);
            StringBuilder subtitle = new StringBuilder();
            for (String sub : gridNames.get(position).subtitles) {
                subtitle.append(sub).append("\n");
            }
            ((TextView) cardView.findViewById(R.id.sub_elements)).setText(subtitle);
            cardView.findViewById(R.id.view_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity) mContext).changeStateOfDataLayout(position);
                }
            });
        } else {
            cardView = (CardView) convertView;
        }
        return cardView;
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
