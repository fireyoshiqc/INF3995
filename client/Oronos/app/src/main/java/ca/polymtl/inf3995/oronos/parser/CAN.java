package ca.polymtl.inf3995.oronos.parser;

import android.support.annotation.Nullable;

/**
 * Created by Felix on 15/févr./2018.
 */

public class CAN {
    public final String id;
    public final String name;
    public final String display;
    public final String minAcceptable;
    public final String maxAcceptable;
    public final String chiffresSign;
    public final String specificSource;
    public final String serialNb;
    public final String customUpdate;
    public final String updateEach;

    public CAN(String id, @Nullable String name, @Nullable String display,
               @Nullable String minAcceptable, @Nullable String maxAcceptable,
               @Nullable String chiffresSign, @Nullable String specificSource,
               @Nullable String serialNb, @Nullable String customUpdate,
               @Nullable String updateEach) {

        this.id = id;
        this.name = name;
        this.display = display;
        this.minAcceptable = minAcceptable;
        this.maxAcceptable = maxAcceptable;
        this.chiffresSign = chiffresSign;
        this.specificSource = specificSource;
        this.serialNb = serialNb;
        this.customUpdate = customUpdate;
        this.updateEach = updateEach;
    }
}
