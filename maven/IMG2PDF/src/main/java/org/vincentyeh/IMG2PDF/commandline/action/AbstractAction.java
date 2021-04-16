package org.vincentyeh.IMG2PDF.commandline.action;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.vincentyeh.IMG2PDF.SharedSpace;
import org.vincentyeh.IMG2PDF.util.FileChecker;

public abstract class AbstractAction implements Action {
    protected Options options;

    public AbstractAction(Options options) {
        this.options = options;
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
            System.out.printf("\t[" + SharedSpace.getResString("public.info.verifying") + "] %s\n", raw.getAbsolutePath());

            System.out.print("\t");

            FileChecker.checkReadableFile(raw);

            System.out.printf("[" + SharedSpace.getResString("public.info.verified") + "] %s\n",
                    raw.getAbsolutePath());

            verified_sources.add(raw);
        }

        File[] sources = new File[verified_sources.size()];
        verified_sources.toArray(sources);
        return sources;
    }

    protected static <T> String listEnum(Class<T> _class_enum) {
        T[] enums = _class_enum.getEnumConstants();
        StringBuilder sb = new StringBuilder();
        sb.append(enums[0].toString());
        for (int i = 1; i < enums.length; i++) {
            sb.append(",");
            sb.append(enums[i].toString());
        }
        return sb.toString();
    }
}
