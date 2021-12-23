package org.vincentyeh.IMG2PDF.concrete.task.exception;


public class EmptyImagesException extends Exception {
    public EmptyImagesException(){
        super("No image was found.");
    }
}
