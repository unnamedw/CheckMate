package com.example.msg_b.checkmate.server;

import android.os.AsyncTask;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CallerToReceiverTask extends AsyncTask<String, Void, String> {

    String roomid;
    String id_caller;
    String id_receiver;
    String nickname_caller;
    String profile_caller;

    public CallerToReceiverTask(String roomid, String id_caller, String id_receiver, String nickname_caller, String profile_caller) {
        this.roomid = roomid;
        this.id_caller = id_caller;
        this.id_receiver = id_receiver;
        this.nickname_caller = nickname_caller;
        this.profile_caller = profile_caller;
    }

    @Override
    protected String doInBackground(String... strings) {

        String strUrl = "http://115.71.238.160/test/fcmCallerToReceiver.php";
        String result = null;



        RequestBody requestBody = new FormBody.Builder()
                .add("roomid", roomid)
                .add("id_caller", id_caller)
                .add("id_receiver", id_receiver)
                .add("nickname_caller", nickname_caller)
                .add("profile_caller", profile_caller)
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
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }


    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

    }
}
