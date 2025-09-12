package org.vincentyeh.img2pdf.lib.pdf.factory;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.vincentyeh.img2pdf.lib.pdf.concrete.factory.PDFBoxImagePDFFactory;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.ImageScalingStrategy;
import org.vincentyeh.img2pdf.lib.pdf.parameter.DocumentArgument;
import org.vincentyeh.img2pdf.lib.pdf.parameter.PageArgument;

public class PDFFactoryTest {

    @Test
    public void ConstructorNullArgumentTest() {

        PageArgument pageArgument = Mockito.mock(PageArgument.class);
        DocumentArgument documentArgument = Mockito.mock(DocumentArgument.class);
        ImageScalingStrategy imageScalingStrategy = Mockito.mock(ImageScalingStrategy.class);
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                new PDFBoxImagePDFFactory(pageArgument, null, null));
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                new PDFBoxImagePDFFactory(pageArgument, documentArgument, null));

        Assertions.assertThrows(IllegalArgumentException.class, () ->
                new PDFBoxImagePDFFactory(null, null, null));

        Assertions.assertDoesNotThrow(() ->
                new PDFBoxImagePDFFactory(pageArgument, documentArgument, imageScalingStrategy));

    }

}
