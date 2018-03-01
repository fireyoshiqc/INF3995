package ca.polymtl.inf3995.oronos.parser;

import android.support.annotation.Nullable;

/**
 * Created by Felix on 15/févr./2018.
 */

public class CAN {
    private final String id;
    private final String name;
    private final String display;
    private final String minAcceptable;
    private final String maxAcceptable;
    private final String chiffresSign;
    private final String specificSource;
    private final String serialNb;
    private final String customUpdate;
    private final String updateEach;

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

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDisplay() {
        return display;
    }

    public String getMinAcceptable() {
        return minAcceptable;
    }

    public String getMaxAcceptable() {
        return maxAcceptable;
    }

    public String getChiffresSign() {
        return chiffresSign;
    }

    public String getSpecificSource() {
        return specificSource;
    }

    public String getSerialNb() {
        return serialNb;
    }

    public String getCustomUpdate() {
        return customUpdate;
    }

    public String getUpdateEach() {
        return updateEach;
    }
}
