package com.example.test.testingHMS.due.helper;

import java.io.ByteArrayOutputStream;

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
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.test.testingHMS.MoneyToWords.NumberToWordsConverter;
import com.example.test.testingHMS.bed.model.RoomBookingDetails;
import com.example.test.testingHMS.bed.model.RoomDetails;
import com.example.test.testingHMS.bill.model.ChargeBill;
import com.example.test.testingHMS.bill.repository.ChargeBillRepository;
import com.example.test.testingHMS.bill.serviceImpl.ChargeBillServiceImpl;
import com.example.test.testingHMS.finalBilling.model.FinalBilling;
import com.example.test.testingHMS.finalBilling.repository.FinalBillingRepository;
import com.example.test.testingHMS.finalBilling.serviceImpl.FinalBillingServiceImpl;
import com.example.test.testingHMS.laboratory.model.LaboratoryRegistration;
import com.example.test.testingHMS.laboratory.repository.LaboratoryRegistrationRepository;
import com.example.test.testingHMS.laboratory.serviceImpl.LaboratoryRegistrationServiceImpl;
import com.example.test.testingHMS.osp.model.OspService;
import com.example.test.testingHMS.osp.repository.OspServiceRepository;
import com.example.test.testingHMS.patient.Helper.MultiplePayment;
import com.example.test.testingHMS.patient.model.CashPlusCard;
import com.example.test.testingHMS.patient.model.PatientDetails;
import com.example.test.testingHMS.patient.model.PatientPayment;
import com.example.test.testingHMS.patient.model.PatientRegistration;
import com.example.test.testingHMS.patient.repository.PatientDetailsRepository;
import com.example.test.testingHMS.patient.repository.PatientPaymentRepository;
import com.example.test.testingHMS.patient.repository.PatientRegistrationRepository;
import com.example.test.testingHMS.patient.serviceImpl.CashPlusCardServiceImpl;
import com.example.test.testingHMS.patient.serviceImpl.PatientRegistrationServiceImpl;
import com.example.test.testingHMS.pharmacist.model.MedicineDetails;
import com.example.test.testingHMS.pharmacist.model.MedicineProcurement;
import com.example.test.testingHMS.pharmacist.model.Sales;
import com.example.test.testingHMS.pharmacist.model.SalesPaymentPdf;
import com.example.test.testingHMS.pharmacist.model.SalesReturn;
import com.example.test.testingHMS.pharmacist.repository.MedicineDetailsRepository;
import com.example.test.testingHMS.pharmacist.repository.SalesRepository;
import com.example.test.testingHMS.pharmacist.repository.SalesReturnRepository;
import com.example.test.testingHMS.pharmacist.serviceImpl.MedicineDetailsServiceImpl;
import com.example.test.testingHMS.pharmacist.serviceImpl.MedicineProcurementServiceImpl;
import com.example.test.testingHMS.pharmacist.serviceImpl.MedicineQuantityServiceImpl;
import com.example.test.testingHMS.pharmacist.serviceImpl.SalesPaymentPdfServiceImpl;
import com.example.test.testingHMS.pharmacist.serviceImpl.SalesServiceImpl;
import com.example.test.testingHMS.pharmacyShopDetails.model.PharmacyShopDetails;
import com.example.test.testingHMS.pharmacyShopDetails.repository.PharmacyShopDetailsRepository;
import com.example.test.testingHMS.user.model.User;
import com.example.test.testingHMS.user.repository.UserRepository;
import com.example.test.testingHMS.user.serviceImpl.UserServiceImpl;
import com.example.test.testingHMS.utils.ConstantValues;
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
import com.itextpdf.text.pdf.draw.VerticalPositionMark;

@Component
public class DueServiceImpl {
	@Autowired
	NumberToWordsConverter numberToWordsConverter;
	
	@Value("${hospital.logo}")
	private Resource hospitalLogo;
	
	@Autowired
	FinalBillingRepository finalBillingRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	LaboratoryRegistrationServiceImpl laboratoryRegistrationServiceImpl;

	@Autowired
	LaboratoryRegistrationRepository laboratoryRegistrationRepository;

	/*
	 * @Autowired PatientSalesRepository patientSalesRepository;
	 * 
	 */	@Autowired
	PharmacyShopDetailsRepository pharmacyShopDetailsRepository;

	@Autowired
	MedicineDetailsServiceImpl medicineDetailsServiceImpl;

	@Autowired
	SalesPaymentPdfServiceImpl salesPaymentPdfServiceImpl;

	@Autowired
	PatientRegistrationRepository patientRegistrationRepository;

	@Autowired
	SalesRepository salesRepository;

	@Autowired
	SalesReturnRepository salesReturnRepository;

	@Autowired
	ResourceLoader resourceLoader;

	@Autowired
	MedicineDetailsRepository medicineDetailsRepository;

	@Autowired
	PatientRegistrationServiceImpl patientRegistrationServiceImpl;

	@Autowired
	MedicineProcurementServiceImpl medicineProcurementServiceImpl;

	@Autowired
	SalesServiceImpl salesServiceImpl;

	@Autowired
	UserServiceImpl userServiceImpl;

	@Autowired
	MedicineQuantityServiceImpl medicineQuantityServiceImpl;

	@Autowired
	PatientDetailsRepository patientDetailsRepository;

	@Autowired
	ChargeBillRepository chargeBillRepository;

	@Autowired
	ChargeBillServiceImpl chargeBillServiceImpl;

	@Autowired
	CashPlusCardServiceImpl cashPlusCardServiceImpl;

	@Autowired
	FinalBillingServiceImpl finalBillingServcieImpl;

	@Autowired
	OspServiceRepository ospServiceRepository;
	@Autowired
	PatientPaymentRepository patientPaymentRepository;
	
	
	private static String EMPTY_STRING="";

	@Transactional
	public SalesPaymentPdf payDue(DueHelper dueHelper, String billNo, Principal principal) {

		float finalCash = 0; // final billing
		float finalCard = 0; // final billing
		float finalCheque = 0; // final billing
		float finalDue = 0; // final billing
		float finalNetAmount = 0;
		float totalAmount=0;
		float discount=0;
		

		String payCash=null;
		String payCard=null;
		String payDue=null;
		String payCheque=null;
        String paymentType=null;
        String paid=null;
        String referenceNumber=null;

		// CreatedBy (Security)
		User userSecurity = userServiceImpl.findByUserName(principal.getName());
		String createdBy = userSecurity.getFirstName() + " " + userSecurity.getLastName();
		String createdid = userSecurity.getUserId();

		SalesPaymentPdf salesPaymentPdf = new SalesPaymentPdf();

		//String modeOfPayment = dueHelper.getMode();
		String dueType = dueHelper.getDueFor();
		String regNo = null;
		List<Object> list = new ArrayList<>();
		
List<MultiplePayment> multiplePayment=dueHelper.getMultiplePayment();
		
		for(MultiplePayment multiplePaymentInfo:multiplePayment) {
			
			if (multiplePaymentInfo.getPayType().equalsIgnoreCase(ConstantValues.CARD)
					|| multiplePaymentInfo.getPayType().equalsIgnoreCase("Credit Card")
					|| multiplePaymentInfo.getPayType().equalsIgnoreCase("Debit Card")
					|| multiplePaymentInfo.getPayType().equalsIgnoreCase(ConstantValues.CASH_PLUS_CARD)) {
				
				referenceNumber=dueHelper.getReferenceNumber();
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
			}
			
		}
		
		//for paytype and paid condition
		if(payDue!=null&&payCash==null&&payCard==null) {
			paid=ConstantValues.NO;
			paymentType=ConstantValues.DUE;
		}else if(payCash!=null&&payDue==null&&payCard==null){
			paid=ConstantValues.YES;
			paymentType=ConstantValues.CASH;
			
		}else if(payCard!=null&&payDue==null&&payCash==null) {
			paid=ConstantValues.YES;
			paymentType=ConstantValues.CARD;
		}else {
			paid=ConstantValues.PARTLY_PAID;
			paymentType=ConstantValues.MULTIPLE_PAYMENT;

			
		}

		System.out.println(paymentType);
		String modeOfPayment=paymentType;
				PatientRegistration patientRegistration = null;
		if (dueType.equalsIgnoreCase("Pharmacy")) {

			List<Sales> sales1 = salesRepository.findByBillNo(billNo);

			List<Map<String, String>> show = new ArrayList<>();
			String medName = null;
			String batch = null;
			String mrp = null;
			long quantity = 0;
			long returnQuantity = 0;
			long netQuantity = 0;
			String refNoSales = null;
			float saleAmt = 0;
			float returnAmt = 0;
			long mobile = 0;
			float totalNetAmt = 0;
			
			String patientName="";

			for (Sales salesInfo : sales1) {

				Map<String, String> map = new HashMap<>();

					regNo = (salesInfo.getPatientRegistration() != null) ? salesInfo.getPatientRegistration().getRegId()
							: "";
					patientRegistration = patientRegistrationServiceImpl.findByRegId(regNo);
					

					salesInfo.setReferenceNumber(referenceNumber);
					refNoSales = salesInfo.getReferenceNumber();


					// Patient Sales
					if (patientRegistration != null ) {
						

						if (patientRegistration.isBlockedStatus()) {
							throw new RuntimeException("Payment for this patinet is blocked !");
						}
					/*
					 * PatientSales patientSales = patientSalesRepository.findOneBill(billNo,
					 * salesInfo.getMedicineName(), salesInfo.getBatchNo());
					 * 
					 * patientSales.setPaid(paid); patientSales.setPaymentType(modeOfPayment);
					 * 
					 * patientSales.setUpdatedBy(createdid); patientSales.setUpdatedDate(new
					 * Timestamp(System.currentTimeMillis()));
					 * 
					 */
						PatientDetails patient = patientRegistration.getPatientDetails();
						String pfn = null;
						String pmn = null;
						String pln = null;
						if (patient.getFirstName() == null) {
							pfn = " ";
						} else {
							pfn = patient.getFirstName();
						}
						if (patient.getMiddleName() == null) {
							pmn = "";
						} else {
							pmn = patient.getMiddleName();
						}
						if (patient.getLastName() == null) {
							pln = " ";
						} else {
							pln = patient.getLastName();
						}
						if (pmn.equalsIgnoreCase("")) {
							patientName = patient.getTitle() + ". " + patient.getFirstName() + " " + patient.getLastName();
						} else {
							patientName = patient.getTitle() + ". " + patient.getFirstName() + " " + patient.getMiddleName() + " "
									+ patient.getLastName();
						}



					}else {
						patientName=sales1.get(0).getName();
						
					}
					
					
					  if (patientRegistration != null &&
					 ( patientRegistration.getpType().equalsIgnoreCase(ConstantValues.INPATIENT)||patientRegistration.getpType().equalsIgnoreCase(ConstantValues.DAYCARE)||patientRegistration.getpType().equalsIgnoreCase(ConstantValues.EMERGENCY))) 
					  { // ChargeBill
						
						 
						ChargeBill chargeBillSales = chargeBillRepository.findBySaleId(salesInfo);
						 if(chargeBillSales!=null) {
						chargeBillSales.setPaymentType(modeOfPayment);
						chargeBillSales.setPaid(paid);
						chargeBillSales.setUpdatedBy(createdid);
						chargeBillSales.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
						  }
					  }
					salesInfo.setPaid(paid);
					salesInfo.setUpdatedBy(createdid);
					salesInfo.setPaymentType(modeOfPayment);
					salesInfo.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
					mobile = salesInfo.getMobileNo();
					medName = salesInfo.getMedicineName();
					batch = salesInfo.getBatchNo();
					netQuantity = salesInfo.getQuantity();
					mrp = String.valueOf(salesInfo.getMrp());

					List<SalesReturn> salesReturns = salesReturnRepository.findByBillNoAndMedicineName(billNo, medName);

					// List<SalesReturn>
					// salesReturns=salesReturnRepository.findByName(medicineName);
					if (!salesReturns.isEmpty()) {
						for (SalesReturn salesReturnsinfo : salesReturns) {

							returnQuantity = returnQuantity + salesReturnsinfo.getQuantity();
							returnAmt = returnAmt + salesReturnsinfo.getAmount();

						}
					} else {

						returnQuantity = 0;
					}

					quantity = netQuantity + returnQuantity;

					map.put("medicineName", medName);
					map.put("batch", batch);
					map.put("quantity", String.valueOf(quantity));
					map.put("mrp", mrp);

					System.out.println(netQuantity);
					map.put("netQty", String.valueOf(netQuantity));
					map.put("saleValue", String.valueOf(salesInfo.getAmount()));
					map.put("returnqty", String.valueOf(returnQuantity));
					returnQuantity = 0;
					show.add(map);
					saleAmt = saleAmt + salesInfo.getAmount();
				}
			finalNetAmount = dueHelper.getNetAmount();
			totalAmount=dueHelper.getAmount();
			discount=dueHelper.getDiscount();
			

			
			List<FinalBilling> finalBillingUpdate=finalBillingRepository.changeStatusDueBills("Sales", ConstantValues.SALES_DUE, billNo);
					
			for(FinalBilling finalBillingUpdateInfo:finalBillingUpdate) {
				finalBillingUpdateInfo.setDueStatus(ConstantValues.NO);
			}
			// Final Billing
			FinalBilling finalBilling = new FinalBilling();
			finalBilling.setBillNo(billNo);
			if(finalDue!=0) {
				finalBilling.setDueStatus(ConstantValues.YES);
			}else {
				finalBilling.setDueStatus(ConstantValues.NO);
			}
			
			finalBilling.setBillType(ConstantValues.SALES_DUE);
			finalBilling.setCardAmount(finalCard);
			finalBilling.setInsertedDate(Timestamp.valueOf(LocalDateTime.now()));
			finalBilling.setCashAmount(finalCash);
			finalBilling.setChequeAmount(finalCheque);
			finalBilling.setDueAmount(finalDue);
			finalBilling.setFinalAmountPaid(Math.round(finalNetAmount));
			finalBilling.setFinalBillUser(userSecurity);
			finalBilling.setUpdatedBy(userSecurity.getUserId());
			finalBilling.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
			finalBilling.setName(patientName);
			finalBilling.setMobile(mobile);
			finalBilling.setRegNo((regNo.length() != 0) ? regNo : null);
			finalBilling.setPaymentType(modeOfPayment);
			finalBilling.setTotalAmount(totalAmount);
			finalBilling.setDiscAmount(discount);
			finalBilling
					.setUmrNo((patientRegistration != null) ? patientRegistration.getPatientDetails().getUmr() : null);
			finalBillingServcieImpl.computeSave(finalBilling);

			totalNetAmt = saleAmt + returnAmt;

			salesPaymentPdf = new SalesPaymentPdf();

			String roundOff = null;

			byte[] pdfByte = null;
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			Document document = new Document(PageSize.A4_LANDSCAPE);
			try {

				Resource fileResourcee = resourceLoader.getResource(
						ConstantValues.IMAGE_PNG_CLASSPATH);
				Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
				Font redFont2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
				Font redFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
				Font redFont3 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
				PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);

				document.open();
				PdfPTable table = new PdfPTable(2);

			Image img = Image.getInstance(hospitalLogo.getURL());
				img.scaleAbsolute(ConstantValues.IMAGE_ABSOLUTE_INTIAL_POSITION, ConstantValues.IMAGE_ABSOLUTE_FINAL_POSITION);
				table.setWidthPercentage(ConstantValues.TABLE_SET_WIDTH_PERECENTAGE);

				Phrase pq = new Phrase(new Chunk(img, ConstantValues.IMAGE_SET_INTIAL_POSITION, ConstantValues.IMAGE_SET_FINAL_POSITION));
                pq.add(new Chunk(ConstantValues.PHARAMACY_RECEIPT, redFont));

				PdfPCell cellp = new PdfPCell(pq);
				PdfPCell cell1 = new PdfPCell();

				// for header bold
				PdfPTable table96 = new PdfPTable(1);
				table96.setWidths(new float[] { 5f });
				table96.setSpacingBefore(10);

				PdfPCell hcell96;
				hcell96 = new PdfPCell(new Phrase(ConstantValues.PHARMACY_NAME, redFont1));
				hcell96.setBorder(Rectangle.NO_BORDER);
				hcell96.setHorizontalAlignment(Element.ALIGN_CENTER);
				hcell96.setPaddingLeft(30f);

				table96.addCell(hcell96);
				cell1.addElement(table96);

				cell1.setFixedHeight(110f);
				cell1.addElement(pq);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(cell1);

				PdfPCell cell0 = new PdfPCell();

				PdfPTable table2 = new PdfPTable(3);
				table2.setWidths(new float[] { 5f, 1f, 5f });
				table2.setSpacingBefore(10);

				PdfPCell hcell1;
				hcell1 = new PdfPCell(new Phrase("Bill#", redFont));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(-15f);
				table2.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase(":", redFont));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(-35f);
				table2.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase(billNo, redFont));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(-25f);
				table2.addCell(hcell1);

				// Display a date in day, month, year format
				Date date = Calendar.getInstance().getTime();
				DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
				String today = formatter.format(date).toString();

				String expdate = null;

				PdfPCell hcel123;
				hcel123 = new PdfPCell(new Phrase("Bill Date", redFont));
				hcel123.setBorder(Rectangle.NO_BORDER);
				hcel123.setPaddingLeft(-15f);

				hcel123.setHorizontalAlignment(Element.ALIGN_LEFT);
				table2.addCell(hcel123);

				hcel123 = new PdfPCell(new Phrase(":", redFont));
				hcel123.setBorder(Rectangle.NO_BORDER);
				hcel123.setPaddingLeft(-35f);
				hcel123.setHorizontalAlignment(Element.ALIGN_LEFT);
				table2.addCell(hcel123);

				hcel123 = new PdfPCell(new Phrase(today, redFont));
				hcel123.setBorder(Rectangle.NO_BORDER);
				hcel123.setPaddingLeft(-25f);
				hcel123.setHorizontalAlignment(Element.ALIGN_LEFT);
				table2.addCell(hcel123);
				if(patientRegistration!=null) {
				PdfPCell hcell18;
				hcell18 = new PdfPCell(new Phrase("Patient Name", redFont));
				hcell18.setBorder(Rectangle.NO_BORDER);
				hcell18.setPaddingLeft(-15f);
				table2.addCell(hcell18);

				hcell18 = new PdfPCell(new Phrase(":", redFont));
				hcell18.setBorder(Rectangle.NO_BORDER);
				hcell18.setPaddingLeft(-35f);
				table2.addCell(hcell18);

				hcell18 = new PdfPCell(new Phrase(patientName, redFont));
				hcell18.setBorder(Rectangle.NO_BORDER);
				hcell18.setPaddingLeft(-25f);
				table2.addCell(hcell18);

				PdfPCell hcel;

				hcel = new PdfPCell(new Phrase("UMR No", redFont));
				hcel.setBorder(Rectangle.NO_BORDER);
				hcel.setPaddingLeft(-15f);
				table2.addCell(hcel);

				hcel = new PdfPCell(new Phrase(":", redFont));
				hcel.setBorder(Rectangle.NO_BORDER);
				hcel.setPaddingLeft(-35f);
				table2.addCell(hcel);

				hcel = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getUmr(), redFont));
				hcel.setBorder(Rectangle.NO_BORDER);
				hcel.setPaddingLeft(-25f);
				table2.addCell(hcel);

				PdfPCell hcel11;
				hcel11 = new PdfPCell(new Phrase("P.RegNo", redFont));
				hcel11.setBorder(Rectangle.NO_BORDER);
				hcel11.setPaddingLeft(-15f);
				hcel11.setHorizontalAlignment(Element.ALIGN_LEFT);
				table2.addCell(hcel11);

				hcel11 = new PdfPCell(new Phrase(":", redFont));
				hcel11.setBorder(Rectangle.NO_BORDER);
				hcel11.setPaddingLeft(-35f);
				hcel11.setHorizontalAlignment(Element.ALIGN_LEFT);
				table2.addCell(hcel11);

				hcel11 = new PdfPCell(new Phrase(patientRegistration.getRegId(), redFont));
				hcel11.setBorder(Rectangle.NO_BORDER);
				hcel11.setPaddingLeft(-25f);
				hcel11.setHorizontalAlignment(Element.ALIGN_LEFT);
				table2.addCell(hcel11);

				PdfPCell hcel1;

				hcel1 = new PdfPCell(new Phrase("Doctor Name", redFont));
				hcel1.setBorder(Rectangle.NO_BORDER);
				hcel1.setPaddingLeft(-15f);
				table2.addCell(hcel1);

				hcel1 = new PdfPCell(new Phrase(":", redFont));
				hcel1.setBorder(Rectangle.NO_BORDER);
				hcel1.setPaddingLeft(-35f);
				table2.addCell(hcel1);

				hcel1 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getConsultant(), redFont));
				hcel1.setBorder(Rectangle.NO_BORDER);
				hcel1.setPaddingLeft(-25f);
				table2.addCell(hcel1);
}else {
	PdfPCell hcell18;
	hcell18 = new PdfPCell(new Phrase("Patient Name", redFont));
	hcell18.setBorder(Rectangle.NO_BORDER);
	hcell18.setPaddingLeft(-15f);
	table2.addCell(hcell18);

	hcell18 = new PdfPCell(new Phrase(":", redFont));
	hcell18.setBorder(Rectangle.NO_BORDER);
	hcell18.setPaddingLeft(-35f);
	table2.addCell(hcell18);

	hcell18 = new PdfPCell(new Phrase(sales1.get(0).getName(), redFont));
	hcell18.setBorder(Rectangle.NO_BORDER);
	hcell18.setPaddingLeft(-25f);
	table2.addCell(hcell18);

	
	
}
				cell0.setFixedHeight(100f);
				cell0.setColspan(2);
				cell0.addElement(table2);
				table.addCell(cell0);

				PdfPCell cell19 = new PdfPCell();

				PdfPTable table21 = new PdfPTable(3);
				table21.setWidths(new float[] { 2f, 5f, 2f });
				table21.setSpacingBefore(10);

				PdfPCell hcell15;
				hcell15 = new PdfPCell(new Phrase("", redFont));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-70f);
				table21.addCell(hcell15);

				hcell15 = new PdfPCell(new Phrase("Pharmacy Settled Receipt", redFont3));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(35);
				hcell15.setHorizontalAlignment(Element.ALIGN_CENTER);
				table21.addCell(hcell15);

				hcell15 = new PdfPCell(new Phrase("", redFont));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingRight(-40f);
				hcell15.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table21.addCell(hcell15);

				cell19.setFixedHeight(20f);
				cell19.setColspan(2);
				cell19.addElement(table21);
				table.addCell(cell19);

				PdfPCell cell3 = new PdfPCell();

				PdfPTable table1 = new PdfPTable(10);
				table1.setWidths(new float[] { 1.5f, 5f, 5f, 2f, 3f, 2f, 2f, 2f, 2f, 2f });

				table1.setSpacingBefore(10);

				PdfPCell hcell;
				hcell = new PdfPCell(new Phrase("S.No", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("Item Name", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("Manf Name", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("Batch No", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("Exp Date", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("Qty", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("Ret Qty", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("Net Qty", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("MRP", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("Sale Value", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table1.addCell(hcell);
				int count = 0;

				float totalsaleValue = 0;

				for (Map<String, String> showInfo : show) {

					MedicineDetails medicineDetails1 = medicineDetailsServiceImpl
							.findByName(showInfo.get("medicineName"));
					List<MedicineProcurement> medicineProcurement = medicineProcurementServiceImpl
							.findByBatchAndMedicine(showInfo.get("batch"), medicineDetails1.getMedicineId());

					PdfPCell cell;

					cell = new PdfPCell(new Phrase(String.valueOf(count = count + 1), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table1.addCell(cell);

					cell = new PdfPCell(new Phrase(showInfo.get("medicineName"), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(-5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					// cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					table1.addCell(cell);

					cell = new PdfPCell(new Phrase(medicineDetails1.getManufacturer(), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					// cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					table1.addCell(cell);

					cell = new PdfPCell(new Phrase(showInfo.get("batch"), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					// cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					table1.addCell(cell);

					// for convert db date to dmy format
try{
					expdate = medicineProcurement.get(medicineProcurement.size() - 1).getExpDate().toString()
							.substring(0, 10);
					SimpleDateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd");
					SimpleDateFormat toFormat = new SimpleDateFormat("dd-MM-yyyy");
					expdate = toFormat.format(fromFormat.parse(expdate));

					System.out.println(expdate);
}catch (Exception e) {
	e.printStackTrace();
}
					cell = new PdfPCell(new Phrase("", redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table1.addCell(cell);

					cell = new PdfPCell(new Phrase(showInfo.get("quantity"), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table1.addCell(cell);
					cell = new PdfPCell(new Phrase(showInfo.get("returnqty"), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table1.addCell(cell);
					System.out.println("--------------inpdf----------");
					System.out.println(netQuantity);

					cell = new PdfPCell(new Phrase(showInfo.get("netQty"), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table1.addCell(cell);

					cell = new PdfPCell(new Phrase(showInfo.get("mrp"), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table1.addCell(cell);

					// float amount = Long.parseLong(showInfo.get("netQty")) *
					// Float.parseFloat(showInfo.get("mrp"));

					cell = new PdfPCell(new Phrase(String.valueOf(showInfo.get("saleValue")), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table1.addCell(cell);

					totalsaleValue = totalsaleValue + Float.parseFloat(showInfo.get("saleValue"));

				}
				cell3.setColspan(2);
				table1.setWidthPercentage(100f);
				cell3.addElement(table1);
				table.addCell(cell3);

				PdfPCell cell4 = new PdfPCell();

				PdfPTable table4 = new PdfPTable(6);
				table4.setWidths(new float[] { 5f, 1f, 5f, 9f, 1f, 3f });
				table4.setSpacingBefore(10);

				// int ttl=(int)Math.round(total);
				PdfPCell hcell2;
				if(finalCash!=0) {
				hcell2 = new PdfPCell(new Phrase("Cash Amt", redFont));
				hcell2.setBorder(Rectangle.NO_BORDER);
				hcell2.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell2.setPaddingLeft(-50f);
				table4.addCell(hcell2);

				hcell2 = new PdfPCell(new Phrase(":", redFont));
				hcell2.setBorder(Rectangle.NO_BORDER);
				hcell2.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell2.setPaddingLeft(-50f);
				table4.addCell(hcell2);
				// numberToWordsConverter.convert(ttl)
				hcell2 = new PdfPCell(new Phrase(String.valueOf(finalCash), redFont));
				hcell2.setBorder(Rectangle.NO_BORDER);
				hcell2.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell2.setPaddingLeft(-60f);
				table4.addCell(hcell2);
				}else {
					hcell2 = new PdfPCell(new Phrase("", redFont));
					hcell2.setBorder(Rectangle.NO_BORDER);
					hcell2.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell2.setPaddingLeft(-50f);
					table4.addCell(hcell2);

					hcell2 = new PdfPCell(new Phrase("", redFont));
					hcell2.setBorder(Rectangle.NO_BORDER);
					hcell2.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell2.setPaddingLeft(-50f);
					table4.addCell(hcell2);
					// numberToWordsConverter.convert(ttl)
					hcell2 = new PdfPCell(new Phrase(" ", redFont));
					hcell2.setBorder(Rectangle.NO_BORDER);
					hcell2.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell2.setPaddingLeft(-60f);
					table4.addCell(hcell2);

					
				}
				hcell2 = new PdfPCell(new Phrase("Total Sale Value", redFont));
				hcell2.setBorder(Rectangle.NO_BORDER);
				hcell2.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell2.setPaddingLeft(85f);
				table4.addCell(hcell2);

				hcell2 = new PdfPCell(new Phrase(":", redFont));
				hcell2.setBorder(Rectangle.NO_BORDER);
				hcell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell2.setPaddingRight(-30f);
				table4.addCell(hcell2);
				// (Math.round(total*100.0)/100.0
				hcell2 = new PdfPCell(
						new Phrase(String.valueOf((Math.round(finalNetAmount * 100.0) / 100.0)), redFont));
				hcell2.setBorder(Rectangle.NO_BORDER);
				hcell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell2.setPaddingRight(-40f);
				table4.addCell(hcell2);

				PdfPCell hcell04;
				if(finalCard!=0) {
				hcell04 = new PdfPCell(new Phrase("Card Amt",redFont));
				hcell04.setBorder(Rectangle.NO_BORDER);
				hcell04.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell04.setPaddingLeft(-50f);
				table4.addCell(hcell04);

				hcell04 = new PdfPCell(new Phrase(":",redFont));
				hcell04.setBorder(Rectangle.NO_BORDER);
				hcell04.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell04.setPaddingLeft(-50f);
				table4.addCell(hcell04);

				hcell04 = new PdfPCell(new Phrase(String.valueOf(finalCard),redFont));
				hcell04.setBorder(Rectangle.NO_BORDER);
				hcell04.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell04.setPaddingLeft(-60f);
				table4.addCell(hcell04);
				}else {
					hcell04 = new PdfPCell(new Phrase(""));
					hcell04.setBorder(Rectangle.NO_BORDER);
					hcell04.setHorizontalAlignment(Element.ALIGN_LEFT);
					table4.addCell(hcell04);

					hcell04 = new PdfPCell(new Phrase(""));
					hcell04.setBorder(Rectangle.NO_BORDER);
					hcell04.setHorizontalAlignment(Element.ALIGN_LEFT);
					table4.addCell(hcell04);

					hcell04 = new PdfPCell(new Phrase(""));
					hcell04.setBorder(Rectangle.NO_BORDER);
					hcell04.setHorizontalAlignment(Element.ALIGN_LEFT);
					table4.addCell(hcell04);
					
				}
				hcell04 = new PdfPCell(new Phrase("Net Amt", redFont));
				hcell04.setBorder(Rectangle.NO_BORDER);
				hcell04.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell04.setPaddingLeft(85f);
				table4.addCell(hcell04);

				hcell04 = new PdfPCell(new Phrase(":", redFont));
				hcell04.setBorder(Rectangle.NO_BORDER);
				hcell04.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell04.setPaddingRight(-30f);
				table4.addCell(hcell04);

				hcell04 = new PdfPCell(new Phrase(String.valueOf((Math.round(finalNetAmount * 100.0) / 100.0)), redFont));
				hcell04.setBorder(Rectangle.NO_BORDER);
				hcell04.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell04.setPaddingRight(-40f);
				table4.addCell(hcell04);

				PdfPCell hcell4;
				if(finalDue!=0) {
				hcell4 = new PdfPCell(new Phrase("Due Amt",redFont));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell4.setPaddingLeft(-50f);
				table4.addCell(hcell4);

				hcell4 = new PdfPCell(new Phrase(":",redFont));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell4.setPaddingLeft(-50f);
				table4.addCell(hcell4);

				hcell4 = new PdfPCell(new Phrase(String.valueOf(finalDue),redFont));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell4.setPaddingLeft(-60f);
				table4.addCell(hcell4);
				}else {
					hcell4 = new PdfPCell(new Phrase(""));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table4.addCell(hcell4);

					hcell4 = new PdfPCell(new Phrase(""));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table4.addCell(hcell4);

					hcell4 = new PdfPCell(new Phrase(""));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table4.addCell(hcell4);
	
					
				}
				hcell4 = new PdfPCell(new Phrase("Due Recieved", redFont));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell4.setPaddingLeft(85f);
				table4.addCell(hcell4);

				hcell4 = new PdfPCell(new Phrase(":", redFont));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell4.setPaddingRight(-30f);
				table4.addCell(hcell4);

				// Math.round(total)
				hcell4 = new PdfPCell(new Phrase(String.valueOf("0.0"), redFont));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell4.setPaddingRight(-40f);
				table4.addCell(hcell4);

				PdfPCell hcell9;
				hcell9 = new PdfPCell(new Phrase(""));
				hcell9.setBorder(Rectangle.NO_BORDER);
				hcell9.setHorizontalAlignment(Element.ALIGN_LEFT);
				table4.addCell(hcell9);

				hcell9 = new PdfPCell(new Phrase(""));
				hcell9.setBorder(Rectangle.NO_BORDER);
				hcell9.setHorizontalAlignment(Element.ALIGN_LEFT);
				table4.addCell(hcell9);

				hcell9 = new PdfPCell(new Phrase(""));
				hcell9.setBorder(Rectangle.NO_BORDER);
				hcell9.setHorizontalAlignment(Element.ALIGN_LEFT);
				table4.addCell(hcell9);

				hcell9 = new PdfPCell(new Phrase("Due Amt", redFont));
				hcell9.setBorder(Rectangle.NO_BORDER);
				hcell9.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell9.setPaddingLeft(85f);
				table4.addCell(hcell9);

				hcell9 = new PdfPCell(new Phrase(":", redFont));
				hcell9.setBorder(Rectangle.NO_BORDER);
				hcell9.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell9.setPaddingRight(-30f);
				table4.addCell(hcell9);
				// Math.round(total)
				hcell9 = new PdfPCell(new Phrase(String.valueOf("0.0"), redFont));
				hcell9.setBorder(Rectangle.NO_BORDER);
				hcell9.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell9.setPaddingRight(-40f);
				table4.addCell(hcell9);
				cell4.setFixedHeight(65f);
				cell4.setColspan(2);
				cell4.addElement(table4);
				table.addCell(cell4);

				// for new row

				PdfPCell cell33 = new PdfPCell();

				PdfPTable table13 = new PdfPTable(5);
				table13.setWidths(new float[] { 4f, 4f, 4f, 1f, 2f });

				table13.setSpacingBefore(10);

				PdfPCell hcell33;
				hcell33 = new PdfPCell(new Phrase("", redFont1));
				hcell33.setBorder(Rectangle.NO_BORDER);
				hcell33.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell33.setPaddingLeft(10f);
				table13.addCell(hcell33);

				hcell33 = new PdfPCell(new Phrase("", redFont1));
				hcell33.setBorder(Rectangle.NO_BORDER);
				hcell33.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell33.setPaddingLeft(35f);
				table13.addCell(hcell33);

				hcell33 = new PdfPCell(new Phrase("Total Reciept Amt", redFont1));
				hcell33.setBorder(Rectangle.NO_BORDER);
				hcell33.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell33.setPaddingLeft(55f);
				table13.addCell(hcell33);

				hcell33 = new PdfPCell(new Phrase(":", redFont1));
				hcell33.setBorder(Rectangle.NO_BORDER);
				hcell33.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell33.setPaddingLeft(24f);
				table13.addCell(hcell33);

				hcell33 = new PdfPCell(new Phrase(String.valueOf((Math.round(totalNetAmt * 100.0) / 100.0)), redFont1));
				hcell33.setBorder(Rectangle.NO_BORDER);
				hcell33.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell33.setPaddingRight(15f);
				table13.addCell(hcell33);

				PdfPCell cell34 = new PdfPCell();

				PdfPTable table15 = new PdfPTable(5);
				table15.setWidths(new float[] { 2f, 3f, 3f, 3f, 3f });

				table15.setSpacingBefore(10);

				PdfPCell hcell34;
				hcell34 = new PdfPCell(new Phrase("Pay Mode", redFont1));
				hcell34.setBorder(Rectangle.NO_BORDER);
				hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell34.setPaddingLeft(10f);
				table15.addCell(hcell34);

				hcell34 = new PdfPCell(new Phrase("Amount", redFont1));
				hcell34.setBorder(Rectangle.NO_BORDER);
				hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell34.setPaddingLeft(35f);
				table15.addCell(hcell34);

				hcell34 = new PdfPCell(new Phrase("Card#", redFont1));
				hcell34.setBorder(Rectangle.NO_BORDER);
				hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell34.setPaddingLeft(40f);
				table15.addCell(hcell34);

				hcell34 = new PdfPCell(new Phrase("Bank Name", redFont1));
				hcell34.setBorder(Rectangle.NO_BORDER);
				hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell34.setPaddingLeft(40f);
				table15.addCell(hcell34);

				hcell34 = new PdfPCell(new Phrase("Exp Date", redFont1));
				hcell34.setBorder(Rectangle.NO_BORDER);
				hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell34.setPaddingLeft(50f);
				table15.addCell(hcell34);

				PdfPCell hcell35;
				hcell35 = new PdfPCell(new Phrase(modeOfPayment, redFont2));
				hcell35.setBorder(Rectangle.NO_BORDER);
				hcell35.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell35.setPaddingLeft(10f);
				table15.addCell(hcell35);

				hcell35 = new PdfPCell(new Phrase(String.valueOf(Math.round(finalNetAmount)), redFont2));
				hcell35.setBorder(Rectangle.NO_BORDER);
				hcell35.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell35.setPaddingLeft(35f);
				table15.addCell(hcell35);
				if (modeOfPayment.equalsIgnoreCase("card") || modeOfPayment.equalsIgnoreCase("cash+card")) {
					hcell35 = new PdfPCell(new Phrase(refNoSales, redFont2));
					hcell35.setBorder(Rectangle.NO_BORDER);
					hcell35.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell35.setPaddingLeft(40f);
					table15.addCell(hcell35);
				} else {
					hcell35 = new PdfPCell(new Phrase("", redFont2));
					hcell35.setBorder(Rectangle.NO_BORDER);
					hcell35.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell35.setPaddingLeft(40f);
					table15.addCell(hcell35);

				}
				hcell35 = new PdfPCell(new Phrase("", redFont1));
				hcell35.setBorder(Rectangle.NO_BORDER);
				hcell35.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell35.setPaddingLeft(40f);
				table15.addCell(hcell35);

				hcell35 = new PdfPCell(new Phrase("", redFont1));
				hcell35.setBorder(Rectangle.NO_BORDER);
				hcell35.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell35.setPaddingLeft(50f);
				table15.addCell(hcell35);

				// cell33.setFixedHeight(35f);
				cell34.setColspan(2);
				table15.setWidthPercentage(100f);
				cell34.addElement(table15);
				table.addCell(cell34);

				// for new row end

				PharmacyShopDetails pharmacyShopDetails = pharmacyShopDetailsRepository.findByShopLocation("Miyapur");

				PdfPCell cell01 = new PdfPCell();

				PdfPTable table211 = new PdfPTable(3);
				table211.setWidths(new float[] { 4f, 4f, 5f });
				table211.setSpacingBefore(10);

				PdfPCell hcell11;
				hcell11 = new PdfPCell(new Phrase("GST#    :  " + pharmacyShopDetails.getGstNO(), redFont));
				hcell11.setBorder(Rectangle.NO_BORDER);
				hcell11.setPaddingLeft(-50f);
				table211.addCell(hcell11);

				hcell11 = new PdfPCell(new Phrase("D.L#    :  " + pharmacyShopDetails.getDlNo(), redFont));
				hcell11.setBorder(Rectangle.NO_BORDER);
				hcell11.setHorizontalAlignment(Element.ALIGN_CENTER);
				table211.addCell(hcell11);

				hcell11 = new PdfPCell(new Phrase("CIN#    :  " + pharmacyShopDetails.getCinNO(), redFont));
				hcell11.setBorder(Rectangle.NO_BORDER);
				hcell11.setPaddingRight(-40f);
				hcell11.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table211.addCell(hcell11);

				cell01.setFixedHeight(20f);
				cell01.setColspan(2);
				cell01.addElement(table211);
				table.addCell(cell01);

				PdfPCell cell6 = new PdfPCell();

				PdfPTable table5 = new PdfPTable(2);
				table5.setWidths(new float[] { 4f, 4f });
				table5.setSpacingBefore(0);

				PdfPCell hcell5;
				hcell5 = new PdfPCell(new Phrase("Created By   : " + createdBy, redFont));
				hcell5.setBorder(Rectangle.NO_BORDER);
				hcell5.setPaddingLeft(-50f);
				hcell5.setPaddingTop(10f);
				table5.addCell(hcell5);

				hcell5 = new PdfPCell(new Phrase("Created Date   : " + today, redFont));
				hcell5.setBorder(Rectangle.NO_BORDER);
				hcell5.setPaddingTop(10f);
				hcell5.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table5.addCell(hcell5);

				PdfPCell hcell6;
				hcell6 = new PdfPCell(new Phrase("Printed By    : " + createdBy, redFont));
				hcell6.setBorder(Rectangle.NO_BORDER);
				hcell6.setPaddingLeft(-50f);
				hcell6.setPaddingTop(1f);
				table5.addCell(hcell6);

				hcell6 = new PdfPCell(new Phrase("Printed Date    : " + today, redFont));
				hcell6.setBorder(Rectangle.NO_BORDER);
				// hcell6.setPaddingLeft(-70f);
				hcell6.setPaddingTop(1f);
				hcell6.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table5.addCell(hcell6);

				PdfPCell hcell331;
				hcell331 = new PdfPCell(new Phrase("Reciever's Signature", redFont1));
				hcell331.setBorder(Rectangle.NO_BORDER);
				hcell331.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell331.setPaddingTop(10f);
				hcell331.setPaddingLeft(-50f);
				table5.addCell(hcell331);

				hcell331 = new PdfPCell(new Phrase("Pharmasist", redFont1));
				hcell331.setBorder(Rectangle.NO_BORDER);
				hcell331.setPaddingTop(10f);
				hcell331.setHorizontalAlignment(Element.ALIGN_RIGHT);

				table5.addCell(hcell331);

				// cell6.setFixedHeight(80f);
				cell6.setColspan(2);
				cell6.addElement(table5);
				table.addCell(cell6);

				document.add(table);

				document.close();
				System.out.println("finished");
				pdfByte = byteArrayOutputStream.toByteArray();
				String uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/sales/viewFile/")
						.path(salesPaymentPdfServiceImpl.getNextId()).toUriString();

				salesPaymentPdf = new SalesPaymentPdf();
				salesPaymentPdf.setFileName(regNo + " Pharmacy Due");
				salesPaymentPdf.setFileuri(uri);
				salesPaymentPdf.setPid(salesPaymentPdfServiceImpl.getNextId());
				salesPaymentPdf.setData(pdfByte);
				salesPaymentPdfServiceImpl.save(salesPaymentPdf);

			} catch (Exception e) {
				// Logger.error(e.getMessage());
				e.printStackTrace();
			}
		} else if (dueType.equalsIgnoreCase("Lab")) {

			String patientName = null;
			String refNoLab = null;
			
			
			List<LaboratoryRegistration> laboratoryRegistrationInfor = laboratoryRegistrationRepository
					.findByBillNo(billNo);

			for (LaboratoryRegistration labInfo : laboratoryRegistrationInfor) {

				regNo = (labInfo.getLaboratoryPatientRegistration() != null)
						? labInfo.getLaboratoryPatientRegistration().getRegId()
						: "";
				patientRegistration = patientRegistrationServiceImpl.findByRegId(regNo);
				if (patientRegistration.isBlockedStatus()) {
					throw new RuntimeException("Payment for this patinet is blocked !");
				}

				labInfo.setReferenceNumber(referenceNumber);
				refNoLab = labInfo.getReferenceNumber();


				labInfo.setPaymentType(modeOfPayment);
				labInfo.setPaid(paid);
				labInfo.setUpdatedBy(createdid);
				labInfo.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));

				ChargeBill chargeBillLab = null;

				
				if (patientRegistration != null && patientRegistration.getpType().equalsIgnoreCase(ConstantValues.INPATIENT)||patientRegistration.getpType().equalsIgnoreCase(ConstantValues.DAYCARE)||patientRegistration.getpType().equalsIgnoreCase(ConstantValues.EMERGENCY)) {
					
					chargeBillLab = (labInfo.getLabServices().getServiceType().equalsIgnoreCase("Lab"))
							? chargeBillServiceImpl.findByLabId(labInfo)
							: chargeBillServiceImpl.findByServiceId(labInfo.getLabServices());

					if(chargeBillLab!=null) {		
					chargeBillLab.setPaid(paid);
					chargeBillLab.setPaymentType(modeOfPayment);
					chargeBillLab.setUpdatedBy(createdid);
					chargeBillLab.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
					}
				}
				 
			}

			patientRegistration = laboratoryRegistrationInfor.get(0).getLaboratoryPatientRegistration();
			PatientDetails patientDetails = patientRegistration.getPatientDetails();

			patientName = (patientDetails.getMiddleName() != null)
					? patientDetails.getTitle() + " " + patientDetails.getFirstName() + " "
							+ patientDetails.getMiddleName() + " " + patientDetails.getLastName()
					: patientDetails.getTitle() + " " + patientDetails.getFirstName() + " "
							+ patientDetails.getLastName();

			finalNetAmount = dueHelper.getNetAmount();
			totalAmount=dueHelper.getAmount();
			discount=dueHelper.getDiscount();

			
			List<FinalBilling> finalBillingUpdate=finalBillingRepository.changeStatusDueBills("Laboratory Registration", ConstantValues.LAB_DUE, billNo);
					
			for(FinalBilling finalBillingUpdateInfo:finalBillingUpdate) {
				finalBillingUpdateInfo.setDueStatus(ConstantValues.NO);
			}
			// Final Billing
			FinalBilling finalBilling = new FinalBilling();
			finalBilling.setBillNo(laboratoryRegistrationInfor.get(0).getBillNo());
			if (finalDue!=0) {
				finalBilling.setDueStatus(ConstantValues.YES);
			} else {
				finalBilling.setDueStatus(ConstantValues.NO);
			}
			finalBilling.setBillType(ConstantValues.LAB_DUE);
			finalBilling.setCardAmount(Math.round(finalCard));
			finalBilling.setCashAmount(Math.round(finalCash));
			finalBilling.setChequeAmount(Math.round(finalCheque));
			finalBilling.setDueAmount(Math.round(finalDue));
			finalBilling.setFinalAmountPaid(Math.round(finalNetAmount));
			finalBilling.setInsertedDate(Timestamp.valueOf(LocalDateTime.now()));
			finalBilling.setFinalBillUser(userSecurity);
			finalBilling.setUpdatedBy(userSecurity.getUserId());
			finalBilling.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
			finalBilling.setName(patientName);
			finalBilling.setMobile(
					(patientRegistration != null) ? patientRegistration.getPatientDetails().getMobile() : null);
			finalBilling.setRegNo((regNo.length() != 0) ? regNo : null);
			finalBilling.setPaymentType(modeOfPayment);
			finalBilling.setTotalAmount(totalAmount);
			finalBilling.setDiscAmount(discount);
			finalBilling
					.setUmrNo((patientRegistration != null) ? patientRegistration.getPatientDetails().getUmr() : null);
			finalBillingServcieImpl.computeSave(finalBilling);

			byte[] pdfBytes = null;
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			Document document = new Document(PageSize.A4_LANDSCAPE);

			try {

				Resource fileResource = resourceLoader.getResource(
						ConstantValues.IMAGE_PNG_CLASSPATH);
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
				hcell90.setPaddingLeft(-61f);

				table99.addCell(hcell90);

				hcell90 = new PdfPCell(new Phrase(patientName, redFont));
				hcell90.setBorder(Rectangle.NO_BORDER);
				hcell90.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell90.setPaddingBottom(-7f);
				hcell90.setPaddingLeft(-85f);
				table99.addCell(hcell90);

				cell3.addElement(table99);

				PdfPTable table2 = new PdfPTable(6);
				table2.setWidths(new float[] { 3f, 1.2f, 5.8f, 3f, 1f, 4f });
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

				hcell4 = new PdfPCell(new Phrase(laboratoryRegistrationInfor.get(0).getInvoiceNo(), redFont));
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

				PdfPCell hcell11;
				hcell11 = new PdfPCell(new Phrase("Reg No", redFont));
				hcell11.setBorder(Rectangle.NO_BORDER);
				hcell11.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell11.setPaddingLeft(-25f);
				table2.addCell(hcell11);

				hcell11 = new PdfPCell(new Phrase(":", redFont));
				hcell11.setBorder(Rectangle.NO_BORDER);
				hcell11.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell11.setPaddingLeft(-20f);
				table2.addCell(hcell11);

				hcell11 = new PdfPCell(new Phrase(patientRegistration.getRegId(), redFont));
				hcell11.setBorder(Rectangle.NO_BORDER);
				hcell11.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell11.setPaddingLeft(-30f);
				table2.addCell(hcell11);

				hcell11 = new PdfPCell(new Phrase("Bill No", redFont));
				hcell11.setBorder(Rectangle.NO_BORDER);
				hcell11.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell11.setPaddingRight(-40f);
				// hcell1.setPaddingTop(-5f);
				table2.addCell(hcell11);

				hcell11 = new PdfPCell(new Phrase(":", redFont));
				hcell11.setBorder(Rectangle.NO_BORDER);
				hcell11.setHorizontalAlignment(Element.ALIGN_RIGHT);
				// hcell1.setPaddingTop(-5f);;
				table2.addCell(hcell11);

				hcell11 = new PdfPCell(new Phrase(laboratoryRegistrationInfor.get(0).getBillNo(), redFont));
				hcell11.setBorder(Rectangle.NO_BORDER);
				hcell11.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell11.setPaddingRight(-30f);
				// hcell1.setPaddingTop(-5f);
				table2.addCell(hcell11);

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
				hcell19 = new PdfPCell(new Phrase("OP/IP Settled Reciept", headFont1));
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
				table1.setWidths(new float[] { 1f, 3f, 5f, 3f, 1f, 2f, 2f, 2f });

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
				long total = 0;
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

					total += laboratoryRegistrationInfo.getNetAmount();

				}

				
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

				cell55 = new PdfPCell(new Phrase(String.valueOf(finalNetAmount), redFont));
				cell55.setBorder(Rectangle.NO_BORDER);
				cell55.setPaddingTop(10f);
				cell55.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell55.setPaddingRight(-30f);
				table37.addCell(cell55);

				PdfPCell hcell56;
				
				if(finalCash!=0) {
				hcell56 = new PdfPCell(new Phrase("Cash Amt", redFont));
				hcell56.setBorder(Rectangle.NO_BORDER);
				hcell56.setPaddingLeft(-50f);
				hcell56.setHorizontalAlignment(Element.ALIGN_LEFT);
				table37.addCell(hcell56);

				hcell56 = new PdfPCell(new Phrase(":", redFont));
				hcell56.setBorder(Rectangle.NO_BORDER);
				hcell56.setPaddingLeft(-50f);
				hcell56.setHorizontalAlignment(Element.ALIGN_LEFT);
				table37.addCell(hcell56);

				hcell56 = new PdfPCell(new Phrase(String.valueOf(finalCash), redFont));
				hcell56.setBorder(Rectangle.NO_BORDER);
				hcell56.setPaddingLeft(-60f);
				hcell56.setHorizontalAlignment(Element.ALIGN_LEFT);
				table37.addCell(hcell56);
				}else {
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

				hcell56 = new PdfPCell(new Phrase(String.valueOf(finalNetAmount), redFont));
				hcell56.setBorder(Rectangle.NO_BORDER);
				// hcell56.setPaddingLeft(-1f);
				hcell56.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell56.setPaddingRight(-30f);
				table37.addCell(hcell56);

				PdfPCell hcell57;
				if(finalCard!=0) {
				hcell57 = new PdfPCell(new Phrase("Card Amt", redFont));
				hcell57.setBorder(Rectangle.NO_BORDER);
				hcell57.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell57.setPaddingLeft(-50f);
				table37.addCell(hcell57);

				hcell57 = new PdfPCell(new Phrase(":", redFont));
				hcell57.setBorder(Rectangle.NO_BORDER);
				hcell57.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell57.setPaddingLeft(-50f);
				table37.addCell(hcell57);

				hcell57 = new PdfPCell(new Phrase(String.valueOf(finalCard), redFont));
				hcell57.setBorder(Rectangle.NO_BORDER);
				hcell57.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell57.setPaddingLeft(-60f);
				table37.addCell(hcell57);
				}else {
					hcell57 = new PdfPCell(new Phrase("", redFont));
					hcell57.setBorder(Rectangle.NO_BORDER);
					hcell57.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell57.setPaddingLeft(-50f);
					table37.addCell(hcell57);

					hcell57 = new PdfPCell(new Phrase("", redFont));
					hcell57.setBorder(Rectangle.NO_BORDER);
					hcell57.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell57.setPaddingLeft(-50f);
					table37.addCell(hcell57);

					hcell57 = new PdfPCell(new Phrase("", redFont));
					hcell57.setBorder(Rectangle.NO_BORDER);
					hcell57.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell57.setPaddingLeft(-40f);
					table37.addCell(hcell57);
	
					
				}
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

				hcell57 = new PdfPCell(new Phrase(String.valueOf(finalNetAmount), redFont));
				hcell57.setBorder(Rectangle.NO_BORDER);
				hcell57.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell57.setPaddingRight(-30f);
				table37.addCell(hcell57);

				PdfPCell hcell58;
				if(finalDue!=0) {
				hcell58 = new PdfPCell(new Phrase("Due Amt",redFont));
				hcell58.setBorder(Rectangle.NO_BORDER);
				hcell58.setPaddingLeft(-50f);
			    hcell58.setHorizontalAlignment(Element.ALIGN_LEFT);
				table37.addCell(hcell58);

				hcell58 = new PdfPCell(new Phrase(":",redFont));
				hcell58.setBorder(Rectangle.NO_BORDER);
				hcell58.setPaddingLeft(-50f);
				hcell58.setHorizontalAlignment(Element.ALIGN_LEFT);
				table37.addCell(hcell58);

				hcell58 = new PdfPCell(new Phrase(String.valueOf(finalDue),redFont));
				hcell58.setBorder(Rectangle.NO_BORDER);
				hcell58.setPaddingLeft(-60f);
				hcell58.setHorizontalAlignment(Element.ALIGN_LEFT);
				table37.addCell(hcell58);
				}else {
					hcell58 = new PdfPCell(new Phrase(""));
					hcell58.setBorder(Rectangle.NO_BORDER);
					hcell58.setPaddingLeft(-50f);
				    hcell58.setHorizontalAlignment(Element.ALIGN_LEFT);
					table37.addCell(hcell58);

					hcell58 = new PdfPCell(new Phrase(""));
					hcell58.setBorder(Rectangle.NO_BORDER);
					hcell58.setPaddingLeft(-50f);
					hcell58.setHorizontalAlignment(Element.ALIGN_LEFT);
					table37.addCell(hcell58);

					hcell58 = new PdfPCell(new Phrase(""));
					hcell58.setBorder(Rectangle.NO_BORDER);
					hcell58.setPaddingLeft(-50f);
					hcell58.setHorizontalAlignment(Element.ALIGN_LEFT);
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

				if (!modeOfPayment.equalsIgnoreCase("Due")) {
					hcell58 = new PdfPCell(new Phrase(String.valueOf(finalNetAmount), redFont));
					hcell58.setBorder(Rectangle.NO_BORDER);
					hcell58.setHorizontalAlignment(Element.ALIGN_RIGHT);
					hcell58.setPaddingRight(-30f);
					table37.addCell(hcell58);
				} else {
					hcell58 = new PdfPCell(new Phrase(String.valueOf(0), redFont));
					hcell58.setBorder(Rectangle.NO_BORDER);
					hcell58.setHorizontalAlignment(Element.ALIGN_RIGHT);
					hcell58.setPaddingRight(-30f);
					table37.addCell(hcell58);
				}
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

				
				hcell59 = new PdfPCell(new Phrase("(" + NumberToWordsConverter.convert(((long) finalNetAmount)) + ")", redFont));
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

				if (!modeOfPayment.equalsIgnoreCase("Due")) {
					hcell60 = new PdfPCell(new Phrase("(" + NumberToWordsConverter.convert((long) finalNetAmount) + ")", redFont));
					hcell60.setBorder(Rectangle.NO_BORDER);
					hcell60.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell60.setPaddingLeft(-20f);
					table37.addCell(hcell60);
				} else {
					hcell60 = new PdfPCell(new Phrase("(" + NumberToWordsConverter.convert(0) + ")", redFont));
					hcell60.setBorder(Rectangle.NO_BORDER);
					hcell60.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell60.setPaddingLeft(-20f);
					table37.addCell(hcell60);

				}
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

				PdfPCell cell34 = new PdfPCell();

				PdfPTable table15 = new PdfPTable(5);
				table15.setWidths(new float[] { 2f, 3f, 3f, 3f, 3f });

				table15.setSpacingBefore(10);

				PdfPCell hcell34;
				hcell34 = new PdfPCell(new Phrase("Pay Mode", headFont));
				hcell34.setBorder(Rectangle.NO_BORDER);
				hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell34.setPaddingLeft(10f);
				table15.addCell(hcell34);

				hcell34 = new PdfPCell(new Phrase("Amount", headFont));
				hcell34.setBorder(Rectangle.NO_BORDER);
				hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell34.setPaddingLeft(35f);
				table15.addCell(hcell34);

				hcell34 = new PdfPCell(new Phrase("Card#", headFont));
				hcell34.setBorder(Rectangle.NO_BORDER);
				hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell34.setPaddingLeft(40f);
				table15.addCell(hcell34);

				hcell34 = new PdfPCell(new Phrase("Bank Name", headFont));
				hcell34.setBorder(Rectangle.NO_BORDER);
				hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell34.setPaddingLeft(40f);
				table15.addCell(hcell34);

				hcell34 = new PdfPCell(new Phrase("Exp Date", headFont));
				hcell34.setBorder(Rectangle.NO_BORDER);
				hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell34.setPaddingLeft(50f);
				table15.addCell(hcell34);

				PdfPCell hcell35;
				hcell35 = new PdfPCell(new Phrase(modeOfPayment, redFont));
				hcell35.setBorder(Rectangle.NO_BORDER);
				hcell35.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell35.setPaddingLeft(10f);
				table15.addCell(hcell35);

				hcell35 = new PdfPCell(new Phrase(String.valueOf(Math.round(finalNetAmount)), redFont));
				hcell35.setBorder(Rectangle.NO_BORDER);
				hcell35.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell35.setPaddingLeft(35f);
				table15.addCell(hcell35);
				if (modeOfPayment.equalsIgnoreCase("card") || modeOfPayment.equalsIgnoreCase("cash+card")) {
					hcell35 = new PdfPCell(new Phrase(refNoLab, redFont));
					hcell35.setBorder(Rectangle.NO_BORDER);
					hcell35.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell35.setPaddingLeft(40f);
					table15.addCell(hcell35);
				} else {
					hcell35 = new PdfPCell(new Phrase("", redFont));
					hcell35.setBorder(Rectangle.NO_BORDER);
					hcell35.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell35.setPaddingLeft(40f);
					table15.addCell(hcell35);

				}
				hcell35 = new PdfPCell(new Phrase("", redFont));
				hcell35.setBorder(Rectangle.NO_BORDER);
				hcell35.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell35.setPaddingLeft(40f);
				table15.addCell(hcell35);

				hcell35 = new PdfPCell(new Phrase("", redFont));
				hcell35.setBorder(Rectangle.NO_BORDER);
				hcell35.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell35.setPaddingLeft(50f);
				table15.addCell(hcell35);

				// cell33.setFixedHeight(35f);
				cell34.setColspan(2);
				table15.setWidthPercentage(100f);
				cell34.addElement(table15);
				table.addCell(cell34);

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
				String uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/sales/viewFile/")
						.path(salesPaymentPdfServiceImpl.getNextId()).toUriString();

				salesPaymentPdf = new SalesPaymentPdf();
				salesPaymentPdf.setFileName(patientRegistration.getRegId() + " Lab Due");
				salesPaymentPdf.setFileuri(uri);
				salesPaymentPdf.setPid(salesPaymentPdfServiceImpl.getNextId());
				salesPaymentPdf.setData(pdfBytes);
				salesPaymentPdfServiceImpl.save(salesPaymentPdf);
			} catch (Exception e) {
				// Logger.error(e.getMessage());
				e.printStackTrace();
			}
		}

		else if (dueType.equalsIgnoreCase("osp")) {

			String refNoOsp = null;
			List<OspService> ospinfo = ospServiceRepository.findByBillNo(billNo);

			User user = userRepository.findOneByUserId(ospinfo.get(0).getRefferedById());
			String docName=null;
			if(user!=null) {
			 docName = user.getFirstName() + "" + user.getMiddleName() + "" + user.getLastName();
			}else {
				
				docName=" ";
			}
			float amount = 0;
			float finalPaidAmount = 0;
			String name = "";
			long mobile = 0;
			for (OspService osp : ospinfo) {

				
				
				osp.setReferenceNumber(referenceNumber);
				refNoOsp = osp.getReferenceNumber();

				amount += osp.getPrice();
				finalPaidAmount += osp.getNetAmount();
				name = osp.getPatientName();
				mobile = osp.getMobile();
				osp.setPaymentType(modeOfPayment);
				osp.setPaid(paid);
				osp.setUpdatedBy(createdid);
				osp.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
			}

			finalNetAmount = dueHelper.getNetAmount();
			totalAmount=dueHelper.getAmount();
			discount=dueHelper.getDiscount();

			
			List<FinalBilling> finalBillingUpdate=finalBillingRepository.changeStatusDueBills("Osp Bill", ConstantValues.OSP_DUE, billNo);
					
			for(FinalBilling finalBillingUpdateInfo:finalBillingUpdate) {
				finalBillingUpdateInfo.setDueStatus(ConstantValues.NO);
			}
			
			FinalBilling finalBilling = new FinalBilling();
			finalBilling.setBillNo(billNo);
			if (finalDue!=0) {
				finalBilling.setDueStatus(ConstantValues.YES);
			} else {
				finalBilling.setDueStatus(ConstantValues.NO);
			}
			finalBilling.setBillType(ConstantValues.OSP_DUE);
			finalBilling.setCardAmount(Math.round(finalCard));
			finalBilling.setCashAmount(Math.round(finalCash));
			finalBilling.setChequeAmount(Math.round(finalCheque));
			finalBilling.setDueAmount(finalDue);
			finalBilling.setFinalAmountPaid(Math.round(finalNetAmount));
			finalBilling.setUpdatedBy(userSecurity.getUserId());
			finalBilling.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
			finalBilling.setTotalAmount(totalAmount);
			finalBilling.setDiscAmount(discount);
			finalBilling.setFinalBillUser(userSecurity);
			finalBilling.setName(name);
			finalBilling.setMobile(mobile);
			finalBilling.setInsertedDate(Timestamp.valueOf(LocalDateTime.now()));
			finalBilling.setPaymentType(modeOfPayment);
			finalBillingServcieImpl.computeSave(finalBilling);

			try {

				
				byte[] pdfByte = null;
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

				Document document = new Document(PageSize.A4_LANDSCAPE);

				Resource fileResource = resourceLoader.getResource(
						ConstantValues.IMAGE_PNG_CLASSPATH);
				// Chunk cnd1 = new Chunk(new VerticalPositionMark());
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
				DateFormat formatter1 = new SimpleDateFormat("dd-MMM-yyyy");
				String today1 = formatter1.format(dateInfo).toString();

				Date date = Calendar.getInstance().getTime();
				DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
				String today = formatter.format(date).toString();

				Timestamp timestamp1 = ospinfo.get(0).getEnteredDate();
				DateFormat dateFormat1 = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa ");

				Calendar calendar1 = Calendar.getInstance();
				calendar1.setTimeInMillis(timestamp1.getTime());

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
				table99.setWidths(new float[] { 3f, 1f, 4f });
				table99.setSpacingBefore(10);

				PdfPCell hcell90;
				hcell90 = new PdfPCell(new Phrase("Patient Name", redFont));
				hcell90.setBorder(Rectangle.NO_BORDER);
				hcell90.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell90.setPaddingBottom(-7f);
				hcell90.setPaddingLeft(-25f);
				table99.addCell(hcell90);

				hcell90 = new PdfPCell(new Phrase(":", redFont));
				hcell90.setBorder(Rectangle.NO_BORDER);
				hcell90.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell90.setPaddingBottom(-7f);
				hcell90.setPaddingLeft(-50f);
				table99.addCell(hcell90);

				hcell90 = new PdfPCell(new Phrase(ospinfo.get(0).getPatientName(), redFont));
				hcell90.setBorder(Rectangle.NO_BORDER);
				hcell90.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell90.setPaddingBottom(-7f);
				hcell90.setPaddingLeft(-50f);
				table99.addCell(hcell90);

				cell3.addElement(table99);

				PdfPTable table2 = new PdfPTable(6);
				table2.setWidths(new float[] { 3f, 1f, 4f, 3f, 1f, 4f });
				table2.setSpacingBefore(10);

				PdfPCell hcell1;
				hcell1 = new PdfPCell(new Phrase("Age/Gender", redFont));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-25f);
				table2.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase(":", redFont));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-20f);
				table2.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase(ospinfo.get(0).getAge() + "/" + ospinfo.get(0).getGender(), redFont));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-30f);
				table2.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase("OSP No", redFont));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingRight(-40f);
				table2.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase(":", redFont));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table2.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase(ospinfo.get(0).getOspServiceId(), redFont));
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

				hcell4 = new PdfPCell(new Phrase(today1, redFont));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell4.setPaddingLeft(-30f);
				table2.addCell(hcell4);

				hcell4 = new PdfPCell(new Phrase("Phone", redFont));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell4.setPaddingRight(-27.5f);
				table2.addCell(hcell4);

				hcell4 = new PdfPCell(new Phrase(":", redFont));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table2.addCell(hcell4);

				hcell4 = new PdfPCell(new Phrase(String.valueOf(ospinfo.get(0).getMobile()), redFont));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell4.setPaddingRight(-27.5f);
				table2.addCell(hcell4);

				cell3.setFixedHeight(115f);
				cell3.setColspan(2);
				cell3.addElement(table2);

				PdfPTable table98 = new PdfPTable(3);
				table98.setWidths(new float[] { 3f, 1f, 4f });
				table98.setSpacingBefore(10);

				PdfPCell hcell91;
				hcell91 = new PdfPCell(new Phrase("Refer By", redFont));
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

				hcell91 = new PdfPCell(new Phrase(docName, redFont));
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
				hcell19 = new PdfPCell(new Phrase("OSP Due BILL CUM RECEIPT", headFont1));
				hcell19.setBorder(Rectangle.NO_BORDER);
				hcell19.setHorizontalAlignment(Element.ALIGN_CENTER);
				table21.addCell(hcell19);

				cell19.setFixedHeight(20f);
				cell19.setColspan(2);
				cell19.addElement(table21);
				table.addCell(cell19);

				PdfPCell cell31 = new PdfPCell();

				PdfPTable table1 = new PdfPTable(8);
				table1.setWidths(new float[] { 1f, 3f, 4f, 3f, 3f, 2.5f, 2f, 2.5f });

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
				long total = 0;
				String serviceId = null;
				String serviceName = null;
				String serviceType = null;
				float totalAmt = 0;
				String chargeBillId = null;
				String insertDt = null;
				String cRegId = null;
				String salesDate = null;

				for (OspService ospServicesInfo : ospinfo) {

					PdfPCell cell;

					cell = new PdfPCell(new Phrase(String.valueOf(count = count + 1), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table1.addCell(cell);

					cell = new PdfPCell(new Phrase(ospServicesInfo.getOspLabServices().getServiceId(), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					table1.addCell(cell);

					cell = new PdfPCell(new Phrase(ospServicesInfo.getServiceName(), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					table1.addCell(cell);

					cell = new PdfPCell(new Phrase(ospServicesInfo.getOspLabServices().getServiceType(), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					table1.addCell(cell);

					cell = new PdfPCell(new Phrase(String.valueOf(ospServicesInfo.getQuantity()), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell.setPaddingRight(30);
					table1.addCell(cell);

					cell = new PdfPCell(new Phrase(String.valueOf(ospServicesInfo.getPrice()), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cell.setPaddingRight(35);
					table1.addCell(cell);

					cell = new PdfPCell(new Phrase(String.valueOf(ospServicesInfo.getDiscount()), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cell.setPaddingRight(30);
					table1.addCell(cell);

					cell = new PdfPCell(new Phrase(String.valueOf(ospServicesInfo.getNetAmount()), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					table1.addCell(cell);

					total += ospServicesInfo.getNetAmount();

				}

				// -------------------------------

				PdfPTable table37 = new PdfPTable(6);
				table37.setWidths(new float[] { 3f, 1f, 4f, 7f, 1f, 4f });
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

				cell55 = new PdfPCell(new Phrase(String.valueOf(finalNetAmount), redFont));
				cell55.setBorder(Rectangle.NO_BORDER);
				cell55.setPaddingTop(10f);
				cell55.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell55.setPaddingRight(-50f);
				table37.addCell(cell55);

				PdfPCell hcell56;
				if (finalCash != 0) {
					hcell56 = new PdfPCell(new Phrase("Cash Amt", redFont));
					hcell56.setBorder(Rectangle.NO_BORDER);
					hcell56.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell56.setPaddingLeft(-50f);
					table37.addCell(hcell56);

				hcell56 = new PdfPCell(new Phrase(":", redFont));
				hcell56.setBorder(Rectangle.NO_BORDER);
				hcell56.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell56.setPaddingLeft(-50f);
				table37.addCell(hcell56);

				hcell56 = new PdfPCell(new Phrase(String.valueOf(finalCash), redFont));
				hcell56.setBorder(Rectangle.NO_BORDER);
				hcell56.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell56.setPaddingLeft(-60f);
				table37.addCell(hcell56);
				}else {
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

				}
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

				hcell56 = new PdfPCell(new Phrase(String.valueOf(finalNetAmount), redFont));
				hcell56.setBorder(Rectangle.NO_BORDER);
				hcell56.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell56.setPaddingRight(-50f);
				table37.addCell(hcell56);

				PdfPCell hcell57;
				if(finalCard!=0) {
				hcell57 = new PdfPCell(new Phrase("Card Amt", redFont));
				hcell57.setBorder(Rectangle.NO_BORDER);
				hcell57.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell57.setPaddingLeft(-50f);
				table37.addCell(hcell57);

				hcell57 = new PdfPCell(new Phrase(":", redFont));
				hcell57.setBorder(Rectangle.NO_BORDER);
				hcell57.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell57.setPaddingLeft(-50f);
				table37.addCell(hcell57);

				hcell57 = new PdfPCell(new Phrase(String.valueOf(finalCard), redFont));
				hcell57.setBorder(Rectangle.NO_BORDER);
				hcell57.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell57.setPaddingLeft(-60f);
				table37.addCell(hcell57);
				}else {
					hcell57 = new PdfPCell(new Phrase(EMPTY_STRING, redFont));
					hcell57.setBorder(Rectangle.NO_BORDER);
					hcell57.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell57.setPaddingLeft(-50f);
					table37.addCell(hcell57);

					hcell57 = new PdfPCell(new Phrase("", redFont));
					hcell57.setBorder(Rectangle.NO_BORDER);
					hcell57.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell57.setPaddingLeft(-50f);
					table37.addCell(hcell57);

					hcell57 = new PdfPCell(new Phrase(EMPTY_STRING, redFont));
					hcell57.setBorder(Rectangle.NO_BORDER);
					hcell57.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell57.setPaddingLeft(-60f);
					table37.addCell(hcell57);

				}
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

				hcell57 = new PdfPCell(new Phrase(String.valueOf(finalNetAmount), redFont));
				hcell57.setBorder(Rectangle.NO_BORDER);
				hcell57.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell57.setPaddingRight(-50f);
				table37.addCell(hcell57);

				PdfPCell hcell58;
				if(finalDue!=0) {
				hcell58 = new PdfPCell(new Phrase("Due Amt",redFont));
				hcell58.setBorder(Rectangle.NO_BORDER);
				hcell58.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell58.setPaddingLeft(-50f);
				table37.addCell(hcell58);

				hcell58 = new PdfPCell(new Phrase(":",redFont));
				hcell58.setBorder(Rectangle.NO_BORDER);
				hcell58.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell58.setPaddingLeft(-50f);
				table37.addCell(hcell58);

				hcell58 = new PdfPCell(new Phrase(String.valueOf(finalDue),redFont));
				hcell58.setBorder(Rectangle.NO_BORDER);
				hcell58.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell58.setPaddingLeft(-60f);
				table37.addCell(hcell58);
				}else {
					hcell58 = new PdfPCell(new Phrase(""));
					hcell58.setBorder(Rectangle.NO_BORDER);
					table37.addCell(hcell58);

					hcell58 = new PdfPCell(new Phrase(""));
					hcell58.setBorder(Rectangle.NO_BORDER);
					table37.addCell(hcell58);

					hcell58 = new PdfPCell(new Phrase(""));
					hcell58.setBorder(Rectangle.NO_BORDER);
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

				hcell58 = new PdfPCell(new Phrase(String.valueOf(finalNetAmount), redFont));
				hcell58.setBorder(Rectangle.NO_BORDER);
				hcell58.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell58.setPaddingRight(-50f);
				table37.addCell(hcell58);

				PdfPTable table371 = new PdfPTable(3);
				table371.setWidths(new float[] { 7f, 1f, 8f });
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

				hcell59 = new PdfPCell(new Phrase("(" + numberToWordsConverter.convert((long)finalNetAmount) + ")", redFont));
				hcell59.setBorder(Rectangle.NO_BORDER);
				hcell59.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell59.setPaddingLeft(-150f);
				table371.addCell(hcell59);

				PdfPCell hcell60;
				hcell60 = new PdfPCell(new Phrase("Received Amount In Words ", redFont));

				hcell60.setBorder(Rectangle.NO_BORDER);
				hcell60.setPaddingLeft(-50f);
				hcell60.setHorizontalAlignment(Element.ALIGN_LEFT);
				table371.addCell(hcell60);

				hcell60 = new PdfPCell(new Phrase("", headFont));
				hcell60.setBorder(Rectangle.NO_BORDER);
				hcell60.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table371.addCell(hcell60);

				hcell60 = new PdfPCell(new Phrase("(" + numberToWordsConverter.convert((long)finalNetAmount) + ")", redFont));
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

				PdfPCell cell34 = new PdfPCell();

				PdfPTable table15 = new PdfPTable(5);
				table15.setWidths(new float[] { 2f, 3f, 3f, 3f, 3f });

				table15.setSpacingBefore(10);

				PdfPCell hcell34;
				hcell34 = new PdfPCell(new Phrase("Pay Mode", headFont));
				hcell34.setBorder(Rectangle.NO_BORDER);
				hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell34.setPaddingLeft(10f);
				table15.addCell(hcell34);

				hcell34 = new PdfPCell(new Phrase("Amount", headFont));
				hcell34.setBorder(Rectangle.NO_BORDER);
				hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell34.setPaddingLeft(35f);
				table15.addCell(hcell34);

				hcell34 = new PdfPCell(new Phrase("Card#", headFont));
				hcell34.setBorder(Rectangle.NO_BORDER);
				hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell34.setPaddingLeft(40f);
				table15.addCell(hcell34);

				hcell34 = new PdfPCell(new Phrase("Bank Name", headFont));
				hcell34.setBorder(Rectangle.NO_BORDER);
				hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell34.setPaddingLeft(40f);
				table15.addCell(hcell34);

				hcell34 = new PdfPCell(new Phrase("Exp Date", headFont));
				hcell34.setBorder(Rectangle.NO_BORDER);
				hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell34.setPaddingLeft(50f);
				table15.addCell(hcell34);

				PdfPCell hcell35;
				hcell35 = new PdfPCell(new Phrase(modeOfPayment, redFont));
				hcell35.setBorder(Rectangle.NO_BORDER);
				hcell35.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell35.setPaddingLeft(10f);
				table15.addCell(hcell35);

				hcell35 = new PdfPCell(new Phrase(String.valueOf(Math.round(total)), redFont));
				hcell35.setBorder(Rectangle.NO_BORDER);
				hcell35.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell35.setPaddingLeft(35f);
				table15.addCell(hcell35);
				if (finalCard!=0) {
					hcell35 = new PdfPCell(new Phrase(refNoOsp, redFont));
					hcell35.setBorder(Rectangle.NO_BORDER);
					hcell35.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell35.setPaddingLeft(40f);
					table15.addCell(hcell35);
				} else {
					hcell35 = new PdfPCell(new Phrase("", redFont));
					hcell35.setBorder(Rectangle.NO_BORDER);
					hcell35.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell35.setPaddingLeft(40f);
					table15.addCell(hcell35);

				}
				hcell35 = new PdfPCell(new Phrase("", redFont));
				hcell35.setBorder(Rectangle.NO_BORDER);
				hcell35.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell35.setPaddingLeft(40f);
				table15.addCell(hcell35);

				hcell35 = new PdfPCell(new Phrase("", redFont));
				hcell35.setBorder(Rectangle.NO_BORDER);
				hcell35.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell35.setPaddingLeft(50f);
				table15.addCell(hcell35);

				// cell33.setFixedHeight(35f);
				cell34.setColspan(2);
				table15.setWidthPercentage(100f);
				cell34.addElement(table15);
				table.addCell(cell34);

				PdfPCell cell5 = new PdfPCell();

				PdfPTable table35 = new PdfPTable(2);
				table35.setWidths(new float[] { 5f, 4f });
				table35.setSpacingBefore(10);

				PdfPCell hcell12;
				hcell12 = new PdfPCell(new Phrase("Created By    : " + createdBy, redFont));
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
				hcell13 = new PdfPCell(new Phrase("Printed By     : " + createdBy, redFont));
				hcell13.setBorder(Rectangle.NO_BORDER);
				hcell13.setPaddingLeft(-50f);
				table35.addCell(hcell13);

				hcell13 = new PdfPCell(new Phrase("Printed Dt       :   " + today, redFont));
				hcell13.setBorder(Rectangle.NO_BORDER);
				// hcell13.setPaddingRight(3f);
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
				salesPaymentPdf.setFileName(ospinfo.get(0).getOspServiceId() + " Osp due Bill");
				salesPaymentPdf.setFileuri(uri);
				salesPaymentPdf.setPid(salesPaymentPdfServiceImpl.getNextId());
				salesPaymentPdf.setData(pdfByte);
				System.out.println(salesPaymentPdf);
				salesPaymentPdfServiceImpl.save(salesPaymentPdf);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		else if (dueType.equalsIgnoreCase("IpFinalDue")) {
			String patientName = null;
			String EMPTY_SPACE = " ";
			String umr = null;
			String consultant = null;
		    float amount = 0;
			String paymentNextBillNo = null;
		    paymentType = null;
			String refNo = null;
            String regIde=null;

			List<FinalBilling> finalBillingInfo = finalBillingServcieImpl.findByBillTypeAndBillNo("Ip Final Billing",
					billNo);
			
			 patientRegistration = patientRegistrationServiceImpl.findByRegId(finalBillingInfo.get(0).getRegNo());
			 regIde=patientRegistration.getRegId();
			// udser details
				User userInfo = userServiceImpl.findByUserName(principal.getName());
				String createdName = null;
				createdName = (userInfo.getMiddleName() != null)
						? (userInfo.getFirstName() + EMPTY_SPACE + userInfo.getMiddleName() + EMPTY_SPACE
								+ userInfo.getLastName())
						: (userInfo.getFirstName() + EMPTY_SPACE + userInfo.getLastName());

				Date date2 = patientRegistration.getDateOfJoining();
				DateFormat formatter2 = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
				String admissionDate = formatter2.format(date2).toString();

				byte[] pdfByte = null;
				ByteArrayOutputStream byteArrayOutputStream = null;

				// for patientname
				String patientFirstName = patientRegistration.getPatientDetails().getFirstName();
				String patientMiddleName = patientRegistration.getPatientDetails().getMiddleName();
				String patientLastName = patientRegistration.getPatientDetails().getLastName();

				if (!patientMiddleName.isEmpty()) {
					patientName = patientFirstName + EMPTY_SPACE + patientMiddleName + EMPTY_SPACE + patientLastName;
				} else {
					patientName = patientFirstName + EMPTY_SPACE + patientLastName;
				}

				// for department
				String dpt = null;

				if (patientRegistration.getVuserD()!=null) {
					dpt = patientRegistration.getVuserD().getDoctorDetails().getSpecilization();

				} else {
					dpt = "";
				}

				// for room details
				String admittedWard = null;

				List<RoomBookingDetails> roomBookingDetails = patientRegistration.getRoomBookingDetails();

				for (RoomBookingDetails roomBookingDetailsInfo : roomBookingDetails) {
					RoomDetails roomDetails = roomBookingDetailsInfo.getRoomDetails();
					admittedWard = roomDetails.getRoomType();
				}

		
			// PATIENT other details
			umr = patientRegistration.getPatientDetails().getUmr();
			String age = patientRegistration.getPatientDetails().getAge();
			String gender = patientRegistration.getPatientDetails().getGender();
			long contactNo = patientRegistration.getPatientDetails().getMobile();
			String patientFatherName = patientRegistration.getPatientDetails().getMotherName();
			consultant = patientRegistration.getPatientDetails().getConsultant();
			String city = patientRegistration.getPatientDetails().getCity();
			String state = patientRegistration.getPatientDetails().getState();
			// refNo = chargeBill.getReferenceNumber();
			String address = patientRegistration.getPatientDetails().getAddress();
			String name = finalBillingInfo.get(0).getName();
			long mobile = finalBillingInfo.get(0).getMobile();
			finalNetAmount = dueHelper.getAmount();

			amount=finalCash+finalCard+finalCheque;
			
            finalNetAmount = dueHelper.getNetAmount();
			totalAmount=dueHelper.getAmount();
			discount=dueHelper.getDiscount();
			
			List<FinalBilling> finalBillingUpdate=finalBillingServcieImpl.findByBillTypeAndBillNo("Ip Final Billing", billNo);
			for(FinalBilling finalBillingUpdateInfo:finalBillingUpdate) {
				finalBillingUpdateInfo.setDueStatus(ConstantValues.NO);
			}
			
			FinalBilling finalBilling = new FinalBilling();
			finalBilling.setBillNo(billNo);
			if (finalDue!=0) {
				finalBilling.setDueStatus(ConstantValues.YES);
			} else {
				finalBilling.setDueStatus(ConstantValues.NO);
			}
			finalBilling.setBillType("Ip Final Billing");
			finalBilling.setCardAmount(Math.round(finalCard));
			finalBilling.setCashAmount(Math.round(finalCash));
			finalBilling.setChequeAmount(Math.round(finalCheque));
			finalBilling.setFinalAmountPaid(Math.round(finalNetAmount));
			finalBilling.setUpdatedBy(userSecurity.getUserId());
			finalBilling.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
			finalBilling.setTotalAmount(totalAmount);
			finalBilling.setDiscAmount(discount);
			finalBilling.setRegNo(regIde);
			finalBilling.setUmrNo(umr);
			finalBilling.setFinalBillUser(userSecurity);
			finalBilling.setName(name);
			finalBilling.setMobile(mobile);
			finalBilling.setInsertedDate(Timestamp.valueOf(LocalDateTime.now()));
			finalBilling.setPaymentType(modeOfPayment);
			finalBillingServcieImpl.computeSave(finalBilling);

						

			Date date = new Date(Timestamp.valueOf(LocalDateTime.now()).getTime());
			DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
			String today = formatter.format(date).toString();

			Date dateAdv = patientRegistration.getRegDate();
			DateFormat formatterAdv = new SimpleDateFormat("dd-MMM-yyyy");
			String advDate = formatterAdv.format(dateAdv).toString();

			
			Font redFont2 = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
			Font redFont3 = new Font(Font.FontFamily.HELVETICA, 12, Font.UNDERLINE);
			Font redFont4 = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
			Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
			Font redFont9 = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.NORMAL);
			Font redFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
			Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);

		
			
			salesPaymentPdf=new SalesPaymentPdf();
			byteArrayOutputStream = new ByteArrayOutputStream();

			try {
				Document document = new Document(PageSize.A4_LANDSCAPE);

				Font headFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
				PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);

				Resource fileResourcee = resourceLoader.getResource(
						ConstantValues.IMAGE_PNG_CLASSPATH);

			Image img = Image.getInstance(hospitalLogo.getURL());

				document.open();
				PdfPTable table = new PdfPTable(2);
				img.scaleAbsolute(ConstantValues.IMAGE_ABSOLUTE_INTIAL_POSITION, ConstantValues.IMAGE_ABSOLUTE_FINAL_POSITION);
				table.setWidthPercentage(ConstantValues.TABLE_SET_WIDTH_PERECENTAGE);

				Phrase pq = new Phrase(new Chunk(img, ConstantValues.IMAGE_SET_INTIAL_POSITION, ConstantValues.IMAGE_SET_FINAL_POSITION));
		pq.add(new Chunk(ConstantValues.ADVANCE_RECEIPT_ADDRESS, redFont));
				PdfPCell cellp = new PdfPCell(pq);
				PdfPCell cell1 = new PdfPCell();

				// for header Bold
				PdfPTable table96 = new PdfPTable(1);
				table96.setWidths(new float[] { 5f });
				table96.setSpacingBefore(10);

				PdfPCell hcell96;
				hcell96 = new PdfPCell(new Phrase(ConstantValues.HOSPITAL_NAME, headFont1));
				hcell96.setBorder(Rectangle.NO_BORDER);
				hcell96.setHorizontalAlignment(Element.ALIGN_CENTER);
				hcell96.setPaddingTop(5f);
				hcell96.setPaddingLeft(52f);

				table96.addCell(hcell96);
				cell1.addElement(table96);

				// for header end

				cell1.addElement(pq);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(cell1);

				PdfPCell cell3 = new PdfPCell();

				PdfPTable table99 = new PdfPTable(3);
				table99.setWidths(new float[] { 3f, 1f, 5f });
				table99.setSpacingBefore(10);

				PdfPCell hcell90;
				hcell90 = new PdfPCell(new Phrase("Patient", redFont));
				hcell90.setBorder(Rectangle.NO_BORDER);
				hcell90.setPaddingBottom(-7f);
				hcell90.setPaddingLeft(-25f);
				table99.addCell(hcell90);

				hcell90 = new PdfPCell(new Phrase(":", redFont));
				hcell90.setBorder(Rectangle.NO_BORDER);
				hcell90.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell90.setPaddingLeft(-45f);
				table99.addCell(hcell90);

				hcell90 = new PdfPCell(new Phrase(patientName, redFont));
				hcell90.setBorder(Rectangle.NO_BORDER);
				hcell90.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell90.setPaddingBottom(-7f);
				hcell90.setPaddingLeft(-60f);
				table99.addCell(hcell90);

				cell3.addElement(table99);

				PdfPTable table2 = new PdfPTable(6);
				table2.setWidths(new float[] { 3f, 1f, 5f, 3f, 1f, 4f });
				table2.setSpacingBefore(10);

				PdfPCell hcell4;
				hcell4 = new PdfPCell(new Phrase("UMR NO", redFont));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-25f);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table2.addCell(hcell4);

				hcell4 = new PdfPCell(new Phrase(":", redFont));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell4.setPaddingLeft(-10f);
				table2.addCell(hcell4);

				hcell4 = new PdfPCell(new Phrase(umr, redFont));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell4.setPaddingLeft(-15f);
				table2.addCell(hcell4);

				hcell4 = new PdfPCell(new Phrase("Bill No", redFont));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table2.addCell(hcell4);

				hcell4 = new PdfPCell(new Phrase(":", redFont));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell4.setPaddingRight(-0.1f);
				table2.addCell(hcell4);

				hcell4 = new PdfPCell(new Phrase(billNo, redFont));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell4.setPaddingRight(-20.5f);
				table2.addCell(hcell4);

				PdfPCell hcell41;
				hcell41 = new PdfPCell(new Phrase("Reg.No", redFont));
				hcell41.setBorder(Rectangle.NO_BORDER);
				hcell41.setPaddingLeft(-25f);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table2.addCell(hcell41);

				hcell41 = new PdfPCell(new Phrase(":", redFont));
				hcell41.setBorder(Rectangle.NO_BORDER);
				hcell41.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell41.setPaddingLeft(-10f);
				table2.addCell(hcell41);

				hcell41 = new PdfPCell(new Phrase(regIde, redFont));
				hcell41.setBorder(Rectangle.NO_BORDER);
				hcell41.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell41.setPaddingLeft(-15f);
				table2.addCell(hcell41);

				hcell41 = new PdfPCell(new Phrase("Reg.Dt", redFont));
				hcell41.setBorder(Rectangle.NO_BORDER);
				hcell41.setHorizontalAlignment(Element.ALIGN_LEFT);
				table2.addCell(hcell41);

				hcell41 = new PdfPCell(new Phrase(":", redFont));
				hcell41.setBorder(Rectangle.NO_BORDER);
				hcell41.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell41.setPaddingRight(-0.1f);
				table2.addCell(hcell41);

				hcell41 = new PdfPCell(new Phrase(advDate, redFont));
				hcell41.setBorder(Rectangle.NO_BORDER);
				hcell41.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell41.setPaddingRight(-20.5f);
				table2.addCell(hcell41);

				PdfPCell hcell15;
				hcell15 = new PdfPCell(new Phrase("Age/Sex", redFont));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-25f);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table2.addCell(hcell15);

				hcell15 = new PdfPCell(new Phrase(":", redFont));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(-10f);
				table2.addCell(hcell15);

				hcell15 = new PdfPCell(new Phrase(age + "/" + gender, redFont));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(-15f);
				table2.addCell(hcell15);

				hcell15 = new PdfPCell(new Phrase("Phone", redFont));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingRight(-27.5f);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				table2.addCell(hcell15);

				hcell41 = new PdfPCell(new Phrase(":", redFont));
				hcell41.setBorder(Rectangle.NO_BORDER);
				hcell41.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell41.setPaddingRight(-0.1f);
				table2.addCell(hcell41);

				hcell41 = new PdfPCell(new Phrase(String.valueOf(contactNo), redFont));
				hcell41.setBorder(Rectangle.NO_BORDER);
				hcell41.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell41.setPaddingRight(-20.5f);
				table2.addCell(hcell41);

				cell3.setFixedHeight(110f);
				cell3.setColspan(2);
				cell3.addElement(table2);

				table.addCell(cell3);

				PdfPCell cell19 = new PdfPCell();

				PdfPTable table21 = new PdfPTable(1);
				table21.setWidths(new float[] { 4f });
				table21.setSpacingBefore(10);

				PdfPCell hcell19;
				hcell19 = new PdfPCell(new Phrase("Final Due Reciept", headFont1));
				hcell19.setBorder(Rectangle.NO_BORDER);
				hcell19.setHorizontalAlignment(Element.ALIGN_CENTER);
				// hcell19.setPaddingLeft(-70f);
				table21.addCell(hcell19);

				cell19.setFixedHeight(20f);
				cell19.setColspan(2);
				cell19.addElement(table21);
				table.addCell(cell19);

				PdfPCell cell4 = new PdfPCell();

				PdfPTable table3 = new PdfPTable(6);
				table3.setWidths(new float[] { 5f, 1f, 6f, 5f, 1f, 6f });
				table3.setSpacingBefore(10);

				PdfPCell hcell;
				hcell = new PdfPCell(new Phrase(ConstantValues.REPORT_SON_DAUGHTER_WIFE, redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setPaddingLeft(-50f);
				table3.addCell(hcell);

				hcell = new PdfPCell(new Phrase(":", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell.setPaddingLeft(-80f);
				table3.addCell(hcell);

				hcell = new PdfPCell(new Phrase(patientFatherName, redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell.setPaddingLeft(-80f);
				table3.addCell(hcell);

				Font redFont5 = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.NORMAL);

				hcell = new PdfPCell(new Phrase("Admitted Ward", redFont5));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
				table3.addCell(hcell);

				hcell = new PdfPCell(new Phrase(":", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell.setPaddingLeft(-15f);
				table3.addCell(hcell);

				hcell = new PdfPCell(new Phrase(admittedWard, redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell.setPaddingLeft(-20f);
				table3.addCell(hcell);

				PdfPCell hcell11;
				hcell11 = new PdfPCell(new Phrase("Admn.Dt", redFont));
				hcell11.setBorder(Rectangle.NO_BORDER);
				hcell11.setPaddingLeft(-50f);
				table3.addCell(hcell11);

				hcell11 = new PdfPCell(new Phrase(":", redFont));
				hcell11.setBorder(Rectangle.NO_BORDER);
				hcell11.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell11.setPaddingLeft(-80f);
				table3.addCell(hcell11);

				hcell11 = new PdfPCell(new Phrase(admissionDate, redFont));
				hcell11.setBorder(Rectangle.NO_BORDER);
				hcell11.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell11.setPaddingLeft(-80f);
				table3.addCell(hcell11);

				hcell11 = new PdfPCell(new Phrase("Department", redFont));
				hcell11.setBorder(Rectangle.NO_BORDER);
				hcell11.setHorizontalAlignment(Element.ALIGN_LEFT);
				table3.addCell(hcell11);

				hcell11 = new PdfPCell(new Phrase(":", redFont));
				hcell11.setBorder(Rectangle.NO_BORDER);
				hcell11.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell11.setPaddingLeft(-15f);
				table3.addCell(hcell11);

				hcell11 = new PdfPCell(new Phrase(dpt, redFont5));
				hcell11.setBorder(Rectangle.NO_BORDER);
				hcell11.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell11.setPaddingLeft(-20f);
				table3.addCell(hcell11);

				PdfPCell hcell14;
				hcell14 = new PdfPCell(new Phrase("Consultant", redFont));
				hcell14.setBorder(Rectangle.NO_BORDER);
				hcell14.setPaddingLeft(-50f);
				table3.addCell(hcell14);

				hcell14 = new PdfPCell(new Phrase(":", redFont));
				hcell14.setBorder(Rectangle.NO_BORDER);
				hcell14.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell14.setPaddingLeft(-80f);
				table3.addCell(hcell14);

				hcell14 = new PdfPCell(new Phrase(consultant, redFont));
				hcell14.setBorder(Rectangle.NO_BORDER);
				hcell14.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell14.setPaddingLeft(-80f);
				table3.addCell(hcell14);

				hcell14 = new PdfPCell(new Phrase("Final Due.Amt", redFont));
				hcell14.setBorder(Rectangle.NO_BORDER);
				hcell14.setHorizontalAlignment(Element.ALIGN_LEFT);
				table3.addCell(hcell14);

				hcell14 = new PdfPCell(new Phrase(":", redFont));
				hcell14.setBorder(Rectangle.NO_BORDER);
				hcell14.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell14.setPaddingLeft(-15f);
				table3.addCell(hcell14);

				hcell14 = new PdfPCell(new Phrase(String.valueOf(amount), redFont));
				hcell14.setBorder(Rectangle.NO_BORDER);
				hcell14.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell14.setPaddingLeft(-20f);
				table3.addCell(hcell14);

				PdfPCell hcell16;
				hcell16 = new PdfPCell(new Phrase("Org.", redFont));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table3.addCell(hcell16);

				hcell16 = new PdfPCell(new Phrase(":", redFont));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell16.setPaddingLeft(-80f);
				table3.addCell(hcell16);

				hcell16 = new PdfPCell(new Phrase(ConstantValues.HOSPITAL_NAME, redFont));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell16.setPaddingLeft(-80f);
				table3.addCell(hcell16);

				hcell16 = new PdfPCell(new Phrase("Amt Recieved", redFont));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table3.addCell(hcell16);

				hcell16 = new PdfPCell(new Phrase(":", redFont));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell16.setPaddingLeft(-15f);
				table3.addCell(hcell16);

				hcell16 = new PdfPCell(new Phrase(String.valueOf(amount), redFont));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell16.setPaddingLeft(-20f);
				table3.addCell(hcell16);

				PdfPCell hcell17;
				hcell17 = new PdfPCell(new Phrase("Address", redFont));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table3.addCell(hcell17);

				hcell17 = new PdfPCell(new Phrase(":", redFont));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell17.setPaddingLeft(-80f);
				table3.addCell(hcell17);

				hcell17 = new PdfPCell(new Phrase(address, redFont));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell17.setPaddingLeft(-80f);
				table3.addCell(hcell17);

				hcell17 = new PdfPCell(new Phrase(EMPTY_SPACE, redFont));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table3.addCell(hcell17);

				hcell17 = new PdfPCell(new Phrase(EMPTY_SPACE, redFont));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell17.setPaddingLeft(-15f);
				table3.addCell(hcell17);

				hcell17 = new PdfPCell(new Phrase(EMPTY_SPACE, redFont));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell17.setPaddingLeft(-20f);
				table3.addCell(hcell17);

				PdfPCell hcell18;
				hcell18 = new PdfPCell(new Phrase("City", redFont));
				hcell18.setBorder(Rectangle.NO_BORDER);
				hcell18.setPaddingLeft(-50f);
				table3.addCell(hcell18);

				hcell18 = new PdfPCell(new Phrase(":", redFont));
				hcell18.setBorder(Rectangle.NO_BORDER);
				hcell18.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell18.setPaddingLeft(-80f);
				table3.addCell(hcell18);

				hcell18 = new PdfPCell(new Phrase(city, redFont));
				hcell18.setBorder(Rectangle.NO_BORDER);
				hcell18.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell18.setPaddingLeft(-80f);
				table3.addCell(hcell18);

				hcell18 = new PdfPCell(new Phrase("State", redFont));
				hcell18.setBorder(Rectangle.NO_BORDER);
				hcell18.setHorizontalAlignment(Element.ALIGN_LEFT);
				table3.addCell(hcell18);

				hcell18 = new PdfPCell(new Phrase(":", redFont));
				hcell18.setBorder(Rectangle.NO_BORDER);
				hcell18.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell18.setPaddingLeft(-15f);
				table3.addCell(hcell18);

				hcell18 = new PdfPCell(new Phrase(state, redFont));
				hcell18.setBorder(Rectangle.NO_BORDER);
				hcell18.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell18.setPaddingLeft(-20f);
				table3.addCell(hcell18);

				PdfPCell hcell20;
				if (amount != 0) {

					hcell20 = new PdfPCell(new Phrase("Payment Mode", redFont));
					hcell20.setBorder(Rectangle.NO_BORDER);
					hcell20.setPaddingLeft(-50f);
					hcell20.setPaddingTop(10f);
					table3.addCell(hcell20);

					hcell20 = new PdfPCell(new Phrase(":", redFont));
					hcell20.setBorder(Rectangle.NO_BORDER);
					hcell20.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell20.setPaddingTop(10f);
					hcell20.setPaddingLeft(-80f);
					table3.addCell(hcell20);

					hcell20 = new PdfPCell(new Phrase(paymentType, redFont));
					hcell20.setBorder(Rectangle.NO_BORDER);
					hcell20.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell20.setPaddingTop(10f);
					hcell20.setPaddingLeft(-80f);
					table3.addCell(hcell20);
				} else {
					hcell20 = new PdfPCell(new Phrase("Payment Mode", redFont));
					hcell20.setBorder(Rectangle.NO_BORDER);
					hcell20.setPaddingLeft(-50f);
					hcell20.setPaddingTop(10f);
					table3.addCell(hcell20);

					hcell20 = new PdfPCell(new Phrase(":", redFont));
					hcell20.setBorder(Rectangle.NO_BORDER);
					hcell20.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell20.setPaddingTop(10f);
					hcell20.setPaddingLeft(-80f);
					table3.addCell(hcell20);

					hcell20 = new PdfPCell(new Phrase(paymentType, redFont));
					hcell20.setBorder(Rectangle.NO_BORDER);
					hcell20.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell20.setPaddingTop(10f);
					hcell20.setPaddingLeft(-80f);
					table3.addCell(hcell20);
				}
				if (finalCard!=0) {
					hcell20 = new PdfPCell(new Phrase("Reference No", redFont));
					hcell20.setBorder(Rectangle.NO_BORDER);
					hcell20.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell20.setPaddingTop(10f);
					table3.addCell(hcell20);

					hcell20 = new PdfPCell(new Phrase(":", redFont));
					hcell20.setBorder(Rectangle.NO_BORDER);
					hcell20.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell20.setPaddingTop(10f);
					hcell20.setPaddingLeft(-15f);
					table3.addCell(hcell20);

					hcell20 = new PdfPCell(new Phrase(referenceNumber, redFont));
					hcell20.setBorder(Rectangle.NO_BORDER);
					hcell20.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell20.setPaddingTop(10f);
					hcell20.setPaddingLeft(-20f);
					table3.addCell(hcell20);
				} else {
					hcell20 = new PdfPCell(new Phrase("", redFont));
					hcell20.setBorder(Rectangle.NO_BORDER);
					hcell20.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell20.setPaddingTop(10f);
					table3.addCell(hcell20);

					hcell20 = new PdfPCell(new Phrase("", redFont));
					hcell20.setBorder(Rectangle.NO_BORDER);
					hcell20.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell20.setPaddingTop(10f);
					hcell20.setPaddingLeft(-15f);
					table3.addCell(hcell20);

					hcell20 = new PdfPCell(new Phrase("", redFont));
					hcell20.setBorder(Rectangle.NO_BORDER);
					hcell20.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell20.setPaddingTop(10f);
					hcell20.setPaddingLeft(-20f);
					table3.addCell(hcell20);
				}

                 PdfPCell hcell161;
				
				
				if(finalCash!=0) {
					hcell161 = new PdfPCell(new Phrase("Cash Amt", redFont));
					hcell161.setBorder(Rectangle.NO_BORDER);
					hcell161.setPaddingTop(10f);
					hcell161.setPaddingLeft(-50f);
					table3.addCell(hcell161);

					hcell161 = new PdfPCell(new Phrase(":", redFont));
					hcell161.setBorder(Rectangle.NO_BORDER);
					hcell161.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell161.setPaddingTop(10f);
					hcell161.setPaddingLeft(-80f);
					table3.addCell(hcell161);

					hcell161 = new PdfPCell(new Phrase(String.valueOf(finalCash), redFont));
					hcell161.setBorder(Rectangle.NO_BORDER);
					hcell161.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell161.setPaddingTop(10f);
					hcell161.setPaddingLeft(-80f);
					table3.addCell(hcell161);
					
					
				}else {
					hcell161 = new PdfPCell(new Phrase(EMPTY_STRING, redFont));
					hcell161.setBorder(Rectangle.NO_BORDER);
					hcell161.setPaddingTop(10f);
					hcell161.setPaddingLeft(-50f);
					table3.addCell(hcell161);

					hcell161 = new PdfPCell(new Phrase(EMPTY_STRING, redFont));
					hcell161.setBorder(Rectangle.NO_BORDER);
					hcell161.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell161.setPaddingTop(10f);
					hcell161.setPaddingLeft(-80f);
					table3.addCell(hcell161);

					hcell161 = new PdfPCell(new Phrase(EMPTY_STRING, redFont));
					hcell161.setBorder(Rectangle.NO_BORDER);
					hcell161.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell161.setPaddingTop(10f);
					hcell161.setPaddingLeft(-80f);
					table3.addCell(hcell161);

					
				}

				if (finalCard!=0) {
					hcell161 = new PdfPCell(new Phrase("Card Amt", redFont));
					hcell161.setBorder(Rectangle.NO_BORDER);
					hcell161.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell161.setPaddingTop(10f);
					table3.addCell(hcell161);

					hcell161 = new PdfPCell(new Phrase(":", redFont));
					hcell161.setBorder(Rectangle.NO_BORDER);
					hcell161.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell161.setPaddingTop(10f);
					hcell161.setPaddingLeft(-15f);
					table3.addCell(hcell161);

					hcell161 = new PdfPCell(new Phrase(String.valueOf(finalCard), redFont));
					hcell161.setBorder(Rectangle.NO_BORDER);
					hcell161.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell161.setPaddingTop(10f);
					hcell161.setPaddingLeft(-20f);
					table3.addCell(hcell161); 
				}else {
					hcell161 = new PdfPCell(new Phrase(EMPTY_STRING, redFont));
					hcell161.setBorder(Rectangle.NO_BORDER);
					hcell161.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell161.setPaddingTop(10f);
					table3.addCell(hcell161);

					hcell161 = new PdfPCell(new Phrase(EMPTY_STRING, redFont));
					hcell161.setBorder(Rectangle.NO_BORDER);
					hcell161.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell161.setPaddingTop(10f);
					hcell161.setPaddingLeft(-15f);
					table3.addCell(hcell161);

					hcell161 = new PdfPCell(new Phrase(EMPTY_STRING, redFont));
					hcell161.setBorder(Rectangle.NO_BORDER);
					hcell161.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell161.setPaddingTop(10f);
					hcell161.setPaddingLeft(-20f);
					table3.addCell(hcell161); 

					
				}
				
             PdfPCell hcell1611;
				
				
				if(finalDue!=0) {
					hcell1611 = new PdfPCell(new Phrase("due Amt", redFont));
					hcell1611.setBorder(Rectangle.NO_BORDER);
					hcell1611.setPaddingTop(10f);
					hcell1611.setPaddingLeft(-50f);
					table3.addCell(hcell1611);

					hcell1611 = new PdfPCell(new Phrase(":", redFont));
					hcell1611.setBorder(Rectangle.NO_BORDER);
					hcell1611.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell1611.setPaddingTop(10f);
					hcell1611.setPaddingLeft(-80f);
					table3.addCell(hcell1611);

					hcell1611 = new PdfPCell(new Phrase(String.valueOf(finalDue), redFont));
					hcell1611.setBorder(Rectangle.NO_BORDER);
					hcell1611.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell1611.setPaddingTop(10f);
					hcell1611.setPaddingLeft(-80f);
					table3.addCell(hcell1611);
					hcell1611 = new PdfPCell(new Phrase(EMPTY_STRING, redFont));
					hcell1611.setBorder(Rectangle.NO_BORDER);
					hcell1611.setPaddingTop(10f);
					hcell1611.setPaddingLeft(-50f);
					table3.addCell(hcell1611);

					hcell1611 = new PdfPCell(new Phrase(EMPTY_STRING, redFont));
					hcell1611.setBorder(Rectangle.NO_BORDER);
					hcell1611.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell1611.setPaddingTop(10f);
					hcell1611.setPaddingLeft(-80f);
					table3.addCell(hcell1611);

					hcell1611 = new PdfPCell(new Phrase(EMPTY_STRING, redFont));
					hcell1611.setBorder(Rectangle.NO_BORDER);
					hcell1611.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell1611.setPaddingTop(10f);
					hcell1611.setPaddingLeft(-80f);
					table3.addCell(hcell1611);

					
				}

				PdfPTable table91 = new PdfPTable(1);
				table91.setWidths(new float[] { 5f });
				table91.setSpacingBefore(10);

				cell4.setFixedHeight(170f);
				cell4.setColspan(2);
				cell4.addElement(table3);

				PdfPCell hcell98;
				hcell98 = new PdfPCell(new Phrase("Received with thanks from " + patientName + ", " + "A sum of Rs. "
						+ amount + "\n\n" + "In Words Rupees " + numberToWordsConverter.convert((int) amount), redFont));
				hcell98.setBorder(Rectangle.NO_BORDER);
				hcell98.setPaddingLeft(-50f);
				hcell98.setPaddingTop(3);
				table91.addCell(hcell98);
				cell4.addElement(table91);
				table.addCell(cell4);

				PdfPCell cell5 = new PdfPCell();

				PdfPTable table35 = new PdfPTable(2);
				table35.setWidths(new float[] { 5f, 4f });
				table35.setSpacingBefore(10);

				PdfPCell hcell21;
				hcell21 = new PdfPCell(new Phrase("*" + umr + "*", headFont1));
				hcell21.setBorder(Rectangle.NO_BORDER);
				hcell21.setPaddingLeft(-50f);
				table35.addCell(hcell21);

				hcell21 = new PdfPCell(new Phrase("*" + regIde + "*", headFont1));
				hcell21.setBorder(Rectangle.NO_BORDER);
				hcell21.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table35.addCell(hcell21);

				PdfPCell hcell12;
				hcell12 = new PdfPCell(new Phrase("Created By    : " + createdName, redFont));
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
				hcell13 = new PdfPCell(new Phrase("Printed By     : " + createdName, redFont));
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
				hcell23.setPaddingTop(25f);
				hcell23.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table35.addCell(hcell23);

				cell5.setFixedHeight(105f);
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
				salesPaymentPdf.setFileName(regIde + " IP FinalDue Due");
				salesPaymentPdf.setFileuri(uri);
				salesPaymentPdf.setPid(salesPaymentPdfServiceImpl.getNextId());
				salesPaymentPdf.setData(pdfByte);
				salesPaymentPdfServiceImpl.save(salesPaymentPdf);


			} catch (Exception e) {
			}

			

		}


		return salesPaymentPdf;
	}

	public List<Object> getPatientDetails(String umrNo) {

		List<Object> patientdts = new ArrayList<>();

		PatientDetails patientDetails = patientDetailsRepository.findByUmr(umrNo);

		long pid = patientDetails.getPatientId();

		List<PatientRegistration> patientRegistrations = patientRegistrationRepository.getRegids(pid);

		String patientName = null;
		patientName = (patientDetails.getMiddleName() != null)
				? patientDetails.getTitle() + " " + patientDetails.getFirstName() + " " + patientDetails.getMiddleName()
						+ " " + patientDetails.getLastName()
				: patientDetails.getTitle() + " " + patientDetails.getFirstName() + " " + patientDetails.getLastName();

		Map<String, String> pmap = new HashMap<>();
		pmap.put("mobileNo", String.valueOf(patientDetails.getMobile()));
		pmap.put("pName", patientName);

		patientdts.add(pmap);
		return patientdts;

	}

	public List<Object> getInfo() {

		List<Object> Allllist = new ArrayList<>();
		
		List<FinalBilling> salesDueBills = finalBillingServcieImpl.findDueBills("Sales",ConstantValues.SALES_DUE, ConstantValues.YES);
		float returamt = 0;
		for (FinalBilling s : salesDueBills) {

			List<FinalBilling> returnDueBills = finalBillingServcieImpl.findByBillTypeAndBillNo("Sales Return",
					s.getBillNo());
			if(!returnDueBills.isEmpty()) {
			for(FinalBilling returnDueBillsInfo:returnDueBills) {
				returamt+=returnDueBillsInfo.getDueAmount();
				
			}
			}
			Map<String, String> saleMap = new HashMap<>();

			saleMap.put("patientName", s.getName());
			saleMap.put("umr", s.getUmrNo());
			saleMap.put("patientregid", s.getRegNo());
			saleMap.put("dueFor", "Pharmacy");
			saleMap.put("billno", s.getBillNo());
		    saleMap.put("amount", String.valueOf(s.getDueAmount() - returamt));
			Date dateAdv = s.getInsertedDate();
			DateFormat formatterAdv = new SimpleDateFormat("dd-MMM-yyyy");
			String advDate = formatterAdv.format(dateAdv).toString();
			saleMap.put("date", advDate);
			Allllist.add(saleMap);
			returamt=0;
		}

		List<FinalBilling> labDueBills = finalBillingServcieImpl.findDueBills("Laboratory Registration",ConstantValues.LAB_DUE,
				ConstantValues.YES);
		for (FinalBilling labInfo : labDueBills) {

			Map<String, String> labMap = new HashMap<>();
			Date dateAdv = labInfo.getInsertedDate();
			DateFormat formatterAdv = new SimpleDateFormat("dd-MMM-yyyy");
			String advDate = formatterAdv.format(dateAdv).toString();
			labMap.put("patientregid", labInfo.getRegNo());
			labMap.put("dueFor", "Lab");
			labMap.put("amount", String.valueOf(labInfo.getDueAmount()));
			labMap.put("umr", labInfo.getUmrNo());
			labMap.put("patientName", labInfo.getName());
			labMap.put("billno", labInfo.getBillNo());
			labMap.put("date", advDate);
			Allllist.add(labMap);

		}
		
		List<FinalBilling> ospDueBills = finalBillingServcieImpl.findDueBills("Osp Bill",ConstantValues.OSP_DUE, ConstantValues.YES);

		for (FinalBilling osp : ospDueBills) {
			String billno = osp.getBillNo();
			Map<String, String> ospMap = new HashMap<>();

			String patientname = osp.getName();

			Date dateAdv = osp.getInsertedDate();
			DateFormat formatterAdv = new SimpleDateFormat("dd-MMM-yyyy");
			String advDate = formatterAdv.format(dateAdv).toString();

			ospMap.put("umr", "");
			ospMap.put("amount", String.valueOf(osp.getDueAmount()));
			ospMap.put("patientregid", "");
			ospMap.put("patientName", patientname);
			ospMap.put("dueFor", "osp");
			ospMap.put("billno", billno);
			ospMap.put("date", advDate);
			Allllist.add(ospMap);
		}
		
		List<FinalBilling> ipFinalDue=finalBillingServcieImpl.findDueBills("Ip Final Billing","Ip Final Billing", ConstantValues.YES);
		for(FinalBilling ipFinalDueInfo:ipFinalDue) {
			Map<String, String> ipFinalMap = new HashMap<>();
			Date dateAdv = ipFinalDueInfo.getInsertedDate();
			DateFormat formatterAdv = new SimpleDateFormat("dd-MMM-yyyy");
			String advDate = formatterAdv.format(dateAdv).toString();
			ipFinalMap.put("patientregid", ipFinalDueInfo.getRegNo());
			ipFinalMap.put("dueFor", "Lab");
			ipFinalMap.put("amount", String.valueOf(ipFinalDueInfo.getDueAmount()));
			ipFinalMap.put("umr", ipFinalDueInfo.getUmrNo());
			ipFinalMap.put("patientName", ipFinalDueInfo.getName());
			ipFinalMap.put("billno", ipFinalDueInfo.getBillNo());
			ipFinalMap.put("date", advDate);
			Allllist.add(ipFinalMap);

			
		}

		return Allllist;

	}

	public List<Object> getDueBillsBasedOnType(Map<String, String> mapInfo) {

		String dueType = mapInfo.get("dueType");
		List<Object> Allllist = new ArrayList<>();

		if (dueType.equalsIgnoreCase("Pharmacy")) {
			

			List<FinalBilling> salesDueBills = finalBillingServcieImpl.findDueBills("Sales",ConstantValues.SALES_DUE, ConstantValues.YES);
			float returamt = 0;
			for (FinalBilling s : salesDueBills) {

				List<FinalBilling> returnDueBills = finalBillingServcieImpl.findByBillTypeAndBillNo("Sales Return",
						s.getBillNo());
				if(!returnDueBills.isEmpty()) {
				for(FinalBilling returnDueBillsInfo:returnDueBills) {
					returamt+=returnDueBillsInfo.getDueAmount();
					
				}
				}
				Map<String, String> saleMap = new HashMap<>();

				saleMap.put("patientName", s.getName());
				saleMap.put("umr", s.getUmrNo());
				saleMap.put("patientregid", s.getRegNo());
				saleMap.put("dueFor", "Pharmacy");
				saleMap.put("billno", s.getBillNo());
			    saleMap.put("amount", String.valueOf(s.getDueAmount() - returamt));
				Date dateAdv = s.getInsertedDate();
				DateFormat formatterAdv = new SimpleDateFormat("dd-MMM-yyyy");
				String advDate = formatterAdv.format(dateAdv).toString();
				saleMap.put("date", advDate);
				Allllist.add(saleMap);
				returamt=0;
			}

		}

		else if (dueType.equalsIgnoreCase("lab")) {
			List<FinalBilling> labDueBills = finalBillingServcieImpl.findDueBills("Laboratory Registration",ConstantValues.LAB_DUE,
					ConstantValues.YES);
			for (FinalBilling labInfo : labDueBills) {

				Map<String, String> labMap = new HashMap<>();
				Date dateAdv = labInfo.getInsertedDate();
				DateFormat formatterAdv = new SimpleDateFormat("dd-MMM-yyyy");
				String advDate = formatterAdv.format(dateAdv).toString();
				labMap.put("patientregid", labInfo.getRegNo());
				labMap.put("dueFor", "Lab");
				labMap.put("amount", String.valueOf(labInfo.getDueAmount()));
				labMap.put("umr", labInfo.getUmrNo());
				labMap.put("patientName", labInfo.getName());
				labMap.put("billno", labInfo.getBillNo());
				labMap.put("date", advDate);
				Allllist.add(labMap);

			}

		}

		else if (dueType.equalsIgnoreCase("osp")) {
			List<FinalBilling> ospDueBills = finalBillingServcieImpl.findDueBills("Osp Bill",ConstantValues.OSP_DUE, ConstantValues.YES);

			String billno = null;
			for (FinalBilling osp : ospDueBills) {
				billno = osp.getBillNo();
				Map<String, String> ospMap = new HashMap<>();

				String patientname = osp.getName();

				Date dateAdv = osp.getInsertedDate();
				DateFormat formatterAdv = new SimpleDateFormat("dd-MMM-yyyy");
				String advDate = formatterAdv.format(dateAdv).toString();

				ospMap.put("umr", "");
				ospMap.put("amount", String.valueOf(osp.getDueAmount()));
				ospMap.put("patientregid", "");
				ospMap.put("patientName", patientname);
				ospMap.put("dueFor", "osp");
				ospMap.put("billno", billno);
				ospMap.put("date", advDate);
				Allllist.add(ospMap);
			}

		} else if (dueType.equalsIgnoreCase("All")) {

			List<FinalBilling> salesDueBills = finalBillingServcieImpl.findDueBills("Sales",ConstantValues.SALES_DUE, ConstantValues.YES);
			float returamt = 0;
			for (FinalBilling s : salesDueBills) {

				List<FinalBilling> returnDueBills = finalBillingServcieImpl.findByBillTypeAndBillNo("Sales Return",
						s.getBillNo());
				if(!returnDueBills.isEmpty()) {
				for(FinalBilling returnDueBillsInfo:returnDueBills) {
					returamt+=returnDueBillsInfo.getDueAmount();
					
				}
				}
				Map<String, String> saleMap = new HashMap<>();

				saleMap.put("patientName", s.getName());
				saleMap.put("umr", s.getUmrNo());
				saleMap.put("patientregid", s.getRegNo());
				saleMap.put("dueFor", "Pharmacy");
				saleMap.put("billno", s.getBillNo());
			    saleMap.put("amount", String.valueOf(s.getDueAmount() - returamt));
				Date dateAdv = s.getInsertedDate();
				DateFormat formatterAdv = new SimpleDateFormat("dd-MMM-yyyy");
				String advDate = formatterAdv.format(dateAdv).toString();
				saleMap.put("date", advDate);
				Allllist.add(saleMap);
				returamt=0;
			}

			List<FinalBilling> ospDueBills = finalBillingServcieImpl.findDueBills("Osp Bill",ConstantValues.OSP_DUE, ConstantValues.YES);

			for (FinalBilling osp : ospDueBills) {
				String billno = osp.getBillNo();
				Map<String, String> ospMap = new HashMap<>();

				String patientname = osp.getName();

				Date dateAdv = osp.getInsertedDate();
				DateFormat formatterAdv = new SimpleDateFormat("dd-MMM-yyyy");
				String advDate = formatterAdv.format(dateAdv).toString();

				ospMap.put("umr", "");
				ospMap.put("amount", String.valueOf(osp.getDueAmount()));
				ospMap.put("patientregid", "");
				ospMap.put("patientName", patientname);
				ospMap.put("dueFor", "osp");
				ospMap.put("billno", billno);
				ospMap.put("date", advDate);
				Allllist.add(ospMap);
			}

			List<FinalBilling> labDueBills = finalBillingServcieImpl.findDueBills("Laboratory Registration",ConstantValues.LAB_DUE,
					ConstantValues.YES);
			for (FinalBilling labInfo : labDueBills) {

				Map<String, String> labMap = new HashMap<>();
				Date dateAdv = labInfo.getInsertedDate();
				DateFormat formatterAdv = new SimpleDateFormat("dd-MMM-yyyy");
				String advDate = formatterAdv.format(dateAdv).toString();
				labMap.put("patientregid", labInfo.getRegNo());
				labMap.put("dueFor", "Lab");
				labMap.put("amount", String.valueOf(labInfo.getDueAmount()));
				labMap.put("umr", labInfo.getUmrNo());
				labMap.put("patientName", labInfo.getName());
				labMap.put("billno", labInfo.getBillNo());
				labMap.put("date", advDate);
				Allllist.add(labMap);

			}
			
			
			List<FinalBilling> ipFinalDue=finalBillingServcieImpl.findDueBills("Ip Final Billing","Ip Final Billing", ConstantValues.YES);
			for(FinalBilling ipFinalDueInfo:ipFinalDue) {
				Map<String, String> ipFinalMap = new HashMap<>();
				Date dateAdv = ipFinalDueInfo.getInsertedDate();
				DateFormat formatterAdv = new SimpleDateFormat("dd-MMM-yyyy");
				String advDate = formatterAdv.format(dateAdv).toString();
				ipFinalMap.put("patientregid", ipFinalDueInfo.getRegNo());
				ipFinalMap.put("dueFor", "Lab");
				ipFinalMap.put("amount", String.valueOf(ipFinalDueInfo.getDueAmount()));
				ipFinalMap.put("umr", ipFinalDueInfo.getUmrNo());
				ipFinalMap.put("patientName", ipFinalDueInfo.getName());
				ipFinalMap.put("billno", ipFinalDueInfo.getBillNo());
				ipFinalMap.put("date", advDate);
				Allllist.add(ipFinalMap);

				
			}

		}else if(dueType.equalsIgnoreCase("IpFinalDue")) {
			
			List<FinalBilling> ipFinalDue=finalBillingServcieImpl.findDueBills("Ip Final Billing", "Ip Final Billing",ConstantValues.YES);
			for(FinalBilling ipFinalDueInfo:ipFinalDue) {
				Map<String, String> ipFinalMap = new HashMap<>();
				Date dateAdv = ipFinalDueInfo.getInsertedDate();
				DateFormat formatterAdv = new SimpleDateFormat("dd-MMM-yyyy");
				String advDate = formatterAdv.format(dateAdv).toString();
				ipFinalMap.put("patientregid", ipFinalDueInfo.getRegNo());
				ipFinalMap.put("dueFor", "Lab");
				ipFinalMap.put("amount", String.valueOf(ipFinalDueInfo.getDueAmount()));
				ipFinalMap.put("umr", ipFinalDueInfo.getUmrNo());
				ipFinalMap.put("patientName", ipFinalDueInfo.getName());
				ipFinalMap.put("billno", ipFinalDueInfo.getBillNo());
				ipFinalMap.put("date", advDate);
				Allllist.add(ipFinalMap);

				
			}
			
			
		}

		return Allllist;

	}
}
