package ca.polymtl.inf3995.oronos.widgets.views;

import android.content.Context;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ca.polymtl.inf3995.oronos.R;
import ca.polymtl.inf3995.oronos.services.BroadcastMessage;
import ca.polymtl.inf3995.oronos.services.DataDispatcher;
import ca.polymtl.inf3995.oronos.utils.GlobalParameters;
import timber.log.Timber;

/**
 * <h1>Map Tag</h1>
 * Displays map using osmdroid
 *
 * @author Patrick Richer St-Onge, Fabrice Charbonneau
 * @version 0.0
 * @since 2018-04-12
 */

public class MapTag extends OronosView implements DataDispatcher.CANDataListener {

    private static final int REFRESH_DELAY = 1000; // milliseconds
    private final Handler handler;
    private MapView mapView;
    private GeoPoint rocketLocation;
    private Marker rocketMarker;

    private final CoordinatorLayout coordinator;
    private final LinearLayout content;

    private GeoPoint serverLocation;
    private final Runnable run = new Runnable() {
        @Override
        public void run() {
            updateMarker();
            handler.postDelayed(this, REFRESH_DELAY);
        }
    };

    public MapTag(Context context) {
        super(context);
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));
        handler = new Handler();

        coordinator = new CoordinatorLayout(getContext());
        content = new LinearLayout(getContext());
        mapView = new MapView(context);

        //buildView();
        setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        content.setOrientation(LinearLayout.VERTICAL);
        content.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        //content.addView(mapView);
        coordinator.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        coordinator.addView(content);
        addView(coordinator);
    }

    private void buildView() {
        //setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

//        setupRocketMarker();
//        setupMapView();
//
//        content.setOrientation(LinearLayout.VERTICAL);
//        content.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
//
//        content.addView(mapView);


    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        handler.postDelayed(run, REFRESH_DELAY);
        mapView = new MapView(getContext());
        setupRocketMarker();
        setupMapView();
        register();
        //addView(mapView);
        content.addView(mapView);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        handler.removeCallbacks(run);
        unregister();
        if (mapView != null) {
            content.removeView(mapView);
        }

    }

    private void register() {
        DataDispatcher.registerCANDataListener(this);
    }

    private void unregister() {
        DataDispatcher.unregisterCANDataListener(this);
    }

    private void setupMapView() {
        mapView.setUseDataConnection(false);
        mapView.setBuiltInZoomControls(false);
        mapView.setMultiTouchControls(true);
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
                Snackbar.make(coordinator, "Unknown map. Default to Spaceport America.", Snackbar.LENGTH_LONG).show();
        }

        mapView.setTileSource(iTileSource);

        IMapController mapController = mapView.getController();

        Marker serverMarker = new Marker(mapView);
        serverMarker.setTitle("Server");
        serverMarker.setIcon(ContextCompat.getDrawable(getContext(), R.drawable.ic_home_black_24dp));

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
        rocketMarker.setIcon(ContextCompat.getDrawable(getContext(), R.drawable.ic_adjust_black_24dp));
        mapView.getOverlays().add(rocketMarker);
    }

    @Override
    public void onCANDataReceived(BroadcastMessage msg) {
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

    @Override
    public List<String> getCANSidList() {
        return new ArrayList<>(Arrays.asList("GPS1_LATITUDE", "GPS1_LONGITUDE", "GPS1_ALT_MSL"));
    }

    @Override
    public String getSourceModule() {
        return null;
    }

    @Override
    public String getSerialNumber() {
        return null;
    }
}
