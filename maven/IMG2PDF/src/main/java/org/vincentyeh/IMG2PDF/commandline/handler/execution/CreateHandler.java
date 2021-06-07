package org.vincentyeh.IMG2PDF.commandline.handler.execution;

import org.vincentyeh.IMG2PDF.commandline.CreateCommand;
import org.vincentyeh.IMG2PDF.commandline.handler.core.ClassHandler;
import org.vincentyeh.IMG2PDF.task.factory.DirlistTaskFactory;

import java.io.FileNotFoundException;
import java.io.IOException;

public class CreateHandler extends ClassHandler {

    public CreateHandler() {
        super(CreateCommand.class);
    }

    private String message;
    private int exitCode;

    @Override
    public void parse(Exception ex) {
        if (ex instanceof CreateCommand.OverwriteTaskListException) {
            CreateCommand.OverwriteTaskListException exception = (CreateCommand.OverwriteTaskListException) ex;
            message = String.format("Overwrite deny: %s", exception.getFile());
        } else if (ex instanceof CreateCommand.SaveException) {
//            TODO:Print error message;
        } else if (ex instanceof DirlistTaskFactory.SourceFileException) {
            DirlistTaskFactory.SourceFileException ex1 = (DirlistTaskFactory.SourceFileException) ex;
            if (ex1.getCause() instanceof FileNotFoundException) {
//            TODO:Print error message;
            } else if (ex1.getCause() instanceof DirlistTaskFactory.EmptyImagesException) {

//            TODO:Print error message;

            }
        } else if (ex instanceof DirlistTaskFactory.DirListException) {
            DirlistTaskFactory.DirListException ex1 = (DirlistTaskFactory.DirListException) ex;
            if (ex1.getCause() instanceof FileNotFoundException) {
                message = ex1.getCause().getMessage();
            } else if (ex1.getCause() instanceof DirlistTaskFactory.WrongFileTypeException) {
                DirlistTaskFactory.WrongFileTypeException ex2 = (DirlistTaskFactory.WrongFileTypeException) ex1.getCause();
                message = ex1.getCause().getMessage();
            } else if (ex1.getCause() instanceof IOException) {
                message = ex1.getCause().getMessage();
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
}
