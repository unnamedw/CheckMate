package com.example.msg_b.checkmate.mainFragment.activityInProfile;

//import android.support.v7.app.AppCompatActivity;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.msg_b.checkmate.R;
import com.example.msg_b.checkmate.util.CurrentUserManager;
import com.example.msg_b.checkmate.util.User;
import com.example.msg_b.checkmate.util.Util;
import com.example.msg_b.checkmate.util.resultGson;
import com.google.gson.Gson;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{

    ImageView iv_profile, iv_profile2, iv_profile3, iv_profile4, iv_profile5, iv_profile6;
    TextView tv_live, tv_live_check, tv_nickname_check;
    EditText et_nickname, et_job, et_introduce;
    Button btn_commit;

    int num_profile;
    String userProfile, userProfile2, userProfile3, userProfile4, userProfile5, userProfile6, userNickname, userJob, userLive, userIntroduce;
    boolean profileCheck, profile2Check, profile3Check, profile4Check, profile5Check, profile6Check, nicknameCheck, jobCheck, liveCheck, introduceCheck;

    ProgressDialog mDialog;
    ArrayList<String> imgList;
    String[] imgArray;
    ArrayList<String> profileList;


    /** <ProfileActivity 설명>
     * onCreate 호출 시 DB 에서 유저 정보를 받아오고
     * 저장 버튼을 눌렀을 때 DB에 유저 정보를 저장한다.
     * 이외에는 네트워크 작업 X
     * 단, 닉네임 중복체크는 제외. **/
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        androidx.appcompat.app.ActionBar ab = getSupportActionBar();
        ab.setTitle("프로필 관리");
        setContentView(R.layout.activity_profile);
        Log.d("PA", "onCreate");

        /** 유저정보를 받아오고 CurrentUserManager 를 통해 SharedPreferences 에 저장 **/
        new GetCurrentUserTask(CurrentUserManager.getCurrentUserId(this)).execute();

        tv_live = findViewById(R.id.Tv_live);
        tv_live_check = findViewById(R.id.Tv_live_check);
        tv_nickname_check = findViewById(R.id.Tv_nickname_check);
        et_nickname = findViewById(R.id.Et_nickname);
        et_job = findViewById(R.id.Et_job);
        et_introduce = findViewById(R.id.Et_introduce);
        btn_commit = findViewById(R.id.Btn_commit);

        iv_profile = findViewById(R.id.Iv_profile);
        iv_profile2 = findViewById(R.id.Iv_profile2);
        iv_profile3 = findViewById(R.id.Iv_profile3);
        iv_profile4 = findViewById(R.id.Iv_profile4);
        iv_profile5 = findViewById(R.id.Iv_profile5);
        iv_profile6 = findViewById(R.id.Iv_profile6);
        //imageFit
        iv_profile.setClipToOutline(true);
        iv_profile2.setClipToOutline(true);
        iv_profile3.setClipToOutline(true);
        iv_profile4.setClipToOutline(true);
        iv_profile5.setClipToOutline(true);
        iv_profile6.setClipToOutline(true);
        //onClickEvent
        iv_profile.setOnClickListener(this);
        iv_profile2.setOnClickListener(this);
        iv_profile3.setOnClickListener(this);
        iv_profile4.setOnClickListener(this);
        iv_profile5.setOnClickListener(this);
        iv_profile6.setOnClickListener(this);
//        tv_live_check.setOnClickListener(this);
        btn_commit.setOnClickListener(this);

        /** 유저정보 초기화 **/
        userProfile = "";
        userProfile2 = "";
        userProfile3 = "";
        userProfile4 = "";
        userProfile5 = "";
        userProfile6 = "";
//        userNickname="";
//        userJob = "";
//        userLive = "";
//        userIntroduce = "";

        /** Checker 초기화 **/
        profileCheck = false;
        profile2Check = false;
        profile3Check = false;
        profile4Check = false;
        profile5Check = false;
        profile6Check = false;
//        nicknameCheck = false;
//        jobCheck = false;
//        liveCheck = false;
//        introduceCheck = false;

        /** 닉네임 입력 칸에 Focus Listener 설정 **/
//        et_nickname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if(hasFocus) {
//                    tv_nickname_check.setVisibility(View.GONE);
//                } else {
//
//                    new NicknameCheckTask(et_nickname.getText().toString()).execute();
//                }
//
//            }
//        });



    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.d("PA", "onStart");

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("PA", "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("PA", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("PA", "onStop");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                //찍은 이미지의 경로

                String croppedPath = result.getUri().getPath();
                Uri croppedUri = result.getUri();
                new EditImgTask(croppedPath).execute();

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    @Override
    public void onClick(View v) {
        User user = CurrentUserManager.getCurrentUser(ProfileActivity.this);


        /** <프로필 사진 onClickListener>
         * 이전 사진이 등록된 경우가 아니면 먼저 이전 사진을 먼저 등록해달라는 토스트 메시지를 띄움.
         * 이전 사진이 등록되어 있다면 현재 사진이 있는지 없는지 구분한다.
         * 현재 사진이 있는 경우에는 1)삭제하는 경우와 2)바꾸는 경우로 나눌 수 있다.
         * 현재 사진이 없는 경우에는 바로 프로필 사진을 추가한다. **/
        switch (v.getId()) {
            case R.id.Iv_profile:
                num_profile = 1;
                CropImage.activity().start(ProfileActivity.this);
                break;

            case R.id.Iv_profile2:
                num_profile = 2;
                if(imgArray[1] != null){ //현재 사진이 등록되어 있는 경우
                    imgEditDialog();
                } else {
                    CropImage.activity().start(ProfileActivity.this);
                }

                break;

            case R.id.Iv_profile3:
                num_profile = 3;
                if(imgArray[1] != null) { //이전 사진이 등록되어 있는 경우
                    if(imgArray[2] != null){ //현재 사진이 등록되어 있는 경우
                        imgEditDialog();
                    } else { //현재 사진이 없는 경우
                        CropImage.activity().start(ProfileActivity.this);
                    }
                } else { //이전 사진이 등록되지 않은 경우
                    Toast.makeText(ProfileActivity.this, "이전 사진을 먼저 등록해주세요.", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.Iv_profile4:
                num_profile = 4;
                if(imgArray[2] != null) { //이전 사진이 등록되어 있는 경우
                    if(imgArray[3] != null){ //현재 사진이 등록되어 있는 경우
                        imgEditDialog();
                    } else { //현재 사진이 없는 경우
                        CropImage.activity().start(ProfileActivity.this);
                    }
                } else { //이전 사진이 등록되지 않은 경우
                    Toast.makeText(ProfileActivity.this, "이전 사진을 먼저 등록해주세요.", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.Iv_profile5:
                num_profile = 5;
                if(imgArray[3] != null) { //이전 사진이 등록되어 있는 경우
                    if(imgArray[4] != null){ //현재 사진이 등록되어 있는 경우
                        imgEditDialog();
                    } else { //현재 사진이 없는 경우
                        CropImage.activity().start(ProfileActivity.this);
                    }
                } else { //이전 사진이 등록되지 않은 경우
                    Toast.makeText(ProfileActivity.this, "이전 사진을 먼저 등록해주세요.", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.Iv_profile6:
                num_profile = 6;
                if(imgArray[4] != null) { //이전 사진이 등록되어 있는 경우
                    if(imgArray[5] != null){ //현재 사진이 등록되어 있는 경우
                        imgEditDialog();
                    } else { //현재 사진이 없는 경우
                        CropImage.activity().start(ProfileActivity.this);
                    }
                } else { //이전 사진이 등록되지 않은 경우
                    Toast.makeText(ProfileActivity.this, "이전 사진을 먼저 등록해주세요.", Toast.LENGTH_SHORT).show();
                }
                break;

//            case R.id.Tv_live_check:
//                ArrayList<String> mLive =  new ArrayList<>();
//                mLive.add("서울");
//                mLive.add("수도권");
//                mLive.add("충남");
//                mLive.add("충북");
//                mLive.add("강원");
//                mLive.add("경북");
//                mLive.add("경남");
//                mLive.add("전북");
//                mLive.add("전남");
//                mLive.add("제주");
//                mLive.add("해외");
//
//                final CharSequence[] Lives = mLive.toArray(new String[mLive.size()]);
//                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ProfileActivity.this);
//                dialogBuilder.setTitle("지역");
//                dialogBuilder.setItems(Lives, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int item) {
//                        String selectedItem = Lives[item].toString();  //Selected item in listview
//                        tv_live.setText(selectedItem);
//                        userLive = selectedItem;
//                        liveCheck = true;
//                    }
//                });
//                AlertDialog alertDialogObject = dialogBuilder.create();
//                alertDialogObject.show();
//                break;

            case R.id.Btn_commit:
//                if(et_nickname.getText().toString() != null && et_nickname.getText().length()>2) {
//                    userNickname = et_nickname.getText().toString();
//                    nicknameCheck = true;
//                }
//                if(et_job.getText().toString() != null && et_job.getText().length()>0) {
//                    userJob = et_job.getText().toString();
//                    jobCheck = true;
//                }
//                if(et_introduce.getText().toString() != null) {
//                    userIntroduce = et_introduce.getText().toString();
//                    introduceCheck = true;
//                } else {
//                    userIntroduce = "";
//                    introduceCheck = true;
//                }
//                if(tv_live.getText().toString() != null) {
//                    userLive = tv_live.getText().toString();
//                    liveCheck = true;
//                }

//                if(nicknameCheck==true && jobCheck==true && introduceCheck==true && liveCheck==true) {
//                    new UpdateUserInfoTask().execute();
//                } else {
//                    Toast.makeText(ProfileActivity.this, "프로필 정보를 확인해주세요.", Toast.LENGTH_SHORT).show();
//                }
                Intent intent = new Intent(ProfileActivity.this, Profile2Activity.class);
                startActivity(intent);




                break;


            default:
                break;
        }
    }

    public void imgEditDialog() {
        final String[] menu = new String[]{
                "이미지 변경", "이미지 삭제",
        };
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProfileActivity.this);
        alertDialogBuilder.setItems(menu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                switch (i) {
                    case 0:
                        CropImage.activity().start(ProfileActivity.this);
                        break;
                    case 1:
                        String delImg = imgArray[num_profile-1];
                        imgArray[num_profile-1] = null;
//                        0 : 'http://115.71.238.160/novaproject1/img/profile/2165853950092488_18121320010018139058.jpg'
//                        1 : 'http://115.71.238.160/novaproject1/img/profile/2165853950092488_18121320000022243982.jpg'
//                        2 : 'null'
//                        3 : 'null'
//                        4 : 'null'
//                        5 : 'null'


                        for(int j=0; j<6; j++) {
                            if(imgArray[j]==null) {
                                imgArray[j]="";
                            }
                        }
                        // url이 들어있는 profile 필드 말고는 전부 ""값을 만들어준 상태
                        // 이걸 다시 떙겨서 값이 있는 필드만 모아주는 작업을 해야함...

//                        0 : 'http://115.71.238.160/novaproject1/img/profile/2165853950092488_18121320010018139058.jpg'
//                        1 : 'http://115.71.238.160/novaproject1/img/profile/2165853950092488_18121320000022243982.jpg'
//                        2 : ''
//                        3 : ''
//                        4 : ''
//                        5 : ''


                        imgList.clear();
                        for(int j=0; j<6; j++) {
                            if(imgArray[j].length()>3) {
                                imgList.add(imgArray[j]);
                            }
                        }
                        // url 이 들어있는(길이가 3보다 큰) 배열값만 모아서 List 에 넣어준다.
                        // 그러면 리스트에 차례대로 정렬됨.
                        for(int j=0; j<6; j++) {
                            if(imgArray[j].length()<3) {
                                imgList.add(imgArray[j]);
                            }
                        }
                        // url 이 없는 나머지 배열값들도 모아서 넣어줌.
//                        0 : 'http://115.71.238.160/novaproject1/img/profile/2165853950092488_18121320010018139058.jpg'
//                        1 : 'http://115.71.238.160/novaproject1/img/profile/2165853950092488_18121320000022243982.jpg'
//                        2 : ''
//                        3 : ''
//                        4 : ''
//                        5 : ''


//                        Log.d("arrayT", " 0 : '"+imgList.get(0)+"'");
//                        Log.d("arrayT", " 1 : '"+imgList.get(1)+"'");
//                        Log.d("arrayT", " 2 : '"+imgList.get(2)+"'");
//                        Log.d("arrayT", " 3 : '"+imgList.get(3)+"'");
//                        Log.d("arrayT", " 4 : '"+imgList.get(4)+"'");
//                        Log.d("arrayT", " 5 : '"+imgList.get(5)+"'");



                        imgArray = imgList.toArray(imgArray);
//                        0 : 'http://115.71.238.160/novaproject1/img/profile/2165853950092488_18121320010018139058.jpg'
//                        1 : 'http://115.71.238.160/novaproject1/img/profile/2165853950092488_18121320000022243982.jpg'
//                        2 : 'null'
//                        3 : ''
//                        4 : ''
//                        5 : ''

//                        Log.d("arrayT", " 0 : '"+imgArray[0]+"'");
//                        Log.d("arrayT", " 1 : '"+imgArray[1]+"'");
//                        Log.d("arrayT", " 2 : '"+imgArray[2]+"'");
//                        Log.d("arrayT", " 3 : '"+imgArray[3]+"'");
//                        Log.d("arrayT", " 4 : '"+imgArray[4]+"'");
//                        Log.d("arrayT", " 5 : '"+imgArray[5]+"'");



                        new DeleteImgTask(imgArray).execute(delImg);
                        break;
                }
            }
        });
        alertDialogBuilder.show();
    }


    /** 이미지 삭제 **/
    class DeleteImgTask extends AsyncTask<String, Void, String> {

        private String[] imgs;
        public DeleteImgTask(String[] mImgs) {
            this.imgs=mImgs;
        }

        ProgressDialog mDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {

            String strurl = "http://115.71.238.160/novaproject1/HomeActivity/ProfileFragment/ProfileActivity/img_profile_delete.php";
            String result = null;

            RequestBody requestBody = new FormBody.Builder()
                    .add("id", CurrentUserManager.getCurrentUserId(ProfileActivity.this))
                    .add("img_profile", imgs[0])
                    .add("img_profile2", imgs[1])
                    .add("img_profile3", imgs[2])
                    .add("img_profile4", imgs[3])
                    .add("img_profile5", imgs[4])
                    .add("img_profile6", imgs[5])
                    .add("img_delete", strings[0])
                    .build();

            Request request = new Request.Builder()
                    .post(requestBody)
                    .url(strurl)
                    .build();

            Call call = new OkHttpClient().newCall(request);

            try {
                Response response = call.execute();
                result = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.equals("1")) {
                Log.d("arrayT", "삭제 후");
                new GetCurrentUserTask(CurrentUserManager.getCurrentUserId(ProfileActivity.this)).execute();
            }

        }
    }





    /** 수정된 이미지를 서버에 업로드 **/
    public class EditImgTask extends AsyncTask<String, Void, String> {

        private String Path;
        public EditImgTask(String path) {
            this.Path = path;
        }



        String key = Util.keyMaker();
        String CurrentId = CurrentUserManager.getCurrentUserId(ProfileActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected String doInBackground(String... strings) {

            File imgFile = new File(Path);
            String result = null;

            if(imgFile.exists()){ // 파일이 존재하는 경우
                if(imgFile.isFile()){ //파일이 맞으면
                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("file", CurrentId+"_"+key, RequestBody.create(MediaType.parse("image/jpg"), imgFile))
                            .addFormDataPart("id", CurrentId)
                            .addFormDataPart("num", Integer.toString(num_profile))
                            .build();



                    Request request = new Request.Builder()
                            .url("http://115.71.238.160/novaproject1/HomeActivity/ProfileFragment/ProfileActivity/img_profile_update.php")
                            .post(requestBody)
                            .build();

                    Call call = new OkHttpClient().newCall(request);

                    try {
                        Response response = call.execute();
                        result = response.body().string();

                        Gson gson = new Gson();
                        resultGson RG = gson.fromJson(result, resultGson.class);
                        Log.d("gtest", RG.getSuccess());
                        Log.d("gtest", RG.getMessage());
                        result = RG.getMessage();



                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d("filetest", "try 실패 : " + e.toString());
                    }
                }
                else { // 이거 파일이 아닌데?
                    Log.d("filetest", "이거 파일 아닌데?");
                }


            }

            else {
                Log.d("filetest", "파일이 없는데?");
            }


            return result;
        }



        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("arrayT", "수정 후");
            new GetCurrentUserTask(CurrentUserManager.getCurrentUserId(ProfileActivity.this)).execute();
        }
    }



    /** imgList & imgArray 초기화
     * SharedPreferences 에 저장된 유저정보를 바탕으로 imgArray 를 만듦
     * 이미지가 존재하는 경우 imgArray[i] 에 경로가, 존재하지 않으면 null 이 들어감.**/
    public void initImgArray() {
        imgList = new ArrayList<>();
        imgArray = new String[6];
        if(CurrentUserManager.getCurrentUser(this).getImg_profile().length()>3) {
            imgArray[0] = (CurrentUserManager.getCurrentUser(this).getImg_profile());
        }
        if(CurrentUserManager.getCurrentUser(this).getImg_profile2().length()>3) {
            imgArray[1] = (CurrentUserManager.getCurrentUser(this).getImg_profile2());
        }
        if(CurrentUserManager.getCurrentUser(this).getImg_profile3().length()>3) {
            imgArray[2] = (CurrentUserManager.getCurrentUser(this).getImg_profile3());
        }
        if(CurrentUserManager.getCurrentUser(this).getImg_profile4().length()>3) {
            imgArray[3] = (CurrentUserManager.getCurrentUser(this).getImg_profile4());
        }
        if(CurrentUserManager.getCurrentUser(this).getImg_profile5().length()>3) {
            imgArray[4] = (CurrentUserManager.getCurrentUser(this).getImg_profile5());
        }
        if(CurrentUserManager.getCurrentUser(this).getImg_profile6().length()>3) {
            imgArray[5] = (CurrentUserManager.getCurrentUser(this).getImg_profile6());
        }

//        Log.d("arrayT", " 0 : '"+imgArray[0]+"'");
//        Log.d("arrayT", " 1 : '"+imgArray[1]+"'");
//        Log.d("arrayT", " 2 : '"+imgArray[2]+"'");
//        Log.d("arrayT", " 3 : '"+imgArray[3]+"'");
//        Log.d("arrayT", " 4 : '"+imgArray[4]+"'");
//        Log.d("arrayT", " 5 : '"+imgArray[5]+"'");

//        0 : 'http://115.71.238.160/novaproject1/img/profile/2165853950092488_18121320010018139058.jpg'
//        1 : 'http://115.71.238.160/novaproject1/img/profile/2165853950092488_18121320000022243982.jpg'
//        2 : 'http://115.71.238.160/novaproject1/img/profile/2165853950092488_1812132018000991988.jpg'
//        3 : 'null'
//        4 : 'null'
//        5 : 'null'


    }



    /** 뷰에 이미지와 텍스트 세팅 **/
    public void setViewFromCurrentUser() {
        User cUser = CurrentUserManager.getCurrentUser(this);
//        Log.d("arrayT", " 0 : '"+ cUser.getImg_profile() +"'");
//        Log.d("arrayT", " 1 : '"+ cUser.getImg_profile2() +"'");
//        Log.d("arrayT", " 2 : '"+ cUser.getImg_profile3() +"'");
//        Log.d("arrayT", " 3 : '"+ cUser.getImg_profile4() +"'");
//        Log.d("arrayT", " 4 : '"+ cUser.getImg_profile5() +"'");
//        Log.d("arrayT", " 5 : '"+ cUser.getImg_profile6() +"'");
        Glide.with(ProfileActivity.this).load(cUser.getImg_profile()).into(iv_profile);
        if(cUser.getImg_profile2() != null) {
            if(cUser.getImg_profile2().length()>3) {
                Glide.with(ProfileActivity.this).load(cUser.getImg_profile2()).into(iv_profile2);
            } else { iv_profile2.setImageResource(R.drawable.icon_plus); }
        }

        if(cUser.getImg_profile3() != null) {
            if(cUser.getImg_profile3().length()>3) {
                Glide.with(ProfileActivity.this).load(cUser.getImg_profile3()).into(iv_profile3);
            } else { iv_profile3.setImageResource(R.drawable.icon_plus); }
        } else {}

        if(cUser.getImg_profile4() != null) {
            if(cUser.getImg_profile4().length()>3) {
                Glide.with(ProfileActivity.this).load(cUser.getImg_profile4()).into(iv_profile4);
            } else { iv_profile4.setImageResource(R.drawable.icon_plus); }
        } else {}

        if(cUser.getImg_profile5() != null) {
            if(cUser.getImg_profile5().length()>3) {
                Glide.with(ProfileActivity.this).load(cUser.getImg_profile5()).into(iv_profile5);
            } else { iv_profile5.setImageResource(R.drawable.icon_plus); }
        } else {}

        if(cUser.getImg_profile6() != null) {
            if(cUser.getImg_profile6().length()>3) {
                Glide.with(ProfileActivity.this).load(cUser.getImg_profile6()).into(iv_profile6);
            } else { iv_profile6.setImageResource(R.drawable.icon_plus); }
        } else {}
//        tv_live.setText(cUser.getLive());
//        et_nickname.setText(cUser.getNickname());
//        et_introduce.setText(cUser.getIntroduce());
//        et_job.setText(cUser.getJob());
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
                    CurrentUserManager.setCurrentUser(ProfileActivity.this, result);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }


        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);
            initImgArray();
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
                    .add("id", CurrentUserManager.getCurrentUserId(ProfileActivity.this))
                    .add("nickname", mNickname)
                    .build();
            Request request = new Request.Builder()
                    .url(strUrl)
                    .post(body)
                    .build();
            Log.d("utest", CurrentUserManager.getCurrentUserId(ProfileActivity.this));
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
            String CurrentId = CurrentUserManager.getCurrentUserId(ProfileActivity.this);
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
            Toast.makeText(ProfileActivity.this, s, Toast.LENGTH_SHORT).show();
            if(s.equals("1")) {
                Toast.makeText(ProfileActivity.this, "프로필 정보가 수정되었습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ProfileActivity.this, "실패", Toast.LENGTH_SHORT).show();
            }
        }

    }




    /** 뒤로가기 버튼 **/
    /*@Override
    public void onBackPressed() {

        AlertDialog.Builder adb = new AlertDialog.Builder(ProfileActivity.this);
        adb.setMessage(" \n종료하시겠습니까?");

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
    }*/








    public class ImgAdapter extends BaseAdapter {

        Context context;
        ArrayList<User> userList;

        public ImgAdapter(Context context, ArrayList<User> userList) {
            this.context = context;
            this.userList = userList;
        }

        @Override
        public int getCount() {
            return userList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View itemView = convertView;






            return null;
        }
    }

}
