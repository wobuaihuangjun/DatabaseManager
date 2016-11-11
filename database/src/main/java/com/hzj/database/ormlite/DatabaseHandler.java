package com.hzj.database.ormlite;

import android.database.sqlite.SQLiteDatabase;

import com.hzj.database.CollectionUtil;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.List;

import timber.log.Timber;

/**
 * 数据库表更新处理的基类
 * <p/>
 * 需要自定义升级方案的数据表可继承该类，重写对应方法
 * <p/>
 * Created by huangzj on 2016/1/25.
 */
public class DatabaseHandler<T> {

    private static final String TAG = "DatabaseHandler";

    private Class<T> clazz;
    private final String tableName;

    public DatabaseHandler(Class<T> clazz) {
        this.clazz = clazz;
        tableName = DatabaseUtil.extractTableName(clazz);
    }

    public String getTableName() {
        return tableName;
    }

    protected void onUpgrade(SQLiteDatabase db, ConnectionSource cs) throws SQLException {
        List<ColumnStruct> oldStruct = DatabaseUtil.getOldTableStruct(db, tableName);
        List<ColumnStruct> newStruct = DatabaseUtil.getNewTableStruct(cs, clazz);

        if (oldStruct.isEmpty() && newStruct.isEmpty()) {
            Timber.d(TAG, "数据表结构都为空！不是合法的数据库bean！！！");
        } else if (oldStruct.isEmpty()) {
            Timber.d(TAG, "新增表");
            create(cs);
        } else if (newStruct.isEmpty()) {
            // 永远不会执行
            Timber.e(TAG, "删除表");
            drop(cs);
        } else {
            dealColumnChange(db, cs, oldStruct, newStruct);
        }
    }

    /**
     * 处理表有变化的情况
     */
    private void dealColumnChange(SQLiteDatabase db, ConnectionSource cs, List<ColumnStruct> oldStruct,
                                  List<ColumnStruct> newStruct) throws SQLException {
        if (DatabaseUtil.hasChangeColumnLimit(oldStruct, newStruct)) {
            Timber.d(TAG, "数据表已有字段的描述改变");
            // 已有字段描述改变了，删除旧表，重建新表
            reset(cs);
        } else {
            // 数据表已有字段的描述没有改变
            // 判断列是否有增减
            List<String> oldColumns = DatabaseUtil.getColumnNames(oldStruct);
            List<String> newColumns = DatabaseUtil.getColumnNames(newStruct);
            if (!oldColumns.equals(newColumns)) {
                Timber.d(TAG, "表发生了变化");
                // 判断列的变化情况：增加、减少、增减
                List<String> deleteList = DatabaseUtil.getDeleteColumns(oldColumns, newColumns);
                upgradeByCopy(db, cs, getCopyColumns(oldColumns, deleteList));
            } else {
                Timber.i(TAG, "表没有发生变化,不需要更新数据表");
            }
        }
    }

    /**
     * 拷贝数据的方式更新
     *
     * @param columns 原始列减去删除的列
     */
    private void upgradeByCopy(SQLiteDatabase db, ConnectionSource cs, String columns) throws SQLException {
        db.beginTransaction();

        String tempTableName = tableName + "_temp";
        String sql = "ALTER TABLE " + tableName + " RENAME TO " + tempTableName;
        try {
            //rename table
            db.execSQL(sql);

            //create table
            try {
                sql = TableUtils.getCreateTableStatements(cs, clazz).get(0);
                db.execSQL(sql);
            } catch (Exception e) {
                Timber.e(TAG, e);
                TableUtils.createTable(cs, clazz);
            }
            sql = "INSERT INTO " + tableName + " (" + columns + ") " +
                    " SELECT " + columns + " FROM " + tempTableName;
            db.execSQL(sql);

            //drop temp table
            sql = "DROP TABLE IF EXISTS " + tempTableName;
            db.execSQL(sql);

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Timber.e(TAG, e);
            throw new SQLException("upgrade database table struct fail");
        } finally {
            db.endTransaction();
        }
    }

    /**
     * 获取需要复制的列名
     */
    private String getCopyColumns(List<String> oldColumns, List<String> deleteList) {
        StringBuilder columns = new StringBuilder("");
        if (oldColumns == null || deleteList == null) {
            return columns.toString();
        }
        int index = 0;
        // 存在删除集合里的列不添加
        for (String columnName : oldColumns) {
            if (!CollectionUtil.existValue(columnName, deleteList)) {
                if (index == 0) {
                    columns.append("`").append(columnName).append("`");
                } else {
                    columns.append(", ").append("`").append(columnName).append("`");
                }
                index++;
            }
        }
        return columns.toString();
    }

    /**
     * 数据库升级
     */
    public void onUpgrade(SQLiteDatabase db, ConnectionSource cs, int oldVersion, int newVersion) throws SQLException {
        try {
            onUpgrade(db, cs);
        } catch (SQLException e) {
            Timber.e(TAG, e);
            reset(cs);
        }
    }

    /**
     * 数据库降级
     */
    public void onDowngrade(ConnectionSource connectionSource, int oldVersion, int newVersion) throws SQLException {
        reset(connectionSource);
    }

    /**
     * 删除重新创建数据表
     *
     * @throws SQLException
     */
    private void reset(ConnectionSource connectionSource) throws SQLException {
        drop(connectionSource);
        create(connectionSource);
    }

    /**
     * 清除表数据
     *
     * @throws SQLException
     */
    public void clear(ConnectionSource connectionSource) throws SQLException {
        TableUtils.clearTable(connectionSource, clazz);
    }

    /**
     * 创建表
     *
     * @throws SQLException
     */
    public void create(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTable(connectionSource, clazz);
    }

    /**
     * 删除表
     *
     * @throws SQLException
     */
    public void drop(ConnectionSource connectionSource) throws SQLException {
        TableUtils.dropTable(connectionSource, clazz, true);
    }

}
