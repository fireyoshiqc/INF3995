package ca.polymtl.inf3995.oronos.parser;

import java.util.List;

/**
 * Created by Felix on 15/févr./2018.
 */

public abstract class AbstractCANContainer implements ContainableWidget {
    public final List<CAN> list;

    AbstractCANContainer(List<CAN> list) {
        this.list = list;
    }
}
