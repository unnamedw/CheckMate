package com.example.msg_b.checkmate;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.msg_b.checkmate.util.CurrentUserManager;
import com.example.msg_b.checkmate.util.User;
import com.example.msg_b.checkmate.util.Util;
import com.example.msg_b.checkmate.R;
import com.google.gson.Gson;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    TextView tv_age, tv_live, tv_height, tv_nickname_check, tv_age_check, tv_live_check, tv_height_check, tv_status, tv_profile;
    EditText et_nickname, et_job;
    RadioGroup rg_sex;
    RadioButton rb_male, rb_female;
    RelativeLayout llContainer;
    Button btn_commit;
    ImageView iv_profile;


    ProgressDialog mDialog;
    String CurrentId;

    private String userProfile;
    private String userAge;
    private String userLive;
    private String userSex;
    private String userJob;
    private String userNickname;
    private String userHeight;
    private String userIntroduce;
    private boolean profileCheck;
    private boolean ageCheck;
    private boolean liveCheck;
    private boolean sexCheck;
    private boolean jobCheck;
    private boolean nicknameCheck;
    private boolean heightCheck;


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        // 타이틀 바를 숨김
        super.onCreate(savedInstanceState);
        androidx.appcompat.app.ActionBar ab = getSupportActionBar();
        ab.setTitle("필수정보입력");
        setContentView(R.layout.activity_register);

        Toast.makeText(this, CurrentUserManager.getCurrentUserId(this), Toast.LENGTH_SHORT).show();


        tv_age = findViewById(R.id.Tv_age);
        tv_live = findViewById(R.id.Tv_live);
        tv_height = findViewById(R.id.Tv_height);
        tv_nickname_check = findViewById(R.id.Tv_nickname_check);
        tv_age_check = findViewById(R.id.Tv_age_check);
        tv_live_check = findViewById(R.id.Tv_live_check);
        tv_height_check = findViewById(R.id.Tv_height_check);
        tv_status = findViewById(R.id.Tv_status);
        tv_profile = findViewById(R.id.Tv_profile);
        et_nickname = findViewById(R.id.Et_nickname);
        et_job = findViewById(R.id.Et_job);
        rg_sex = findViewById(R.id.Rg_sex);
        rb_male = findViewById(R.id.Rb_male);
        rb_female = findViewById(R.id.Rb_female);
        llContainer = findViewById(R.id.Ll_backgound);
        btn_commit = findViewById(R.id.Btn_commit);
        iv_profile = findViewById(R.id.Iv_profile);
        iv_profile.setClipToOutline(true);



        /** 유저정보 초기화 **/
        userProfile = "";
        userAge = "";
        userHeight = "";
        userJob = "";
        userLive = "";
        userNickname="";
        userSex = "";
        userIntroduce = "";

        /** Checker 초기화 **/
        profileCheck = false;
        ageCheck = false;
        heightCheck = false;
        jobCheck = false;
        liveCheck = false;
        nicknameCheck = false;
        sexCheck = false;




        /** 프로필 사진 등록 **/
        iv_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().start(RegisterActivity.this);
            }
        });







        /** 닉네임 중복확인 **/
        tv_nickname_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_nickname.getText().toString().length()>1) {
                    userNickname = et_nickname.getText().toString();
                    new NicknameCheckTask(userNickname).execute();
                } else {
                    Toast.makeText(RegisterActivity.this, "닉네임은 3자 이상 10자 이하입니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });



        /** 성별체크 **/
        rg_sex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.Rb_male:
                        userSex = "남";
                        break;
                    case R.id.Rb_female:
                        userSex = "여";
                        break;
                    default:
                        break;
                }
                sexCheck = true;
            }
        });




        /** 생년월일 선택 **/
        tv_age_check.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                DatePickerDialog mDialog = new DatePickerDialog(RegisterActivity.this);
                mDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String selectedAge = getAgeFromBirth(year);
                        
                        // 18세 이상인 경우만 서비스 이용이 가능
                        if(Integer.valueOf(selectedAge)<18) {
                            Toast.makeText(RegisterActivity.this, "18세 미만은 이용하실 수 없습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            tv_age.setText(selectedAge);
                            userAge = selectedAge;
                            ageCheck = true;
                        }
                        
                    }
                });
                mDialog.show();
            }
        });



        /** 지역 선택 **/
        tv_live_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(RegisterActivity.this);
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

            }
        });



        /** 키 선택 **/
        tv_height_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<String> mHeight = new ArrayList<>();
                for(int i=0; i<60; i++){
                    mHeight.add(i, Integer.toString(i+140));
                }

                //Create sequence of items
                final CharSequence[] Heights = mHeight.toArray(new String[mHeight.size()]);
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(RegisterActivity.this);
                dialogBuilder.setTitle("키");
                dialogBuilder.setItems(Heights, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        String selectedItem = Heights[item].toString();  //Selected item in listview
                        tv_height.setText(selectedItem + "cm");
                        userHeight = selectedItem;
                        heightCheck = true;
                    }
                });
                AlertDialog alertDialogObject = dialogBuilder.create();
                alertDialogObject.show();

            }
        });



        /** 가입완료 버튼 **/
        btn_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_nickname.getText().toString() != null && et_nickname.getText().toString().length()>1){
                    userNickname = et_nickname.getText().toString();
                    nicknameCheck = true;
                }
                if(et_job.getText().toString() != null) {
                    userJob = et_job.getText().toString();
                    jobCheck = true;
                }


                if(profileCheck&&liveCheck&&jobCheck&&heightCheck&&nicknameCheck&&sexCheck&&ageCheck) {
                    new AddUserInfoTask().execute();
                } else {
                    Toast.makeText(RegisterActivity.this, "가입정보를 확인해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }//onCreate


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                //찍은 이미지의 경로
                userProfile = result.getUri().getPath();
                profileCheck = true;

                //이미지뷰에 띄워줌
                iv_profile.setImageURI(result.getUri());
                tv_profile.setVisibility(View.INVISIBLE);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }







    /** 가입된 유저정보를 DB에 저장하는 Task **/
    class AddUserInfoTask extends AsyncTask<String, Void, String> {

        String key;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = new ProgressDialog(RegisterActivity.this);
            mDialog.show();

            key = Util.keyMaker();
        }


        @Override
        protected String doInBackground(String... strings) {

            File imgFile = new File(userProfile);
            String strUrl = "http://115.71.238.160/novaproject1/RegisterActivity/addinfo.php";
            String result = null;
            CurrentId = CurrentUserManager.getCurrentUserId(RegisterActivity.this);
            Log.d("regtest", CurrentId);

            if(imgFile.exists()){ // 파일이 존재하는 경우
                if(imgFile.isFile()) { //파일이 맞으면
                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("file", CurrentId + "_" + key, RequestBody.create(MediaType.parse("image/jpg"), imgFile))
                            .addFormDataPart("id", CurrentId)
                            .addFormDataPart("nickname", userNickname)
                            .addFormDataPart("sex", userSex)
                            .addFormDataPart("live", userLive)
                            .addFormDataPart("job", userJob)
                            .addFormDataPart("age", userAge)
                            .addFormDataPart("height", userHeight)
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
                }
            }

            return result;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("regtest", s);
            if(s.equals("1")) {
                Toast.makeText(RegisterActivity.this, "가입정보가 저장되었습니다.", Toast.LENGTH_SHORT).show();
                new UpdateCurrentUserTask(CurrentId).execute();
            } else {
                Toast.makeText(RegisterActivity.this, "예상치 못한 오류가 발생하였습니다. \n앱을 재실행하여 주세요.", Toast.LENGTH_SHORT).show();
                CurrentUserManager.LogoutAll();
                finish();
            }

            mDialog.dismiss();
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
            mDialog = new ProgressDialog(RegisterActivity.this);
            mDialog.show();

        }


        @Override
        protected String doInBackground(String... strings) {

            OkHttpClient client = new OkHttpClient();

            String strUrl = "http://115.71.238.160/novaproject1/RegisterActivity/nicknameCheck.php";
            String result = null;

            RequestBody body = new FormBody.Builder()
                    .add("id", CurrentUserManager.getCurrentUserId(RegisterActivity.this))
                    .add("nickname", userNickname)
                    .build();
            Request request = new Request.Builder()
                    .url(strUrl)
                    .post(body)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                result = response.body().string();
            }
            catch (Exception e) {
                e.printStackTrace();
            }


            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mDialog.dismiss();

            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
            if(s.equals("1")) {
                builder.setMessage("이미 존재하는 닉네임입니다.");
            }
            else {
                builder.setMessage("사용할 수 있는 닉네임입니다.");
            }
            builder.setTitle("")
                    .setPositiveButton("확인",null)
                    .create() //다이얼로그생성
                    .show(); // 보여주기

        }
    }






    /** 태어난 년도로 현재나이를 구한다. **/
    public String getAgeFromBirth(int birthY) {
        long now = System.currentTimeMillis();
        Date date = new Date(now);

        SimpleDateFormat y_form = new SimpleDateFormat("yyyy");
        int y_now = Integer.valueOf(y_form.format(date));
        String result = Integer.toString(y_now - birthY + 1);

        return result;
    }






    /** 뒤로가기 버튼 **/
    @Override
    public void onBackPressed() {

        AlertDialog.Builder adb = new AlertDialog.Builder(RegisterActivity.this);
        adb.setMessage("필수정보를 입력중입니다. \n저장하지 않은 정보는 사라집니다");

        //Positive[오른쪽]
        adb.setPositiveButton("종료", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which){
                finish();
            }
        });

        //Negative[왼쪽]
        adb.setNegativeButton("돌아가기", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                //Do something when user press no button from alert dialog
            }
        });
        adb.show();
    }




    /** Activity 이동 **/
    public void goToHomeActivityAndFinish() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }


    /** 현재유저 세팅 TASK **/
    private class UpdateCurrentUserTask extends AsyncTask<String, Void, User> {

        String mId;
        public UpdateCurrentUserTask(String id) {
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

            CurrentUserManager.setCurrentUser(getApplicationContext(), user);
            goToHomeActivityAndFinish();
        }
    }


}
