package ca.polymtl.inf3995.oronos.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import ca.polymtl.inf3995.oronos.R;

/**
 * Created by Felix on 13/févr./2018.
 */

public class FixedContainerWidget extends AbstractContainerWidget<AbstractWidget> {


    public FixedContainerWidget(Context context) {
        super(context);
    }

    public FixedContainerWidget(Context context, @Nullable AttributeSet attrs) throws UnsupportedWidgetDispositionException {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FixedContainerWidget);
        String disp = a.getString(R.styleable.FixedContainerWidget_disposition);
        switch (disp) {
            case "column":
                this.disp = WidgetDisposition.COLUMN;
                break;
            case "row":
                this.disp = WidgetDisposition.ROW;
                break;
            default:
                throw new UnsupportedWidgetDispositionException(disp);
        }
    }
}
