package org.vincentyeh.IMG2PDF.commandline.handler.execution;

import org.vincentyeh.IMG2PDF.commandline.CreateCommand;
import org.vincentyeh.IMG2PDF.commandline.handler.core.ClassHandler;
import org.vincentyeh.IMG2PDF.commandline.handler.core.IHandler;
import org.vincentyeh.IMG2PDF.commandline.handler.core.ListIHandlerRegister;
import org.vincentyeh.IMG2PDF.task.factory.DirlistTaskFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ResourceBundle;

public class CreateHandler extends ClassHandler {

    private final ResourceBundle resourceBundle;

    public CreateHandler(ResourceBundle resourceBundle) {
        super(CreateCommand.class);
        this.resourceBundle = resourceBundle;
    }

    private String message;
    private int exitCode;

    @Override
    public void parse(Exception ex) {
        if (ex instanceof CreateCommand.OverwriteTaskListException) {
            CreateCommand.OverwriteTaskListException exception = (CreateCommand.OverwriteTaskListException) ex;
            message = String.format(getLocaleResource("overwrite"), exception.getFile());
        } else if (ex instanceof CreateCommand.SaveException) {
            CreateCommand.SaveException exception=(CreateCommand.SaveException)ex;
            message = String.format(getLocaleResource("save"),exception.getFile());
        } else if (ex instanceof DirlistTaskFactory.SourceFileException) {
            DirlistTaskFactory.SourceFileException ex1 = (DirlistTaskFactory.SourceFileException) ex;
            if (ex1.getCause() instanceof FileNotFoundException) {
                message=String.format(getLocaleResource("source.not_found"),ex1.getSource());
            } else if (ex1.getCause() instanceof DirlistTaskFactory.EmptyImagesException) {
                message=String.format(getLocaleResource("source.empty_image"),ex1.getSource());
            }else if(ex1.getCause() instanceof DirlistTaskFactory.WrongFileTypeException){
                DirlistTaskFactory.WrongFileTypeException ex2=(DirlistTaskFactory.WrongFileTypeException) ex1.getCause();
                message=String.format(getLocaleResource("source.wrong_type"),getFileTypeResource(ex2.getExpected()),getFileTypeResource(ex2.getValue()),ex1.getSource());
            }
        } else if (ex instanceof DirlistTaskFactory.DirListException) {
            DirlistTaskFactory.DirListException ex1 = (DirlistTaskFactory.DirListException) ex;
            if (ex1.getCause() instanceof FileNotFoundException) {
//                TODO:declare message value
            } else if (ex1.getCause() instanceof DirlistTaskFactory.WrongFileTypeException) {
                DirlistTaskFactory.WrongFileTypeException ex2 = (DirlistTaskFactory.WrongFileTypeException) ex1.getCause();
//                TODO:declare message value
            } else if (ex1.getCause() instanceof IOException) {
//                TODO:declare message value
            }
        }
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
        return resourceBundle.getString("err.execution.create.handler." + key);
    }

    private String getFileTypeResource(DirlistTaskFactory.WrongFileTypeException.Type type){
        return getPublicResource("file.type."+type.toString().toLowerCase());
    }
    private String getPublicResource(String key) {
        return resourceBundle.getString("public." + key);
    }

}
