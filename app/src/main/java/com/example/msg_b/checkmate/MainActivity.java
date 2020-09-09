package com.example.msg_b.checkmate;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.msg_b.checkmate.service.MyService;
import com.example.msg_b.checkmate.util.CurrentUserManager;
import com.example.msg_b.checkmate.util.User;
import com.facebook.AccessToken;
import com.facebook.accountkit.AccountKit;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    public static final int ACCOUNT_KIT = 1000;
    public static final int KAKAO = 1001;
    public static final int FACEBOOK = 1002;
    private final int TAG_SETTING = 1003;
    private int LOGIN_TYPE;
    private int LOGIN_CHECKER;
    private String login_id;
    private String login_type;

    LoginTask mTask;
    ProgressDialog mainDialog;
    ConstraintLayout cl_main;

    Double longi, lati;

    private final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 111;
    LocationManager lm;


    /**
     * [MainActivity 작업 순서도]
     *
     * 1. onCreate 에서 현재 존재하고 있는 토큰을 확인한다. (AccountKit, Kakao, Facebook API)
     *    토큰이 존재하고 있는 경우 LOGIN_CHECKER 라는 변수를 1씩 더한다.
     *    LOGIN_TYPE 에는 토큰이 존재하는 API 이름을 저장.
     *
     * 2. 다음은 앞서 저장된 변수들을 확인하는 과정이다.
     *    LOGIN_CHECKER 가 1이 아닌 경우에는 토큰이 존재하지 않거나 중복토큰이 존재하는 것이므로
     *    모든 API 의 토큰과 세션을 초기화하고 다시 로그인을 요구한다.
     *    LOGIN_CHECKER 가 1인 경우, 토큰이 정상적으로 존재하는 것이므로
     *    이 때는 login_id 와 login_type 에 각각 고유 id 와 API 유형을 저장하고 LoginTask 를 진행한다.
     *
     * 3. LoginTask 에서는 login_id 와 login_type 을 서버로 보내 DB 에서 확인작업을 진행한다.
     *    이때 정상적인 응답으로 돌아오는 값은 -1, 0, 1이다.
     *    응답이 -1인 경우, DB 상에 해당 유저의 아이디가 존재하지 않는 경우이므로 이 경우 DB 에 계정을 생성하고 필수절차를 진행한다.
     *    응답이 0인 경우, DB 상에 해당 유저의 아이디는 존재하나 필수정보가 없으므로 필수절차를 진행한다.
     *    응답이 1인 경우, 이미 정상적으로 등록된 회원이므로 해당 유저를 현재 로그인 유저로 저장한다.
     *
     * 4. UpdateCurrentUserTask 를 통해 SharedPreferences 에 현재 유저의 정보가 저장되며
     *    해당 작업이 끝나면 setTokenIdTask 를 진행한다.
     *
     * 5. setTokenIdTask 는 로그인 한 앱의 고유 토큰에 현재 로그인 된 아이디를 지정하는 Task 이며
     *    정상적으로 토큰이 세팅된 경우 GPS 활성화가 되어있는지 확인한다.
     *    GPS 기능이 활성화 되어있지 않은 경우 GPS 설정 창으로 이동한다.
     *    GPS 기능이 정상적으로 활성화 되어있는 경우 HomeActivity 로 이동한다.
     *
     *
     * 보류중. getLocation 은 현재 기기의 위치정보를 받아오는 메소드로서 위치를 받아온 후에
     *    updateLocationTask 를 진행한다.
     *
     * 보류중. updateLocationTask 는 현재 로그인 된 유저에게 앞서 받아온 위치정보를 DB 에 반영하는 Task 이며
     *    이 과정이 완료되면 Home 화면으로 이동한다.
     *
     *
     *
     * **/








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        // 타이틀 바를 숨김
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cl_main = findViewById(R.id.Cl_main);



        /**
         * 테스트를 위해 바로 액티비티 이동
         * **/






        //cl_main.getBackground().setAlpha(200);
        // 배경화면 투명도 설정
        Log.d("mTask", "Main onCreate");
        mainDialog = new ProgressDialog(this);
        mainDialog.setCancelable(false);
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        
        startService(new Intent(this, MyService.class));

        @SuppressLint("WrongThread") String mId = FirebaseInstanceId.getInstance().getId();
        String mToken = FirebaseInstanceId.getInstance().getToken();
        if(mId!=null && mToken!=null) {
            Log.d("fcmtest", mId);
            Log.d("fcmtest", mToken);
        } else {
            Log.d("fcmtest", "null");
        }




        /** AccountKit 오류 해결을 위한 임시 코드
         * 앱을 지웠다 다시 설치한 경우 일단 모든 토큰을 없애고 다시 로그인을 요구한다.**/
        SharedPreferences sf = getSharedPreferences("APP", MODE_PRIVATE);
        SharedPreferences.Editor editor = sf.edit();
        if(sf.contains("status")) {
            // if app has already installed.
            Log.d("apptest", "already installed");
        } else {
            // if this is first open.
            editor.putString("status", "1");
            editor.commit();
            Log.d("apptest", "first run");
            CurrentUserManager.LogoutAll();
        }


        // 권한 요청
        requestPermission();




    } // onCreate


    public void loginCheck() {
        /** 로그인 체크 **/
        LOGIN_CHECKER = 0;
        LOGIN_TYPE = 0;
        String userId = "";
        //AccountKit.initialize(getApplicationContext());

        com.facebook.accountkit.AccessToken accessToken = AccountKit.getCurrentAccessToken();
        if(accessToken != null) {
            Log.d("loginT", "AccountKit Token exist");
            Log.d("loginT", accessToken.getToken());
            LOGIN_TYPE = ACCOUNT_KIT;
            LOGIN_CHECKER =+ 1;
            //userId = AccountKit.getCurrentAccessToken().getAccountId();
        } else {
            Log.d("loginT", "AccountKit Token doesn't exist");
        }
        if(com.kakao.auth.Session.getCurrentSession().isOpened()) {
            Log.d("loginT", "Kakao Session is opened");
            LOGIN_TYPE = KAKAO;
            LOGIN_CHECKER =+ 1;

        } else {
            Log.d("loginT", "Kakao Session is closed");
        }
        if(AccessToken.getCurrentAccessToken() != null) {
            Log.d("loginT", "Facebook Token exist");
            LOGIN_TYPE = FACEBOOK;
            LOGIN_CHECKER =+ 1;
            //userId = AccessToken.getCurrentAccessToken().getUserId();
        } else {
            Log.d("loginT", "Facebook Token doesn't exist");
        }
//        CurrentUserManager.setCurrentUserId(this, userId);
//        Log.d("idtest", userId);
        /**
         * LOGIN_CHECKER 는 현재 유지되고 있는 AccessToken 이나 열려있는 Session 의 갯수를 의미한다.
         * LOGIN_CHECKER 가 1이 아니면 정상적인 로그인이 되지 않았다고 판단한다.
         * LOGIN_TYPE 은 현재 로그인된 유저가 어떤 방법으로 로그인했는지를 나타낸다. **/






        /** 토큰이 정상적으로 존재하는 경우(LOGIN_CHECKER == 1) **/
        if(LOGIN_CHECKER ==1) {
            Log.d("loginT", " Checker = 1");

            switch(LOGIN_TYPE) {

                case ACCOUNT_KIT:
                    login_id = AccountKit.getCurrentAccessToken().getAccountId();
                    login_type = "ACCOUNT_KIT";
                    mTask = new LoginTask();
                    mTask.execute();
                    break;
                case KAKAO:
                    KakaoUserLogin();
                    /*CurrentUserManager.setCurrentUserId(this, getKakaoUserId());
                    login_id =  CurrentUserManager.getCurrentUserId(this);
                    login_type = "KAKAO";*/
                    break;
                case FACEBOOK:
                    login_id = AccessToken.getCurrentAccessToken().getUserId();
                    login_type = "FACEBOOK";
                    mTask = new LoginTask();
                    mTask.execute();
                    break;
                default:
                    break;

            }


            Log.d("mTask", "login_id : "+login_id+"\nlogin_type : "+login_type);


//            if(getIntent().getStringExtra("ID") != null) {
//                login_id = getIntent().getExtras().getString("ID");
//                login_type = getIntent().getExtras().getString("TYPE");
//            }
//            //새로 로그인하여 접속한 경우 Intent 가 존재하므로 넘어온 id와 type 를 파라미터로 보낸다.
//
//            else {
//                if(CurrentUserManager.getCurrentUserId(this) != null) {
//                    login_id = CurrentUserManager.getCurrentUserId(this);
//                    login_type = "";
//                } else {
//                    login_id = "";
//                    login_type = "";
//                }
//            }
            //기존에 로그인 한 토큰이 남아있는 경우 현재 저장되어 있는 아이디를 확인한다.
            //저장된 아이디가 있으면 계정이 존재하므로 해당 아이디만 파라미터로 보낸다.
            //저장된 아이디도 없는 경우 꼬인 상태이므로 아이디와 타입 모두 빈 값을 파라미터로 보낸다.
            //이후 태스크에서 어차피 오류값을 반환할 것이므로 다시 로그인하는 과정으로 돌아가게 된다.

            /** 회원정보 확인을 위한 login_id, login_type 을 세팅하는 부분
             * LoginActivity 로 부터 전달받은 값이 있으면 저장하고
             * 없으면 현재 저장되어 있는 아이디 값을 넣는다.
             * 나머지는 #default 처리 **/

        }

        /** 토큰이 정상적으로 존재하지 않는 경우(LOGIN_CHECKER != 1)이므로 다시 로그인을 시도한다. **/
        else {
            Log.d("loginT", "Checker != 1");
            Toast.makeText(this, "정상적인 토큰이 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
            CurrentUserManager.LogoutAll();
            CurrentUserManager.initCurrentUser(this);
            goToLoginActivityAndFinish();
        }
    }




    @Override
    protected void onResume() {
        super.onResume();
        Log.d("mTask", "Main onResume");
    }


    @Override
    protected void onStart() {
        mainDialog.show();
        super.onStart();
        Log.d("mTask", "Main onStart");
    }

    @Override
    protected void onStop() {
        mainDialog.hide();
        if(mainDialog != null){
            mainDialog.dismiss();
        }
        Log.d("mTask", "Main onStop");
        super.onStop();
    }



    /** 현재 돌고있는 AsyncTask 가 있다면 Activity 종료시에 같이 종료시킨다. **/
    @Override
    protected void onDestroy() {
        Log.d("mTask", "Main onDestroy");
        try {
            if (mTask.getStatus() == AsyncTask.Status.RUNNING) {
                mTask.cancel(true);
            } else {
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        super.onDestroy();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("mTask", "Main onActivityResult");
        if(requestCode == TAG_SETTING) {

            if(lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                goToHomeActivityAndFinish();
            } else {
                goToActivateGPS();

            }

        }
    }







    /** Activity 전환 **/
    public void goToLoginActivityAndFinish() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    //로그인 화면으로 이동

    public void goToHomeActivityAndFinish() {
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
    //홈 화면으로 이동

    public void goToRegisterActivityAndFinish() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }
    //가입절차 화면으로 이동








    /** 회원정보 확인을 위한 Task **/
    private class LoginTask extends AsyncTask<String, Void, String> {


        ProgressDialog mDialog = new ProgressDialog(MainActivity.this);
        // 로딩화면을 위한 다이얼로그 선언




        @Override
        protected void onPreExecute() {


            //mainDialog.show();
            // 로딩화면 띄우기
        }


        @Override
        protected String doInBackground(String... strings) {
            OkHttpClient client = new OkHttpClient();
            Log.d("intask", "login_id : "+login_id);
            Log.d("intask", "login_type : "+login_type);

            String strUrl = "http://115.71.238.160/novaproject1/MainActivity/user_create.php";
            String result = null;

            RequestBody body = new FormBody.Builder()
                    .add("id", login_id) //현재 로그인 된 유저의 id를 'id'라는 키값으로 보냄
                    .add("type", login_type)
                    .build();
            Request request = new Request.Builder()
                    .url(strUrl)
                    .post(body)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                result = response.body().string();
                // 서버에 id 라는 키 값으로 현재 아이디(CurrentUserId)를 보낸다.
                // 이때 아이디가 존재하면 1을, 존재하지 않으면 -1을 결과값으로 리턴받는다.
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }



        @Override
        protected void onPostExecute(String s) {

            Log.d("mTask", s);
            //mDialog.dismiss();
            // 로딩화면 제거

            if(s.equals("1")) {
                Toast.makeText(MainActivity.this, "이미 가입된 회원입니다.", Toast.LENGTH_SHORT).show();
                //requestPermission();
                //goToHomeActivityAndFinish();
                new MainActivity.UpdateCurrentUserTask(login_id).execute();
                //이미 가입된 회원이면 홈화면으로 이동

            }
            else if(s.equals("0")) {
                Toast.makeText(MainActivity.this, "가입절차가 완료되지 않았습니다. \n 필수정보를 입력해주세요.", Toast.LENGTH_SHORT).show();
                goToRegisterActivityAndFinish();
                //필수정보를 입력하지 않은 회원의 경우 입력화면으로 이동
            }
            else if(s.equals("-1")) {
                if(login_id.equals("")){
                    CurrentUserManager.initCurrentUser(MainActivity.this);
                    CurrentUserManager.LogoutAll();
                    goToLoginActivityAndFinish();
                    //위에서 언급한 꼬인 상황이므로 로그인 절차를 다시 진행한다.

                } else {
                    Toast.makeText(MainActivity.this, "신규 회원으로 등록합니다.", Toast.LENGTH_SHORT).show();
                    goToRegisterActivityAndFinish();
                    //신규 회원이면 가입화면으로 이동
                }


            }
            else {
                Toast.makeText(MainActivity.this, "유저정보 확인 실패", Toast.LENGTH_SHORT).show();
                CurrentUserManager.LogoutAll();
                CurrentUserManager.setCurrentUserId(MainActivity.this, null);





                //예외의 경우 로그아웃 처리 후 로그인 화면으로 이동
            }
            //mainDialog.hide();

        }

    }//LoginTask



    /** Permission 여부를 확인하고 요청한다. **/
    public void requestPermission() {
        Log.d("permissionT", "요청실행");
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("permissionT", "권한이 없네?");
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.d("permissionT", "설명이 좀 필요해");

                AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
                adb.setMessage("서비스 이용을 위해 위치정보가 필요합니다.");
                adb.setCancelable(false);

                //Positive[오른쪽]
                adb.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                    }
                });
                adb.show();

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                Log.d("permissionT", "설명 안해도 돼");
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            Log.d("permissionT", "권한 이미 있어");
            loginCheck();
        }
    }




    /** Permission 콜백 **/
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("permissionT", "권한 승인했음");
                    Toast.makeText(MainActivity.this, "권한이 승인되었습니다.", Toast.LENGTH_SHORT).show();
                    loginCheck();
                    //new UpdateCurrentUserTask(login_id).execute();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    Log.d("permissionT", "권한 거절함");
                    Toast.makeText(this, "권한 요청이 거절되었습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }


                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }









    private void KakaoUserLogin() {

        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onSessionClosed(ErrorResult errorResult) {
            }

            @Override
            public void onNotSignedUp() {

            }

            @Override
            public void onSuccess(UserProfile result) {
                Log.d("idtest", "id : " + Long.toString(result.getId()));
                Log.d("idtest", result.getProfileImagePath());
                Log.d("idtest", result.getThumbnailImagePath());
                login_id = String.valueOf(result.getId());
                login_type = "KAKAO";

                new LoginTask().execute();
            }
        });
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
                Log.d("chatTT", data);
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
            CurrentUserManager.setCurrentUser(getApplicationContext(), user);
            String paramsId = CurrentUserManager.getCurrentUserId(getApplicationContext());


            if(FirebaseInstanceId.getInstance().getToken()!= null) {
                String paramsToken = FirebaseInstanceId.getInstance().getToken();
                new setTokenIdTask().execute(paramsId, paramsToken);
            } else {
                Toast.makeText(MainActivity.this, "토큰이 제대로 생성되지 않았습니다. 앱을 재실행하여 주세요.", Toast.LENGTH_SHORT).show();
                finish();
            }

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
                Toast.makeText(MainActivity.this, "토큰 id 업데이트 성공"+"\n결과값 : "+s, Toast.LENGTH_SHORT).show();
            } else if (s.equals("0")) {
                Toast.makeText(MainActivity.this, "토큰 id 업데이트 실패"+"\n결과값 : "+s, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "토큰 존재 x"+"\n결과값 : "+s, Toast.LENGTH_SHORT).show();
            }


            if(lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                goToHomeActivityAndFinish();
                Log.d("T2", "GPS is enabled");
            } else {
                goToActivateGPS();
                Log.d("T2", "GPS is disabled");

            }

        }
    }


    /** GPS 동의를 물어보고 활성화시키는 기능 **/
    public void goToActivateGPS() {
        AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
        adb.setMessage("서비스 이용을 위해 GPS 활성화가 필요합니다.");
        adb.setCancelable(false);

        //Negative[왼쪽]
        adb.setNegativeButton("동의안함", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        //Positive[오른쪽]
        adb.setPositiveButton("동의", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which){
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                startActivityForResult(intent, TAG_SETTING);
            }
        });
        adb.show();
    }




    /** 위치정보를 업데이트하는 Task **/
    class updateLocationTask extends AsyncTask<String, Void, String> {

        String _id;
        String _status;
        String _longi;
        String _lati;

        @Override
        protected String doInBackground(String... strings) {
            _id = strings[0];
            _status = strings[1];
            _longi = strings[2];
            _lati = strings[3];

            Log.d("locT", "doing updateLocationTask");

            String result = null;
            try {
                URL url = new URL("http://115.71.238.160/novaproject1/setlocation.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST"); // URL 요청에 대한 메소드 설정 : POST.
                conn.setRequestProperty("Accept-Charset", "UTF-8"); // Accept-Charset 설정.
                conn.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;cahrset=UTF-8");

                String outData = "id="+_id+"&status="+_status+"&longi="+_longi+"&lati="+_lati;
                OutputStream outputStream = conn.getOutputStream();
                outputStream.write(outData.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                /** 응답 받기 **/
                if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.d("thTT", conn.getResponseMessage());
                }
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                String line;
                StringBuilder page = new StringBuilder();

                // 라인을 받아와 합친다.
                while ((line = bufferedReader.readLine()) != null){
                    page.append(line);
                }

                result = page.toString();
                Log.d("thTT", page.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("locT", "post updateLocationTask");
            if(s.equals("1")) {
                Toast.makeText(getApplicationContext(), "위치정보 저장 성공", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "에러코드 : "+s, Toast.LENGTH_SHORT).show();
            }
            goToHomeActivityAndFinish();

        }
    }


    public void getLocation() {
//        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // GPS 제공자의 정보가 바뀌면 콜백하도록 리스너 등록하기~!!!

        final LocationListener mLocationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                //여기서 위치값이 갱신되면 이벤트가 발생한다.
                //값은 Location 형태로 리턴되며 좌표 출력 방법은 다음과 같다.

                Log.d("locT", "onLocationChanged, location:" + location);
                double longitude = location.getLongitude(); //경도
                double latitude = location.getLatitude();   //위도
                double altitude = location.getAltitude();   //고도
                float accuracy = location.getAccuracy();    //정확도
                String provider = location.getProvider();   //위치제공자
                //Gps 위치제공자에 의한 위치변화. 오차범위가 좁다.
                //Network 위치제공자에 의한 위치변화
                //Network 위치는 Gps에 비해 정확도가 많이 떨어진다.
                String result = "위치정보 : " + provider + "\n위도 : " + longitude + "\n경도 : " + latitude
                        + "\n고도 : " + altitude + "\n정확도 : "  + accuracy;

                Log.d("locT", result);
//                Toast.makeText(HomeActivity.this, result, Toast.LENGTH_SHORT).show();
                longi = location.getLongitude();
                lati = location.getLatitude();

                String id = CurrentUserManager.getCurrentUserId(getApplicationContext());
                String status = "1";

                new updateLocationTask().execute(id, status, longi+"", lati+"");

            }
            public void onProviderDisabled(String provider) {
                // Disabled시
                Log.d("locT", "onProviderDisabled, provider:" + provider);
            }

            public void onProviderEnabled(String provider) {
                // Enabled시
                Log.d("locT", "onProviderEnabled, provider:" + provider);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
                // 변경시
                Log.d("locT", "onStatusChanged, provider:" + provider + ", status:" + status + " ,Bundle:" + extras);
            }
        };



        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_DENIED) {
            Log.d("locT", "권한있음");
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, // 등록할 위치제공자
                    600000, // 통지사이의 최소 시간간격 (miliSecond)
                    1, // 통지사이의 최소 변경거리 (m)
                    mLocationListener);
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자
                    600000, // 통지사이의 최소 시간간격 (miliSecond)
                    1, // 통지사이의 최소 변경거리 (m)
                    mLocationListener);

        } else {
            Log.d("locT", "권한없음");
        }

    }

}
