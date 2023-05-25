package com.hilog.hiloglib.printer

import android.util.Log
import com.hilog.hiloglib.HiLogConfig
import com.hilog.hiloglib.utils.DateStr2Long
import com.hilog.hiloglib.utils.ThreadManager
import com.hilog.hiloglib.utils.format
import com.hilog.hiloglib.utils.getFileSimpleName
import java.io.BufferedWriter
import java.io.File
import java.io.IOException
import java.util.Arrays


class HiFilePrinter constructor(val config: HiLogConfig) : HiLogPrinter {
    private val filePath: String = config.getLogFileDir()
    private val fileDir: File = File(filePath)

    init {
        if (!fileDir.exists()) {
            fileDir.mkdirs()
        }
    }

    private fun createNewFile(): File {
        val fileName = System.currentTimeMillis().format() + ".log"
        val filePath = fileDir.absolutePath + "/" + fileName
        Log.i("tag", "filePath == " + filePath)
        val file = File(filePath)
        if (!file.exists()) {
            file.createNewFile()
        }
        return file
    }

    /**
     * 文件大小是否满足配置条件
     */
    private fun fileSizeIsFull(file: File): Boolean {
        return file.length() > config.getLogFileSize()
    }

    private fun allLogFileSizeIsFull(file: File): Boolean {
        var size: Long = 0
        if (file.isDirectory) {
            for (listFile in file.listFiles()) {
                if (listFile.name.contains(".log"))//判断是日志文件
                    size += listFile.length()
            }
        }
        return size > config.getMaxFileSize()
    }

    private fun getLastOldOrNewFile(file: File, isLastNew: Boolean = true): File? {

        var targetFile: File? = null
        var lastDate: Long = 0
        if (file.isDirectory) {
            val listFiles = file.listFiles()
            if (listFiles == null || listFiles?.isEmpty() == true) {
                return createNewFile()
            } else if (listFiles.size == 1) {
                val file = listFiles[0]
                if (fileSizeIsFull(file)) {
                    return createNewFile()
                } else {
                    return file
                }
            }
            listFiles.forEachIndexed { index, file ->
                val fileDate = file.name.getFileSimpleName().DateStr2Long()

                if (isLastNew) {//最新的日期时间越大
                    if (fileDate > lastDate) {
                        lastDate = fileDate
                        targetFile = file
                    }
                } else {//最老的事件越小
                    if (index == 0) {
                        lastDate = fileDate
                        targetFile = file
                    }
                    if (fileDate < lastDate) {
                        lastDate = fileDate
                        targetFile = file
                    }
                }
            }
        }
        if (isLastNew && fileSizeIsFull(targetFile!!)) {
            return createNewFile()
        }

        return targetFile

    }

    private fun deleteFile(file: File) {
        if (file.exists()) {
            file.delete()
        }
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

    var file: File? = null
    var bw: BufferedWriter? = null
    private fun write2local(level: Int, tag: String, substring: String) {
        val title = System.currentTimeMillis().format() + "-|-" + level + "-|-" + tag + "-|"


        if (file == null) {
            if (allLogFileSizeIsFull(fileDir)) {
                deleteFile(getLastOldOrNewFile(fileDir, false)!!)
            }
            file = getLastOldOrNewFile(fileDir, true)
            bw = BufferedWriter(java.io.FileWriter(file))
        }
        if (fileSizeIsFull(file!!)) {
            if (allLogFileSizeIsFull(fileDir)) {
                deleteFile(getLastOldOrNewFile(fileDir, false)!!)
            }
            bw?.close()//如果文件满了，则释放之前的bw对象
            bw = null
            file = getLastOldOrNewFile(fileDir, true)
            bw = BufferedWriter(java.io.FileWriter(file))
        }
        write2File(title, substring, bw!!)

    }

    val batchSize = 1000 // 每次写入的批量大小
    private fun write2File(title: String, substring: String, bw: BufferedWriter) {
        ThreadManager.executor.submit {
            val content = title + "\n" + substring
            try {
                val totalDataSize = content.length
                var startIndex = 0
                while (startIndex < totalDataSize) {
                    val endIndex = Math.min(startIndex + batchSize, totalDataSize)
                    val batchData = content.substring(startIndex, endIndex)
                    bw.write(batchData)
                    bw.flush()
                    startIndex += batchSize
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }


    }


    fun callLastNewFile() {
        //找到所有的文件，注入到集合中
        val files = mutableListOf<File>()
        val file = File(config.getLogFileDir())
        val lastOldOrNewFile = getLastOldOrNewFile(file, true)
        files.add(lastOldOrNewFile!!)
        val iUploadLogFile = config.getIUploadLogFile()
        iUploadLogFile?.uploadLogFile(file = files)
    }

    fun callAllFile() {
        //找到所有的文件，注入到集合中
        val files = mutableListOf<File>()
        val file = File(config.getLogFileDir())
        if (file.exists()) {
            file.listFiles().forEach {
                if (it.name.contains(".log")) {
                    files.add(it)
                }
            }
        }
        config.getIUploadLogFile()?.uploadLogFile(file = files)
    }
}


