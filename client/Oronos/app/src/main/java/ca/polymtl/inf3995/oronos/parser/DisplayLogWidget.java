package ca.polymtl.inf3995.oronos.parser;

import android.view.View;

import java.util.List;

/**
 * Created by Felix on 15/févr./2018.
 */

public class DisplayLogWidget extends AbstractCANContainer implements ContainableWidget {
    DisplayLogWidget(List<CAN> list) {
        super(list);
    }

    @Override
    public View getView() {
        return null;
    }
}
