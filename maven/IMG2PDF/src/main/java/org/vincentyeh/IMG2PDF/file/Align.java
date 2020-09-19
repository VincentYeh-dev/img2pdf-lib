package org.vincentyeh.IMG2PDF.file;

import org.vincentyeh.IMG2PDF.file.PDFFile.LeftRightAlign;
import org.vincentyeh.IMG2PDF.file.PDFFile.TopBottomAlign;

//public class Align{
//		private final LeftRightAlign LRA;
//		private final TopBottomAlign TBA;
//		
//		public Align(LeftRightAlign LRA,TopBottomAlign TBA) {
//			this.LRA=LRA;
//			this.TBA=TBA;
//		}
//		
//		public Align(String str) {
//			String[] LR_TB_A = str.split("\\|");
//			TBA = TopBottomAlign.getByStr(LR_TB_A[0]);
//			LRA = LeftRightAlign.getByStr(LR_TB_A[1]);
//		}
//		public LeftRightAlign getLRA() {
//			return LRA;
//		}
//		public TopBottomAlign getTBA() {
//			return TBA;
//		}
//		@Override
//		public String toString() {
//			// TODO Auto-generated method stub
//			return String.format("%s|%s", TBA.getStr(), LRA.getStr());
//		}
//	}