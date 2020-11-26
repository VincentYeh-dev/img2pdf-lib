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

public class ConvertAction extends AbstractAction{
	
	protected ArrayList<String> tasklist_sources;
	
//	protected static ResourceBundle MainProgram.lagug_resource;

//	static {
//		MainProgram.lagug_resource = ResourceBundle.getBundle("language_package",new Locale("en","US"));
//		MainProgram.lagug_resource = ResourceBundle.getBundle("language_package",MainProgram.locale);
//	}

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
				PDFConverter pdf = new PDFConverter(task);
				
				pdf.setListener(listener);
				Future<ImagesPDFDocument> future = executor.submit(pdf);
				ImagesPDFDocument result = future.get();
				result.save();
				result.close();
			} catch (Exception e) {
				if (e.getMessage().equals("Unsupported Image Type")) {
					System.err.println("\nUnsupported Image Type\n");
				}
				e.printStackTrace();
			}
		}
		executor.shutdown();
	}

	public static void setupParser(Subparsers subparsers) {
		Subparser convert_parser = subparsers.addParser("convert").help(lagug_resource.getString("help_convert"));
		convert_parser.setDefault("action", new ConvertAction());
		convert_parser.addArgument("tasklist_source").nargs("*").help(lagug_resource.getString("help_convert_tasklist_source"));
		
	}
	
	private ConversionListener listener=new ConversionListener() {
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
		public void onConverting(int index) throws Exception {
			progress += perImg;
			while (progress >= 1) {
				System.out.print("=");
				progress -= 1;
			}
		}

		@Override
		public void onConversionComplete() {
			System.out.print("]%100");
//			if(isProtectedByPwd)
//				System.out.print(" *");
			System.out.println("\nDONE.");
			
			System.out.println("\n\n");
			
//			try {
//				Runtime.getRuntime().exec("explorer.exe /select," + doc.getDestination());
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}

		@Override
		public void onConversionFail(int index, Exception e) {
			System.out.print("FAIL]");
			e.printStackTrace();
			
		}
	};
}
