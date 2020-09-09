package com.example.msg_b.checkmate.mainFragment.activityInProfile;

//import android.support.v7.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.msg_b.checkmate.R;
import com.example.msg_b.checkmate.util.CurrentUserManager;
import com.example.msg_b.checkmate.util.User;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Profile2Activity extends AppCompatActivity implements View.OnClickListener {


    TextView tv_live, tv_live_check, tv_nickname_check;
    EditText et_nickname, et_job, et_introduce;
    Button btn_commit;

    String userNickname, userJob, userLive, userIntroduce;
    boolean nicknameCheck, jobCheck, liveCheck, introduceCheck;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        androidx.appcompat.app.ActionBar ab = getSupportActionBar();
        ab.setTitle("프로필 수정");
        setContentView(R.layout.activity_profile2);


        /** 유저정보를 받아오고 CurrentUserManager 를 통해 SharedPreferences 에 저장 **/
        new Profile2Activity.GetCurrentUserTask(CurrentUserManager.getCurrentUserId(this)).execute();

        tv_live = findViewById(R.id.Tv_live);
        tv_live_check = findViewById(R.id.Tv_live_check);
        tv_nickname_check = findViewById(R.id.Tv_nickname_check);
        et_nickname = findViewById(R.id.Et_nickname);
        et_job = findViewById(R.id.Et_job);
        et_introduce = findViewById(R.id.Et_introduce);
        btn_commit = findViewById(R.id.Btn_commit);


        //onClickEvent
        tv_live_check.setOnClickListener(this);
        btn_commit.setOnClickListener(this);

        /** 유저정보 초기화 **/
        userNickname="";
        userJob = "";
        userLive = "";
        userIntroduce = "";

        /** Checker 초기화 **/
        nicknameCheck = false;
        jobCheck = false;
        liveCheck = false;
        introduceCheck = false;

        /** 닉네임 입력 칸에 Focus Listener 설정 **/
        et_nickname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    tv_nickname_check.setVisibility(View.GONE);
                } else {

                    new Profile2Activity.NicknameCheckTask(et_nickname.getText().toString()).execute();
                }

            }
        });


    }



    @Override
    public void onClick(View v) {
        User user = CurrentUserManager.getCurrentUser(Profile2Activity.this);


        /** <프로필 사진 onClickListener>
         * 이전 사진이 등록된 경우가 아니면 먼저 이전 사진을 먼저 등록해달라는 토스트 메시지를 띄움.
         * 이전 사진이 등록되어 있다면 현재 사진이 있는지 없는지 구분한다.
         * 현재 사진이 있는 경우에는 1)삭제하는 경우와 2)바꾸는 경우로 나눌 수 있다.
         * 현재 사진이 없는 경우에는 바로 프로필 사진을 추가한다. **/
        switch (v.getId()) {
            case R.id.Tv_live_check:
                ArrayList<String> mLive =  new ArrayList<>();
                mLive.add("서울");
                mLive.add("수도권");
                mLive.add("충남");
                mLive.add("충북");
                mLive.add("강원");
                mLive.add("경북");
                mLive.add("경남");
                mLive.add("전북");
                mLive.add("전남");
                mLive.add("제주");
                mLive.add("해외");

                final CharSequence[] Lives = mLive.toArray(new String[mLive.size()]);
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Profile2Activity.this);
                dialogBuilder.setTitle("지역");
                dialogBuilder.setItems(Lives, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        String selectedItem = Lives[item].toString();  //Selected item in listview
                        tv_live.setText(selectedItem);
                        userLive = selectedItem;
                        liveCheck = true;
                    }
                });
                AlertDialog alertDialogObject = dialogBuilder.create();
                alertDialogObject.show();
                break;

            case R.id.Btn_commit:
                if(et_nickname.getText().toString() != null && et_nickname.getText().length()>2) {
                    userNickname = et_nickname.getText().toString();
                    nicknameCheck = true;
                }
                if(et_job.getText().toString() != null && et_job.getText().length()>0) {
                    userJob = et_job.getText().toString();
                    jobCheck = true;
                }
                if(et_introduce.getText().toString() != null) {
                    userIntroduce = et_introduce.getText().toString();
                    introduceCheck = true;
                } else {
                    userIntroduce = "";
                    introduceCheck = true;
                }
                if(tv_live.getText().toString() != null) {
                    userLive = tv_live.getText().toString();
                    liveCheck = true;
                }

                if(nicknameCheck==true && jobCheck==true && introduceCheck==true && liveCheck==true) {
                    new Profile2Activity.UpdateUserInfoTask().execute();
                } else {
                    Toast.makeText(Profile2Activity.this, "프로필 정보를 확인해주세요.", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                break;
        }
    }





    /** 뷰에 이미지와 텍스트 세팅 **/
    public void setViewFromCurrentUser() {
        User cUser = CurrentUserManager.getCurrentUser(this);
        tv_live.setText(cUser.getLive());
        et_nickname.setText(cUser.getNickname());
        et_introduce.setText(cUser.getIntroduce());
        et_job.setText(cUser.getJob());
    }




    /** 현재 유저의 정보를 받아오는 Task **/
    class GetCurrentUserTask extends AsyncTask<String, Void, User> {

        String mId;
        public GetCurrentUserTask(String id) {
            this.mId = id;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected User doInBackground(String... strings) {

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

                User mUser = new Gson().fromJson(data, User.class);
                result = mUser;
                if(result != null) {
                    CurrentUserManager.setCurrentUser(Profile2Activity.this, result);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }


        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);
            setViewFromCurrentUser();
        }
    }





    /** 닉네임 중복을 확인하는 Task **/
    class NicknameCheckTask extends AsyncTask<String, Void, String> {

        String mNickname;

        public NicknameCheckTask(String nick) {
            this.mNickname = nick;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        @Override
        protected String doInBackground(String... strings) {

            OkHttpClient client = new OkHttpClient();

            String strUrl = "http://115.71.238.160/novaproject1/RegisterActivity/nicknameCheck.php";
            String result = null;

            RequestBody body = new FormBody.Builder()
                    .add("id", CurrentUserManager.getCurrentUserId(Profile2Activity.this))
                    .add("nickname", mNickname)
                    .build();
            Request request = new Request.Builder()
                    .url(strUrl)
                    .post(body)
                    .build();
            Log.d("utest", CurrentUserManager.getCurrentUserId(Profile2Activity.this));
            try {
                Response response = client.newCall(request).execute();
                result = response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }


            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s.equals("1")) {
                tv_nickname_check.setVisibility(View.VISIBLE);
                tv_nickname_check.setText("이미 존재하는 닉네임입니다.");
            } else {
                tv_nickname_check.setVisibility(View.VISIBLE);
                tv_nickname_check.setText("사용할 수 있는 닉네임입니다.");
            }

        }

    }






    /** 수정된 유저정보를 DB에 저장하는 Task **/
    class UpdateUserInfoTask extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected String doInBackground(String... strings) {

            String strUrl = "http://115.71.238.160/novaproject1/HomeActivity/ProfileFragment/ProfileActivity/addinfo.php";
            String result = null;
            String CurrentId = CurrentUserManager.getCurrentUserId(Profile2Activity.this);
            Log.d("regtest", CurrentId);

            RequestBody requestBody = new FormBody.Builder()
                    .add("id", CurrentId)
                    .add("nickname", userNickname)
                    .add("live", userLive)
                    .add("job", userJob)
                    .add("introduce", userIntroduce)
                    .build();

            Request request = new Request.Builder()
                    .url(strUrl)
                    .post(requestBody)
                    .build();

            try {
                OkHttpClient client = new OkHttpClient();
                Response response = client.newCall(request).execute();
                result = response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }


            return result;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("1")) {
                Toast.makeText(Profile2Activity.this, "프로필 정보가 수정되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(Profile2Activity.this, "실패", Toast.LENGTH_SHORT).show();
            }
        }
    }



}
