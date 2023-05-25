package com.hilog.hiloglib.format

class HiThreadFormatter : HiLogFormatter<Thread> {
    override fun format(data: Thread): String {
        return "Thread:" + data.name
    }
}