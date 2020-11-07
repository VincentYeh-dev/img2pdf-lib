package org.vincentyeh.IMG2PDF.commandline;

import org.junit.Test;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

public class Argparse4jTest {
	@Test
	public void stringtest() {
		char[] permissions="10101".toCharArray();
		for(char buf:permissions) {
			int permission=buf-0x30;
			boolean check=permission==0?false:true;
			System.out.println(check);
		}
	}
	

}
