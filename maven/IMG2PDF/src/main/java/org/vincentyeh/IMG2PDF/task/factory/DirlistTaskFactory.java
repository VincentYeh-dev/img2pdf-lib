package org.vincentyeh.IMG2PDF.task.factory;

import org.vincentyeh.IMG2PDF.task.DocumentArgument;
import org.vincentyeh.IMG2PDF.task.PageArgument;
import org.vincentyeh.IMG2PDF.task.Task;
import org.vincentyeh.IMG2PDF.task.factory.exception.DirListException;
import org.vincentyeh.IMG2PDF.task.factory.exception.SourceFileException;
import org.vincentyeh.IMG2PDF.util.NameFormatter;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;

public abstract class DirlistTaskFactory {

    private static FileFilter imageFilter;
    private static Comparator<? super File> fileSorter;
    private static DocumentArgument documentArgument;
    private static PageArgument pageArgument;
    private static String pdf_destination;

    public static void setArgument(DocumentArgument documentArgument, PageArgument pageArgument, String pdf_destination) {
        DirlistTaskFactory.documentArgument = documentArgument;
        DirlistTaskFactory.pageArgument = pageArgument;
        DirlistTaskFactory.pdf_destination = pdf_destination;
    }

    public static void setImageFilesRule(FileFilter imageFilter, Comparator<? super File> fileSorter) {
        DirlistTaskFactory.imageFilter = imageFilter;
        DirlistTaskFactory.fileSorter = fileSorter;
    }

    public static List<Task> createFromDirlist(File dirlist, Charset charset) throws SourceFileException, DirListException {
        if(dirlist==null)
            throw new IllegalArgumentException("dirlist==null");
        if(charset==null)
            throw new IllegalArgumentException("charset==null");

        List<String> lines = readAllLinesFromDirlist(dirlist, charset);

        List<Task> tasks = new ArrayList<>();

        for (String line : lines) {
            tasks.add(createTaskFromSource(getCheckedFileFromLine(line, dirlist)));
        }

        return tasks;
    }

    private static File getCheckedFileFromLine(String line, File directoryList) throws SourceFileException {
        File dir = new File(line);
        File result;
        if (!dir.isAbsolute()) {
            result = new File(directoryList.getParent(), line).getAbsoluteFile();
        } else {
            result = dir;
        }

        if (!result.exists()) {
            throw new SourceFileException("Source file not found:" +result, result);
        }

        if (!result.isDirectory()) {
            throw new SourceFileException(new IOException("Source file is not a directory:" + result), result);
        }

        return result;
    }

    private static Task createTaskFromSource(File directory) throws SourceFileException {
        NameFormatter nf = new NameFormatter(directory);
        return createTask(documentArgument,
                pageArgument,
                importSortedImagesFiles(directory),
                new File(nf.format(pdf_destination)).getAbsoluteFile());
    }

    private static List<String> readAllLinesFromDirlist(File dirlist, Charset charset) throws DirListException {
        if (!dirlist.exists())
            throw new DirListException("Dirlist not found:" + dirlist, dirlist);

        if (!dirlist.isFile())
            throw new DirListException("Dirlist file is not a file:" + dirlist, dirlist);


        try (BufferedReader reader = Files.newBufferedReader(dirlist.toPath(), charset)) {
            List<String> result = new ArrayList<>();
            for (; ; ) {
                String line = reader.readLine();
                if (line == null || getFixedLine(line) == null)
                    break;
                result.add(getFixedLine(line));
            }
            return result;
        } catch (Exception e) {
            throw new DirListException(e, dirlist);
        }
    }

    private static String getFixedLine(String raw) {
        if (raw == null || raw.isEmpty() || raw.trim().isEmpty())
            return null;
        else {
//            remove BOM header in UTF8 line
            String result = raw.replace("\uFEFF", "");
            return result.trim();
        }
    }


    private static File[] importSortedImagesFiles(File source_directory) throws SourceFileException {
        File[] files = source_directory.listFiles(imageFilter);

        if (files == null || files.length == 0)
            throw new SourceFileException("No image was found in:" + source_directory, source_directory);

        Arrays.sort(files, fileSorter);

        return files;
    }

    private static Task createTask(DocumentArgument documentArgument, PageArgument pageArgument, File[] images, File pdf_destination) {
        return new Task(documentArgument, pageArgument, images, pdf_destination);
    }

}
