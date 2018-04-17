package ca.polymtl.inf3995.oronos.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import ca.polymtl.inf3995.oronos.R;

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

    /**
     * Get the instance of the ThemeUtil (singleton)
     *
     * @return the shared instance
     */
    public static ThemeUtil getInstance() {
        if (instance == null) instance = new ThemeUtil();
        return instance;
    }

    /**
     * Initialize ThemeUtil with the Application Context
     *
     * @param ctxt application context
     */
    public void initialize(Context ctxt) {
        context = ctxt.getApplicationContext();
        myPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Add a pair to the SharedPreference
     *
     * @param key   key to identify the pair
     * @param value value of the pair
     */
    private void writePreference(String key, boolean value) {
        SharedPreferences.Editor e = myPreferences.edit();
        e.putBoolean(key, value);
        e.commit();
    }

    /**
     * Store in SharedPreference that we are using Dark Theme
     */
    public void switchToDarkTheme() {
        writePreference(THEME, true);
    }

    /**
     * Store in SharedPreference that we are using Light Theme
     */
    public void switchToLightTheme() {
        writePreference(THEME, false);
    }

    /**
     * Check if theme is SharedPreference is Dark Theme
     *
     * @return true or false
     */
    public boolean isThemeSetToDark() {
        if (!myPreferences.contains(THEME)) {
            return GlobalParameters.DEFAULT_THEME_IS_DARK;
        }
        return myPreferences.getBoolean(THEME, false);
    }

    /**
     * Sets the theme on the Activity
     *
     * @param activity Activity to set theme on
     */
    public static void onActivityCreateSetTheme(Activity activity) {
        if (getInstance().isThemeSetToDark()) {
            activity.setTheme(R.style.Dark);
        } else {
            activity.setTheme(R.style.Light);
        }
        //activity.recreate();

    }

}
