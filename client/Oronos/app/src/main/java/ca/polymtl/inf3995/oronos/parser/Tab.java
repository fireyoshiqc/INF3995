package ca.polymtl.inf3995.oronos.parser;

import android.support.annotation.Nullable;

/**
 * Created by Felix on 15/févr./2018.
 */

public class Tab implements ContainableWidget {
    private final String name;
    private final ContainableWidget contents;

    Tab(String name, @Nullable ContainableWidget contents) {
        this.name = name;
        this.contents = contents;
    }

    public String getName() {
        return name;
    }

    public ContainableWidget getContents() {
        return contents;
    }
}
