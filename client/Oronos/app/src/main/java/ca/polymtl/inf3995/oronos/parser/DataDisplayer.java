package ca.polymtl.inf3995.oronos.parser;

import java.util.List;

/**
 * Created by Felix on 15/févr./2018.
 */

public class DataDisplayer extends AbstractCANContainer implements ContainableWidget {
    protected DataDisplayer(List<CAN> list) {
        super(list);
    }
}
