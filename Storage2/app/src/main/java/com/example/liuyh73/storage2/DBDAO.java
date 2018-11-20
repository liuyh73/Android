package com.example.liuyh73.storage2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class DBDAO extends SQLiteOpenHelper {
    private static final String DB_NAME = "users.db";
    private static final int DB_VERSION = 1;
    private static final String USER_TABLE_NAME = "user";
    // private static final String COMMENT_TABLE_NAME = "comment";

    private static final String SQL_CREATE_USER_TABLE = "create table " + USER_TABLE_NAME
            + " (username text primary key not null,"
            + " password text not null,"
            + " portrait blob not null,"
            + " comments blob,"
            + " thumbsUpComments blob)";

//    private static final String SQL_CREATE_COMMENT_TABLE = "create table " + COMMENT_TABLE_NAME
//            + " (commentUsername text not null,"
//            + " commentTime text not null,"
//            + " commentContent text not null,"
//            + " thumbsUpCount integer not null,"
//            + " primary key(commentUsername, commentTime))";

    public DBDAO(Context ctx) {
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
        db.execSQL(SQL_CREATE_USER_TABLE);
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
    /* insert()函数的返回值是新数据插入的位置，即ID值。ContentValues类是一个数据承载容器，主要用来向数据库表中添加一条数据 */
    public long insert(User user) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        // key/value
        contentValues.put("username", user.getUsername());
        contentValues.put("password", user.getPassword());
        contentValues.put("portrait", bitmapToBytes(user.getPortrait()));
        contentValues.put("comments", "");
        contentValues.put("thumbsUpComments", "");
        // 必须保证contentValues至少一个字段不为null，否则报错
        long rid = db.insert(USER_TABLE_NAME, null, contentValues);
        db.close();
        return rid;
    }

    /* delete */
    /**
     * delete()函数第1个参数是数据库表名，后面的参数是删除条件
     * 参数id指明了需要删除数据的id值，因此deleteById()函数仅删除一条数 据，此时delete()函数的返回值表示被删除的数据的数量；
     * 如果后面两个参数均为null，那么表示删除数据库中的全部数据。
     */
    public int delete(User user) {
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = "username = ?";
        String[] whereArgs = { user.getUsername() };
        int row = db.delete(USER_TABLE_NAME, whereClause, whereArgs);
        db.close();
        return row;
    }

    /* updateComments */
    public int updateComments(User user) {
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = "username = ?";
        String[] whereArgs = { user.getUsername() };
        ContentValues contentValues = new ContentValues();
        contentValues.put("comments", serializeComments(user.getComments()));
        int rows = db.update(USER_TABLE_NAME, contentValues, whereClause, whereArgs);
        db.close();
        return rows;
    }

    /* updateThumbsUpComments */
    public int updateThumbsUpComments(User user) {
        Log.i("updatePortrait", new String(bitmapToBytes(user.getPortrait())));
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = "username = ?";
        String[] whereArgs = { user.getUsername() };
        ContentValues contentValues = new ContentValues();
        contentValues.put("thumbsUpComments", serializeComments(user.getThumbsUpComments()));
        int rows = db.update(USER_TABLE_NAME, contentValues, whereClause, whereArgs);
        db.close();
        return rows;
    }

    /* query user */
    public User get(String username){
        User user = null;
        SQLiteDatabase db = getReadableDatabase();
        Log.i("username", username);
        String selection = "username = ?";
        String[] selectionArgs = { username };
        Cursor c = db.query(USER_TABLE_NAME, null, selection, selectionArgs, null,null, null);
        if (c.moveToNext()){
            user = new User(c.getString(0),c.getString(1), bytesToBitmap(c.getBlob(2)), deserializeComments(c.getBlob(3)), deserializeComments(c.getBlob(4)));
        }
        c.close();
        db.close();
        return user;
    }

    /* query all comments */
    public List<Comment> getAllComments(){
        List<Comment>comments = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(USER_TABLE_NAME, null, null, null, null,null, null);
        while (c.moveToNext()){
            comments.addAll(deserializeComments(c.getBlob(3)));
        }
        c.close();
        db.close();
        return comments;
    }

    //图片转为二进制数据
    public byte[] bitmapToBytes(Bitmap bitmap){
        int size = bitmap.getWidth() * bitmap.getHeight() * 4;
        //创建一个字节数组输出流,流的大小为size
        ByteArrayOutputStream baos= new ByteArrayOutputStream(size);
        try {
            //设置位图的压缩格式，质量为100%，并放入字节数组输出流中
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            //将字节数组输出流转化为字节数组byte[]
            byte[] imagedata = baos.toByteArray();
            return imagedata;
        }catch (Exception e){
        }finally {
            try {
                bitmap.recycle();
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new byte[0];
    }

    public Bitmap bytesToBitmap(byte[] bytes) {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    // 序列化comments
    public byte[] serializeComments(List<Comment> comments){
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(arrayOutputStream);
            objectOutputStream.writeObject(comments);
            objectOutputStream.writeObject(null);
            objectOutputStream.flush();
            byte[] data = arrayOutputStream.toByteArray();
            objectOutputStream.close();
            arrayOutputStream.close();
            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[]{};
    }

    // 反序列化comments
    public List<Comment> deserializeComments(byte[] commentsBytes) {
        try {
            ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(commentsBytes);
            ObjectInputStream inputStream = new ObjectInputStream(arrayInputStream);
            List<Comment>comments = (ArrayList<Comment>) inputStream.readObject();
            inputStream.close();
            arrayInputStream.close();
            return comments;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
