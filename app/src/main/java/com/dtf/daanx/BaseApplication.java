package com.dtf.daanx;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;

/**
 * Created by acer on 2015/12/30.
 */
public class BaseApplication extends Application {
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "ycD1PSY4ksIF10wZAYu8PjjFG7C3WofyM4BVV6y2", "zVYZBHGAXQaFcNFMgkGlqOChCgC9EqCd5LzV90jN");
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }
}
