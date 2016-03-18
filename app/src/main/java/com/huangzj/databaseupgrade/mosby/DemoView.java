package com.huangzj.databaseupgrade.mosby;

import com.hannesdorfmann.mosby.mvp.MvpView;

/**
 * Created by huangzj on 2016/3/17.
 */
public interface DemoView extends MvpView {

    void clearView();

    void updateView(String result);
}
