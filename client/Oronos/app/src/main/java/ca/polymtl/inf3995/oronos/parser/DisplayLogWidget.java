package ca.polymtl.inf3995.oronos.parser;

import android.content.Context;
import android.view.View;

import java.util.List;

/**
 * Created by Felix on 15/févr./2018.
 */

public class DisplayLogWidget extends AbstractWidgetContainer<CAN> implements ContainableWidget {
    DisplayLogWidget(Context context, List<CAN> list) {
        super(context, list);
    }
}
