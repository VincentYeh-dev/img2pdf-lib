package org.vincentyeh.IMG2PDF.configuration;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

class DefaultConfiguration implements Configuration{
    @Override
    public Charset getDirectoryListCharset() {
        return StandardCharsets.UTF_8;
    }

    @Override
    public Locale getLocale() {
        return Locale.ROOT;
    }

}
