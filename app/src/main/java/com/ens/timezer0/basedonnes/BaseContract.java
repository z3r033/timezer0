package com.ens.timezer0.basedonnes;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class BaseContract {
    public BaseContract(){}
    public static final String CONTENT_AUTHORITY = "com.ens.timezer0.todo";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH = "Todo";


    public static final class InfoBase implements BaseColumns {


        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH;



        // Uri to access the provider
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,PATH);

        //table name and column names
        public static final String TABLE_NAME = "TachDoo";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_HEADING = "heading";
        public static final String COLUMN_MESSAGE = "message";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_TIME = "time";
        public static final String COLUMN_NOTIFICATION = "notification";
        public static final String COLUMN_IMPO = "impo";



    }

    }

