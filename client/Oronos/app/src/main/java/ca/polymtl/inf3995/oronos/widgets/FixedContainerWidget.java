package ca.polymtl.inf3995.oronos.widgets;

/**
 * Created by Felix on 13/févr./2018.
 */

public class FixedContainerWidget extends AbstractContainerWidget<AbstractWidget> {

    public FixedContainerWidget(WidgetDisposition disp) throws UnsupportedWidgetDispositionException {
        super();
        if (disp == WidgetDisposition.COLUMN || disp == WidgetDisposition.ROW) {
            this.disp = disp;
        } else {
            throw new UnsupportedWidgetDispositionException(disp.toString());
        }
    }
}
