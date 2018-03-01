package ca.polymtl.inf3995.oronos;

import android.os.Bundle;

import java.net.CookieHandler;
import java.net.CookieManager;

import timber.log.Timber;

public class MainActivity extends DrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        super.onCreateDrawer();
        CookieHandler.setDefault(new CookieManager());

        Timber.plant(new LogTree());

    }


}