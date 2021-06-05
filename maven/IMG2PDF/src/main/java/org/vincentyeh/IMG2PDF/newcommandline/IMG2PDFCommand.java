package org.vincentyeh.IMG2PDF.newcommandline;

import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(subcommands = CreateCommand.class,description = "IMG2PDF description")
public class IMG2PDFCommand implements Callable<Integer> {

    @Override
    public Integer call() throws Exception {
        System.out.println(getClass().getSimpleName());
        return 16;
    }
}
