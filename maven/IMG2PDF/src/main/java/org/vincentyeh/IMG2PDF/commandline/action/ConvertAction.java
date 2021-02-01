package org.vincentyeh.IMG2PDF.commandline.action;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.vincentyeh.IMG2PDF.pdf.converter.ConversionListener;
import org.vincentyeh.IMG2PDF.pdf.converter.PDFConverter;
import org.vincentyeh.IMG2PDF.pdf.document.ImagesPDFDocument;
import org.vincentyeh.IMG2PDF.task.Task;
import org.vincentyeh.IMG2PDF.task.TaskList;

import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

public class ConvertAction extends AbstractAction {

	protected ArrayList<String> tasklist_sources;
	protected boolean open_when_complete = false;

	@Override
	public void setupByNamespace(Namespace ns) {
		this.tasklist_sources = ns.get("tasklist_source");
		this.open_when_complete=ns.getBoolean("open_when_complete");

	}

	@Override
	public void start() throws Exception {
		super.start();
		System.out.println("Import tasklists.");
		for (String str_src : tasklist_sources) {
			File source = new File(str_src);
			if (!source.exists())
				throw new FileNotFoundException("File not found:" + source.getAbsolutePath());

			TaskList tasks = null;

			tasks = new TaskList(source);

			System.out.println("\t[imported] " + source.getAbsolutePath() + "\n");

			startConversion(tasks);
		}

		super.done();
	}

	private void startConversion(TaskList tasks) {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		for (Task task : tasks) {
			ImagesPDFDocument result = null;

			try {
				PDFConverter pdf = new PDFConverter(task);
				pdf.setListener(listener);
				Future<ImagesPDFDocument> future = executor.submit(pdf);
				try {
					result = future.get();
				} catch (InterruptedException | ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				result.save();

				if (open_when_complete) {
					Desktop desktop = Desktop.getDesktop();

					File dst = new File(task.getDestination());

					if (dst.exists())
						try {
							desktop.open(dst);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}

			} catch (IOException e) {
				e.printStackTrace();
			} catch (RuntimeException e) {
				e.printStackTrace();
			} finally {
				try {
					if (result != null)
						result.close();
				} catch (IOException ignore) {
				}
			}

		}
		executor.shutdown();
	}

	public static void setupParser(Subparsers subparsers) {
		Subparser convert_parser = subparsers.addParser("convert").help(lagug_resource.getString("help_convert"));
		convert_parser.setDefault("action", new ConvertAction());
		convert_parser.addArgument("tasklist_source").nargs("*")
				.help(lagug_resource.getString("help_convert_tasklist_source"));
		
		convert_parser.addArgument("-o","--open_when_complete").required(false).type(Arguments.booleanType("yes", "no")).setDefault(Boolean.FALSE)
		.help("Open PDF after conversion is completed.");

	}

	private ConversionListener listener = new ConversionListener() {
		private int size_of_imgs;
		private double perImg;
		private double progress = 0;

		@Override
		public void onConversionPreparing(Task task) {
			size_of_imgs = task.getImgs().size();
			perImg = (10. / size_of_imgs);
			System.out.println("###PDF Conversion Task###");
			System.out.printf("destination:%s\n", task.getDestination());
			System.out.printf("name:%s\n", new File(task.getDestination()).getName());
			System.out.print("\nProgress->");
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

//			try {
//				Runtime.getRuntime().exec("explorer.exe /select," + );
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
