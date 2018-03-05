package ca.polymtl.inf3995.oronos.parser;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Felix on 15/févr./2018.
 */

public class DualHWidget extends AbstractWidgetContainer<ContainableWidget> implements ContainableWidget, CleanableWidget {

    private LinearLayout layout;

    DualHWidget(List<ContainableWidget> list, Context context) {
        super(list);
        layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.HORIZONTAL);
    }

    public void buildContents() {
        for (ContainableWidget widget : list) {
            layout.addView(widget.getView());
        }
    }

    @Override
    public ContainableWidget cleanup() {
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
            return this;
        }
    }

    @Override
    public View getView() {
        return layout;
    }
}
