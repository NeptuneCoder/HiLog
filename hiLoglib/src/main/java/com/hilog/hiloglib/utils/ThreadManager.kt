package com.hilog.hiloglib.utils

import androidx.work.WorkManager
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

object ThreadManager {


    val corePoolSize = 2
    val maxPoolSize = 10
    val keepAliveTime = 10L // 单位为秒
    val executor = ThreadPoolExecutor(
        corePoolSize,
        maxPoolSize,
        keepAliveTime,
        TimeUnit.SECONDS,
        LinkedBlockingQueue<Runnable>()
    )


}