package com.huangzj.databaseupgrade;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.huangzj.databaseupgrade.dao.DbCallBack;
import com.huangzj.databaseupgrade.dao.bean.City;
import com.huangzj.databaseupgrade.dao.bean.CityDao;
import com.huangzj.databaseupgrade.util.UUIDUtil;

import java.util.List;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initData();
                getData();
            }
        });
        cityDao = new CityDao(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    CityDao cityDao;

    private void getData() {
        cityDao.queryForAllSync(new DbCallBack() {
            @Override
            public void onSuccess(Object data) {
                Timber.d("收到数据变更通知");
                updateView((List<City>) data);
            }

            @Override
            public void onError(Throwable throwable) {

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
            public void onSuccess(Object data) {
                Timber.d("---------sync insert success");
            }

            @Override
            public void onError(Throwable throwable) {
                Timber.e("---------sync insert fail", throwable);
            }
        });
    }

}
