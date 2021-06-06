package org.vincentyeh.IMG2PDF.commandline;

import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(name = "img2pdf", mixinStandardHelpOptions = true,
        version = {
        "IMG2PDF-0.0.1-SNAPSHOT",
        "Picocli " + picocli.CommandLine.VERSION,
        "JVM: ${java.version} (${java.vendor} ${java.vm.name} ${java.vm.version})",
        "OS: ${os.name} ${os.version} ${os.arch}"},
        subcommands = {CreateCommand.class,ConvertCommand.class}
        , resourceBundle = "cmd")

public class IMG2PDFCommand implements Callable<Integer> {
    @CommandLine.Option(names = {"--debug", "-d"})
    boolean debug;


    @Override
    public Integer call() throws Exception {
        return null;
    }
}
