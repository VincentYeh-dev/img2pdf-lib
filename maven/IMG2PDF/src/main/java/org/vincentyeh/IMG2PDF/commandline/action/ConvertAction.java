package org.vincentyeh.IMG2PDF.commandline.action;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.vincentyeh.IMG2PDF.commandline.CheckHelpParser;
import org.vincentyeh.IMG2PDF.commandline.action.exception.ArgumentNotFoundException;
import org.vincentyeh.IMG2PDF.commandline.action.exception.HelperException;
import org.vincentyeh.IMG2PDF.pdf.converter.ConversionListener;
import org.vincentyeh.IMG2PDF.pdf.converter.PDFConverter;
import org.vincentyeh.IMG2PDF.pdf.document.ImagesPDFDocument;
import org.vincentyeh.IMG2PDF.pdf.page.PageSize;
import org.vincentyeh.IMG2PDF.task.Task;
import org.vincentyeh.IMG2PDF.task.TaskList;


public class ConvertAction extends AbstractAction {

	protected final File[] tasklist_sources;
	protected final boolean open_when_complete = false;

	private static final Option opt_help = new Option("h", "help", false, "help");

	public ConvertAction(String[] args) throws ParseException {
		this((new CheckHelpParser(opt_help)).parse(setupOptions(), args));
	}

	public ConvertAction(CommandLine cmd) throws HelperException, ArgumentNotFoundException {
		if (cmd.hasOption("-h"))
			throw new HelperException(setupOptions());

		String[] str_sources = cmd.getOptionValues("tasklist_source");

		if (str_sources == null)
			throw new ArgumentNotFoundException("sources");
		
		tasklist_sources = new File[str_sources.length];
		for (int i = 0; i < tasklist_sources.length; i++) {
			System.out.println("sources checking....");
			tasklist_sources[i] = new File(str_sources[i]);

			if (!tasklist_sources[i].exists()) {
				System.err.println("File not found:" + tasklist_sources[i].getAbsolutePath());
				continue;
			} else if (tasklist_sources[i].isDirectory()) {
				System.err.println("Path should be a file:" + tasklist_sources[i].getAbsolutePath());
				continue;
			} else {
				System.out.println("[Verified] " + tasklist_sources[i].getAbsolutePath());
			}

		}

	}

	@Override
	public void start() throws Exception {
		super.start();
		System.out.println("Import tasklists.");
		for (File src : tasklist_sources) {
			TaskList tasks = new TaskList(src);

			System.out.println("\t[imported] " + src.getAbsolutePath() + "\n");

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

	private static Options setupOptions() {
		Options options = new Options();
		Option opt_tasklist_source = createArgOption("lsrc", "tasklist_source", "help_convert_tasklist_source");
		opt_tasklist_source.setRequired(true);
		
		Option opt_open_when_complete = createArgOption("o", "open_when_complete", "help_create_pdf_align");
		opt_open_when_complete.setRequired(true);
		
		options.addOption(opt_help);
		options.addOption(opt_tasklist_source);
		options.addOption(opt_open_when_complete);

		Option opt_mode = new Option("m", "mode", true, "mode");
		options.addOption(opt_mode);

		return options;
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
