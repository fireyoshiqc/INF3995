package ca.polymtl.inf3995.oronos.parser;

import android.content.Context;

/**
 * Created by Felix on 15/févr./2018.
 */

public class ModuleStatus extends OronosView {
    private final int nGrid;
    private final int nColumns;

    protected ModuleStatus(Context context, int nGrid, int nColumns) {
        super(context);
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
