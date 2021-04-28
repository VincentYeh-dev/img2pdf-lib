package org.vincentyeh.IMG2PDF.commandline.parser;

import org.apache.commons.cli.*;
import org.vincentyeh.IMG2PDF.SharedSpace;
import org.vincentyeh.IMG2PDF.commandline.action.CreateAction;
import org.vincentyeh.IMG2PDF.commandline.action.exception.UnrecognizedEnumException;
import org.vincentyeh.IMG2PDF.commandline.parser.core.CheckHelpParser;
import org.vincentyeh.IMG2PDF.commandline.parser.core.HandledException;
import org.vincentyeh.IMG2PDF.commandline.PropertiesOption;
import org.vincentyeh.IMG2PDF.pdf.doc.DocumentAccessPermission;
import org.vincentyeh.IMG2PDF.pdf.doc.DocumentArgument;
import org.vincentyeh.IMG2PDF.pdf.page.PageAlign;
import org.vincentyeh.IMG2PDF.pdf.page.PageArgument;
import org.vincentyeh.IMG2PDF.pdf.page.PageDirection;
import org.vincentyeh.IMG2PDF.pdf.page.PageSize;
import org.vincentyeh.IMG2PDF.util.FileFilterHelper;
import org.vincentyeh.IMG2PDF.util.FileSorter;

import java.io.File;
import java.io.IOException;

public class CreateActionParser extends ActionParser<CreateAction> {

    private static final String DEFAULT_PDF_SIZE = "A4";
    private static final String DEFAULT_PDF_ALIGN = "CENTER-CENTER";
    private static final String DEFAULT_PDF_DIRECTION = "Portrait";
    private static final String DEFAULT_PDF_SORTBY = "NAME";
    private static final String DEFAULT_PDF_SEQUENCE = "INCREASE";
    private static final String DEFAULT_PDF_FILTER = "glob:*.{PNG,JPG}";

    private final CheckHelpParser parser;
    private final Options options = new Options();

    @Override
    public CreateAction parse(String[] arguments) throws ParseException, HandledException {
        CommandLine cmd = parser.parse(options, arguments);

        boolean debug = cmd.hasOption("debug");
        boolean overwrite = cmd.hasOption("overwrite");

        String pdf_dst = cmd.getOptionValue("pdf_destination");

        File tasklist_dst = getTaskListDestination(cmd);

        FileSorter.Sortby pdf_sortby;
        FileSorter.Sequence pdf_sequence;
        DocumentArgument documentArgument;
        PageArgument pageArgument;
        try {
            pdf_sortby = FileSorter.Sortby.getByString(cmd.getOptionValue("pdf_sortby", DEFAULT_PDF_SORTBY));
            pdf_sequence = FileSorter.Sequence.getByString(cmd.getOptionValue("pdf_sequence", DEFAULT_PDF_SEQUENCE));
            pageArgument = getPageArgument(cmd);
            documentArgument = getDocumentArgument(cmd);
        } catch (UnrecognizedEnumException e) {
            System.err.printf(SharedSpace.getResString("public.err.unrecognizable_enum_long") + "\n", e.getUnrecognizableEnum(), e.getEnumName(), listStringArray(e.getAvailiableValues()));
            throw new HandledException(e, getClass());
        }

        FileFilterHelper ffh;
        try {
            ffh = getFileFilterHelper(cmd);
        } catch (UnsupportedOperationException e) {
            System.err.printf(SharedSpace.getResString("create.err.filter") + "\n", e.getMessage());
            throw new HandledException(e, getClass());
        }

        String[] str_sources = cmd.getOptionValues("source");
        if (str_sources == null) {
            throw new HandledException(new IllegalArgumentException("source==null"), getClass());
        }

        File[] sourceFiles;
        try {
            sourceFiles = verifyFiles(str_sources);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            throw new HandledException(e, getClass());
        }
        return new CreateAction(pdf_sortby, pdf_sequence, ffh, documentArgument, pageArgument, pdf_dst, tasklist_dst, debug, overwrite, sourceFiles);
    }

    private FileFilterHelper getFileFilterHelper(CommandLine cmd) {
        return new FileFilterHelper(cmd.getOptionValue("filter", DEFAULT_PDF_FILTER));
    }

    private File getTaskListDestination(CommandLine cmd) {
        String list_destination = cmd.getOptionValue("list_destination");
        return (new File(list_destination)).getAbsoluteFile();
    }

    private DocumentArgument getDocumentArgument(CommandLine cmd) {
        DocumentAccessPermission pdf_permission = new DocumentAccessPermission(cmd.getOptionValue("pdf_permission", "11"));
        String pdf_owner_password = cmd.getOptionValue("pdf_owner_password");
        String pdf_user_password = cmd.getOptionValue("pdf_user_password");
        return new DocumentArgument(pdf_owner_password, pdf_user_password, pdf_permission);
    }

    private PageArgument getPageArgument(CommandLine cmd) throws UnrecognizedEnumException {
        PageArgument pageArgument = new PageArgument();
        pageArgument.setAlign(new PageAlign(cmd.getOptionValue("pdf_align", DEFAULT_PDF_ALIGN)));
        pageArgument.setSize(PageSize.getByString(cmd.getOptionValue("pdf_size", DEFAULT_PDF_SIZE)));
        pageArgument.setDirection(PageDirection.getByString(cmd.getOptionValue("pdf_direction", DEFAULT_PDF_DIRECTION)));
        pageArgument.setAutoRotate(cmd.hasOption("pdf_auto_rotate"));
        return pageArgument;
    }


    public CreateActionParser() {
        Option opt_help = PropertiesOption.getOption("h", "help", "create.help");

        Option opt_debug = PropertiesOption.getOption("d", "debug", "create.arg.debug.help");
        Option opt_overwrite = PropertiesOption.getOption("ow", "overwrite", "create.arg.overwrite_tasklist.help");

        Option opt_pdf_size = PropertiesOption.getArgumentOption("pz", "pdf_size", "create.arg.pdf_size.help", listStringArray(ArrayToStringArray(PageSize.values())));
        Option opt_pdf_align = PropertiesOption.getArgumentOption("pa", "pdf_align", "create.arg.pdf_align.help");
        Option opt_pdf_direction = PropertiesOption.getArgumentOption("pdi", "pdf_direction", "create.arg.pdf_direction.help",
                listStringArray(ArrayToStringArray(PageDirection.values())));

        Option opt_pdf_auto_rotate = PropertiesOption.getOption("par", "pdf_auto_rotate", "create.arg.pdf_auto_rotate.help");
        Option opt_pdf_sortby = PropertiesOption.getArgumentOption("ps", "pdf_sortby", "create.arg.pdf_sortby.help", listStringArray(ArrayToStringArray(FileSorter.Sortby.values())));
        Option opt_pdf_sequence = PropertiesOption.getArgumentOption("pseq", "pdf_sequence", "create.arg.pdf_sequence.help", listStringArray(ArrayToStringArray(FileSorter.Sequence.values())));
        Option opt_pdf_owner_password = PropertiesOption.getArgumentOption("popwd", "pdf_owner_password",
                "create.arg.pdf_owner_password.help");
        Option opt_pdf_user_password = PropertiesOption.getArgumentOption("pupwd", "pdf_user_password", "create.arg.pdf_user_password.help");

        Option opt_pdf_permission = PropertiesOption.getArgumentOption("pp", "pdf_permission", "create.arg.pdf_permission.help");

        Option opt_pdf_destination = PropertiesOption.getArgumentOption("pdst", "pdf_destination", "create.arg.pdf_destination.help");
        opt_pdf_destination.setRequired(true);

        Option opt_filter = PropertiesOption.getArgumentOption("f", "filter", "create.arg.filter.help");

        Option opt_sources = PropertiesOption.getArgumentOption("src", "source", "create.arg.source.help");
        opt_sources.setRequired(true);

        Option opt_list_destination = PropertiesOption.getArgumentOption("ldst", "list_destination", "create.arg.list_destination.help");
        opt_list_destination.setRequired(true);

        options.addOption(opt_debug);
        options.addOption(opt_overwrite);
        options.addOption(opt_help);
        options.addOption(opt_pdf_size);
        options.addOption(opt_pdf_align);
        options.addOption(opt_pdf_direction);
        options.addOption(opt_pdf_auto_rotate);
        options.addOption(opt_pdf_sortby);
        options.addOption(opt_pdf_sequence);
        options.addOption(opt_pdf_owner_password);
        options.addOption(opt_pdf_user_password);
        options.addOption(opt_pdf_permission);
        options.addOption(opt_pdf_destination);
        options.addOption(opt_filter);
        options.addOption(opt_sources);
        options.addOption(opt_list_destination);

        parser = new CheckHelpParser(opt_help);

        Option opt_mode = new Option("m", "mode", true, "mode");

        options.addOption(opt_mode);

    }

}
