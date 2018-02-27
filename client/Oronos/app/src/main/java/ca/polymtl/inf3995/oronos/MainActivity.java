package ca.polymtl.inf3995.oronos;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieManager;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CookieManager cookieManager = new CookieManager();
        java.net.CookieHandler.setDefault(cookieManager);

        setContentView(R.layout.activity_main);

        Timber.plant(new LogTree());
        final TextView myAwesomeTextView = (TextView)findViewById(R.id.myAwesomeTextView);
        myAwesomeTextView.setText("ce texte devrait changer mais ca veut pas dire que le rest fonctionne");
        myAwesomeTextView.setText("ce texte devrait changer si le rest fonctionne");
        RestHttpWrapper wrapper = new RestHttpWrapper(getApplicationContext());
        wrapper.randomJSONForTest(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println("Response from server TESTTEST");
                System.out.println(response);
                myAwesomeTextView.setText(response.toString());
            }
        });
        wrapper.randomJSONForTest(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //myAwesomeTextView.setText(response.toString());
            }
        });
    }


}