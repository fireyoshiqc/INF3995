package ca.polymtl.inf3995.oronos.services;

import android.content.Context;
import android.os.Build;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;


import ca.polymtl.inf3995.oronos.utils.VolleySingleton;
import timber.log.Timber;

/**
 * <h1>Rest Http Wrapper</h1>
 * Singleton Rest Http Wrapper that is the entry and departure point for any rest/http communication
 * with the server.
 *
 *
 * @author  Fabrice Charbonneau, Charles Hosson
 * @version 0.0
 * @since   2018-04-12
 **/
public class RestHttpWrapper {
    private static RestHttpWrapper instance;

    private RequestQueue volleyQueue;
    private String       serverUrl = "";
    private String       username = "";
    private String       password = "";
    private RetryPolicy  retryPolicy;

    /**
     * Class containing a template of a Json POST Request
     * */
    private static class JSONPostRequestVoidResponse extends JsonRequest<Void> {

        /**
         * Constructor building a POSt request to url sending a JSONObject
         *
         * @param url address to which the POST will be sent.
         * @param jsonRequest the message to post; can be null.
         * @param listener the listener waiting for the response.
         * @param errorListener the listener waiting for any error response that could be sent.
         * */
        JSONPostRequestVoidResponse(String url, JSONObject jsonRequest, Response.Listener<Void> listener,
                                    Response.ErrorListener errorListener) {
            super(Request.Method.POST, url, (jsonRequest == null) ? null : jsonRequest.toString(), listener, errorListener);
        }

        /**
         * {@inheritDoc}
         * */
        @Override
        protected Response<Void> parseNetworkResponse(NetworkResponse response) {
            return Response.success(null, HttpHeaderParser.parseCacheHeaders(response));
        }
    }

    /**
     * Constructor called by getInstance() of this Singleton class.
     * */
    private RestHttpWrapper() {
        this.retryPolicy = new DefaultRetryPolicy(5000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
    }

    /**
     * This method returns the Rest Http Wrapper Instance.
     *
     * @return instance of Rest Http Wrapper.
     * */
    public static RestHttpWrapper getInstance() {
        if (instance == null) {
            instance = new RestHttpWrapper();
        }
        return instance;
    }

    /**
     * This method takes as argument the application context and creates the instance of Volley
     * Singleton.
     *
     * @param appContext the application context.
     * */
    public void setup(Context appContext) {
        if (this.volleyQueue == null) {
            this.volleyQueue = VolleySingleton.getInstance(appContext).getRequestQueue();
        }
        else {
            Timber.e("Error: RestHttpWrapper has already been set up, this method should only be called once.");
        }
    }

    /**
     * This method is useful to set the server url, the username and the password that will be sent
     * to the server.
     *
     * @param serverIp IP address of the server.
     * @param port Port number of the server.
     * @param username Username the client wish to log in as.
     * @param password Password associated to the username.
     * */
    public void setLoginInfo(String serverIp, int port, String username, String password) {
        this.serverUrl = "http://" + serverIp + ":" + port;
        this.username = username;
        this.password = password;
    }

    /**
     * This method attempts to log in a user with the information provided to the Rest Http Wrapper
     * using the setLoginInfo. Therefore, setLoginInfo() method should be called at least once before
     * trying to sendPostUsersLogin().
     *
     * @param resListen the listener waiting for the response.
     * @param errListen the listener waiting for any error response that could be sent.
     * */
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
        request.setRetryPolicy(this.retryPolicy);
        request.setShouldCache(false);
        volleyQueue.add(request);
    }

    /**
     * This method attempts to log out a user with the information provided to the Rest Http Wrapper
     * using the setLoginInfo. Therefore, setLoginInfo() method should be called at least once before
     * trying to sendPostUsersLogout(). It is not mandatory to sendPostUsersLogin() before, but if
     * no user has been logged in previously, the log out will do nothing even if sent.
     *
     * @param resListen the listener waiting for the response.
     * @param errListen the listener waiting for any error response that could be sent.
     * */
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
        request.setRetryPolicy(this.retryPolicy);
        request.setShouldCache(false);
        volleyQueue.add(request);
    }

    /**
     * This method signals to the server the client is still up and running. If no heartbeat is heard
     * from the client by the server, the server will eventually close any socket related to the client.
     *
     * @param resListen the listener waiting for the response.
     * @param errListen the listener waiting for any error response that could be sent.
     * */
    public void sendPostUsersHeartbeat(Response.Listener<Void> resListen, Response.ErrorListener errListen) {
        String fullUrl = this.serverUrl + "/users/heartbeat";

        JSONObject json = new JSONObject();

        JSONPostRequestVoidResponse request = new JSONPostRequestVoidResponse(fullUrl,
                                                                              json,
                                                                              resListen,
                                                                              errListen);
        request.setRetryPolicy(this.retryPolicy);
        request.setShouldCache(false);
        volleyQueue.add(request);
    }

    /**
     * This method sends a Json Get Request to obtain the basic configuration of what the server is
     * currently sending.
     *
     * @param resListen the listener waiting for the response.
     * @param errListen the listener waiting for any error response that could be sent.
     * */
    public void sendGetConfigBasic(Response.Listener<JSONObject> resListen, Response.ErrorListener errListen) {
        this.sendJsonGetRequest("/config/basic", resListen, errListen);
    }

    /**
     * This method sends a Json Get Request to obtain the currently emitting rocket xml configuration
     *
     * @param resListen the listener waiting for the response.
     * @param errListen the listener waiting for any error response that could be sent.
     * */
    public void sendGetConfigRockets(Response.Listener<JSONObject> resListen, Response.ErrorListener errListen) {
        this.sendJsonGetRequest("/config/rockets", resListen, errListen);
    }

    /**
     * This method sends a String Get Request to obtain the currently (re)emitting rocket xml configuration
     *
     * @param rocket the name of the rocket xml configuration
     * @param resListen the listener waiting for the response.
     * @param errListen the listener waiting for any error response that could be sent.
     * */
    public void sendGetConfigRockets(String rocket, Response.Listener<String> resListen, Response.ErrorListener errListen) {
        this.sendStringGetRequest("/config/rockets/" + rocket, resListen, errListen);
    }

    /**
     * This method sends a Json Get Request to obtain the can sid configuration of whatever is
     * currently running on the server.
     *
     * @param resListen the listener waiting for the response.
     * @param errListen the listener waiting for any error response that could be sent.
     * */
    public void sendGetConfigCanSid(Response.Listener<JSONObject> resListen, Response.ErrorListener errListen) {
        this.sendJsonGetRequest("/config/canSid", resListen, errListen);
    }

    /**
     * This method sends a Json Get Request to obtain the can data types of whatever is currently
     * running on the server.
     *
     * @param resListen the listener waiting for the response.
     * @param errListen the listener waiting for any error response that could be sent.
     * */
    public void sendGetConfigCanDataTypes(Response.Listener<JSONObject> resListen, Response.ErrorListener errListen) {
        this.sendJsonGetRequest("/config/canDataTypes", resListen, errListen);
    }

    /**
     * This method sends a Json Get Request to obtain the can msg data types of whatever is
     * currently running on the server.
     *
     * @param resListen the listener waiting for the response.
     * @param errListen the listener waiting for any error response that could be sent.
     * */
    public void sendGetConfigCanMsgDataTypes(Response.Listener<JSONObject> resListen, Response.ErrorListener errListen) {
        this.sendJsonGetRequest("/config/canMsgDataTypes", resListen, errListen);
    }

    /**
     * This method sends a Json Get Request to obtain the can msg data types of whatever is
     * currently running on the server.
     *
     * @param resListen the listener waiting for the response.
     * @param errListen the listener waiting for any error response that could be sent.
     * */
    public void sendGetConfigCanModuleTypes(Response.Listener<JSONObject> resListen, Response.ErrorListener errListen) {
        this.sendJsonGetRequest("/config/canModuleTypes", resListen, errListen);
    }

    /**
     * This method sends a Json Get Request to obtain the timeout configuration that is on the server
     * at this moment.
     *
     * @param resListen the listener waiting for the response.
     * @param errListen the listener waiting for any error response that could be sent.
     * */
    public void sendGetConfigTimeout(Response.Listener<JSONObject> resListen, Response.ErrorListener errListen) {
        this.sendJsonGetRequest("/config/timeout", resListen, errListen);
    }

    // TODO: miscFiles

    /**
     * This method is a template for any Json Get Request; it formats the url by adding the specific
     * part of any request to the general «root» part of the server url. Then, it sends the request.
     *
     * @param url the specific part of a get request to the server.
     * @param resListener the listener waiting for the response.
     * @param errListener the listener waiting for any error response that could be sent.
     * */
    private void sendJsonGetRequest(String url, Response.Listener<JSONObject> resListener, Response.ErrorListener errListener) {

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                                                          this.serverUrl + url,
                                                          null,
                                                          resListener,
                                                          errListener);
        request.setRetryPolicy(this.retryPolicy);
        request.setShouldCache(false);
        this.volleyQueue.add(request);
    }

    /**
     * This method is a template for any String Get Request; it formats the url by adding the specific
     * part of any request to the general «root» part of the server url. Then, it sends the request.
     *
     * @param url the specific part of a get request to the server.
     * @param resListener the listener waiting for the response.
     * @param errListener the listener waiting for any error response that could be sent.
     * */
    private void sendStringGetRequest(String url, Response.Listener<String> resListener, Response.ErrorListener errListener) {
        StringRequest request = new StringRequest(Request.Method.GET,
                                                  this.serverUrl + url,
                                                  resListener,
                                                  errListener);
        request.setRetryPolicy(this.retryPolicy);
        request.setShouldCache(false);
        this.volleyQueue.add(request);
    }

    /**
     * This method returns the device name.
     *
     * @return device name as a string.
     * */
    private String getDeviceName() {
        return Build.MODEL;
    }
}
