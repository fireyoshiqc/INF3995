package ca.polymtl.inf3995.oronos.parser;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;

public class ModuleStatus extends OronosView {
    private ModuleStatusAdapter adapter;
    private GridView gridView;

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

//        IntentFilter intentFilter = new IntentFilter();
//        for (int i = 0; i < ModuleType.values().length; i++) {
//            intentFilter.addAction(ModuleType.values()[i].toString());
//        }
//        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver, intentFilter);

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

//    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String module = intent.getAction();
//            int counter = intent.getParcelableExtra("counter");
//            Timber.v(module + counter);
//        }
//    };

}
