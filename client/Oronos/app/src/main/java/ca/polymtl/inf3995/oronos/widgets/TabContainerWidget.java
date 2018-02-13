package ca.polymtl.inf3995.oronos.widgets;

import android.app.ActionBar;

/**
 * Created by Felix on 13/févr./2018.
 */

public class TabContainerWidget extends AbstractContainerWidget<TabWidget>  {

    public TabContainerWidget() {
        super();
        this.disp = WidgetDisposition.TABS;
    }
}
