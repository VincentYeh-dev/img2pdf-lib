package org.vincentyeh.IMG2PDF.configuration;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Locale;

public class ConfigurationFactory {
    public static Configuration createFromPropertiesFile(File file) throws IOException {
        if(file==null)
            throw new IllegalArgumentException("file==null");
        return new ConfigPropertiesAdaptor(file);
    }

    public static Configuration createDefault(){
        return new DefaultConfiguration();
    }

    public static Configuration createValidOptionFromPropertiesFile(File file) throws IOException {
        if(file==null)
            throw new IllegalArgumentException("file==null");

        Configuration configuration=createFromPropertiesFile(file);
        Configuration default_configuration=createDefault();

        Charset directoryListCharset;
        Locale locale;
        try{
            directoryListCharset=configuration.getDirectoryListCharset();
        }catch (Exception e){
            directoryListCharset=default_configuration.getDirectoryListCharset();
        }

        try{
            locale=configuration.getLocale();
        }catch (Exception e){
            locale=default_configuration.getLocale();
        }

        Charset finalDirectoryListCharset = directoryListCharset;
        Locale finalLocale = locale;
        return new Configuration() {
            @Override
            public Charset getDirectoryListCharset() {
                return finalDirectoryListCharset;
            }

            @Override
            public Locale getLocale() {
                return finalLocale;
            }
        };

    }


}
