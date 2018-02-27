package ca.polymtl.inf3995.oronos;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
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
public class AndroidVolleySingletonTest {
    private RequestQueue volleyQueue;
    private JSONObject responseJSON;
    private Context appContext;

    @Before
    public void setUp() {
        appContext = InstrumentationRegistry.getTargetContext();
    }


    @Test
    public void getRequest_JSON() throws Exception {

        String JSONURL = "http://pastebin.com/raw/2bW31yqa";
        appContext = InstrumentationRegistry.getTargetContext();

        volleyQueue = VolleySingleton.getInstance(appContext).getRequestQueue();

        JsonObjectRequest JSONRequest = new JsonObjectRequest(
            Request.Method.GET,
            JSONURL,
            null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    responseJSON = response;
                    String firstName = null;
                    try {
                        JSONArray array = responseJSON.getJSONArray("students");
                        JSONObject student = array.getJSONObject(0);
                        firstName = student.getString("firstname");
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //assertEquals("This test should not pass", firstName);
                    assertEquals("Richard", firstName);
                    assertNotEquals("QWERTfgh", firstName);
                    System.out.println("The following string should be richard: " + firstName);


                }
            },
            new Response.ErrorListener() { // Error listener
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }
        );
        volleyQueue.add(JSONRequest);
    }

    public void getRequestFromServer_JSON() throws Exception {

        String JSONURL = "http://pastebin.com/raw/2bW31yqa";
        appContext = InstrumentationRegistry.getTargetContext();

        volleyQueue = VolleySingleton.getInstance(appContext).getRequestQueue();

        JsonObjectRequest JSONRequest = new JsonObjectRequest(
                Request.Method.GET,
                JSONURL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        responseJSON = response;
                        String firstName = null;
                        try {
                            JSONArray array = responseJSON.getJSONArray("students");
                            JSONObject student = array.getJSONObject(0);
                            firstName = student.getString("firstname");
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //assertEquals("This test should not pass", firstName);
                        assertEquals("Richard", firstName);
                        assertNotEquals("QWERTfgh", firstName);
                        System.out.println("The following string should be richard: " + firstName);


                    }
                },
                new Response.ErrorListener() { // Error listener
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
        volleyQueue.add(JSONRequest);
    }

    public void getRequest_JSON_FunctionPass(Response.Listener<JSONObject> resListener, Response.ErrorListener errListener) {

        String JSONURL = "http://pastebin.com/raw/2bW31yqa";
        appContext = InstrumentationRegistry.getTargetContext();

        volleyQueue = VolleySingleton.getInstance(appContext).getRequestQueue();

        JsonObjectRequest JSONRequest = new JsonObjectRequest(
                Request.Method.GET,
                JSONURL,
                null,
                resListener,
                errListener
        );
        volleyQueue.add(JSONRequest);
    }

    @Test
    public void FunctionPassTest() throws Exception {
        getRequest_JSON_FunctionPass(
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    responseJSON = response;
                    String lastname = null;
                    try {
                        JSONArray array = responseJSON.getJSONArray("students");
                        JSONObject student = array.getJSONObject(0);
                        lastname = student.getString("lastname");
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //Décommenter la ligne suivante devrait faire planter les tests
                    //assertEquals("This test should not pass", lastname);

                    assertEquals("Levi", lastname);
                    assertNotEquals("QWERTfgh", lastname);


                }
            },
            new Response.ErrorListener() { // Error listener
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }
        );
    }


}
