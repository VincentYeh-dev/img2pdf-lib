package org.vincentyeh.IMG2PDF.pdf.page.core;

class SizeCalculator {
    private static SizeCalculator calculator = null;

    public static SizeCalculator getInstance() {
        if (calculator == null)
            calculator = new SizeCalculator();
        return calculator;
    }


    public Size scaleUpToMax(Size img_size, Size page_size) {
        float img_height = img_size.getHeight();
        float img_width = img_size.getWidth();
        float page_height = page_size.getHeight();
        float page_width = page_size.getWidth();

        float out_width = (img_width / img_height) * page_height;
        float out_height = (img_height / img_width) * page_width;
        if (out_height <= page_height) {
//			width fill
            out_width = page_width;
//			out_height = (img_height / img_width) * page_width;
        } else if (out_width <= page_width) {
//			height fill
            out_height = page_height;
//			out_width = (img_width / img_height) * page_height;
        }

        return new Size(out_height, out_width);
    }


}
