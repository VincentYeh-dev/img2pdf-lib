package org.vincentyeh.IMG2PDF.lib.pdf.function.converter;

import org.vincentyeh.IMG2PDF.lib.pdf.framework.appender.PageAppender;
import org.vincentyeh.IMG2PDF.lib.pdf.framework.calculation.strategy.ImagePageCalculateStrategy;
import org.vincentyeh.IMG2PDF.lib.pdf.framework.objects.PdfDocument;
import org.vincentyeh.IMG2PDF.lib.pdf.framework.objects.PdfPage;
import org.vincentyeh.IMG2PDF.lib.task.framework.Task;
import org.vincentyeh.IMG2PDF.lib.pdf.framework.calculation.Size;
import org.vincentyeh.IMG2PDF.lib.pdf.framework.converter.PDFCreator;
import org.vincentyeh.IMG2PDF.lib.pdf.framework.converter.PDFCreatorImpl;

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
        if(imagePDFCreatorImpl==null)
            throw new IllegalArgumentException("imagePDFCreatorImpl is null.");
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
