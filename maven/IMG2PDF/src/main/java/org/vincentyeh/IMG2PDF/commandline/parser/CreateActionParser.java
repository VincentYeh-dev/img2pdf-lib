package org.vincentyeh.IMG2PDF.commandline.parser;

import org.apache.commons.cli.*;
import org.vincentyeh.IMG2PDF.SharedSpace;
import org.vincentyeh.IMG2PDF.commandline.action.CreateAction;
import org.vincentyeh.IMG2PDF.commandline.parser.core.HandledException;
import org.vincentyeh.IMG2PDF.commandline.option.MultiLanguageOptionFactory;
import org.vincentyeh.IMG2PDF.pdf.doc.DocumentAccessPermission;
import org.vincentyeh.IMG2PDF.pdf.doc.DocumentArgument;
import org.vincentyeh.IMG2PDF.pdf.page.PageAlign;
import org.vincentyeh.IMG2PDF.pdf.page.PageArgument;
import org.vincentyeh.IMG2PDF.pdf.page.PageDirection;
import org.vincentyeh.IMG2PDF.pdf.page.PageSize;
import org.vincentyeh.IMG2PDF.util.file.FileFilterHelper;
import org.vincentyeh.IMG2PDF.util.file.FileSorter;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class CreateActionParser extends ActionParser<CreateAction> {

    private static final String DEFAULT_PDF_SIZE = "A4";
    private static final String DEFAULT_PDF_ALIGN = "CENTER-CENTER";
    private static final String DEFAULT_PDF_DIRECTION = "Portrait";
    private static final String DEFAULT_PDF_SORTBY = "NAME";
    private static final String DEFAULT_PDF_SEQUENCE = "INCREASE";
    private static final String DEFAULT_PDF_FILTER = "glob:*.{PNG,JPG}";

    @Override
    public CreateAction parse(String[] arguments) throws ParseException, HandledException {
        CommandLine cmd = parser.parse(options, arguments);

        boolean debug = cmd.hasOption("debug");
        boolean overwrite = cmd.hasOption("overwrite");

        String pdf_dst = cmd.getOptionValue("pdf_destination");


        File tasklist_dst = getTaskListDestination(cmd);

        FileSorter.Sortby pdf_sortby = getValueOfSortby(cmd.getOptionValue("pdf_sortby", DEFAULT_PDF_SORTBY));
        FileSorter.Sequence pdf_sequence = getValueOfSequence(cmd.getOptionValue("pdf_sequence", DEFAULT_PDF_SEQUENCE));
        DocumentArgument documentArgument = getDocumentArgument(cmd);
        PageArgument pageArgument = getPageArgument(cmd);

        FileFilterHelper ffh = getFileFilterHelper(cmd);

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

    private FileFilterHelper getFileFilterHelper(CommandLine cmd) throws HandledException {
        try {
            return new FileFilterHelper(cmd.getOptionValue("filter", DEFAULT_PDF_FILTER));

        } catch (UnsupportedOperationException e) {
            System.err.printf(SharedSpace.getResString("create.err.filter") + "\n", e.getMessage());
            throw new HandledException(e, getClass());
        }
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

    private PageArgument getPageArgument(CommandLine cmd) throws HandledException {
        PageArgument pageArgument = new PageArgument();
        pageArgument.setAlign(getValueOfAlign(cmd.getOptionValue("pdf_align", DEFAULT_PDF_ALIGN)));
        pageArgument.setSize(getValueOfSize(cmd.getOptionValue("pdf_size", DEFAULT_PDF_SIZE)));
        pageArgument.setDirection(getValueOfDirection(cmd.getOptionValue("pdf_direction", DEFAULT_PDF_DIRECTION)));

        pageArgument.setAutoRotate(cmd.hasOption("pdf_auto_rotate"));
        return pageArgument;
    }

    private PageDirection getValueOfDirection(String value) throws HandledException {
        try {
            return PageDirection.valueOf(value);
        } catch (IllegalArgumentException e) {
//            TODO:Check
            System.err.printf(SharedSpace.getResString("public.err.unrecognizable_enum_long") + "\n", value, PageDirection.class.getSimpleName(), Arrays.toString(PageDirection.values()));
            throw new HandledException(e, getClass());
        }
    }

    private PageSize getValueOfSize(String value) throws HandledException {
        try {
            return PageSize.valueOf(value);
        } catch (IllegalArgumentException e) {
//            TODO:Check
            System.err.printf(SharedSpace.getResString("public.err.unrecognizable_enum_long") + "\n", value, PageSize.class.getSimpleName(), Arrays.toString(PageSize.values()));
            throw new HandledException(e, getClass());
        }
    }


    private PageAlign getValueOfAlign(String value) throws HandledException {
        try {
            return new PageAlign(value);
        } catch (IllegalArgumentException e) {
//            TODO:Check
            System.err.printf(SharedSpace.getResString("public.err.unrecognizable_enum_long") + "\n", value, PageAlign.class.getSimpleName(), Arrays.toString(new String[]{""}));
            throw new HandledException(e, getClass());
        }
    }


    private FileSorter.Sequence getValueOfSequence(String value) throws HandledException {
        try {
            return FileSorter.Sequence.valueOf(value);
        } catch (IllegalArgumentException e) {
//            TODO:Check
            System.err.printf(SharedSpace.getResString("public.err.unrecognizable_enum_long") + "\n", value, FileSorter.Sequence.class.getSimpleName(), Arrays.toString(FileSorter.Sequence.values()));
            throw new HandledException(e, getClass());
        }
    }

    private FileSorter.Sortby getValueOfSortby(String value) throws HandledException {
        try {
            return FileSorter.Sortby.valueOf(value);
        } catch (IllegalArgumentException e) {
//            TODO:Check
            System.err.printf(SharedSpace.getResString("public.err.unrecognizable_enum_long") + "\n", value, FileSorter.Sortby.class.getSimpleName(), Arrays.toString(FileSorter.Sortby.values()));
            throw new HandledException(e, getClass());
        }
    }

    public CreateActionParser() {
        super(MultiLanguageOptionFactory.getOption("h", "help", "create.help"));
        Option opt_help = MultiLanguageOptionFactory.getOption("h", "help", "create.help");

        Option opt_debug = MultiLanguageOptionFactory.getOption("d", "debug", "create.arg.debug.help");
        Option opt_overwrite = MultiLanguageOptionFactory.getOption("ow", "overwrite", "create.arg.overwrite_tasklist.help");

        Option opt_pdf_size = MultiLanguageOptionFactory.getArgumentOption("pz", "pdf_size", "create.arg.pdf_size.help", listStringArray(ArrayToStringArray(PageSize.values())));
        Option opt_pdf_align = MultiLanguageOptionFactory.getArgumentOption("pa", "pdf_align", "create.arg.pdf_align.help");
        Option opt_pdf_direction = MultiLanguageOptionFactory.getArgumentOption("pdi", "pdf_direction", "create.arg.pdf_direction.help",
                listStringArray(ArrayToStringArray(PageDirection.values())));

        Option opt_pdf_auto_rotate = MultiLanguageOptionFactory.getOption("par", "pdf_auto_rotate", "create.arg.pdf_auto_rotate.help");
        Option opt_pdf_sortby = MultiLanguageOptionFactory.getArgumentOption("ps", "pdf_sortby", "create.arg.pdf_sortby.help", listStringArray(ArrayToStringArray(FileSorter.Sortby.values())));
        Option opt_pdf_sequence = MultiLanguageOptionFactory.getArgumentOption("pseq", "pdf_sequence", "create.arg.pdf_sequence.help", listStringArray(ArrayToStringArray(FileSorter.Sequence.values())));
        Option opt_pdf_owner_password = MultiLanguageOptionFactory.getArgumentOption("popwd", "pdf_owner_password",
                "create.arg.pdf_owner_password.help");
        Option opt_pdf_user_password = MultiLanguageOptionFactory.getArgumentOption("pupwd", "pdf_user_password", "create.arg.pdf_user_password.help");

        Option opt_pdf_permission = MultiLanguageOptionFactory.getArgumentOption("pp", "pdf_permission", "create.arg.pdf_permission.help");

        Option opt_pdf_destination = MultiLanguageOptionFactory.getArgumentOption("pdst", "pdf_destination", "create.arg.pdf_destination.help");
        opt_pdf_destination.setRequired(true);

        Option opt_filter = MultiLanguageOptionFactory.getArgumentOption("f", "filter", "create.arg.filter.help");

        Option opt_sources = MultiLanguageOptionFactory.getArgumentOption("src", "source", "create.arg.source.help");
        opt_sources.setRequired(true);

        Option opt_list_destination = MultiLanguageOptionFactory.getArgumentOption("ldst", "list_destination", "create.arg.list_destination.help");
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


        Option opt_mode = new Option("m", "mode", true, "mode");

        options.addOption(opt_mode);

    }
}
