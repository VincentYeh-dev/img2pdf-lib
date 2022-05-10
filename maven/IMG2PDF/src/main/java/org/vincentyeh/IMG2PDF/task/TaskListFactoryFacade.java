package org.vincentyeh.IMG2PDF.task;

import org.vincentyeh.IMG2PDF.task.framework.Task;
import org.vincentyeh.IMG2PDF.task.framework.factory.TaskFactory;
import org.vincentyeh.IMG2PDF.task.framework.factory.exception.TaskFactoryProcessException;
import org.vincentyeh.IMG2PDF.util.file.FileUtils;
import org.vincentyeh.IMG2PDF.util.file.exception.WrongFileTypeException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TaskListFactoryFacade {
    private TaskListFactoryFacade() {

    }

    public static List<Task> getTaskFromDirectoryList(File dirlist, Charset dir_list_read_charset, TaskFactory<File> factory) throws IOException, TaskFactoryProcessException {
        List<String> lines = readAllLines(dirlist, dir_list_read_charset);

        List<Task> tasks = new LinkedList<>();
        for (int index = 0; index < lines.size(); index++) {
            File raw = new File(lines.get(index));

            File result;
            if (!raw.isAbsolute())
                result = new File(FileUtils.getExistedParentFile(dirlist), lines.get(index)).getAbsoluteFile();
            else
                result = raw;

            tasks.add(factory.create(result));
        }
        return tasks;


    }


    private static List<String> readAllLines(File file, Charset charset) throws IOException {
        FileUtils.checkExists(file);
        FileUtils.checkType(file, WrongFileTypeException.Type.FILE);

        BufferedReader reader = Files.newBufferedReader(file.toPath(), charset);
        List<String> result = new ArrayList<>();
        for (; ; ) {
            String line = reader.readLine();
            if (line == null || getFixedLine(line) == null)
                break;
            result.add(getFixedLine(line));
        }
        return result;
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
}
