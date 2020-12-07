package cn.flyfun.ktx.gamesdk.core.network

import android.content.Context
import cn.flyfun.support.HostModelUtils

/**
 * @author #Suyghur.
 * Created on 2020/12/1
 */
object Host {

    private const val DEFAULT_ONLINE_GAME_HOST = "https://fgame.flyfungame.com"
    private const val DEFAULT_ONLINE_LOGIN_HOST = "https://flogin.flyfungame.com"
    private const val DEFAULT_ONLINE_PAY_HOST = "https://fpay.flyfungame.com"
    private const val DEFAULT_ONLINE_ADV_HOST = "https://adv.flyfungame.com"
    private const val DEFAULT_TEST_GAME_HOST = "http://testgame.flyfungame.com"
    private const val DEFAULT_TEST_LOGIN_HOST = "http://testlogin.flyfungame.com"
    private const val DEFAULT_TEST_PAY_HOST = "http://testpay.flyfungame.com"
    private const val DEFAULT_TEST_ADV_HOST = "http://testadv.flyfungame.com"

    @JvmField
    var BASIC_URL_INIT_SDK = "/game-api/v1/game/api/find-info"

    @JvmField
    var BASIC_URL_USER_VERIFY = "/login-api/v1/user/login"

    @JvmField
    var BASIC_URL_USER_REGISTER = "/login-api/v1/user/registered"

    @JvmField
    var BASIC_URL_USER_BIND = "/login-api/v1/user/bind"

    @JvmField
    var BASIC_URL_GET_CAPTCHA = "/login-api/v1/user/send-email-code"

    @JvmField
    var BASIC_URL_FORGET_PASSWORD = "/login-api/v1/user/reset-pwd"

    @JvmField
    var BASIC_URL_GET_ORDER_ID = "/pay-api/v1/api/pay/init-order"

    @JvmField
    var BASIC_URL_NOTIFY_ORDER = "/pay-api/v1/api/pay/check-order"

    @JvmField
    var BASIC_URL_SUBMIT_ROLE = "/adv-api/v1/new-role/save"

    @JvmField
    var GAME_HOST = ""

    @JvmField
    var LOGIN_HOST = ""

    @JvmField
    var PAY_HOST = ""

    @JvmField
    var ADV_HOST = ""

    /**
     * 默认线上环境
     */
    @JvmField
    var IP_MODEL = 3

    @JvmStatic
    fun initHostModel(context: Context) {
        IP_MODEL = HostModelUtils.getHostModel(context)
        setDefaultHost()
    }

    private fun setDefaultHost() {
        when (IP_MODEL) {
            HostModelUtils.ENV_TEST -> {
                GAME_HOST = DEFAULT_TEST_GAME_HOST
                LOGIN_HOST = DEFAULT_TEST_LOGIN_HOST
                PAY_HOST = DEFAULT_TEST_PAY_HOST
                ADV_HOST = DEFAULT_TEST_ADV_HOST
            }
            HostModelUtils.ENV_ONLINE -> {
                GAME_HOST = DEFAULT_ONLINE_GAME_HOST
                LOGIN_HOST = DEFAULT_ONLINE_LOGIN_HOST
                PAY_HOST = DEFAULT_ONLINE_PAY_HOST
                ADV_HOST = DEFAULT_ONLINE_ADV_HOST
            }
        }

        BASIC_URL_INIT_SDK = GAME_HOST + BASIC_URL_INIT_SDK
        BASIC_URL_USER_VERIFY = LOGIN_HOST + BASIC_URL_USER_VERIFY
        BASIC_URL_USER_REGISTER = LOGIN_HOST + BASIC_URL_USER_REGISTER
        BASIC_URL_USER_BIND = LOGIN_HOST + BASIC_URL_USER_BIND
        BASIC_URL_GET_CAPTCHA = LOGIN_HOST + BASIC_URL_GET_CAPTCHA
        BASIC_URL_FORGET_PASSWORD = LOGIN_HOST + BASIC_URL_FORGET_PASSWORD
        BASIC_URL_GET_ORDER_ID = PAY_HOST + BASIC_URL_GET_ORDER_ID
        BASIC_URL_NOTIFY_ORDER = PAY_HOST + BASIC_URL_NOTIFY_ORDER
        BASIC_URL_SUBMIT_ROLE = ADV_HOST + BASIC_URL_SUBMIT_ROLE
    }

}