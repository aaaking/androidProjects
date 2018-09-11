package com.example.jeliu.bipawallet.util

import android.os.Process
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by 周智慧 on 2018/9/11.
 */
class PriorityThreadFactory(private val mName: String, private val mPriority: Int) : ThreadFactory {
    private val mNumber = AtomicInteger()

    override fun newThread(r: Runnable): Thread {
        return object : Thread(r, mName + "-" + mNumber.getAndIncrement()) {
            override fun run() {
                // 设置线程的优先级
                Process.setThreadPriority(mPriority)
                super.run()
            }
        }
    }

}
