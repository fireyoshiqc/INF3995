package ca.polymtl.inf3995.oronos.parser;

/**
 * Created by Felix on 15/févr./2018.
 */

public class Grid {
    public final int row;
    public final int col;
    public final TabContainer tabContainer;

    protected Grid(int row, int col, TabContainer tabContainer) {
        this.row = row;
        this.col = col;
        this.tabContainer = tabContainer;
    }
}
