package ca.polymtl.inf3995.oronos.widgets.containers;

/**
 * <h1>Unsupported Container Widget Exception</h1>
 * This class is an exception that can occur when a Container Widget is inappropriate.
 *
 * @author Félix Boulet
 * @version 0.0
 * @since 2018-04-12
 */
public class UnsupportedContainerWidgetException extends Exception {
    public UnsupportedContainerWidgetException(String message) {
        super("Erreur! Le conteneur " + message + " contient trop peu ou trop de sous-widgets.");
    }
}

