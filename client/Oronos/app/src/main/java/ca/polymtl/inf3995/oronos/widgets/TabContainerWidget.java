package ca.polymtl.inf3995.oronos.widgets;

import android.app.ActionBar;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * Created by Felix on 13/févr./2018.
 */

public class TabContainerWidget extends AbstractContainerWidget<TabWidget>  {


    public TabContainerWidget(Context context) {
        super(context);
    }

    public TabContainerWidget(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.disp = WidgetDisposition.TABS;
    }
}
