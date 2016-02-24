package com.huangzj.databaseupgrade.dao.ormlite;

import android.content.Context;

import com.huangzj.databaseupgrade.dao.bean.City;

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
    private static final int DATABASE_VERSION = 2;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        addTable();
    }

    /**
     *  注册数据表
     */
    private void addTable() {
        registerTable(City.class);
    }

}
