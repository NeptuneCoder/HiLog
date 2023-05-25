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

            .setMaxFileSize(1024 * 1024 * 3)
            .setLogFileSize(1024 * 1024 / 2)
            .setUploadLogFileCallback(object : HiLogConfig.IUploadLogFile {
                override fun uploadLogFile(file: Array<File>) {
                    HiLog.i("回调日志文件个数：" + file.size)
                    //TODO 该处可自行上传文件到服务器
                }

            }).setJsonParser(object : HiLogConfig.JsonParser {
                override fun toJson(src: Any): String {
                    return JSON.toJSONString(src)
                }
            })
            .build()
    }
}