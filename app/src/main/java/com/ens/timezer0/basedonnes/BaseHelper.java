package com.ens.timezer0.basedonnes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class BaseHelper  extends SQLiteOpenHelper {

    // Database Info
    private static final String DATABASE_NAME = "TacheDooo.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String fTABLE_TACHES = "tache";
    private static final String TABLE_USERS = "users";
    // Post Table Columns
  /*  private static final String KEY_POST_ID = "id";
    private static final String KEY_POST_USER_ID_FK = "userId";
    private static final String KEY_POST_TEXT = "text";

    // User Table Columns
    private static final String KEY_USER_ID = "id";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_PROFILE_PICTURE_URL = "profilePictureUrl";*/
    public BaseHelper(@Nullable Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_TACHE_TABLE =  "CREATE TABLE " + BaseContract.InfoBase.TABLE_NAME + "("
                + BaseContract.InfoBase._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BaseContract.InfoBase.COLUMN_HEADING + " TEXT NOT NULL, "
                + BaseContract.InfoBase.COLUMN_MESSAGE + " TEXT NOT NULL, "
                + BaseContract.InfoBase.COLUMN_DATE + " TEXT NOT NULL, "
                + BaseContract.InfoBase.COLUMN_TIME + " TEXT NOT NULL, "
                + BaseContract.InfoBase.COLUMN_NOTIFICATION + " INTEGER NOT NULL, "
                + BaseContract.InfoBase.COLUMN_IMPO + " TEXT NOT NULL);";
               // + " project_id TEXT NOT NULL, "
                //+ " FOREIGN KEY(project_id) REFERENCES project(_id));";


        String SQL_CREATE_PROJECT_TABLE = "CREATE TABLE project (_id INTEGER PRIMARY KEY AUTOINCREMENT , project_name TEXT NOT NULL );";
   //     String SQL_INSERT = "insert into project values ('project1');";
  //      String SQL_INSERT2 = "insert into project values ('project2');";
        db.execSQL(SQL_CREATE_TACHE_TABLE);
        db.execSQL(SQL_CREATE_PROJECT_TABLE);
  //      db.execSQL(SQL_INSERT);
    //    db.execSQL(SQL_INSERT2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
