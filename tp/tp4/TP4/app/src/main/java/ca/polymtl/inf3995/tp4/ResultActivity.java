package ca.polymtl.inf3995.tp4;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.android.volley.toolbox.StringRequest;


public class ResultActivity extends AppCompatActivity {
    private static final String TAG = "ResultActivity";

    static final String SERVER_IP = "127.0.0.1"; //132.207.89.30
    static final int SERVER_PORT = 5000;
    //static final String SERVER_URL = "http://" + SERVER_IP + ":" + SERVER_PORT + "/";
    static final String SERVER_URL = "https://leanpub.com/site_images/jelinux/tux.png";
    static final String TEXT_URL = "http://www.perdu.com/";
    static final String HTML_URL = "http://www.perdu.com/";

    //static final String SERVER_URL = "https://httpstat.us/";

    RequestQueue volleyQueue;

    private ImageView mImageView;
    private TextView mTextView;
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        volleyQueue = VolleySingleton.getInstance(this.getApplicationContext()).getRequestQueue();

        Intent intent = getIntent();
        String value = intent.getStringExtra("key"); //if it's a string you stored.


        requestSomething(value);


    }

    private void requestSomething(final String request) {
        if (request.equals("test1")) {
            mTextView = new TextView(this);
            setContentView(mTextView);
            requestText(request);
        } else if (request.equals("test2")) {
            mWebView = new WebView(this);
            setContentView(mWebView);
            requestHTML(request);
        } else if (request.equals("test3")) {
            mImageView = new ImageView(this);
            setContentView(mImageView);
            requestImage(request);
        }
    }

    private void requestHTML(String subfix) {

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                //SERVER_URL + subfix,
                HTML_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mWebView.loadData(response, "text/html", null);
                    }
                },
                new Response.ErrorListener() { // Error listener
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            //This indicates that the request has either time out or there is no connection
                        } else if (error instanceof AuthFailureError) {
                            // Error indicating that there was an Authentication Failure while performing the request
                        } else if (error instanceof ServerError) {
                            //Indicates that the server responded with a error response
                            String statusCode = String.valueOf(error.networkResponse.statusCode);
                            Log.d(TAG, statusCode);
                            Toast.makeText(getApplicationContext(), statusCode, Toast.LENGTH_SHORT).show();
                        } else if (error instanceof NetworkError) {
                            //Indicates that there was network error while performing the request
                        } else if (error instanceof ParseError) {
                            // Indicates that the server response could not be parsed
                        }
                    }
                }
        );

        volleyQueue.add(stringRequest);
    }

    private void requestImage(String subfix) {

        ImageRequest imageRequest = new ImageRequest(
                //SERVER_URL + subfix,
                SERVER_URL,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        mImageView.setImageBitmap(response);
                    }
                },
                0,
                0,
                ImageView.ScaleType.CENTER_CROP,
                Bitmap.Config.RGB_565,
                new Response.ErrorListener() { // Error listener
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            //This indicates that the request has either time out or there is no connection
                        } else if (error instanceof AuthFailureError) {
                            // Error indicating that there was an Authentication Failure while performing the request
                        } else if (error instanceof ServerError) {
                            //Indicates that the server responded with a error response
                            String statusCode = String.valueOf(error.networkResponse.statusCode);
                            Log.d(TAG, statusCode);
                            Toast.makeText(getApplicationContext(), statusCode, Toast.LENGTH_SHORT).show();
                        } else if (error instanceof NetworkError) {
                            //Indicates that there was network error while performing the request
                        } else if (error instanceof ParseError) {
                            // Indicates that the server response could not be parsed
                        }
                    }
                }
        );

        volleyQueue.add(imageRequest);
    }

    private void requestText(String subfix) {

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                //SERVER_URL + subfix,
                TEXT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mTextView.setText("response: " + response);
                    }
                },
                new Response.ErrorListener() { // Error listener
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            //This indicates that the request has either time out or there is no connection
                        } else if (error instanceof AuthFailureError) {
                            // Error indicating that there was an Authentication Failure while performing the request
                        } else if (error instanceof ServerError) {
                            //Indicates that the server responded with a error response
                            String statusCode = String.valueOf(error.networkResponse.statusCode);
                            Log.d(TAG, statusCode);
                            Toast.makeText(getApplicationContext(), statusCode, Toast.LENGTH_SHORT).show();
                        } else if (error instanceof NetworkError) {
                            //Indicates that there was network error while performing the request
                        } else if (error instanceof ParseError) {
                            // Indicates that the server response could not be parsed
                        }
                    }
                }
        );

        volleyQueue.add(stringRequest);

    }


}
