package ca.polymtl.inf3995.oronos.widgets.views;

import android.content.Context;

import java.util.List;

import ca.polymtl.inf3995.oronos.widgets.containers.AbstractWidgetContainer;

public class DisplayLogWidget extends AbstractWidgetContainer<CAN> implements ContainableWidget {
    public DisplayLogWidget(Context context, List<CAN> list) {
        super(context, list);
    }

    public void receiveMsgToLog(
            String CANSID,
            String data1,
            String data2,
            String srcModule,
            String noSerial,
            String noMsg) {

    }
}
