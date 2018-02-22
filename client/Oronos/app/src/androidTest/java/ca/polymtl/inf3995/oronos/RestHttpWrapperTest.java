package ca.polymtl.inf3995.oronos;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by Fabri on 2018-02-20.
 */

@RunWith(AndroidJUnit4.class)
public class RestHttpWrapperTest {
    private Context appContext;

    @Test
    public void getJSON() {
        appContext = InstrumentationRegistry.getContext();
        RestHttpWrapper wrapper = new RestHttpWrapper();
        wrapper.GET_JSON(
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                },
                appContext
        );
    }

    @Test
    public void postLogin() {
        appContext = InstrumentationRegistry.getContext();
        RestHttpWrapper wrapper = new RestHttpWrapper();
        wrapper.POST_LOGIN(
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                },
                appContext
        );
    }

}
