package org.vincentyeh.IMG2PDF;

import org.vincentyeh.IMG2PDF.commandline.command.ConvertCommand;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

public class ConfigurationAgent {
    private static final Locale[] supportedLocales = {
            Locale.TRADITIONAL_CHINESE
    };
    private static Properties properties;
    private static Locale locale;

    public static ResourceBundle getHandlerResourceBundle(){
        return ResourceBundle.getBundle("cmd",locale);
    }

    public static ResourceBundle getCommandResourceBundle(){
        return ResourceBundle.getBundle("cmd",locale);
    }

    public static ConvertCommand.Configurations getConvertConfig() {
        return new ConvertCommand.Configurations(locale,getDirListReadCharsetFromProperties(properties), ResourceBundle.getBundle("cmd",locale));
    }
    public static void loadOrCreateProperties(File file) {
        locale = getLanguageSupport(Locale.getDefault());
        properties = new Properties();
        properties.setProperty("dirlist-read-charset", "UTF-8");
        properties.setProperty("language", locale.toLanguageTag());

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
            properties.load(reader);
            locale = getLanguageSupport(getLocaleFromProperties());
        } catch (FileNotFoundException e) {
            try {
                properties.store(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file))), "This config file is for img2pdf.");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

    }

    private static Locale getLocaleFromProperties() {
        String language = properties.getProperty("language");
        if (language == null || language.isEmpty()) {
            System.err.println("Option \"language\" not found.\nUse default language.");
            return Locale.ROOT;
        }
        if (!Locale.forLanguageTag(language).toString().isEmpty())
            return Locale.forLanguageTag(language);
        else
            return Locale.ROOT;
    }

    private static Charset getDirListReadCharsetFromProperties(Properties properties) {
        String charset = properties.getProperty("dirlist-read-charset");
        if (charset == null || charset.isEmpty()) {
            System.err.printf("Option \"%s\" not found.\nUse default charset:UTF-8.\n", "dirlist-read-charset");
            return StandardCharsets.UTF_8;
        }
        try {
            return Charset.forName(charset);
        } catch (UnsupportedCharsetException e) {
            System.err.printf("Option \"%s\" not support:%s.\nUse default charset:UTF-8\n", "dirlist-read-charset", e.getMessage());
            return StandardCharsets.UTF_8;
        }
    }


    private static Locale getLanguageSupport(Locale target) {
        if (target.equals(Locale.ROOT))
            return target;

        for (Locale locale : supportedLocales) {
            if (target.equals(locale)) {
                return locale;
            }
        }
        System.err.printf("Option \"language\" locale not support:%s.\nUse default language.\n", target.toLanguageTag());
        return Locale.ROOT;
    }

    public static Locale getLocale() {
        return locale;
    }
}
