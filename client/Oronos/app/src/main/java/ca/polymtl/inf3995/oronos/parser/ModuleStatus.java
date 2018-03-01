package ca.polymtl.inf3995.oronos.parser;

/**
 * Created by Felix on 15/févr./2018.
 */

public class ModuleStatus implements ContainableWidget {
    private final int nGrid;
    private final int nColumns;

    protected ModuleStatus(int nGrid, int nColumns) {
        this.nGrid = nGrid;
        this.nColumns = nColumns;
    }

    public int getnGrid() {
        return nGrid;
    }

    public int getnColumns() {
        return nColumns;
    }
}
