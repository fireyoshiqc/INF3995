package ca.polymtl.inf3995.oronos.parser;

import java.util.List;

/**
 * Created by Felix on 20/févr./2018.
 */

public class Rocket extends AbstractWidgetContainer<ContainableWidget> {

    public final String name;
    public final String id;

    protected Rocket(String name, String id, List<ContainableWidget> list) {
        super(list);
        this.name = name;
        this.id = id;
    }
}
