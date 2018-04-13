package ca.polymtl.inf3995.oronos.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Response;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ca.polymtl.inf3995.oronos.R;
import ca.polymtl.inf3995.oronos.activities.OronosActivity;
import ca.polymtl.inf3995.oronos.services.DataDispatcher;
import ca.polymtl.inf3995.oronos.services.OronosXmlParser;
import ca.polymtl.inf3995.oronos.services.RestHttpWrapper;
import ca.polymtl.inf3995.oronos.services.SocketClient;
import ca.polymtl.inf3995.oronos.utils.GlobalParameters;
import ca.polymtl.inf3995.oronos.widgets.adapters.GridSelectorAdapter;
import ca.polymtl.inf3995.oronos.widgets.containers.AbstractWidgetContainer;
import ca.polymtl.inf3995.oronos.widgets.containers.Rocket;
import ca.polymtl.inf3995.oronos.widgets.containers.Tab;
import ca.polymtl.inf3995.oronos.widgets.views.OronosView;
import ca.polymtl.inf3995.oronos.widgets.views.Plot;
import timber.log.Timber;

public class TelemetryFragment extends Fragment {

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
    private View fragmentView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((OronosActivity) getActivity()).showToolbar();
        setHasOptionsMenu(true);
        this.isRunning = true;

        if (fragmentView != null) {
            Timber.v("Telemetry fragment rendered using saved view.");
            return fragmentView;
        }

        fragmentView = inflater.inflate(R.layout.activity_telemetry, container, false);

        fillViewsContainer();

        // Check if filling the viewsContainer worked;
        dataLayout = fragmentView.findViewById(R.id.data_layout);
        if (viewsContainer != null && !viewsContainer.isEmpty()) {
            dataLayout.addView(viewsContainer.get(0), -1);
        } else {
            Timber.e("No view in viewsContainer, cannot display any data.");
        }

        setupActionDisplayGrid();

        currentDataViewState = 0;
        isMenuActive = false;

        // Ready to start
        Timber.v("Telemetry fragment rendered using new inflated view.");

        return fragmentView;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpHeartbeatTask();
        setUpUtilities();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (this.warningToast != null)
            this.warningToast.cancel();
        SocketClient.getInstance().disconnect();
        DataDispatcher.clearAllListeners();
        this.isRunning = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPause() {
        super.onPause();

        this.isRunning = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onResume() {
        super.onResume();

        this.isRunning = true;
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
        if (this.heartbeatTimer != null) {
            lastServerAnswer = System.nanoTime();
            return;
        }

        // At least one hearbeat per minute.
        long heartbeatPeriod = Math.min((long) GlobalParameters.serverTimeout * 1000 / 4, 60 * 1000);

        this.heartbeatTimer = new Timer(true);
        TimerTask heartbeatTask = new TimerTask() {
            @Override
            public void run() {
                if (GlobalParameters.serverAddress == null)
                    return;

                RestHttpWrapper.getInstance().sendPostUsersHeartbeat(new Response.Listener<Void>() {
                    @Override
                    public void onResponse(Void response) {
                        lastServerAnswer = System.nanoTime();
                    }
                }, null);

                // We send the request if app is in background, but we do not check for timeouts
                if (!isRunning)
                    return;

                long serverTimeoutNs = (long) (GlobalParameters.serverTimeout * 1.0e9);
                long timeSinceLastAnswer = System.nanoTime() - lastServerAnswer;
                boolean toastIsShown = warningToast != null && warningToast.getView() != null &&
                        warningToast.getView().isShown();
                if (timeSinceLastAnswer > serverTimeoutNs && !toastIsShown) {
                    Handler mainHandler = new Handler(getActivity().getApplicationContext().getMainLooper());
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            String msg = (GlobalParameters.hasRetardedErrorMessages) ?
                                    "henlo fren, server not know da wae" :
                                    "WARNING : Server has not answered to heartbeats for a while";
                            warningToast = Toast.makeText(getActivity().getApplicationContext(), msg, Toast.LENGTH_LONG);
                            warningToast.show();
                        }
                    });
                }

            }
        };
        this.heartbeatTimer.scheduleAtFixedRate(heartbeatTask, heartbeatPeriod, heartbeatPeriod);
    }

    private void setupActionDisplayGrid() {
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

        recycler = new RecyclerView(getActivity());
        recycler.setBackgroundColor(Color.BLACK);
        recycler.getBackground().setAlpha(128);
        GridSelectorAdapter adapter = new GridSelectorAdapter(getActivity(), names, this);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recycler.setLayoutManager(staggeredGridLayoutManager);
        recycler.setAdapter(adapter);
        recycler.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT));
        recycler.setNestedScrollingEnabled(false);
        recycler.setItemAnimator(null);
        recycler.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                if (e.getAction() != MotionEvent.ACTION_UP) {
                    return false;
                }
                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if (child != null) {
                    return false;
                } else {
                    changeStateOfDataLayout(MENU_VIEW_ID);
                    return true;
                }
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }

    /**
     * This method uses the xml parser to fill the viewsContainer (list having all views
     * to be displayed in the dataLayout).
     */
    private void fillViewsContainer() {
        OronosXmlParser parser = new OronosXmlParser(getActivity());
        viewsContainer = new ArrayList<>();
        try {
            InputStream fis = new FileInputStream(new File(getActivity().getCacheDir(), GlobalParameters.layoutName));
            Rocket rocket = parser.parse(fis);
            if (rocket != null) {
                ((OronosActivity) getActivity()).changeToolbarTitle(rocket.getName() + " (#" + rocket.getRocketId() + ")");
                viewsContainer.addAll(rocket.getList());
            } else {
                ((OronosActivity) getActivity()).changeToolbarTitle("NO ROCKET");
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
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
     * State machine for the dataLayout.
     *
     * @param nextView The int representing the nextView the client will display in its
     *                 dataLayout.
     */
    public void changeStateOfDataLayout(int nextView) {
        if (nextView == MENU_VIEW_ID) {
            Fade transition = new Fade();
            TransitionManager.beginDelayedTransition(dataLayout, transition);
            if (isMenuActive) {
                LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.layout_scale_out);
                recycler.setLayoutAnimation(controller);
                recycler.scheduleLayoutAnimation();
                dataLayout.removeView(recycler);
                isMenuActive = false;

            } else {
                LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.layout_scale_in);
                recycler.setLayoutAnimation(controller);
                recycler.scheduleLayoutAnimation();
                if (recycler.getParent() != null) {
                    ((ViewGroup) recycler.getParent()).removeView(recycler);
                }
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
