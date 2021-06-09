package org.vincentyeh.IMG2PDF.commandline.handler.execution;

import org.vincentyeh.IMG2PDF.commandline.CreateCommand;
import org.vincentyeh.IMG2PDF.commandline.handler.ResourceBundleExecutionHandler;
import org.vincentyeh.IMG2PDF.pattern.Handler;
import org.vincentyeh.IMG2PDF.task.factory.DirlistTaskFactory;

import java.io.FileNotFoundException;

public class CreateHandler extends ResourceBundleHandler<String, ResourceBundleExecutionHandler.HandleCondition> {

    public CreateHandler(Handler<String, ResourceBundleExecutionHandler.HandleCondition> handler) {
        super(handler);
    }

    @Override
    public String handle(ResourceBundleExecutionHandler.HandleCondition data) {
        if (data.getClazz().equals(CreateCommand.class)) {
            Exception ex= data.getException();
            String message = ex.getMessage();
            if (ex instanceof CreateCommand.OverwriteTaskListException) {
                CreateCommand.OverwriteTaskListException exception = (CreateCommand.OverwriteTaskListException) ex;
                message = String.format(getLocaleResource("overwrite"), exception.getFile());
            } else if (ex instanceof CreateCommand.SaveException) {
                CreateCommand.SaveException exception = (CreateCommand.SaveException) ex;
                message = String.format(getLocaleResource("save"), exception.getFile());
            } else if (ex instanceof DirlistTaskFactory.SourceFileException) {
                DirlistTaskFactory.SourceFileException ex1 = (DirlistTaskFactory.SourceFileException) ex;
                if (ex1.getCause() instanceof FileNotFoundException) {
                    message = String.format(getLocaleResource("source.not_found"), ex1.getSource());
                } else if (ex1.getCause() instanceof DirlistTaskFactory.EmptyImagesException) {
                    message = String.format(getLocaleResource("source.empty_image"), ex1.getSource());
                } else if (ex1.getCause() instanceof DirlistTaskFactory.WrongFileTypeException) {
                    DirlistTaskFactory.WrongFileTypeException ex2 = (DirlistTaskFactory.WrongFileTypeException) ex1.getCause();
                    message = String.format(getLocaleResource("source.wrong_type"), getFileTypeResource(ex2.getExpected()), getFileTypeResource(ex2.getValue()), ex1.getSource());
                }
            } else if (ex instanceof DirlistTaskFactory.DirListException) {
                DirlistTaskFactory.DirListException ex1 = (DirlistTaskFactory.DirListException) ex;
                if (ex1.getCause() instanceof FileNotFoundException) {
                    message = String.format(getLocaleResource("dirlist.not_found"), ex1.getDirlist());
                } else if (ex1.getCause() instanceof DirlistTaskFactory.WrongFileTypeException) {
                    DirlistTaskFactory.WrongFileTypeException ex2 = (DirlistTaskFactory.WrongFileTypeException) ex1.getCause();
                    message = String.format(getLocaleResource("dirlist.wrong_type"), getFileTypeResource(ex2.getExpected()), getFileTypeResource(ex2.getValue()), ex1.getDirlist());
                }
            }
            return message;
        } else {
            return doNext(data, data.getException().getMessage());
        }
    }

    private String getLocaleResource(String key) {
        return getResourceBundle().getString("err.execution.create.handler." + key);
    }

    private String getFileTypeResource(DirlistTaskFactory.WrongFileTypeException.Type type) {
        return getPublicResource("file.type." + type.toString().toLowerCase());
    }

    private String getPublicResource(String key) {
        return getResourceBundle().getString("public." + key);
    }

}
