package org.vincentyeh.IMG2PDF.commandline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.xml.parsers.ParserConfigurationException;

import org.vincentyeh.IMG2PDF.pdf.CustomPDFConveter;
import org.vincentyeh.IMG2PDF.pdf.ImagesPDFDocument;
import org.vincentyeh.IMG2PDF.task.configured.ConfiguredTask;
import org.vincentyeh.IMG2PDF.task.configured.ConfiguredTaskList;
import org.xml.sax.SAXException;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

/**
 * The Main Program
 * 
 * @author VincentYeh
 *
 */
public class TaskProcessor {
	private ArrayList<String> lists;

	public TaskProcessor(String args) throws IOException, ParserConfigurationException, SAXException {
		this(args.trim().split("\\s"));
	}

	public TaskProcessor(String[] args) throws IOException, ParserConfigurationException, SAXException {
		ArgumentParser parser = createArgParser();
		Arg2Values(parser, args);

		for (String filepath : lists) {
			start(new ConfiguredTaskList(filepath));
		}
	}

	public TaskProcessor(ConfiguredTaskList tasks) {
		start(tasks);
	}

	private void start(ConfiguredTaskList tasks) {
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

	public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
		new TaskProcessor(args);
	}
	
	private ArgumentParser createArgParser() {
		ArgumentParser parser = ArgumentParsers.newFor("IMG2PDF").build().defaultHelp(true)
				.description("Convert or merge image file to PDF");
		parser.addArgument("source").nargs("*").help("File to convert");
		return parser;
	}

	private void Arg2Values(ArgumentParser parser, String[] args) {
		Namespace ns = null;
		try {
			ns = parser.parseArgs(args);
		} catch (ArgumentParserException e) {
			parser.handleError(e);
			System.exit(1);
		}
		lists = new ArrayList<String>(ns.<String>getList("source"));
	}
}
