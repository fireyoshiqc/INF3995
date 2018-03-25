package ca.polymtl.inf3995.oronos.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ca.polymtl.inf3995.oronos.R;
import ca.polymtl.inf3995.oronos.services.RestHttpWrapper;
import ca.polymtl.inf3995.oronos.utils.GlobalParameters;
import ca.polymtl.inf3995.oronos.utils.JsonHelper;
import timber.log.Timber;


class HomeScreenInputs {
    String serverAddress = "";
    String username = "";
    String password = "";
}

public class HomeScreenActivity extends AppCompatActivity {
    private static String[] retardedMessages = {"Ooopsie doopsie!",
                                                "Oh no! Mama mia!",
                                                "We'we vewy sowwy, please twy again.",
                                                "Holy matrimony Batman!",
                                                "Please forward your complaints to /dev/null",
                                                "It's the lizard people's fault!",
                                                "Yeah, whatever, it's not working, I dunno.",
                                                "It's not a bug, it's a feature, ok!",
                                                "Server machine broke"};

    private EditText    editAddr;
    private EditText    editUser;
    private EditText    editPassword;
    private AlertDialog dialog;
    private Random      rng = new Random();

    class StartBtnListener implements View.OnClickListener {
        private HomeScreenActivity parentActivity = null;

        StartBtnListener ( HomeScreenActivity parentActivity ) {
            this.parentActivity = parentActivity;
        }

        @Override
        public void onClick ( View view ) {
            parentActivity.handleLogin();
        }
    }

    private static class BasicErrorListener implements Response.ErrorListener {
        protected HomeScreenActivity parent;
        protected String             name;

        BasicErrorListener ( HomeScreenActivity parent, String name ) {
            this.parent = parent;
            this.name = name;
        }

        @Override
        public void onErrorResponse ( VolleyError error ) {
            this.parent.dialog.dismiss();

            String msg = this.parent.getErrorMsg(error);
            if ( !GlobalParameters.hasRetardedErrorMessages ) {
                this.parent.showInSnackbar("In '" + this.name + "' request" + "\n" + msg);
            }
            else {
                this.parent.showInSnackbar(this.parent.getRetardedErrorMsg());
            }
            Timber.v(msg);
        }
    }

    private static class PostUsersLoginListener extends BasicErrorListener implements Response.Listener<Void> {
        PostUsersLoginListener ( HomeScreenActivity parent ) {
            super(parent, "POST /users/login");
        }

        @Override
        public void onResponse ( Void result ) {
            this.parent.saveInputs(this.parent.getTextInputs());

            this.parent.dialog.setMessage("Sending basic config request...");
            GetConfigBasicListener nextListener = new GetConfigBasicListener(this.parent);
            RestHttpWrapper.getInstance().sendGetConfigBasic(nextListener, nextListener);
        }
    }

    private static class GetConfigBasicListener extends BasicErrorListener implements Response.Listener<JSONObject> {
        GetConfigBasicListener ( HomeScreenActivity parent ) {
            super(parent, "GET /config/basic");
        }

        @Override
        public void onResponse ( JSONObject result ) {
            try {
                GlobalParameters.udpPort = result.getInt("otherPort");
                GlobalParameters.layoutName = result.getString("layout");
                GlobalParameters.mapName = result.getString("map");
            }
            catch ( JSONException e ) {
                this.parent.dialog.dismiss();
                this.parent.showInSnackbar("In '" + this.name + "' request" + "\n" + "Wrong JSON response");
                return;
            }

            this.parent.dialog.setMessage("Sending rocket config request...");
            GetConfigRocketsListener nextListener = new GetConfigRocketsListener(this.parent);
            RestHttpWrapper.getInstance().sendGetConfigRockets(GlobalParameters.layoutName, nextListener, nextListener);
        }
    }

    private static class GetConfigRocketsListener extends BasicErrorListener implements Response.Listener<String> {
        GetConfigRocketsListener ( HomeScreenActivity parent ) {
            super(parent, "GET /config/rockets/" + GlobalParameters.layoutName);
        }

        @Override
        public void onResponse ( String result ) {
            // TODO: Pass the XML content to the XML parser.
            File cacheFile = new File(this.parent.getCacheDir(), GlobalParameters.layoutName);
            try {
                cacheFile.delete();
                boolean ok = cacheFile.createNewFile();
                if ( !ok ) {
                    this.parent.dialog.dismiss();
                    this.parent.showInSnackbar("Could not create file '" + GlobalParameters.layoutName + "' " +
                                               "in application cache");
                }
            }
            catch ( IOException e ) { }

            try {
                OutputStream outStrm = new FileOutputStream(cacheFile);
                outStrm.write(result.getBytes("UTF-8"));
                outStrm.flush();
            }
            catch (FileNotFoundException e) {
                this.parent.dialog.dismiss();
                this.parent.showInSnackbar("In '" + this.name + "' request" + "\n" +
                                           "ERROR : FileNotFoundException");
                return;
            }
            catch (UnsupportedEncodingException e) {
                this.parent.dialog.dismiss();
                this.parent.showInSnackbar("In '" + this.name + "' request" + "\n" +
                                           "ERROR : UnsupportedEncodingException");
                return;
            }
            catch (IOException e) {
                this.parent.dialog.dismiss();
                this.parent.showInSnackbar("In '" + this.name + "' request" + "\n" +
                                           "ERROR : IOException");
                return;
            }

            this.parent.dialog.setMessage("Sending CAN SID enum request...");
            GetConfigCanSidListener nextListener = new GetConfigCanSidListener(this.parent);
            RestHttpWrapper.getInstance().sendGetConfigCanSid(nextListener, nextListener);
        }
    }

    private static class GetConfigCanSidListener extends BasicErrorListener implements Response.Listener<JSONObject> {
        GetConfigCanSidListener ( HomeScreenActivity parent ) {
            super(parent, "GET /config/canSid");
        }

        @Override
        public void onResponse ( JSONObject result ) {
            Map<String, Object> canSid = null;

            try {
                canSid = JsonHelper.toMap(result);
            } catch (JSONException e) {
                Timber.e(e.getMessage());
            }

            if (canSid != null) {
                Map<Integer, String> inverseMap = new HashMap<>();
                for (Map.Entry<String, Object> entry : canSid.entrySet()) {
                    inverseMap.put((Integer) entry.getValue(), entry.getKey());
                }

                GlobalParameters.canSid = inverseMap;

            } else {
                Timber.e("Error");
            }

            this.parent.dialog.setMessage("Sending CAN data types enum request...");
            GetConfigCanDataTypesListener nextListener = new GetConfigCanDataTypesListener(this.parent);
            RestHttpWrapper.getInstance().sendGetConfigCanDataTypes(nextListener, nextListener);
        }
    }

    private static class GetConfigCanDataTypesListener extends BasicErrorListener implements Response.Listener<JSONObject> {
        GetConfigCanDataTypesListener ( HomeScreenActivity parent ) {
            super(parent, "GET /config/canDataTypes");
        }

        @Override
        public void onResponse ( JSONObject result ) {
            Map<String, Object> canDataTypes = null;

            try {
                canDataTypes = JsonHelper.toMap(result);
            } catch (JSONException e) {
                Timber.e(e.getMessage());
            }

            if (canDataTypes != null) {
                Map<String, String> map = new HashMap<>();
                for (Map.Entry<String, Object> entry : canDataTypes.entrySet()) {
                    map.put(entry.getKey(), entry.getKey());
                }

                GlobalParameters.canDataTypes = map;
            } else {
                Timber.e("Error");
            }

            this.parent.dialog.setMessage("Sending CAN message data types association request");
            GetConfigCanMsgDataTypesListener nextListener = new GetConfigCanMsgDataTypesListener(this.parent);
            RestHttpWrapper.getInstance().sendGetConfigCanMsgDataTypes(nextListener, nextListener);
        }
    }

    private static class GetConfigCanMsgDataTypesListener extends BasicErrorListener implements Response.Listener<JSONObject> {
        GetConfigCanMsgDataTypesListener ( HomeScreenActivity parent ) {
            super(parent, "GET /config/canMsgDataTypes");
        }

        @Override
        public void onResponse ( JSONObject result ) {
            Map<String, Object> canMsgDataTypes = null;

            try {
                canMsgDataTypes = JsonHelper.toMap(result);
            } catch (JSONException e) {
                Timber.e(e.getMessage());
            }

            if (canMsgDataTypes != null) {
                Map<String, List<String>> map = new HashMap<>();
                for (Map.Entry<String, Object> entry : canMsgDataTypes.entrySet()) {
                    map.put(entry.getKey(), (List<String>)entry.getValue());
                }

                GlobalParameters.canMsgDataTypes = map;
            } else {
                Timber.e("Error");
            }

            this.parent.dialog.setMessage("Sending CAN module types enum request...");
            GetConfigCanModuleTypesListener nextListener = new GetConfigCanModuleTypesListener(this.parent);
            RestHttpWrapper.getInstance().sendGetConfigCanModuleTypes(nextListener, nextListener);
        }
    }

    private static class GetConfigCanModuleTypesListener extends BasicErrorListener implements Response.Listener<JSONObject> {
        GetConfigCanModuleTypesListener ( HomeScreenActivity parent ) {
            super(parent, "GET /config/canModuleTypes");
        }

        @Override
        public void onResponse ( JSONObject result ) {
            Map<String, Object> canModuleTypes = null;

            try {
                canModuleTypes = JsonHelper.toMap(result);
            } catch (JSONException e) {
                Timber.e(e.getMessage());
            }

            if (canModuleTypes != null) {
                Map<String, Integer> map = new HashMap<>();
                for (Map.Entry<String, Object> entry : canModuleTypes.entrySet()) {
                    map.put(entry.getKey(), (Integer) entry.getValue());
                }

                GlobalParameters.canModuleTypes = map;
            } else {
                Timber.e("Error");
            }

            this.parent.dialog.setTitle("All Good!");
            this.parent.dialog.setMessage("Switching to main activity...");
            this.parent.switchToMainActivity();
        }
    }


    public void handleLogin ( ) {
        HomeScreenInputs inputs = this.getTextInputs();

        this.sendLoginRequest(inputs);
    }

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.editAddr = (EditText)findViewById(R.id.editAddr);
        this.editUser = (EditText)findViewById(R.id.editUser);
        this.editPassword = (EditText)findViewById(R.id.editPassword);

        // TODO: Setup the UI.
        Button btnStart = (Button)findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new StartBtnListener(this));

        CookieHandler.setDefault(new CookieManager());
        RestHttpWrapper.getInstance().setup(this.getApplicationContext());

        HomeScreenInputs cachedInputs = this.loadCachedInputs();
        this.setTextInputs(cachedInputs);
    }

    private void sendLoginRequest ( HomeScreenInputs inputs ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeScreenActivity.this);
        this.dialog = builder.create();
        this.dialog.setCancelable(false);
        this.dialog.setTitle("Authentication and Configuration from Server");
        this.dialog.setMessage("Sending login request...");
        this.dialog.show();

        RestHttpWrapper restWrapper = RestHttpWrapper.getInstance();
        restWrapper.setLoginInfo(inputs.serverAddress, 80, inputs.username, inputs.password);
        PostUsersLoginListener listener = new PostUsersLoginListener(this);
        restWrapper.sendPostUsersLogin(listener, listener);
    }

    private String getErrorMsg ( VolleyError error ) {
        if ( error instanceof TimeoutError || error instanceof NoConnectionError ) {
            //This indicates that the request has either time out or there is no connection
            return "ERROR : TimeoutError or NoConnectionError";
        }
        else if ( error instanceof NetworkError ) {
            //Indicates that there was network error while performing the request
            return "ERROR : NetworkError";
        }
        else if ( error.networkResponse != null ) {
            return "ERROR : HTTP " + error.networkResponse.statusCode;
        }
        else {
            return "ERROR : N/A";
        }
    }

    private String getRetardedErrorMsg ( ) {
        int index = this.rng.nextInt(retardedMessages.length);
        return retardedMessages[index];
    }

    private HomeScreenInputs loadCachedInputs ( ) {
        HomeScreenInputs result = new HomeScreenInputs();

        SharedPreferences prefs = this.getPreferences(MODE_PRIVATE);
        if ( prefs.contains("serverAddress") ) {
            result.serverAddress = prefs.getString("serverAddress", "");
            result.username = prefs.getString("username", "");
            result.password = prefs.getString("password", "");
        }

        return result;
    }

    private void saveInputs ( HomeScreenInputs inputs ) {
        SharedPreferences prefs = this.getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("serverAddress", inputs.serverAddress);
        editor.putString("username", inputs.username);
        editor.putString("password", inputs.password);
        editor.apply();
    }

    private void setTextInputs ( HomeScreenInputs inputs ) {
        this.editAddr.setText(inputs.serverAddress);
        this.editUser.setText(inputs.username);
        this.editPassword.setText(inputs.password);
    }

    private HomeScreenInputs getTextInputs ( ) {
        HomeScreenInputs result = new HomeScreenInputs();
        result.serverAddress = this.editAddr.getText().toString();
        result.username = this.editUser.getText().toString();
        result.password = this.editPassword.getText().toString();
        return result;
    }

    private void showInSnackbar ( String msg ) {
        View thisView = findViewById(android.R.id.content);
        Snackbar bar = Snackbar.make(thisView, msg, Snackbar.LENGTH_LONG);
        TextView snackbarTextView = (TextView)bar.getView().findViewById(R.id.snackbar_text);
        snackbarTextView.setMaxLines(3);
        snackbarTextView.setTextSize(16);
        bar.show();
    }

    private void switchToMainActivity ( ) {
        Intent intent = new Intent(this, MainActivity.class);
        this.startActivity(intent);
    }
}
