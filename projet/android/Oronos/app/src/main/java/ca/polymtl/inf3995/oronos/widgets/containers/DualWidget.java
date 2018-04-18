package ca.polymtl.inf3995.oronos.widgets.containers;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import ca.polymtl.inf3995.oronos.R;
import ca.polymtl.inf3995.oronos.widgets.views.CleanableWidget;
import ca.polymtl.inf3995.oronos.widgets.views.ContainableWidget;
import ca.polymtl.inf3995.oronos.widgets.views.OronosView;
import ca.polymtl.inf3995.oronos.widgets.views.UnsupportedWidget;

/**
 * <h1>Dual Widget</h1>
 * This Dual Widget class is responsible of displaying two views either vertically aligned or
 * horizontally aligned.
 *
 * @author Félix Boulet
 * @version 0.0
 * @since 2018-04-12
 */
public class DualWidget extends AbstractWidgetContainer<OronosView> implements CleanableWidget {

    /**
     * Constructor requesting the activity context, a list of views to put into the dual Widget and
     * the orientation of the widget(either vertical or horizontal).
     *
     * @param context context of the activity.
     * @param list a list of views that are to be displayed in the widget.
     * @param orientation DualWidgetOrientation.
     */
    public DualWidget(Context context, List<OronosView> list, DualWidgetOrientation orientation) {
        super(context, list);
        switch (orientation) {
            case HORIZONTAL:
                setOrientation(LinearLayout.HORIZONTAL);
                break;
            case VERTICAL:
                setOrientation(LinearLayout.VERTICAL);
                break;
        }
        setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
    }

    /**
     * This method is setting up harmoniously two views according to the orientation of the Dual
     * Widget.
     */
    private void buildContents() {
        for (OronosView widget : list) {
            LinearLayout container = new LinearLayout(getContext());
            LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1);
            Resources r = getResources();
            int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, r.getDisplayMetrics());
            params.setMargins(px, px, px, px);
            container.setLayoutParams(params);
            container.addView(widget);
            container.setBackgroundResource(R.drawable.can_data_large_border_black);
            addView(container);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OronosView cleanup() {
        List<ContainableWidget> toRemove = new ArrayList<>();
        for (ContainableWidget widget : list) {
            if (widget instanceof UnsupportedWidget) {
                toRemove.add(widget);
            }
        }
        list.removeAll(toRemove);
        if (list.size() == 1) {
            return list.get(0);
        } else if (list.size() == 0) {
            return null;
        } else {
            buildContents();
            return this;
        }
    }

    /**
     * Enum of all possible orientations of a Dual Widget.
     * */
    public enum DualWidgetOrientation {
        HORIZONTAL, VERTICAL
    }
}