package ca.polymtl.inf3995.oronos.parser;

import android.content.Context;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Felix on 15/févr./2018.
 */

public class DataDisplayer extends AbstractCANContainer implements ContainableWidget {

    private TextView view;

    DataDisplayer(List<CAN> list, Context context) {
        super(list);
        view = new TextView(context);
        for (CAN item : list) {
            view.append("CAN:"+item.getId()+"\n");
        }
    }

    @Override
    public TextView getView() {
        return view;
    }
}
