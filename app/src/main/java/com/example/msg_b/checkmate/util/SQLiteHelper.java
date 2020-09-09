package com.example.msg_b.checkmate.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class SQLiteHelper extends SQLiteOpenHelper {

    public static final int DB_VERSION = 1;
    public static final String DBFILE_CONTACT = "test.db";

    public static final String TBL_CONTACT = "chat";
    public static final String COL_IDX= "idx" ;
    public static final String COL_ID = "id" ;
    public static final String COL_ROOM = "room";
    public static final String COL_TYPE = "type" ;
    public static final String COL_TIME_SENT = "time_sent" ;
    public static final String COL_TIME_SERVER = "time_server" ;
    public static final String COL_TIME_RECEIVED = "time_received" ;
    public static final String COL_STATUS = "status" ;
    public static final String COL_MSG = "msg" ;


    SQLiteDatabase wdb;
    SQLiteDatabase rdb;

    // 생성자
    public SQLiteHelper(Context context) {
        super(context, DBFILE_CONTACT, null, DB_VERSION);
        this.wdb = this.getWritableDatabase();
        this.rdb = this.getReadableDatabase();
    }

    // DB를 새로이 생성할 때 호출
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("dbTest", "SQLiteHelper on Create");
        db.execSQL(SQL_CREATE_TBL);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        Log.d("dbTest", "SQLiteHelper on Open");
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        Log.d("dbTest", "SQLiteHelper on Configure");
    }

    // DB 버전이 변경될 시 업그레이드를 위해 호출
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }

    // CREATE TABLE IF NOT EXISTS CONTACT_T (NO INTEGER NOT NULL, NAME TEXT, PHONE TEXT, OVER20 INTEGER)
    public static final String SQL_CREATE_TBL = "CREATE TABLE IF NOT EXISTS " + TBL_CONTACT +
            " (" +
            COL_IDX + " INTEGER PRIMARY KEY" + ", " +
            COL_ID + " TEXT" + ", " +
            COL_ROOM + " TEXT" + ", " +
            COL_TYPE + " TEXT" + ", " +
            COL_TIME_SENT + " TEXT" + ", " +
            COL_TIME_SERVER + " TEXT" + ", " +
            COL_TIME_RECEIVED + " TEXT" + ", " +
            COL_STATUS + " TEXT" + ", " +
            COL_MSG + " TEXT" +
            ")" ;







    // DROP TABLE IF EXISTS CONTACT_T
    public static final String SQL_DROP_TBL = "DROP TABLE IF EXISTS " + TBL_CONTACT ;

    // SELECT * FROM CONTACT_T
    public static final String SQL_SELECT = "SELECT * FROM " + TBL_CONTACT ;

    // INSERT OR REPLACE INTO CONTACT_T (NO, NAME, PHONE, OVER20) VALUES (x, x, x, x)
    public static final String SQL_INSERT = "INSERT OR REPLACE INTO " + TBL_CONTACT + " " +
            "(" + COL_ID + ", " + COL_ROOM + ", " + COL_TYPE  + ", " + COL_MSG + ") VALUES " ;

    // DELETE FROM CONTACT_T
    public static final String SQL_DELETE = "DELETE FROM " + TBL_CONTACT ;



    void insertMSG (ChatMessage chatMessage) {

    }

    ArrayList<ChatMessage> selectMSG () {

        return null;
    }








}
