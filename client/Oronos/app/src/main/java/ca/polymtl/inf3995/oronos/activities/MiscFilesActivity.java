package ca.polymtl.inf3995.oronos.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
    private AlertDialog          dialog;
    private boolean              isRunning = false;
    private ArrayAdapter<String> listAdapter;

    private static class CustomArrayAdapter extends ArrayAdapter<String> {
        private MiscFilesActivity parentActivity;

        CustomArrayAdapter(MiscFilesActivity parent, @NonNull Context context, @LayoutRes int resource) {
            super(context, resource);
            this.parentActivity = parent;
        }

        @Override
        public @NonNull View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView textView = (TextView)v;
                    String fileToDownload = textView.getText().toString();
                    parentActivity.downloadAndOpenFile(fileToDownload);
                }
            });

            return view;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setUpView();
        this.isRunning = true;
        this.requestAndShowFilesList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.isRunning = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onResume() {
        super.onResume();
        this.isRunning = true;
    }

    private void setUpView() {
        setContentView(R.layout.activity_misc_files);
        changeToolbarTitle("Miscellaneous files");
        ListView listView = findViewById(R.id.misc_files_listview);
        this.listAdapter = new CustomArrayAdapter(this, this, R.layout.misc_files_textview);
        listView.setAdapter(this.listAdapter);
        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
    }

    private void requestAndShowFilesList() {
        Response.Listener<JSONObject> resListen = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject result) {
                final ArrayList<String> allFiles = new ArrayList<>();
                JSONArray names = result.names();
                try {
                    for (int i = 0; i < names.length(); i++) {
                        if (!names.get(i).equals("nFiles")){
                            String filename = result.getString(names.get(i).toString());
                            allFiles.add(filename);
                        }
                    }
                }
                catch (JSONException e) {
                    allFiles.clear();
                }

                showFilesList(allFiles);
            }
        };
        RestHttpWrapper.getInstance().sendGetConfigMiscFiles(resListen, null);
    }

    private void showFilesList(final ArrayList<String> allFiles) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listAdapter.clear();
                listAdapter.addAll(allFiles);
                listAdapter.notifyDataSetChanged();
            }
        });
    }

    private void downloadAndOpenFile(String fileToDownload) {
        final Response.Listener<RestHttpWrapper.FileAttachment> downloadListen = new Response.Listener<RestHttpWrapper.FileAttachment>() {
            @Override
            public void onResponse(RestHttpWrapper.FileAttachment result) {
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
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                PackageManager packageManager = getPackageManager();

                // Resolve implicit intent.
                List<ResolveInfo> activities = packageManager.queryIntentActivities(intent,
                                                                                    PackageManager.MATCH_DEFAULT_ONLY);
                boolean isIntentSafe = activities.size() > 0;
                dialog.dismiss();
                if (isRunning || isIntentSafe)
                    startActivity(intent);
            }
        };

        final Response.ErrorListener errorListen = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.setTitle("Error while downloading");
                dialog.setMessage(error.toString());
                dialog.show();
            }
        };

        final Request<?> request = RestHttpWrapper.getInstance().sendGetConfigMiscFiles(fileToDownload, downloadListen, errorListen);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        this.dialog = builder.create();
        this.dialog.setCancelable(true);
        this.dialog.setTitle("Downloading...");
        this.dialog.setMessage("File : '" + fileToDownload + "'");
        this.dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        this.dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                request.cancel();
                dialog.dismiss();
            }
        });
        this.dialog.show();

    }
}
