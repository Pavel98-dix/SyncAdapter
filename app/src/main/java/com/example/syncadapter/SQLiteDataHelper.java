package com.example.syncadapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SQLiteDataHelper extends SQLiteOpenHelper {
    public static final String db_name = "contactosDB";
    public static final String table_name = "usuario";
    public static final String column_id = "id_usuario";
    public static final String column_name = "nombre";
    public static final String column_phone ="telefono";
    public static final String column_status = "status";
    String sql =" CREATE TABLE "+ table_name
            +" ("+column_id+
            " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + column_name
            + " VARCHAR, "
            +column_phone + " VARCHAR ,"
            +column_status+ " TINYINT);";
    private static final int db_version=1;
    String dropSql="DROP TABLE IF EXISTS "+table_name;

    public SQLiteDataHelper(@Nullable Context context ) {
        super(context, db_name, null, db_version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(dropSql);
    }

    public boolean addName( String name,String telefono, int status){
        SQLiteDatabase db= this.getWritableDatabase();
        ContentValues contentValues= new ContentValues();

        contentValues.put(column_name,name);
        contentValues.put(column_phone,telefono);
        contentValues.put(column_status,status);

        db.insert(table_name,null,contentValues);
        db.close();
        return true;
    }
    public boolean  updateNameStatus(int id, int status){
        SQLiteDatabase db= this.getWritableDatabase();
        ContentValues contentValues= new ContentValues();
        contentValues.put(column_status,status);
        db.update(table_name,contentValues,column_id+"="+id,null);
        db.close();
        return true;
    }
    public Cursor getNames(){
        SQLiteDatabase db= this.getReadableDatabase();
        String sql= "SELECT * FROM "+ table_name+ " ORDER BY " + column_id+ " ASC;";
        Cursor  c= db.rawQuery(sql,null);
        return c;
    }
    public Cursor getUnsyncedNames(){
        SQLiteDatabase db= this.getReadableDatabase();
        String sql= "SELECT * FROM "+ table_name+ " WHERE " + column_status+ " = 0;";
        Cursor c= db.rawQuery(sql,null);
        return c;
    }
}
