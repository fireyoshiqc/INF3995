package ca.polymtl.inf3995.oronos.utils;

import org.osmdroid.tileprovider.tilesource.BitmapTileSourceBase;

/**
 * Created by prst on 2018-03-26.
 */

public class OronosTileSource extends BitmapTileSourceBase {

    public OronosTileSource(String aName, int aZoomMinLevel, int aZoomMaxLevel, int aTileSizePixels, String aImageFilenameEnding) {
        super(aName, aZoomMinLevel, aZoomMaxLevel, aTileSizePixels, aImageFilenameEnding);
    }

    public OronosTileSource(String aName, int aZoomMinLevel, int aZoomMaxLevel, int aTileSizePixels, String aImageFilenameEnding, String aCopyrightNotice) {
        super(aName, aZoomMinLevel, aZoomMaxLevel, aTileSizePixels, aImageFilenameEnding, aCopyrightNotice);
    }



}
