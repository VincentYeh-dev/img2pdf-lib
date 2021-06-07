package org.vincentyeh.IMG2PDF.commandline.handler;

import org.vincentyeh.IMG2PDF.commandline.CreateCommand;
import org.vincentyeh.IMG2PDF.task.factory.DirlistTaskFactory;
import picocli.CommandLine;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ResourceBundle;

public class ResourceBundleExecutionHandler implements CommandLine.IExecutionExceptionHandler {

    private final ResourceBundle resourceBundle;

    public ResourceBundleExecutionHandler(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    @Override
    public int handleExecutionException(Exception ex, CommandLine cmd, CommandLine.ParseResult parseResult) throws Exception {

        if (getCommandClass(cmd).equals(CreateCommand.class))
            handleCreate(ex, cmd, parseResult);
        return 0;
    }

    private void handleCreate(Exception ex, CommandLine cmd, CommandLine.ParseResult parseResult) {
        if(ex instanceof CreateCommand.OverwriteTaskListException){
            CreateCommand.OverwriteTaskListException exception=(CreateCommand.OverwriteTaskListException)ex;
            printErrorText(cmd,String.format("Overwrite deny: %s",exception.getFile()));
        }else if(ex instanceof CreateCommand.SaveException){
//            TODO:Print error message;
        }else if(ex instanceof DirlistTaskFactory.SourceFileException){
            DirlistTaskFactory.SourceFileException ex1=(DirlistTaskFactory.SourceFileException) ex;
            if(ex1.getCause() instanceof FileNotFoundException){
//            TODO:Print error message;
            }else if(ex1.getCause() instanceof DirlistTaskFactory.EmptyImagesException){

//            TODO:Print error message;

            }
        }else if(ex instanceof DirlistTaskFactory.DirListException){
            DirlistTaskFactory.DirListException ex1=(DirlistTaskFactory.DirListException)ex;
            if(ex1.getCause() instanceof FileNotFoundException){
//            TODO:Print error message;
            }else if(ex1.getCause() instanceof DirlistTaskFactory.WrongFileTypeException){
                DirlistTaskFactory.WrongFileTypeException ex2=(DirlistTaskFactory.WrongFileTypeException) ex1.getCause();
//            TODO:Print error message;
            }else if(ex1.getCause() instanceof IOException){
                printErrorText(cmd,ex1.getCause().getMessage());
//            todo:print error message;
            }
        }else{
            printErrorText(cmd,ex.getMessage());
//            todo:print error message;
        }
    }

    private Class<?> getCommandClass(CommandLine commandLine) {
        return commandLine.getCommand().getClass();
    }

    private void printErrorText(CommandLine cmd, String message) {
        PrintWriter printer = cmd.getErr();
        printer.println(cmd.getColorScheme().errorText(message)); // bold red
    }

    private void printText(CommandLine cmd, String message) {
        PrintWriter printer = cmd.getErr();
        printer.println(message); // bold red
    }

    private String getLocaleResource(String key) {
        return resourceBundle.getString("err.parameter.handler." + key);
    }

    private String getPublicResource(String key) {
        return resourceBundle.getString("public." + key);
    }
}
