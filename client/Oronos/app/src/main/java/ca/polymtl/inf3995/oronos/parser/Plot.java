package ca.polymtl.inf3995.oronos.parser;

import android.content.Context;
import android.view.View;

import java.util.List;

/**
 * Created by Felix on 15/févr./2018.
 */

public class Plot extends AbstractWidgetContainer<CAN> {
    private final String name;
    private final String unit;
    private final String axis;

    Plot(Context context, String name, String unit, String axis, List<CAN> list) {
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
