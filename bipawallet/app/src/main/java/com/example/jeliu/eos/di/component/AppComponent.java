package com.example.jeliu.eos.di.component;

import android.app.Application;
import android.content.Context;

import com.example.jeliu.bipawallet.Application.HZApplication;
import com.example.jeliu.eos.data.EoscDataManager;
import com.example.jeliu.eos.data.remote.HostInterceptor;
import com.example.jeliu.eos.di.ApplicationContext;
import com.example.jeliu.eos.di.module.AppModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by swapnibble on 2017-08-24.
 */
@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {

    void inject(HZApplication eosCommanderApp);

    @ApplicationContext
    Context context();

    Application application();

    EoscDataManager dataManager();

    HostInterceptor hostInterceptor();
}
