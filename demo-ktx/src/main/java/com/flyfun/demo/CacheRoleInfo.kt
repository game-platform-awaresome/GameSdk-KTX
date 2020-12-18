package com.flyfun.demo

import android.content.Context
import android.text.TextUtils
import cn.flyfun.gamesdk.base.utils.Logger
import org.json.JSONException
import org.json.JSONObject

/**
 * @author #Suyghur.
 * Created on 2020/12/7
 */
class CacheRoleInfo {

    companion object {

        fun setDemoRoleInfo(context: Context, userId: String): RoleInfo {
            Logger.d("setDemoRoleInfo")
            val roleInfo = RoleInfo(
                    roleId = "aaa${System.currentTimeMillis()}",
                    roleName = "角色名123",
                    roleLevel = "110",
                    serverCode = "333",
                    serverName = "服务器名333",
                    vipLevel = "1",
                    balance = "600")
            val info = roleInfo.toJsonString()
            val sp = context.getSharedPreferences("app_role_info", Context.MODE_PRIVATE)
            val editor = sp.edit()
            editor.putString(userId, info)
            editor.apply()
            return roleInfo
        }

        fun getDemoRoleInfo(context: Context, userId: String): RoleInfo? {
            try {
                val sp = context.getSharedPreferences("app_role_info", Context.MODE_PRIVATE)
                val info = sp.getString(userId, "")
                return if (TextUtils.isEmpty(info)) {
                    //没有则创建
                    setDemoRoleInfo(context, userId)
                } else {
                    val jsonObject = JSONObject(info!!)
                    RoleInfo(
                            roleId = jsonObject.getString("role_id"),
                            roleName = jsonObject.getString("role_name"),
                            roleLevel = jsonObject.getString("role_level"),
                            serverCode = jsonObject.getString("server_code"),
                            serverName = jsonObject.getString("server_name"),
                            vipLevel = jsonObject.getString("vip_level"),
                            balance = jsonObject.getString("balance"))
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }

        data class RoleInfo(val roleId: String, val roleName: String, val roleLevel: String,
                            val serverCode: String, val serverName: String, val vipLevel: String,
                            val balance: String) {


            fun toJsonString(): String {
                try {
                    val jsonObject = JSONObject()
                    jsonObject.put("role_id", roleId)
                    jsonObject.put("role_name", roleName)
                    jsonObject.put("role_level", roleLevel)
                    jsonObject.put("server_code", serverCode)
                    jsonObject.put("server_name", serverName)
                    jsonObject.put("vip_level", vipLevel)
                    jsonObject.put("balance", balance)
                    return jsonObject.toString()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                return ""
            }
        }
    }
}