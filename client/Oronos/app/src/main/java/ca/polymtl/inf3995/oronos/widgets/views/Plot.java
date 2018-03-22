package ca.polymtl.inf3995.oronos.widgets.views;

import android.content.Context;

import java.util.List;

import ca.polymtl.inf3995.oronos.widgets.containers.AbstractWidgetContainer;

/**
 * Created by Felix on 15/févr./2018.
 */

public class Plot extends AbstractWidgetContainer<CAN> {
    private final String name;
    private final String unit;
    private final String axis;

    public Plot(Context context, String name, String unit, String axis, List<CAN> list) {
        super(context, list);
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
