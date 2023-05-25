package com.hilog.hiloglib.printer

import java.text.SimpleDateFormat
import java.util.Locale

val sdf = SimpleDateFormat("yy-MM--dd HH:mm:ss", Locale.CHINA)

data class HiLogModel(val timeMills: Long, val level: Int, val tag: String, val log: String) {
    fun getPlattened(): String {
        return format(timeMills) + "|" + level + "|" + tag + "|"
    }

    fun format(timeMills: Long): String {
        return sdf.format(timeMills)
    }

}