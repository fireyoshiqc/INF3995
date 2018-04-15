package ca.polymtl.inf3995.oronos.widgets.views;

import android.content.Context;
import android.widget.LinearLayout;

/**
 * <h1>Oronos View</h1>
 * Each view that displays data coming from an Oronos rocket extends Oronos View.
 *
 * @author Félix Boulet
 * @version 0.0
 * @since 2018-04-12
 */
public class OronosView extends LinearLayout implements ContainableWidget {
    public OronosView(Context context) {
        super(context);
    }
}
