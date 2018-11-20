package com.example.liuyh73.storage2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class CommentDAO extends SQLiteOpenHelper {
    private static final String DB_NAME = "comments.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "comment";
    private static final String SQL_CREATE_TABLE = "create table " + TABLE_NAME
            + " (commentUsername text not null,"
            + " commentTime text not null,"
            + " commentContent text not null,"
            + " thumbsUpCount integer not null,"
            + " primary key(commentUsername, commentTime))";

    public CommentDAO(Context ctx) {
        /**
         * 创建数据库访问对象，实际没有创建数据库，马上返回。只有调用GetWriteableDatabase()或
         * getReadableDatabase() 时才会创建数据库。数据库文件位于/data/data/<package>/databases
         */
        super(ctx, DB_NAME, null, DB_VERSION);
    }

    /**
     * 第一次调用 getWritableDatabase() 或 getReadableDatabase() 时调用
     */
    @Override
    public void onCreate(SQLiteDatabase db){
        /**
         * 创建数据库
         */
        db.execSQL(SQL_CREATE_TABLE);
    }

    /**
     * DB_VERSION变化时调用此函数
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        /**
         * 更新数据库版本（这里不使用）
         * db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
         * onCreate(db);
         */
    }

    /* insert */
    /* insert()函数的返回值是新数据插入的位置，即ID值。ContentValues类是一 个数据承载容器，主要用来向数据库表中添加一条数据 */
    public long insert(Comment comment) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        // key/value
        contentValues.put("commentUsername", comment.getCommentUsername());
        contentValues.put("commentTime", comment.getCommentTime());
        contentValues.put("commentContent", comment.getCommentContent());
        contentValues.put("thumbsUpCount", comment.getThumbsUpCount());
        // 必须保证contentValues至少一个字段不为null，否则报错
        long rid = db.insert(TABLE_NAME, null, contentValues);
        db.close();
        return rid;
    }

    /* delete */
    /**
     * delete()函数第1个参数是数据库表名，后面的参数是删除条件
     * 参数id指明了需要删除数据的id值，因此deleteById()函数仅删除一条数 据，此时delete()函数的返回值表示被删除的数据的数量；
     * 如果后面两个参数均为null，那么表示删除数据库中的全部数据。
     */
    public int delete(Comment comment) {
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = " commentUsername = ? and commentTime = ?";
        String[] whereArgs = { comment.getCommentUsername(), comment.getCommentTime() };
        int row = db.delete(TABLE_NAME, whereClause, whereArgs);
        db.close();
        return row;
    }

    /* update */
    public int update(Comment comment) {
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = "commentUsername = ? and commentTime = ?";
        String[] whereArgs = { comment.getCommentUsername(), comment.getCommentTime() };
        ContentValues values = new ContentValues();
        values.put("commentUsername", comment.getCommentUsername());
        values.put("commentTime", comment.getCommentTime());
        values.put("commentContent", comment.getCommentContent());
        values.put("thumbsUpCount", comment.getThumbsUpCount());
        int rows = db.update(TABLE_NAME, values, whereClause, whereArgs);
        db.close();
        return rows;
    }

    /* query */
    public List<Comment> get(String commentUsername){
        List<Comment>commentList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String selection = "commentUsername = ?";
        String[] selectionArgs = { commentUsername };
        Cursor c = db.query(TABLE_NAME, null, selection, selectionArgs, null,null, null);
        while (c.moveToNext()){
            commentList.add(new Comment(c.getString(0),c.getString(1),c.getString(2),c.getInt(3)));
        }
        c.close();
        db.close();
        return commentList;
    }

    /* query all */
    public List<Comment> getAll(){
        List<Comment>commentList = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_NAME, null, null, null, null,null, null);
        while (c.moveToNext()){
            commentList.add(new Comment(c.getString(0),c.getString(1),c.getString(2),c.getInt(3)));
        }
        c.close();
        db.close();
        return commentList;
    }
}
