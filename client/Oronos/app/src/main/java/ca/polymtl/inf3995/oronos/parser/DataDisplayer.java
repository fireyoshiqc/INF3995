package ca.polymtl.inf3995.oronos.parser;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Felix on 15/févr./2018.
 */

public class DataDisplayer extends AbstractWidgetContainer<CAN> implements ContainableWidget {

    private RecyclerView recycler;

    DataDisplayer(Context context, List<CAN> list) {
        super(context, list);
        recycler = new RecyclerView(context);
        CANAdapter adapter = new CANAdapter(context, list);
        recycler.setLayoutManager(new GridLayoutManager(context, 3));
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.setAdapter(adapter);
        recycler.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT));
        addView(recycler);
    }
}
