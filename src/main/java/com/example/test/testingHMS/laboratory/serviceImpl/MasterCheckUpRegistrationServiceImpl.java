package com.example.test.testingHMS.laboratory.serviceImpl;

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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.test.testingHMS.MoneyToWords.NumberToWordsConverter;
import com.example.test.testingHMS.finalBilling.model.FinalBilling;
import com.example.test.testingHMS.finalBilling.serviceImpl.FinalBillingServiceImpl;
import com.example.test.testingHMS.laboratory.helper.MasterCheckUpHelper;
import com.example.test.testingHMS.laboratory.model.MasterCheckUpRegistration;
import com.example.test.testingHMS.laboratory.model.MasterCheckupService;
import com.example.test.testingHMS.laboratory.repository.MasterCheckUpRegistrationRepository;
import com.example.test.testingHMS.laboratory.repository.MasterCheckupServiceRepository;
import com.example.test.testingHMS.laboratory.service.MasterCheckUpRegistrationService;
import com.example.test.testingHMS.patient.Helper.MultiplePayment;
import com.example.test.testingHMS.patient.model.PatientDetails;
import com.example.test.testingHMS.patient.model.PatientRegistration;
import com.example.test.testingHMS.patient.serviceImpl.PatientRegistrationServiceImpl;
import com.example.test.testingHMS.pharmacist.helper.RefSales;
import com.example.test.testingHMS.pharmacist.model.Location;
import com.example.test.testingHMS.pharmacist.model.MedicineDetails;
import com.example.test.testingHMS.pharmacist.model.MedicineProcurement;
import com.example.test.testingHMS.pharmacist.model.SalesPaymentPdf;
import com.example.test.testingHMS.pharmacist.serviceImpl.LocationServiceImpl;
import com.example.test.testingHMS.pharmacist.serviceImpl.SalesPaymentPdfServiceImpl;
import com.example.test.testingHMS.pharmacyShopDetails.model.PharmacyShopDetails;
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
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@Service
public class MasterCheckUpRegistrationServiceImpl implements MasterCheckUpRegistrationService {

	@Autowired
	LocationServiceImpl locationServiceImpl;

	@Autowired
	private MasterCheckUpRegistrationRepository masterCheckUpRegistrationRepository;

	@Autowired
	private PatientRegistrationServiceImpl patientRegistrationServiceImpl;

	@Autowired
	private MasterCheckupServiceRepository masterCheckupServiceRepository;

	@Autowired
	UserServiceImpl userServiceImpl;

	@Autowired
	FinalBillingServiceImpl finalBillingServcieImpl;
	
	
	@Autowired
	ResourceLoader resourceLoader;

	@Value("${hospital.logo}")
	private Resource hospitalLogo;


	@Autowired
	NumberToWordsConverter numberToWordsConverter;

	
	@Autowired
	SalesPaymentPdfServiceImpl salesPaymentPdfServiceImpl;

	public String getNextBillNo() {
		MasterCheckUpRegistration masterCheckUpRegistration = masterCheckUpRegistrationRepository
				.findFirstByOrderByBillNoDesc();
		String nextId = null;
		if (masterCheckUpRegistration == null) {
			nextId = "BL000000001";
		} else {
			int nextIntId = Integer.parseInt(masterCheckUpRegistration.getBillNo().substring(2));
			nextIntId += 1;
			nextId = "BL" + String.format("%09d", nextIntId);
		}
		return nextId;
	}

	public String getNextCheckupId() {
		MasterCheckUpRegistration masterCheckUpRegistration = masterCheckUpRegistrationRepository
				.findFirstByOrderByCheckUpRegistrationIdDesc();
		String nextId = null;
		if (masterCheckUpRegistration == null) {
			nextId = "MCR0000001";
		} else {
			int nextIntId = Integer.parseInt(masterCheckUpRegistration.getCheckUpRegistrationId().substring(3));
			nextIntId += 1;
			nextId = "MCR" + String.format("%07d", nextIntId);
		}
		return nextId;
	}

	public List<Object> pageRefresh() {
		List<Object> displayList = new ArrayList<>();

		Map<String, String> map = new HashMap<String, String>();
		map.put("billNo", getNextBillNo());
		List<MasterCheckupService> masterCheckupServices = masterCheckupServiceRepository.getAllServices();
		displayList.add(map);
		displayList.add(masterCheckupServices);
		Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
		map.put("date",timestamp.toString().substring(0, 10));
		return displayList;
	}
	
	
	@Override
	public SalesPaymentPdf registerMastercheckupServices(MasterCheckUpRegistration masterCheckUpRegistration,
			Principal principal) {
		String regId = null;
		SalesPaymentPdf salesPaymentPdf = null;
		PatientRegistration patientRegistration = null;
		float finalCash = 0; // final billing
		float finalCard = 0; // final billing
		float finalCheque = 0; // final billing
		float finalDue = 0; // final billing
		// float finalNetAmount = 0;
		String billNo = "";
		String umr = "";
		float finalAmount = 0;
		String paid = null;
		String paymentType = null;

		String payCash = null;
		String payCard = null;
		String payDue = null;
		String payCheque = null;

		/*
		 * for multiple payments
		 */
		List<MultiplePayment> multiplePayment = masterCheckUpRegistration.getMultiplePayment();

		for (MultiplePayment multiplePaymentInfo : multiplePayment) {

			if (multiplePaymentInfo.getPayType().equalsIgnoreCase(ConstantValues.CARD)
					|| multiplePaymentInfo.getPayType().equalsIgnoreCase("Credit Card")
					|| multiplePaymentInfo.getPayType().equalsIgnoreCase("Debit Card")
					|| multiplePaymentInfo.getPayType().equalsIgnoreCase(ConstantValues.CASH_PLUS_CARD)) {
				masterCheckUpRegistration.setReferenceNumber(masterCheckUpRegistration.getReferenceNumber());
			}

			if (multiplePaymentInfo.getPayType().equalsIgnoreCase(ConstantValues.CASH)) {
				finalCash = multiplePaymentInfo.getAmount();
				payCash = ConstantValues.CASH;
			} else if (multiplePaymentInfo.getPayType().equalsIgnoreCase(ConstantValues.CARD)) {
				finalCard = multiplePaymentInfo.getAmount();
				payCard = ConstantValues.CARD;
			} else if (multiplePaymentInfo.getPayType().equalsIgnoreCase(ConstantValues.CHEQUE)) {
				finalCheque = multiplePaymentInfo.getAmount();
				payCheque = ConstantValues.CHEQUE;
			} else if (multiplePaymentInfo.getPayType().equalsIgnoreCase(ConstantValues.DUE)) {
				finalDue = multiplePaymentInfo.getAmount();
				payDue = ConstantValues.DUE;
			}

		}

		// for paytype and paid condition
		if (payDue != null && payCash == null && payCard == null) {
			paid = ConstantValues.NO;
			paymentType = ConstantValues.DUE;
		} else if (payCash != null && payDue == null && payCard == null) {
			paid = ConstantValues.YES;
			paymentType = ConstantValues.CASH;

		} else if (payCard != null && payDue == null && payCash == null) {
			paid = ConstantValues.YES;
			paymentType = ConstantValues.CARD;
		} else {
			paid = ConstantValues.PARTLY_PAID;
			paymentType = ConstantValues.MULTIPLE_PAYMENT;

		}

		if (masterCheckUpRegistration.getRegId() != null) {
			patientRegistration = patientRegistrationServiceImpl.findByRegId(masterCheckUpRegistration.getRegId());

			if (patientRegistration.isBlockedStatus()) {
				throw new RuntimeException("Payment for this patinet is blocked !");
			}

			PatientDetails patientDetails = patientRegistration.getPatientDetails();

			masterCheckUpRegistration.setUmr(patientDetails.getUmr());
			masterCheckUpRegistration
					.setPatientName(patientDetails.getFirstName() + " " + patientDetails.getLastName());
			masterCheckUpRegistration.setMobileNo(patientDetails.getMobile());
			masterCheckUpRegistration.setCheckuppatientRegistration(patientRegistration);

		}
		User userSecurity = userServiceImpl.findByUserName(principal.getName());
		String createdBy = userSecurity.getFirstName() + " " + userSecurity.getLastName();
		masterCheckUpRegistration.setPatientName(masterCheckUpRegistration.getPatientName());
		masterCheckUpRegistration.setMobileNo(masterCheckUpRegistration.getMobileNo());
		masterCheckUpRegistration.setBillDate(Timestamp.valueOf(LocalDateTime.now()));
		masterCheckUpRegistration.setCheckUpRegistrationUser(userSecurity);
		Location location = locationServiceImpl.findByLocationName(masterCheckUpRegistration.getLocation());
		masterCheckUpRegistration.setPatientcheckUplocation(location);
		;
		masterCheckUpRegistration.setBillNo(getNextBillNo());
		masterCheckUpRegistration.setUpdatedBy(userSecurity.getUserId());
		masterCheckUpRegistration.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
		masterCheckUpRegistration.setCreatedBy(userSecurity.getUserId());
		masterCheckUpRegistration.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));

		billNo = masterCheckUpRegistration.getBillNo();
		masterCheckUpRegistration.setPaymentType(paymentType);
		masterCheckUpRegistration.setPaid(paid);

		String billNoo = getNextBillNo();
		List<MasterCheckUpHelper> masterCheckUpHelper = masterCheckUpRegistration.getMasterCheckUpHelper();

		for (MasterCheckUpHelper masterCheckUpHelperInfo : masterCheckUpHelper) {
			masterCheckUpRegistration.setCheckUpRegistrationId(getNextCheckupId());
			masterCheckUpRegistration.setCheckupName(masterCheckUpHelperInfo.getServiceName());
			masterCheckUpRegistration.setPrice(masterCheckUpHelperInfo.getCost());
			finalAmount += masterCheckUpHelperInfo.getCost();
			//List<MasterCheckupService> masterCheckUpService=masterCheckupServiceRepository.findByMasterServiceName(masterCheckUpHelperInfo.getServiceName());
			
			masterCheckUpRegistrationRepository.save(masterCheckUpRegistration);

		}

		// for final billing
		FinalBilling finalBilling = new FinalBilling();
		finalBilling.setBillNo(billNo);
		if (finalDue != 0) {
			finalBilling.setDueStatus(ConstantValues.YES);
		} else {
			finalBilling.setDueStatus(ConstantValues.NO);
		}
		finalBilling.setBillType("master checkup");
		finalBilling.setCardAmount(finalCard);
		finalBilling.setCashAmount(finalCash);
		finalBilling.setChequeAmount(finalCheque);
		// finalBilling.setDiscAmount(Math.round(finalAmount - finalNetAmount));
		finalBilling.setDiscAmount(masterCheckUpRegistration.getDiscount());
		finalBilling.setUpdatedBy(userSecurity.getUserId());
		finalBilling.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
		finalBilling.setDueAmount(finalDue);
		finalBilling.setInsertedDate(Timestamp.valueOf(LocalDateTime.now()));
		finalBilling.setFinalAmountPaid(Math.round(masterCheckUpRegistration.getNetAmount()));
		finalBilling.setFinalBillUser(userSecurity);
		finalBilling.setName(masterCheckUpRegistration.getPatientName());
		finalBilling.setRegNo((regId != null) ? regId : null);
		finalBilling.setMobile(masterCheckUpRegistration.getMobileNo());
		finalBilling.setPaymentType(paymentType);
		finalBilling.setTotalAmount(masterCheckUpRegistration.getTotalAmount());
		finalBilling.setUmrNo(umr);
		finalBillingServcieImpl.computeSave(finalBilling);
		
		
		
		if(patientRegistration!=null) {
		byte[] pdfByte = null;
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		Document document = new Document(PageSize.A4_LANDSCAPE);
		try {

			Resource fileResourcee = resourceLoader.getResource(ConstantValues.IMAGE_PNG_CLASSPATH);
			Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
			Font redFonts = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.NORMAL);
			Font redFont2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
			Font redFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
			Font redFont3 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
			PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);

			document.open();
			PdfPTable table = new PdfPTable(2);

			Image img = Image.getInstance(hospitalLogo.getURL());
			img.scaleAbsolute(ConstantValues.IMAGE_ABSOLUTE_INTIAL_POSITION,
					ConstantValues.IMAGE_ABSOLUTE_FINAL_POSITION);
			table.setWidthPercentage(ConstantValues.TABLE_SET_WIDTH_PERECENTAGE);

			Phrase pq = new Phrase(new Chunk(img, ConstantValues.IMAGE_SET_INTIAL_POSITION,
					ConstantValues.IMAGE_SET_FINAL_POSITION));

			pq.add(new Chunk(ConstantValues.PHARAMACY_RECEIPT, redFont));

			PdfPCell cellp = new PdfPCell(pq);
			PdfPCell cell1 = new PdfPCell();

			// for header bold
			PdfPTable table96 = new PdfPTable(1);
			table96.setWidths(new float[] { 5f });
			table96.setSpacingBefore(10);

			PdfPCell hcell96;
			hcell96 = new PdfPCell(new Phrase(ConstantValues.HOSPITAL_NAME, redFont1));
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

			hcell1 = new PdfPCell(new Phrase(masterCheckUpRegistration.getBillNo(), redFont));
			hcell1.setBorder(Rectangle.NO_BORDER);
			hcell1.setPaddingLeft(-25f);
			table2.addCell(hcell1);

			// Display a date in day, month, year format
			Date date = Calendar.getInstance().getTime();
			DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
			String today = formatter.format(date).toString();

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

			PdfPCell hcell18;
			hcell18 = new PdfPCell(new Phrase("Patient Name", redFont));
			hcell18.setBorder(Rectangle.NO_BORDER);
			hcell18.setPaddingLeft(-15f);
			table2.addCell(hcell18);

			hcell18 = new PdfPCell(new Phrase(":", redFont));
			hcell18.setBorder(Rectangle.NO_BORDER);
			hcell18.setPaddingLeft(-35f);
			table2.addCell(hcell18);

			hcell18 = new PdfPCell(new Phrase(masterCheckUpRegistration.getPatientName(), redFont));
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

			hcel = new PdfPCell(
					new Phrase(masterCheckUpRegistration.getUmr(), redFont));
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

			hcel11 = new PdfPCell(new Phrase(masterCheckUpRegistration.getRegId(), redFont));
			hcel11.setBorder(Rectangle.NO_BORDER);
			hcel11.setPaddingLeft(-25f);
			hcel11.setHorizontalAlignment(Element.ALIGN_LEFT);
			table2.addCell(hcel11);

			cell0.setFixedHeight(100f);
			cell0.setColspan(2);
			cell0.addElement(table2);
			table.addCell(cell0);

			PdfPCell cell19 = new PdfPCell();

			PdfPTable table21 = new PdfPTable(3);
			table21.setWidths(new float[] { 4f, 4f, 5f });
			table21.setSpacingBefore(10);

			PdfPCell hcell15;
			hcell15 = new PdfPCell(new Phrase("", redFont));
			hcell15.setBorder(Rectangle.NO_BORDER);
			hcell15.setPaddingLeft(-70f);
			table21.addCell(hcell15);

			hcell15 = new PdfPCell(new Phrase("Master HealthCheckup Reciept", redFont3));
			hcell15.setBorder(Rectangle.NO_BORDER);
			hcell15.setPaddingLeft(-35);
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

			PdfPTable table1 = new PdfPTable(3);
			table1.setWidths(new float[] { 1.5f, 5.4f, 4.5f});

			table1.setSpacingBefore(10);

			PdfPCell hcell;
			hcell = new PdfPCell(new Phrase("S.No", redFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
			table1.addCell(hcell);

			hcell = new PdfPCell(new Phrase("Service Name", redFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
			table1.addCell(hcell);

			hcell = new PdfPCell(new Phrase("Cost", redFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell.setPaddingLeft(-15f);

			table1.addCell(hcell);
			int count = 0;

			PdfPTable table20 = new PdfPTable(3);
			table20.setWidths(new float[] { 1.5f, 5.4f, 4.5f });

			table20.setSpacingBefore(10);


			for (MasterCheckUpHelper masterCheckUpHelperInfo : masterCheckUpHelper) {
			
				PdfPCell cell;

				cell = new PdfPCell(new Phrase(String.valueOf(count = count + 1), redFonts));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table20.addCell(cell);

				cell = new PdfPCell(new Phrase(masterCheckUpHelperInfo.getServiceName(), redFonts));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setPaddingLeft(-1);
				// cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				// cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				table20.addCell(cell);

				cell = new PdfPCell(new Phrase(String.valueOf(masterCheckUpHelperInfo.getCost()), redFonts));
				cell.setBorder(Rectangle.NO_BORDER);
				// cell.setPaddingLeft(-30);
				// cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				// cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell.setPaddingLeft(-15f);
				table20.addCell(cell);

					}
			cell3.setColspan(2);
			table1.setWidthPercentage(100f);
			table20.setWidthPercentage(100f);
			cell3.addElement(table1);
			cell3.addElement(table20);
			table.addCell(cell3);
			PdfPCell cell4 = new PdfPCell();

			PdfPTable table4 = new PdfPTable(6);
			table4.setWidths(new float[] { 5f, 1f, 5f, 9f, 1f, 3f });
			table4.setSpacingBefore(10);

			int ttl = (int) Math.round(masterCheckUpRegistration.getNetAmount());
			PdfPCell hcell2;
			hcell2 = new PdfPCell(new Phrase("Recieved Sum of Rupees", redFont));
			hcell2.setBorder(Rectangle.NO_BORDER);
			hcell2.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell2.setPaddingLeft(-50f);
			table4.addCell(hcell2);

			hcell2 = new PdfPCell(new Phrase(":", redFont));
			hcell2.setBorder(Rectangle.NO_BORDER);
			hcell2.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell2.setPaddingLeft(-40f);
			table4.addCell(hcell2);

			hcell2 = new PdfPCell(new Phrase(numberToWordsConverter.convert(ttl) + " Rupees Only", redFont));
			hcell2.setBorder(Rectangle.NO_BORDER);
			hcell2.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell2.setPaddingLeft(-50f);
			table4.addCell(hcell2);

			hcell2 = new PdfPCell(new Phrase("", redFont));
			hcell2.setBorder(Rectangle.NO_BORDER);
			hcell2.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell2.setPaddingLeft(85f);
			table4.addCell(hcell2);

			hcell2 = new PdfPCell(new Phrase("", redFont));
			hcell2.setBorder(Rectangle.NO_BORDER);
			hcell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
			hcell2.setPaddingRight(-30f);
			table4.addCell(hcell2);

			hcell2 = new PdfPCell(new Phrase("", redFont));
			hcell2.setBorder(Rectangle.NO_BORDER);
			hcell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
			hcell2.setPaddingRight(-40f);
			table4.addCell(hcell2);

			PdfPCell hcell21;
			if (finalCash != 0) {
				hcell21 = new PdfPCell(new Phrase("Cash Amt", redFont));
				hcell21.setBorder(Rectangle.NO_BORDER);
				hcell21.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell21.setPaddingLeft(-50f);
				table4.addCell(hcell21);

				hcell21 = new PdfPCell(new Phrase(":", redFont));
				hcell21.setBorder(Rectangle.NO_BORDER);
				hcell21.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell21.setPaddingLeft(-80f);
				table4.addCell(hcell21);

				hcell21 = new PdfPCell(new Phrase(String.valueOf(finalCash), redFont));
				hcell21.setBorder(Rectangle.NO_BORDER);
				hcell21.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell21.setPaddingLeft(-90f);
				table4.addCell(hcell21);
			} else {
				hcell21 = new PdfPCell(new Phrase("", redFont));
				hcell21.setBorder(Rectangle.NO_BORDER);
				hcell21.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell21.setPaddingLeft(-50f);
				table4.addCell(hcell21);

				hcell21 = new PdfPCell(new Phrase("", redFont));
				hcell21.setBorder(Rectangle.NO_BORDER);
				hcell21.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell21.setPaddingLeft(-50f);
				table4.addCell(hcell21);

				hcell21 = new PdfPCell(new Phrase("", redFont));
				hcell21.setBorder(Rectangle.NO_BORDER);
				hcell21.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell21.setPaddingLeft(-40f);
				table4.addCell(hcell21);
			}
			hcell21 = new PdfPCell(new Phrase("Total Value", redFont));
			hcell21.setBorder(Rectangle.NO_BORDER);
			hcell21.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell21.setPaddingLeft(85f);
			table4.addCell(hcell21);

			hcell21 = new PdfPCell(new Phrase(":", redFont));
			hcell21.setBorder(Rectangle.NO_BORDER);
			hcell21.setHorizontalAlignment(Element.ALIGN_RIGHT);
			hcell21.setPaddingRight(-30f);
			table4.addCell(hcell21);

			hcell21 = new PdfPCell(new Phrase(String.valueOf(Math.round(masterCheckUpRegistration.getTotalAmount() * 100.0) / 100.0), redFont));
			hcell21.setBorder(Rectangle.NO_BORDER);
			hcell21.setHorizontalAlignment(Element.ALIGN_RIGHT);
			hcell21.setPaddingRight(-40f);
			table4.addCell(hcell21);

			PdfPCell hcell04;
			if (finalCard != 0) {
				hcell04 = new PdfPCell(new Phrase("Card Amt", redFont));
				hcell04.setBorder(Rectangle.NO_BORDER);
				hcell04.setPaddingLeft(-50f);
				hcell04.setHorizontalAlignment(Element.ALIGN_LEFT);
				table4.addCell(hcell04);

				hcell04 = new PdfPCell(new Phrase(":", redFont));
				hcell04.setBorder(Rectangle.NO_BORDER);
				hcell04.setPaddingLeft(-80f);
				hcell04.setHorizontalAlignment(Element.ALIGN_LEFT);
				table4.addCell(hcell04);

				hcell04 = new PdfPCell(new Phrase(String.valueOf(finalCard), redFont));
				hcell04.setBorder(Rectangle.NO_BORDER);
				hcell04.setPaddingLeft(-90f);
				hcell04.setHorizontalAlignment(Element.ALIGN_LEFT);
				table4.addCell(hcell04);
			} else {
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
			hcell04 = new PdfPCell(new Phrase("Rounded Off To", redFont));
			hcell04.setBorder(Rectangle.NO_BORDER);
			hcell04.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell04.setPaddingLeft(85f);
			table4.addCell(hcell04);

			hcell04 = new PdfPCell(new Phrase(":", redFont));
			hcell04.setBorder(Rectangle.NO_BORDER);
			hcell04.setHorizontalAlignment(Element.ALIGN_RIGHT);
			hcell04.setPaddingRight(-30f);
			table4.addCell(hcell04);

		
			hcell04 = new PdfPCell(new Phrase(String.valueOf(Math.round(masterCheckUpRegistration.getTotalAmount())), redFont));
			hcell04.setBorder(Rectangle.NO_BORDER);
			hcell04.setHorizontalAlignment(Element.ALIGN_RIGHT);
			hcell04.setPaddingRight(-40f);
			table4.addCell(hcell04);
								PdfPCell hcell041;
			hcell041 = new PdfPCell(new Phrase("", redFont));
			hcell041.setBorder(Rectangle.NO_BORDER);
			hcell041.setPaddingLeft(-50f);
			hcell041.setHorizontalAlignment(Element.ALIGN_LEFT);
			table4.addCell(hcell041);

			hcell041 = new PdfPCell(new Phrase("", redFont));
			hcell041.setBorder(Rectangle.NO_BORDER);
			hcell041.setPaddingLeft(-80f);
			hcell041.setHorizontalAlignment(Element.ALIGN_LEFT);
			table4.addCell(hcell041);

			hcell041 = new PdfPCell(new Phrase("", redFont));
			hcell041.setBorder(Rectangle.NO_BORDER);
			hcell041.setPaddingLeft(-90f);
			hcell041.setHorizontalAlignment(Element.ALIGN_LEFT);
			table4.addCell(hcell041);
			hcell041 = new PdfPCell(new Phrase("Disc Amt", redFont));
			hcell041.setBorder(Rectangle.NO_BORDER);
			hcell041.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell041.setPaddingLeft(85f);
			table4.addCell(hcell041);

			hcell041 = new PdfPCell(new Phrase(":", redFont));
			hcell041.setBorder(Rectangle.NO_BORDER);
			hcell041.setHorizontalAlignment(Element.ALIGN_RIGHT);
			hcell041.setPaddingRight(-30f);
			table4.addCell(hcell041);

			hcell041 = new PdfPCell(new Phrase(String.valueOf(Math.round(masterCheckUpRegistration.getDiscount())), redFont));
			hcell041.setBorder(Rectangle.NO_BORDER);
			hcell041.setHorizontalAlignment(Element.ALIGN_RIGHT);
			hcell041.setPaddingRight(-40f);
			table4.addCell(hcell041);					
								
			PdfPCell hcell9;
			if (finalDue != 0) {
				hcell9 = new PdfPCell(new Phrase("Due Amt", redFont));
				hcell9.setBorder(Rectangle.NO_BORDER);
				hcell9.setPaddingLeft(-50f);
				hcell9.setHorizontalAlignment(Element.ALIGN_LEFT);
				table4.addCell(hcell9);

				hcell9 = new PdfPCell(new Phrase(":", redFont));
				hcell9.setBorder(Rectangle.NO_BORDER);
				hcell9.setPaddingLeft(-80f);
				hcell9.setHorizontalAlignment(Element.ALIGN_LEFT);
				table4.addCell(hcell9);

				hcell9 = new PdfPCell(new Phrase(String.valueOf(finalDue), redFont));
				hcell9.setBorder(Rectangle.NO_BORDER);
				hcell9.setPaddingLeft(-90f);
				hcell9.setHorizontalAlignment(Element.ALIGN_LEFT);
				table4.addCell(hcell9);
			} else {
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
			}
			hcell9 = new PdfPCell(new Phrase("Reciept Amount", redFont));
			hcell9.setBorder(Rectangle.NO_BORDER);
			hcell9.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell9.setPaddingLeft(85f);
			table4.addCell(hcell9);

			hcell9 = new PdfPCell(new Phrase(":", redFont));
			hcell9.setBorder(Rectangle.NO_BORDER);
			hcell9.setHorizontalAlignment(Element.ALIGN_RIGHT);
			hcell9.setPaddingRight(-30f);
			table4.addCell(hcell9);

			hcell9 = new PdfPCell(new Phrase(String.valueOf(Math.round(masterCheckUpRegistration.getTotalAmount())-masterCheckUpRegistration.getDiscount()), redFont));
			hcell9.setBorder(Rectangle.NO_BORDER);
			hcell9.setHorizontalAlignment(Element.ALIGN_RIGHT);
			hcell9.setPaddingRight(-40f);
			table4.addCell(hcell9);
			cell4.setFixedHeight(80f);
			cell4.setColspan(2);
			cell4.addElement(table4);
			table.addCell(cell4);
			// for new row

			PdfPCell cell33 = new PdfPCell();

			PdfPTable table13 = new PdfPTable(5);
			table13.setWidths(new float[] { 2f, 3f, 3f, 3f, 3f });

			table13.setSpacingBefore(10);

			PdfPCell hcell33;
			hcell33 = new PdfPCell(new Phrase("Pay Mode", redFont1));
			hcell33.setBorder(Rectangle.NO_BORDER);
			hcell33.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell33.setPaddingLeft(10f);
			table13.addCell(hcell33);

			hcell33 = new PdfPCell(new Phrase("Amount", redFont1));
			hcell33.setBorder(Rectangle.NO_BORDER);
			hcell33.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell33.setPaddingLeft(35f);
			table13.addCell(hcell33);

			hcell33 = new PdfPCell(new Phrase("Card#", redFont1));
			hcell33.setBorder(Rectangle.NO_BORDER);
			hcell33.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell33.setPaddingLeft(40f);
			table13.addCell(hcell33);

			hcell33 = new PdfPCell(new Phrase("Bank Name", redFont1));
			hcell33.setBorder(Rectangle.NO_BORDER);
			hcell33.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell33.setPaddingLeft(40f);
			table13.addCell(hcell33);

			hcell33 = new PdfPCell(new Phrase("Exp Date", redFont1));
			hcell33.setBorder(Rectangle.NO_BORDER);
			hcell33.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell33.setPaddingLeft(50f);
			table13.addCell(hcell33);

			PdfPCell hcell34;
			hcell34 = new PdfPCell(new Phrase(masterCheckUpRegistration.getPaymentType(), redFont2));
			hcell34.setBorder(Rectangle.NO_BORDER);
			hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell34.setPaddingLeft(10f);
			table13.addCell(hcell34);

			hcell34 = new PdfPCell(new Phrase(String.valueOf(Math.round(masterCheckUpRegistration.getPrice())), redFont2));
			hcell34.setBorder(Rectangle.NO_BORDER);
			hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell34.setPaddingLeft(35f);
			table13.addCell(hcell34);
			if (masterCheckUpRegistration.getPaymentType().equalsIgnoreCase("card")
					|| masterCheckUpRegistration.getPaymentType().equalsIgnoreCase("cash+card")) {
				hcell34.setBorder(Rectangle.NO_BORDER);
				hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell34.setPaddingLeft(40f);
				table13.addCell(hcell34);
			} else {
				hcell34 = new PdfPCell(new Phrase("", redFont2));
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

			// cell33.setFixedHeight(35f);
			cell33.setColspan(2);
			table13.setWidthPercentage(100f);
			cell33.addElement(table13);
			table.addCell(cell33);

			
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

			cell6.setFixedHeight(80f);
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
			salesPaymentPdf.setFileName(billNo + "-" + regId + " Master Health Checkup");
			salesPaymentPdf.setFileuri(uri);
			salesPaymentPdf.setPid(salesPaymentPdfServiceImpl.getNextId());
			salesPaymentPdf.setData(pdfByte);
			salesPaymentPdfServiceImpl.save(salesPaymentPdf);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		}else {
			
			byte[] pdfByte = null;
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			Document document = new Document(PageSize.A4_LANDSCAPE);
			try {

				Resource fileResourcee = resourceLoader.getResource(ConstantValues.IMAGE_PNG_CLASSPATH);
				Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
				Font redFonts = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.NORMAL);
				Font redFont2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
				Font redFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
				Font redFont3 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
				PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);

				document.open();
				PdfPTable table = new PdfPTable(2);

				Image img = Image.getInstance(hospitalLogo.getURL());
				img.scaleAbsolute(ConstantValues.IMAGE_ABSOLUTE_INTIAL_POSITION,
						ConstantValues.IMAGE_ABSOLUTE_FINAL_POSITION);
				table.setWidthPercentage(ConstantValues.TABLE_SET_WIDTH_PERECENTAGE);

				Phrase pq = new Phrase(new Chunk(img, ConstantValues.IMAGE_SET_INTIAL_POSITION,
						ConstantValues.IMAGE_SET_FINAL_POSITION));

				pq.add(new Chunk(ConstantValues.PHARAMACY_RECEIPT, redFont));

				PdfPCell cellp = new PdfPCell(pq);
				PdfPCell cell1 = new PdfPCell();

				// for header bold
				PdfPTable table96 = new PdfPTable(1);
				table96.setWidths(new float[] { 5f });
				table96.setSpacingBefore(10);

				PdfPCell hcell96;
				hcell96 = new PdfPCell(new Phrase(ConstantValues.HOSPITAL_NAME, redFont1));
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

				hcell1 = new PdfPCell(new Phrase(masterCheckUpRegistration.getBillNo(), redFont));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(-25f);
				table2.addCell(hcell1);

				// Display a date in day, month, year format
				Date date = Calendar.getInstance().getTime();
				DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
				String today = formatter.format(date).toString();

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

				PdfPCell hcell18;
				hcell18 = new PdfPCell(new Phrase("Patient Name", redFont));
				hcell18.setBorder(Rectangle.NO_BORDER);
				hcell18.setPaddingLeft(-15f);
				table2.addCell(hcell18);

				hcell18 = new PdfPCell(new Phrase(":", redFont));
				hcell18.setBorder(Rectangle.NO_BORDER);
				hcell18.setPaddingLeft(-35f);
				table2.addCell(hcell18);

				hcell18 = new PdfPCell(new Phrase(masterCheckUpRegistration.getPatientName(), redFont));
				hcell18.setBorder(Rectangle.NO_BORDER);
				hcell18.setPaddingLeft(-25f);
				table2.addCell(hcell18);
				cell0.setFixedHeight(100f);
				cell0.setColspan(2);
				cell0.addElement(table2);
				table.addCell(cell0);

				PdfPCell cell19 = new PdfPCell();

				PdfPTable table21 = new PdfPTable(3);
				table21.setWidths(new float[] { 4f, 4f, 5f });
				table21.setSpacingBefore(10);

				PdfPCell hcell15;
				hcell15 = new PdfPCell(new Phrase("", redFont));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-70f);
				table21.addCell(hcell15);

				hcell15 = new PdfPCell(new Phrase("Master HealthCheckup Reciept", redFont3));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-35);
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

				PdfPTable table1 = new PdfPTable(3);
				table1.setWidths(new float[] { 1.5f, 5.4f, 4.5f});

				table1.setSpacingBefore(10);

				PdfPCell hcell;
				hcell = new PdfPCell(new Phrase("S.No", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
				
				hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("Service Name", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("Cost", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell.setPaddingLeft(-15f);

				table1.addCell(hcell);
				int count = 0;

				PdfPTable table20 = new PdfPTable(3);
				table20.setWidths(new float[] { 1.5f, 5.4f, 4.5f });

				table20.setSpacingBefore(10);


				for (MasterCheckUpHelper masterCheckUpHelperInfo : masterCheckUpHelper) {
				
					PdfPCell cell;

					cell = new PdfPCell(new Phrase(String.valueOf(count = count + 1), redFonts));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table20.addCell(cell);

					cell = new PdfPCell(new Phrase(masterCheckUpHelperInfo.getServiceName(), redFonts));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(-1);
					// cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					// cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					table20.addCell(cell);

					cell = new PdfPCell(new Phrase(String.valueOf(masterCheckUpHelperInfo.getCost()), redFonts));
					cell.setBorder(Rectangle.NO_BORDER);
					// cell.setPaddingLeft(-30);
					// cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					// cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell.setPaddingLeft(-15f);
					table20.addCell(cell);

						}
				cell3.setColspan(2);
				table1.setWidthPercentage(100f);
				table20.setWidthPercentage(100f);
				cell3.addElement(table1);
				cell3.addElement(table20);
				table.addCell(cell3);
				PdfPCell cell4 = new PdfPCell();

				PdfPTable table4 = new PdfPTable(6);
				table4.setWidths(new float[] { 5f, 1f, 5f, 9f, 1f, 3f });
				table4.setSpacingBefore(10);

				int ttl = (int) Math.round(masterCheckUpRegistration.getNetAmount());
				PdfPCell hcell2;
				hcell2 = new PdfPCell(new Phrase("Recieved Sum of Rupees", redFont));
				hcell2.setBorder(Rectangle.NO_BORDER);
				hcell2.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell2.setPaddingLeft(-50f);
				table4.addCell(hcell2);

				hcell2 = new PdfPCell(new Phrase(":", redFont));
				hcell2.setBorder(Rectangle.NO_BORDER);
				hcell2.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell2.setPaddingLeft(-40f);
				table4.addCell(hcell2);

				hcell2 = new PdfPCell(new Phrase(numberToWordsConverter.convert(ttl) + " Rupees Only", redFont));
				hcell2.setBorder(Rectangle.NO_BORDER);
				hcell2.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell2.setPaddingLeft(-50f);
				table4.addCell(hcell2);

				hcell2 = new PdfPCell(new Phrase("", redFont));
				hcell2.setBorder(Rectangle.NO_BORDER);
				hcell2.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell2.setPaddingLeft(85f);
				table4.addCell(hcell2);

				hcell2 = new PdfPCell(new Phrase("", redFont));
				hcell2.setBorder(Rectangle.NO_BORDER);
				hcell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell2.setPaddingRight(-30f);
				table4.addCell(hcell2);

				hcell2 = new PdfPCell(new Phrase("", redFont));
				hcell2.setBorder(Rectangle.NO_BORDER);
				hcell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell2.setPaddingRight(-40f);
				table4.addCell(hcell2);

				PdfPCell hcell21;
				if (finalCash != 0) {
					hcell21 = new PdfPCell(new Phrase("Cash Amt", redFont));
					hcell21.setBorder(Rectangle.NO_BORDER);
					hcell21.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell21.setPaddingLeft(-50f);
					table4.addCell(hcell21);

					hcell21 = new PdfPCell(new Phrase(":", redFont));
					hcell21.setBorder(Rectangle.NO_BORDER);
					hcell21.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell21.setPaddingLeft(-80f);
					table4.addCell(hcell21);

					hcell21 = new PdfPCell(new Phrase(String.valueOf(finalCash), redFont));
					hcell21.setBorder(Rectangle.NO_BORDER);
					hcell21.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell21.setPaddingLeft(-90f);
					table4.addCell(hcell21);
				} else {
					hcell21 = new PdfPCell(new Phrase("", redFont));
					hcell21.setBorder(Rectangle.NO_BORDER);
					hcell21.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell21.setPaddingLeft(-50f);
					table4.addCell(hcell21);

					hcell21 = new PdfPCell(new Phrase("", redFont));
					hcell21.setBorder(Rectangle.NO_BORDER);
					hcell21.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell21.setPaddingLeft(-50f);
					table4.addCell(hcell21);

					hcell21 = new PdfPCell(new Phrase("", redFont));
					hcell21.setBorder(Rectangle.NO_BORDER);
					hcell21.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell21.setPaddingLeft(-40f);
					table4.addCell(hcell21);
				}
				hcell21 = new PdfPCell(new Phrase("Total Value", redFont));
				hcell21.setBorder(Rectangle.NO_BORDER);
				hcell21.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell21.setPaddingLeft(85f);
				table4.addCell(hcell21);

				hcell21 = new PdfPCell(new Phrase(":", redFont));
				hcell21.setBorder(Rectangle.NO_BORDER);
				hcell21.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell21.setPaddingRight(-30f);
				table4.addCell(hcell21);

				hcell21 = new PdfPCell(new Phrase(String.valueOf(Math.round(masterCheckUpRegistration.getTotalAmount() * 100.0) / 100.0), redFont));
				hcell21.setBorder(Rectangle.NO_BORDER);
				hcell21.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell21.setPaddingRight(-40f);
				table4.addCell(hcell21);

				PdfPCell hcell04;
				if (finalCard != 0) {
					hcell04 = new PdfPCell(new Phrase("Card Amt", redFont));
					hcell04.setBorder(Rectangle.NO_BORDER);
					hcell04.setPaddingLeft(-50f);
					hcell04.setHorizontalAlignment(Element.ALIGN_LEFT);
					table4.addCell(hcell04);

					hcell04 = new PdfPCell(new Phrase(":", redFont));
					hcell04.setBorder(Rectangle.NO_BORDER);
					hcell04.setPaddingLeft(-80f);
					hcell04.setHorizontalAlignment(Element.ALIGN_LEFT);
					table4.addCell(hcell04);

					hcell04 = new PdfPCell(new Phrase(String.valueOf(finalCard), redFont));
					hcell04.setBorder(Rectangle.NO_BORDER);
					hcell04.setPaddingLeft(-90f);
					hcell04.setHorizontalAlignment(Element.ALIGN_LEFT);
					table4.addCell(hcell04);
				} else {
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
				hcell04 = new PdfPCell(new Phrase("Rounded Off To", redFont));
				hcell04.setBorder(Rectangle.NO_BORDER);
				hcell04.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell04.setPaddingLeft(85f);
				table4.addCell(hcell04);

				hcell04 = new PdfPCell(new Phrase(":", redFont));
				hcell04.setBorder(Rectangle.NO_BORDER);
				hcell04.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell04.setPaddingRight(-30f);
				table4.addCell(hcell04);

			
				hcell04 = new PdfPCell(new Phrase(String.valueOf(Math.round(masterCheckUpRegistration.getTotalAmount())), redFont));
				hcell04.setBorder(Rectangle.NO_BORDER);
				hcell04.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell04.setPaddingRight(-40f);
				table4.addCell(hcell04);
									PdfPCell hcell041;
				hcell041 = new PdfPCell(new Phrase("", redFont));
				hcell041.setBorder(Rectangle.NO_BORDER);
				hcell041.setPaddingLeft(-50f);
				hcell041.setHorizontalAlignment(Element.ALIGN_LEFT);
				table4.addCell(hcell041);

				hcell041 = new PdfPCell(new Phrase("", redFont));
				hcell041.setBorder(Rectangle.NO_BORDER);
				hcell041.setPaddingLeft(-80f);
				hcell041.setHorizontalAlignment(Element.ALIGN_LEFT);
				table4.addCell(hcell041);

				hcell041 = new PdfPCell(new Phrase("", redFont));
				hcell041.setBorder(Rectangle.NO_BORDER);
				hcell041.setPaddingLeft(-90f);
				hcell041.setHorizontalAlignment(Element.ALIGN_LEFT);
				table4.addCell(hcell041);
				hcell041 = new PdfPCell(new Phrase("Disc Amt", redFont));
				hcell041.setBorder(Rectangle.NO_BORDER);
				hcell041.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell041.setPaddingLeft(85f);
				table4.addCell(hcell041);

				hcell041 = new PdfPCell(new Phrase(":", redFont));
				hcell041.setBorder(Rectangle.NO_BORDER);
				hcell041.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell041.setPaddingRight(-30f);
				table4.addCell(hcell041);

				hcell041 = new PdfPCell(new Phrase(String.valueOf(Math.round(masterCheckUpRegistration.getDiscount())), redFont));
				hcell041.setBorder(Rectangle.NO_BORDER);
				hcell041.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell041.setPaddingRight(-40f);
				table4.addCell(hcell041);					
									
				PdfPCell hcell9;
				if (finalDue != 0) {
					hcell9 = new PdfPCell(new Phrase("Due Amt", redFont));
					hcell9.setBorder(Rectangle.NO_BORDER);
					hcell9.setPaddingLeft(-50f);
					hcell9.setHorizontalAlignment(Element.ALIGN_LEFT);
					table4.addCell(hcell9);

					hcell9 = new PdfPCell(new Phrase(":", redFont));
					hcell9.setBorder(Rectangle.NO_BORDER);
					hcell9.setPaddingLeft(-80f);
					hcell9.setHorizontalAlignment(Element.ALIGN_LEFT);
					table4.addCell(hcell9);

					hcell9 = new PdfPCell(new Phrase(String.valueOf(finalDue), redFont));
					hcell9.setBorder(Rectangle.NO_BORDER);
					hcell9.setPaddingLeft(-90f);
					hcell9.setHorizontalAlignment(Element.ALIGN_LEFT);
					table4.addCell(hcell9);
				} else {
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
				}
				hcell9 = new PdfPCell(new Phrase("Reciept Amount", redFont));
				hcell9.setBorder(Rectangle.NO_BORDER);
				hcell9.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell9.setPaddingLeft(85f);
				table4.addCell(hcell9);

				hcell9 = new PdfPCell(new Phrase(":", redFont));
				hcell9.setBorder(Rectangle.NO_BORDER);
				hcell9.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell9.setPaddingRight(-30f);
				table4.addCell(hcell9);

				hcell9 = new PdfPCell(new Phrase(String.valueOf(Math.round(masterCheckUpRegistration.getTotalAmount())-masterCheckUpRegistration.getDiscount()), redFont));
				hcell9.setBorder(Rectangle.NO_BORDER);
				hcell9.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell9.setPaddingRight(-40f);
				table4.addCell(hcell9);
				cell4.setFixedHeight(80f);
				cell4.setColspan(2);
				cell4.addElement(table4);
				table.addCell(cell4);
				// for new row

				PdfPCell cell33 = new PdfPCell();

				PdfPTable table13 = new PdfPTable(5);
				table13.setWidths(new float[] { 2f, 3f, 3f, 3f, 3f });

				table13.setSpacingBefore(10);

				PdfPCell hcell33;
				hcell33 = new PdfPCell(new Phrase("Pay Mode", redFont1));
				hcell33.setBorder(Rectangle.NO_BORDER);
				hcell33.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell33.setPaddingLeft(10f);
				table13.addCell(hcell33);

				hcell33 = new PdfPCell(new Phrase("Amount", redFont1));
				hcell33.setBorder(Rectangle.NO_BORDER);
				hcell33.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell33.setPaddingLeft(35f);
				table13.addCell(hcell33);

				hcell33 = new PdfPCell(new Phrase("Card#", redFont1));
				hcell33.setBorder(Rectangle.NO_BORDER);
				hcell33.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell33.setPaddingLeft(40f);
				table13.addCell(hcell33);

				hcell33 = new PdfPCell(new Phrase("Bank Name", redFont1));
				hcell33.setBorder(Rectangle.NO_BORDER);
				hcell33.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell33.setPaddingLeft(40f);
				table13.addCell(hcell33);

				hcell33 = new PdfPCell(new Phrase("Exp Date", redFont1));
				hcell33.setBorder(Rectangle.NO_BORDER);
				hcell33.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell33.setPaddingLeft(50f);
				table13.addCell(hcell33);

				PdfPCell hcell34;
				hcell34 = new PdfPCell(new Phrase(masterCheckUpRegistration.getPaymentType(), redFont2));
				hcell34.setBorder(Rectangle.NO_BORDER);
				hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell34.setPaddingLeft(10f);
				table13.addCell(hcell34);

				hcell34 = new PdfPCell(new Phrase(String.valueOf(Math.round(masterCheckUpRegistration.getPrice())), redFont2));
				hcell34.setBorder(Rectangle.NO_BORDER);
				hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell34.setPaddingLeft(35f);
				table13.addCell(hcell34);
				if (masterCheckUpRegistration.getPaymentType().equalsIgnoreCase("card")
						|| masterCheckUpRegistration.getPaymentType().equalsIgnoreCase("cash+card")) {
					hcell34.setBorder(Rectangle.NO_BORDER);
					hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell34.setPaddingLeft(40f);
					table13.addCell(hcell34);
				} else {
					hcell34 = new PdfPCell(new Phrase("", redFont2));
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

				// cell33.setFixedHeight(35f);
				cell33.setColspan(2);
				table13.setWidthPercentage(100f);
				cell33.addElement(table13);
				table.addCell(cell33);

				
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

				cell6.setFixedHeight(80f);
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
				salesPaymentPdf.setFileName(billNo + "-" + " Master Health Checkup");
				salesPaymentPdf.setFileuri(uri);
				salesPaymentPdf.setPid(salesPaymentPdfServiceImpl.getNextId());
				salesPaymentPdf.setData(pdfByte);
				salesPaymentPdfServiceImpl.save(salesPaymentPdf);
			
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
			
		}
		return salesPaymentPdf;
	}


	}
