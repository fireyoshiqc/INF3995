package ca.polymtl.inf3995.oronos.parser;

import java.util.List;

/**
 * Created by Felix on 20/févr./2018.
 */

public class Rocket {

    public final String name;
    public final String id;
    public final List<GridContainer> list;

    protected Rocket(String name, String id, List<GridContainer> list) {
        this.name = name;
        this.id = id;
        this.list = list;
    }
}
