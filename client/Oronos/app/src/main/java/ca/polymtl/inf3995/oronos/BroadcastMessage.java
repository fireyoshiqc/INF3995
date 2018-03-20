package ca.polymtl.inf3995.oronos;

import org.parceler.Parcel;

/**
 * Created by prst on 2018-03-15.
 */

@Parcel
public class BroadcastMessage {

    String canSid;
    Number data1;
    Number data2;
    ModuleType moduleSource;
    Integer noSerieSource;
    Integer counter;

    public BroadcastMessage() {
    }

    public BroadcastMessage(String canSid, Number data1, Number data2, ModuleType moduleSource, Integer noSerieSource, Integer counter) {
        this.canSid = canSid;
        this.data1 = data1;
        this.data2 = data2;
        this.moduleSource = moduleSource;
        this.noSerieSource = noSerieSource;
        this.counter = counter;
    }

    public String getCanSid() {
        return canSid;
    }

    public Number getData1() {
        return data1;
    }

    public Number getData2() {
        return data2;
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
