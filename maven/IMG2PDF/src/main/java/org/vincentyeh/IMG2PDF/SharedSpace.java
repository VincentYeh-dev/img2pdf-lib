package org.vincentyeh.IMG2PDF;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.ResourceBundle;


public class SharedSpace {
	public static class Configuration {
		public static final String PROGRAM_NAME = "IMG2PDF";
		public static final String PROGRAM_VER = "0.0.1-SNAPSHOT";
		public static final String DEVELOPER = "VincentYeh-dev";
		public static final Charset DEFAULT_CHARSET= StandardCharsets.UTF_8;
	}

	private static ResourceBundle language_resource =null;
	static {
		System.out.println("Static SharedSpace");
//		Language Setting:

//	Local:
		SharedSpace.setLanguageRes(ResourceBundle.getBundle("language_package", Locale.getDefault()));
//	root:

//		Configuration.setLanguageRes(ResourceBundle.getBundle("language_package", Locale.ROOT));
	}

	public static void setLanguageRes(ResourceBundle lagug_resource) {
		SharedSpace.language_resource = lagug_resource;
	}

	
	public static String getResString(String key) {
		return SharedSpace.language_resource.getString(key);
	}


}
