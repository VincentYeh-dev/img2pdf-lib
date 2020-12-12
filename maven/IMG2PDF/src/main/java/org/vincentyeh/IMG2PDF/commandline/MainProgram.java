package org.vincentyeh.IMG2PDF.commandline;

import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.vincentyeh.IMG2PDF.commandline.action.AbstractAction;
import org.vincentyeh.IMG2PDF.commandline.action.ConvertAction;
import org.vincentyeh.IMG2PDF.commandline.action.CreateAction;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparsers;

public class MainProgram {
	private static final String SYMBOL_SPACE="$SPACE";
	
	public static ResourceBundle lagug_resource;
	
	public final static String PROGRAM_NAME = "IMG2PDF";
	public final static String PROGRAM_VERSION = "v0.9";
	private final AbstractAction action;

	static {
		lagug_resource = ResourceBundle.getBundle("language_package", Locale.getDefault());
	}

	public MainProgram(String[] args) throws ArgumentParserException {
		args=compileSpaceSymbol(args);
		
		ArgumentParser parser = ArgumentParsers.newFor(PROGRAM_NAME).build();
		parser.version(PROGRAM_VERSION);

		AbstractAction.setLagug_resource(lagug_resource);
		parser.description(lagug_resource.getString("root_description"));
		Subparsers subparser = parser.addSubparsers().help(lagug_resource.getString("help_action"));
		CreateAction.setupParser(subparser);
		ConvertAction.setupParser(subparser);

		Namespace ns = null;
		try {
			ns = fixSpaceSymbol(parser.parseArgs(args));

		} catch (ArgumentParserException e) {
			parser.handleError(e);

			System.err.println("Program is interrupted.");
			throw e;
//			System.exit(1);
		}

		if (ns == null)
			throw new NullPointerException("Namespace is null.");

		action = (AbstractAction) ns.get("action");
		action.setupByNamespace(ns);
	}

	public void startCommand() {
		if (action == null)
			throw new NullPointerException("Action is null.");
		action.start();
	}

	public static void main(String[] args) {

		if (args == null) {
			System.err.println("args is null.");
			return;
		}

		MainProgram main = null;
		try {
			main = new MainProgram(args);
			main.startCommand();
		} catch (ArgumentParserException e) {
//			System.exit(0);
			return;
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}

//	Only for test.This function never been used in the release executable file.
	public static void main(String args) throws ArgumentParserException {
		main(args.split("\\s"));
	}
	
	
	private String[] compileSpaceSymbol(String[] args) {
		StringBuffer buffer=new StringBuffer();
		
		buffer.append(args[0]);
		for(int i=1;i<args.length;i++) {
			buffer.append(" ");
			buffer.append(args[i]);
		}
		String changed = buffer.toString();
		Pattern pattern = Pattern.compile("(\".*?\")");
		Matcher matcher = pattern.matcher(changed);
		
		while (matcher.find()) {
			String origin = matcher.group(1);
			String fixed = origin.replaceAll("\\s",'\\'+SYMBOL_SPACE);
			fixed=fixed.replace("\"", "");
			changed=changed.replace(origin, fixed);
		}
		return changed.split("\\s");
	}
	
	private Namespace fixSpaceSymbol(Namespace raw) {
		Map<String,Object> data=raw.getAttrs();
		Iterator<String> a=data.keySet().iterator();
		while(a.hasNext()) {
			String key=a.next();
			Object obj=data.get(key);
			if(obj instanceof String) {
				String value=(String)obj;
				data.put(key, value.replace(SYMBOL_SPACE," "));
			}
		}
		return new Namespace(data);
	}
}
