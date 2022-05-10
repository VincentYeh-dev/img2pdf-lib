package org.vincentyeh.IMG2PDF.task.concrete.factory;

import org.vincentyeh.IMG2PDF.pdf.parameter.DocumentArgument;
import org.vincentyeh.IMG2PDF.pdf.parameter.PageArgument;
import org.vincentyeh.IMG2PDF.task.concrete.factory.exception.EmptyImagesException;
import org.vincentyeh.IMG2PDF.task.framework.Task;
import org.vincentyeh.IMG2PDF.task.framework.factory.TaskFactory;
import org.vincentyeh.IMG2PDF.task.framework.factory.exception.TaskFactoryProcessException;
import org.vincentyeh.IMG2PDF.util.interfaces.NameFormatter;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Comparator;

public class DirectoryTaskFactory implements TaskFactory<File> {

    private final DocumentArgument documentArgument;
    private final PageArgument pageArgument;
    private final FileFilter filter;
    private final Comparator<? super File> sorter;
    private final NameFormatter<File> formatter;

    public DirectoryTaskFactory(DocumentArgument documentArgument, PageArgument pageArgument, FileFilter filter, Comparator<? super File> sorter, NameFormatter<File> formatter) {
        this.documentArgument = documentArgument;
        this.pageArgument = pageArgument;
        this.filter = filter;
        this.sorter = sorter;
        this.formatter = formatter;
    }

    @Override
    public Task create(File file) throws TaskFactoryProcessException {
        try {
            return new Task(documentArgument, pageArgument, getImagesFromSource(file), getPdfDestinationFromSource(file));
        } catch (Exception e) {
            throw new TaskFactoryProcessException(file, e);
        }
    }

    private File getPdfDestinationFromSource(File file) throws NameFormatter.FormatException {
        return new File(formatter.format(file)).getAbsoluteFile();
    }

    private File[] getImagesFromSource(File file) throws EmptyImagesException {
        File[] files = file.listFiles(filter);
        if (files == null || files.length == 0)
            throw new EmptyImagesException();
        Arrays.sort(files, sorter);
        return files;
    }
}
