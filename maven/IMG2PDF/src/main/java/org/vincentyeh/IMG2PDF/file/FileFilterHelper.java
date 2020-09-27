package org.vincentyeh.IMG2PDF.file;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

public class FileFilterHelper implements FileFilter {

	public static final int CONDITION_IS_ANY = 0x0001;
	public static final int CONDITION_IS_DIR = 0x0002;
	public static final int CONDITION_IS_FILE = 0x0003;

	public static final int CONDITION_NAME_CONTENT = 0x1000;
	public static final int CONDITION_NAME_EQUALS = 0x2000;

	public static final int CONDITION_EXT_CONTENT = 0x0100;
	public static final int CONDITION_EXT_EQUALS = 0x0200;

	private final int condition;
	private ArrayList<String> name_selector = new ArrayList<String>();
	private ArrayList<String> ext_selector = new ArrayList<String>();

	public FileFilterHelper(int condition) {
		this.condition = condition;
	}

	public void appendExtSLT(String ext) {
		if ((condition & 0x0F00) == 0)
			System.err.println("USELESS SETTING:selector is Disabled");

		ext_selector.add(ext);

	}

	public void setExtSLT(ArrayList<String> ext_selector) {
		if ((condition & 0x0F00) == 0)
			System.err.println("USELESS SETTING:selector is Disabled");

		this.ext_selector = ext_selector;
	}

	public void appendNameSLT(String name) {
		if ((condition & 0xF000) == 0)
			System.err.println("USELESS SETTING:selector is Disabled");
		name_selector.add(name);

	}

	public void setNameSLT(ArrayList<String> name_selector) {
		if ((condition & 0xF000) == 0)
			System.err.println("USELESS SETTING:selector is Disabled");
		this.name_selector = name_selector;
	}

	@Override
	public boolean accept(File file) {
		// TODO Auto-generated method stub
		String raw_name = file.getName();
		String[] buf = raw_name.split("\\.");

		int file_is = condition & 0x000F;
		int name_content = condition & 0xF000;
		int ext_content = condition & 0x0F00;
		if (file_is == CONDITION_IS_FILE && !file.isFile())
			return false;
		else if (file_is == CONDITION_IS_DIR && !file.isDirectory())
			return false;
		
		boolean name_flag=false;
		
		for (int i = 0; i < name_selector.size(); i++) {
			String sel = name_selector.get(i);
			
			if (name_content == CONDITION_NAME_CONTENT && !buf[0].contains(sel))
				continue;
			else if (name_content == ~CONDITION_NAME_CONTENT && buf[0].contains(sel))
				continue;
			else if (name_content == CONDITION_NAME_EQUALS && !buf[0].equals(sel))
				continue;
			else if (name_content == ~CONDITION_NAME_EQUALS && buf[0].equals(sel))
				continue;
			name_flag=true;
		}

		
		boolean ext_flag=false;
		if (file.isFile()) {
			for(int i=0;i<ext_selector.size();i++) {
				String sel=ext_selector.get(i);
				if (ext_content == CONDITION_EXT_CONTENT && !buf[1].contains(sel))
					continue;
				else if (ext_content == ~CONDITION_EXT_CONTENT && buf[1].contains(sel))
					continue;
				else if (ext_content == CONDITION_EXT_EQUALS && !buf[1].equals(sel))
					continue;
				else if (ext_content == ~CONDITION_EXT_EQUALS && buf[1].equals(sel))
					continue;
				ext_flag= true;
			}
			if((ext_content>0&&!ext_flag)||(name_content>0&&!name_flag))return false;
		}else if (ext_content != 0) {
			System.err.println("USELESS BEHAVIOR:only file have extension:" + file.getName());
		}
		return true;
	}

}
