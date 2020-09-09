package com.example.msg_b.checkmate.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.facebook.accountkit.AccountKit;
import com.facebook.login.LoginManager;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

public class CurrentUserManager {


    /** 현재 사용자의 ID를 불러온다 **/
    public static String getCurrentUserId(Context context) {
        SharedPreferences userId = context.getSharedPreferences("CURRENT_USER", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userId.edit();
        String result = userId.getString("id", null);
        return result;
    }
    //현재 저장되어 있는 유저의 id를 불러옴


    /** 현재 사용자의 ID를 업데이트 한다. **/
    public static void setCurrentUserId(Context context, String id) {
        SharedPreferences userId = context.getSharedPreferences("CURRENT_USER", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userId.edit();
        editor.putString("id", id);
        editor.commit();
    }
    //"CURRENT_USER"에 "id" 라는 값으로 유저의 아이디값을 저장함.

    /** 현재 사용자의 프로필 이미지 경로를 불러온다 **/
    public static String getCurrentUserImg_profile(Context context) {
        SharedPreferences userId = context.getSharedPreferences("CURRENT_USER", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userId.edit();
        String result = userId.getString("img_profile", null);
        return result;
    }

    /** 현재 사용자의 프로필 이미지 경로를 저장한다. **/
    public static void setCurrentUserImg_profile(Context context, String path) {
        SharedPreferences userId = context.getSharedPreferences("CURRENT_USER", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userId.edit();
        editor.putString("img_profile", path);
        editor.commit();
    }


    /** 현재 사용자의 정보를 업데이트 한다. **/
    public static void setCurrentUser(Context context, User mUser) {
        SharedPreferences cUser = context.getSharedPreferences("CURRENT_USER", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = cUser.edit();
        editor.putString("id", mUser.getId());
        editor.putString("type", mUser.getType());
        editor.putString("status", mUser.getStatus());
        editor.putString("nickname", mUser.getNickname());
        editor.putString("age", mUser.getAge());
        editor.putString("height", mUser.getHeight());
        editor.putString("img_profile", mUser.getImg_profile());
        editor.putString("img_profile2", mUser.getImg_profile2());
        editor.putString("img_profile3", mUser.getImg_profile3());
        editor.putString("img_profile4", mUser.getImg_profile4());
        editor.putString("img_profile5", mUser.getImg_profile5());
        editor.putString("img_profile6", mUser.getImg_profile6());
        editor.putString("introduce", mUser.getIntroduce());
        editor.putString("live", mUser.getLive());
        editor.putString("sex", mUser.getSex());
        editor.putString("job", mUser.getJob());
        editor.commit();
    }

    /** 현재 사용자의 정보를 불러온다. **/
    public static User getCurrentUser(Context context) {
        SharedPreferences cUser = context.getSharedPreferences("CURRENT_USER", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = cUser.edit();

        User mUser = new User();
        mUser.setId(cUser.getString("id", ""));
        mUser.setType(cUser.getString("type", ""));
        mUser.setStatus(cUser.getString("status", ""));
        mUser.setNickname(cUser.getString("nickname", ""));
        mUser.setAge(cUser.getString("age", ""));
        mUser.setHeight(cUser.getString("height", ""));
        mUser.setImg_profile(cUser.getString("img_profile", ""));
        mUser.setImg_profile2(cUser.getString("img_profile2", ""));
        mUser.setImg_profile3(cUser.getString("img_profile3", ""));
        mUser.setImg_profile4(cUser.getString("img_profile4", ""));
        mUser.setImg_profile5(cUser.getString("img_profile5", ""));
        mUser.setImg_profile6(cUser.getString("img_profile6", ""));
        mUser.setIntroduce(cUser.getString("introduce", ""));
        mUser.setLive(cUser.getString("live", ""));
        mUser.setSex(cUser.getString("sex", ""));
        mUser.setJob(cUser.getString("job", ""));

        return mUser;
    }


    /** 현재 사용자의 정보를 초기화한다. **/
    public static void initCurrentUser(Context context) {
        SharedPreferences cUser = context.getSharedPreferences("CURRENT_USER", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = cUser.edit();
        editor.clear().commit();
    }





    public static void LogoutAll() {
        AccountKit.logOut();
        LoginManager.getInstance().logOut();
        UserManagement.requestLogout(new LogoutResponseCallback() {

            @Override
            public void onSuccess(Long result) {
                Log.d("CB", "onClickLogout.onSuccess = "+result.toString());
            }

            @Override
            public void onCompleteLogout() {
                Log.d("CB", "onClickLogout.onCompleteLogout");

            }

            @Override
            public void onFailure(ErrorResult errorResult) {
                Log.d("CB", "onClickLogout.onFailure = "+ errorResult.getErrorMessage());
            }
        });
    }
    //모든 유저를 로그아웃 시킴






    /** CURRENT_USER 에 저장되어 있는 유저정보를 업데이트 함 **/
    class updateCurrentUser extends AsyncTask<String, Void, String> {

        private Context context;

        public updateCurrentUser(Context context) {
            this.context = context;
        }
        //생성자


        ProgressDialog mDialog = new ProgressDialog(context);

        @Override
        protected void onPreExecute() {
            mDialog.show();
            //사전 작업은 여기에 작성
        }


        @Override
        protected String doInBackground(String... strings) {

            //메인 작업은 여기에 작성


            return null;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mDialog.dismiss();

            //이후 작업은 여기에 작성
        }
    }




}
