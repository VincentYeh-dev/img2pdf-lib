package org.vincentyeh.IMG2PDF.handler;

import org.vincentyeh.IMG2PDF.handler.concrete.TaskFactoryProcessExceptionHandler;
import org.vincentyeh.IMG2PDF.handler.concrete.PDFConversionExceptionHandler;
import org.vincentyeh.IMG2PDF.handler.concrete.ParameterExceptionHandler;
import org.vincentyeh.IMG2PDF.handler.framework.ExceptionHandler;

import java.util.ResourceBundle;

public class ExceptionHandlerFacade {
    private static ResourceBundle resourceBundle;

    private ExceptionHandlerFacade() {

    }

    public static ParameterExceptionHandler getParameterExceptionHandler(ExceptionHandler next) {
        return new ParameterExceptionHandler(next, resourceBundle);
    }

    public static PDFConversionExceptionHandler getPDFConversionExceptionHandler(ExceptionHandler next) {
        return new PDFConversionExceptionHandler(next, resourceBundle);
    }

    public static ExceptionHandler getTaskFactoryProcessExceptionHandler(ExceptionHandler next) {
        return new TaskFactoryProcessExceptionHandler(next, resourceBundle);
    }

    public static void setResourceBundle(ResourceBundle resourceBundle) {
        ExceptionHandlerFacade.resourceBundle = resourceBundle;
    }
}
