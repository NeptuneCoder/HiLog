package com.hilog.hiloglib

import android.util.Log
import androidx.annotation.NonNull
import com.hilog.hiloglib.encrypt.DefaultEncryptUtil
import com.hilog.hiloglib.printer.HiFilePrinter
import com.hilog.hiloglib.utils.HiStackTraceUtil

import java.lang.IllegalArgumentException
import java.lang.StringBuilder

object HiLog {
    private val clazzName = HiLog::class.java.name
    private val HI_LOG_PACKAGE = clazzName.substring(0, clazzName.lastIndexOf(".") + 1)
    fun v(vararg args: Any) {
        log(HiLogType.A, contents = args)
    }

    fun vt(tag: String = "", vararg args: Any) {
        log(HiLogType.A, tag, *args)
    }

    fun d(vararg args: Any) {
        log(HiLogType.D, contents = args)
    }

    fun dt(tag: String = "", vararg args: Any) {
        log(HiLogType.D, tag, *args)
    }

    fun i(vararg args: Any) {
        log(HiLogType.I, contents = args)
    }

    fun it(tag: String = "", vararg args: Any) {
        log(HiLogType.I, tag, *args)
    }

    fun w(vararg args: Any) {
        log(HiLogType.I, contents = args)
    }

    fun wt(tag: String = "", vararg args: Any) {
        log(HiLogType.I, tag, *args)
    }

    fun e(vararg args: Any) {
        log(HiLogType.E, contents = *args)
    }

    fun et(tag: String = "", vararg args: Any) {
        log(HiLogType.E, tag, *args)
    }

    fun a(vararg args: Any) {
        log(HiLogType.A, contents = *args)
    }

    fun at(tag: String, vararg args: Any) {
        log(HiLogType.A, tag, *args)
    }


    private fun log(
        @HiLogType.Type type: Int,
        @NonNull tag: String = HiLogManager.getConfig().getGlobalTag(),
        vararg contents: Any
    ) {
        log(HiLogManager.getConfig(), type, tag, *contents)
    }

    val sb = StringBuilder()
    fun log(
        @NonNull config: HiLogConfig,
        @HiLogType.Type type: Int,
        @NonNull tag: String,
        vararg contents: Any
    ) {
        if (!config.enable()) {
            throw IllegalArgumentException("请配置config信息")
            return
        }
        sb.clear()
        if (config.includeSystemLog()) {
            val sysLogInfo = HiLogConfig.HI_SYS_FORMATTER.format(ProcessBuilder("logcat", "-d"))
            sb.append(sysLogInfo).append("\n")
        }
        if (config.includeThread()) {
            val threadInfo = HiLogConfig.HI_THREAD_FORMATTER.format(Thread.currentThread())
            sb.append(threadInfo).append("\n")
        }

        if (config.stackTraceDepth() > 0) {

            val res = HiStackTraceUtil.getCropRealStackTrack(
                Throwable().stackTrace.toList().toTypedArray(),
                HI_LOG_PACKAGE,
                config.stackTraceDepth()
            )
            val stackTrace = HiLogConfig.HI_STACK_TRACE_FORMATTER.format(res)
            sb.append(stackTrace).append("\n")
        }
        val body = parseBody(config, *contents)
        sb.append(body)
        val printers = config.getPrinter()
        if (printers.isNotEmpty()) {
            printers.forEach {
                if (it is HiFilePrinter) {
                    val content = sb.toString()
                    if (config.useDefaultEncrypt()) {
                        val encryptBeforeStr = DefaultEncryptUtil.encrypt(
                            content.toByteArray(),
                            config.getDefaultEncryptKey().toByteArray()
                        ).toString(Charsets.UTF_8) ?: ""
                        it.print(config, type, tag, encryptBeforeStr)
                    } else {
                        val encryptBeforeStr = config.getEncryptCallback()?.encrypt(content) ?: ""
                        it.print(config, type, tag, encryptBeforeStr)
                    }
                } else {
                    it.print(config, type, tag, sb.toString())
                }
            }
        }


    }

    private fun parseBody(@NonNull config: HiLogConfig, vararg contents: Any): String {
        if (config.injectJsonParser() != null) {
            return "${config.injectJsonParser()?.toJson(contents)}"
        }
        val sb = StringBuilder()
        for (v in contents) {
            sb.append(v.toString()).append(";")
        }


        if (sb.isNotEmpty()) {
            sb.deleteCharAt(sb.length - 1)
        }
        return sb.toString()
    }
}