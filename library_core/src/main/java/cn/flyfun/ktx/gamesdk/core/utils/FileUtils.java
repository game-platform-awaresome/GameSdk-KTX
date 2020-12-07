package cn.flyfun.ktx.gamesdk.core.utils;

import android.content.Context;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import cn.flyfun.ktx.gamesdk.base.utils.Logger;
import cn.flyfun.ktx.gamesdk.core.network.Host;
import cn.flyfun.support.HostModelUtils;
import cn.flyfun.support.encryption.aes.AesUtils;

/**
 * @author #Suyghur.
 * Created on 2020/8/4
 */
public class FileUtils {

    private static final String AES_KEY = "flyfunoverseasdk";

    public static final String USER_DAT = "USER.DAT";
    public static final String USER_TEST_DAT = "USER_TEST.DAT";
    public static final String USER_DEV_DAT = "USER_DEV.DAT";


    public static String getUserInfoFilePath(Context context) {
        String filePath = getUserInfoFileBySDCard(context);
        //LogKKK.d("getUserInfoFilePath：" + filePath);
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    Logger.d("创建用户文件成功：" + filePath);
                } else {
                    Logger.e("创建用户文件失败：" + filePath);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        Logger.d("返回新版SD卡文件路径：" + filePath);
        return filePath;
    }

    private static String getUserInfoFileBySDCard(Context context) {
        String fileName = null;
        switch (Host.IP_MODEL) {
            case HostModelUtils.ENV_DEV:
                fileName = USER_DEV_DAT;
                Logger.d("dev环境，读取文件：" + fileName);
                break;
            case HostModelUtils.ENV_TEST:
                //test
                fileName = USER_TEST_DAT;
                Logger.d("test环境，读取文件：" + fileName);
                break;
            case HostModelUtils.ENV_ONLINE:
                //online
                fileName = USER_DAT;
                break;
        }
        return context.getExternalFilesDir(null).getAbsolutePath() + "/" + fileName;
    }

    /**
     * 从文件中读取数据
     *
     * @return
     */
    public static String readFile(String filePath) {
        StringBuilder content = new StringBuilder();
        try {
            File file = new File(filePath);
            // 判断文件是否存在,如果不存在则返回空
            if (!file.exists() || !file.isFile()) {
                return "";
            }
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String tempString = null;

            while ((tempString = reader.readLine()) != null) {
                content.append(tempString);
            }
            reader.close();

            // AES解密
            if (!TextUtils.isEmpty(content.toString())) {
                content = new StringBuilder(AesUtils.decrypt(AES_KEY, content.toString()));
            } else {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    /**
     * 将数据写入到文件中保存
     *
     * @param content  内容
     * @param filePath 文件路径
     */
    public static void writeFile(String content, String filePath) {
        try {
            File file = new File(filePath);
            // 取文件的上级目录
            String fileDir = filePath.substring(0, filePath.lastIndexOf("/") + 1);
            File parentFile = new File(fileDir);
            // 如果文件夹不存在或者不是文件夹,则创建文件夹
            if (!parentFile.exists() || !parentFile.isDirectory()) {
                parentFile.mkdirs();
            }
            // 如果文件不存在,则创建一个文件
            if (!file.exists() || !file.isFile()) {
                file.createNewFile();
            }

            FileWriter fileWriter = new FileWriter(file, false);

            // AES加密
            content = AesUtils.encrypt(AES_KEY, content);

            if (!TextUtils.isEmpty(content)) {
                fileWriter.write(content);
            }
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
