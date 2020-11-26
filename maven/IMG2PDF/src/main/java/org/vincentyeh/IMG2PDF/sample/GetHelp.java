package org.vincentyeh.IMG2PDF.sample;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import org.vincentyeh.IMG2PDF.commandline.MainProgram;

public class GetHelp {

	public static void main(String[] args) throws Exception {
		
//		MainProgram.main("create add -h");
		MainProgram.main("create import -h");
//		MainProgram.main("convert -h");
	}

}
