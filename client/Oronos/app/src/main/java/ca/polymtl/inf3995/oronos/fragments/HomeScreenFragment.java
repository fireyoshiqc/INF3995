package ca.polymtl.inf3995.oronos.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import ca.polymtl.inf3995.oronos.activities.OronosActivity;
import ca.polymtl.inf3995.oronos.services.RestHttpWrapper;
import ca.polymtl.inf3995.oronos.utils.GlobalParameters;
import ca.polymtl.inf3995.oronos.utils.JsonHelper;
import timber.log.Timber;

public class HomeScreenFragment extends Fragment {
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
    private View fragmentView;

    /**
     * This method is responsible of parsing and verifying the user input in the fields of the log in
     * questionnaire. If something is missing or the IP address format is invalid, a error message
     * will be displayed so the user can correct is input. Else, the login request will be send to
     * the server by calling the sendLoginRequest(inputs) private method.
     */
    public void handleLogin() {
        HomeScreenFragment.HomeScreenInputs inputs = this.getTextInputs();
        boolean valid = true;
        if (inputs.username.equalsIgnoreCase("")) {
            ((TextInputLayout) getView().findViewById(R.id.userField)).setError("Username is required !");
            valid = false;
        } else {
            ((TextInputLayout) getView().findViewById(R.id.userField)).setError(null);
        }
        if (inputs.password.equalsIgnoreCase("")) {
            ((TextInputLayout) getView().findViewById(R.id.passwordField)).setError("Password is required !");
            valid = false;
        } else {
            ((TextInputLayout) getView().findViewById(R.id.passwordField)).setError(null);
        }
        if (inputs.serverAddress.equalsIgnoreCase("")) {
            ((TextInputLayout) getView().findViewById(R.id.addrField)).setError(("Server IP address is required !"));
            valid = false;
        } else if (!inputs.serverAddress.matches("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$")) {
            ((TextInputLayout) getView().findViewById(R.id.addrField)).setError(("Server IP address format is invalid !"));
            valid = false;
        } else {
            ((TextInputLayout) getView().findViewById(R.id.addrField)).setError(null);
        }
        if (valid) {
            this.sendLoginRequest(inputs);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (fragmentView != null) {
            return fragmentView;
        }
        fragmentView = inflater.inflate(R.layout.activity_home_screen, container, false);
        this.editAddr = fragmentView.findViewById(R.id.editAddr);
        this.editUser = fragmentView.findViewById(R.id.editUser);
        this.editPassword = fragmentView.findViewById(R.id.editPassword);
        this.saveUserAddr = fragmentView.findViewById(R.id.userAddrCheckbox);
        this.savePassword = fragmentView.findViewById(R.id.passwordCheckbox);

        if (GlobalParameters.hasRetardedErrorMessages) {
            ((ImageView) fragmentView.findViewById(R.id.imgOronosLogo)).setImageResource(R.drawable.oronos);
        }

        Button btnStart = fragmentView.findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new HomeScreenFragment.StartBtnListener(this));

        saveUserAddr.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                savePassword.setEnabled(b);
                if (!b) {
                    savePassword.setChecked(false);
                }
            }
        });

        return fragmentView;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HomeScreenFragment.PostUsersLogoutListener listener = new HomeScreenFragment.PostUsersLogoutListener(getActivity().getApplicationContext());
        RestHttpWrapper.getInstance().sendPostUsersLogout(listener, listener);
    }

    /**
     * This method is responsible of sending the log in request to the server using the inputs
     * provided in arguments.
     *
     * @param inputs HomeScreenInputs composed of three strings: username, password and IP
     *               serverAddress.
     */
    private void sendLoginRequest(HomeScreenFragment.HomeScreenInputs inputs) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        this.dialog = builder.create();
        this.dialog.setCancelable(false);
        this.dialog.setTitle("Authentication and Configuration from Server");
        this.dialog.setMessage("Sending login request...");
        this.dialog.show();

        RestHttpWrapper restWrapper = RestHttpWrapper.getInstance();
        restWrapper.setLoginInfo(inputs.serverAddress, 80, inputs.username, inputs.password);
        HomeScreenFragment.PostUsersLoginListener listener = new HomeScreenFragment.PostUsersLoginListener(this);
        restWrapper.sendPostUsersLogin(listener, listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onResume() {
        super.onResume();
        ((OronosActivity) getActivity()).hideToolbar();

        HomeScreenFragment.HomeScreenInputs cachedInputs = this.loadCachedInputs();
        this.setTextInputs(cachedInputs);

        if (GlobalParameters.serverAddress != null && !GlobalParameters.serverAddress.isEmpty())
            RestHttpWrapper.getInstance().sendPostUsersLogout(null, null);
        GlobalParameters.serverAddress = null;

        if (this.dialog != null && this.dialog.isShowing())
            this.dialog.dismiss();
    }

    /**
     * This method unwraps a REST error by transforming a VolleyError code into a legible string.
     *
     * @param error VolleyError to be decrypted.
     * @return string containing error in a legible form.
     */
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

    /**
     * This method chooses a retarded error msg from the table of dummy error msgs.
     *
     * @return string containing dummy error in a legible form.
     */
    private String getRetardedErrorMsg() {
        int index = this.rng.nextInt(retardedMessages.length);
        return retardedMessages[index];
    }

    /**
     * This method loads the previous user inputs according to the user preferences. Every input the
     * user wants to be remembered by the app is loaded.
     *
     * @return HomeScreenInputs containing three strings: username, password, IP serverAddress.
     */
    private HomeScreenFragment.HomeScreenInputs loadCachedInputs() {
        HomeScreenFragment.HomeScreenInputs result = new HomeScreenFragment.HomeScreenInputs();
        SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        result.serverAddress = prefs.getString("serverAddress", "");
        result.username = prefs.getString("username", "");
        result.password = prefs.getString("password", "");
        this.saveUserAddr.setChecked(prefs.getBoolean("userAddrChecked", false));
        this.savePassword.setChecked(prefs.getBoolean("passwordChecked", false));
        this.savePassword.setEnabled(this.saveUserAddr.isChecked());

        return result;
    }

    /**
     * This method saves the current user inputs according to the user preferences. Every input the
     * user wants to be remembered by the app is saved.
     *
     * @param inputs HomeScreenInputs containing three strings: username, password, IP serverAddress.
     */
    private void saveInputs(HomeScreenFragment.HomeScreenInputs inputs) {
        SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
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

    /**
     * This method reads the input fields of the log in questionnaire and save each string into the
     * corresponding HomeScreenInputs string.
     *
     * @return HomeScreenInputs containing three strings: username, password, IP serverAddress.
     */
    private HomeScreenFragment.HomeScreenInputs getTextInputs() {
        HomeScreenFragment.HomeScreenInputs result = new HomeScreenFragment.HomeScreenInputs();
        result.serverAddress = this.editAddr.getText().toString();
        result.username = this.editUser.getText().toString();
        result.password = this.editPassword.getText().toString();
        return result;
    }

    /**
     * This method writes the inputs passed in argument into the corresponding fields of the log in
     * questionnaire.
     *
     * @param inputs HomeScreenInputs containing three strings: username, password, IP serverAddress.
     */
    private void setTextInputs(HomeScreenFragment.HomeScreenInputs inputs) {
        this.editAddr.setText(inputs.serverAddress);
        this.editUser.setText(inputs.username);
        this.editPassword.setText(inputs.password);
    }

    /**
     * This method displays the argument string in a snackbar.
     *
     * @param msg to be shown in a snackbar.
     */
    private void showInSnackbar(String msg) {
        View thisView = getView().findViewById(R.id.coordinatorLayout);
        this.snackbar = Snackbar.make(thisView, msg, Snackbar.LENGTH_INDEFINITE);
        TextView snackbarTextView = this.snackbar.getView().findViewById(R.id.snackbar_text);
        snackbarTextView.setMaxLines(3);
        snackbarTextView.setTextSize(16);
        this.snackbar.show();
    }

    /**
     * This method starts the Main Activity.
     */
    private void switchToTelemetryFragment() {
        ((OronosActivity) getActivity()).setTelemetryFragment(new TelemetryFragment());
        ((OronosActivity) getActivity()).switchToMainActivity();
    }


    /**
     * Listener for the error responses that can occur when trying to log in the user.
     */
    private static class BasicErrorListener implements Response.ErrorListener {
        protected HomeScreenFragment parent;
        protected String name;

        BasicErrorListener(HomeScreenFragment parent, String name) {
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

    /**
     * Listener for the response to a user log in request. If the log in succeeds, the response of
     * the server will trigger the next request to obtain the basic configuration parameters such as
     * the udp port or the xml layout name.
     */
    private static class PostUsersLoginListener extends HomeScreenFragment.BasicErrorListener implements Response.Listener<Void> {
        PostUsersLoginListener(HomeScreenFragment parent) {
            super(parent, "POST /users/login");
        }

        @Override
        public void onResponse(Void result) {
            if (this.parent.snackbar != null)
                this.parent.snackbar.dismiss();
            this.parent.saveInputs(this.parent.getTextInputs());

            GlobalParameters.serverAddress = this.parent.getTextInputs().serverAddress;

            this.parent.dialog.setMessage("Sending basic config request...");
            HomeScreenFragment.GetConfigBasicListener nextListener = new HomeScreenFragment.GetConfigBasicListener(this.parent);
            RestHttpWrapper.getInstance().sendGetConfigBasic(nextListener, nextListener);
        }
    }

    /**
     * Listener for the response to a get basic configuration request. If the get succeeds, the
     * response of the server will trigger the next request to obtain the xml layout of a specific
     * rocket.
     */
    private static class GetConfigBasicListener extends HomeScreenFragment.BasicErrorListener implements Response.Listener<JSONObject> {
        GetConfigBasicListener(HomeScreenFragment parent) {
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
            HomeScreenFragment.GetConfigRocketsListener nextListener = new HomeScreenFragment.GetConfigRocketsListener(this.parent);
            RestHttpWrapper.getInstance().sendGetConfigRockets(GlobalParameters.layoutName, nextListener, nextListener);
        }
    }

    /**
     * Listener for the response to a get rocket configuration request. If the get succeeds, the
     * xml rocket file in cache of the android app will be replaced by the new rocket config. Also,
     * the response of the server will trigger the next request to obtain a can sid map of a certain
     * rocket.
     */
    private static class GetConfigRocketsListener extends HomeScreenFragment.BasicErrorListener implements Response.Listener<String> {
        GetConfigRocketsListener(HomeScreenFragment parent) {
            super(parent, "GET /config/rockets/" + GlobalParameters.layoutName);
        }

        @Override
        public void onResponse(String result) {
            File cacheFile = new File(this.parent.getActivity().getCacheDir(), GlobalParameters.layoutName);
            try {
                boolean isDeleted = cacheFile.delete();
                if (!isDeleted) {
                    Timber.w("Cache file unsuccessfully deleted.");
                }
                if (!cacheFile.createNewFile()) {
                    this.parent.dialog.dismiss();
                    this.parent.showInSnackbar("Could not create file '" + GlobalParameters.layoutName + "' " +
                            "in application cache");
                }
            } catch (IOException e) {
                Timber.e("Cachefile for Home Screen cannot be deleted or created correctly.");
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
            HomeScreenFragment.GetConfigCanSidListener nextListener = new HomeScreenFragment.GetConfigCanSidListener(this.parent);
            RestHttpWrapper.getInstance().sendGetConfigCanSid(nextListener, nextListener);
        }
    }

    /**
     * Listener for the response to a get can sid configuration request. If the get succeeds, the
     * response of the server will trigger the next request to obtain a can data types map of a certain
     * rocket.
     */
    private static class GetConfigCanSidListener extends HomeScreenFragment.BasicErrorListener implements Response.Listener<JSONObject> {
        GetConfigCanSidListener(HomeScreenFragment parent) {
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
            HomeScreenFragment.GetConfigCanDataTypesListener nextListener = new HomeScreenFragment.GetConfigCanDataTypesListener(this.parent);
            RestHttpWrapper.getInstance().sendGetConfigCanDataTypes(nextListener, nextListener);
        }
    }

    /**
     * Listener for the response to a get can data types request. If the get succeeds, the
     * response of the server will trigger the next request to obtain a can msg data types map of a
     * certain rocket.
     */
    private static class GetConfigCanDataTypesListener extends HomeScreenFragment.BasicErrorListener implements Response.Listener<JSONObject> {
        GetConfigCanDataTypesListener(HomeScreenFragment parent) {
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
            HomeScreenFragment.GetConfigCanMsgDataTypesListener nextListener = new HomeScreenFragment.GetConfigCanMsgDataTypesListener(this.parent);
            RestHttpWrapper.getInstance().sendGetConfigCanMsgDataTypes(nextListener, nextListener);
        }
    }

    /**
     * Listener for the response to a get can msg data types request. If the get succeeds, the
     * response of the server will trigger the next request to obtain can module types map of a
     * certain rocket.
     */
    private static class GetConfigCanMsgDataTypesListener extends HomeScreenFragment.BasicErrorListener implements Response.Listener<JSONObject> {
        GetConfigCanMsgDataTypesListener(HomeScreenFragment parent) {
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
            HomeScreenFragment.GetConfigCanModuleTypesListener nextListener = new HomeScreenFragment.GetConfigCanModuleTypesListener(this.parent);
            RestHttpWrapper.getInstance().sendGetConfigCanModuleTypes(nextListener, nextListener);
        }
    }

    /**
     * Listener for the response to a get module types request. If the get succeeds, the
     * response of the server will trigger the next request to obtain the server timeout delay
     */
    private static class GetConfigCanModuleTypesListener extends HomeScreenFragment.BasicErrorListener implements Response.Listener<JSONObject> {
        GetConfigCanModuleTypesListener(HomeScreenFragment parent) {
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

            this.parent.dialog.setMessage("Sending server timeout delay request...");
            HomeScreenFragment.GetConfigTimeoutListener nextListener = new HomeScreenFragment.GetConfigTimeoutListener(this.parent);
            RestHttpWrapper.getInstance().sendGetConfigTimeout(nextListener, nextListener);
        }
    }

    /**
     * Listener for the response to a get module types request. If the get succeeds, the
     * response of the server will trigger the switch to the main activity.
     */
    private static class GetConfigTimeoutListener extends HomeScreenFragment.BasicErrorListener implements Response.Listener<JSONObject> {
        GetConfigTimeoutListener(HomeScreenFragment parent) {
            super(parent, "GET /config/timeout");
        }

        @Override
        public void onResponse(JSONObject result) {
            Double timeoutMinutes = null;

            try {
                timeoutMinutes = (Double) JsonHelper.toMap(result).get("timeoutMinutes");
            } catch (JSONException e) {
                Timber.e(e.getMessage());
            }

            if (timeoutMinutes != null) {
                GlobalParameters.serverTimeout = timeoutMinutes * 60.0;
            } else {
                Timber.e("Error");
            }

            this.parent.dialog.setTitle("All Good!");
            this.parent.dialog.setMessage("Switching to main activity...");
            this.parent.switchToTelemetryFragment();
            this.parent.dialog.dismiss();
        }
    }

    private static class PostUsersLogoutListener implements Response.ErrorListener, Response.Listener<Void> {
        Context context;

        PostUsersLogoutListener(Context context) {
            this.context = context;
        }

        @Override
        public void onResponse(Void result) {
            this.resetCookieHandler();
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            this.resetCookieHandler();
        }

        private void resetCookieHandler() {
            GlobalParameters.serverAddress = null;
            CookieHandler.setDefault(new CookieManager());
        }
    }

    /**
     * Listener for the click on the button responsible of trying to log in the user.
     */
    class StartBtnListener implements View.OnClickListener {
        private HomeScreenFragment parentActivity;

        StartBtnListener(HomeScreenFragment parentActivity) {
            this.parentActivity = parentActivity;
        }

        @Override
        public void onClick(View view) {
            parentActivity.handleLogin();
        }
    }

    /**
     * Container for the user input.
     */
    class HomeScreenInputs {
        String serverAddress = "";
        String username = "";
        String password = "";
    }
}
