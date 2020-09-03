package org.vincentyeh.IMG2PDF.commandline;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Encoder;

import org.bouncycastle.util.encoders.Base64Encoder;
import org.vincentyeh.IMG2PDF.file.FileFilterHelper;

public class FileFinder {

	ArrayList<File> target = new ArrayList<File>();
	private int condition;
	private File folder;

	public FileFinder(File folder, int condition) {
		this.condition = condition;
		this.folder = folder;

	}

	public static void main(String[] args) throws IOException {

		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[0]), "UTF-8"));

		FileFinder ff = new FileFinder(new File(args[1]), FileFilterHelper.CONDITION_IS_DIR);

		FileFilterHelper ffh = new FileFilterHelper(ff.condition);

		File[] files = ff.folder.listFiles(ffh);
		
		for (File f : files) {
			System.out.println(f.getAbsolutePath());
			out.write(f.getAbsolutePath());
			out.write("\n");
		}
		out.close();
	}

}
