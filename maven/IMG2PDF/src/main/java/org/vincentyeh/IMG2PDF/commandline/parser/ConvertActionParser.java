package org.vincentyeh.IMG2PDF.commandline.parser;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;
import org.vincentyeh.IMG2PDF.commandline.option.MultiLanguageOptionFactory;
import org.vincentyeh.IMG2PDF.commandline.action.ConvertAction;
import org.vincentyeh.IMG2PDF.commandline.parser.core.CheckHelpParser;
import org.vincentyeh.IMG2PDF.commandline.parser.core.HandledException;
import org.vincentyeh.IMG2PDF.util.BytesSize;
import org.vincentyeh.IMG2PDF.util.file.FileChecker;

import java.io.File;
import java.io.IOException;

public class ConvertActionParser extends ActionParser<ConvertAction> {
    private static final String DEFAULT_TEMP_FOLDER = ".org.vincentyeh.IMG2PDF.tmp";
    private static final String DEFAULT_MAX_MEMORY_USAGE = "50MB";
    private final CommandLineParser parser;
    @Override
    public ConvertAction parse(String[] arguments) throws ParseException, HandledException {
        CommandLine cmd = parser.parse(options, arguments);

        File tempFolder = getTempFolder(cmd);
        try {
            FileChecker.makeDirsIfNotExists(tempFolder);
        } catch (IOException e) {
            e.printStackTrace();
            throw new HandledException(e, getClass());
        }

        return new ConvertAction(tempFolder, getMaxMemoryBytes(cmd), getTaskListSources(cmd), cmd.hasOption("open_when_complete"), cmd.hasOption("overwrite"));
    }

    private File[] getTaskListSources(CommandLine cmd) throws HandledException {
        try {
            return verifyFiles(cmd.getOptionValues("tasklist_source"));
        } catch (IOException e) {
            System.err.println(e.getMessage());
            throw new HandledException(e, getClass());
        }
    }

    private long getMaxMemoryBytes(CommandLine cmd) throws HandledException {
        try {
            return BytesSize.valueOf(cmd.getOptionValue("memory_max_usage", DEFAULT_MAX_MEMORY_USAGE)).getBytes();
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            throw new HandledException(e, getClass());
        }

    }

    private File getTempFolder(CommandLine cmd) {
        return new File(cmd.getOptionValue("temp_folder", DEFAULT_TEMP_FOLDER)).getAbsoluteFile();
    }

    public ConvertActionParser() {

        Option opt_help = MultiLanguageOptionFactory.getOption("h", "help", "convert.help");

        Option opt_tasklist_source = MultiLanguageOptionFactory.getArgumentOption("lsrc", "tasklist_source", "convert.arg.tasklist_source.help");
        opt_tasklist_source.setRequired(true);

        Option opt_open_when_complete = MultiLanguageOptionFactory.getOption("o", "open_when_complete", "convert.arg.open_when_complete.help");

        Option opt_overwrite = MultiLanguageOptionFactory.getOption("ow", "overwrite", "convert.arg.overwrite_output.help");

        Option opt_tmp_folder = MultiLanguageOptionFactory.getArgumentOption("tmp", "temp_folder", "convert.arg.tmp_folder.help", ".org.vincentyeh.IMG2PDF.tmp");
        Option opt_max_memory_usage = MultiLanguageOptionFactory.getArgumentOption("mx", "memory_max_usage", "convert.arg.memory_max_usage.help", "50MB");

        options.addOption(opt_help);
        options.addOption(opt_tasklist_source);
        options.addOption(opt_tmp_folder);
        options.addOption(opt_max_memory_usage);
        options.addOption(opt_open_when_complete);
        options.addOption(opt_overwrite);

        parser=new CheckHelpParser(opt_help);
    }

}
