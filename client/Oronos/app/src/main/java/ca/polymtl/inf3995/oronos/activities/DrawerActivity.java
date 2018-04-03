package ca.polymtl.inf3995.oronos.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import ca.polymtl.inf3995.oronos.R;
import ca.polymtl.inf3995.oronos.utils.ThemeUtil;
import timber.log.Timber;

/**
 * Created by Fabri on 2018-03-01.
 */

public class DrawerActivity extends AppCompatActivity {
     private final int dataIndex = 0;
     private final int themeIndex = 1;
     private final int pdfIndex = 2;
    static private DrawerLayout drawerLayout;
    private boolean selectedThemeIsDark = ThemeUtil.isThemeSetToDark();

    private Toolbar toolbar;
    private NavigationView navigationView;

    protected void onCreateDrawer() {
        drawerLayout = findViewById(R.id.main_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        drawerLayout.closeDrawers();

                        if (menuItem == navigationView.getMenu().getItem(dataIndex)) {
                            switchToMainActivity();
                        } else if (menuItem == navigationView.getMenu().getItem(themeIndex)) {
                            themeSelectionPopup();
                        } else if (menuItem == navigationView.getMenu().getItem(pdfIndex)) {
                            switchToPdfActivity();
                        } else {
                            //toolbar.setTitle("Disconnection");
                            disconnectionPopup();
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


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtil.onActivityCreateSetTheme(this);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID)
    {
        super.setContentView(layoutResID);
        onCreateDrawer();
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

    private void disconnectionPopup() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        switchToHomeScreenActivity();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to disconnect?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private void themeSelectionPopup() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Light theme button clicked
                        if(selectedThemeIsDark){
                            selectedThemeIsDark = false;
                            themeWarningPopup();
                        }

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //Dark theme button clicked
                        if(!selectedThemeIsDark) {
                            selectedThemeIsDark = true;
                            themeWarningPopup();
                        }

                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Select which theme you want: ").setPositiveButton("Light theme", dialogClickListener)
                .setNegativeButton("Dark theme", dialogClickListener).show();
    }

    private void themeWarningPopup() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Ok button clicked
                        if(selectedThemeIsDark){
                            setThemeToDark();
                        } else {
                            setThemeToLight();
                        }
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //Cancel button clicked
                        selectedThemeIsDark = !selectedThemeIsDark;
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("WARNING\n\nChanging theme will clear logs, plots, and all previously acquired data. Are you sure you want to continue?").setPositiveButton("Ok", dialogClickListener)
                .setNegativeButton("Cancel", dialogClickListener).show();
    }

    private void switchToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        //toolbar.setTitle("ORONOS");
        finish();
        this.startActivity(intent);
    }

    private void switchToPdfActivity() { //This will have to be a fragment. 
        Intent intent = new Intent(this, PdfActivity.class);
        this.startActivity(intent);
    }

    private void switchToHomeScreenActivity() {
        Intent intent = new Intent(this, HomeScreenActivity.class);
        finish();
        this.startActivity(intent);
    }

    protected void changeToolbarTitle(String title){
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
    }

    private void setThemeToLight(){
        //TODO
        ThemeUtil.switchToLightTheme();
        recreate();
    }

    private void setThemeToDark(){
        //TODO
        ThemeUtil.switchToDarkTheme();
        recreate();
    }
}