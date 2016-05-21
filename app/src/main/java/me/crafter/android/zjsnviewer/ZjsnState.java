package me.crafter.android.zjsnviewer;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

/**
 * Created by JohnnySun on 2015/11/19.
 */
public class ZjsnState {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static boolean isUsageStatsAllowed(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int uid = android.os.Process.myUid();
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, uid, context.getPackageName());
        return  mode == AppOpsManager.MODE_ALLOWED;
    }

    public static int getZjsnState(Context context, long updateInterval) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (!isUsageStatsAllowed(context)) {
                Log.i("getZjsnState", "Permission Not Allowed!!");
                return -1;
            }
        } else {
            return -1;
        }

        long time = System.currentTimeMillis();
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService("usagestats");
        //long interval = updateInterval * 1000;
        UsageEvents events = usm.queryEvents(time - updateInterval, time);
        while (events.hasNextEvent()) {
            UsageEvents.Event event = new UsageEvents.Event();
            if(events.getNextEvent(event)) {
                if(event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    Log.i("packagename", event.getPackageName());
                    if(event.getPackageName().startsWith("com.muka.shipwar") || event.getPackageName().startsWith("com.huanmeng.zhanjian2")) {
                        Log.i("getZjsnState", "Zjsn Move to Foreground");
                        return 0;
                    }
                }else  if(event.getEventType() == UsageEvents.Event.MOVE_TO_BACKGROUND) {
                        Log.i("packagename", event.getPackageName());
                        if (event.getPackageName().startsWith("com.muka.shipwar") || event.getPackageName().startsWith("com.huanmeng.zhanjian2")) {
                            Log.i("getZjsnState", "Zjsn Move to Background");
                            return 1;
                        }
                    }

            }

        }
        Log.i("getZjsnState", "Zjsn not cache");
        return -1;
    }
}