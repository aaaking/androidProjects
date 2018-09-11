package com.example.jeliu.bipawallet.util

import android.content.Context
import com.example.jeliu.bipawallet.BuildConfig

/**
 * Created by 周智慧 on 2018/9/11.
 */
var debugBuildType = false//是否是debug编译
var DEV_VERSION = 0//0 dev; 1 release
var sMaxThread: Int = 0
var sAppContext: Context? = null

fun initConstant(context : Context) {
    sAppContext = context
    sMaxThread = Runtime.getRuntime().availableProcessors() * 2 + 1
    //
    if ("release" == BuildConfig.BUILD_TYPE) {
        debugBuildType = false
    } else if ("debug" == BuildConfig.BUILD_TYPE) {
        debugBuildType = true
    }
//    ApiConfig.isTestVersion = mSettingsPreferences.sp_setting.getBoolean(SettingsPreferences.KEY_APP_IS_TEST_VERSION, true)
//    if (!debugBuildType) {
//        ApiConfig.isTestVersion = CacheConstant.debugBuildType
//    }
}