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

    public void postUsersLogin(Response.Listener<Void> resListen, Response.ErrorListener errListen) {
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

    public void postUsersLogout(Response.Listener<Void> resListen, Response.ErrorListener errListen) {
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

    public void getConfigRockets(Response.Listener<JSONObject> resListener) {
        getJSON("config/rockets", resListener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                volleyOnErrorResponse(error);
            }
        });
    }

    public void getRocket(String rocketName, Response.Listener<JSONObject> resListener) {
        getJSON("config/rockets/" + rocketName, resListener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                volleyOnErrorResponse(error);
            }
        });
    }

    public void getConfigMap(Response.Listener<JSONObject> resListener) {
        getJSON("config/map", resListener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                volleyOnErrorResponse(error);
            }
        });
    }

    public void getConfigMiscFiles(Response.Listener<JSONObject> resListener) {
        getJSON("config/miscFiles", resListener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                volleyOnErrorResponse(error);
            }
        });
    }

    public void getMiscFile(String fileName, Response.Listener<JSONObject> resListener) {
        getJSON("config/miscFiles/" + fileName, resListener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                volleyOnErrorResponse(error);
            }
        });
    }

    private void getJSON(String URLSubfix, Response.Listener<JSONObject> resListener, Response.ErrorListener errListener) {

        JsonObjectRequest JSONRequest = new JsonObjectRequest(
                Request.Method.GET,
                this.serverUrl,
                null,
                resListener,
                errListener
        );
        volleyQueue.add(JSONRequest);
    }

    public void getConfigCanSid(Response.Listener<JSONObject> resListener) {
        getJSON("config/canSid", resListener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                volleyOnErrorResponse(error);
            }
        });
    }

    public void getConfigCanDataTypes(Response.Listener<JSONObject> resListener) {
        getJSON("config/canDataTypes", resListener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                volleyOnErrorResponse(error);
            }
        });
    }

    public void getConfigCanMsgDataTypes(Response.Listener<JSONObject> resListener) {
        getJSON("config/canMsgDataTypes", resListener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                volleyOnErrorResponse(error);
            }
        });
    }

    public void getConfigCanModuleTypes(Response.Listener<JSONObject> resListener) {
        getJSON("config/canModuleTypes", resListener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                volleyOnErrorResponse(error);
            }
        });
    }


    //Peut être utilisé dans errListener
    private void volleyOnErrorResponse(VolleyError error) {

    }

    private String getDeviceName() {
        return Build.MODEL;
    }

}
