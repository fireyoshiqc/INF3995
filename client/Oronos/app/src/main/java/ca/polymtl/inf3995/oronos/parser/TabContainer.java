package ca.polymtl.inf3995.oronos.parser;

import java.util.List;

/**
 * Created by Felix on 15/févr./2018.
 */

public class TabContainer implements ContainableWidget {
    public final List<Tab> list;

    protected TabContainer(List<Tab> list) {
        this.list = list;
    }
}
