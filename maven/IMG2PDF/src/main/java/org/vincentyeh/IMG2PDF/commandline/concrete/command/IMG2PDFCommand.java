package org.vincentyeh.IMG2PDF.commandline.concrete.command;

import org.vincentyeh.IMG2PDF.Constants;
import picocli.CommandLine;

@CommandLine.Command(name = "img2pdf", mixinStandardHelpOptions = true,
        version = {
                Constants.APP_NAME + " " + Constants.APP_VERSION,
                "Picocli " + picocli.CommandLine.VERSION,
                "JVM: ${java.version} (${java.vendor} ${java.vm.name} ${java.vm.version})",
                "OS: ${os.name} ${os.version} ${os.arch}"}
)
public class IMG2PDFCommand {

    @CommandLine.Option(names = {"--debug", "-d"})
    private boolean debug;

    public boolean isDebug() {
        return debug;
    }
}
