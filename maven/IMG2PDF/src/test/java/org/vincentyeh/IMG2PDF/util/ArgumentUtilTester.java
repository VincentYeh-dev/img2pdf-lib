package org.vincentyeh.IMG2PDF.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.vincentyeh.IMG2PDF.util.ArgumentUtil.ArgumentSyntaxException;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.helper.HelpScreenException;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ArgumentUtilTester {

	@Test(expected = HelpScreenException.class)
	public void testHelp() throws ArgumentParserException {
		passArgument("-h");
	}

//	-----------------------
	@Test(expected = ArgumentParserException.class)
	public void testPositionalNoQuotes() throws ArgumentParserException {
		passArgument("Hello World. Nice to meet you human!!");
	}

	@Test
	public void testPositionalWithQuotes() throws ArgumentParserException {
		String raw = "\"Hello World. Nice to meet you human!!\"";
		String expected = "Hello World. Nice to meet you human!!";
		Namespace ns = passArgument(raw);
		System.out.println(ns);
		assertEquals(expected, ns.get("p1").toString());
	}
	
	@Test
	public void testPositional_Prefix_WithQuotes() throws ArgumentParserException {
		String raw = "-a1 XXX \"Hello World. Nice to meet you human!!\"";
		String expected = "Hello World. Nice to meet you human!!";
		Namespace ns = passArgument(raw);
		System.out.println(ns);
		assertEquals(expected, ns.get("p1").toString());
	}
//	-----------------------

//	-----------------------
	@Test(expected = ArgumentParserException.class)
	public void testPrefixNoQuotes() throws ArgumentParserException {
		passArgument("-a1 Hello World. Nice to meet you human!!");
	}

	@Test
	public void testPrefixWithQuotes() throws ArgumentParserException {
		String raw = "-a1 \"Hello World. Nice to meet you human!!\" Hello";
		String expected = "Hello World. Nice to meet you human!!";
		Namespace ns = passArgument(raw);
		System.out.println(ns);
		assertEquals(expected, ns.get("arg1").toString());
	}
//	-----------------------

	@Test
	public void testNonSense() throws ArgumentParserException {
		System.out.println("##########testNonSense##########");
		ArrayList<String> test_list = new ArrayList<>();
		test_list.add("-a1 \"Hello Wo\"rld. Nice to meet you human!!\" Hello");
		test_list.add("-a1 \"\"Hello World. Nice to meet you human!!\" Hello");
		test_list.add("-a1 \"Hello World. Nice to meet you hum\"an!!\" Hello");
		test_list.add("-a1 \"Hello\" World. Nice to meet you human!!\" Hello");
		test_list.add("-a1 \"Hello\" World\". Nice to meet you human!!\" Hello");
		test_list.add("-a1 Hello World. Nice to meet you human!!\" Hello");
//		test_list.add("-a1 \"Hello\" World \". Nice to meet you human!!\" Hello");
		test_list.add("\"");
		test_list.add("\"\"");
		test_list.add("\"\"\"Hello world");
		test_list.add("\"\"Hello'\"world'");
		
//		test_list.add("-a1 \"Hello world\"");
		
		
		for (String str : test_list) {
			try {
				passArgument(str);

				System.out.println("##########END#########");
				assertTrue(false);
			} catch (Exception e) {
//				e.printStackTrace();
				if (!(e instanceof ArgumentSyntaxException)) {
					System.out.println("##########END#########");
					assertTrue(false);
				}
			}
		}

		System.out.println("##########END#########");
	}

	Namespace passArgument(String str) throws ArgumentParserException, ArgumentSyntaxException {
		System.out.println(str);
		String[] result = ArgumentUtil.fixArgumentSpaceArray(str.split("\\s"));
		dumpArray(result);

		ArgumentParser parser = ArgumentParsers.newFor("Tester").build();
		parser.addArgument("-a1", "--arg1").type(String.class).required(false);
		parser.addArgument("p1").type(String.class);
		return parser.parseArgs(result);
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
}
