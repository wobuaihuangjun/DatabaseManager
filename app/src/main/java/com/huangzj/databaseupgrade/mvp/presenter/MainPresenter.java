package com.huangzj.databaseupgrade.mvp.presenter;

import android.content.Context;

import com.huangzj.databaseupgrade.bean.City;
import com.huangzj.databaseupgrade.mvp.base.MvpPresenter;
import com.huangzj.databaseupgrade.mvp.view.MainView;
import com.huangzj.databaseupgrade.util.UUIDUtil;
import com.hzj.database.ormlite.DbCallBack;
import com.hzj.database.ormlite.RxDao;

import java.util.List;


import timber.log.Timber;

/**
 * Created by huangzj on 2016/3/15.
 */
public class MainPresenter extends MvpPresenter<MainView> {

    private RxDao cityDao;

    public MainPresenter(MainView mainView, Context context) {
        super();
        viewDelegate = mainView;
        cityDao = new RxDao<>(context, City.class);
    }

    public void insert() {
        String cityUuid = UUIDUtil.getUUID();
        City city = new City();
        city.setProvinceName("广东省");
        city.setCityName("东莞市");
        city.setCityNo(cityUuid);

        cityDao.insert(city, new DbCallBack() {
            @Override
            public void onComplete(Object data) {
                Timber.d("---------sync insert complete--" + data);
                query();
            }
        });
    }

    public void query() {
        cityDao.queryForAll(new DbCallBack() {
            @Override
            public void onComplete(Object data) {
                Timber.d("---------sync query success");
                queryFinish((List<City>) data);
            }
        });
    }

    private void queryFinish(List<City> list) {
        StringBuilder sb = new StringBuilder("查询结果：\n");
        if (list == null || list.size() <= 0) {
            sb.append("空");
        } else {
            sb.append("查询到的总条数").append(list.size()).append("\n\n");
            sb.append("第一条记录为：\n");
            sb.append(list.get(0).toString()).append("\n\n");
            sb.append("最后一条记录为：\n");
            sb.append(list.get(list.size() - 1).toString()).append("\n\n");
        }
        viewDelegate.updateView(sb.toString());
    }

    public void clear() {
        cityDao.clearTableData(new DbCallBack() {
            @Override
            public void onComplete(Object data) {
                Timber.d("---------sync clear complete--" + data);
                viewDelegate.clearView();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        cityDao.subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
        cityDao.unsubscribe();
    }

    @Override
    public void onDestroy() {
        cityDao = null;
    }

}
