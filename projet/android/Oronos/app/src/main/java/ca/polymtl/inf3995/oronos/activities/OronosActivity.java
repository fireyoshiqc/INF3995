package ca.polymtl.inf3995.oronos.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import ca.polymtl.inf3995.oronos.R;
import ca.polymtl.inf3995.oronos.fragments.HomeScreenFragment;
import ca.polymtl.inf3995.oronos.fragments.MiscFilesFragment;
import ca.polymtl.inf3995.oronos.fragments.TelemetryFragment;
import ca.polymtl.inf3995.oronos.services.RestHttpWrapper;
import ca.polymtl.inf3995.oronos.utils.LogTree;
import ca.polymtl.inf3995.oronos.utils.PermissionsUtil;
import ca.polymtl.inf3995.oronos.utils.ThemeUtil;
import ca.polymtl.inf3995.oronos.widgets.views.FindMe;
import timber.log.Timber;

/**
 * <h1>Oronos Activity</h1>
 * This Oronos activity is the main activity of the application. It is responsible of handling the
 * three fragments (Home Screen Fragment, Misc Files Fragment, Telemetry Fragment)between which the
 * user can navigate. It also takes care of the theme switch.
 *
 * @author Félix Boulet, Fabrice Charbonneau
 * @version 0.0
 * @since 2018-04-12
 */
public class OronosActivity extends AppCompatActivity {

    static final private int STORAGE_PERMISSION_REQUEST = 42;
    private static String lastFragmentTag = "";
    private final int dataIndex = 0;
    private final int themeIndex = 1;
    private final int pdfIndex = 2;
    private DrawerLayout drawerLayout;
    private boolean selectedThemeIsDark;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private Snackbar warningBar;
    private TelemetryFragment telemetryFragment;
    private HomeScreenFragment homeScreenFragment;
    private MiscFilesFragment miscFilesFragment;

    /**
     * {@inheritDoc}
     * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtil.getInstance().initialize(this);
        selectedThemeIsDark = ThemeUtil.getInstance().isThemeSetToDark();
        ThemeUtil.onActivityCreateSetTheme(this);
        Timber.plant(new LogTree(getApplicationContext()));

        if (!PermissionsUtil.hasPermissions(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            warningBar = Snackbar.make(findViewById(android.R.id.content), "Write to external memory permission is required for using this app.", Snackbar.LENGTH_INDEFINITE);
            warningBar.setAction("ENABLE", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_REQUEST);
                }
            }).show();
        } else {
            grantPermissions();
        }

        homeScreenFragment = new HomeScreenFragment();
        telemetryFragment = new TelemetryFragment();
        miscFilesFragment = new MiscFilesFragment();

        setContentView(R.layout.activity_main);
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if (f != null) {
                    setLastFragmentTag(f.getTag());
                }
            }
        });

        if (savedInstanceState == null) {
            RestHttpWrapper.getInstance().setup(getApplicationContext());
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeScreenFragment, "home").addToBackStack("home").commit();
            setLastFragmentTag("home");
        } else {
            switch (lastFragmentTag) {
                case "home":
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeScreenFragment, "home").addToBackStack("home").commit();
                    setLastFragmentTag("home");
                    break;
                case "telemetry":
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, telemetryFragment, "telemetry").addToBackStack("telemetry").commit();
                    setLastFragmentTag("telemetry");
                    break;
                case "misc":
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, miscFilesFragment, "misc").addToBackStack("misc").commit();
                    setLastFragmentTag("misc");
                    break;
                default:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeScreenFragment, "home").addToBackStack("home").commit();
                    setLastFragmentTag("home");

            }
        }
    }

    /**
     * This method dismiss the permissions not granted warning bar once the permissions are granted.
     */
    public void grantPermissions() {
        if (warningBar != null && warningBar.isShown()) {
            warningBar.dismiss();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case STORAGE_PERMISSION_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    grantPermissions();
                } else {
                    warningBar.show();
                }
            }
            break;
            case FindMe.GPS_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    for (FindMe findMe : FindMe.getInstances()) {
                        findMe.grantPermissions();
                    }


                } else {
                    for (FindMe findMe : FindMe.getInstances()) {
                        findMe.showPermissionWarning();
                    }
                }
            }
            break;
        }
    }

    /**
     * This method sets up the hamburger menu and the callback responsible for the actions generated
     * by a user click on an item of the menu.
     */
    protected void onCreateDrawer() {
        drawerLayout = findViewById(R.id.main_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        // close drawer when item is tapped
                        drawerLayout.closeDrawers();

                        if (menuItem == navigationView.getMenu().getItem(dataIndex)) {
                            switchToMainActivity();
                        } else if (menuItem == navigationView.getMenu().getItem(themeIndex)) {
                            themeSelectionPopup();
                        } else if (menuItem == navigationView.getMenu().getItem(pdfIndex)) {
                            switchToMiscFilesActivity();
                        } else {
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

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        onCreateDrawer();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBackPressed() {

        int count = getFragmentManager().getBackStackEntryCount();
        if (lastFragmentTag.equals("home")) {
            finish();
        } else if (count == 0) {
            super.onBackPressed();
        } else {
            getSupportFragmentManager().popBackStack();
        }

    }

    /**
     * This method displays a small pop up to confirm the user wants to be disconnected and sent
     * to Home Screen Activity.
     */
    private void disconnectionPopup() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
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

    /**
     * This method displays a small pop up to let the user choose between the available themes.
     */
    private void themeSelectionPopup() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Light theme button clicked
                        if (selectedThemeIsDark) {
                            selectedThemeIsDark = false;
                            themeWarningPopup();
                        }

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //Dark theme button clicked
                        if (!selectedThemeIsDark) {
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

    /**
     * This method displays a small pop up to confirm the user wants to switch themes.
     */
    private void themeWarningPopup() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Ok button clicked
                        if (selectedThemeIsDark) {
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

    /**
     * This method destroys whatever activity is currently up and is starting the Main Activity.
     */
    public void switchToMainActivity() {
        if (!lastFragmentTag.equals("telemetry")) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, telemetryFragment, "telemetry").addToBackStack("telemetry").commit();
            setLastFragmentTag("telemetry");
        }

    }

    /**
     * This method starts a fragment responsible of managing the available pdf list from the
     * server.
     */
    private void switchToMiscFilesActivity() {
        if (!lastFragmentTag.equals("misc")) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, miscFilesFragment, "misc").addToBackStack("misc").commit();
            setLastFragmentTag("misc");
        }
    }

    /**
     * This method destroys whatever activity is currently up and is starting the Home Screen
     * Activity.
     */
    private void switchToHomeScreenActivity() {
        if (!lastFragmentTag.equals("home")) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeScreenFragment, "home").addToBackStack("home").commit();
            setLastFragmentTag("home");
        }
    }

    /**
     * This method takes a string and displays it as the new toolbar title.
     *
     * @param title The new title to display.
     */
    public void changeToolbarTitle(String title) {
        toolbar.setTitle(title);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
    }

    /**
     * This method provocates a theme switch in order to display the light theme.
     */
    private void setThemeToLight() {
        //TODO
        ThemeUtil.getInstance().switchToLightTheme();
        recreate();
    }

    /**
     * This method provocates a theme switch in order to display the dark theme.
     */
    private void setThemeToDark() {
        //TODO
        ThemeUtil.getInstance().switchToDarkTheme();
        recreate();
    }

    /**
     * This method allows the app to display its toolbar.
     * */
    public void showToolbar() {
        this.toolbar.setVisibility(View.VISIBLE);
    }

    /**
     * This method allows the app to hide its toolbar.
     * */
    public void hideToolbar() {
        this.toolbar.setVisibility(View.GONE);
    }

    /**
     * Set method for the last fragment tag.
     *
     * @param tag a string representing a fragment tag, either telemetry, home or misc. If something
     *            else is set, the app will redirect itself towards the home screen fragment.
     * */
    public void setLastFragmentTag(String tag) {
        lastFragmentTag = tag;
    }

    /**
     * Set method for the telemetry fragment.
     *
     * @param telemetryFragment a telemetry fragment.
     * */
    public void setTelemetryFragment(TelemetryFragment telemetryFragment) {
        this.telemetryFragment = telemetryFragment;
    }

}
