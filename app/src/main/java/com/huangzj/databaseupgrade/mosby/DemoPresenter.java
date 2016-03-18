package com.huangzj.databaseupgrade.mosby;

import android.content.Context;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.huangzj.databaseupgrade.dao.RxDao;
import com.huangzj.databaseupgrade.dao.bean.City;
import com.huangzj.databaseupgrade.dao.ormlite.DbCallBack;
import com.huangzj.databaseupgrade.util.UUIDUtil;

import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by huangzj on 2016/3/17.
 */
public class DemoPresenter extends MvpBasePresenter<DemoView> {

    private RxDao cityDao;

    public DemoPresenter(Context context) {
        cityDao = new RxDao<>(context, City.class);
    }

    public void subscribe() {
        cityDao.subscribe();
    }

    public void unsubscribe() {
        cityDao.unsubscribe();
    }

    public void insert() {
        String cityUuid = UUIDUtil.getUUID();
        City city = new City();
        city.setProvinceName("广东省");
        city.setCityName("东莞市");
        city.setCityNo(cityUuid);

        cityDao.insertSync(city, new DbCallBack() {
            @Override
            public void onComplete(Object data) {
                Timber.d("---------sync insert complete--" + data);
                query();
            }
        });
    }

    public void query() {
        cityDao.queryForAllSync(new DbCallBack() {
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
        if (isViewAttached()) {
            getView().updateView(sb.toString());
        }
    }

    public void clear() {
//        Observable<Boolean> observable = Observable.create(new Observable.OnSubscribe<Boolean>() {
//            @Override
//            public void call(Subscriber<? super Boolean> subscriber) {
//                subscriber.onNext(cityDao.clearTableData());
//                subscriber.onCompleted();
//            }
//        });
//        observable.subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<Boolean>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onNext(Boolean aBoolean) {
//                        if (isViewAttached()) {
//                            getView().clearView();
//                        }
//                    }
//                });

        cityDao.clearTableDataSync(new DbCallBack() {
            @Override
            public void onComplete(Object data) {
                Timber.d("---------sync clear complete--" + data);
                if (isViewAttached()) {
                    getView().clearView();
                }
            }
        });
    }

}
