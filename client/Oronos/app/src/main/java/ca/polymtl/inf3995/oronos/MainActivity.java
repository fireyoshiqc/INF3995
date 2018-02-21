package ca.polymtl.inf3995.oronos;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Tests for socketClient
        SocketClient socketClient = new SocketClient(GlobalParameters.CLIENT_ADDRESS, GlobalParameters.CLIENT_PORT);
        FakeServer server = new FakeServer(GlobalParameters.CLIENT_ADDRESS, GlobalParameters.CLIENT_PORT);
        server.send("Testing if client can be reached form server through udp");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Client received " + socketClient.numMessagesReceived(0) + " messages.");
    }
}
