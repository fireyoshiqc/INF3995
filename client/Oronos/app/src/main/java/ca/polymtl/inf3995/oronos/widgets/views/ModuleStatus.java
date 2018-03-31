package ca.polymtl.inf3995.oronos.widgets.views;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import ca.polymtl.inf3995.oronos.services.DataDispatcher;
import ca.polymtl.inf3995.oronos.services.ModuleMessage;
import ca.polymtl.inf3995.oronos.widgets.adapters.ModuleStatusAdapter;

public class ModuleStatus extends OronosView implements DataDispatcher.ModuleDataListener {
    private RecyclerView recycler;

    public ModuleStatus(Context context, int nGrid, int nColumns) {
        super(context);

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
        String module = msg.getSourceModule();
        Integer noSerie = msg.getSerialNb();
        Integer counter = msg.getCounter();

        ((ModuleStatusAdapter) recycler.getAdapter()).receiveItem(module, noSerie, counter);
    }

    public RecyclerView getGlobalView() {
        return this.recycler;
    }

}
