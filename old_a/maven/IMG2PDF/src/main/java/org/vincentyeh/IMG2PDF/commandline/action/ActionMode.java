package org.vincentyeh.IMG2PDF.commandline.action;

import org.vincentyeh.IMG2PDF.commandline.action.exception.UnrecognizedEnumException;
import org.vincentyeh.IMG2PDF.pdf.page.PageDirection;

public enum ActionMode {
	create,convert;
	
	
	public static ActionMode getByString(String str) throws UnrecognizedEnumException {
		ActionMode[] modes = ActionMode.values();
		for (ActionMode mode : modes) {
			if (mode.toString().equals(str))
				return mode;
		}
		throw new UnrecognizedEnumException(str,ActionMode.class);
	}

}
