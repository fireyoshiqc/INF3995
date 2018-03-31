package ca.polymtl.inf3995.oronos.services;

/**
 * Created by prst on 2018-03-22.
 */

public class ModuleMessage {

    private String sourceModule;
    private Integer serialNb;
    private Integer counter;

    public ModuleMessage() {

    }

    public ModuleMessage(String sourceModule, Integer serialNb, Integer counter) {
        this.sourceModule = sourceModule;
        this.serialNb = serialNb;
        this.counter = counter;
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
