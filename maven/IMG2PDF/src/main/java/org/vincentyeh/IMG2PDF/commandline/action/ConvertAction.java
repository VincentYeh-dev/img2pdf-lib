package org.vincentyeh.IMG2PDF.commandline.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.xml.parsers.ParserConfigurationException;

import org.vincentyeh.IMG2PDF.pdf.converter.CustomPDFConveter;
import org.vincentyeh.IMG2PDF.pdf.document.ImagesPDFDocument;
import org.vincentyeh.IMG2PDF.task.Task;
import org.vincentyeh.IMG2PDF.task.TaskList;
import org.xml.sax.SAXException;

import net.sourceforge.argparse4j.annotation.Arg;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

public class ConvertAction implements Action {
	
	protected ArrayList<String> tasklist_sources;
	
	protected static ResourceBundle lagug_resource;

	static {
//		lagug_resource = ResourceBundle.getBundle("language_package",new Locale("en","US"));
		lagug_resource = ResourceBundle.getBundle("language_package", Locale.getDefault());
		
	}

	@Override
	public void setupByNamespace(Namespace ns) {
		this.tasklist_sources = ns.get("tasklist_source");

	}

	@Override
	public void start() {
		for (String filepath : tasklist_sources) {
			try {
				convertList(new TaskList(filepath));
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

	private void convertList(TaskList tasks) {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		for (Task task : tasks) {
			try {
				CustomPDFConveter pdf = new CustomPDFConveter(task);
				Future<ImagesPDFDocument> future = executor.submit(pdf);
				ImagesPDFDocument result = future.get();
				result.save();
				result.close();


			} catch (Exception e) {
				if (e.getMessage().equals("Unsupported Image Type")) {
					System.err.println("\nUnsupported Image Type\n");
				}
			}
		}
		executor.shutdown();
	}

	public static void setupParser(Subparsers subparsers) {
		Subparser convert_parser = subparsers.addParser("convert").help(lagug_resource.getString("help_convert"));
		convert_parser.setDefault("action", new ConvertAction());
		convert_parser.addArgument("tasklist_source").nargs("*").help(lagug_resource.getString("help_convert_tasklist_source"));
		
	}
}
