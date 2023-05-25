package com.hilog.hiloglib.utils

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale


fun Int.dp2px(context: Context): Int {
    val density = context.resources.displayMetrics.density
    return (this * density).toInt()
}

fun getScreenWidth(context: Context): Int {
    val displayMetrics = context.resources.displayMetrics
    val screenWidth = displayMetrics.widthPixels
    return screenWidth
}


private val sdf = SimpleDateFormat("yy-MM-dd HH:mm:ss", Locale.CHINA)
fun Long.format(sdformat: SimpleDateFormat = sdf): String {
    return sdformat.format(this)
}

fun String.DateStr2Long(sdformat: SimpleDateFormat = sdf): Long {
    return sdformat.parse(this)?.time ?: System.currentTimeMillis()
}

fun String.getFileSimpleName(): String {
    val len = this.length
    val parts: List<String> = this.split("/")
    val lastName = parts[parts.size - 1].split(".")
    return lastName[0]
}


