package org.vincentyeh.IMG2PDF.commandline;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class Argparse4jTest {
	@Test
	public void spaceTest() {
		String[] raw = "-f \"Hello  W orld\" -b \"helloworld\"".split("\\s");
		String[] changed=compileSpaceSymbol(raw);
		
		ArgumentParser parser = ArgumentParsers.newFor("Test").build();
		parser.addArgument("-f","--foo");
		parser.addArgument("-b","--boom");
		Namespace ns = null;
		try {
			ns = fixSpaceSymbol(parser.parseArgs(changed));
			System.out.println(ns);
		} catch (ArgumentParserException e) {
			parser.handleError(e);
			System.err.println("Program is interrupted.");
			System.exit(1);
		}
	}
	
	String[] compileSpaceSymbol(String[] args) {
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
			String fixed = origin.replaceAll("\\s", "\\$SPACE");
			fixed=fixed.replace("\"", "");
			changed=changed.replace(origin, fixed);
		}
		return changed.split("\\s");
	}
	
	Namespace fixSpaceSymbol(Namespace raw) {
		Map<String,Object> data=raw.getAttrs();
		Iterator<String> a=data.keySet().iterator();
		while(a.hasNext()) {
			String key=a.next();
			Object obj=data.get(key);
			if(obj instanceof String) {
				String value=(String)obj;
				data.put(key, value.replace("$SPACE"," "));
			}
		}
		return new Namespace(data);
	}
}
