package ca.polymtl.inf3995.oronos.widgets.containers;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import ca.polymtl.inf3995.oronos.widgets.views.CleanableWidget;
import ca.polymtl.inf3995.oronos.widgets.views.OronosView;
import ca.polymtl.inf3995.oronos.widgets.views.UnsupportedWidget;

/**
 * <h1>Tab Container</h1>
 * This class represent a Tab Container (child to any sort of container and parent to tab children).
 *
 * @author Félix Boulet
 * @version 0.0
 * @since 2018-04-12
 */
public class TabContainer extends AbstractWidgetContainer<Tab> implements CleanableWidget {

    private TabLayout tabLayout;
    private LinearLayout containerLayout;

    /**
     * Constructor that needs the activity context and a list of something relevant to this container
     * (like tabs for s tab container). It sets the layout in which the tabs are going to be displayed.
     *
     * @param context the context of the activity.
     * @param list the list of tabs in the tab container.
     * */
    public TabContainer(Context context, List<Tab> list) {
        super(context, list);
        setOrientation(LinearLayout.VERTICAL);
        setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        tabLayout = new TabLayout(getContext());
        tabLayout.setLayoutParams(new TabLayout.LayoutParams(TabLayout.LayoutParams.MATCH_PARENT, TabLayout.LayoutParams.WRAP_CONTENT));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        containerLayout = new LinearLayout(getContext());
        containerLayout.setOrientation(LinearLayout.VERTICAL);
        containerLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        addView(tabLayout);
        addView(containerLayout);
    }

    /**
     * This method adds every tab in the tab list into the tab container and register the actions on
     * tab selected/unselected/reselected to only display the selected tab in the tab container view.
     * */
    private void buildTabs() {
        for (Tab tab : list) {
            TabLayout.Tab vTab = tabLayout.newTab();
            vTab.setText(tab.getName());
            tabLayout.addTab(vTab);
        }
        containerLayout.addView(list.get(tabLayout.getSelectedTabPosition()).getContents());
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                containerLayout.removeAllViewsInLayout();

                OronosView newTab  = list.get(tabLayout.getSelectedTabPosition()).getContents();
                if (newTab.getParent() != null) {
                    ((ViewGroup)newTab.getParent()).removeView(newTab);
                }
                containerLayout.addView(newTab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public OronosView cleanup() {
        List<Tab> toRemove = new ArrayList<>();
        for (Tab tab : list) {
            if (tab.getContents() == null || tab.getContents() instanceof UnsupportedWidget) {
                toRemove.add(tab);
            }
        }
        list.removeAll(toRemove);
        if (list.size() == 1) {
            return list.get(0).getContents();
        } else if (list.size() == 0) {
            return null;
        } else {
            buildTabs();
            return this;
        }
    }
}
