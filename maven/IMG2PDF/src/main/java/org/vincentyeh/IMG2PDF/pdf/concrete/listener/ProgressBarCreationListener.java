package org.vincentyeh.IMG2PDF.pdf.concrete.listener;

import org.fusesource.jansi.Ansi;
import org.vincentyeh.IMG2PDF.pdf.framework.appender.PageAppender;
import org.vincentyeh.IMG2PDF.pdf.framework.converter.PDFCreator;
import org.vincentyeh.IMG2PDF.task.framework.Task;

import java.io.File;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.vincentyeh.IMG2PDF.util.PrinterUtils.*;

public class ProgressBarCreationListener implements PDFCreator.CreationListener, PageAppender.PageAppendListener {
    private final ResourceBundle resourceBundle;
    private long startSeconds;

    public ProgressBarCreationListener(Locale locale) {
        this.resourceBundle = ResourceBundle.getBundle("pdf_converter_listener", locale);

    }

    private int previous_msg_length;
    private Task task;
    private int total;

    public void initializing(Task task) {
        this.task = task;
        total = task.getImages().length;

        startSeconds = (System.currentTimeMillis() / 1000);

        Ansi msg = getRenderFormat("\r" + resourceBundle.getString("convert.listener.converting"), "          ", task.getPdfDestination().getName(), 0, total);
        print(msg);
        previous_msg_length = msg.toString().length();
        done_counter = 0;
    }

    @Override
    public void onConversionComplete() {
        backDel(previous_msg_length);
        long completeSeconds = System.currentTimeMillis() / 1000;
        printRenderFormat("\r" + resourceBundle.getString("convert.listener.done"), (completeSeconds - startSeconds), task.getPdfDestination().getAbsolutePath());
    }

    @Override
    public void onSaved(File destination) {

    }

    private int done_counter = 0;

    @Override
    public void onPageAppended(int index) {
        done_counter++;

        char[] progress_bar = new char[10];
        Arrays.fill(progress_bar, ' ');
        float rate = ((float) done_counter) / total;
        int level = ((int) (rate * 10));
        for (int i = 0; i < level; i++) {
            progress_bar[i] = '=';
        }
        Ansi msg = getRenderFormat("\r" + resourceBundle.getString("convert.listener.converting"), new String(progress_bar), task.getPdfDestination().getName(), index + 1, total);
        printWithLengthRecord(msg);
    }

    @Override
    public void onFinally() {
        print("\n\r");
    }

    private void printWithLengthRecord(Object msg) {
        print(msg);
        previous_msg_length = msg.toString().length();
    }

//    private String getSimplifiedName(String raw) {
//
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < raw.length(); i++) {
//            if (i < 30) {
//                sb.append(raw.charAt(i));
//            } else if (i > raw.length() - 30) {
//                sb.append(raw.charAt(i));
//            } else {
//                sb.append("#");
//            }
//        }
//        return sb.toString().replaceAll("#+", "...");
//    }

}
