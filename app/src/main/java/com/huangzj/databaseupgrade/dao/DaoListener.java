package com.huangzj.databaseupgrade.dao;

/**
 * Created by lhd on 2015/10/19.
 */
public class DaoListener {

    private DaoThreadMode daoThreadMode = DaoThreadMode.MainThread;

    public void onDataChanged(int daoOperationType, Object data){}

    public void onDataChanged(Object data){}

    protected DaoThreadMode getDaoThreadMode() {
        return daoThreadMode;
    }

    protected void setDaoThreadMode(DaoThreadMode daoThreadMode) {
        this.daoThreadMode = daoThreadMode;
    }
}
