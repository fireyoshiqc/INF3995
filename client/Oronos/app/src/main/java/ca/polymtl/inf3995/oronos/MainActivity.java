package ca.polymtl.inf3995.oronos;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GlobalParameters parameters = new GlobalParameters();
        SocketClient socketClient = new SocketClient(parameters.SERVER_ADDRESS, parameters.SERVER_PORT);
    }
}
