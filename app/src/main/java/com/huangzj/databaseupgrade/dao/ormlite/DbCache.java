package com.huangzj.databaseupgrade.dao.ormlite;

import android.support.v4.util.LruCache;

import com.huangzj.databaseupgrade.util.JSONUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by huangzj on 2016/3/2.
 * <p/>
 * 数据库查询后的缓存数据
 */
public class DbCache {

    /**
     * 最大缓存的查询记录条数
     */
    private static final int CACHE_SIZE = 100;

    /**
     * 所有缓存的总集，以表名为key，value为对应数据表下，所有查询条件的数据总集，每条查询对应的数据以json格式保存
     */
    private static Map<String, Map<String, String>> cacheData = new HashMap<>();

    private static LruCache<String, Map<String, String>> mLruCache;

    public static <T> void addCache(String tableName, String query, T value) {
        if (mLruCache == null) {
            initCache();
        }

        if (cacheData == null) {
            cacheData = new HashMap<>();
        }

        Map<String, String> tableCache;
        if (cacheData.containsKey(tableName)) {
            tableCache = cacheData.remove(tableName);
        } else {
            System.out.println("--------don't have tableCache");
            tableCache = new HashMap<>();
        }

        if (tableCache.containsKey(query)) {
            System.out.println("--------already have this query");
            tableCache.remove(query);
        }
        tableCache.put(query, JSONUtil.toJSON(value));
        cacheData.put(tableName, tableCache);
    }

    private static void initCache() {
        mLruCache = new LruCache<String, Map<String, String>>(CACHE_SIZE) {
            @Override
            protected int sizeOf(String key, Map<String, String> value) {
                return value.size();
            }

        };
    }

    public static String getCache(String tableName, String query) {
        if (cacheData == null || !cacheData.containsKey(tableName)) {
            System.out.println("--------this cacheData is empty");
            return null;
        }

        Map<String, String> tableCache = cacheData.get(tableName);
        if (tableCache == null || tableCache.isEmpty()) {
            System.out.println("--------tableCache is empty");
            return null;
        }
        if (tableCache.containsKey(query)) {
            return tableCache.get(query);
        }
        return null;
    }

    public static void clearByQuery(String tableName, String query) {
        if (cacheData == null) {
            return;
        }
        if (cacheData.containsKey(tableName)
                && cacheData.get(tableName).containsKey(query)) {
            cacheData.get(tableName).remove(query);
        }
    }

    public static void clearByTable(String tableName) {
        if (cacheData == null) {
            return;
        }
        if (cacheData.containsKey(tableName)) {
            cacheData.remove(tableName);
        }
    }

    public void clearAll() {
        if (cacheData != null) {
            cacheData.clear();
        }
    }

}
