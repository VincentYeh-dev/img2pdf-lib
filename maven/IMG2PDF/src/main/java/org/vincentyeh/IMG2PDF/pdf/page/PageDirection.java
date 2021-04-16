package org.vincentyeh.IMG2PDF.pdf.page;

import org.vincentyeh.IMG2PDF.commandline.action.exception.UnrecognizedEnumException;

/**
 * Direction of page.Horizontal or Vertical.
 * 
 * @author VincentYeh
 */
public enum PageDirection {
	Landscape, Portrait;

	public static PageDirection getByString(String str) throws UnrecognizedEnumException {
		PageDirection[] directions = PageDirection.values();
		for (PageDirection direction : directions) {
			if (direction.toString().equals(str))
				return direction;
		}
		throw new UnrecognizedEnumException(str,PageDirection.class);
	}

}
