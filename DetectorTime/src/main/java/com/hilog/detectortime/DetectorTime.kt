package com.hilog.detectortime

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.URI

fun getVideoDuration(filePath: File): Long {

    if (!filePath.exists()) {
        println("文件不存在")
        return 0
    }
    val processBuilder = ProcessBuilder("ffmpeg", "-i", filePath.absolutePath)
    val process = processBuilder.start()
    val reader = BufferedReader(InputStreamReader(process.inputStream))
    var line: String?
    var duration: Long = 0

    while (reader.readLine().also { line = it } != null) {
        if (line!!.contains("Duration")) {
            val durationString =
                line!!.substring(line!!.indexOf("Duration:") + 10, line!!.indexOf(","))
            val timeComponents = durationString.split(":")
            val hours = timeComponents[0].toLong()
            val minutes = timeComponents[1].toLong()
            val seconds = timeComponents[2].split(".")[0].toLong()
            duration = hours * 3600 + minutes * 60 + seconds
            break
        }
    }

    reader.close()
    return duration
}

fun main() {
    val filePath = ""

    findAllMp4File(filePath)

    println("总时长为 = " + totalTime)

}


var totalTime: Long = 0
val eachMp4Time = mutableMapOf<String, Long>()
fun findAllMp4File(dir: String) {
    val file = File(dir)
    if (file.isDirectory) {
        file.listFiles().forEach {
            if (it.isDirectory) {
                findAllMp4File(it.absolutePath)
            } else if (it.isFile) {
                formatFileName(it)
//                isMp4File(it)
            }
        }
    } else {
//        isMp4File(file)
        formatFileName(file)
    }


}

fun formatFileName(file: File) {


}

fun isMp4File(it: File) {
    if (it.name.contains(".mp4")) {
        val fielName = it.name
        val duration = getVideoDuration(it)
        duration.let {
            eachMp4Time.put(fielName, duration)
            totalTime += it
        }
    }
}



