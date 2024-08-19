package org.vincentyeh.img2pdf.image;

public class ImageReadException extends RuntimeException{
    public ImageReadException(){

    }

    public ImageReadException(Throwable e){
        super(e);
    }

    public ImageReadException(String message,Throwable e){
        super(message,e);
    }
}
