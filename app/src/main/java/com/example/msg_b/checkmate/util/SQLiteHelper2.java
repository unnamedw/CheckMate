package com.example.msg_b.checkmate.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class SQLiteHelper2 extends SQLiteOpenHelper {

    public static final int DB_VERSION = 1;
    public static final String DBFILE_CONTACT = "test.db";

    public static final String COL_IDX = "idx" ;
    public static final String COL_ROOMID = "roomid" ;
    public static final String COL_USER = "user";
    public static final String COL_USER2 = "user2" ;
    public static final String COL_TO_NICKNAME = "to_nickname" ;
    public static final String COL_TO_PROFILE = "to_profile" ;
    public static final String COL_LASTMSG = "lastmsg";
    public static final String COL_LASTTIME = "lasttime";
    public static final String COL_STATUS = "status" ;

    public static final String TABLENAME = "room";
    public static final String SQL_CREATE_TBL = "CREATE TABLE IF NOT EXISTS " + TABLENAME +
            " (" +
            COL_IDX + " INTEGER PRIMARY KEY AUTOINCREMENT" + ", " +
            COL_ROOMID + " TEXT" + ", " +
            COL_USER + "TEXT" + ", " +
            COL_USER2 + " TEXT" + ", " +
            COL_TO_NICKNAME + " TEXT" + ", " +
            COL_TO_PROFILE + " TEXT" + ", " +
            COL_LASTMSG + " TEXT" + ", " +
            COL_LASTTIME + " TEXT" + ", " +
            COL_STATUS + " TEXT" +
            ")" ;


    SQLiteDatabase writeDB;
    SQLiteDatabase readDB;


    public SQLiteHelper2(Context context) {
        super(context, DBFILE_CONTACT, null, DB_VERSION);
        this.writeDB = this.getWritableDatabase();
        this.readDB = this.getReadableDatabase();
    }


    // 생성자


    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("dbT", "onCreate");
        createTable(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        Log.d("dbT", "onConfigure");
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        Log.d("dbT", "onOpen");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void createTable(SQLiteDatabase db)
    {
        Log.d("dbT", "createTable");
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLENAME +
                " (" +
                COL_IDX + " INTEGER PRIMARY KEY" + ", " +
                COL_ROOMID + " TEXT UNIQUE" + ", " +
                COL_USER + " TEXT" + ", " +
                COL_USER2 + " TEXT" + ", " +
                COL_TO_NICKNAME + " TEXT" + ", " +
                COL_TO_PROFILE + " TEXT" + ", " +
                COL_LASTMSG + " TEXT" + ", " +
                COL_LASTTIME + " TEXT" + ", " +
                COL_STATUS + " TEXT" +
                ")" ;
        db.execSQL(sql);
    }



    public void insertROOM(SQLiteDatabase db, String roomid, String user, String user2,
                           String to_nickname, String to_profile, String lastmsg, String lasttime, String status) {
        String key = Util.keyMaker();

        ContentValues cv = new ContentValues();
                    cv.put("roomid", roomid);
                    cv.put("user", user);
                    cv.put("user2", user2);
                    cv.put("to_nickname", to_nickname);
                    cv.put("to_profile", to_profile);
                    cv.put("lastmsg", lastmsg);
                    cv.put("lasttime", lasttime);
                    cv.put("status", status);

                    db.insert(SQLiteHelper2.TABLENAME, null, cv);
                    db.insertWithOnConflict(SQLiteHelper2.TABLENAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE);

    }

    /** 채팅방 정보를 업데이트 **/
    public void updateRoomMsg(String roomid, String msg, String time, int count) {

        String query = "UPDATE " + SQLiteHelper2.TABLENAME + " SET " +
                "lastmsg='" + msg + "', " +
                "lasttime='" + time + "', " +
                "status=status+" + count +
                " WHERE ROOMID='" + roomid + "'";
        writeDB.execSQL(query);
    }

    /** 채팅방 카운트를 초기화 **/
    public void initRoomCount(String roomid) {
        String query = "UPDATE " + SQLiteHelper2.TABLENAME + " SET " +
                "status='0' " + "WHERE roomid='" + roomid +"'";
        writeDB.execSQL(query);
    }





}
