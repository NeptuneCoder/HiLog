package com.hilog.hilog

import android.app.Application
import com.alibaba.fastjson.JSON
import com.hilog.hiloglib.HiLog
import com.hilog.hiloglib.printer.HiConsolePrinter
import com.hilog.hiloglib.HiLogConfig
import com.hilog.hiloglib.HiLogManager
import java.io.File


class App : Application() {
    override fun onCreate() {
        super.onCreate()

        HiLogManager.Factory()
            .setIsIncludeThread(true)
            .setIsOpenGlobalFloatingWidget(true)
            .setGlobalTag("MyApplication")

            .setStackTraceDepth(5)
            .addPrinter(HiConsolePrinter())
            .setApplication(this)
            .setIsSaveLogFile(true)
            .setLogFileDir(this.cacheDir.absolutePath)
            .setMaxFileSize(1024 * 60)
            .setLogFileSize(1024 * 10)
            .setUploadLogFileCallback(object : HiLogConfig.IUploadLogFile {
                override fun uploadLogFile(file: MutableList<File>) {
                    HiLog.i("回调日志文件个数：" + file.size)
                    //TODO 该处可自行上传文件到服务器
                }

            }).setJsonParser(object : HiLogConfig.JsonParser {
                override fun toJson(src: Any): String {
                    return JSON.toJSONString(src)
                }
            }).build()



    }


}