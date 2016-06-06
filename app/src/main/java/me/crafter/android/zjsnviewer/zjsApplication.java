package me.crafter.android.zjsnviewer;

import android.app.Application;
import android.content.Intent;

/**
 * @author traburiss
 * @date 2016/6/6
 * @info ZjsnViewer
 * @desc
 */

public class zjsApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        startService(new Intent(this, TimerService.class));
    }
}
