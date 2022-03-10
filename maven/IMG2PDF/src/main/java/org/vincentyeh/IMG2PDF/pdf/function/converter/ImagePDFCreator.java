package org.vincentyeh.IMG2PDF.pdf.function.converter;

import org.vincentyeh.IMG2PDF.image.helper.framework.ImageHelper;
import org.vincentyeh.IMG2PDF.pdf.framework.calculation.strategy.ImagePageCalculateStrategy;
import org.vincentyeh.IMG2PDF.pdf.framework.converter.PDFCreator;
import org.vincentyeh.IMG2PDF.pdf.framework.converter.PDFCreatorImpl;
import org.vincentyeh.IMG2PDF.pdf.framework.converter.exception.ReadImageException;
import org.vincentyeh.IMG2PDF.pdf.framework.listener.ImagePDFCreationListener;
import org.vincentyeh.IMG2PDF.pdf.framework.objects.PdfDocument;
import org.vincentyeh.IMG2PDF.pdf.framework.objects.PdfPage;
import org.vincentyeh.IMG2PDF.task.framework.Task;
import org.vincentyeh.IMG2PDF.util.file.FileUtils;

import java.awt.image.BufferedImage;
import java.io.File;

public class ImagePDFCreator extends PDFCreator<ImagePDFCreationListener> {
    private final ImagePageCalculateStrategy strategy;
    private final ImageHelper helper;

    public ImagePDFCreator(PDFCreatorImpl impl, boolean overwrite, ImagePageCalculateStrategy strategy, ImageHelper helper) {
        super(impl, overwrite);
        this.strategy = strategy;
        this.helper = helper;
    }

    @Override
    protected PageIterator getPageIterator(PdfDocument<?> document, Task task) {
        File[] files = task.getImages();
        return new PageIterator() {
            int next = 0;

            @Override
            public PdfPage<?> next() throws Exception {
                if (listener != null)
                    listener.onAppendingImage(next, files.length, files[next]);

                PdfPage<?> page = impl.createEmptyPage(document);

                BufferedImage bufferedImage = readImage(files[next++]);
                strategy.execute(task.getPageArgument(), bufferedImage);
                page.setSize(strategy.getPageSize());
                page.putImage(bufferedImage, strategy.getImagePosition(), strategy.getImageSize());
                return page;
            }

            @Override
            public boolean hasNext() {
                return next < files.length;
            }
        };
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
