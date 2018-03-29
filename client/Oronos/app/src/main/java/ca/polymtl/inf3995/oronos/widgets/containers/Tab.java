package ca.polymtl.inf3995.oronos.widgets.containers;

import android.support.annotation.Nullable;

import ca.polymtl.inf3995.oronos.widgets.views.ContainableWidget;
import ca.polymtl.inf3995.oronos.widgets.views.OronosView;

/**
 * Created by Felix on 15/févr./2018.
 */

public class Tab implements ContainableWidget {
    private final String name;
    private final OronosView contents;

    public Tab(String name, @Nullable OronosView contents) {
        this.name = name;
        this.contents = contents;
    }

    public String getName() {
        return name;
    }

    public OronosView getContents() {
        return contents;
    }
}
