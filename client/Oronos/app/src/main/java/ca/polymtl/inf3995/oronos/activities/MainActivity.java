package ca.polymtl.inf3995.oronos.activities;

import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.transition.AutoTransition;
import android.transition.Explode;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.GridLayoutAnimationController;
import android.view.animation.LayoutAnimationController;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Response;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ca.polymtl.inf3995.oronos.R;
import ca.polymtl.inf3995.oronos.services.DataDispatcher;
import ca.polymtl.inf3995.oronos.services.OronosXmlParser;
import ca.polymtl.inf3995.oronos.services.RestHttpWrapper;
import ca.polymtl.inf3995.oronos.services.SocketClient;
import ca.polymtl.inf3995.oronos.utils.GlobalParameters;
import ca.polymtl.inf3995.oronos.widgets.adapters.GridSelectorAdapter;
import ca.polymtl.inf3995.oronos.widgets.containers.AbstractWidgetContainer;
import ca.polymtl.inf3995.oronos.widgets.containers.Rocket;
import ca.polymtl.inf3995.oronos.widgets.containers.Tab;
import ca.polymtl.inf3995.oronos.widgets.views.FindMe;
import ca.polymtl.inf3995.oronos.widgets.views.OronosView;
import ca.polymtl.inf3995.oronos.widgets.views.Plot;
import timber.log.Timber;

/**
 * <h1>Main Activity</h1>
 * The Main Activity is in charge of switching between the views containing the rocket data.
 * It is also responsible of the menu view that allows such navigation.
 * <p>
 * While being created, Main Activity will open a Socket Client to receive the rocket data, and will
 * parse the xml file obtained through a REST request to the server to create the layout of the
 * views containing the rocket data.
 *
 *
 * @author  Félix Boulet, Fabrice Charbonneau, Justine Pepin, Patrick Richer St-Onge
 * @version 0.0
 * @since   2018-04-12
 */
public class MainActivity extends DrawerActivity {
    private final int MENU_VIEW_ID = -1;
    private int currentDataViewState;
    private boolean isMenuActive;
    private Timer heartbeatTimer = null;
    private long lastServerAnswer = System.nanoTime();
    private Toast warningToast = null;
    private boolean isRunning = false;

    private List<OronosView> viewsContainer;
    private RelativeLayout dataLayout;

    private RecyclerView recycler;

    /**
     * {@inheritDoc}
     * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setUpHeartbeatTask();
        setUpUtilities();
        setContentView(R.layout.activity_main);

        fillViewsContainer();
        setUpToolbar();
        // Check if filling the viewsContainer worked;
        dataLayout = findViewById(R.id.data_layout);
        if (viewsContainer != null && !viewsContainer.isEmpty()) {
            dataLayout.addView(viewsContainer.get(0), -1);
        } else {
            Timber.e("No view in viewsContainer, cannot display any data.");
        }

        // Ready to start
        currentDataViewState = 0;
        isMenuActive = false;
        Timber.v("Main Activity : Creation Done.");
        this.isRunning = true;
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if ( this.warningToast != null )
            this.warningToast.cancel();
        SocketClient.getInstance().disconnect();
        DataDispatcher.clearAllListeners();
        this.isRunning = false;
    }

    @Override
    protected void onPause() {
        super.onPause();

        this.isRunning = false;
    }

    /**
     * Utilities for the client
     */
    private void setUpUtilities() {
        SocketClient.getInstance().connect(GlobalParameters.CLIENT_ADDRESS, GlobalParameters.udpPort);

    }

    /**
     * Setup the heartbeat task if not already started.
     */
    private void setUpHeartbeatTask() {
        if ( this.heartbeatTimer != null ) {
            lastServerAnswer = System.nanoTime();
            return;
        }

        // At least one hearbeat per minute.
        long heartbeatPeriod = Math.min((long)GlobalParameters.serverTimeout * 1000 / 4, 60 * 1000);

        this.heartbeatTimer = new Timer(true);
        TimerTask heartbeatTask = new TimerTask() {
            @Override
            public void run() {
                if ( GlobalParameters.serverAddress == null || !isRunning )
                    return;

                RestHttpWrapper.getInstance().sendPostUsersHeartbeat(new Response.Listener<Void>() {
                    @Override
                    public void onResponse(Void response) {
                        lastServerAnswer = System.nanoTime();
                    }
                },null);

                long serverTimeoutNs = (long)(GlobalParameters.serverTimeout * 1.0e9);
                long timeSinceLastAnswer = System.nanoTime() - lastServerAnswer;
                boolean toastIsShown = warningToast != null && warningToast.getView() != null &&
                                       warningToast.getView().isShown();
                if ( timeSinceLastAnswer > serverTimeoutNs && !toastIsShown ) {
                    Handler mainHandler = new Handler(getApplicationContext().getMainLooper());
                    mainHandler.post(new Runnable(){
                        @Override
                        public void run() {
                            String msg = ( GlobalParameters.hasRetardedErrorMessages ) ?
                                         "henlo fren, server not know da wae" :
                                         "WARNING : Server has not answered to heartbeats for a while";
                            warningToast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG);
                            warningToast.show();
                        }
                    });
                }

            }
        };
        this.heartbeatTimer.scheduleAtFixedRate(heartbeatTask, heartbeatPeriod, heartbeatPeriod);
    }

    /**
     * This method declares the toolbar and its menu elements.
     */
    private void setUpToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ArrayList<GridSelectorAdapter.OronosViewCardContents> names = new ArrayList<>();
        if (viewsContainer != null) {
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
                names.add(new GridSelectorAdapter.OronosViewCardContents(name, subnames));
            }
        } else {
            Timber.e("View container is empty, cannot create menu properly.");
        }


        recycler = new RecyclerView(this);
        GridSelectorAdapter adapter = new GridSelectorAdapter(this, names);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recycler.setLayoutManager(staggeredGridLayoutManager);
        recycler.setAdapter(adapter);
        recycler.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT));
        recycler.setNestedScrollingEnabled(false);
    }

    /**
     * This method uses the xml parser to fill the viewsContainer (list having all views
     * to be displayed in the dataLayout).
     */
    private void fillViewsContainer() {
        OronosXmlParser parser = new OronosXmlParser(this);
        viewsContainer = new ArrayList<>();
        try {
            InputStream fis = new FileInputStream(new File(getCacheDir(), GlobalParameters.layoutName));
            Rocket rocket = parser.parse(fis);
            if (rocket != null) {
                changeToolbarTitle(rocket.getName() + " (#" + rocket.getRocketId() + ")");
                viewsContainer.addAll(rocket.getList());
            } else {
                changeToolbarTitle("NO ROCKET");
            }

        } catch (IOException e) {
            Timber.e("There was an issue while reading the XML file. Exception message :\n" +
                    e.getMessage());
        }
    }

    /**
     * Callback for inflating the menu in the toolbar. The menu used by the client can be found
     * in the res/menu/menu_main.xml file.
     *
     * @param menu Menu to inflate in the toolbar.
     * @return true
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
     * @return true for the menu to be displayed.
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case FindMe.GPS_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    // TODO: REFACTOR THIS!
                    for (FindMe findMe : FindMe.getInstances()) {
                        findMe.grantPermissions();
                    }


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    // TODO: REFACTOR THIS!
                    for (FindMe findMe : FindMe.getInstances()) {
                        findMe.showPermissionWarning();
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
                LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_scale_out);
                recycler.setLayoutAnimation(controller);
                //TODO: Fix this if able to make the fade out animation appear
                /*
                recycler.setLayoutAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        dataLayout.removeView(recycler);
                        isMenuActive = false;
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                Maybe use recycler.getItemAnimator().endAnimations()
                */
                recycler.scheduleLayoutAnimation();
                dataLayout.removeView(recycler);
                isMenuActive = false;

            } else {
                LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_scale_in);
                recycler.setLayoutAnimation(controller);
                recycler.scheduleLayoutAnimation();
                dataLayout.addView(recycler);
                isMenuActive = true;
            }
        } else {
            Slide transition = new Slide();
            TransitionManager.beginDelayedTransition(dataLayout, transition);
            if (isMenuActive) {
                dataLayout.removeView(recycler);
                isMenuActive = false;
            }
            dataLayout.removeView(viewsContainer.get(currentDataViewState));
            dataLayout.addView(viewsContainer.get(nextView));
            currentDataViewState = nextView;
        }
    }
}