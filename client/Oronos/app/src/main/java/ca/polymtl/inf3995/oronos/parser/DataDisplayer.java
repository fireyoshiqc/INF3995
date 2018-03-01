package ca.polymtl.inf3995.oronos.parser;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import ca.polymtl.inf3995.oronos.DataMessage;

/**
 * Created by Felix on 15/févr./2018.
 */

public class DataDisplayer extends AbstractCANContainer implements ContainableWidget {

    DataDisplayer(List<CAN> list) {
        super(list);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataMessage(DataMessage dataMessage) {
        for (CAN can : list) {
            if (dataMessage.getId().equals(can.getId())) {
                List<Object> dataList = dataMessage.getData();
            }
        }
    }

}
