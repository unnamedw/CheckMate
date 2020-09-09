package com.example.msg_b.checkmate.server;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SelectUserThread extends Thread {

    String[] params;
    public SelectUserThread(String[] info) {
        this.params = info;
    }

    @Override
    public void run() {
        URL url = null;
        HttpURLConnection conn = null;

        try {
            /** 요청 보내기 **/
            url = new URL("http://115.71.238.160/novaproject1/setlike.php");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST"); // URL 요청에 대한 메소드 설정 : POST.
            conn.setRequestProperty("Accept-Charset", "UTF-8"); // Accept-Charset 설정.
            conn.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;cahrset=UTF-8");

            String outData = "id_from="+params[0]+"&id_to="+params[1]+"&status="+params[2];
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(outData.getBytes("UTF-8"));
            outputStream.flush();
            outputStream.close();


            /** 응답 받기 **/
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.d("thTT", conn.getResponseMessage());
            }
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line;
            StringBuilder page = new StringBuilder();

            // 라인을 받아와 합친다.
            while ((line = bufferedReader.readLine()) != null){
                page.append(line);
            }

            Log.d("thTT", page.toString());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (conn != null)
                conn.disconnect();
        }

    }
}
