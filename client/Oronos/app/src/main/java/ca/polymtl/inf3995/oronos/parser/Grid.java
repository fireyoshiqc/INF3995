package ca.polymtl.inf3995.oronos.parser;

import android.support.annotation.Nullable;

/**
 * Created by Felix on 15/févr./2018.
 */

public class Grid implements ContainableWidget {
    public final int row;
    public final int col;
    public final ContainableWidget contents;

    protected Grid(int row, int col, @Nullable ContainableWidget contents) {
        this.row = row;
        this.col = col;
        this.contents = contents;
    }
}
