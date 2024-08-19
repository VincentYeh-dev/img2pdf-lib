package org.vincentyeh.img2pdf.pdf.factory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.vincentyeh.img2pdf.pdf.concrete.factory.DefaultImageScalingStrategy;
import org.vincentyeh.img2pdf.pdf.framework.objects.SizeF;
import org.vincentyeh.img2pdf.pdf.parameter.PageAlign;
import org.vincentyeh.img2pdf.pdf.parameter.PageArgument;
import org.vincentyeh.img2pdf.pdf.parameter.PageDirection;
import org.vincentyeh.img2pdf.pdf.parameter.PageSize;

public class StrategyTest {
    @Test
    public void MathTest1() {
        DefaultImageScalingStrategy strategy = new DefaultImageScalingStrategy();
        strategy.execute(new PageArgument(PageSize.A4), new SizeF(100, 100));
        Assertions.assertEquals(strategy.getPageSize(), PageSize.A4.getSizeInPixels());

        Assertions.assertEquals(strategy.getImageSize().width, PageSize.A4.getSizeInPixels().width);
        Assertions.assertNotEquals(strategy.getImageSize().height, PageSize.A4.getSizeInPixels().height);

    }

    @Test
    public void MathTest2() {
        DefaultImageScalingStrategy strategy = new DefaultImageScalingStrategy();
        strategy.execute(new PageArgument(PageSize.A4), new SizeF(1000, 1000));
        Assertions.assertEquals(strategy.getPageSize(), PageSize.A4.getSizeInPixels());

        Assertions.assertEquals(strategy.getImageSize().width, PageSize.A4.getSizeInPixels().width);
        Assertions.assertNotEquals(strategy.getImageSize().height, PageSize.A4.getSizeInPixels().height);

    }

    @Test
    public void MathTest3() {
        DefaultImageScalingStrategy strategy = new DefaultImageScalingStrategy();
        SizeF img_size = new SizeF(1000, 1000);
        strategy.execute(
                new PageArgument(
                        PageAlign.VerticalAlign.CENTER,
                        PageAlign.HorizontalAlign.CENTER,
                        PageSize.DEPEND_ON_IMG,
                        PageDirection.Portrait
                ), img_size);
        Assertions.assertEquals(strategy.getPageSize(), img_size);
    }

    @Test
    public void MathTest4() {
        DefaultImageScalingStrategy strategy = new DefaultImageScalingStrategy();
        SizeF img_size = new SizeF(500, 1000);
        strategy.execute(
                new PageArgument(
                        PageAlign.VerticalAlign.CENTER,
                        PageAlign.HorizontalAlign.CENTER,
                        PageSize.A4,
                        PageDirection.Landscape
                ), img_size);
        Assertions.assertEquals(strategy.getPageSize().height, PageSize.A4.getSizeInPixels().width);
        Assertions.assertEquals(strategy.getPageSize().width, PageSize.A4.getSizeInPixels().height);
    }

    @Test
    public void MathTest5() {
        DefaultImageScalingStrategy strategy = new DefaultImageScalingStrategy();
        SizeF img_size = new SizeF(5000, 1000);
        strategy.execute(
                new PageArgument(
                        PageAlign.VerticalAlign.CENTER,
                        PageAlign.HorizontalAlign.CENTER,
                        PageSize.A4
                ), img_size);
        Assertions.assertEquals(strategy.getPageSize().width, PageSize.A4.getSizeInPixels().height);
    }
}

