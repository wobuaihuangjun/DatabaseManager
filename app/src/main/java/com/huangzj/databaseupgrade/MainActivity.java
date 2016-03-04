package com.huangzj.databaseupgrade;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.huangzj.databaseupgrade.dao.ormlite.DbCallBack;
import com.huangzj.databaseupgrade.dao.bean.City;
import com.huangzj.databaseupgrade.dao.bean.CityDao;
import com.huangzj.databaseupgrade.util.UUIDUtil;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        cityDao = new CityDao(this, true);
    }

    @OnClick(R.id.insert)
    void inset() {
        initData();
    }

    @OnClick(R.id.query)
    void query() {
        getData();
    }

    @OnClick(R.id.clear)
    void clear() {
        cityDao.clearTableDataSync(new DbCallBack() {
            @Override
            public void onComplete(Object data) {
                Timber.d("---------sync clear complete--" + data);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cityDao.subscribe();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cityDao.unsubscribe();
    }

    CityDao cityDao;

    private void getData() {
        cityDao.queryForAllSync(new DbCallBack() {
            @Override
            public void onComplete(Object data) {
                Timber.d("---------sync query success");
                updateView((List<City>) data);
            }
        });
    }

    private void updateView(List<City> list) {
        TextView textView = (TextView) findViewById(R.id.text_city);

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

        textView.setText(sb.toString());
    }

    private void initData() {
        String cityUuid = UUIDUtil.getUUID();
        City city = new City();
        city.setProvinceName("广东省");
        city.setCityName("东莞市");
        city.setCityNo(cityUuid);

        cityDao.insertSync(city, new DbCallBack() {
            @Override
            public void onComplete(Object data) {
                Timber.d("---------sync insert complete--" + data);
            }
        });
    }

}
