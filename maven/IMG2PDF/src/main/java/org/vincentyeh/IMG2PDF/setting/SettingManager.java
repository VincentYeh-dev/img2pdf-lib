package org.vincentyeh.IMG2PDF.setting;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Properties;

public class SettingManager {

    private static final File setting_file = new File("setting.properties");
    private static final Locale[] supportedLocales = {
            Locale.TRADITIONAL_CHINESE,
            Locale.US
    };
    private static Properties properties = new Properties();

    private SettingManager() {

    }

    public static void load() throws IOException {
        reset();
        if (setting_file.exists() && setting_file.isFile())
            properties.load(new InputStreamReader(new FileInputStream(setting_file), StandardCharsets.UTF_8));

    }

    public static void save() throws IOException {
        properties.store(new OutputStreamWriter(new FileOutputStream(setting_file), StandardCharsets.UTF_8), "Setting");
    }

    public static void reset() {
        properties = new Properties();
        properties.setProperty("language", Locale.ROOT.toLanguageTag());
        properties.setProperty("dirlist-read-charset", "UTF-8");
    }

    public static Charset getDirectoryListCharset() {
        String value = properties.getProperty("dirlist-read-charset");
        return Charset.forName(value);
    }

    public static Locale getLocale() throws LanguageNotSupportException {
        String value = properties.getProperty("language");
        Locale locale = Locale.forLanguageTag(value);
        checkLanguageSupport(locale);
        return locale;
    }

    private static void checkLanguageSupport(Locale target) throws LanguageNotSupportException {
        boolean found = target.equals(Locale.ROOT);

        for (Locale locale : supportedLocales) {
            if (target.equals(locale)) {
                found = true;
                break;
            }
        }
        if (!found)
            throw new LanguageNotSupportException(target + " not supported");
    }

    static class LanguageNotSupportException extends RuntimeException {
        public LanguageNotSupportException(String message) {
            super(message);
        }
    }
}
