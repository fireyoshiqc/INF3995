package ca.polymtl.inf3995.oronos.widgets.views;

import android.content.Context;
import android.widget.TextView;

/**
 * Created by Felix on 15/févr./2018.
 */

public class Map extends OronosView {

    private TextView view;

    public Map(Context context) {
        super(context);
        view = new TextView(context);
        view.append("MAP");
    }

}
