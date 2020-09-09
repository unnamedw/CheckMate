package com.example.msg_b.checkmate.mainFragment.profileFragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.msg_b.checkmate.LoginActivity;
import com.example.msg_b.checkmate.R;
import com.example.msg_b.checkmate.util.CurrentUserManager;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/** 앱설정 **/
public class Sub3ProfileFragment extends androidx.fragment.app.Fragment {
    View v;
    public Sub3ProfileFragment() {
        // Required empty public constructor
    }

    TextView tv_logout, tv_unregister, tv_type;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_sub3_profile, container, false);

        tv_logout = v.findViewById(R.id.Tv_logout);
        tv_unregister = v.findViewById(R.id.Tv_unregister);
        tv_type = v.findViewById(R.id.Tv_type);

        tv_logout. setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        tv_unregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /** 회원탈퇴 버튼 **/
                AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
                adb.setMessage("삭제된 정보는 복구되지 않습니다. \n탈퇴하시겠습니까?");
                adb.setCancelable(false);


                //Positive[오른쪽]
                adb.setPositiveButton("회원탈퇴", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        WithdrawalTask mTask = new WithdrawalTask();
                        mTask.execute();
                        /** 회원탈퇴를 할 경우 회원탈퇴 Task 가 호출된다. **/
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

        String mType = CurrentUserManager.getCurrentUser(getContext()).getType();
        tv_type.setText("[ " + mType + " ]");


        return v;
    }


    /** 회원탈퇴 Task **/
    public class WithdrawalTask extends AsyncTask<String, Void, String> {


        ProgressDialog mDialog = new ProgressDialog(getActivity());
        String nowUserId = CurrentUserManager.getCurrentUserId(getActivity());
        // 전역변수로 로딩화면을 위한 다이얼로그 선언
        // nowUserId 는 현재 로그인에 성공한 유저의 id를 말함.


        @Override
        protected void onPreExecute() {
            mDialog.show();
            // 로딩화면 띄우기
        }


        @Override
        protected String doInBackground(String... strings) {
            OkHttpClient client = new OkHttpClient();

            String strUrl = "http://115.71.238.160/novaproject1/users/user_delete.php";
            String result = null;

            RequestBody body = new FormBody.Builder()
                    .add("id", nowUserId)
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

            Log.d("mTask", s);

            mDialog.dismiss();
            //로딩화면 종료

            CurrentUserManager.LogoutAll();
            CurrentUserManager.setCurrentUserId(getActivity(), null);
            goToLoginActivityAndFinish();
            //메인 화면으로 이동

        }

    }
    /** 회원탈퇴 Task **/







    /** Activity 전환 **/
    public void goToLoginActivityAndFinish() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
