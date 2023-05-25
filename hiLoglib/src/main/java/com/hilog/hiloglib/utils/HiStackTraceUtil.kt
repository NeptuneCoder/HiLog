package com.hilog.hiloglib.utils


object HiStackTraceUtil {

    fun getCropRealStackTrack(
        stackTrace: Array<StackTraceElement?>,
        ignorePackName: String,
        maxDepth: Int
    ): Array<StackTraceElement?> {
        return cropStackTrace(getRealStackTrack(stackTrace, ignorePackName), maxDepth)
    }

    /**
     * 裁剪堆栈信息
     */
    private fun cropStackTrace(
        callStack: Array<StackTraceElement?>,
        maxDepth: Int
    ): Array<StackTraceElement?> {
        var realDepth = callStack.size
        if (maxDepth > 0) {
            realDepth = Math.min(maxDepth, realDepth)
        }

        val realStack = arrayOfNulls<StackTraceElement>(realDepth)
        System.arraycopy(callStack, 0, realStack, 0, realDepth)
        return realStack
    }

    private fun getRealStackTrack(
        stackTrace: Array<StackTraceElement?>,
        ignorePackName: String
    ): Array<StackTraceElement?> {
        var ignoreDepth: Int = 0
        var allDeath = stackTrace.size
        var clazzName: String = ""
        for (i in (allDeath - 1) downTo 0) {
            clazzName = stackTrace[i]!!.className
            if (ignorePackName != null && clazzName.startsWith(ignorePackName)) {
                ignoreDepth = i + 1
                break
            }
        }
        val realDepth = allDeath - ignoreDepth
        val realStack = arrayOfNulls<StackTraceElement>(realDepth)
        System.arraycopy(stackTrace, 0, realStack, 0, realDepth)
        return realStack
    }
}