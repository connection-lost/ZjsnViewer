package me.crafter.android.zjsnviewer;

import android.content.Context;
import android.util.TypedValue;
import android.widget.RemoteViews;

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
        views.setTextViewTextSize(R.id.textView1, TypedValue.COMPLEX_UNIT_PX, textsize);
        views.setTextViewTextSize(R.id.textView, TypedValue.COMPLEX_UNIT_PX, textsize);
    }
}
