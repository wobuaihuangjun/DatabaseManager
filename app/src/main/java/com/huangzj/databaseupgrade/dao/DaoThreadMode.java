package com.huangzj.databaseupgrade.dao;

/**
 * Created by lhd on 2015/12/14.
 */
public enum DaoThreadMode {

    /**
     * 同一个线程
     */
    PostThread,

    /**
     * 主线程
     */
    MainThread,

    /**
     * 后台线程
     */
    BackgroundThread,

    /**
     * 单独开一个线程
     */
    Async
}
