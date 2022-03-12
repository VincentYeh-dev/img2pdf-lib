package org.vincentyeh.IMG2PDF.pdf.function.converter;

import org.vincentyeh.IMG2PDF.pdf.framework.appender.PageAppender;
import org.vincentyeh.IMG2PDF.pdf.framework.calculation.Size;
import org.vincentyeh.IMG2PDF.pdf.framework.calculation.strategy.ImagePageCalculateStrategy;
import org.vincentyeh.IMG2PDF.pdf.framework.converter.PDFCreator;
import org.vincentyeh.IMG2PDF.pdf.framework.converter.PDFCreatorImpl;
import org.vincentyeh.IMG2PDF.pdf.framework.objects.PdfDocument;
import org.vincentyeh.IMG2PDF.pdf.framework.objects.PdfPage;
import org.vincentyeh.IMG2PDF.task.framework.Task;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

public class ImagePDFCreator extends PDFCreator {
    private final ImagePDFCreatorImpl imagePDFCreatorImpl;
    private final ImagePageCalculateStrategy strategy;

    public ImagePDFCreator(PDFCreatorImpl pdfCreatorImpl, ImagePDFCreatorImpl imagePDFCreatorImpl, PageAppender pageAppender, boolean overwrite, ImagePageCalculateStrategy strategy) {
        super(pdfCreatorImpl, pageAppender, overwrite);
        this.imagePDFCreatorImpl = imagePDFCreatorImpl;
        this.strategy = strategy;
    }

    @Override
    protected List<Callable<PdfPage<?>>> getPageCallables(PdfDocument<?> document, Task task) {
        File[] files = task.getImages();
        List<Callable<PdfPage<?>>> list = new LinkedList<>();
        for (File file : files) {
            list.add(() -> {
                PdfPage<?> page = impl.createEmptyPage(document);
                BufferedImage bufferedImage = imagePDFCreatorImpl.readImage(file);
                strategy.execute(task.getPageArgument(),new Size(bufferedImage.getWidth(),bufferedImage.getHeight()));
                page.setSize(strategy.getPageSize());
                page.putImage(bufferedImage, strategy.getImagePosition(), strategy.getImageSize());
                return page;
            });
        }
        return list;
    }

}
