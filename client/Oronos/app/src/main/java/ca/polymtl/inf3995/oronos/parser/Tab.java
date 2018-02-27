package ca.polymtl.inf3995.oronos.parser;

import android.support.annotation.Nullable;

/**
 * Created by Felix on 15/févr./2018.
 */

public class Tab implements ContainableWidget {
    public final String name;
    public final ContainableWidget contents;

    protected Tab(String name, @Nullable ContainableWidget contents) {
        this.name = name;
        this.contents = contents;
    }
}
