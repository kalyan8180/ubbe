package com.example.test.testingHMS.bill.serviceImpl;

import java.io.ByteArrayOutputStream;
import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.test.testingHMS.MoneyToWords.NumberToWordsConverter;
import com.example.test.testingHMS.bed.serviceImpl.RoomDetailsServiceImpl;
import com.example.test.testingHMS.bill.model.ChargeBill;
import com.example.test.testingHMS.bill.repository.ChargeBillRepository;
import com.example.test.testingHMS.bill.service.ChargeBillService;
import com.example.test.testingHMS.laboratory.model.LabServices;
import com.example.test.testingHMS.laboratory.model.LaboratoryRegistration;
import com.example.test.testingHMS.patient.model.PatientRegistration;
import com.example.test.testingHMS.patient.serviceImpl.PatientRegistrationServiceImpl;
import com.example.test.testingHMS.pharmacist.model.Sales;
import com.example.test.testingHMS.pharmacist.model.SalesPaymentPdf;
import com.example.test.testingHMS.pharmacist.serviceImpl.SalesPaymentPdfServiceImpl;
import com.example.test.testingHMS.user.model.User;
import com.example.test.testingHMS.user.serviceImpl.UserServiceImpl;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@Service
public class ChargeBillServiceImpl implements ChargeBillService {
	
	public static Logger Logger=LoggerFactory.getLogger(ChargeBillServiceImpl.class);
	

	@Autowired
	ChargeBillRepository chargeBillRepository;
	
	@Autowired
	UserServiceImpl userServiceImpl;
	
	@Autowired
	SalesPaymentPdf salesPaymentPdf;
	
	@Autowired
	SalesPaymentPdfServiceImpl salesPaymentPdfServiceImpl;
	
	@Autowired
    ResourceLoader resourceLoader;
	
	@Autowired
	PatientRegistrationServiceImpl patientRegistrationServiceImpl;
	
	@Autowired
	NumberToWordsConverter numberToWordsConverter;
	
	@Autowired
	ChargeBillServiceImpl chargeBillServiceImpl;
	
	@Override
	public String getNextId() {
		ChargeBill chargeBill=chargeBillRepository.findFirstByOrderByChargeBillIdDesc();
		String nextId=null;
		if(chargeBill==null)
		{
			nextId="CB0000001";
		}
		else
		{
			int nextIntId=Integer.parseInt(chargeBill.getChargeBillId().substring(2));
			nextIntId+=1;
			nextId="CB"+String.format("%07d", nextIntId);
		}
		return nextId;
	}
	
	public String getNextBillNo()
	{
		ChargeBill chargeBill=chargeBillRepository.findFirstByOrderByChargeBillIdDesc();
		String nextBill=null;
		if(chargeBill==null)
		{
			nextBill="BL0000001";
		}
		else
		{
			int nextIntId=Integer.parseInt(chargeBill.getBillNo().substring(2));
			nextIntId+=1;
			nextBill="BL"+String.format("%07d", nextIntId);
		}
		return nextBill;
		
	}
	
	public ChargeBill findBySaleId(Sales sale)
	{
		return chargeBillRepository.findBySaleId(sale);
	}
	
	public void save(ChargeBill bill)
	{
		chargeBillRepository.save(bill);
	}

	public List<ChargeBill> findByPatRegId(PatientRegistration patientRegistration)
	{
		return chargeBillRepository.findByPatRegId(patientRegistration);
	}
	
	public List<ChargeBill> findAllLab(String regId)
	{
		return chargeBillRepository.findAllLab(regId);
	}
	
	public ChargeBill findByLabId(LaboratoryRegistration laboratoryRegistration)
	{
		return chargeBillRepository.findByLabId(laboratoryRegistration);
	}
	
	public ChargeBill findByChargeBillId(String id)
	{
		return chargeBillRepository.findByChargeBillId(id);
	}

	@Override
	public List<ChargeBill> findByPatRegIdAndPaid(PatientRegistration patientRegistration, String paid) {
		return chargeBillRepository.findByPatRegIdAndPaid(patientRegistration, paid);
	}
	
	public SalesPaymentPdf getOpBillReceipt(String regId, Principal principal) {
		
		User userSecurity=userServiceImpl.findByUserName(principal.getName());
		String createdBy=userSecurity.getFirstName()+" "+userSecurity.getLastName();
		
		PatientRegistration patientRegistration = patientRegistrationServiceImpl.findByRegId(regId);
		
		List<ChargeBill> chargeBill = chargeBillRepository.findByPatRegId(patientRegistration);
		
		byte[] pdfByte=null;
		
		String addr="         Plot No14,15,16 & 17,Nandi Co-op.Society,"
				+ "\n                            	       Main Road, Beside Navya Grand Hotel, \n                                Miyapur,Hyderabad-49,Phone:040-23046789   \n                               "
				+ "   For Appointment Contact:8019114481   "
				+ "\n                                   Email :udbhavahospitals@gmail.com ";

		
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		Document document = new Document(PageSize.A4_LANDSCAPE);
		
		try {

			Resource fileResource=resourceLoader.getResource("classpath:udbhava.png");
			//Chunk cnd1 = new Chunk(new VerticalPositionMark());
			Font redFont1 = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
			PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);
			Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
			Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
			Font headFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
			
			// Display a date in day, month, year format
			Date dateInfo = Calendar.getInstance().getTime();
			DateFormat formatter1 = new SimpleDateFormat("dd-MMM-yyyy");
			String today = formatter1.format(dateInfo).toString();
				
				document.open();
				PdfPTable table = new PdfPTable(2);

				Image img = Image.getInstance(fileResource.getFile().getAbsolutePath());
				img.scaleAbsolute(56, 87);
				table.setWidthPercentage(105);

				Phrase pq = new Phrase(new Chunk(img, 0, -70));

				pq.add(new Chunk(
						addr,
						redFont));
				//pq.add(new Chunk(newAddress,redFont));
				PdfPCell cellp = new PdfPCell(pq);
				PdfPCell cell1 = new PdfPCell();


				PdfPTable table96 = new PdfPTable(1);
				table96.setWidths(new float[] { 5f });
				table96.setSpacingBefore(10);

				PdfPCell hcell96;
				hcell96 = new PdfPCell(new Phrase("UDBHAVA HOSPITALS", headFont1));
				hcell96.setBorder(Rectangle.NO_BORDER);
				hcell96.setHorizontalAlignment(Element.ALIGN_CENTER);
				hcell96.setPaddingLeft(50f);

				table96.addCell(hcell96);
				cell1.addElement(table96);

				cell1.addElement(pq);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(cell1);

				PdfPCell cell3 = new PdfPCell();

				PdfPTable table99 = new PdfPTable(3);
				table99.setWidths(new float[] {3f,1f,4f});
				table99.setSpacingBefore(10);

				PdfPCell hcell90;
				hcell90 = new PdfPCell(new Phrase("Patient", redFont));
				hcell90.setBorder(Rectangle.NO_BORDER);
				hcell90.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell90.setPaddingBottom(-7f);
				hcell90.setPaddingLeft(-25f);
				table99.addCell(hcell90);
				
				hcell90 = new PdfPCell(new Phrase(":", redFont));
				hcell90.setBorder(Rectangle.NO_BORDER);
				hcell90.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell90.setPaddingBottom(-7f);
				hcell90.setPaddingLeft(-61f);
				table99.addCell(hcell90);
				
				hcell90 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getTitle()+" "+patientRegistration.getPatientDetails().getFirstName()+" "+patientRegistration.getPatientDetails().getMiddleName()+" "+patientRegistration.getPatientDetails().getLastName(), redFont));
				hcell90.setBorder(Rectangle.NO_BORDER);
				hcell90.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell90.setPaddingBottom(-7f);
				hcell90.setPaddingLeft(-85f);
				table99.addCell(hcell90);
				
				cell3.addElement(table99);

				PdfPTable table2 = new PdfPTable(6);
				table2.setWidths(new float[] {3f,1f,4f,3f,1f,4f });
				table2.setSpacingBefore(10);

				PdfPCell hcell1;
				hcell1 = new PdfPCell(new Phrase("Age/Sex", redFont));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-25f);
				table2.addCell(hcell1);
				
				hcell1 = new PdfPCell(new Phrase(":", redFont));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-20f);
				table2.addCell(hcell1);
				
				hcell1 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getAge()+" "+patientRegistration.getPatientDetails().getGender(), redFont));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-30f);
				table2.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase("UMR No", redFont));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingRight(-40f);
				table2.addCell(hcell1);
	
				hcell1 = new PdfPCell(new Phrase(":", redFont));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table2.addCell(hcell1);
	
				hcell1 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getUmr(), redFont));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingRight(-30f);
				table2.addCell(hcell1);
			        
			        
				PdfPCell hcell4;
				hcell4 = new PdfPCell(new Phrase("Bill Dt" , redFont));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell4.setPaddingLeft(-25f);
				table2.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(":", redFont));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell4.setPaddingLeft(-20f);
				table2.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(today, redFont));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell4.setPaddingLeft(-30f);
				table2.addCell(hcell4);

				hcell4 = new PdfPCell(new Phrase("RegId", redFont));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
		        hcell4.setPaddingRight(-27.5f);
				table2.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(":",redFont));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setHorizontalAlignment(Element.ALIGN_RIGHT);
			    table2.addCell(hcell4);
			        
				hcell4 = new PdfPCell(new Phrase(patientRegistration.getRegId(), redFont));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell4.setPaddingRight(-27.5f);
				table2.addCell(hcell4);
			        
				PdfPCell hcell15;
				hcell15 = new PdfPCell(new Phrase("Ref.By", redFont));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(-25f);	
				table2.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(":", redFont));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(-20f);
				table2.addCell(hcell15);
				
				String refBy=null;
				if(patientRegistration.getPatientDetails().getvRefferalDetails()!=null)
				{
					refBy=patientRegistration.getPatientDetails().getvRefferalDetails().getRefName();
				}
				else
				{
					refBy="";
				}
				hcell15 = new PdfPCell(new Phrase(refBy, redFont));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(-30f);
				table2.addCell(hcell15);


				hcell15 = new PdfPCell(new Phrase("Phone", redFont));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingRight(-27.5f);
				table2.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(":",redFont));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_RIGHT);
			    table2.addCell(hcell15);
			        
				hcell15 = new PdfPCell(new Phrase(String.valueOf(patientRegistration.getPatientDetails().getMobile()), redFont));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingRight(-27.5f);
				table2.addCell(hcell15);

				cell3.setFixedHeight(115f);
				cell3.setColspan(2);
				cell3.addElement(table2);

				PdfPTable table98 = new PdfPTable(3);
				table98.setWidths(new float[] { 3f,1f,4f });
				table98.setSpacingBefore(10);

				PdfPCell hcell91;
				hcell91 = new PdfPCell(new Phrase("Consultant", redFont));
				hcell91.setBorder(Rectangle.NO_BORDER);
				hcell91.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell91.setPaddingTop(-5f);
				hcell91.setPaddingLeft(-25f);
				table98.addCell(hcell91);
				
				
				hcell91 = new PdfPCell(new Phrase(":", redFont));
				hcell91.setBorder(Rectangle.NO_BORDER);
				hcell91.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell91.setPaddingTop(-5f);
				hcell91.setPaddingLeft(-61f);
				table98.addCell(hcell91);
				
				hcell91 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getConsultant(), redFont));
				hcell91.setBorder(Rectangle.NO_BORDER);
				hcell91.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell91.setPaddingTop(-5f);
				hcell91.setPaddingLeft(-85f);
				table98.addCell(hcell91);
				
				cell3.addElement(table98);

				/*PdfPTable table97 = new PdfPTable(1);
				table97.setWidths(new float[] { 5f });
				table97.setSpacingBefore(10);

				PdfPCell hcell97;
				hcell97 = new PdfPCell(new Phrase(
						"*" + "OBN0094995" + "*" + "  " + "==> Scan This BarCode To Take Report At KIOSK", headFont1));
				hcell97.setBorder(Rectangle.NO_BORDER);
				// hcell3.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell97.setPaddingBottom(-10f);
				hcell97.setPaddingLeft(-35f);

				table97.addCell(hcell97);
				cell3.addElement(table97);
*/
				table.addCell(cell3);

				PdfPCell cell19 = new PdfPCell();

				PdfPTable table21 = new PdfPTable(1);
				table21.setWidths(new float[] { 4f });
				table21.setSpacingBefore(10);

				PdfPCell hcell19;
				hcell19 = new PdfPCell(new Phrase("OUTPATIENT BILL RECEIPT", headFont1));
				hcell19.setBorder(Rectangle.NO_BORDER);
				hcell19.setHorizontalAlignment(Element.ALIGN_CENTER);
				table21.addCell(hcell19);

				cell19.setFixedHeight(20f);
				cell19.setColspan(2);
				cell19.addElement(table21);
				table.addCell(cell19);

				PdfPCell cell31 = new PdfPCell();

				PdfPTable table1 = new PdfPTable(7);
				table1.setWidths(new float[] { 1f, 3f, 5f, 3f, 1f, 2f,2f });

				table1.setSpacingBefore(10);

				PdfPCell hcell;
				hcell = new PdfPCell(new Phrase("S.No", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
				hcell.setPaddingBottom(5f);
				hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("Service Code", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("Service Name", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("Service Type", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("Qty", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("Rate", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table1.addCell(hcell);
				
				hcell = new PdfPCell(new Phrase("Amount(RS)", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table1.addCell(hcell);

				int count = 0;
				long total=0;
				String serviceId = null;
				String serviceName = null;
				String serviceType = null;
				float totalAmt = 0;
				for (ChargeBill chargebill:chargeBill ) {
					if(chargebill.getServiceId()!=null)
					{
					LabServices labServices = chargebill.getServiceId();
					serviceId = labServices.getServiceId();
					serviceName = labServices.getServiceName();
					serviceType = labServices.getServiceType();
					
					PdfPCell cell;

					cell = new PdfPCell(new Phrase(String.valueOf(count = count + 1), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table1.addCell(cell);

					cell = new PdfPCell(new Phrase(serviceId, redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					table1.addCell(cell);

					cell = new PdfPCell(new Phrase(serviceName, redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					table1.addCell(cell);

					cell = new PdfPCell(new Phrase(serviceType, redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					table1.addCell(cell);

					cell = new PdfPCell(new Phrase(String.valueOf(chargebill.getQuantity()), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table1.addCell(cell);

					cell = new PdfPCell(new Phrase(String.valueOf(chargebill.getAmount()), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					table1.addCell(cell);

					//Calculation for total amount
					totalAmt = (chargebill.getQuantity()*chargebill.getAmount());
					cell = new PdfPCell(new Phrase(String.valueOf(totalAmt), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					table1.addCell(cell);
					
			       total+=totalAmt;

				}
				}
				/*
				 * cell31.setColspan(2); table1.setWidthPercentage(100f);
				 * cell31.addElement(table1); //cell31.addElement(table37);
				 * table.addCell(cell31);
				 */
				// -------------------------------

				PdfPTable table37 = new PdfPTable(6);
				table37.setWidths(new float[] {3f,1f, 4f,7f,1f, 4f });
				table37.setSpacingBefore(10);

				PdfPCell cell55;
				cell55 = new PdfPCell(new Phrase("", redFont));
				cell55.setBorder(Rectangle.NO_BORDER);
				cell55.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell55.setPaddingTop(10f);
				table37.addCell(cell55);
				
				cell55 = new PdfPCell(new Phrase("", redFont));
				cell55.setBorder(Rectangle.NO_BORDER);
				cell55.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell55.setPaddingTop(10f);
				table37.addCell(cell55);
				
				cell55 = new PdfPCell(new Phrase("", redFont));
				cell55.setBorder(Rectangle.NO_BORDER);
				cell55.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell55.setPaddingTop(10f);
				table37.addCell(cell55);

				cell55 = new PdfPCell(new Phrase("Gross Amt", redFont));
				cell55.setBorder(Rectangle.NO_BORDER);
				cell55.setPaddingTop(10f);
				cell55.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell55.setPaddingRight(-70f);
				table37.addCell(cell55);
				
				cell55 = new PdfPCell(new Phrase(":", redFont));
				cell55.setBorder(Rectangle.NO_BORDER);
				cell55.setPaddingTop(10f);
				cell55.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell55.setPaddingRight(-60f);
				table37.addCell(cell55);
				
				cell55 = new PdfPCell(new Phrase(String.valueOf(total), redFont));
				cell55.setBorder(Rectangle.NO_BORDER);
				cell55.setPaddingTop(10f);
				cell55.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell55.setPaddingRight(-50f);
				table37.addCell(cell55);
				
				
				PdfPCell hcell56;
				hcell56 = new PdfPCell(new Phrase("", redFont));
				hcell56.setBorder(Rectangle.NO_BORDER);
				hcell56.setPaddingLeft(-1f);
				hcell56.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table37.addCell(hcell56);
				
				hcell56 = new PdfPCell(new Phrase("", redFont));
				hcell56.setBorder(Rectangle.NO_BORDER);
				hcell56.setPaddingLeft(-1f);
				hcell56.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table37.addCell(hcell56);
				
				hcell56 = new PdfPCell(new Phrase("", redFont));
				hcell56.setBorder(Rectangle.NO_BORDER);
				hcell56.setPaddingLeft(-1f);
				hcell56.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table37.addCell(hcell56);


				hcell56 = new PdfPCell(new Phrase("Paid Amt.", redFont));
				hcell56.setBorder(Rectangle.NO_BORDER);
				hcell56.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell56.setPaddingRight(-70f);
				table37.addCell(hcell56);
				
				hcell56 = new PdfPCell(new Phrase(":", redFont));
				hcell56.setBorder(Rectangle.NO_BORDER);
				hcell56.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell56.setPaddingRight(-60f);
				table37.addCell(hcell56);
				
				hcell56 = new PdfPCell(new Phrase(String.valueOf(total), redFont));
				hcell56.setBorder(Rectangle.NO_BORDER);
				hcell56.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell56.setPaddingRight(-50f);
				table37.addCell(hcell56);

				PdfPCell hcell57;
				hcell57 = new PdfPCell(new Phrase("Cash Amt.", redFont));
				hcell57.setBorder(Rectangle.NO_BORDER);
				hcell57.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell57.setPaddingLeft(-50f);
				table37.addCell(hcell57);

				hcell57 = new PdfPCell(new Phrase(":", redFont));
				hcell57.setBorder(Rectangle.NO_BORDER);
				hcell57.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell57.setPaddingLeft(-50f);
				table37.addCell(hcell57);

				hcell57 = new PdfPCell(new Phrase(String.valueOf(total), redFont));
				hcell57.setBorder(Rectangle.NO_BORDER);
				hcell57.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell57.setPaddingLeft(-60f);
				table37.addCell(hcell57);

				hcell57 = new PdfPCell(new Phrase("Net Amt.", redFont));
				hcell57.setBorder(Rectangle.NO_BORDER);
				hcell57.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell57.setPaddingRight(-70f);
				table37.addCell(hcell57);
				
				hcell57 = new PdfPCell(new Phrase(":", redFont));
				hcell57.setBorder(Rectangle.NO_BORDER);
				hcell57.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell57.setPaddingRight(-60f);
				table37.addCell(hcell57);
				
				hcell57 = new PdfPCell(new Phrase(String.valueOf(total), redFont));
				hcell57.setBorder(Rectangle.NO_BORDER);
				hcell57.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell57.setPaddingRight(-50f);
				table37.addCell(hcell57);

				PdfPCell hcell58;
				hcell58 = new PdfPCell(new Phrase(""));
				hcell58.setBorder(Rectangle.NO_BORDER);
				table37.addCell(hcell58);
				
				hcell58 = new PdfPCell(new Phrase(""));
				hcell58.setBorder(Rectangle.NO_BORDER);
				table37.addCell(hcell58);
				
				hcell58 = new PdfPCell(new Phrase(""));
				hcell58.setBorder(Rectangle.NO_BORDER);
				table37.addCell(hcell58);

				hcell58 = new PdfPCell(new Phrase("Received Amt.", redFont));
				hcell58.setBorder(Rectangle.NO_BORDER);
				hcell58.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell58.setPaddingRight(-70f);
				table37.addCell(hcell58);
				
				hcell58 = new PdfPCell(new Phrase(":", redFont));
				hcell58.setBorder(Rectangle.NO_BORDER);
				hcell58.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell58.setPaddingRight(-60f);
				table37.addCell(hcell58);
				
				hcell58 = new PdfPCell(new Phrase(String.valueOf(total), redFont));
				hcell58.setBorder(Rectangle.NO_BORDER);
				hcell58.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell58.setPaddingRight(-50f);
				table37.addCell(hcell58);
			
				PdfPTable table31 = new PdfPTable(2);
				table31.setWidths(new float[] { 6f, 12f });
				table31.setSpacingBefore(10);
				
				PdfPCell hcell59;
				hcell59 = new PdfPCell(new Phrase("Gross Amount In Words ", redFont));
				hcell59.setBorder(Rectangle.NO_BORDER);
				hcell59.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell59.setPaddingLeft(-50f);
				table31.addCell(hcell59);
				
				hcell59 = new PdfPCell(new Phrase("(" + numberToWordsConverter.convert(total) + ")", redFont));
				hcell59.setBorder(Rectangle.NO_BORDER);
				hcell59.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell59.setPaddingLeft(-90f);
				table31.addCell(hcell59);
			
				hcell59 = new PdfPCell(new Phrase("", headFont));
				hcell59.setBorder(Rectangle.NO_BORDER);
				hcell59.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table31.addCell(hcell59);
				
				hcell59 = new PdfPCell(new Phrase("", headFont));
				hcell59.setBorder(Rectangle.NO_BORDER);
				hcell59.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table31.addCell(hcell59);

				PdfPCell hcell60;
				hcell60 = new PdfPCell(new Phrase(
						"Received Amount In Words ", redFont));

				hcell60.setBorder(Rectangle.NO_BORDER);
				hcell60.setPaddingLeft(-50f);
				hcell60.setHorizontalAlignment(Element.ALIGN_LEFT);
				table31.addCell(hcell60);

				/*hcell60 = new PdfPCell(new Phrase("", headFont));
				hcell60.setBorder(Rectangle.NO_BORDER);
				hcell60.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table31.addCell(hcell60);*/
				
				hcell60 = new PdfPCell(new Phrase("(" + numberToWordsConverter.convert(total) + ")", redFont));
				hcell60.setBorder(Rectangle.NO_BORDER);
				hcell60.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell60.setPaddingLeft(-80f);
				table31.addCell(hcell60);
				
				hcell60 = new PdfPCell(new Phrase("", headFont));
				hcell60.setBorder(Rectangle.NO_BORDER);
				hcell60.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table31.addCell(hcell60);
				
				/*hcell60 = new PdfPCell(new Phrase("", headFont));
				hcell60.setBorder(Rectangle.NO_BORDER);
				hcell60.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table31.addCell(hcell60);
				*/
				hcell60 = new PdfPCell(new Phrase("", headFont));
				hcell60.setBorder(Rectangle.NO_BORDER);
				hcell60.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table31.addCell(hcell60);
				
				cell31.setColspan(2);
				table1.setWidthPercentage(100f);
				cell31.addElement(table1);
				cell31.addElement(table37);
				cell31.addElement(table31);
				table.addCell(cell31);


				PdfPCell cell5 = new PdfPCell();

				PdfPTable table35 = new PdfPTable(2);
				table35.setWidths(new float[] { 5f, 4f });
				table35.setSpacingBefore(10);

				PdfPCell hcell12;
				hcell12 = new PdfPCell(new Phrase("Created By    : "+createdBy, redFont));
				hcell12.setBorder(Rectangle.NO_BORDER);
				hcell12.setPaddingTop(10f);
				hcell12.setPaddingLeft(-50f);
				table35.addCell(hcell12);

				hcell12 = new PdfPCell(new Phrase("Created Dt   :   " + today, redFont));
				hcell12.setBorder(Rectangle.NO_BORDER);
				hcell12.setPaddingTop(10f);
				hcell12.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table35.addCell(hcell12);

				PdfPCell hcell13;
				hcell13 = new PdfPCell(new Phrase("Printed By     : "+createdBy, redFont));
				hcell13.setBorder(Rectangle.NO_BORDER);
				hcell13.setPaddingLeft(-50f);
				table35.addCell(hcell13);

				hcell13 = new PdfPCell(new Phrase("Print Dt       :   " + today, redFont));
				hcell13.setBorder(Rectangle.NO_BORDER);
				hcell13.setPaddingRight(3f);
				hcell13.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table35.addCell(hcell13);

				PdfPCell hcell23;
				hcell23 = new PdfPCell(new Phrase(""));
				hcell23.setBorder(Rectangle.NO_BORDER);
				table35.addCell(hcell23);

				hcell23 = new PdfPCell(new Phrase("(Authorized Signature)", headFont));
				hcell23.setBorder(Rectangle.NO_BORDER);
				hcell23.setPaddingTop(22f);
				hcell23.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table35.addCell(hcell23);

				cell5.setFixedHeight(90f);
				cell5.setColspan(2);
				cell5.addElement(table35);
				table.addCell(cell5);

				document.add(table);

				document.close();							
					
				System.out.println("finished");
				pdfByte = byteArrayOutputStream.toByteArray();
				String uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/sales/viewFile/")
						.path(salesPaymentPdfServiceImpl.getNextId()).toUriString();

				salesPaymentPdf = new SalesPaymentPdf();
				salesPaymentPdf.setFileName(regId+" Final Bill");
				salesPaymentPdf.setFileuri(uri);
				salesPaymentPdf.setPid(salesPaymentPdfServiceImpl.getNextId());
				salesPaymentPdf.setData(pdfByte);
				System.out.println(salesPaymentPdf);
				//System.out.println(pType);
				salesPaymentPdfServiceImpl.save(salesPaymentPdf);
			}catch (Exception e) 
			{
				Logger.error(e.getMessage());
			}

	return salesPaymentPdf;	

		
	}
	
	public 	List<ChargeBill> findDueBill(String regId)
	{
		return chargeBillRepository.findDueBill(regId);
	}

	@Override
	public List<ChargeBill> findByPatRegIdAndNetAmountNot(PatientRegistration patientRegistration, float amt) {
		return chargeBillRepository.findByPatRegIdAndNetAmountNot(patientRegistration, amt);
	}

	@Override
	public ChargeBill findByServiceId(LabServices service) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ChargeBill findByServiceIdAndPaymentType(LabServices service, String paymentType) {
		return chargeBillRepository.findByServiceIdAndPaymentType(service, paymentType);
	}

	@Override
	public ChargeBill findBySaleIdAndPaymentType(Sales sales, String paymentType) {
		return chargeBillRepository.findBySaleIdAndPaymentType(sales, paymentType);
	}

	@Override
	public ChargeBill findByLabIdAndPaymentType(LaboratoryRegistration lab, String paymentType) {
		return chargeBillRepository.findByLabIdAndPaymentType(lab, paymentType);
	}

	@Override
	public List<ChargeBill> findByPatRegIdAndIpSettledFlag(PatientRegistration patientRegistration,
			String ipSettledFlag) {
		return chargeBillRepository.findByPatRegIdAndIpSettledFlag(patientRegistration, ipSettledFlag);
	}

	

	

}
