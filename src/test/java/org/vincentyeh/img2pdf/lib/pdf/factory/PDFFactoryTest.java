package org.vincentyeh.img2pdf.lib.pdf.factory;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.vincentyeh.img2pdf.lib.pdf.concrete.factory.PDFBoxImagePDFFactory;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.ImageScalingStrategy;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.exception.PDFFactoryException;
import org.vincentyeh.img2pdf.lib.pdf.parameter.DocumentArgument;
import org.vincentyeh.img2pdf.lib.pdf.parameter.PageArgument;

public class PDFFactoryTest {

    @Test
    public void ConstructorIllegalArgumentTest() {

        Assertions.assertThrows(IllegalArgumentException.class, () ->
                new PDFBoxImagePDFFactory(null, 1));

        ImageScalingStrategy imageScalingStrategy = Mockito.mock(ImageScalingStrategy.class);
        Assertions.assertDoesNotThrow(() ->
                new PDFBoxImagePDFFactory(imageScalingStrategy, 1));

        Assertions.assertThrows(IllegalArgumentException.class, () ->
                new PDFBoxImagePDFFactory(imageScalingStrategy,-1));

        Assertions.assertThrows(IllegalArgumentException.class, () ->
                new PDFBoxImagePDFFactory(imageScalingStrategy,0));


    }

    @Test
    public void StartNullArgumentTest() {
        PageArgument pageArgument = Mockito.mock(PageArgument.class);
        DocumentArgument documentArgument = Mockito.mock(DocumentArgument.class);
        ImageScalingStrategy imageScalingStrategy = Mockito.mock(ImageScalingStrategy.class);
        PDFBoxImagePDFFactory factory = new PDFBoxImagePDFFactory(imageScalingStrategy);
        Assertions.assertThrows(PDFFactoryException.class, () -> factory.start(null, null, null,null));
        Assertions.assertDoesNotThrow(() -> factory.shutdown());

    }

}
