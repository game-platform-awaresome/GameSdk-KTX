package cn.flyfun.gamesdk.core.entity

/**
 * @author #Suyghur,
 * Created on 2021/3/19
 */
class GameRewardInfo {

    var userId: String? = null
    var serverCode: String? = null
    var serverName: String? = null
    var roleName: String? = null
    var roleId: String? = null
    var roleLevel: String? = null
    var rewardId: String? = null
    var purchaseToken: String? = null
    override fun toString(): String {
        return "GameRoleInfo{" +
                "userId='" + userId + '\'' +
                ", serverId='" + serverCode + '\'' +
                ", serverName='" + serverName + '\'' +
                ", roleName='" + roleName + '\'' +
                ", roleId='" + roleId + '\'' +
                ", roleLevel='" + roleLevel + '\'' +
                ", rewardId='" + rewardId + '\'' +
                ", purchaseToken='" + purchaseToken + '\'' +
                '}'
    }
}