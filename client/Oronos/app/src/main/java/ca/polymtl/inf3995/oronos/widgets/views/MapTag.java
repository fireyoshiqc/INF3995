package ca.polymtl.inf3995.oronos.widgets.views;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.parceler.Parcels;

import ca.polymtl.inf3995.oronos.services.BroadcastMessage;
import ca.polymtl.inf3995.oronos.utils.GlobalParameters;

/**
 * Created by Felix on 15/févr./2018.
 */

public class MapTag extends OronosView {

    private MapView mapView;
    private GeoPoint rocketLocation;

    public MapTag(Context context) {
        super(context);

        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));

        mapView = new MapView(context);
        rocketLocation = new GeoPoint(0.0, 0.0, 0.0);

        setupMapView();
        addView(mapView);
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
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        unregister();
    }

    private void register() {
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
                intentFilter.addCategory(String.valueOf(i));
            }

            LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver, intentFilter);
        }
    }

    private void unregister() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(broadcastReceiver);
    }

    private void setupMapView() {
        mapView.setUseDataConnection(false);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        mapView.setMaxZoomLevel(15.0);
        mapView.setMinZoomLevel(1.0);


        ITileSource iTileSource = new XYTileSource("map", 0, 15, 256, ".jpg", new String[]{"empty"});
        mapView.setTileSource(iTileSource);

        IMapController mapController = mapView.getController();
        mapController.setZoom(13.0);
        GeoPoint startPoint = new GeoPoint(32.9401475, -106.9193209);
        mapController.setCenter(startPoint);


        GeoPoint spaceport_america = new GeoPoint(32.9401475, -106.9193209);
        GeoPoint motel_6 = new GeoPoint(32.3417429, -106.7628682);
        GeoPoint convention_center = new GeoPoint(32.2799304, -106.7468314);
        GeoPoint st_pie_de_guire = new GeoPoint(46.0035479, -72.7311097);

        Marker serverMarker = new Marker(mapView);
        serverMarker.setPosition(startPoint);
        serverMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mapView.getOverlays().add(serverMarker);

    }


}
