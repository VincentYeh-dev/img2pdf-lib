package org.vincentyeh.IMG2PDF.newcommandline;

import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(name = "img2pdf",subcommands = CreateCommand.class,resourceBundle = "cmd")
public class IMG2PDFCommand implements Callable<Integer> {
    @CommandLine.Option(names = {"--debug","-d"})
    boolean debug;

    @CommandLine.Option(names = {"-h", "--help"}, usageHelp = true)
    boolean usageHelpRequested;

    @Override
    public Integer call() throws Exception {
        return null;
    }
}
