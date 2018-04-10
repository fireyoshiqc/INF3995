package ca.polymtl.inf3995.oronos.widgets.containers;

import android.content.Context;

import java.util.List;

import ca.polymtl.inf3995.oronos.widgets.views.ContainableWidget;
import ca.polymtl.inf3995.oronos.widgets.views.OronosView;

/**
 * <h1>Abstract Widget Container</h1>
 * This abstract class is parent to any widget in need of a list (of CAN tags in most cases).
 *
 * @author Félix Boulet
 * @version 0.0
 * @since 2018-04-12
 */
public abstract class AbstractWidgetContainer<T extends ContainableWidget> extends OronosView {

    protected final List<T> list;

    /**
     * Constructor requesting the activity context and a list of something relevant to a widget.
     *
     * @param context context of the activity.
     * @param list a list of something relevant to a widget.
     */
    public AbstractWidgetContainer(Context context, List<T> list) {
        super(context);
        this.list = list;
    }

    public List<T> getList() {
        return list;
    }

}
