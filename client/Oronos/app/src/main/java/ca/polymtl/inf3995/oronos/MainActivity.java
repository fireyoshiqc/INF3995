package ca.polymtl.inf3995.oronos;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.net.CookieHandler;
import java.net.CookieManager;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CookieHandler.setDefault(new CookieManager());

        setContentView(R.layout.activity_main);

        Timber.plant(new LogTree());


    }


}