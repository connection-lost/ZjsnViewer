package me.crafter.android.zjsnviewer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class TimerService extends Service {
    // constant
    public static long NOTIFY_INTERVAL = 5 * 1000; // 10 seconds
    public static TimerService instance;
    public static int NOTIFICATION_ID = 1314;

    public static BroadcastReceiver mReceiver;

    public static int lastWidgetUpdate = 0;

    // run on another Thread to avoid crash
    private Handler mHandler = new Handler();
    // timer handling
    private Timer mTimer = null;

    private ForegroundReceive receive;

//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        // If we get killed, after returning from here, restart
//        return START_STICKY;
//    }
    private void setForeGround(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (prefs.getBoolean("notification_foreground", true)){

            String[] info = DockInfo.getTravelBoard();
            String title = Storage.str_tiduName;
            String msj_name = prefs.getString("notification_msj_name","");
            if (!msj_name.isEmpty()){

                Storage.language = Integer.parseInt(prefs.getString("language", "0"));
                title = msj_name + Storage.str_msj_foreground_reportTitle[Storage.language];
            }
            String text = Storage.str_thereIs[Storage.language] + DockInfo.countTravelIng() + Storage.str_teamsTravelling[Storage.language];
            String msg = "";
            NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle()
                    .setBigContentTitle(title)
                    .setSummaryText(text);

            for (int i = 0; i < 4; i++){
    //            style.addLine(info[i]);
                msg += info[i] + "\n";
            }
            style.bigText(msg);

            final NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.cat_icon)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setStyle(style)
                    .setGroup(Storage.NOTIFICATION_GROUP_KEY)
                    .setGroupSummary(true)
                    .setContentIntent(Storage.getInfoIntent(context));

            startForeground(NOTIFICATION_ID,builder.build());
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.i("TimerService", "onCreate()");
        if (mReceiver == null){
            IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            mReceiver = new ScreenReceiver();
            registerReceiver(mReceiver, filter);
        }

        if (mTimer != null) {
            mTimer.cancel();
        } else {
            mTimer = new Timer();
            instance = this;
        }
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, NOTIFY_INTERVAL);

        setForeGround(this);

        receive = new ForegroundReceive();
        IntentFilter filter = new IntentFilter();
        filter.addAction("OnOrOff");
        registerReceiver(receive, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receive);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent){
        // test
        Log.d("TimerService", "onTaskRemoved is called");
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());
        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartServicePendingIntent);
        super.onTaskRemoved(rootIntent);
    }

    class TimeDisplayTimerTask extends TimerTask {
        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    new Proceed().execute();
                }
            });
        }
    }

    private class Proceed extends AsyncTask {
        @Override
        protected Object doInBackground(Object... arg0){
            Context context = instance;
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

            Storage.language = Integer.parseInt(prefs.getString("language", "0"));
            if (prefs.getBoolean("auto_run", true)) {
                DockInfo.requestUpdate(context);
            }
            //notification checker
            if (DockInfo.shouldNotify(context)){

                String msj_name = prefs.getString("notification_msj_name","");
                if (msj_name.isEmpty()){

                    NotificationSender.notify(context, Storage.str_reportTitle[Storage.language], DockInfo.getStatusReportAllFull());
                }else {

                    NotificationSender.notify(context, msj_name + Storage.str_msjreportTitle[Storage.language], DockInfo.getStatusReportAllFull());
                }
            }
            //check if screen is on
            //if screen not on, widget should not update
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            boolean screenon = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH){
                screenon = pm.isInteractive();
            } else {
                screenon = pm.isScreenOn();
            }
            if (!screenon){
                //Log.i("TimerService", "run() - Screen is off, ignores update.");
            } else {
                int currentUnix = DockInfo.currentUnix();
                if (currentUnix - lastWidgetUpdate >= Integer.parseInt(prefs.getString("refresh", "60"))){

                    lastWidgetUpdate = currentUnix;
                    Widget_Main.updateWidget(context);
                    Widget_Travel.updateWidget(context);
                    Widget_Repair.updateWidget(context);
                    Widget_Build.updateWidget(context);
                    Widget_Make.updateWidget(context);
                } else {
                    //not time yet, ignore widget update
                }
                setForeGround(context);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(instance);
            if (NOTIFY_INTERVAL != (Long.valueOf(prefs.getString("refresh", "60"))) * 1000) {

                NOTIFY_INTERVAL = (Long.valueOf(prefs.getString("refresh", "60"))) * 1000;
                mTimer.cancel();
                mTimer = new Timer();
                mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), NOTIFY_INTERVAL, NOTIFY_INTERVAL);
            }
            Log.i("TimerService", "run() - TimerService Receive Call\nNOTIFY_INTERVAL:" + NOTIFY_INTERVAL + "\nrefresh:" + prefs.getString("refresh", "60"));
        }
    }

    public class ForegroundReceive extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            if (prefs.getBoolean("notification_foreground", true)){

                setForeGround(context);
            }else {

                stopForeground(true);
            }
        }
    }
}