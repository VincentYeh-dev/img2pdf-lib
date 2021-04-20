package org.vincentyeh.IMG2PDF.commandline;

import org.vincentyeh.IMG2PDF.SharedSpace;
import org.vincentyeh.IMG2PDF.converter.ConversionListener;
import org.vincentyeh.IMG2PDF.task.Task;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class CustomConversionListener implements ConversionListener {
    private final char[] progress_bar;
    private Task task;

    public CustomConversionListener() {
        progress_bar = new char[10];
        Arrays.fill(progress_bar, ' ');
    }

    private int total;
    private int counter = 0;

    private double perImg;
    private double progress = 0;

    @Override
    public void onConversionPreparing(Task task) {
        this.task = task;
        total = task.getImgs().length;
        perImg = (10. / total);
    }

    @Override
    public void onConverting(int index, File file) {
        progress += perImg;
        while (progress >= 1) {
            progress_bar[counter] = '=';
            progress -= 1;
            counter++;
        }
        String name = task.getDocumentArgument().getDestination().getName();
        System.out.printf("\t" + SharedSpace.getResString("convert.listener.converting") + "\r", new String(progress_bar), getSimplifiedName(name), index + 1, total, file.getName());
    }

    @Override
    public void onConversionComplete(File dst) {

        System.out.printf("\t" + SharedSpace.getResString("convert.listener.done") + "\r\n", dst.getName());
        counter = total = 0;
    }


    @Override
    public void onConversionFail(int index, Exception e) {
        System.out.printf("\t" + SharedSpace.getResString("convert.listener.err.image") + "\r\n", e.getMessage());
        counter = total = 0;
    }

    @Override
    public void onFileAlreadyExists(File file) {
        System.err.printf("\t" + SharedSpace.getResString("convert.listener.err.overwrite") + "\r\n", file.getName());
        counter = total = 0;
    }

    @Override
    public void onImageReadFail(int index, File image, IOException e) {
        System.out.printf("\t" + SharedSpace.getResString("convert.listener.err.conversion") + "\r\n", image.getPath(), e.getMessage());
        counter = total = 0;
    }

    private String getSimplifiedName(String raw){

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < raw.length(); i++) {
            if (i < 30) {
                sb.append(raw.charAt(i));
            } else if (i > raw.length() - 30) {
                sb.append(raw.charAt(i));
            }else{
                sb.append("#");
            }
        }
       return sb.toString().replaceAll("#+","...");
    }

}
