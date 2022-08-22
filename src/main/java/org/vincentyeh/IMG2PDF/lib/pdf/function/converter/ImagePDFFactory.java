package org.vincentyeh.img2pdf.lib.pdf.function.converter;

import org.vincentyeh.img2pdf.lib.pdf.concrete.calculation.strategy.StandardImagePageCalculationStrategy;
import org.vincentyeh.img2pdf.lib.pdf.framework.builder.PDFBuilder;
import org.vincentyeh.img2pdf.lib.pdf.framework.converter.PDFFactory;
import org.vincentyeh.img2pdf.lib.pdf.framework.objects.SizeF;
import org.vincentyeh.img2pdf.lib.pdf.parameter.DocumentArgument;
import org.vincentyeh.img2pdf.lib.pdf.parameter.PageArgument;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImagePDFFactory extends PDFFactory {
    private final ImageReadImpl imageReadImpl;
    private final StandardImagePageCalculationStrategy strategy;
    private final PageArgument pageArgument;
    private File[] imageFiles;

    public ImagePDFFactory(PageArgument pageArgument, DocumentArgument documentArgument, PDFBuilder builder, ImageReadImpl imageReadImpl,
                           boolean overwrite) {
        super(documentArgument, builder, overwrite);
        this.pageArgument = pageArgument;
        this.imageReadImpl = imageReadImpl;
        this.strategy = new StandardImagePageCalculationStrategy();
    }

    public void setImages(File[] imageFiles) {
        this.imageFiles = imageFiles;
    }

    @Override
    protected void AddPages(long procedure_id, PDFBuilder builder, Listener listener) {
        int i = 0;
        if (imageFiles != null)
            for (File file : imageFiles) {
                try {
                    BufferedImage bufferedImage = imageReadImpl != null ? imageReadImpl.readImage(file) : ImageIO.read(file);
                    strategy.execute(this.pageArgument, new SizeF(bufferedImage.getWidth(), bufferedImage.getHeight()));
                    var index = builder.addPage(strategy.getPageSize());
                    builder.addImage(index, bufferedImage, strategy.getImagePosition(), strategy.getImageSize());
                    if (listener != null)
                        listener.onAppend(procedure_id, i);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                i++;
            }
    }
}
