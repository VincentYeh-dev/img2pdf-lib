package org.vincentyeh.IMG2PDF.commandline.action;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

import org.vincentyeh.IMG2PDF.SharedSpace;
import org.vincentyeh.IMG2PDF.commandline.parser.core.HandledException;
import org.vincentyeh.IMG2PDF.task.*;
import org.vincentyeh.IMG2PDF.util.file.FileFilterHelper;
import org.vincentyeh.IMG2PDF.util.file.FileSorter;
import org.vincentyeh.IMG2PDF.util.file.FileUtils;

public class CreateAction implements Action {


    private final FileSorter fileSorter;
    protected final FileFilterHelper ffh;

    //    For PDF
    private final DocumentArgument documentArgument;
    private final PageArgument pageArgument;
    private final String pdf_dst;

    //    For tasklist
    private final File tasklist_dst;
    private final boolean debug;
    private final boolean overwrite;
    private final File[] sourceFiles;

    private CreateAction(FileSorter fileSorter, FileFilterHelper ffh, DocumentArgument documentArgument, PageArgument pageArgument, String pdf_dst, File tasklist_dst, boolean debug, boolean overwrite, File[] sourceFiles) {
        this.fileSorter = fileSorter;
        this.ffh = ffh;
        this.documentArgument = documentArgument;
        this.pageArgument = pageArgument;
        this.pdf_dst = pdf_dst;
        this.tasklist_dst = tasklist_dst;
        this.debug = debug;
        this.overwrite = overwrite;
        this.sourceFiles = sourceFiles;
    }

    @Override
    public void start() throws Exception {

        if (!overwrite && tasklist_dst.exists()) {
            System.err.printf(SharedSpace.getResString("public.err.overwrite") + "\n", tasklist_dst.getAbsolutePath());
            throw new HandledException(new RuntimeException("Overwrite deny"), getClass());
        }
        DirlistTaskFactory.setArgument(documentArgument, pageArgument, pdf_dst);
        DirlistTaskFactory.setImageFilesRule(ffh, fileSorter);

        List<Task> tasks = new ArrayList<>();

        for (File dirlist : sourceFiles) {
            tasks.addAll(DirlistTaskFactory.createFromDirlist(dirlist, SharedSpace.Configuration.DIRLIST_READ_CHARSET));
        }

        save(tasks, tasklist_dst);
    }

    public void save(List<Task> taskList, File destination) throws HandledException {
        try {
            FileUtils.makeDirsIfNotExists(destination.getParentFile());
            FileUtils.checkAbsolute(destination);
            FileUtils.checkIsFile(destination);

            TaskListConverter converter = new TaskListConverter();
            Files.writeString(destination.toPath(), converter.toXml(taskList), SharedSpace.Configuration.TASKlIST_WRITE_CHARSET);
            System.out.printf("[" + SharedSpace.getResString("public.info.exported") + "] %s\n", tasklist_dst.getAbsolutePath());
        } catch (IOException e) {
            System.err.printf(SharedSpace.getResString("create.err.tasklist_create") + "\n", e.getMessage());
            throw new HandledException(e, getClass());
        }
    }

    public static class Builder {
        private FileSorter fileSorter;
        protected FileFilterHelper ffh;

        private DocumentArgument documentArgument;
        private PageArgument pageArgument;
        private String pdf_dst;

        private File tasklist_dst;
        private boolean debug;
        private boolean overwrite;
        private File[] sourceFiles;

        public Builder setFileSorter(FileSorter fileSorter) {
            this.fileSorter = fileSorter;
            return this;
        }

        public Builder setFileFilterHelper(FileFilterHelper ffh) {
            this.ffh = ffh;
            return this;
        }

        public Builder setDocumentArgument(DocumentArgument documentArgument) {
            this.documentArgument = documentArgument;
            return this;
        }

        public Builder setPageArgument(PageArgument pageArgument) {
            this.pageArgument = pageArgument;
            return this;
        }

        public Builder setPdfDestination(String pdf_dst) {
            this.pdf_dst = pdf_dst;

            return this;
        }

        public Builder setTasklistDestination(File tasklist_dst) {
            this.tasklist_dst = tasklist_dst;
            return this;
        }

        public Builder setDebug(boolean debug) {
            this.debug = debug;
            return this;
        }

        public Builder setOverwrite(boolean overwrite) {
            this.overwrite = overwrite;
            return this;
        }

        public Builder setSourceFiles(File[] sourceFiles) {
            this.sourceFiles = sourceFiles;
            return this;
        }

        public CreateAction build() {
            return new CreateAction(fileSorter, ffh, documentArgument, pageArgument, pdf_dst, tasklist_dst, debug, overwrite, sourceFiles);
        }
    }
}