package ca.polymtl.inf3995.oronos.parser;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.GridView;
import android.widget.LinearLayout;

public class ModuleStatus extends OronosView {

    protected ModuleStatus(Context context, int nGrid, int nColumns) {
        super(context);
        GridView gridView = new GridView(context);
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
        setOrientation(LinearLayout.VERTICAL);
        setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        addView(gridView);
    }
}
