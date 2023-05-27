package com.hilog.decryptlib


import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

fun main() {
    val file = File("/Users/yh/Desktop/23-05-26 17_00_46.log")
    if (file.exists()) {
        val key = "232sxdasxzaasfaa232sxdasxzaasfaa".toByteArray()
        val content =
            file?.readText(StandardCharsets.UTF_8)?.split("<segment>")

        content?.forEach {
            try {
                System.out.println("it len = " + it.length)
                val res = DecryptUtil.decrypt(
                    it.toByteArray(),
                    key
                )
                System.out.println(
                    res.toString(Charsets.UTF_8)
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


    }


}


object DecryptUtil {
    fun readEncryptedDataFromFile(filePath: String): ByteArray {
        return Files.readAllBytes(Paths.get(filePath))
    }

    fun hexToString(hexString: String): String? {
        val strBuilder = java.lang.StringBuilder()
        var i = 0
        while (i < hexString.length - 1) {
            val hex = hexString.substring(i, i + 2)
            val decimal = hex.toInt(16)
            strBuilder.append(decimal.toChar())
            i += 2
        }
        return strBuilder.toString()
    }

    @JvmStatic
    fun decrypt(data: ByteArray, key: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        val secretKey = SecretKeySpec(key, "AES")
        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        return cipher.doFinal(data)
    }

    fun encrypt(data: ByteArray, key: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        val secretKey = SecretKeySpec(key, "AES")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        return cipher.doFinal(data)
    }

}