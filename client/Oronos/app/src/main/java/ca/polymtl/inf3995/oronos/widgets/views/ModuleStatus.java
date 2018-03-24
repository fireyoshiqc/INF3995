package ca.polymtl.inf3995.oronos.widgets.views;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;

import org.parceler.Parcels;

import ca.polymtl.inf3995.oronos.services.BroadcastMessage;
import ca.polymtl.inf3995.oronos.services.ModuleMessage;
import ca.polymtl.inf3995.oronos.utils.GlobalParameters;
import ca.polymtl.inf3995.oronos.widgets.adapters.ModuleStatusAdapter;
import timber.log.Timber;

public class ModuleStatus extends OronosView {
    private RecyclerView recycler;
    private BroadcastReceiver broadcastReceiver;
    public ModuleStatus(Context context, int nGrid, int nColumns) {
        super(context);

        recycler = new RecyclerView(context);
        ModuleStatusAdapter adapter = new ModuleStatusAdapter(context, nGrid, nColumns);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, nGrid/nColumns);
        recycler.setLayoutManager(gridLayoutManager);
        recycler.getItemAnimator().setChangeDuration(100);
        recycler.getItemAnimator().setAddDuration(250);
        recycler.setAdapter(adapter);
        recycler.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT));
        recycler.setNestedScrollingEnabled(false);
        addView(recycler);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        enableModuleUpdates();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        disableModuleUpdates();
    }

    private void enableModuleUpdates() {
        if (GlobalParameters.canModuleTypes != null) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    ModuleMessage msg = Parcels.unwrap(intent.getParcelableExtra("data"));
                    String module = msg.getSourceModule();
                    Integer noSerie = msg.getSerialNb();
                    Integer counter = msg.getCounter();

                    ((ModuleStatusAdapter)recycler.getAdapter()).receiveItem(module, noSerie, counter);
                }
            };
            IntentFilter intentFilter = new IntentFilter();
            for (String key : GlobalParameters.canModuleTypes.keySet()) {
                intentFilter.addAction(key);
            }
            LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver, intentFilter);
        } else {
            final Handler handler = new Handler();

            // Retry enabling updates
            handler.postDelayed(new Runnable() {
                public void run() {
                    enableModuleUpdates();
                }
            }, 1000);
        }
    }

    private void disableModuleUpdates() {
        if (broadcastReceiver != null) {
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }
    }

    public RecyclerView getGlobalView() {
        return this.recycler;
    }

}
