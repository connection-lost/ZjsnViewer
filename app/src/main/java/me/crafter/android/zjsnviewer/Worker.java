package me.crafter.android.zjsnviewer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RemoteViews;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class Worker {

    public static void widgetSetTextSize(Context context, RemoteViews views){
        float textsize = Storage.getTextSizeMinor(context);
        views.setTextViewTextSize(R.id.textView1, TypedValue.COMPLEX_UNIT_PX, textsize);
        views.setTextViewTextSize(R.id.textView2, TypedValue.COMPLEX_UNIT_PX, textsize);
        views.setTextViewTextSize(R.id.textView3, TypedValue.COMPLEX_UNIT_PX, textsize);
        views.setTextViewTextSize(R.id.textView4, TypedValue.COMPLEX_UNIT_PX, textsize);
    }

    public static void mainSetTextSize(Context context, RemoteViews views){
        float textsize = Storage.getTextSizeMajor(context);
        views.setTextViewTextSize(R.id.textView2, TypedValue.COMPLEX_UNIT_PX, textsize);
        views.setTextViewTextSize(R.id.textView, TypedValue.COMPLEX_UNIT_PX, textsize);
    }

    public static void checkUpdate(final Activity activity, final Context context){
        Toast.makeText(context, context.getString(R.string.checking_update), Toast.LENGTH_SHORT).show();
        new AsyncTask<Void, Void, Void>() {
            State state = State.INTERRUPT;
            String newVersionString = "";
            String newVersionFeature = "";
            boolean important = false;
            String downloadLink = "";
            @Override
            protected Void doInBackground( final Void ... params ) {
                String response = visit("http://zjsn.acg.land/version.json");
                if (response.equals("ERR1")){
                    state = State.CONNECTION_FAIL;
                    return null;
                }
                try {
                    JSONObject obj = new JSONObject(response);
                    int currVersion = Storage.getVersion(context);
                    int newVersion = obj.getInt("currentVersion");
                    if (newVersion > currVersion){
                        state = State.UPDATE_FOUND;
                    } else {
                        state = State.NO_UPDATE_FOUND;
                        return null;
                    }
                    int importantSince = obj.getInt("importantSince");
                    if (importantSince <= currVersion){
                        important = true;
                    }
                    newVersionString = obj.getString("versionString");
                    newVersionFeature = obj.getString("whatsNew");
                    downloadLink = obj.getString("downloadLink");
                } catch (Exception ex){
                    state = State.PARSE_ERROR;
                    return null;
                }
                return null;
            }
            @Override
            protected void onPostExecute( final Void result ) {
                switch (state){
                    case CONNECTION_FAIL:
                    case CONNECTION_RESET:
                    case INTERRUPT:
                        Toast.makeText(context, context.getString(R.string.check_update_fail_connection), Toast.LENGTH_SHORT).show();
                        break;
                    case PARSE_ERROR:
                        Toast.makeText(context, context.getString(R.string.check_update_fail_parse), Toast.LENGTH_SHORT).show();
                        break;
                    case UPDATE_FOUND:
                        Toast.makeText(context, context.getString(R.string.check_update_success_update_available) + " " + newVersionString, Toast.LENGTH_SHORT).show();
                        String ready = context.getResources().getString(R.string.check_update_body);
                        ready = ready.replace("@curr@", context.getResources().getString(R.string.versions));
                        ready = ready.replace("@new@", newVersionString);
                        ready = ready.replace("@feature@", newVersionFeature);
                        if (important){
                            ready += context.getResources().getString(R.string.check_update_important);
                        }
                        askUpdate(activity, context, ready, downloadLink);
                        break;
                    case NO_UPDATE_FOUND:
                        Toast.makeText(context, context.getString(R.string.check_update_success_no_update), Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(context, context.getString(R.string.check_update_fail_parse), Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }.execute();
    }

    public static String visit(String urls){
        String ret = "";
        try {
            URL url = new URL(urls);
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            ret = response.toString();
        } catch (Exception ex){
            ret = "ERR1";
        }
        return ret;
    }

    public static void askUpdate(final Activity activity, final Context context, String message, final String url){
        new AlertDialog.Builder(activity)
                .setTitle(R.string.check_update_title)
                .setMessage(message)
                .setPositiveButton(R.string.check_update_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
//                        DownloadManager downloadManager = (DownloadManager)activity.getSystemService(activity.DOWNLOAD_SERVICE);
//                        Uri Download_Uri = Uri.parse(url);
//                        DownloadManager.Request request = new DownloadManager.Request(Download_Uri);
//                        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
//                        request.setAllowedOverRoaming(true);
//                        request.setDescription(context.getResources().getString(R.string.check_update_title_download_manager));
//                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "ZjsnViewer.apk");
//                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//                        downloadManager.enqueue(request);
//                        Toast.makeText(context, context.getString(R.string.check_update_downloading), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        activity.startActivity(intent);
                    }
                })
                .setNegativeButton(R.string.check_update_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }

}
