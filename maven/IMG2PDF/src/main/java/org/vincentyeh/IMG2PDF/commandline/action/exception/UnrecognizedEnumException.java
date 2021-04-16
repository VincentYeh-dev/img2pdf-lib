package org.vincentyeh.IMG2PDF.commandline.action.exception;

public class UnrecognizedEnumException extends IllegalArgumentException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5818347104393060334L;
	private final Class enum_class;
	private final String unrecognizable_enum;
	public UnrecognizedEnumException(String unrecognizable_enum, Class _class) {
	   	super(String.format("%s is not a recognizable enum of %s.",unrecognizable_enum,_class.getSimpleName()));
	   	enum_class=_class;
		this.unrecognizable_enum = unrecognizable_enum;
	}

	public String getUnrecognizableEnum() {
		return unrecognizable_enum;
	}

	public Class getEnumClass() {
		return enum_class;
	}
}
