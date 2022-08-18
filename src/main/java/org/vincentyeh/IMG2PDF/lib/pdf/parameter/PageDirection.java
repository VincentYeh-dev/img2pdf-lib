package org.vincentyeh.img2pdf.lib.pdf.parameter;


/**
 * Direction of page.Horizontal or Vertical.
 * 
 * @author VincentYeh
 */
public enum PageDirection {
	Landscape, Portrait;

	public static PageDirection detectDirection(float height, float width){
		return height/ width > 1 ? Portrait : Landscape;
	}
}
