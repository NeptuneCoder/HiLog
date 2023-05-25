package com.hilog.hiloglib.format

import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.StringBuilder

class HiSysFormatter : HiLogFormatter<ProcessBuilder> {
    override fun format(processBuilder: ProcessBuilder): String {

        val process = processBuilder.start()
        val bufferedReader = BufferedReader(InputStreamReader(process.inputStream))
        var sb = StringBuilder()
        var line: String? = null
        while (bufferedReader.readLine().also { line = it } != null) {
            // 处理日志行
            sb.append(line).append("\n")
            Log.i("SysLog", "$line")
        }

        return sb.toString()
    }
}