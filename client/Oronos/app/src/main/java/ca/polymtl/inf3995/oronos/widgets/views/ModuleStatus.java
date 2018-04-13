package ca.polymtl.inf3995.oronos.widgets.views;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;

import ca.polymtl.inf3995.oronos.services.DataDispatcher;
import ca.polymtl.inf3995.oronos.services.ModuleMessage;
import ca.polymtl.inf3995.oronos.widgets.adapters.ModuleStatusAdapter;

public class ModuleStatus extends OronosView implements DataDispatcher.ModuleDataListener {
    private RecyclerView recycler;

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

    public RecyclerView getGlobalView() {
        return this.recycler;
    }

}
