package com.huangzj.databaseupgrade.mvp.base;

import android.app.Activity;
import android.os.Bundle;

import butterknife.ButterKnife;

/**
 * Created by huangzj on 2016/3/15.
 */
public abstract class MvpActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        injectViews();
    }

    protected abstract int getLayoutId();

    private void injectViews() {
        ButterKnife.bind(this);
    }

}
