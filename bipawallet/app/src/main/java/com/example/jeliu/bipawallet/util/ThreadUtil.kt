package com.example.jeliu.bipawallet.util

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Created by 周智慧 on 2018/9/11.
 */
internal var sInstance: ExecutorService? = null

fun GetInstance(): ExecutorService {
    if (sInstance == null)
        sInstance = Executors.newFixedThreadPool(sMaxThread, PriorityThreadFactory("ThreadUtil", android.os.Process.THREAD_PRIORITY_BACKGROUND))
    return sInstance!!
}

/**
 *
 * @param runnable
 */
fun Execute(runnable: Runnable) {
    GetInstance().execute(runnable)
}

fun Shutdown() {
    GetInstance().shutdown()
}