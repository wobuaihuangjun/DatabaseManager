package com.huangzj.databaseupgrade.dao.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by huangzj on 2016/2/18.
 */
@DatabaseTable(tableName = "a_test")
public class TestBean {

    @DatabaseField(generatedId = true)
    private Integer id;

    @DatabaseField
    private int count;

    @DatabaseField(unique = true)
    private String msgId;

    @DatabaseField(canBeNull = false)
    private Long createTime;

    @DatabaseField(defaultValue = "zhangsan")
    private String name;

//    @DatabaseField
//    private String watchId;
//
//    @DatabaseField(unique = true)
//    private String mobileId;
//
//    @DatabaseField
//    private String content;
}
