package org.vincentyeh.IMG2PDF.util;

import org.junit.Assert;
import org.junit.Test;
import org.vincentyeh.IMG2PDF.commandline.action.exception.UnrecognizedEnumException;

public class BytesSizeTester {
    @Test
    public void testKB(){
        long a=BytesSize.parseString("1024KB").getBytes();
        Assert.assertEquals(1048576L,a);
    }
    @Test
    public void testMB(){
        long a=BytesSize.parseString("1024MB").getBytes();
        Assert.assertEquals(1073741824,a);
    }
    @Test
    public void testGB(){
        long a=BytesSize.parseString("9GB").getBytes();
        Assert.assertEquals(9663676416L,a);
    }
    @Test(expected = RuntimeException.class)
    public void testWrongSuffix(){
        long a=BytesSize.parseString("9AB").getBytes();
    }

}
