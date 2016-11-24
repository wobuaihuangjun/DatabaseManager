package com.huangzj.databaseupgrade.mosby;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.hannesdorfmann.mosby.mvp.MvpActivity;
import com.huangzj.databaseupgrade.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by huangzj on 2016/3/17.
 */
public class DemoActivity extends MvpActivity<DemoView, DemoPresenter> implements DemoView {

    @Bind(R.id.text_city)
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.subscribe();
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.unsubscribe();
    }

    @OnClick(R.id.insert)
    void insert() {
        presenter.insert();
    }

    @OnClick(R.id.query)
    void query() {
        presenter.query();
    }

    @OnClick(R.id.clear)
    void clear() {
        presenter.clear();
    }

    @OnClick(R.id.update)
    void update() {
        presenter.update();
    }

    @NonNull
    @Override
    public DemoPresenter createPresenter() {
        return new DemoPresenter(this);
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
