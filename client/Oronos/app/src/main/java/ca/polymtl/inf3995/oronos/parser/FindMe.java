package ca.polymtl.inf3995.oronos.parser;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import ca.polymtl.inf3995.oronos.PermissionsUtil;
import ca.polymtl.inf3995.oronos.R;
import timber.log.Timber;

/**
 * Created by Felix on 15/févr./2018.
 */

public class FindMe extends LinearLayout implements ContainableWidget {

    private final int LOCATION_REFRESH_TIME = 1000; // 1 second refresh time
    private final float LOCATION_REFRESH_DISTANCE = 1.0f; // 1 meter refresh distance
    public static final int GPS_PERMISSION = 1;

    private LocationManager locationManager;

    private TextView status;
    private Button permsButton;

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {


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



    public FindMe(Context context) {
        super(context);
        buildView();

    }

    private void buildView() {
        setOrientation(LinearLayout.VERTICAL);
        setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        status = new TextView(getContext());
        addView(status);
        if (!PermissionsUtil.hasPermissions(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION)) {
            status.setText("GPS Permissions are required for using this tag. Press the button to enable this widget once permissions are granted.\n" +
                    "Pressing the button when permissions are not granted will open the dialog to allow that.");
            permsButton = new Button(getContext());
            permsButton.setText("ENABLE");
            permsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, GPS_PERMISSION);
                }
            });
            addView(permsButton);

        } else {
            grantPermissions();
        }
    }

    public void grantPermissions() {
        removeViewInLayout(permsButton);
        status.setText("PERMS GRANTED");
        setupLocationManager();
    }

    public void showPermissionWarning() {
        Snackbar.make(this, "Location permissions are required for this widget to function properly.", Snackbar.LENGTH_LONG).show();
    }

    private void setupLocationManager() {
        if (locationManager == null) {
            locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
            if (locationManager != null) {
                try {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME, LOCATION_REFRESH_DISTANCE, locationListener);
                } catch (SecurityException e) {
                    Timber.e("FindMe: Error accessing location permissions.");
                    buildView();
                }
            } else {
                Timber.e("FindMe: Error accessing location permissions.");
                buildView();
            }
        }
    }

    @Override
    public View getView() {
        return null;
    }
}
