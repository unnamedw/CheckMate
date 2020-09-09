package com.example.msg_b.checkmate.util;

public class SQLTest {

    public SQLTest() {
    }

    public static final String TBL_CONTACT = "CHAT";
    public static final String COL_IDX= "IDX" ;
    public static final String COL_ID = "ID" ;
    public static final String COL_TYPE = "TYPE" ;
    public static final String COL_TIME = "TIME" ;
    public static final String COL_MSG = "MSG" ;

    // CREATE TABLE IF NOT EXISTS CONTACT_T (NO INTEGER NOT NULL, NAME TEXT, PHONE TEXT, OVER20 INTEGER)
    public static final String SQL_CREATE_TBL = "CREATE TABLE IF NOT EXISTS " + TBL_CONTACT +
            " (" +
            COL_IDX + " INTEGER PRIMARY KEY" + ", " +
            COL_ID + " TEXT" + ", " +
            COL_TYPE + " TEXT" + ", " +
            COL_TIME + " TEXT" + ", " +
            COL_MSG + " TEXT" +
            ")" ;

    // DROP TABLE IF EXISTS CONTACT_T
    public static final String SQL_DROP_TBL = "DROP TABLE IF EXISTS " + TBL_CONTACT ;

    // SELECT * FROM CONTACT_T
    public static final String SQL_SELECT = "SELECT * FROM " + TBL_CONTACT ;

    // INSERT OR REPLACE INTO CONTACT_T (NO, NAME, PHONE, OVER20) VALUES (x, x, x, x)
    public static final String SQL_INSERT = "INSERT OR REPLACE INTO " + TBL_CONTACT + " " +
            "(" + COL_ID + ", " + COL_TYPE + ", " + COL_TIME + ", " + COL_MSG + ") VALUES " ;

    // DELETE FROM CONTACT_T
    public static final String SQL_DELETE = "DELETE FROM " + TBL_CONTACT ;

}
