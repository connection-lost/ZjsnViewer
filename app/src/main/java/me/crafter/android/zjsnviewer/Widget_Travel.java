package me.crafter.android.zjsnviewer;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RemoteViews;


/**
 * Implementation of App Widget functionality.
 */
public class Widget_Travel extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        context.startService(new Intent(context, TimerService.class));
        updateWidget(context, appWidgetManager);
    }


    @Override
    public void onEnabled(Context context) {
        context.startService(new Intent(context, TimerService.class));
    }

    @Override
    public void onDisabled(Context context) {

    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        context.startService(new Intent(context, TimerService.class));
    }

    public static void updateWidget(Context context){
        updateWidget(context, AppWidgetManager.getInstance(context));
    }

    public static void updateWidget(Context context, AppWidgetManager appWidgetManager) {
        //Log.i("Widget_Travel", "updateWidget()");
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget__travel);
        String[] info = DockInfo.getTravelBoard();
        views.setTextViewText(R.id.textView1, info[0]);
        views.setTextViewText(R.id.textView2, info[1]);
        views.setTextViewText(R.id.textView3, info[2]);
        views.setTextViewText(R.id.textView4, info[3]);
        float textsize = Storage.getTextSizeMinor(context);
        views.setTextViewTextSize(R.id.textView1, TypedValue.COMPLEX_UNIT_PX, textsize);
        views.setTextViewTextSize(R.id.textView2, TypedValue.COMPLEX_UNIT_PX, textsize);
        views.setTextViewTextSize(R.id.textView3, TypedValue.COMPLEX_UNIT_PX, textsize);
        views.setTextViewTextSize(R.id.textView4, TypedValue.COMPLEX_UNIT_PX, textsize);
        appWidgetManager.updateAppWidget(new ComponentName(context, Widget_Travel.class), views);
    }
}


