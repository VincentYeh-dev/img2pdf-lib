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

        Handler<String, Exception> handler = new SourceFileExceptionHandler(new DirlistExceptionHandler(new OverwriteTaskListHandler(new SaveExceptionHandler(null))));
        if (data.getClazz().equals(CreateCommand.class)) {
            return handler.handle(data.getException());
        } else {
            return doNext(data);
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

    private class SourceFileExceptionHandler extends Handler<String, Exception> {

        public SourceFileExceptionHandler(Handler<String, Exception> next) {
            super(next);
        }

        @Override
        public String handle(Exception data) {
            if (data instanceof DirlistTaskFactory.SourceFileException) {
                DirlistTaskFactory.SourceFileException ex1 = (DirlistTaskFactory.SourceFileException) data;
                if (ex1.getCause() instanceof FileNotFoundException) {
                    return String.format(getLocaleResource("source.not_found"), ex1.getSource());
                } else if (ex1.getCause() instanceof DirlistTaskFactory.EmptyImagesException) {
                    return String.format(getLocaleResource("source.empty_image"), ex1.getSource());
                } else if (ex1.getCause() instanceof DirlistTaskFactory.WrongFileTypeException) {
                    DirlistTaskFactory.WrongFileTypeException ex2 = (DirlistTaskFactory.WrongFileTypeException) ex1.getCause();
                    return String.format(getLocaleResource("source.wrong_type"), getFileTypeResource(ex2.getExpected()), getFileTypeResource(ex2.getValue()), ex1.getSource());
                }
            }

            return doNext(data);
        }
    }

    private class DirlistExceptionHandler extends Handler<String, Exception> {

        public DirlistExceptionHandler(Handler<String, Exception> next) {
            super(next);
        }

        @Override
        public String handle(Exception data) {
            if (data instanceof DirlistTaskFactory.DirListException) {
                DirlistTaskFactory.DirListException ex1 = (DirlistTaskFactory.DirListException) data;
                if (ex1.getCause() instanceof FileNotFoundException) {
                    return String.format(getLocaleResource("dirlist.not_found"), ex1.getDirlist());
                } else if (ex1.getCause() instanceof DirlistTaskFactory.WrongFileTypeException) {
                    DirlistTaskFactory.WrongFileTypeException ex2 = (DirlistTaskFactory.WrongFileTypeException) ex1.getCause();
                    return String.format(getLocaleResource("dirlist.wrong_type"), getFileTypeResource(ex2.getExpected()), getFileTypeResource(ex2.getValue()), ex1.getDirlist());
                }
            }

            return doNext(data);
        }
    }

    private class OverwriteTaskListHandler extends Handler<String, Exception> {
        public OverwriteTaskListHandler(Handler<String, Exception> next) {
            super(next);
        }

        @Override
        public String handle(Exception data) {
            if (data instanceof CreateCommand.OverwriteTaskListException) {
                CreateCommand.OverwriteTaskListException exception = (CreateCommand.OverwriteTaskListException) data;
                String dd = getLocaleResource("overwrite");
                return String.format(dd, exception.getFile());
            } else {
                return doNext(data);
            }
        }
    }

    private class SaveExceptionHandler extends Handler<String, Exception> {

        public SaveExceptionHandler(Handler<String, Exception> next) {
            super(next);
        }

        @Override
        public String handle(Exception data) {
            if (data instanceof CreateCommand.SaveException) {
                CreateCommand.SaveException exception = (CreateCommand.SaveException) data;
                return String.format(getLocaleResource("save"), exception.getFile());
            } else {
                return doNext(data);
            }
        }
    }
}
