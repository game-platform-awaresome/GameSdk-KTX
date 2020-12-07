package cn.flyfun.ktx.gamesdk.core.utils;


import cn.flyfun.ktx.gamesdk.base.utils.Logger;

/**
 * 手机验证码倒计时
 */
public class TimeDownUtils {

    private static boolean isRunning;
    private static boolean isCancel;
    private static volatile Times times;

    public static void start(TimeCallback timeCallback) {
        if (isRunning) {
            Logger.d("倒计时还在执行中...");
        } else {
            isRunning = true;
            isCancel = false;
            times = new Times(timeCallback);
            times.start();
        }
    }

    public static void cancel() {
        isCancel = true;
        if (isRunning) {
            if (times != null) {
                times.interrupt();
                times = null;
            }
        }
        isRunning = false;
        Logger.d("TimeDownHelper.cancel");
    }

    public static void resetCallback(TimeCallback timeCallback) {
        if (times != null) {
            times.setCallback(timeCallback);
        }
    }

    public static boolean isRunning() {
        return isRunning;
    }

    public interface TimeCallback {
        void onTime(int time);
    }

    /**
     * 倒计时
     */
    static class Times extends Thread {
        TimeCallback callback;

        public Times(TimeCallback callback) {
            this.callback = callback;
        }

        public void setCallback(TimeCallback callback) {
            this.callback = callback;
        }

        @Override
        public void run() {
            if (callback != null)
                callback.onTime(59);
            Logger.d("TimeDownHelper 倒计时开始...");
            for (int i = 59; i >= 0; i--) {
                //LogWaves.d("倒计时：" + i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (isCancel) {
                    Logger.d("TimeDownHelper 线程已退出");
                    return;
                }
                if (callback != null)
                    callback.onTime(i);
            }
            cancel();
        }
    }
}
