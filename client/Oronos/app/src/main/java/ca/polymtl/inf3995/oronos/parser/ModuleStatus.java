package ca.polymtl.inf3995.oronos.parser;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.GridView;

/**
 * Created by Felix on 15/févr./2018.
 */

public class ModuleStatus extends OronosView {
    private final int nGrid;
    private final int nColumns;

    private GridView gridView;

    protected ModuleStatus(Context context, int nGrid, int nColumns) {
        super(context);
        this.nGrid = nGrid;
        this.nColumns = nColumns;
        gridView = new GridView(context);
        gridView.setNumColumns(nColumns);
        gridView.setBackgroundColor(Color.TRANSPARENT);
        gridView.setVerticalSpacing(10);
        gridView.setHorizontalSpacing(10);
        gridView.setGravity(Gravity.CENTER);
        gridView.setLayoutParams(new GridView.LayoutParams(
                GridView.LayoutParams.MATCH_PARENT,
                GridView.LayoutParams.MATCH_PARENT
        ));
        gridView.setAdapter(new ModuleStatusAdapter(context, nGrid, nColumns));
    }

    public int getnGrid() {
        return nGrid;
    }

    public int getnColumns() {
        return nColumns;
    }

    public View getView() {
        return gridView;
    }
}
