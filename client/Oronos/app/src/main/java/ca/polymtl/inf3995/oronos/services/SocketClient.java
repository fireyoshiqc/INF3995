package ca.polymtl.inf3995.oronos.services;

import android.os.Process;
import android.util.Log;

import com.illposed.osc.OSCMessage;
import com.illposed.osc.utility.OSCByteArrayToJavaConverter;
import com.koushikdutta.async.AsyncDatagramSocket;
import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.callback.DataCallback;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import timber.log.Timber;

/**
 * <h1>Socket Client</h1>
 * Singleton Datagram Sockets for asynchronous reception of UDP packets. Heavily inspired by the
 * example found on github : https://github.com/reneweb/AndroidAsyncSocketExamples/tree/master/
 * app/src/main/java/com/github/reneweb/androidasyncsocketexamples/udp
 * <p>
 * Packets are handled by JavaOSC to create OSCMessage and parse automatically binary data.
 * Extracted data is forwarded to DataDispatcher in List format.
 *
 * @author Félix Boulet, Justine Pepin, Patrick Richer St-Onge
 * @version 0.0
 * @since 2018-04-12
 **/
public class SocketClient {

    private static SocketClient instance;
    private InetSocketAddress host;
    private AsyncDatagramSocket asyncDatagramSocket;
    private OSCByteArrayToJavaConverter byteToJavaConverter = new OSCByteArrayToJavaConverter();
    private Executor dispatchExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    // This is for test purpose.
    private int numOfMessageReceived = 0;

    /**
     * Constructor of SocketClient.
     */
    private SocketClient() {
    }

    public static SocketClient getInstance() {
        if (instance == null) {
            instance = new SocketClient();
        }
        return instance;
    }

    /**
     * This method counts the number of msg received. Useful for testing.
     */
    public synchronized int numMessagesReceived(int num) {
        if (numOfMessageReceived == Integer.MAX_VALUE) {
            numOfMessageReceived = 0;
        }
        return numOfMessageReceived += num;
    }

    /**
     * This method is connecting a UDP socket client at given IP address and port. Declares the
     * callback responsible of handling any incoming UDP packet.
     *
     * @param hostname local IP address
     * @param port     local port
     */
    public void connect(String hostname, int port) {
        if (host == null) {
            try {
                this.host = new InetSocketAddress(hostname, port);
            } catch (IllegalArgumentException e) {
                Timber.e(
                        Log.getStackTraceString(e),
                        "Port is outside the range of valid port values or host is null."
                );
                throw new RuntimeException(e);
            }

            try {
                asyncDatagramSocket = AsyncServer.getDefault().openDatagram(this.host, true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            asyncDatagramSocket.setClosedCallback(new CompletedCallback() {
                @Override
                public void onCompleted(Exception ex) {
                    if (ex != null) throw new RuntimeException(ex);
                    Timber.v("SocketClient Successfully closed connection");
                }
            });

            asyncDatagramSocket.setEndCallback(new CompletedCallback() {
                @Override
                public void onCompleted(Exception ex) {
                    if (ex != null) throw new RuntimeException(ex);
                    Timber.v("SocketClient Successfully end connection");
                }
            });

            asyncDatagramSocket.setDataCallback(new DataCallback() {
                @Override
                public void onDataAvailable(DataEmitter emitter, final ByteBufferList bb) {
                    if (bb != null) {
                        byte[] bytesReceived = bb.getAllByteArray();
                        OSCMessage message = getOSCMessage(bytesReceived);
                        forwardToDispatcher(message.getAddress(), message);
                        numMessagesReceived(1);
                    }
                }
            });
        } else {
            Timber.e("SocketClient has already been set up, this method should only be called once.");
        }

    }

    /**
     * This method is disconnecting the UDP socket client.
     */
    public void disconnect() {
        asyncDatagramSocket = null;
        host = null;
    }


    /**
     * This method is converting the bytes received by UDP socket into an OSC msg.
     *
     * @param bytesReceived array of bytes to be converted.
     */
    private OSCMessage getOSCMessage(byte[] bytesReceived) {
        return (OSCMessage) byteToJavaConverter.convert(bytesReceived, bytesReceived.length);
    }

    /**
     * This method is sending an OSC msg to the DataDispatcher.
     *
     * @param address address of DataDispatcher channel on which to send an OSC msg.
     * @param message OSC msg to send.
     */
    private void forwardToDispatcher(final String address, final OSCMessage message) {
        dispatchExecutor.execute(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

                if (address.equals("/inf3995-03/can-data")) {
                    DataDispatcher.dataToDispatch(message.getArguments());

                } else if (address.equals("/inf3995-03/modules")) {
                    DataDispatcher.moduleToDispatch(message.getArguments());
                }
            }
        });
    }
}
