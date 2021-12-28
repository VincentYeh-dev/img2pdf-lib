package org.vincentyeh.IMG2PDF.commandline.converter;

import org.vincentyeh.IMG2PDF.commandline.converter.core.BasicCheckConverter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ByteSizeConverter extends BasicCheckConverter<Long> {

    private enum Suffix {
        B(1L), KB(1024L), MB(1048576L), GB(1073741824);

        private final long bytes;

        Suffix(long bytes) {
            this.bytes = bytes;
        }

    }

    @Override
    protected Long doConvert(String raw) {
        Pattern pattern = Pattern.compile("([0-9]+)(B|KB|MB|GB)");
        Matcher matcher = pattern.matcher(raw);
        if (matcher.find()) {
            if (matcher.group(1) == null)
                throw new IllegalArgumentException("Invalid number format:" + raw);
            if (matcher.group(2) == null)
                throw new IllegalArgumentException("Invalid suffix format:" + raw);

            long suffix_bytes_count = Suffix.valueOf(matcher.group(2)).bytes;
            long number_bytes_count = Integer.parseInt(matcher.group(1));

            return suffix_bytes_count * number_bytes_count;
        }
        throw new IllegalStateException("Invalid state");
    }
}
