package com.example.msg_b.checkmate.server;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.msg_b.checkmate.util.CurrentUserManager;
import com.example.msg_b.checkmate.util.User;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UpdateCurrentUserTask extends AsyncTask<String, Void, User> {

    Context context;
    String mId;
    public UpdateCurrentUserTask(Context context, String id) {
        this.context = context;
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
        if(user != null) {
            CurrentUserManager.setCurrentUser(context, user);
            String paramsId = CurrentUserManager.getCurrentUserId(context);
        }

    }
}
