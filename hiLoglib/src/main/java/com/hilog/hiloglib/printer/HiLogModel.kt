package com.hilog.hiloglib.printer

import com.hilog.hiloglib.utils.format

data class HiLogModel(val timeMills: Long, val level: Int, val tag: String, val log: String) {
    fun getPlattened(): String {
        return timeMills.format() + "-|-" + level + "-|-" + tag + "-|"
    }


}