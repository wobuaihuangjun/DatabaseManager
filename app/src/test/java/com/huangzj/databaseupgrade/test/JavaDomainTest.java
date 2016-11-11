package com.huangzj.databaseupgrade.test;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by huangzj on 2016/3/17.
 */
public class JavaDomainTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testA() throws Exception {
        String result = JavaDomain.a(-1);
        Assert.assertEquals(result, "b");

        String result1 = JavaDomain.a(1);
        Assert.assertEquals(result1, "a");

        JavaDomain javaDomain = new JavaDomain();
        Assert.assertEquals(javaDomain.getInt(), 2);
    }
}