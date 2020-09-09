package com.example.msg_b.checkmate.server;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;



/** 유저상태를 온라인, 오프라인으로 바꾸는 Task **/
public class SetStatusTask extends AsyncTask<String, Void, String> {


    public SetStatusTask() {
        super();
    }

    @Override
    protected String doInBackground(String... strings) {

        //첫 번째는 id, 두 번째는 상태를 파라미터로 받는다.
        String id = strings[0];
        String status = strings[1];

        HttpURLConnection conn = null;
        try {
            URL url = new URL("http://115.71.238.160/novaproject1/setstatus.php");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept-Charset", "UTF-8");
            conn.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;cahrset=UTF-8");

            String data = "id="+id+"&status="+status;
            OutputStream os = conn.getOutputStream();
            os.write(data.getBytes("UTF-8"));
            os.flush();
            os.close();


            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

            String line;
            StringBuilder result = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null){
                result.append(line);
            }

            return result.toString();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
