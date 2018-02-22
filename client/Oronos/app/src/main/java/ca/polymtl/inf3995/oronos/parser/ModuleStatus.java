package ca.polymtl.inf3995.oronos.parser;

/**
 * Created by Felix on 15/févr./2018.
 */

public class ModuleStatus implements ContainableWidget {
    public final int nGrid;
    public final int nColumns;

    protected ModuleStatus(int nGrid, int nColumns) {
        this.nGrid = nGrid;
        this.nColumns = nColumns;
    }
}
