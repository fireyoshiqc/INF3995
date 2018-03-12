package ca.polymtl.inf3995.oronos.parser;

import android.content.Context;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Felix on 11/mars/2018.
 */

public class DualWidget extends AbstractWidgetContainer<OronosView> implements CleanableWidget {

    enum DualWidgetOrientation {
        HORIZONTAL, VERTICAL
    }

    DualWidget(Context context, List<OronosView> list, DualWidgetOrientation orientation) {
        super(context, list);
        switch (orientation) {
            case HORIZONTAL:
                setOrientation(LinearLayout.HORIZONTAL);
            case VERTICAL:
                setOrientation(LinearLayout.VERTICAL);
        }
        setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
    }

    private void buildContents() {
        for (OronosView widget : list) {
            LinearLayout container = new LinearLayout(getContext());
            container.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1));
            container.addView(widget);
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
}