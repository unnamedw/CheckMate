package com.example.msg_b.checkmate.api;

import android.app.Activity;
import android.app.Application;

import com.kakao.auth.KakaoSDK;

public class GlobalApplication extends Application {

    private static volatile GlobalApplication obj = null;
    private static volatile Activity currentActivity = null;


    @Override
    public void onCreate() {
        super.onCreate();
        obj = this;
        KakaoSDK.init(new KaKaoSDKAdapter());

//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            // This process is dedicated to LeakCanary for heap analysis.
//            // You should not init your app in this process.
//            return;
//        }
//        LeakCanary.install(this);
//        // Normal app init code...

//        Log.d("sT", "GlobalApplication onCreate");
    }






    public static GlobalApplication getGlobalApplicationContext(){
        return obj;
    }


    public static Activity getCurrentActivity(){
        return currentActivity;
    }


    // Activity 가 올라올 때마다 Activity 의 onCreate 에서 호출해줘야 함.
    public static void setCurrentActivity(Activity currentActivity){
        GlobalApplication.currentActivity = currentActivity;
    }

}
