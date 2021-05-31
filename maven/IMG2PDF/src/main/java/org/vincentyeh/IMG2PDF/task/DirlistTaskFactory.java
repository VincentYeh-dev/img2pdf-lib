package org.vincentyeh.IMG2PDF.task;

import org.vincentyeh.IMG2PDF.util.NameFormatter;
import org.vincentyeh.IMG2PDF.util.file.FileUtils;

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

    public static List<Task> createFromDirlist(File dirlist, Charset charset) throws IOException {
        FileUtils.checkAbsolute(dirlist);
        FileUtils.checkIsFile(dirlist);
        List<String> lines = readAllLinesFromDirlist(dirlist, charset);

        List<Task> tasks = new ArrayList<>();
        for (String line : lines) {
            tasks.add(mergeArgumentIntoTask(getCheckedFileFromLine(line, dirlist)));
        }
        return tasks;
    }

    private static File getCheckedFileFromLine(String line, File directoryList) throws FileNotFoundException, FileUtils.WrongTypeException {
        File dir = new File(line);
        File result;
        if (!dir.isAbsolute()) {
            result = new File(directoryList.getParent(), line).getAbsoluteFile();
        } else {
            result = dir;
        }
        FileUtils.checkExists(result);
        FileUtils.checkIsDirectory(result);

        return result;
    }

    private static Task mergeArgumentIntoTask(File source_directory) {
        NameFormatter nf = new NameFormatter(source_directory);
        return createTask(documentArgument, pageArgument, importSortedImagesFiles(source_directory), new File(nf.format(pdf_destination)).getAbsoluteFile());
    }

    private static List<String> readAllLinesFromDirlist(File dirlist, Charset charset) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(dirlist.toPath(), charset)) {
            List<String> result = new ArrayList<>();
            for (; ; ) {
                String line = reader.readLine();
                if (line == null || getFixedLine(line) == null)
                    break;
                result.add(getFixedLine(line));
            }
            return result;
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


    private static File[] importSortedImagesFiles(File source_directory) {
        File[] files = source_directory.listFiles(imageFilter);
        if (files == null)
            files = new File[0];

        Arrays.sort(files, fileSorter);

        return files;
    }

    private static Task createTask(DocumentArgument documentArgument, PageArgument pageArgument, File[] images, File pdf_destination) {
        return new Task(documentArgument, pageArgument, images, pdf_destination);
    }
}
