package ca.polymtl.inf3995.oronos.parser;

import java.util.List;

/**
 * Created by Felix on 15/févr./2018.
 */

public class DisplayLogWidget extends AbstractCANContainer implements ContainableWidget {
    protected DisplayLogWidget(List<CAN> list) {
        super(list);
    }
}
