package org.vincentyeh.IMG2PDF.commandline;

import org.vincentyeh.IMG2PDF.SharedSpace;
import org.vincentyeh.IMG2PDF.converter.ConversionListener;
import org.vincentyeh.IMG2PDF.task.Task;

import java.io.File;
import java.io.IOException;

public class CustomConversionListener implements ConversionListener {
    private final char[] progress_bar={' ',' ',' ',' ',' ',' ',' ',' ',' ',' '};
    private int total;
    private int counter=0;

    private double perImg;
    private double progress = 0;

    @Override
    public void onConversionPreparing(Task task) {
        total = task.getImgs().length;
        perImg = (10. / total);
        System.out.printf("\t###%s###\n", SharedSpace.getResString("convert.pdf_conversion_task"));
        System.out.printf("\t%s:%s\n", SharedSpace.getResString("create.arg.pdf_destination.name"), task.getDocumentArgument().getDestination());
        System.out.printf("\t%s:%s\n", SharedSpace.getResString("public.info.name"),
                new File(task.getDocumentArgument().getDestination().getName()));
        System.out.printf("\t%s:\n", SharedSpace.getResString("public.info.progress"));

    }

    @Override
    public void onConverting(int index,File file) {
        progress += perImg;
        while (progress >= 1) {
            progress_bar[counter]='=';
            progress -= 1;
            counter++;
        }
        System.out.printf("\t[%s] %d/%d images added. %s\r",new String(progress_bar),index+1,total,file.getName());
    }

    @Override
    public void onConversionComplete(File dst) {
        System.out.print("\r\n");
        counter=total=0;
    }


    @Override
    public void onConversionFail(int index, Exception e) {
//            System.out.print("CONVERSION FAIL]\n\n");
        System.err.println(e.getMessage());
        counter=total=0;
    }

    @Override
    public void onImageReadFail(int index, IOException e) {
//            System.out.print("IMAGE READ FAIL]\n\n");
        System.err.println(e.getMessage());
        counter=total=0;
    }

}
