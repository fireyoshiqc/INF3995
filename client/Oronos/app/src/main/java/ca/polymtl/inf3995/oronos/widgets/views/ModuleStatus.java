package ca.polymtl.inf3995.oronos.widgets.views;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;

import org.parceler.Parcels;

import ca.polymtl.inf3995.oronos.services.BroadcastMessage;
import ca.polymtl.inf3995.oronos.utils.GlobalParameters;
import ca.polymtl.inf3995.oronos.widgets.adapters.ModuleStatusAdapter;

public class ModuleStatus extends OronosView {
    private ModuleStatusAdapter adapter;
    private GridView gridView;
    private BroadcastReceiver broadcastReceiver;
    public ModuleStatus(Context context, int nGrid, int nColumns) {
        super(context);
        gridView = new GridView(context);
        gridView.setNumColumns(nColumns);
        gridView.setBackgroundColor(Color.TRANSPARENT);
        gridView.setVerticalSpacing(10);
        gridView.setHorizontalSpacing(10);
        gridView.setGravity(Gravity.CENTER);
        gridView.setLayoutParams(new GridView.LayoutParams(
                GridView.LayoutParams.MATCH_PARENT,
                GridView.LayoutParams.MATCH_PARENT
        ));
        adapter = new ModuleStatusAdapter(context, nGrid, nColumns);
        gridView.setAdapter(adapter);
        setOrientation(LinearLayout.VERTICAL);
        setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        addView(gridView);
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
                    BroadcastMessage msg = Parcels.unwrap(intent.getParcelableExtra("data"));
                    String module = msg.getModuleSource();
                    Integer noSerie = msg.getNoSerieSource();
                    Integer counter = msg.getCounter();

                    receiveItem(module, noSerie, counter);
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

    public int getCount() {
        return adapter.getCount();
    }

    public void receiveItem(String PCBname, int noSerial, int noMsg) {
        adapter.receiveItem(PCBname, noSerial, noMsg);
    }

    public View getLocalView(int position, View convertView, ViewGroup parent) {
        return adapter.getView(position, convertView, parent);
    }

    public GridView getGlobalView() {
        return this.gridView;
    }

}
