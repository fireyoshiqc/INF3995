package ca.polymtl.inf3995.oronos.activities;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import java.net.CookieHandler;
import java.net.CookieManager;

import ca.polymtl.inf3995.oronos.R;
import ca.polymtl.inf3995.oronos.services.RestHttpWrapper;
import timber.log.Timber;


class HomeScreenInputs {
    String serverAddress = "";
    String username = "";
    String password = "";
}

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

public class HomeScreenActivity extends AppCompatActivity {
    private EditText    editAddr = null;
    private EditText    editUser = null;
    private EditText    editPassword = null;
    private AlertDialog dialog = null;

    private static class LoginListener implements Response.Listener<Void>, Response.ErrorListener {
        private HomeScreenActivity parent;

        LoginListener ( HomeScreenActivity parent ) {
            this.parent = parent;
        }

        @Override
        public void onResponse ( Void result ) {
            this.parent.dialog.dismiss();
            this.parent.showInSnackbar("Awww yeah!");
            this.parent.saveInputs(this.parent.getTextInputs());
        }

        @Override
        public void onErrorResponse ( VolleyError error ) {
            this.parent.dialog.dismiss();

            if ( error instanceof TimeoutError || error instanceof NoConnectionError ) {
                //This indicates that the request has either time out or there is no connection
                this.parent.showInSnackbar("ERROR : TimeoutError or NoConnectionError");
                Timber.v("TimeoutError or NoConnectionError");
            }
            else if ( error instanceof NetworkError ) {
                //Indicates that there was network error while performing the request
                this.parent.showInSnackbar("ERROR : NetworkError");
                Timber.v("NetworkError");
            }
            else {
                this.parent.showInSnackbar("ERROR : HTTP " + error.networkResponse.statusCode);
            }
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
        this.dialog.setTitle("Connecting to server");
        this.dialog.setMessage("Sending login request...");
        this.dialog.show();

        RestHttpWrapper restWrapper = RestHttpWrapper.getInstance();
        restWrapper.setLoginInfo(inputs.serverAddress, 80, inputs.username, inputs.password);
        LoginListener listener = new LoginListener(this);
        restWrapper.postUsersLogin(listener, listener);
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
}
