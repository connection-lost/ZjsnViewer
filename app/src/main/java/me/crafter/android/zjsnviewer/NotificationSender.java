package me.crafter.android.zjsnviewer;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

public class NotificationSender {

    private static final String NOTIFICATION_TAG = "ZjsnViewer";
    private static final int notificationId = 0;

    public static void notify(final Context context, final String title, final String text) {
        final Resources res = context.getResources();

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean screen_light = prefs.getBoolean("notification_screen_light", true);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
//                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(title)
                .setContentText(text)
//                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
//                TODO 加个是否显示浮动窗口的选项
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setDefaults(Notification.DEFAULT_LIGHTS)
                .setContentIntent(Storage.getStartPendingIntent(context))
                .setAutoCancel(true);

        if(screen_light) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder.setVisibility(Notification.VISIBILITY_PUBLIC);
            } else {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "ZJSN");
            wl.acquire(10000);
            }
        }
        SendNotification(context, builder.build());
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private static void SendNotification(final Context context, final Notification notification) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.notify(NOTIFICATION_TAG, 0, notification);
        } else {
            nm.notify(NOTIFICATION_TAG.hashCode(), notification);
        }
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    public static void cancel(final Context context) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.cancel(NOTIFICATION_TAG, 0);
        } else {
            nm.cancel(NOTIFICATION_TAG.hashCode());
        }
    }
}