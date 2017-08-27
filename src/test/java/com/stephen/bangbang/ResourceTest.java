package com.stephen.bangbang;

import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;

public class ResourceTest {

    @Test
    public void testResource() {
        InputStream is = this.getClass().getResourceAsStream("/ehcache.xml");
        Assert.assertNotNull(is);
    }

}
