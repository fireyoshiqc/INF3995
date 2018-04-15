package ca.polymtl.inf3995.oronos.widgets.containers;

import android.support.annotation.Nullable;

import ca.polymtl.inf3995.oronos.widgets.views.ContainableWidget;
import ca.polymtl.inf3995.oronos.widgets.views.OronosView;

/**
 * <h1>Tab</h1>
 * This class represent a tab (container that is a child of a tabContainer and parent to an
 * Oronos View).
 *
 * @author Félix Boulet
 * @version 0.0
 * @since 2018-04-12
 */
public class Tab implements ContainableWidget {
    private final String name;
    private final OronosView contents;

    /**
     * Constructor that needs the name of the tab and the view that is to be displayed in the tab.
     *
     * @param name string representing the name of the tab.
     * @param contents an Oronos view representing the content of the tab.
     * */
    public Tab(String name, @Nullable OronosView contents) {
        this.name = name;
        this.contents = contents;
    }

    /**
     * Accessor of the tab name, a string.
     * */
    public String getName() {
        return name;
    }

    /**
     * Accessor of the tab content, an Oronos view.
     * */
    public OronosView getContents() {
        return contents;
    }
}
