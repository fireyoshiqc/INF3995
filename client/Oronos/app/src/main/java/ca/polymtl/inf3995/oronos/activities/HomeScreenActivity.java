package ca.polymtl.inf3995.oronos.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import ca.polymtl.inf3995.oronos.R;


class HomeScreenInputs {
    public String serverAddress = "";
    public String username = "";
    public String password = "";
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
    private EditText editAddr = null;
    private EditText editUser = null;
    private EditText editPassword = null;

    private enum LoginRequestResult {
        OK,
        UNAUTHORIZED,
        CONNECTION_ERROR
    }

    public void handleLogin ( ) {
        HomeScreenInputs inputs = this.getTextInputs();

        LoginRequestResult loginResult = this.sendLoginRequest(inputs);

        switch ( loginResult ) {
            case OK : {
                // TODO: Switch to main activity.
                this.showInSnackbar("All good!");

                this.saveInputs(inputs);

                break;
            }
            case UNAUTHORIZED : {
                // TODO: Proper error highlighting of the username and password fields.
                this.showInSnackbar("ERROR: Incorrect username or password");

                break;
            }
            case CONNECTION_ERROR : {
                // TODO: Proper error highlighting of the IP field.
                this.showInSnackbar("ERROR: Unable to connect to server at IP " + inputs.serverAddress);

                break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        btnStart.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange ( View view, boolean hasFocus ) {
                if ( hasFocus ) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                }
            }
        });

        HomeScreenInputs cachedInputs = this.loadCachedInputs();
        this.setTextInputs(cachedInputs);
    }

    private LoginRequestResult sendLoginRequest ( HomeScreenInputs inputs ) {
        // TODO: Try connecting to the server

        if ( !inputs.serverAddress.equals("127.0.0.1") )
            return LoginRequestResult.CONNECTION_ERROR;

        if ( !inputs.username.equals("foo") || !inputs.password.equals("password1234") )
            return LoginRequestResult.UNAUTHORIZED;

        return LoginRequestResult.OK;
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
        editor.commit();
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
