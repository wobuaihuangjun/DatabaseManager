package com.hzj.database.ormlite;

/**
 * Created by huangzj on 2016/1/25.
 * <p/>
 * 数据库表中列的结构
 */
public class ColumnStruct {

    private String columnName;
    private String columnLimit;

    public ColumnStruct() {
    }

    public ColumnStruct(String columnName, String columnLimit) {
        this.columnName = columnName;
        this.columnLimit = columnLimit;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnLimit() {
        return columnLimit;
    }

    public void setColumnLimit(String columnLimit) {
        this.columnLimit = columnLimit;
    }

    @Override
    public String toString() {
        return "ColumnStruct{" +
                "columnName='" + columnName + '\'' +
                ", columnLimit='" + columnLimit + '\'' +
                '}';
    }
}
