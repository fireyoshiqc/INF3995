package ca.polymtl.inf3995.oronos;

import org.parceler.Parcel;

/**
 * Created by prst on 2018-03-22.
 */

@Parcel
public class ModuleMessage {

    ModuleType moduleSource;
    Integer noSerieSource;
    Integer counter;

    public ModuleMessage() {
    }

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
