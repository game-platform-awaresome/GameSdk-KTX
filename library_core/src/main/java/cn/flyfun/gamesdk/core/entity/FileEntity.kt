package cn.flyfun.gamesdk.core.entity

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException

/**
 * @author #Suyghur,
 * Created on 2021/1/21
 */
data class FileEntity(
        var name: String,
        var fileName: String,
        var file: File,
        var mime: String = "application/octet-stream") {

    fun getFileBytes(): ByteArray? {
        var fis: FileInputStream? = null
        var baos: ByteArrayOutputStream? = null
        try {
            fis = FileInputStream(file)
            baos = ByteArrayOutputStream()
            var len: Int
            val bytes = ByteArray(1024)
            while (fis.read(bytes).also { len = it } != -1) {
                baos.write(bytes, 0, len)
            }
            return baos.toByteArray()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            fis?.close()
            baos?.close()
        }
        return null
    }


}