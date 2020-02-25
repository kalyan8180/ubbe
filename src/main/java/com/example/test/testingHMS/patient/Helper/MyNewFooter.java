package com.example.test.testingHMS.patient.Helper;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

public class MyNewFooter extends PdfPageEventHelper {

	Font ffont = new Font(Font.FontFamily.UNDEFINED, 20, Font.ITALIC);
	Font font = new Font(Font.FontFamily.UNDEFINED, 12, Font.NORMAL);

	public void onEndPage(PdfWriter writer, Document document) {
		
		  PdfContentByte cb = writer.getDirectContent();
		  Font font = new Font(Font.FontFamily.UNDEFINED, 12, Font.NORMAL);
		
		Phrase footer = new Phrase("MIG - 196, KPHB Road Number 1, K P H B Phase 1, Kukatpally, Hyderabad, Telangana 500072", font);
        Phrase p1 = new Phrase("___________________________________________________________________________________________");
        ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                footer,
                (document.right() - document.left()) / 2 + document.leftMargin(),
                document.bottom() - 10, 0);
        
        ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, p1,
	            (document.right() - document.left()) / 2 + document.leftMargin(),
	            document.bottom() + 20, 0);
        
        
        
		
		
	}

}