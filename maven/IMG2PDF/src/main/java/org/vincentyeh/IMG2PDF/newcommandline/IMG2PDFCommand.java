package org.vincentyeh.IMG2PDF.newcommandline;

import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(name = "img2pdf",subcommands = CreateCommand.class,description = "IMG2PDF description")
public class IMG2PDFCommand implements Callable<Integer> {
    @CommandLine.Option(names = {"--debug","-d"})
    boolean debug;
    @Override
    public Integer call() throws Exception {
        return null;
    }
}
