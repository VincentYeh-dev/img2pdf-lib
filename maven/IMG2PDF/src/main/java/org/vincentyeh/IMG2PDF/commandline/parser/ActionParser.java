package org.vincentyeh.IMG2PDF.commandline.parser;

import org.apache.commons.cli.Options;
import org.vincentyeh.IMG2PDF.SharedSpace;
import org.vincentyeh.IMG2PDF.commandline.action.Action;
import org.vincentyeh.IMG2PDF.commandline.parser.core.HandledException;
import org.vincentyeh.IMG2PDF.util.file.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class ActionParser<T extends Action> {

    protected final Options options = new Options();

    public abstract T parse(String[] arguments)throws Exception;

    protected String listEnum(Class<? extends Enum<?>> _class){
        return Arrays.stream(_class.getEnumConstants()).map(Enum::toString).collect(Collectors.joining(","));
    }

    protected <K extends Enum<K>> K getValueOfEnum(Class<K> _enum, String value) throws HandledException {
        try {
            return Enum.valueOf(_enum,value);
        } catch (IllegalArgumentException e) {
            System.err.printf(SharedSpace.getResString("public.err.unrecognizable_enum_long") + "\n", value, _enum.getSimpleName(), Arrays.stream(_enum.getEnumConstants()).map(Objects::toString).collect(Collectors.joining(",")));
            throw new HandledException(e, getClass());
        }
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

            FileUtils.checkIsFile(raw);
            FileUtils.checkAbsolute(raw);
            FileUtils.checkExists(raw);

            System.out.printf("\t[" + SharedSpace.getResString("public.info.verified") + "] %s\r\n",
                    raw.getAbsolutePath());

            verified_sources.add(raw);
        }

        File[] sources = new File[verified_sources.size()];
        verified_sources.toArray(sources);
        return sources;
    }

}
