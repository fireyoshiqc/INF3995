package ca.polymtl.inf3995.oronos.widgets.views;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;

import org.parceler.Parcels;

import ca.polymtl.inf3995.oronos.services.ModuleMessage;
import ca.polymtl.inf3995.oronos.utils.ModuleType;
import ca.polymtl.inf3995.oronos.widgets.adapters.ModuleStatusAdapter;

public class ModuleStatus extends OronosView {
    private ModuleStatusAdapter adapter;
    private GridView gridView;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ModuleMessage msg = Parcels.unwrap(intent.getParcelableExtra("data"));
            ModuleType module = msg.getModuleSource();
            Integer noSerie = msg.getNoSerieSource();
            Integer counter = msg.getCounter();

            receiveItem(module.toString(), noSerie, counter);
        }
    };

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

        IntentFilter intentFilter = new IntentFilter();
        for (int i = 0; i < ModuleType.values().length; i++) {
            intentFilter.addAction(ModuleType.values()[i].toString());
        }
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver, intentFilter);

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
