package ca.polymtl.inf3995.oronos.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.content.FileProvider;
import android.widget.TextView;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import ca.polymtl.inf3995.oronos.R;
import ca.polymtl.inf3995.oronos.services.RestHttpWrapper;

/**
 * <h1>Miscellaneous Files Activity</h1>
 * The Miscellaneous Files Activity is responsible of managing the files list the client gets
 * from the server and to start the file downloads that are requested by the user.
 *
 * @author  Fabrice Charbonneau
 * @version 0.0
 * @since   2018-04-12
 */
public class MiscFilesActivity extends DrawerActivity {
    private TextView textViewTitle = null;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_misc_files);
        changeToolbarTitle("Miscellaneous files");
        this.textViewTitle = findViewById(R.id.misc_files_title);
        textViewTitle.setText("Waiting for list from server...");

        this.requestAndShowFilesList();
    }

    private void requestAndShowFilesList() {
        Response.Listener<JSONObject> resListen = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject result) {
                ArrayList<String> allFiles = new ArrayList<>();
                JSONArray names = result.names();
                String fileToDownload = "";
                StringBuffer msg = new StringBuffer();
                try {
                    msg.append("List of available files for download : " + "\n");
                    for (int i = 0; i < names.length(); i++) {
                        if (!names.get(i).equals("nFiles"))
                            msg.append(" - ").append(result.getString(names.get(i).toString())).append("\n");
                    }
                    fileToDownload = "eggs/file1.txt";
                }
                catch (JSONException e) {
                    msg = new StringBuffer("Ooops!");
                }
                textViewTitle.setText(msg.toString());

                SystemClock.sleep(1000);

                Response.Listener<RestHttpWrapper.FileAttachment> downloadListen = new Response.Listener<RestHttpWrapper.FileAttachment>() {
                    @Override
                    public void onResponse(RestHttpWrapper.FileAttachment result) {
                        textViewTitle.setText("Downloaded file '" + result.filename + "' of type " +
                                              "'" + result.mimeType + "' of size " + result.fileContent.length + " bytes.");

                        File miscFilesFolder = new File(getFilesDir(), "miscFiles");
                        if (!miscFilesFolder.exists()) {
                            miscFilesFolder.mkdir();
                        }
                        File file = new File(getFilesDir() + File.separator + "miscFiles", result.filename);
                        try {
                            OutputStream outStrm = new FileOutputStream(file);
                            outStrm.write(result.fileContent);
                            outStrm.flush();
                        } catch (IOException e) {
                            return;
                        }

                        // Get URI of file.
                        Context context = getApplicationContext();
                        Uri uri = FileProvider.getUriForFile(context, "ca.polymtl.inf3995.oronos.fileprovider", file);

                        // Open file with user selected app.
                        Intent intent = new Intent();
                        intent.setDataAndType(uri, result.mimeType);
                        intent.setDataAndType(uri, "text/plain");
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        PackageManager packageManager = getPackageManager();

                        // Resolve implicit intent.
                        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent,
                                                                                            PackageManager.MATCH_DEFAULT_ONLY);
                        boolean isIntentSafe = activities.size() > 0;
                        if (isIntentSafe)
                            startActivity(intent);
                    }
                };
                RestHttpWrapper.getInstance().sendGetConfigMiscFiles(fileToDownload, downloadListen, null);
            }
        };
        RestHttpWrapper.getInstance().sendGetConfigMiscFiles(resListen, null);
    }
}
