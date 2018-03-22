package ca.polymtl.inf3995.oronos.services;

import org.parceler.Parcel;

import ca.polymtl.inf3995.oronos.utils.ModuleType;

/**
 * Created by prst on 2018-03-22.
 */

@Parcel
public class ModuleMessage {

    private ModuleType moduleSource;
    private Integer noSerieSource;
    private Integer counter;

    public ModuleMessage(ModuleType moduleSource, Integer noSerieSource, Integer counter) {
        this.moduleSource = moduleSource;
        this.noSerieSource = noSerieSource;
        this.counter = counter;
    }

    public ModuleType getModuleSource() {
        return moduleSource;
    }

    public Integer getNoSerieSource() {
        return noSerieSource;
    }

    public Integer getCounter() {
        return counter;
    }

}
