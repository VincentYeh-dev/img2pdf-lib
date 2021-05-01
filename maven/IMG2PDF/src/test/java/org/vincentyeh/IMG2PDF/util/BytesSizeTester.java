package org.vincentyeh.IMG2PDF.util;

import org.junit.Assert;
import org.junit.Test;

public class BytesSizeTester {
    @Test
    public void testKB(){
        long a=BytesSize.valueOf("1024KB").getBytes();
        Assert.assertEquals(1048576L,a);
    }
    @Test
    public void testMB(){
        long a=BytesSize.valueOf("1024MB").getBytes();
        Assert.assertEquals(1073741824,a);
    }
    @Test
    public void testGB(){
        long a=BytesSize.valueOf("9GB").getBytes();
        Assert.assertEquals(9663676416L,a);
    }
    @Test(expected = RuntimeException.class)
    public void testWrongSuffix(){
        long a=BytesSize.valueOf("9AB").getBytes();
    }

}
