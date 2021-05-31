package org.vincentyeh.IMG2PDF.task;

import org.vincentyeh.IMG2PDF.util.NameFormatter;
import org.vincentyeh.IMG2PDF.util.file.FileUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;

public abstract class DirlistTaskFactory {

    private static FileFilter imageFilter;
    private static Comparator<? super File> fileSorter;
    private static DocumentArgument documentArgument;
    private static PageArgument pageArgument;
    private static String pdf_destination;

    public static void setArgument(DocumentArgument documentArgument,PageArgument pageArgument,String pdf_destination) {
        DirlistTaskFactory.documentArgument = documentArgument;
        DirlistTaskFactory.pageArgument = pageArgument;
        DirlistTaskFactory.pdf_destination = pdf_destination;
    }

    public static void setImageFilesRule(FileFilter imageFilter, Comparator<? super File> fileSorter){
        DirlistTaskFactory.imageFilter = imageFilter;
        DirlistTaskFactory.fileSorter = fileSorter;
    }

    public static List<Task> createFromDirlist(File dirlist, Charset charset) throws IOException {
        FileUtils.checkAbsolute(dirlist);
        FileUtils.checkIsFile(dirlist);
        List<String> lines = readAllLinesFromDirlist(dirlist, charset);

        List<Task> tasks = new ArrayList<>();

        for (String s : lines) {
            String line = getFixedLine(s);
            if (line == null)
                continue;
            File dir = getAbsoluteFileFromLine(line, dirlist);
            checkDirectoryInSource(dir);
            tasks.add(mergeAllIntoTask(dir));
        }

        return tasks;
    }

    private static File getAbsoluteFileFromLine(String line, File directoryList) {
        File dir = new File(line);
        if (!dir.isAbsolute()) {
            return new File(directoryList.getParent(), line).getAbsoluteFile();
        } else {
            return dir;
        }
    }

    private static void checkDirectoryInSource(File directory) throws FileNotFoundException, FileUtils.WrongTypeException {
        FileUtils.checkExists(directory);
        FileUtils.checkIsDirectory(directory);

    }


    private static String getFixedLine(String raw) {
        if (raw.isEmpty() || raw.trim().isEmpty())
            return null;
        else
            return removeBOMHeaderInUTF8Line(raw).trim();
    }

    private static String removeBOMHeaderInUTF8Line(String raw) {
        return raw.replace("\uFEFF", "");
    }

    private static Task mergeAllIntoTask(File source_directory) throws IOException {
        FileUtils.checkAbsolute(source_directory);
        FileUtils.checkExists(source_directory);
        FileUtils.checkIsDirectory(source_directory);

        NameFormatter nf = new NameFormatter(source_directory);
        return createTask(documentArgument, pageArgument, importSortedImagesFiles(source_directory), new File(nf.format(pdf_destination)).getAbsoluteFile());
    }

    private static List<String> readAllLinesFromDirlist(File dirlist, Charset charset) throws IOException {
        return Files.readAllLines(dirlist.toPath(),charset);
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
