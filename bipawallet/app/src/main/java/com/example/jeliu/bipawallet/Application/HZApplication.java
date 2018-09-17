package com.example.jeliu.bipawallet.Application;

import android.app.Application;
import android.content.Context;

import com.example.jeliu.bipawallet.Common.Common;
import com.example.jeliu.bipawallet.Network.RequestManager;
import com.example.jeliu.eos.data.EoscDataManager;
import com.example.jeliu.eos.di.component.AppComponent;
import com.example.jeliu.eos.di.component.DaggerAppComponent;
import com.example.jeliu.eos.di.module.AppModule;

import javax.inject.Inject;

/**
 * Created by liuming on 12/05/2018.
 */

public class HZApplication extends Application {
    private AppComponent mAppComponent;

    @Inject
    EoscDataManager mDataManager;
//    public static HZApplication s_inst;

//    public static HZApplication getInst() {
//        return s_inst;
//    }

    public void onCreate() {
        super.onCreate();
        RequestManager.init(this);
        Common.setWalletPath(this);
        com.example.jeliu.bipawallet.util.CacheConstantKt.initConstant(this);
        mAppComponent = DaggerAppComponent.builder()
                .appModule( new AppModule(this))
                .build();

        mAppComponent.inject( this );
    }

    public static HZApplication get(Context context) {
        return (HZApplication) context.getApplicationContext();
    }

    public AppComponent getAppComponent() { return mAppComponent; }
}
