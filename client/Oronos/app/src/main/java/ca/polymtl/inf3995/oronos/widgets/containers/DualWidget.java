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
 * Created by Felix on 11/mars/2018.
 */

public class DualWidget extends AbstractWidgetContainer<OronosView> implements CleanableWidget {

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

    public enum DualWidgetOrientation {
        HORIZONTAL, VERTICAL
    }
}