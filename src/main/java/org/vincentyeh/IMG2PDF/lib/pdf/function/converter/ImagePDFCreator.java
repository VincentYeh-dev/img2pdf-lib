package org.vincentyeh.img2pdf.lib.pdf.function.converter;

import org.vincentyeh.img2pdf.lib.pdf.framework.appender.PageAppender;
import org.vincentyeh.img2pdf.lib.pdf.framework.objects.SizeF;
import org.vincentyeh.img2pdf.lib.pdf.framework.calculation.strategy.ImagePageCalculateStrategy;
import org.vincentyeh.img2pdf.lib.pdf.framework.converter.PDFCreator;
import org.vincentyeh.img2pdf.lib.pdf.framework.converter.PDFCreatorImpl;
import org.vincentyeh.img2pdf.lib.pdf.framework.objects.PdfDocument;
import org.vincentyeh.img2pdf.lib.pdf.framework.objects.PdfPage;
import org.vincentyeh.img2pdf.lib.pdf.parameter.DocumentArgument;
import org.vincentyeh.img2pdf.lib.pdf.parameter.PageArgument;

import javax.imageio.ImageIO;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

public class ImagePDFCreator extends PDFCreator {
    private final ImageReadImpl imageReadImpl;
    private final ImagePageCalculateStrategy strategy;
    private final PageArgument pageArgument;
    private File[] imageFiles;

    public ImagePDFCreator(PageArgument pageArgument, DocumentArgument documentArgument, PDFCreatorImpl pdfCreatorImpl, ImageReadImpl imageReadImpl,
                           PageAppender pageAppender, boolean overwrite, ImagePageCalculateStrategy strategy) {
        super(documentArgument,pdfCreatorImpl, pageAppender, overwrite);
        this.pageArgument = pageArgument;
        this.imageReadImpl = imageReadImpl;
        this.strategy = strategy;
    }

    public void setImages(File[] imageFiles){
        this.imageFiles = imageFiles;
    }

    @Override
    protected List<Callable<PdfPage<?>>> getPageCallables(PdfDocument<?> document) {
        var list = new LinkedList<Callable<PdfPage<?>>>();
        if(imageFiles!=null)
            for (File file : imageFiles) {
                list.add(() -> {
                    PdfPage<?> page = impl.createEmptyPage(document);
                    var bufferedImage =imageReadImpl!=null?imageReadImpl.readImage(file): ImageIO.read(file);
                    strategy.execute(this.pageArgument,new SizeF(bufferedImage.getWidth(),bufferedImage.getHeight()));
                    page.setSize(strategy.getPageSize());
                    page.putImage(bufferedImage, strategy.getImagePosition(), strategy.getImageSize());
                    return page;
                });
            }
        imageFiles=null;
        return list;
    }

}
