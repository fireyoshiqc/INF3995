package ca.polymtl.inf3995.oronos.widgets.views;

import android.content.Context;

import java.util.List;

import ca.polymtl.inf3995.oronos.widgets.containers.AbstractWidgetContainer;

/**
 * Created by Felix on 15/févr./2018.
 */

public class DisplayLogWidget extends AbstractWidgetContainer<CAN> implements ContainableWidget {
    public DisplayLogWidget(Context context, List<CAN> list) {
        super(context, list);
    }
}
