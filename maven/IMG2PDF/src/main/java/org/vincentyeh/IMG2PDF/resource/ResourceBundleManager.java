package org.vincentyeh.IMG2PDF.resource;

import java.util.Locale;
import java.util.ResourceBundle;

public class ResourceBundleManager {
    private static Locale locale;

    private ResourceBundleManager(){

    }

    public static void setLocale(Locale locale){
        ResourceBundleManager.locale = locale;
    }

    public static ResourceBundle getHandlerResourceBundle(){
        return ResourceBundle.getBundle("handler", locale);
    }

    public static ResourceBundle getCommandResourceBundle(){
        return ResourceBundle.getBundle("cmd",locale);
    }
    public static ResourceBundle getListenerResourceBundle(){
        return ResourceBundle.getBundle("pdf_converter_listener", locale);
    }
}
