package com.crl.zzh.customrefreshlayout

import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.view.WindowManager
import com.crl.zzh.customrefreshlayout.util.ScreenUtil

open class BaseActivity : AppCompatActivity() {
    companion object {
        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        ScreenUtil.init(this)
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            var localLayoutParams: WindowManager.LayoutParams = getWindow().getAttributes()
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or localLayoutParams.flags or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
    }
}