package ca.polymtl.inf3995.oronos.parser;

/**
 * Created by Felix on 20/févr./2018.
 */

public class UnsupportedContainerWidgetException extends Exception {
    UnsupportedContainerWidgetException(String message) {
        super("Erreur! Le conteneur " + message + " contient trop peu ou trop de sous-tags.");
    }
}

