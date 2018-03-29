package ca.polymtl.inf3995.oronos.widgets.views;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ca.polymtl.inf3995.oronos.services.BroadcastMessage;
import ca.polymtl.inf3995.oronos.utils.GlobalParameters;
import ca.polymtl.inf3995.oronos.utils.PermissionsUtil;
import timber.log.Timber;

/**
 * FindMe class for the FindMe tag.
 */
public class FindMe extends OronosView implements SensorEventListener, LocationListener {

    //TODO: Refactor this horrible pattern
    private static List<FindMe> instances = new ArrayList<>();

    public static final int GPS_PERMISSION = 1;
    private final SensorManager sensorManager;
    private final Sensor accelerometer;
    private final Sensor magnetometer;
    private final int LOCATION_REFRESH_TIME = 1000; // 1 second refresh time
    private final float LOCATION_REFRESH_DISTANCE = 0.0f; // 0 meter refresh distance
    private final int SENSOR_REFRESH_TIME = 500000; // 0.5 second
    private final int SENSOR_UPDATE_TIME = 200; // 200 milliseconds
    private final float[] accelerometerReading = new float[3];
    private final float[] magnetometerReading = new float[3];
    private final float[] rotationMatrix = new float[16];
    private final CoordinatorLayout coordinator;
    private final LinearLayout content;
    private final WebView threeView;
    private final TextView statusText;
    private final float[] unitDistance = {0, -1, 0};
    private Timer sensorUpdater;
    private Timer locationUpdater;
    private LocationManager locationManager;
    private Snackbar warningBar;

    private long lastLocationTime = 0;
    private Location rocketLocation = new Location("");
    private Location deviceLocation = new Location("");
    private BroadcastReceiver locationReceiver;

    /**
     * FindMe widget constructor. Initializes fields then calls the view builder method.
     *
     * @param context The application context.
     */
    public FindMe(Context context) {
        super(context);
        instances.add(this);
        coordinator = new CoordinatorLayout(getContext());
        content = new LinearLayout(getContext());
        threeView = new WebView(getContext());
        statusText = new TextView(getContext());
        sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        buildView();
    }

    public static List<FindMe> getInstances() {
        return instances;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        instances.remove(this);
    }

    /**
     * View builder method. This method sets layout parameters for the views contained in the widget,
     * including the various containers, the WebView for displaying the arrow and the coordinator
     * for the snackbar.
     */
    @SuppressLint({"SetJavaScriptEnabled", "ClickableViewAccessibility"})
    private void buildView() {
        setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        content.setOrientation(LinearLayout.VERTICAL);
        content.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        statusText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        statusText.setTextSize(12);
        statusText.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        int textPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
        statusText.setPadding(textPadding, textPadding, textPadding, textPadding);
        content.addView(statusText);

        threeView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        threeView.getSettings().setJavaScriptEnabled(true);
        threeView.addJavascriptInterface(this, "android");
        threeView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return (motionEvent.getAction() == MotionEvent.ACTION_MOVE);
            }
        });
        content.addView(threeView);

        coordinator.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        coordinator.addView(content);
        addView(coordinator);
    }

    /**
     * This method loads the Javascript Three.js renderer, which starts itself, into the WebView.
     * It should only be called when the FindMe view is visible (onAttachedToWindow) to save
     * on CPU/GPU resources.
     */
    private void setupWebGLRenderer() {
        threeView.loadUrl("file:///android_asset/html/findme_renderer.html");
    }

    /**
     * This method loads a blank page into the WebView to save on resources. Should be called
     * when first building the view and when the FindMe view is invisible (onDetachedFromWindow).
     */
    private void flushWebGLRenderer() {
        threeView.loadUrl("about:blank");
    }

    /**
     * This method is called when the FindMe widget becomes visible on screen (usually when the user
     * selects a container page (TabContainer, DualWidget, ...) that has a FindMe widget in it.
     * It checks if the app has location permissions, which are needed for the widget to work.
     * If not, a persistent snackbar is made to inform the user that they need to enable them.
     * When pressed, the button on the snackbar will open a permission dialog to ask for permissions.
     * The listener for this action is implemented in MainActivity.
     * Otherwise, if the app has permissions, grantPermissions is called.
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        flushWebGLRenderer();
        if (!PermissionsUtil.hasPermissions(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            warningBar = Snackbar.make(coordinator, "GPS Permissions are required for using this tag. Swipe to dismiss.", Snackbar.LENGTH_INDEFINITE);
            warningBar.setAction("ENABLE", new OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, GPS_PERMISSION);
                }
            }).show();
        } else {
            grantPermissions();
        }
    }

    /**
     * This method is called when the FindMe widget gets off screen (app is closed, displayed
     * view container changes, ...). It stops sensor related updates, location updates, and
     * removes the 3D renderer from the view.
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopSensorTask();
        unregisterSensors();
        stopLocationUpdates();
        flushWebGLRenderer();
    }

    private void startLocationTask() {
        locationUpdater = new Timer(true);
        TimerTask locationTask = new TimerTask() {
            @Override
            public void run() {
                ((Activity) getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateLocation();
                    }
                });
            }
        };
        locationUpdater.scheduleAtFixedRate(locationTask, 0, LOCATION_REFRESH_TIME);
    }

    /**
     * This method stops the task started by the method above.
     */
    private void stopLocationTask() {
        if (locationUpdater != null) {
            locationUpdater.cancel();
            locationUpdater.purge();
        }
    }

    /**
     * This method starts a fixed-rate task as a daemon (meaning it will be killed if the app is
     * closed) to update sensor data at a specific frequency (every 200 milliseconds).
     */
    private void startSensorTask() {
        sensorUpdater = new Timer(true);
        TimerTask sensorTask = new TimerTask() {
            @Override
            public void run() {
                ((Activity) getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateOrientationAngles();
                    }
                });

            }
        };
        sensorUpdater.scheduleAtFixedRate(sensorTask, 0, SENSOR_UPDATE_TIME);
    }

    /**
     * This method stops the task started by the method above.
     */
    private void stopSensorTask() {
        if (sensorUpdater != null) {
            sensorUpdater.cancel();
            sensorUpdater.purge();
        }
    }

    /**
     * This method enables sensor and location updates once permissions are granted.
     * It creates a locationManager using the appropriate system service, then registers sensors
     * with the FindMe itself as a listener. It also sets up the 3D renderer.
     * This method can be called from the MainActivity in the permission listener
     * 'onRequestPermissionsResult'.
     */
    public void grantPermissions() {
        if (locationManager == null) {
            locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        }
        if (locationManager != null) {
            enableLocationUpdates();
            registerSensors();
            startSensorTask();
            setupWebGLRenderer();
        }
        if (warningBar != null && warningBar.isShown()) {
            warningBar.dismiss();
        }
    }

    /**
     * This method is called from the MainActivity listener to display the warning snackbar
     * if permissions are not enabled.
     */
    public void showPermissionWarning() {
        warningBar.show();
    }

    /**
     * This method registers FindMe as the listener for the accelerometer and magnetometer sensors.
     */
    private void registerSensors() {
        sensorManager.registerListener(this, accelerometer, SENSOR_REFRESH_TIME);
        sensorManager.registerListener(this, magnetometer, SENSOR_REFRESH_TIME);
    }

    /**
     * This method unregisters FindMe as the listener for the sensors.
     */
    private void unregisterSensors() {
        sensorManager.unregisterListener(this);
    }

    /**
     * This method enables location updates if a location manager is available.
     * It requests updates from both the GPS and network, which will be handled by the
     * onLocationChanged listener.
     * It also tries getting the last known location of the device, if available.
     */
    private void enableLocationUpdates() {
        if (locationManager != null) {
            try {
                Location lastLocation;
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME, LOCATION_REFRESH_DISTANCE, this);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_REFRESH_TIME, LOCATION_REFRESH_DISTANCE, this);
                if ((lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)) == null) {
                    lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }

                if (lastLocation != null) {
                    this.onLocationChanged(lastLocation);
                    updateLocation();
                }

                locationReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        BroadcastMessage msg = Parcels.unwrap(intent.getParcelableExtra("data"));
                        switch (msg.getCanSid()) {
                            case "GPS1_LATITUDE":
                                rocketLocation.setLatitude(msg.getData1().doubleValue());
                                break;
                            case "GPS1_LONGITUDE":
                                rocketLocation.setLongitude(msg.getData1().doubleValue());
                                break;
                            case "GPS1_ALT_MSL":
                                rocketLocation.setAltitude(msg.getData1().doubleValue());
                                break;
                        }
                    }
                };

                enableLocationReceiver();
                startLocationTask();

            } catch (SecurityException e) {
                Timber.e("FindMe: Error accessing location permissions.");
            }
        }
    }

    /**
     * This method removes FindMe as the listener for location updates, effectively disabling them.
     */
    private void stopLocationUpdates() {
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
        if (locationReceiver != null) {
            disableLocationReceiver();
        }
        stopLocationTask();
    }

    private void enableLocationReceiver() {

        if (GlobalParameters.canModuleTypes != null) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("GPS1_LATITUDE");
            intentFilter.addAction("GPS1_LONGITUDE");
            intentFilter.addAction("GPS1_ALT_MSL");

            // Listen for all categories, since it depends on the rocket

            for (String key : GlobalParameters.canModuleTypes.keySet()) {
                intentFilter.addCategory(key);
            }

            for (int i = 0; i < 16; i++) {
                intentFilter.addCategory(String.format("%d", i));
            }

            LocalBroadcastManager.getInstance(getContext()).registerReceiver(locationReceiver, intentFilter);
        } else {
            final Handler handler = new Handler();

            // Retry enabling updates
            handler.postDelayed(new Runnable() {
                public void run() {
                    enableLocationReceiver();
                }
            }, 1000);
        }
    }

    private void disableLocationReceiver() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(locationReceiver);
        locationReceiver = null;
    }

    /**
     * This method updates the device's rotation matrix from sensor data.
     */
    private void updateOrientationAngles() {
        SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerReading, magnetometerReading);
    }

    /**
     * This method updates the location unit vector from the device's location to the rocket's location.
     */
    @SuppressLint("DefaultLocale")
    private void updateLocation() {
        float[] xDist = new float[1];
        float[] yDist = new float[1];
        float zDist;
        float totalDist = deviceLocation.distanceTo(rocketLocation);
        String deviceInfo = !deviceLocation.getProvider().equals("") ? String.format("Device - Lat : %.6f, Long : %.6f, Alt : %.2f m, Provider : %s",
                deviceLocation.getLatitude(), deviceLocation.getLongitude(), deviceLocation.getAltitude(), deviceLocation.getProvider()) : "Device - NO LOCATION DATA AVAILABLE";
        String rocketInfo = String.format("Rocket - Lat : %.6f, Long : %.6f, Alt : %.2f m",
                rocketLocation.getLatitude(), rocketLocation.getLongitude(), rocketLocation.getAltitude());
        if (totalDist > 1000.0) {
            statusText.setText(String.format("Distance : %.2f km\n%s\n%s", totalDist / 1000.0, deviceInfo, rocketInfo));
        } else {
            statusText.setText(String.format("Distance : %.1f m\n%s\n%s", totalDist, deviceInfo, rocketInfo));
        }

        Location.distanceBetween(deviceLocation.getLatitude(),
                deviceLocation.getLongitude(), rocketLocation.getLatitude(), deviceLocation.getLongitude(), xDist);
        Location.distanceBetween(deviceLocation.getLatitude(),
                deviceLocation.getLongitude(), deviceLocation.getLatitude(), rocketLocation.getLongitude(), yDist);
        zDist = (float) (rocketLocation.getAltitude() - deviceLocation.getAltitude());
        unitDistance[0] = xDist[0] / totalDist;
        unitDistance[1] = yDist[0] / totalDist;
        unitDistance[2] = zDist / totalDist;
    }

    /**
     * This method is called when a sensor (accelerometer or magnetometer) has acquired new data.
     * It places the values from the event into arrays for later processing.
     *
     * @param sensorEvent The sensor event emitted by the sensor.
     */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.equals(accelerometer)) {
            System.arraycopy(sensorEvent.values, 0, accelerometerReading, 0, accelerometerReading.length);
        }
        if (sensorEvent.sensor.equals(magnetometer)) {
            System.arraycopy(sensorEvent.values, 0, magnetometerReading, 0, magnetometerReading.length);
        }
    }

    /**
     * Unused method that would be called when sensor accuracy has changed.
     *
     * @param sensor The affected sensor.
     * @param i      The new accuracy of the sensor.
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    /**
     * This method is called when the device's location has changed.
     * It tries to evaluate the accuracy of the new location, then calls updateLocation
     * if it is deemed sufficient. Accuracy criterion are the following (one or more must be true) :
     * - The location comes from the GPS.
     * - The location comes from the network, it has been more than 10 seconds since the last update,
     * and the accuracy is better than two times the last location's accuracy (i.e. if the last
     * location's accuracy was 20 meters, it would need to be 40 meters or less).
     * - The location has not been updated in 60 seconds or more.
     *
     * @param location The new location provided by the GPS or network.
     */
    @Override
    public void onLocationChanged(Location location) {
        // Try to find better criteria for evaluating accuracy
        deviceLocation = location;
        lastLocationTime = System.currentTimeMillis();
    }

    /**
     * Unused method that would be called if a provider (GPS or network) becomes unavailable or
     * erratic. Since we already have a listener for the two providers, this has no real use.
     *
     * @param provider The affected provider.
     * @param status   The status of the provider.
     * @param extras   Extra data.
     */
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    /**
     * Unused method that would be called when a provider is enabled.
     *
     * @param provider The provider in question.
     */
    @Override
    public void onProviderEnabled(String provider) {

    }

    /**
     * Unused method that would be called when a provider is disabled.
     *
     * @param provider The provider in question.
     */
    @Override
    public void onProviderDisabled(String provider) {

    }

    /**
     * This method is used by the WebView (threeView) to get the location unit vector for rendering.
     * It is only called from Javascript.
     *
     * @param i The index of the wanted element in the unitDistance array.
     * @return The element in question.
     */
    @JavascriptInterface
    public float getUnitVectorElement(int i) {
        return this.unitDistance[i];
    }

    /**
     * This method is used by the WebView (threeView) to get the rotation matrix (4x4) for rendering.
     * It is only called from Javascript.
     *
     * @param i The index of the wanted element in the rotationMatrix array.
     * @return The element in question.
     */
    @JavascriptInterface
    public float getRotationMatrixElement(int i) {
        return rotationMatrix[i];
    }

}
