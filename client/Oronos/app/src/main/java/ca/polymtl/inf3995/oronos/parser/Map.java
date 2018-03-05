package ca.polymtl.inf3995.oronos.parser;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Felix on 15/févr./2018.
 */

public class Map implements ContainableWidget {

    private TextView view;

    Map (Context context) {
        view = new TextView(context);
        view.append("MAP");
    }

    @Override
    public TextView getView() {
        return view;
    }
}
