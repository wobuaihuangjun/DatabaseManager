package com.huangzj.databaseupgrade.dao.ormlite;

import android.content.Context;
import android.support.annotation.NonNull;

import com.huangzj.databaseupgrade.dao.DbCallBack;
import com.huangzj.databaseupgrade.rxjava.RxUtils;
import com.huangzj.databaseupgrade.util.JSONUtil;

import java.util.List;
import java.util.concurrent.Callable;

import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Created by huangzj on 2016/2/29.
 * <p/>
 * 提供同步与异步两种方式读写数据库
 * <p/>
 * 如果使用异步方式读写数据库，必须调用{@link RxDao#subscribe()}方法订阅，
 * 调用{@link RxDao#unsubscribe()}方法取消订阅
 */
public class RxDao<T> extends OrmLiteDao<T> {

    private CompositeSubscription subscriptions;
    private boolean cache;
    private Class<T> clazz;
    private String tabaleName;

    /**
     * @param context
     * @param cls
     * @param cache   是否缓存，如果设置缓存，数据查询将优先读取缓存
     */
    public RxDao(Context context, Class<T> cls, boolean cache) {
        super(context, cls);
        this.clazz = cls;
        this.cache = cache;
        tabaleName = DatabaseUtil.extractTableName(cls);
    }

    /**
     * 订阅读写操作的返回结果
     * <p/>
     * 注意：调用{@link RxDao#unsubscribe()}方法后，如果需要重新订阅读写操作，必须调用此方法
     */
    public void subscribe() {
        subscriptions = RxUtils.getNewCompositeSubIfUnsubscribed(subscriptions);
    }

    /**
     * 异步读写后，必须调用此方法取消订阅
     */
    public void unsubscribe() {
        RxUtils.unsubscribeIfNotNull(subscriptions);
    }

    /**
     * 增加一条记录
     */
    public boolean insert(T t) {
        boolean result = super.insert(t);
        if (result) {
            DbCache.clearByTable(tabaleName);
        }
        return result;
    }

    /**
     * 增加一条记录
     */
    public CompositeSubscription insertSync(final T t, @NonNull final DbCallBack listener) {
        Subscription subscription = RxUtils.subscribe(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                return insert(t);
            }
        }, new Action1<Boolean>() {
            @Override
            public void call(Boolean result) {
                listener.onComplete(result);
            }
        });
        subscriptions.add(subscription);
        return subscriptions;
    }

    /**
     * 批量插入
     */
    public boolean insertForBatch(List<T> list) {
        boolean result = super.insertForBatch(list);
        if (result) {
            DbCache.clearByTable(tabaleName);
        }
        return result;
    }

    /**
     * 批量插入
     */
    public CompositeSubscription insertForBatchSync(final List<T> list, @NonNull final DbCallBack listener) {
        Subscription subscription = RxUtils.subscribe(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                return insertForBatch(list);
            }
        }, new Action1<Boolean>() {
            @Override
            public void call(Boolean result) {
                listener.onComplete(result);
            }
        });
        subscriptions.add(subscription);
        return subscriptions;
    }

    /**
     * 清空数据
     */
    public boolean clearTableData() {
        boolean result = super.clearTableData();
        if (result) {
            DbCache.clearByTable(tabaleName);
        }
        return result;
    }

    /**
     * 清空数据
     */
    public CompositeSubscription clearTableDataSync(@NonNull final DbCallBack listener) {
        Subscription subscription = RxUtils.subscribe(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                return clearTableData();
            }
        }, new Action1<Boolean>() {
            @Override
            public void call(Boolean result) {
                listener.onComplete(result);
            }
        });
        subscriptions.add(subscription);
        return subscriptions;
    }

    /**
     * 根据id删除记录
     */
    public boolean deleteById(Integer id) {
        boolean result = super.deleteById(id);
        if (result) {
            DbCache.clearByTable(tabaleName);
        }
        return result;
    }

    /**
     * 根据id删除记录
     */
    public CompositeSubscription deleteByIdSync(final Integer id, @NonNull final DbCallBack listener) {
        Subscription subscription = RxUtils.subscribe(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                return deleteById(id);
            }
        }, new Action1<Boolean>() {
            @Override
            public void call(Boolean result) {
                listener.onComplete(result);
            }
        });
        subscriptions.add(subscription);
        return subscriptions;
    }

    public List<T> queryForAll() {
        if (!cache) {
            return super.queryForAll();
        }
        String json = DbCache.getCache(tabaleName, "queryForAll");
        List<T> result = JSONUtil.toCollection(json, List.class, clazz);
        if (result != null) {
            Timber.d("---------query from cache--");
//            return result;
        }
        result = super.queryForAll();
        DbCache.addCache(tabaleName, "queryForAll", result);
        return result;
    }

    public CompositeSubscription queryForAllSync(@NonNull final DbCallBack listener) {
        Subscription subscription = RxUtils.subscribe(new Callable<List<T>>() {
            @Override
            public List<T> call() {
                return queryForAll();
            }
        }, new Action1<List<T>>() {
            @Override
            public void call(List<T> result) {
                listener.onComplete(result);
            }
        });
        subscriptions.add(subscription);
        return subscriptions;
    }


}
