package ca.polymtl.inf3995.oronos.widgets;

/**
 * Created by Felix on 13/févr./2018.
 */

public class UnsupportedWidgetDispositionException extends Exception {
    public UnsupportedWidgetDispositionException(String message) {
        super("Ce type de conteneur ne supporte pas la disposition : " + message);
    }
}
