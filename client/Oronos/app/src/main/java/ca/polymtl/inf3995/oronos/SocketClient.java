package ca.polymtl.inf3995.oronos;

import com.koushikdutta.async.AsyncDatagramSocket;
import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.callback.DataCallback;
import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import android.util.Log;

public class SocketClient {

    private final InetSocketAddress host;
    private AsyncDatagramSocket asyncDatagramSocket;

    /**
     * Datagram Sockets for asynchronous reception of UDP packets. Heavily inspired by the
     * example found on github : https://github.com/reneweb/AndroidAsyncSocketExamples/tree/master/
     * app/src/main/java/com/github/reneweb/androidasyncsocketexamples/udp
     *
     * @param host      String representing IP address of server
     * @param port      int that is the server port for communication.
     */
    public SocketClient(String host, int port) {
        try {
            this.host = new InetSocketAddress(host, port);
        } catch (IllegalArgumentException e) {
            Log.e(
                    Log.getStackTraceString(e),
                    "Port is outside the range of valid port values or host is null."
            );
            throw new RuntimeException(e);
        }
        setup();
    }

    private void setup() {
        try {
            asyncDatagramSocket = AsyncServer.getDefault().connectDatagram(host);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        asyncDatagramSocket.setClosedCallback(new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                if(ex != null) throw new RuntimeException(ex);
                Log.v("SocketClient", "Successfully closed connection");
            }
        });

        asyncDatagramSocket.setEndCallback(new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                if(ex != null) throw new RuntimeException(ex);
                Log.v("SocketClient", "Successfully end connection");
            }
        });

        asyncDatagramSocket.setDataCallback(new DataCallback() {
            @Override
            public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {
                Log.v("SocketClient", "[UDP] : " + new String(bb.getAllByteArray()));
            }
        });
    }
}
