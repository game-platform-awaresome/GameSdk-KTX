package cn.flyfun.ktx.gamesdk.base.entity

import java.io.Serializable

/**
 * @author #Suyghur.
 * Created on 10/20/20
 */
class GameChargeInfo : Serializable {
    var userId: String? = null
    var orderId: String? = null
    var serverCode: String? = null
    var serverName: String? = null
    var roleName: String? = null
    var roleId: String? = null
    var roleLevel: String? = null
    var price = 0f
    var productId: String? = null
    var productName: String? = null
    var productDesc: String? = null
    var cpOrderId: String? = null
    var cpNotifyUrl: String? = null
    var cpCallbackInfo: String? = null
    override fun toString(): String {
        return "GameChargeInfo{" +
                "userId='" + userId + '\'' +
                ", orderId='" + orderId + '\'' +
                ", serverId='" + serverCode + '\'' +
                ", serverName='" + serverName + '\'' +
                ", roleName='" + roleName + '\'' +
                ", roleId='" + roleId + '\'' +
                ", roleLevel='" + roleLevel + '\'' +
                ", price=" + price +
                ", productId='" + productId + '\'' +
                ", productName='" + productName + '\'' +
                ", productDesc='" + productDesc + '\'' +
                ", cpOrderId='" + cpOrderId + '\'' +
                ", cpNotifyUrl='" + cpNotifyUrl + '\'' +
                ", cpCallbackInfo='" + cpCallbackInfo + '\'' +
                '}'
    }
}