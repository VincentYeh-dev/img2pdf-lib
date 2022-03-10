package org.vincentyeh.IMG2PDF.pdf.concrete.appender;

import org.vincentyeh.IMG2PDF.pdf.framework.appender.PageAppender;
import org.vincentyeh.IMG2PDF.pdf.framework.objects.PdfDocument;
import org.vincentyeh.IMG2PDF.pdf.framework.objects.PdfPage;

import java.util.List;
import java.util.concurrent.*;

public class ExecutorPageAppender implements PageAppender {

    private final int nThread;

    public ExecutorPageAppender(int nThread) {
        this.nThread = nThread;
    }

    @Override
    public void appendAll(PdfDocument<?> document, List<Callable<PdfPage<?>>> list) throws Exception {
        ExecutorService service = getExecutorService();
        try {
            List<Future<PdfPage<?>>> futures = service.invokeAll(list);
            int monitor = 0;
            int page_index = -1;
            boolean[] flag_done = new boolean[futures.size()];

            while (true) {
                Future<PdfPage<?>> pageFuture = futures.get(monitor);

                if (pageFuture.isDone()) {

                    if (!flag_done[monitor] && pageAppendListener != null)
                        pageAppendListener.onPageDone(monitor);

                    flag_done[monitor] = true;

                    if (monitor == page_index + 1) {
                        try {
                            document.addPage(pageFuture.get());
                        } catch (ExecutionException e) {
                            throw (Exception) e.getCause();
                        }
                        page_index = monitor;
                    }

                }

                if (page_index + 1 == futures.size())
                    break;

                if (monitor == futures.size() - 1)
                    monitor = 0;
                else
                    monitor++;
                try {
                    Thread.sleep(100);
                }catch (InterruptedException ignored){

                }
            }
        } finally {
            service.shutdown();
        }
    }

    private ExecutorService getExecutorService() {
        return Executors.newFixedThreadPool(nThread);
    }

    @Override
    public void append(PdfDocument<?> document, Callable<PdfPage<?>> callable) throws Exception {
        ExecutorService service = getExecutorService();
        try {
            Future<PdfPage<?>> future = service.submit(callable);
            while (!future.isDone()) {
                document.addPage(future.get());
            }
        } finally {
            service.shutdown();
        }
    }

    public interface PageAppendListener {
        void onPageDone(int index);
    }

    private PageAppendListener pageAppendListener;

    public void setPageAppendListener(PageAppendListener listener) {
        this.pageAppendListener = listener;
    }


}
