package org.vincentyeh.IMG2PDF.util;

import org.junit.Test;

public class ArgumentUtilTester {
	@Test
	public void testNoPrefix() {
		passArgument("Hello World. Nice to meet you human!!");
	}
	@Test
	public void testNoPrefixWithQuotes() {
		passArgument(" \"Hello World. Nice to meet you human!!\"");
	}
	
	void passArgument(String str) {
		System.out.println("input:");
		dumpArray(str.split("\\s"));
		String[] result=ArgumentUtil.fixArgumentSpaceArray(str.split("\\s"));
		System.out.println("output:");
		dumpArray(result);
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
