package com.huangzj.databaseupgrade.mvp.view;

import android.os.Bundle;
import android.widget.TextView;

import com.huangzj.databaseupgrade.R;
import com.huangzj.databaseupgrade.mvp.presenter.MainPresenter;
import com.huangzj.databaseupgrade.mvp.base.MvpActivity;

import butterknife.Bind;
import butterknife.OnClick;

public class MainActivity extends MvpActivity implements MainView {

    @Bind(R.id.text_city)
    TextView textView;

    private MainPresenter mainPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainPresenter = new MainPresenter(this, this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mainPresenter.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mainPresenter.onSaveInstanceState(outState);
    }

    @OnClick(R.id.insert)
    void insert() {
        mainPresenter.insert();
    }

    @OnClick(R.id.query)
    void query() {
        mainPresenter.query();
    }

    @OnClick(R.id.clear)
    void clear() {
        mainPresenter.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mainPresenter.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mainPresenter.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainPresenter.onDestroy();
        mainPresenter = null;
    }

    @Override
    public void clearView() {
        textView.setText("数据已清空");
    }

    @Override
    public void updateView(String result) {
        textView.setText(result);
    }


}
