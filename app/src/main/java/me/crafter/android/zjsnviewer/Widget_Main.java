package me.crafter.android.zjsnviewer;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

public class Widget_Main extends AppWidgetProvider {

    private static final String SYNC_CLICKED    = "automaticWidgetSyncButtonClick";
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
        RemoteViews remoteViews;
        ComponentName watchWidget;

        remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget__main);
        watchWidget = new ComponentName(context, Widget_Main.class);

        remoteViews.setOnClickPendingIntent(R.id.imageButton, getPendingSelfIntent(context, SYNC_CLICKED));
        appWidgetManager.updateAppWidget(watchWidget, remoteViews);
        updateWidget(context);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions){
        context.startService(new Intent(context, TimerService.class));
        //Toast.makeText(context, "啦啦啦", Toast.LENGTH_SHORT).show();
        updateWidget(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        super.onReceive(context, intent);

        if (SYNC_CLICKED.equals(intent.getAction())) {
            Log.i("widget","clicked");

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            DockInfo.updateInterval = 0;
            new UpdateTask(context).execute();
            RemoteViews remoteViews;
            ComponentName watchWidget;

            remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget__main);
            watchWidget = new ComponentName(context, Widget_Main.class);

//            remoteViews.setTextViewText(R.id.imageButton, "TESTING");

            appWidgetManager.updateAppWidget(watchWidget, remoteViews);

        }
    }
    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    public static void updateWidget(Context context){
        updateWidget(context, AppWidgetManager.getInstance(context));
    }

    public static void updateWidget(Context context, AppWidgetManager appWidgetManager){
        //Log.i("Widget_Main", "updateWidget()");
//        PendingIntent pending = Storage.getStartPendingIntent(context);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget__main);
//
//        views.setOnClickPendingIntent(R.id.imageButton, pendingIntent);

        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        //views.setTextViewText(R.id.textView, "Last Update: " + sdf.format(new Date()));
        views.setTextViewText(R.id.textView2, Storage.str_tiduName);
        views.setTextViewText(R.id.textView, "Level: " + DockInfo.level + " (" + DockInfo.exp + "/" + DockInfo.nextExp + ")");
        if (DockInfo.level.equals("150")){
            views.setTextViewText(R.id.textView, "Level: 150 (MAX)");
        }
        Worker.mainSetTextSize(context, views);
        appWidgetManager.updateAppWidget(new ComponentName(context, Widget_Main.class), views);
    }
}
