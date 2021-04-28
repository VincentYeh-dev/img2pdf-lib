package org.vincentyeh.IMG2PDF.util;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;

public class FileSorterTester {
    @Test(expected = IllegalArgumentException.class)
    public void testNullArgs() {
        new FileSorter(null, null);

    }

    @Test
    public void testNameSorting(){
        FileSorter sorter=new FileSorter(FileSorter.Sortby.NAME, FileSorter.Sequence.INCREASE);
        File[] files=new File[20];
        for(int i=0;i<files.length;i++){
            files[i]=new File(i+"");
        }
        Arrays.sort(files,sorter);

    }
}
