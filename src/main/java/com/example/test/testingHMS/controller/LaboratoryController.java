package com.example.test.testingHMS.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.Principal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.test.testingHMS.MoneyToWords.NumberToWordsConverter;
import com.example.test.testingHMS.bed.model.RoomBookingDetails;
import com.example.test.testingHMS.bed.model.RoomDetails;
import com.example.test.testingHMS.bill.dto.ChargeBillDto;
import com.example.test.testingHMS.bill.helper.RefBillDetails;
import com.example.test.testingHMS.bill.model.ChargeBill;
import com.example.test.testingHMS.bill.repository.ChargeBillRepository;
import com.example.test.testingHMS.bill.serviceImpl.ChargeBillServiceImpl;
import com.example.test.testingHMS.finalBilling.model.FinalBilling;
import com.example.test.testingHMS.finalBilling.serviceImpl.FinalBillingServiceImpl;
import com.example.test.testingHMS.laboratory.dto.LaboratoryRegistrationDto;
import com.example.test.testingHMS.laboratory.dto.PatientServiceDetailsDto;
import com.example.test.testingHMS.laboratory.helper.RefLaboratoryRegistration;
import com.example.test.testingHMS.laboratory.helper.RefLaboratoryService;
import com.example.test.testingHMS.laboratory.model.LabServiceRange;
import com.example.test.testingHMS.laboratory.model.LabServices;
import com.example.test.testingHMS.laboratory.model.LaboratoryRegistration;
import com.example.test.testingHMS.laboratory.model.PatientServiceDetails;
import com.example.test.testingHMS.laboratory.model.ServicePdf;
import com.example.test.testingHMS.laboratory.repository.LabServiceRangeRepository;
import com.example.test.testingHMS.laboratory.repository.LaboratoryRegistrationRepository;
import com.example.test.testingHMS.laboratory.serviceImpl.LabServicesServiceImpl;
import com.example.test.testingHMS.laboratory.serviceImpl.LaboratoryRegistrationServiceImpl;
import com.example.test.testingHMS.laboratory.serviceImpl.PatientServiceDetailsServiceImpl;
import com.example.test.testingHMS.laboratory.serviceImpl.ServicePdfServiceImpl;
import com.example.test.testingHMS.patient.model.CashPlusCard;
import com.example.test.testingHMS.patient.model.PatientDetails;
import com.example.test.testingHMS.patient.model.PatientPayment;
import com.example.test.testingHMS.patient.model.PatientPaymentPdf;
import com.example.test.testingHMS.patient.model.PatientRegistration;
import com.example.test.testingHMS.patient.serviceImpl.CashPlusCardServiceImpl;
import com.example.test.testingHMS.patient.serviceImpl.PatientRegistrationServiceImpl;
import com.example.test.testingHMS.patient.serviceImpl.PaymentPdfServiceImpl;
import com.example.test.testingHMS.patient.serviceImpl.PaymentServiceImpl;
import com.example.test.testingHMS.pharmacist.model.SalesPaymentPdf;
import com.example.test.testingHMS.pharmacist.serviceImpl.SalesPaymentPdfServiceImpl;
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

@CrossOrigin(origins="*",maxAge=36000)
@RestController
@RequestMapping("/v1/lab")
public class LaboratoryController {
	
	public static Logger Logger=LoggerFactory.getLogger(LaboratoryController.class);
	
	@Value("${hospital.logo}")
	private Resource hospitalLogo;

	@Autowired
	LaboratoryRegistrationServiceImpl laboratoryRegistrationServiceImpl;
	
	@Autowired
	PaymentServiceImpl  paymentServiceImpl;
	
	@Autowired
	FinalBillingServiceImpl finalBillingServcieImpl;
	
	@Autowired
	LabServiceRangeRepository labServiceRangeRepository;
	
	@Autowired
	SalesPaymentPdfServiceImpl salesPaymentPdfServiceImpl;
	
	@Autowired
	ChargeBillRepository chargeBillRepository;
	
	@Autowired
	NumberToWordsConverter numberToWordsConverter;
	
	@Autowired
	ServicePdfServiceImpl servicePdfServiceImpl;
	
	@Autowired
	ResourceLoader resourceLoader;
	
	@Autowired
	ChargeBillServiceImpl chargeBillServiceImpl;
	
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
	CashPlusCardServiceImpl cashPlusCardServiceImpl;
	
	
	@Autowired
	LabServicesServiceImpl labServicesServiceImpl;
	
	@Autowired
	RefLaboratoryService refLaboratoryService;
	
	
	
	@RequestMapping(value="/register",method=RequestMethod.GET)
	public List<Object> getService()
	{
		return laboratoryRegistrationServiceImpl.getService();
	}
	
	
	/*
	 * to get cost for that service Inpatient
	 */
	@RequestMapping(value="/service/{name}/{regId}",method=RequestMethod.GET)
	public Map<String, String> getServiceCost(@PathVariable("name") String name,@PathVariable("regId") String regId)
	{
		return laboratoryRegistrationServiceImpl.getServiceCost(name, regId);
	}
	
	/*
	 * to get cost for that service Outpatient
	 */
	@RequestMapping(value="/opservice/{name}/{regId}",method=RequestMethod.GET)
	public Map<String, String> getOpServiceCost(@PathVariable("name") String name,@PathVariable("regId") String regId)
	{
		
		return laboratoryRegistrationServiceImpl.getOpServiceCost(name, regId);
	}
	

	/*
	 * To get services according to the patient type
	 */
	@RequestMapping(value="/service/type/{regId}",method=RequestMethod.GET)
	public List<LabServices> accordingToService(@PathVariable String regId)
	{
		String patientType= patientRegistrationServiceImpl.findByRegId(regId).getpType();
		if(patientType.equalsIgnoreCase(ConstantValues.INPATIENT)||patientType.equalsIgnoreCase(ConstantValues.DAYCARE)||patientType.equalsIgnoreCase(ConstantValues.EMERGENCY))
		{
		return labServicesServiceImpl.servicesForInptient(ConstantValues.INPATIENT);
		}
		else if(patientType.equalsIgnoreCase(ConstantValues.OUTPATIENT))
		{
			return labServicesServiceImpl.servicesForInptient(ConstantValues.OUTPATIENT);
		}
		else
		{
			return null;
		}
	}
	

	
	@RequestMapping(value="/register/patient",method=RequestMethod.POST)
	public PatientPaymentPdf registerServiceP(@RequestBody LaboratoryRegistrationDto laboratoryRegistrationDto,Principal principal)  throws Exception
	{
		//createdBy (Security)
		User userSecurity=userServiceImpl.findByUserName(principal.getName());
		
		LaboratoryRegistration laboratoryRegistration=new LaboratoryRegistration();
		BeanUtils.copyProperties(laboratoryRegistrationDto, laboratoryRegistration);
		
		return laboratoryRegistrationServiceImpl.registerServiceP(laboratoryRegistration, principal);
	}
	
	
	
	/*
	 * To pay for the laboratory
	 */
	@RequestMapping(value="/register/pay/{regId}/{invoice}",method=RequestMethod.PUT)
	public PatientPaymentPdf payForService(@PathVariable String regId,@PathVariable String invoice,Principal principal)
	{
		String refNo=null;
		String payMode=null;
		String bill=null;
		List<LaboratoryRegistration> laboratoryRegistration=laboratoryRegistrationServiceImpl.findBill(regId, invoice);
		for(LaboratoryRegistration laboratoryRegistrationInfo:laboratoryRegistration)
		{
			laboratoryRegistrationInfo.setPaid("YES");
			laboratoryRegistrationRepository.save(laboratoryRegistrationInfo);
			payMode=laboratoryRegistrationInfo.getPaymentType();
			refNo=laboratoryRegistrationInfo.getReferenceNumber();
			bill=laboratoryRegistrationInfo.getBillNo();
		}
		PatientRegistration patientRegistration=patientRegistrationServiceImpl.findByRegId(regId);
		if(patientRegistration.getpType().equals(ConstantValues.INPATIENT)||patientRegistration.getpType().equals(ConstantValues.DAYCARE)||patientRegistration.getpType().equals(ConstantValues.EMERGENCY))
		{
			for(LaboratoryRegistration laboratoryRegistrationInfo:laboratoryRegistration)
			{
				ChargeBill chargeBill=chargeBillServiceImpl.findByLabId(laboratoryRegistrationInfo);
				chargeBill.setPaid("YES");
				chargeBillRepository.save(chargeBill);
			}
			
		}
		
		// createdBy (Security)
		User user=userServiceImpl.findByUserName(principal.getName());
		String createdBy=user.getFirstName()+" "+user.getLastName();
		
		
		// PDF
		
		String newAddress=" Plot No14,15,16 & 17,Nandi Co-op.Society,"
				+ "\n                                   Main Road, Beside Navya Grand Hotel, \n                                Miyapur,Hyderabad-49,Phone:040-23046789   \n                               "
				+ "   For Appointment Contact:8019114481   "
				+ "\n                                   Email :udbhavahospitals@gmail.com ";
				PatientPaymentPdf patientPaymentPdf=null;
				 byte[] pdfBytes=null;
					ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

				Document document = new Document(PageSize.A4.rotate());
				
								
				try {

					Resource fileResource=resourceLoader.getResource("classpath:udbhava.png");
					Chunk cnd1 = new Chunk(new VerticalPositionMark());
					Font redFont1 = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
					PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);
					Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
					Font redFont2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
					Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
					Font headFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
						
						document.open();
						PdfPTable table = new PdfPTable(2);

						Image img = Image.getInstance(hospitalLogo.getURL());
						img.scaleAbsolute(56, 87);
						table.setWidthPercentage(105);

						Phrase pq = new Phrase(new Chunk(img, 0, -70));

						pq.add(new Chunk(newAddress,redFont));
						PdfPCell cellp = new PdfPCell(pq);
						PdfPCell cell1 = new PdfPCell();

						
						// Display a date in day, month, year format
						Date date = Calendar.getInstance().getTime();
						DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
						String today = formatter.format(date).toString();

						
						// for header Bold
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

						// for header end

						cell1.addElement(pq);
						cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
						table.addCell(cell1);

						PdfPCell cell3 = new PdfPCell();

						PdfPTable table99 = new PdfPTable(1);
						table99.setWidths(new float[] { 5f });
						table99.setSpacingBefore(10);

						PdfPCell hcell90;
						hcell90 = new PdfPCell(new Phrase("Patient        : " +  patientRegistration.getPatientDetails().getTitle()+". "+patientRegistration.getPatientDetails().getFirstName()+" "+patientRegistration.getPatientDetails().getLastName(), redFont));
						hcell90.setBorder(Rectangle.NO_BORDER);
						// hcell3.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell90.setPaddingBottom(-7f);
						hcell90.setPaddingLeft(-25f);

						table99.addCell(hcell90);
						cell3.addElement(table99);
						// table.addCell(cell3);

						PdfPTable table2 = new PdfPTable(2);
						table2.setWidths(new float[] { 5f, 4f });
						table2.setSpacingBefore(10);

						PdfPCell hcell1;
						hcell1 = new PdfPCell(new Phrase("Age/Sex     : " + patientRegistration.getPatientDetails().getAge()+"/"+patientRegistration.getPatientDetails().getGender(), redFont));
						hcell1.setBorder(Rectangle.NO_BORDER);
						hcell1.setPaddingLeft(-25f);
						// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);

						table2.addCell(hcell1);

						hcell1 = new PdfPCell(new Phrase("UMR NO    : " +patientRegistration.getPatientDetails().getUmr() , redFont));
						hcell1.setBorder(Rectangle.NO_BORDER);
						//hcell1.setPaddingRight(1f);
						hcell1.setPaddingLeft(25f);

						//hcell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
						table2.addCell(hcell1);

						PdfPCell hcell4;
						hcell4 = new PdfPCell(new Phrase("Bill Dt        : " + today, redFont));
						hcell4.setBorder(Rectangle.NO_BORDER);
						hcell4.setPaddingLeft(-25f);
						// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
						table2.addCell(hcell4);

						hcell4 = new PdfPCell(new Phrase("Bill No        : " + invoice, redFont));
						hcell4.setBorder(Rectangle.NO_BORDER);
						//hcell4.setPaddingRight(3f);
						hcell4.setPaddingLeft(25f);

						//hcell4.setHorizontalAlignment(Element.ALIGN_RIGHT);
						table2.addCell(hcell4);
						PdfPCell hcell15;
						hcell15 = new PdfPCell(new Phrase("Ref.By       : " +  patientRegistration.getPatientDetails().getvRefferalDetails().getRefName(), redFont));
						hcell15.setBorder(Rectangle.NO_BORDER);
						hcell15.setPaddingLeft(-25f);
						// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
						table2.addCell(hcell15);

						hcell15 = new PdfPCell(new Phrase("Phone          : " + patientRegistration.getPatientDetails().getMobile(), redFont));
						hcell15.setBorder(Rectangle.NO_BORDER);
						//hcell15.setPaddingRight(7f);
						hcell15.setPaddingLeft(25f);

						//hcell15.setHorizontalAlignment(Element.ALIGN_RIGHT);
						table2.addCell(hcell15);

						cell3.setFixedHeight(115f);
						cell3.setColspan(2);
						cell3.addElement(table2);

						PdfPTable table98 = new PdfPTable(1);
						table98.setWidths(new float[] { 5f });
						table98.setSpacingBefore(10);

						PdfPCell hcell91;
						hcell91 = new PdfPCell(new Phrase("Consultant : " + patientRegistration.getPatientDetails().getConsultant(), redFont));
						hcell91.setBorder(Rectangle.NO_BORDER);
						// hcell3.setHorizontalAlignment(Element.ALIGN_LEFT);
						// hcell91.setPaddingBottom(3f);
						hcell91.setPaddingTop(-5f);
						hcell91.setPaddingLeft(-25f);

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

						// *****************************

						PdfPCell cell19 = new PdfPCell();

						PdfPTable table21 = new PdfPTable(1);
						table21.setWidths(new float[] { 4f });
						table21.setSpacingBefore(10);

						PdfPCell hcell19;
						hcell19 = new PdfPCell(new Phrase("Laboratory Bill Receipt", headFont1));
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

						PdfPTable table1 = new PdfPTable(8);
						table1.setWidths(new float[] { 0.8f, 3f, 5f, 3f, 1f, 2f,1f, 2f });

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
						hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table1.addCell(hcell);

						hcell = new PdfPCell(new Phrase("Service Type", redFont));
						hcell.setBorder(Rectangle.NO_BORDER);
						hcell.setBackgroundColor(BaseColor.GRAY);
						hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
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
						
						
						hcell = new PdfPCell(new Phrase("Discount", redFont));
						hcell.setBorder(Rectangle.NO_BORDER);
						hcell.setBackgroundColor(BaseColor.GRAY);
						hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table1.addCell(hcell);


						hcell = new PdfPCell(new Phrase("Amount(RS)", redFont));
						hcell.setBorder(Rectangle.NO_BORDER);
						hcell.setBackgroundColor(BaseColor.GRAY);
						hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table1.addCell(hcell);

						int count = 0;
						long total=0;
						for (LaboratoryRegistration laboratoryRegistrationInfo:laboratoryRegistration ) {

							PdfPCell cell;

							cell = new PdfPCell(new Phrase(String.valueOf(count = count + 1), redFont));
							cell.setBorder(Rectangle.NO_BORDER);
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_CENTER);
							table1.addCell(cell);

							cell = new PdfPCell(new Phrase(laboratoryRegistrationInfo.getLabServices().getServiceId(), redFont));
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
							cell.setHorizontalAlignment(Element.ALIGN_CENTER);
							//cell.setHorizontalAlignment(Element.ALIGN_LEFT);
							table1.addCell(cell);

							cell = new PdfPCell(new Phrase(laboratoryRegistrationInfo.getLabServices().getServiceType(), redFont));
							cell.setBorder(Rectangle.NO_BORDER);
							cell.setPaddingLeft(5);
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_CENTER);
							// cell.setHorizontalAlignment(Element.ALIGN_LEFT);
							table1.addCell(cell);

							cell = new PdfPCell(new Phrase("1", redFont));
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

							cell = new PdfPCell(new Phrase(String.valueOf(laboratoryRegistrationInfo.getDiscount()), redFont));
							cell.setBorder(Rectangle.NO_BORDER);
							cell.setPaddingLeft(5);
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_CENTER);
							table1.addCell(cell);
							
							cell = new PdfPCell(new Phrase(String.valueOf(laboratoryRegistrationInfo.getNetAmount()), redFont));
							cell.setBorder(Rectangle.NO_BORDER);
							cell.setPaddingLeft(5);
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_CENTER);
							table1.addCell(cell);
							
					        total+=laboratoryRegistrationInfo.getNetAmount();

						}

						/*
						 * cell31.setColspan(2); table1.setWidthPercentage(100f);
						 * cell31.addElement(table1); //cell31.addElement(table37);
						 * table.addCell(cell31);
						 */
						// -------------------------------

						PdfPTable table37 = new PdfPTable(2);
						table37.setWidths(new float[] { 5f, 4f });
						table37.setSpacingBefore(10);

						PdfPCell cell55;
						cell55 = new PdfPCell(new Phrase("", redFont));
						cell55.setBorder(Rectangle.NO_BORDER);
						// hcell3.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell55.setPaddingTop(10f);
						cell55.setPaddingLeft(-50f);
						table37.addCell(cell55);

						cell55 = new PdfPCell(new Phrase("Gross Amt             :     " + total, redFont));
						cell55.setBorder(Rectangle.NO_BORDER);
						cell55.setPaddingTop(10f);
						cell55.setPaddingRight(-48f);
						cell55.setHorizontalAlignment(Element.ALIGN_RIGHT);
						table37.addCell(cell55);

						PdfPCell hcell56;
						hcell56 = new PdfPCell(new Phrase("", redFont));
						hcell56.setBorder(Rectangle.NO_BORDER);
						hcell56.setPaddingLeft(-1f);
						hcell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
						table37.addCell(hcell56);

						hcell56 = new PdfPCell(new Phrase("Paid Amt.              :     " + total, redFont));
						hcell56.setBorder(Rectangle.NO_BORDER);
						hcell56.setPaddingRight(-48f);
						hcell56.setHorizontalAlignment(Element.ALIGN_RIGHT);
						table37.addCell(hcell56);

						PdfPCell hcell57;
						hcell57 = new PdfPCell(new Phrase("Cash Amt.   : " + total, redFont));
						hcell57.setBorder(Rectangle.NO_BORDER);
						hcell57.setPaddingLeft(-70f);
						// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
						table37.addCell(hcell57);

						hcell57 = new PdfPCell(new Phrase("Net Amt.               :     " + total, redFont));
						hcell57.setBorder(Rectangle.NO_BORDER);
						hcell57.setPaddingRight(-48f);
						hcell57.setHorizontalAlignment(Element.ALIGN_RIGHT);
						table37.addCell(hcell57);

						PdfPCell hcell58;
						hcell58 = new PdfPCell(new Phrase(""));
						hcell58.setBorder(Rectangle.NO_BORDER);
						// hcell23.setPaddingLeft(-50f);
						// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
						table37.addCell(hcell58);

						hcell58 = new PdfPCell(new Phrase("Received Amt.      :     " + total, redFont));
						hcell58.setBorder(Rectangle.NO_BORDER);
						hcell58.setPaddingRight(-48f);
						hcell58.setHorizontalAlignment(Element.ALIGN_RIGHT);
						table37.addCell(hcell58);

						PdfPCell hcell59;
						hcell59 = new PdfPCell(
								new Phrase("Gross Amount In Words " + "(" +  numberToWordsConverter.convert(total) + ")", redFont));

						hcell59.setBorder(Rectangle.NO_BORDER);
						hcell59.setPaddingLeft(-70f);
						// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
						table37.addCell(hcell59);

						hcell59 = new PdfPCell(new Phrase("", headFont));
						hcell59.setBorder(Rectangle.NO_BORDER);
						// hcell57.setPaddingTop(18f);
						hcell59.setHorizontalAlignment(Element.ALIGN_RIGHT);
						table37.addCell(hcell59);

						PdfPCell hcell60;
						hcell60 = new PdfPCell(new Phrase(
								"Received Amount In Words " + "(" + numberToWordsConverter.convert(total) + ")", redFont));

						hcell60.setBorder(Rectangle.NO_BORDER);
						hcell60.setPaddingLeft(-70f);
						// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
						table37.addCell(hcell60);

						hcell60 = new PdfPCell(new Phrase("", headFont));
						hcell60.setBorder(Rectangle.NO_BORDER);
						// hcell57.setPaddingTop(18f);
						hcell60.setHorizontalAlignment(Element.ALIGN_RIGHT);
						table37.addCell(hcell60);

						
						
						
						
						cell31.setColspan(2);
						table1.setWidthPercentage(100f);
						cell31.addElement(table1);
						cell31.addElement(table37);
						table.addCell(cell31);

						
						PdfPCell cell33 = new PdfPCell();

						PdfPTable table13 = new PdfPTable(5);
						table13.setWidths(new float[] { 2f, 3f,3f, 3f, 3f });

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
						hcell34 = new PdfPCell(new Phrase(payMode, redFont));
						hcell34.setBorder(Rectangle.NO_BORDER);
						hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell34.setPaddingLeft(10f);
						table13.addCell(hcell34);

						hcell34 = new PdfPCell(new Phrase(String.valueOf(Math.round(total)), redFont));
						hcell34.setBorder(Rectangle.NO_BORDER);
						hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell34.setPaddingLeft(35f);
						table13.addCell(hcell34);
						if(payMode.equalsIgnoreCase("card") || payMode.equalsIgnoreCase("cash+card"))
						{
						hcell34 = new PdfPCell(new Phrase(refNo, redFont));
						hcell34.setBorder(Rectangle.NO_BORDER);
						hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell34.setPaddingLeft(40f);
						table13.addCell(hcell34);
						}
						else
						{
							hcell34 = new PdfPCell(new Phrase("", redFont));
							hcell34.setBorder(Rectangle.NO_BORDER);
							hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
							hcell34.setPaddingLeft(40f);
							table13.addCell(hcell34);

						}
						hcell34 = new PdfPCell(new Phrase("", redFont1));
						hcell34.setBorder(Rectangle.NO_BORDER);
						hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell34.setPaddingLeft(40f);
						table13.addCell(hcell34);

						hcell34 = new PdfPCell(new Phrase("", redFont1));
						hcell34.setBorder(Rectangle.NO_BORDER);
						hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell34.setPaddingLeft(50f);
						table13.addCell(hcell34);

						//cell33.setFixedHeight(35f);
						cell33.setColspan(2);
						table13.setWidthPercentage(100f);
						cell33.addElement(table13);
						table.addCell(cell33);
						
						
						
						
						// -----------------------

						PdfPCell cell5 = new PdfPCell();

						PdfPTable table35 = new PdfPTable(2);
						table35.setWidths(new float[] { 5f, 4f });
						table35.setSpacingBefore(10);

						PdfPCell hcell12;
						hcell12 = new PdfPCell(new Phrase("Created By    : "+createdBy, redFont));
						hcell12.setBorder(Rectangle.NO_BORDER);
						// hcell3.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell12.setPaddingTop(10f);
						hcell12.setPaddingLeft(-70f);
						table35.addCell(hcell12);

						hcell12 = new PdfPCell(new Phrase("Created Dt   :   " + today, redFont));
						hcell12.setBorder(Rectangle.NO_BORDER);
						hcell12.setPaddingTop(10f);
						// hcell12.setPaddingRight(0f);
						hcell12.setHorizontalAlignment(Element.ALIGN_RIGHT);
						table35.addCell(hcell12);

						PdfPCell hcell13;
						hcell13 = new PdfPCell(new Phrase("Printed By     : "+createdBy, redFont));
						hcell13.setBorder(Rectangle.NO_BORDER);
						hcell13.setPaddingLeft(-70f);
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

						cell5.setFixedHeight(105f);
						cell5.setColspan(2);
						cell5.addElement(table35);
						table.addCell(cell5);

						document.add(table);

						document.close();
					
					
					
					System.out.println("finished");			
			
			pdfBytes = byteArrayOutputStream.toByteArray();
			String uri=ServletUriComponentsBuilder.fromCurrentContextPath()
	                .path("/v1/payment/viewFile/")
	                .path(paymentPdfServiceImpl.getNextPdfId())
	                .toUriString();
			
			patientPaymentPdf=new PatientPaymentPdf(regId+" Lab Payment",uri,regId,pdfBytes,bill);
			patientPaymentPdf.setPid(paymentPdfServiceImpl.getNextPdfId());
			paymentPdfServiceImpl.save(patientPaymentPdf);
			
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
		return patientPaymentPdf;
		
	}
	
	
	
	/*
	 * To get measures for Services
	 */
	@RequestMapping(value="/report/{service}/{regId}",method=RequestMethod.GET)
	public List<LabServiceRange> labReportService(@PathVariable String service, @PathVariable String regId)
	{
		PatientRegistration patientRegistration=patientRegistrationServiceImpl.findByRegId(regId);
		List<LabServices> labServices=labServicesServiceImpl.findByServiceName(service);
		float age1=0;

		int age=0;
		String ageType=null;
		String gender=null;
		LocalDate today = LocalDate.now();
		LocalDate birthday = patientRegistration.getPatientDetails().getDob().toLocalDateTime().toLocalDate();
		Period p = Period.between(birthday, today);
		System.out.println("Days"+p.getDays()+"months"+p.getMonths()+"years"+p.getYears());
		System.out.println("service id"+labServices.get(0).getServiceId());
		Object String;
		if(service.equalsIgnoreCase("LFT - LIVER FUNCTON TESTS") ||service.equalsIgnoreCase("SERUM BILIRUBIN")){
			String newAge=patientRegistration.getPatientDetails().getAge();
			
			if(p.getDays()>=0 && p.getMonths()==0 &&p.getYears()==0){
				
				age=p.getDays();
				ageType="days";
				gender="CHILDREN";
			}
			else if(p.getDays()>=0 && p.getMonths()>=0 &&p.getYears()>=0){
				
				age=p.getMonths();
				if(age == 0)
				{
					age = 1;
				}
				ageType="months";
				 gender=patientRegistration.getPatientDetails().getGender();
			}
			return labServicesServiceImpl.findNewMeasures(labServices,age ,gender , ageType);
		}
		else if(service.equalsIgnoreCase("COMPLETE BLOOD PICTURE"))
		{	
			if(p.getDays()>=0 && p.getMonths()==0 &&p.getYears()==0){
				
				age=p.getDays();
				ageType="days";
				gender="CHILDREN";
			}
			else if(p.getDays()>=0&& p.getMonths()>=0 &&p.getYears()<2){
				
				age=p.getMonths();
				ageType="months";
				 gender="CHILDREN";
			}else if(p.getDays()>=0 && p.getMonths()>=0 &&p.getYears()>2 && p.getYears()<13 ){
				
				age=p.getYears();
				ageType="years";
				gender="CHILDREN";
			}else if(p.getDays()>=0 && p.getMonths()>=0 &&p.getYears()>13){
				age=p.getYears();
				ageType="years";
				gender=patientRegistration.getPatientDetails().getGender();
				
			}
			return labServicesServiceImpl.findNewMeasures(labServices,age ,gender , ageType);
		
		}else{
		String newAge=patientRegistration.getPatientDetails().getAge();
		age1=p.getYears();
		int labAge = 0;

		if (age1 <= 12) {
			labAge=(int)age1;
			gender = "CHILDREN";
		} else

		{
			labAge=(int)age1;
			gender = patientRegistration.getPatientDetails().getGender();

		}
		
		 gender=patientRegistration.getPatientDetails().getGender();
		if(labAge>=0 && labAge<=12)
		{
			gender="CHILDREN";
		}
		else if(labAge>=13 && labAge<=50 && gender.equalsIgnoreCase("MALE"))
		{
			gender="MALE";
		}
		else if(labAge>=13 && labAge<=50 && gender.equalsIgnoreCase("FEMALE"))
		{
			gender="FEMALE";
		}
		else if(labAge>=50 && labAge<=100 && gender.equalsIgnoreCase("MALE"))
		{
			gender="MALE";
		}
		else if(labAge>=50 && labAge<=100 && gender.equalsIgnoreCase("FEMALE"))
		{
			gender="FEMALE";
		}
		
		
		return labServicesServiceImpl.findMeasures(labServices, labAge,gender);
		}
		
	}

	/*
	 * displaying Lab measure Report for tracker
	 * in Lab Admin view 
	 */
	@RequestMapping(value="/measure/{regId}/{measureName}")
	public ServicePdf getPdfLink(@PathVariable String regId,@PathVariable String measureName)
	{
		List<ServicePdf> servicePdfsList= servicePdfServiceImpl.findByRegAndMeasureName(regId, measureName);
		if(servicePdfsList.isEmpty())
		{
			throw new RuntimeException("No Pdf Generated");
		}
		return servicePdfsList.get(0);
	}
	
	
	@RequestMapping(value="/servicePdf/viewFile/{id}",method=RequestMethod.GET)
	public ResponseEntity<Resource> uriLink(@PathVariable String id)
	{
		
		ServicePdf servicePaymentPdf=servicePdfServiceImpl.findBySid(id);
		
		
		 return ResponseEntity.ok()
	        		.contentType(MediaType.parseMediaType("application/pdf"))
	                .header(HttpHeaders.CONTENT_DISPOSITION,String.format("inline; filename=\"" + servicePaymentPdf.getFileName() + "\""))
	                .body(new ByteArrayResource(servicePaymentPdf.getData()));
		
	}
	
	
	// For get all registerd lab
		@RequestMapping(value="/adminLab/approve",method=RequestMethod.GET)
		public List<LaboratoryRegistration> getAll()
		{
			List<LaboratoryRegistration> laboratoryRegistrationsList=new ArrayList<>();
			List<LaboratoryRegistration> laboratoryRegistrationsInfo=laboratoryRegistrationServiceImpl.findAll();
			for(LaboratoryRegistration laboratoryRegistrations:laboratoryRegistrationsInfo)
			{
				PatientRegistration patientRegistration=laboratoryRegistrations.getLaboratoryPatientRegistration();
				User user=userServiceImpl.findOneByUserId(laboratoryRegistrations.getRefferedById());
				laboratoryRegistrations.setReg_id(patientRegistration.getRegId());
				laboratoryRegistrations.setDocName(user.getFirstName()+" "+user.getLastName());
				laboratoryRegistrations.setServName(laboratoryRegistrations.getLabServices().getServiceId());
				laboratoryRegistrations.setpType(patientRegistration.getpType());
				laboratoryRegistrations.setPatientName(patientRegistration.getPatientDetails().getTitle()+" "+
						patientRegistration.getPatientDetails().getFirstName()+" "+
						patientRegistration.getPatientDetails().getMiddleName()+" "+
						patientRegistration.getPatientDetails().getLastName());
				laboratoryRegistrationsList.add(laboratoryRegistrations);
			}
			
			
			return laboratoryRegistrationServiceImpl.findAll();
		}
		
		// To change status from not-completed to completed 
		@RequestMapping(value="/adminLab/approve",method=RequestMethod.PUT)
		public void approveAll(@RequestBody List<LaboratoryRegistration> laboratoryRegistration)
		{
			for(LaboratoryRegistration laboratoryRegistrationList:laboratoryRegistration)
			{
				LaboratoryRegistration laboratoryRegistrationInfo= laboratoryRegistrationServiceImpl.findByLabRegId(laboratoryRegistrationList.getLabRegId());
			//	laboratoryRegistrationList.setPatientLabServices(laboratoryRegistrationInfo.getPatientLabServices());
				laboratoryRegistrationList.setStatus("Completed");
				laboratoryRegistrationList.setInvoiceNo(laboratoryRegistrationList.getInvoiceNo());
				laboratoryRegistrationList.setLabServiceDate(Timestamp.valueOf(LocalDateTime.now()));
				laboratoryRegistrationList.setUserLaboratoryRegistration(laboratoryRegistrationInfo.getUserLaboratoryRegistration());
				laboratoryRegistrationList.setLaboratoryPatientRegistration(laboratoryRegistrationInfo.getLaboratoryPatientRegistration());
				laboratoryRegistrationList.setLabServices(laboratoryRegistrationInfo.getLabServices());
				laboratoryRegistrationRepository.save(laboratoryRegistrationList);
			}
		}
		
		@RequestMapping(value="/onePatient/{patId}",method=RequestMethod.GET)
		public List<LaboratoryRegistration> getServiceForPatient(@PathVariable String patId)
		{
			PatientRegistration patientRegistration=patientRegistrationServiceImpl.findByRegId(patId);
			return laboratoryRegistrationServiceImpl.findByLaboratoryPatientRegistration(patientRegistration);
		}
		
		
	
		/*
		 * Add new Service
		 */
			@RequestMapping(value = "/service", method = RequestMethod.POST)
			public ServicePdf Service(@RequestBody PatientServiceDetailsDto patientServiceDetailsDto,Principal principal) {

				
				PatientServiceDetails patientServiceDetails = new PatientServiceDetails();
				BeanUtils.copyProperties(patientServiceDetailsDto, patientServiceDetails);
				if(patientServiceDetails.getServiceName().equalsIgnoreCase("Serum Electrolytes") ||
						patientServiceDetails.getServiceName().equalsIgnoreCase("CALCIUM SERUM") ||
						patientServiceDetails.getServiceName().equalsIgnoreCase("MAGNESIUM-SERUM") ||
						patientServiceDetails.getServiceName().equalsIgnoreCase("COMPLETE STOOL EXAMINATION") ||
						patientServiceDetails.getServiceName().equalsIgnoreCase("LFT - LIVER FUNCTON TESTS") ||
						patientServiceDetails.getServiceName().equalsIgnoreCase("RENAL FUNCTION TEST(RFT)") ||
						patientServiceDetails.getServiceName().equalsIgnoreCase("ULTRA SOUND WHOLE ABDOMEN") ||
						patientServiceDetails.getServiceName().equalsIgnoreCase("2D ECHO") ||
						patientServiceDetails.getServiceName().equalsIgnoreCase("COMPLETE BLOOD PICTURE") ||
						patientServiceDetails.getServiceName().equalsIgnoreCase("COMPLETE URINE EXAMINATION") ||
						patientServiceDetails.getServiceName().equalsIgnoreCase("WIDAL TEST") ||
						patientServiceDetails.getServiceName().equalsIgnoreCase("C - REACTIVE PROTEIN (CRP)") ||
						patientServiceDetails.getServiceName().equalsIgnoreCase("MALARIA PARASITE PF&PV") ||
						patientServiceDetails.getServiceName().equalsIgnoreCase("PROTHROMBIN TIME") ||
						patientServiceDetails.getServiceName().equalsIgnoreCase("SERUM BILIRUBIN")	||
						patientServiceDetails.getServiceName().equalsIgnoreCase("DENGUE SEROLOGY") ||
						patientServiceDetails.getServiceName().equalsIgnoreCase("HBs Ag"))
				{
				
				return patientServiceDetailsServiceImpl.saveInfo(patientServiceDetails,principal);
				}
				else if(patientServiceDetails.getServiceName().equalsIgnoreCase("HCV I & II") ||
						patientServiceDetails.getServiceName().equalsIgnoreCase("ESR") ||
						patientServiceDetails.getServiceName().equalsIgnoreCase("TROPONIN - 1") ||
						patientServiceDetails.getServiceName().equalsIgnoreCase("STOOL FOR OCCULT BLOOD") ||
						patientServiceDetails.getServiceName().equalsIgnoreCase("FASTING BLOOD SUGAR") ||
						patientServiceDetails.getServiceName().equalsIgnoreCase("SEMEN ANALYSIS") ||
						patientServiceDetails.getServiceName().equalsIgnoreCase("CPKMB") ||
						patientServiceDetails.getServiceName().equalsIgnoreCase("GLUCOSE TOLERANCE TEST(GTT)") ||
						patientServiceDetails.getServiceName().equalsIgnoreCase("STOOL FOR REDUCING SUBSTANCES") ||
						patientServiceDetails.getServiceName().equalsIgnoreCase("VDRL") ||
						patientServiceDetails.getServiceName().equalsIgnoreCase("HCV"))
				{
					return patientServiceDetailsServiceImpl.saveInfo1(patientServiceDetails,principal);
				}
				else
				{
					return patientServiceDetailsServiceImpl.saveInfoOthers(patientServiceDetails,principal);
				}
				
				
			
			}
		@RequestMapping(value="/enteredMeasures/{regId}/{serviceId}",method=RequestMethod.GET)
		public List<PatientServiceDetails> enteredMeasures(@PathVariable String regId,@PathVariable String serviceId)
		{
			List<PatientServiceDetails> patientServiceDetailsList=patientServiceDetailsServiceImpl.findByPatientServiceAndPatientLabService(regId, serviceId);
			for(PatientServiceDetails patientServiceDetailsInfo:patientServiceDetailsList)
			{
				patientServiceDetailsInfo.setServiceName(patientServiceDetailsInfo.getPatientLabServiceRange().getMeasureName());
				patientServiceDetailsInfo.setMaxRange(patientServiceDetailsInfo.getPatientLabServiceRange().getMaxRange());
				patientServiceDetailsInfo.setMinRange(patientServiceDetailsInfo.getPatientLabServiceRange().getMinRange());
				patientServiceDetailsInfo.setDimension(patientServiceDetailsInfo.getPatientLabServiceRange().getParameter());
			}
			return patientServiceDetailsList;
		}
		
		/*
		 * Getting other services for outpatient 
		 */
		@RequestMapping(value="/opservice",method=RequestMethod.GET)
		public List<Object> getOpServices()
		{
			return laboratoryRegistrationServiceImpl.getOpServices();
		}
		
		/*
		 * OP Service Report
		 */
		@RequestMapping(value="/opservice",method=RequestMethod.POST)
		public SalesPaymentPdf chargeForOpServices(@RequestBody ChargeBillDto chargeBillDto,Principal principal)
		{
			SalesPaymentPdf salesPaymentPdf=null;
			String pdfBill=null;
			String regId="";
			//createdBy Security
			User userSecurity=userServiceImpl.findByUserName(principal.getName());
			String createdBy=userSecurity.getFirstName()+" "+userSecurity.getLastName();

		
		//Adding service 
		ChargeBill chargeBill=new ChargeBill();
		BeanUtils.copyProperties(chargeBillDto, chargeBill);
		regId=chargeBill.getRegId();
		PatientRegistration patientRegistration=patientRegistrationServiceImpl.findByRegId(chargeBill.getRegId());
		chargeBill.setPatRegId(patientRegistration);

		String patientName=patientRegistration.getPatientDetails().getFirstName()+"%20"+patientRegistration.getPatientDetails().getLastName();
		float amount=chargeBill.getNetAmount();	
		long mob=patientRegistration.getPatientDetails().getMobile();
		chargeBill.setUserChargeBillId(userSecurity);

		
		String newAddress="        Plot No14,15,16 & 17,Nandi Co-op.Society,"
				+ "\n                                   Main Road, Beside Navya Grand Hotel, \n                                Miyapur,Hyderabad-49,Phone:040-23046789   \n                               "
				+ "   For Appointment Contact:8019114481   "
				+ "\n                                   Email :udbhavahospitals@gmail.com ";
		
		String billNoo=null;
		String paymentMode="";
		List<RefBillDetails> refBillDetails=chargeBill.getRefBillDetails();
		System.out.println(refBillDetails.isEmpty());
		if(!refBillDetails.isEmpty())
		{
		for(RefBillDetails refBillDetailsInfo:refBillDetails)
		{
			chargeBill.setChargeBillId(chargeBillServiceImpl.getNextId());
			chargeBill.setAmount(refBillDetailsInfo.getAmount());
			chargeBill.setQuantity(refBillDetailsInfo.getQuantity());
			chargeBill.setDiscount(refBillDetailsInfo.getDiscount());
			chargeBill.setNetAmount(refBillDetailsInfo.getNetAmount());
			chargeBill.setMrp(refBillDetailsInfo.getAmount());
			chargeBill.setQuantity(1);
			List<ChargeBill> chargeBillList=chargeBillServiceImpl.findByPatRegId(patientRegistration);
			if(chargeBillList.isEmpty())
			{
				if(chargeBillRepository.findMaxBill()!=null)
				{
					billNoo=chargeBillRepository.findMaxBill();
					int intbillNoo=Integer.parseInt(billNoo.substring(2));
					intbillNoo+=1;
					billNoo="BL"+String.format("%07d",intbillNoo);
				}
				else
				{
					billNoo=chargeBillServiceImpl.getNextBillNo();
				}
				chargeBill.setBillNo(billNoo);
			}
			else
			{
				chargeBill.setBillNo(chargeBillList.get(0).getBillNo());
			}
			pdfBill=chargeBill.getBillNo();
			chargeBill.setInsertedDate(Timestamp.valueOf(LocalDateTime.now()));
			LabServices labServices=labServicesServiceImpl.findByServiceNameAndPatientType(refBillDetailsInfo.getChargeName(), patientRegistration.getpType());
			//Charge charge=chargeServiceImpl.findByName(refBillDetailsInfo.getChargeName());
			chargeBill.setServiceId(labServices);
			paymentMode=chargeBill.getPaymentType();
			chargeBill.setPaid("YES");
			//chargeBill.setPaymentType("Due");
			chargeBill.setInsertedBy(userSecurity.getUserId());
			
			chargeBillServiceImpl.save(chargeBill);
		}
		
		
		if(paymentMode.equalsIgnoreCase("Cash+Card"))
		{
			int cashAmount=0;
			int cardAmount=0;
			int chequeAmount=0;
			CashPlusCard cashPlusCardLab=new CashPlusCard();
			List<Map<String,String>> multiMode=chargeBill.getMultimode();
			for(Map<String,String> multiModeInfo:multiMode)
			{
				if(multiModeInfo.get("mode").equalsIgnoreCase("Cash"))
				{
					cashAmount=Integer.parseInt(multiModeInfo.get("amount"));
				}
				else if(multiModeInfo.get("mode").equalsIgnoreCase("Card"))
				{
					cardAmount=Integer.parseInt(multiModeInfo.get("amount"));
				}
				else if(multiModeInfo.get("mode").equalsIgnoreCase("Cheque"))
				{
					chequeAmount=Integer.parseInt(multiModeInfo.get("amount"));
				}
				
			}
			cashPlusCardLab.setBillNo(billNoo);
			cashPlusCardLab.setDescription("Op Bill");
			cashPlusCardLab.setPatientRegistrationCashCard(patientRegistration);
			cashPlusCardLab.setCardAmount(cardAmount);
			cashPlusCardLab.setBillNo(chargeBill.getBillNo());
			cashPlusCardLab.setCashAmount(cashAmount);
			cashPlusCardLab.setChequeAmount(chequeAmount);
			cashPlusCardServiceImpl.save(cashPlusCardLab);
			
			
			
		}
		}
		
		
		//*************************//
		
		
		List<ChargeBill> chargeBillList=chargeBillServiceImpl.findByPatRegId(patientRegistrationServiceImpl.findByRegId(regId));
		String billNo=null;
		 patientName=null;
		String tokenNo=null;
		
		
			for(ChargeBill chargeBillInfo:chargeBillList)
			{
	    		patientName=chargeBillInfo.getPatRegId().getPatientDetails().getFirstName()+" "+chargeBillInfo.getPatRegId().getPatientDetails().getLastName();
	    		tokenNo=chargeBillInfo.getPatRegId().getRegId().substring(2);
	    		billNo=chargeBillInfo.getBillNo();
	    	}
			
			
				Date date = Calendar.getInstance().getTime();
				DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
				String today = formatter.format(date).toString();


				Timestamp timestamp1 = patientRegistration.getRegDate();
				DateFormat dateFormat1 = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa ");

				Calendar calendar1 = Calendar.getInstance();
				calendar1.setTimeInMillis(timestamp1.getTime());

				String to = dateFormat1.format(calendar1.getTime());

				//for department
				String dpt=null;

				if(patientRegistration.getVuserD().getDepartment()!=null)
				{
					dpt=patientRegistration.getVuserD().getDepartment();
					
				}
				else
				{
					dpt="";
				}
				
				
				Set<PatientPayment> patientPayment = patientRegistration.getPatientPayment();
				
				long totalRecieptAmt=0;

				if(chargeBill!=null)
				{
				if(patientRegistration.getpType().equalsIgnoreCase("OUTPATIENT"))
				{
					
					byte[] pdfByte=null;
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
						
						document.open();
						PdfPTable table = new PdfPTable(2);

						Image img = Image.getInstance(hospitalLogo.getURL());
						img.scaleAbsolute(56, 87);
						table.setWidthPercentage(105);

						Phrase pq = new Phrase(new Chunk(img, 0, -70));

						
						pq.add(new Chunk(newAddress,redFont));
						PdfPCell cellp = new PdfPCell(pq);
						PdfPCell cell1 = new PdfPCell();

						
						// Display a date in day, month, year format
						Date dateInfo = Calendar.getInstance().getTime();
						DateFormat formatter1 = new SimpleDateFormat("dd-MMM-yyyy");
						String today1 = formatter1.format(dateInfo).toString();

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
						
						hcell4 = new PdfPCell(new Phrase(today1, redFont));
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
						
						String refBy="";
						if(patientRegistration.getPatientDetails().getvRefferalDetails()!=null)
						{
							refBy=patientRegistration.getPatientDetails().getvRefferalDetails().getRefName();
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

						
						table.addCell(cell3);

						PdfPCell cell19 = new PdfPCell();

						PdfPTable table21 = new PdfPTable(1);
						table21.setWidths(new float[] { 4f });
						table21.setSpacingBefore(10);

						PdfPCell hcell19;
						hcell19 = new PdfPCell(new Phrase("OP BILL CUM RECEIPT", headFont1));
						hcell19.setBorder(Rectangle.NO_BORDER);
						hcell19.setHorizontalAlignment(Element.ALIGN_CENTER);
						table21.addCell(hcell19);

						cell19.setFixedHeight(20f);
						cell19.setColspan(2);
						cell19.addElement(table21);
						table.addCell(cell19);

						PdfPCell cell31 = new PdfPCell();

						PdfPTable table1 = new PdfPTable(8);
						table1.setWidths(new float[] { 1f, 3f, 4f,3f, 3f, 2.5f, 2f,2.5f });

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
						hcell.setPaddingRight(30f);
						table1.addCell(hcell);

						hcell = new PdfPCell(new Phrase("Rate", redFont));
						hcell.setBorder(Rectangle.NO_BORDER);
						hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
						hcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
						hcell.setPaddingRight(35f);
						table1.addCell(hcell);
						
						hcell = new PdfPCell(new Phrase("Disc", redFont));
						hcell.setBorder(Rectangle.NO_BORDER);
						hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
						hcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
						hcell.setPaddingRight(30f);
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
						String chargeBillId=null;
						String insertDt = null;
						String cRegId=null;
						String salesDate=null;
						
						
					
				
					String chargeName=null;
						
						for (RefBillDetails refBillDetailsInfo:refBillDetails) {
							
							chargeName=refBillDetailsInfo.getChargeName();
							
							List<LabServices> labServices =labServicesServiceImpl.findByServiceName(chargeName);
							for(LabServices lab:labServices)
							{
							serviceId = lab.getServiceId();
							serviceType = lab.getServiceType();
							}
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

							cell = new PdfPCell(new Phrase(refBillDetailsInfo.getChargeName(), redFont));
							cell.setBorder(Rectangle.NO_BORDER);
							cell.setPaddingLeft(5);
							cell.setHorizontalAlignment(Element.ALIGN_LEFT);
							table1.addCell(cell);

							cell = new PdfPCell(new Phrase(serviceType, redFont));
							cell.setBorder(Rectangle.NO_BORDER);
							cell.setPaddingLeft(5);
							cell.setHorizontalAlignment(Element.ALIGN_LEFT);
							table1.addCell(cell);

							cell = new PdfPCell(new Phrase("1", redFont));
							cell.setBorder(Rectangle.NO_BORDER);
							cell.setHorizontalAlignment(Element.ALIGN_CENTER);
							cell.setPaddingRight(30);
							table1.addCell(cell);

							cell = new PdfPCell(new Phrase(String.valueOf(refBillDetailsInfo.getAmount()), redFont));
							cell.setBorder(Rectangle.NO_BORDER);
							cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
							cell.setPaddingRight(35);
							table1.addCell(cell);
							
							cell = new PdfPCell(new Phrase(String.valueOf(refBillDetailsInfo.getDiscount()), redFont));
							cell.setBorder(Rectangle.NO_BORDER);
							cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
							cell.setPaddingRight(30);
							table1.addCell(cell);

							
							cell = new PdfPCell(new Phrase(String.valueOf(refBillDetailsInfo.getNetAmount()), redFont));
							cell.setBorder(Rectangle.NO_BORDER);
							cell.setPaddingLeft(5);
							cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
							table1.addCell(cell);
							
					       total+=refBillDetailsInfo.getNetAmount();

						}
						

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
						hcell57 = new PdfPCell(new Phrase(chargeBill.getPaymentType()+" Amt.", redFont));
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

						
						PdfPTable table371 = new PdfPTable(3);
						table371.setWidths(new float[] {7f,1f, 8f });
						table371.setSpacingBefore(10);
						
						PdfPCell hcell59;
						hcell59 = new PdfPCell(new Phrase("Gross Amount In Words ", redFont));
						hcell59.setBorder(Rectangle.NO_BORDER);
						hcell59.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell59.setPaddingLeft(-50f);
						table371.addCell(hcell59);

						hcell59 = new PdfPCell(new Phrase("", headFont));
						hcell59.setBorder(Rectangle.NO_BORDER);
						hcell59.setHorizontalAlignment(Element.ALIGN_LEFT);
						table371.addCell(hcell59);
						
						hcell59 = new PdfPCell(new Phrase("(" + numberToWordsConverter.convert(total) + ")", redFont));
						hcell59.setBorder(Rectangle.NO_BORDER);
						hcell59.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell59.setPaddingLeft(-150f);
						table371.addCell(hcell59);
						

						PdfPCell hcell60;
						hcell60 = new PdfPCell(new Phrase(
								"Received Amount In Words ", redFont));

						hcell60.setBorder(Rectangle.NO_BORDER);
						hcell60.setPaddingLeft(-50f);
						hcell60.setHorizontalAlignment(Element.ALIGN_LEFT);
						table371.addCell(hcell60);

						hcell60 = new PdfPCell(new Phrase("", headFont));
						hcell60.setBorder(Rectangle.NO_BORDER);
						hcell60.setHorizontalAlignment(Element.ALIGN_RIGHT);
						table371.addCell(hcell60);
						
						hcell60 = new PdfPCell(new Phrase("(" + numberToWordsConverter.convert(total) + ")", redFont));
						hcell60.setBorder(Rectangle.NO_BORDER);
						hcell60.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell60.setPaddingLeft(-145f);
						table371.addCell(hcell60);
						
						

						cell31.setColspan(2);
						table1.setWidthPercentage(100f);
						cell31.addElement(table1);
						cell31.addElement(table37);
						cell31.addElement(table371);
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

						hcell12 = new PdfPCell(new Phrase("Created Dt      :   " + today, redFont));
						hcell12.setBorder(Rectangle.NO_BORDER);
						hcell12.setPaddingTop(10f);
						hcell12.setHorizontalAlignment(Element.ALIGN_LEFT);
						table35.addCell(hcell12);

						PdfPCell hcell13;
						hcell13 = new PdfPCell(new Phrase("Printed By     : "+createdBy, redFont));
						hcell13.setBorder(Rectangle.NO_BORDER);
						hcell13.setPaddingLeft(-50f);
						table35.addCell(hcell13);

						hcell13 = new PdfPCell(new Phrase("Printed Dt       :   " + today, redFont));
						hcell13.setBorder(Rectangle.NO_BORDER);
						//hcell13.setPaddingRight(3f);
						hcell13.setHorizontalAlignment(Element.ALIGN_LEFT);
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
						salesPaymentPdf.setFileName(regId+" Op Service Bill");
						salesPaymentPdf.setFileuri(uri);
						salesPaymentPdf.setPid(salesPaymentPdfServiceImpl.getNextId());
						salesPaymentPdf.setData(pdfByte);
						System.out.println(salesPaymentPdf);
						salesPaymentPdfServiceImpl.save(salesPaymentPdf);
					}catch (Exception e) 
					{
						Logger.error(e.getMessage());
					}
				}
				}
					return salesPaymentPdf;	
			
		}
		
		
		// Charge bill
		@RequestMapping(value="/removeLabService")
		public void addEmail(Principal p)
		{
			
			List<LaboratoryRegistration> allService=laboratoryRegistrationServiceImpl.findAll();
			
			allService.forEach((s) ->{
				s.setLabServices(null);
				laboratoryRegistrationRepository.save(s);
			});
			
		}
		
		// Charge bill
		@RequestMapping(value="/addLabService")
		public void addServiceEmail()
		{
			List<LaboratoryRegistration> allService=laboratoryRegistrationServiceImpl.findAll();
			
			allService.forEach((s) -> {
				String roomtype=s.getLaboratoryPatientRegistration().getRoomBookingDetails().get(s.getLaboratoryPatientRegistration().getRoomBookingDetails().size()-1).getRoomDetails().getRoomType();
				s.setLabServices(labServicesServiceImpl.findPriceByType(s.getServiceName(),s.getLaboratoryPatientRegistration().getpType(), (s.getLaboratoryPatientRegistration().getpType().equalsIgnoreCase("INPATIENT")) ? roomtype : "NA"));
				laboratoryRegistrationRepository.save(s);
				});
			
		}
		
		
		
	/* 
	 * deletion of particular services in ip/op billing 
	 * 
	 */
		@RequestMapping(value="/deleteService/{labRegId}",method=RequestMethod.DELETE)
		public void deleteLabservice(@PathVariable("labRegId") String labRegId,Principal principal) {
			
			String patientName=null;
			String regId=null;
			String umr=null;
			long mobile=0;
			User user=userServiceImpl.findByUserName(principal.getName());
			
			LaboratoryRegistration laboratoryRegistration=laboratoryRegistrationServiceImpl.findByLabRegId(labRegId);
			String paymentType=laboratoryRegistration.getPaymentType();
			
			
			PatientRegistration patientRegistration=laboratoryRegistration.getLaboratoryPatientRegistration();
			regId=patientRegistration.getRegId();
			PatientDetails patientDetails = patientRegistration.getPatientDetails();
		    mobile=patientDetails.getMobile();
		    umr=patientDetails.getUmr();
			if (patientDetails.getMiddleName() == null) {
			patientName = patientDetails.getTitle() + ". " + patientDetails.getFirstName()
					+ ConstantValues.ONE_SPACE_STRING + patientDetails.getLastName();
		} else {
			patientName = patientDetails.getTitle() + ". " + patientDetails.getFirstName()
					+ ConstantValues.ONE_SPACE_STRING + patientDetails.getMiddleName() + ConstantValues.ONE_SPACE_STRING
					+ patientDetails.getLastName();
		}

			 ChargeBill chargeBill=chargeBillRepository.findByLabId(laboratoryRegistration);
			 if(chargeBill!=null) {
			 chargeBill.setLabId(null);
			 chargeBill.setPatRegId(null);
			 chargeBillServiceImpl.save(chargeBill);
			 }
			 
			 
			 //for final billing
			 FinalBilling finalBilling=new FinalBilling();
			 finalBilling.setBillNo(laboratoryRegistration.getBillNo());
			 finalBilling.setBillType("ip/op cancellation");
			 if(paymentType.equalsIgnoreCase(ConstantValues.DUE)) {
				 finalBilling.setPaymentType(ConstantValues.DUE);

				 finalBilling.setDueAmount(-laboratoryRegistration.getNetAmount());  
			 }else if(!paymentType.equalsIgnoreCase(ConstantValues.DUE)) {
				 finalBilling.setCashAmount(-laboratoryRegistration.getNetAmount());
				 finalBilling.setPaymentType(ConstantValues.CASH);
			 }
			 finalBilling.setUpdatedBy(user.getUserId());
			 finalBilling.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
			 finalBilling.setInsertedDate(Timestamp.valueOf(LocalDateTime.now()));
			 finalBilling.setFinalAmountPaid(Math.round(-laboratoryRegistration.getNetAmount()));
			 finalBilling.setFinalBillUser(user);
			 finalBilling.setName(patientName);	
			 finalBilling.setRegNo((regId!=null) ? regId : null);
			 finalBilling.setMobile(mobile);
			 finalBilling.setTotalAmount(-laboratoryRegistration.getNetAmount());
			 finalBilling.setUmrNo((umr!=null) ? umr : null);
			 finalBillingServcieImpl.computeSave(finalBilling);
			 
			laboratoryRegistrationServiceImpl.deleteByLabRegId(labRegId);
			
		}
		
		
		@RequestMapping(value="/cancel/find/{billNo}")
		public List<Object> getCancelSErvices(@PathVariable("billNo") String billNo){
			
			return laboratoryRegistrationServiceImpl.getCancelSErvices(billNo);
		}
		
		
	/*
	 * deletion of op/ip bill based on billno
	 */
		@RequestMapping(value="/delete/billno/{billNo}",method=RequestMethod.DELETE)
		public void deleteByBillNo(@PathVariable("billNo") String billNo) {
			
			String regNo=null;
			
			List<LaboratoryRegistration> laboratoryRegistrations=laboratoryRegistrationRepository.findByBillNo(billNo);
			for(LaboratoryRegistration laboratoryRegistrationInfo:laboratoryRegistrations) {
				
				regNo=laboratoryRegistrationInfo.getLaboratoryPatientRegistration().getRegId();
				 ChargeBill chargeBill=chargeBillRepository.findByLabId(laboratoryRegistrationInfo);
				 if(chargeBill!=null) {
				 chargeBill.setLabId(null);
				 chargeBill.setPatRegId(null);
				 chargeBillServiceImpl.save(chargeBill);
				 }
			}
			finalBillingServcieImpl.deleteByBillTypeAndBillNoAndRegNo("Laboratory Registration", billNo, regNo);
			
			
			laboratoryRegistrationServiceImpl.deleteAllByBillNo(billNo);
		}
}
