package ca.polymtl.inf3995.oronos.parser;

/**
 * Created by Felix on 15/févr./2018.
 */

public class Tab {
    public final String name;
    public final TabbableWidget contents;

    protected Tab(String name, TabbableWidget contents) {
        this.name = name;
        this.contents = contents;
    }
}
