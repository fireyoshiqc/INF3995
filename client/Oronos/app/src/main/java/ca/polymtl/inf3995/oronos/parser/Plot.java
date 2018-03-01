package ca.polymtl.inf3995.oronos.parser;

import java.util.List;

/**
 * Created by Felix on 15/févr./2018.
 */

public class Plot extends AbstractCANContainer implements ContainableWidget {
    private final String name;
    private final String unit;
    private final String axis;

    Plot(String name, String unit, String axis, List<CAN> list) {
        super(list);
        this.name = name;
        this.unit = unit;
        this.axis = axis;
    }

    public String getName() {
        return name;
    }

    public String getUnit() {
        return unit;
    }

    public String getAxis() {
        return axis;
    }
}
