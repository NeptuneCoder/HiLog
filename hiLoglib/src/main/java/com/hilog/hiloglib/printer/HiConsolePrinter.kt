package com.hilog.hiloglib.printer

import android.util.Log
import com.hilog.hiloglib.HiLogConfig

class HiConsolePrinter : HiLogPrinter {
    override fun print(config: HiLogConfig, level: Int, tag: String, printString: String) {
        val len = printString.length
        val countOfSub = len / HiLogConfig.MAX_LEN
        if (countOfSub > 0) {
            var index = 0
            repeat(countOfSub) {
                Log.println(level, tag, printString.substring(index, index + HiLogConfig.MAX_LEN))
                index += HiLogConfig.MAX_LEN
            }
            if (index != len) {
                Log.println(
                    level, tag, printString.substring(
                        index, len
                    )
                )
            }
        } else {
            Log.println(
                level, tag, printString
            )
        }
    }
}