# 修订记录

|	日期 	|	版本	|	说明	|	 作者	|
| :--: | :--: | :-- | :--: |
|	2020-11-13	|	1.0.0	|	文档建立	| 麦锦培 |
|	2020-12-07	|	1.0.1	|	1）优化SDK客服功能；<br/>2）增加对外接口getCurrentSdkVersion()获取SDK版本；<br/>3）谷歌IAB支付SDK3.0.2升级；<br/>4）修复一些已知问题	| 麦锦培 |
|	2020-12-21	|	1.0.2	|	1）优化SDK客服功能，修复个别机型UI显示问题；<br/>2）增加对外接口openExitView()显示SDK退出框；<br/>3）接口hasBindPlatformAccount()更改为hasBindAccount()，调用时机和方式无变更；<br/>4）接口bindPlatformAccount更改为openBindAccount()，调用时机和方式无变更；<br/>5）sdk新增kotlin版本，接入方式和java一致，具体看SDK资源接入说明中的远程依赖部分<br/>6）修复一些已知问题	| 麦锦培 |
|	2021-1-08	|	1.1.0	|	1）Adjust上报SDK4.25.0更新；<br/>2）Facebook SDK7.1.0更新；<br/>3）SDK部分feature迁移至native层；<br/>4）修复一些已知问题  | 麦锦培 |
|	2021-1-15	|	1.1.1	|	1）Facebook SDK9.0.0更新；<br/>2）调整Webview中SslErrorHandler回调的默认处理方案;    | 麦锦培 |
|	2021-1-18	|	1.1.2	|	1）修复5.0以下多个Dex文件加载问题    | 麦锦培 |
|	2021-1-22	|	1.1.3	|	1）修复客服中心提交问题上传图片失败的问题   | 麦锦培 |

# 1.接入前检查

- 游戏资源文件名、布局名、布局id名等建议使用规范命名，避免和SDK资源冲突
- Android Studio 3.0及以上，由于谷歌服务一系列SDK已不提供jar包形式，因此SDK不提供jar包形式以eclipse接入
- Android Gradle Plugin Version : 3.5.0+
- Gradle Version : 5.4.1+
- Android Studio开启Android X支持，请在游戏项目根目录的**`gradle.properties`**中设置

```properties
android.useAndroidX=true
android.enableJetifier=true
```

- 请务必使用我们提供的keystore签名文件进行签名，否则SDK功能会异常

# 2.SDK资源接入说明

- 1.接入中存在问题请参考SDK提供的Demo工程或联系技术解决

- 2.拷贝SDK目录下**`assets/flyfun`**至项目中

- 3.在游戏AndroidManifest.xml清单文件的application节点下填写Facebook配置，Facebook AppId在SDK目录下**`assets/flyfun/flyfun_cfg.properties`**文件中的**`FLYFUN_FACEBOOK_ID`**

```xml
	<application>
		...
		<!-- Facebook start-->
		<meta-data
			android:name="com.facebook.sdk.ApplicationId"
			android:value="填入我方提供的Facebook appid，注意前面有'\'，如appid为12345678，则填写为\12345678" />

		<activity
			android:name="com.facebook.FacebookActivity"
			android:configChanges="keyboard|keyboardHidden|screenLayout|screenSize|orientation" />

		<activity
			android:name="com.facebook.CustomTabActivity"
			android:exported="true">
				<intent-filter>
					<action android:name="android.intent.action.VIEW" />
					<category android:name="android.intent.category.DEFAULT" />
					<category android:name="android.intent.category.BROWSABLE" />
					<data android:scheme="填入我方提供的Facebook appid，注意前面有需要加上'fb'" />
				</intent-filter>
		</activity>
		<!-- Facebook start-->
		···
	</application>
```

- 4.在项目**`build.gradle`**中**`dependencies`**节点下添加SDK依赖，目前只提供以下两种引入形式

> <font color=red size=4>**1.远程依赖（推荐）**：</font>

```groovy
	implementation 'cn.flyfun.gamesdk:core:1.1.3'
	//kotlin版本目前只提供远程依赖，需本地aar请联系我方技术获取
	//implementation 'cn.flyfun.gamesdk:core-ktx:1.1.3'
```

> <font size=4>**2.本地aar依赖：**</font>
拷贝lib目录下**`flyfun_core_1.1.3.aar`**到项目中，并在引入

```groovy
	api(name: 'flyfun_core_1.1.3.aar', ext: 'aar')
```

	添加谷歌、Facebook等第三方库资源

```groovy
	implementation 'androidx.core:core:1.3.2'
	implementation 'androidx.fragment:fragment:1.2.5'
	implementation 'com.google.android.material:material:1.2.1'
	implementation 'com.android.installreferrer:installreferrer:2.2'
	implementation 'com.facebook.android:facebook-login:9.0.0'
	implementation 'com.google.android.gms:play-services-auth:19.0.0'
	implementation 'com.android.billingclient:billing:3.0.2'
	implementation 'com.adjust.sdk:adjust-android:4.25.0'
	implementation 'androidx.constraintlayout:constraintlayout:2.0.4'
```

# 3.接口说明

> <font color=red size=5>**SDK的所有接口均为必接且请务必在游戏主线程调用**</font>

## 1）同步Application中的生命周期

- 若游戏无自定义Application则直接在清单文件AndroidManifest.xml的**``application``**节点中设置name属性为SDK提供的Application**``cn.flyfun.gamesdk.base.FlyFunGameApplication``**

```xml
  <application android:name="cn.flyfun.gamesdk.base.FlyFunGameApplication">
    ···
  </application>
```

- 若游戏有自定义Application，则需要继承SDK提供的Application

```java
	public class DemoApplication extends FlyFunGameApplication {
		@Override
		protected void attachBaseContext(Context base) {
			super.attachBaseContext(base);
		}

		@Override
		public void onCreate() {
			super.onCreate();
		}
	}
```

## 2）同步游戏Activity中各个生命周期至SDK

- 同步onStart

```java
	@Override
	protected void onStart() {
		super.onStart();
		FlyFunGame.getInstance().onStart(this);
	}
```

- 同步onResume

```java
	@Override
	protected void onResume() {
		super.onResume();
    FlyFunGame.getInstance().onResume(this);
	}
```

- 同步onRestart

```java
	@Override
	protected void onRestart() {
		super.onRestart();
		FlyFunGame.getInstance().onReStart(this);
	}
```

- 同步onPause

```java
	@Override
	protected void onPause() {
		super.onPause();
		FlyFunGame.getInstance().onPause(this);
	}
```

- 同步onStop

```java
	@Override
	protected void onStop() {
		super.onStop();
		FlyFunGame.getInstance().onStop(this);
	}
```

- 同步onDestroy

```java
	@Override
	protected void onDestroy() {
		super.onDestroy();
		FlyFunGame.getInstance().onDestroy(this);
	}
```

- 同步onActivityResult

```java
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		FlyFunGame.getInstance().onActivityResult(this, requestCode, requestCode, data);
	}
```

- 同步onNewIntent

```java
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		FlyFunGame.getInstance().onNewIntent(this, intent);
	}
```

## 3）SDK通用回调接口ICallback说明

```java
	public interface ICallback {
		void onResult(int code, String result);
	}
```

|	参数 	|	类型	|	说明	|
| :--: | :--: | :--: |
|	code	|	int	|	状态码（0成功，-1失败）	|
|	result	|	string	|	返回信息	|

## 4）SDK初始化

- 在游戏的主Activity的onCreate中调用

```java
	/**
	 * SDK初始化
	 *
	 * @param activity    Activity上下文
	 * @param isLandscape 是否横屏
	 * @param callback    SDK初始化回调
	 */
	public void initialize(final Activity activity, final Boolean isLandscape, ICallback callback)
```

- 示例

```java
	@Override
	protected void onCreate(Bundle savedInstanceState){
		FlyFunGame.getInstance().initialize(this, false, new ICallback() {
			@Override
			public void onResult(int code, String result) {
				if (code == 0) {
					//TODO SDK初始化成功
				} else {
					//TODO SDK初始化失败，result是失败的debug信息
        }
			});
	}
```

## 5）用户账号登录

> <font color=red size=5>**接入方需要自己处理登录框的Logo，一般为游戏的Logo，建议大小300x120，命名为ffg_login_logo_img.png，放在res/drawable-xhdpi目录下**</font>

```java
	/**
	 * SDK用户登录
	 *
	 * @param activity Activity上下文
	 * @param isAuto   是否自动登录
	 * @param callback 登录回调对象
	 */
	public void login(Activity activity, Boolean isAuto, ICallback callback)
```

**登录回调返回的result信息**

|	参数 	|	类型	|	说明	|
| :--: | :--: | :--: |
|	user_id	|	string	|	SDK的用户ID	|
|	timestamp	|	string	|	用户登录的服务端时间戳	|
|	cp_sign	|	string	|	签名	|

- 示例

```java
	FlyFunGame.getInstance().login(DemoActivity.this, true, new ICallback(){
		@Override
		public void onResult(int code, String result) {
			if (code == 0) {
				//TODO 对SDK返回的用户信息进行验签
				//result返回的是JSON字符串，可以得到user_id、timestamp和cp_sign等信息
				//sign=md5(app_secret+user_id+timestamp)('+'只做连接，不参与加密)
			} else {
				//TODO 登录失败，result是返回的debug信息
			}
		}
	});
```

## 6）用户账号登出

> 用户在游戏中触发登出，游戏回到选服界面重新拉起登录

```java
	/**
	 * SDK用户登出账号
	 *
	 * @param activity Activity上下文
	 * @param callback 登出回调对象
	 */
	public void logout(Activity activity, ICallback callback)
```

- 示例

```java
	FlyFunGame.getInstance().logout(this, new ICallback() {
		@Override
		public void onResult(int code, String result) {
			if (code == 0) {
				//TODO 用户登出成功，登出成功后请返回游戏选服界面，然后拉起登录框
			} else {
				//TODO 用户登出失败，result是返回的debug信息
			}
	}
```

## 7）角色信息上报

> 请务必根据当前角色的触发的事件进行上报

角色信息实体对象GameRoleInfo，如无特别说明所有字段均不能为null或空串""

|	参数 	|	类型	|	说明	|
| :--: | :--: | :--: |
|	userId	|	string	|	当前用户ID	|
|	serverCode	|	string	|	当前角色所在的服务器ID	|
|	serverName	|	string	|	当前角色所在的服务器名	|
|	roleId	|	string	|	当前角色ID	|
|	roleName	|	string	|	当前角色名	|
|	roleLevel	|	string	|	当前角色等级	|
|	vipLevel	|	string	|	当前Vip角色等级，若无传"none"	|
|	balance	|	string	|	当前角色游戏币余额，若无传"none"	|

> 角色创建

```java
	/**
	 * SDK角色创建信息上报
	 *
	 * @param activity Activity上下文
	 * @param roleInfo 角色信息实体
	 */
	public void roleCreate(Activity activity, GameRoleInfo roleInfo)
```

> 角色进入服务器（角色登录）

```java
	/**
	 * SDK角色登录信息上报
	 *
	 * @param activity Activity上下文
	 * @param roleInfo 角色信息实体
	 */
	public void roleLauncher(Activity activity, GameRoleInfo roleInfo)
```

> 角色升级

```java
	/**
	 * SDK角色升级信息上报
	 *
	 * @param activity Activity上下文
	 * @param roleInfo 角色信息实体
	 */
	public void roleUpgrade(Activity activity, GameRoleInfo roleInfo)
```

- 示例

```java
	GameRoleInfo gameRoleInfo = new GameRoleInfo();
	//用户ID
	gameRoleInfo.setUserId(FlyFunGame.getInstance().getCurrentUserId());
	//角色ID
	gameRoleInfo.setRoleId(roleId);
	//角色名称
	gameRoleInfo.setRoleName(roleName);
	//角色等级
	gameRoleInfo.setRoleLevel(roleLevel);
	//服务器ID
	gameRoleInfo.setServerCode(serverId);
	//服务器名
	gameRoleInfo.setServerName(serverName);
	//用户VIP等级，无该字段则传空串""
	gameRoleInfo.setVipLevel("1");
	//当前角色游戏币余额
	gameRoleInfo.setBalance("600");

	//角色创建
	FlyFunGame.getInstance().roleCreate(this, gameRoleInfo);

	//角色登录
	FlyFunGame.getInstance().roleLauncher(this, gameRoleInfo;

	//角色升级
	FlyFunGame.getInstance().roleUpgrade(this, gameRoleInfo;
```


## 8）支付储值

> 请务必保证调用角色信息上报中的角色登录上报后再调用
> <font color=red size=5>**客户端SDK回调的只是支付流程的结果，实际支付结果将由服务端回调**</font>

支付信息实体对象GameChargeInfo，如无特别说明所有字段均不能为null或空串""

|	参数 	|	类型	|	说明	|
| :--: | :--: | :--: |
|	userId	|	string	|	当前用户ID	|
|	serverCode	|	string	|	当前角色所在的服务器ID	|
|	serverName	|	string	|	当前角色所在的服务器名	|
|	roleId	|	string	|	当前角色ID	|
|	roleName	|	string	|	当前角色名	|
|	roleLevel	|	string	|	当前角色等级	|
|	cpOrderId	|	string	|	游戏订单号	|
|	cpNotifyUrl	|	string	|	游戏商品通知发货地址	|
|	cpCallbackInfo	|	string	|	透传字段，会通过回调地址原样返回，可为空	|
|	price	|	float	|	金额，单位元，币种固定为币种美金	|
|	productId	|	string	|	商品ID	|
|	productName	|	string	|	商品名称	|
|	productDesc	|	string	|	商品描述	|

```java
	/**
	 * SDK用户支付
	 *
	 * @param activity   Activity上下文
	 * @param chargeInfo 支付信息实体对象
	 * @param callback   支付回调对象
	 */
	public void charge(Activity activity, GameChargeInfo chargeInfo, ICallback callback)
```

- 示例

```java
	GameChargeInfo gameChargeInfo = new GameChargeInfo();
	String orderId = "order_" + System.currentTimeMillis();
	//用户ID
	gameChargeInfo.setUserId(FlyFunGame.getInstance().getCurrentUserId());
	//角色ID
	gameChargeInfo.setRoleId(roleId);
	//角色名称
	gameChargeInfo.setRoleName(roleName);
	//角色等级
	gameChargeInfo.setRoleLevel(roleLevel);
	//服务器ID
	gameChargeInfo.setServerCode(serverId);
	//服务器名
	gameChargeInfo.setServerName(serverName);
	//游戏的订单号
	gameChargeInfo.setCpOrderId(orderId);
	//游戏的发货地址（支付回调地址）
	gameChargeInfo.setCpNotifyUrl("http://www.flyfungame.com/?ac=order&ct=notify");
	//透传字段，会在回调地址中原样返回
	gameChargeInfo.setCpCallbackInfo("cp_callback_info||" + orderId);
	//金额，单位元，币种美金
	gameChargeInfo.setPrice(0.99f);
	//商品ID，计费点
	gameChargeInfo.setProductId("com.flyfun.ylj.60");
	//商品名称
	gameChargeInfo.setProductName("70元宝");
	//商品描述
	gameChargeInfo.setProductDesc("70元宝");

	//客户端SDK回调的只是支付流程的结果，实际支付结果将由服务端回调
	FlyFunGame.getInstance().charge(this, gameChargeInfo, new ICallback() {
		@Override
			public void onResult(int code, String result) {
				if (code == 0) {
					//TODO 支付流程完成
				} else {
					//TODO 支付失败
				}
			}
	});
```

## 9）用户账号绑定

> 登录校验成功后判断当前用户是否已经绑定平台账号，若未绑定则在游戏中显示入口，当玩家触发时会弹出绑定页面

判断当前用户是否已绑定平台账号

```java
 /**
	 * 当前用户是否已绑定平台账号
	 *
	 * @return
	 */
	public Boolean hasBindAccount()
```

显示绑定页面

```java
	/**
	 * 显示绑定平台账号页面
	 *
	 * @param activity Activity上下文
	 * @param callback 绑定回调对象
	 */
	public void openBindAccount(Activity activity, ICallback callback)
```

- 示例

```java
	FlyFunGame.getInstance().openBindAccount(this, new ICallback() {
		@Override
		public void onResult(int code, String result) {
			if (code == 0) {
				//TODO 绑定成功
			} else {
				//TODO 绑定失败，result是返回的debug信息
			}
		}
	});
```

## 10）跳转用户客服中心

> 登录校验成功后判断SDK是否开启客服功能，若开启则在游戏中显示入口，当玩家触发时会跳转到客服页面

判断SDK是否开启客服功能

```java
	/**
	 * 客服中心是否可用
	 *
	 * @return
	 */
	public Boolean isGmCenterEnable()
```

跳转到客服中心

```java
	/**
	 * 跳转到客服中心
	 *
	 * @param activity Activity上下文
	 * @param callback 客服回调对象（预留）
	 */
	public void openGmCenter(Activity activity, ICallback callback)
```

- 示例

```java
	FlyFunGame.getInstance().openGmCenter(this, new ICallback() {
		@Override
			public void onResult(int code, String result) {
				//TODO 回调功能预留，暂时不需要处理
			}
		}
	});
```

## 11）显示SDK退出框（选接）

> 用户（玩家）按下返回键时调用，接入方需要实现Activity的onKeyDown，并判断keyCode为KeyEvent.KEYCODE_BACK时调用该接口

```java
	/**
	 * 显示退出框
	 *
	 * @param activity Activity上下文
	 * @param callback 退出回调对象
	 */
	public void openExitView(Activity activity, ICallback callback)
```

- 示例

```java
	//重写Activity的onKeyDown并判断KeyDown事件
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			FlyFunGame.getInstance().openExitView(this, new ICallback() {
				@Override
				public void onResult(int code, String result) {
					if (code == 0) {
						//结束当前Activity
						finish();
					}
				}
			});
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		FlyFunGame.getInstance().onDestroy(this);
		//结束当前应用进程
		System.exit(0);
	}
```

## 12）获取SDK当前版本（选接）

```java
	/**
	 * 获取当前SDK版本
	 *
	 * @return
	 */
	public String getCurrentSdkVersion()
```

- 示例

```java
	FlyFunGame.getInstance().getCurrentSdkVersion();
```