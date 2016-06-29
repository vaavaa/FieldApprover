package com.asiawaters.fieldapprover.classes;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBController extends SQLiteOpenHelper {

    public static final int STATE_OPENED = 0;
    public static final int STATE_CLOSED = 1;
    public static final int STATE_NOTACTIVATED = -1;

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "FieldApprover";

    private SQLiteDatabase db; //Update access
    private SQLiteDatabase dbr; //ReadA access
    private int state;
    private static final String _Credentials = "Credentials";
    private static final String CREATE_TABLE_Credentials = "CREATE TABLE Credentials (login VARCHAR(150) NOT NULL, password VARCHAR(150) NOT NULL)";

    public DBController(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        state = STATE_NOTACTIVATED;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating required tables
        db.execSQL(CREATE_TABLE_Credentials);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS" + _Credentials);
        // create new tables
        onCreate(db);
    }

    public boolean openDB() {
        boolean bRet;
        try {
            db = this.getWritableDatabase();
            dbr = this.getReadableDatabase();
            state = STATE_OPENED;
            bRet = true;
        } catch (SQLException ex) {
            bRet = false;
        }
        return bRet;
    }

    public void close() {
        db.close();
        state = STATE_CLOSED;
    }

    public int getDBState() {
        return state;
    }

    /* Credentials  GET*/
    public String[] getCredentials() {
        String[] credentials = new String[]{"", ""};
        String selectQuery = "SELECT * FROM " + _Credentials;
        Cursor c = dbr.rawQuery(selectQuery, null);
        if (c != null && c.moveToFirst()) {
            credentials[0] = c.getString(c.getColumnIndex("login"));
            credentials[1] = c.getString(c.getColumnIndex("password"));
            c.close();
        }
        return credentials;
    }

    /* Credentials SET*/
    public boolean setCredentials(String login, String password) {
        boolean bReturn;
        String selectQuery = "SELECT * FROM " + _Credentials;
        String selectQueryAction = "";
        Cursor c = dbr.rawQuery(selectQuery, null);
        if (c != null && c.moveToFirst()) {
            selectQueryAction = "Update " + _Credentials + " SET login='" + login + "', password='" + password + "'";
            c.close();
        } else selectQueryAction = "insert into " + _Credentials + " ( login, password) " +
                "VALUES ('" + login + "','" + password + "')";

        try {
            db.execSQL(selectQueryAction);
            bReturn = true;
        } catch (Exception ex) {
            bReturn = false;
        }
        return bReturn;
    }

}