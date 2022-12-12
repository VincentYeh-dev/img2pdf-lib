package org.vincentyeh.img2pdf.lib.pdf.factory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.vincentyeh.img2pdf.lib.pdf.concrete.factory.StandardImagePageCalculationStrategy;
import org.vincentyeh.img2pdf.lib.pdf.framework.objects.SizeF;
import org.vincentyeh.img2pdf.lib.pdf.parameter.PageAlign;
import org.vincentyeh.img2pdf.lib.pdf.parameter.PageArgument;
import org.vincentyeh.img2pdf.lib.pdf.parameter.PageDirection;
import org.vincentyeh.img2pdf.lib.pdf.parameter.PageSize;

public class StrategyTest {
    @Test
    public void MathTest1() {
        StandardImagePageCalculationStrategy strategy = new StandardImagePageCalculationStrategy();
        strategy.execute(new PageArgument(PageSize.A4), new SizeF(100, 100));
        Assertions.assertEquals(strategy.getPageSize(), PageSize.A4.getSizeInPixels());

        Assertions.assertEquals(strategy.getImageSize().width, PageSize.A4.getSizeInPixels().width);
        Assertions.assertNotEquals(strategy.getImageSize().height, PageSize.A4.getSizeInPixels().height);

    }

    @Test
    public void MathTest2() {
        StandardImagePageCalculationStrategy strategy = new StandardImagePageCalculationStrategy();
        strategy.execute(new PageArgument(PageSize.A4), new SizeF(1000, 1000));
        Assertions.assertEquals(strategy.getPageSize(), PageSize.A4.getSizeInPixels());

        Assertions.assertEquals(strategy.getImageSize().width, PageSize.A4.getSizeInPixels().width);
        Assertions.assertNotEquals(strategy.getImageSize().height, PageSize.A4.getSizeInPixels().height);

    }

    @Test
    public void MathTest3() {
        StandardImagePageCalculationStrategy strategy = new StandardImagePageCalculationStrategy();
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
        StandardImagePageCalculationStrategy strategy = new StandardImagePageCalculationStrategy();
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
        StandardImagePageCalculationStrategy strategy = new StandardImagePageCalculationStrategy();
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

