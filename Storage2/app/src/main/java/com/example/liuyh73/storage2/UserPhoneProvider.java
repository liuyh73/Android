package com.example.liuyh73.storage2;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

public class UserPhoneProvider extends ContentProvider {
    // 删除数据集
    @Override
    public int delete(Uri uri, String selection, String[]selectionArgs) {
        return 0;
    }

    // 返回指定URI的MIME数据类型
    // 如果URI是单条数据，则返回的MIME数据类型应以 vnd.android.cursor.item开头
    // 如果URI是多条数据，则返回的MIME数据类型应以 vnd.android.cursor.dir/开头
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public boolean onCreate(){
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
