package com.hilog.hiloglib.encrypt

import java.nio.charset.StandardCharsets
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object DefaultEncryptUtil {
    @JvmStatic
    fun encrypt(data: String, key: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        val secretKey = SecretKeySpec(key, "AES")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        return cipher.doFinal(data.toByteArray(StandardCharsets.UTF_8))
    }

    // 解密
    @JvmStatic
    fun decrypt(data: ByteArray, key: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        val secretKey = SecretKeySpec(key, "AES")
        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        return cipher.doFinal(data)
    }
}