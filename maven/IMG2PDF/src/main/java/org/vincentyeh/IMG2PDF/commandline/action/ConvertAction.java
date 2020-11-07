package org.vincentyeh.IMG2PDF.commandline.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.xml.parsers.ParserConfigurationException;

import org.vincentyeh.IMG2PDF.file.ImgFile.Order;
import org.vincentyeh.IMG2PDF.file.ImgFile.Sortby;
import org.vincentyeh.IMG2PDF.pdf.CustomPDFConveter;
import org.vincentyeh.IMG2PDF.pdf.DocumentAccessPermission;
import org.vincentyeh.IMG2PDF.pdf.ImagesPDFDocument;
import org.vincentyeh.IMG2PDF.pdf.page.Align;
import org.vincentyeh.IMG2PDF.pdf.page.PageDirection;
import org.vincentyeh.IMG2PDF.pdf.page.Size;
import org.vincentyeh.IMG2PDF.task.configured.ConfiguredTask;
import org.vincentyeh.IMG2PDF.task.configured.ConfiguredTaskList;
import org.xml.sax.SAXException;

import net.sourceforge.argparse4j.annotation.Arg;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

public class ConvertAction implements Action {
	@Arg(dest = "tasklist_source")
	protected ArrayList<String> tasklist_sources;

	public ConvertAction() {

	}

	@Override
	public void setupByNamespace(Namespace ns) {
		this.tasklist_sources = ns.get("tasklist_source");

	}

	@Override
	public void start() {
		for (String filepath : tasklist_sources) {
			try {
				convertList(new ConfiguredTaskList(filepath));
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void convertList(ConfiguredTaskList tasks) {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		for (ConfiguredTask task : tasks) {
			try {
				CustomPDFConveter pdf = new CustomPDFConveter(task);
				Future<ImagesPDFDocument> future=executor.submit(pdf);
				ImagesPDFDocument result=future.get();
				result.save();
				result.close();
				
//				pdf.process();

			} catch (Exception e) {
				if(e.getMessage().equals("Unsupported Image Type")) {
					System.err.println("\nUnsupported Image Type\n");
				}
			}
		}
		executor.shutdown();
	}

	public static void setupParser(Subparsers subparsers) {
		Subparser convert_parser = subparsers.addParser("convert").help("Type \"convert -h\" to get more help.");
		convert_parser.setDefault("action", new ConvertAction());
		convert_parser.addArgument("tasklist_source").nargs("*");

	}
}
