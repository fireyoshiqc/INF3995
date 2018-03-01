package ca.polymtl.inf3995.oronos;

import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * Created by Fabri on 2018-03-01.
 */

public class DrawerActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    protected void onCreateDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout);
        final NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        drawerLayout.closeDrawers();

                        //data
                        if(menuItem == navigationView.getMenu().getItem(0)){
                            TextView mainText = findViewById(R.id.myTextView);
                            mainText.setText("data selected");
                        } else if(menuItem == navigationView.getMenu().getItem(1)) {
                            TextView mainText = findViewById(R.id.myTextView);
                            mainText.setText("Select which theme you want.");
                        } else if(menuItem == navigationView.getMenu().getItem(2)) {
                            TextView mainText = findViewById(R.id.myTextView);
                            mainText.setText("List of PDFs. Select one to download.");
                        } else {
                            TextView mainText = findViewById(R.id.myTextView);
                            mainText.setText("Are you sure you want to disconnect? Yes/No.");
                        }


                        return true;
                    }
                });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
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
