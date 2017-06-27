package com.vfinworks.vfsdk.db;

import android.content.ContentValues;
import android.content.Context;

import com.vfinworks.vfsdk.model.KeyModel;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

/**
 * 存储密钥，用于加解密密码
 * Created by xiaoshengke on 2016/9/23.
 */
public class KeyDatabaseHelper extends SQLiteOpenHelper {
    private static KeyDatabaseHelper keyDatabaseHelper;
    public static final String CREATE_TABLE = "create table BaseLineKey(type integer,privateKey text, publicKey text,createTime integer)";
    private Context context;

    public KeyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context.getApplicationContext();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public synchronized static KeyDatabaseHelper getInstance(Context context){
        if(keyDatabaseHelper == null){
            keyDatabaseHelper = new KeyDatabaseHelper(context.getApplicationContext(), "key.com.vfinworks.vfsdk.db", null, 1);
        }
        return keyDatabaseHelper;
    }

    public void init(){
        getInstance(context).getWritableDatabase("secret_key").close();
    }

    public SQLiteDatabase getDatabase(Context context){
        return getInstance(context).getWritableDatabase("secret_key");
    }

    public KeyModel queryKey(){
        SQLiteDatabase db = getDatabase(context);
        KeyModel keyModel = null;
        Cursor cursor = db.query("BaseLineKey", null, null, null, null, null, null);
        if(cursor.getCount() > 1){
            throw new RuntimeException("保存的加密密钥不唯一");
        }
        if(cursor!=null&&cursor.moveToFirst()){
            do{
                keyModel = new KeyModel();
                keyModel.privateKey = cursor.getString(cursor.getColumnIndex("privateKey"));
                keyModel.publicKey = cursor.getString(cursor.getColumnIndex("publicKey"));
                keyModel.createTime = cursor.getLong(cursor.getColumnIndex("createTime"));
            }while(cursor.moveToNext());

        }
        cursor.close();
        db.close();
        return keyModel;
    }

    public void insertKey(KeyModel keyModel){
        SQLiteDatabase db = getDatabase(context);
        ContentValues values = new ContentValues();
        values.put("type", 1);
        values.put("privateKey", keyModel.privateKey);
        values.put("publicKey", keyModel.publicKey);
        values.put("createTime", keyModel.createTime);
        db.insert("BaseLineKey", null, values);
        db.close();
    }

    public void updateKey(KeyModel keyModel){
        SQLiteDatabase db = getDatabase(context);
        ContentValues values = new ContentValues();
        values.put("createTime", System.currentTimeMillis());
        values.put("privateKey", keyModel.privateKey);
        values.put("publicKey", keyModel.publicKey);
        db.update("BaseLineKey", values,"type = ?", new String[]{"1"});
        db.close();
    }
}
