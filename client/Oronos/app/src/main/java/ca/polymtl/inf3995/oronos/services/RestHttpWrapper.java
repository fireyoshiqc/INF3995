package ca.polymtl.inf3995.oronos.services;

/**
 * Created by Fabri on 2018-02-13.
 * Wrapper for Http and Rest requests
 */

import android.content.Context;
import android.os.Build;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;


import ca.polymtl.inf3995.oronos.utils.VolleySingleton;
import timber.log.Timber;


public class RestHttpWrapper {
    private static RestHttpWrapper instance;

    private RequestQueue volleyQueue;
    private String       serverUrl = "";
    private String       username = "";
    private String       password = "";

    private static class JSONPostRequestVoidResponse extends JsonRequest<Void> {

        JSONPostRequestVoidResponse(String url, JSONObject jsonRequest, Response.Listener<Void> listener,
                                    Response.ErrorListener errorListener) {
            super(Request.Method.POST, url, (jsonRequest == null) ? null : jsonRequest.toString(), listener, errorListener);
        }

        @Override
        protected Response<Void> parseNetworkResponse(NetworkResponse response) {
            return Response.success(null, HttpHeaderParser.parseCacheHeaders(response));
        }
    }

    private RestHttpWrapper() { }

    public static RestHttpWrapper getInstance() {
        if (instance == null) {
            instance = new RestHttpWrapper();
        }
        return instance;
    }

    public void setup(Context appContext) {
        if (this.volleyQueue == null) {
            //this.appContext = appContext;
            this.volleyQueue = VolleySingleton.getInstance(appContext).getRequestQueue();
        }
        else {
            Timber.e("Error: RestHttpWrapper has already been set up, this method should only be called once.");
        }
    }

    public void setLoginInfo(String serverIp, int port, String username, String password) {
        this.serverUrl = "http://" + serverIp + ":" + port;
        this.username = username;
        this.password = password;
    }

    public void sendPostUsersLogin(Response.Listener<Void> resListen, Response.ErrorListener errListen) {
        String fullUrl = this.serverUrl + "/users/login";

        JSONObject json = new JSONObject();
        try {
            json.put("username", this.username);
            json.put("password", this.password);
            json.put("device", this.getDeviceName());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONPostRequestVoidResponse request = new JSONPostRequestVoidResponse(fullUrl,
                                                                              json,
                                                                              resListen,
                                                                              errListen);

        request.setShouldCache(false);
        volleyQueue.add(request);
    }

    public void sendPostUsersLogout(Response.Listener<Void> resListen, Response.ErrorListener errListen) {
        String fullUrl = this.serverUrl + "/users/logout";

        JSONObject json = new JSONObject();
        try {
            json.put("username", this.username);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONPostRequestVoidResponse request = new JSONPostRequestVoidResponse(fullUrl,
                                                                              json,
                                                                              resListen,
                                                                              errListen);

        request.setShouldCache(false);
        volleyQueue.add(request);
    }

    public void sendGetConfigBasic(Response.Listener<JSONObject> resListen, Response.ErrorListener errListen) {
        this.sendJsonGetRequest("/config/basic", resListen, errListen);
    }

    public void sendGetConfigRockets(Response.Listener<JSONObject> resListen, Response.ErrorListener errListen) {
        this.sendJsonGetRequest("/config/rockets", resListen, errListen);
    }

    public void sendGetConfigRockets(String rocket, Response.Listener<String> resListen, Response.ErrorListener errListen) {
        this.sendStringGetRequest("/config/rockets/" + rocket, resListen, errListen);
    }

    public void sendGetConfigCanSid(Response.Listener<JSONObject> resListen, Response.ErrorListener errListen) {
        this.sendJsonGetRequest("/config/canSid", resListen, errListen);
    }

    public void sendGetConfigCanDataTypes(Response.Listener<JSONObject> resListen, Response.ErrorListener errListen) {
        this.sendJsonGetRequest("/config/canDataTypes", resListen, errListen);
    }

    public void sendGetConfigCanMsgDataTypes(Response.Listener<JSONObject> resListen, Response.ErrorListener errListen) {
        this.sendJsonGetRequest("/config/canMsgDataTypes", resListen, errListen);
    }

    public void sendGetConfigCanModuleTypes(Response.Listener<JSONObject> resListen, Response.ErrorListener errListen) {
        this.sendJsonGetRequest("/config/canModuleTypes", resListen, errListen);
    }

    // TODO: miscFiles

    private void sendJsonGetRequest(String url, Response.Listener<JSONObject> resListener, Response.ErrorListener errListener) {

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                                                          this.serverUrl + url,
                                                          null,
                                                          resListener,
                                                          errListener);
        request.setShouldCache(false);
        this.volleyQueue.add(request);
    }

    private void sendStringGetRequest(String url, Response.Listener<String> resListener, Response.ErrorListener errListener) {
        StringRequest request = new StringRequest(Request.Method.GET,
                                                  this.serverUrl + url,
                                                  resListener,
                                                  errListener);
        request.setShouldCache(false);
        this.volleyQueue.add(request);
    }

    private String getDeviceName() {
        return Build.MODEL;
    }

}
