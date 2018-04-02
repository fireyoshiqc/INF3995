package ca.polymtl.inf3995.oronos.utils;

import android.app.Activity;

import ca.polymtl.inf3995.oronos.R;

/**
 * Created by Fabri on 2018-04-01.
 */

public class ThemeUtil {
    private static boolean isDarkTheme = GlobalParameters.DEFAULT_THEME_IS_DARK;

    public static void switchToDarkTheme(){
        isDarkTheme = true;
    }

    public static void switchToLightTheme(){
        isDarkTheme = false;
    }

    public static boolean isThemeSetToDark(){
        return isDarkTheme;
    }



    public static void onActivityCreateSetTheme(Activity activity){
        if (isDarkTheme){
            activity.setTheme(R.style.Dark);
        } else {
            activity.setTheme(R.style.Light);
        }
        //activity.recreate();

    }

}
