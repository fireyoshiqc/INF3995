package ca.polymtl.inf3995.oronos.parser;

/**
 * Created by Felix on 15/févr./2018.
 */

public class Tab {
    public final String name;
    public final ContainableWidget contents;

    protected Tab(String name, ContainableWidget contents) {
        this.name = name;
        this.contents = contents;
    }
}
