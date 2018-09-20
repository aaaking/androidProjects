package com.example.jeliu.eos.di.component;

import com.example.jeliu.bipawallet.Splash.SplashActivity;
import com.example.jeliu.bipawallet.ui.PayEosWindow;
import com.example.jeliu.eos.CreateEosWalletAC;
import com.example.jeliu.eos.ImportEosWalletAC;
import com.example.jeliu.eos.InviteCreateEosAC;
import com.example.jeliu.eos.di.PerActivity;
import com.example.jeliu.eos.di.module.ActivityModule;

import dagger.Component;

/**
 * Created by swapnibble on 2017-08-24.
 */
@PerActivity
@Component( dependencies = AppComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(SplashActivity activity);
    void inject(ImportEosWalletAC activity);
    void inject(CreateEosWalletAC activity);
    void inject(InviteCreateEosAC activity);
    void inject(PayEosWindow dialog);
//    void inject(AccountMainFragment fragment);
}
