package me.crafter.android.zjsnviewer;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;


public class Widget_Main extends AppWidgetProvider {

    @Override
    public void onEnabled(Context context) {
        context.startService(new Intent(context, TimerService.class));
        super.onEnabled(context);
        //Toast.makeText(context, "启动了吔", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        //Toast.makeText(context, "关闭了哇", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){
        context.startService(new Intent(context, TimerService.class));
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        updateWidget(context);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions){
        context.startService(new Intent(context, TimerService.class));
        //Toast.makeText(context, "啦啦啦", Toast.LENGTH_SHORT).show();
        updateWidget(context);
    }


    public static void updateWidget(Context context){
        updateWidget(context, AppWidgetManager.getInstance(context));
    }

    public static void updateWidget(Context context, AppWidgetManager appWidgetManager){
        //Log.i("Widget_Main", "updateWidget()");
        PendingIntent pending = Storage.getStartPendingIntent(context);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget__main);

        views.setOnClickPendingIntent(R.id.imageButton, pending);

        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        //views.setTextViewText(R.id.textView, "Last Update: " + sdf.format(new Date()));
        views.setTextViewText(R.id.textView2, Storage.str_tiduName);
        views.setTextViewText(R.id.textView, "Level: " + DockInfo.level + " (" + DockInfo.exp + "/" + DockInfo.nextExp + ")");
        if (DockInfo.level.equals("150")){
            views.setTextViewText(R.id.textView, "Level: 150 (MAX)");
        }

        appWidgetManager.updateAppWidget(new ComponentName(context, Widget_Main.class), views);
    }
}
