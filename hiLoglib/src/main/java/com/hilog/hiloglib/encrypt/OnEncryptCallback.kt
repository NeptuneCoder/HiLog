package com.hilog.hiloglib.encrypt

interface OnEncryptCallback {
    fun encrypt(content: String): String
}