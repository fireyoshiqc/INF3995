package ca.polymtl.inf3995.oronos.widgets.containers;

/**
 * Created by Felix on 20/févr./2018.
 */

public class UnsupportedContainerWidgetException extends Exception {
    public UnsupportedContainerWidgetException(String message) {
        super("Erreur! Le conteneur " + message + " contient trop peu ou trop de sous-widgets.");
    }
}

