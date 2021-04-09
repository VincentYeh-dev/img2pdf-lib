package org.vincentyeh.IMG2PDF.util;

import org.vincentyeh.IMG2PDF.commandline.action.exception.UnrecognizedEnumException;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;

public class FileSorter implements Comparator<File> {
    private final Sortby sortby;
    private final Sequence sequence;

    public FileSorter(Sortby sortby, Sequence sequence) {
        if(sortby==null)
            throw new IllegalArgumentException("sortby==null");
        if(sequence==null)
            throw new IllegalArgumentException("sequence==null");

        this.sortby = sortby;
        this.sequence = sequence;
    }

    @Override
    public int compare(File o1, File o2) {
        switch (sortby) {
            case NAME:
                if (sequence == Sequence.INCREASE)
                    return o1.getName().compareTo(o2.getName());
                else if (sequence == Sequence.DECREASE)
                    return o2.getName().compareTo(o1.getName());
            case DATE:
                if (sequence == Sequence.INCREASE)
                    return (o1.lastModified() - o2.lastModified() > 0) ? 1 : -1;

                else if (sequence == Sequence.DECREASE)
                    return (o1.lastModified() - o2.lastModified() > 0) ? -1 : 1;
            case NUMERIC:
                if (sequence == Sequence.INCREASE)
                    return compareNumeric(o1.getName(), o2.getName());

                else if (sequence == Sequence.DECREASE)
                    return compareNumeric(o2.getName(), o1.getName());

            default:
                throw new RuntimeException("Multiple files need to be sorted by sort and sequence arguments.");
        }
    }

    private int compareNumeric(String ThisStr, String OStr) {
        String noNumThis = ThisStr.replaceAll("[0-9]{1,}", "*");
        String noNumO = OStr.replaceAll("[0-9]{1,}", "*");

        if (noNumThis.equals(noNumO)) {
            int[] a = getNumber(ThisStr);
            int[] b = getNumber(OStr);
            for (int i = 0; i < a.length; i++) {
                int r = a[i] - b[i];
                if (r != 0) {
                    return r;
                }
            }
            return 0;
        } else {
            return ThisStr.compareTo(OStr);
        }
    }

    private int[] getNumber(String str) {
        String s[] = str.split("[^0-9]{1,}");
        ArrayList<Integer> buf = new ArrayList<>();
        for (int i = 0; i < s.length; i++) {
            if (!s[i].isEmpty()) {
                buf.add(Integer.valueOf(s[i]));
            }
        }
        Integer[] result=new Integer[buf.size()];
        buf.toArray(result);
        int[] ints=new int[result.length];
        for (int i=0;i<ints.length;i++){
            ints[i]= result[i];
        }
        return ints;
    }


    public enum Sequence {
        INCREASE, DECREASE;

        public static Sequence getByString(String str) throws UnrecognizedEnumException {
            Sequence[] sequences = Sequence.values();
            for (Sequence sequence : sequences) {
                if (sequence.toString().equals(str))
                    return sequence;
            }
            throw new UnrecognizedEnumException(str, Sequence.class);
        }

    }

    public enum Sortby {
        NAME, DATE, NUMERIC;

        public static Sortby getByString(String str) throws UnrecognizedEnumException {
            Sortby[] sortbys = Sortby.values();
            for (Sortby sortby : sortbys) {
                if (sortby.toString().equals(str))
                    return sortby;
            }
            throw new UnrecognizedEnumException(str, Sortby.class);
        }

    }

}
