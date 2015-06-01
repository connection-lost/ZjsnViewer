package me.crafter.android.zjsnviewer;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RemoteViews;
import android.widget.Toast;

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

    public static void checkUpdate(final Context context){
        Toast.makeText(context, context.getString(R.string.checking_update), Toast.LENGTH_SHORT).show();
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground( final Void ... params ) {
                Log.i("Worker",  "checkUpdate");
                int currVersion = Storage.getVersion(context);




                return null;
            }
            @Override
            protected void onPostExecute( final Void result ) {
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

}
