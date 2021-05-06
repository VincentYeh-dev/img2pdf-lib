package org.vincentyeh.IMG2PDF;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.ResourceBundle;


public class SharedSpace {
    private static final Locale[] supportedLocales = {
            Locale.TRADITIONAL_CHINESE,
            Locale.ENGLISH
    };

    public static class Configuration {
        public static final String PROGRAM_NAME = "IMG2PDF";
        public static final String PROGRAM_VER = "0.0.1-SNAPSHOT";
        public static final String DEVELOPER = "VincentYeh-dev";
        public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    }

    private static Locale locale;
    private static ResourceBundle language_resource = null;

    static {
        setLocale(Locale.getDefault());
        updateLanguageResource();
    }

    public static void setLocale(Locale locale) {
        SharedSpace.locale = locale;
    }

    public static void updateLanguageResource() {
        SharedSpace.setLanguageRes(ResourceBundle.getBundle("language_package", getLanguageSupport(locale)));

    }

    private static Locale getLanguageSupport(Locale target){
        for(Locale locale:supportedLocales){
            if(target.equals(locale)){
                return locale;
            }
        }
        return Locale.ROOT;
    }

    public static void setLanguageRes(ResourceBundle LanguageRes) {
        SharedSpace.language_resource = LanguageRes;
    }


    public static String getResString(String key) {
        return SharedSpace.language_resource.getString(key);
    }


}
