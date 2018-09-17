package com.example.jeliu.eos.di.component;

import com.example.jeliu.bipawallet.Splash.SplashActivity;
import com.example.jeliu.eos.ImportEosWalletAC;
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
//
//    void inject(AccountMainFragment fragment);
//    void inject(CreateEosAccountDialog dialog);
//
//    void inject(WalletFragment fragment);
//    void inject(PushFragment fragment);
//    void inject(GetTableFragment fragment);
//
//    void inject(CurrencyFragment fragment);
//    void inject(TransferFragment fragment);
//
//    void inject(InputDataDialog dialog);
//    void inject(CreateWalletDialog dialog);
//
//    void inject(InputAccountDialog dialog);
//
//    void inject(MsgInputActivity activity);
}
