package cn.flyfun.gamesdk.core.utils

import android.content.Context
import android.text.TextUtils
import cn.flyfun.gamesdk.base.utils.Logger
import cn.flyfun.gamesdk.core.network.Host
import cn.flyfun.support.HostModelUtils
import cn.flyfun.support.encryption.aes.AesUtils
import java.io.*

/**
 * @author #Suyghur.
 * Created on 2020/12/10
 */
object FileUtils {

    private const val AES_KEY = "flyfunoverseasdk"
    private const val USER_DAT = "USER.DAT"
    private const val USER_TEST_DAT = "USER_TEST.DAT"
    private const val USER_DEV_DAT = "USER_DEV.DAT"

    fun getUserInfoFilePath(context: Context): String {
        val filePath = getUserInfoBySDCard(context)
        val file = File(filePath)
        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    Logger.d("创建用户文件成功:$filePath")
                } else {
                    Logger.e("创建用户文件失败:$filePath")
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return filePath
    }


    private fun getUserInfoBySDCard(context: Context): String {
        var fileName = ""
        when (Host.IP_MODEL) {
            HostModelUtils.ENV_DEV -> {
                fileName = USER_DEV_DAT
                Logger.d("dev环境,读取文件:$fileName")
            }
            HostModelUtils.ENV_TEST -> {
                fileName = USER_TEST_DAT
                Logger.d("test环境,读取文件:$fileName")
            }
            HostModelUtils.ENV_ONLINE -> fileName = USER_DAT
        }
        return context.getExternalFilesDir(null)?.absolutePath + File.separator + fileName
    }

    /**
     * 从文件中读取数据并解密
     *
     * @param filePath
     * @return
     */
    fun readFile(filePath: String): String {
        var content = StringBuilder()
        try {
            val file = File(filePath)
            // 判断文件是否存在,如果不存在则返回空
            if (!file.exists() || !file.isFile) {
                return ""
            }
            val reader = BufferedReader(FileReader(file))
            var tempString: String?
            while (reader.readLine().also { tempString = it } != null) {
                content.append(tempString)
            }
            reader.close()

            // AES解密
            content = if (!TextUtils.isEmpty(content.toString())) {
                StringBuilder(AesUtils.decrypt(AES_KEY, content.toString()))
            } else {
                return ""
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return content.toString()
    }

    /**
     * 将数据写入到文件中保存并加密
     *
     * @param content  内容
     * @param filePath 文件路径
     */
    fun writeFile(content: String, filePath: String) {
        try {
            val file = File(filePath)
            //取文件的上级目录
            val fileDir = filePath.substring(0, filePath.lastIndexOf(File.separator) + 1)
            val parentFile = File(fileDir)
            //如果文件夹不存在或者不是文件夹，则创建文件夹
            if (!parentFile.exists() || !parentFile.isDirectory) {
                parentFile.mkdirs()
            }
            //如果文件不存在，则创建一个文件
            if (!file.exists() || !file.isFile) {
                file.createNewFile()
            }
            val fileWriter = FileWriter(file, false)

            //AES加密
            val enc = AesUtils.encrypt(AES_KEY, content)
            if (!TextUtils.isEmpty(enc)) {
                fileWriter.write(enc)
            }
            fileWriter.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}