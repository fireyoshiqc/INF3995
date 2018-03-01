package ca.polymtl.inf3995.oronos.parser;

import java.util.List;

/**
 * Created by Felix on 22/févr./2018.
 */

public abstract class AbstractWidgetContainer<T extends ContainableWidget> {

    protected final List<T> list;

    AbstractWidgetContainer(List<T> list) {
        this.list = list;
    }

    public List<T> getList() {
        return list;
    }
}
