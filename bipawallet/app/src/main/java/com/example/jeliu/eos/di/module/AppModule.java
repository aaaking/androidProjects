package com.example.jeliu.eos.di.module;

import android.app.Application;
import android.content.Context;

import com.example.jeliu.bipawallet.Common.Constant;
import com.example.jeliu.eos.data.remote.HostInterceptor;
import com.example.jeliu.eos.data.remote.NodeosApi;
import com.example.jeliu.eos.data.util.GsonEosTypeAdapterFactory;
import com.example.jeliu.eos.data.wallet.EosWalletManager;
import com.example.jeliu.eos.di.ApplicationContext;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by swapnibble on 2017-08-24.
 */
@Module
public class AppModule {

    private final Application mApp;

    public AppModule(Application application) {
        mApp = application;
    }

    @Provides
    Application provideApp() {
        return mApp;
    }

    @Provides
    @ApplicationContext
    Context provideAppContext() {
        return mApp;
    }


    @Provides
    @Singleton
    HostInterceptor providesHostInterceptor() {
        return new HostInterceptor();
    }

    @Provides
    @Singleton
    OkHttpClient providesOkHttpClient(HostInterceptor interceptor) {
        return new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();
    }


    @Provides
    @Singleton
    Gson providesGson() {
        return new GsonBuilder()
                .registerTypeAdapterFactory(new GsonEosTypeAdapterFactory())
                .serializeNulls()
                .excludeFieldsWithoutExposeAnnotation().create();
    }

    @Provides
    @Singleton
    NodeosApi providesEosService(Gson gson, OkHttpClient okHttpClient) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.EOS_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // retrofit 용 rxjava2 adapter
                .client(okHttpClient)
                .build();

        return retrofit.create(NodeosApi.class);
    }

    @Provides
    @Singleton
    EosWalletManager providesWalletManager() {
        return new EosWalletManager();
    }
//
//    @Provides
//    @Singleton
//    AppDatabase provideAppDatabase( @ApplicationContext  Context context){
//        return Room.databaseBuilder( context.getApplicationContext(), AppDatabase.class, "eosc.db")
//                .build();
//    }
//
//    @Provides
//    @Singleton
//    EosAccountRepository provideAccountRepository(AppDatabase database ) {
//        return new EosAccountRepositoryImpl(database);
//    }
}
