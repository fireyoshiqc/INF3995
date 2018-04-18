package ca.polymtl.inf3995.oronos.services;

/**
 * <h1>Module Message</h1>
 * Indicates the fields of the messages going to any tag in need of module information. A Module
 * Message is created any time the client receive a UDP packet from the client containing module
 * information.
 *
 * @author Patrick Richer St-Onge
 * @version 0.0
 * @since 2018-04-12
 **/
public class ModuleMessage {

    private String sourceModule;
    private Integer serialNb;
    private Integer counter;

    /**
     * Constructor that should not be used as the Module Message content can only be set on
     * construction since the client should only display the information sent by the server.
     */
    ModuleMessage() {

    }

    /**
     * Constructor that should be used so that the Module Message is not empty.
     *
     * @param sourceModule the name of the module emitting the message.
     * @param serialNb     the serial number of the sourceModule.
     * @param counter      the n-message emitted by the sourceModule has counter == n.
     */
    ModuleMessage(String sourceModule, Integer serialNb, Integer counter) {
        this.sourceModule = sourceModule;
        this.serialNb = serialNb;
        this.counter = counter;
    }

    /**
     * @return sourceModule, the string representing the name of the source module.
     */
    public String getSourceModule() {
        return sourceModule;
    }

    /**
     * @return serialNb, the integer representing the serial number of the source module.
     */
    public Integer getSerialNb() {
        return serialNb;
    }

    /**
     * @return counter, the integer representing the n-message of the source module.
     */
    public Integer getCounter() {
        return counter;
    }

}
