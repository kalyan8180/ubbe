package com.example.test.testingHMS.laboratory.serviceImpl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.Principal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.test.testingHMS.MoneyToWords.NumberToWordsConverter;
import com.example.test.testingHMS.bed.model.RoomBookingDetails;
import com.example.test.testingHMS.bed.model.RoomDetails;
import com.example.test.testingHMS.bill.model.ChargeBill;
import com.example.test.testingHMS.bill.repository.ChargeBillRepository;
import com.example.test.testingHMS.bill.serviceImpl.ChargeBillServiceImpl;
import com.example.test.testingHMS.finalBilling.model.FinalBilling;
import com.example.test.testingHMS.finalBilling.serviceImpl.FinalBillingServiceImpl;
import com.example.test.testingHMS.laboratory.helper.RefLaboratoryRegistration;
import com.example.test.testingHMS.laboratory.helper.RefLaboratoryService;
import com.example.test.testingHMS.laboratory.model.LabServices;
import com.example.test.testingHMS.laboratory.model.LaboratoryRegistration;
import com.example.test.testingHMS.laboratory.model.ServicePdf;
import com.example.test.testingHMS.laboratory.repository.LabServiceRangeRepository;
import com.example.test.testingHMS.laboratory.repository.LaboratoryRegistrationRepository;
import com.example.test.testingHMS.laboratory.service.LaboratoryRegistrationService;
import com.example.test.testingHMS.patient.Helper.MultiplePayment;
import com.example.test.testingHMS.patient.model.PatientPaymentPdf;
import com.example.test.testingHMS.patient.model.PatientRegistration;
import com.example.test.testingHMS.patient.serviceImpl.CashPlusCardServiceImpl;
import com.example.test.testingHMS.patient.serviceImpl.PatientRegistrationServiceImpl;
import com.example.test.testingHMS.patient.serviceImpl.PaymentPdfServiceImpl;
import com.example.test.testingHMS.patient.serviceImpl.PaymentServiceImpl;
import com.example.test.testingHMS.user.model.User;
import com.example.test.testingHMS.user.serviceImpl.UserServiceImpl;
import com.example.test.testingHMS.utils.ConstantValues;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;

@Service
public class LaboratoryRegistrationServiceImpl implements LaboratoryRegistrationService{
	
	public static Logger Logger=LoggerFactory.getLogger(LaboratoryRegistrationServiceImpl.class);
	
	
	@Value("${hospital.logo}")
	private Resource hospitalLogo;
	
	@Autowired
	ChargeBillServiceImpl chargeBillServiceImpl;
	
	@Autowired
	ChargeBillRepository chargeBillRepository;
	
	@Autowired
	NumberToWordsConverter numberToWordsConverter;
	
	@Autowired
	PatientPaymentPdf patientPaymentPdf;

	@Autowired
	LaboratoryRegistrationServiceImpl laboratoryRegistrationServiceImpl;
	
	@Autowired
	PaymentServiceImpl  paymentServiceImpl;
	
	@Autowired
	CashPlusCardServiceImpl cashPlusCardServiceImpl;
	
	@Autowired
	FinalBillingServiceImpl finalBillingServcieImpl;
	
	@Autowired
	LabServiceRangeRepository labServiceRangeRepository;
	
	
	@Autowired
	ServicePdfServiceImpl servicePdfServiceImpl;
	
	@Autowired
	ResourceLoader resourceLoader;
	
	
	@Autowired
	ServicePdf servicePdf;
	
	@Autowired
	PaymentPdfServiceImpl paymentPdfServiceImpl;
	
	@Autowired
	PatientRegistrationServiceImpl patientRegistrationServiceImpl;
	
	@Autowired
	PatientServiceDetailsServiceImpl patientServiceDetailsServiceImpl;
	
	@Autowired
	UserServiceImpl userServiceImpl;
	
	@Autowired
	LaboratoryRegistrationRepository laboratoryRegistrationRepository;
	
	@Autowired
	LabServicesServiceImpl labServicesServiceImpl;
	
	@Autowired
	RefLaboratoryService refLaboratoryService;
	
	
	
	
	public String getNextLabId()
	{
		LaboratoryRegistration laboratoryRegistration=laboratoryRegistrationRepository.findFirstByOrderByLabRegIdDesc();
		String nextId=null;
		if(laboratoryRegistration==null)
		{
			nextId="LAB0000001";
		}
		else
		{
			int nextIntId=Integer.parseInt(laboratoryRegistration.getLabRegId().substring(3));
			nextIntId+=1;
			nextId="LAB"+String.format("%07d", nextIntId);
		}
		return nextId;
	}
	
	public String getNextOpId()
	{
		LaboratoryRegistration laboratoryRegistration=laboratoryRegistrationRepository.findFirstByOrderByLabRegIdDesc();
		String nextId=null;
		if(laboratoryRegistration==null)
		{
			nextId="OP00000001";
		}
		else
		{
			int nextIntId=Integer.parseInt(laboratoryRegistration.getLabRegId().substring(3));
			nextIntId+=1;
			nextId="OP"+String.format("%08d", nextIntId);
		}
		return nextId;
	}
	
	public String getNextBillNo() 
	{
		LaboratoryRegistration laboratoryRegistration=laboratoryRegistrationRepository.findFirstByOrderByBillNoDesc();
		String nextId=null;
		if(laboratoryRegistration==null)
		{
			nextId="BL000000001";
		}
		else
		{
			int nextIntId=Integer.parseInt(laboratoryRegistration.getBillNo().substring(2));
			nextIntId+=1;
			nextId="BL"+String.format("%09d", nextIntId);
		}
		return nextId;
	}
	
	public String getNextInvoice()
	{
		LaboratoryRegistration laboratoryRegistration=laboratoryRegistrationRepository.findFirstByOrderByLabRegIdDesc();
		String nextId=null;
		if(laboratoryRegistration==null)
		{
			nextId="INV0000001";
		}
		else
		{
			int nextIntId=Integer.parseInt(laboratoryRegistration.getInvoiceNo().substring(3));
			nextIntId+=1;
			nextId="INV"+String.format("%07d", nextIntId);
		}
		return nextId;
	}
	
	
	@Override
	public PatientPaymentPdf computeSave(LaboratoryRegistration laboratoryRegistration,Principal principal) 
	{
		String regId=null;
		
		System.out.println("---------------------------principal---------------------");
		System.out.println(principal.getName());
		PatientRegistration patientRegistration=patientRegistrationServiceImpl.findByRegId(laboratoryRegistration.getReg_id());
		laboratoryRegistration.setLaboratoryPatientRegistration(patientRegistration);
		laboratoryRegistration.setMobile(patientRegistration.getPatientDetails().getMobile());
		laboratoryRegistration.setEnteredDate(Timestamp.valueOf(LocalDateTime.now()));
		laboratoryRegistration.setPatientName(patientRegistration.getPatientDetails().getFirstName()+" "+patientRegistration.getPatientDetails().getLastName());
		String docName=patientRegistration.getPatientDetails().getConsultant();
		String[] docNameList=docName.split(" ");
		User user= userServiceImpl.findByFirstNameAndLastName(docNameList[0]+" "+docNameList[1], docNameList[2]);
		laboratoryRegistration.setRefferedById(user.getUserId());
		laboratoryRegistration.setStatus("Not-Completed");
		laboratoryRegistration.setInvoiceNo(getNextInvoice());
		regId=laboratoryRegistration.getReg_id();
		laboratoryRegistration.setEnteredBy(principal.getName());
		laboratoryRegistration.setLabServiceDate(Timestamp.valueOf(LocalDateTime.now()));
		List<RefLaboratoryRegistration> refLaboratoryRegistrations=laboratoryRegistration.getRefLaboratoryRegistrations();
	
		String billNoo=chargeBillServiceImpl.getNextBillNo();
		
		//createdBy (Security)
		User userSecurity=userServiceImpl.findOneByUserId(principal.getName());
		String createdBy=userSecurity.getFirstName()+" "+userSecurity.getLastName();
		
		String roomType="";
		
		if(patientRegistration.getpType().equalsIgnoreCase("INPATIENT"))
		{
			List<RoomBookingDetails> roomBookingDetails=patientRegistration.getRoomBookingDetails();
			 roomType=roomBookingDetails.get(0).getRoomDetails().getRoomType();
		}
		else
		{
			roomType="NA";
		}
		
		//for sms
		String serviceName="%20";
		String bill="";
		for(RefLaboratoryRegistration refLaboratoryRegistrationList:refLaboratoryRegistrations)
		{
			laboratoryRegistration.setLabRegId(getNextLabId());
			LabServices patientLabServices=labServicesServiceImpl.findPriceByType(refLaboratoryRegistrationList.getServiceName(), patientRegistration.getpType(),roomType);
			laboratoryRegistration.setLabServices(patientLabServices);	
			
			float amount=patientLabServices.getCost();
			//float discount=refLaboratoryRegistrationList.getDiscount();
			//float dicountAmount=(amount*discount)/100;
			//float netAmount=amount-dicountAmount;
			//laboratoryRegistration.setDiscount(refLaboratoryRegistrationList.getDiscount());
			laboratoryRegistration.setNetAmount(amount);
			laboratoryRegistration.setPrice(patientLabServices.getCost());
			laboratoryRegistration.setServiceName(refLaboratoryRegistrationList.getServiceName());
			laboratoryRegistration.setPaid("NO");
			bill= laboratoryRegistration.getBillNo();
			laboratoryRegistrationRepository.save(laboratoryRegistration);
			
			serviceName+=refLaboratoryRegistrationList.getServiceName()+"%20";
			if(patientRegistration.getpType().equals("INPATIENT"))
			{
				ChargeBill chargeBill=new ChargeBill();
				
				chargeBill.setChargeBillId(chargeBillServiceImpl.getNextId());
				
				chargeBill.setAmount(laboratoryRegistration.getPrice());
				chargeBill.setDiscount(laboratoryRegistration.getDiscount());
				chargeBill.setNetAmount(amount);
				List<ChargeBill> chargeBillList=chargeBillServiceImpl.findByPatRegId(patientRegistration);
				if(chargeBillList.isEmpty())
				{
					chargeBill.setBillNo(billNoo);
				}
				else
				{
					chargeBill.setBillNo(chargeBillList.get(0).getBillNo());
				}
				chargeBill.setPatRegId(patientRegistration);
				chargeBill.setPaid("NO");
				chargeBill.setQuantity(1);
				chargeBill.setInsertedBy(principal.getName());
				chargeBill.setInsertedDate(Timestamp.valueOf(LocalDateTime.now()));
				chargeBill.setLabId(laboratoryRegistration);
				chargeBillRepository.save(chargeBill);
			}
		}
		
		//for sms
				String firstname=null;
				String lastname=null;
				String mob=null;
				
				//for sms
				firstname=patientRegistration.getPatientDetails().getFirstName();
				lastname=patientRegistration.getPatientDetails().getLastName();
				Timestamp timestamp=Timestamp.valueOf(LocalDateTime.now()); 
				String date=timestamp.toString().substring(0, 10);
				String time=timestamp.toString().substring(11, 19);
				mob=String.valueOf(patientRegistration.getPatientDetails().getMobile());
				
				//Sending sms
				
				try {
			        System.out.println(firstname+" "+lastname);
				 String msg="Hi%20,"+firstname+"%20"+lastname+"%20Thanks%20for%20opting%20for%20laboratory%20Services%20"+serviceName+"%20from%20us%20UdbhavaHospitals.%20Your%20scheduled%20time%20is%20"+time+"%20at%20"+date;
		        	URL url = new URL("https://smsapi.engineeringtgr.com/send/?Mobile=9019438586&Password=N3236Q&Key=nikhilfI0ahSMOQkcb6uJ&Message="+msg+"&To="+mob);
		        	URLConnection urlcon = url.openConnection();
		            InputStream stream = urlcon.getInputStream();
		            int i;
		            String response="";
		            while ((i = stream.read()) != -1) {
		                response+=(char)i;
		            } 
		            if(response.contains("success")){
		                System.out.println("Successfully send SMS");
		            }else{
		                System.out.println(response);
		            }
		        } catch (IOException e) {
		            Logger.error(e.getMessage());
		        }
		
			/*
			 * pdf for inpatient
			 */
				PatientPaymentPdf patientPaymentPdf=null;
				if (patientRegistration.getpType().equals("INPATIENT")) {

					byte[] pdfBytes = null;
					ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

					final Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.NORMAL, BaseColor.RED);
					final Font blueFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
					final Font font = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
					final Font font1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

					final Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
					final Font headFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

					Document document = new Document(PageSize.A4_LANDSCAPE);

					String admittedWard = null;

					List<RoomBookingDetails> roomBookingDetails = patientRegistration.getRoomBookingDetails();

					for (RoomBookingDetails roomBookingDetailsInfo : roomBookingDetails) {
						RoomDetails roomDetails = roomBookingDetailsInfo.getRoomDetails();
						admittedWard = roomDetails.getRoomType();
					}

					try {

						PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);

						document.open();

						Resource fileResource = resourceLoader.getResource("classpath:udbhava.png");

										PdfPCell cell2 = new PdfPCell();

										PdfPTable table = new PdfPTable(2);

						Image img = Image.getInstance(hospitalLogo.getURL());
						img.scaleAbsolute(56, 87);
						table.setWidthPercentage(105);

						Phrase pq = new Phrase(new Chunk(img, 0, -70));
						document.add(pq);

						Paragraph p51 = new Paragraph("UDBHAVA CHILDRENS HOSPITAL", headFont);
						p51.setAlignment(Element.ALIGN_CENTER);

						document.add(p51);
						Paragraph p52 = new Paragraph("Road No. 1,MIG - 196, KPHB-Hyderabad", headFont1);
						p52.setAlignment(Element.ALIGN_CENTER);
						document.add(p52);

						/*
						 * Paragraph p53 = new Paragraph("Feel Mother Touch",redFont);
						 * p53.setAlignment(Element.ALIGN_); document.add(p53);
						 */
						// Display a date in day, month, year format
						Date date1 = Calendar.getInstance().getTime();
						DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
						String today = formatter.format(date1).toString();

						Font redFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

						Paragraph p9 = new Paragraph(" \n IP Service Slip", blueFont);
						p9.setAlignment(Element.ALIGN_CENTER);
						document.add(p9);

						PdfPCell cell1 = new PdfPCell();
						PdfPTable table2 = new PdfPTable(2);
						table2.setWidths(new float[] { 5f, 7f });
						// table2.setSpacingBefore(10);

						PdfPCell hcell1;
						hcell1 = new PdfPCell(new Phrase(
								"\n  Service No                :	" + laboratoryRegistration.getInvoiceNo(), redFont1));

						hcell1.setBorder(Rectangle.NO_BORDER);
						hcell1.setPaddingLeft(-25f);
						// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);

						table2.addCell(hcell1);

						hcell1 = new PdfPCell(new Phrase(" \n  Service Date    :	" + today, redFont1));
						hcell1.setBorder(Rectangle.NO_BORDER);
						// hcell1.setPaddingRight(-40f);
						hcell1.setPaddingLeft(70f);
						hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
						table2.addCell(hcell1);

						PdfPCell hcell4;
						hcell4 = new PdfPCell(
								new Phrase("  Addmission No         :	" + patientRegistration.getRegId(), redFont1));
						hcell4.setBorder(Rectangle.NO_BORDER);
						hcell4.setPaddingLeft(-25f);
						// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
						table2.addCell(hcell4);

						hcell4 = new PdfPCell(new Phrase(
								"  UMR No          :	" + patientRegistration.getPatientDetails().getUmr(), redFont1));
						hcell4.setBorder(Rectangle.NO_BORDER);
						// hcell4.setPaddingRight(5f);
						hcell4.setPaddingLeft(70f);

						hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
						table2.addCell(hcell4);
						PdfPCell hcell15;
						hcell15 = new PdfPCell(new Phrase(
								"  P. Name                    :	" + patientRegistration.getPatientDetails().getFirstName() + " "
										+ patientRegistration.getPatientDetails().getLastName(),
								redFont1));
						hcell15.setBorder(Rectangle.NO_BORDER);
						hcell15.setPaddingLeft(-25f);
						// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
						table2.addCell(hcell15);

						hcell15 = new PdfPCell(
								new Phrase("  Age/Sex           :	" + patientRegistration.getPatientDetails().getAge() + "/"
										+ patientRegistration.getPatientDetails().getGender(), redFont1));
						hcell15.setBorder(Rectangle.NO_BORDER);
						// hcell15.setPaddingRight(5f);
						hcell15.setPaddingLeft(70f);
						hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
						table2.addCell(hcell15);

						PdfPCell hcell16;
						hcell16 = new PdfPCell(new Phrase("  Ward                         :	" + admittedWard, redFont1));

						hcell16.setBorder(Rectangle.NO_BORDER);
						hcell16.setPaddingLeft(-25f);
						// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);

						table2.addCell(hcell16);

						hcell16 = new PdfPCell(new Phrase(
								"  Ref. By             : "
										+ patientRegistration.getPatientDetails().getvRefferalDetails().getRefName(),
								redFont1));
						hcell16.setBorder(Rectangle.NO_BORDER);
						hcell16.setPaddingLeft(70f);
						hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
						table2.addCell(hcell16);

						PdfPCell hcell17;
						hcell17 = new PdfPCell(new Phrase("  History                      :	" + "  ", redFont1));

						hcell17.setBorder(Rectangle.NO_BORDER);
						hcell17.setPaddingLeft(-25f);
						// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);

						table2.addCell(hcell17);

						hcell17 = new PdfPCell(new Phrase("  Lab No             :	", redFont1));
						hcell17.setBorder(Rectangle.NO_BORDER);
						hcell17.setPaddingLeft(70f);
						hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
						table2.addCell(hcell17);

						hcell17 = new PdfPCell(new Phrase("  Indent No                  :	", redFont1));
						hcell17.setBorder(Rectangle.NO_BORDER);
						hcell17.setPaddingLeft(-25f);
						// hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
						table2.addCell(hcell17);
						// table2.addCell(cell1);

						hcell17 = new PdfPCell(new Phrase(" ", redFont1));
						hcell17.setBorder(Rectangle.NO_BORDER);
						hcell17.setPaddingLeft(70f);
						hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
						table2.addCell(hcell17);

						cell1.setFixedHeight(107f);
						cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
						// cell1.setBorder(Rectangle.LEFT);
						// cell1.setBorder(Rectangle.RIGHT);
						cell1.setColspan(2);
						document.add(table2);

						document.add(cell1);

						PdfPTable table15 = new PdfPTable(1);
						table15.setWidths(new int[] { 15 });

						PdfPCell hcell63 = new PdfPCell(new Phrase(
								"__________________________________________________________________________________________________________________ ",
								font));
						hcell63.setBorder(Rectangle.NO_BORDER);
						table15.setWidthPercentage(120f);
						// hcell63 .setFixedHeight(10);
						hcell63.setHorizontalAlignment(Element.ALIGN_CENTER);
						hcell63.setPaddingRight(10f);
						table15.addCell(hcell63);
						document.add(table15);

						PdfPCell cell3 = new PdfPCell();
						PdfPTable table3 = new PdfPTable(7);
						table3.setWidths(new float[] { 2f, 3f, 3f, 4f, 3f, 3f, 2f });
						// table2.setSpacingBefore(10);
						// cell3.setBorder(Rectangle.BOX);
						PdfPCell hcell2;
						hcell2 = new PdfPCell(new Phrase("\n S.No ", font));

						hcell2.setBorder(Rectangle.NO_BORDER);
						// hcell2.setPaddingLeft(-25f);
						hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);

						table3.addCell(hcell2);

						hcell2 = new PdfPCell(new Phrase("\n  Ser.Code", font));
						hcell2.setBorder(Rectangle.NO_BORDER);
						// hcell1.setPaddingRight(-40f);
						// hcell3.setPaddingLeft(90f);
						hcell2.setHorizontalAlignment(Element.ALIGN_CENTER);
						table3.addCell(hcell2);

						hcell2 = new PdfPCell(new Phrase(" \n Dept. Name", font));
						hcell2.setBorder(Rectangle.NO_BORDER);
						// hcell5.setPaddingLeft(-25f);
						hcell2.setHorizontalAlignment(Element.ALIGN_CENTER);
						table3.addCell(hcell2);

						hcell2 = new PdfPCell(new Phrase(" \n Ser.Name", font));
						hcell2.setBorder(Rectangle.NO_BORDER);
						// hcell4.setPaddingRight(5f);
						// hcell6.setPaddingLeft(90f);
						hcell2.setHorizontalAlignment(Element.ALIGN_CENTER);
						table3.addCell(hcell2);

						hcell2 = new PdfPCell(new Phrase(" \n Profile Name", font));
						hcell2.setBorder(Rectangle.NO_BORDER);
						// hcell5.setPaddingLeft(-25f);
						hcell2.setHorizontalAlignment(Element.ALIGN_CENTER);
						table3.addCell(hcell2);

						hcell2 = new PdfPCell(new Phrase(" \n Is Emergency", font));
						hcell2.setBorder(Rectangle.NO_BORDER);
						// hcell5.setPaddingLeft(-25f);
						hcell2.setHorizontalAlignment(Element.ALIGN_CENTER);
						table3.addCell(hcell2);

						hcell2 = new PdfPCell(new Phrase(" \n Specimen", font));
						hcell2.setBorder(Rectangle.NO_BORDER);
						// hcell5.setPaddingLeft(-25f);
						hcell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
						table3.addCell(hcell2);

						table3.setWidthPercentage(100f);
						document.add(table3);

						cell3.setFixedHeight(107f);
						cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
						// cell1.setBorder(Rectangle.LEFT);
						// cell1.setBorder(Rectangle.RIGHT);
						cell3.setColspan(2);
						document.add(cell3);

						PdfPTable table16 = new PdfPTable(1);
						table16.setWidths(new int[] { 15 });

						PdfPCell hcell64 = new PdfPCell(new Phrase(
								"__________________________________________________________________________________________________________________ ",
								font));
						hcell64.setBorder(Rectangle.NO_BORDER);
						table16.setWidthPercentage(120f);
						// hcell64.setPaddingBottom(10);
						// hcell64 .setFixedHeight(15);
						hcell64.setHorizontalAlignment(Element.ALIGN_CENTER);
						hcell64.setPaddingRight(10f);
						table16.addCell(hcell64);
						document.add(table16);

						
						List<LaboratoryRegistration> laboratoryRegistrationInfo = laboratoryRegistrationRepository
								.findByInvoiceNo(laboratoryRegistration.getInvoiceNo());
						int count = 0;
						System.out.println("----------INVOICE COUNT------------------");
						System.out.println(laboratoryRegistrationInfo.size());

						for (LaboratoryRegistration lab : laboratoryRegistrationInfo) {
				
							PdfPCell cell4 = new PdfPCell();
							PdfPTable table5 = new PdfPTable(7);
							table5.setWidths(new float[] { 2f, 3f, 3f, 4f, 3f, 3f, 2f });
							// table2.setSpacingBefore(10);

							
							PdfPCell hcell5;
							hcell5 = new PdfPCell(new Phrase(String.valueOf(count = count + 1), font1));

							hcell5.setBorder(Rectangle.NO_BORDER);
							// hcell2.setPaddingLeft(-25f);
							hcell5.setHorizontalAlignment(Element.ALIGN_LEFT);

							table5.addCell(hcell5);

							hcell5 = new PdfPCell(new Phrase(lab.getLabServices().getServiceId(), font1));
							hcell5.setBorder(Rectangle.NO_BORDER);
							// hcell1.setPaddingRight(-40f);
							// hcell3.setPaddingLeft(90f);
							hcell5.setHorizontalAlignment(Element.ALIGN_CENTER);
							table5.addCell(hcell5);

							hcell5 = new PdfPCell(new Phrase(lab.getLabServices().getDepartment(), font1));
							hcell5.setBorder(Rectangle.NO_BORDER);
							// hcell5.setPaddingLeft(-25f);
							hcell5.setHorizontalAlignment(Element.ALIGN_CENTER);
							table5.addCell(hcell5);

							hcell5 = new PdfPCell(new Phrase(lab.getServiceName(), font1));
							hcell5.setBorder(Rectangle.NO_BORDER);
							// hcell4.setPaddingRight(5f);
							// hcell6.setPaddingLeft(90f);
							hcell5.setHorizontalAlignment(Element.ALIGN_CENTER);
							table5.addCell(hcell5);

							hcell5 = new PdfPCell(new Phrase("--", font1));
							hcell5.setBorder(Rectangle.NO_BORDER);
							// hcell1.setPaddingRight(-40f);
							// hcell3.setPaddingLeft(90f);
							hcell5.setHorizontalAlignment(Element.ALIGN_CENTER);
							table5.addCell(hcell5);

							hcell5 = new PdfPCell(new Phrase(" ", font1));
							hcell5.setBorder(Rectangle.NO_BORDER);
							// hcell1.setPaddingRight(-40f);
							// hcell3.setPaddingLeft(90f);
							hcell5.setHorizontalAlignment(Element.ALIGN_CENTER);
							table5.addCell(hcell5);

							hcell5 = new PdfPCell(new Phrase("", font1));
							hcell5.setBorder(Rectangle.NO_BORDER);
							// hcell1.setPaddingRight(-40f);
							// hcell3.setPaddingLeft(90f);
							hcell5.setHorizontalAlignment(Element.ALIGN_CENTER);
							table5.addCell(hcell5);
							table5.setWidthPercentage(100f);
						
							document.add(table5);
						
							cell4.setFixedHeight(107f);
							cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
							// cell4.setBorder(Rectangle.LEFT);
							// cell1.setBorder(Rectangle.RIGHT);
							cell4.setColspan(2);
							document.add(cell4);

						}
						
						Paragraph p17 = new Paragraph("\n \n \n \n \n \n \n \n \n \n \n", font1);
						p17.setAlignment(Element.ALIGN_CENTER);
						document.add(p17);

						Chunk cnd = new Chunk(new VerticalPositionMark());

						Paragraph p18 = new Paragraph("Created By : " + createdBy, font);
						p18.add(cnd);
						p18.add("Create Date : " + today);

						document.add(p18);

						Paragraph p19 = new Paragraph("Printed By : " + createdBy, font);
						p19.add(cnd);
						p19.add("Printed Date : " + today);

						document.add(p19);

						document.close();

						System.out.println("finished");

						pdfBytes = byteArrayOutputStream.toByteArray();
						String uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/payment/viewFile/")
								.path(paymentPdfServiceImpl.getNextPdfId()).toUriString();

						patientPaymentPdf = new PatientPaymentPdf(regId+" Lab Registration", uri,regId, pdfBytes, bill);
						patientPaymentPdf.setPid(paymentPdfServiceImpl.getNextPdfId());
						paymentPdfServiceImpl.save(patientPaymentPdf);

					} catch (Exception e) {
						Logger.error(e.getMessage());
					}
				}

				return patientPaymentPdf;
		
	}
	

	public List<LaboratoryRegistration> findBill(String regId, String invoice)
	{
		return laboratoryRegistrationRepository.findBill(regId, invoice);
	}

	
	public LaboratoryRegistration findByLabRegId(String id)
	{
		return laboratoryRegistrationRepository.findByLabRegId(id);
	}

	
	public List<LaboratoryRegistration> findAll()
	{
		return laboratoryRegistrationRepository.findAll();
	}
	
	public static PdfPCell createCell(String content, float borderWidth, int colspan, int alignment, Font redFont) 
	{
	    PdfPCell cell1 = new PdfPCell(new Phrase(content));
	    cell1.setBorderWidth(borderWidth);
	    cell1.setColspan(colspan);
	    cell1.setHorizontalAlignment(alignment);
	    return cell1;
	}
	
	public List<Object> getService()
	{
		List<LabServices> labServices= labServicesServiceImpl.findAll();
		List<Object> displayList= new ArrayList<>();
		ArrayList<String> ref=new ArrayList<>();
		List<User> userLab=userServiceImpl.findByUserRole("LAB");
		List<User> userDoctor=userServiceImpl.findByUserRole("DOCTOR");
		displayList.add(userLab);
		displayList.add(userDoctor);
		
		for(LabServices labServicesInfo:labServices)
		{
			RefLaboratoryService refLaboratoryService=new RefLaboratoryService();
			if(!ref.contains(labServicesInfo.getServiceName()))
			{
				refLaboratoryService.setId(laboratoryRegistrationServiceImpl.getNextLabId());
				
				
				String date=Timestamp.valueOf(LocalDateTime.now()).toString().substring(0, 10);
				
				SimpleDateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat toFormat = new SimpleDateFormat("dd-MM-yyyy");
				
				Date newDate;
				String formatDate=null;
				try {
					newDate = fromFormat.parse(date);
					 formatDate=toFormat.format(newDate);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Logger.error(e.getMessage());
				}
				
 
				refLaboratoryService.setDate(formatDate);
				refLaboratoryService.setServiceName(labServicesInfo.getServiceName());
				refLaboratoryService.setInvoice(laboratoryRegistrationServiceImpl.getNextInvoice());

				ref.add(labServicesInfo.getServiceName());
				displayList.add(refLaboratoryService);
			}
			
		}
		return displayList;

	}
	
	public Map<String, String> getServiceCost(String name,String regId)
	{
		Map<String,String> displayInfo=new HashMap<>();
		PatientRegistration patientRegistration=patientRegistrationServiceImpl.findByRegId(regId);
		String roomType="";
		String patientType="";
		
		if(patientRegistration.getpType().equalsIgnoreCase(ConstantValues.INPATIENT)||patientRegistration.getpType().equalsIgnoreCase(ConstantValues.DAYCARE)||patientRegistration.getpType().equalsIgnoreCase(ConstantValues.EMERGENCY))
		{
			List<RoomBookingDetails> roomBookingDetails=patientRegistration.getRoomBookingDetails();
			 roomType=roomBookingDetails.get(0).getRoomDetails().getRoomType();
			 patientType=ConstantValues.INPATIENT;
		}
		else
		{
			roomType="NA";
			patientType=patientRegistration.getpType();
		}
		LabServices labServices=labServicesServiceImpl.findPriceByType(name,patientType,roomType);
		displayInfo.put("cost", String.valueOf(labServices.getCost()));
		displayInfo.put("type", patientRegistration.getpType());
		
		return displayInfo;
	}
	
	public List<LaboratoryRegistration> findByLaboratoryPatientRegistration(PatientRegistration patientRegistration)
	{
		
		return laboratoryRegistrationRepository.findByLaboratoryPatientRegistration(patientRegistration);
	}

	@Override
	public List<LaboratoryRegistration> findByPaymentTypeAndLaboratoryPatientRegistration(String payment,
			PatientRegistration reg) 
	{
		return laboratoryRegistrationRepository.findByPaymentTypeAndLaboratoryPatientRegistration(payment, reg);
	}
	
	public List<LaboratoryRegistration> findUserWiseIpOpDetailed(Object fromDate,Object toDate,String userName, String status)
	{
		return laboratoryRegistrationRepository.findUserWiseIpOpDetailed(fromDate, toDate, userName, status);
	}
	
	public List<Object> getOpServices()
	{
		List<LabServices> labServices= labServicesServiceImpl.findOnlyOthers();
		List<Object> displayList= new ArrayList<>();
		ArrayList<String> ref=new ArrayList<>();
		List<User> userLab=userServiceImpl.findByUserRole("LAB");
		List<User> userDoctor=userServiceImpl.findByUserRole("DOCTOR");
		displayList.add(userLab);
		displayList.add(userDoctor);
		
		for(LabServices labServicesInfo:labServices)
		{
			RefLaboratoryService refLaboratoryService=new RefLaboratoryService();
			if(!ref.contains(labServicesInfo.getServiceName()))
			{
				refLaboratoryService.setId(laboratoryRegistrationServiceImpl.getNextOpId());
				
				
				String date=Timestamp.valueOf(LocalDateTime.now()).toString().substring(0, 10);
				
				SimpleDateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat toFormat = new SimpleDateFormat("dd-MM-yyyy");
				
				Date newDate;
				String formatDate=null;
				try {
					newDate = fromFormat.parse(date);
					 formatDate=toFormat.format(newDate);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Logger.error(e.getMessage());
				}
				
 
				refLaboratoryService.setDate(formatDate);
				refLaboratoryService.setServiceName(labServicesInfo.getServiceName());
				//refLaboratoryService.setInvoice(laboratoryRegistrationServiceImpl.getNextInvoice());

				ref.add(labServicesInfo.getServiceName());
				displayList.add(refLaboratoryService);
			}
			
		}
		return displayList;

	}
	
	public Map<String, String> getOpServiceCost(String name,String regId)
	{
		Map<String,String> displayInfo=new HashMap<>();
		PatientRegistration patientRegistration=patientRegistrationServiceImpl.findByRegId(regId);
	
		
		
		if(patientRegistration.getpType().equalsIgnoreCase("OUTPATIENT"))
		{
		LabServices labServices=labServicesServiceImpl.findPriceByType(name,patientRegistration.getpType(),"NA");
		System.out.println(String.valueOf(labServices.getCost()));
		System.out.println(patientRegistration.getpType());
		displayInfo.put("cost", String.valueOf(labServices.getCost()));
		displayInfo.put("type", patientRegistration.getpType());
		}
		return displayInfo;
	}
	
	
	
	@Transactional
	public PatientPaymentPdf registerServiceP(LaboratoryRegistration laboratoryRegistration, Principal principal)
			throws Exception {

		float amount = 0;
		float discount = 0;
		float dicountAmount = 0;
		float netAmount = 0;
		long mobile = 0;
		String umr = "";
		String regId = null;
		String[] docNameList = null;
		String docName = "";
		String paymentMode = "";
		String roomType = "";
		float finalCash = 0; // final billing
		float finalCard = 0; // final billing
		float finalCheque = 0; // final billing
		float finalDue = 0; // final billing
		float finalAmountPaid = 0; // final billing
		float finalTotalAmount = 0; // final billing
		//float finalDiscAmount = 0; // final billing
		String refNo = null;
		String firstName = null;
		String lastName = null;
		String paymentType = null;
		String paid = null;
		float finalInsuranceAmt=0;
		float finalpaidInKPHBAmt=0;

		String payCash = null;
		String payCard = null;
		String payDue = null;
		String payCheque = null;
		String payInsurance = null;
		String payKphb = null;
		String patientType=null;

		/*
		 * if(laboratoryRegistration.getPaymentType()==null) { throw new
		 * RuntimeException("Plz Enter Payment Type"); }
		 */

		// createdBy (Security)
		User userSecurity = userServiceImpl.findByUserName(principal.getName());
		String createdBy = userSecurity.getFirstName() + " " + userSecurity.getLastName();

		

		/* 
		 * for multiple payments 
		 */
				List<MultiplePayment> multiplePayment=laboratoryRegistration.getMultiplePayment();
		
		for(MultiplePayment multiplePaymentInfo:multiplePayment) {
			
			if (multiplePaymentInfo.getPayType().equalsIgnoreCase(ConstantValues.CARD) || multiplePaymentInfo.getPayType().equalsIgnoreCase("Credit Card")
					||multiplePaymentInfo.getPayType().equalsIgnoreCase("Debit Card")
					|| multiplePaymentInfo.getPayType().equalsIgnoreCase(ConstantValues.CASH_PLUS_CARD)) {
				laboratoryRegistration.setReferenceNumber(laboratoryRegistration.getReferenceNumber());
			}
			if (multiplePaymentInfo.getPayType().equalsIgnoreCase(ConstantValues.CASH)) {
				finalCash = multiplePaymentInfo.getAmount();
				payCash=ConstantValues.CASH;
			} else if (multiplePaymentInfo.getPayType().equalsIgnoreCase(ConstantValues.CARD)) {
				finalCard = multiplePaymentInfo.getAmount();
				payCard=ConstantValues.CARD;
			} else if (multiplePaymentInfo.getPayType().equalsIgnoreCase(ConstantValues.CHEQUE)) {
				finalCheque =multiplePaymentInfo.getAmount(); 
				payCheque=ConstantValues.CHEQUE;
			}else if(multiplePaymentInfo.getPayType().equalsIgnoreCase(ConstantValues.DUE)) {
				finalDue=multiplePaymentInfo.getAmount();
				payDue=ConstantValues.DUE;
			}else if(multiplePaymentInfo.getPayType().equalsIgnoreCase(ConstantValues.INSURANCE)) {
				finalInsuranceAmt=multiplePaymentInfo.getAmount();
				payInsurance=ConstantValues.INSURANCE;
			}else if(multiplePaymentInfo.getPayType().equalsIgnoreCase(ConstantValues.PAIDINKPHB)) {
				finalpaidInKPHBAmt=multiplePaymentInfo.getAmount();
				payKphb=ConstantValues.PAIDINKPHB;
			}
			
		}
		
				
		
		//for paytype and paid condition
		if(payDue!=null&&payCash==null&&payCard==null&&payInsurance==null&&payKphb==null) {
			paid=ConstantValues.NO;
			paymentType=ConstantValues.DUE;
		}else if(payDue==null&&payCash==null&&payCard==null&&payInsurance!=null&&payKphb==null){
			paid=ConstantValues.NO;
			paymentType=ConstantValues.INSURANCE;
			
		}else if(payDue==null&&payCash==null&&payCard==null&&payInsurance==null&&payKphb!=null) {
			paid=ConstantValues.YES;
			paymentType=ConstantValues.PAIDINKPHB;
			
		} else if(payDue==null&&payCash!=null&&payCard==null&&payInsurance==null&&payKphb==null){
			
			paid=ConstantValues.YES;
			paymentType=ConstantValues.CASH;
		} else if(payDue==null&&payCash==null&&payCard!=null&&payInsurance==null&&payKphb==null){
			paid=ConstantValues.YES;
			paymentType=ConstantValues.CARD;
		} else {
			paid=ConstantValues.PARTLY_PAID;
			paymentType=ConstantValues.MULTIPLE_PAYMENT;

			
		}

		 
		
		PatientRegistration patientRegistration = patientRegistrationServiceImpl
				.findByRegId(laboratoryRegistration.getReg_id());
		if (patientRegistration.isBlockedStatus()) {
			throw new RuntimeException("Payment for this patinet is blocked !");
		}

		String patientName = null;
		if (patientRegistration.getPatientDetails().getMiddleName() != null) {
			patientName = patientRegistration.getPatientDetails().getTitle() + ". "
					+ patientRegistration.getPatientDetails().getFirstName() + " "
					+ patientRegistration.getPatientDetails().getMiddleName() + " "
					+ patientRegistration.getPatientDetails().getLastName();
		} else {
			patientName = patientRegistration.getPatientDetails().getTitle() + ". "
					+ patientRegistration.getPatientDetails().getFirstName() + " "
					+ patientRegistration.getPatientDetails().getLastName();
		}

		umr = patientRegistration.getPatientDetails().getUmr();
		laboratoryRegistration.setLaboratoryPatientRegistration(patientRegistration);
		regId = laboratoryRegistration.getReg_id();
		laboratoryRegistration.setMobile(patientRegistration.getPatientDetails().getMobile());
		mobile = patientRegistration.getPatientDetails().getMobile();
		laboratoryRegistration.setEnteredDate(Timestamp.valueOf(LocalDateTime.now()));
		laboratoryRegistration.setPatientName(patientRegistration.getPatientDetails().getFirstName() + " "
				+ patientRegistration.getPatientDetails().getLastName());
		docName = patientRegistration.getPatientDetails().getConsultant();
		docNameList = docName.split(" ");
		User user = userServiceImpl.findOneByUserId(patientRegistration.getVuserD().getUserId());
		laboratoryRegistration.setRefferedById(user.getUserId());
		laboratoryRegistration.setUpdatedBy(userSecurity.getUserId());
		laboratoryRegistration.setUserLaboratoryRegistration(userSecurity);
		laboratoryRegistration.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
		laboratoryRegistration.setInvoiceNo(laboratoryRegistrationServiceImpl.getNextInvoice());
		laboratoryRegistration.setBillNo(laboratoryRegistrationServiceImpl.getNextBillNo());
		laboratoryRegistration.setEnteredBy(userSecurity.getUserId());
		laboratoryRegistration.setIpSettledFlag(ConstantValues.IP_SETTLED_FLAG_NO);
		List<RefLaboratoryRegistration> refLaboratoryRegistrations = laboratoryRegistration
				.getRefLaboratoryRegistrations();
		paymentMode = paymentType;

		if (patientRegistration.getpType().equalsIgnoreCase(ConstantValues.INPATIENT)||patientRegistration.getpType().equalsIgnoreCase(ConstantValues.DAYCARE)||patientRegistration.getpType().equalsIgnoreCase(ConstantValues.EMERGENCY)) {
			List<RoomBookingDetails> roomBookingDetails = patientRegistration.getRoomBookingDetails();
			roomType = roomBookingDetails.get(0).getRoomDetails().getRoomType();
			patientType=ConstantValues.INPATIENT;
		} else {
			roomType = "NA";
			patientType=patientRegistration.getpType();
		}

		// for sms
		String serviceName = "%20";
		String bill = "";
		for (RefLaboratoryRegistration refLaboratoryRegistrationList : refLaboratoryRegistrations) {
			laboratoryRegistration.setLabRegId(laboratoryRegistrationServiceImpl.getNextLabId());
			
			LabServices patientLabServices = labServicesServiceImpl.findPriceByType(
					refLaboratoryRegistrationList.getServiceName(), patientType, roomType);
			laboratoryRegistration.setLabServices(patientLabServices);

			if (laboratoryRegistration.getLabServices() == null) {
				laboratoryRegistration.setLabServices(labServicesServiceImpl.findPriceByType(
						refLaboratoryRegistrationList.getServiceName(), patientRegistration.getpType(), roomType));
			}

			if (laboratoryRegistration.getLabServices() == null) {
				throw new RuntimeException("Not Inserted");
			}

			// Discount Number
			amount = refLaboratoryRegistrationList.getAmount(); // total amount
			netAmount = refLaboratoryRegistrationList.getAmount() * refLaboratoryRegistrationList.getQuantity();
			//discount = refLaboratoryRegistrationList.getDiscount(); // discount
			netAmount = netAmount - (discount); // total amount after applying discount

			//laboratoryRegistration.setDiscount(refLaboratoryRegistrationList.getDiscount());
			laboratoryRegistration.setNetAmount(netAmount);
			laboratoryRegistration.setQuantity(refLaboratoryRegistrationList.getQuantity());
			laboratoryRegistration.setPrice(amount);
			laboratoryRegistration.setServiceName(refLaboratoryRegistrationList.getServiceName());
			/*
			 * if (laboratoryRegistration.getPaymentType().equalsIgnoreCase("Advance") &&
			 * patientRegistration.getpType().equals("INPATIENT")) {
			 * laboratoryRegistration.setPaid("NO"); } if
			 * (laboratoryRegistration.getPaymentType().equalsIgnoreCase("Due") ||
			 * laboratoryRegistration.getPaymentType().equalsIgnoreCase("Insurance")) {
			 * laboratoryRegistration.setPaid("NO"); } else {
			 * laboratoryRegistration.setPaid("YES"); }
			 */
			laboratoryRegistration.setPaid(paid);
			laboratoryRegistration.setPaymentType(paymentType);
			laboratoryRegistration.setStatus(
					(patientLabServices.getServiceType().equalsIgnoreCase("Lab")) ? "Not-Completed" : "Completed");
			//paymentMode = laboratoryRegistration.getPaymentType();
			bill = laboratoryRegistration.getBillNo();
			laboratoryRegistrationRepository.save(laboratoryRegistration);

			// Final Billing
			finalAmountPaid += netAmount;
			finalTotalAmount += refLaboratoryRegistrationList.getAmount() * refLaboratoryRegistrationList.getQuantity();
			serviceName += refLaboratoryRegistrationList.getServiceName() + "%20";
			
			if (patientRegistration.getpType().equals(ConstantValues.INPATIENT)||patientRegistration.getpType().equals(ConstantValues.DAYCARE)||patientRegistration.getpType().equals(ConstantValues.EMERGENCY)) {
				ChargeBill chargeBill = new ChargeBill();

				chargeBill.setChargeBillId(chargeBillServiceImpl.getNextId());

				chargeBill.setAmount(laboratoryRegistration.getPrice());
				chargeBill.setDiscount(laboratoryRegistration.getDiscount());
				chargeBill.setNetAmount(netAmount);
				chargeBill.setUpdatedBy(userSecurity.getUserId());
				chargeBill.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
				chargeBill.setMrp(refLaboratoryRegistrationList.getAmount());
				chargeBill.setIpSettledFlag(ConstantValues.IP_SETTLED_FLAG_NO);
				List<ChargeBill> chargeBillList = chargeBillServiceImpl.findByPatRegId(patientRegistration);
				String topBill = null;
				if (chargeBillList.isEmpty()) {
					if (chargeBillRepository.findMaxBill() != null) {
						topBill = chargeBillRepository.findMaxBill();
						int intVal = Integer.parseInt(topBill.substring(2));
						intVal += 1;
						topBill = "BL" + String.format("%07d", intVal);
					} else {
						topBill = chargeBillServiceImpl.getNextBillNo();
					}
					chargeBill.setBillNo(topBill);
				} else {

					chargeBill.setBillNo(chargeBillList.get(0).getBillNo());
				}
				chargeBill.setPatRegId(patientRegistration);
				/*
				 * if (laboratoryRegistration.getPaymentType().equalsIgnoreCase("Advance") ||
				 * laboratoryRegistration.getPaymentType().equalsIgnoreCase("Due") ||
				 * laboratoryRegistration.getPaymentType().equalsIgnoreCase("Insurance")) {
				 * chargeBill.setPaymentType(laboratoryRegistration.getPaymentType());
				 * chargeBill.setPaid("NO"); } else {
				 * chargeBill.setPaymentType(laboratoryRegistration.getPaymentType());
				 * chargeBill.setPaid("YES"); }
				 */
				chargeBill.setPaid(paid);
				chargeBill.setPaymentType(paymentType);
				chargeBill.setQuantity(refLaboratoryRegistrationList.getQuantity());
				chargeBill.setInsertedBy(userSecurity.getUserId());
				chargeBill.setInsertedDate(Timestamp.valueOf(LocalDateTime.now()));

				if (patientLabServices.getServiceType().equalsIgnoreCase("Lab")) {
					chargeBill.setLabId(laboratoryRegistration);
				} else {
					chargeBill.setServiceId(patientLabServices);
				}

				chargeBillRepository.save(chargeBill);
			}
		}

		float cashAmount = 0;
		float cardAmount = 0; 
		float chequeAmount = 0;
		

		
		
		
		
		//for final billing
		
		
	//	finalDiscAmount = finalTotalAmount - finalAmountPaid;
		FinalBilling finalBilling = new FinalBilling();
		if(finalDue!=0) {
			finalBilling.setDueStatus(ConstantValues.YES);
		}else {
			finalBilling.setDueStatus(ConstantValues.NO);
		}
		finalBilling.setBillNo(laboratoryRegistration.getBillNo());
		finalBilling.setBillType("Laboratory Registration");
		finalBilling.setCardAmount(Math.round(finalCard));
		finalBilling.setCashAmount(Math.round(finalCash));
		finalBilling.setChequeAmount(Math.round(finalCheque));
		finalBilling.setUpdatedBy(userSecurity.getUserId());
		finalBilling.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
		finalBilling.setDiscAmount(laboratoryRegistration.getDiscount());
		finalBilling.setDueAmount(Math.round(finalDue));
		finalBilling.setFinalAmountPaid(Math.round(finalAmountPaid)-laboratoryRegistration.getDiscount());
		finalBilling.setInsertedDate(Timestamp.valueOf(LocalDateTime.now()));
		finalBilling.setFinalBillUser(userSecurity);
		finalBilling.setName(patientName);
		finalBilling.setMobile(mobile);
		finalBilling.setRegNo((regId != null) ? regId : null);
		finalBilling.setPaymentType(paymentType);
		finalBilling.setTotalAmount(finalTotalAmount);
		finalBilling.setUmrNo(umr);
		finalBillingServcieImpl.computeSave(finalBilling);

	//	String refno=laboratoryRegistration.getReferenceNumber();
	String refno=	laboratoryRegistration.getReferenceNumber();
//Sending sms lab registrtion

		// for sms Dear Pavan Kumar, Thank you for Opting Laboratory Services from
		// Udbhava Hospitals.
		firstName = patientRegistration.getPatientDetails().getFirstName();
		lastName = patientRegistration.getPatientDetails().getLastName();
		Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
		Date date7 = Calendar.getInstance().getTime();
		DateFormat formatter7 = new SimpleDateFormat("dd-MMM-yyyy");
		String smsdate = formatter7.format(date7).toString();
		String time = timestamp.toString().substring(11, 19);

		try {
			// System.out.println(firstname+" "+lastname);
			String msg = "Dear%20" + firstName + "%20" + lastName
					+ ",%20Thank%20you%20for%20Opting%20Laboratory%20Services%20from%20Udbhava%20Hospitals.";
			URL url = new URL("http://api.msg91.com/api/sendhttp.php?route=4&sender=Udbava&mobiles=" + mobile
					+ "&authkey=269360AGcfrpoZe5c9a142b&message=" + msg + "&country=91");
			URLConnection urlcon = url.openConnection();
			InputStream stream = urlcon.getInputStream();
			int i;
			String response = "";
			while ((i = stream.read()) != -1) {
				response += (char) i;
			}
			if (response.contains("success")) {
				System.out.println("Successfully send SMS");
			} else {
				System.out.println(response);
			}
		} catch (IOException e) {
			Logger.error(e.getMessage());
		}

		/*
		 * // for other receipts leaving service slip String addr =
		 * "        Plot No14,15,16 & 17,Nandi Co-op.Society," +
		 * "\n                                   Main Road, Beside Navya Grand Hotel, \n                                Miyapur,Hyderabad-49,Phone:040-23046789   \n                               "
		 * + "   For Appointment Contact:8019114481   " +
		 * "\n                                   Email :udbhavahospitals@gmail.com ";
		 */

		float totalAmount = 0;
		/*
		 * pdf for inpatient
		 */
		PatientPaymentPdf patientPaymentPdf = null;
		if (patientRegistration.getpType().equals("INPATIENT")
				&& laboratoryRegistration.getPaymentType().equalsIgnoreCase("Advance")
				&& !laboratoryRegistration.getPaymentType().equalsIgnoreCase("Insurance")
				&& !laboratoryRegistration.getPaymentType().equalsIgnoreCase("Paid In KPHB")) {

			byte[] pdfBytes = null;
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			final Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.NORMAL, BaseColor.RED);
			final Font blueFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
			final Font font = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
			final Font font1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

			final Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
			final Font headFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

			Document document = new Document(PageSize.A4_LANDSCAPE);

			String admittedWard = null;

			List<RoomBookingDetails> roomBookingDetails = patientRegistration.getRoomBookingDetails();

			for (RoomBookingDetails roomBookingDetailsInfo : roomBookingDetails) {
				RoomDetails roomDetails = roomBookingDetailsInfo.getRoomDetails();
				admittedWard = roomDetails.getRoomType();
			}

			try {

				PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);

				document.open();

				Resource fileResource = resourceLoader.getResource(ConstantValues.IMAGE_PNG_CLASSPATH);

				PdfPCell cell2 = new PdfPCell();

				PdfPTable table = new PdfPTable(2);

				Image img = Image.getInstance(hospitalLogo.getURL());
				img.scaleAbsolute(ConstantValues.IMAGE_ABSOLUTE_INTIAL_POSITION, ConstantValues.IMAGE_ABSOLUTE_FINAL_POSITION);
				table.setWidthPercentage(ConstantValues.TABLE_SET_WIDTH_PERECENTAGE);

				Phrase pq = new Phrase(new Chunk(img, ConstantValues.IMAGE_SET_INTIAL_POSITION, ConstantValues.IMAGE_SET_FINAL_POSITION));				document.add(pq);

				Paragraph p51 = new Paragraph(ConstantValues.HOSPITAL_NAME, headFont);
				p51.setAlignment(Element.ALIGN_CENTER);

				document.add(p51);

				//String newAddress = "        Plot No.14,15,16 &17,Nandi Co-op. Society,     \n                         Main Road,Beside Navya Grand Hotel,Miyapur,Hyderabad,TS                       \n          Phone:040-23046789 | Appointment Contact: 8019114481   \n    Email : udbhavahospitals@gmail.com";

				Paragraph p52 = new Paragraph(ConstantValues.PATIENT_SERVICE_SLIP, headFont1);
				p52.setAlignment(Element.ALIGN_CENTER);
				document.add(p52);

				/*
				 * Paragraph p53 = new Paragraph("Feel Mother Touch",redFont);
				 * p53.setAlignment(Element.ALIGN_); document.add(p53);
				 */
				// Display a date in day, month, year format
				Date date1 = Calendar.getInstance().getTime();
				DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
				String today = formatter.format(date1).toString();

				Font redFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

				Paragraph p9 = new Paragraph(" \n IP Service Slip", blueFont);
				p9.setAlignment(Element.ALIGN_CENTER);
				document.add(p9);

				PdfPCell cell1 = new PdfPCell();
				PdfPTable table2 = new PdfPTable(6);
				table2.setWidths(new float[] { 3f, 1f, 5f, 3f, 1f, 5f });
				// table2.setSpacingBefore(10);

				PdfPCell hcell1;
				hcell1 = new PdfPCell(new Phrase("\nService No", font));

				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(-25f);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);

				table2.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase("\n:", redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(-20f);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);

				table2.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase("\n" + laboratoryRegistration.getInvoiceNo(), redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(-25f);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);

				table2.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase(" \nService Date", font));
				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setPaddingRight(-40f);

				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(10f);
				table2.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase(" \n:", redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setPaddingRight(-40f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(10f);
				table2.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase("\n" + today, redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setPaddingRight(-40f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(10f);
				table2.addCell(hcell1);

				PdfPCell hcell4;
				hcell4 = new PdfPCell(new Phrase("Addmission No", font));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-25f);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table2.addCell(hcell4);

				hcell4 = new PdfPCell(new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-20f);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table2.addCell(hcell4);

				hcell4 = new PdfPCell(new Phrase(patientRegistration.getRegId(), redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-25f);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table2.addCell(hcell4);

				hcell4 = new PdfPCell(new Phrase("UMR No", font));
				hcell4.setBorder(Rectangle.NO_BORDER);
				// hcell4.setPaddingRight(5f);
				hcell4.setPaddingLeft(10f);
				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table2.addCell(hcell4);

				hcell4 = new PdfPCell(new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				// hcell4.setPaddingRight(5f);
				hcell4.setPaddingLeft(10f);
				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table2.addCell(hcell4);

				hcell4 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getUmr(), redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				// hcell4.setPaddingRight(5f);
				hcell4.setPaddingLeft(10f);
				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table2.addCell(hcell4);

				PdfPCell hcell15;
				hcell15 = new PdfPCell(new Phrase("Patient Name", font));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-25f);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table2.addCell(hcell15);

				hcell15 = new PdfPCell(new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-20f);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table2.addCell(hcell15);

				hcell15 = new PdfPCell(new Phrase(patientName, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-25f);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table2.addCell(hcell15);

				hcell15 = new PdfPCell(new Phrase("Age/Sex", font));
				hcell15.setBorder(Rectangle.NO_BORDER);
				// hcell15.setPaddingRight(5f);
				hcell15.setPaddingLeft(10f);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				table2.addCell(hcell15);

				hcell15 = new PdfPCell(new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				// hcell15.setPaddingRight(5f);
				hcell15.setPaddingLeft(10f);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				table2.addCell(hcell15);

				hcell15 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getAge() + "/"
						+ patientRegistration.getPatientDetails().getGender(), redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				// hcell15.setPaddingRight(5f);
				hcell15.setPaddingLeft(10f);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				table2.addCell(hcell15);

				PdfPCell hcell16;
				hcell16 = new PdfPCell(new Phrase("Ward", font));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-25f);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);

				table2.addCell(hcell16);

				hcell16 = new PdfPCell(new Phrase(":", redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-20f);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);

				table2.addCell(hcell16);

				hcell16 = new PdfPCell(new Phrase(admittedWard, redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-25f);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);

				table2.addCell(hcell16);

				String refBy = null;
				if (patientRegistration.getPatientDetails().getvRefferalDetails() == null) {
					refBy = "";
				} else {
					refBy = patientRegistration.getPatientDetails().getvRefferalDetails().getRefName();
				}

				hcell16 = new PdfPCell(new Phrase("Ref. By", font));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table2.addCell(hcell16);

				hcell16 = new PdfPCell(new Phrase(":", redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table2.addCell(hcell16);

				hcell16 = new PdfPCell(new Phrase(refBy, redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table2.addCell(hcell16);

				PdfPCell hcell17;
				hcell17 = new PdfPCell(new Phrase("History", font));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-25f);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);

				table2.addCell(hcell17);

				hcell17 = new PdfPCell(new Phrase(":", redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-20f);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);

				table2.addCell(hcell17);

				hcell17 = new PdfPCell(new Phrase("  ", redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-25f);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);

				table2.addCell(hcell17);

				hcell17 = new PdfPCell(new Phrase("Lab No", font));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table2.addCell(hcell17);

				hcell17 = new PdfPCell(new Phrase(":", redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table2.addCell(hcell17);

				hcell17 = new PdfPCell(new Phrase(" ", redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table2.addCell(hcell17);

				PdfPCell hcell18;
				hcell18 = new PdfPCell(new Phrase("Indent No", font));
				hcell18.setBorder(Rectangle.NO_BORDER);
				hcell18.setPaddingLeft(-25f);
				hcell18.setPaddingBottom(20f);
				// hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table2.addCell(hcell18);
				// table2.addCell(cell1);

				hcell18 = new PdfPCell(new Phrase(":", redFont1));
				hcell18.setBorder(Rectangle.NO_BORDER);
				hcell18.setPaddingLeft(-20f);
				hcell18.setPaddingBottom(20f);
				// hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table2.addCell(hcell18);
				// table2.addCell(cell1);

				hcell18 = new PdfPCell(new Phrase(" ", redFont1));
				hcell18.setBorder(Rectangle.NO_BORDER);
				hcell18.setPaddingLeft(-25f);
				hcell18.setPaddingBottom(20f);
				// hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table2.addCell(hcell18);
				// table2.addCell(cell1);

				hcell18 = new PdfPCell(new Phrase(" ", redFont1));
				hcell18.setBorder(Rectangle.NO_BORDER);
				hcell18.setPaddingLeft(10f);
				hcell18.setPaddingBottom(20f);
				hcell18.setHorizontalAlignment(Element.ALIGN_LEFT);
				table2.addCell(hcell18);

				hcell18 = new PdfPCell(new Phrase(" ", redFont1));
				hcell18.setBorder(Rectangle.NO_BORDER);
				hcell18.setPaddingLeft(10f);
				hcell18.setPaddingBottom(20f);
				hcell18.setHorizontalAlignment(Element.ALIGN_LEFT);
				table2.addCell(hcell18);

				hcell18 = new PdfPCell(new Phrase(" ", redFont1));
				hcell18.setBorder(Rectangle.NO_BORDER);
				hcell18.setPaddingLeft(10f);
				hcell18.setPaddingBottom(20f);
				hcell18.setHorizontalAlignment(Element.ALIGN_LEFT);
				table2.addCell(hcell18);

				// cell1.setFixedHeight(107f);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				// cell1.setBorder(Rectangle.LEFT);
				// cell1.setBorder(Rectangle.RIGHT);
				cell1.setColspan(2);
				document.add(table2);

				document.add(cell1);

				/*
				 * PdfPTable table15 = new PdfPTable(1); table15.setWidths(new int[] { 15 });
				 * 
				 * PdfPCell hcell63 = new PdfPCell(new Phrase(
				 * "__________________________________________________________________________________________________________________ "
				 * , font)); hcell63.setBorder(Rectangle.NO_BORDER);
				 * table15.setWidthPercentage(120f); // hcell63 .setFixedHeight(10);
				 * hcell63.setHorizontalAlignment(Element.ALIGN_CENTER);
				 * hcell63.setPaddingRight(10f); table15.addCell(hcell63);
				 * document.add(table15);
				 */
				PdfPCell cell3 = new PdfPCell();
				PdfPTable table3 = new PdfPTable(7);
				table3.setWidths(new float[] { 2f, 2f, 4f, 4f, 3f, 2.5f, 3f });
				// table2.setSpacingBefore(10);
				// cell3.setBorder(Rectangle.BOX);
				PdfPCell hcell2;
				hcell2 = new PdfPCell(new Phrase("S.No ", font));
				hcell2.setBorder(Rectangle.TOP | Rectangle.BOTTOM);
				hcell2.setPaddingTop(5f);
				hcell2.setPaddingTop(5f);
				hcell2.setBackgroundColor(BaseColor.LIGHT_GRAY);
				// hcell2.setPaddingLeft(-25f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);

				table3.addCell(hcell2);

				hcell2 = new PdfPCell(new Phrase("Ser.Code", font));
				hcell2.setBorder(Rectangle.TOP | Rectangle.BOTTOM);
				hcell2.setPaddingTop(5f);
				hcell2.setPaddingTop(5f);
				hcell2.setBackgroundColor(BaseColor.LIGHT_GRAY);
				// hcell1.setPaddingRight(-40f);
				hcell2.setHorizontalAlignment(Element.ALIGN_CENTER);
				hcell2.setPaddingLeft(-15f);
				table3.addCell(hcell2);

				hcell2 = new PdfPCell(new Phrase("Dept. Name", font));
				hcell2.setBorder(Rectangle.TOP | Rectangle.BOTTOM);
				hcell2.setPaddingTop(5f);
				hcell2.setPaddingTop(5f);
				hcell2.setBackgroundColor(BaseColor.LIGHT_GRAY);
				hcell2.setPaddingLeft(-30f);
				hcell2.setHorizontalAlignment(Element.ALIGN_CENTER);
				table3.addCell(hcell2);

				hcell2 = new PdfPCell(new Phrase("Ser.Name", font));
				// hcell2.setBorder(Rectangle.NO_BORDER);
				hcell2.setBorder(Rectangle.TOP | Rectangle.BOTTOM);
				hcell2.setPaddingTop(5f);
				hcell2.setPaddingTop(5f);
				hcell2.setBackgroundColor(BaseColor.LIGHT_GRAY);
				// hcell4.setPaddingRight(5f);
				hcell2.setPaddingLeft(-30f);
				hcell2.setHorizontalAlignment(Element.ALIGN_CENTER);
				table3.addCell(hcell2);

				hcell2 = new PdfPCell(new Phrase("Ser.Cost", font));
				hcell2.setBorder(Rectangle.TOP | Rectangle.BOTTOM);
				hcell2.setPaddingTop(5f);
				hcell2.setPaddingTop(5f);
				hcell2.setBackgroundColor(BaseColor.LIGHT_GRAY);
				// hcell4.setPaddingRight(5f);
				hcell2.setPaddingLeft(20f);
				hcell2.setHorizontalAlignment(Element.ALIGN_LEFT);
				table3.addCell(hcell2);

				hcell2 = new PdfPCell(new Phrase("Profile Name", font));
				hcell2.setBorder(Rectangle.TOP | Rectangle.BOTTOM);
				hcell2.setPaddingTop(5f);
				hcell2.setPaddingTop(5f);
				hcell2.setBackgroundColor(BaseColor.LIGHT_GRAY);
				// hcell2.setPaddingLeft(40f);
				hcell2.setHorizontalAlignment(Element.ALIGN_CENTER);
				table3.addCell(hcell2);

				hcell2 = new PdfPCell(new Phrase("Is Emergency", font));
				hcell2.setBorder(Rectangle.TOP | Rectangle.BOTTOM);
				hcell2.setPaddingTop(5f);
				hcell2.setPaddingTop(5f);
				hcell2.setBackgroundColor(BaseColor.LIGHT_GRAY);
				// hcell2.setPaddingLeft(15f);
				hcell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table3.addCell(hcell2);

				table3.setWidthPercentage(100f);
				document.add(table3);

				cell3.setFixedHeight(107f);
				cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
				// cell1.setBorder(Rectangle.LEFT);
				// cell1.setBorder(Rectangle.RIGHT);
				cell3.setColspan(2);
				document.add(cell3);

				List<LaboratoryRegistration> laboratoryRegistrationInfo = laboratoryRegistrationRepository
						.findByInvoiceNo(laboratoryRegistration.getInvoiceNo());
				int count = 0;
				System.out.println("----------INVOICE COUNT------------------");
				System.out.println(laboratoryRegistrationInfo.size());

				for (LaboratoryRegistration lab : laboratoryRegistrationInfo) {

					PdfPCell cell4 = new PdfPCell();
					PdfPTable table5 = new PdfPTable(7);
					table5.setWidths(new float[] { 2f, 2f, 4f, 4f, 3f, 2f, 2f });
					// table2.setSpacingBefore(10);

					PdfPCell hcell5;
					hcell5 = new PdfPCell(new Phrase(String.valueOf(count = count + 1), font1));

					hcell5.setBorder(Rectangle.NO_BORDER);
					hcell5.setHorizontalAlignment(Element.ALIGN_LEFT);

					table5.addCell(hcell5);

					hcell5 = new PdfPCell(new Phrase("", font1));
					hcell5.setBorder(Rectangle.NO_BORDER);

					hcell5.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell5.setPaddingLeft(-10f);
					table5.addCell(hcell5);

					hcell5 = new PdfPCell(new Phrase("", font1));
					hcell5.setBorder(Rectangle.NO_BORDER);
					hcell5.setHorizontalAlignment(Element.ALIGN_LEFT);
					table5.addCell(hcell5);

					hcell5 = new PdfPCell(new Phrase(lab.getServiceName(), font1));
					hcell5.setBorder(Rectangle.NO_BORDER);
					hcell5.setPaddingLeft(-20f);
					hcell5.setHorizontalAlignment(Element.ALIGN_LEFT);
					table5.addCell(hcell5);

					hcell5 = new PdfPCell(new Phrase(String.valueOf(lab.getPrice()), font1));
					hcell5.setBorder(Rectangle.NO_BORDER);

					hcell5.setHorizontalAlignment(Element.ALIGN_RIGHT);
					hcell5.setPaddingRight(50f);
					table5.addCell(hcell5);

					hcell5 = new PdfPCell(new Phrase("--", font1));
					hcell5.setBorder(Rectangle.NO_BORDER);
					hcell5.setHorizontalAlignment(Element.ALIGN_LEFT);
					table5.addCell(hcell5);

					hcell5 = new PdfPCell(new Phrase("", font1));
					hcell5.setBorder(Rectangle.NO_BORDER);
					hcell5.setHorizontalAlignment(Element.ALIGN_CENTER);
					table5.addCell(hcell5);
					table5.setWidthPercentage(100f);

					document.add(table5);

					cell4.setFixedHeight(107f);
					cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell4.setColspan(2);
					document.add(cell4);

					totalAmount += lab.getPrice();

				}

				Paragraph p18 = new Paragraph("\n", font1);
				p18.setAlignment(Element.ALIGN_CENTER);
				document.add(p18);

				PdfPCell cell4 = new PdfPCell();
				PdfPTable table5 = new PdfPTable(7);
				table5.setWidths(new float[] { 6f, 2f, 2f, 2f, 3f, 2f, 2f });

				PdfPCell hcell5;
				hcell5 = new PdfPCell(new Phrase("TOTAL", font));

				hcell5.setBorder(Rectangle.TOP | Rectangle.BOTTOM);
				hcell5.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell5.setPaddingLeft(45f);
				table5.addCell(hcell5);

				hcell5 = new PdfPCell(new Phrase("", font1));
				hcell5.setBorder(Rectangle.TOP | Rectangle.BOTTOM);

				hcell5.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell5.setPaddingLeft(-10f);
				table5.addCell(hcell5);

				hcell5 = new PdfPCell(new Phrase("", font1));
				hcell5.setBorder(Rectangle.TOP | Rectangle.BOTTOM);
				hcell5.setHorizontalAlignment(Element.ALIGN_LEFT);
				table5.addCell(hcell5);

				hcell5 = new PdfPCell(new Phrase("", font1));
				hcell5.setBorder(Rectangle.TOP | Rectangle.BOTTOM);
				hcell5.setPaddingLeft(-20f);
				hcell5.setHorizontalAlignment(Element.ALIGN_LEFT);
				table5.addCell(hcell5);

				hcell5 = new PdfPCell(new Phrase(String.valueOf(totalAmount), font));
				hcell5.setBorder(Rectangle.TOP | Rectangle.BOTTOM);

				hcell5.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell5.setPaddingRight(50f);
				table5.addCell(hcell5);

				hcell5 = new PdfPCell(new Phrase("", font1));
				hcell5.setBorder(Rectangle.TOP | Rectangle.BOTTOM);
				hcell5.setHorizontalAlignment(Element.ALIGN_LEFT);
				table5.addCell(hcell5);

				hcell5 = new PdfPCell(new Phrase("", font1));
				hcell5.setBorder(Rectangle.TOP | Rectangle.BOTTOM);
				hcell5.setHorizontalAlignment(Element.ALIGN_CENTER);
				table5.addCell(hcell5);

				table5.setWidthPercentage(100f);
				document.add(table5);

				Paragraph p17 = new Paragraph("\n \n \n \n \n \n \n \n \n \n \n", font1);
				p17.setAlignment(Element.ALIGN_CENTER);
				document.add(p17);

				Chunk cnd = new Chunk(new VerticalPositionMark());

				Paragraph p29 = new Paragraph("Created By : " + createdBy, font);
				p29.add(cnd);
				p29.add("Create Date : " + today);

				document.add(p29);

				Paragraph p19 = new Paragraph("Printed By : " + createdBy, font);
				p19.add(cnd);
				p19.add("Printed Date : " + today);

				document.add(p19);

				document.close();

				System.out.println("finished");

				pdfBytes = byteArrayOutputStream.toByteArray();
				String uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/payment/viewFile/")
						.path(paymentPdfServiceImpl.getNextPdfId()).toUriString();

				patientPaymentPdf = new PatientPaymentPdf(regId + " Lab Registration", uri, regId, pdfBytes, bill);
				patientPaymentPdf.setPid(paymentPdfServiceImpl.getNextPdfId());
				paymentPdfServiceImpl.save(patientPaymentPdf);

			} catch (Exception e) {
				Logger.error(e.getMessage());
			}
		}

		else if (patientRegistration.getpType().equals("INPATIENT")
				&& laboratoryRegistration.getPaymentType().equalsIgnoreCase("Due")
				&& !laboratoryRegistration.getPaymentType().equalsIgnoreCase("Insurance")
				&& !laboratoryRegistration.getPaymentType().equalsIgnoreCase("Paid In KPHB")) {

			List<LaboratoryRegistration> laboratoryRegistrationInfor = laboratoryRegistrationServiceImpl
					.findBill(laboratoryRegistration.getReg_id(), laboratoryRegistration.getInvoiceNo());

			patientPaymentPdf = null;
			byte[] pdfBytes = null;
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			Document document = new Document(PageSize.A4_LANDSCAPE);

			try {

				Resource fileResource = resourceLoader.getResource(ConstantValues.IMAGE_PNG_CLASSPATH);
				Chunk cnd1 = new Chunk(new VerticalPositionMark());
				Font redFont1 = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
				Font redFont2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
				PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);
				Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
				Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
				Font headFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);

				Font redFontb = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.NORMAL);

				document.open();
				PdfPTable table = new PdfPTable(2);

				Image img = Image.getInstance(hospitalLogo.getURL());
				img.scaleAbsolute(ConstantValues.IMAGE_ABSOLUTE_INTIAL_POSITION, ConstantValues.IMAGE_ABSOLUTE_FINAL_POSITION);
				table.setWidthPercentage(ConstantValues.TABLE_SET_WIDTH_PERECENTAGE);

				Phrase pq = new Phrase(new Chunk(img, ConstantValues.IMAGE_SET_INTIAL_POSITION, ConstantValues.IMAGE_SET_FINAL_POSITION));
				pq.add(new Chunk(ConstantValues.LAB_OSP_ADDRESS, redFont));
				PdfPCell cellp = new PdfPCell(pq);
				PdfPCell cell1 = new PdfPCell();

				// Display a date in day, month, year format
				Date dateInfo = Calendar.getInstance().getTime();

				DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
				String today = formatter.format(dateInfo).toString();

				// for header Bold
				PdfPTable table96 = new PdfPTable(1);
				table96.setWidths(new float[] { 5f });
				table96.setSpacingBefore(10);

				PdfPCell hcell96;
				hcell96 = new PdfPCell(new Phrase(ConstantValues.HOSPITAL_NAME, headFont1));
				hcell96.setBorder(Rectangle.NO_BORDER);
				hcell96.setHorizontalAlignment(Element.ALIGN_CENTER);
				hcell96.setPaddingLeft(50f);

				table96.addCell(hcell96);
				cell1.addElement(table96);

				// for header end

				cell1.addElement(pq);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(cell1);

				PdfPCell cell3 = new PdfPCell();

				PdfPTable table99 = new PdfPTable(3);
				table99.setWidths(new float[] { 3f, 1f, 4f });
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
				hcell90.setPaddingLeft(-64f);

				table99.addCell(hcell90);

				hcell90 = new PdfPCell(new Phrase(patientName, redFont));
				hcell90.setBorder(Rectangle.NO_BORDER);
				hcell90.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell90.setPaddingBottom(-7f);
				hcell90.setPaddingLeft(-88f);
				table99.addCell(hcell90);

				cell3.addElement(table99);

				PdfPTable table2 = new PdfPTable(6);
				table2.setWidths(new float[] { 3f, 1f, 5.5f, 3f, 1f, 4f });
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

				hcell1 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getAge() + "/"
						+ patientRegistration.getPatientDetails().getGender(), redFont));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-30f);
				table2.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase("UMR NO", redFont));
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
				hcell4 = new PdfPCell(new Phrase("Bill Dt", redFont));
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

				hcell4 = new PdfPCell(new Phrase("INV No", redFont));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell4.setPaddingRight(-27.5f);
				table2.addCell(hcell4);

				hcell4 = new PdfPCell(new Phrase(":", redFont));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table2.addCell(hcell4);

				hcell4 = new PdfPCell(new Phrase(laboratoryRegistration.getInvoiceNo(), redFont));
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

				String refBy = null;
				if (patientRegistration.getPatientDetails().getvRefferalDetails() == null) {
					refBy = "";
				} else {
					refBy = patientRegistration.getPatientDetails().getvRefferalDetails().getRefName();
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

				hcell15 = new PdfPCell(new Phrase(":", redFont));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table2.addCell(hcell15);

				hcell15 = new PdfPCell(
						new Phrase(String.valueOf(patientRegistration.getPatientDetails().getMobile()), redFont));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingRight(-27.5f);
				// hcell1.setPaddingTop(-5f);
				table2.addCell(hcell15);

				PdfPCell hcell6;
				hcell6 = new PdfPCell(new Phrase("RegNo", redFont));
				hcell6.setBorder(Rectangle.NO_BORDER);
				hcell6.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell6.setPaddingLeft(-25f);
				table2.addCell(hcell6);

				hcell6 = new PdfPCell(new Phrase(":", redFont));
				hcell6.setBorder(Rectangle.NO_BORDER);
				hcell6.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell6.setPaddingLeft(-20f);
				table2.addCell(hcell6);

				hcell6 = new PdfPCell(new Phrase(patientRegistration.getRegId(), redFont));
				hcell6.setBorder(Rectangle.NO_BORDER);
				hcell6.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell6.setPaddingLeft(-30f);
				table2.addCell(hcell6);

				hcell6 = new PdfPCell(new Phrase("Bill No ", redFont));
				hcell6.setBorder(Rectangle.NO_BORDER);
				hcell6.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell6.setPaddingRight(-27.5f);
				// hcell4.setPaddingLeft(25f);
				table2.addCell(hcell6);

				hcell6 = new PdfPCell(new Phrase(":", redFont));
				hcell6.setBorder(Rectangle.NO_BORDER);
				hcell6.setHorizontalAlignment(Element.ALIGN_RIGHT);
				// hcell1.setPaddingTop(-5f);;
				table2.addCell(hcell6);

				hcell6 = new PdfPCell(new Phrase(laboratoryRegistration.getBillNo(), redFont));
				hcell6.setBorder(Rectangle.NO_BORDER);
				hcell6.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell6.setPaddingRight(-27.5f);
				// hcell1.setPaddingTop(-5f);
				table2.addCell(hcell6);

				cell3.setFixedHeight(115f);
				cell3.setColspan(2);
				cell3.addElement(table2);

				PdfPTable table98 = new PdfPTable(3);
				table98.setWidths(new float[] { 3f, 1f, 4f });
				table98.setSpacingBefore(10);

				PdfPCell hcell91;
				hcell91 = new PdfPCell(new Phrase("Consultant", redFont));
				hcell91.setBorder(Rectangle.NO_BORDER);
				hcell91.setHorizontalAlignment(Element.ALIGN_LEFT);
				// hcell91.setPaddingBottom(3f);
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

				table.addCell(cell3);

				// *****************************

				PdfPCell cell19 = new PdfPCell();

				PdfPTable table21 = new PdfPTable(1);
				table21.setWidths(new float[] { 4f });
				table21.setSpacingBefore(10);

				PdfPCell hcell19;
				hcell19 = new PdfPCell(new Phrase("IP Due Bill Cum Reciept", headFont1));
				hcell19.setBorder(Rectangle.NO_BORDER);
				hcell19.setHorizontalAlignment(Element.ALIGN_CENTER);
				table21.addCell(hcell19);

				cell19.setFixedHeight(20f);
				cell19.setColspan(2);
				cell19.addElement(table21);
				table.addCell(cell19);

				PdfPCell cell31 = new PdfPCell();

				PdfPTable table1 = new PdfPTable(7);
				table1.setWidths(new float[] { 1f, 3f, 5f, 3f, 1f, 2f, 2f });

				table1.setSpacingBefore(10);

				PdfPCell hcell;
				hcell = new PdfPCell(new Phrase("S.No", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("Service Code", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("Service Name", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("Service Type", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("Qty", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("Rate", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table1.addCell(hcell);

				/*
				 * hcell = new PdfPCell(new Phrase("Discount", redFont));
				 * hcell.setBorder(Rectangle.NO_BORDER);
				 * hcell.setBackgroundColor(BaseColor.GRAY);
				 * hcell.setHorizontalAlignment(Element.ALIGN_CENTER); table1.addCell(hcell);
				 */
				hcell = new PdfPCell(new Phrase("Amount(RS)", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table1.addCell(hcell);

				int count = 0;
				float total = 0;
				for (LaboratoryRegistration laboratoryRegistrationInfo : laboratoryRegistrationInfor) {

					PdfPCell cell;

					cell = new PdfPCell(new Phrase(String.valueOf(count = count + 1), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table1.addCell(cell);

					cell = new PdfPCell(
							new Phrase(laboratoryRegistrationInfo.getLabServices().getServiceId(), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table1.addCell(cell);

					cell = new PdfPCell(new Phrase(laboratoryRegistrationInfo.getServiceName(), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					// cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					table1.addCell(cell);

					cell = new PdfPCell(
							new Phrase(laboratoryRegistrationInfo.getLabServices().getServiceType(), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					// cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					table1.addCell(cell);

					cell = new PdfPCell(new Phrase(String.valueOf(laboratoryRegistrationInfo.getQuantity()), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table1.addCell(cell);

					cell = new PdfPCell(new Phrase(String.valueOf(laboratoryRegistrationInfo.getPrice()), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table1.addCell(cell);

					/*
					 * cell = new PdfPCell(new
					 * Phrase(String.valueOf(laboratoryRegistrationInfo.getDiscount()), redFont));
					 * cell.setBorder(Rectangle.NO_BORDER); cell.setPaddingLeft(5);
					 * cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					 * cell.setHorizontalAlignment(Element.ALIGN_CENTER); table1.addCell(cell);
					 */
					cell = new PdfPCell(new Phrase(String.valueOf(laboratoryRegistrationInfo.getNetAmount()), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table1.addCell(cell);

					total += laboratoryRegistrationInfo.getNetAmount();
					System.out.println("------------Nikhil----------");
					System.out.println(total);

				}
				total = Math.round(total);
				System.out.println(total);

				PdfPTable table37 = new PdfPTable(6);
				table37.setWidths(new float[] { 3f, 1f, 4f, 7f, 1f, 4f });
				table37.setSpacingBefore(10);

				PdfPCell cell55;
				cell55 = new PdfPCell(new Phrase("", redFont));
				cell55.setBorder(Rectangle.NO_BORDER);
				cell55.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell55.setPaddingTop(10f);
				// cell55.setPaddingLeft(-50f);
				table37.addCell(cell55);

				cell55 = new PdfPCell(new Phrase("", redFont));
				cell55.setBorder(Rectangle.NO_BORDER);
				cell55.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell55.setPaddingTop(10f);
				// cell55.setPaddingLeft(-50f);
				table37.addCell(cell55);

				cell55 = new PdfPCell(new Phrase("", redFont));
				cell55.setBorder(Rectangle.NO_BORDER);
				cell55.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell55.setPaddingTop(10f);
				// cell55.setPaddingLeft(-50f);
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
				cell55.setPaddingRight(-30f);
				table37.addCell(cell55);
				
				
				
				PdfPCell cell551;
				cell551 = new PdfPCell(new Phrase("", redFont));
				cell551.setBorder(Rectangle.NO_BORDER);
				cell551.setHorizontalAlignment(Element.ALIGN_LEFT);
				// cell55.setPaddingLeft(-50f);
				table37.addCell(cell551);

				cell551 = new PdfPCell(new Phrase("", redFont));
				cell551.setBorder(Rectangle.NO_BORDER);
				cell551.setHorizontalAlignment(Element.ALIGN_LEFT);
				// cell55.setPaddingLeft(-50f);
				table37.addCell(cell551);

				cell551 = new PdfPCell(new Phrase("", redFont));
				cell551.setBorder(Rectangle.NO_BORDER);
				cell551.setHorizontalAlignment(Element.ALIGN_LEFT);
				// cell55.setPaddingLeft(-50f);
				table37.addCell(cell551);

				cell551 = new PdfPCell(new Phrase("Disc Amt", redFont));
				cell551.setBorder(Rectangle.NO_BORDER);
				cell551.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell551.setPaddingRight(-70f);
				table37.addCell(cell551);

				cell551 = new PdfPCell(new Phrase(":", redFont));
				cell551.setBorder(Rectangle.NO_BORDER);
				cell551.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell551.setPaddingRight(-60f);
				table37.addCell(cell551);

				cell551 = new PdfPCell(new Phrase(String.valueOf(laboratoryRegistration.getDiscount()), redFont));
				cell551.setBorder(Rectangle.NO_BORDER);
				cell551.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell551.setPaddingRight(-30f);
				table37.addCell(cell551);


				PdfPCell hcell56;
				hcell56 = new PdfPCell(new Phrase(paymentMode + " Amt.", redFont));
				hcell56.setBorder(Rectangle.NO_BORDER);
				hcell56.setPaddingLeft(-1f);
				hcell56.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell56.setPaddingLeft(-50f);
				table37.addCell(hcell56);

				hcell56 = new PdfPCell(new Phrase(":", redFont));
				hcell56.setBorder(Rectangle.NO_BORDER);
				hcell56.setPaddingLeft(-1f);
				hcell56.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell56.setPaddingLeft(-50f);
				table37.addCell(hcell56);

				hcell56 = new PdfPCell(new Phrase(String.valueOf(total-laboratoryRegistration.getDiscount()), redFont));
				hcell56.setBorder(Rectangle.NO_BORDER);
				hcell56.setPaddingLeft(-1f);
				hcell56.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell56.setPaddingLeft(-40f);
				table37.addCell(hcell56);

				hcell56 = new PdfPCell(new Phrase("Due Amt.", redFont));
				hcell56.setBorder(Rectangle.NO_BORDER);
				hcell56.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell56.setPaddingRight(-70f);
				table37.addCell(hcell56);

				hcell56 = new PdfPCell(new Phrase(":", redFont));
				hcell56.setBorder(Rectangle.NO_BORDER);
				// hcell56.setPaddingLeft(-1f);
				hcell56.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell56.setPaddingRight(-60f);
				table37.addCell(hcell56);

				hcell56 = new PdfPCell(new Phrase(String.valueOf(total-laboratoryRegistration.getDiscount()), redFont));
				hcell56.setBorder(Rectangle.NO_BORDER);
				// hcell56.setPaddingLeft(-1f);
				hcell56.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell56.setPaddingRight(-30f);
				table37.addCell(hcell56);

				PdfPCell hcell58;
				hcell58 = new PdfPCell(new Phrase(""));
				hcell58.setBorder(Rectangle.NO_BORDER);
				// hcell23.setPaddingLeft(-50f);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table37.addCell(hcell58);

				hcell58 = new PdfPCell(new Phrase(""));
				hcell58.setBorder(Rectangle.NO_BORDER);
				// hcell23.setPaddingLeft(-50f);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table37.addCell(hcell58);

				hcell58 = new PdfPCell(new Phrase(""));
				hcell58.setBorder(Rectangle.NO_BORDER);
				// hcell23.setPaddingLeft(-50f);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table37.addCell(hcell58);

				hcell58 = new PdfPCell(new Phrase("Total Amt", redFont));
				hcell58.setBorder(Rectangle.NO_BORDER);
				hcell58.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell58.setPaddingRight(-70f);
				table37.addCell(hcell58);

				hcell58 = new PdfPCell(new Phrase(":", redFont));
				hcell58.setBorder(Rectangle.NO_BORDER);
				hcell58.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell58.setPaddingRight(-60f);
				table37.addCell(hcell58);

				hcell58 = new PdfPCell(new Phrase(String.valueOf(total-laboratoryRegistration.getDiscount()), redFont));
				hcell58.setBorder(Rectangle.NO_BORDER);
				hcell58.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell58.setPaddingRight(-30f);
				table37.addCell(hcell58);

				PdfPCell hcell59;
				hcell59 = new PdfPCell(new Phrase("Gross Amount In Words ", redFont));
				hcell59.setBorder(Rectangle.NO_BORDER);
				hcell59.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell59.setPaddingLeft(-50f);
				table37.addCell(hcell59);

				hcell59 = new PdfPCell(new Phrase("", headFont));
				hcell59.setBorder(Rectangle.NO_BORDER);
				// hcell57.setPaddingTop(18f);
				hcell59.setHorizontalAlignment(Element.ALIGN_LEFT);
				table37.addCell(hcell59);
				
				long numAmount=(long)(total-laboratoryRegistration.getDiscount());

				hcell59 = new PdfPCell(new Phrase("(" + numberToWordsConverter.convert((long)total) + ")", redFont));
				hcell59.setBorder(Rectangle.NO_BORDER);
				hcell59.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell59.setPaddingLeft(-35f);
				table37.addCell(hcell59);

				hcell59 = new PdfPCell(new Phrase("", headFont));
				hcell59.setBorder(Rectangle.NO_BORDER);
				// hcell57.setPaddingTop(18f);
				hcell59.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table37.addCell(hcell59);

				hcell59 = new PdfPCell(new Phrase("", headFont));
				hcell59.setBorder(Rectangle.NO_BORDER);
				// hcell57.setPaddingTop(18f);
				hcell59.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table37.addCell(hcell59);

				hcell59 = new PdfPCell(new Phrase("", headFont));
				hcell59.setBorder(Rectangle.NO_BORDER);
				// hcell57.setPaddingTop(18f);
				hcell59.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table37.addCell(hcell59);

				PdfPCell hcell60;
				hcell60 = new PdfPCell(new Phrase("Due Amount In Words ", redFont));

				hcell60.setBorder(Rectangle.NO_BORDER);
				hcell60.setPaddingLeft(-50f);
				hcell60.setHorizontalAlignment(Element.ALIGN_LEFT);
				table37.addCell(hcell60);

				hcell60 = new PdfPCell(new Phrase("", headFont));
				hcell60.setBorder(Rectangle.NO_BORDER);
				// hcell57.setPaddingTop(18f);
				hcell60.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table37.addCell(hcell60);

				hcell60 = new PdfPCell(new Phrase("(" + numberToWordsConverter.convert(numAmount) + ")", redFont));
				hcell60.setBorder(Rectangle.NO_BORDER);
				hcell60.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell60.setPaddingLeft(-35f);
				table37.addCell(hcell60);

				hcell60 = new PdfPCell(new Phrase("", headFont));
				hcell60.setBorder(Rectangle.NO_BORDER);
				// hcell57.setPaddingTop(18f);
				hcell60.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table37.addCell(hcell60);

				hcell60 = new PdfPCell(new Phrase("", headFont));
				hcell60.setBorder(Rectangle.NO_BORDER);
				// hcell57.setPaddingTop(18f);
				hcell60.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table37.addCell(hcell60);

				hcell60 = new PdfPCell(new Phrase("", headFont));
				hcell60.setBorder(Rectangle.NO_BORDER);
				// hcell57.setPaddingTop(18f);
				hcell60.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table37.addCell(hcell60);

				cell31.setColspan(2);
				// cell31.setFixedHeight(170f);
				table1.setWidthPercentage(100f);
				cell31.addElement(table1);
				cell31.addElement(table37);
				table.addCell(cell31);

				// -----------------------

				PdfPCell cell33 = new PdfPCell();

				PdfPTable table13 = new PdfPTable(5);
				table13.setWidths(new float[] { 2f, 3f, 3f, 3f, 3f });

				table13.setSpacingBefore(10);

				PdfPCell hcell33;
				hcell33 = new PdfPCell(new Phrase("Pay Mode", redFont2));
				hcell33.setBorder(Rectangle.NO_BORDER);
				hcell33.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell33.setPaddingLeft(10f);
				table13.addCell(hcell33);

				hcell33 = new PdfPCell(new Phrase("Amount", redFont2));
				hcell33.setBorder(Rectangle.NO_BORDER);
				hcell33.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell33.setPaddingLeft(35f);
				table13.addCell(hcell33);

				hcell33 = new PdfPCell(new Phrase("Card#", redFont2));
				hcell33.setBorder(Rectangle.NO_BORDER);
				hcell33.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell33.setPaddingLeft(40f);
				table13.addCell(hcell33);

				hcell33 = new PdfPCell(new Phrase("Bank Name", redFont2));
				hcell33.setBorder(Rectangle.NO_BORDER);
				hcell33.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell33.setPaddingLeft(40f);
				table13.addCell(hcell33);

				hcell33 = new PdfPCell(new Phrase("Exp Date", redFont2));
				hcell33.setBorder(Rectangle.NO_BORDER);
				hcell33.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell33.setPaddingLeft(50f);
				table13.addCell(hcell33);

				PdfPCell hcell34;
				hcell34 = new PdfPCell(new Phrase(paymentMode, redFont));
				hcell34.setBorder(Rectangle.NO_BORDER);
				hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell34.setPaddingLeft(10f);
				table13.addCell(hcell34);

				hcell34 = new PdfPCell(new Phrase(String.valueOf(Math.round(total)), redFont));
				hcell34.setBorder(Rectangle.NO_BORDER);
				hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell34.setPaddingLeft(35f);
				table13.addCell(hcell34);
				if (paymentMode.equalsIgnoreCase("card") || paymentMode.equalsIgnoreCase("cash+card")) {
					hcell34 = new PdfPCell(new Phrase(refNo, redFont));
					hcell34.setBorder(Rectangle.NO_BORDER);
					hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell34.setPaddingLeft(40f);
					table13.addCell(hcell34);
				} else {
					hcell34 = new PdfPCell(new Phrase("", redFont));
					hcell34.setBorder(Rectangle.NO_BORDER);
					hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell34.setPaddingLeft(40f);
					table13.addCell(hcell34);

				}
				hcell34 = new PdfPCell(new Phrase("", redFont));
				hcell34.setBorder(Rectangle.NO_BORDER);
				hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell34.setPaddingLeft(40f);
				table13.addCell(hcell34);

				hcell34 = new PdfPCell(new Phrase("", redFont));
				hcell34.setBorder(Rectangle.NO_BORDER);
				hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell34.setPaddingLeft(50f);
				table13.addCell(hcell34);

				// cell33.setFixedHeight(35f);
				cell33.setColspan(2);
				table13.setWidthPercentage(100f);
				cell33.addElement(table13);
				table.addCell(cell33);

				PdfPCell cell5 = new PdfPCell();

				PdfPTable table35 = new PdfPTable(2);
				table35.setWidths(new float[] { 5f, 4f });
				table35.setSpacingBefore(10);

				PdfPCell hcell12;
				hcell12 = new PdfPCell(new Phrase("Created By    : " + createdBy, redFont));
				hcell12.setBorder(Rectangle.NO_BORDER);
				// hcell3.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell12.setPaddingTop(10f);
				hcell12.setPaddingLeft(-50f);
				table35.addCell(hcell12);

				hcell12 = new PdfPCell(new Phrase("Created Dt   :   " + today, redFont));
				hcell12.setBorder(Rectangle.NO_BORDER);
				hcell12.setPaddingTop(10f);
				// hcell12.setPaddingRight(0f);
				hcell12.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table35.addCell(hcell12);

				PdfPCell hcell13;
				hcell13 = new PdfPCell(new Phrase("Printed By     : " + createdBy, redFont));
				hcell13.setBorder(Rectangle.NO_BORDER);
				hcell13.setPaddingLeft(-50f);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table35.addCell(hcell13);

				hcell13 = new PdfPCell(new Phrase("Print Dt       :   " + today, redFont));
				hcell13.setBorder(Rectangle.NO_BORDER);
				hcell13.setPaddingRight(3f);
				hcell13.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table35.addCell(hcell13);

				PdfPCell hcell23;
				hcell23 = new PdfPCell(new Phrase(""));
				hcell23.setBorder(Rectangle.NO_BORDER);
				// hcell23.setPaddingLeft(-50f);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
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

				pdfBytes = byteArrayOutputStream.toByteArray();
				String uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/payment/viewFile/")
						.path(paymentPdfServiceImpl.getNextPdfId()).toUriString();

				patientPaymentPdf = new PatientPaymentPdf(regId + " Lab Registration", uri, regId, pdfBytes, bill);
				patientPaymentPdf.setPid(paymentPdfServiceImpl.getNextPdfId());
				paymentPdfServiceImpl.save(patientPaymentPdf);

			} catch (Exception e) {
				Logger.error(e.getMessage());
			}

		} else if (patientRegistration.getpType().equals("INPATIENT")
				&& !laboratoryRegistration.getPaymentType().equalsIgnoreCase("Insurance")
				&& !laboratoryRegistration.getPaymentType().equalsIgnoreCase("Paid In KPHB")) {

			List<LaboratoryRegistration> laboratoryRegistrationInfor = laboratoryRegistrationServiceImpl
					.findBill(laboratoryRegistration.getReg_id(), laboratoryRegistration.getInvoiceNo());

			patientPaymentPdf = null;
			byte[] pdfBytes = null;
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			Document document = new Document(PageSize.A4_LANDSCAPE);

			try {

				Resource fileResource = resourceLoader.getResource(ConstantValues.IMAGE_PNG_CLASSPATH);
				Chunk cnd1 = new Chunk(new VerticalPositionMark());
				Font redFont1 = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
				Font redFont2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
				PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);
				Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
				Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
				Font headFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);

				document.open();
				PdfPTable table = new PdfPTable(2);

				Image img = Image.getInstance(hospitalLogo.getURL());
				img.scaleAbsolute(ConstantValues.IMAGE_ABSOLUTE_INTIAL_POSITION, ConstantValues.IMAGE_ABSOLUTE_FINAL_POSITION);
				table.setWidthPercentage(ConstantValues.TABLE_SET_WIDTH_PERECENTAGE);

				Phrase pq = new Phrase(new Chunk(img, ConstantValues.IMAGE_SET_INTIAL_POSITION, ConstantValues.IMAGE_SET_FINAL_POSITION));
				pq.add(new Chunk(ConstantValues.LAB_OSP_ADDRESS, redFont));
				PdfPCell cellp = new PdfPCell(pq);
				PdfPCell cell1 = new PdfPCell();

				// Display a date in day, month, year format
				Date dateInfo = Calendar.getInstance().getTime();
				DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
				String today = formatter.format(dateInfo).toString();

				// for header Bold
				PdfPTable table96 = new PdfPTable(1);
				table96.setWidths(new float[] { 5f });
				table96.setSpacingBefore(10);

				PdfPCell hcell96;
				hcell96 = new PdfPCell(new Phrase(ConstantValues.HOSPITAL_NAME, headFont1));
				hcell96.setBorder(Rectangle.NO_BORDER);
				hcell96.setHorizontalAlignment(Element.ALIGN_CENTER);
				hcell96.setPaddingLeft(50f);

				table96.addCell(hcell96);
				cell1.addElement(table96);

				// for header end

				cell1.addElement(pq);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(cell1);

				PdfPCell cell3 = new PdfPCell();

				PdfPTable table99 = new PdfPTable(3);
				table99.setWidths(new float[] { 3f, 1f, 4f });
				table99.setSpacingBefore(10);

				PdfPCell hcell90;
				hcell90 = new PdfPCell(new Phrase("Patient", redFont));
				hcell90.setBorder(Rectangle.NO_BORDER);
				hcell90.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell90.setPaddingBottom(-7f);
				hcell90.setPaddingLeft(-25f);

				table99.addCell(hcell90);

				// table.addCell(cell3);

				hcell90 = new PdfPCell(new Phrase(":", redFont));
				hcell90.setBorder(Rectangle.NO_BORDER);
				hcell90.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell90.setPaddingBottom(-7f);
				hcell90.setPaddingLeft(-64f);

				table99.addCell(hcell90);

				hcell90 = new PdfPCell(new Phrase(patientName, redFont));
				hcell90.setBorder(Rectangle.NO_BORDER);
				hcell90.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell90.setPaddingBottom(-7f);
				hcell90.setPaddingLeft(-88f);
				table99.addCell(hcell90);

				cell3.addElement(table99);

				PdfPTable table2 = new PdfPTable(6);
				table2.setWidths(new float[] { 3f, 1f, 5.5f, 3f, 1f, 4f });
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

				hcell1 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getAge() + "/"
						+ patientRegistration.getPatientDetails().getGender(), redFont));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-30f);
				table2.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase("UMR NO", redFont));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingRight(-40f);
				// hcell1.setPaddingTop(-5f);
				table2.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase(":", redFont));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				// hcell1.setPaddingTop(-5f);;
				table2.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getUmr(), redFont));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingRight(-30f);
				// hcell1.setPaddingTop(-5f);
				table2.addCell(hcell1);

				PdfPCell hcell4;
				hcell4 = new PdfPCell(new Phrase("Bill Dt", redFont));
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

				hcell4 = new PdfPCell(new Phrase("INV No", redFont));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell4.setPaddingRight(-27.5f);
				// hcell4.setPaddingLeft(25f);
				table2.addCell(hcell4);

				hcell4 = new PdfPCell(new Phrase(":", redFont));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setHorizontalAlignment(Element.ALIGN_RIGHT);
				// hcell1.setPaddingTop(-5f);;
				table2.addCell(hcell4);

				hcell4 = new PdfPCell(new Phrase(laboratoryRegistration.getInvoiceNo(), redFont));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell4.setPaddingRight(-27.5f);
				// hcell1.setPaddingTop(-5f);
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

				String refBy = null;
				if (patientRegistration.getPatientDetails().getvRefferalDetails() == null) {
					refBy = "";
				} else {
					refBy = patientRegistration.getPatientDetails().getvRefferalDetails().getRefName();
				}
				hcell15 = new PdfPCell(new Phrase(refBy, redFont));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(-30f);
				table2.addCell(hcell15);

				hcell15 = new PdfPCell(new Phrase("Phone", redFont));
				hcell15.setBorder(Rectangle.NO_BORDER);
				// hcell15.setPaddingRight(7f);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingRight(-27.5f);

				// hcell15.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table2.addCell(hcell15);

				hcell15 = new PdfPCell(new Phrase(":", redFont));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_RIGHT);
				// hcell1.setPaddingTop(-5f);;
				table2.addCell(hcell15);

				hcell15 = new PdfPCell(
						new Phrase(String.valueOf(patientRegistration.getPatientDetails().getMobile()), redFont));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingRight(-27.5f);
				// hcell1.setPaddingTop(-5f);
				table2.addCell(hcell15);

				PdfPCell hcell6;
				hcell6 = new PdfPCell(new Phrase("RegNo", redFont));
				hcell6.setBorder(Rectangle.NO_BORDER);
				hcell6.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell6.setPaddingLeft(-25f);
				table2.addCell(hcell6);

				hcell6 = new PdfPCell(new Phrase(":", redFont));
				hcell6.setBorder(Rectangle.NO_BORDER);
				hcell6.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell6.setPaddingLeft(-20f);
				table2.addCell(hcell6);

				hcell6 = new PdfPCell(new Phrase(patientRegistration.getRegId(), redFont));
				hcell6.setBorder(Rectangle.NO_BORDER);
				hcell6.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell6.setPaddingLeft(-30f);
				table2.addCell(hcell6);

				hcell6 = new PdfPCell(new Phrase("Bill No ", redFont));
				hcell6.setBorder(Rectangle.NO_BORDER);
				hcell6.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell6.setPaddingRight(-27.5f);
				// hcell4.setPaddingLeft(25f);
				table2.addCell(hcell6);

				hcell6 = new PdfPCell(new Phrase(":", redFont));
				hcell6.setBorder(Rectangle.NO_BORDER);
				hcell6.setHorizontalAlignment(Element.ALIGN_RIGHT);
				// hcell1.setPaddingTop(-5f);;
				table2.addCell(hcell6);

				hcell6 = new PdfPCell(new Phrase(laboratoryRegistration.getBillNo(), redFont));
				hcell6.setBorder(Rectangle.NO_BORDER);
				hcell6.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell6.setPaddingRight(-27.5f);
				// hcell1.setPaddingTop(-5f);
				table2.addCell(hcell6);

				cell3.setFixedHeight(115f);
				cell3.setColspan(2);
				cell3.addElement(table2);

				PdfPTable table98 = new PdfPTable(3);
				table98.setWidths(new float[] { 3f, 1f, 4f });
				table98.setSpacingBefore(10);

				PdfPCell hcell91;
				hcell91 = new PdfPCell(new Phrase("Consultant", redFont));
				hcell91.setBorder(Rectangle.NO_BORDER);
				hcell91.setHorizontalAlignment(Element.ALIGN_LEFT);
				// hcell91.setPaddingBottom(3f);
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

				/*
				 * PdfPTable table97 = new PdfPTable(1); table97.setWidths(new float[] { 5f });
				 * table97.setSpacingBefore(10);
				 * 
				 * PdfPCell hcell97; hcell97 = new PdfPCell(new Phrase( "*" + "OBN0094995" + "*"
				 * + "  " + "==> Scan This BarCode To Take Report At KIOSK", headFont1));
				 * hcell97.setBorder(Rectangle.NO_BORDER); //
				 * hcell3.setHorizontalAlignment(Element.ALIGN_LEFT);
				 * hcell97.setPaddingBottom(-10f); hcell97.setPaddingLeft(-35f);
				 * 
				 * table97.addCell(hcell97); cell3.addElement(table97);
				 */
				table.addCell(cell3);

				// *****************************

				PdfPCell cell19 = new PdfPCell();

				PdfPTable table21 = new PdfPTable(1);
				table21.setWidths(new float[] { 4f });
				table21.setSpacingBefore(10);

				PdfPCell hcell19;
				hcell19 = new PdfPCell(new Phrase("IP Bill Cum Reciept", headFont1));
				hcell19.setBorder(Rectangle.NO_BORDER);
				hcell19.setHorizontalAlignment(Element.ALIGN_CENTER);
				// hcell19.setPaddingLeft(-70f);
				table21.addCell(hcell19);

				cell19.setFixedHeight(20f);
				cell19.setColspan(2);
				cell19.addElement(table21);
				table.addCell(cell19);

				// **************
				PdfPCell cell31 = new PdfPCell();

				PdfPTable table1 = new PdfPTable(7);
				table1.setWidths(new float[] { 1f, 3f, 5f, 3f, 1f, 2f, 2f });

				table1.setSpacingBefore(10);

				PdfPCell hcell;
				hcell = new PdfPCell(new Phrase("S.No", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("Service Code", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("Service Name", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("Service Type", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("Qty", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("Rate", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table1.addCell(hcell);

				/*
				 * hcell = new PdfPCell(new Phrase("Discount", redFont));
				 * hcell.setBorder(Rectangle.NO_BORDER);
				 * hcell.setBackgroundColor(BaseColor.GRAY);
				 * hcell.setHorizontalAlignment(Element.ALIGN_CENTER); table1.addCell(hcell);
				 */
				hcell = new PdfPCell(new Phrase("Amount(RS)", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table1.addCell(hcell);

				int count = 0;
				float total = 0;
				for (LaboratoryRegistration laboratoryRegistrationInfo : laboratoryRegistrationInfor) {

					PdfPCell cell;

					cell = new PdfPCell(new Phrase(String.valueOf(count = count + 1), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table1.addCell(cell);

					cell = new PdfPCell(
							new Phrase(laboratoryRegistrationInfo.getLabServices().getServiceId(), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					// cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					table1.addCell(cell);

					cell = new PdfPCell(new Phrase(laboratoryRegistrationInfo.getServiceName(), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					// cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					table1.addCell(cell);

					cell = new PdfPCell(
							new Phrase(laboratoryRegistrationInfo.getLabServices().getServiceType(), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					// cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					table1.addCell(cell);

					cell = new PdfPCell(new Phrase(String.valueOf(laboratoryRegistrationInfo.getQuantity()), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table1.addCell(cell);

					cell = new PdfPCell(new Phrase(String.valueOf(laboratoryRegistrationInfo.getPrice()), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table1.addCell(cell);

					/*
					 * cell = new PdfPCell(new
					 * Phrase(String.valueOf(laboratoryRegistrationInfo.getDiscount()), redFont));
					 * cell.setBorder(Rectangle.NO_BORDER); cell.setPaddingLeft(5);
					 * cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					 * cell.setHorizontalAlignment(Element.ALIGN_CENTER); table1.addCell(cell);
					 */
					cell = new PdfPCell(new Phrase(String.valueOf(laboratoryRegistrationInfo.getNetAmount()), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table1.addCell(cell);

					total += laboratoryRegistrationInfo.getNetAmount();

				}
				total = Math.round(total);

				/*
				 * cell31.setColspan(2); table1.setWidthPercentage(100f);
				 * cell31.addElement(table1); //cell31.addElement(table37);
				 * table.addCell(cell31);
				 */
				// -------------------------------

				PdfPTable table37 = new PdfPTable(6);
				table37.setWidths(new float[] { 3f, 1f, 4f, 7f, 1f, 4f });
				table37.setSpacingBefore(10);

				PdfPCell cell55;
				
				if(finalCash!=0) {
				cell55 = new PdfPCell(new Phrase("Cash Amt", redFont));
				cell55.setBorder(Rectangle.NO_BORDER);
				cell55.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell55.setPaddingTop(10f);
				 cell55.setPaddingLeft(-50f);
				table37.addCell(cell55);

				cell55 = new PdfPCell(new Phrase(":", redFont));
				cell55.setBorder(Rectangle.NO_BORDER);
				cell55.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell55.setPaddingTop(10f);
				 cell55.setPaddingLeft(-50f);
				table37.addCell(cell55);

			
				cell55 = new PdfPCell(new Phrase(String.valueOf(finalCash), redFont));
				cell55.setBorder(Rectangle.NO_BORDER);
				cell55.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell55.setPaddingTop(10f);
				 cell55.setPaddingLeft(-40f);
				table37.addCell(cell55);
				}else
				{
					cell55 = new PdfPCell(new Phrase("", redFont));
					cell55.setBorder(Rectangle.NO_BORDER);
					cell55.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell55.setPaddingTop(10f);
					 cell55.setPaddingLeft(-50f);
					table37.addCell(cell55);

					cell55 = new PdfPCell(new Phrase("", redFont));
					cell55.setBorder(Rectangle.NO_BORDER);
					cell55.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell55.setPaddingTop(10f);
					 cell55.setPaddingLeft(-50f);
					table37.addCell(cell55);

				
					cell55 = new PdfPCell(new Phrase("", redFont));
					cell55.setBorder(Rectangle.NO_BORDER);
					cell55.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell55.setPaddingTop(10f);
					 cell55.setPaddingLeft(-40f);
					table37.addCell(cell55);
				}
				
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
				cell55.setPaddingRight(-30f);
				table37.addCell(cell55);
				
				PdfPCell cell551;
				cell551 = new PdfPCell(new Phrase("", redFont));
				cell551.setBorder(Rectangle.NO_BORDER);
				cell551.setHorizontalAlignment(Element.ALIGN_LEFT);
				// cell55.setPaddingLeft(-50f);
				table37.addCell(cell551);

				cell551 = new PdfPCell(new Phrase("", redFont));
				cell551.setBorder(Rectangle.NO_BORDER);
				cell551.setHorizontalAlignment(Element.ALIGN_LEFT);
				// cell55.setPaddingLeft(-50f);
				table37.addCell(cell551);

				cell551 = new PdfPCell(new Phrase("", redFont));
				cell551.setBorder(Rectangle.NO_BORDER);
				cell551.setHorizontalAlignment(Element.ALIGN_LEFT);
				// cell55.setPaddingLeft(-50f);
				table37.addCell(cell551);

				cell551 = new PdfPCell(new Phrase("Disc Amt", redFont));
				cell551.setBorder(Rectangle.NO_BORDER);
				cell551.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell551.setPaddingRight(-70f);
				table37.addCell(cell551);

				cell551 = new PdfPCell(new Phrase(":", redFont));
				cell551.setBorder(Rectangle.NO_BORDER);
				cell551.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell551.setPaddingRight(-60f);
				table37.addCell(cell551);

				cell551 = new PdfPCell(new Phrase(String.valueOf(laboratoryRegistration.getDiscount()), redFont));
				cell551.setBorder(Rectangle.NO_BORDER);
				cell551.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell551.setPaddingRight(-30f);
				table37.addCell(cell551);


				PdfPCell hcell56;
				if(finalCard!=0) {
				hcell56 = new PdfPCell(new Phrase("Card Amt", redFont));
				hcell56.setBorder(Rectangle.NO_BORDER);
				hcell56.setHorizontalAlignment(Element.ALIGN_LEFT);
				//hcell56.setPaddingTop(10f);
				hcell56.setPaddingLeft(-50f);
				table37.addCell(hcell56);

				hcell56 = new PdfPCell(new Phrase(":", redFont));
				hcell56.setBorder(Rectangle.NO_BORDER);
				hcell56.setHorizontalAlignment(Element.ALIGN_LEFT);
				//hcell56.setPaddingTop(10f);
				hcell56.setPaddingLeft(-50f);
				table37.addCell(hcell56);
	
			
				hcell56 = new PdfPCell(new Phrase(String.valueOf(finalCard), redFont));
				hcell56.setBorder(Rectangle.NO_BORDER);
				hcell56.setHorizontalAlignment(Element.ALIGN_LEFT);
				//hcell56.setPaddingTop(10f);
				hcell56.setPaddingLeft(-40f);
				table37.addCell(hcell56);
				}else {
					hcell56 = new PdfPCell(new Phrase("", redFont));
					hcell56.setBorder(Rectangle.NO_BORDER);
					hcell56.setHorizontalAlignment(Element.ALIGN_LEFT);
					//hcell56.setPaddingTop(10f);
					hcell56.setPaddingLeft(-50f);
					table37.addCell(hcell56);

					hcell56 = new PdfPCell(new Phrase("", redFont));
					hcell56.setBorder(Rectangle.NO_BORDER);
					hcell56.setHorizontalAlignment(Element.ALIGN_LEFT);
					//hcell56.setPaddingTop(10f);
					hcell56.setPaddingLeft(-50f);
					table37.addCell(hcell56);
		
				
					hcell56 = new PdfPCell(new Phrase("", redFont));
					hcell56.setBorder(Rectangle.NO_BORDER);
					hcell56.setHorizontalAlignment(Element.ALIGN_LEFT);
					//hcell56.setPaddingTop(10f);
					hcell56.setPaddingLeft(-40f);
					table37.addCell(hcell56);
				}
				hcell56 = new PdfPCell(new Phrase("Paid Amt.", redFont));
				hcell56.setBorder(Rectangle.NO_BORDER);
				hcell56.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell56.setPaddingRight(-70f);
				table37.addCell(hcell56);

				hcell56 = new PdfPCell(new Phrase(":", redFont));
				hcell56.setBorder(Rectangle.NO_BORDER);
				// hcell56.setPaddingLeft(-1f);
				hcell56.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell56.setPaddingRight(-60f);
				table37.addCell(hcell56);

				hcell56 = new PdfPCell(new Phrase(String.valueOf(total-laboratoryRegistration.getDiscount()), redFont));
				hcell56.setBorder(Rectangle.NO_BORDER);
				// hcell56.setPaddingLeft(-1f);
				hcell56.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell56.setPaddingRight(-30f);
				table37.addCell(hcell56);
				/*
				 * PdfPCell hcell57; if (finalCard!=0) { hcell57 = new PdfPCell(new
				 * Phrase("Card Amt" , redFont)); hcell57.setBorder(Rectangle.NO_BORDER);
				 * hcell57.setHorizontalAlignment(Element.ALIGN_LEFT);
				 * hcell57.setPaddingLeft(-50f); table37.addCell(hcell57);
				 * 
				 * hcell57 = new PdfPCell(new Phrase(":", redFont));
				 * hcell57.setBorder(Rectangle.NO_BORDER);
				 * hcell57.setHorizontalAlignment(Element.ALIGN_LEFT);
				 * hcell57.setPaddingLeft(-50f); table37.addCell(hcell57);
				 * 
				 * hcell57 = new PdfPCell(new Phrase(String.valueOf(finalCard), redFont));
				 * hcell57.setBorder(Rectangle.NO_BORDER);
				 * hcell57.setHorizontalAlignment(Element.ALIGN_LEFT);
				 * hcell57.setPaddingLeft(-40f); table37.addCell(hcell57); } else { hcell57 =
				 * new PdfPCell(new Phrase( " ", redFont));
				 * hcell57.setBorder(Rectangle.NO_BORDER);
				 * hcell57.setHorizontalAlignment(Element.ALIGN_LEFT);
				 * hcell57.setPaddingLeft(-50f); table37.addCell(hcell57);
				 * 
				 * hcell57 = new PdfPCell(new Phrase("", redFont));
				 * hcell57.setBorder(Rectangle.NO_BORDER);
				 * hcell57.setHorizontalAlignment(Element.ALIGN_LEFT);
				 * hcell57.setPaddingLeft(-50f); table37.addCell(hcell57);
				 * 
				 * hcell57 = new PdfPCell(new Phrase("", redFont));
				 * hcell57.setBorder(Rectangle.NO_BORDER);
				 * hcell57.setHorizontalAlignment(Element.ALIGN_LEFT);
				 * hcell57.setPaddingLeft(-40f); table37.addCell(hcell57);
				 * 
				 * }
				 * 
				 * hcell57 = new PdfPCell(new Phrase("Net Amt.", redFont));
				 * hcell57.setBorder(Rectangle.NO_BORDER);
				 * hcell57.setHorizontalAlignment(Element.ALIGN_RIGHT);
				 * hcell57.setPaddingRight(-70f); table37.addCell(hcell57);
				 * 
				 * hcell57 = new PdfPCell(new Phrase(":", redFont));
				 * hcell57.setBorder(Rectangle.NO_BORDER);
				 * hcell57.setHorizontalAlignment(Element.ALIGN_RIGHT);
				 * hcell57.setPaddingRight(-60f); table37.addCell(hcell57);
				 * 
				 * hcell57 = new PdfPCell(new Phrase(String.valueOf(total), redFont));
				 * hcell57.setBorder(Rectangle.NO_BORDER);
				 * hcell57.setHorizontalAlignment(Element.ALIGN_RIGHT);
				 * hcell57.setPaddingRight(-30f); table37.addCell(hcell57);
				 */
				PdfPCell hcell58;
            if(finalDue!=0) {
				hcell58 = new PdfPCell(new Phrase("Due Amt", redFont));
				hcell58.setBorder(Rectangle.NO_BORDER);
				hcell58.setPaddingLeft(-50f);
				table37.addCell(hcell58);

				hcell58 = new PdfPCell(new Phrase("", redFont));
				hcell58.setBorder(Rectangle.NO_BORDER);
				hcell58.setPaddingLeft(-50f);
				table37.addCell(hcell58);

				hcell58 = new PdfPCell(new Phrase(String.valueOf(finalDue), redFont));
				hcell58.setBorder(Rectangle.NO_BORDER);
				hcell58.setPaddingLeft(-40f);
				table37.addCell(hcell58);
}else {
	
	
	hcell58 = new PdfPCell(new Phrase("", redFont));
	hcell58.setBorder(Rectangle.NO_BORDER);
	hcell58.setPaddingLeft(-50f);
	table37.addCell(hcell58);

	hcell58 = new PdfPCell(new Phrase("", redFont));
	hcell58.setBorder(Rectangle.NO_BORDER);
	hcell58.setPaddingLeft(-50f);
	table37.addCell(hcell58);

	hcell58 = new PdfPCell(new Phrase("", redFont));
	hcell58.setBorder(Rectangle.NO_BORDER);
	hcell58.setPaddingLeft(-40f);
	table37.addCell(hcell58);
	
}
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

				hcell58 = new PdfPCell(new Phrase(String.valueOf(total-laboratoryRegistration.getDiscount()), redFont));
				hcell58.setBorder(Rectangle.NO_BORDER);
				hcell58.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell58.setPaddingRight(-30f);
				table37.addCell(hcell58);

				long numAmount=(long)(total-laboratoryRegistration.getDiscount());
				PdfPCell hcell59;
				hcell59 = new PdfPCell(new Phrase("Gross Amount In Words ", redFont));
				hcell59.setBorder(Rectangle.NO_BORDER);
				hcell59.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell59.setPaddingLeft(-50f);
				table37.addCell(hcell59);

				hcell59 = new PdfPCell(new Phrase("", headFont));
				hcell59.setBorder(Rectangle.NO_BORDER);
				// hcell57.setPaddingTop(18f);
				hcell59.setHorizontalAlignment(Element.ALIGN_LEFT);
				table37.addCell(hcell59);

				hcell59 = new PdfPCell(new Phrase("(" + numberToWordsConverter.convert((long) total) + ")", redFont));
				hcell59.setBorder(Rectangle.NO_BORDER);
				hcell59.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell59.setPaddingLeft(-35f);
				table37.addCell(hcell59);

				hcell59 = new PdfPCell(new Phrase("", headFont));
				hcell59.setBorder(Rectangle.NO_BORDER);
				// hcell57.setPaddingTop(18f);
				hcell59.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table37.addCell(hcell59);

				hcell59 = new PdfPCell(new Phrase("", headFont));
				hcell59.setBorder(Rectangle.NO_BORDER);
				// hcell57.setPaddingTop(18f);
				hcell59.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table37.addCell(hcell59);

				hcell59 = new PdfPCell(new Phrase("", headFont));
				hcell59.setBorder(Rectangle.NO_BORDER);
				// hcell57.setPaddingTop(18f);
				hcell59.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table37.addCell(hcell59);

				PdfPCell hcell60;
				hcell60 = new PdfPCell(new Phrase("Received Amount In Words ", redFont));

				hcell60.setBorder(Rectangle.NO_BORDER);
				hcell60.setPaddingLeft(-50f);
				hcell60.setHorizontalAlignment(Element.ALIGN_LEFT);
				table37.addCell(hcell60);

				hcell60 = new PdfPCell(new Phrase("", headFont));
				hcell60.setBorder(Rectangle.NO_BORDER);
				// hcell57.setPaddingTop(18f);
				hcell60.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table37.addCell(hcell60);

				hcell60 = new PdfPCell(new Phrase("(" + numberToWordsConverter.convert(numAmount) + ")", redFont));
				hcell60.setBorder(Rectangle.NO_BORDER);
				hcell60.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell60.setPaddingLeft(-20f);
				table37.addCell(hcell60);

				hcell60 = new PdfPCell(new Phrase("", headFont));
				hcell60.setBorder(Rectangle.NO_BORDER);
				// hcell57.setPaddingTop(18f);
				hcell60.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table37.addCell(hcell60);

				hcell60 = new PdfPCell(new Phrase("", headFont));
				hcell60.setBorder(Rectangle.NO_BORDER);
				// hcell57.setPaddingTop(18f);
				hcell60.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table37.addCell(hcell60);

				hcell60 = new PdfPCell(new Phrase("", headFont));
				hcell60.setBorder(Rectangle.NO_BORDER);
				// hcell57.setPaddingTop(18f);
				hcell60.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table37.addCell(hcell60);

				cell31.setColspan(2);
				// cell31.setFixedHeight(170f);
				table1.setWidthPercentage(100f);
				cell31.addElement(table1);
				cell31.addElement(table37);
				table.addCell(cell31);

				// -----------------------

				PdfPCell cell33 = new PdfPCell();

				PdfPTable table13 = new PdfPTable(5);
				table13.setWidths(new float[] { 2f, 3f, 3f, 3f, 3f });

				table13.setSpacingBefore(10);

				PdfPCell hcell33;
				hcell33 = new PdfPCell(new Phrase("Pay Mode", redFont2));
				hcell33.setBorder(Rectangle.NO_BORDER);
				hcell33.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell33.setPaddingLeft(10f);
				table13.addCell(hcell33);

				hcell33 = new PdfPCell(new Phrase("Amount", redFont2));
				hcell33.setBorder(Rectangle.NO_BORDER);
				hcell33.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell33.setPaddingLeft(35f);
				table13.addCell(hcell33);

				hcell33 = new PdfPCell(new Phrase("Card#", redFont2));
				hcell33.setBorder(Rectangle.NO_BORDER);
				hcell33.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell33.setPaddingLeft(40f);
				table13.addCell(hcell33);

				hcell33 = new PdfPCell(new Phrase("Bank Name", redFont2));
				hcell33.setBorder(Rectangle.NO_BORDER);
				hcell33.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell33.setPaddingLeft(40f);
				table13.addCell(hcell33);

				hcell33 = new PdfPCell(new Phrase("Exp Date", redFont2));
				hcell33.setBorder(Rectangle.NO_BORDER);
				hcell33.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell33.setPaddingLeft(50f);
				table13.addCell(hcell33);

				PdfPCell hcell34;
				hcell34 = new PdfPCell(new Phrase(paymentMode, redFont));
				hcell34.setBorder(Rectangle.NO_BORDER);
				hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell34.setPaddingLeft(10f);
				table13.addCell(hcell34);

				hcell34 = new PdfPCell(new Phrase(String.valueOf(Math.round(total)), redFont));
				hcell34.setBorder(Rectangle.NO_BORDER);
				hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell34.setPaddingLeft(35f);
				table13.addCell(hcell34);
				if (paymentMode.equalsIgnoreCase("card") || paymentMode.equalsIgnoreCase("cash+card")) {
					hcell34 = new PdfPCell(new Phrase(refNo, redFont));
					hcell34.setBorder(Rectangle.NO_BORDER);
					hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell34.setPaddingLeft(40f);
					table13.addCell(hcell34);
				} else {
					hcell34 = new PdfPCell(new Phrase("", redFont));
					hcell34.setBorder(Rectangle.NO_BORDER);
					hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell34.setPaddingLeft(40f);
					table13.addCell(hcell34);

				}
				hcell34 = new PdfPCell(new Phrase("", redFont));
				hcell34.setBorder(Rectangle.NO_BORDER);
				hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell34.setPaddingLeft(40f);
				table13.addCell(hcell34);

				hcell34 = new PdfPCell(new Phrase("", redFont));
				hcell34.setBorder(Rectangle.NO_BORDER);
				hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell34.setPaddingLeft(50f);
				table13.addCell(hcell34);

				// cell33.setFixedHeight(35f);
				cell33.setColspan(2);
				table13.setWidthPercentage(100f);
				cell33.addElement(table13);
				table.addCell(cell33);

				PdfPCell cell5 = new PdfPCell();

				PdfPTable table35 = new PdfPTable(2);
				table35.setWidths(new float[] { 5f, 4f });
				table35.setSpacingBefore(10);

				PdfPCell hcell12;
				hcell12 = new PdfPCell(new Phrase("Created By    : " + createdBy, redFont));
				hcell12.setBorder(Rectangle.NO_BORDER);
				// hcell3.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell12.setPaddingTop(10f);
				hcell12.setPaddingLeft(-50f);
				table35.addCell(hcell12);

				hcell12 = new PdfPCell(new Phrase("Created Dt   :   " + today, redFont));
				hcell12.setBorder(Rectangle.NO_BORDER);
				hcell12.setPaddingTop(10f);
				// hcell12.setPaddingRight(0f);
				hcell12.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table35.addCell(hcell12);

				PdfPCell hcell13;
				hcell13 = new PdfPCell(new Phrase("Printed By     : " + createdBy, redFont));
				hcell13.setBorder(Rectangle.NO_BORDER);
				hcell13.setPaddingLeft(-50f);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table35.addCell(hcell13);

				hcell13 = new PdfPCell(new Phrase("Print Dt       :   " + today, redFont));
				hcell13.setBorder(Rectangle.NO_BORDER);
				hcell13.setPaddingRight(3f);
				hcell13.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table35.addCell(hcell13);

				PdfPCell hcell23;
				hcell23 = new PdfPCell(new Phrase(""));
				hcell23.setBorder(Rectangle.NO_BORDER);
				// hcell23.setPaddingLeft(-50f);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
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

				pdfBytes = byteArrayOutputStream.toByteArray();
				String uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/payment/viewFile/")
						.path(paymentPdfServiceImpl.getNextPdfId()).toUriString();

				patientPaymentPdf = new PatientPaymentPdf(regId + " Lab Registration", uri, regId, pdfBytes, bill);
				patientPaymentPdf.setPid(paymentPdfServiceImpl.getNextPdfId());
				paymentPdfServiceImpl.save(patientPaymentPdf);

			} catch (Exception e) {
				Logger.error(e.getMessage());
			}

		} else if (laboratoryRegistration.getPaymentType().equalsIgnoreCase("Insurance")
				|| laboratoryRegistration.getPaymentType().equalsIgnoreCase("Paid In KPHB")) {

			List<LaboratoryRegistration> laboratoryRegistrationInfor = laboratoryRegistrationServiceImpl
					.findBill(laboratoryRegistration.getReg_id(), laboratoryRegistration.getInvoiceNo());

			patientPaymentPdf = null;
			byte[] pdfBytes = null;
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			Document document = new Document(PageSize.A4_LANDSCAPE);

			try {

				Resource fileResource = resourceLoader.getResource(ConstantValues.IMAGE_PNG_CLASSPATH);
				Chunk cnd1 = new Chunk(new VerticalPositionMark());
				Font redFont1 = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
				PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);
				Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
				Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
				Font headFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);

				document.open();
				PdfPTable table = new PdfPTable(2);

				Image img = Image.getInstance(hospitalLogo.getURL());
				img.scaleAbsolute(ConstantValues.IMAGE_ABSOLUTE_INTIAL_POSITION, ConstantValues.IMAGE_ABSOLUTE_FINAL_POSITION);
				table.setWidthPercentage(ConstantValues.TABLE_SET_WIDTH_PERECENTAGE);

				Phrase pq = new Phrase(new Chunk(img, ConstantValues.IMAGE_SET_INTIAL_POSITION, ConstantValues.IMAGE_SET_FINAL_POSITION));
				pq.add(new Chunk(ConstantValues.LAB_OSP_ADDRESS, redFont));
				PdfPCell cellp = new PdfPCell(pq);
				PdfPCell cell1 = new PdfPCell();

				// Display a date in day, month, year format
				Date dateInfo = Calendar.getInstance().getTime();
				DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
				String today = formatter.format(dateInfo).toString();

				// for header Bold
				PdfPTable table96 = new PdfPTable(1);
				table96.setWidths(new float[] { 5f });
				table96.setSpacingBefore(10);

				PdfPCell hcell96;
				hcell96 = new PdfPCell(new Phrase(ConstantValues.HOSPITAL_NAME, headFont1));
				hcell96.setBorder(Rectangle.NO_BORDER);
				hcell96.setHorizontalAlignment(Element.ALIGN_CENTER);
				hcell96.setPaddingLeft(50f);

				table96.addCell(hcell96);
				cell1.addElement(table96);

				// for header end

				cell1.addElement(pq);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(cell1);

				PdfPCell cell3 = new PdfPCell();

				PdfPTable table99 = new PdfPTable(3);
				table99.setWidths(new float[] { 3f, 1f, 4f });
				table99.setSpacingBefore(10);

				PdfPCell hcell90;
				hcell90 = new PdfPCell(new Phrase("Patient", redFont));
				hcell90.setBorder(Rectangle.NO_BORDER);
				hcell90.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell90.setPaddingBottom(-7f);
				hcell90.setPaddingLeft(-25f);

				table99.addCell(hcell90);

				// table.addCell(cell3);

				hcell90 = new PdfPCell(new Phrase(":", redFont));
				hcell90.setBorder(Rectangle.NO_BORDER);
				hcell90.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell90.setPaddingBottom(-7f);
				hcell90.setPaddingLeft(-64f);

				table99.addCell(hcell90);

				hcell90 = new PdfPCell(new Phrase(patientName, redFont));
				hcell90.setBorder(Rectangle.NO_BORDER);
				hcell90.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell90.setPaddingBottom(-7f);
				hcell90.setPaddingLeft(-88f);
				table99.addCell(hcell90);

				cell3.addElement(table99);

				PdfPTable table2 = new PdfPTable(6);
				table2.setWidths(new float[] { 3f, 1f, 5.5f, 3f, 1f, 4f });
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

				hcell1 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getAge() + "/"
						+ patientRegistration.getPatientDetails().getGender(), redFont));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-30f);
				table2.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase("UMR NO", redFont));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingRight(-40f);
				// hcell1.setPaddingTop(-5f);
				table2.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase(":", redFont));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				// hcell1.setPaddingTop(-5f);;
				table2.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getUmr(), redFont));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingRight(-30f);
				// hcell1.setPaddingTop(-5f);
				table2.addCell(hcell1);

				PdfPCell hcell4;
				hcell4 = new PdfPCell(new Phrase("Bill Dt", redFont));
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

				hcell4 = new PdfPCell(new Phrase("INV No", redFont));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell4.setPaddingRight(-27.5f);
				// hcell4.setPaddingLeft(25f);
				table2.addCell(hcell4);

				hcell4 = new PdfPCell(new Phrase(":", redFont));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setHorizontalAlignment(Element.ALIGN_RIGHT);
				// hcell1.setPaddingTop(-5f);;
				table2.addCell(hcell4);

				hcell4 = new PdfPCell(new Phrase(laboratoryRegistration.getInvoiceNo(), redFont));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell4.setPaddingRight(-27.5f);
				// hcell1.setPaddingTop(-5f);
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

				String refBy = null;
				if (patientRegistration.getPatientDetails().getvRefferalDetails() == null) {
					refBy = "";
				} else {
					refBy = patientRegistration.getPatientDetails().getvRefferalDetails().getRefName();
				}
				hcell15 = new PdfPCell(new Phrase(refBy, redFont));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(-30f);
				table2.addCell(hcell15);

				hcell15 = new PdfPCell(new Phrase("Phone", redFont));
				hcell15.setBorder(Rectangle.NO_BORDER);
				// hcell15.setPaddingRight(7f);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingRight(-27.5f);

				// hcell15.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table2.addCell(hcell15);

				hcell15 = new PdfPCell(new Phrase(":", redFont));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_RIGHT);
				// hcell1.setPaddingTop(-5f);;
				table2.addCell(hcell15);

				hcell15 = new PdfPCell(
						new Phrase(String.valueOf(patientRegistration.getPatientDetails().getMobile()), redFont));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingRight(-27.5f);
				// hcell1.setPaddingTop(-5f);
				table2.addCell(hcell15);

				PdfPCell hcell6;
				hcell6 = new PdfPCell(new Phrase("RegNo", redFont));
				hcell6.setBorder(Rectangle.NO_BORDER);
				hcell6.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell6.setPaddingLeft(-25f);
				table2.addCell(hcell6);

				hcell6 = new PdfPCell(new Phrase(":", redFont));
				hcell6.setBorder(Rectangle.NO_BORDER);
				hcell6.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell6.setPaddingLeft(-20f);
				table2.addCell(hcell6);

				hcell6 = new PdfPCell(new Phrase(patientRegistration.getRegId(), redFont));
				hcell6.setBorder(Rectangle.NO_BORDER);
				hcell6.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell6.setPaddingLeft(-30f);
				table2.addCell(hcell6);

				hcell6 = new PdfPCell(new Phrase(" ", redFont));
				hcell6.setBorder(Rectangle.NO_BORDER);
				hcell6.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell6.setPaddingRight(-27.5f);
				// hcell4.setPaddingLeft(25f);
				table2.addCell(hcell6);

				hcell6 = new PdfPCell(new Phrase("", redFont));
				hcell6.setBorder(Rectangle.NO_BORDER);
				hcell6.setHorizontalAlignment(Element.ALIGN_RIGHT);
				// hcell1.setPaddingTop(-5f);;
				table2.addCell(hcell6);

				hcell6 = new PdfPCell(new Phrase("", redFont));
				hcell6.setBorder(Rectangle.NO_BORDER);
				hcell6.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell6.setPaddingRight(-27.5f);
				// hcell1.setPaddingTop(-5f);
				table2.addCell(hcell6);

				cell3.setFixedHeight(115f);
				cell3.setColspan(2);
				cell3.addElement(table2);

				PdfPTable table98 = new PdfPTable(3);
				table98.setWidths(new float[] { 3f, 1f, 4f });
				table98.setSpacingBefore(10);

				PdfPCell hcell91;
				hcell91 = new PdfPCell(new Phrase("Consultant", redFont));
				hcell91.setBorder(Rectangle.NO_BORDER);
				hcell91.setHorizontalAlignment(Element.ALIGN_LEFT);
				// hcell91.setPaddingBottom(3f);
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

				/*
				 * PdfPTable table97 = new PdfPTable(1); table97.setWidths(new float[] { 5f });
				 * table97.setSpacingBefore(10); PdfPCell hcell97; hcell97 = new PdfPCell(new
				 * Phrase( "*" + "OBN0094995" + "*" + "  " +
				 * "==> Scan This BarCode To Take Report At KIOSK", headFont1));
				 * hcell97.setBorder(Rectangle.NO_BORDER); //
				 * hcell3.setHorizontalAlignment(Element.ALIGN_LEFT);
				 * hcell97.setPaddingBottom(-10f); hcell97.setPaddingLeft(-35f);
				 * table97.addCell(hcell97); cell3.addElement(table97);
				 */
				table.addCell(cell3);

				// *****************************

				PdfPCell cell19 = new PdfPCell();

				PdfPTable table21 = new PdfPTable(1);
				table21.setWidths(new float[] { 4f });
				table21.setSpacingBefore(10);

				PdfPCell hcell19;

				hcell19 = new PdfPCell(new Phrase("IP/OP Receipt", headFont1));
				hcell19.setBorder(Rectangle.NO_BORDER);
				hcell19.setHorizontalAlignment(Element.ALIGN_CENTER);
				// hcell19.setPaddingLeft(-70f);
				table21.addCell(hcell19);

				cell19.setFixedHeight(20f);
				cell19.setColspan(2);
				cell19.addElement(table21);
				table.addCell(cell19);

				// **************
				PdfPCell cell31 = new PdfPCell();

				PdfPTable table1 = new PdfPTable(7);
				table1.setWidths(new float[] { 1f, 3f, 5f, 3f, 1f, 2f, 2f });

				table1.setSpacingBefore(10);

				PdfPCell hcell;
				hcell = new PdfPCell(new Phrase("S.No", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("Service Code", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("Service Name", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("Service Type", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("Qty", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("Rate", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table1.addCell(hcell);

				/*
				 * hcell = new PdfPCell(new Phrase("Discount", redFont));
				 * hcell.setBorder(Rectangle.NO_BORDER);
				 * hcell.setBackgroundColor(BaseColor.GRAY);
				 * hcell.setHorizontalAlignment(Element.ALIGN_CENTER); table1.addCell(hcell);
				 */
				hcell = new PdfPCell(new Phrase("Amount(RS)", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table1.addCell(hcell);

				int count = 0;
				float total = 0;
				for (LaboratoryRegistration laboratoryRegistrationInfo : laboratoryRegistrationInfor) {

					PdfPCell cell;

					cell = new PdfPCell(new Phrase(String.valueOf(count = count + 1), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table1.addCell(cell);

					cell = new PdfPCell(
							new Phrase(laboratoryRegistrationInfo.getLabServices().getServiceId(), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					// cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					table1.addCell(cell);

					cell = new PdfPCell(new Phrase(laboratoryRegistrationInfo.getServiceName(), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					// cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					table1.addCell(cell);

					cell = new PdfPCell(
							new Phrase(laboratoryRegistrationInfo.getLabServices().getServiceType(), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					// cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					table1.addCell(cell);

					cell = new PdfPCell(new Phrase(String.valueOf(laboratoryRegistrationInfo.getQuantity()), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table1.addCell(cell);

					cell = new PdfPCell(new Phrase(String.valueOf(laboratoryRegistrationInfo.getPrice()), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table1.addCell(cell);

					/*
					 * cell = new PdfPCell(new
					 * Phrase(String.valueOf(laboratoryRegistrationInfo.getDiscount()), redFont));
					 * cell.setBorder(Rectangle.NO_BORDER); cell.setPaddingLeft(5);
					 * cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					 * cell.setHorizontalAlignment(Element.ALIGN_CENTER); table1.addCell(cell);
					 */
					cell = new PdfPCell(new Phrase(String.valueOf(laboratoryRegistrationInfo.getNetAmount()), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table1.addCell(cell);

					total += laboratoryRegistrationInfo.getNetAmount();

				}
				total = Math.round(total);

				/*
				 * cell31.setColspan(2); table1.setWidthPercentage(100f);
				 * cell31.addElement(table1); //cell31.addElement(table37);
				 * table.addCell(cell31);
				 */
				// -------------------------------

				PdfPTable table37 = new PdfPTable(6);
				table37.setWidths(new float[] { 3f, 1f, 4f, 7f, 1f, 4f });
				table37.setSpacingBefore(10);

				PdfPCell cell55;
				cell55 = new PdfPCell(new Phrase("", redFont));
				cell55.setBorder(Rectangle.NO_BORDER);
				cell55.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell55.setPaddingTop(10f);
				// cell55.setPaddingLeft(-50f);
				table37.addCell(cell55);

				cell55 = new PdfPCell(new Phrase("", redFont));
				cell55.setBorder(Rectangle.NO_BORDER);
				cell55.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell55.setPaddingTop(10f);
				// cell55.setPaddingLeft(-50f);
				table37.addCell(cell55);

				cell55 = new PdfPCell(new Phrase("", redFont));
				cell55.setBorder(Rectangle.NO_BORDER);
				cell55.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell55.setPaddingTop(10f);
				// cell55.setPaddingLeft(-50f);
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
				cell55.setPaddingRight(-30f);
				table37.addCell(cell55);

				PdfPCell cell551;
				cell551 = new PdfPCell(new Phrase("", redFont));
				cell551.setBorder(Rectangle.NO_BORDER);
				cell551.setHorizontalAlignment(Element.ALIGN_LEFT);
				// cell55.setPaddingLeft(-50f);
				table37.addCell(cell551);

				cell551 = new PdfPCell(new Phrase("", redFont));
				cell551.setBorder(Rectangle.NO_BORDER);
				cell551.setHorizontalAlignment(Element.ALIGN_LEFT);
				// cell55.setPaddingLeft(-50f);
				table37.addCell(cell551);

				cell551 = new PdfPCell(new Phrase("", redFont));
				cell551.setBorder(Rectangle.NO_BORDER);
				cell551.setHorizontalAlignment(Element.ALIGN_LEFT);
				// cell55.setPaddingLeft(-50f);
				table37.addCell(cell551);

				cell551 = new PdfPCell(new Phrase("Disc Amt", redFont));
				cell551.setBorder(Rectangle.NO_BORDER);
				cell551.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell551.setPaddingRight(-70f);
				table37.addCell(cell551);

				cell551 = new PdfPCell(new Phrase(":", redFont));
				cell551.setBorder(Rectangle.NO_BORDER);
				cell551.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell551.setPaddingRight(-60f);
				table37.addCell(cell551);

				cell551 = new PdfPCell(new Phrase(String.valueOf(laboratoryRegistration.getDiscount()), redFont));
				cell551.setBorder(Rectangle.NO_BORDER);
				cell551.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell551.setPaddingRight(-30f);
				table37.addCell(cell551);

				
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
				// hcell56.setPaddingLeft(-1f);
				hcell56.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell56.setPaddingRight(-60f);
				table37.addCell(hcell56);

				hcell56 = new PdfPCell(new Phrase(String.valueOf(total-laboratoryRegistration.getDiscount()), redFont));
				hcell56.setBorder(Rectangle.NO_BORDER);
				// hcell56.setPaddingLeft(-1f);
				hcell56.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell56.setPaddingRight(-30f);
				table37.addCell(hcell56);

				PdfPCell hcell57;
				hcell57 = new PdfPCell(new Phrase(paymentMode + " Amt.", redFont));
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
				hcell57.setPaddingLeft(-40f);
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

				hcell57 = new PdfPCell(new Phrase(String.valueOf(total-laboratoryRegistration.getDiscount()), redFont));
				hcell57.setBorder(Rectangle.NO_BORDER);
				hcell57.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell57.setPaddingRight(-30f);
				table37.addCell(hcell57);

				PdfPCell hcell58;
				hcell58 = new PdfPCell(new Phrase(""));
				hcell58.setBorder(Rectangle.NO_BORDER);
				// hcell23.setPaddingLeft(-50f);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table37.addCell(hcell58);

				hcell58 = new PdfPCell(new Phrase(""));
				hcell58.setBorder(Rectangle.NO_BORDER);
				// hcell23.setPaddingLeft(-50f);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table37.addCell(hcell58);

				hcell58 = new PdfPCell(new Phrase(""));
				hcell58.setBorder(Rectangle.NO_BORDER);
				// hcell23.setPaddingLeft(-50f);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
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

				hcell58 = new PdfPCell(new Phrase(String.valueOf(total-laboratoryRegistration.getDiscount()), redFont));
				hcell58.setBorder(Rectangle.NO_BORDER);
				hcell58.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell58.setPaddingRight(-30f);
				table37.addCell(hcell58);
				
				long numAmount=(long)(total-laboratoryRegistration.getDiscount());

				PdfPCell hcell59;
				hcell59 = new PdfPCell(new Phrase("Gross Amount In Words ", redFont));
				hcell59.setBorder(Rectangle.NO_BORDER);
				hcell59.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell59.setPaddingLeft(-50f);
				table37.addCell(hcell59);

				hcell59 = new PdfPCell(new Phrase("", headFont));
				hcell59.setBorder(Rectangle.NO_BORDER);
				// hcell57.setPaddingTop(18f);
				hcell59.setHorizontalAlignment(Element.ALIGN_LEFT);
				table37.addCell(hcell59);

				hcell59 = new PdfPCell(new Phrase("(" + numberToWordsConverter.convert((long) total) + ")", redFont));
				hcell59.setBorder(Rectangle.NO_BORDER);
				hcell59.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell59.setPaddingLeft(-35f);
				table37.addCell(hcell59);

				hcell59 = new PdfPCell(new Phrase("", headFont));
				hcell59.setBorder(Rectangle.NO_BORDER);
				// hcell57.setPaddingTop(18f);
				hcell59.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table37.addCell(hcell59);

				hcell59 = new PdfPCell(new Phrase("", headFont));
				hcell59.setBorder(Rectangle.NO_BORDER);
				// hcell57.setPaddingTop(18f);
				hcell59.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table37.addCell(hcell59);

				hcell59 = new PdfPCell(new Phrase("", headFont));
				hcell59.setBorder(Rectangle.NO_BORDER);
				// hcell57.setPaddingTop(18f);
				hcell59.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table37.addCell(hcell59);

				PdfPCell hcell60;
				hcell60 = new PdfPCell(new Phrase("Received Amount In Words ", redFont));

				hcell60.setBorder(Rectangle.NO_BORDER);
				hcell60.setPaddingLeft(-50f);
				hcell60.setHorizontalAlignment(Element.ALIGN_LEFT);
				table37.addCell(hcell60);

				hcell60 = new PdfPCell(new Phrase("", headFont));
				hcell60.setBorder(Rectangle.NO_BORDER);
				// hcell57.setPaddingTop(18f);
				hcell60.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table37.addCell(hcell60);

				hcell60 = new PdfPCell(new Phrase("(" + numberToWordsConverter.convert(numAmount) + ")", redFont));
				hcell60.setBorder(Rectangle.NO_BORDER);
				hcell60.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell60.setPaddingLeft(-20f);
				table37.addCell(hcell60);

				hcell60 = new PdfPCell(new Phrase("", headFont));
				hcell60.setBorder(Rectangle.NO_BORDER);
				// hcell57.setPaddingTop(18f);
				hcell60.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table37.addCell(hcell60);

				hcell60 = new PdfPCell(new Phrase("", headFont));
				hcell60.setBorder(Rectangle.NO_BORDER);
				// hcell57.setPaddingTop(18f);
				hcell60.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table37.addCell(hcell60);

				hcell60 = new PdfPCell(new Phrase("", headFont));
				hcell60.setBorder(Rectangle.NO_BORDER);
				// hcell57.setPaddingTop(18f);
				hcell60.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table37.addCell(hcell60);

				cell31.setColspan(2);
				// cell31.setFixedHeight(170f);
				table1.setWidthPercentage(100f);
				cell31.addElement(table1);
				cell31.addElement(table37);
				table.addCell(cell31);

				// -----------------------

				PdfPCell cell5 = new PdfPCell();

				PdfPTable table35 = new PdfPTable(2);
				table35.setWidths(new float[] { 5f, 4f });
				table35.setSpacingBefore(10);

				PdfPCell hcell12;
				hcell12 = new PdfPCell(new Phrase("Created By    : " + createdBy, redFont));
				hcell12.setBorder(Rectangle.NO_BORDER);
				// hcell3.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell12.setPaddingTop(10f);
				hcell12.setPaddingLeft(-50f);
				table35.addCell(hcell12);

				hcell12 = new PdfPCell(new Phrase("Created Dt   :   " + today, redFont));
				hcell12.setBorder(Rectangle.NO_BORDER);
				hcell12.setPaddingTop(10f);
				// hcell12.setPaddingRight(0f);
				hcell12.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table35.addCell(hcell12);

				PdfPCell hcell13;
				hcell13 = new PdfPCell(new Phrase("Printed By     : " + createdBy, redFont));
				hcell13.setBorder(Rectangle.NO_BORDER);
				hcell13.setPaddingLeft(-50f);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table35.addCell(hcell13);

				hcell13 = new PdfPCell(new Phrase("Print Dt       :   " + today, redFont));
				hcell13.setBorder(Rectangle.NO_BORDER);
				hcell13.setPaddingRight(3f);
				hcell13.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table35.addCell(hcell13);

				PdfPCell hcell23;
				hcell23 = new PdfPCell(new Phrase(""));
				hcell23.setBorder(Rectangle.NO_BORDER);
				// hcell23.setPaddingLeft(-50f);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
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

				pdfBytes = byteArrayOutputStream.toByteArray();
				String uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/payment/viewFile/")
						.path(paymentPdfServiceImpl.getNextPdfId()).toUriString();

				patientPaymentPdf = new PatientPaymentPdf(regId + " Lab Registration", uri, regId, pdfBytes, bill);
				patientPaymentPdf.setPid(paymentPdfServiceImpl.getNextPdfId());
				paymentPdfServiceImpl.save(patientPaymentPdf);

			} catch (Exception e) {
				Logger.error(e.getMessage());
			}

		} else {

			List<LaboratoryRegistration> laboratoryRegistrationInfor = laboratoryRegistrationServiceImpl
					.findBill(laboratoryRegistration.getReg_id(), laboratoryRegistration.getInvoiceNo());

			patientPaymentPdf = null;
			byte[] pdfBytes = null;
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			Document document = new Document(PageSize.A4_LANDSCAPE);

			try {

				Resource fileResource = resourceLoader.getResource(ConstantValues.IMAGE_PNG_CLASSPATH);
				Chunk cnd1 = new Chunk(new VerticalPositionMark());
				Font redFont1 = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
				Font redFont2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
				PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);
				Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
				Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
				Font headFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);

				document.open();
				PdfPTable table = new PdfPTable(2);

				Image img = Image.getInstance(hospitalLogo.getURL());
				img.scaleAbsolute(ConstantValues.IMAGE_ABSOLUTE_INTIAL_POSITION, ConstantValues.IMAGE_ABSOLUTE_FINAL_POSITION);
				table.setWidthPercentage(ConstantValues.TABLE_SET_WIDTH_PERECENTAGE);

				Phrase pq = new Phrase(new Chunk(img, ConstantValues.IMAGE_SET_INTIAL_POSITION, ConstantValues.IMAGE_SET_FINAL_POSITION));
				pq.add(new Chunk(ConstantValues.LAB_OSP_ADDRESS, redFont));
				PdfPCell cellp = new PdfPCell(pq);
				PdfPCell cell1 = new PdfPCell();

				// Display a date in day, month, year format
				Date dateInfo = Calendar.getInstance().getTime();
				DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
				String today = formatter.format(dateInfo).toString();

				// for header Bold
				PdfPTable table96 = new PdfPTable(1);
				table96.setWidths(new float[] { 5f });
				table96.setSpacingBefore(10);

				PdfPCell hcell96;
				hcell96 = new PdfPCell(new Phrase(ConstantValues.HOSPITAL_NAME, headFont1));
				hcell96.setBorder(Rectangle.NO_BORDER);
				hcell96.setHorizontalAlignment(Element.ALIGN_CENTER);
				hcell96.setPaddingLeft(50f);

				table96.addCell(hcell96);
				cell1.addElement(table96);

				// for header end

				cell1.addElement(pq);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(cell1);

				PdfPCell cell3 = new PdfPCell();

				PdfPTable table99 = new PdfPTable(3);
				table99.setWidths(new float[] { 3f, 1f, 4f });
				table99.setSpacingBefore(10);

				PdfPCell hcell90;
				hcell90 = new PdfPCell(new Phrase("Patient", redFont));
				hcell90.setBorder(Rectangle.NO_BORDER);
				hcell90.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell90.setPaddingBottom(-7f);
				hcell90.setPaddingLeft(-25f);

				table99.addCell(hcell90);

				// table.addCell(cell3);

				hcell90 = new PdfPCell(new Phrase(":", redFont));
				hcell90.setBorder(Rectangle.NO_BORDER);
				hcell90.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell90.setPaddingBottom(-7f);
				hcell90.setPaddingLeft(-64f);

				table99.addCell(hcell90);

				hcell90 = new PdfPCell(new Phrase(patientName, redFont));
				hcell90.setBorder(Rectangle.NO_BORDER);
				hcell90.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell90.setPaddingBottom(-7f);
				hcell90.setPaddingLeft(-88f);
				table99.addCell(hcell90);

				cell3.addElement(table99);

				PdfPTable table2 = new PdfPTable(6);
				table2.setWidths(new float[] { 3f, 1f, 5.5f, 3f, 1f, 4f });
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

				hcell1 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getAge() + "/"
						+ patientRegistration.getPatientDetails().getGender(), redFont));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-30f);
				table2.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase("UMR NO", redFont));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingRight(-40f);
				// hcell1.setPaddingTop(-5f);
				table2.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase(":", redFont));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				// hcell1.setPaddingTop(-5f);;
				table2.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getUmr(), redFont));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingRight(-30f);
				// hcell1.setPaddingTop(-5f);
				table2.addCell(hcell1);

				PdfPCell hcell4;
				hcell4 = new PdfPCell(new Phrase("Bill Dt", redFont));
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

				hcell4 = new PdfPCell(new Phrase("INV No", redFont));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell4.setPaddingRight(-27.5f);
				// hcell4.setPaddingLeft(25f);
				table2.addCell(hcell4);

				hcell4 = new PdfPCell(new Phrase(":", redFont));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setHorizontalAlignment(Element.ALIGN_RIGHT);
				// hcell1.setPaddingTop(-5f);;
				table2.addCell(hcell4);

				hcell4 = new PdfPCell(new Phrase(laboratoryRegistration.getInvoiceNo(), redFont));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell4.setPaddingRight(-27.5f);
				// hcell1.setPaddingTop(-5f);
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

				String refBy = null;
				if (patientRegistration.getPatientDetails().getvRefferalDetails() == null) {
					refBy = "";
				} else {
					refBy = patientRegistration.getPatientDetails().getvRefferalDetails().getRefName();
				}
				hcell15 = new PdfPCell(new Phrase(refBy, redFont));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(-30f);
				table2.addCell(hcell15);

				hcell15 = new PdfPCell(new Phrase("Phone", redFont));
				hcell15.setBorder(Rectangle.NO_BORDER);
				// hcell15.setPaddingRight(7f);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingRight(-27.5f);

				// hcell15.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table2.addCell(hcell15);

				hcell15 = new PdfPCell(new Phrase(":", redFont));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_RIGHT);
				// hcell1.setPaddingTop(-5f);;
				table2.addCell(hcell15);

				hcell15 = new PdfPCell(
						new Phrase(String.valueOf(patientRegistration.getPatientDetails().getMobile()), redFont));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingRight(-27.5f);
				// hcell1.setPaddingTop(-5f);
				table2.addCell(hcell15);
				PdfPCell hcell6;
				hcell6 = new PdfPCell(new Phrase("RegNo", redFont));
				hcell6.setBorder(Rectangle.NO_BORDER);
				hcell6.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell6.setPaddingLeft(-25f);
				table2.addCell(hcell6);

				hcell6 = new PdfPCell(new Phrase(":", redFont));
				hcell6.setBorder(Rectangle.NO_BORDER);
				hcell6.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell6.setPaddingLeft(-20f);
				table2.addCell(hcell6);

				hcell6 = new PdfPCell(new Phrase(patientRegistration.getRegId(), redFont));
				hcell6.setBorder(Rectangle.NO_BORDER);
				hcell6.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell6.setPaddingLeft(-30f);
				table2.addCell(hcell6);

				hcell6 = new PdfPCell(new Phrase("Bill No ", redFont));
				hcell6.setBorder(Rectangle.NO_BORDER);
				hcell6.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell6.setPaddingRight(-27.5f);
				// hcell4.setPaddingLeft(25f);
				table2.addCell(hcell6);

				hcell6 = new PdfPCell(new Phrase(":", redFont));
				hcell6.setBorder(Rectangle.NO_BORDER);
				hcell6.setHorizontalAlignment(Element.ALIGN_RIGHT);
				// hcell1.setPaddingTop(-5f);;
				table2.addCell(hcell6);

				hcell6 = new PdfPCell(new Phrase(laboratoryRegistration.getBillNo(), redFont));
				hcell6.setBorder(Rectangle.NO_BORDER);
				hcell6.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell6.setPaddingRight(-27.5f);
				// hcell1.setPaddingTop(-5f);
				table2.addCell(hcell6);

				cell3.setFixedHeight(115f);
				cell3.setColspan(2);
				cell3.addElement(table2);

				PdfPTable table98 = new PdfPTable(3);
				table98.setWidths(new float[] { 3f, 1f, 4f });
				table98.setSpacingBefore(10);

				PdfPCell hcell91;
				hcell91 = new PdfPCell(new Phrase("Consultant", redFont));
				hcell91.setBorder(Rectangle.NO_BORDER);
				hcell91.setHorizontalAlignment(Element.ALIGN_LEFT);
				// hcell91.setPaddingBottom(3f);
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

				/*
				 * PdfPTable table97 = new PdfPTable(1); table97.setWidths(new float[] { 5f });
				 * table97.setSpacingBefore(10);
				 * 
				 * PdfPCell hcell97; hcell97 = new PdfPCell(new Phrase( "*" + "OBN0094995" + "*"
				 * + "  " + "==> Scan This BarCode To Take Report At KIOSK", headFont1));
				 * hcell97.setBorder(Rectangle.NO_BORDER); //
				 * hcell3.setHorizontalAlignment(Element.ALIGN_LEFT);
				 * hcell97.setPaddingBottom(-10f); hcell97.setPaddingLeft(-35f);
				 * 
				 * table97.addCell(hcell97); cell3.addElement(table97);
				 */
				table.addCell(cell3);

				// *****************************

				PdfPCell cell19 = new PdfPCell();

				PdfPTable table21 = new PdfPTable(1);
				table21.setWidths(new float[] { 4f });
				table21.setSpacingBefore(10);

				PdfPCell hcell19;
				hcell19 = new PdfPCell(new Phrase("OP Bill Cum Reciept", headFont1));
				hcell19.setBorder(Rectangle.NO_BORDER);
				hcell19.setHorizontalAlignment(Element.ALIGN_CENTER);
				// hcell19.setPaddingLeft(-70f);
				table21.addCell(hcell19);

				cell19.setFixedHeight(20f);
				cell19.setColspan(2);
				cell19.addElement(table21);
				table.addCell(cell19);

				// **************
				PdfPCell cell31 = new PdfPCell();

				PdfPTable table1 = new PdfPTable(7);
				table1.setWidths(new float[] { 1f, 3f, 5f, 3f, 1f, 2f, 2f });

				table1.setSpacingBefore(10);

				PdfPCell hcell;
				hcell = new PdfPCell(new Phrase("S.No", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("Service Code", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("Service Name", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("Service Type", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("Qty", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("Rate", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table1.addCell(hcell);

				/*
				 * hcell = new PdfPCell(new Phrase("Discount", redFont));
				 * hcell.setBorder(Rectangle.NO_BORDER);
				 * hcell.setBackgroundColor(BaseColor.GRAY);
				 * hcell.setHorizontalAlignment(Element.ALIGN_CENTER); table1.addCell(hcell);
				 */
				hcell = new PdfPCell(new Phrase("Amount(RS)", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table1.addCell(hcell);

				int count = 0;
				float total = 0;
				for (LaboratoryRegistration laboratoryRegistrationInfo : laboratoryRegistrationInfor) {

					PdfPCell cell;

					cell = new PdfPCell(new Phrase(String.valueOf(count = count + 1), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table1.addCell(cell);

					cell = new PdfPCell(
							new Phrase(laboratoryRegistrationInfo.getLabServices().getServiceId(), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					// cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					table1.addCell(cell);

					cell = new PdfPCell(new Phrase(laboratoryRegistrationInfo.getServiceName(), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					// cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					table1.addCell(cell);

					cell = new PdfPCell(
							new Phrase(laboratoryRegistrationInfo.getLabServices().getServiceType(), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					// cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					table1.addCell(cell);

					cell = new PdfPCell(new Phrase(String.valueOf(laboratoryRegistrationInfo.getQuantity()), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table1.addCell(cell);

					cell = new PdfPCell(new Phrase(String.valueOf(laboratoryRegistrationInfo.getPrice()), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table1.addCell(cell);

					/*
					 * cell = new PdfPCell(new
					 * Phrase(String.valueOf(laboratoryRegistrationInfo.getDiscount()), redFont));
					 * cell.setBorder(Rectangle.NO_BORDER); cell.setPaddingLeft(5);
					 * cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					 * cell.setHorizontalAlignment(Element.ALIGN_CENTER); table1.addCell(cell);
					 */
					cell = new PdfPCell(new Phrase(String.valueOf(laboratoryRegistrationInfo.getNetAmount()), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table1.addCell(cell);

					total += laboratoryRegistrationInfo.getNetAmount();

				}
				total = Math.round(total);

				/*
				 * cell31.setColspan(2); table1.setWidthPercentage(100f);
				 * cell31.addElement(table1); //cell31.addElement(table37);
				 * table.addCell(cell31);
				 */
				// -------------------------------

				PdfPTable table37 = new PdfPTable(6);
				table37.setWidths(new float[] { 3f, 1f, 4f, 7f, 1f, 4f });
				table37.setSpacingBefore(10);

				PdfPCell cell55;
				
				if(finalCash!=0) {
				cell55 = new PdfPCell(new Phrase("Cash Amt", redFont));
				cell55.setBorder(Rectangle.NO_BORDER);
				cell55.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell55.setPaddingTop(10f);
				 cell55.setPaddingLeft(-50f);
				table37.addCell(cell55);

				cell55 = new PdfPCell(new Phrase(":", redFont));
				cell55.setBorder(Rectangle.NO_BORDER);
				cell55.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell55.setPaddingTop(10f);
				 cell55.setPaddingLeft(-50f);
				table37.addCell(cell55);

			
				cell55 = new PdfPCell(new Phrase(String.valueOf(finalCash), redFont));
				cell55.setBorder(Rectangle.NO_BORDER);
				cell55.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell55.setPaddingTop(10f);
				 cell55.setPaddingLeft(-40f);
				table37.addCell(cell55);
				}else
				{
					cell55 = new PdfPCell(new Phrase("", redFont));
					cell55.setBorder(Rectangle.NO_BORDER);
					cell55.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell55.setPaddingTop(10f);
					 cell55.setPaddingLeft(-50f);
					table37.addCell(cell55);

					cell55 = new PdfPCell(new Phrase("", redFont));
					cell55.setBorder(Rectangle.NO_BORDER);
					cell55.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell55.setPaddingTop(10f);
					 cell55.setPaddingLeft(-50f);
					table37.addCell(cell55);

				
					cell55 = new PdfPCell(new Phrase("", redFont));
					cell55.setBorder(Rectangle.NO_BORDER);
					cell55.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell55.setPaddingTop(10f);
					 cell55.setPaddingLeft(-40f);
					table37.addCell(cell55);
				}
				
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
				cell55.setPaddingRight(-30f);
				table37.addCell(cell55);
				
				PdfPCell cell551;
				cell551 = new PdfPCell(new Phrase("", redFont));
				cell551.setBorder(Rectangle.NO_BORDER);
				cell551.setHorizontalAlignment(Element.ALIGN_LEFT);
				// cell55.setPaddingLeft(-50f);
				table37.addCell(cell551);

				cell551 = new PdfPCell(new Phrase("", redFont));
				cell551.setBorder(Rectangle.NO_BORDER);
				cell551.setHorizontalAlignment(Element.ALIGN_LEFT);
				// cell55.setPaddingLeft(-50f);
				table37.addCell(cell551);

				cell551 = new PdfPCell(new Phrase("", redFont));
				cell551.setBorder(Rectangle.NO_BORDER);
				cell551.setHorizontalAlignment(Element.ALIGN_LEFT);
				// cell55.setPaddingLeft(-50f);
				table37.addCell(cell551);

				cell551 = new PdfPCell(new Phrase("Disc Amt", redFont));
				cell551.setBorder(Rectangle.NO_BORDER);
				cell551.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell551.setPaddingRight(-70f);
				table37.addCell(cell551);

				cell551 = new PdfPCell(new Phrase(":", redFont));
				cell551.setBorder(Rectangle.NO_BORDER);
				cell551.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell551.setPaddingRight(-60f);
				table37.addCell(cell551);

				cell551 = new PdfPCell(new Phrase(String.valueOf(laboratoryRegistration.getDiscount()), redFont));
				cell551.setBorder(Rectangle.NO_BORDER);
				cell551.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell551.setPaddingRight(-30f);
				table37.addCell(cell551);


				PdfPCell hcell56;
				if(finalCard!=0) {
				hcell56 = new PdfPCell(new Phrase("Card Amt", redFont));
				hcell56.setBorder(Rectangle.NO_BORDER);
				hcell56.setHorizontalAlignment(Element.ALIGN_LEFT);
				//hcell56.setPaddingTop(10f);
				hcell56.setPaddingLeft(-50f);
				table37.addCell(hcell56);

				hcell56 = new PdfPCell(new Phrase(":", redFont));
				hcell56.setBorder(Rectangle.NO_BORDER);
				hcell56.setHorizontalAlignment(Element.ALIGN_LEFT);
				//hcell56.setPaddingTop(10f);
				hcell56.setPaddingLeft(-50f);
				table37.addCell(hcell56);
	
			
				hcell56 = new PdfPCell(new Phrase(String.valueOf(finalCard), redFont));
				hcell56.setBorder(Rectangle.NO_BORDER);
				hcell56.setHorizontalAlignment(Element.ALIGN_LEFT);
				//hcell56.setPaddingTop(10f);
				hcell56.setPaddingLeft(-40f);
				table37.addCell(hcell56);
				}else {
					hcell56 = new PdfPCell(new Phrase("", redFont));
					hcell56.setBorder(Rectangle.NO_BORDER);
					hcell56.setHorizontalAlignment(Element.ALIGN_LEFT);
					//hcell56.setPaddingTop(10f);
					hcell56.setPaddingLeft(-50f);
					table37.addCell(hcell56);

					hcell56 = new PdfPCell(new Phrase("", redFont));
					hcell56.setBorder(Rectangle.NO_BORDER);
					hcell56.setHorizontalAlignment(Element.ALIGN_LEFT);
					//hcell56.setPaddingTop(10f);
					hcell56.setPaddingLeft(-50f);
					table37.addCell(hcell56);
		
				
					hcell56 = new PdfPCell(new Phrase("", redFont));
					hcell56.setBorder(Rectangle.NO_BORDER);
					hcell56.setHorizontalAlignment(Element.ALIGN_LEFT);
					//hcell56.setPaddingTop(10f);
					hcell56.setPaddingLeft(-40f);
					table37.addCell(hcell56);
				}
				hcell56 = new PdfPCell(new Phrase("Paid Amt.", redFont));
				hcell56.setBorder(Rectangle.NO_BORDER);
				hcell56.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell56.setPaddingRight(-70f);
				table37.addCell(hcell56);

				hcell56 = new PdfPCell(new Phrase(":", redFont));
				hcell56.setBorder(Rectangle.NO_BORDER);
				// hcell56.setPaddingLeft(-1f);
				hcell56.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell56.setPaddingRight(-60f);
				table37.addCell(hcell56);

				hcell56 = new PdfPCell(new Phrase(String.valueOf(total-laboratoryRegistration.getDiscount()), redFont));
				hcell56.setBorder(Rectangle.NO_BORDER);
				// hcell56.setPaddingLeft(-1f);
				hcell56.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell56.setPaddingRight(-30f);
				table37.addCell(hcell56);
				
				/*
				 * PdfPCell hcell57; if (finalCard!=0) { hcell57 = new PdfPCell(new
				 * Phrase("Card Amt" , redFont)); hcell57.setBorder(Rectangle.NO_BORDER);
				 * hcell57.setHorizontalAlignment(Element.ALIGN_LEFT);
				 * hcell57.setPaddingLeft(-50f); table37.addCell(hcell57);
				 * 
				 * hcell57 = new PdfPCell(new Phrase(":", redFont));
				 * hcell57.setBorder(Rectangle.NO_BORDER);
				 * hcell57.setHorizontalAlignment(Element.ALIGN_LEFT);
				 * hcell57.setPaddingLeft(-50f); table37.addCell(hcell57);
				 * 
				 * hcell57 = new PdfPCell(new Phrase(String.valueOf(finalCard), redFont));
				 * hcell57.setBorder(Rectangle.NO_BORDER);
				 * hcell57.setHorizontalAlignment(Element.ALIGN_LEFT);
				 * hcell57.setPaddingLeft(-40f); table37.addCell(hcell57); } else { hcell57 =
				 * new PdfPCell(new Phrase( " ", redFont));
				 * hcell57.setBorder(Rectangle.NO_BORDER);
				 * hcell57.setHorizontalAlignment(Element.ALIGN_LEFT);
				 * hcell57.setPaddingLeft(-50f); table37.addCell(hcell57);
				 * 
				 * hcell57 = new PdfPCell(new Phrase("", redFont));
				 * hcell57.setBorder(Rectangle.NO_BORDER);
				 * hcell57.setHorizontalAlignment(Element.ALIGN_LEFT);
				 * hcell57.setPaddingLeft(-50f); table37.addCell(hcell57);
				 * 
				 * hcell57 = new PdfPCell(new Phrase("", redFont));
				 * hcell57.setBorder(Rectangle.NO_BORDER);
				 * hcell57.setHorizontalAlignment(Element.ALIGN_LEFT);
				 * hcell57.setPaddingLeft(-40f); table37.addCell(hcell57);
				 * 
				 * }
				 * 
				 * hcell57 = new PdfPCell(new Phrase("Net Amt.", redFont));
				 * hcell57.setBorder(Rectangle.NO_BORDER);
				 * hcell57.setHorizontalAlignment(Element.ALIGN_RIGHT);
				 * hcell57.setPaddingRight(-70f); table37.addCell(hcell57);
				 * 
				 * hcell57 = new PdfPCell(new Phrase(":", redFont));
				 * hcell57.setBorder(Rectangle.NO_BORDER);
				 * hcell57.setHorizontalAlignment(Element.ALIGN_RIGHT);
				 * hcell57.setPaddingRight(-60f); table37.addCell(hcell57);
				 * 
				 * hcell57 = new PdfPCell(new Phrase(String.valueOf(total), redFont));
				 * hcell57.setBorder(Rectangle.NO_BORDER);
				 * hcell57.setHorizontalAlignment(Element.ALIGN_RIGHT);
				 * hcell57.setPaddingRight(-30f); table37.addCell(hcell57);
				 */
				PdfPCell hcell58;
            if(finalDue!=0) {
				hcell58 = new PdfPCell(new Phrase("Due Amt", redFont));
				hcell58.setBorder(Rectangle.NO_BORDER);
				hcell58.setPaddingLeft(-50f);
				table37.addCell(hcell58);

				hcell58 = new PdfPCell(new Phrase("", redFont));
				hcell58.setBorder(Rectangle.NO_BORDER);
				hcell58.setPaddingLeft(-50f);
				table37.addCell(hcell58);

				hcell58 = new PdfPCell(new Phrase(String.valueOf(finalDue), redFont));
				hcell58.setBorder(Rectangle.NO_BORDER);
				hcell58.setPaddingLeft(-40f);
				table37.addCell(hcell58);
}else {
	
	
	hcell58 = new PdfPCell(new Phrase("", redFont));
	hcell58.setBorder(Rectangle.NO_BORDER);
	hcell58.setPaddingLeft(-50f);
	table37.addCell(hcell58);

	hcell58 = new PdfPCell(new Phrase("", redFont));
	hcell58.setBorder(Rectangle.NO_BORDER);
	hcell58.setPaddingLeft(-50f);
	table37.addCell(hcell58);

	hcell58 = new PdfPCell(new Phrase("", redFont));
	hcell58.setBorder(Rectangle.NO_BORDER);
	hcell58.setPaddingLeft(-40f);
	table37.addCell(hcell58);
	
}
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

				hcell58 = new PdfPCell(new Phrase(String.valueOf(total-laboratoryRegistration.getDiscount()), redFont));
				hcell58.setBorder(Rectangle.NO_BORDER);
				hcell58.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell58.setPaddingRight(-30f);
				table37.addCell(hcell58);
				
				long numAmount=(long)(total-laboratoryRegistration.getDiscount());

				PdfPCell hcell59;
				hcell59 = new PdfPCell(new Phrase("Gross Amount In Words ", redFont));
				hcell59.setBorder(Rectangle.NO_BORDER);
				hcell59.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell59.setPaddingLeft(-50f);
				table37.addCell(hcell59);

				hcell59 = new PdfPCell(new Phrase("", headFont));
				hcell59.setBorder(Rectangle.NO_BORDER);
				// hcell57.setPaddingTop(18f);
				hcell59.setHorizontalAlignment(Element.ALIGN_LEFT);
				table37.addCell(hcell59);

				hcell59 = new PdfPCell(new Phrase("(" + numberToWordsConverter.convert((long) total) + ")", redFont));
				hcell59.setBorder(Rectangle.NO_BORDER);
				hcell59.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell59.setPaddingLeft(-35f);
				table37.addCell(hcell59);

				hcell59 = new PdfPCell(new Phrase("", headFont));
				hcell59.setBorder(Rectangle.NO_BORDER);
				// hcell57.setPaddingTop(18f);
				hcell59.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table37.addCell(hcell59);

				hcell59 = new PdfPCell(new Phrase("", headFont));
				hcell59.setBorder(Rectangle.NO_BORDER);
				// hcell57.setPaddingTop(18f);
				hcell59.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table37.addCell(hcell59);

				hcell59 = new PdfPCell(new Phrase("", headFont));
				hcell59.setBorder(Rectangle.NO_BORDER);
				// hcell57.setPaddingTop(18f);
				hcell59.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table37.addCell(hcell59);

				PdfPCell hcell60;
				hcell60 = new PdfPCell(new Phrase("Received Amount In Words ", redFont));

				hcell60.setBorder(Rectangle.NO_BORDER);
				hcell60.setPaddingLeft(-50f);
				hcell60.setHorizontalAlignment(Element.ALIGN_LEFT);
				table37.addCell(hcell60);

				hcell60 = new PdfPCell(new Phrase("", headFont));
				hcell60.setBorder(Rectangle.NO_BORDER);
				// hcell57.setPaddingTop(18f);
				hcell60.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table37.addCell(hcell60);

				hcell60 = new PdfPCell(new Phrase("(" + numberToWordsConverter.convert(numAmount) + ")", redFont));
				hcell60.setBorder(Rectangle.NO_BORDER);
				hcell60.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell60.setPaddingLeft(-20f);
				table37.addCell(hcell60);

				hcell60 = new PdfPCell(new Phrase("", headFont));
				hcell60.setBorder(Rectangle.NO_BORDER);
				// hcell57.setPaddingTop(18f);
				hcell60.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table37.addCell(hcell60);

				hcell60 = new PdfPCell(new Phrase("", headFont));
				hcell60.setBorder(Rectangle.NO_BORDER);
				// hcell57.setPaddingTop(18f);
				hcell60.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table37.addCell(hcell60);

				hcell60 = new PdfPCell(new Phrase("", headFont));
				hcell60.setBorder(Rectangle.NO_BORDER);
				// hcell57.setPaddingTop(18f);
				hcell60.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table37.addCell(hcell60);

				cell31.setColspan(2);
				// cell31.setFixedHeight(170f);
				table1.setWidthPercentage(100f);
				cell31.addElement(table1);
				cell31.addElement(table37);
				table.addCell(cell31);

				// -----------------------

				PdfPCell cell33 = new PdfPCell();

				PdfPTable table13 = new PdfPTable(5);
				table13.setWidths(new float[] { 2f, 3f, 3f, 3f, 3f });

				table13.setSpacingBefore(10);

				PdfPCell hcell33;
				hcell33 = new PdfPCell(new Phrase("Pay Mode", redFont2));
				hcell33.setBorder(Rectangle.NO_BORDER);
				hcell33.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell33.setPaddingLeft(10f);
				table13.addCell(hcell33);

				hcell33 = new PdfPCell(new Phrase("Amount", redFont2));
				hcell33.setBorder(Rectangle.NO_BORDER);
				hcell33.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell33.setPaddingLeft(35f);
				table13.addCell(hcell33);

				hcell33 = new PdfPCell(new Phrase("Card#", redFont2));
				hcell33.setBorder(Rectangle.NO_BORDER);
				hcell33.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell33.setPaddingLeft(40f);
				table13.addCell(hcell33);

				hcell33 = new PdfPCell(new Phrase("Bank Name", redFont2));
				hcell33.setBorder(Rectangle.NO_BORDER);
				hcell33.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell33.setPaddingLeft(40f);
				table13.addCell(hcell33);

				hcell33 = new PdfPCell(new Phrase("Exp Date", redFont2));
				hcell33.setBorder(Rectangle.NO_BORDER);
				hcell33.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell33.setPaddingLeft(50f);
				table13.addCell(hcell33);

				PdfPCell hcell34;
				hcell34 = new PdfPCell(new Phrase(paymentMode, redFont));
				hcell34.setBorder(Rectangle.NO_BORDER);
				hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell34.setPaddingLeft(10f);
				table13.addCell(hcell34);

				hcell34 = new PdfPCell(new Phrase(String.valueOf(Math.round(total)), redFont));
				hcell34.setBorder(Rectangle.NO_BORDER);
				hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell34.setPaddingLeft(50f);
				table13.addCell(hcell34);
				if (paymentMode.equalsIgnoreCase("card") || paymentMode.equalsIgnoreCase("cash+card")) {
					hcell34 = new PdfPCell(new Phrase(refNo, redFont));
					hcell34.setBorder(Rectangle.NO_BORDER);
					hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell34.setPaddingLeft(40f);
					table13.addCell(hcell34);
				} else {
					hcell34 = new PdfPCell(new Phrase("", redFont));
					hcell34.setBorder(Rectangle.NO_BORDER);
					hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell34.setPaddingLeft(40f);
					table13.addCell(hcell34);

				}
				hcell34 = new PdfPCell(new Phrase("", redFont));
				hcell34.setBorder(Rectangle.NO_BORDER);
				hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell34.setPaddingLeft(40f);
				table13.addCell(hcell34);

				hcell34 = new PdfPCell(new Phrase("", redFont));
				hcell34.setBorder(Rectangle.NO_BORDER);
				hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell34.setPaddingLeft(50f);
				table13.addCell(hcell34);

				// cell33.setFixedHeight(35f);
				cell33.setColspan(2);
				table13.setWidthPercentage(100f);
				cell33.addElement(table13);
				table.addCell(cell33);

				PdfPCell cell5 = new PdfPCell();

				PdfPTable table35 = new PdfPTable(2);
				table35.setWidths(new float[] { 5f, 4f });
				table35.setSpacingBefore(10);

				PdfPCell hcell12;
				hcell12 = new PdfPCell(new Phrase("Created By    : " + createdBy, redFont));
				hcell12.setBorder(Rectangle.NO_BORDER);
				// hcell3.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell12.setPaddingTop(10f);
				hcell12.setPaddingLeft(-50f);
				table35.addCell(hcell12);

				hcell12 = new PdfPCell(new Phrase("Created Dt   :   " + today, redFont));
				hcell12.setBorder(Rectangle.NO_BORDER);
				hcell12.setPaddingTop(10f);
				// hcell12.setPaddingRight(0f);
				hcell12.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table35.addCell(hcell12);

				PdfPCell hcell13;
				hcell13 = new PdfPCell(new Phrase("Printed By     : " + createdBy, redFont));
				hcell13.setBorder(Rectangle.NO_BORDER);
				hcell13.setPaddingLeft(-50f);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table35.addCell(hcell13);

				hcell13 = new PdfPCell(new Phrase("Print Dt       :   " + today, redFont));
				hcell13.setBorder(Rectangle.NO_BORDER);
				hcell13.setPaddingRight(3f);
				hcell13.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table35.addCell(hcell13);

				PdfPCell hcell23;
				hcell23 = new PdfPCell(new Phrase(""));
				hcell23.setBorder(Rectangle.NO_BORDER);
				// hcell23.setPaddingLeft(-50f);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
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

				pdfBytes = byteArrayOutputStream.toByteArray();
				String uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/payment/viewFile/")
						.path(paymentPdfServiceImpl.getNextPdfId()).toUriString();

				patientPaymentPdf = new PatientPaymentPdf(regId + " Lab Registration", uri, regId, pdfBytes, bill);
				patientPaymentPdf.setPid(paymentPdfServiceImpl.getNextPdfId());
				paymentPdfServiceImpl.save(patientPaymentPdf);

			} catch (Exception e) {
				Logger.error(e.getMessage());
			}

		}
		return patientPaymentPdf;

	}

	
	
	@Override
	public List<LaboratoryRegistration> particularPatientRecordScroll(Object fromDate, Object toDate, String userName, String regId) {
		return laboratoryRegistrationRepository.particularPatientRecordScroll(fromDate, toDate, userName,regId);
	}

	
	@Override
	@Transactional
	public void deleteByLabRegId(String labRegId) {
		
	laboratoryRegistrationRepository.deleteByLabRegId(labRegId);
	}

	
	@Override
	public List<Object> getCancelSErvices(String billNo) {
		
		List<Object> list=new ArrayList<Object>();
		List<Object> mapList=new ArrayList<Object>();
				
		List<LaboratoryRegistration> laboratoryRegistrations=laboratoryRegistrationRepository.findByBillNo(billNo);
		
		if(laboratoryRegistrations.isEmpty()) {
			
			throw new RuntimeException(" NO SERVICES AVAILABLE FOR THIS BILL NUMBER");
		}
		
		
		Date dateAdv = laboratoryRegistrations.get(0).getEnteredDate();
		DateFormat formatterAdv = new SimpleDateFormat("dd-MMM-yyyy");
		String advDate = formatterAdv.format(dateAdv).toString();

		Map<String, String> map=new HashMap<String, String>();
		map.put("date",advDate );
		map.put("name", laboratoryRegistrations.get(0).getPatientName());
		map.put("mobileNo", String.valueOf(laboratoryRegistrations.get(0).getMobile()));
		map.put("regNo", laboratoryRegistrations.get(0).getLaboratoryPatientRegistration().getRegId());
		mapList.add(map);
		list.add(mapList);
		
		list.add(laboratoryRegistrations);
		return list;
	}

	
	@Transactional
	public void deleteAllByBillNo(String billNo) {

		laboratoryRegistrationRepository.deleteAllByBillNo(billNo);
	}
	

	
	
}
