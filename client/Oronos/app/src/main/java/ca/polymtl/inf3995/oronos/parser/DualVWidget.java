package ca.polymtl.inf3995.oronos.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Felix on 15/févr./2018.
 */

public class DualVWidget extends AbstractWidgetContainer<ContainableWidget> implements ContainableWidget, CleanableWidget {

    public DualVWidget(List<ContainableWidget> list) {
        super(list);
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
}
