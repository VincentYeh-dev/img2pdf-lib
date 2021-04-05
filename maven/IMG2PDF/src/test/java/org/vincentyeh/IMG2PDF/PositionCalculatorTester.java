package org.vincentyeh.IMG2PDF;

import org.junit.Assert;
import org.junit.ComparisonFailure;
import org.junit.Test;
import org.vincentyeh.IMG2PDF.pdf.page.PageAlign;
import org.vincentyeh.IMG2PDF.pdf.page.core.Position;
import org.vincentyeh.IMG2PDF.pdf.page.core.PositionCalculator;

import java.util.Locale;
import java.util.ResourceBundle;

public class PositionCalculatorTester {
    static {
//		Language Setting:

//	Local:
        Configuration.setLagugRes(ResourceBundle.getBundle("language_package", Locale.getDefault()));
//	root:

//		Configuration.setLagugRes(ResourceBundle.getBundle("language_package", Locale.ROOT));

    }

    @Test
    public void test() {

        PositionCalculator calculator = new PositionCalculator(false, 1024, 1024, 2048, 2048);

        Position pos = calculator.calculate(new PageAlign("TOP-CENTER"));
        EqualsPosition(new Position(2048-512-1024,1024),pos);


        pos = calculator.calculate(new PageAlign("TOP-LEFT"));
        EqualsPosition(new Position(0,1024),pos);

        pos = calculator.calculate(new PageAlign("TOP-RIGHT"));
        EqualsPosition(new Position(1024,1024),pos);


    }


    @Test
    public void testRotated() {
        PositionCalculator calculator = new PositionCalculator(true, 1024, 1024, 2048, 2048);

        Position pos = calculator.calculate(new PageAlign("TOP-CENTER"));
        EqualsPosition(new Position(0,2048-512-1024),pos);

        pos = calculator.calculate(new PageAlign("TOP-LEFT"));
        EqualsPosition(new Position(0,0),pos);

        pos = calculator.calculate(new PageAlign("TOP-RIGHT"));
        EqualsPosition(new Position(0,1024),pos);

    }

    void EqualsPosition(Position excepted,Position a){
        String str_a=String.format("(%d,%d)",(int)a.getX(),(int)a.getY());
        String str_excepted=String.format("(%d,%d)",(int)excepted.getX(),(int)excepted.getY());
        Assert.assertEquals(str_excepted,str_a);
    }
}
