package org.vincentyeh.IMG2PDF.commandline.action;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public abstract class AbstractAction implements Action{
	protected static ResourceBundle lagug_resource;
	static {
		lagug_resource = ResourceBundle.getBundle("language_package",Locale.ROOT);
	}
	
	public AbstractAction() {
		
	}
	
	public static void setLagug_resource(ResourceBundle lagug_resource) {
		AbstractAction.lagug_resource = lagug_resource;
	}

}
