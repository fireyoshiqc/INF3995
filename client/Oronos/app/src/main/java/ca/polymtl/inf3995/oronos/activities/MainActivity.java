package ca.polymtl.inf3995.oronos.activities;

import android.content.pm.PackageManager;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.polymtl.inf3995.oronos.R;
import ca.polymtl.inf3995.oronos.services.DataDispatcher;
import ca.polymtl.inf3995.oronos.services.OronosXmlParser;
import ca.polymtl.inf3995.oronos.services.RestHttpWrapper;
import ca.polymtl.inf3995.oronos.services.SocketClient;
import ca.polymtl.inf3995.oronos.utils.GlobalParameters;
import ca.polymtl.inf3995.oronos.utils.JsonHelper;
import ca.polymtl.inf3995.oronos.utils.LogTree;
import ca.polymtl.inf3995.oronos.widgets.adapters.ImageAdapter;
import ca.polymtl.inf3995.oronos.widgets.containers.AbstractWidgetContainer;
import ca.polymtl.inf3995.oronos.widgets.containers.Rocket;
import ca.polymtl.inf3995.oronos.widgets.containers.Tab;
import ca.polymtl.inf3995.oronos.widgets.containers.UnsupportedContainerWidgetException;
import ca.polymtl.inf3995.oronos.widgets.views.FindMe;
import ca.polymtl.inf3995.oronos.widgets.views.OronosView;
import ca.polymtl.inf3995.oronos.widgets.views.Plot;
import timber.log.Timber;


public class MainActivity extends DrawerActivity {
    private final int MENU_VIEW_ID = -1;
    private int currentDataViewState;
    private boolean isMenuActive;

    private List<OronosView> viewsContainer;
    private GridView gridView;
    private RelativeLayout dataLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setUpUtilities();
        setContentView(R.layout.activity_main);

        fillViewsContainer();
        setUpToolbar();
        super.onCreateDrawer();
        // Check if filling the viewsContainer worked;
        dataLayout = findViewById(R.id.data_layout);
        if (viewsContainer != null) {
            dataLayout.addView(viewsContainer.get(0), -1);
        } else {
            throw new NullPointerException("No view in viewOfGrid, cannot display any data.");
        }

        // Ready to start
        currentDataViewState = 0;
        isMenuActive = false;
        Timber.v("Main Activity : Creation Done.");
    }

    /**
     * Utilities for the client
     */
    private void setUpUtilities() {
        //CookieHandler.setDefault(new CookieManager());
        Timber.plant(new LogTree());
        SocketClient.getInstance().setup(GlobalParameters.CLIENT_ADDRESS, GlobalParameters.udpPort);
        DataDispatcher.setContext(getApplicationContext());
        //RestHttpWrapper.getInstance().setup(getApplicationContext());
    }


    /**
     * This method declare the toolbar and its menu elements.
     */
    private void setUpToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("ORONOS");
        setSupportActionBar(toolbar);

        gridView = new GridView(this);
        int gridPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        gridView.setPadding(gridPadding, gridPadding, gridPadding, gridPadding);
        gridView.setNumColumns(GridView.AUTO_FIT);
        gridView.setVerticalSpacing(gridPadding);
        gridView.setHorizontalSpacing(gridPadding);
        gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        gridView.setGravity(Gravity.CENTER);
        ArrayList<ImageAdapter.OronosViewCardContents> names = new ArrayList<>();
        for (OronosView view : viewsContainer) {
            String name = view.getClass().getSimpleName();
            ArrayList<String> subnames = new ArrayList<>();
            if (view instanceof AbstractWidgetContainer) {
                for (Object sub : ((AbstractWidgetContainer) view).getList()) {
                    if (sub instanceof Tab) {
                        subnames.add("Tab - " + ((Tab) sub).getName());
                    } else if (sub instanceof Plot) {
                        subnames.add("Plot - " + ((Plot) sub).getName());
                    } else {
                        subnames.add(sub.getClass().getSimpleName());
                    }

                }
            }
            names.add(new ImageAdapter.OronosViewCardContents(name, subnames));
        }
        gridView.setAdapter(new ImageAdapter(this, names));

        /*
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                changeStateOfDataLayout(position);
            }
        });
        */
    }

    /**
     * This method uses the xml parser to fill the viewsContainer (list having all views
     * to be displayed in the dataLayout).
     */
    private void fillViewsContainer() {
        OronosXmlParser parser = new OronosXmlParser(this);
        try {
            //InputStream fis = getAssets().open("10_polaris.xml");
            InputStream fis = new FileInputStream(new File(getCacheDir(), GlobalParameters.layoutName));
            Rocket rocket = parser.parse(fis);

            viewsContainer = new ArrayList<>();
            viewsContainer.addAll(rocket.getList());
            //Afficher seulement 1 tab container
            //TabContainer tabtest = (TabContainer) rocket.getList().get(0);
            viewsContainer = new ArrayList<>();
            //viewsContainer.add(tabtest);

            //Afficher tout
            viewsContainer.addAll(rocket.getList());


            FindMe test = new FindMe(this);
            viewsContainer.add(test);

        } catch (IOException | XmlPullParserException | UnsupportedContainerWidgetException e) {
            e.printStackTrace();
        }
    }

    /**
     * Callback for inflating the menu in the toolbar. The menu used by the client can be found
     * in the res/menu/menu_main.xml file.
     *
     * @param menu Menu to inflate in the toolbar.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Callback for any triggered MenuItem in the toolbar.
     *
     * @param item MenuItem that was triggered in the toolbar.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_display_grid) {
            changeStateOfDataLayout(MENU_VIEW_ID);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case FindMe.GPS_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    // TODO: Use some kind of observer pattern to notify FindMe and Map elements
                    for (View view : viewsContainer) {
                        if (view instanceof FindMe) {
                            ((FindMe) view).grantPermissions();
                        }
                    }


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    // TODO: Use some kind of observer pattern to notify FindMe and Map elements
                    for (View view : viewsContainer) {
                        if (view instanceof FindMe) {
                            ((FindMe) view).showPermissionWarning();
                        }
                    }
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    /**
     * State machine for the dataLayout.
     *
     * @param nextView The int representing the nextView the client will display in its
     *                 dataLayout.
     */
    public void changeStateOfDataLayout(int nextView) {
        if (nextView == MENU_VIEW_ID) {
            if (isMenuActive) {
                dataLayout.removeView(gridView);
                isMenuActive = false;
            } else {
                dataLayout.addView(gridView);
                isMenuActive = true;
            }
        } else {
            if (isMenuActive) {
                dataLayout.removeView(gridView);
                isMenuActive = false;
            }
            dataLayout.removeView(viewsContainer.get(currentDataViewState));
            dataLayout.addView(viewsContainer.get(nextView));
            currentDataViewState = nextView;
        }
    }
}