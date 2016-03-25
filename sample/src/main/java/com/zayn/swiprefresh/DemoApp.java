package com.zayn.swiprefresh;

import android.app.Application;

import com.jiongbull.jlog.JLog;

/**
 * Created by zhou on 16-3-25.
 */
public class DemoApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        JLog.init(this);
    }
}
