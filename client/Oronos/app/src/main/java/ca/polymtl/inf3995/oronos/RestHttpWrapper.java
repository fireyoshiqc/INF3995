package ca.polymtl.inf3995.oronos;

/**
 * Created by Fabri on 2018-02-13.
 * Wrapper for Http and Rest requests
 */

import android.content.Context;
import android.os.Build;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import timber.log.Timber;


public class RestHttpWrapper {


    static final String SERVER_IP = "192.168.0.102"; //has to change according to user input
    static final int SERVER_PORT = 80;
    static final String SERVER_URL = "http://" + SERVER_IP + ":" + SERVER_PORT + "/";
    static final String DEVICE_NAME = Build.MODEL;
    Context appContext;
    private RequestQueue volleyQueue;
    private String username = "foo"; //has to change according to user input
    private String password = "password1234"; //has to change according to user input

    public RestHttpWrapper(Context appContext) {
        this.appContext = appContext;
        volleyQueue = VolleySingleton.getInstance(appContext).getRequestQueue();

    }


    public void postUserLogin(Response.Listener<JSONObject> resListener) {
        String URLSubfix = "users/login";

        JSONObject json = new JSONObject();
        try {
            json.put("username", username);
            json.put("password", password);
            json.put("device", DEVICE_NAME);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest POSTRequest = new JsonObjectRequest(
                Request.Method.POST,
                SERVER_URL + URLSubfix,
                json,
                resListener,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        volleyOnErrorResponse(error);
                    }
                }
        ) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("statusCode", response.statusCode);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return Response.success(jsonObject, HttpHeaderParser.parseCacheHeaders(response));
            }
        };

        volleyQueue.add(POSTRequest);
    }

    public void postUserLogout(Response.Listener<JSONObject> resListener) {

        String URLSubfix = "users/logout";

        JSONObject json = new JSONObject();
        try {
            json.put("username", username);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest POSTRequest = new JsonObjectRequest(
                Request.Method.POST,
                SERVER_URL + URLSubfix,
                json,
                resListener,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        volleyOnErrorResponse(error);
                    }
                }
        ) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("statusCode", response.statusCode);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return Response.success(jsonObject, HttpHeaderParser.parseCacheHeaders(response));
            }
        };

        volleyQueue.add(POSTRequest);
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
                SERVER_URL + URLSubfix,
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


    //Peut être utilisé dans errListener
    private void volleyOnErrorResponse(VolleyError error) {

        //TODO: ajouter les logs

        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
            //This indicates that the request has either time out or there is no connection
            Toast.makeText(this.appContext, "TimeoutError or NoConnectionError", Toast.LENGTH_SHORT).show();
            Timber.v("TimeoutError or NoConnectionError");
        } else if (error instanceof AuthFailureError) {
            // Error indicating that there was an Authentication Failure while performing the request
            Toast.makeText(this.appContext, "AuthFailureError", Toast.LENGTH_SHORT).show();
            Timber.v("AuthFailureError");
        } else if (error instanceof ServerError) {
            //Indicates that the server responded with a error response
            String statusCode = String.valueOf(error.networkResponse.statusCode);
            Toast.makeText(this.appContext, statusCode, Toast.LENGTH_SHORT).show();
            Timber.v(statusCode);
        } else if (error instanceof NetworkError) {
            //Indicates that there was network error while performing the request
            Toast.makeText(this.appContext, "NetworkError", Toast.LENGTH_SHORT).show();
            Timber.v("NetworkError");
        } else if (error instanceof ParseError) {
            // Indicates that the server response could not be parsed
            Toast.makeText(this.appContext, "ParseError", Toast.LENGTH_SHORT).show();
            Timber.v("ParseError");
        }
    }

}
