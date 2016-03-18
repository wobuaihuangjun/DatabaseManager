package com.huangzj.databaseupgrade.mosby;


import com.huangzj.databaseupgrade.BuildConfig;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

/**
 * Created by huangzj on 2016/3/17.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class DemoPresenterTest {
    DemoActivity mainActivity;
    DemoPresenter demoPresenter;

    @Before
    public void setUp() throws Exception {
        mainActivity = Robolectric.setupActivity(DemoActivity.class);

//        mainActivity = Robolectric.buildActivity(DemoActivity.class).create().get();
//        以上代码用于创建一个待测试的DemoActivity，其中的create()会调用onCreate()方法。

        demoPresenter = new DemoPresenter(mainActivity);
        mainActivity.onResume();
    }

    @After
    public void tearDown() throws Exception {
        mainActivity.onPause();
    }

    @Test
    public void testQuery() throws Exception {
        mainActivity.query();
    }

    @Test
    public void testClear() throws Exception {
        mainActivity.clear();
    }

    @Test
    public void testInserty() throws Exception {
        mainActivity.insert();
    }
}