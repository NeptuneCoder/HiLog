package com.hilog.hiloglib.printer

import androidx.annotation.NonNull
import com.hilog.hiloglib.HiLogConfig
import com.hilog.hiloglib.HiLogType

interface HiLogPrinter {
    fun print(
        @NonNull config: HiLogConfig,
        @HiLogType.Type level: Int,
        tag: String,
        @NonNull printString: String
    )

}