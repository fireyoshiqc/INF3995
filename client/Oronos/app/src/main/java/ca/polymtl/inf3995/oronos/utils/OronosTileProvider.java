package ca.polymtl.inf3995.oronos.utils;

import android.content.res.AssetManager;

import org.osmdroid.tileprovider.IRegisterReceiver;
import org.osmdroid.tileprovider.modules.MapTileAssetsProvider;
import org.osmdroid.tileprovider.tilesource.ITileSource;

/**
 * Created by prst on 2018-03-26.
 */

public class OronosTileProvider extends MapTileAssetsProvider {

    public OronosTileProvider(IRegisterReceiver pRegisterReceiver, AssetManager pAssets) {
        super(pRegisterReceiver, pAssets);
    }

    public OronosTileProvider(IRegisterReceiver pRegisterReceiver, AssetManager pAssets, ITileSource pTileSource) {
        super(pRegisterReceiver, pAssets, pTileSource);
    }

    public OronosTileProvider(IRegisterReceiver pRegisterReceiver, AssetManager pAssets, ITileSource pTileSource, int pThreadPoolSize, int pPendingQueueSize) {
        super(pRegisterReceiver, pAssets, pTileSource, pThreadPoolSize, pPendingQueueSize);
    }
}
