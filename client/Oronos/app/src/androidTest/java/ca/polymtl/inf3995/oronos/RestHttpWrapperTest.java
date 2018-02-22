package ca.polymtl.inf3995.oronos;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by Fabri on 2018-02-20.
 */

@RunWith(AndroidJUnit4.class)
public class RestHttpWrapperTest {
    private Context appContext;



    @Test
    public void getJSON() {

        appContext = InstrumentationRegistry.getContext();
        RestHttpWrapper wrapper = new RestHttpWrapper(appContext);
        wrapper.getJSON(
                "config/basic",
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //"map": "spaceport_america"
                        String resString = "";
                        try {
                            resString = response.getString("map");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        assertEquals("spaceport_america", resString);
                        System.out.println("ResponseFromServer" + resString);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
    }
/*
    @Test
    public void postLogin() {
        appContext = InstrumentationRegistry.getContext();
        RestHttpWrapper wrapper = new RestHttpWrapper(appContext);
        wrapper.postJSON(
                "users/login",
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }

        );
    }*/

}
