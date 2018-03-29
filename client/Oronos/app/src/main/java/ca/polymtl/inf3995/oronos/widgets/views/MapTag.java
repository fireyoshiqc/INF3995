package ca.polymtl.inf3995.oronos.widgets.views;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import ca.polymtl.inf3995.oronos.R;
import ca.polymtl.inf3995.oronos.services.BroadcastMessage;
import ca.polymtl.inf3995.oronos.utils.GlobalParameters;
import timber.log.Timber;

/**
 * Created by Felix on 15/févr./2018.
 */

public class MapTag extends OronosView {

    private final int REFRESH_DELAY = 1000; // milliseconds

    private MapView mapView;
    private Handler handler;

    private GeoPoint rocketLocation;
    private Marker rocketMarker;

    private GeoPoint serverLocation;

    public MapTag(Context context) {
        super(context);

        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));

        handler = new Handler();
        mapView = new MapView(context);

    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
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

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        register();
        handler.postDelayed(run, REFRESH_DELAY);
        mapView = new MapView(getContext());
        setupMapView();
        setupRocketMarker();
        addView(mapView);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        handler.removeCallbacks(run);
        unregister();
        removeView(mapView);
    }

    private void register() {
        if (GlobalParameters.canModuleTypes == null) {
            return;
        }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("GPS1_LATITUDE");
        intentFilter.addAction("GPS1_LONGITUDE");
        intentFilter.addAction("GPS1_ALT_MSL");

        // Listen for all categories, since it depends on the rocket

        for (String key : GlobalParameters.canModuleTypes.keySet()) {
            intentFilter.addCategory(key);
        }

        for (int i = 0; i < 16; i++) {
            intentFilter.addCategory(String.valueOf(i));
        }

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver, intentFilter);

    }

    private void unregister() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(broadcastReceiver);
    }

    private void setupMapView() {
        mapView.setUseDataConnection(false);
        mapView.setBuiltInZoomControls(false);
        mapView.setMultiTouchControls(true);
        //mapView.setMaxZoomLevel(15.0);
        mapView.setMinZoomLevel(1.0);

        if (GlobalParameters.mapName == null) {
            return;
        }

        ITileSource iTileSource;

        Timber.v(GlobalParameters.mapName);

        switch (GlobalParameters.mapName) {
            case "spaceport_america":
            case "motel_6":
            case "convention_center":
                iTileSource = new XYTileSource("map/usa", 0, 15, 256, ".jpg", null);
                break;
            case "st_pie_de_guire":
                iTileSource = new XYTileSource("map/canada", 2, 18, 256, ".png", null);
                break;
            default:
                // Default to USA
                iTileSource = new XYTileSource("map/usa", 0, 15, 256, ".jpg", null);
                Snackbar.make(getRootView(), "Unknown map. Default to Spaceport America.", Snackbar.LENGTH_LONG).show();
        }

        mapView.setTileSource(iTileSource);

        IMapController mapController = mapView.getController();

        Marker serverMarker = new Marker(mapView);
        serverMarker.setTitle("Server");
        serverMarker.setIcon(getResources().getDrawable(R.drawable.ic_home_black_24dp));

        switch (GlobalParameters.mapName) {
            case "spaceport_america":
                serverLocation = new GeoPoint(32.9401475, -106.9193209);
                break;
            case "motel_6":
                serverLocation = new GeoPoint(32.3417429, -106.7628682);
                break;
            case "convention_center":
                serverLocation = new GeoPoint(32.2799304, -106.7468314);
                break;
            case "st_pie_de_guire":
                serverLocation = new GeoPoint(46.0035479, -72.7311097);
                break;
            default:
                // Default to Spaceport America
                serverLocation = new GeoPoint(32.9401475, -106.9193209);
        }

        serverMarker.setPosition(serverLocation);
        serverMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mapView.getOverlays().add(serverMarker);

        mapController.setZoom(15.0);
        mapController.setCenter(serverLocation);

    }

    private Runnable run = new Runnable() {
        @Override
        public void run() {
            updateMarker();
            handler.postDelayed(this, REFRESH_DELAY);
        }
    };

    private void updateMarker() {
        rocketMarker.setPosition(rocketLocation);

        List<GeoPoint> geoPointList = new ArrayList<>();
        geoPointList.add(serverLocation);
        geoPointList.add(rocketLocation);
        BoundingBox bbMarkers = BoundingBox.fromGeoPoints(geoPointList);

        mapView.zoomToBoundingBox(bbMarkers, true, 60);

        mapView.invalidate();
    }

    private void setupRocketMarker() {
        rocketLocation = new GeoPoint(0.0, 0.0, 0.0);
        rocketMarker = new Marker(mapView);
        rocketMarker.setPosition(rocketLocation);
        rocketMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        rocketMarker.setTitle("Rocket");
        rocketMarker.setIcon(getResources().getDrawable(R.drawable.ic_adjust_black_24dp));
        mapView.getOverlays().add(rocketMarker);
    }

}
