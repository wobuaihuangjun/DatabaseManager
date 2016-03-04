package com.huangzj.databaseupgrade.dao;

import android.content.Context;

import com.huangzj.databaseupgrade.dao.bean.City;
import com.huangzj.databaseupgrade.dao.ormlite.OrmLiteDatabaseHelper;

/**
 * ormlite操作数据库Helper
 * <p/>
 * Created by lhd on 2015/9/14.
 */
public class DatabaseHelper extends OrmLiteDatabaseHelper {

    /**
     * 数据库名称
     */
    private static final String DATABASE_NAME = "myapplication.db";

    /**
     * 数据库版本号
     */
    private static final int DATABASE_VERSION = 4;

    private static DatabaseHelper instance;

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        addTable();
    }

    public static DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (DatabaseHelper.class) {
                if (instance == null) {
                    instance = new DatabaseHelper(context);
                }
            }
        }
        return instance;
    }

    /**
     * 注册数据表
     */
    private void addTable() {
        registerTable(City.class);
    }

}
