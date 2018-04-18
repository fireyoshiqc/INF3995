package ca.polymtl.inf3995.oronos.widgets.views;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ca.polymtl.inf3995.oronos.widgets.adapters.DataDisplayerAdapter;
import ca.polymtl.inf3995.oronos.widgets.containers.AbstractWidgetContainer;

/**
 * <h1>Data Displayer</h1>
 * This class is in charge of displaying a set of CAN messages in a legible way.
 *
 * @author Félix Boulet
 * @version 0.0
 * @since 2018-04-12
 */
public class DataDisplayer extends AbstractWidgetContainer<CAN> implements ContainableWidget {

    private final int TARGET_SCREEN_SIZE = 9;
    private final int HALF_SPAN = 2;
    private final int FULL_SPAN = 4;
    private final int MAX_LARGE_DATA = 32;
    private final int UI_CHANGE_ANIM_DURATION = 100;
    private final int DATA_UPDATE_PERIOD = 200;
    private RecyclerView recycler;
    private Timer listUpdater;

    /**
     * This Data Displayer Constructor needs the context of the activity, a list of CAN message
     * types that are going to have a spot in the Data Displayer and a layout to arrange
     * the disposition of the data.
     *
     * @param context the activity context.
     * @param list a list of CAN message types that are the ones to display.
     * @param layout either VERTICAL, HORIZONTAL or FULL.
     * */
    public DataDisplayer(Context context, List<CAN> list, DataLayout layout) {
        super(context, list);
        recycler = new RecyclerView(context);
        DataDisplayerAdapter adapter = null;
        GridLayoutManager gridLayoutManager = null;
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        double x = Math.pow(width / metrics.xdpi, 2);
        double y = Math.pow(height / metrics.ydpi, 2);
        double screenInches = Math.sqrt(x + y);

        switch (layout) {
            case HORIZONTAL:
                adapter = new DataDisplayerAdapter(context, list, (int) (MAX_LARGE_DATA / 2 * (screenInches / TARGET_SCREEN_SIZE)));
                gridLayoutManager = new GridLayoutManager(context, (int) (FULL_SPAN * (screenInches / TARGET_SCREEN_SIZE)));
                break;
            case VERTICAL:
                adapter = new DataDisplayerAdapter(context, list, (int) (MAX_LARGE_DATA / 2 * (screenInches / TARGET_SCREEN_SIZE)));
                gridLayoutManager = new GridLayoutManager(context, (int) (HALF_SPAN * (screenInches / TARGET_SCREEN_SIZE)));
                break;
            case FULL:
                adapter = new DataDisplayerAdapter(context, list, (int) (MAX_LARGE_DATA * (screenInches / TARGET_SCREEN_SIZE)));
                gridLayoutManager = new GridLayoutManager(context, (int) (FULL_SPAN * (screenInches / TARGET_SCREEN_SIZE)));
                break;
        }

        recycler.setLayoutManager(gridLayoutManager);
        recycler.getItemAnimator().setChangeDuration(UI_CHANGE_ANIM_DURATION);
        recycler.setAdapter(adapter);
        recycler.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT));
        recycler.setNestedScrollingEnabled(false);
        addView(recycler);

    }

    /**
     * {@inheritDoc}
     * */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startUpdateTask();
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopUpdateTask();
    }

    /**
     * This method is called when the Data Displayer view is on to start the updates
     * on the CAN message types of the Data Displayer.
     * */
    private void startUpdateTask() {
        listUpdater = new Timer(true);
        TimerTask sensorTask = new TimerTask() {
            @Override
            public void run() {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).isChanged()) {
                        final int position = i;
                        ((Activity) getContext()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                recycler.getAdapter().notifyItemChanged(position);
                            }
                        });
                        list.get(i).notifyReset();
                    }
                }
            }
        };
        listUpdater.scheduleAtFixedRate(sensorTask, 0, DATA_UPDATE_PERIOD);
    }

    /**
     * This method stops the task started by the method above.
     */
    private void stopUpdateTask() {
        if (listUpdater != null) {
            listUpdater.cancel();
            listUpdater.purge();
        }
    }

    /**
     * This enum represents every possible layout the Data Displayer can have.
     * */
    public enum DataLayout {
        HORIZONTAL, VERTICAL, FULL
    }
}
