package cn.flyfun.ktx.gamesdk.core.utils

import android.content.Context
import android.text.TextUtils
import cn.flyfun.ktx.gamesdk.base.utils.Logger
import cn.flyfun.ktx.gamesdk.core.entity.Session
import cn.flyfun.support.JsonUtils
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


/**
 * @author #Suyghur.
 * Created on 2020/12/9
 */
class SessionUtils private constructor() {

    companion object {
        fun getInstance(): SessionUtils {
            return SessionUtilsHolder.INSTANCE
        }
    }

    private object SessionUtilsHolder {
        val INSTANCE: SessionUtils = SessionUtils()
    }


    @Synchronized
    fun saveSession(context: Context, currentSession: Session) {
        val userList = getLocalSession(context)
        userList.apply {
            if (size > 0) {
                var isSessionExist = false
                for (item in userList) {
                    if (currentSession.userId == item.userId) {
                        isSessionExist = true
                        //移除
                        userList.remove(item)
                        userList.add(0, currentSession)
                        break
                    }
                }
                if (!isSessionExist) {
                    //当前登录用户不存在用户列表里，在0的位置插入
                    userList.add(0, currentSession)
                }
            } else {
                userList.add(currentSession)
            }

            val jsonArray = JSONArray()
            val jsonObject = JSONObject()
            for (item in userList) {
                jsonArray.put(item.toJSONObject())
            }
            val filePath = FileUtils.getUserInfoFilePath(context)
            try {
                jsonObject.put("info", jsonArray)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            FileUtils.writeFile(jsonObject.toString(), filePath)
        }
    }

    fun getLocalLastSession(context: Context): Session? {
        val list = getLocalSession(context)
        if (list.size == 0) {
            return null
        }
        val session = list[0]
        Logger.d("最后登录的用户信息:$session")
        return session
    }

    fun getLocalSessionLimit5(context: Context): MutableList<Session> {
        val list = getLocalSession(context)
        list.let {
            if (it.size == 0) {
                return mutableListOf()
            }
            return if (it.size <= 5) {
                it
            } else {
                val temp = mutableListOf<Session>()
                for (item in it) {
                    if (item.loginType == 0) {
                        temp.add(item)
                    }
                    if (temp.size == 5) {
                        break
                    }
                }
                temp
            }
        }
    }

    private fun getLocalSession(context: Context): MutableList<Session> {
        val json = FileUtils.readFile(FileUtils.getUserInfoFilePath(context))
        return toList(json)
    }

    @Synchronized
    fun deleteUserInfo(context: Context, userId: String) {
        val userList = getLocalSession(context)
        if (userList.size == 0) {
            return
        }

        var deleteUser: Session? = null
        for (item in userList) {
            if (item.userId == userId) {
                deleteUser = item
                break
            }
        }

        deleteUser.apply {
            userList.remove(this)
        }

        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        for (item in userList) {
            Logger.d(item.toString())
            jsonArray.put(item.toJSONObject())
        }

        val filePath = FileUtils.getUserInfoFilePath(context)

        try {
            jsonObject.put("info", jsonArray)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        Logger.d("写入用户信息:$jsonObject")
        FileUtils.writeFile(jsonObject.toString(), filePath)
    }

    private fun toList(json: String): MutableList<Session> {
        if (TextUtils.isEmpty(json)) {
            return mutableListOf()
        }
        Logger.d("文件读出来:$json")
        val infoList = mutableListOf<Session>()
        try {
            val jsonObject = JSONObject(json)
            if (!JsonUtils.hasJsonKey(jsonObject, "info")) {
                return mutableListOf()
            }
            val infoObject = jsonObject.getJSONArray("info")
            for (i in 0 until infoObject.length()) {
                val session = Session()
                val obj = infoObject.getJSONObject(i)
                if (JsonUtils.hasJsonKey(obj, "user_id")) {
                    session.userId = obj.getString("user_id")
                }
                if (JsonUtils.hasJsonKey(obj, "user_name")) {
                    session.userName = obj.getString("user_name")
                }
                if (JsonUtils.hasJsonKey(obj, "pwd")) {
                    session.pwd = obj.getString("pwd")
                }
                if (JsonUtils.hasJsonKey(obj, "login_type")) {
                    session.loginType = obj.getInt("login_type")
                }
                infoList.add(session)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            return mutableListOf()
        }
        return infoList
    }
}


