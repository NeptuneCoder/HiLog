package com.hilog.hiloglib

import android.app.Application
import com.hilog.hiloglib.printer.HiFilePrinter
import com.hilog.hiloglib.printer.HiLogPrinter
import com.hilog.hiloglib.printer.HiViewPrinter
import java.io.File


object HiLogManager {

    private var hiConfig: HiLogConfig? = null
    private fun init(hiConfig: HiLogConfig) {
        this.hiConfig = hiConfig
        if (hiConfig.isOpenGlobalFloatingWidget() && BuildConfig.DEBUG) {
            val app = hiConfig.getApplication()
            val hiViewPrinter = HiViewPrinter(app!!)
            hiConfig.getPrinter().add(hiViewPrinter)
        }
        if (hiConfig.isSaveLogFile()) {
            hiConfig.getPrinter().add(HiFilePrinter(config = hiConfig))
        }
    }

    @JvmStatic
    fun getConfig(): HiLogConfig {
        return hiConfig!!
    }


    /**
     * 回调最新的日志文件
     */
    @JvmStatic
    fun sendLastNewLogFile() {
        hiConfig?.getPrinter()?.forEach {
            if (it is HiFilePrinter) {
                it.callLastNewFile()
            }
        }
    }

    /**
     * 回调所有的日志文件
     */
    @JvmStatic
    fun sendAllLogFile() {
        hiConfig?.let {
            it.getPrinter().forEach {
                if (it is HiFilePrinter) {
                    it.callAllFile()
                }
            }
        }
    }

    /**
     * 删除日志文件
     */
    @JvmStatic
    fun deleteLogFile(file: File) {
        if (file.exists()) {
            file.delete()
        }
    }

    class Factory {
        private var maxFileSize: Int = 1024 * 1024 * 5
        private var logFileSize: Int = 1024 * 1024 * 1
        private var logFileDir: String = "/"
        private var isSaveLogFile: Boolean = false
        private var iLogFile: HiLogConfig.IUploadLogFile? = null
        private var app: Application? = null
        private var globalTag: String = "HiLog"
        private var stackTraceDepth: Int = 5
        private var isOpenGlobalFloatingWidget: Boolean = false
        private var isIncludeThread: Boolean = false
        private var jsonParser: HiLogConfig.JsonParser? = null
        private val printers = mutableListOf<HiLogPrinter>()


        fun addPrinter(printer: HiLogPrinter): Factory {
            printers.add(printer)
            return this
        }

        fun setJsonParser(jsonParser: HiLogConfig.JsonParser): Factory {
            this.jsonParser = jsonParser
            return this
        }

        fun setIsIncludeThread(isIncludeThread: Boolean): Factory {
            this.isIncludeThread = isIncludeThread
            return this
        }

        fun setIsOpenGlobalFloatingWidget(isOpenGlobalFloatingWidget: Boolean): Factory {
            this.isOpenGlobalFloatingWidget = isOpenGlobalFloatingWidget
            return this
        }

        fun setStackTraceDepth(stackTraceDepth: Int): Factory {
            this.stackTraceDepth = stackTraceDepth
            return this
        }

        fun setGlobalTag(globalTag: String): Factory {
            this.globalTag = globalTag
            return this
        }

        fun setApplication(app: Application): Factory {
            this.app = app
            return this
        }

        fun setUploadLogFileCallback(iLogFile: HiLogConfig.IUploadLogFile): Factory {
            this.iLogFile = iLogFile
            return this
        }

        fun setIsSaveLogFile(isSaveLogFile: Boolean): Factory {
            this.isSaveLogFile = isSaveLogFile
            return this
        }

        fun setLogFileDir(logFileDir: String): Factory {
            this.logFileDir = logFileDir
            return this
        }

        fun setLogFileSize(logFileSize: Int): Factory {
            this.logFileSize = logFileSize
            return this
        }

        /**
         * 1024 == 1k
         */
        fun setMaxFileSize(maxFileSize: Int): Factory {
            this.maxFileSize = maxFileSize
            return this
        }


        fun build() {
            checkParams()
            // 构建Config对象
            val config = object : HiLogConfig() {
                override fun getPrinter(): MutableList<HiLogPrinter> {
                    return printers
                }

                override fun injectJsonParser(): JsonParser? {
                    return jsonParser
                }

                override fun includeThread(): Boolean {
                    return isIncludeThread
                }

                override fun isOpenGlobalFloatingWidget(): Boolean {
                    return isOpenGlobalFloatingWidget
                }

                override fun stackTraceDepth(): Int {
                    return stackTraceDepth
                }

                override fun getGlobalTag(): String {
                    return globalTag
                }

                override fun getApplication(): Application? {
                    return app
                }

                override fun getIUploadLogFile(): IUploadLogFile? {
                    return iLogFile
                }

                override fun isSaveLogFile(): Boolean {
                    return isSaveLogFile
                }

                override fun getLogFileDir(): String {
                    return logFileDir
                }

                override fun getLogFileSize(): Int {
                    return logFileSize
                }

                override fun getMaxFileSize(): Int {
                    return maxFileSize
                }
            }

            init(config)

        }

        /**
         * 检查参数的合理性
         */
        private fun checkParams() {
            if (isSaveLogFile) {
                //如果要保存日志，则存放在系统的默认文件夹下面
                logFileDir = "${app?.cacheDir?.absolutePath}"
            }
        }


    }
}
