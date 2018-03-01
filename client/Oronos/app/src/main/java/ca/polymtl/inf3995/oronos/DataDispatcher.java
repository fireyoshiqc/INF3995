package ca.polymtl.inf3995.oronos;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by prst on 2018-03-01.
 */

public class DataDispatcher {

    static public void dataToDispatch(List<Object> data) {
        EventBus.getDefault().post(new DataMessage((String) data.get(0), data));
    }

}
