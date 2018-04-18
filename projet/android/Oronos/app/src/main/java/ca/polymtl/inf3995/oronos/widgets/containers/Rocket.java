package ca.polymtl.inf3995.oronos.widgets.containers;

import android.content.Context;

import java.util.List;

import ca.polymtl.inf3995.oronos.widgets.views.OronosView;

/**
 * <h1>Rocket</h1>
 * This class represent the rocket as a xml tag having a name and a rocket id and
 * being parent of many views.
 *
 * @author Félix Boulet
 * @version 0.0
 * @since 2018-04-12
 */
public class Rocket extends AbstractWidgetContainer<OronosView> {

    private final String name;
    private final String rocketId;

    /**
     * Constructor of a rocket; needs the activity context, a rocket name & id and
     * a list of all children views.
     *
     * @param context activity context.
     * @param name string that is the name of the rocket.
     * @param id string that is the rocket id.
     * @param list a list of OronosView that are children of the rocket.
     * */
    public Rocket(Context context, String name, String id, List<OronosView> list) {
        super(context, list);
        this.name = name;
        this.rocketId = id;
    }

    /**
     * Get accessor of the rocket's name.
     * */
    public String getName() {
        return name;
    }

    /**
     * Get accessor of the rocket's id.
     * */
    public String getRocketId() {
        return rocketId;
    }
}
