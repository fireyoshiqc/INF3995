package ca.polymtl.inf3995.oronos.parser;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;

import java.util.List;

/**
 * Created by Felix on 15/févr./2018.
 */

public class DataDisplayer extends AbstractWidgetContainer<CAN> implements ContainableWidget {

    private final int TARGET_SCREEN_SIZE = 9;
    private final int HALF_SPAN = 2;
    private final int FULL_SPAN = 4;
    private final int MAX_LARGE_DATA = 32;

    enum DataLayout {
        HORIZONTAL, VERTICAL, FULL
    }

    private RecyclerView recycler;

    DataDisplayer(Context context, List<CAN> list, DataLayout layout) {
        super(context, list);
        recycler = new RecyclerView(context);
        CANAdapter adapter = null;
        GridLayoutManager gridLayoutManager = null;
        DisplayMetrics metrics =getContext().getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        double x = Math.pow(width/metrics.xdpi, 2);
        double y = Math.pow(height/metrics.ydpi, 2);
        double screenInches = Math.sqrt(x+y);

        switch (layout) {
            case HORIZONTAL:
                adapter = new CANAdapter(context, list, (int)(MAX_LARGE_DATA/2 * (screenInches/TARGET_SCREEN_SIZE)));
                gridLayoutManager = new GridLayoutManager(context, (int)(FULL_SPAN * (screenInches/TARGET_SCREEN_SIZE)));
                break;
            case VERTICAL:
                adapter = new CANAdapter(context, list, (int)(MAX_LARGE_DATA/2 * (screenInches/TARGET_SCREEN_SIZE)));
                gridLayoutManager = new GridLayoutManager(context, (int)(HALF_SPAN * (screenInches/TARGET_SCREEN_SIZE)));
                break;
            case FULL:
                adapter = new CANAdapter(context, list, (int)(MAX_LARGE_DATA * (screenInches/TARGET_SCREEN_SIZE)));
                gridLayoutManager = new GridLayoutManager(context, (int)(FULL_SPAN * (screenInches/TARGET_SCREEN_SIZE)));
                break;

        }

        recycler.setLayoutManager(gridLayoutManager);
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.setAdapter(adapter);
        recycler.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT));
        addView(recycler);
    }
}
