package org.vincentyeh.img2pdf.lib.pdf.framework.objects;

public class ImageScalingResult {
    private final PointF imagePosition;
    private final SizeF pageSize;
    private final SizeF imageSize;

    public ImageScalingResult(PointF imagePosition, SizeF pageSize, SizeF imageSize) {
        this.imagePosition = imagePosition;
        this.pageSize = pageSize;
        this.imageSize = imageSize;
    }

    public PointF getImagePosition() {
        return imagePosition;
    }

    public SizeF getPageSize() {
        return pageSize;
    }

    public SizeF getImageSize() {
        return imageSize;
    }

}
