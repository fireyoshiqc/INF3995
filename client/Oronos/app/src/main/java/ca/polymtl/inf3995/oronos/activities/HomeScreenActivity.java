package ca.polymtl.inf3995.oronos.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
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
import ca.polymtl.inf3995.oronos.utils.PermissionsUtil;
import timber.log.Timber;

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

    private EditText editAddr;
    private EditText editUser;
    private EditText editPassword;
    private CheckBox saveUserAddr;
    private CheckBox savePassword;
    private AlertDialog dialog;
    private Snackbar snackbar;
    private Random rng = new Random();
    private Snackbar warningBar;

    class StartBtnListener implements View.OnClickListener {
        private HomeScreenActivity parentActivity = null;

        StartBtnListener(HomeScreenActivity parentActivity) {
            this.parentActivity = parentActivity;
        }

        @Override
        public void onClick(View view) {
            parentActivity.handleLogin();
        }
    }

    private static class BasicErrorListener implements Response.ErrorListener {
        protected HomeScreenActivity parent;
        protected String name;

        BasicErrorListener(HomeScreenActivity parent, String name) {
            this.parent = parent;
            this.name = name;
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            this.parent.dialog.dismiss();

            String msg = this.parent.getErrorMsg(error);
            if (!GlobalParameters.hasRetardedErrorMessages) {
                this.parent.showInSnackbar("In '" + this.name + "' request" + "\n" + msg);
            } else {
                this.parent.showInSnackbar(this.parent.getRetardedErrorMsg());
            }
            Timber.v(msg);
        }
    }

    private static class PostUsersLoginListener extends BasicErrorListener implements Response.Listener<Void> {
        PostUsersLoginListener(HomeScreenActivity parent) {
            super(parent, "POST /users/login");
        }

        @Override
        public void onResponse(Void result) {
            if (this.parent.snackbar != null)
                this.parent.snackbar.dismiss();
            this.parent.saveInputs(this.parent.getTextInputs());

            this.parent.dialog.setMessage("Sending basic config request...");
            GetConfigBasicListener nextListener = new GetConfigBasicListener(this.parent);
            RestHttpWrapper.getInstance().sendGetConfigBasic(nextListener, nextListener);
        }
    }

    private static class GetConfigBasicListener extends BasicErrorListener implements Response.Listener<JSONObject> {
        GetConfigBasicListener(HomeScreenActivity parent) {
            super(parent, "GET /config/basic");
        }

        @Override
        public void onResponse(JSONObject result) {
            try {
                GlobalParameters.udpPort = result.getInt("otherPort");
                GlobalParameters.layoutName = result.getString("layout");
                GlobalParameters.mapName = result.getString("map");
            } catch (JSONException e) {
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
        GetConfigRocketsListener(HomeScreenActivity parent) {
            super(parent, "GET /config/rockets/" + GlobalParameters.layoutName);
        }

        @Override
        public void onResponse(String result) {
            File cacheFile = new File(this.parent.getCacheDir(), GlobalParameters.layoutName);
            try {
                cacheFile.delete();
                if (!cacheFile.createNewFile()) {
                    this.parent.dialog.dismiss();
                    this.parent.showInSnackbar("Could not create file '" + GlobalParameters.layoutName + "' " +
                            "in application cache");
                }
            } catch (IOException e) {
            }

            try {
                OutputStream outStrm = new FileOutputStream(cacheFile);
                outStrm.write(result.getBytes("UTF-8"));
                outStrm.flush();
            } catch (FileNotFoundException e) {
                this.parent.dialog.dismiss();
                this.parent.showInSnackbar("In '" + this.name + "' request" + "\n" +
                        "ERROR : FileNotFoundException");
                return;
            } catch (UnsupportedEncodingException e) {
                this.parent.dialog.dismiss();
                this.parent.showInSnackbar("In '" + this.name + "' request" + "\n" +
                        "ERROR : UnsupportedEncodingException");
                return;
            } catch (IOException e) {
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
        GetConfigCanSidListener(HomeScreenActivity parent) {
            super(parent, "GET /config/canSid");
        }

        @Override
        public void onResponse(JSONObject result) {
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
        GetConfigCanDataTypesListener(HomeScreenActivity parent) {
            super(parent, "GET /config/canDataTypes");
        }

        @Override
        public void onResponse(JSONObject result) {
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
        GetConfigCanMsgDataTypesListener(HomeScreenActivity parent) {
            super(parent, "GET /config/canMsgDataTypes");
        }

        @Override
        public void onResponse(JSONObject result) {
            Map<String, Object> canMsgDataTypes = null;

            try {
                canMsgDataTypes = JsonHelper.toMap(result);
            } catch (JSONException e) {
                Timber.e(e.getMessage());
            }

            if (canMsgDataTypes != null) {
                Map<String, List<String>> map = new HashMap<>();
                for (Map.Entry<String, Object> entry : canMsgDataTypes.entrySet()) {
                    map.put(entry.getKey(), (List<String>) entry.getValue());
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
        GetConfigCanModuleTypesListener(HomeScreenActivity parent) {
            super(parent, "GET /config/canModuleTypes");
        }

        @Override
        public void onResponse(JSONObject result) {
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


    public void handleLogin() {
        HomeScreenInputs inputs = this.getTextInputs();
        boolean valid = true;
        if (inputs.username.equalsIgnoreCase("")) {
            ((TextInputLayout) findViewById(R.id.userField)).setError("Username is required !");
            valid = false;
        } else {
            ((TextInputLayout) findViewById(R.id.userField)).setError(null);
        }
        if (inputs.password.equalsIgnoreCase("")) {
            ((TextInputLayout) findViewById(R.id.passwordField)).setError("Password is required !");
            valid = false;
        } else {
            ((TextInputLayout) findViewById(R.id.passwordField)).setError(null);
        }
        if (inputs.serverAddress.equalsIgnoreCase("")) {
            ((TextInputLayout) findViewById(R.id.addrField)).setError(("Server IP address is required !"));
            valid = false;
        } else if (!inputs.serverAddress.matches("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$")) {
            ((TextInputLayout) findViewById(R.id.addrField)).setError(("Server IP address format is invalid !"));
            valid = false;
        } else {
            ((TextInputLayout) findViewById(R.id.addrField)).setError(null);
        }
        if (valid) {
            this.sendLoginRequest(inputs);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        if (!PermissionsUtil.hasPermissions(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            warningBar = Snackbar.make(findViewById(android.R.id.content), "Write to external memory permission is required for using this app.", Snackbar.LENGTH_INDEFINITE);
            warningBar.setAction("ENABLE", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityCompat.requestPermissions((Activity) HomeScreenActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
            }).show();
        } else {
            grantPermissions();
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.editAddr = findViewById(R.id.editAddr);
        this.editUser = findViewById(R.id.editUser);
        this.editPassword = findViewById(R.id.editPassword);
        this.saveUserAddr = findViewById(R.id.userAddrCheckbox);
        this.savePassword = findViewById(R.id.passwordCheckbox);

        if (GlobalParameters.hasRetardedErrorMessages) {
            ((ImageView) findViewById(R.id.imgOronosLogo)).setImageResource(R.drawable.oronos);
        }

        Button btnStart = findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new StartBtnListener(this));

        saveUserAddr.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                savePassword.setEnabled(b);
                if (!b) {
                    savePassword.setChecked(false);
                }
            }
        });

        CookieHandler.setDefault(new CookieManager());
        RestHttpWrapper.getInstance().setup(this.getApplicationContext());

        HomeScreenInputs cachedInputs = this.loadCachedInputs();
        this.setTextInputs(cachedInputs);
    }

    private void sendLoginRequest(HomeScreenInputs inputs) {
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

    @Override
    protected void onResume() {
        super.onResume();

        if (this.dialog != null && this.dialog.isShowing())
            this.dialog.dismiss();
    }

    private String getErrorMsg(VolleyError error) {
        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
            //This indicates that the request has either time out or there is no connection
            return "ERROR : TimeoutError or NoConnectionError";
        } else if (error instanceof NetworkError) {
            //Indicates that there was network error while performing the request
            return "ERROR : NetworkError";
        } else if (error.networkResponse != null) {
            return "ERROR : HTTP " + error.networkResponse.statusCode;
        } else {
            return "ERROR : N/A";
        }
    }

    private String getRetardedErrorMsg() {
        int index = this.rng.nextInt(retardedMessages.length);
        return retardedMessages[index];
    }

    private HomeScreenInputs loadCachedInputs() {
        HomeScreenInputs result = new HomeScreenInputs();
        SharedPreferences prefs = this.getPreferences(MODE_PRIVATE);
        result.serverAddress = prefs.getString("serverAddress", "");
        result.username = prefs.getString("username", "");
        result.password = prefs.getString("password", "");
        this.saveUserAddr.setChecked(prefs.getBoolean("userAddrChecked", false));
        this.savePassword.setChecked(prefs.getBoolean("passwordChecked", false));
        this.savePassword.setEnabled(this.saveUserAddr.isChecked());

        return result;
    }

    private void saveInputs(HomeScreenInputs inputs) {
        SharedPreferences prefs = this.getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        if (this.saveUserAddr.isChecked()) {
            editor.putString("serverAddress", inputs.serverAddress);
            editor.putString("username", inputs.username);
        } else {
            if (prefs.contains("serverAddress") && prefs.contains("username")) {
                editor.remove("serverAddress");
                editor.remove("username");
            }
        }
        if (this.savePassword.isChecked()) {
            editor.putString("password", inputs.password);
        } else {
            if (prefs.contains("password")) {
                editor.remove("password");
            }
        }
        editor.putBoolean("userAddrChecked", this.saveUserAddr.isChecked());
        editor.putBoolean("passwordChecked", this.savePassword.isChecked());
        editor.apply();
    }

    private void setTextInputs(HomeScreenInputs inputs) {
        this.editAddr.setText(inputs.serverAddress);
        this.editUser.setText(inputs.username);
        this.editPassword.setText(inputs.password);
    }

    private HomeScreenInputs getTextInputs() {
        HomeScreenInputs result = new HomeScreenInputs();
        result.serverAddress = this.editAddr.getText().toString();
        result.username = this.editUser.getText().toString();
        result.password = this.editPassword.getText().toString();
        return result;
    }

    private void showInSnackbar(String msg) {
        View thisView = findViewById(R.id.coordinatorLayout);
        this.snackbar = Snackbar.make(thisView, msg, Snackbar.LENGTH_INDEFINITE);
        TextView snackbarTextView = this.snackbar.getView().findViewById(R.id.snackbar_text);
        snackbarTextView.setMaxLines(3);
        snackbarTextView.setTextSize(16);
        this.snackbar.show();
    }

    private void switchToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        this.startActivity(intent);
    }

    class HomeScreenInputs {
        String serverAddress = "";
        String username = "";
        String password = "";
    }

    public void grantPermissions() {
//        if (locationManager == null) {
//            locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
//        }
//        if (locationManager != null) {
//            enableLocationUpdates();
//            registerSensors();
//            startSensorTask();
//            setupWebGLRenderer();
//        }
        if (warningBar != null && warningBar.isShown()) {
            warningBar.dismiss();
        }
    }

}
