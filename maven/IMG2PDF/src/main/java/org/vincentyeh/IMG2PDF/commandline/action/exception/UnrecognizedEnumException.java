package org.vincentyeh.IMG2PDF.commandline.action.exception;

public class UnrecognizedEnumException extends IllegalArgumentException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5818347104393060334L;
	private final String unrecognizable_enum;
	private final String[] enum_values;
	private final String enum_name;

	public <T> UnrecognizedEnumException(String unrecognizable_enum, String enum_name, T[] enum_values) {
	   	super(String.format("%s is not a recognizable enum of %s.",unrecognizable_enum,enum_name));
	   	this.enum_values=new String[enum_values.length];
	   	for (int i=0;i<enum_values.length;i++){
	   		this.enum_values[i]=enum_values[i].toString();
		}
		this.enum_name=enum_name;
		this.unrecognizable_enum = unrecognizable_enum;
	}

	public String getUnrecognizableEnum() {
		return unrecognizable_enum;
	}

	public String[] getAvailiableValues() {
		return enum_values;
	}

	public String getEnumName() {
		return enum_name;
	}

}
