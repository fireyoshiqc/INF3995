package ca.polymtl.inf3995.oronos.utils;

import android.app.Activity;

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
    private static boolean isDarkTheme = GlobalParameters.DEFAULT_THEME_IS_DARK;

    public static void switchToDarkTheme() {
        isDarkTheme = true;
    }

    public static void switchToLightTheme() {
        isDarkTheme = false;
    }

    public static boolean isThemeSetToDark() {
        return isDarkTheme;
    }

    public static void onActivityCreateSetTheme(Activity activity) {
        if (isDarkTheme) {
            activity.setTheme(R.style.Dark);
        } else {
            activity.setTheme(R.style.Light);
        }
        //activity.recreate();

    }

}
