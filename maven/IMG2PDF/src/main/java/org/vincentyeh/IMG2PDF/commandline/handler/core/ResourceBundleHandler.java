package org.vincentyeh.IMG2PDF.commandline.handler.core;

import org.vincentyeh.IMG2PDF.pattern.Handler;
import org.vincentyeh.IMG2PDF.util.file.exception.WrongFileTypeException;

import java.util.ResourceBundle;

public abstract class ResourceBundleHandler<R> extends Handler<String, R> {
    private static ResourceBundle resourceBundle;

    private final String tag;
    public ResourceBundleHandler(Handler<String, R> next, String tag) {
        super(next);
        this.tag = tag;
    }

    protected String getPublicString(String key){
        return resourceBundle.getString("public."+key);
    }

    protected String getFileTypeString(WrongFileTypeException.Type type) {
        return getPublicString("file.type." + type.toString().toLowerCase());
    }

    protected String getLocaleString(String key){
        return resourceBundle.getString("handler."+tag+"."+key);
    }

    public static void setResourceBundle(ResourceBundle resourceBundle) {
        ResourceBundleHandler.resourceBundle = resourceBundle;
    }
}
