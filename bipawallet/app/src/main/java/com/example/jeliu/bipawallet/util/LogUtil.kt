package com.example.jeliu.bipawallet.util

import android.util.Log

/**
 * Created by 周智慧 on 2018/9/11.
 */
object LogUtil {
    private val TAG = "zzh"
    
    fun v(tag: String, msg: String) {
        if (!debugBuildType) {
            return
        }
        Log.v(tag, buildMessage(msg))
    }

    fun v(tag: String, msg: String, thr: Throwable) {
        if (!debugBuildType) {
            return
        }
        Log.v(tag, buildMessage(msg), thr)
    }

    fun d(tag: String, msg: String) {
        if (!debugBuildType) {
            return
        }
        Log.d(tag, buildMessage(msg))
    }

    fun d(tag: String, msg: String, thr: Throwable) {
        if (!debugBuildType) {
            return
        }
        Log.d(tag, buildMessage(msg), thr)
    }

    fun i(msg: String) {
        if (!debugBuildType) {
            return
        }
        Log.i(TAG, buildMessage(msg))
    }

    fun i(tag: String, msg: String) {
        if (!debugBuildType) {
            return
        }
        Log.i(tag, buildMessage(msg))
    }

    fun i(tag: String, msg: String, thr: Throwable) {
        if (!debugBuildType) {
            return
        }
        Log.i(tag, buildMessage(msg), thr)
    }

    fun w(tag: String, msg: String) {
        if (!debugBuildType) {
            return
        }
        Log.w(tag, buildMessage(msg))
    }

    fun w(tag: String, msg: String, thr: Throwable) {
        if (!debugBuildType) {
            return
        }
        Log.w(tag, buildMessage(msg), thr)
    }

    fun w(tag: String, thr: Throwable) {
        if (!debugBuildType) {
            return
        }
        Log.w(tag, buildMessage(""), thr)
    }

    fun e(tag: String, msg: String) {
        if (!debugBuildType) {
            return
        }
        Log.e(tag, buildMessage(msg))
    }

    fun e(tag: String, msg: String, thr: Throwable) {
        if (!debugBuildType) {
            return
        }
        Log.e(tag, buildMessage(msg), thr)
    }

    fun ui(msg: String) {
        if (!debugBuildType) {
            return
        }
        Log.i("ui", buildMessage(msg))
    }

    fun res(msg: String) {
        if (!debugBuildType) {
            return
        }
        Log.i("RES", buildMessage(msg))
    }

    private fun buildMessage(msg: String): String {
        return msg
    }
}