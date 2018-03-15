package ca.polymtl.inf3995.oronos.parser;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import ca.polymtl.inf3995.oronos.PermissionsUtil;
import timber.log.Timber;

/**
 * Created by Felix on 15/févr./2018.
 */

public class FindMe extends OronosView implements SensorEventListener {

    public static final int GPS_PERMISSION = 1;
    private final SensorManager sensorManager;
    private final Sensor accelerometer;
    private final Sensor magnetometer;
    private final int LOCATION_REFRESH_TIME = 1000; // 1 second refresh time
    private final float LOCATION_REFRESH_DISTANCE = 0.0f; // 0 meter refresh distance
    private final int SENSOR_REFRESH_TIME = 500000; // 0.5 second
    private final LocationListener locationListener;
    private final float[] accelerometerReading = new float[3];
    private final float[] magnetometerReading = new float[3];
    private final float[] rotationMatrix = new float[9];
    private final float[] orientationAngles = new float[3];
    private final CoordinatorLayout coordinator;
    private final LinearLayout content;
    private final WebView threeView;
    private final float[] unitDistance = {1, 0, 0};
    private final float[] arrowVector = {0, 0, 0};
    private Timer sensorUpdater;
    private LocationManager locationManager;
    private TextView locationText;
    private TextView sensorText;
    private Snackbar warningBar;


    public FindMe(Context context) {
        super(context);
        coordinator = new CoordinatorLayout(getContext());
        content = new LinearLayout(getContext());
        threeView = new WebView(getContext());
        threeView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        threeView.getSettings().setJavaScriptEnabled(true);
        threeView.addJavascriptInterface(this, "android");
        sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        //TODO: Error checking for sensors
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Location fakeLocation = new Location(location);
                fakeLocation.setLatitude(45.504422);
                fakeLocation.setLongitude(-73.612883);
                fakeLocation.setAltitude(0.0);
                float[] xDist = new float[1];
                float[] yDist = new float[1];
                float zDist;
                Location.distanceBetween(location.getLatitude(),
                        location.getLongitude(), fakeLocation.getLatitude(), location.getLongitude(), xDist);
                Location.distanceBetween(location.getLatitude(),
                        location.getLongitude(), location.getLatitude(), fakeLocation.getLongitude(), yDist);
                zDist = (float) (fakeLocation.getAltitude() - location.getAltitude());
                locationText.setText("Latitude: " + location.getLatitude() + "\n" +
                        "Longitude: " + location.getLongitude() + "\n" +
                        "Altitude: " + location.getAltitude() + "\n" +
                        "Accuracy: " + location.getAccuracy() + "\n" +
                        "DistanceFromPoly (test): " + location.distanceTo(fakeLocation) + "\n" +
                        "XFromPoly (test):" + xDist[0] + "\n" +
                        "YFromPoly (test):" + yDist[0] + "\n" +
                        "ZFromPoly (test):" + zDist);
                unitDistance[0] = (float) (xDist[0] / (Math.sqrt(Math.pow(xDist[0], 2) + Math.pow(yDist[0], 2) + Math.pow(zDist, 2))));
                unitDistance[1] = (float) (yDist[0] / (Math.sqrt(Math.pow(xDist[0], 2) + Math.pow(yDist[0], 2) + Math.pow(zDist, 2))));
                unitDistance[2] = (float) (zDist / (Math.sqrt(Math.pow(xDist[0], 2) + Math.pow(yDist[0], 2) + Math.pow(zDist, 2))));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        buildView();
    }

    private void buildView() {
        setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        content.setOrientation(LinearLayout.VERTICAL);
        content.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        locationText = new TextView(getContext());
        content.addView(locationText);
        sensorText = new TextView(getContext());
        content.addView(sensorText);
        content.addView(threeView);
        coordinator.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        coordinator.addView(content);
        addView(coordinator);
    }

    private void setupWebGLRenderer() {
        threeView.loadUrl("file:///android_asset/html/findme_renderer.html");
    }

    private void flushWebGLRenderer() {
        threeView.loadUrl("about:blank");
    }

    // Called when the FindMe widget comes on screen, should enable sensors
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        flushWebGLRenderer();
        if (!PermissionsUtil.hasPermissions(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            warningBar = Snackbar.make(coordinator, "GPS Permissions are required for using this tag.", Snackbar.LENGTH_INDEFINITE);
            warningBar.setAction("ENABLE", new OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, GPS_PERMISSION);
                }
            }).show();
        } else {
            grantPermissions(true);
        }

        if (PermissionsUtil.hasPermissions(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            enableLocationUpdates();
            setupWebGLRenderer();
        }
        registerSensors();
        startSensorTask();
    }

    // Called when the FindMe widget goes off screen, should disable sensors
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopSensorTask();
        stopLocationUpdates();
        unregisterSensors();
        flushWebGLRenderer();
    }

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
        sensorUpdater.scheduleAtFixedRate(sensorTask, 0, 150);
    }

    private void stopSensorTask() {
        sensorUpdater.cancel();
        sensorUpdater.purge();
    }

    public void grantPermissions(boolean calledFromInstance) {
        if (locationManager == null) {
            locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        }
        if (locationManager != null && !calledFromInstance) {
            enableLocationUpdates();
        }
        if (warningBar != null && warningBar.isShown()) {
            warningBar.dismiss();
        }
    }

    public void showPermissionWarning() {
        warningBar.show();
    }

    private void registerSensors() {
        sensorManager.registerListener(this, accelerometer, SENSOR_REFRESH_TIME);
        sensorManager.registerListener(this, magnetometer, SENSOR_REFRESH_TIME);
    }

    private void unregisterSensors() {
        sensorManager.unregisterListener(this);
    }

    private void enableLocationUpdates() {
        if (locationManager != null) {
            try {
                //Criteria providerCriteria = new Criteria();
                //providerCriteria.setAccuracy(Criteria.ACCURACY_COARSE);
                //String bestProvider = locationManager.getBestProvider(providerCriteria, true);
                //Timber.v("FindMe: Provider chosen for location : %s", bestProvider);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME, LOCATION_REFRESH_DISTANCE, locationListener);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_REFRESH_TIME, LOCATION_REFRESH_DISTANCE, locationListener);
                Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastLocation != null) {
                    locationListener.onLocationChanged(lastLocation);
                } else {
                    lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (lastLocation != null) {
                        locationListener.onLocationChanged(lastLocation);
                    }
                }
            } catch (SecurityException e) {
                Timber.e("FindMe: Error accessing location permissions.");
                buildView();
            }
        }
    }

    private void stopLocationUpdates() {
        if (locationManager != null && locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

    // https://developer.android.com/guide/topics/sensors/sensors_position.html
    private void updateOrientationAngles() {
        SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerReading, magnetometerReading);
        SensorManager.getOrientation(rotationMatrix, orientationAngles);
        arrowVector[0] = 0;//orientationAngles[1] - (float)Math.PI/2 + (float) Math.atan(unitDistance[2]/unitDistance[1]);
        arrowVector[1] = 0;//(float) Math.atan(unitDistance[0]/unitDistance[2]);
        arrowVector[2] = ((orientationAngles[0]+(float) Math.atan(unitDistance[1]/unitDistance[0]))+ (float)(2*Math.PI)) % (float)(2*Math.PI);//-orientationAngles[2] + (float) Math.atan(unitDistance[1]/unitDistance[0]);

        sensorText.setText("Sensors: x-" + orientationAngles[0] + " y-" + orientationAngles[1] + " z-" + orientationAngles[2]);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.equals(accelerometer)) {
            System.arraycopy(sensorEvent.values, 0, accelerometerReading, 0, accelerometerReading.length);
        }
        if (sensorEvent.sensor.equals(magnetometer)) {
            System.arraycopy(sensorEvent.values, 0, magnetometerReading, 0, magnetometerReading.length);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @JavascriptInterface
    public float getArrowVectorX() {
        return this.arrowVector[0];
    }

    @JavascriptInterface
    public float getArrowVectorY() {
        return this.arrowVector[1];
    }

    @JavascriptInterface
    public float getArrowVectorZ() {
        return this.arrowVector[2];
    }

    @JavascriptInterface
    public float[] getRotationMatrix() {
        return this.rotationMatrix;
    }
}
