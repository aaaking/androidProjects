package com.example.jeliu.bipawallet.Application;

import android.app.Activity;
import android.app.Application;

import com.example.jeliu.bipawallet.Network.RequestManager;
import com.example.jeliu.bipawallet.UserInfo.UserInfoManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuming on 12/05/2018.
 */

public class HZApplication extends Application {
    private static HZApplication s_inst;
    private List<Activity> activities = new ArrayList<Activity>();

    public void addActivity(Activity act) {
        activities.add(act);
    }

    public void exit() {
        for (Activity act : activities) {
            act.finish();
        }
    }

    public static HZApplication getInst() {
        return s_inst;
    }

    public void onCreate() {
        super.onCreate();
        s_inst = this;
        RequestManager.init(this);
    }
}