package org.vincentyeh.IMG2PDF.configuration;

import org.vincentyeh.IMG2PDF.commandline.command.ConvertCommand;

import java.nio.charset.Charset;
import java.util.Locale;

public class ConvertConfigurationAdaptor implements ConvertCommand.Configuration {

    private final Configuration configuration;
    public ConvertConfigurationAdaptor(Configuration configuration) {
        this.configuration=configuration;
    }

    @Override
    public Charset getDirectoryListCharset() {
        return configuration.getDirectoryListCharset();
    }

    @Override
    public Locale getLocale() {
        return configuration.getLocale();
    }


}
