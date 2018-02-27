package ca.polymtl.inf3995.oronos;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
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

    @Before
    public void setUp() {
        appContext = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void postUserLoginTest() {
        RestHttpWrapper wrapper = new RestHttpWrapper(appContext);
        wrapper.postUserLogin(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        });
    }

    @Test
    public void postUserLogoutTest() {
        RestHttpWrapper wrapper = new RestHttpWrapper(appContext);
        wrapper.postUserLogout(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        });
    }

    @Test
    public void getConfigRocketsTest() {
        RestHttpWrapper wrapper = new RestHttpWrapper(appContext);
        wrapper.getConfigRockets(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        });
    }

    @Test
    public void getRocketTest() {
        RestHttpWrapper wrapper = new RestHttpWrapper(appContext);
        wrapper.getRocket("rocketName", new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        });
    }

    @Test
    public void getConfigMapTest() {
        RestHttpWrapper wrapper = new RestHttpWrapper(appContext);
        wrapper.getConfigMap(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        });
    }

    @Test
    public void getConfigMiscFilesTest() {
        RestHttpWrapper wrapper = new RestHttpWrapper(appContext);
        wrapper.getConfigMiscFiles(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        });
    }

    @Test
    public void getMiscFile() {
        RestHttpWrapper wrapper = new RestHttpWrapper(appContext);
        wrapper.getMiscFile("fileName", new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        });
    }


}
