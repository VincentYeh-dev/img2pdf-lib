package org.vincentyeh.IMG2PDF.commandline.handler.execution;

import org.vincentyeh.IMG2PDF.pattern.Handler;

import java.util.ResourceBundle;

public abstract class ResourceBundleHandler<T,R> extends Handler<T,R> {
    private static ResourceBundle resourceBundle;

    public ResourceBundleHandler(Handler<T, R> next) {
        super(next);
    }

    public static ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    public static void setResourceBundle(ResourceBundle resourceBundle) {
        ResourceBundleHandler.resourceBundle = resourceBundle;
    }
}
