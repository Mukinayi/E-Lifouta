package com.example.exact_it_dev.e_lifouta.network;

import android.app.Application;
import com.pushbots.push.Pushbots;

/**
 * Created by EXACT-IT-DEV on 3/28/2018.
 */

public class Myapp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Pushbots.sharedInstance().init(this);
    }
}
