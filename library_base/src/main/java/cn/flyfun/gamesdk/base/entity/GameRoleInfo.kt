package cn.flyfun.gamesdk.base.entity

import java.io.Serializable

/**
 * @author #Suyghur.
 * Created on 10/20/20
 */
class GameRoleInfo : Serializable {
    var userId: String? = null
    var serverCode: String? = null
    var serverName: String? = null
    var roleName: String? = null
    var roleId: String? = null
    var roleLevel: String? = null
    var vipLevel: String? = null
    var balance: String? = null
    override fun toString(): String {
        return "GameRoleInfo{" +
                "userId='" + userId + '\'' +
                ", serverId='" + serverCode + '\'' +
                ", serverName='" + serverName + '\'' +
                ", roleName='" + roleName + '\'' +
                ", roleId='" + roleId + '\'' +
                ", roleLevel='" + roleLevel + '\'' +
                ", vipLevel='" + vipLevel + '\'' +
                ", balance='" + balance + '\'' +
                '}'
    }
}