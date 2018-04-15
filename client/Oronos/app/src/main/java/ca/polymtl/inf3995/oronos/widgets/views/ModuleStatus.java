package ca.polymtl.inf3995.oronos.widgets.views;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;

import ca.polymtl.inf3995.oronos.services.DataDispatcher;
import ca.polymtl.inf3995.oronos.services.ModuleMessage;
import ca.polymtl.inf3995.oronos.widgets.adapters.ModuleStatusAdapter;

/**
 * <h1>Module Status</h1>
 * Displays the module status in a grid layout.
 *
 * @author Félix Boulet, Justine Pepin
 * @version 0.0
 * @since 2018-04-12
 */
public class ModuleStatus extends OronosView implements DataDispatcher.ModuleDataListener {
    private RecyclerView recycler;

    /**
     * This Module Status constructor is prepping a grid layout and an Module Status Adapter to take
     * care of the updates.
     *
     * @param context the activity context.
     * @param nGrid the number of lines in the grid.
     * @param nColumns the number of columns in the grid.
     * */
    public ModuleStatus(Context context, int nGrid, int nColumns) {
        super(context);
        setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        recycler = new RecyclerView(context);
        ModuleStatusAdapter adapter = new ModuleStatusAdapter(context, nGrid, nColumns);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, nGrid / nColumns);
        recycler.setLayoutManager(gridLayoutManager);
        recycler.getItemAnimator().setChangeDuration(100);
        recycler.getItemAnimator().setAddDuration(250);
        recycler.setAdapter(adapter);
        recycler.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT));
        recycler.setNestedScrollingEnabled(false);
        addView(recycler);
        DataDispatcher.registerModuleDataListener(this);
    }

    /**
     * This method takes care of every module data broadcast message the client receives by
     * extracting the info from the message and passing it to the adapter (that makes everything
     * pretty and ergonomic).
     * */
    @Override
    public void onModuleDataReceived(ModuleMessage msg) {
        final String module = msg.getSourceModule();
        final Integer noSerie = msg.getSerialNb();
        final Integer counter = msg.getCounter();

        ((Activity) getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((ModuleStatusAdapter) recycler.getAdapter()).receiveItem(module, noSerie, counter);
            }
        });

    }

    /**
     * Accessor for this view, which is recycled.
     * */
    public RecyclerView getGlobalView() {
        return this.recycler;
    }

}
