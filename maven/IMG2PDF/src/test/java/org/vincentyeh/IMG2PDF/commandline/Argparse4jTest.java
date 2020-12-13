package org.vincentyeh.IMG2PDF.commandline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.vincentyeh.IMG2PDF.pdf.page.PageAlign;
import org.vincentyeh.IMG2PDF.pdf.page.PageSize;
import org.vincentyeh.IMG2PDF.util.ArgumentUtil;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class Argparse4jTest {

	@Test
	public void spaceTest1() throws ArgumentParserException {
		System.out.println("spaceTest1\n");
		String raw = "-a1 \"Hello World from program\" -a2 \"Hello2World\" -a3 Hello3World Hay";
		passArguments(raw);
		System.out.println("------------\n");
	}

	@Test(expected = ArgumentParserException.class)
	public void spaceTest2() throws ArgumentParserException {
		System.out.println("spaceTest2\n");
		String raw = "-a1 Hello World -a2 \"Hello2World\" -a3 Hello3World Hay";
		passArguments(raw);
		System.out.println("------------\n");
	}

	@Test(expected = ArgumentParserException.class)
	public void noQuotesTest1() throws ArgumentParserException {
		System.out.println("noQuotesTest1\n");
		String raw = "-a1 Hello World -a2 \"Hello2World\" -a3 Hello3World Hay";
		passArguments(raw);
		System.out.println("------------\n");
	}

	@Test
	public void noQuotesTest2() throws ArgumentParserException {
		System.out.println("noQuotesTest2\n");
		String raw = "-a1 \"Hello World\" -a2 Hello2World -a3 Hello3World Hay";
		passArguments(raw);
		System.out.println("------------\n");
	}

	@Test
	public void chineseTest() throws ArgumentParserException {
		System.out.println("chineseTest\n");
		String raw = "-a1 \"你好 世界\" -a2 你好2世界 -a3 你好3世界 你好4世界";
		passArguments(raw);
		System.out.println("------------\n");
	}

	@Test(expected = ArgumentParserException.class)
	public void chineseNoQuotesTest() throws ArgumentParserException {
		System.out.println("chineseTest\n");
		String raw = "-a1 你好 世界 -a2 你好2世界 -a3 你好3世界 你好4世界";
		passArguments(raw);
		System.out.println("------------\n");
	}

	@Test(expected = ArgumentParserException.class)
	public void noQuotesTest3() throws ArgumentParserException {
		System.out.println("noQuotesTest3\n");
		String raw = "-a1 \"Hello World\" -a2 \"Hello2World\" -a3 Hello3World Hello World";
		passArguments(raw);
		System.out.println("------------\n");
	}

	@Test(expected = ArgumentUtil.QuotesNotpairException.class)
	public void test1() throws ArgumentParserException {
		System.out.println("test1\n");
		String raw = "\"\"\"Hello world";
		passArguments(raw);
		System.out.println("------------\n");
	}

	@Test(expected = ArgumentUtil.QuotesNotpairException.class)
	public void test2() throws ArgumentParserException {
		System.out.println("test2\n");
		String raw = "\"\"Hello'\"world'";
		passArguments(raw);
		System.out.println("------------\n");
	}

	void passArguments(String raw) throws ArgumentParserException {
		String[] raw_arr = raw.split("\\s");
		System.out.println("raw string:" + raw);
		System.out.println("raw array:");
		dumpArray(raw_arr);

		System.out.println("new array:");
		String[] new_array = ArgumentUtil.fixArgumentSpaceArray(raw_arr);
		dumpArray(new_array);
		passToArgparse4(new_array);
	}

	@Test
	public void testVBAR() {
		String raw = "CENTER$VBARCENTER ";
		String[] raw_arr = raw.split("\\s");
		dumpArray(raw_arr);
		dumpArray(ArgumentUtil.fixSymbol(raw_arr));
	}

	void passToArgparse4(String[] args) throws ArgumentParserException {
		ArgumentParser parser = ArgumentParsers.newFor("Tester").build();
		parser.addArgument("-a1", "--arg1").type(String.class);
		parser.addArgument("-a2", "--arg2").type(String.class);
		parser.addArgument("-a3", "--arg3").type(String.class);
		parser.addArgument("p1").type(String.class);

		Namespace ns = null;
		ns = parser.parseArgs(args);
		System.out.println(ns.toString());
	}

	void dumpArray(String[] array) {
		System.out.print("[");
		System.out.print(array[0]);
		for (int i = 1; i < array.length; i++) {
			System.out.print(",");
			System.out.print(array[i]);
		}
		System.out.print("]\n");
	}

//	String[] fixArgumentArray(String[] args) {
//		StringBuffer buffer = new StringBuffer();
//
//		buffer.append(args[0]);
//		for (int i = 1; i < args.length; i++) {
//			buffer.append(" ");
//			buffer.append(args[i]);
//		}
//		String combined = buffer.toString();
//
////		((\s+|[^"]*\s+)("[^"]*")|([^"]*))*\s*
//		if (!combined.matches("((\\s+|[^\"]*\\s+)(\"[^\"]*\")|([^\"]*))*\\s*"))
//			throw new RuntimeException("symbol error.");
//
////		(\s+|[^"]*\s+)("[^"]*")
//		Pattern pattern = Pattern.compile("(\\s+|[^\"]*\\s+)(\"[^\"]*\")");
//		Matcher matcher = pattern.matcher(combined);
//		String replaced = combined;
//		while (matcher.find()) {
//			String origin = matcher.group(2);
//			String fixed = origin.replaceAll("\\s", "\\$SPACE");
//			fixed = fixed.replace("\"", "");
//			replaced = replaced.replace(origin, fixed);
//		}
//
//		String[] a = replaced.split("\\s");
//		for (int i = 0; i < a.length; i++) {
//			a[i] = a[i].replace("$SPACE", " ");
//		}
//		return a;
//	}
}
