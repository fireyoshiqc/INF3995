package ca.polymtl.inf3995.oronos.parser;

import android.support.annotation.Nullable;

/**
 * Created by Felix on 15/févr./2018.
 */

public class CAN {
    public final String id;
    public final String name;
    public final int minAcceptable;
    public final int maxAcceptable;
    public final int chiffresSign;
    public final String specificSource;
    public final int serialNb;
    public final String customUpdate;
    public final int updateEach;

    public CAN(String id, @Nullable String name, @Nullable int minAcceptable, @Nullable int maxAcceptable,
               @Nullable int chiffresSign, @Nullable String specificSource, @Nullable int serialNb,
               @Nullable String customUpdate, @Nullable int updateEach) {

        this.id = id;
        this.name = name;
        this.minAcceptable = minAcceptable;
        this.maxAcceptable = maxAcceptable;
        this.chiffresSign = chiffresSign;
        this.specificSource = specificSource;
        this.serialNb = serialNb;
        this.customUpdate = customUpdate;
        this.updateEach = updateEach;
    }
}
