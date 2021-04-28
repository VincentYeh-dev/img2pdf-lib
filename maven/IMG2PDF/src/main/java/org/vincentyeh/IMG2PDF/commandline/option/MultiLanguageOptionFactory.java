package org.vincentyeh.IMG2PDF.commandline.option;

import org.apache.commons.cli.Option;
import org.vincentyeh.IMG2PDF.SharedSpace;

public class MultiLanguageOptionFactory {
    private static class MultiLanguageOption extends Option {
        private MultiLanguageOption(String opt, String longOpt, boolean hasArg, String description_res, Object... values) throws IllegalArgumentException {
            super(opt, longOpt, hasArg, String.format(SharedSpace.getResString(description_res), values));
        }
    }

    public static Option getArgumentOption(String opt, String longOpt, String description_res, Object... values) {
        return new MultiLanguageOption(opt, longOpt, true, description_res, values);
    }

    public static Option getOption(String opt, String longOpt, String description_res, Object... values) {
        return new MultiLanguageOption(opt, longOpt, false, description_res, values);
    }


}
