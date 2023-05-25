package com.hilog.hiloglib

import android.util.Log
import androidx.annotation.IntDef

object HiLogType {
    @IntDef(V, D, I, W, E, A)
    annotation class Type

    const val V = Log.VERBOSE
    const val D = Log.DEBUG
    const val I = Log.INFO
    const val W = Log.WARN
    const val E = Log.ERROR
    const val A = Log.ASSERT
}