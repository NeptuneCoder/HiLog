package com.hilog.hiloglib.format

import java.lang.StringBuilder

class HiStackTraceFormatter : HiLogFormatter<Array<StackTraceElement?>> {
    override fun format(data: Array<StackTraceElement?>): String {
        val sb = StringBuilder(128)
        val len = data.size
        if (data == null || data.isEmpty()) {
            return ""
        } else if (data.size == 1) {
            return "\t-" + data[0].toString()
        } else {
            data.forEachIndexed { index, v ->
                if (index == 0) {
                    sb.append("stackTrace:\n")
                }
                if (index == len - 1) {
                    sb.append("\t|-")
                    sb.append(v.toString())
                    sb.append("\n")
                } else {
                    sb.append("\t")
                    sb.append(v.toString())
                    sb.append("\n")
                }
            }
        }

        return sb.toString()
    }
}