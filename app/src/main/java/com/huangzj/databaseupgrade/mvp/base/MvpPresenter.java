package com.huangzj.databaseupgrade.mvp.base;

import android.os.Bundle;

/**
 * Created by huangzj on 2016/3/15.
 */
public abstract class MvpPresenter<V extends MvpView> {

    protected V viewDelegate;

    public MvpPresenter() {
    }

    /**
     * 保存状态
     */
    public void onSaveInstanceState(Bundle outState) {
    }

    /**
     * 恢复状态
     */
    public void onRestoreInstanceState(Bundle savedInstanceState) {
    }

    public void onCreated(Bundle savedInstanceState) {
    }

    public void onResume() {
    }

    public void onPause() {
    }

    /**
     * 进行一些资源的释放
     */
    public void onDestroy() {
        viewDelegate = null;
    }
}
