package com.huangzj.databaseupgrade;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by huangzj on 2016/3/2.
 */
public class Test {

    public static void main(String[] args) {

//        test1();
        test2();
    }

    private static void test2() {
        Map<String, String> map = new HashMap<>();
        map.put("aaa", "bbb");
        System.out.println(map.toString());
        map.put("aaa", "bbb");
        System.out.println(map.toString());
        map.put("aaa", "ccc");
        System.out.println(map.toString());
        map.put("aa", "cc");
        System.out.println(map.toString());
    }

    private static void test1() {

//        DbCache.addCache("aaa", "bbb", "ccc");
//
//        System.out.println(DbCache.getCache("aaa", "bbb"));
    }


}
