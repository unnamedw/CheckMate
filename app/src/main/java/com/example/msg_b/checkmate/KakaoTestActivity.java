package com.example.msg_b.checkmate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.msg_b.checkmate.R;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;

import androidx.appcompat.app.AppCompatActivity;

public class KakaoTestActivity extends AppCompatActivity {

    TextView tv;
    Button btn_kakao_logout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kakao_test);

        tv = (TextView) findViewById(R.id.tv);
        btn_kakao_logout = (Button) findViewById(R.id.Btn_kakaologout);

        Log.d("KaKaoActivity", "KAKAO Create");
        Log.d("KaKaoActivity", "토큰큰 : " + Session.getCurrentSession().getAccessToken());
        Log.d("KaKaoActivity", "토큰큰 리프레쉬토큰 : " + Session.getCurrentSession().getRefreshToken());

        requestMe();

        btn_kakao_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickLogout();
            }
        });
    }





    @Override
    protected void onResume() {
        super.onResume();
        if(Session.getCurrentSession().isOpened()){
            Log.d("sessioncheck", String.valueOf(Session.getCurrentSession().checkState()));
        }
    }







    private void onClickLogout() {
        UserManagement.requestLogout(new LogoutResponseCallback() {

            @Override
            public void onSuccess(Long result) {
                Log.d("CB", "onClickLogout.onSuccess = "+result.toString());
            }

            @Override
            public void onCompleteLogout() {
                Log.d("CB", "onClickLogout.onCompleteLogout");
                goToLoginActivityAndFinish();
            }

            @Override
            public void onFailure(ErrorResult errorResult) {
                Log.d("CB", "onClickLogout.onFailure = "+ errorResult.getErrorMessage());
            }
        });
    }






    public void goToLoginActivityAndFinish() {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }







    private void requestMe() {
        /*List<String> keys = new ArrayList<>();
        keys.add("properties.nickname");*/

        MeResponseCallback mCallback = new MeResponseCallback() {
            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                goToLoginActivityAndFinish();
                Log.d("CB", "requestMe.onSessionClosed");
            }

            @Override
            public void onNotSignedUp() {
                goToLoginActivityAndFinish();
                Log.d("CB", "requestMe.onNotSignedUp");
            }

            @Override
            public void onSuccess(UserProfile result) {
                if(Session.getCurrentSession().isOpened()) {
                    tv.setText("UserProfile.getNickname : \n" + result.getId() +"\n");
                    Log.d("CB","requestMe.onSuccess");
//                    Log.d("kakaoprofile",result.toString());
//                    //Log.d("kakaoprofile",result.getUUID()); 에러
//                    Log.d("kakaoprofile",Long.toString(result.getId()));

                    Long userId = result.getId();

                } else {
                    goToLoginActivityAndFinish();
                    Log.d("CB","현재세션 없음");
                }
            }

            @Override
            public void onFailure(ErrorResult errorResult) {
                String message = "failed to get user info. msg=" + errorResult;
                Log.d("CB", "requestMe.onFailure = "+message);
            }
        };

        UserManagement.requestMe(mCallback);
    }










}
