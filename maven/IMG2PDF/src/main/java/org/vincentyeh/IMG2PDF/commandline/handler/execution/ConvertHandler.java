package org.vincentyeh.IMG2PDF.commandline.handler.execution;

import org.vincentyeh.IMG2PDF.commandline.ConvertCommand;
import org.vincentyeh.IMG2PDF.commandline.handler.core.ClassHandler;
import org.vincentyeh.IMG2PDF.task.factory.DirlistTaskFactory;

import java.util.ResourceBundle;

public class ConvertHandler extends ClassHandler {
    private final ResourceBundle resourceBundle;

    private String message;
    private int exitCode;

    public ConvertHandler(ResourceBundle resourceBundle) {
        super(ConvertCommand.class);
        this.resourceBundle = resourceBundle;
    }

    @Override
    public void parse(Exception e) {
        message=e.getMessage()+"CCCCCCC";
    }

    @Override
    public String getErrorMessage() {
        return message;
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }

    private String getLocaleResource(String key) {
        return resourceBundle.getString("err.execution.convert.handler." + key);
    }

    private String getFileTypeResource(DirlistTaskFactory.WrongFileTypeException.Type type) {
        return getPublicResource("file.type." + type.toString().toLowerCase());
    }

    private String getPublicResource(String key) {
        return resourceBundle.getString("public." + key);
    }

}
