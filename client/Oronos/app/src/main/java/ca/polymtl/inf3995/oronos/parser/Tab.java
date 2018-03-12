package ca.polymtl.inf3995.oronos.parser;

import android.support.annotation.Nullable;
import android.view.View;

/**
 * Created by Felix on 15/févr./2018.
 */

public class Tab implements ContainableWidget {
    private final String name;
    private final OronosView contents;

    Tab(String name, @Nullable OronosView contents) {
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
