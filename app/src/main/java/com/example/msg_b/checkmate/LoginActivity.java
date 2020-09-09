package com.example.msg_b.checkmate;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.msg_b.checkmate.util.CurrentUserManager;
import com.example.msg_b.checkmate.util.User;
import com.example.msg_b.checkmate.R;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.kakao.auth.ErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import java.io.IOException;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    //번호 로그인
    public static int APP_REQUEST_CODE = 99;

    //카카오 로그인
    SessionCallback Kcallback;

    //페북 로그인
    private CallbackManager callbackManager;
    private ProfileTracker mProfileTracker;

    EditText et_test;
    TextView tv_title, tv_title2;
    Button btn_kakao, btn_number, btn_fb, btn_test;
    LoginButton btn_fb_real;
    com.kakao.usermgmt.LoginButton btn_kakao_real;

    ImageView iv;


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(Kcallback);
        //카카오 로그인 시 onDestroy 에 반드시 콜백을 제거하는 코드를 넣어야 함.
        //아니면 코드가 계속해서 호출됨.
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 바를 숨김
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        tv_title = (TextView) findViewById(R.id.Tv_title);
        tv_title2 = (TextView) findViewById(R.id.Tv_title2);
        btn_number = (Button) findViewById(R.id.Btn_number);
        btn_kakao = (Button) findViewById(R.id.Btn_kakao);
        btn_kakao_real = (com.kakao.usermgmt.LoginButton) findViewById(R.id.Btn_kakao_real);
        btn_fb = (Button) findViewById(R.id.Btn_fb);
        btn_fb_real = (LoginButton) findViewById(R.id.Btn_fb_real);
        btn_test = findViewById(R.id.Btn_test);
        et_test = findViewById(R.id.Et_test);
        iv = (ImageView) findViewById(R.id.imageView);

        btn_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = et_test.getText().toString();
                if(!text.isEmpty()) {
                    new UpdateCurrentUserTask(text).execute();
                }
            }
        });








    /** Account Kit 로그인 **/
        btn_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneLogin(view);
            }
        });
        //'전화번호로 로그인' 버튼을 누르면 phoneLogin 메소드가 호출됨.





     /** 페이스북 로그인 **/
        // If using in a fragment
        // Btn_fblogin.setFragment(this);
        // Callback registration
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        callbackManager = CallbackManager.Factory.create();
        btn_fb_real.setReadPermissions("email");
        btn_fb_real.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {


            //로그인 성공
            @Override
            public void onSuccess(final LoginResult loginResult) {
                // App code
                Log.d("fbLogin","onSucces LoginResult="+loginResult);
                Log.d("fbtest", "fb id : " + loginResult.getAccessToken().getUserId());

                // 1. 현재 프로필 정보가 null 이면 mProfileTracker 를 이용해 현재 프로필을 업데이트한다.
                // 2. serCurrentProfile 을 이용해 Profile 에 currentProfile 을 넣어준다.
                // 3. MainActivity 로 이동한다.
                if(Profile.getCurrentProfile() == null) {
                    Log.d("fbtest", "프로필 읍써");
                    mProfileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {

                            Profile.setCurrentProfile(currentProfile);
                            mProfileTracker.stopTracking();
                            String id = Profile.getCurrentProfile().getId();
                            CurrentUserManager.setCurrentUserId(LoginActivity.this, id);
                            goToMainActivityAndFinish(id, "FACEBOOK");
                        }
                    };
                    // no need to call startTracking() on mProfileTracker
                    // because it is called by its constructor, internally.


                } else {
                    Log.d("fbtest", "프로필 있어");
                    String id = loginResult.getAccessToken().getUserId();
                    CurrentUserManager.setCurrentUserId(LoginActivity.this, id);
                    goToMainActivityAndFinish(id, "FACEBOOK");
                    // 현재 프로필 정보가 null 이 아닌 경우 id를 저장하고 MainActivity 로 이동.
                }
            }


            @Override
            public void onCancel() {
                // App code
            }


            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.d("fbLogin","onError="+exception.toString());
            }
        });
        btn_fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_fb_real.performClick();
            }
        }); //'페이스북 계정으로 시작하기' 버튼을 누르면 상기에 정의된 페이스북 로그인 버튼이 실행됨.








    /** 카카오 로그인 **/
        Kcallback = new SessionCallback(); // Kcallback 이라는 이름으로 새로운 콜백 객체를 생성

        Session.getCurrentSession().addCallback(Kcallback); //현재 세션에 대한 콜백을 설정함.

        /** 토큰 만료시 갱신을 시켜준다**/
//        if(Session.getCurrentSession().isOpenable()){
//            Session.getCurrentSession().checkAndImplicitOpen();
//        }


        btn_kakao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Session.getCurrentSession().isOpened()) {
                    btn_kakao_real.performClick();
                } else {
                    Toast.makeText(LoginActivity.this, "이미로그인중", Toast.LENGTH_SHORT).show();
                }
            }
        });


    } //onCreate






    @Override
    protected void onResume() {
        super.onResume();
        if(Session.getCurrentSession().isClosed()){
            Log.d("sessioncheck", String.valueOf(Session.getCurrentSession().checkState()));
        }
    }


    @Override
    protected void onActivityResult(
            final int requestCode,
            final int resultCode,
            final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        //Account Kit 로그인
        if (requestCode == APP_REQUEST_CODE) { // confirm that this response matches your request
            AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            String toastMessage;
            if (loginResult.getError() != null) {
                toastMessage = loginResult.getError().getErrorType().getMessage();
                //showErrorActivity(loginResult.getError());
            } else if (loginResult.wasCancelled()) {
                toastMessage = "Login Cancelled";
            } else {
                if (loginResult.getAccessToken() != null) { //로그인에 성공했을 경우.

                    toastMessage = "Success:" + loginResult.getAccessToken().getAccountId();
                    String id = loginResult.getAccessToken().getAccountId();
                    CurrentUserManager.setCurrentUserId(LoginActivity.this, id);
                    goToMainActivityAndFinish(id, "ACCOUNT_KIT");
                    //로그인에 성공하면 현재 로그인 된 유저의 id를 얻어 CurrentUser 에 저장하고 메인으로 이동.

                } else {
                    toastMessage = String.format(
                            "Success:%s...",
                            loginResult.getAuthorizationCode().substring(0,10));
                }

                // If you have an authorization code, retrieve it from
                // loginResult.getAuthorizationCode()
                // and pass it to your server and exchange it for an access token.

                // Success! Start your next activity...

            }

            // Surface the result to your user in an appropriate way.
            Toast.makeText(
                    this,
                    toastMessage,
                    Toast.LENGTH_LONG)
                    .show();
        }


        //페북로그인
        callbackManager.onActivityResult(requestCode, resultCode, data);


        //KaKao 로그인
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
    }





    // 새 Activity 를 실행하고 기존의 Activity 를 종료하는 메소드.
    public void goToMainActivityAndFinish(@Nullable String id, @Nullable String type) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("ID", id);
        intent.putExtra("TYPE", type);
        startActivity(intent);
        finish();
    }
    public void goToKaKaoActivityAndFinish() {
        Intent intent_login = new Intent(LoginActivity.this, KakaoTestActivity.class);
        intent_login.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent_login);
        finish();
    }
    public void goToHomeActivityAndFinish() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }







    //카카오 로그인 시 세션의 콜백을 설정하는 부분
    public class SessionCallback implements ISessionCallback{
        @Override
        public void onSessionOpened() {
            Log.d("kakao", "onSessionOpened");
            UserManagement.requestMe(new MeResponseCallback() {

                @Override
                public void onFailure(ErrorResult errorResult) {
                    String message = "failed to get user info. msg=" + errorResult;
                    ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                    if (result == ErrorCode.CLIENT_ERROR_CODE) { //에러로 인한 로그인 실패 시
                        //finish();
                        Log.d("Isession", "I.onFailure");
                    } else { // 그외의 경우
                        //redirectMainActivity();
                    }
                }

                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                    Log.d("kakao", "onSessionClosed");
                }

                @Override
                public void onNotSignedUp() {
                    Log.d("Isession", "I.onNotSignedUp");
                }

                @Override
                public void onSuccess(UserProfile userProfile) {
                    //로그인에 성공하면 로그인한 사용자의 일련번호, 닉네임, 이미지 url 등을 리턴.
                    //사용자 ID는 보안상의 문제로 제공하지 않고 일련번호는 제공
                    String id = Long.toString(userProfile.getId());
                    Log.d("kakao", "onSuccess");

                    CurrentUserManager.setCurrentUserId(LoginActivity.this, id);
                    goToMainActivityAndFinish(id, "KAKAO");
                    //로그인에 성공하면 id를 저장하고 메인으로 이동.


                }
            });
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) { // 세션 연결이 실패했을 때
            if(exception != null) {
                Logger.e(exception);
            }
        }
    } //SessionCallback




    //Account Kit 로그인 부분
    public void phoneLogin(final View view) {
        final Intent intent = new Intent(this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(
                        LoginType.PHONE,
                        AccountKitActivity.ResponseType.TOKEN); // or .ResponseType.TOKEN
        // ... perform additional configuration ...
        intent.putExtra(
                AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                configurationBuilder.build());
        startActivityForResult(intent, APP_REQUEST_CODE);
    }




    private class UpdateCurrentUserTask extends AsyncTask<String, Void, User> {

        String mId;
        public UpdateCurrentUserTask(String id) {
            this.mId = id;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("permissionT", "preUpdating");
        }


        @Override
        protected User doInBackground(String... strings) {
            Log.d("permissionT", "backgroundUpdating");
            String strUrl = "http://115.71.238.160/novaproject1/HomeActivity/ProfileFragment/ProfileActivity/getuser.php";
            User result = null;

            RequestBody requestBody = new FormBody.Builder()
                    .add("id", mId)
                    .build();

            Request request = new Request.Builder()
                    .url(strUrl)
                    .post(requestBody)
                    .build();

            OkHttpClient mClient = new OkHttpClient();
            Call mCall = mClient.newCall(request);
            try {
                Response mResponse = mCall.execute();
                String data = mResponse.body().string();
                Log.d("sad1", data);
                User mUser = new Gson().fromJson(data, User.class);
                result = mUser;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }


        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);
            Log.d("permissionT", "postUpdating");
            if(user != null) {
                CurrentUserManager.setCurrentUser(getApplicationContext(), user);
                String paramsToken = FirebaseInstanceId.getInstance().getToken();
                new setTokenIdTask().execute(user.getId(), paramsToken);
            }
            else
                Toast.makeText(LoginActivity.this, "유저 정보가 없음", Toast.LENGTH_SHORT).show();
        }
    }




    /**
     * 현재 앱의 fcm token 에 로그인 된 id를 세팅한다.
     * 앱의 진입점인 MainActivity 와 앱 설치시 토큰을 생성하는 fcm Instance 클래스에 onRefreshToken 에 넣어준다.**/
    class setTokenIdTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {

            String strUrl = "http://115.71.238.160/novaproject1/fcmid.php";
            String result = null;

            //파라미터의 첫 번째 변수는 현재의 id, 두 번째 변수는 앱의 fcm 토큰이다.
            RequestBody requestBody = new FormBody.Builder()
                    .add("id", strings[0])
                    .add("token", strings[1])
                    .build();

            Request request = new Request.Builder()
                    .url(strUrl)
                    .post(requestBody)
                    .build();

            OkHttpClient mClient = new OkHttpClient();
            Call mCall = mClient.newCall(request);
            try {
                Response mResponse = mCall.execute();
                result = mResponse.body().string();
                Log.d("fcmT", "result : " + result);
                Log.d("fcmT", strings[0]);
                Log.d("fcmT", strings[1]);
            } catch (IOException e) {
                e.printStackTrace();
            }



            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s.equals("1")) {
                Toast.makeText(LoginActivity.this, "토큰 id 업데이트 성공"+"\n결과값 : "+s, Toast.LENGTH_SHORT).show();
                goToHomeActivityAndFinish();
            } else if (s.equals("0")) {
                Toast.makeText(LoginActivity.this, "토큰 id 업데이트 실패"+"\n결과값 : "+s, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(LoginActivity.this, "토큰 존재 x"+"\n결과값 : "+s, Toast.LENGTH_SHORT).show();
            }

        }
    }

}
