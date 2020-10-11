package org.vincentyeh.IMG2PDF.commandline;

import java.util.ArrayList;

import org.junit.Test;

public class DefaultTest {

	@Test
	public void RTest() {

		System.out.println(compareTo("walka1", "walka10"));

	}

	int compareTo(String ThisStr, String OStr) {
		String noNumThis = ThisStr.replaceAll("[0-9]{1,}", "*");
		String noNumO = OStr.replaceAll("[0-9]{1,}", "*");

		if (noNumThis.equals(noNumO)) {
			ArrayList<Integer> a = getNum(ThisStr);
			ArrayList<Integer> b = getNum(OStr);
			for (int i = 0; i < a.size(); i++) {
				int r = a.get(i) - b.get(i);
				if (r != 0) {
					return r;
				}
			}
			return 0;
		}else {
			return ThisStr.compareTo(OStr);
		}
	}

	ArrayList<Integer> getNum(String str) {
		String s[] = str.split("[^0-9]{1,}");
		ArrayList<Integer> buf = new ArrayList<Integer>();
		for (int i = 0; i < s.length; i++) {
			if (!s[i].isEmpty()) {
				buf.add(Integer.valueOf(s[i]));
			}
		}

		System.out.println(buf.toString());
		return buf;
	}
}
