package com.huangzj.databaseupgrade.dao.ormlite;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.huangzj.databaseupgrade.util.CollectionUtil;
import com.j256.ormlite.misc.JavaxPersistence;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by huangzj on 2016/1/23.
 */
public class DatabaseUtil {


    /**
     * 获取表名
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> String extractTableName(Class<T> clazz) {
        DatabaseTable databaseTable = clazz.getAnnotation(DatabaseTable.class);
        String name;
        if (databaseTable != null && databaseTable.tableName() != null && databaseTable.tableName().length() > 0) {
            name = databaseTable.tableName();
        } else {
            name = JavaxPersistence.getEntityName(clazz);
            if (name == null) {
                name = clazz.getSimpleName().toLowerCase();
            }
        }
        return name;
    }


    /**
     * 获取数据库中对应表的列名集合
     *
     * @param columnStructList
     * @return
     */
    public static List<String> getColumnNames(List<ColumnStruct> columnStructList) {
        List<String> columnNames = new ArrayList<>();
        if (columnStructList == null) {
            return columnNames;
        }
        for (ColumnStruct columnStruct : columnStructList) {
            columnNames.add(columnStruct.getColumnName());
        }

        return columnNames;
    }

    /**
     * 生成新的数据表结构
     *
     * @param connectionSource
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> List<ColumnStruct> getNewTableStruct(ConnectionSource connectionSource, Class<T> clazz) {
        List<ColumnStruct> columnStruct = new ArrayList<>();
        try {
            String struct = TableUtils.getCreateTableStatements(connectionSource, clazz).get(0);
            Timber.i("新的建表语句：" + struct);
            columnStruct = getColumnStruct(struct);
        } catch (SQLException e) {
            Timber.e("", e);
            ;
        }
        return columnStruct;
    }

    /**
     * 查询数据库中对应表的表结构
     *
     * @param db
     * @param tableName
     * @return
     */
    public static List<ColumnStruct> getOldTableStruct(SQLiteDatabase db, String tableName) {

        List<ColumnStruct> columnStruct = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("select * from sqlite_master where type = ? AND name = ?",
                    new String[]{"table", tableName});
            if (cursor != null) {
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex("sql");
                if (-1 != columnIndex && cursor.getCount() > 0) {
                    String struct = cursor.getString(columnIndex);
                    Timber.i("旧的建表语句：" + struct);
                    columnStruct = getColumnStruct(struct);
                }
            }
        } catch (Exception e) {
            Timber.e("", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return columnStruct;
    }

    /**
     * 生成表结构
     *
     * @param tableStruct 规范建表语句
     * @return
     */
    public static List<ColumnStruct> getColumnStruct(String tableStruct) {
        // 这个函数总是给人一种不爽的感觉，欢迎提出更优秀的方案
        List<ColumnStruct> columnStructList = new ArrayList<>();
//      "CREATE TABLE `msg_record` (`id` INTEGER PRIMARY KEY AUTOINCREMENT , `title` VARCHAR ,  UNIQUE (`number`))"
        // 解析过程根据标准的ormlite建表语句设计
        String subString = tableStruct.substring(tableStruct.indexOf("(") + 1, tableStruct.length() - 1);
        String[] sub = subString.split(", ");
        for (String str : sub) {
            if (str.contains("(") || str.contains(")")) {
                str = str.replace("(", "").replace(")", "");
            }
            str = removeUnlessSpace(str);
            if (str.startsWith("`")) {
                String[] column = str.split("` ");
                columnStructList.add(new ColumnStruct(column[0].replace("`", ""), column[1]));
            } else {
                // 附加的额外字段限制,不是以`开始
                if (str.contains(",")) {
                    String[] column = str.split(" `");
                    String[] columns = column[1].replace("`", "").split(",");
                    for (String str1 : columns) {
                        modifyColumnStruct(columnStructList, str1, "UniqueCombo");
                    }
                } else {
                    String[] column = str.split(" `");
                    modifyColumnStruct(columnStructList, column[1].replace("`", ""), column[0]);
                }
            }
        }
        return columnStructList;
    }

    private static void modifyColumnStruct(List<ColumnStruct> list, String columnName, String limit) {
        if (list == null || list.isEmpty()) {
            return;
        }
        if (TextUtils.isEmpty(columnName) || TextUtils.isEmpty(limit)) {
            return;
        }
        int size = list.size();
        for (int i = 0; i < size; i++) {
            ColumnStruct struct = list.get(i);
            if (columnName.equals(struct.getColumnName())) {
                StringBuilder sb = new StringBuilder(struct.getColumnLimit());
                sb.append(" ");
                sb.append(limit);
                struct.setColumnLimit(sb.toString());
                return;
            }
        }

        list.add(new ColumnStruct(columnName, limit));
    }

    /**
     * 去除首尾空格
     */
    private static String removeUnlessSpace(String str) {
        while (str.startsWith(" ")) {
            str = str.substring(1);
        }
        while (str.endsWith(" ")) {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

    /**
     * 判断数据表已有字段的描述是否有改变
     *
     * @param oldStructList
     * @param newStructList
     * @return
     */
    public static boolean hasChangeColumnLimit(List<ColumnStruct> oldStructList,
                                               List<ColumnStruct> newStructList) {
        if (oldStructList == null || newStructList == null) {
            return false;
        }
        for (ColumnStruct oldStruct : oldStructList) {
            if (oldStruct == null) {
                continue;
            }
            String oldName = oldStruct.getColumnName();
            if (oldName == null || oldName.length() <= 0) {
                continue;
            }
            for (ColumnStruct newStruct : newStructList) {
                if (newStruct == null) {
                    continue;
                }
                if (compareColumnStruct(oldStruct, newStruct)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 比较列结构是否有变化
     *
     * @param oldStruct
     * @param newStruct
     * @return
     */
    private static boolean compareColumnStruct(ColumnStruct oldStruct, ColumnStruct newStruct) {
        if (oldStruct == null || newStruct == null) {
            return false;
        }
        String oldName = oldStruct.getColumnName();
        String oldLimit = oldStruct.getColumnLimit();
        if (oldName == null || !oldName.equals(newStruct.getColumnName())) {
            // 不是同一字段
            return false;
        }
        // 比较同一字段的差异
        String newLimit = newStruct.getColumnLimit();
        if (oldLimit == null && newLimit == null) {
            // 都没有限制符
            return false;
        } else if (oldLimit == null || newLimit == null) {
            // 某一个添加了限制符
            System.out.println("某一个添加了限制符");
            return true;
        } else {
            if (!oldLimit.equals(newLimit)) {
                // 修改了限制符
                System.out.println("修改了限制符");
                System.out.println("oldStruct = " + oldStruct.toString());
                System.out.println("newStruct = " + newStruct.toString());
                return true;
            }
        }
        return false;
    }

    /**
     * 添加自增的列到集合
     *
     * @param columns
     * @param oldStruct
     * @return
     */
    public static List<String> addGeneratedId(List<String> columns, List<ColumnStruct> oldStruct) {
        if (columns == null || oldStruct == null) {
            return null;
        }

        for (ColumnStruct struct : oldStruct) {
            // 自增的列不纳入数据拷贝的范围
            if (struct != null && struct.getColumnLimit().contains("INTEGER PRIMARY KEY AUTOINCREMENT")) {
                String columnName = struct.getColumnName();
                if (!CollectionUtil.existValue(columnName, columns)) {
                    columns.add(columnName);
                }
            }
        }
        return columns;
    }

    /**
     * 获得数据表的增减字段
     *
     * @param oldColumns
     * @param newColumns
     * @return
     */
    public static List<String> getDeleteColumns(List<String> oldColumns, List<String> newColumns) {
        if (oldColumns == null || newColumns == null) {
            return null;
        }
        return getColumnChange(oldColumns, newColumns);
    }

    public static List<String> getAddColumns(List<String> oldColumns, List<String> newColumns) {
        if (oldColumns == null || newColumns == null) {
            return null;
        }
        return getColumnChange(newColumns, oldColumns);
    }

    private static List<String> getColumnChange(List<String> oldColumns, List<String> newColumns) {
        List<String> columnList = new ArrayList<>();
        if (oldColumns == null || newColumns == null) {
            return columnList;
        }

        boolean exist;
        for (String oldColumn : oldColumns) {
            exist = false;
            for (String newColumn : newColumns) {
                if (newColumn != null && newColumn.equals(oldColumn)) {
                    // 存在对应的字段
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                // 字段不匹配
                columnList.add(oldColumn);
            }
        }
        return columnList;
    }

    /**
     * 集合中是否存在指定列
     */
    public static ColumnStruct existColumn(String columnName, List<ColumnStruct> list) {
        if (list == null || columnName == null) {
            return null;
        }
        for (ColumnStruct struct : list) {
            if (struct == null) {
                continue;
            }
            if (columnName.equals(struct.getColumnName())) {
                return struct;
            }
        }
        return null;
    }

}
