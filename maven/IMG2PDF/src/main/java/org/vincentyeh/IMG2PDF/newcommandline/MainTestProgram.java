package org.vincentyeh.IMG2PDF.newcommandline;

import picocli.CommandLine;

public class MainTestProgram {
    public static void main(String[] args) {
        int exitCode = new CommandLine(new IMG2PDFCommand()).execute(args);
        System.exit(exitCode);
    }
}
