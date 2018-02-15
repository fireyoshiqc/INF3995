package ca.polymtl.inf3995.oronos;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Calendar;

import timber.log.Timber;

/**
 * Created by prst on 2018-02-13.
 */

/* USAGE
// à appeller seulement une fois pour l'application?
Timber.plant(new LogTree());

// pour logger
Timber.v("hello");
Timber.d("hello");
Timber.i("hello");
Timber.w("hello");
Timber.e("hello");

*/

public class LogTree extends Timber.DebugTree {

    @Override
    protected void log(int priority, String tag, @NonNull String message, Throwable t) {

        String currentTime = Calendar.getInstance().getTime().toString();

        switch (priority) {
            case Log.VERBOSE:
                Log.println(priority, tag, currentTime + " - VERBOSE - " + message);
                break;
            case Log.DEBUG:
                Log.println(priority, tag, currentTime + " - DEBUG   - " + message);
                break;
            case Log.INFO:
                Log.println(priority, tag, currentTime + " - INFO    - " + message);
                break;
            case Log.WARN:
                Log.println(priority, tag, currentTime + " - WARNING - " + message);
                break;
            case Log.ERROR:
                Log.println(priority, tag, currentTime + " - ERROR   - " + message);
                break;
        }

    }

}
