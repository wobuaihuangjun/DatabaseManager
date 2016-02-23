package com.huangzj.databaseupgrade.dao;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lhd on 2015/10/19.
 */
public class DaoObserver {

    private static List<DaoListener> listeners = new ArrayList<>();

    private static Handler uiHandler = new Handler(Looper.getMainLooper());

    private static Handler asyncHandler;

    private static HandlerThread handlerThread;

    static {
        handlerThread = new HandlerThread("backgroud_thread");
        handlerThread.start();
        asyncHandler = new Handler(handlerThread.getLooper());
    }

    public static void regist(DaoListener daoListener) {
        daoListener.setDaoThreadMode(DaoThreadMode.MainThread);
        regist(daoListener, DaoThreadMode.MainThread);
    }

    public static void regist(DaoListener daoListener, DaoThreadMode daoThreadMode) {
        daoListener.setDaoThreadMode(daoThreadMode);
        listeners.add(daoListener);
    }

    public static void unRegist(DaoListener daoListener) {
        listeners.remove(daoListener);
    }

    /**
     * 回调将保证在主线程中执行，禁止执行耗时任务，若执行耗时任务则要另开线程
     *
     * @param data
     */
    public static synchronized void publish(int daoOperationType, Object data) {
        notifyDataChanged(daoOperationType, data);
    }

    private static void notifyDataChanged(final int daoOperationType, final Object data) {
        for (final DaoListener daoListener : listeners) {
            DaoThreadMode daoThreadMode = daoListener.getDaoThreadMode();
            if (daoThreadMode == DaoThreadMode.MainThread) {
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        onDataChanged(daoListener, daoOperationType, data);
                    }
                });
            } else if (daoThreadMode == DaoThreadMode.BackgroundThread) {
                if (Looper.myLooper() == Looper.getMainLooper()) {
                    asyncHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            onDataChanged(daoListener, daoOperationType, data);
                        }
                    });
                } else {
                    onDataChanged(daoListener, daoOperationType, data);
                }
            } else if (daoThreadMode == DaoThreadMode.Async) {
                asyncHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        onDataChanged(daoListener, daoOperationType, data);
                    }
                });
            } else {
                // PostThread线程模式
                onDataChanged(daoListener, daoOperationType, data);
            }
        }
    }

    private static void onDataChanged(DaoListener daoListener, int daoOperationType, Object data) {
        daoListener.onDataChanged(daoOperationType, data);
        daoListener.onDataChanged(data);
    }
}
