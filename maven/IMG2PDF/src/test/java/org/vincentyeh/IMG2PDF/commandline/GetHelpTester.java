package org.vincentyeh.IMG2PDF.commandline;

import org.junit.Test;

import net.sourceforge.argparse4j.inf.ArgumentParserException;

public class GetHelpTester {
	
	@Test
	public void h() throws ArgumentParserException {
		MainProgram.main("-h");
	}
	
	@Test
	public void create() throws ArgumentParserException {
		MainProgram.main("create -h");
	}

	@Test
	public void create_add() throws ArgumentParserException {
		MainProgram.main("create add -h");
	}

	@Test
	public void create_import() throws ArgumentParserException {
		MainProgram.main("create import -h");
	}

	@Test
	public void convert() throws ArgumentParserException {
		MainProgram.main("convert -h");
	}

}
