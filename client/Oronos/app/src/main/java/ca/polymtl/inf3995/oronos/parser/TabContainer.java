package ca.polymtl.inf3995.oronos.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Felix on 15/févr./2018.
 */

public class TabContainer extends AbstractWidgetContainer<Tab> implements ContainableWidget, CleanableWidget {

    TabContainer(List<Tab> list) {
        super(list);
    }

    @Override
    public ContainableWidget cleanup() {
        List<Tab> toRemove = new ArrayList<>();
        for (Tab tab : list) {
            if (tab.getContents() == null) {
                toRemove.add(tab);
            }
        }
        list.removeAll(toRemove);
        if (list.size() == 1) {
            return list.get(0).getContents();
        } else if (list.size() == 0) {
            return null;
        } else {
            return this;
        }
    }
}
