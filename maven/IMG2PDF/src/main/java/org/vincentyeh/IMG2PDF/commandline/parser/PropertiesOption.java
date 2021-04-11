package org.vincentyeh.IMG2PDF.commandline.parser;

import org.apache.commons.cli.Option;
import org.vincentyeh.IMG2PDF.Configuration;

public class PropertiesOption extends Option {
//    private PropertiesOption(String opt, String description_res, Object... values) throws IllegalArgumentException {
//        super(opt, String.format(Configuration.getResString(description_res), values));
//    }
//
//    private PropertiesOption(String opt, boolean hasArg, String description_res, Object... values) throws IllegalArgumentException {
//        super(opt, hasArg, String.format(Configuration.getResString(description_res), values));
//    }

    private PropertiesOption(String opt, String longOpt, boolean hasArg, String description_res,Object... values) throws IllegalArgumentException {
        super(opt,longOpt, hasArg, String.format(Configuration.getResString(description_res), values));
    }

    public static PropertiesOption getArgumentOption(String opt, String longOpt, String description_res,Object... values){
        return new PropertiesOption(opt,longOpt,true,description_res,values);
    }
    public static PropertiesOption getOption(String opt, String longOpt, String description_res,Object... values){
        return new PropertiesOption(opt,longOpt,false,description_res,values);
    }


}
