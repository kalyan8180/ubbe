package com.example.test.testingHMS.doctor.model;


import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

public class MyFooter extends PdfPageEventHelper {
    Font ffont = new Font(Font.FontFamily.UNDEFINED, 20, Font.ITALIC);
    Font font = new Font(Font.FontFamily.UNDEFINED, 12, Font.ITALIC);

    public void onEndPage(PdfWriter writer, Document document) {
        PdfContentByte cb = writer.getDirectContent();
        Phrase header = new Phrase("UDHBAVA HOSPITALS ", ffont);
       Phrase footer = new Phrase("Plot No.14,15,16 &17,Nandi Co-op. Society,Main Road,Beside Navya Grand Hotel,Miyapur,Hyderabad-49", font);
        Phrase p1 = new Phrase("___________________________________________________________________________________________");
        Phrase h1= new Phrase("___________________________________________________________________________________________");
        
        
        
        ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,header,
                (document.right() - document.left()) / 2 + document.leftMargin(),
                document.top() + 10, 0);
        ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,h1,
                (document.right() - document.left()) / 2 + document.leftMargin(),
                document.top() + 6, 0);
        ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                footer,
                (document.right() - document.left()) / 2 + document.leftMargin(),
                document.bottom() - 10, 0);
        
        
     
        
        ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, p1,
	            (document.right() - document.left()) / 2 + document.leftMargin(),
	            document.bottom() + 20, 0);
    }
}