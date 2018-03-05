package ca.polymtl.inf3995.oronos.parser;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Felix on 15/févr./2018.
 */

public class TabContainer extends AbstractWidgetContainer<Tab> implements ContainableWidget, CleanableWidget {

    private LinearLayout layout;
    private TabLayout tabLayout;
    private LinearLayout containerLayout;

    TabContainer(List<Tab> list, Context context) {
        super(list);
        layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        tabLayout = new TabLayout(context);
        tabLayout.setLayoutParams(new TabLayout.LayoutParams(TabLayout.LayoutParams.MATCH_PARENT, TabLayout.LayoutParams.WRAP_CONTENT));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        containerLayout = new LinearLayout(context);
        containerLayout.setOrientation(LinearLayout.VERTICAL);
        containerLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        layout.addView(tabLayout);
        layout.addView(containerLayout);
    }

    public void buildTabs(Context context) {
        for (Tab tab : list) {
            TabLayout.Tab vTab = tabLayout.newTab();
            vTab.setText(tab.getName());
            tabLayout.addTab(vTab);
        }
        containerLayout.addView(list.get(tabLayout.getSelectedTabPosition()).getContents().getView());
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                containerLayout.removeAllViewsInLayout();
                containerLayout.addView(list.get(tabLayout.getSelectedTabPosition()).getContents().getView());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }});
    }

    @Override
    public ContainableWidget cleanup() {
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
            return this;
        }
    }

    @Override
    public View getView() {
        return layout;
    }
}
