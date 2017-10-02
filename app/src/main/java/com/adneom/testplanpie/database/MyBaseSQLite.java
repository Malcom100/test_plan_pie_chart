package com.adneom.testplanpie.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by gtshilombowanticale on 04-08-16.
 */
public class MyBaseSQLite extends SQLiteOpenHelper {


    private static final String TABLE_USER = "table_users";
    private static final String COL_ID = "ID";
    private static final String COL_EMAIL= "EMAIL";
    private static final String COL_DATE = "DATE";

    private static final String REQUEST_CREATE_USERS_TANBLE = "CREATE TABLE "+TABLE_USER +"("
                                                              +COL_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "
                                                              +COL_EMAIL +" TEXT NOT NULL, " +COL_DATE
                                                              +" TEXT NOT NULL);";

    public MyBaseSQLite(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context,name,factory,version);
        Log.i("Adneom", " *** constructor *** ");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(REQUEST_CREATE_USERS_TANBLE);
        Log.i("Adneom", " *** onCreate *** ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " +TABLE_USER +";");
        onCreate(db);
        Log.i("Adneom"," *** onUpdate *** ");
    }
}
