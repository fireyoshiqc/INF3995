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

public class RestHttpWrapper {
    private RequestQueue volleyQueue;
    static final String SERVER_IP = "132.207.89.30"; //addr a modifier avec ce que le user entre
    static final int SERVER_PORT = 80;
    static final String SERVER_URL = "http://" + SERVER_IP + ":" + SERVER_PORT + "/";

    public RestHttpWrapper(Context appContext) {
        volleyQueue = VolleySingleton.getInstance(appContext).getRequestQueue();
    }

    public void getJSON(String URLSubfix, Response.Listener<JSONObject> resListener, Response.ErrorListener errListener) {

        JsonObjectRequest JSONRequest = new JsonObjectRequest(
                Request.Method.GET,
                SERVER_URL + URLSubfix,
                null,
                resListener,
                errListener
        );
        volleyQueue.add(JSONRequest);
    }

    public void postJSON(String URLSubfix, Response.Listener<JSONObject> resListener, Response.ErrorListener errListener) {

        JsonObjectRequest POSTRequest = new JsonObjectRequest(
                Request.Method.POST,
                SERVER_URL + URLSubfix,
                null,
                resListener,
                errListener
        );

        volleyQueue.add(POSTRequest);
    }


    //https://stackoverflow.com/questions/24740228/android-download-pdf-from-url-then-open-it-with-a-pdf-reader

/*
    private void volleyOnErrorResponse(VolleyError error) {

        //TODO: ajouter les logs

        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
            //This indicates that the request has either time out or there is no connection
            //Toast.makeText(getApplicationContext(), "TimeoutError or NoConnectionError", Toast.LENGTH_SHORT).show();
        } else if (error instanceof AuthFailureError) {
            // Error indicating that there was an Authentication Failure while performing the request
            //Toast.makeText(getApplicationContext(), "AuthFailureError", Toast.LENGTH_SHORT).show();
        } else if (error instanceof ServerError) {
            //Indicates that the server responded with a error response
            String statusCode = String.valueOf(error.networkResponse.statusCode);
            //Toast.makeText(getApplicationContext(), statusCode, Toast.LENGTH_SHORT).show();
        } else if (error instanceof NetworkError) {
            //Indicates that there was network error while performing the request
            //Toast.makeText(getApplicationContext(), "NetworkError", Toast.LENGTH_SHORT).show();
        } else if (error instanceof ParseError) {
            // Indicates that the server response could not be parsed
            //Toast.makeText(getApplicationContext(), "ParseError", Toast.LENGTH_SHORT).show();
        }
    }
*/
}
