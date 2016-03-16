package com.huangzj.databaseupgrade.mvp.view;

import com.huangzj.databaseupgrade.mvp.base.MvpView;

/**
 * Created by huangzj on 2016/3/15.
 */
public interface MainView extends MvpView{

    void clearView();

    void updateView(String result);
}
