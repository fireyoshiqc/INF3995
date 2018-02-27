package ca.polymtl.inf3995.oronos;

/**
 * Created by Fabri on 2018-02-13.
 * Wrapper for Http and Rest requests
 */
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.Toast;

import ca.polymtl.inf3995.oronos.VolleySingleton;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.CookieHandler;
import java.net.CookieManager;


public class RestHttpWrapper {


    private RequestQueue volleyQueue;
    static final String SERVER_IP = "10.0.2.2"; //addr a modifier avec ce que le user entre
    static final int SERVER_PORT = 80;
    static final String SERVER_URL = "http://" + SERVER_IP + ":" + SERVER_PORT + "/";
    Context appContext;

    public RestHttpWrapper(Context appContext) {
        this.appContext = appContext;
        volleyQueue = VolleySingleton.getInstance(appContext).getRequestQueue();

    }

    public void postUserLogin(Response.Listener<JSONObject> resListener) {
        String username = "foo";
        String password = "password1234";

        JSONObject json = new JSONObject();
        try {
            json.put("username", username);
            json.put("password", password);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        postJSON("users/login", json, resListener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                volleyOnErrorResponse(error);
            }
        });
    }

    private void getJSON(String URLSubfix, Response.Listener<JSONObject> resListener, Response.ErrorListener errListener) {

        JsonObjectRequest JSONRequest = new JsonObjectRequest(
                Request.Method.GET,
                SERVER_URL + URLSubfix,
                null,
                resListener,
                errListener
        );
        volleyQueue.add(JSONRequest);
    }

    private void postJSON(String URLSubfix, JSONObject JSONbody, Response.Listener<JSONObject> resListener, Response.ErrorListener errListener) {

        JsonObjectRequest POSTRequest = new JsonObjectRequest(
                Request.Method.POST,
                SERVER_URL + URLSubfix,
                JSONbody,
                resListener,
                errListener
        );

        volleyQueue.add(POSTRequest);
    }




    public void randomJSONForTest( Response.Listener<JSONObject> resListener) {

        String username = "foo";

        JSONObject json = new JSONObject();
        try {
            json.put("username", username);
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        getRandomJSONForTest(json, resListener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                volleyOnErrorResponse(error);
            }
        });
    }

    private void getRandomJSONForTest(JSONObject JSONbody, Response.Listener<JSONObject> resListener, Response.ErrorListener errListener){

        JsonObjectRequest JSONRequest = new JsonObjectRequest(
                Request.Method.GET,
                SERVER_URL + "config/basic",
                JSONbody,
                resListener,
                errListener
        );
        volleyQueue.add(JSONRequest);

    }




    //Peut être utilisé dans errListener
    private void volleyOnErrorResponse(VolleyError error) {

        //TODO: ajouter les logs

        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
            //This indicates that the request has either time out or there is no connection
            Toast.makeText(this.appContext, "TimeoutError or NoConnectionError", Toast.LENGTH_SHORT).show();
        } else if (error instanceof AuthFailureError) {
            // Error indicating that there was an Authentication Failure while performing the request
            Toast.makeText(this.appContext, "AuthFailureError", Toast.LENGTH_SHORT).show();
        } else if (error instanceof ServerError) {
            //Indicates that the server responded with a error response
            String statusCode = String.valueOf(error.networkResponse.statusCode);
            Toast.makeText(this.appContext, statusCode, Toast.LENGTH_SHORT).show();
        } else if (error instanceof NetworkError) {
            //Indicates that there was network error while performing the request
            Toast.makeText(this.appContext, "NetworkError", Toast.LENGTH_SHORT).show();
        } else if (error instanceof ParseError) {
            // Indicates that the server response could not be parsed
            Toast.makeText(this.appContext, "ParseError", Toast.LENGTH_SHORT).show();
        }
    }

}
