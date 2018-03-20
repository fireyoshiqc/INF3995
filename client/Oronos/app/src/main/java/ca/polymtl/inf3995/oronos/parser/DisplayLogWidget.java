package ca.polymtl.inf3995.oronos.parser;

import android.content.Context;

import java.util.List;

public class DisplayLogWidget extends AbstractWidgetContainer<CAN> implements ContainableWidget {


    DisplayLogWidget(Context context, List<CAN> list) {
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
