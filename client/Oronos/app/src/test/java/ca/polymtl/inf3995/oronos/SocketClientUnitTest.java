package ca.polymtl.inf3995.oronos;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Local unit test to assert that the SocketClient receive UDP packets when connected to server.
 */
public class SocketClientUnitTest {
    // SocketClient and FakeServer Obj
    SocketClient clientObj;
    FakeServer fakeServerObj;

    /**
  	 * Create new SocketClient object before every test method
  	 */
    @Before
    public void setUpClient() throws Exception {
        clientObj = new SocketClient(GlobalParameters.CLIENT_ADDRESS, GlobalParameters.CLIENT_PORT);
    }

    /**
     * Assert the SocketClient is not null
     */
    @Test
    public void testConstructedObject() {
        assertNotNull(clientObj);
    }

    /**
     * This method tests if the DataCallback() can be reached.
     */
    @Test
    public void receiveMsgFromServer() throws Exception {
        fakeServerObj = new FakeServer(GlobalParameters.CLIENT_ADDRESS, GlobalParameters.CLIENT_PORT);
        fakeServerObj.send("Testing if client can be reached form server through udp");
        Thread.sleep(1000);
        assertEquals(1, clientObj.numMessagesReceived(0));
        fakeServerObj.send("Testing if client can be reached form server another time");
        fakeServerObj.send("Testing if client can be reached form server again");
        Thread.sleep(1000);
        assertEquals(3, clientObj.numMessagesReceived(0));
    }
}