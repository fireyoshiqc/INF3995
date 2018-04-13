package ca.polymtl.inf3995.oronos.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import ca.polymtl.inf3995.oronos.activities.OronosActivity;
import ca.polymtl.inf3995.oronos.services.RestHttpWrapper;
import ca.polymtl.inf3995.oronos.widgets.adapters.MiscFilesAdapter;

public class MiscFilesFragment extends Fragment {
    private AlertDialog                dialog;
    private boolean                    isRunning = false;
    private MiscFilesAdapter           adapter;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.isRunning = true;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        this.isRunning = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onResume() {
        super.onResume();
        this.isRunning = true;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_misc_files, container, false);
        OronosActivity activity = (OronosActivity)this.getActivity();
        activity.changeToolbarTitle("Miscellaneous files");

        //ListView listView = view.findViewById(R.id.misc_files_listview);
        //this.listAdapter = new MiscFilesFragment.CustomArrayAdapter(this, getActivity(), R.layout.misc_files_textview);
        //listView.setAdapter(this.listAdapter);
        //listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
        //this.requestAndShowFilesList();

        RecyclerView recyclerView = view.findViewById(R.id.misc_files_recview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(layoutManager);
        this.adapter = new MiscFilesAdapter(new ArrayList<String>(), this);
        recyclerView.setAdapter(this.adapter);

        this.requestAndShowFilesList();

        return view;
    }

    public void downloadAndOpenFile(String fileToDownload) {
        final Response.Listener<RestHttpWrapper.FileAttachment> downloadListen = new Response.Listener<RestHttpWrapper.FileAttachment>() {
            @Override
            public void onResponse(RestHttpWrapper.FileAttachment result) {
                File file = writeDownloadedFile(result);
                if (file == null)
                    return;

                Context context = getActivity().getApplicationContext();
                Uri uri = FileProvider.getUriForFile(context, "ca.polymtl.inf3995.oronos.fileprovider", file);

                openFileWithAppChooser(uri, result.mimeType);
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

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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

    private void requestAndShowFilesList() {
        this.showMiscFilesRequestMessage();

        Response.Listener<JSONObject> resListen = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject result) {
                final ArrayList<String> allFiles = new ArrayList<>();
                JSONArray names = result.names();
                try {
                    for (int i = 0; i < names.length(); i++) {
                        if (!names.get(i).equals("nFiles")) {
                            String filename = result.getString(names.get(i).toString());
                            allFiles.add(filename);
                        }
                    }
                } catch (JSONException e) {
                    allFiles.clear();
                }

                showFilesList(allFiles);
            }
        };
        Response.ErrorListener errListen = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showListErrorMsg(error);
            }
        };
        RestHttpWrapper.getInstance().sendGetConfigMiscFiles(resListen, errListen);
    }

    private void showMiscFilesRequestMessage() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.clear();
                adapter.add("Downloading misc files list...");
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void showFilesList(final ArrayList<String> allFiles) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.clear();
                if (!allFiles.isEmpty())
                    adapter.addAll(allFiles);
                else
                    adapter.add("Nothing to show.");
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void showListErrorMsg(VolleyError error) {
        final String errorMsg = error.toString();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.clear();
                adapter.add("Error while getting misc files list : " + errorMsg);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private File writeDownloadedFile(RestHttpWrapper.FileAttachment result) {
        File miscFilesFolder = new File(getActivity().getFilesDir(), "miscFiles");
        if (!miscFilesFolder.exists()) {
            miscFilesFolder.mkdir();
        }
        File file = new File(getActivity().getFilesDir() + File.separator + "miscFiles", result.filename);
        try {
            OutputStream outStrm = new FileOutputStream(file);
            outStrm.write(result.fileContent);
            outStrm.flush();
            return file;
        } catch (IOException e) {
            return null;
        }
    }

    private void openFileWithAppChooser(Uri uri, String mimeType) {
        // Open file with user selected app.
        Intent intent = new Intent();
        intent.setDataAndType(uri, mimeType);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        PackageManager packageManager = getActivity().getPackageManager();

        // Resolve implicit intent.
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        boolean isIntentSafe = activities.size() > 0;
        dialog.dismiss();
        if (isRunning || isIntentSafe)
            startActivity(intent);
    }
}
