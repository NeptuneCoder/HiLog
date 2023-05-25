package com.hilog.hiloglib.utils

import java.util.concurrent.Executors

object ThreadManager {
    val executor = Executors.newCachedThreadPool()
}