package ca.polymtl.inf3995.oronos.services;

/**
 * <h1>Broadcast Message</h1>
 * Indicates the fields of the messages going to any tag in need of can information. A Broadcast
 * Message is created any time the client receive a UDP packet from the client containing can
 * information.
 *
 *
 * @author  Patrick Richer St-Onge
 * @version 0.0
 * @since   2018-04-12
 **/
public class BroadcastMessage {

    private String canSid;
    private Number data1;
    private Number data2;
    private String sourceModule;
    private Integer serialNb;
    private Integer counter;

    /**
     * Constructor that should not be used as the Broadcast Message content can only be set on
     * construction since the client should only display the information sent by the server.
     * */
    BroadcastMessage() {
    }

    /**
     * Constructor that should be used so that the Broadcast Message is not empty.
     * @param canSid the id name of a can message.
     * @param data1 the content of the first data field of a can message.
     * @param data2 the content of the second data field of a can message.
     * @param sourceModule the name of the module emitting the message.
     * @param serialNb the serial number of the sourceModule.
     * @param counter the n-message emitted by the sourceModule has counter == n.
     * */
    BroadcastMessage(String canSid, Number data1, Number data2, String sourceModule, Integer serialNb, Integer counter) {
        this.canSid = canSid;
        this.data1 = data1;
        this.data2 = data2;
        this.sourceModule = sourceModule;
        this.serialNb = serialNb;
        this.counter = counter;
    }

    /**
     * @return canSid, the string representing the name id of a can message.
     * */
    public String getCanSid() {
        return canSid;
    }

    /**
     * @return the number contained in the first data field of the can message.
     * */
    public Number getData1() {
        return data1;
    }

    /**
     * @return the number contained in the second data field of the can message.
     * */
    public Number getData2() {
        return data2;
    }

    /**
     * @return sourceModule, the string representing the name of the source module.
     * */
    public String getSourceModule() {
        return sourceModule;
    }

    /**
     * @return serialNb, the integer representing the serial number of the source module.
     * */
    public Integer getSerialNb() {
        return serialNb;
    }

    /**
     * @return counter, the integer representing the n-message of the source module.
     * */
    public Integer getCounter() {
        return counter;
    }

}
