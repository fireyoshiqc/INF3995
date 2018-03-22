package ca.polymtl.inf3995.oronos.activities;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import ca.polymtl.inf3995.oronos.R;
import timber.log.Timber;

/**
 * Created by Fabri on 2018-03-01.
 */

public class DrawerActivity extends AppCompatActivity {
    private final int dataIndex = 0;
    private final int themeIndex = 1;
    private final int pdfIndex = 2;
    private final int disconnectionIndex = 3;
    private DrawerLayout drawerLayout;

    protected void onCreateDrawer() {
        drawerLayout = findViewById(R.id.main_layout);
        final NavigationView navigationView = findViewById(R.id.nav_view);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        drawerLayout.closeDrawers();

                        if (menuItem == navigationView.getMenu().getItem(dataIndex)) {
                            toolbar.setTitle("ORONOS");
                        } else if (menuItem == navigationView.getMenu().getItem(themeIndex)) {
                            toolbar.setTitle("Theme selection");
                        } else if (menuItem == navigationView.getMenu().getItem(pdfIndex)) {
                            toolbar.setTitle("PDF downloads");
                        } else {
                            toolbar.setTitle("Disconnection");
                        }
                        return true;
                    }
                });

        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        } else {
            Timber.e("Error: Could not create action bar for DrawerActivity.");
        }

        //default selected button
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
