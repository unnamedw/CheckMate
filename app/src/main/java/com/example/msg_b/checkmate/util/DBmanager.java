package com.example.msg_b.checkmate.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBmanager {


    // DB 관련 상수 선언
    private static final String DB_NAME = "test.db";
    private static final String CHATROOM_TABLE_NAME = "chatroom";
    public static final int DB_VERSION = 1;

    // DB 관련 객체 선언
    private OpenHelper mOpener;
    private SQLiteDatabase mDb;

    // 기타 객체
    private Context mContext;

    // 생성자
    public DBmanager(Context context) {
        this.mContext = context;
        this.mOpener = new OpenHelper(mContext, DB_NAME, null, DB_VERSION);
        this.mDb = this.mOpener.getWritableDatabase();
    }


    // Opener of DB and Table
    private class OpenHelper extends SQLiteOpenHelper {

        public OpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        // 생성된 DB가 없을 경우에 한 번만 호출됨
        @Override
        public void onCreate(SQLiteDatabase db) {
            String createQuery = "CREATE TABLE " + CHATROOM_TABLE_NAME + " ("
                    + "idx INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "roomid TEXT, "
                    + "lastmsg TEXT)";

            db.execSQL(createQuery);
        }

        @Override
        public void onOpen(SQLiteDatabase db) {
            super.onOpen(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }




}
