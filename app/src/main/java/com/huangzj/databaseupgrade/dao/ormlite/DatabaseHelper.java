package com.huangzj.databaseupgrade.dao.ormlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.huangzj.databaseupgrade.dao.bean.City;
import com.huangzj.databaseupgrade.util.LogUtil;
import com.j256.ormlite.android.AndroidDatabaseConnection;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * ormlite操作数据库Helper
 * <p/>
 * Created by lhd on 2015/9/14.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    /**
     * 数据库名称
     */
    private static final String DATABASE_NAME = "myapplication.db";

    /**
     * 数据库版本号
     */
    private static final int DATABASE_VERSION = 3;

    private static DatabaseHelper instance;

    private static ArrayList<DatabaseHandler> TableHandlers;

    /**
     * dao缓存
     */
    private Map<String, Dao> daoMap;

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        daoMap = new HashMap<>();
    }

    public static DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (DatabaseHelper.class) {
                if (instance == null) {
                    instance = new DatabaseHelper(context);

                    // 注册所有用于处理升级等表操作的Handler
                    TableHandlers = new ArrayList<>();

                    TableHandlers.add(new DatabaseHandler<>(City.class));
                }
            }
        }
        return instance;
    }

    /**
     * 升级处理
     */
    public void upgrade(SQLiteDatabase db, ConnectionSource cs, int oldVersion, int newVersion) {
        LogUtil.i("数据库升级了" + " oldVersion = " +oldVersion+ " newVersion = "+newVersion);
        try {
            for (DatabaseHandler handler : TableHandlers) {
                handler.onUpgrade(db, cs, oldVersion, newVersion);
            }
        } catch (SQLException e) {
            LogUtil.e(e);
        }
    }

    /**
     * 降级处理
     */
    public void downgrade(ConnectionSource cs, int oldVersion, int newVersion) {
        LogUtil.i("数据库降级了" + " oldVersion = " +oldVersion+ " newVersion = "+newVersion);
        try {
            for (DatabaseHandler handler : TableHandlers) {
                handler.onDowngrade(cs, oldVersion, newVersion);
            }
        } catch (SQLException e) {
            LogUtil.e(e);
        }
    }

    /**
     * 清空所有表数据
     */
    public void clearAllData() {
        try {
            for (DatabaseHandler handler : TableHandlers) {
                handler.clear(connectionSource);
            }
        } catch (SQLException e) {
            LogUtil.e(e);
        }
    }

    public void createTables(ConnectionSource connectionSource) {
        try {
            for (DatabaseHandler handler : TableHandlers) {
                handler.create(connectionSource);
            }
        } catch (SQLException e) {
            LogUtil.e(e);
        }
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        createTables(connectionSource);
    }

    /**
     * 数据库升级，注意控制好数据库版本号，不然此方法将不会被调用到
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        upgrade(sqLiteDatabase, connectionSource, oldVersion, newVersion);
    }

    /**
     * 数据库降级
     */
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        ConnectionSource cs = getConnectionSource();
        Object conn = cs.getSpecialConnection();
        boolean clearSpecial = false;
        if (conn == null) {
            conn = new AndroidDatabaseConnection(db, true, this.cancelQueriesEnabled);

            try {
                cs.saveSpecialConnection((DatabaseConnection) conn);
                clearSpecial = true;
            } catch (SQLException var11) {
                throw new IllegalStateException("Could not save special connection", var11);
            }
        }

        try {
            this.onDowngrade(cs, oldVersion, newVersion);
        } finally {
            if (clearSpecial) {
                cs.clearSpecialConnection((DatabaseConnection) conn);
            }
        }
    }

    public void onDowngrade(ConnectionSource cs, int oldVersion, int newVersion) {
        downgrade(cs, oldVersion, newVersion);
    }

    public synchronized Dao getDao(Class cls) {
        Dao dao = null;
        String clsName = cls.getSimpleName();
        if (daoMap.containsKey(clsName)) {
            dao = daoMap.get(clsName);
        } else {
            try {
                dao = super.getDao(cls);
            } catch (SQLException e) {
                LogUtil.e(e);
                return null;
            }
            daoMap.put(clsName, dao);
        }
        return dao;
    }

    @Override
    public void close() {
        super.close();
        synchronized (this) {
            Iterator<Map.Entry<String, Dao>> it = daoMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Dao> entry = it.next();
                Dao dao = entry.getValue();
                dao = null;
                it.remove();
            }
        }
    }
}
