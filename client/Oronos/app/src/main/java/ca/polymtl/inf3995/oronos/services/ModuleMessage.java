package ca.polymtl.inf3995.oronos.services;

import org.parceler.Parcel;

/**
 * Created by prst on 2018-03-22.
 */

@Parcel
public class ModuleMessage {

    private String moduleSource;
    private Integer noSerieSource;
    private Integer counter;

    public ModuleMessage() {

    }

    public ModuleMessage(String moduleSource, Integer noSerieSource, Integer counter) {
        this.moduleSource = moduleSource;
        this.noSerieSource = noSerieSource;
        this.counter = counter;
    }

    public String getModuleSource() {
        return moduleSource;
    }

    public Integer getNoSerieSource() {
        return noSerieSource;
    }

    public Integer getCounter() {
        return counter;
    }

}
