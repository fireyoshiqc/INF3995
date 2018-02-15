package ca.polymtl.inf3995.oronos.widgets;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Felix on 13/févr./2018.
 */

public abstract class AbstractContainerWidget<T extends AbstractWidget> extends AbstractWidget {

    protected WidgetDisposition disp;
    private ArrayList<T> children = new ArrayList<>();;

    public AbstractContainerWidget(Context context) {
        super(context);
    }

    public AbstractContainerWidget(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void addChild(T widget) {
        this.children.add(widget);
    }

    public void addChildren(T[] widgets) {
        this.children.addAll(Arrays.asList(widgets));
    }

    public void addChildren(List<T> widgets) {
        this.children.addAll(widgets);
    }

    public ArrayList<T> getChildren() {
        return this.children;
    }
}
