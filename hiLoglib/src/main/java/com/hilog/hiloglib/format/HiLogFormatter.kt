package com.hilog.hiloglib.format

/**
 * 不管什么数据格式化后都是一个String
 */
interface HiLogFormatter<T> {

    fun format(data: T): String
}