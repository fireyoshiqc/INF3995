package ca.polymtl.inf3995.oronos.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Felix on 22/févr./2018.
 */

public abstract class AbstractWidgetContainer<T extends ContainableWidget> {
    public final List<T> list;

    protected AbstractWidgetContainer(List<T> list) {
        this.list = list;
    }

}
