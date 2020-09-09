package com.example.msg_b.checkmate.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Base64;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

public class Util {


    /**
     *
     * yyyyMMddHHmmssS 형식으로 만들어진 timeSet 을 반환.
     *
     * **/
    public static synchronized String getCurrentTime() {

        long now = System.currentTimeMillis();
        Date date = new Date(now);

        SimpleDateFormat y_form = new SimpleDateFormat("yyyy");
        SimpleDateFormat M_form = new SimpleDateFormat("MM");
        SimpleDateFormat d_form = new SimpleDateFormat("dd");
        SimpleDateFormat H_form = new SimpleDateFormat("HH");
        SimpleDateFormat m_form = new SimpleDateFormat("mm");
        SimpleDateFormat s_form = new SimpleDateFormat("ss");
        SimpleDateFormat S_form = new SimpleDateFormat("S"); // 밀리초
        String y_now = y_form.format(date);
        String M_now = M_form.format(date);
        String d_now = d_form.format(date);
        String H_now = H_form.format(date);
        String m_now = m_form.format(date);
        String s_now = s_form.format(date);
        String S_now = S_form.format(date);
//        int H_now_int = Integer.valueOf(H_now);
//        if(12<H_now_int && H_now_int<25) {
//            H_now_int -= 12;
//            H_now = "오후 " + Integer.toString(H_now_int);
//        } else {
//            H_now = "오전 " + Integer.toString(H_now_int);
//        }
//
//        String currentTime = H_now + ":" + m_now;

//        return y_now + M_now + d_now + "@" + currentTime;
        return y_now+M_now+d_now+H_now+m_now+s_now+S_now;
    }

    public static String parseTimeYMD(String timeSet){

        if(timeSet == null || timeSet.isEmpty()) {
            return "";
        }
        String Y = timeSet.substring(0, 4);
        String M = timeSet.substring(4, 6);
        String D = timeSet.substring(6, 8);

        String Time = Y+"년 "+M+"월 "+D+"일";

        return Time;
    }



    public static String getHmsS(String timeSet) {
        String result = null;
        result = timeSet.substring(8, 15);
        return result;

    }




    /**
     * yyyyMMddHHmmssS 형식으로 만들어진 timeSet 을 입력하면 시, 분으로 이루어진 timeSet 으로 바꾸어 준다.
     * ex) 오후 4:13
     *
     * **/
    public static String parseTimeHM(String timeSet) {

        if(timeSet == null || timeSet.isEmpty()) {
            return "";
        }

        String H = timeSet.substring(8, 10);
        String M = timeSet.substring(10, 12);
        Log.d("timeT", H);
        Log.d("timeT", M);
        int H_int = Integer.valueOf(H);
        if(12<H_int && H_int<25) {
            H_int -= 12;
            H = "오후 " + Integer.toString(H_int);
        } else {
            H = "오전 " + Integer.toString(H_int);
        }

        String Time = H + ":" + M;

        return Time;

    }


    /**
     * yyyyMMddHHmmss 형식으로 만들어진 timeSet 과 100000~999999 사이의 랜덤 값을 더해 UniqueKey 를 만듦
     *
     * **/
    public static String keyMaker () {
        long now = System.currentTimeMillis();
        Date date = new Date(now);

        SimpleDateFormat y_form = new SimpleDateFormat("yy");
        SimpleDateFormat M_form = new SimpleDateFormat("MM");
        SimpleDateFormat d_form = new SimpleDateFormat("dd");
        SimpleDateFormat H_form = new SimpleDateFormat("HH");
        SimpleDateFormat m_form = new SimpleDateFormat("mm");
        SimpleDateFormat s_form = new SimpleDateFormat("ssss");
        String y_now = y_form.format(date);
        String M_now = M_form.format(date);
        String d_now = d_form.format(date);
        String H_now = H_form.format(date);
        String m_now = m_form.format(date);
        String s_now = s_form.format(date);

        int fromNum = 100000;
        int toNum = 999999;
        int random = new Random().nextInt(toNum - fromNum + 1) + 1;


        String key = y_now + M_now + d_now + H_now + m_now + s_now + random;

        return key;
    }




    /**
     *
     * 채팅방의 고유한 이름을 만든다.
     * 마지막에는 Base64 인코딩을 통해 깔끔하게 만든다.
     *
     * **/
    public static String getRoomCode(String user1, String user2) {

        @SuppressLint("UseSparseArrays") HashMap<String, String> userMap = new HashMap<>();
        userMap.put(user1, "humanoid");
        userMap.put(user2, "humanoid");
        String strMap = userMap.toString();

        String result = getBase64EncodeString(strMap);
        if(result.contains("=") && !result.contains("==")) {
            result += "1";
        }
        else if(result.contains("==")) {
            result += "2";
        }
        else {
            result += "0";
        }

        result = result.replace("=", "");

        return result;
    }





    public static String getBase64EncodeString(String string) {

        byte[] bytes = string.getBytes();
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }
    public static String getBase64DecodeString(String string) {

        return new String(Base64.decode(string, Base64.NO_WRAP));
    }




    /**
     * RoomCode 를 넣으면 해당 방을 유저가 들어있는 String[] 형태로 반환함.
     *
     * ex) parseRoomCode(12zd2h21h8ha9osd) => String[0] = user1, String[1] = user2
     *
     * **/
    public static String[] parseRoomCode(String roomname) {

        String[] result = null;

        System.out.println(roomname.charAt(roomname.length()-1));
        switch(String.valueOf( roomname.charAt(roomname.length()-1) )) {
            // 나머지 0
            case "0":
                roomname = roomname.substring(0, roomname.length()-1);
                break;

            // 나머지 1
            case "1":
                roomname = roomname.substring(0, roomname.length()-1);
                roomname += "=";
                break;

            // 나머지 2
            case "2":
                roomname = roomname.substring(0, roomname.length()-1);
                roomname += "==";
                break;
        }
        System.out.println("현재 방 : "+roomname);
        byte[] bytes = Base64.decode(roomname, Base64.NO_WRAP);
        String str = new String(bytes);
        String tmp = str
                .replaceAll("humanoid", "")
                .replace("{", "")
                .replace("}", "")
                .replace("=", "")
                .replace(" ", "");

        result = tmp.trim().split(",");

        return result;

    }



    /** 만들어 놓은 채팅방이 있는지 확인 **/
    public static boolean isRoomExist(Context context, String id, String id2) {
        SQLiteDatabase DB = new SQLiteHelper2(context).getReadableDatabase();

        String ROOM_CODE = Util.getRoomCode(id, id2);
        boolean result = true;

        String query = "SELECT * FROM " + SQLiteHelper2.TABLENAME +
                " WHERE ROOMID='" + ROOM_CODE + "'";

        Cursor cursor = DB.rawQuery(query, null);
        Log.d("dbT2", "getCount = " + cursor.getCount());

        if(cursor.getCount() == 0)
            result = false;

        cursor.close();
        return result;
    }



    public static int getRandomNum(int fromNum, int toNum) {
        int result = 0;
        result = (int) ((Math.random()*toNum)+fromNum);

        //fromNum ~ toNum

        return result;
    }

}
