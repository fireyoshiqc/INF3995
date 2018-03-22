package ca.polymtl.inf3995.oronos.widgets.containers;

import android.content.Context;

import java.util.List;

import ca.polymtl.inf3995.oronos.widgets.views.ContainableWidget;
import ca.polymtl.inf3995.oronos.widgets.views.OronosView;

/**
 * Created by Felix on 22/févr./2018.
 */

public abstract class AbstractWidgetContainer<T extends ContainableWidget> extends OronosView {

    protected final List<T> list;

    public AbstractWidgetContainer(Context context, List<T> list) {
        super(context);
        this.list = list;
    }

    public List<T> getList() {
        return list;
    }

}
