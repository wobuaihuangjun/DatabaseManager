package com.huangzj.databaseupgrade.mosby;

import android.test.AndroidTestCase;

/**
 * Created by huangzj on 2016/3/17.
 */
public class DemoPresenterTest extends AndroidTestCase {

    DemoPresenter demoPresenter;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        demoPresenter = new DemoPresenter(getContext());
        demoPresenter.subscribe();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        demoPresenter.unsubscribe();
    }

    public void testInsert() throws Exception {
        demoPresenter.insert();
    }

    public void testQuery() throws Exception {

    }

    public void testClear() throws Exception {

    }
}