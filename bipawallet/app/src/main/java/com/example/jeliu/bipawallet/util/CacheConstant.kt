package com.example.jeliu.bipawallet.util

import android.content.Context
import com.example.jeliu.bipawallet.Application.HZApplication
import com.example.jeliu.bipawallet.BuildConfig
import com.example.jeliu.bipawallet.Common.Constant

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
        Constant.EOS_URL = "https://nodes.get-scatter.com"
    } else if ("debug" == BuildConfig.BUILD_TYPE) {
        debugBuildType = true
        Constant.EOS_URL = "http://192.168.1.120:8888"//"http://193.93.219.219:8888"//
    }
//    ApiConfig.isTestVersion = mSettingsPreferences.sp_setting.getBoolean(SettingsPreferences.KEY_APP_IS_TEST_VERSION, true)
//    if (!debugBuildType) {
//        ApiConfig.isTestVersion = CacheConstant.debugBuildType
//    }
}