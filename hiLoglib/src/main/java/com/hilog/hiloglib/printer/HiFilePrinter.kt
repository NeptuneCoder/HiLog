package com.hilog.hiloglib.printer

import android.util.Log
import com.hilog.hiloglib.HiLogConfig
import java.io.File

class HiFilePrinter constructor(val config: HiLogConfig) : HiLogPrinter {
    val filePath: String = config.getLogFileDir()
    val fileDir: File = File(filePath)

    init {
        if (!fileDir.exists()) {
            fileDir.mkdirs()
        }
    }

    fun getLastNewFile(): File? {
        if (fileDir.listFiles().isEmpty()) {
            //如果一个文件都没有则创建文件
        } else {
            //如果存在文件则找到文件日期最新的那个
        }
        //TODO 判断文件的大小，如果大于文件大小，config.getLogFileSize(),则进行重新创建
        return null
    }

    override fun print(config: HiLogConfig, level: Int, tag: String, printString: String) {

        //todo 日志写入到本地文件中，考虑文件太大，所有切成多个文件进行保存
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
                write2local(
                    level, tag, printString.substring(
                        index, len
                    )
                )
            }
        } else {
            write2local(
                level, tag, printString
            )
        }
    }

    private fun write2local(level: Int, tag: String, substring: String) {

    }


    fun callLastNewFile() {
        //找到所有的文件，注入到集合中
        val files = mutableListOf<File>()
        val iUploadLogFile = config.getIUploadLogFile()
        iUploadLogFile?.uploadLogFile(file = files.toTypedArray())

    }

    fun callAllFile() {
        //找到所有的文件，注入到集合中
        val files = mutableListOf<File>()
        config.getIUploadLogFile()?.uploadLogFile(file = files.toTypedArray())

    }
}