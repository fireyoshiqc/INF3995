package ca.polymtl.inf3995.oronos.parser;

import java.util.List;

/**
 * Created by Felix on 15/févr./2018.
 */

public class Plot extends AbstractCANContainer {
    public final String name;
    public final String unit;
    public final String axis;

    protected Plot(String name, String unit, String axis, List<CAN> list) {
        super(list);
        this.name = name;
        this.unit = unit;
        this.axis = axis;
    }
}
