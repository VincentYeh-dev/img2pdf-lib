package org.vincentyeh.IMG2PDF.commandline;

import org.vincentyeh.IMG2PDF.SharedSpace;
import picocli.CommandLine;

public class MainTestProgram {
    public static void main(String[] args) {
        SharedSpace.initialize();
        CommandLine cmd= new CommandLine(new IMG2PDFCommand());
        int exitCode =cmd.execute(args);
        System.exit(exitCode);
    }
}
