package org.vincentyeh.IMG2PDF.commandline;

import java.util.ResourceBundle;


public class Configuration {
	public static final String PROGRAM_NAME="IMG2PDF";
	public static final String PROGRAM_VER="0.0.1-SNAPSHOT";
	public static final String DEVELOPER="VincentYeh-dev";
	
	private static ResourceBundle lagug_resource=null;
	public static void setLagugRes(ResourceBundle lagug_resource) {
		Configuration.lagug_resource = lagug_resource;
	}

	public static ResourceBundle getLagugRes() {
		return Configuration.lagug_resource;
	}
	
	public static String getResString(String key) {
		return Configuration.lagug_resource.getString(key);
	}


}
