package com.example.msg_b.checkmate.server;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FcmLikeRequest extends AsyncTask<String, Void, String> {


    Context mContext;
    String from;
    String to;

    public FcmLikeRequest(Context context, String from, String to) {
        this.mContext = context;
        this.from = from;
        this.to = to;
    }


    @Override
    protected String doInBackground(String... strings) {

        String serverURL = strings[0];
        String tokenid = strings[1];
        String mTitle = "좋아요 알림";
        String mMessage = from+"님이 "+to+"님을 좋아합니다!";

        try {

            // Http 커넥션을 만들기
            URL url = new URL(serverURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(5000);
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept-Charset", "UTF-8");
            conn.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;cahrset=UTF-8");

            String data = "title="+mTitle+"&message="+mMessage+"&tokenid="+tokenid;
            OutputStream os = conn.getOutputStream();
            os.write(data.getBytes("UTF-8"));
            os.flush();
            os.close();
            Log.d("swipeT", tokenid);
            Log.d("swipeT", FirebaseInstanceId.getInstance().getToken());


            // InputStream 받아오기
            int responseStatusCode = conn.getResponseCode();
            InputStream inputStream;

            if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                inputStream = conn.getInputStream();
            }
            else{
                inputStream = conn.getErrorStream();
            }


            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder sb = new StringBuilder();
            String line;


            while((line = bufferedReader.readLine()) != null){
                sb.append(line);
            }

            bufferedReader.close();
            String result = sb.toString().trim();
            return result;

        } catch (Exception e) {
            String errorString = e.toString();
            return null;
        }
    }
}
