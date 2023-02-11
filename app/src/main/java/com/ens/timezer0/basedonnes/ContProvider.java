package com.ens.timezer0.basedonnes;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ContProvider extends ContentProvider {
    private BaseHelper dbHelper;

    private static final int TACHE = 100;
    private static final int TACHE_ID = 101;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        uriMatcher.addURI(BaseContract.CONTENT_AUTHORITY, BaseContract.PATH,TACHE);
        uriMatcher.addURI(BaseContract.CONTENT_AUTHORITY, BaseContract.PATH + "/#",TACHE_ID);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new BaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor;

        int match = uriMatcher.match(uri);

        switch(match){
            case TACHE:
                cursor = db.query(BaseContract.InfoBase.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;

            case TACHE_ID:
                selection = BaseContract.InfoBase._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(BaseContract.InfoBase.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;

            default:
                throw new IllegalArgumentException(uri + "INVALID");
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        final int match = uriMatcher.match(uri);
        switch (match){
            case TACHE:
                return BaseContract.InfoBase.CONTENT_LIST_TYPE;

            case TACHE_ID:
                return BaseContract.InfoBase.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalStateException("Unknown URI" + uri + " with match" + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = uriMatcher.match(uri);

        switch (match){
            case TACHE:

                SQLiteDatabase db = dbHelper.getWritableDatabase();
                long id = db.insert(BaseContract.InfoBase.TABLE_NAME,null,values);

                if(id == -1){
                   Log.i("Error: "," Insertion failed");
                    return null;
                }else{
                    getContext().getContentResolver().notifyChange(uri,null);
                    return ContentUris.withAppendedId(uri,id);
                }
            default:
                throw new IllegalArgumentException("Insertion Failed!");
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        int rowsDeleted;

        final int match = uriMatcher.match(uri);
        switch (match) {
            case TACHE:
                rowsDeleted = database.delete(BaseContract.InfoBase.TABLE_NAME, selection, selectionArgs);
                break;
            case TACHE_ID:
                selection = BaseContract.InfoBase._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(BaseContract.InfoBase.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case TACHE:
                return updateTache(uri, values, selection, selectionArgs);
            case TACHE_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = BaseContract.InfoBase._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateTache(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }
    private int updateTache(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.size() == 0) {
            return 0;
        }
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        int rowsUpdated = database.update(BaseContract.InfoBase.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
