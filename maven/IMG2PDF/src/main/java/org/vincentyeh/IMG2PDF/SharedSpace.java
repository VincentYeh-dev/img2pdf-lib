package org.vincentyeh.IMG2PDF;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;


public class SharedSpace {
    private static final Locale[] supportedLocales = {
            Locale.TRADITIONAL_CHINESE,
            Locale.ENGLISH
    };

    public static class Constance {
        public static String PROGRAM_NAME = "IMG2PDF";
        public static String PROGRAM_VER = "0.0.1-SNAPSHOT";
    }

    public static class Configuration {
        static Locale locale;
        public static Charset TASKLIST_READ_CHARSET;
        public static Charset TASKlIST_WRITE_CHARSET;
        public static Charset DIRLIST_READ_CHARSET;

    }

    private static ResourceBundle language_resource;
    public static void initialize(){
        Properties properties = new Properties();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("config.properties"), StandardCharsets.UTF_8));
            properties.load(reader);
            Configuration.TASKLIST_READ_CHARSET=getTaskListReadCharsetFromProperties(properties);
            Configuration.TASKlIST_WRITE_CHARSET=getTaskListWriteCharsetFromProperties(properties);
            Configuration.DIRLIST_READ_CHARSET=getDirListReadCharsetFromProperties(properties);
            Configuration.locale=getLocaleFromProperties(properties);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }

        language_resource = ResourceBundle.getBundle("language_package", getLanguageSupport(Configuration.locale));
    }


    private static Charset getDirListReadCharsetFromProperties(Properties properties) {
        return getCharsetFromProperties("dirlist-read-charset",properties);
    }
    
    private static Charset getTaskListWriteCharsetFromProperties(Properties properties) {
        return getCharsetFromProperties("tasklist-write-charset",properties);
    }

    private static Charset getTaskListReadCharsetFromProperties(Properties properties) {
        return getCharsetFromProperties("tasklist-read-charset",properties);
    }

    private static Charset getCharsetFromProperties(String option,Properties properties){
        String charset = properties.getProperty(option);
        if (charset == null || charset.isEmpty()) {
            System.err.printf("Option \"%s\" not found.\nUse default charset:UTF-8.\n",option);
            return StandardCharsets.UTF_8;
        }
        try {
            return Charset.forName(charset);
        } catch (UnsupportedCharsetException e) {
            System.err.printf("Option \"%s\" not support:%s.\nUse default charset:UTF-8\n", option);
            return StandardCharsets.UTF_8;
        }
    }


    private static Locale getLocaleFromProperties(Properties properties) {
        String language = properties.getProperty("language");
        if (language == null || language.isEmpty()) {
            System.err.println("Option \"language\" not found.\nUse default language.");
            return Locale.ROOT;
        }
        return Locale.forLanguageTag(language);
    }


    private static Locale getLanguageSupport(Locale target) {
        for (Locale locale : supportedLocales) {
            if (target.equals(locale)) {
                return locale;
            }
        }
        System.err.printf("Option \"language\" locale not support:%s.\nUse default language.\n", target.toLanguageTag());
        return Locale.ROOT;
    }


    public static String getResString(String key) {
        return SharedSpace.language_resource.getString(key);
    }

    public static ResourceBundle getLanguageResourceBundle() {
        return language_resource;
    }
}
