package ca.polymtl.inf3995.oronos.parser;

import android.content.Context;

import java.util.List;

/**
 * Created by Felix on 20/févr./2018.
 */

public class Rocket extends AbstractWidgetContainer<OronosView> {

    private final String name;
    private final String rocketId;

    Rocket(Context context, String name, String id, List<OronosView> list) {
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
