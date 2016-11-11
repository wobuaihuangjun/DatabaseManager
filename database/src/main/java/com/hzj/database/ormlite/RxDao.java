package com.hzj.database.ormlite;

import android.content.Context;

import com.hzj.database.RxUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Created by huangzj on 2016/2/29.
 * 数据库读写操作Dao
 * 提供同步与异步两种方式读写数据库
 * <p>
 * 如果使用异步方式读写数据库，必须调用{@link RxDao#subscribe()}方法订阅，
 * 调用{@link RxDao#unsubscribe()}方法取消订阅
 */
public class RxDao<T> extends OrmLiteDao<T> {

    private static final String TAG = "RxDao";

    private CompositeSubscription subscriptions;

    public RxDao(Context context, Class<T> cls) {
        super(context, cls);
    }

    /**
     * 订阅读写操作的返回结果
     * <p>
     * 注意：调用{@link RxDao#unsubscribe()}方法后，如果需要重新订阅读写操作，必须调用此方法
     */
    public void subscribe() {
        subscriptions = RxUtil.getNewCompositeSubIfUnsubscribed(subscriptions);
    }

    /**
     * 异步读写后，必须调用此方法取消订阅
     */
    public void unsubscribe() {
        RxUtil.unsubscribeIfNotNull(subscriptions);
    }

    /**
     * 增加一条记录
     */
    public void insert(final T t, final DbCallBack listener) {
        subscribe(new Callable<Boolean>() {
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
    }

    /**
     * 批量插入
     */
    public void insertForBatch(final List<T> list, final DbCallBack listener) {
        subscribe(new Callable<Boolean>() {
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
    }


    /**
     * 清空数据
     */
    public void clearTableData(final DbCallBack listener) {
        subscribe(new Callable<Boolean>() {
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
    }

    /**
     * 按列删除
     *
     * @param columnName 指定条件列名
     * @param value      值
     */
    public void deleteByColumnName(final String columnName, final Object value, final DbCallBack listener) {
        subscribe(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                return deleteByColumnName(columnName, value);
            }
        }, new Action1<Boolean>() {
            @Override
            public void call(Boolean result) {
                listener.onComplete(result);
            }
        });
    }

    /**
     * 通过列的键值组合删除
     *
     * @param map      列的键值组合
     * @param listener
     */
    public void deleteByColumnName(final Map<String, Object> map, final DbCallBack listener) {
        subscribe(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                return deleteByColumnName(map);
            }
        }, new Action1<Boolean>() {
            @Override
            public void call(Boolean result) {
                listener.onComplete(result);
            }
        });
    }

    /**
     * 删除小于指定列指定值的数据
     *
     * @param columnName 列名
     * @param value      列值，删除小于该值的所有数据将被删除
     * @param listener   回调接口
     */
    public void deleteLtValue(final String columnName, final Object value, final DbCallBack listener) {
        subscribe(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return deleteLtValue(columnName, value);
            }
        }, new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                listener.onComplete(integer);
            }
        });
    }

    /**
     * 批量删除
     */
    public void deleteForBatch(final List<T> list, final DbCallBack listener) {
        subscribe(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                return deleteForBatch(list);
            }
        }, new Action1<Boolean>() {
            @Override
            public void call(Boolean result) {
                listener.onComplete(result);
            }
        });
    }

    /**
     * 修改记录
     *
     * @param t 新的数据实体,ID不能为空
     */
    public void update(final T t, final DbCallBack listener) {
        subscribe(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                return update(t);
            }
        }, new Action1<Boolean>() {
            @Override
            public void call(Boolean result) {
                listener.onComplete(result);
            }
        });
    }

    /**
     * 修改记录
     *
     * @param t          新的数据实体
     * @param columnName 指定查询条件列名
     * @param value      查询条件值
     * @return
     */
    public void updateBy(final T t, final String columnName, final Object value, final DbCallBack listener) {
        subscribe(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                return updateBy(t, columnName, value);
            }
        }, new Action1<Boolean>() {
            @Override
            public void call(Boolean result) {
                listener.onComplete(result);
            }
        });
    }

    /**
     * 按条件更新
     *
     * @param t     新的数据实体
     * @param value 更新条件
     * @return
     */
    public void updateBy(final T t, final Map<String, Object> value, final DbCallBack listener) {
        subscribe(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                return updateBy(t, value);
            }
        }, new Action1<Boolean>() {
            @Override
            public void call(Boolean result) {
                listener.onComplete(result);
            }
        });
    }

    /**
     * 批量修改
     *
     * @param list 记录集合
     * @return
     */
    public void updateForBatch(final List<T> list, final DbCallBack listener) {
        subscribe(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                return updateForBatch(list);
            }
        }, new Action1<Boolean>() {
            @Override
            public void call(Boolean result) {
                listener.onComplete(result);
            }
        });
    }

    /**
     * 查询符号指定条件的记录数
     *
     * @param map 查询条件的键值组合
     * @return 记录数
     */
    public long getCount(Map<String, Object> map) {
        return super.getCount(map);
    }

    /**
     * 查询符号指定条件的记录数
     *
     * @param map      查询条件的键值组合
     * @param listener
     */
    public void getCount(final Map<String, Object> map, final DbCallBack listener) {
        subscribe(new Callable<Long>() {
            @Override
            public Long call() {
                return getCount(map);
            }
        }, new Action1<Long>() {
            @Override
            public void call(Long result) {
                listener.onComplete(result);
            }
        });
    }

    /**
     * 查询数据库所有的记录数
     *
     * @return 记录数
     */
    public long getCount() {
        return super.getCount();
    }

    /**
     * 查询数据库所有的记录数
     *
     * @param listener 回调
     */
    public void getCount(final DbCallBack listener) {
        subscribe(new Callable<Long>() {
            @Override
            public Long call() {
                return getCount();
            }
        }, new Action1<Long>() {
            @Override
            public void call(Long result) {
                listener.onComplete(result);
            }
        });
    }

    /**
     * 查询表中所有数据
     */
    public void queryForAll(final DbCallBack listener) {
        subscribe(new Callable<List<T>>() {
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
    }

    /**
     * 根据表中键值组合行查询
     *
     * @param map 查询条件的键值组合
     * @return
     */
    public void queryByColumnName(final Map<String, Object> map, final DbCallBack listener) {
        subscribe(new Callable<List<T>>() {
            @Override
            public List<T> call() {
                return queryByColumnName(map);
            }
        }, new Action1<List<T>>() {
            @Override
            public void call(List<T> result) {
                listener.onComplete(result);
            }
        });
    }

    /**
     * 列名查询
     *
     * @param columnName 指定查询条件列名
     * @param value      查询条件值
     */
    public void queryByColumnName(final String columnName, final Object value, final DbCallBack listener) {
        subscribe(new Callable<List<T>>() {
            @Override
            public List<T> call() {
                return queryByColumnName(columnName, value);
            }
        }, new Action1<List<T>>() {
            @Override
            public void call(List<T> result) {
                listener.onComplete(result);
            }
        });
    }

    /**
     * 排序查询
     *
     * @param orderColumn 排序的列
     * @param columnName  指定查询条件列名
     * @param value       查询条件值
     * @param ascending   true为升序,false为降序
     * @return
     */
    public void queryByOrder(final String orderColumn, final String columnName,
                             final Object value, final boolean ascending, final DbCallBack listener) {
        subscribe(new Callable<List<T>>() {
            @Override
            public List<T> call() {
                return queryByOrder(orderColumn, columnName, value, ascending);
            }
        }, new Action1<List<T>>() {
            @Override
            public void call(List<T> result) {
                listener.onComplete(result);
            }
        });
    }

    /**
     * 返回数据库中所有记录的指定列的值
     *
     * @param selectColumns 指定列名
     * @param listener
     */
    public void queryAllBySelectColumns(final String[] selectColumns, final DbCallBack listener) {

        subscribe(new Callable<List<T>>() {
            @Override
            public List<T> call() {
                return queryAllBySelectColumns(selectColumns);
            }
        }, new Action1<List<T>>() {
            @Override
            public void call(List<T> result) {
                listener.onComplete(result);
            }
        });
    }

    /**
     * 排序查询指定条件下，大于指定值的所有记录
     *
     * @param orderColumn 大于的列
     * @param limitValue  大于的值
     * @param columnName  查询条件列名
     * @param value       查询条件值
     * @param ascending   true为升序,false为降序
     * @return
     */
    public void queryGeByOrder(final String orderColumn, final Object limitValue, final String columnName,
                               final Object value, final boolean ascending, final DbCallBack listener) {
        subscribe(new Callable<List<T>>() {
            @Override
            public List<T> call() {
                return queryGeByOrder(orderColumn, limitValue, columnName, value, ascending);
            }
        }, new Action1<List<T>>() {
            @Override
            public void call(List<T> result) {
                listener.onComplete(result);
            }
        });
    }

    /**
     * 排序查询小于指定值的所有记录
     *
     * @param orderColumn 小于的列
     * @param limitValue  小于的值
     * @param ascending   true为升序,false为降序
     * @return 查询结果
     */
    public void queryLeByOrder(final String orderColumn, final Object limitValue,
                               final boolean ascending, final DbCallBack listener) {
        subscribe(new Callable<List<T>>() {
            @Override
            public List<T> call() {
                return queryLeByOrder(orderColumn, limitValue, ascending);
            }
        }, new Action1<List<T>>() {
            @Override
            public void call(List<T> result) {
                listener.onComplete(result);
            }
        });
    }

    /**
     * 分页查询,并按列排序
     *
     * @param columnName  查询条件列名
     * @param value       查询条件值
     * @param orderColumn 排序列名
     * @param ascending   true为升序,false为降序
     * @param offset      搜索下标
     * @param count       搜索条数
     */
    public void queryForPagesByOrder(final String columnName, final Object value,
                                     final String orderColumn, final boolean ascending,
                                     final Long offset, final Long count,
                                     final DbCallBack listener) {
        subscribe(new Callable<List<T>>() {
            @Override
            public List<T> call() {
                return queryForPagesByOrder(columnName, value, orderColumn, ascending, offset, count);
            }
        }, new Action1<List<T>>() {
            @Override
            public void call(List<T> result) {
                listener.onComplete(result);
            }
        });
    }

    /**
     * 分页查询,并按列排序
     *
     * @param map         查询的列条件
     * @param offset      查询的下标
     * @param count       查询的条数
     * @param orderColumn 排序的列
     * @param ascending   升序或降序,true为升序,false为降序
     * @return
     */
    public void queryForPagesByOrder(final Map<String, Object> map, final String orderColumn,
                                     final boolean ascending, final Long offset,
                                     final Long count, final DbCallBack listener) {
        subscribe(new Callable<List<T>>() {
            @Override
            public List<T> call() {
                return queryForPagesByOrder(map, orderColumn, ascending, offset, count);
            }
        }, new Action1<List<T>>() {
            @Override
            public void call(List<T> result) {
                listener.onComplete(result);
            }
        });
    }

    /**
     * 按条件查询，返回第一条符号要求的记录
     *
     * @param columnName 查询条件列名
     * @param value      查询条件值
     */
    public void queryForFirst(final String columnName, final Object value, final DbCallBack listener) {
        subscribe(new Callable<T>() {
            @Override
            public T call() {
                return queryForFirst(columnName, value);
            }
        }, new Action1<T>() {
            @Override
            public void call(T result) {
                listener.onComplete(result);
            }
        });
    }

    /**
     * 通过表列名查询第一条记录
     *
     * @param map 查询条件键值组合
     */
    public void queryForFirst(final Map<String, Object> map, final DbCallBack listener) {
        subscribe(new Callable<T>() {
            @Override
            public T call() {
                return queryForFirst(map);
            }
        }, new Action1<T>() {
            @Override
            public void call(T result) {
                listener.onComplete(result);
            }
        });
    }

    /**
     * 排序查询
     *
     * @param map         查询条件键值组合
     * @param orderColumn 排序的列
     * @param ascending   是否升序
     * @return
     */
    public void queryForFirstByOrder(final Map<String, Object> map, final String orderColumn,
                                     final boolean ascending, final DbCallBack listener) {
        subscribe(new Callable<T>() {
            @Override
            public T call() {
                return queryForFirstByOrder(map, orderColumn, ascending);
            }
        }, new Action1<T>() {
            @Override
            public void call(T result) {
                listener.onComplete(result);
            }
        });
    }

    /**
     * 排序查询
     *
     * @param columnName  查询条件列名
     * @param value       查询条件值
     * @param orderColumn 排序的列
     * @param ascending   是否升序
     * @return
     */
    public void queryForFirstByOrder(final String columnName, final Object value, final String orderColumn,
                                     final boolean ascending, final DbCallBack listener) {
        subscribe(new Callable<T>() {
            @Override
            public T call() {
                return queryForFirstByOrder(columnName, value, orderColumn, ascending);
            }
        }, new Action1<T>() {
            @Override
            public void call(T result) {
                listener.onComplete(result);
            }
        });
    }

    /**
     * 异步处理
     */
    private <T> Observable<T> subscribe(Callable<T> callable, Action1<T> action) {
        Observable<T> observable = getDbObservable(callable);
        Subscription subscription = observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(action);
        if (subscriptions == null) {
            throw new RuntimeException("Do you call subscribe()");
        }
        subscriptions.add(subscription);
        return observable;
    }

    private <T> Observable<T> getDbObservable(final Callable<T> func) {
        return Observable.create(
                new Observable.OnSubscribe<T>() {
                    @Override
                    public void call(Subscriber<? super T> subscriber) {
                        try {
                            subscriber.onNext(func.call());
                        } catch (Exception ex) {
                            Timber.e(TAG, "Error reading from the database" + ex);
                        }
                    }
                });
    }
}
