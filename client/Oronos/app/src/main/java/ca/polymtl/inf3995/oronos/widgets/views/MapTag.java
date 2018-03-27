package ca.polymtl.inf3995.oronos.widgets.views;

import android.content.Context;
import android.preference.PreferenceManager;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

/**
 * Created by Felix on 15/févr./2018.
 */

public class MapTag extends OronosView {

    private MapView mapView;

    public MapTag(Context context) {
        super(context);

        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));

        mapView = new MapView(context);

        mapView.setUseDataConnection(false);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        ITileSource iTileSource = new XYTileSource("map", 0, 15, 256, ".jpg", new String[]{"empty"});
        mapView.setTileSource(iTileSource);

        IMapController mapController = mapView.getController();
        mapController.setZoom(13.0);
        GeoPoint startPoint = new GeoPoint(32.9401475, -106.9193209);
        mapController.setCenter(startPoint);

        addView(mapView);

    }

}
