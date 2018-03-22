package ca.polymtl.inf3995.oronos.widgets.containers;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import ca.polymtl.inf3995.oronos.widgets.views.UnsupportedWidget;
import ca.polymtl.inf3995.oronos.widgets.views.CleanableWidget;
import ca.polymtl.inf3995.oronos.widgets.views.OronosView;

/**
 * Created by Felix on 15/févr./2018.
 */

public class TabContainer extends AbstractWidgetContainer<Tab> implements CleanableWidget {

    private TabLayout tabLayout;
    private LinearLayout containerLayout;

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
                containerLayout.addView(list.get(tabLayout.getSelectedTabPosition()).getContents());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

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
