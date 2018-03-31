package ca.polymtl.inf3995.oronos.services;

/**
 * Created by prst on 2018-03-15.
 */

public class BroadcastMessage {

    private String canSid;
    private Number data1;
    private Number data2;
    private String sourceModule;
    private Integer serialNb;
    private Integer counter;

    public BroadcastMessage() {
    }

    public BroadcastMessage(String canSid, Number data1, Number data2, String sourceModule, Integer serialNb, Integer counter) {
        this.canSid = canSid;
        this.data1 = data1;
        this.data2 = data2;
        this.sourceModule = sourceModule;
        this.serialNb = serialNb;
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

    public String getSourceModule() {
        return sourceModule;
    }

    public Integer getSerialNb() {
        return serialNb;
    }

    public Integer getCounter() {
        return counter;
    }

}
