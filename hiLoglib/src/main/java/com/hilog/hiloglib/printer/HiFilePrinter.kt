package com.hilog.hiloglib.printer

import androidx.work.WorkManager
import com.hilog.hiloglib.HiLogConfig
import com.hilog.hiloglib.callback.ErrorType
import com.hilog.hiloglib.encrypt.DefaultEncryptUtil
import com.hilog.hiloglib.utils.DateStr2Long
import com.hilog.hiloglib.utils.ThreadManager
import com.hilog.hiloglib.utils.format
import com.hilog.hiloglib.utils.getFileSimpleName
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.nio.charset.StandardCharsets


class HiFilePrinter constructor(val config: HiLogConfig) : HiLogPrinter {
    private val filePath: String = config.getLogFileDir()
    private val fileDir: File = File(filePath)
    val work by lazy {
        WorkManager.getInstance(config.getApplication()!!).apply {

        }
    }

    init {
        if (!fileDir.exists()) {
            fileDir.mkdirs()
        }
    }

    private fun createNewFile(): File {
        val fileName = System.currentTimeMillis().format() + ".log"
        val filePath = fileDir.absolutePath + "/" + fileName
        val file = File(filePath)
        try {
            if (!file.exists()) {
                file.createNewFile()
            }
        } catch (e: Exception) {
            config.getStateCallback()?.onError(ErrorType.CREATE_FILE_ERROR, e.toString())
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

    private fun getLastOldOrNewFile(file: File, isLastNew: Boolean = true): File {

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

        return targetFile!!

    }

    private fun deleteFile(file: File) {
        if (file.exists()) {
            file.delete()
        }
    }


    override fun print(config: HiLogConfig, level: Int, tag: String, printString: String) {

        var title = System.currentTimeMillis().format() + "-|-" + level + "-|-" + tag + "-|"
        val newStr = title + "\n" + printString
        if (config.useDefaultEncrypt()) {
            val temRes = DefaultEncryptUtil.encrypt(
                newStr,
                config.getDefaultEncryptKey().toByteArray()
            )

            write2local(temRes + "<segment>".toByteArray())


        } else if (config.useCustomEncrypt()) {
            val encryptCallback = config.getEncryptCallback()

            write2local(encryptCallback?.encrypt(newStr) ?: "".toByteArray(Charsets.UTF_8))
        } else {
            newStr
            write2local(newStr.toByteArray(Charsets.UTF_8))
        }

    }

    var file: File? = null

    var randomAccessFile: RandomAccessFile? = null
    var fileChannel: FileChannel? = null
    var mappedBuffer: MappedByteBuffer? = null
    private fun write2local(content: ByteArray) {
        if (file == null) {
            if (allLogFileSizeIsFull(fileDir)) {
                deleteFile(getLastOldOrNewFile(fileDir, false))
            }
            file = getLastOldOrNewFile(fileDir, true)

            randomAccessFile = RandomAccessFile(file, "rw")
        }
        if (fileSizeIsFull(file!!)) {
            if (allLogFileSizeIsFull(fileDir)) {
                deleteFile(getLastOldOrNewFile(fileDir, false))
            }
            randomAccessFile?.close()//如果文件满了，则释放之前的bw对象
            randomAccessFile = null
            file = getLastOldOrNewFile(fileDir, true)
            randomAccessFile = RandomAccessFile(file, "rw")
        }
        fileChannel = randomAccessFile?.channel

        mappedBuffer =
            fileChannel!!.map(
                FileChannel.MapMode.READ_WRITE,
                file?.length() ?: 0,
                content.size.toLong()
            )
        write2File(content, mappedBuffer!!)

    }

    private fun write2File(content: ByteArray, mappedBuffer: MappedByteBuffer) {
        ThreadManager.executor.submit {
            try {

                mappedBuffer.put(content)
                mappedBuffer.force()
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
        files.add(lastOldOrNewFile)
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


