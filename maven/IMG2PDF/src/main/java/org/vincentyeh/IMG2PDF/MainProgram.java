package org.vincentyeh.IMG2PDF;

import org.vincentyeh.IMG2PDF.commandline.command.ConvertCommand;
import org.vincentyeh.IMG2PDF.commandline.command.CreateCommand;
import org.vincentyeh.IMG2PDF.commandline.command.IMG2PDFCommand;
import org.vincentyeh.IMG2PDF.commandline.handler.ResourceBundleExecutionHandler;
import org.vincentyeh.IMG2PDF.commandline.handler.ResourceBundleParameterHandler;
import picocli.CommandLine;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;


public class MainProgram {

    private static final Locale[] supportedLocales = {
            Locale.TRADITIONAL_CHINESE,
            Locale.ENGLISH
    };

    public static void main(String[] args) {
        Properties properties=getProperties();
        Locale locale=getLanguageSupport(getLocaleFromProperties(properties));
        locale=Locale.TAIWAN;

        ResourceBundle resourceBundle=ResourceBundle.getBundle("cmd",locale);
        CommandLine cmd = new CommandLine(new IMG2PDFCommand());
        cmd.addSubcommand(new CreateCommand(resourceBundle,getCreateConfig(properties)));
        cmd.addSubcommand(new ConvertCommand(resourceBundle,getConvertConfig(properties)));

        cmd.setExecutionExceptionHandler(new ResourceBundleExecutionHandler(resourceBundle));
        cmd.setParameterExceptionHandler(new ResourceBundleParameterHandler(resourceBundle));
        cmd.setResourceBundle(resourceBundle);

        System.exit(cmd.execute(args));
    }

    private static ConvertCommand.Configurations getConvertConfig(Properties properties) {
        return new ConvertCommand.Configurations(getTaskListReadCharsetFromProperties(properties));
    }

    private static Properties getProperties(){
        Properties properties = new Properties();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("config.properties"), StandardCharsets.UTF_8));
            properties.load(reader);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            properties.setProperty("dirlist-read-charset","UTF-8");
            properties.setProperty("tasklist-write-charset","UTF-8");
            properties.setProperty("tasklist-read-charset","UTF-8");
            properties.setProperty("language","en-US");
        }

        return properties;
    }


    private static CreateCommand.Configurations getCreateConfig(Properties properties) {
        return new CreateCommand.Configurations(getTaskListWriteCharsetFromProperties(properties),getDirListReadCharsetFromProperties(properties));
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
        if(!Locale.forLanguageTag(language).toString().isEmpty())
            return Locale.forLanguageTag(language);
        else
            return Locale.ROOT;
    }


    private static Locale getLanguageSupport(Locale target) {
        if(target.equals(Locale.ROOT))
            return target;

        for (Locale locale : supportedLocales) {
            if (target.equals(locale)) {
                return locale;
            }
        }
        System.err.printf("Option \"language\" locale not support:%s.\nUse default language.\n", target.toLanguageTag());
        return Locale.ROOT;
    }
}
