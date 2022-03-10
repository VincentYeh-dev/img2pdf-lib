package org.vincentyeh.IMG2PDF.pdf.function.converter;

import org.vincentyeh.IMG2PDF.image.helper.framework.ImageHelper;
import org.vincentyeh.IMG2PDF.pdf.framework.appender.PageAppender;
import org.vincentyeh.IMG2PDF.pdf.framework.calculation.strategy.ImagePageCalculateStrategy;
import org.vincentyeh.IMG2PDF.pdf.framework.converter.PDFCreator;
import org.vincentyeh.IMG2PDF.pdf.framework.converter.PDFCreatorImpl;
import org.vincentyeh.IMG2PDF.pdf.framework.converter.exception.ReadImageException;
import org.vincentyeh.IMG2PDF.pdf.framework.listener.PDFCreationListener;
import org.vincentyeh.IMG2PDF.pdf.framework.objects.PdfDocument;
import org.vincentyeh.IMG2PDF.pdf.framework.objects.PdfPage;
import org.vincentyeh.IMG2PDF.task.framework.Task;
import org.vincentyeh.IMG2PDF.util.file.FileUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

public class ImagePDFCreator extends PDFCreator<PDFCreationListener> {
    private final ImagePageCalculateStrategy strategy;
    private final ImageHelper helper;

    public ImagePDFCreator(PDFCreatorImpl impl, PageAppender pageAppender, boolean overwrite, ImagePageCalculateStrategy strategy, ImageHelper helper) {
        super(impl, pageAppender, overwrite);
        this.strategy = strategy;
        this.helper = helper;
    }

    @Override
    protected List<Callable<PdfPage<?>>> getPageCallables(PdfDocument<?> document, Task task) {
        File[] files = task.getImages();
        List<Callable<PdfPage<?>>> list = new LinkedList<>();
        for (File file : files) {
            list.add(() -> {
                PdfPage<?> page = impl.createEmptyPage(document);
                BufferedImage bufferedImage = readImage(file);
                strategy.execute(task.getPageArgument(), bufferedImage);
                page.setSize(strategy.getPageSize());
                page.putImage(bufferedImage, strategy.getImagePosition(), strategy.getImageSize());
                return page;
            });
        }
        return list;
    }


    private BufferedImage readImage(File file) throws ReadImageException {
        try {
            FileUtils.checkExists(file);
            BufferedImage image = helper.read(file);
            if (image == null)
                throw new RuntimeException("image==null");
            return image;
        } catch (Exception e) {
            throw new ReadImageException(e, file);
        }
    }
}
