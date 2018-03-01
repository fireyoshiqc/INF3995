package ca.polymtl.inf3995.oronos.parser;

import java.util.List;

/**
 * Created by Felix on 20/févr./2018.
 */

public class Rocket extends AbstractWidgetContainer<ContainableWidget> {

    private final String name;
    private final String id;

    Rocket(String name, String id, List<ContainableWidget> list) {
        super(list);
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}
