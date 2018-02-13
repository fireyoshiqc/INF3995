package ca.polymtl.inf3995.oronos.widgets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Felix on 13/févr./2018.
 */

public abstract class AbstractContainerWidget<T extends AbstractWidget> extends AbstractWidget {

    protected WidgetDisposition disp;
    private ArrayList<T> children;

    public AbstractContainerWidget() {
        this.children = new ArrayList<>();
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
