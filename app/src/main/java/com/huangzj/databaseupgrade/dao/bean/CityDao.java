package com.huangzj.databaseupgrade.dao.bean;

import android.content.Context;

import com.huangzj.databaseupgrade.dao.RxDao;

import java.util.List;

/**
 * 城市数据表操作类
 * <p/>
 * Created by lhd on 2015/10/9.
 */
public class CityDao extends RxDao<City> {

    public CityDao(Context context) {
        super(context, City.class, false);
    }

    public CityDao(Context context, boolean cache) {
        super(context, City.class, cache);
    }

    public boolean createForBatch(List<City> list) {
        return super.insertForBatch(list);
    }

    public List<City> queryByProvinceNo(String provinceNo) {
        return super.queryByColumnName("provinceNo", provinceNo);
    }

    public City queryByCityNo(String cityNo) {
        return super.queryForFirst("cityNo", cityNo);
    }
}
