package org.vincentyeh.IMG2PDF.commandline;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import org.junit.Test;
import org.vincentyeh.IMG2PDF.commandline.action.exception.UnrecognizedEnumException;
import org.vincentyeh.IMG2PDF.pdf.page.PageDirection;
import org.vincentyeh.IMG2PDF.pdf.page.PageSize;
import org.vincentyeh.IMG2PDF.util.NameFormatter.ParentOverPointException;

/**
 * Test on https://regex101.com/.
 * 
 * @author VincentYeh
 */
public class UEETester {
	@Test
	public void testMsg() throws FileNotFoundException {
		UnrecognizedEnumException e=new UnrecognizedEnumException("A4",PageDirection.class);
		System.err.println(e.getMessage());
	}
}
