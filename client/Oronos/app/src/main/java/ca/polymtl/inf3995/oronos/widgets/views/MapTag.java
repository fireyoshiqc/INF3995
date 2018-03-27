package ca.polymtl.inf3995.oronos.widgets.views;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
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

//        setupRocketMarker();
//        setupMapView();
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
        mapView.setMultiTouchControls(false);
        mapView.setBuiltInZoomControls(false);
        mapView.setMaxZoomLevel(15.0);
        mapView.setMinZoomLevel(0.0);

        if (GlobalParameters.mapName == null) {
            return;
        }

        ITileSource iTileSource;

        switch (GlobalParameters.mapName) {
            case "spaceport_america":
            case "motel_6":
            case "convention_center":
                iTileSource = new XYTileSource("map/usa", 0, 15, 256, ".jpg", null);
                break;
            case "st_pie_de_guide":
                iTileSource = new XYTileSource("map/canada", 2, 18, 256, ".png", null);
                break;
            default:
                iTileSource = new XYTileSource("map/usa", 0, 15, 256, ".jpg", null);
        }

        mapView.setTileSource(iTileSource);

        IMapController mapController = mapView.getController();

        serverLocation = new GeoPoint(0.0, 0.0);
        Marker serverMarker = new Marker(mapView);
        serverMarker.setTitle("Server");

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
            case "st_pie_de_guide":
                serverLocation = new GeoPoint(46.0035479, -72.7311097);
                break;
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

//        BoundingBox boundingBox = mapView.getBoundingBox();
//        boolean isRocketVisible = boundingBox.contains(rocketLocation);
//        boolean isServerVisible = boundingBox.contains(serverLocation);
//
//        if (!isRocketVisible || !isServerVisible) {
//            mapView.getController().zoomOut();
//        } else {
//            List<GeoPoint> geoPointList = new ArrayList<>();
//            geoPointList.add(serverLocation);
//            geoPointList.add(rocketLocation);
//            BoundingBox bb = BoundingBox.fromGeoPoints(geoPointList);
//        }


//        double latitudeSpan = Math.round(Math.abs(serverLocation.getLatitude() - rocketLocation.getLatitude()));
//        double longitudeSpan = Math.round(Math.abs(rocketLocation.getLongitude() - rocketLocation.getLongitude()));
//        double currentLatitudeSpan = mapView.getLatitudeSpanDouble();
//        double currentLongitudeSpan = mapView.getLongitudeSpanDouble();
//        double ratio = currentLongitudeSpan / currentLatitudeSpan;
//        mapView.getController().zoomToSpan(latitudeSpan * 2, longitudeSpan * 2);


        zoomSpan(rocketLocation.getLatitude(), serverLocation.getLatitude(), rocketLocation.getLongitude(), serverLocation.getLongitude());

        mapView.invalidate();
    }

    private void setupRocketMarker() {
        rocketLocation = new GeoPoint(0.0, 0.0, 0.0);
        rocketMarker = new Marker(mapView);
        rocketMarker.setPosition(rocketLocation);
        rocketMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        rocketMarker.setTitle("Rocket");
        mapView.getOverlays().add(rocketMarker);
    }

    private boolean isMarkerVisible(Marker marker) {
        Rect currentMapBoundsRect = new Rect();
        Point currentDevicePosition = new Point();
        GeoPoint deviceLocation = new GeoPoint(marker.getPosition().getLatitude() * 1000000.0, marker.getPosition().getLongitude() * 1000000.0);

        mapView.getProjection().toPixels(deviceLocation, currentDevicePosition);
        mapView.getDrawingRect(currentMapBoundsRect);

        return currentMapBoundsRect.contains(currentDevicePosition.x, currentDevicePosition.y);
    }

    private void zoomSpan(double northernLat, double southernLat, double easternLon, double westernLon) {
        double latSpan = northernLat - southernLat;
        double lonSpan = easternLon - westernLon;
        double latCenter = southernLat + latSpan / 2;
        double lonCenter = westernLon + lonSpan / 2;

        double viewLatSpan = mapView.getLatitudeSpanDouble();
        double viewLonSpan = mapView.getLongitudeSpanDouble();
        boolean actionTaken = false;
        if (mapView.getZoomLevelDouble() > 1 && (latSpan > viewLatSpan || lonSpan > viewLonSpan)) {
            mapView.getController().zoomOut();
            actionTaken = true;
        }
        if (mapView.getZoomLevelDouble() < mapView.getMaxZoomLevel() && latSpan * 2 <= viewLatSpan && lonSpan * 2 <= viewLonSpan) {
            mapView.getController().zoomIn();
            actionTaken = true;
        }

        mapView.getController().animateTo((int) latCenter, (int) lonCenter);
    }

}
