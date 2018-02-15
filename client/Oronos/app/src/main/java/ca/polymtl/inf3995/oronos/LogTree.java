package ca.polymtl.inf3995.oronos;

import android.support.annotation.NonNull;
import android.util.Log;

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

        switch (priority) {
            case Log.VERBOSE:
                Log.v(tag, "VERBOSE - " + message);
                break;
            case Log.DEBUG:
                Log.d(tag, "DEBUG   - " + message);
                break;
            case Log.INFO:
                Log.i(tag, "INFO    - " + message);
                break;
            case Log.WARN:
                Log.w(tag, "WARNING - " + message);
                break;
            case Log.ERROR:
                Log.e(tag, "ERROR   - " + message);
                break;
        }

    }

}
