package org.vincentyeh.IMG2PDF.commandline.action;

import org.vincentyeh.IMG2PDF.commandline.parser.ActionParser;
import org.vincentyeh.IMG2PDF.commandline.parser.ConvertActionParser;
import org.vincentyeh.IMG2PDF.commandline.parser.CreateActionParser;

public enum ActionMode {
	create(new CreateActionParser()),convert(new ConvertActionParser());

	ActionParser parser;

	ActionMode(ActionParser parser) {
		this.parser = parser;
	}

	public ActionParser getParser() {
		return parser;
	}
}
