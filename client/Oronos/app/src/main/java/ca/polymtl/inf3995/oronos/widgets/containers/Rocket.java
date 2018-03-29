package ca.polymtl.inf3995.oronos.widgets.containers;

import android.content.Context;

import java.util.List;

import ca.polymtl.inf3995.oronos.widgets.views.OronosView;

/**
 * Created by Felix on 20/févr./2018.
 */

public class Rocket extends AbstractWidgetContainer<OronosView> {

    private final String name;
    private final String rocketId;

    public Rocket(Context context, String name, String id, List<OronosView> list) {
        super(context, list);
        this.name = name;
        this.rocketId = id;
    }

    public String getName() {
        return name;
    }

    public String getRocketId() {
        return rocketId;
    }
}
