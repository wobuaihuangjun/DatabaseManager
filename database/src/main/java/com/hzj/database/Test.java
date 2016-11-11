package com.hzj.database;


import com.hzj.database.bean.City;
import com.hzj.database.ormlite.ColumnStruct;
import com.hzj.database.ormlite.DatabaseHandler;
import com.hzj.database.ormlite.DatabaseUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangzj on 2016/1/25.
 */
public class Test {

    public static void main(String[] args) {

//        test1();
//        test2();
//        test3();
        test4();
    }

    private static void test4() {
        registerTable(City.class);
        registerTable(City.class);
    }

    static List<DatabaseHandler> tableHandlers;

    public static <T> void registerTable(Class<T> clazz) {
        if (tableHandlers == null) {
            tableHandlers = new ArrayList<>();
        }
        DatabaseHandler handler = new DatabaseHandler<>(clazz);
        if (isValid(handler, tableHandlers)) {
            tableHandlers.add(handler);
            System.out.println("注册成功");
        } else {
            System.out.println("已经注册过了");
        }
    }

    public static boolean isValid(DatabaseHandler handler, List<DatabaseHandler> list) {
        if (list == null || handler == null) {
            return false;
        }
        String tableName = handler.getTableName();
        for (DatabaseHandler element : list) {
            if (tableName.equals(element.getTableName())) {
                return false;
            }
        }
        return true;
    }

    private static void test3() {
        String tableStruct0 = "CREATE TABLE `dialog` (`dialogId` BIGINT , `dialogType` INTEGER , `id` INTEGER PRIMARY KEY AUTOINCREMENT , `title` VARCHAR ,  UNIQUE (`dialogId`))";
        String tableStruct1 = "CREATE TABLE `dialog` (`dialogId` BIGINT , `dialogType` INTEGER , `id` INTEGER PRIMARY KEY AUTOINCREMENT , `title` VARCHAR ,  UNIQUE (`dialogId`))";

        List<ColumnStruct> oldStruct = DatabaseUtil.getColumnStruct(tableStruct0);
        List<ColumnStruct> newStruct = DatabaseUtil.getColumnStruct(tableStruct1);

        System.out.println(oldStruct.toString());

        if (oldStruct.isEmpty() && newStruct.isEmpty()) {
            System.out.println("数据表结构都为空！不是合法的数据库bean！！！");
            return;
        } else if (oldStruct.isEmpty()) {
            System.out.println("新增表");
        } else if (newStruct.isEmpty()) {
            System.out.println("删除重建表");
        } else {
            dealColumnChange(oldStruct, newStruct);
        }
    }

    private static void dealColumnChange(List<ColumnStruct> oldStruct, List<ColumnStruct> newStruct) {
        if (DatabaseUtil.hasChangeColumnLimit(oldStruct, newStruct)) {
            System.out.println("数据表已有字段的描述改变");
            // 已有字段描述改变了，删除旧表，重建新表
        } else {
            // 数据表已有字段的描述没有改变
            // 判断列是否有增减
            List<String> oldColumns = DatabaseUtil.getColumnNames(oldStruct);
            List<String> newColumns = DatabaseUtil.getColumnNames(newStruct);
            if (!oldColumns.equals(newColumns)) {
                System.out.println("列发生了变化");
                // 判断列的变化情况：增加、减少、增减
                List<String> deleteList = DatabaseUtil.getDeleteColumns(oldColumns, newColumns);
                if (deleteList == null || deleteList.isEmpty()) {
//                    upgradeByAdd(db, addList, newStruct);
                } else {
//                    upgradeByCopy(db, cs, getCopyColumns(oldColumns, deleteList));
                }
            } else {
                System.out.println("列没有发送变化");
                // 不需要升级数据库
            }
        }
    }

    private static void test2() {
        String tableStruct = "CREATE TABLE `a_test` (`name` VARCHAR DEFAULT 'zhangsan' , `createTime` BIGINT NOT NULL , `id` INTEGER PRIMARY KEY AUTOINCREMENT , `msgId` VARCHAR , `count` INTEGER , watchId VARCHAR,  UNIQUE (`msgId`))";
        List<ColumnStruct> struct = DatabaseUtil.getColumnStruct(tableStruct);
        System.out.println(struct.toString());
    }

    public static void test1() {
        String str0 = "CREATE TABLE `msg_record` (`id` INTEGER PRIMARY KEY AUTOINCREMENT , `title` VARCHAR )";
        String str1 = "CREATE TABLE `msg_record` (`id` INTEGER PRIMARY KEY AUTOINCREMENT , `title` VARCHAR )";
        String str2 = "CREATE TABLE `msg_record` (`id` INTEGER PRIMARY KEY AUTOINCREMENT , `title` INTEGER )";
        String str3 = "CREATE TABLE `msg_record` (`id` INTEGER PRIMARY KEY AUTOINCREMENT , `title` VARCHAR , `msgId` INTEGER )";
        String str4 = "CREATE TABLE `msg_record` (`id` INTEGER PRIMARY KEY AUTOINCREMENT , `msgId` INTEGER )";

        upgrade(str0, str1);

        upgrade(str0, str2);

        upgrade(str0, str3);

        upgrade(str0, str4);
    }

    public static void upgrade(String oldStr, String newStr) {
        System.out.println("---------比较开始-------");
        System.out.println("");
        List<ColumnStruct> oldStruct = DatabaseUtil.getColumnStruct(oldStr);
        List<ColumnStruct> newStruct = DatabaseUtil.getColumnStruct(newStr);

        List<String> oldColumns = DatabaseUtil.getColumnNames(oldStruct);
        List<String> newColumns = DatabaseUtil.getColumnNames(newStruct);

        if (oldStruct.isEmpty() && newStruct.isEmpty()) {
            System.out.println("数据表结构都为空！不是合法的数据库bean！！！");
        } else if (oldStruct.isEmpty()) {
            System.out.println("新增表");
        } else if (newStruct.isEmpty()) {
            System.out.println("删除表");
        } else {
            if (DatabaseUtil.hasChangeColumnLimit(oldStruct, newStruct)) {
                System.out.println("数据表已有字段的描述改变");
                // 如果字段描述改变了，删除旧表，重建新表
            } else {
                System.out.println("数据表已有字段的描述没有改变");

                // 判断列是否有增减
                if (oldColumns.equals(newColumns)) {
                    System.out.println("列没有发生变化");
                    System.out.println("数据表的结构没有更改！！！");
                } else {
                    System.out.println("列发生了变化");
                    List<String> deleteList = DatabaseUtil.getDeleteColumns(oldColumns, newColumns);
//                    List<String> addList = DatabaseUtil.getAddColumns(oldColumns, newColumns);
//                    if (addList != null && !addList.isEmpty()) {
//                        StringBuilder builder = new StringBuilder();
//                        for (String srt : addList) {
//                            builder.append(srt).append(",");
//                        }
//                        System.out.println("增加了列：" + builder.toString());
//                    }
                    if (deleteList != null && !deleteList.isEmpty()) {
                        StringBuilder builder = new StringBuilder();
                        for (String srt : deleteList) {
                            builder.append(srt).append(",");
                        }
                        System.out.println("删除了列：" + builder.toString());
                    }
                }
            }
        }
        System.out.println("");
        System.out.println("-------比较结束---------");
    }

}
