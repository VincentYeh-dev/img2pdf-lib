package org.vincentyeh.IMG2PDF;
import org.vincentyeh.IMG2PDF.commandline.IMG2PDFCommand;
import picocli.CommandLine;


public class MainProgram {

    public static void main(String[] args) {
        SharedSpace.initialize();
        CommandLine cmd= new CommandLine(new IMG2PDFCommand());
        int exitCode =cmd.execute(args);
        System.exit(exitCode);
    }
}
