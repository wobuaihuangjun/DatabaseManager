package com.huangzj.databaseupgrade.dao.ormlite;

import android.content.Context;

import com.huangzj.databaseupgrade.dao.DbCallBack;
import com.huangzj.databaseupgrade.rxjava.RxUtils;

import java.util.List;
import java.util.concurrent.Callable;

import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by huangzj on 2016/2/29.
 */
public class RxDao<T> extends OrmLiteDao<T> {

    private CompositeSubscription subscriptions;

    public RxDao(Context context, Class<T> cls) {
        super(context, cls);
        subscriptions = RxUtils.getNewCompositeSubIfUnsubscribed(subscriptions);
    }

    public CompositeSubscription queryForAllSync(final DbCallBack listener) {
        Subscription subscription = RxUtils.subscribe(new Callable<List<T>>() {
            @Override
            public List<T> call() throws Exception {
                return queryForAll();
            }
        }, new Action1<List<T>>() {
            @Override
            public void call(List<T> result) {
                listener.onSuccess(result);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                listener.onError(throwable);
            }
        });
        subscriptions.add(subscription);
        return subscriptions;
    }

    public List<T> queryForAll() {
        return super.queryForAll();
    }

    public CompositeSubscription insertSync(final T t, final DbCallBack listener) {
        Subscription subscription = RxUtils.subscribe(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return insert(t);
            }
        }, new Action1<Boolean>() {
            @Override
            public void call(Boolean result) {
                listener.onSuccess(result);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                listener.onError(throwable);
            }
        });
        subscriptions.add(subscription);
        return subscriptions;
    }

    public boolean insert(T t) {
        return super.insert(t);
    }
}
