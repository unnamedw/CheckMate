package com.example.msg_b.checkmate;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "myInstanceIdServiceTAG";

    @Override
    public void onTokenRefresh() {


        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        String refreshedId = FirebaseInstanceId.getInstance().getId();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        Log.d(TAG, "Refreshed id: " + refreshedId);


        sendRegistrationToServer(refreshedToken, refreshedId);
    }

    public void sendRegistrationToServer(String refreshedToken, String refreshedId) {

        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("token", refreshedToken)
                .add("id", refreshedId)
                .build();

        //request
        Request request = new Request.Builder()
                .url("http://115.71.238.160/novaproject1/fcmtoken.php")
                .post(body)
                .build();

        try {
            client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }






}
