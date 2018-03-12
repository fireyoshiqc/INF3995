package ca.polymtl.inf3995.oronos.parser;

import android.content.Context;
import android.widget.LinearLayout;

import java.util.List;

/**
 * Created by Felix on 22/févr./2018.
 */

public abstract class AbstractWidgetContainer<T extends ContainableWidget> extends OronosView {

    protected final List<T> list;

    AbstractWidgetContainer(Context context, List<T> list) {
        super(context);
        this.list = list;
    }

    public List<T> getList() {
        return list;
    }

}
