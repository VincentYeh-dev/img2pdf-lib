package org.vincentyeh.IMG2PDF.util;

import org.vincentyeh.IMG2PDF.commandline.action.exception.UnrecognizedEnumException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BytesSize {
    private final long bytes;

    private BytesSize(int number, Suffix suffix) {
        bytes = number * suffix.getBytes();
    }

    public enum Suffix {
        B(1L), KB(1024L), MB(1048576L), GB(1073741824);

        private final long bytes;

        Suffix(long bytes) {
            this.bytes = bytes;
        }

        public long getBytes() {
            return bytes;
        }

        public static Suffix getInstance(String raw) {
            Suffix[] values = values();
            for (Suffix value : values) {
                if (value.toString().equals(raw)) {
                    return value;
                }
            }
            throw new UnrecognizedEnumException(raw, Suffix.class);
        }
    }

    public static BytesSize parseString(String raw) {
        Pattern pattern = Pattern.compile("([0-9]+)(B|KB|MB|GB)");
        Matcher matcher = pattern.matcher(raw);
        if (matcher.find()) {
            return new BytesSize(Integer.parseInt(matcher.group(1)),Suffix.getInstance(matcher.group(2)));
        }
//        TODO:Write error message
        throw new RuntimeException();
    }

    public long getBytes() {
        return bytes;
    }
}
