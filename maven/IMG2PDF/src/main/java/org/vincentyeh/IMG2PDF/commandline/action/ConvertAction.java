package org.vincentyeh.IMG2PDF.commandline.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.xml.parsers.ParserConfigurationException;

import org.vincentyeh.IMG2PDF.commandline.MainProgram;
import org.vincentyeh.IMG2PDF.pdf.converter.ConversionListener;
import org.vincentyeh.IMG2PDF.pdf.converter.PDFConverter;
import org.vincentyeh.IMG2PDF.pdf.document.ImagesPDFDocument;
import org.vincentyeh.IMG2PDF.task.Task;
import org.vincentyeh.IMG2PDF.task.TaskList;
import org.xml.sax.SAXException;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

public class ConvertAction extends AbstractAction {

	protected ArrayList<String> tasklist_sources;

	@Override
	public void setupByNamespace(Namespace ns) {
		this.tasklist_sources = ns.get("tasklist_source");

	}

	@Override
	public void start() {
		for (String filepath : tasklist_sources) {
			TaskList tasks = null;
			try {
				tasks = new TaskList(filepath);
			} catch (Exception e) {
				System.err.println("Unable to parse xml.\n" + e.getMessage());
				e.printStackTrace();
			}
			
			if (tasks != null)
				startConversion(tasks);
			
		}
	}

	private void startConversion(TaskList tasks) {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		for (Task task : tasks) {
			ImagesPDFDocument result = null;
			try {
				PDFConverter pdf = new PDFConverter(task);
				pdf.setListener(listener);
				Future<ImagesPDFDocument> future = executor.submit(pdf);
				result = future.get();

			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				if (result != null)
					result.save();
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}

			try {
				if (result != null)
					result.close();
			} catch (IOException ignore) {
				ignore.printStackTrace();
			}

		}
		executor.shutdown();
	}

	public static void setupParser(Subparsers subparsers) {
		Subparser convert_parser = subparsers.addParser("convert").help(lagug_resource.getString("help_convert"));
		convert_parser.setDefault("action", new ConvertAction());
		convert_parser.addArgument("tasklist_source").nargs("*")
				.help(lagug_resource.getString("help_convert_tasklist_source"));

	}

	private ConversionListener listener = new ConversionListener() {
		private int size_of_imgs;
		private double perImg;
		private double progress = 0;

		@Override
		public void onConversionPreparing(Task task) {
			size_of_imgs = task.getImgs().size();
			perImg = (10. / size_of_imgs);
			System.out.println("---PDF Conversion---");
			System.out.printf("destination:%s\n", task.getDestination());
			System.out.print("Progress->");
			System.out.print("0%[");

		}

		@Override
		public void onConverting(int index) {
			progress += perImg;
			while (progress >= 1) {
				System.out.print("=");
				progress -= 1;
			}
		}

		@Override
		public void onConversionComplete() {
			System.out.print("]%100\n");
//			if(isProtectedByPwd)
//				System.out.print(" *");
			System.out.println("DONE.\n\n");

//			try {
//				Runtime.getRuntime().exec("explorer.exe /select," + doc.getDestination());
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}

		@Override
		public void onConversionFail(int index, Exception e) {
			System.out.print("CONVERSION FAIL]\n");
			System.err.println(e.getMessage());
//			e.printStackTrace();

		}

		@Override
		public void onImageReadFail(int index, IOException e) {
			System.out.print("IMAGE READ FAIL]\n");
			System.err.println(e.getMessage());
//			e.printStackTrace();
		}

	};
}
