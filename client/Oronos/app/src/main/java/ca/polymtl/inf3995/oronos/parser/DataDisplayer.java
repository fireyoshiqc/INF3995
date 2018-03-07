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

public class DataDisplayer extends AbstractCANContainer implements ContainableWidget {

    private RecyclerView view;

    DataDisplayer(List<CAN> list, Context context) {
        super(list);
        view = new RecyclerView(context);
        CANAdapter adapter = new CANAdapter(context, list);
        view.setLayoutManager(new GridLayoutManager(context, 4));
        view.setItemAnimator(new DefaultItemAnimator());
        view.setAdapter(adapter);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT));
    }

    @Override
    public RecyclerView getView() {
        return view;
    }
}
