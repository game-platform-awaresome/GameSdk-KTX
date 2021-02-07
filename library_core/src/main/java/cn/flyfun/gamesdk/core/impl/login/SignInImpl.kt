package cn.flyfun.gamesdk.core.impl.login

import android.app.Activity
import android.content.Intent
import cn.flyfun.gamesdk.base.utils.Logger
import cn.flyfun.gamesdk.base.utils.ParamsUtils
import cn.flyfun.gamesdk.core.entity.LoginType
import cn.flyfun.support.jarvis.Toast
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import org.json.JSONException
import org.json.JSONObject
import java.util.Collections


/**
 * @author #Suyghur.
 * Created on 2020/12/2
 */
class SignInImpl constructor(val activity: LoginActivity, val callback: ISignInCallback) {

    private var fbCallback: CallbackManager? = null
    private var googleSignInClient: GoogleSignInClient? = null


    init {
        this.fbCallback = CallbackManager.Factory.create()
        val googleServerClientId = ParamsUtils.getGoogleClientId(activity)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(googleServerClientId)
                .requestId()
                .requestProfile()
                .build()
        this.googleSignInClient = GoogleSignIn.getClient(activity, gso)
    }

    fun facebookLogin(activity: Activity) {
        if (!FacebookSdk.isInitialized()) {
            callback.onFailed("Facebook SDK 初始化失败")
            return
        }
        //先注销账号，防止同一台设备有多个应用登录后直接回调onCancel
        LoginManager.getInstance().logOut()
        LoginManager.getInstance().registerCallback(fbCallback, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                result?.apply {
                    try {
                        val jsonObject = JSONObject()
                        jsonObject.put("login_type", LoginType.TYPE_FACEBOOK_LOGIN)
                        jsonObject.put("user_name", "")
                        jsonObject.put("pwd", "")
                        jsonObject.put("third_plat_id", AccessToken.getCurrentAccessToken().userId)
                        jsonObject.put("third_plat_token", AccessToken.getCurrentAccessToken().token)
                        callback.onSuccess(jsonObject.toString())
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        callback.onFailed("Facebook登录异常")
                    }
                }
            }

            override fun onCancel() {
                callback.onFailed("Facebook登录取消")
            }

            override fun onError(error: FacebookException?) {
                error?.apply {
                    Logger.e("FBLogin onError error : " + this.message)
                }
                callback.onFailed("Facebook登录失败")
            }
        })
        LoginManager.getInstance().logInWithReadPermissions(activity, Collections.singletonList("public_profile"))
    }

    fun googleLogin(activity: Activity) {
        if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(activity) != ConnectionResult.SUCCESS) {
            Toast.toastInfo(activity, "Google Services is not available for this device")
            callback.onFailed("谷歌登录服务不可用，登录失败")
            return
        }
        googleSignInClient?.apply {
            val intent = signInIntent
            activity.startActivityForResult(intent, 10000)

        }
    }

    private fun handleGoogleSignInResult(data: Intent) {
        val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)
            try {
                val jsonObject = JSONObject()
                jsonObject.put("login_type", LoginType.TYPE_GOOGLE_LOGIN)
                jsonObject.put("user_name", "")
                jsonObject.put("pwd", "")
                jsonObject.put("third_plat_id", account?.id)
                jsonObject.put("third_plat_token", account?.idToken)
                callback.onSuccess(jsonObject.toString())
            } catch (e: JSONException) {
                e.printStackTrace()
                callback.onFailed("Google登录异常")
            }
        } catch (e: ApiException) {
            e.printStackTrace()
            callback.onFailed("Google登录异常")
        }
    }

    fun guestLogin() {
        try {
            val jsonObject = JSONObject()
            jsonObject.put("login_type", LoginType.TYPE_GUEST_LOGIN)
            jsonObject.put("user_name", "")
            jsonObject.put("pwd", "")
            jsonObject.put("third_plat_id", "")
            jsonObject.put("third_plat_token", "")
            callback.onSuccess(jsonObject.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
            callback.onFailed("游客登录异常")
        }
    }

    fun accountLogin(userName: String, pwd: String) {
        try {
            val jsonObject = JSONObject()
            jsonObject.put("login_type", LoginType.TYPE_ACCOUNT_LOGIN)
            jsonObject.put("user_name", userName)
            jsonObject.put("pwd", pwd)
            jsonObject.put("third_plat_token", "")
            jsonObject.put("third_plat_id", "")
            callback.onSuccess(jsonObject.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
            callback.onFailed("用户登录异常")
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        fbCallback?.apply {
            onActivityResult(requestCode, resultCode, data)
        }
        if (requestCode == 10000) {
            handleGoogleSignInResult(data)
        }
    }

    interface ISignInCallback {
        fun onSuccess(result: String)

        fun onFailed(result: String)

    }

}