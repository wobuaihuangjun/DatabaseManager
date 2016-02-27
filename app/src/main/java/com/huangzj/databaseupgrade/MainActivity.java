package com.huangzj.databaseupgrade;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.huangzj.databaseupgrade.dao.bean.City;
import com.huangzj.databaseupgrade.dao.bean.CityDao;
import com.huangzj.databaseupgrade.util.UUIDUtil;

import java.util.List;

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
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                initData();
                initView();
            }
        });
        cityDao = new CityDao(this);
        initView();
    }

    CityDao cityDao;

    private void initView() {
        TextView textView = (TextView) findViewById(R.id.text_city);
        textView.setText(getData());
    }

    private String getData() {
        List<City> list = cityDao.queryForAll();
        StringBuilder sb = new StringBuilder("查询结果：\n");
        if (list == null || list.size() <= 0) {
            sb.append("空");
            return sb.toString();
        }
        sb.append("查询到的总条数").append(list.size()).append("\n\n");
        sb.append("第一条记录为：\n");
        sb.append(list.get(0).toString()).append("\n\n");
        sb.append("最后一条记录为：\n");
        sb.append(list.get(list.size() - 1).toString()).append("\n\n");
        return sb.toString();
    }

    private void initData() {
        String cityUuid = UUIDUtil.getUUID();
        City city = new City();
        city.setProvinceName("广东省");
        city.setCityName("东莞市");
        city.setCityNo(cityUuid);

        cityDao.insert(city);
    }

}
