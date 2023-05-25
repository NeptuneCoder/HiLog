package com.hilog.hiloglib

import android.app.Application
import com.hilog.hiloglib.format.HiStackTraceFormatter
import com.hilog.hiloglib.format.HiThreadFormatter
import com.hilog.hiloglib.printer.HiLogPrinter
import java.io.File

abstract class HiLogConfig {
    companion object {
        val MAX_LEN = 512
        val HI_STACK_TRACE_FORMATTER = HiStackTraceFormatter()
        val HI_THREAD_FORMATTER = HiThreadFormatter()
    }

    private val printersList = mutableListOf<HiLogPrinter>()
    open fun getPrinter(): MutableList<HiLogPrinter> {
        return printersList
    }

    open fun injectJsonParser(): JsonParser? {
        return null
    }


    /**
     * 判断是否需要打印线程信息
     */
    open fun includeThread(): Boolean {
        return false
    }

    open fun isOpenGlobalFloatingWidget(): Boolean {
        return false
    }

    /**
     * 配置获取堆栈深度
     */
    open fun stackTraceDepth(): Int {
        return 5
    }


    open fun getGlobalTag(): String {
        return "hiLog"
    }

    open fun enable(): Boolean {
        return true
    }

    interface JsonParser {
        fun toJson(src: Any): String
    }

    open fun getApplication(): Application? {
        return null
    }

    open fun getIUploadLogFile(): IUploadLogFile? {
        return null
    }

    interface IUploadLogFile {
        fun uploadLogFile(file: Array<File>)
    }

    open fun isSaveLogFile(): Boolean {
        return false
    }

    open fun getLogFileDir(): String {
        return "${getApplication()?.cacheDir?.absolutePath}"
    }

    /**
     * 创建单个文件大小
     */
    open fun getLogFileSize(): Int {
        return 1000
    }

    open fun getMaxFileSize(): Int {
        return 1024 * 1024 * 5
    }


}