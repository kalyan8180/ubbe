package com.example.test.testingHMS.pharmacist.serviceImpl;

import java.io.ByteArrayOutputStream;

import java.security.Principal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
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
import com.example.test.testingHMS.bill.model.ChargeBill;
import com.example.test.testingHMS.bill.serviceImpl.ChargeBillServiceImpl;
import com.example.test.testingHMS.finalBilling.model.FinalBilling;
import com.example.test.testingHMS.finalBilling.serviceImpl.FinalBillingServiceImpl;
import com.example.test.testingHMS.patient.model.PatientDetails;
import com.example.test.testingHMS.patient.model.PatientRegistration;
import com.example.test.testingHMS.patient.serviceImpl.PatientRegistrationServiceImpl;
import com.example.test.testingHMS.pharmacist.helper.RefSalesReturn;
import com.example.test.testingHMS.pharmacist.model.Location;
import com.example.test.testingHMS.pharmacist.model.MedicineDetails;
import com.example.test.testingHMS.pharmacist.model.MedicineProcurement;
import com.example.test.testingHMS.pharmacist.model.MedicineQuantity;
import com.example.test.testingHMS.pharmacist.model.Sales;
import com.example.test.testingHMS.pharmacist.model.SalesPaymentPdf;
import com.example.test.testingHMS.pharmacist.model.SalesRefund;
import com.example.test.testingHMS.pharmacist.model.SalesReturn;
import com.example.test.testingHMS.pharmacist.repository.MedicineQuantityRepository;
import com.example.test.testingHMS.pharmacist.repository.SalesRefundRepository;
import com.example.test.testingHMS.pharmacist.repository.SalesRepository;
import com.example.test.testingHMS.pharmacist.repository.SalesReturnRepository;
import com.example.test.testingHMS.pharmacist.service.SalesReturnService;
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
public class SalesReturnServiceImpl implements SalesReturnService {
	private static final Logger Logger = LoggerFactory.getLogger(SalesReturnServiceImpl.class);

	@Value("${hospital.logo}")
	private Resource hospitalLogo;

	@Autowired
	SalesReturnRepository salesReturnRepository;

	@Autowired
	SalesServiceImpl salesServiceImpl;

	/*
	 * @Autowired PatientSalesRepository patientSalesRepository;
	 * 
	 */@Autowired
	ResourceLoader resourceLoader;

	@Autowired
	UserServiceImpl userServiceImpl;

	@Autowired
	SalesPaymentPdf salesPaymentPdf;

	@Autowired
	SalesPaymentPdfServiceImpl salesPaymentPdfServiceImpl;

	@Autowired
	ChargeBillServiceImpl chargeBillServiceImpl;

	@Autowired
	SalesRepository salesRepository;

	@Autowired
	SalesRefundServiceImpl salesRefundServiceImpl;

	@Autowired
	SalesRefundRepository salesRefundRepository;

	@Autowired
	MedicineDetailsServiceImpl medicineDetailsServiceImpl;

	@Autowired
	PatientRegistrationServiceImpl patientRegistrationServiceImpl;

	@Autowired
	MedicineQuantityServiceImpl medicineQuantityServiceImpl;

	@Autowired
	MedicineQuantityRepository medicineQuantityRepository;

	@Autowired
	LocationServiceImpl locationServiceImpl;

	@Autowired
	NumberToWordsConverter numberToWordsConverter;

	@Autowired
	FinalBillingServiceImpl finalBillingServiceImpl;

	public String getNextReturnSaleNo() {
		SalesReturn salesReturn = salesReturnRepository.findFirstByOrderBySaleNoDesc();
		String nextId = null;
		if (salesReturn == null) {
			nextId = "RSL0000001";
		} else {
			int nextIntId = Integer.parseInt(salesReturn.getSaleNo().substring(3));
			nextIntId += 1;
			nextId = "RSL" + String.format("%07d", nextIntId);
		}
		return nextId;
	}

	public String getNextReturnMasterSaleNo() {
		SalesReturn salesReturn = salesReturnRepository.findFirstByOrderByMasterSaleNoDesc();
		String nextId = null;
		if (salesReturn == null) {
			nextId = "MRSL0000001";
		} else {
			int nextIntId = Integer.parseInt(salesReturn.getMasterSaleNo().substring(4));
			nextIntId += 1;
			nextId = "MRSL" + String.format("%07d", nextIntId);
		}
		return nextId;

	}

	public List<SalesReturn> findByMasterSaleNo(String id) {
		return salesReturnRepository.findByMasterSaleNo(id);
	}

	public static PdfPCell createCell(String content, float borderWidth, int colspan, int alignment, Font redFont) {
		PdfPCell cell = new PdfPCell(new Phrase(content));
		cell.setBorderWidth(borderWidth);
		cell.setColspan(colspan);
		cell.setHorizontalAlignment(alignment);
		return cell;
	}

	@Transactional
	public SalesPaymentPdf computeSave(SalesReturn salesReturn, Principal principal) {

		// createdBy (Security)
		User userSecurity = userServiceImpl.findByUserName(principal.getName());
		String createdBy = userSecurity.getFirstName() + " " + userSecurity.getLastName();

		String billNo = "";
		String regId = null;
		String umr = null;
		float finalNetAmount = 0;
		String paymentMode = "";
		String patientName = "";
		long mobile = 0;

		billNo = salesReturn.getBillNo();
		PatientRegistration patientRegistration = null;
		if (salesReturn.getRegId() != null) {
			patientRegistration = patientRegistrationServiceImpl.findByRegId(salesReturn.getRegId());
			if (patientRegistration.isBlockedStatus()) {
				throw new RuntimeException("Payment for this patinet is blocked !");
			}

			PatientDetails patientDetails = patientRegistration.getPatientDetails();
			if (patientDetails.getMiddleName() == null) {
				patientName = patientDetails.getTitle() + ". " + patientDetails.getFirstName()
						+ ConstantValues.ONE_SPACE_STRING + patientDetails.getLastName();
			} else {
				patientName = patientDetails.getTitle() + ". " + patientDetails.getFirstName()
						+ ConstantValues.ONE_SPACE_STRING + patientDetails.getMiddleName()
						+ ConstantValues.ONE_SPACE_STRING + patientDetails.getLastName();
			}

			regId = patientRegistration.getRegId();
			umr = patientRegistration.getPatientDetails().getUmr();
			salesReturn.setSalesReturnPatientRegistration(patientRegistration);
		}

		List<RefSalesReturn> refSales = salesReturn.getRefSalesReturns();
		salesReturn.setDate(Timestamp.valueOf(LocalDateTime.now()));
		Location location = locationServiceImpl.findByLocationName(salesReturn.getLocation());
		salesReturn.setSalesReturnLocation(location);
		salesReturn.setMasterSaleNo(getNextReturnMasterSaleNo());
		salesReturn.setRaisedBy(userSecurity.getUserId());
		salesReturn.setSalesReturnUser(userSecurity);
		paymentMode = salesReturn.getPaymentType();
		for (RefSalesReturn refSalesList : refSales) {

			salesReturn.setSaleNo(getNextReturnSaleNo());
			salesReturn.setAmount(refSalesList.getAmount());
			salesReturn.setDiscount(refSalesList.getDiscount());

			salesReturn.setMedicineName(refSalesList.getMedicineName());
			salesReturn.setQuantity(refSalesList.getQuantity());

			// Sales sales=salesRepository.findOneBill(salesReturn.getBillNo(),
			// refSalesList.getMedicineName(), refSalesList.getBatchNo());
			Sales sales = salesRepository.findBySaleNo(refSalesList.getSaleId());
			salesReturn.setGst(sales.getGst());
			refSalesList.setGst(sales.getGst());
			// PatientSales
			// patientSales=patientSalesRepository.findOneBill(salesReturn.getBillNo(),
			// refSalesList.getMedicineName(), refSalesList.getBatchNo());

			ChargeBill chargeBill = chargeBillServiceImpl.findBySaleId(sales);

			if (sales.getQuantity() == 0) {
				throw new RuntimeException("0 Quantity Cann't Be Updated " + refSalesList.getMedicineName());
			}
			sales.setAmount(sales.getAmount() - refSalesList.getAmount());
			sales.setQuantity(sales.getQuantity() - refSalesList.getQuantity());
			sales.setCostPrice(sales.getMrp() * sales.getQuantity());
			mobile = sales.getMobileNo();
			salesReturn.setBatchNo(sales.getBatchNo());
			salesReturn.setMrp(sales.getMrp());
			salesReturn.setName(sales.getName());
			salesReturn.setMobileNo(sales.getMobileNo());

			finalNetAmount += salesReturn.getAmount();

			if (patientRegistration != null
					&& (patientRegistration.getpType().equalsIgnoreCase(ConstantValues.INPATIENT)
							|| patientRegistration.getpType().equalsIgnoreCase(ConstantValues.DAYCARE))) {

				chargeBill.setAmount(sales.getCostPrice());
				chargeBill.setQuantity(sales.getQuantity());
				chargeBill.setNetAmount(sales.getAmount());
				chargeBillServiceImpl.save(chargeBill);

			}
			// for refund management
			SalesRefund salesRefund = new SalesRefund();
			salesRefund.setReturnId(salesRefundServiceImpl.getNextReturnId());
			salesRefund.setAmount(salesReturn.getAmount());
			salesRefund.setMobileNo(salesReturn.getMobileNo());
			salesRefund.setRefundBy(createdBy);
			salesReturn.setSalesReturnUser(userSecurity);
			salesRefund.setName(salesReturn.getName());
			salesRefund.setMobileNo(salesReturn.getMobileNo());
			salesRefund.setPaymentType(salesReturn.getPaymentType());

			// After security
			if (salesReturn.getSalesReturnPatientRegistration() == null) {
				salesRefund.setRefundBy(salesReturn.getName());
			} else {
				salesRefund.setUmr(patientRegistration.getPatientDetails().getUmr());
				salesRefund.setRefundBy(patientRegistration.getPatientDetails().getFirstName() + " "
						+ patientRegistration.getPatientDetails().getLastName());
				salesRefund.setSalesRefundPatientRegistration(salesReturn.getSalesReturnPatientRegistration());
			}
			salesRefund.setStatus("Not-Approved");
			salesRefund.setRefundDate(salesReturn.getDate());
			salesRefund.setReturnAmount(salesReturn.getAmount());
			salesRefund.setBillNo(salesReturn.getBillNo());

			// salesRefund.setSalesRefundPatientRegistration();

			salesRefundRepository.save(salesRefund);
			salesRepository.save(sales);
			salesReturnRepository.save(salesReturn);

			// for medicine quantity
			MedicineQuantity medicineQuantity = new MedicineQuantity();

			MedicineDetails medicineDetails = medicineDetailsServiceImpl.findByName(salesReturn.getMedicineName());
			MedicineQuantity medicineQuantityInfo = medicineQuantityServiceImpl
					.findByBatchIdAndMedicineDetails(sales.getBatchNo(), medicineDetails);

			if (medicineQuantityInfo != null) {

				long totalReturn = salesReturn.getQuantity();
				medicineQuantityInfo.setBalance(medicineQuantityInfo.getBalance() + totalReturn);
				medicineQuantityInfo.setSold(medicineQuantityInfo.getSold() - totalReturn);
				medicineQuantityRepository.save(medicineQuantityInfo);

			}

		}

		// Final Billing
		FinalBilling finalBilling = new FinalBilling();
		finalBilling.setBillNo(billNo);
		finalBilling.setBillType("Sales Return");
		if (paymentMode.equalsIgnoreCase("Cash")) {
			finalBilling.setCashAmount(Math.round(finalNetAmount));
		} else if (paymentMode.equalsIgnoreCase("Card")) {
			finalBilling.setCardAmount(Math.round(finalNetAmount));
		} else if (paymentMode.equalsIgnoreCase("Cheque")) {
			finalBilling.setChequeAmount(Math.round(finalNetAmount));
		} else if (paymentMode.equalsIgnoreCase(ConstantValues.DUE)) {
			finalBilling.setDueAmount(Math.round(finalNetAmount));

		}
		finalBilling.setDueStatus(ConstantValues.NO);
		finalBilling.setUpdatedBy(userSecurity.getUserId());
		finalBilling.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
		finalBilling.setInsertedDate(Timestamp.valueOf(LocalDateTime.now()));
		finalBilling.setFinalAmountPaid(Math.round(finalNetAmount));
		finalBilling.setFinalBillUser(userSecurity);
		finalBilling.setName((regId != null) ? patientName : salesReturn.getName());
		finalBilling.setRegNo((regId != null) ? regId : null);
		finalBilling.setMobile(mobile);
		finalBilling.setPaymentType(paymentMode);
		finalBilling.setTotalAmount(finalNetAmount);
		finalBilling.setUmrNo((umr != null) ? umr : null);
		finalBillingServiceImpl.computeSave(finalBilling);

		SalesPaymentPdf salesPaymentPdf = null;

		// To find total
		float total = 0;
		List<SalesReturn> salesReturnList = findByMasterSaleNo(salesReturn.getMasterSaleNo());
		for (SalesReturn salesReturnInfo : salesReturnList) {
			total += salesReturnInfo.getAmount();
		}

		if (patientRegistration != null) {

			byte[] pdfByte = null;
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			try {
				Document document = new Document(PageSize.A4_LANDSCAPE);

				Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

				Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
				Font headFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
				PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);
				document.open();

				// Display a date in day, month, year format
				Date date = Calendar.getInstance().getTime();
				DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
				String today = formatter.format(date).toString();

				Resource fileResourcee = resourceLoader.getResource(ConstantValues.IMAGE_PNG_CLASSPATH);
				PdfPTable table = new PdfPTable(2);

				Image img = Image.getInstance(hospitalLogo.getURL());
				img.scaleAbsolute(ConstantValues.IMAGE_ABSOLUTE_INTIAL_POSITION,
						ConstantValues.IMAGE_ABSOLUTE_FINAL_POSITION);
				table.setWidthPercentage(ConstantValues.TABLE_SET_WIDTH_PERECENTAGE);

				Phrase pq = new Phrase(new Chunk(img, ConstantValues.IMAGE_SET_INTIAL_POSITION,
						ConstantValues.IMAGE_SET_FINAL_POSITION));

				pq.add(new Chunk(ConstantValues.SALES_RETURN, redFont));
				PdfPCell cellp = new PdfPCell(pq);
				PdfPCell cell1 = new PdfPCell();

				// for header Bold
				PdfPTable table96 = new PdfPTable(1);
				table96.setWidths(new float[] { 5f });
				table96.setSpacingBefore(10);

				PdfPCell hcell96;
				hcell96 = new PdfPCell(new Phrase(ConstantValues.PHARMACY_NAME, headFont1));
				hcell96.setBorder(Rectangle.NO_BORDER);
				hcell96.setHorizontalAlignment(Element.ALIGN_CENTER);
				table96.addCell(hcell96);
				cell1.addElement(table96);

				// for header end
				cell1.setFixedHeight(130f);
				cell1.addElement(pq);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setColspan(2);
				table.addCell(cell1);

				PdfPCell cell19 = new PdfPCell();

				PdfPTable table21 = new PdfPTable(1);
				table21.setWidths(new float[] { 4f });
				table21.setSpacingBefore(10);

				PdfPCell hcell19;
				hcell19 = new PdfPCell(new Phrase("Patient Sales Returns ", headFont1));
				hcell19.setBorder(Rectangle.NO_BORDER);
				hcell19.setHorizontalAlignment(Element.ALIGN_CENTER);
				table21.addCell(hcell19);

				cell19.setFixedHeight(20f);
				cell19.setColspan(2);
				cell19.addElement(table21);
				table.addCell(cell19);

				PdfPCell cell3 = new PdfPCell();

				PdfPTable table2 = new PdfPTable(6);
				table2.setWidths(new float[] { 5f, 1f, 4f, 5f, 1f, 5f });
				table2.setSpacingBefore(10);

				PdfPCell hcell3;
				hcell3 = new PdfPCell(new Phrase("UMR#", redFont));
				hcell3.setBorder(Rectangle.NO_BORDER);
				hcell3.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell3.setPaddingLeft(-50f);
				table2.addCell(hcell3);

				hcell3 = new PdfPCell(new Phrase(":", redFont));
				hcell3.setBorder(Rectangle.NO_BORDER);
				hcell3.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell3.setPaddingLeft(-100f);
				table2.addCell(hcell3);

				hcell3 = new PdfPCell(new Phrase(umr, redFont));
				hcell3.setBorder(Rectangle.NO_BORDER);
				hcell3.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell3.setPaddingLeft(-100f);
				table2.addCell(hcell3);

				hcell3 = new PdfPCell(new Phrase("Reg Id", redFont));
				hcell3.setBorder(Rectangle.NO_BORDER);
				hcell3.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell3.setPaddingLeft(60f);
				table2.addCell(hcell3);

				hcell3 = new PdfPCell(new Phrase(":", redFont));
				hcell3.setBorder(Rectangle.NO_BORDER);
				hcell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell3.setPaddingRight(10f);
				table2.addCell(hcell3);

				hcell3 = new PdfPCell(new Phrase(regId, redFont));
				hcell3.setBorder(Rectangle.NO_BORDER);
				hcell3.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell3.setPaddingLeft(7f);
				table2.addCell(hcell3);

				PdfPCell hcell1;
				hcell1 = new PdfPCell(new Phrase("Ret#", redFont));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table2.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase(":", redFont));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-100f);
				table2.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase(salesReturn.getSaleNo(), redFont));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-100f);
				table2.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase("Ret Dt", redFont));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(60f);
				table2.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase(":", redFont));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell1.setPaddingRight(10f);
				table2.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase(today, redFont));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(5f);
				table2.addCell(hcell1);

				PdfPCell hcell2;
				hcell2 = new PdfPCell(new Phrase("Patient Name", redFont));
				hcell2.setBorder(Rectangle.NO_BORDER);
				hcell2.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell2.setPaddingLeft(-50f);
				table2.addCell(hcell2);

				hcell2 = new PdfPCell(new Phrase(":", redFont));
				hcell2.setBorder(Rectangle.NO_BORDER);
				hcell2.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell2.setPaddingLeft(-100f);
				table2.addCell(hcell2);

				hcell2 = new PdfPCell(new Phrase(
						salesReturn.getSalesReturnPatientRegistration().getPatientDetails().getTitle() + ". "
								+ salesReturn.getSalesReturnPatientRegistration().getPatientDetails().getFirstName()
								+ " "
								+ salesReturn.getSalesReturnPatientRegistration().getPatientDetails().getLastName(),
						redFont));
				hcell2.setBorder(Rectangle.NO_BORDER);
				hcell2.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell2.setPaddingLeft(-100f);
				table2.addCell(hcell2);

				hcell2 = new PdfPCell(new Phrase("Bill#", redFont));
				hcell2.setBorder(Rectangle.NO_BORDER);
				hcell2.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell2.setPaddingLeft(60f);
				table2.addCell(hcell2);

				hcell2 = new PdfPCell(new Phrase(":", redFont));
				hcell2.setBorder(Rectangle.NO_BORDER);
				hcell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell2.setPaddingRight(10f);
				table2.addCell(hcell2);

				hcell2 = new PdfPCell(new Phrase(salesReturn.getBillNo(), redFont));
				hcell2.setBorder(Rectangle.NO_BORDER);
				hcell2.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell2.setPaddingLeft(5f);
				table2.addCell(hcell2);

				PdfPCell hcell9;
				hcell9 = new PdfPCell(new Phrase("Created By", redFont));
				hcell9.setBorder(Rectangle.NO_BORDER);
				hcell9.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell9.setPaddingLeft(-50f);
				table2.addCell(hcell9);

				hcell9 = new PdfPCell(new Phrase(":", redFont));
				hcell9.setBorder(Rectangle.NO_BORDER);
				hcell9.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell9.setPaddingLeft(-100f);
				table2.addCell(hcell9);

				hcell9 = new PdfPCell(new Phrase(createdBy, redFont));
				hcell9.setBorder(Rectangle.NO_BORDER);
				hcell9.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell9.setPaddingLeft(-100f);
				table2.addCell(hcell9);

				hcell9 = new PdfPCell(new Phrase("Created Dt", redFont));
				hcell9.setBorder(Rectangle.NO_BORDER);
				hcell9.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell9.setPaddingLeft(60f);
				table2.addCell(hcell9);

				hcell9 = new PdfPCell(new Phrase(":", redFont));
				hcell9.setBorder(Rectangle.NO_BORDER);
				hcell9.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell9.setPaddingRight(10f);
				table2.addCell(hcell9);

				hcell9 = new PdfPCell(new Phrase(today, redFont));
				hcell9.setBorder(Rectangle.NO_BORDER);
				hcell9.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell9.setPaddingLeft(7f);
				table2.addCell(hcell9);

				cell3.addElement(table2);
				cell3.setColspan(2);
				cell3.setFixedHeight(70f);
				table.addCell(cell3);

				PdfPCell cell31 = new PdfPCell();

				PdfPTable table1 = new PdfPTable(8);
				table1.setWidths(new float[] { 0.5f, 2.5f, 0.9f, 2.5f, 1f, 2f, 2f, 2f });

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

				hcell = new PdfPCell(new Phrase("Batch#", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("Exp Dt", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("Qty", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("Sale Rate", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("GST", redFont));
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
				for (RefSalesReturn a : refSales) {

					PdfPCell cell;

					MedicineDetails medicineDetails1 = medicineDetailsServiceImpl.findByName(a.getMedicineName());
					List<MedicineProcurement> medicineProcurement = medicineDetails1.getMedicineProcurement();

					cell = new PdfPCell(new Phrase(String.valueOf(count = count + 1), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table1.addCell(cell);

					cell = new PdfPCell(new Phrase(a.getMedicineName(), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					// cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					table1.addCell(cell);

					cell = new PdfPCell(new Phrase(a.getBatchNo(), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					// cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					table1.addCell(cell);

					cell = new PdfPCell(new Phrase(String.valueOf(a.getExpDate()).substring(0, 10), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					// cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					table1.addCell(cell);

					cell = new PdfPCell(new Phrase(String.valueOf(a.getQuantity()), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table1.addCell(cell);

					cell = new PdfPCell(new Phrase(String.valueOf(a.getMrp()), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table1.addCell(cell);

					cell = new PdfPCell(new Phrase(String.valueOf((a.getGst())), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table1.addCell(cell);

					cell = new PdfPCell(new Phrase(String.valueOf((a.getAmount())), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table1.addCell(cell);

				}

				cell31.setColspan(2);
				table1.setWidthPercentage(100f);
				cell31.addElement(table1);
				table.addCell(cell31);

				// -------------------------------
				PdfPCell cell12 = new PdfPCell();

				PdfPTable table37 = new PdfPTable(6);
				table37.setWidths(new float[] { 5f, 1f, 4f, 7f, 1f, 8f });
				table37.setSpacingBefore(10);

				PdfPCell cell55;
				cell55 = new PdfPCell(new Phrase("", redFont));
				cell55.setBorder(Rectangle.NO_BORDER);
				cell55.setPaddingTop(10f);
				cell55.setPaddingLeft(-50f);
				table37.addCell(cell55);

				cell55 = new PdfPCell(new Phrase("", redFont));
				cell55.setBorder(Rectangle.NO_BORDER);
				cell55.setPaddingTop(10f);
				cell55.setPaddingLeft(-50f);
				table37.addCell(cell55);

				cell55 = new PdfPCell(new Phrase("", redFont));
				cell55.setBorder(Rectangle.NO_BORDER);
				cell55.setPaddingTop(10f);
				cell55.setPaddingLeft(-50f);
				table37.addCell(cell55);

				cell55 = new PdfPCell(new Phrase("Total Sale Value", redFont));
				cell55.setBorder(Rectangle.NO_BORDER);
				cell55.setPaddingRight(-30f);
				cell55.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table37.addCell(cell55);

				cell55 = new PdfPCell(new Phrase(":", redFont));
				cell55.setBorder(Rectangle.NO_BORDER);
				cell55.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell55.setPaddingRight(-50f);
				table37.addCell(cell55);

				cell55 = new PdfPCell(new Phrase(String.valueOf(total), redFont));
				cell55.setBorder(Rectangle.NO_BORDER);
				cell55.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell55.setPaddingRight(-15f);
				table37.addCell(cell55);

				PdfPCell cell6;
				cell6 = new PdfPCell(new Phrase("", redFont));
				cell6.setBorder(Rectangle.NO_BORDER);
				cell6.setPaddingLeft(-50f);
				table37.addCell(cell6);

				cell6 = new PdfPCell(new Phrase("", redFont));
				cell6.setBorder(Rectangle.NO_BORDER);
				cell6.setPaddingLeft(-50f);
				table37.addCell(cell6);

				cell6 = new PdfPCell(new Phrase("", redFont));
				cell6.setBorder(Rectangle.NO_BORDER);
				cell6.setPaddingLeft(-50f);
				table37.addCell(cell6);

				cell6 = new PdfPCell(new Phrase("Round Off To", redFont));
				cell6.setBorder(Rectangle.NO_BORDER);
				// cell6.setPaddingTop(10f);
				cell6.setPaddingRight(-30f);
				cell6.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table37.addCell(cell6);

				cell6 = new PdfPCell(new Phrase(":", redFont));
				cell6.setBorder(Rectangle.NO_BORDER);
				// cell6.setPaddingTop(10f);
				cell6.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell6.setPaddingRight(-50f);
				table37.addCell(cell6);

				/*
				 * BigDecimal bg=new BigDecimal(total-Math.floor(total));
				 * bg=bg.setScale(2,RoundingMode.HALF_DOWN); float round=bg.floatValue();
				 * //float rd=Math.nextUp(1.0f-round); float rd=1.00f-round;
				 * 
				 * 
				 * if(round<0.50) { cell6 = new PdfPCell(new Phrase("-"+round, redFont));
				 * cell6.setBorder(Rectangle.NO_BORDER);
				 * cell6.setHorizontalAlignment(Element.ALIGN_RIGHT);
				 * cell6.setPaddingRight(-15f); table37.addCell(cell6); } else { String
				 * rds=String.valueOf(rd); if(rds.length()>=4) { cell6 = new PdfPCell(new
				 * Phrase("+"+rds.substring(0,4), redFont)); } else { cell6 = new PdfPCell(new
				 * Phrase("+"+String.valueOf(rds), redFont)); }
				 * cell6.setBorder(Rectangle.NO_BORDER);
				 * cell6.setHorizontalAlignment(Element.ALIGN_RIGHT);
				 * cell6.setPaddingRight(-15f); table37.addCell(cell6); }
				 */
				cell6 = new PdfPCell(new Phrase(String.valueOf(Math.round(total)), redFont));
				cell6.setBorder(Rectangle.NO_BORDER);
				cell6.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell6.setPaddingRight(-15f);
				table37.addCell(cell6);

				PdfPCell cell7;
				cell7 = new PdfPCell(new Phrase("", redFont));
				cell7.setBorder(Rectangle.NO_BORDER);
				table37.addCell(cell7);

				cell7 = new PdfPCell(new Phrase("", redFont));
				cell7.setBorder(Rectangle.NO_BORDER);
				cell7.setPaddingLeft(-50f);
				table37.addCell(cell7);

				cell7 = new PdfPCell(new Phrase("", redFont));
				cell7.setBorder(Rectangle.NO_BORDER);
				cell7.setPaddingLeft(-50f);
				table37.addCell(cell7);

				cell7 = new PdfPCell(new Phrase("Return Value", redFont));
				cell7.setBorder(Rectangle.NO_BORDER);
				cell7.setPaddingRight(-30f);
				cell7.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table37.addCell(cell7);

				cell7 = new PdfPCell(new Phrase(":", redFont));
				cell7.setBorder(Rectangle.NO_BORDER);
				cell7.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell7.setPaddingRight(-50f);
				table37.addCell(cell7);

				cell7 = new PdfPCell(new Phrase(String.valueOf(Math.round(total)), redFont));
				cell7.setBorder(Rectangle.NO_BORDER);
				cell7.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell7.setPaddingRight(-15f);
				table37.addCell(cell7);

				PdfPCell cell8;
				cell8 = new PdfPCell(new Phrase("Printed By ", redFont));
				cell8.setBorder(Rectangle.NO_BORDER);
				cell8.setPaddingTop(10f);
				cell8.setPaddingLeft(-50f);
				table37.addCell(cell8);

				cell8 = new PdfPCell(new Phrase(":", redFont));
				cell8.setBorder(Rectangle.NO_BORDER);
				cell8.setPaddingTop(10f);
				cell8.setPaddingLeft(-50f);
				table37.addCell(cell8);

				cell8 = new PdfPCell(new Phrase(createdBy, redFont));
				cell8.setBorder(Rectangle.NO_BORDER);
				cell8.setPaddingTop(10f);
				cell8.setPaddingLeft(-50f);
				table37.addCell(cell8);

				cell8 = new PdfPCell(new Phrase("Printed Dt", redFont));
				cell8.setBorder(Rectangle.NO_BORDER);
				cell8.setPaddingTop(10f);
				cell8.setPaddingRight(-30f);
				cell8.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table37.addCell(cell8);

				cell8 = new PdfPCell(new Phrase(":", redFont));
				cell8.setBorder(Rectangle.NO_BORDER);
				cell8.setPaddingTop(10f);
				cell8.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell8.setPaddingRight(-50f);
				table37.addCell(cell8);

				cell8 = new PdfPCell(new Phrase(today, redFont));
				cell8.setBorder(Rectangle.NO_BORDER);
				cell8.setPaddingTop(10f);
				// cell55.setPaddingRight(-20f);
				cell8.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell8.setPaddingRight(-15f);
				table37.addCell(cell8);
				cell12.setColspan(2);
				cell12.setFixedHeight(80f);
				// table1.setWidthPercentage(100f);
				cell12.addElement(table37);
				table.addCell(cell12);
				document.add(table);
				document.close();
				System.out.println("finished");
				pdfByte = byteArrayOutputStream.toByteArray();
				String uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/sales/viewFile/")
						.path(salesPaymentPdfServiceImpl.getNextId()).toUriString();

				salesPaymentPdf = new SalesPaymentPdf();
				salesPaymentPdf.setFileName(billNo + " Return");
				salesPaymentPdf.setFileuri(uri);
				salesPaymentPdf.setPid(salesPaymentPdfServiceImpl.getNextId());
				salesPaymentPdf.setData(pdfByte);
				salesPaymentPdfServiceImpl.save(salesPaymentPdf);

			} catch (Exception e) {
				Logger.error(e.getMessage());
			}
		} else {
			byte[] pdfByte = null;
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			try {
				Document document = new Document(PageSize.A4_LANDSCAPE);

				Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

				Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
				Font headFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
				PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);
				document.open();

				// Display a date in day, month, year format
				Date date = Calendar.getInstance().getTime();
				DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
				String today = formatter.format(date).toString();

				Resource fileResourcee = resourceLoader.getResource(ConstantValues.IMAGE_PNG_CLASSPATH);
				PdfPTable table = new PdfPTable(2);

				Image img = Image.getInstance(hospitalLogo.getURL());
				img.scaleAbsolute(ConstantValues.IMAGE_ABSOLUTE_INTIAL_POSITION,
						ConstantValues.IMAGE_ABSOLUTE_FINAL_POSITION);
				table.setWidthPercentage(ConstantValues.TABLE_SET_WIDTH_PERECENTAGE);

				Phrase pq = new Phrase(new Chunk(img, ConstantValues.IMAGE_SET_INTIAL_POSITION,
						ConstantValues.IMAGE_SET_FINAL_POSITION));

				pq.add(new Chunk(ConstantValues.SALES_RETURN, redFont));
				PdfPCell cellp = new PdfPCell(pq);
				PdfPCell cell1 = new PdfPCell();

				// for header Bold
				PdfPTable table96 = new PdfPTable(1);
				table96.setWidths(new float[] { 5f });
				table96.setSpacingBefore(10);

				PdfPCell hcell96;
				hcell96 = new PdfPCell(new Phrase(ConstantValues.PHARMACY_NAME, headFont1));
				hcell96.setBorder(Rectangle.NO_BORDER);
				hcell96.setHorizontalAlignment(Element.ALIGN_CENTER);
				table96.addCell(hcell96);
				cell1.addElement(table96);

				// for header end
				cell1.setFixedHeight(130f);
				cell1.addElement(pq);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setColspan(2);
				table.addCell(cell1);

				PdfPCell cell19 = new PdfPCell();

				PdfPTable table21 = new PdfPTable(1);
				table21.setWidths(new float[] { 4f });
				table21.setSpacingBefore(10);

				PdfPCell hcell19;
				hcell19 = new PdfPCell(new Phrase("Patient Sales Returns", headFont1));
				hcell19.setBorder(Rectangle.NO_BORDER);
				hcell19.setHorizontalAlignment(Element.ALIGN_CENTER);
				table21.addCell(hcell19);

				cell19.setFixedHeight(20f);
				cell19.setColspan(2);
				cell19.addElement(table21);
				table.addCell(cell19);

				PdfPCell cell3 = new PdfPCell();

				PdfPTable table2 = new PdfPTable(6);
				table2.setWidths(new float[] { 5f, 1f, 4f, 5f, 1f, 5f });
				table2.setSpacingBefore(10);

				PdfPCell hcell1;
				hcell1 = new PdfPCell(new Phrase("Ret#", redFont));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table2.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase(":", redFont));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-100f);
				table2.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase(salesReturn.getSaleNo(), redFont));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-100f);
				table2.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase("Ret Dt", redFont));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(60f);
				table2.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase(":", redFont));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell1.setPaddingRight(10f);
				table2.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase(today, redFont));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(5f);
				table2.addCell(hcell1);

				PdfPCell hcell2;
				hcell2 = new PdfPCell(new Phrase("Patient Name", redFont));
				hcell2.setBorder(Rectangle.NO_BORDER);
				hcell2.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell2.setPaddingLeft(-50f);
				table2.addCell(hcell2);

				hcell2 = new PdfPCell(new Phrase(":", redFont));
				hcell2.setBorder(Rectangle.NO_BORDER);
				hcell2.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell2.setPaddingLeft(-100f);
				table2.addCell(hcell2);

				hcell2 = new PdfPCell(new Phrase(salesReturn.getName(), redFont));
				hcell2.setBorder(Rectangle.NO_BORDER);
				hcell2.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell2.setPaddingLeft(-100f);
				table2.addCell(hcell2);

				hcell2 = new PdfPCell(new Phrase("Bill#", redFont));
				hcell2.setBorder(Rectangle.NO_BORDER);
				hcell2.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell2.setPaddingLeft(60f);
				table2.addCell(hcell2);

				hcell2 = new PdfPCell(new Phrase(":", redFont));
				hcell2.setBorder(Rectangle.NO_BORDER);
				hcell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell2.setPaddingRight(10f);
				table2.addCell(hcell2);

				hcell2 = new PdfPCell(new Phrase(salesReturn.getBillNo(), redFont));
				hcell2.setBorder(Rectangle.NO_BORDER);
				hcell2.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell2.setPaddingLeft(5f);
				table2.addCell(hcell2);

				PdfPCell hcell9;
				hcell9 = new PdfPCell(new Phrase("Created By", redFont));
				hcell9.setBorder(Rectangle.NO_BORDER);
				hcell9.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell9.setPaddingLeft(-50f);
				table2.addCell(hcell9);

				hcell9 = new PdfPCell(new Phrase(":", redFont));
				hcell9.setBorder(Rectangle.NO_BORDER);
				hcell9.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell9.setPaddingLeft(-100f);
				table2.addCell(hcell9);

				hcell9 = new PdfPCell(new Phrase(createdBy, redFont));
				hcell9.setBorder(Rectangle.NO_BORDER);
				hcell9.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell9.setPaddingLeft(-100f);
				table2.addCell(hcell9);

				hcell9 = new PdfPCell(new Phrase("Created Dt", redFont));
				hcell9.setBorder(Rectangle.NO_BORDER);
				hcell9.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell9.setPaddingLeft(60f);
				table2.addCell(hcell9);

				hcell9 = new PdfPCell(new Phrase(":", redFont));
				hcell9.setBorder(Rectangle.NO_BORDER);
				hcell9.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell9.setPaddingRight(10f);
				table2.addCell(hcell9);

				hcell9 = new PdfPCell(new Phrase(today, redFont));
				hcell9.setBorder(Rectangle.NO_BORDER);
				hcell9.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell9.setPaddingLeft(7f);
				table2.addCell(hcell9);

				cell3.addElement(table2);
				cell3.setColspan(2);
				cell3.setFixedHeight(70f);
				table.addCell(cell3);

				PdfPCell cell31 = new PdfPCell();

				PdfPTable table1 = new PdfPTable(8);
				table1.setWidths(new float[] { 0.5f, 2.5f, 0.9f, 2.5f, 1f, 2f, 2f, 2f });

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

				hcell = new PdfPCell(new Phrase("Batch#", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("Exp Dt", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("Qty", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("Sale Rate", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("GST", redFont));
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
				for (RefSalesReturn a : refSales) {

					PdfPCell cell;

					MedicineDetails medicineDetails1 = medicineDetailsServiceImpl.findByName(a.getMedicineName());
					List<MedicineProcurement> medicineProcurement = medicineDetails1.getMedicineProcurement();

					cell = new PdfPCell(new Phrase(String.valueOf(count = count + 1), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table1.addCell(cell);

					cell = new PdfPCell(new Phrase(a.getMedicineName(), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					// cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					table1.addCell(cell);

					cell = new PdfPCell(new Phrase(a.getBatchNo(), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					// cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					table1.addCell(cell);

					cell = new PdfPCell(new Phrase(String.valueOf(a.getExpDate()).substring(0, 10), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					// cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					table1.addCell(cell);

					cell = new PdfPCell(new Phrase(String.valueOf(a.getQuantity()), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table1.addCell(cell);

					cell = new PdfPCell(new Phrase(String.valueOf(a.getMrp()), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table1.addCell(cell);

					cell = new PdfPCell(new Phrase(String.valueOf((a.getGst())), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table1.addCell(cell);

					cell = new PdfPCell(new Phrase(String.valueOf((a.getAmount())), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table1.addCell(cell);

				}

				cell31.setColspan(2);
				table1.setWidthPercentage(100f);
				cell31.addElement(table1);
				table.addCell(cell31);

				// -------------------------------
				PdfPCell cell12 = new PdfPCell();

				PdfPTable table37 = new PdfPTable(6);
				table37.setWidths(new float[] { 5f, 1f, 5f, 8f, 1f, 3f });
				table37.setSpacingBefore(10);

				PdfPCell cell55;
				cell55 = new PdfPCell(new Phrase("", redFont));
				cell55.setBorder(Rectangle.NO_BORDER);
				cell55.setPaddingTop(10f);
				cell55.setPaddingLeft(-50f);
				table37.addCell(cell55);

				cell55 = new PdfPCell(new Phrase("", redFont));
				cell55.setBorder(Rectangle.NO_BORDER);
				cell55.setPaddingTop(10f);
				cell55.setPaddingLeft(-50f);
				table37.addCell(cell55);

				cell55 = new PdfPCell(new Phrase("", redFont));
				cell55.setBorder(Rectangle.NO_BORDER);
				cell55.setPaddingTop(10f);
				cell55.setPaddingLeft(-50f);
				table37.addCell(cell55);

				cell55 = new PdfPCell(new Phrase("Total Sale Value", redFont));
				cell55.setBorder(Rectangle.NO_BORDER);
				cell55.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell55.setPaddingLeft(80f);
				table37.addCell(cell55);

				cell55 = new PdfPCell(new Phrase(":", redFont));
				cell55.setBorder(Rectangle.NO_BORDER);
				cell55.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell55.setPaddingRight(-30f);
				table37.addCell(cell55);

				cell55 = new PdfPCell(new Phrase(String.valueOf(total), redFont));
				cell55.setBorder(Rectangle.NO_BORDER);
				cell55.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell55.setPaddingRight(-40f);
				table37.addCell(cell55);

				PdfPCell cell6;
				cell6 = new PdfPCell(new Phrase("", redFont));
				cell6.setBorder(Rectangle.NO_BORDER);
				cell6.setPaddingLeft(-50f);
				table37.addCell(cell6);

				cell6 = new PdfPCell(new Phrase("", redFont));
				cell6.setBorder(Rectangle.NO_BORDER);
				cell6.setPaddingLeft(-50f);
				table37.addCell(cell6);

				cell6 = new PdfPCell(new Phrase("", redFont));
				cell6.setBorder(Rectangle.NO_BORDER);
				cell6.setPaddingLeft(-50f);
				table37.addCell(cell6);

				cell6 = new PdfPCell(new Phrase("Rounded Off To", redFont));
				cell6.setBorder(Rectangle.NO_BORDER);
				// cell6.setPaddingTop(10f);
				cell6.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell6.setPaddingLeft(80f);
				table37.addCell(cell6);

				cell6 = new PdfPCell(new Phrase(":", redFont));
				cell6.setBorder(Rectangle.NO_BORDER);
				// cell6.setPaddingTop(10f);
				cell6.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell6.setPaddingRight(-30f);
				table37.addCell(cell6);

				/*
				 * BigDecimal bg=new BigDecimal(total-Math.floor(total));
				 * bg=bg.setScale(2,RoundingMode.HALF_DOWN); float round=bg.floatValue();
				 * //float rd=Math.nextUp(1.0f-round); float rd=1.00f-round ;
				 * 
				 * if(round<0.50) { cell6 = new PdfPCell(new Phrase("-"+round, redFont));
				 * cell6.setBorder(Rectangle.NO_BORDER);
				 * cell6.setHorizontalAlignment(Element.ALIGN_RIGHT);
				 * cell6.setPaddingRight(-40f); table37.addCell(cell6); } else { String
				 * rds=String.valueOf(rd); if(rds.length()>=4) { cell6 = new PdfPCell(new
				 * Phrase("+"+rds.substring(0)+"/"+Math.round(1.00f-round), redFont)); } else {
				 * cell6 = new PdfPCell(new
				 * Phrase("+"+String.valueOf(rds)+"/"+Math.round(1.00f-round), redFont)); }
				 * cell6.setBorder(Rectangle.NO_BORDER);
				 * cell6.setHorizontalAlignment(Element.ALIGN_RIGHT);
				 * cell6.setPaddingRight(-40f); table37.addCell(cell6); }
				 */

				cell6 = new PdfPCell(new Phrase(String.valueOf(Math.round(total)), redFont));
				cell6.setBorder(Rectangle.NO_BORDER);
				cell6.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell6.setPaddingRight(-40f);
				table37.addCell(cell6);

				PdfPCell cell7;
				cell7 = new PdfPCell(new Phrase("", redFont));
				cell7.setBorder(Rectangle.NO_BORDER);
				table37.addCell(cell7);

				cell7 = new PdfPCell(new Phrase("", redFont));
				cell7.setBorder(Rectangle.NO_BORDER);
				cell7.setPaddingLeft(-50f);
				table37.addCell(cell7);

				cell7 = new PdfPCell(new Phrase("", redFont));
				cell7.setBorder(Rectangle.NO_BORDER);
				cell7.setPaddingLeft(-50f);
				table37.addCell(cell7);

				cell7 = new PdfPCell(new Phrase("Return Value", redFont));
				cell7.setBorder(Rectangle.NO_BORDER);

				cell7.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell7.setPaddingLeft(80f);
				table37.addCell(cell7);

				cell7 = new PdfPCell(new Phrase(":", redFont));
				cell7.setBorder(Rectangle.NO_BORDER);
				cell7.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell7.setPaddingRight(-30f);
				table37.addCell(cell7);

				cell7 = new PdfPCell(new Phrase(String.valueOf(Math.round(total)), redFont));
				cell7.setBorder(Rectangle.NO_BORDER);
				cell7.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell7.setPaddingRight(-40f);
				table37.addCell(cell7);

				PdfPCell cell8;
				cell8 = new PdfPCell(new Phrase("Printed By ", redFont));
				cell8.setBorder(Rectangle.NO_BORDER);
				cell8.setPaddingTop(10f);
				cell8.setPaddingLeft(-50f);
				table37.addCell(cell8);

				cell8 = new PdfPCell(new Phrase(":", redFont));
				cell8.setBorder(Rectangle.NO_BORDER);
				cell8.setPaddingTop(10f);
				cell8.setPaddingLeft(-50f);
				table37.addCell(cell8);

				cell8 = new PdfPCell(new Phrase(createdBy, redFont));
				cell8.setBorder(Rectangle.NO_BORDER);
				cell8.setPaddingTop(10f);
				cell8.setPaddingLeft(-50f);
				table37.addCell(cell8);

				cell8 = new PdfPCell(new Phrase("Printed Dt", redFont));
				cell8.setBorder(Rectangle.NO_BORDER);
				cell8.setPaddingTop(10f);
				cell8.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell8.setPaddingLeft(80f);
				table37.addCell(cell8);

				cell8 = new PdfPCell(new Phrase(":", redFont));
				cell8.setBorder(Rectangle.NO_BORDER);
				cell8.setPaddingTop(10f);
				cell8.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell8.setPaddingRight(15f);
				table37.addCell(cell8);

				cell8 = new PdfPCell(new Phrase(today, redFont));
				cell8.setBorder(Rectangle.NO_BORDER);
				cell8.setPaddingTop(10f);
				// cell55.setPaddingRight(-20f);
				cell8.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell8.setPaddingRight(-40f);
				table37.addCell(cell8);
				cell12.setColspan(2);
				cell12.setFixedHeight(80f);
				// table1.setWidthPercentage(100f);
				cell12.addElement(table37);
				table.addCell(cell12);
				document.add(table);
				document.close();
				System.out.println("finished");
				pdfByte = byteArrayOutputStream.toByteArray();
				String uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/sales/viewFile/")
						.path(salesPaymentPdfServiceImpl.getNextId()).toUriString();

				salesPaymentPdf = new SalesPaymentPdf();
				salesPaymentPdf.setFileName(billNo + " Return");
				salesPaymentPdf.setFileuri(uri);
				salesPaymentPdf.setPid(salesPaymentPdfServiceImpl.getNextId());
				salesPaymentPdf.setData(pdfByte);
				salesPaymentPdfServiceImpl.save(salesPaymentPdf);

			} catch (Exception e) {
				Logger.error(e.getMessage());
			}
		}

		return salesPaymentPdf;

	}

	@Override
	public List<Object> displaySalesReturnList(int days) {

		String today = Timestamp.valueOf(LocalDateTime.now()).toString().substring(0, 10);
		String nextDay = null;
		String fromDay = null;
		List<SalesReturn> salesReturns = null;
		List<String> returnList = new ArrayList<String>();
		String returnBillNo = null;
		String userName = null;
		float returnAmount = 0;
		String salesdate = null;
		List<Object> list = new ArrayList<>();

		if (days == 2) {
			nextDay = LocalDate.parse(today).plusDays(1).toString();
			fromDay = LocalDate.parse(today).plusDays(-2).toString();
			salesReturns = salesReturnRepository.findTheReturnList(fromDay, nextDay);
		} else if (days == 7) {
			nextDay = LocalDate.parse(today).plusDays(1).toString();
			fromDay = LocalDate.parse(today).plusDays(-7).toString();
			salesReturns = salesReturnRepository.findTheReturnList(fromDay, nextDay);
		} else if (days == 15) {
			nextDay = LocalDate.parse(today).plusDays(1).toString();
			fromDay = LocalDate.parse(today).plusDays(-15).toString();
			salesReturns = salesReturnRepository.findTheReturnList(fromDay, nextDay);
		} else if (days == 30) {
			nextDay = LocalDate.parse(today).plusDays(1).toString();
			fromDay = LocalDate.parse(today).plusDays(-30).toString();
			salesReturns = salesReturnRepository.findTheReturnList(fromDay, nextDay);
		}

		for (SalesReturn salesReturnsInfo : salesReturns) {
			returnBillNo = salesReturnsInfo.getBillNo();

			if (!returnList.contains(returnBillNo)) {

				List<SalesReturn> salesreturn = salesReturnRepository.findByBillNo(returnBillNo);
				for (SalesReturn salesreturnInfo : salesreturn) {

					returnAmount += salesreturnInfo.getAmount();
				}

				List<Sales> salesInfo = salesRepository.findByBillNo(returnBillNo);
				String soldBy = salesReturnsInfo.getRaisedBy();

				User user = userServiceImpl.findOneByUserId(soldBy);
				userName = (user.getMiddleName() != null)
						? user.getFirstName() + ConstantValues.ONE_SPACE_STRING + user.getMiddleName()
								+ ConstantValues.ONE_SPACE_STRING + user.getLastName()
						: user.getFirstName() + ConstantValues.ONE_SPACE_STRING + user.getLastName();

				String saleDate = String.valueOf(salesReturnsInfo.getDate().toString());
				SimpleDateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				SimpleDateFormat toFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
				try {
					salesdate = toFormat.format(fromFormat.parse(saleDate));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Map<String, String> returnMap = new HashMap<String, String>();
				returnMap.put("billNo", returnBillNo);
				returnMap.put("mobileNo", String.valueOf(salesInfo.get(0).getMobileNo()));
				returnMap.put("patientName", salesInfo.get(0).getName());
				returnMap.put("Date", salesdate);
				returnMap.put("paymentType", salesReturnsInfo.getPaymentType());
				returnMap.put("userName", userName);
				returnMap.put("transactionType", "SalesReturn");
				returnMap.put("Amount", String.valueOf(Math.round(returnAmount)));
				if (salesInfo.get(0).getPatientRegistration() != null) {
					returnMap.put("umrNo", salesInfo.get(0).getUmr());
					returnMap.put("regNo", salesInfo.get(0).getPatientRegistration().getRegId());
				}
				returnList.add(returnBillNo);
				returnAmount = 0;
				list.add(returnMap);
			}

		}
		return list;

	}

	@Override
	public List<SalesReturn> findBySalesReturnPatientRegistration(PatientRegistration patientRegistration) {
		return salesReturnRepository.findBySalesReturnPatientRegistration(patientRegistration);
	}

	@Override
	public SalesPaymentPdf createIpPharmacyReturn(SalesReturn salesReturn, String regId, Principal principal) {

		// createdBy (Security)
		User userSecurity = userServiceImpl.findByUserName(principal.getName());
		String createdBy = userSecurity.getFirstName() + " " + userSecurity.getLastName();

		String billNo = "";
		String umr = null;
		float finalNetAmount = 0;
		String paymentMode = "";
		long mobile = 0;
		Location location = locationServiceImpl.findByLocationName(salesReturn.getLocation());

		PatientRegistration patientRegistration = null;

		patientRegistration = patientRegistrationServiceImpl.findByRegId(regId);
		regId = patientRegistration.getRegId();
		umr = patientRegistration.getPatientDetails().getUmr();
		salesReturn.setSalesReturnPatientRegistration(patientRegistration);

		List<RefSalesReturn> refSales = salesReturn.getRefSalesReturns();
		salesReturn.setDate(Timestamp.valueOf(LocalDateTime.now()));

		salesReturn.setSalesReturnLocation(location);
		salesReturn.setMasterSaleNo(getNextReturnMasterSaleNo());
		salesReturn.setRaisedBy(userSecurity.getUserId());
		salesReturn.setSalesReturnUser(userSecurity);
		paymentMode = salesReturn.getPaymentType();
		for (RefSalesReturn refSalesList : refSales) {

			billNo = refSalesList.getBillNo();

			System.out.println(refSalesList.getBillNo());
			System.out.println(refSalesList.getBatchNo());
			System.out.println(refSalesList.getMedicineName());

			salesReturn.setBillNo(refSalesList.getBillNo());
			salesReturn.setSaleNo(getNextReturnSaleNo());
			salesReturn.setAmount(refSalesList.getAmount());
			salesReturn.setDiscount(refSalesList.getDiscount());

			salesReturn.setMedicineName(refSalesList.getMedicineName());
			salesReturn.setQuantity(refSalesList.getQuantity());

			// Sales sales=salesRepository.findOneBill(refSalesList.getBillNo(),
			// refSalesList.getMedicineName(), refSalesList.getBatchNo());
			Sales sales = salesRepository.findBySaleNo(refSalesList.getSaleId());
			salesReturn.setGst(sales.getGst());
			refSalesList.setGst(sales.getGst());
			// PatientSales
			// patientSales=patientSalesRepository.findOneBill(refSalesList.getBillNo(),
			// refSalesList.getMedicineName(), refSalesList.getBatchNo());

			ChargeBill chargeBill = chargeBillServiceImpl.findBySaleId(sales);

			if (sales.getQuantity() == 0) {
				throw new RuntimeException("0 Quantity Cann't Be Updated " + refSalesList.getMedicineName());
			}

			sales.setAmount(sales.getAmount() - refSalesList.getAmount());
			sales.setQuantity(sales.getQuantity() - refSalesList.getQuantity());
			sales.setCostPrice(sales.getMrp() * sales.getQuantity());
			mobile = sales.getMobileNo();
			salesReturn.setBatchNo(sales.getBatchNo());
			salesReturn.setMrp(sales.getMrp());
			salesReturn.setName(sales.getName());
			salesReturn.setMobileNo(sales.getMobileNo());

			finalNetAmount += salesReturn.getAmount();

			System.out.println(sales.getCostPrice());

			if (patientRegistration != null && patientRegistration.getpType().equalsIgnoreCase("INPATIENT")) {

				chargeBill.setAmount(sales.getCostPrice());
				chargeBill.setQuantity(sales.getQuantity());
				chargeBill.setNetAmount(sales.getAmount());
				chargeBillServiceImpl.save(chargeBill);

			}
			// for refund management
			SalesRefund salesRefund = new SalesRefund();
			salesRefund.setReturnId(salesRefundServiceImpl.getNextReturnId());
			salesRefund.setAmount(salesReturn.getAmount());
			salesRefund.setMobileNo(salesReturn.getMobileNo());
			salesRefund.setRefundBy(createdBy);
			salesReturn.setSalesReturnUser(userSecurity);
			salesRefund.setName(salesReturn.getName());
			salesRefund.setMobileNo(salesReturn.getMobileNo());
			salesRefund.setPaymentType(salesReturn.getPaymentType());

			// After security
			if (salesReturn.getSalesReturnPatientRegistration() == null) {
				salesRefund.setRefundBy(salesReturn.getName());
			} else {
				salesRefund.setUmr(patientRegistration.getPatientDetails().getUmr());
				salesRefund.setRefundBy(patientRegistration.getPatientDetails().getFirstName() + " "
						+ patientRegistration.getPatientDetails().getLastName());
				salesRefund.setSalesRefundPatientRegistration(salesReturn.getSalesReturnPatientRegistration());
			}
			salesRefund.setStatus("Not-Approved");
			salesRefund.setRefundDate(salesReturn.getDate());
			salesRefund.setReturnAmount(salesReturn.getAmount());
			salesRefund.setBillNo(billNo);

			// salesRefund.setSalesRefundPatientRegistration();

			salesRefundRepository.save(salesRefund);
			salesRepository.save(sales);
			salesReturnRepository.save(salesReturn);

			// for medicine quantity
			MedicineQuantity medicineQuantity = new MedicineQuantity();

			MedicineDetails medicineDetails = medicineDetailsServiceImpl.findByName(salesReturn.getMedicineName());
			MedicineQuantity medicineQuantityInfo = medicineQuantityServiceImpl
					.findByBatchIdAndMedicineDetails(refSalesList.getBatchNo(), medicineDetails);

			if (medicineQuantityInfo != null) {

				long totalReturn = salesReturn.getQuantity();
				medicineQuantityInfo.setBalance(medicineQuantityInfo.getBalance() + totalReturn);
				medicineQuantityInfo.setSold(medicineQuantityInfo.getSold() - totalReturn);
				medicineQuantityRepository.save(medicineQuantityInfo);

			}

		}

		List<SalesReturn> finalreturns = findByMasterSaleNo(salesReturn.getMasterSaleNo());
		List<String> finalBillList = new ArrayList<String>();

		float returnfianlAmout = 0;
		for (SalesReturn finalreturnsInfo : finalreturns) {

			String finalBillno = finalreturnsInfo.getBillNo();

			if (!finalBillList.contains(finalBillno)) {

				List<SalesReturn> salereturnByBill = salesReturnRepository
						.findByMasterSaleNoAndBillNo(salesReturn.getMasterSaleNo(), finalBillno);

				for (SalesReturn salereturnByBillInfo : salereturnByBill) {
					returnfianlAmout += salereturnByBillInfo.getAmount();

				}
				// Final Billing
				FinalBilling finalBilling = new FinalBilling();
				finalBilling.setBillNo(finalBillno);
				finalBilling.setBillType("Ip Sales Return");
				if (paymentMode.equalsIgnoreCase("Cash")) {
					finalBilling.setCashAmount(Math.round(returnfianlAmout));
				} else if (paymentMode.equalsIgnoreCase("Card")) {
					finalBilling.setCardAmount(Math.round(returnfianlAmout));
				} else if (paymentMode.equalsIgnoreCase("Cheque")) {
					finalBilling.setChequeAmount(Math.round(returnfianlAmout));
				} else if (paymentMode.equalsIgnoreCase(ConstantValues.DUE)) {
					finalBilling.setDueAmount(Math.round(returnfianlAmout));

				}
				finalBilling.setDueStatus(ConstantValues.NO);
				finalBilling.setUpdatedBy(userSecurity.getUserId());
				finalBilling.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
				finalBilling.setInsertedDate(Timestamp.valueOf(LocalDateTime.now()));
				finalBilling.setFinalAmountPaid(Math.round(returnfianlAmout));
				finalBilling.setFinalBillUser(userSecurity);
				finalBilling.setName(salesReturn.getName());
				finalBilling.setRegNo((regId != null) ? regId : null);
				finalBilling.setMobile(mobile);
				finalBilling.setPaymentType(paymentMode);
				finalBilling.setTotalAmount(returnfianlAmout);
				finalBilling.setUmrNo((umr != null) ? umr : null);
				finalBillingServiceImpl.computeSave(finalBilling);

				finalBillList.add(finalBillno);
				returnfianlAmout = 0;
			}

		}

		// for address

		/*
		 * String
		 * addr="                                                                 Udbhava Hospitals\n                                                                              Plot No14,15,16 & 17,Nandi Co-op.Society,"
		 * +
		 * "\n                                                                                    Main Road, Beside Navya Grand Hotel, \n                                                                             Miyapur,Hyderabad-49, Phone:040-23046789   \n                               "
		 * +
		 * "                                                    For Appointment Contact:8019114481     "
		 * +
		 * "\n                                                                                    Email :udbhavahospitals@gmail.com"
		 * ;
		 */

		SalesPaymentPdf salesPaymentPdf = null;

		// To find total
		float total = 0;
		List<SalesReturn> salesReturnList = findByMasterSaleNo(salesReturn.getMasterSaleNo());
		for (SalesReturn salesReturnInfo : salesReturnList) {
			total += salesReturnInfo.getAmount();
		}

		if (patientRegistration != null) {

			byte[] pdfByte = null;
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			try {
				Document document = new Document(PageSize.A4_LANDSCAPE);

				Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

				Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
				Font headFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
				PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);
				document.open();

				// Display a date in day, month, year format
				Date date = Calendar.getInstance().getTime();
				DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
				String today = formatter.format(date).toString();

				Resource fileResourcee = resourceLoader.getResource(ConstantValues.IMAGE_PNG_CLASSPATH);
				PdfPTable table = new PdfPTable(2);

				Image img = Image.getInstance(hospitalLogo.getURL());
				img.scaleAbsolute(ConstantValues.IMAGE_ABSOLUTE_INTIAL_POSITION,
						ConstantValues.IMAGE_ABSOLUTE_FINAL_POSITION);
				table.setWidthPercentage(ConstantValues.TABLE_SET_WIDTH_PERECENTAGE);

				Phrase pq = new Phrase(new Chunk(img, ConstantValues.IMAGE_SET_INTIAL_POSITION,
						ConstantValues.IMAGE_SET_FINAL_POSITION));

				pq.add(new Chunk(ConstantValues.SALES_RETURN, redFont));
				PdfPCell cellp = new PdfPCell(pq);
				PdfPCell cell1 = new PdfPCell();

				// for header Bold
				PdfPTable table96 = new PdfPTable(1);
				table96.setWidths(new float[] { 5f });
				table96.setSpacingBefore(10);

				PdfPCell hcell96;
				hcell96 = new PdfPCell(new Phrase(ConstantValues.PHARMACY_NAME, headFont1));
				hcell96.setBorder(Rectangle.NO_BORDER);
				hcell96.setHorizontalAlignment(Element.ALIGN_CENTER);
				table96.addCell(hcell96);
				cell1.addElement(table96);

				// for header end
				cell1.setFixedHeight(130f);
				cell1.addElement(pq);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setColspan(2);
				table.addCell(cell1);

				PdfPCell cell19 = new PdfPCell();

				PdfPTable table21 = new PdfPTable(1);
				table21.setWidths(new float[] { 4f });
				table21.setSpacingBefore(10);

				PdfPCell hcell19;
				hcell19 = new PdfPCell(new Phrase("Inpatient Sales Returns ", headFont1));
				hcell19.setBorder(Rectangle.NO_BORDER);
				hcell19.setHorizontalAlignment(Element.ALIGN_CENTER);
				table21.addCell(hcell19);

				cell19.setFixedHeight(20f);
				cell19.setColspan(2);
				cell19.addElement(table21);
				table.addCell(cell19);

				PdfPCell cell3 = new PdfPCell();

				PdfPTable table2 = new PdfPTable(6);
				table2.setWidths(new float[] { 5f, 1f, 5f, 6f, 1f, 6f });
				table2.setSpacingBefore(10);

				PdfPCell hcell3;
				hcell3 = new PdfPCell(new Phrase("UMR#", redFont));
				hcell3.setBorder(Rectangle.NO_BORDER);
				hcell3.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell3.setPaddingLeft(-50f);
				table2.addCell(hcell3);

				hcell3 = new PdfPCell(new Phrase(":", redFont));
				hcell3.setBorder(Rectangle.NO_BORDER);
				hcell3.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell3.setPaddingLeft(-70f);
				table2.addCell(hcell3);

				hcell3 = new PdfPCell(new Phrase(umr, redFont));
				hcell3.setBorder(Rectangle.NO_BORDER);
				hcell3.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell3.setPaddingLeft(-50f);
				table2.addCell(hcell3);

				hcell3 = new PdfPCell(new Phrase("Reg Id", redFont));
				hcell3.setBorder(Rectangle.NO_BORDER);
				hcell3.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell3.setPaddingLeft(60f);
				table2.addCell(hcell3);

				hcell3 = new PdfPCell(new Phrase(":", redFont));
				hcell3.setBorder(Rectangle.NO_BORDER);
				hcell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell3.setPaddingRight(10f);
				table2.addCell(hcell3);

				hcell3 = new PdfPCell(new Phrase(regId, redFont));
				hcell3.setBorder(Rectangle.NO_BORDER);
				hcell3.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell3.setPaddingLeft(7f);
				table2.addCell(hcell3);

				PdfPCell hcell1;
				hcell1 = new PdfPCell(new Phrase("Patient Name", redFont));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table2.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase(":", redFont));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-70f);
				table2.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase(
						salesReturn.getSalesReturnPatientRegistration().getPatientDetails().getTitle() + ". "
								+ salesReturn.getSalesReturnPatientRegistration().getPatientDetails().getFirstName()
								+ " "
								+ salesReturn.getSalesReturnPatientRegistration().getPatientDetails().getLastName(),
						redFont));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table2.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase("Ret Dt", redFont));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(60f);
				table2.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase(":", redFont));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell1.setPaddingRight(10f);
				table2.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase(today, redFont));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(5f);
				table2.addCell(hcell1);

				PdfPCell hcell9;
				hcell9 = new PdfPCell(new Phrase("Created By", redFont));
				hcell9.setBorder(Rectangle.NO_BORDER);
				hcell9.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell9.setPaddingLeft(-50f);
				table2.addCell(hcell9);

				hcell9 = new PdfPCell(new Phrase(":", redFont));
				hcell9.setBorder(Rectangle.NO_BORDER);
				hcell9.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell9.setPaddingLeft(-70f);
				table2.addCell(hcell9);

				hcell9 = new PdfPCell(new Phrase(createdBy, redFont));
				hcell9.setBorder(Rectangle.NO_BORDER);
				hcell9.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell9.setPaddingLeft(-50f);
				table2.addCell(hcell9);

				hcell9 = new PdfPCell(new Phrase("Created Dt", redFont));
				hcell9.setBorder(Rectangle.NO_BORDER);
				hcell9.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell9.setPaddingLeft(60f);
				table2.addCell(hcell9);

				hcell9 = new PdfPCell(new Phrase(":", redFont));
				hcell9.setBorder(Rectangle.NO_BORDER);
				hcell9.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell9.setPaddingRight(10f);
				table2.addCell(hcell9);

				hcell9 = new PdfPCell(new Phrase(today, redFont));
				hcell9.setBorder(Rectangle.NO_BORDER);
				hcell9.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell9.setPaddingLeft(7f);
				table2.addCell(hcell9);

				cell3.addElement(table2);
				cell3.setColspan(2);
				cell3.setFixedHeight(70f);
				table.addCell(cell3);

				PdfPCell cell31 = new PdfPCell();

				PdfPTable table1 = new PdfPTable(8);
				table1.setWidths(new float[] { 1f, 2.5f, 2f, 2f, 1f, 2f, 2f, 2f });

				table1.setSpacingBefore(10);

				PdfPCell hcell;
				hcell = new PdfPCell(new Phrase("S.No", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("Bill No#", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("Item Name", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("Batch#", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("Qty", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("Sale Rate", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("GST", redFont));
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
				for (RefSalesReturn a : refSales) {

					PdfPCell cell;

					MedicineDetails medicineDetails1 = medicineDetailsServiceImpl.findByName(a.getMedicineName());
					List<MedicineProcurement> medicineProcurement = medicineDetails1.getMedicineProcurement();

					cell = new PdfPCell(new Phrase(String.valueOf(count = count + 1), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table1.addCell(cell);

					cell = new PdfPCell(new Phrase(a.getBillNo(), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					// cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					table1.addCell(cell);

					cell = new PdfPCell(new Phrase(a.getMedicineName(), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					// cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					table1.addCell(cell);

					cell = new PdfPCell(new Phrase(a.getBatchNo(), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					// cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					table1.addCell(cell);

					/*
					 * cell = new PdfPCell(new Phrase(a.getBillNo(), redFont));
					 * cell.setBorder(Rectangle.NO_BORDER);
					 * cell.setVerticalAlignment(Element.ALIGN_MIDDLE); //
					 * cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					 * cell.setHorizontalAlignment(Element.ALIGN_LEFT); table1.addCell(cell);
					 */

					cell = new PdfPCell(new Phrase(String.valueOf(a.getQuantity()), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table1.addCell(cell);

					cell = new PdfPCell(new Phrase(String.valueOf(a.getMrp()), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table1.addCell(cell);

					cell = new PdfPCell(new Phrase(String.valueOf((a.getGst())), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table1.addCell(cell);

					cell = new PdfPCell(new Phrase(String.valueOf((a.getAmount())), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table1.addCell(cell);

				}

				cell31.setColspan(2);
				table1.setWidthPercentage(100f);
				cell31.addElement(table1);
				table.addCell(cell31);

				// -------------------------------
				PdfPCell cell12 = new PdfPCell();

				PdfPTable table37 = new PdfPTable(6);
				table37.setWidths(new float[] { 5f, 1f, 4f, 7f, 1f, 8f });
				table37.setSpacingBefore(10);

				PdfPCell cell55;
				cell55 = new PdfPCell(new Phrase("", redFont));
				cell55.setBorder(Rectangle.NO_BORDER);
				cell55.setPaddingTop(10f);
				cell55.setPaddingLeft(-50f);
				table37.addCell(cell55);

				cell55 = new PdfPCell(new Phrase("", redFont));
				cell55.setBorder(Rectangle.NO_BORDER);
				cell55.setPaddingTop(10f);
				cell55.setPaddingLeft(-50f);
				table37.addCell(cell55);

				cell55 = new PdfPCell(new Phrase("", redFont));
				cell55.setBorder(Rectangle.NO_BORDER);
				cell55.setPaddingTop(10f);
				cell55.setPaddingLeft(-50f);
				table37.addCell(cell55);

				cell55 = new PdfPCell(new Phrase("Total Sale Value", redFont));
				cell55.setBorder(Rectangle.NO_BORDER);
				cell55.setPaddingRight(-30f);
				cell55.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table37.addCell(cell55);

				cell55 = new PdfPCell(new Phrase(":", redFont));
				cell55.setBorder(Rectangle.NO_BORDER);
				cell55.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell55.setPaddingRight(-50f);
				table37.addCell(cell55);

				cell55 = new PdfPCell(new Phrase(String.valueOf(total), redFont));
				cell55.setBorder(Rectangle.NO_BORDER);
				cell55.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell55.setPaddingRight(-15f);
				table37.addCell(cell55);

				PdfPCell cell6;
				cell6 = new PdfPCell(new Phrase("", redFont));
				cell6.setBorder(Rectangle.NO_BORDER);
				cell6.setPaddingLeft(-50f);
				table37.addCell(cell6);

				cell6 = new PdfPCell(new Phrase("", redFont));
				cell6.setBorder(Rectangle.NO_BORDER);
				cell6.setPaddingLeft(-50f);
				table37.addCell(cell6);

				cell6 = new PdfPCell(new Phrase("", redFont));
				cell6.setBorder(Rectangle.NO_BORDER);
				cell6.setPaddingLeft(-50f);
				table37.addCell(cell6);

				cell6 = new PdfPCell(new Phrase("Round Off To", redFont));
				cell6.setBorder(Rectangle.NO_BORDER);
				// cell6.setPaddingTop(10f);
				cell6.setPaddingRight(-30f);
				cell6.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table37.addCell(cell6);

				cell6 = new PdfPCell(new Phrase(":", redFont));
				cell6.setBorder(Rectangle.NO_BORDER);
				// cell6.setPaddingTop(10f);
				cell6.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell6.setPaddingRight(-50f);
				table37.addCell(cell6);

				cell6 = new PdfPCell(new Phrase(String.valueOf(Math.round(total)), redFont));
				cell6.setBorder(Rectangle.NO_BORDER);
				cell6.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell6.setPaddingRight(-15f);
				table37.addCell(cell6);

				PdfPCell cell7;
				cell7 = new PdfPCell(new Phrase("", redFont));
				cell7.setBorder(Rectangle.NO_BORDER);
				table37.addCell(cell7);

				cell7 = new PdfPCell(new Phrase("", redFont));
				cell7.setBorder(Rectangle.NO_BORDER);
				cell7.setPaddingLeft(-50f);
				table37.addCell(cell7);

				cell7 = new PdfPCell(new Phrase("", redFont));
				cell7.setBorder(Rectangle.NO_BORDER);
				cell7.setPaddingLeft(-50f);
				table37.addCell(cell7);

				cell7 = new PdfPCell(new Phrase("Return Value", redFont));
				cell7.setBorder(Rectangle.NO_BORDER);
				cell7.setPaddingRight(-30f);
				cell7.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table37.addCell(cell7);

				cell7 = new PdfPCell(new Phrase(":", redFont));
				cell7.setBorder(Rectangle.NO_BORDER);
				cell7.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell7.setPaddingRight(-50f);
				table37.addCell(cell7);

				cell7 = new PdfPCell(new Phrase(String.valueOf(Math.round(total)), redFont));
				cell7.setBorder(Rectangle.NO_BORDER);
				cell7.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell7.setPaddingRight(-15f);
				table37.addCell(cell7);

				PdfPCell cell8;
				cell8 = new PdfPCell(new Phrase("Printed By ", redFont));
				cell8.setBorder(Rectangle.NO_BORDER);
				cell8.setPaddingTop(10f);
				cell8.setPaddingLeft(-50f);
				table37.addCell(cell8);

				cell8 = new PdfPCell(new Phrase(":", redFont));
				cell8.setBorder(Rectangle.NO_BORDER);
				cell8.setPaddingTop(10f);
				cell8.setPaddingLeft(-50f);
				table37.addCell(cell8);

				cell8 = new PdfPCell(new Phrase(createdBy, redFont));
				cell8.setBorder(Rectangle.NO_BORDER);
				cell8.setPaddingTop(10f);
				cell8.setPaddingLeft(-50f);
				table37.addCell(cell8);

				cell8 = new PdfPCell(new Phrase("Printed Dt", redFont));
				cell8.setBorder(Rectangle.NO_BORDER);
				cell8.setPaddingTop(10f);
				cell8.setPaddingRight(-30f);
				cell8.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table37.addCell(cell8);

				cell8 = new PdfPCell(new Phrase(":", redFont));
				cell8.setBorder(Rectangle.NO_BORDER);
				cell8.setPaddingTop(10f);
				cell8.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell8.setPaddingRight(-50f);
				table37.addCell(cell8);

				cell8 = new PdfPCell(new Phrase(today, redFont));
				cell8.setBorder(Rectangle.NO_BORDER);
				cell8.setPaddingTop(10f);
				// cell55.setPaddingRight(-20f);
				cell8.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell8.setPaddingRight(-15f);
				table37.addCell(cell8);
				cell12.setColspan(2);
				cell12.setFixedHeight(80f);
				// table1.setWidthPercentage(100f);
				cell12.addElement(table37);
				table.addCell(cell12);
				document.add(table);
				document.close();
				System.out.println("finished");
				pdfByte = byteArrayOutputStream.toByteArray();
				String uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/sales/viewFile/")
						.path(salesPaymentPdfServiceImpl.getNextId()).toUriString();

				salesPaymentPdf = new SalesPaymentPdf();
				salesPaymentPdf.setFileName(regId + "Ip Pharmacy Return");
				salesPaymentPdf.setFileuri(uri);
				salesPaymentPdf.setPid(salesPaymentPdfServiceImpl.getNextId());
				salesPaymentPdf.setData(pdfByte);
				salesPaymentPdfServiceImpl.save(salesPaymentPdf);

			} catch (Exception e) {
				Logger.error(e.getMessage());
			}
		}
		return salesPaymentPdf;

	}

	@Override
	public List<Object> displaySalesReturnList(String days) {

		String today = Timestamp.valueOf(LocalDateTime.now()).toString().substring(0, 10);
		String nextDay = null;
		String fromDay = null;
		List<SalesReturn> salesReturns = null;
		List<String> returnList = new ArrayList<String>();
		String returnBillNo = null;
		String userName = null;
		float returnAmount = 0;
		String salesdate = null;
		List<Object> list = new ArrayList<>();

		if (days.equalsIgnoreCase("2")) {
			nextDay = LocalDate.parse(today).plusDays(1).toString();
			fromDay = LocalDate.parse(today).plusDays(-2).toString();
			salesReturns = salesReturnRepository.findTheReturnList(fromDay, nextDay);
		} else if (days.equalsIgnoreCase("7")) {
			nextDay = LocalDate.parse(today).plusDays(1).toString();
			fromDay = LocalDate.parse(today).plusDays(-7).toString();
			salesReturns = salesReturnRepository.findTheReturnList(fromDay, nextDay);
		} else if (days.equalsIgnoreCase("15")) {
			nextDay = LocalDate.parse(today).plusDays(1).toString();
			fromDay = LocalDate.parse(today).plusDays(-15).toString();
			salesReturns = salesReturnRepository.findTheReturnList(fromDay, nextDay);
		} else if (days.equalsIgnoreCase("30")) {
			nextDay = LocalDate.parse(today).plusDays(1).toString();
			fromDay = LocalDate.parse(today).plusDays(-30).toString();
			salesReturns = salesReturnRepository.findTheReturnList(fromDay, nextDay);
		} else if (days.equalsIgnoreCase("ALL")) {

			salesReturns = salesReturnRepository.findAllReturns();
		}

		for (SalesReturn salesReturnsInfo : salesReturns) {
			returnBillNo = salesReturnsInfo.getBillNo();

			if (!returnList.contains(returnBillNo)) {

				List<SalesReturn> salesreturn = salesReturnRepository.findByBillNo(returnBillNo);
				for (SalesReturn salesreturnInfo : salesreturn) {

					returnAmount += salesreturnInfo.getAmount();
				}

				PatientRegistration patientRegistration = salesReturnsInfo.getSalesReturnPatientRegistration();
				String soldBy = salesReturnsInfo.getRaisedBy();

				User user = userServiceImpl.findOneByUserId(soldBy);
				if (user != null) {
					userName = (user.getMiddleName() != null)
							? user.getFirstName() + ConstantValues.ONE_SPACE_STRING + user.getMiddleName()
									+ ConstantValues.ONE_SPACE_STRING + user.getLastName()
							: user.getFirstName() + ConstantValues.ONE_SPACE_STRING + user.getLastName();
				} else {

					userName = ConstantValues.ONE_SPACE_STRING;
				}
				String saleDate = String.valueOf(salesReturnsInfo.getDate().toString());
				SimpleDateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				SimpleDateFormat toFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
				try {
					salesdate = toFormat.format(fromFormat.parse(saleDate));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Map<String, String> returnMap = new HashMap<String, String>();
				returnMap.put("billNo", returnBillNo);
				returnMap.put("mobileNo", String.valueOf(salesReturnsInfo.getMobileNo()));

				returnMap.put("Date", salesdate);
				returnMap.put("paymentType", salesReturnsInfo.getPaymentType());
				returnMap.put("userName", userName);
				returnMap.put("transactionType", "SalesReturn");
				returnMap.put("Amount", String.valueOf(Math.round(returnAmount)));
				returnMap.put("patientName", salesReturnsInfo.getName());

				if (patientRegistration != null) {

					returnMap.put("umrNo", patientRegistration.getPatientDetails().getUmr());
					returnMap.put("regNo", patientRegistration.getRegId());
				}

				returnList.add(returnBillNo);
				returnAmount = 0;
				list.add(returnMap);
			}

		}
		return list;

	}

}
