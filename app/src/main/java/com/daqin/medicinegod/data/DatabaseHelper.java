package com.daqin.medicinegod.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.daqin.medicinegod.Constant;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static volatile DatabaseHelper instance = null;

    /**
     * 创建实例
     *
     * @param context
     * @return
     */
    public static DatabaseHelper getDBHelper(Context context) {

        if (instance == null) {
            synchronized (DatabaseHelper.class) {
                if (instance == null)
                    instance = new DatabaseHelper(context);
            }
        }
        return instance;
    }

    public DatabaseHelper(Context context) {
        super(context, Constant.DATABASE_NAME, null, Constant.DATABASE_VERSION);
    }

    //第一个参数是上下文，第二个参数是数据库名称，
    //第三个参数是CursorFactory对象，一般设置为null，第四个参数是数据库的版本
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Constant.SQL_CREATE_M_TABLE);

        db.execSQL("create table if not exists " + Constant.TABLE_NAME_HISTORY + "(" +
                Constant.COLUMN_H_ID + " INT(8)  NOT NULL," +
                Constant.COLUMN_H_STR + " INT(8) NOT NULL)");

        db.execSQL(Constant.SQL_CREATE_U_TABLE);

        db.execSQL("create table if not exists " + Constant.TABLE_NAME_HISTORY_EXCEL + "(" +
                Constant.COLUMN_HE_FLAG + " VARCHAR(10)  NOT NULL," +
                Constant.COLUMN_HE_NAME + " VARCHAR(100)  NOT NULL," +
                Constant.COLUMN_HE_FILEPATH + " VARCHAR(255) NOT NULL ," +
                Constant.COLUMN_HE_TIME + " DATETIME NOT NULL )");
        db.execSQL("create table if not exists " + Constant.TABLE_NAME_NOTICE + " (" +
                Constant.COLUMN_N_ID + " int(20)  NOT NULL  PRIMARY KEY ," +
                Constant.COLUMN_N_TITLE + " VARCHAR(100) NOT NULL," +
                Constant.COLUMN_N_CONTEXT + " VARCHAR(255) NOT NULL," +
                Constant.COLUMN_N_FROM + " VARCHAR(100) NOT NULL," +
                Constant.COLUMN_N_TO + " VARCHAR(100) NOT NULL," +
                Constant.COLUMN_N_TIME + " VARCHAR(20) NOT NULL," +
                Constant.COLUMN_N_LNAME + " VARCHAR(20) NOT NULL," +
                Constant.COLUMN_N_CHECKD + " INT(2) NOT NULL " +
                ") ");

    }

    //创建表
    //数据库版本发生变化时调用
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


        for (int i = oldVersion; i <= newVersion; i++) {
            if (i == 2) {
                String sql1 = "ALTER TABLE " + Constant.TABLE_NAME_MEDICINE + " ADD  `" + Constant.COLUMN_M_UID + "` VARCHAR(20);";
                db.execSQL(sql1);
//				String sql2 = "ALTER TABLE " + Constant.TABLE_NAME_MEDICINE + " ADD  " + Constant.COLUMN_M_MD5KEY + " VARCHAR(20);";
//				db.execSQL(sql2);
                String sql3 = "ALTER TABLE `" + Constant.TABLE_NAME_MEDICINE + "` ADD PRIFMARY KEY  " + Constant.TABLE_NAME_MEDICINE + " (`" + Constant.COLUMN_M_KEYID + "`)";
                db.execSQL(sql3);
                db.execSQL(Constant.SQL_CREATE_U_TABLE);
                Log.d("myDeBug", "数据库版本已更新" + i);
            } else if (i == 5) {
//                String sql1 = "ALTER TABLE " + Constant.TABLE_NAME_MEDICINE + " ADD  `" + Constant.COLUMN_M_GROUP + "` VARCHAR(20);";
//                String sql2 = "update " + Constant.TABLE_NAME_MEDICINE + " set `" + Constant.COLUMN_M_GROUP + "` = '默认' ;";
//                db.execSQL(sql1);
//                db.execSQL(sql2);

                Log.d("myDeBug", "数据库版本已更新" + i);
            } else if (i == 6) {
                db.execSQL("create table if not exists " + Constant.TABLE_NAME_HISTORY_EXCEL + "(" +
                        Constant.COLUMN_HE_FLAG + " VARCHAR(10)  NOT NULL," +
                        Constant.COLUMN_HE_NAME + " VARCHAR(100)  NOT NULL," +
                        Constant.COLUMN_HE_FILEPATH + " VARCHAR(255) NOT NULL ," +
                        Constant.COLUMN_HE_TIME + " DATETIME NOT NULL )");
                Log.d("myDeBug", "数据库版本已更新" + i);

            } else if (i == 7) {
                db.execSQL("create table if not exists " + Constant.TABLE_NAME_NOTICE + " (" +
                        Constant.COLUMN_N_ID + " int(20)  NOT NULL  PRIMARY KEY ," +
                        Constant.COLUMN_N_TITLE + " VARCHAR(100) NOT NULL," +
                        Constant.COLUMN_N_CONTEXT + " VARCHAR(255) NOT NULL," +
                        Constant.COLUMN_N_FROM + " VARCHAR(100) NOT NULL," +
                        Constant.COLUMN_N_TO + " VARCHAR(100) NOT NULL," +
                        Constant.COLUMN_N_TIME + " VARCHAR(20) NOT NULL," +
                        Constant.COLUMN_N_LNAME + " VARCHAR(20) NOT NULL," +
                        Constant.COLUMN_N_CHECKD + " INT(2) NOT NULL " +
                        ") ");
                Log.d("myDeBug", "数据库版本已更新" + i);
            }
        }


    }


}
