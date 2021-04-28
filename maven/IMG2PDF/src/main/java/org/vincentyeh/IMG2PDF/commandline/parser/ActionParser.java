package org.vincentyeh.IMG2PDF.commandline.parser;

import org.vincentyeh.IMG2PDF.SharedSpace;
import org.vincentyeh.IMG2PDF.util.FileChecker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public abstract class ActionParser<T> {
    public abstract T parse(String[] arguments)throws Exception;

    protected String listStringArray(String[] values) {
        StringBuilder sb = new StringBuilder();
        sb.append(values[0]);
        for (int i = 1; i < values.length; i++) {
            sb.append(",");
            sb.append(values[i]);
        }
        return sb.toString();
    }

    protected <J> String[] ArrayToStringArray(J[] arr1) {
        String[] string_array = new String[arr1.length];
        for (int i = 0; i < arr1.length; i++) {
            string_array[i] = arr1[i].toString();
        }
        return string_array;
    }

    protected File[] verifyFiles(String[] strSources) throws IOException {
        if (strSources == null) {
            throw new IllegalArgumentException("strSources==null");
        }
        if (strSources.length == 0) {
            throw new IllegalArgumentException("strSources is empty");
        }

//        TODO:Add to language pack.
        System.out.println(("Verifying files"));

        ArrayList<File> verified_sources = new ArrayList<>();
        for (String str_source : strSources) {
            File raw = (new File(str_source)).getAbsoluteFile();
            System.out.printf("\t[" + SharedSpace.getResString("public.info.verifying") + "] %s\r", raw.getAbsolutePath());

            FileChecker.checkReadableFile(raw);

            System.out.printf("\t[" + SharedSpace.getResString("public.info.verified") + "] %s\r\n",
                    raw.getAbsolutePath());

            verified_sources.add(raw);
        }

        File[] sources = new File[verified_sources.size()];
        verified_sources.toArray(sources);
        return sources;
    }

}
