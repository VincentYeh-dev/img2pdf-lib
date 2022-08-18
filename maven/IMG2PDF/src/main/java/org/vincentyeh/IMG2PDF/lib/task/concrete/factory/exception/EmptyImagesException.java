package org.vincentyeh.IMG2PDF.lib.task.concrete.factory.exception;


public class EmptyImagesException extends Exception {
    public EmptyImagesException(){
        super("No image was found.");
    }
}
