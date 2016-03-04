package com.huangzj.databaseupgrade.rxjava;

import java.util.concurrent.Callable;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class RxUtils {

    public static void unsubscribeIfNotNull(Subscription subscription) {
        if (subscription != null) {
            subscription.unsubscribe();
            Timber.i("-------unsubscribe");
        }
    }

    public static CompositeSubscription getNewCompositeSubIfUnsubscribed(CompositeSubscription subscription) {
        if (subscription == null || subscription.isUnsubscribed()) {
            Timber.i("-------init rxjava");
            return new CompositeSubscription();
        }
        return subscription;
    }

    // 分割线
    // 数据库操作demo

    /**
     * 注册
     *
     * @param callable
     * @param action
     * @param <T>
     * @return
     */
    public static <T> Subscription subscribe(Callable<T> callable, Action1<T> action) {
        return getDbObservable(callable)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(action);
    }


    private static <T> Observable<T> getDbObservable(final Callable<T> func) {
        return Observable.create(
                new Observable.OnSubscribe<T>() {
                    @Override
                    public void call(Subscriber<? super T> subscriber) {
                        try {
                            subscriber.onNext(func.call());
                        } catch (Exception ex) {
                            Timber.e("Error reading from the database", ex);
                        }
                    }
                });
    }
}
