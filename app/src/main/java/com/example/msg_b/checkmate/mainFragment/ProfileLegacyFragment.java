package com.example.msg_b.checkmate.mainFragment;


import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.msg_b.checkmate.ExActivity;
import com.example.msg_b.checkmate.LoginActivity;
import com.example.msg_b.checkmate.R;
import com.example.msg_b.checkmate.mainFragment.activityInProfile.AccountActivity;
import com.example.msg_b.checkmate.mainFragment.activityInProfile.ProfileActivity;
import com.example.msg_b.checkmate.util.CurrentUserManager;
import com.example.msg_b.checkmate.util.User;
import com.example.msg_b.checkmate.util.Util;
import com.example.msg_b.checkmate.util.resultGson;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileLegacyFragment extends androidx.fragment.app.Fragment {

    public ProfileLegacyFragment() {
        // Required empty public constructor
    }

    View v;

    CircleImageView iv_profile, iv_edit;
    TextView tv_title, tv_nickname, tv_item1, tv_item2, tv_item3, tv_item4, tv_item5;
    Button btn_edit;
    ImageView iv_item1, iv_item2, iv_item3, iv_item4, iv_item5;
    View v1, v2, v3, v4;

    String imgPath;
    String CurrentId;
    ProgressDialog mDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_profile_legacy, null);


        iv_profile = (CircleImageView) v.findViewById(R.id.Iv_profile);
        iv_edit = (CircleImageView) v.findViewById(R.id.Iv_edit);

        btn_edit = (Button) v.findViewById(R.id.Btn_edit);

        tv_title = (TextView) v.findViewById(R.id.Tv_title);
        tv_nickname = (TextView) v.findViewById(R.id.Tv_nickname);
        tv_item1 = (TextView) v.findViewById(R.id.Tv_item1);
        tv_item2 = (TextView) v.findViewById(R.id.Tv_item2);
        tv_item3 = (TextView) v.findViewById(R.id.Tv_item3);
        tv_item4 = (TextView) v.findViewById(R.id.Tv_item4);
        tv_item5 = (TextView) v.findViewById(R.id.Tv_item5);

        CurrentId = CurrentUserManager.getCurrentUserId(getActivity());
        new GetCurrentUserTask(getActivity(), CurrentUserManager.getCurrentUserId(getActivity())).execute();


        /** 사진수정 **/
        iv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /**
                 * Cropper 라이브러리 삭제로 인한 리팩토링 요망
                 * */
//                CropImage.activity().start(getContext(), ProfileLegacyFragment.this);



                /** 다이얼로그를 이용한 이미지 편집. [보류중] **/
//                final String[] menu = new String[]{
//                        "카메라","갤러리에서 가져오기",
//                };
//                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
//                alertDialogBuilder.setItems(menu, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//
//                        switch (i) {
//                            case 0:
//                                Toast.makeText(getActivity(), "카메라", Toast.LENGTH_SHORT).show();
//
//                                break;
//                            case 1:
//                                Toast.makeText(getActivity(), "갤러리", Toast.LENGTH_SHORT).show();
//                                break;
//                        }
//                    }
//                });
//                alertDialogBuilder.show();


            }
        });
        //프로필 사진 수정


        /** 프로필 보기 **/
        iv_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ExActivity.class);
                startActivity(intent);
            }
        });


        /** 프로필 편집 **/
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                startActivity(intent);
            }
        });




        /** 계정관리 **/
        tv_item2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AccountActivity.class);
                startActivity(intent);
            }
        });




        /** 로그아웃 **/
        tv_item5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
                adb.setMessage("로그아웃 하시겠어요?");
                adb.setCancelable(false);

                //Positive[오른쪽]
                adb.setPositiveButton("로그아웃", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        CurrentUserManager.LogoutAll();
                        CurrentUserManager.setCurrentUserId(getActivity(), null);
                        goToLoginActivityAndFinish();
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
        });

        return v;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("PF", "onCreate");
    }

    @Override
    public void onStart() {
        super.onStart();
        new GetCurrentUserTask(getActivity(), CurrentUserManager.getCurrentUserId(getActivity())).execute();
        Log.d("PF", "onStart");

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("PF", "onResume");

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("PF", "onStop");
        if(mDialog != null) {
            mDialog.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("text5", "FragDestroy");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /**
         * Cropper 라이브러리 삭제로 인한 리팩토링 요망
         * */
//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//            if (resultCode == RESULT_OK) {
//                //찍은 이미지의 경로
//                String croppedPath = result.getUri().getPath();
//
//                //해당 uri 를 파라미터로 하여 서버에 업로드
//                new MyImgTask(croppedPath).execute();
//
//            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                Exception error = result.getError();
//            }
//        }

    }




    public void goToLoginActivityAndFinish() {
        Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
        startActivity(loginIntent);
        getActivity().finish();
    }





    /** 프로필 이미지를 서버에 업로드 **/
    class MyImgTask extends AsyncTask<String, Void, String> {

        private String Path;

        public MyImgTask(String path) {
            this.Path = path;
        }



        String key = Util.keyMaker();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = new ProgressDialog(getActivity());
            mDialog.show();
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
                            .build();

                    Request request = new Request.Builder()
                            .url("http://115.71.238.160/novaproject1/HomeActivity/ProfileLegacyFragment/img_profile_update.php")
                            .post(requestBody)
                            .build();

                    Call call = new OkHttpClient().newCall(request);

                    try {
                        Response response = call.execute();
                        result = response.body().string();

                        Gson gson = new Gson();
                        resultGson RG = gson.fromJson(result, resultGson.class);
                        Log.d("gsontest", RG.getMessage());
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
            Log.d("filetest", "response : "+s);
            CurrentUserManager.setCurrentUserImg_profile(getActivity(), s);
            Glide.with(getActivity()).load(s).into(iv_profile);
            mDialog.dismiss();
        }
    }



    /** 현재 유저의 정보를 받아오는 Task **/
    class GetCurrentUserTask extends AsyncTask<String, Void, User> {

        String mId;
        private Context mContext;
        public GetCurrentUserTask(Context context, String id) {
            this.mId = id;
            this.mContext = context;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected User doInBackground(String... strings) {

            String strUrl = "http://115.71.238.160/novaproject1/HomeActivity/ProfileLegacyFragment/ProfileActivity/getuser.php";
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
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }


        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);
            CurrentUserManager.setCurrentUser(mContext, user);
            Log.d("PF", user.getImg_profile());
            Glide.with(mContext).load(user.getImg_profile()).into(iv_profile);
            tv_nickname.setText(user.getNickname());
        }
    }








//    public String GetPath(Uri uri) {
//
//        String[] projection = { MediaStore.Images.Media.DATA };
//        Cursor cursor = managedQuery(uri, projection, null, null, null);
//        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//        cursor.moveToFirst();
//
//        return cursor.getString(column_index);
//    }

}
