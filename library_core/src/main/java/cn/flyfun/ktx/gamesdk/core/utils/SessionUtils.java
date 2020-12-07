package cn.flyfun.ktx.gamesdk.core.utils;

import android.content.Context;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.flyfun.ktx.gamesdk.base.utils.Logger;
import cn.flyfun.ktx.gamesdk.core.entity.Session;
import cn.flyfun.support.JsonUtils;


/**
 * @author #Suyghur.
 * Created on 2020/8/4
 */
public class SessionUtils {

    private static SessionUtils mInstance;
    private SessionUtils() {

    }

    public static SessionUtils getInstance() {
        if (null == mInstance) {
            synchronized (SessionUtils.class) {
                if (null == mInstance) {
                    mInstance = new SessionUtils();
                }
            }
        }
        return mInstance;
    }


    public synchronized void saveSession(Context context, Session currentSession) {
        if (currentSession == null) {
            return;
        }
        ArrayList<Session> userList = getLocalSession(context);
        if (null == userList) {
            userList = new ArrayList<>();
        }
        if (userList.size() > 0) {
            boolean isSessionExist = false;
            for (int i = 0; i < userList.size(); i++) {
                //TODO 当前登录用户已经存在用户列表里，更新用户数据(移除旧的，在0的位置插入)
                Session session = userList.get(i);
                if (currentSession.getUserId().equals(session.getUserId())) {
                    isSessionExist = true;
                    //移除
                    userList.remove(session);
                    userList.add(0, currentSession);
                    break;
                }
            }
            if (!isSessionExist) {
                //TODO 当前登录用户不存在用户列表里，在0的位置插入
                userList.add(0, currentSession);
            }
        } else {
            userList.add(currentSession);
        }

        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        for (Session session : userList) {
//            Logger.d(session.toString());
            jsonArray.put(session.toJSONObject());
        }
        String filePath = FileUtils.getUserInfoFilePath(context);

        try {
            jsonObject.put("info", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        FileUtils.writeFile(jsonObject.toString(), filePath);
    }

    public Session getLocalLastSession(Context context) {
        ArrayList<Session> list = getLocalSession(context);
        if (null == list) {
            return null;
        }
        Session session = list.get(0);
        Logger.d("最后登陆的用户信息:" + session.toString());
        return session;

    }

    public ArrayList<Session> getLocalSessionLimit5(Context context) {
        ArrayList<Session> list = getLocalSession(context);
        if (null == list) {
            return null;
        }
        if (list.size() <= 5) {
            return list;
        } else {
            ArrayList<Session> temp = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                Session session = list.get(i);
                if (session.getLoginType() == 0) {
                    temp.add(session);
                }
                if (temp.size() == 5) {
                    break;
                }
            }
            return temp;
        }
    }

    /**
     * {"info":{"abc123":{"uid":"abc123","username":"","userpwd":"","token":"","auto_login":1}}}
     *
     * @param context
     * @return
     */
    public ArrayList<Session> getLocalSession(Context context) {
        String json = FileUtils.readFile(FileUtils.getUserInfoFilePath(context));
        return toList(json);
    }

    /**
     * 删除用户信息
     *
     * @param context
     * @param userId
     */
    public synchronized void deleteUserInfo(Context context, String userId) {
        ArrayList<Session> userLists = getLocalSession(context);
        if (userLists == null || userLists.size() == 0) {
            return;
        }
        Session deleteUser = null;
        for (Session session : userLists) {
            if (session.getUserId().equals(userId)) {
                deleteUser = session;
                break;
            }
        }
        if (deleteUser != null) {
            userLists.remove(deleteUser);
        }

        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        for (Session session : userLists) {
            Logger.d(session.toString());
            jsonArray.put(session.toJSONObject());
        }
        String filePath = FileUtils.getUserInfoFilePath(context);

        try {
            jsonObject.put("info", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Logger.d("写入用户信息：" + jsonObject.toString());
        FileUtils.writeFile(jsonObject.toString(), filePath);
    }

    private static ArrayList<Session> toList(String json) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        Logger.d("文件读出来：" + json);
        ArrayList<Session> infoList = null;
        try {
            JSONObject jsonObject = new JSONObject(json);
            infoList = new ArrayList<>();
            if (!JsonUtils.hasJsonKey(jsonObject, "info")) {
                return null;
            }
            JSONArray infoObject = jsonObject.getJSONArray("info");
            for (int i = 0; i < infoObject.length(); i++) {
                Session session = new Session();
                JSONObject obj = infoObject.getJSONObject(i);
                if (JsonUtils.hasJsonKey(obj, "user_id")) {
                    session.setUserId(obj.getString("user_id"));
                }
                if (JsonUtils.hasJsonKey(obj, "user_name")) {
                    session.setUserName(obj.getString("user_name"));
                }
                if (JsonUtils.hasJsonKey(obj, "pwd")) {
                    session.setPwd(obj.getString("pwd"));
                }
                if (JsonUtils.hasJsonKey(obj, "login_type")) {
                    session.setLoginType(obj.getInt("login_type"));
                }
                infoList.add(session);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return infoList;
    }

}
