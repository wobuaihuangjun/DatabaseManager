package com.huangzj.databaseupgrade;

import android.content.Context;

import com.huangzj.databaseupgrade.bean.City;
import com.hzj.database.DbConfig;
import com.hzj.database.ormlite.DatabaseManagerWrapper;


/**
 * 数据库初始化管理
 * <p/>
 * Created by hzj on 2016/10/9.
 */
public class DatabaseManager extends DatabaseManagerWrapper {

    private static volatile DatabaseManager instance;

    private DatabaseManager() {
    }

    public static DatabaseManager getInstance() {
        DatabaseManager inst = instance;  // <<< 在这里创建临时变量
        if (inst == null) {
            synchronized (DatabaseManager.class) {
                inst = instance;
                if (inst == null) {
                    inst = new DatabaseManager();
                    instance = inst;
                }
            }
        }
        return inst;
    }

    /**
     * 初始化数据库
     */
    public void init(Context context) {
        super.init(context, DbConfig.Setting.DATABASE_NAME, DbConfig.Setting.DATABASE_VERSION);
    }


    @Override
    protected void registerTable() {
        addTable(City.class);
    }
}
