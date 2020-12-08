package org.vincentyeh.IMG2PDF.commandline.action;
import net.sourceforge.argparse4j.inf.Namespace;

/**
 * Action of command line.This interface make its subclass need to initialize and do stuff. 
 * 
 * @author vincent
 */
public interface Action {
	
	public void setupByNamespace(Namespace ns);
	public void start();
}
