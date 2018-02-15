package ca.polymtl.inf3995.oronos.parser;

import java.util.List;

/**
 * Created by Felix on 15/févr./2018.
 */

public class DualHWidget implements TabbableWidget {
    List<TabbableWidget> list;

    public DualHWidget(List<TabbableWidget> list) {
        this.list = list;
    }
}
