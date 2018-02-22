package ca.polymtl.inf3995.oronos;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
/**
 * Local unit test to assert that the SocketClient receive UDP packets when connected to server.
 */
public class SocketClientUnitTest {
    private Context appContext = null;
    // SocketClient and FakeServer Obj
    SocketClient clientObj;
    FakeServer fakeServerObj;

    /**
     * Assert the SocketClient is not null
     */
    @Test
    public void testConstructedObject() {
        clientObj = new SocketClient(GlobalParameters.CLIENT_ADDRESS, GlobalParameters.CLIENT_PORT);
        assertNotNull(clientObj);
    }

    /**
     * This method tests if the DataCallback() can be reached.
     */
    @Test
    public void receiveMsgFromServer() throws Exception {
        appContext = InstrumentationRegistry.getTargetContext();
        clientObj = new SocketClient(GlobalParameters.CLIENT_ADDRESS, GlobalParameters.CLIENT_PORT);
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