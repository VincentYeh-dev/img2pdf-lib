package org.vincentyeh.IMG2PDF.commandline.action.exception;

import org.vincentyeh.IMG2PDF.Configuration;

public class UnrecognizedEnumException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5818347104393060334L;

	public <T> UnrecognizedEnumException(String unrecognizable_enum, Class<T> _class) {
		super(String.format(Configuration.getResString("err_unrecognizable_enum_long"),unrecognizable_enum,
				_class.getSimpleName(), listEnum(_class)));

	}

	public UnrecognizedEnumException(String unrecognizable_enum, String _enum) {
		super(String.format(Configuration.getResString("err_unrecognizable_enum_short"), unrecognizable_enum, _enum));
	}

	public <T> UnrecognizedEnumException(String unrecognizable_enum, String _enum, Class<T> _class) {
		super(String.format(Configuration.getResString("err_unrecognizable_enum_long"), unrecognizable_enum, _enum,
				listEnum(_class)));

	}

	private static <T> String listEnum(Class<T> _class_enum) {
		T[] enums = _class_enum.getEnumConstants();
		StringBuilder sb = new StringBuilder();
		sb.append(enums[0].toString());
		for (int i = 1; i < enums.length; i++) {
			sb.append(",");
			sb.append(enums[i].toString());
		}
		return sb.toString();
	}
}
