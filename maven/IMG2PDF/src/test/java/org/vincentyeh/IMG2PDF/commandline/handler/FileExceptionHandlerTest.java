package org.vincentyeh.IMG2PDF.commandline.handler;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.vincentyeh.IMG2PDF.commandline.handler.core.ResourceBundleHandler;
import org.vincentyeh.IMG2PDF.pattern.Handler;
import org.vincentyeh.IMG2PDF.util.file.exception.*;

import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileExceptionHandlerTest {
    private static FileExceptionHandler handler;
    @BeforeAll
    public static void initialize(){
        ResourceBundleHandler.setResourceBundle(ResourceBundle.getBundle("cmd", Locale.ROOT));
        handler=new FileExceptionHandler(null);
    }

    @Test
    public void testFileExistsException() throws Handler.CantHandleException {
        String result=handler.handle(new FileNotExistsException(new File("AAAA")));
        System.out.println(result);
        assertEquals("File not found : AAAA",result);
    }
    @Test
    public void testInvalidFile() throws Handler.CantHandleException {
        String result=handler.handle(new InvalidFileException(new File("AAAA")));
        System.out.println(result);
        assertEquals("Invalid file : AAAA",result);
    }

    @Test
    public void testWrongFileTypeException() throws Handler.CantHandleException {
        String result=handler.handle(new WrongFileTypeException(WrongFileTypeException.Type.FILE, WrongFileTypeException.Type.FOLDER,new File("AAAA")));
        System.out.println(result);
        assertEquals("Path should be a file,but was folder: AAAA",result);
    }

    @Test
    public void testNoParentException() throws Handler.CantHandleException {
        String result=handler.handle(new NoParentException(new File("AAAA")));
        System.out.println(result);
        assertEquals("No parent above : AAAA",result);
    }


}
