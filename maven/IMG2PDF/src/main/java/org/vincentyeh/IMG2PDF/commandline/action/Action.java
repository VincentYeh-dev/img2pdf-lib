package org.vincentyeh.IMG2PDF.commandline.action;

import java.util.ResourceBundle;

import net.sourceforge.argparse4j.inf.Namespace;

public interface Action {
	
	public void setupByNamespace(Namespace ns);
	public void start();
	
}
