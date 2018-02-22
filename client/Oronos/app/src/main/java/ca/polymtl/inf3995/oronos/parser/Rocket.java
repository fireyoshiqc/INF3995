package ca.polymtl.inf3995.oronos.parser;

import java.util.List;

/**
 * Created by Felix on 20/févr./2018.
 */

public class Rocket extends AbstractWidgetContainer<GridContainer> {

    public final String name;
    public final String id;

    protected Rocket(String name, String id, List<GridContainer> list) {
        super(list);
        this.name = name;
        this.id = id;
    }
}
