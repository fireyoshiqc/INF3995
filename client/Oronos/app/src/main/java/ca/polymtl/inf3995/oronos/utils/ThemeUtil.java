package ca.polymtl.inf3995.oronos.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import ca.polymtl.inf3995.oronos.R;

import static android.content.Context.MODE_PRIVATE;

/**
 * <h1>Theme Util</h1>
 * Theme related methods
 *
 * @author Fabrice Charbonneau
 * @version 0.0
 * @since 2018-04-12
 */

public class ThemeUtil {
    private final String THEME = "theme";

    private static ThemeUtil instance;
    Context context;

    private SharedPreferences myPreferences;

    public static ThemeUtil getInstance() {
        if (instance == null) instance = new ThemeUtil();
        return instance;
    }

    public void initialize(Context ctxt){
        context = ctxt;
        myPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    private void writePreference(String key, boolean value){
        SharedPreferences.Editor e = myPreferences.edit();
        e.putBoolean(key, value);
        e.commit();
    }

    public void switchToDarkTheme() {
        writePreference(THEME, true);
    }

    public void switchToLightTheme() {
        writePreference(THEME, false);
    }

    public boolean isThemeSetToDark() {
        if(!myPreferences.contains(THEME)){
            return GlobalParameters.DEFAULT_THEME_IS_DARK;
        }
        return myPreferences.getBoolean(THEME, false);
    }



    public static void onActivityCreateSetTheme(Activity activity) {
        if (getInstance().isThemeSetToDark()) {
            activity.setTheme(R.style.Dark);
        } else {
            activity.setTheme(R.style.Light);
        }
        //activity.recreate();

    }

}
