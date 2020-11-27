package org.vincentyeh.IMG2PDF.commandline;

import org.junit.Test;
import org.vincentyeh.IMG2PDF.sample.WalkAnimation;
import org.vincentyeh.IMG2PDF.sample.kuma_fish_album_1;

import net.sourceforge.argparse4j.inf.ArgumentParserException;

public class MainProgramTester {
	@Test
	public void PassNull() throws ArgumentParserException {
		System.out.println("---------PassNull---------");
		String buf[]=null;
		MainProgram.main(buf);
	}
	
	@Test
	public void PassEmpty() throws ArgumentParserException {
		System.out.println("---------PassEmpty---------");
		MainProgram.main("".split("\\s"));
	}
	
	@Test
	public void kuma_fish_album_1Test() throws ArgumentParserException {
		System.out.println("---------kuma_fish_album_1 Test---------");
		kuma_fish_album_1.main(null);
	}
	
	@Test
	public void WalkAnimationTest() throws ArgumentParserException {
		System.out.println("---------WalkAnimation Test---------");
		WalkAnimation.main(null);
	}
	
}
