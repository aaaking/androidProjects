package com.crl.zzh.customrefreshlayout.extensions

import android.app.Activity
import android.content.Intent

/**
 * Created by 周智慧 on 22/11/2017.
 */
fun Activity.startActivityExt(cls: Class<*>, finishCallingActivity: Boolean = false) {
    val intent = Intent(this, cls)
    startActivity(intent)
    if (finishCallingActivity) {
        finish()
    }
}