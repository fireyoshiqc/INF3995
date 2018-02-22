package ca.polymtl.inf3995.oronos.parser;

import java.util.List;

/**
 * Created by Felix on 15/févr./2018.
 */

public class GridContainer extends AbstractWidgetContainer<Grid> implements ContainableWidget {

    protected GridContainer(List<Grid> list) {
        super(list);
    }
}
