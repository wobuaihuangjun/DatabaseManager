package com.hzj.database.ormlite;

import android.text.TextUtils;
import android.util.LruCache;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

/**
 * Created by hzj on 2016/3/2.
 * <p/>
 * 数据库查询后的缓存数据
 */
public class DbCache {

    private static final String TAG = "DbCache";

    /**
     * 最大缓存的查询记录条数
     */
    private static final int CACHE_SIZE = 100;

    /**
     * 所有缓存的总集，以表名为key，value为对应数据表下，所有查询条件的数据总集，每条查询对应的数据以json格式保存
     */
    private LruCache<String, Map<String, String>> mLruCache;

    private static DbCache mInstance;

    private DbCache() {
    }

    public static DbCache getInstance() {
        if (mInstance == null) {
            synchronized (DbCache.class) {
                if (mInstance == null) {
                    mInstance = new DbCache();
                }
            }
        }
        return mInstance;
    }

    private synchronized void initCache() {
        mLruCache = new LruCache<String, Map<String, String>>(CACHE_SIZE) {
            @Override
            protected int sizeOf(String key, Map<String, String> value) {
                // 暂时用记录的条数来缓存
                return value.size();
            }
        };
    }

    /**
     * 添加某一表，指定查询条件的缓存
     *
     * @param tableName 表名
     * @param query     查询条件对应的关键字
     * @param value     要缓存的数据
     * @param <T>
     */
    public <T> void addCache(String tableName, String query, T value) {
        if (mLruCache == null) {
            initCache();
        }
        if (inValid(tableName) || inValid(query) || value == null) {
            Timber.i(TAG, "value is null");
            return;
        }
        Map<String, String> tableCache = mLruCache.remove(tableName);
        if (tableCache == null) {
            tableCache = new HashMap<>();
//            tableCache.put(query, JSONUtil.toJSON(value));
        } else {
//            tableCache.put(query, JSONUtil.toJSON(value));
        }
        // TODO: 2016/10/9  JSONUtil工具尚未补充
        // 必须先remove,然后在put，否则mLruCache.size更新you问题
        mLruCache.put(tableName, tableCache);
    }

    /**
     * 获取某一表，指定查询条件的缓存
     *
     * @param tableName 表名
     * @param query     查询条件对应的关键字
     * @return
     */
    public String getCache(String tableName, String query) {
        if (inValid(tableName) || inValid(query)) {
            Timber.i(TAG, "mLruCache is empty");
            return null;
        }

        Map<String, String> tableCache = mLruCache.get(tableName);
        if (tableCache == null || tableCache.isEmpty()) {
            Timber.i(TAG, "tableCache is empty");
            return null;
        }
        return tableCache.get(query);
    }

    /**
     * 删除某张表下，对应查询条件的缓存
     *
     * @param tableName 表名
     * @param query     查询条件对应的关键字
     */
    public void clearByQuery(String tableName, String query) {
        if (inValid(tableName) || inValid(query)) {
            return;
        }
        Map<String, String> tableCache = mLruCache.get(tableName);
        if (tableCache != null) {
            tableCache.remove(query);
        }
    }

    /**
     * 删除对应表的缓存
     *
     * @param tableName 表名
     */
    public void clearByTable(String tableName) {
        if (inValid(tableName)) {
            return;
        }
        mLruCache.remove(tableName);
    }

    /**
     * 清除所有缓存
     */
    public void clearAll() {
        if (mLruCache != null) {
            if (mLruCache.size() > 0) {
                mLruCache.evictAll();
            }
        }
        System.gc();
    }

    /**
     * 检测key是否可用
     *
     * @param key
     * @return true：可用；false；不可用
     */
    private boolean inValid(String key) {
        if (mLruCache == null) {
            return true;
        }
        return TextUtils.isEmpty(key);
    }

}
