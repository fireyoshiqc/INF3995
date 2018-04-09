package ca.polymtl.inf3995.oronos.utils;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Calendar;

import timber.log.Timber;

/**
 * <h1>Log Tree</h1>
 * Replace Android Log in order to have custom logging messages and redirect the output to file
 *
 * @author Patrick Richer St-Onge, Justine Pepin, Fabrice Charbonneau
 * @version 0.0
 * @since 2018-04-12
 * <p>
 * <pre>
 * {@code
 * Timber.plant(new LogTree());
 * Timber.v("hello");
 * Timber.d("hello");
 * Timber.i("hello");
 * Timber.w("hello");
 * Timber.e("hello");
 * }
 * </pre>
 */

public class LogTree extends Timber.DebugTree {
    private Context appContext;
    private File logFile;

    public LogTree(Context context) {
        super();
        this.appContext = context;

        createLogFile();
    }

    @Override
    protected void log(int priority, String tag, @NonNull String message, Throwable t) {

        String currentTime = Calendar.getInstance().getTime().toString();
        String messageStr = "";

        switch (priority) {
            case Log.VERBOSE:
                messageStr = currentTime + " - INFO - " + message;
                break;
            case Log.DEBUG:
                messageStr = currentTime + " - DEBUG   - " + message;
                break;
            case Log.INFO:
                messageStr = currentTime + " - INFO    - " + message;
                break;
            case Log.WARN:
                messageStr = currentTime + " - WARNING - " + message;
                break;
            case Log.ERROR:
                messageStr = currentTime + " - ERROR - " + message;
                break;
        }
        Log.println(priority, tag, messageStr);

        if (isExternalStorageReadable() && isExternalStorageWritable()) {
            writeToFile(messageStr);
        }

    }

    /* Checks if external storage is available for read and write */
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /* Checks if external storage is available to at least read */
    private boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    private void createLogFile() {
        String currentTime = Calendar.getInstance().getTime().toString();
        File file = new File(getPrivateLogStorageDir(), currentTime + ".txt");
        try {
            if (!file.createNewFile()) {
                Log.println(Log.ERROR, "Timber", "file could not be created");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.logFile = file;

        Log.println(Log.ERROR, "Timber", "this is what file looks like: " + file.getName());
    }

    private File getPrivateLogStorageDir() {
        // Get the directory OronosLogs from the app's private document directory.

        File file = new File(appContext.getExternalFilesDir(
                Environment.DIRECTORY_DOCUMENTS), GlobalParameters.LOG_DIR_NAME);
        if (file.exists()) {
            Log.println(Log.INFO, "Timber", "new File() finds the log directory. All is good. =)");
        } else {
            Log.println(Log.INFO, "Timber", "new File() isn't working; directory not accessible. Creating new directory...");
            if (!file.mkdirs()) {
                Log.println(Log.ERROR, "Timber", "error creating directory.");
            }
        }
        return file;
    }

    private void writeToFile(String message) {
        FileOutputStream fileOutput = null;
        try {
            fileOutput = new FileOutputStream(this.logFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutput);
        try {
            outputStreamWriter.write(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outputStreamWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fileOutput.getFD().sync();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outputStreamWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
