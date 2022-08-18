package org.vincentyeh.IMG2PDF.lib.pdf.framework.appender;

import org.vincentyeh.IMG2PDF.lib.pdf.framework.objects.PdfDocument;
import org.vincentyeh.IMG2PDF.lib.pdf.framework.objects.PdfPage;

import java.util.List;
import java.util.concurrent.Callable;

public abstract class PageAppender {

    public interface PageAppendListener {
        void onPageAppended(int index);
    }

    private PageAppendListener pageAppendListener;

    protected final void onPageAppended(int index) {
        if (pageAppendListener != null)
            pageAppendListener.onPageAppended(index);
    }

    public abstract void append(PdfDocument<?> document, List<Callable<PdfPage<?>>> list) throws Exception;


    public final void setPageAppendListener(PageAppendListener listener) {
        this.pageAppendListener = listener;
    }
}
