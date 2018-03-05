package ca.polymtl.inf3995.oronos.parser;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.support.v7.widget.CardView;
import ca.polymtl.inf3995.oronos.R;


public class ImageAdapter extends BaseAdapter {
    private Context mContext;

    /**
     * Adapter that can display small images in the Grid View for the MainActivity menu.
     *
     * Eventually, we will be able to adapt this to take View instead of ImageView, and thus display
     * miniatures of all the data received by the client.
     *
     * @param c      Context of the app.
     */
    public ImageAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        CardView cardView;
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
            imageView.setBackgroundColor(Color.TRANSPARENT);
            imageView.setImageResource(mThumbIds[position]);
            cardView = new CardView(mContext);
            cardView.setLayoutParams(new GridView.LayoutParams(85, 85));
            cardView.addView(imageView);
        } else {
            cardView = (CardView) convertView;
        }

        ((ImageView)cardView.getChildAt(0)).setImageResource(mThumbIds[position]);
        return cardView;
    }

    // references to our images
    private Integer[] mThumbIds = {
            R.drawable.ic_sample_2, R.drawable.ic_sample_3,
            R.drawable.ic_sample_4, R.drawable.ic_sample_5,
            R.drawable.ic_sample_6, R.drawable.ic_sample_7,
            R.drawable.ic_sample_0, R.drawable.ic_sample_1,
            R.drawable.ic_sample_2, R.drawable.ic_sample_3,
            R.drawable.ic_sample_4, R.drawable.ic_sample_5,
            R.drawable.ic_sample_6, R.drawable.ic_sample_7,
            R.drawable.ic_sample_0, R.drawable.ic_sample_1,
            R.drawable.ic_sample_2, R.drawable.ic_sample_3,
            R.drawable.ic_sample_4, R.drawable.ic_sample_5,
            R.drawable.ic_sample_6, R.drawable.ic_sample_7
    };
}
