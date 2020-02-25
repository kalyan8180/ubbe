package com.example.test.testingHMS.pharmacist.serviceImpl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import com.example.test.testingHMS.bill.repository.ChargeBillRepository;
import com.example.test.testingHMS.bill.serviceImpl.ChargeBillServiceImpl;
import com.example.test.testingHMS.finalBilling.model.FinalBilling;
import com.example.test.testingHMS.finalBilling.repository.FinalBillingRepository;
import com.example.test.testingHMS.finalBilling.serviceImpl.FinalBillingServiceImpl;
import com.example.test.testingHMS.patient.Helper.MultiplePayment;
import com.example.test.testingHMS.patient.model.CashPlusCard;
import com.example.test.testingHMS.patient.model.PatientDetails;
import com.example.test.testingHMS.patient.model.PatientPaymentPdf;
import com.example.test.testingHMS.patient.model.PatientRegistration;
import com.example.test.testingHMS.patient.serviceImpl.CashPlusCardServiceImpl;
import com.example.test.testingHMS.patient.serviceImpl.PatientDetailsServiceImpl;
import com.example.test.testingHMS.patient.serviceImpl.PatientRegistrationServiceImpl;
import com.example.test.testingHMS.patient.serviceImpl.PaymentPdfServiceImpl;
import com.example.test.testingHMS.pharmacist.helper.RefSales;
import com.example.test.testingHMS.pharmacist.helper.RefSalesIds;
import com.example.test.testingHMS.pharmacist.model.Location;
import com.example.test.testingHMS.pharmacist.model.MedicineDetails;
import com.example.test.testingHMS.pharmacist.model.MedicineProcurement;
import com.example.test.testingHMS.pharmacist.model.MedicineQuantity;
import com.example.test.testingHMS.pharmacist.model.Sales;
import com.example.test.testingHMS.pharmacist.model.SalesPaymentPdf;
import com.example.test.testingHMS.pharmacist.repository.MedicineDetailsRepository;
import com.example.test.testingHMS.pharmacist.repository.MedicineProcurementRepository;
import com.example.test.testingHMS.pharmacist.repository.MedicineQuantityRepository;
import com.example.test.testingHMS.pharmacist.repository.SalesRepository;
import com.example.test.testingHMS.pharmacist.repository.SalesReturnRepository;
import com.example.test.testingHMS.pharmacist.service.SalesService;
import com.example.test.testingHMS.pharmacyShopDetails.model.PharmacyShopDetails;
import com.example.test.testingHMS.pharmacyShopDetails.repository.PharmacyShopDetailsRepository;
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
public class SalesServiceImpl implements SalesService {

	private static final Logger Logger = LoggerFactory.getLogger(SalesServiceImpl.class);
	@Autowired
	PatientDetailsServiceImpl patientDetailsServiceImpl;
	
	
	@Autowired
	PaymentPdfServiceImpl paymentPdfServiceImpl;
	
	@Autowired
	ServletContext context;

	@Autowired
	SalesRepository salesRepository;

	@Autowired
	SalesReturnRepository salesReturnRepository;

	@Autowired
	FinalBillingRepository finalBillingRepository;

	@Autowired
	NumberToWordsConverter numberToWordsConverter;

	@Autowired
	RefSalesIds refSalesIds;

	@Autowired
	UserServiceImpl userServiceImpl;

	@Autowired
	MedicineDetailsRepository medicineDetailsRepository;

	@Autowired
	FinalBillingServiceImpl finalBillingServcieImpl;

	@Autowired
	MedicineProcurementServiceImpl medicineProcurementServiceImpl;

	@Autowired
	SalesServiceImpl salesServiceImpl;

	@Autowired
	ChargeBillServiceImpl chargeBillServiceImpl;

	@Autowired
	PharmacyShopDetailsRepository pharmacyShopDetailsRepository;

	@Autowired
	ChargeBillRepository chargeBillRepository;

	@Autowired
	MedicineProcurementRepository medicineProcurementRepository;

	@Autowired
	MedicineDetailsServiceImpl medicineDetailsServiceImpl;

	@Autowired
	ResourceLoader resourceLoader;

	@Autowired
	LocationServiceImpl locationServiceImpl;

	@Autowired
	CashPlusCardServiceImpl cashPlusCardServiceImpl;

	@Autowired
	SalesPaymentPdf salesPaymentPdf;

	@Autowired
	SalesPaymentPdfServiceImpl salesPaymentPdfServiceImpl;

	@Autowired
	PatientRegistrationServiceImpl patientRegistrationServiceImpl;

	@Autowired
	MedicineQuantityServiceImpl medicineQuantityServiceImpl;

	@Autowired
	MedicineQuantityRepository medicineQuantityRepository;

	@Value("${hospital.logo}")
	private Resource hospitalLogo;

	public String getNextSaleNo() {
		Sales sales = salesRepository.findFirstByOrderBySaleNoDesc();
		String nextId = null;
		if (sales == null) {
			nextId = "SL0000001";

		} else {
			int nextIntId = Integer.parseInt(sales.getSaleNo().substring(2));
			nextIntId += 1;
			nextId = "SL" + String.format("%07d", nextIntId);

		}
		return nextId;
	}

	public String getNextBillNo() {
		Sales sales = salesRepository.findFirstByOrderBySaleNoDesc();
		String nextId = null;
		if (sales == null) {
			nextId = "BL0000001";

		} else {
			int nextIntId = Integer.parseInt(sales.getBillNo().substring(2));
			nextIntId += 1;
			nextId = "BL" + String.format("%07d", nextIntId);

		}
		return nextId;
	}

	public static PdfPCell createCell(String content, float borderWidth, int colspan, int alignment, Font redFont) {
		PdfPCell cell = new PdfPCell(new Phrase(content));
		cell.setBorderWidth(borderWidth);
		cell.setColspan(colspan);
		cell.setHorizontalAlignment(alignment);
		return cell;
	}

	public List<Object> displaySalesList(String days) {

		Iterable<Sales> sales = null;

		List<FinalBilling> finalBilling = null;

		String today = Timestamp.valueOf(LocalDateTime.now()).toString().substring(0, 10);
		String nextDay = null;
		String fromDay = null;

		if (days.equalsIgnoreCase("2")) {
			nextDay = LocalDate.parse(today).plusDays(1).toString();
			fromDay = LocalDate.parse(today).plusDays(-2).toString();
			// sales = salesRepository.findTheUserWiseDetails(fromDay, nextDay);
			finalBilling = finalBillingRepository.forSales("Sales", fromDay, nextDay);
		} else if (days.equalsIgnoreCase("7")) {
			nextDay = LocalDate.parse(today).plusDays(1).toString();
			fromDay = LocalDate.parse(today).plusDays(-7).toString();
			// sales = salesRepository.findTheUserWiseDetails(fromDay, nextDay);
			finalBilling = finalBillingRepository.forSales("Sales", fromDay, nextDay);
		} else if (days.equalsIgnoreCase("15")) {
			nextDay = LocalDate.parse(today).plusDays(1).toString();
			fromDay = LocalDate.parse(today).plusDays(-15).toString();
			// sales = salesRepository.findTheUserWiseDetails(fromDay, nextDay);
			finalBilling = finalBillingRepository.forSales("Sales", fromDay, nextDay);
		} else if (days.equalsIgnoreCase("30")) {
			nextDay = LocalDate.parse(today).plusDays(1).toString();
			fromDay = LocalDate.parse(today).plusDays(-30).toString();
			// sales = salesRepository.findTheUserWiseDetails(fromDay, nextDay);
			finalBilling = finalBillingRepository.forSales("Sales", fromDay, nextDay);
		} else if (days.equalsIgnoreCase("ALL")) {

			// sales=salesRepository.findAllSales();
			finalBilling = finalBillingRepository.findBillType("Sales");
		}

		String userName = null;
		String billNo = null;
		float amount = 0;
		float returnAmount = 0;
		String salesdate = null;
		List<Object> list = new ArrayList<>();

		List<String> billList = new ArrayList<>();
		Map<String, String> map = null;
		for (FinalBilling finalBillingInfo : finalBilling) {

			map = new HashMap<String, String>();
			User user = userServiceImpl.findOneByUserId(finalBillingInfo.getUpdatedBy());

			if (user != null) {
				userName = (user.getMiddleName() != null)
						? user.getFirstName() + ConstantValues.ONE_SPACE_STRING + user.getMiddleName()
								+ ConstantValues.ONE_SPACE_STRING + user.getLastName()
						: user.getFirstName() + ConstantValues.ONE_SPACE_STRING + user.getLastName();
			}
			String saleDate = String.valueOf(finalBillingInfo.getUpdatedDate().toString());
			SimpleDateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			SimpleDateFormat toFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
			try {
				salesdate = toFormat.format(fromFormat.parse(saleDate));
			} catch (Exception e) { // TODO Auto-generated catch block e.printStackTrace();

			}

			map = new HashMap<String, String>();
			map.put("billNo", finalBillingInfo.getBillNo());
			map.put("mobileNo", String.valueOf(finalBillingInfo.getMobile()));
			map.put("patientName", finalBillingInfo.getName());
			map.put("Date", salesdate);
			map.put("paymentType", finalBillingInfo.getPaymentType());
			map.put("userName", userName);
			map.put("Amount", String.valueOf(Math.round(finalBillingInfo.getTotalAmount())));

			list.add(map);
		}

		return list;

	}

	@Transactional
	public SalesPaymentPdf computeSave(Sales sales, Principal principal) {
		String regId = null;
		SalesPaymentPdf salesPaymentPdf = null;
		String paymentMode = "";
		PatientRegistration patientRegistration = null;
		float finalCash = 0; // final billing
		float finalCard = 0; // final billing
		float finalCheque = 0; // final billing
		float finalDue = 0; // final billing
		long netAmount = 0;
		//float finalNetAmount = 0;
		float finalAmount = 0;
		String billNo = "";
		String patientName = "";
		String umr = "";
		String expdate = null;
		String refNo = null;

		String paid = null;
		String paymentType = null;

		String payCash = null;
		String payCard = null;
		String payDue = null;
		String payCheque = null;

		/*
		 * for multiple payments
		 */
		List<MultiplePayment> multiplePayment = sales.getMultiplePayment();

		for (MultiplePayment multiplePaymentInfo : multiplePayment) {

			if (multiplePaymentInfo.getPayType().equalsIgnoreCase(ConstantValues.CARD)
					|| multiplePaymentInfo.getPayType().equalsIgnoreCase("Credit Card")
					|| multiplePaymentInfo.getPayType().equalsIgnoreCase("Debit Card")
					|| multiplePaymentInfo.getPayType().equalsIgnoreCase(ConstantValues.CASH_PLUS_CARD)) {
				sales.setReferenceNumber(sales.getReferenceNumber());
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

		System.out.println(paymentType);

		/*
		 * if (sales.getPaymentType() == null) { throw new
		 * RuntimeException(ConstantValues.ENTER_PAYMENT_TYPE_ERROR_MSG); }
		 */

		if (sales.getRegId() != null) {
			patientRegistration = patientRegistrationServiceImpl.findByRegId(sales.getRegId());

			if (patientRegistration.isBlockedStatus()) {
				throw new RuntimeException("Payment for this patinet is blocked !");
			}

			PatientDetails patientDetails = patientRegistration.getPatientDetails();

			sales.setUmr(patientDetails.getUmr());
			sales.setName(patientDetails.getFirstName() + " " + patientDetails.getLastName());
			sales.setMobileNo(patientDetails.getMobile());
			sales.setPatientRegistration(patientRegistration);
			sales.setIpSettledFlag(ConstantValues.IP_SETTLED_FLAG_NO);
			umr = patientRegistration.getPatientDetails().getUmr();

			if (sales.getPatientRegistration().getPatientDetails().getMiddleName() != null) {
				patientName = sales.getPatientRegistration().getPatientDetails().getTitle() + ". "
						+ sales.getPatientRegistration().getPatientDetails().getFirstName() + " "
						+ sales.getPatientRegistration().getPatientDetails().getMiddleName() + " "
						+ sales.getPatientRegistration().getPatientDetails().getLastName();

			} else {
				patientName = sales.getPatientRegistration().getPatientDetails().getTitle() + ". "
						+ sales.getPatientRegistration().getPatientDetails().getFirstName() + " "
						+ sales.getPatientRegistration().getPatientDetails().getLastName();

			}
		}

		// for umr
		if (sales.getUmr() != null) {
			PatientDetails patientDetails = patientDetailsServiceImpl.findByUmr(sales.getUmr());

			sales.setUmr(sales.getUmr());
			sales.setName(patientDetails.getFirstName() + " " + patientDetails.getLastName());
			sales.setMobileNo(patientDetails.getMobile());
			sales.setIpSettledFlag(ConstantValues.IP_SETTLED_FLAG_NO);
			sales.setPaymentType(paymentType);
			sales.setPaid(paid);

		}

		// CreatedBy (Security)
		User userSecurity = userServiceImpl.findByUserName(principal.getName());
		String createdBy = userSecurity.getFirstName() + " " + userSecurity.getLastName();

		List<RefSales> refSales = sales.getRefSales();
		sales.setBillDate(Timestamp.valueOf(LocalDateTime.now()));
		sales.setSoldBy(userSecurity.getUserId());
		sales.setPatientSalesUser(userSecurity);
		Location location = locationServiceImpl.findByLocationName(sales.getLocation());
		sales.setPatientSaleslocation(location);
		sales.setBillNo(getNextBillNo());
		sales.setUpdatedBy(userSecurity.getUserId());
		sales.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
		billNo = sales.getBillNo();
		sales.setPaymentType(paymentType);

		String billNoo = chargeBillServiceImpl.getNextBillNo();

		for (RefSales refSalesList : refSales) {
			//finalNetAmount += refSalesList.getAmount();
			finalAmount += refSalesList.getMrp() * refSalesList.getQuantity();
			sales.setSaleNo(getNextSaleNo());
			sales.setAmount(refSalesList.getAmount());
			sales.setBatchNo(refSalesList.getBatchNo());
			//sales.setDiscount(refSalesList.getDiscount());
			sales.setGst(refSalesList.getGst());
			sales.setMedicineName(refSalesList.getMedicineName());
			MedicineDetails medicineDetails = medicineDetailsServiceImpl.findByName(refSalesList.getMedicineName());
			sales.setPatientSalesMedicineDetails(medicineDetails);
			sales.setMrp(refSalesList.getMrp());
			sales.setQuantity(refSalesList.getQuantity());
			sales.setActualAmount(refSalesList.getAmount());
			sales.setExpireDate(refSalesList.getExpDate());
			sales.setCostPrice(refSalesList.getMrp() * refSalesList.getQuantity());
			// for medicine quantity
			expdate = refSalesList.getExpDate();

			MedicineQuantity medicineQuantity = new MedicineQuantity();

			MedicineDetails medicineDetailsQuantity = medicineDetailsServiceImpl.findByName(sales.getMedicineName());

			MedicineQuantity medicineQuantityInfo = medicineQuantityServiceImpl
					.findByBatchIdAndMedicineDetails(refSalesList.getBatchNo(), medicineDetailsQuantity);

			if (medicineQuantityInfo != null) {
				long totalSold = medicineQuantityInfo.getSold();
				totalSold += sales.getQuantity();
				medicineQuantityInfo.setSold(totalSold);
				medicineQuantityInfo
						.setBalance(medicineQuantityInfo.getTotalQuantity() - medicineQuantityInfo.getSold());
				medicineQuantityRepository.save(medicineQuantityInfo);

			}

			// for patient sales
			if (sales.getRegId() != null) {
				regId = sales.getRegId();
				if ((patientRegistration.getpType().equals(ConstantValues.INPATIENT)
						|| patientRegistration.getpType().equals(ConstantValues.DAYCARE)
						|| patientRegistration.getpType().equals(ConstantValues.EMERGENCY))
						&& (paymentType.equalsIgnoreCase(ConstantValues.DUE))) {
					// patientSales.setPaid(paid);
					sales.setPaid(paid);
					ChargeBill chargeBill = new ChargeBill();
					chargeBill.setChargeBillId(chargeBillServiceImpl.getNextId());
					chargeBill.setPatRegId(patientRegistration);
					chargeBill.setMrp(sales.getMrp());
					chargeBill.setAmount(refSalesList.getQuantity() * refSalesList.getMrp());
					chargeBill.setDiscount(sales.getDiscount());
					List<ChargeBill> chargeBillList = chargeBillServiceImpl.findByPatRegId(patientRegistration);
					if (chargeBillList.isEmpty()) {
						chargeBill.setBillNo(billNoo);
					} else {
						chargeBill.setBillNo(chargeBillList.get(0).getBillNo());
					}
					chargeBill.setPaid(paid);
					chargeBill.setQuantity(sales.getQuantity());
					chargeBill.setNetAmount(refSalesList.getAmount());
					netAmount = (long) refSalesList.getAmount();
					chargeBill.setInsertedDate(Timestamp.valueOf(LocalDateTime.now()));
					chargeBill.setSaleId(sales);
					chargeBill.setUpdatedBy(userSecurity.getUserId());
					chargeBill.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
					chargeBill.setPaymentType(paymentType);
					chargeBill.setInsertedBy(userSecurity.getUserId());
					chargeBill.setIpSettledFlag(ConstantValues.IP_SETTLED_FLAG_NO);
					chargeBillRepository.save(chargeBill);
				} else if ((patientRegistration.getpType().equals(ConstantValues.INPATIENT)
						|| patientRegistration.getpType().equals(ConstantValues.DAYCARE)
						|| patientRegistration.getpType().equals(ConstantValues.EMERGENCY))
						&& (finalDue == 0 || finalCash != 0 || finalCard != 0)) {
					// patientSales.setPaid(paid);
					sales.setPaid(paid);
					ChargeBill chargeBill = new ChargeBill();
					chargeBill.setChargeBillId(chargeBillServiceImpl.getNextId());
					chargeBill.setPatRegId(patientRegistration);
					chargeBill.setMrp(sales.getMrp());
					chargeBill.setPaymentType(paymentType);
					chargeBill.setUpdatedBy(userSecurity.getUserId());
					chargeBill.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
					chargeBill.setAmount(refSalesList.getQuantity() * refSalesList.getMrp());
					chargeBill.setDiscount(sales.getDiscount());
					chargeBill.setIpSettledFlag(ConstantValues.IP_SETTLED_FLAG_NO);
					List<ChargeBill> chargeBillList = chargeBillServiceImpl.findByPatRegId(patientRegistration);
					if (chargeBillList.isEmpty()) {
						chargeBill.setBillNo(billNoo);
					} else {
						chargeBill.setBillNo(chargeBillList.get(0).getBillNo());
					}
					chargeBill.setPaid(paid);
					chargeBill.setQuantity(sales.getQuantity());
					chargeBill.setNetAmount(refSalesList.getAmount());
					chargeBill.setInsertedDate(Timestamp.valueOf(LocalDateTime.now()));
					chargeBill.setSaleId(sales);
					chargeBill.setInsertedBy(userSecurity.getUserId());
					chargeBillRepository.save(chargeBill);
				} else if (finalDue != 0 && finalCash == 0 && finalCard == 0) {
					sales.setPaid(paid);
					// patientSales.setPaid(paid);
				} else {
					sales.setPaid(paid);
					// patientSales.setPaid(paid);
				}

			} else {
				// paymentMode = sales.getPaymentType();
				if (finalDue == 0 || finalCash != 0 || finalCard != 0) {
					sales.setPaid(paid);
				} else if (finalDue != 0 && finalCash == 0 && finalCard == 0) {
					sales.setPaid(paid);

				}
			}

			// For Employee sales
			if (sales.getEmpId() != null) {
				User user = userServiceImpl.findOneByUserId(sales.getEmpId());
				sales.setEmployeeId(user);
			}

			salesRepository.save(sales);

		}
		refNo = sales.getReferenceNumber();

		float cashAmount = 0;
		float cardAmount = 0;
		float chequeAmount = 0;

		// Final Billing
		FinalBilling finalBilling = new FinalBilling();
		finalBilling.setBillNo(billNo);
		if (finalDue != 0) {
			finalBilling.setDueStatus(ConstantValues.YES);
		} else {
			finalBilling.setDueStatus(ConstantValues.NO);
		}
		finalBilling.setBillType("Sales");
		finalBilling.setCardAmount(finalCard);
		finalBilling.setCashAmount(finalCash);
		finalBilling.setChequeAmount(finalCheque);
		//finalBilling.setDiscAmount(Math.round(finalAmount - finalNetAmount));
		finalBilling.setDiscAmount(sales.getDiscount());
		finalBilling.setUpdatedBy(userSecurity.getUserId());
		finalBilling.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
		finalBilling.setDueAmount(finalDue);
		finalBilling.setInsertedDate(Timestamp.valueOf(LocalDateTime.now()));
		finalBilling.setFinalAmountPaid(Math.round(finalAmount-sales.getDiscount()));
		finalBilling.setFinalBillUser(userSecurity);
		finalBilling.setName((regId != null) ? patientName : sales.getName());
		finalBilling.setRegNo((regId != null) ? regId : null);
		finalBilling.setMobile(sales.getMobileNo());
		finalBilling.setPaymentType(paymentType);
		finalBilling.setTotalAmount(finalAmount);
		finalBilling.setUmrNo(umr);
		finalBillingServcieImpl.computeSave(finalBilling);

		// To find total amount of bill

		float total = 0.0f;
		List<Sales> listSales = findByBillNo(billNo);
		for (Sales listSalesInfo : listSales) {
			total += listSalesInfo.getAmount();
		}

		String roundOff = null;

		if (patientRegistration != null) {
			if (!patientRegistration.getpType().equals(ConstantValues.INPATIENT)) {
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

					hcell1 = new PdfPCell(new Phrase(sales.getBillNo(), redFont));
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

					hcel = new PdfPCell(
							new Phrase(sales.getPatientRegistration().getPatientDetails().getUmr(), redFont));
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

					hcel11 = new PdfPCell(new Phrase(sales.getPatientRegistration().getRegId(), redFont));
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

					hcel1 = new PdfPCell(
							new Phrase(sales.getPatientRegistration().getPatientDetails().getConsultant(), redFont));
					hcel1.setBorder(Rectangle.NO_BORDER);
					hcel1.setPaddingLeft(-25f);
					table2.addCell(hcel1);

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

					hcell15 = new PdfPCell(new Phrase("Pharmacy Receipt", redFont3));
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

					PdfPTable table1 = new PdfPTable(9);
					table1.setWidths(new float[] { 1.5f, 5.4f, 4.5f, 3f, 2f, 1.2f, 2f, 1.5f, 2.8f });

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
					hcell.setPaddingLeft(-15f);

					table1.addCell(hcell);

					hcell = new PdfPCell(new Phrase("Batch No", redFont));
					hcell.setBorder(Rectangle.NO_BORDER);
					hcell.setBackgroundColor(BaseColor.GRAY);
					hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell.setPaddingLeft(20f);
					table1.addCell(hcell);

					hcell = new PdfPCell(new Phrase("Exp Date", redFont));
					hcell.setBorder(Rectangle.NO_BORDER);
					hcell.setBackgroundColor(BaseColor.GRAY);
					hcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					hcell.setPaddingRight(-5f);
					table1.addCell(hcell);

					hcell = new PdfPCell(new Phrase("Qty", redFont));
					hcell.setBorder(Rectangle.NO_BORDER);
					hcell.setBackgroundColor(BaseColor.GRAY);
					hcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					hcell.setPaddingRight(-8);
					table1.addCell(hcell);

					/*
					 * hcell = new PdfPCell(new Phrase("Disc(%)", redFont));
					 * hcell.setBorder(Rectangle.NO_BORDER);
					 * hcell.setBackgroundColor(BaseColor.GRAY);
					 * hcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					 * hcell.setPaddingRight(-25f); table1.addCell(hcell);
					 */
					hcell = new PdfPCell(new Phrase("MRP", redFont));
					hcell.setBorder(Rectangle.NO_BORDER);
					hcell.setBackgroundColor(BaseColor.GRAY);
					hcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					hcell.setPaddingRight(-10f);
					table1.addCell(hcell);

					hcell = new PdfPCell(new Phrase("GST", redFont));
					hcell.setBorder(Rectangle.NO_BORDER);
					hcell.setBackgroundColor(BaseColor.GRAY);
					hcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					hcell.setPaddingRight(-10f);
					table1.addCell(hcell);

					hcell = new PdfPCell(new Phrase("Sale Value", redFont));
					hcell.setBorder(Rectangle.NO_BORDER);
					hcell.setBackgroundColor(BaseColor.GRAY);
					hcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					table1.addCell(hcell);
					int count = 0;

					PdfPTable table20 = new PdfPTable(9);
					table20.setWidths(new float[] { 1.5f, 5.4f, 4.5f, 3f, 2f, 1.2f, 2f, 1.5f, 2f });

					table20.setSpacingBefore(10);

					for (RefSales a : refSales) {

						MedicineDetails medicineDetails1 = medicineDetailsServiceImpl.findByName(a.getMedicineName());
						List<MedicineProcurement> medicineProcurement = medicineProcurementServiceImpl
								.findByBatchAndMedicine(a.getBatchNo(), medicineDetails1.getMedicineId());
						PdfPCell cell;

						cell = new PdfPCell(new Phrase(String.valueOf(count = count + 1), redFonts));
						cell.setBorder(Rectangle.NO_BORDER);
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table20.addCell(cell);

						cell = new PdfPCell(new Phrase(a.getMedicineName(), redFonts));
						cell.setBorder(Rectangle.NO_BORDER);
						cell.setPaddingLeft(-1);
						// cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						// cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						table20.addCell(cell);

						cell = new PdfPCell(new Phrase(medicineDetails1.getManufacturer(), redFonts));
						cell.setBorder(Rectangle.NO_BORDER);
						// cell.setPaddingLeft(-30);
						// cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						// cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setPaddingLeft(-15f);
						table20.addCell(cell);

						cell = new PdfPCell(new Phrase(String.valueOf(a.getBatchNo()), redFonts));
						cell.setBorder(Rectangle.NO_BORDER);

						// cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setPaddingLeft(12);
						// cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						table20.addCell(cell);

						// for convert db date to dmy format

						/*
						 * String expdate=medicineProcurement.get(medicineProcurement.size() -
						 * 1).getExpDate().toString().substring(0,10); SimpleDateFormat fromFormat = new
						 * SimpleDateFormat("yyyy-MM-dd"); SimpleDateFormat toFormat = new
						 * SimpleDateFormat("dd-MM-yyyy");
						 * expdate=toFormat.format(fromFormat.parse(expdate));
						 */

						try {
							expdate = a.getExpDate().toString().substring(0, 10);
							SimpleDateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd");
							SimpleDateFormat toFormat = new SimpleDateFormat("dd-MM-yyyy");
							expdate = toFormat.format(fromFormat.parse(expdate));

						} catch (Exception e) {
							e.printStackTrace();
						}

						cell = new PdfPCell(new Phrase(expdate, redFonts));
						cell.setBorder(Rectangle.NO_BORDER);
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
						cell.setPaddingLeft(-10);
						table20.addCell(cell);

						cell = new PdfPCell(new Phrase(String.valueOf((a.getQuantity())), redFonts));
						cell.setBorder(Rectangle.NO_BORDER);
						cell.setPaddingLeft(8);
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table20.addCell(cell);

						/*
						 * cell = new PdfPCell(new Phrase(String.valueOf((a.getDiscount())), redFonts));
						 * cell.setBorder(Rectangle.NO_BORDER); cell.setPaddingLeft(8);
						 * cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						 * cell.setHorizontalAlignment(Element.ALIGN_CENTER); cell.setPaddingLeft(10f);
						 * table20.addCell(cell);
						 */
						cell = new PdfPCell(new Phrase(String.valueOf((a.getMrp())), redFonts));
						cell.setBorder(Rectangle.NO_BORDER);
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
						cell.setPaddingRight(10);
						table20.addCell(cell);

						cell = new PdfPCell(new Phrase(String.valueOf((a.getGst())), redFonts));
						cell.setBorder(Rectangle.NO_BORDER);
						cell.setPaddingLeft(-15);
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
						table20.addCell(cell);

						cell = new PdfPCell(new Phrase(String.valueOf((a.getAmount())), redFonts));
						cell.setBorder(Rectangle.NO_BORDER);
						cell.setPaddingLeft(5);
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
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

					int ttl = (int) Math.round(total)-(int)(sales.getDiscount());
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
					hcell21 = new PdfPCell(new Phrase("Total Sale Value", redFont));
					hcell21.setBorder(Rectangle.NO_BORDER);
					hcell21.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell21.setPaddingLeft(85f);
					table4.addCell(hcell21);

					hcell21 = new PdfPCell(new Phrase(":", redFont));
					hcell21.setBorder(Rectangle.NO_BORDER);
					hcell21.setHorizontalAlignment(Element.ALIGN_RIGHT);
					hcell21.setPaddingRight(-30f);
					table4.addCell(hcell21);

					hcell21 = new PdfPCell(new Phrase(String.valueOf(Math.round(total * 100.0) / 100.0), redFont));
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

				
					hcell04 = new PdfPCell(new Phrase(String.valueOf(Math.round(total)), redFont));
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

					hcell041 = new PdfPCell(new Phrase(String.valueOf(Math.round(sales.getDiscount())), redFont));
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

					hcell9 = new PdfPCell(new Phrase(String.valueOf(Math.round(total)-sales.getDiscount()), redFont));
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
					hcell34 = new PdfPCell(new Phrase(sales.getPaymentType(), redFont2));
					hcell34.setBorder(Rectangle.NO_BORDER);
					hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell34.setPaddingLeft(10f);
					table13.addCell(hcell34);

					hcell34 = new PdfPCell(new Phrase(String.valueOf(Math.round(total)), redFont2));
					hcell34.setBorder(Rectangle.NO_BORDER);
					hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell34.setPaddingLeft(35f);
					table13.addCell(hcell34);
					if (sales.getPaymentType().equalsIgnoreCase("card")
							|| sales.getPaymentType().equalsIgnoreCase("cash+card")) {
						hcell34 = new PdfPCell(new Phrase(refNo, redFont2));
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

					// for new row end

					PharmacyShopDetails pharmacyShopDetails = pharmacyShopDetailsRepository
							.findByShopLocation(sales.getLocation());

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

					PdfPCell hcell7;

					hcell7 = new PdfPCell(new Phrase("Instructions  : "
							+ "1) Returns are accepted within TEN(10)Days. \n                       2) Fridge Items once sold cannot be taken Back.",
							redFont));
					hcell7.setBorder(Rectangle.NO_BORDER);
					hcell7.setPaddingLeft(-50f);
					table5.addCell(hcell7);

					hcell7 = new PdfPCell(new Phrase("Pharmacist"));
					hcell7.setBorder(Rectangle.NO_BORDER);
					hcell7.setHorizontalAlignment(Element.ALIGN_RIGHT);
					hcell7.setPaddingTop(25f);
					table5.addCell(hcell7);

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
					salesPaymentPdf.setFileName(billNo + "-" + regId + " Medicine Sales");
					salesPaymentPdf.setFileuri(uri);
					salesPaymentPdf.setPid(salesPaymentPdfServiceImpl.getNextId());
					salesPaymentPdf.setData(pdfByte);
					salesPaymentPdfServiceImpl.save(salesPaymentPdf);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			else if (patientRegistration.getpType().equals(ConstantValues.INPATIENT)
					&& sales.getPaymentType().equalsIgnoreCase("Advance")) {
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

					hcell1 = new PdfPCell(new Phrase(sales.getBillNo(), redFont));
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

					hcel = new PdfPCell(
							new Phrase(sales.getPatientRegistration().getPatientDetails().getUmr(), redFont));
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

					hcel11 = new PdfPCell(new Phrase(sales.getPatientRegistration().getRegId(), redFont));
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

					hcel1 = new PdfPCell(
							new Phrase(sales.getPatientRegistration().getPatientDetails().getConsultant(), redFont));
					hcel1.setBorder(Rectangle.NO_BORDER);
					hcel1.setPaddingLeft(-25f);
					table2.addCell(hcel1);

					cell0.setFixedHeight(100f);
					cell0.setColspan(2);
					cell0.addElement(table2);
					table.addCell(cell0);

					PdfPCell cell19 = new PdfPCell();

					PdfPTable table21 = new PdfPTable(3);
					table21.setWidths(new float[] { 4f, 8f, 5f });
					table21.setSpacingBefore(10);

					PdfPCell hcell15;
					hcell15 = new PdfPCell(new Phrase("", redFont));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setPaddingLeft(-70f);
					table21.addCell(hcell15);

					hcell15 = new PdfPCell(new Phrase("Pharmacy Advance Receipt", redFont3));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setPaddingLeft(10);
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

					PdfPTable table1 = new PdfPTable(9);
					table1.setWidths(new float[] { 1.5f, 5.4f, 4.5f, 3f, 2f, 1.2f,  2f, 1.5f, 2.8f });

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
					hcell.setPaddingLeft(-15f);

					table1.addCell(hcell);

					hcell = new PdfPCell(new Phrase("Batch No", redFont));
					hcell.setBorder(Rectangle.NO_BORDER);
					hcell.setBackgroundColor(BaseColor.GRAY);
					hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell.setPaddingLeft(20f);
					table1.addCell(hcell);

					hcell = new PdfPCell(new Phrase("Exp Date", redFont));
					hcell.setBorder(Rectangle.NO_BORDER);
					hcell.setBackgroundColor(BaseColor.GRAY);
					hcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					hcell.setPaddingRight(-5f);
					table1.addCell(hcell);

					hcell = new PdfPCell(new Phrase("Qty", redFont));
					hcell.setBorder(Rectangle.NO_BORDER);
					hcell.setBackgroundColor(BaseColor.GRAY);
					hcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					hcell.setPaddingRight(-8);
					table1.addCell(hcell);

					/*
					 * hcell = new PdfPCell(new Phrase("Disc(%)", redFont));
					 * hcell.setBorder(Rectangle.NO_BORDER);
					 * hcell.setBackgroundColor(BaseColor.GRAY);
					 * hcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					 * hcell.setPaddingRight(-25f); table1.addCell(hcell);
					 */
					hcell = new PdfPCell(new Phrase("MRP", redFont));
					hcell.setBorder(Rectangle.NO_BORDER);
					hcell.setBackgroundColor(BaseColor.GRAY);
					hcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					hcell.setPaddingRight(-10f);
					table1.addCell(hcell);

					hcell = new PdfPCell(new Phrase("GST", redFont));
					hcell.setBorder(Rectangle.NO_BORDER);
					hcell.setBackgroundColor(BaseColor.GRAY);
					hcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					hcell.setPaddingRight(-10f);
					table1.addCell(hcell);

					hcell = new PdfPCell(new Phrase("Sale Value", redFont));
					hcell.setBorder(Rectangle.NO_BORDER);
					hcell.setBackgroundColor(BaseColor.GRAY);
					hcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					table1.addCell(hcell);
					int count = 0;

					PdfPTable table20 = new PdfPTable(9);
					table20.setWidths(new float[] { 1.5f, 5.4f, 4.5f, 3f, 2f, 1.2f, 2f, 1.5f, 2f });

					table20.setSpacingBefore(10);

					for (RefSales a : refSales) {

						MedicineDetails medicineDetails1 = medicineDetailsServiceImpl.findByName(a.getMedicineName());
						List<MedicineProcurement> medicineProcurement = medicineProcurementServiceImpl
								.findByBatchAndMedicine(a.getBatchNo(), medicineDetails1.getMedicineId());
						PdfPCell cell;

						cell = new PdfPCell(new Phrase(String.valueOf(count = count + 1), redFonts));
						cell.setBorder(Rectangle.NO_BORDER);
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table20.addCell(cell);

						cell = new PdfPCell(new Phrase(a.getMedicineName(), redFonts));
						cell.setBorder(Rectangle.NO_BORDER);
						cell.setPaddingLeft(-1);
						// cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						// cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						table20.addCell(cell);

						cell = new PdfPCell(new Phrase(medicineDetails1.getManufacturer(), redFonts));
						cell.setBorder(Rectangle.NO_BORDER);
						// cell.setPaddingLeft(-30);
						// cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						// cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setPaddingLeft(-15f);
						table20.addCell(cell);

						cell = new PdfPCell(new Phrase(String.valueOf(a.getBatchNo()), redFonts));
						cell.setBorder(Rectangle.NO_BORDER);

						// cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setPaddingLeft(12);
						// cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						table20.addCell(cell);

						// for convert db date to dmy format

						/*
						 * String expdate=medicineProcurement.get(medicineProcurement.size() -
						 * 1).getExpDate().toString().substring(0,10); SimpleDateFormat fromFormat = new
						 * SimpleDateFormat("yyyy-MM-dd"); SimpleDateFormat toFormat = new
						 * SimpleDateFormat("dd-MM-yyyy");
						 * expdate=toFormat.format(fromFormat.parse(expdate));
						 */

						try {
							expdate = a.getExpDate().toString().substring(0, 10);
							SimpleDateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd");
							SimpleDateFormat toFormat = new SimpleDateFormat("dd-MM-yyyy");
							expdate = toFormat.format(fromFormat.parse(expdate));

						} catch (Exception e) {
							e.printStackTrace();
						}

						cell = new PdfPCell(new Phrase(expdate, redFonts));
						cell.setBorder(Rectangle.NO_BORDER);
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
						cell.setPaddingLeft(-10);
						table20.addCell(cell);

						cell = new PdfPCell(new Phrase(String.valueOf((a.getQuantity())), redFonts));
						cell.setBorder(Rectangle.NO_BORDER);
						cell.setPaddingLeft(8);
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table20.addCell(cell);

						/*
						 * cell = new PdfPCell(new Phrase(String.valueOf((a.getDiscount())), redFonts));
						 * cell.setBorder(Rectangle.NO_BORDER); cell.setPaddingLeft(8);
						 * cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						 * cell.setHorizontalAlignment(Element.ALIGN_CENTER); cell.setPaddingLeft(10f);
						 * table20.addCell(cell);
						 */
						cell = new PdfPCell(new Phrase(String.valueOf((a.getMrp())), redFonts));
						cell.setBorder(Rectangle.NO_BORDER);
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
						cell.setPaddingRight(10);
						table20.addCell(cell);

						cell = new PdfPCell(new Phrase(String.valueOf((a.getGst())), redFonts));
						cell.setBorder(Rectangle.NO_BORDER);
						cell.setPaddingLeft(-15);
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
						table20.addCell(cell);

						cell = new PdfPCell(new Phrase(String.valueOf((a.getAmount())), redFonts));
						cell.setBorder(Rectangle.NO_BORDER);
						cell.setPaddingLeft(5);
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
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

					int ttl = (int) Math.round(total)-(int)sales.getDiscount();
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
					hcell21 = new PdfPCell(new Phrase("Total Sale Value", redFont));
					hcell21.setBorder(Rectangle.NO_BORDER);
					hcell21.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell21.setPaddingLeft(85f);
					table4.addCell(hcell21);

					hcell21 = new PdfPCell(new Phrase(":", redFont));
					hcell21.setBorder(Rectangle.NO_BORDER);
					hcell21.setHorizontalAlignment(Element.ALIGN_RIGHT);
					hcell21.setPaddingRight(-30f);
					table4.addCell(hcell21);

					hcell21 = new PdfPCell(new Phrase(String.valueOf(Math.round(total * 100.0) / 100.0), redFont));
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

						hcell04 = new PdfPCell(new Phrase(String.valueOf(Math.round(total)), redFont));
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

					hcell041 = new PdfPCell(new Phrase(String.valueOf(Math.round(sales.getDiscount())), redFont));
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

					hcell9 = new PdfPCell(new Phrase(String.valueOf(Math.round(total)-sales.getDiscount()), redFont));
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
					hcell34 = new PdfPCell(new Phrase(sales.getPaymentType(), redFont2));
					hcell34.setBorder(Rectangle.NO_BORDER);
					hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell34.setPaddingLeft(10f);
					table13.addCell(hcell34);

					hcell34 = new PdfPCell(new Phrase(String.valueOf(Math.round(total)), redFont2));
					hcell34.setBorder(Rectangle.NO_BORDER);
					hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell34.setPaddingLeft(35f);
					table13.addCell(hcell34);

					if (sales.getPaymentType().equalsIgnoreCase("card")
							|| sales.getPaymentType().equalsIgnoreCase("cash+card")) {
						hcell34 = new PdfPCell(new Phrase(refNo, redFont2));
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

					cell33.setFixedHeight(35f);
					cell33.setColspan(2);
					table13.setWidthPercentage(100f);
					cell33.addElement(table13);
					table.addCell(cell33);

					// for new row end

					PharmacyShopDetails pharmacyShopDetails = pharmacyShopDetailsRepository
							.findByShopLocation(sales.getLocation());

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

					PdfPCell hcell7;

					hcell7 = new PdfPCell(new Phrase("Instructions  : "
							+ "1) Returns are accepted within TEN(10)Days. \n                       2) Fridge Items once sold cannot be taken Back.",
							redFont));
					hcell7.setBorder(Rectangle.NO_BORDER);
					hcell7.setPaddingLeft(-50f);
					table5.addCell(hcell7);

					hcell7 = new PdfPCell(new Phrase("Pharmacist"));
					hcell7.setBorder(Rectangle.NO_BORDER);
					hcell7.setHorizontalAlignment(Element.ALIGN_RIGHT);
					hcell7.setPaddingTop(25f);
					table5.addCell(hcell7);

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
					salesPaymentPdf.setFileName(billNo + "-" + regId + " Medicine Sales");
					salesPaymentPdf.setFileuri(uri);
					salesPaymentPdf.setPid(salesPaymentPdfServiceImpl.getNextId());
					salesPaymentPdf.setData(pdfByte);
					salesPaymentPdfServiceImpl.save(salesPaymentPdf);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			else if (patientRegistration.getpType().equals(ConstantValues.INPATIENT)
					&& sales.getPaymentType().equalsIgnoreCase("Advance")) {
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

					hcell1 = new PdfPCell(new Phrase(sales.getBillNo(), redFont));
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

					hcel = new PdfPCell(
							new Phrase(sales.getPatientRegistration().getPatientDetails().getUmr(), redFont));
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

					hcel11 = new PdfPCell(new Phrase(sales.getPatientRegistration().getRegId(), redFont));
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

					hcel1 = new PdfPCell(
							new Phrase(sales.getPatientRegistration().getPatientDetails().getConsultant(), redFont));
					hcel1.setBorder(Rectangle.NO_BORDER);
					hcel1.setPaddingLeft(-25f);
					table2.addCell(hcel1);

					cell0.setFixedHeight(100f);
					cell0.setColspan(2);
					cell0.addElement(table2);
					table.addCell(cell0);

					PdfPCell cell19 = new PdfPCell();

					PdfPTable table21 = new PdfPTable(3);
					table21.setWidths(new float[] { 4f, 8f, 5f });
					table21.setSpacingBefore(10);

					PdfPCell hcell15;
					hcell15 = new PdfPCell(new Phrase("", redFont));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setPaddingLeft(-70f);
					table21.addCell(hcell15);

					hcell15 = new PdfPCell(new Phrase("Pharmacy Advance Receipt", redFont3));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setPaddingLeft(10);
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

					PdfPTable table1 = new PdfPTable(9);
					table1.setWidths(new float[] { 1.5f, 5.4f, 4.5f, 3f, 2f, 1.2f,  2f, 1.5f, 2.8f });

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
					hcell.setPaddingLeft(-15f);

					table1.addCell(hcell);

					hcell = new PdfPCell(new Phrase("Batch No", redFont));
					hcell.setBorder(Rectangle.NO_BORDER);
					hcell.setBackgroundColor(BaseColor.GRAY);
					hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell.setPaddingLeft(20f);
					table1.addCell(hcell);

					hcell = new PdfPCell(new Phrase("Exp Date", redFont));
					hcell.setBorder(Rectangle.NO_BORDER);
					hcell.setBackgroundColor(BaseColor.GRAY);
					hcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					hcell.setPaddingRight(-5f);
					table1.addCell(hcell);

					hcell = new PdfPCell(new Phrase("Qty", redFont));
					hcell.setBorder(Rectangle.NO_BORDER);
					hcell.setBackgroundColor(BaseColor.GRAY);
					hcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					hcell.setPaddingRight(-8);
					table1.addCell(hcell);

					/*
					 * hcell = new PdfPCell(new Phrase("Disc(%)", redFont));
					 * hcell.setBorder(Rectangle.NO_BORDER);
					 * hcell.setBackgroundColor(BaseColor.GRAY);
					 * hcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					 * hcell.setPaddingRight(-25f); table1.addCell(hcell);
					 */
					hcell = new PdfPCell(new Phrase("MRP", redFont));
					hcell.setBorder(Rectangle.NO_BORDER);
					hcell.setBackgroundColor(BaseColor.GRAY);
					hcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					hcell.setPaddingRight(-10f);
					table1.addCell(hcell);

					hcell = new PdfPCell(new Phrase("GST", redFont));
					hcell.setBorder(Rectangle.NO_BORDER);
					hcell.setBackgroundColor(BaseColor.GRAY);
					hcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					hcell.setPaddingRight(-10f);
					table1.addCell(hcell);

					hcell = new PdfPCell(new Phrase("Sale Value", redFont));
					hcell.setBorder(Rectangle.NO_BORDER);
					hcell.setBackgroundColor(BaseColor.GRAY);
					hcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					table1.addCell(hcell);
					int count = 0;

					PdfPTable table20 = new PdfPTable(9);
					table20.setWidths(new float[] { 1.5f, 5.4f, 4.5f, 3f, 2f, 1.2f,  2f, 1.5f, 2f });

					table20.setSpacingBefore(10);

					for (RefSales a : refSales) {

						MedicineDetails medicineDetails1 = medicineDetailsServiceImpl.findByName(a.getMedicineName());
						List<MedicineProcurement> medicineProcurement = medicineProcurementServiceImpl
								.findByBatchAndMedicine(a.getBatchNo(), medicineDetails1.getMedicineId());
						PdfPCell cell;

						cell = new PdfPCell(new Phrase(String.valueOf(count = count + 1), redFonts));
						cell.setBorder(Rectangle.NO_BORDER);
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table20.addCell(cell);

						cell = new PdfPCell(new Phrase(a.getMedicineName(), redFonts));
						cell.setBorder(Rectangle.NO_BORDER);
						cell.setPaddingLeft(-1);
						// cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						// cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						table20.addCell(cell);

						cell = new PdfPCell(new Phrase(medicineDetails1.getManufacturer(), redFonts));
						cell.setBorder(Rectangle.NO_BORDER);
						// cell.setPaddingLeft(-30);
						// cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						// cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setPaddingLeft(-15f);
						table20.addCell(cell);

						cell = new PdfPCell(new Phrase(String.valueOf(a.getBatchNo()), redFonts));
						cell.setBorder(Rectangle.NO_BORDER);

						// cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setPaddingLeft(12);
						// cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						table20.addCell(cell);

						// for convert db date to dmy format

						/*
						 * String expdate=medicineProcurement.get(medicineProcurement.size() -
						 * 1).getExpDate().toString().substring(0,10); SimpleDateFormat fromFormat = new
						 * SimpleDateFormat("yyyy-MM-dd"); SimpleDateFormat toFormat = new
						 * SimpleDateFormat("dd-MM-yyyy");
						 * expdate=toFormat.format(fromFormat.parse(expdate));
						 */

						try {
							expdate = a.getExpDate().toString().substring(0, 10);
							SimpleDateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd");
							SimpleDateFormat toFormat = new SimpleDateFormat("dd-MM-yyyy");
							expdate = toFormat.format(fromFormat.parse(expdate));

						} catch (Exception e) {
							e.printStackTrace();
						}

						cell = new PdfPCell(new Phrase(expdate, redFonts));
						cell.setBorder(Rectangle.NO_BORDER);
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
						cell.setPaddingLeft(-10);
						table20.addCell(cell);

						cell = new PdfPCell(new Phrase(String.valueOf((a.getQuantity())), redFonts));
						cell.setBorder(Rectangle.NO_BORDER);
						cell.setPaddingLeft(8);
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table20.addCell(cell);

						/*
						 * cell = new PdfPCell(new Phrase(String.valueOf((a.getDiscount())), redFonts));
						 * cell.setBorder(Rectangle.NO_BORDER); cell.setPaddingLeft(8);
						 * cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						 * cell.setHorizontalAlignment(Element.ALIGN_CENTER); cell.setPaddingLeft(10f);
						 * table20.addCell(cell);
						 * 
						 */						cell = new PdfPCell(new Phrase(String.valueOf((a.getMrp())), redFonts));
						cell.setBorder(Rectangle.NO_BORDER);
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
						cell.setPaddingRight(10);
						table20.addCell(cell);

						cell = new PdfPCell(new Phrase(String.valueOf((a.getGst())), redFonts));
						cell.setBorder(Rectangle.NO_BORDER);
						cell.setPaddingLeft(-15);
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
						table20.addCell(cell);

						cell = new PdfPCell(new Phrase(String.valueOf((a.getAmount())), redFonts));
						cell.setBorder(Rectangle.NO_BORDER);
						cell.setPaddingLeft(5);
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
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

					int ttl = (int) Math.round(total)-(int)sales.getDiscount();
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
					hcell21 = new PdfPCell(new Phrase("Total Sale Value", redFont));
					hcell21.setBorder(Rectangle.NO_BORDER);
					hcell21.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell21.setPaddingLeft(85f);
					table4.addCell(hcell21);

					hcell21 = new PdfPCell(new Phrase(":", redFont));
					hcell21.setBorder(Rectangle.NO_BORDER);
					hcell21.setHorizontalAlignment(Element.ALIGN_RIGHT);
					hcell21.setPaddingRight(-30f);
					table4.addCell(hcell21);

					hcell21 = new PdfPCell(new Phrase(String.valueOf(Math.round(total * 100.0) / 100.0), redFont));
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
					hcell04 = new PdfPCell(new Phrase(String.valueOf(Math.round(total)), redFont));
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

					hcell041 = new PdfPCell(new Phrase(String.valueOf(Math.round(sales.getDiscount())), redFont));
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

					hcell9 = new PdfPCell(new Phrase(String.valueOf(Math.round(total)-sales.getDiscount()), redFont));
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
					hcell34 = new PdfPCell(new Phrase(sales.getPaymentType(), redFont2));
					hcell34.setBorder(Rectangle.NO_BORDER);
					hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell34.setPaddingLeft(10f);
					table13.addCell(hcell34);

					hcell34 = new PdfPCell(new Phrase(String.valueOf(Math.round(total)), redFont2));
					hcell34.setBorder(Rectangle.NO_BORDER);
					hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell34.setPaddingLeft(35f);
					table13.addCell(hcell34);

					if (sales.getPaymentType().equalsIgnoreCase("card")
							|| sales.getPaymentType().equalsIgnoreCase("cash+card")) {
						hcell34 = new PdfPCell(new Phrase(refNo, redFont2));
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

					cell33.setFixedHeight(35f);
					cell33.setColspan(2);
					table13.setWidthPercentage(100f);
					cell33.addElement(table13);
					table.addCell(cell33);

					// for new row end

					PharmacyShopDetails pharmacyShopDetails = pharmacyShopDetailsRepository
							.findByShopLocation(sales.getLocation());

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

					PdfPCell hcell7;

					hcell7 = new PdfPCell(new Phrase("Instructions  : "
							+ "1) Returns are accepted within TEN(10)Days. \n                       2) Fridge Items once sold cannot be taken Back.",
							redFont));
					hcell7.setBorder(Rectangle.NO_BORDER);
					hcell7.setPaddingLeft(-50f);
					table5.addCell(hcell7);

					hcell7 = new PdfPCell(new Phrase("Pharmacist"));
					hcell7.setBorder(Rectangle.NO_BORDER);
					hcell7.setHorizontalAlignment(Element.ALIGN_RIGHT);
					hcell7.setPaddingTop(25f);
					table5.addCell(hcell7);

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
					salesPaymentPdf.setFileName(billNo + "-" + regId + " Medicine Sales");
					salesPaymentPdf.setFileuri(uri);
					salesPaymentPdf.setPid(salesPaymentPdfServiceImpl.getNextId());
					salesPaymentPdf.setData(pdfByte);
					salesPaymentPdfServiceImpl.save(salesPaymentPdf);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			else if (patientRegistration.getpType().equals(ConstantValues.INPATIENT)
					&& !sales.getPaymentType().equalsIgnoreCase("Advance")) {
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

					hcell1 = new PdfPCell(new Phrase(sales.getBillNo(), redFont));
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

					hcel = new PdfPCell(
							new Phrase(sales.getPatientRegistration().getPatientDetails().getUmr(), redFont));
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

					hcel11 = new PdfPCell(new Phrase(sales.getPatientRegistration().getRegId(), redFont));
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

					hcel1 = new PdfPCell(
							new Phrase(sales.getPatientRegistration().getPatientDetails().getConsultant(), redFont));
					hcel1.setBorder(Rectangle.NO_BORDER);
					hcel1.setPaddingLeft(-25f);
					table2.addCell(hcel1);

					cell0.setFixedHeight(100f);
					cell0.setColspan(2);
					cell0.addElement(table2);
					table.addCell(cell0);

					PdfPCell cell19 = new PdfPCell();

					PdfPTable table21 = new PdfPTable(3);
					table21.setWidths(new float[] { 4f, 8f, 5f });
					table21.setSpacingBefore(10);

					PdfPCell hcell15;
					hcell15 = new PdfPCell(new Phrase("", redFont));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setPaddingLeft(-70f);
					table21.addCell(hcell15);

					if (sales.getPaymentType().equalsIgnoreCase(ConstantValues.DUE)) {
						hcell15 = new PdfPCell(new Phrase("Pharmacy Due Receipt", redFont3));
						hcell15.setBorder(Rectangle.NO_BORDER);
						hcell15.setPaddingLeft(35);
						hcell15.setHorizontalAlignment(Element.ALIGN_CENTER);
						table21.addCell(hcell15);
					} else {
						hcell15 = new PdfPCell(new Phrase("Pharmacy Receipt", redFont3));
						hcell15.setBorder(Rectangle.NO_BORDER);
						hcell15.setPaddingLeft(35);
						hcell15.setHorizontalAlignment(Element.ALIGN_CENTER);
						table21.addCell(hcell15);
					}

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

					PdfPTable table1 = new PdfPTable(9);
					table1.setWidths(new float[] { 1.5f, 5.4f, 4.5f, 3f, 2f, 1.2f,  2f, 1.5f, 2.8f });

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
					hcell.setPaddingLeft(-15f);

					table1.addCell(hcell);

					hcell = new PdfPCell(new Phrase("Batch No", redFont));
					hcell.setBorder(Rectangle.NO_BORDER);
					hcell.setBackgroundColor(BaseColor.GRAY);
					hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell.setPaddingLeft(20f);
					table1.addCell(hcell);

					hcell = new PdfPCell(new Phrase("Exp Date", redFont));
					hcell.setBorder(Rectangle.NO_BORDER);
					hcell.setBackgroundColor(BaseColor.GRAY);
					hcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					hcell.setPaddingRight(-5f);
					table1.addCell(hcell);

					hcell = new PdfPCell(new Phrase("Qty", redFont));
					hcell.setBorder(Rectangle.NO_BORDER);
					hcell.setBackgroundColor(BaseColor.GRAY);
					hcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					hcell.setPaddingRight(-8);
					table1.addCell(hcell);

					/*
					 * hcell = new PdfPCell(new Phrase("Disc(%)", redFont));
					 * hcell.setBorder(Rectangle.NO_BORDER);
					 * hcell.setBackgroundColor(BaseColor.GRAY);
					 * hcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					 * hcell.setPaddingRight(-25f); table1.addCell(hcell);
					 */
					hcell = new PdfPCell(new Phrase("MRP", redFont));
					hcell.setBorder(Rectangle.NO_BORDER);
					hcell.setBackgroundColor(BaseColor.GRAY);
					hcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					hcell.setPaddingRight(-10f);
					table1.addCell(hcell);

					hcell = new PdfPCell(new Phrase("GST", redFont));
					hcell.setBorder(Rectangle.NO_BORDER);
					hcell.setBackgroundColor(BaseColor.GRAY);
					hcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					hcell.setPaddingRight(-10f);
					table1.addCell(hcell);

					hcell = new PdfPCell(new Phrase("Sale Value", redFont));
					hcell.setBorder(Rectangle.NO_BORDER);
					hcell.setBackgroundColor(BaseColor.GRAY);
					hcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					table1.addCell(hcell);
					int count = 0;

					PdfPTable table20 = new PdfPTable(9);
					table20.setWidths(new float[] { 1.5f, 5.4f, 4.5f, 3f, 2f, 1.2f, 2f, 1.5f, 2f });

					table20.setSpacingBefore(10);

					for (RefSales a : refSales) {

						MedicineDetails medicineDetails1 = medicineDetailsServiceImpl.findByName(a.getMedicineName());
						List<MedicineProcurement> medicineProcurement = medicineProcurementServiceImpl
								.findByBatchAndMedicine(a.getBatchNo(), medicineDetails1.getMedicineId());
						PdfPCell cell;

						cell = new PdfPCell(new Phrase(String.valueOf(count = count + 1), redFonts));
						cell.setBorder(Rectangle.NO_BORDER);
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table20.addCell(cell);

						cell = new PdfPCell(new Phrase(a.getMedicineName(), redFonts));
						cell.setBorder(Rectangle.NO_BORDER);
						cell.setPaddingLeft(-1);
						// cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						// cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						table20.addCell(cell);

						cell = new PdfPCell(new Phrase(medicineDetails1.getManufacturer(), redFonts));
						cell.setBorder(Rectangle.NO_BORDER);
						// cell.setPaddingLeft(-30);
						// cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						// cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setPaddingLeft(-15f);
						table20.addCell(cell);

						cell = new PdfPCell(new Phrase(String.valueOf(a.getBatchNo()), redFonts));
						cell.setBorder(Rectangle.NO_BORDER);

						// cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setPaddingLeft(12);
						// cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						table20.addCell(cell);

						// for convert db date to dmy format

						/*
						 * String expdate=medicineProcurement.get(medicineProcurement.size() -
						 * 1).getExpDate().toString().substring(0,10); SimpleDateFormat fromFormat = new
						 * SimpleDateFormat("yyyy-MM-dd"); SimpleDateFormat toFormat = new
						 * SimpleDateFormat("dd-MM-yyyy");
						 * expdate=toFormat.format(fromFormat.parse(expdate));
						 */

						try {
							expdate = a.getExpDate().toString().substring(0, 10);
							SimpleDateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd");
							SimpleDateFormat toFormat = new SimpleDateFormat("dd-MM-yyyy");
							expdate = toFormat.format(fromFormat.parse(expdate));

						} catch (Exception e) {
							e.printStackTrace();
						}

						cell = new PdfPCell(new Phrase(expdate, redFonts));
						cell.setBorder(Rectangle.NO_BORDER);
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
						cell.setPaddingLeft(-10);
						table20.addCell(cell);

						cell = new PdfPCell(new Phrase(String.valueOf((a.getQuantity())), redFonts));
						cell.setBorder(Rectangle.NO_BORDER);
						cell.setPaddingLeft(8);
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table20.addCell(cell);

						/*
						 * cell = new PdfPCell(new Phrase(String.valueOf((a.getDiscount())), redFonts));
						 * cell.setBorder(Rectangle.NO_BORDER); cell.setPaddingLeft(8);
						 * cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						 * cell.setHorizontalAlignment(Element.ALIGN_CENTER); cell.setPaddingLeft(10f);
						 * table20.addCell(cell);
						 */
						cell = new PdfPCell(new Phrase(String.valueOf((a.getMrp())), redFonts));
						cell.setBorder(Rectangle.NO_BORDER);
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
						cell.setPaddingRight(10);
						table20.addCell(cell);

						cell = new PdfPCell(new Phrase(String.valueOf((a.getGst())), redFonts));
						cell.setBorder(Rectangle.NO_BORDER);
						cell.setPaddingLeft(-15);
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
						table20.addCell(cell);

						cell = new PdfPCell(new Phrase(String.valueOf((a.getAmount())), redFonts));
						cell.setBorder(Rectangle.NO_BORDER);
						cell.setPaddingLeft(5);
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
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

					int ttl = (int) Math.round(total)-(int)sales.getDiscount();
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
					hcell21 = new PdfPCell(new Phrase("Total Sale Value", redFont));
					hcell21.setBorder(Rectangle.NO_BORDER);
					hcell21.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell21.setPaddingLeft(85f);
					table4.addCell(hcell21);

					hcell21 = new PdfPCell(new Phrase(":", redFont));
					hcell21.setBorder(Rectangle.NO_BORDER);
					hcell21.setHorizontalAlignment(Element.ALIGN_RIGHT);
					hcell21.setPaddingRight(-30f);
					table4.addCell(hcell21);

					hcell21 = new PdfPCell(new Phrase(String.valueOf(Math.round(total * 100.0) / 100.0), redFont));
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

					hcell04 = new PdfPCell(new Phrase(String.valueOf(Math.round(total)), redFont));
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

					hcell041 = new PdfPCell(new Phrase(String.valueOf(Math.round(sales.getDiscount())), redFont));
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

					hcell9 = new PdfPCell(new Phrase(String.valueOf(Math.round(total)-sales.getDiscount()), redFont));
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
					hcell34 = new PdfPCell(new Phrase(sales.getPaymentType(), redFont2));
					hcell34.setBorder(Rectangle.NO_BORDER);
					hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell34.setPaddingLeft(10f);
					table13.addCell(hcell34);

					hcell34 = new PdfPCell(new Phrase(String.valueOf(Math.round(total)), redFont2));
					hcell34.setBorder(Rectangle.NO_BORDER);
					hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell34.setPaddingLeft(35f);
					table13.addCell(hcell34);

					if (sales.getPaymentType().equalsIgnoreCase("card")
							|| sales.getPaymentType().equalsIgnoreCase("cash+card")) {
						hcell34 = new PdfPCell(new Phrase(refNo, redFont2));
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

					cell33.setFixedHeight(35f);
					cell33.setColspan(2);
					table13.setWidthPercentage(100f);
					cell33.addElement(table13);
					table.addCell(cell33);

					// for new row end

					PharmacyShopDetails pharmacyShopDetails = pharmacyShopDetailsRepository
							.findByShopLocation(sales.getLocation());

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

					PdfPCell hcell7;

					hcell7 = new PdfPCell(new Phrase("Instructions  : "
							+ "1) Returns are accepted within TEN(10)Days. \n                       2) Fridge Items once sold cannot be taken Back.",
							redFont));
					hcell7.setBorder(Rectangle.NO_BORDER);
					hcell7.setPaddingLeft(-50f);
					table5.addCell(hcell7);

					hcell7 = new PdfPCell(new Phrase("Pharmacist"));
					hcell7.setBorder(Rectangle.NO_BORDER);
					hcell7.setHorizontalAlignment(Element.ALIGN_RIGHT);
					hcell7.setPaddingTop(25f);
					table5.addCell(hcell7);

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
					salesPaymentPdf.setFileName(billNo + "-" + regId + " Medicine Sales");
					salesPaymentPdf.setFileuri(uri);
					salesPaymentPdf.setPid(salesPaymentPdfServiceImpl.getNextId());
					salesPaymentPdf.setData(pdfByte);
					salesPaymentPdfServiceImpl.save(salesPaymentPdf);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} else // for walk-ins
		{
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

				hcell1 = new PdfPCell(new Phrase(sales.getBillNo(), redFont));
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

				hcell18 = new PdfPCell(new Phrase(sales.getName(), redFont));
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

				hcell15 = new PdfPCell(new Phrase("Pharmacy Receipt", redFont3));
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

				PdfPTable table1 = new PdfPTable(9);
				table1.setWidths(new float[] { 1.5f, 5.4f, 4.5f, 3f, 2f, 1.2f,  2f, 1.5f, 2.8f });

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
				hcell.setPaddingLeft(-15f);

				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("Batch No", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell.setPaddingLeft(20f);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("Exp Date", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell.setPaddingRight(-5f);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("Qty", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell.setPaddingRight(-8);
				table1.addCell(hcell);

				/*
				 * hcell = new PdfPCell(new Phrase("Disc(%)", redFont));
				 * hcell.setBorder(Rectangle.NO_BORDER);
				 * hcell.setBackgroundColor(BaseColor.GRAY);
				 * hcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				 * hcell.setPaddingRight(-25f); table1.addCell(hcell);
				 */
				hcell = new PdfPCell(new Phrase("MRP", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell.setPaddingRight(-10f);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("GST", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell.setPaddingRight(-10f);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("Sale Value", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table1.addCell(hcell);
				int count = 0;

				PdfPTable table20 = new PdfPTable(9);
				table20.setWidths(new float[] { 1.5f, 5.4f, 4.5f, 3f, 2f, 1.2f,  2f, 1.5f, 2f });

				table20.setSpacingBefore(10);

				for (RefSales a : refSales) {

					MedicineDetails medicineDetails1 = medicineDetailsServiceImpl.findByName(a.getMedicineName());
					List<MedicineProcurement> medicineProcurement = medicineProcurementServiceImpl
							.findByBatchAndMedicine(a.getBatchNo(), medicineDetails1.getMedicineId());
					PdfPCell cell;

					cell = new PdfPCell(new Phrase(String.valueOf(count = count + 1), redFonts));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table20.addCell(cell);

					cell = new PdfPCell(new Phrase(a.getMedicineName(), redFonts));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(-1);
					// cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					// cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					table20.addCell(cell);

					cell = new PdfPCell(new Phrase(medicineDetails1.getManufacturer(), redFonts));
					cell.setBorder(Rectangle.NO_BORDER);
					// cell.setPaddingLeft(-30);
					// cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					// cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell.setPaddingLeft(-15f);
					table20.addCell(cell);

					cell = new PdfPCell(new Phrase(String.valueOf(a.getBatchNo()), redFonts));
					cell.setBorder(Rectangle.NO_BORDER);

					// cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell.setPaddingLeft(12);
					// cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					table20.addCell(cell);

					// for convert db date to dmy format

					/*
					 * String expdate=medicineProcurement.get(medicineProcurement.size() -
					 * 1).getExpDate().toString().substring(0,10); SimpleDateFormat fromFormat = new
					 * SimpleDateFormat("yyyy-MM-dd"); SimpleDateFormat toFormat = new
					 * SimpleDateFormat("dd-MM-yyyy");
					 * expdate=toFormat.format(fromFormat.parse(expdate));
					 */

					try {
						expdate = a.getExpDate().toString().substring(0, 10);
						SimpleDateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd");
						SimpleDateFormat toFormat = new SimpleDateFormat("dd-MM-yyyy");
						expdate = toFormat.format(fromFormat.parse(expdate));

					} catch (Exception e) {
						e.printStackTrace();
					}

					cell = new PdfPCell(new Phrase(expdate, redFonts));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cell.setPaddingLeft(-10);
					table20.addCell(cell);

					cell = new PdfPCell(new Phrase(String.valueOf((a.getQuantity())), redFonts));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(8);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table20.addCell(cell);

					/*
					 * cell = new PdfPCell(new Phrase(String.valueOf((a.getDiscount())), redFonts));
					 * cell.setBorder(Rectangle.NO_BORDER); cell.setPaddingLeft(8);
					 * cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					 * cell.setHorizontalAlignment(Element.ALIGN_CENTER); cell.setPaddingLeft(10f);
					 * table20.addCell(cell);
					 */
					cell = new PdfPCell(new Phrase(String.valueOf((a.getMrp())), redFonts));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cell.setPaddingRight(10);
					table20.addCell(cell);

					cell = new PdfPCell(new Phrase(String.valueOf((a.getGst())), redFonts));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(-15);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					table20.addCell(cell);

					cell = new PdfPCell(new Phrase(String.valueOf((a.getAmount())), redFonts));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
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

				int ttl = (int) Math.round(total)-(int)sales.getDiscount();
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
				hcell21 = new PdfPCell(new Phrase("Total Sale Value", redFont));
				hcell21.setBorder(Rectangle.NO_BORDER);
				hcell21.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell21.setPaddingLeft(85f);
				table4.addCell(hcell21);

				hcell21 = new PdfPCell(new Phrase(":", redFont));
				hcell21.setBorder(Rectangle.NO_BORDER);
				hcell21.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell21.setPaddingRight(-30f);
				table4.addCell(hcell21);

				hcell21 = new PdfPCell(new Phrase(String.valueOf(Math.round(total * 100.0) / 100.0), redFont));
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

				hcell04 = new PdfPCell(new Phrase(String.valueOf(Math.round(total)), redFont));
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

				hcell041 = new PdfPCell(new Phrase(String.valueOf(Math.round(sales.getDiscount())), redFont));
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

				hcell9 = new PdfPCell(new Phrase(String.valueOf(Math.round(total)-sales.getDiscount()), redFont));
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
				hcell34 = new PdfPCell(new Phrase(sales.getPaymentType(), redFont2));
				hcell34.setBorder(Rectangle.NO_BORDER);
				hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell34.setPaddingLeft(10f);
				table13.addCell(hcell34);

				hcell34 = new PdfPCell(new Phrase(String.valueOf(Math.round(total)), redFont2));
				hcell34.setBorder(Rectangle.NO_BORDER);
				hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell34.setPaddingLeft(35f);
				table13.addCell(hcell34);

				if (sales.getPaymentType().equalsIgnoreCase("card")
						|| sales.getPaymentType().equalsIgnoreCase("cash+card")) {
					hcell34 = new PdfPCell(new Phrase(refNo, redFont2));
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

				// for new row end

				PharmacyShopDetails pharmacyShopDetails = pharmacyShopDetailsRepository
						.findByShopLocation(sales.getLocation());

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

				PdfPCell hcell7;

				hcell7 = new PdfPCell(new Phrase("Instructions  : "
						+ "1) Returns are accepted within TEN(10)Days. \n                       2) Fridge Items once sold cannot be taken Back.",
						redFont));
				hcell7.setBorder(Rectangle.NO_BORDER);
				hcell7.setPaddingLeft(-50f);
				table5.addCell(hcell7);

				hcell7 = new PdfPCell(new Phrase("Pharmacist", redFont1));
				hcell7.setBorder(Rectangle.NO_BORDER);
				hcell7.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell7.setPaddingTop(25f);
				table5.addCell(hcell7);

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
				salesPaymentPdf.setFileName(billNo + " " + "Sales");
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
	
	

	@Transactional
	public SalesPaymentPdf wardSave(Sales sales, Principal principal) {
		User userSecurity = userServiceImpl.findByUserName(principal.getName());
		StringBuilder createdBy = new StringBuilder();
		createdBy = (userSecurity.getMiddleName() != null)
				? createdBy.append(userSecurity.getFirstName()).append(" ").append(userSecurity.getMiddleName())
						.append(" ").append(userSecurity.getLastName())
				: createdBy.append(userSecurity.getFirstName()).append(" ").append(userSecurity.getLastName());

		float total = 0;
		String wardName = "";
		String expdate = "";
		String billNo = "";
		/*
		 * String myAd = "     Plot No14,15,16 & 17,Nandi Co-op.Society," +
		 * "\n                                   Main Road, Beside Navya Grand Hotel, \n                                Miyapur,Hyderabad-49,Phone:040-23046789   \n                               "
		 * + "   For Appointment Contact:8019114481   " +
		 * "\n                                   Email :udbhavahospitals@gmail.com ";
		 */
		List<RefSales> refSales = sales.getRefSales();

		sales.setBillDate(Timestamp.valueOf(LocalDateTime.now()));
		sales.setBillNo(getNextBillNo());
		billNo = sales.getBillNo();
		sales.setUpdatedBy(userSecurity.getUserId());
		sales.setSoldBy(userSecurity.getUserId());
		sales.setPatientSalesUser(userSecurity);
		sales.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
		Location location = locationServiceImpl.findByLocationName(sales.getLocation());
		sales.setPatientSaleslocation(location);

		for (RefSales refSalesList : refSales) {
			sales.setSaleNo(getNextSaleNo());
			sales.setAmount(refSalesList.getAmount());
			sales.setBatchNo(refSalesList.getBatchNo());
			sales.setDiscount(refSalesList.getDiscount());
			sales.setGst(refSalesList.getGst());
			sales.setMedicineName(refSalesList.getMedicineName());
			MedicineDetails medicineDetails = medicineDetailsServiceImpl.findByName(refSalesList.getMedicineName());
			sales.setPatientSalesMedicineDetails(medicineDetails);
			sales.setMrp(refSalesList.getMrp());
			sales.setQuantity(refSalesList.getQuantity());
			sales.setActualAmount(refSalesList.getAmount());
			sales.setExpireDate(refSalesList.getExpDate());
			expdate = sales.getExpireDate();
			wardName = sales.getName();
			sales.setCostPrice(refSalesList.getMrp() * refSalesList.getQuantity());
			sales.setPaymentType("NA_WO");
			// for medicine quantity
			sales.setPaid("-");
			total += sales.getActualAmount();
			MedicineDetails medicineDetailsQuantity = medicineDetailsServiceImpl.findByName(sales.getMedicineName());

			MedicineQuantity medicineQuantityInfo = medicineQuantityServiceImpl
					.findByBatchIdAndMedicineDetails(refSalesList.getBatchNo(), medicineDetailsQuantity);

			if (medicineQuantityInfo != null) {
				long totalSold = medicineQuantityInfo.getSold();
				totalSold += sales.getQuantity();
				medicineQuantityInfo.setSold(totalSold);
				medicineQuantityInfo
						.setBalance(medicineQuantityInfo.getTotalQuantity() - medicineQuantityInfo.getSold());
				medicineQuantityRepository.save(medicineQuantityInfo);

			}

			salesRepository.save(sales);
		}

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

			Phrase pq = new Phrase(
					new Chunk(img, ConstantValues.IMAGE_SET_INTIAL_POSITION, ConstantValues.IMAGE_SET_FINAL_POSITION));
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

			hcell1 = new PdfPCell(new Phrase(sales.getBillNo(), redFont));
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
			hcell18 = new PdfPCell(new Phrase("Ward Name", redFont));
			hcell18.setBorder(Rectangle.NO_BORDER);
			hcell18.setPaddingLeft(-15f);
			table2.addCell(hcell18);

			hcell18 = new PdfPCell(new Phrase(":", redFont));
			hcell18.setBorder(Rectangle.NO_BORDER);
			hcell18.setPaddingLeft(-35f);
			table2.addCell(hcell18);

			hcell18 = new PdfPCell(new Phrase(wardName, redFont));
			hcell18.setBorder(Rectangle.NO_BORDER);
			hcell18.setPaddingLeft(-25f);
			table2.addCell(hcell18);

			/*
			 * PdfPCell hcel;
			 * 
			 * hcel = new PdfPCell(new Phrase("" , redFont));
			 * hcel.setBorder(Rectangle.NO_BORDER); hcel.setPaddingLeft(-15f);
			 * table2.addCell(hcel);
			 * 
			 * hcel = new PdfPCell(new Phrase("" , redFont));
			 * hcel.setBorder(Rectangle.NO_BORDER); hcel.setPaddingLeft(-35f);
			 * table2.addCell(hcel);
			 * 
			 * hcel = new PdfPCell(new Phrase("", redFont));
			 * hcel.setBorder(Rectangle.NO_BORDER); hcel.setPaddingLeft(-25f);
			 * table2.addCell(hcel);
			 * 
			 * 
			 * PdfPCell hcel11; hcel11 = new PdfPCell(new Phrase("" , redFont));
			 * hcel11.setBorder(Rectangle.NO_BORDER); hcel11.setPaddingLeft(-15f);
			 * hcel11.setHorizontalAlignment(Element.ALIGN_LEFT); table2.addCell(hcel11);
			 * 
			 * hcel11 = new PdfPCell(new Phrase("" , redFont));
			 * hcel11.setBorder(Rectangle.NO_BORDER); hcel11.setPaddingLeft(-35f);
			 * hcel11.setHorizontalAlignment(Element.ALIGN_LEFT); table2.addCell(hcel11);
			 * 
			 * hcel11 = new PdfPCell(new Phrase( "", redFont));
			 * hcel11.setBorder(Rectangle.NO_BORDER); hcel11.setPaddingLeft(-25f);
			 * hcel11.setHorizontalAlignment(Element.ALIGN_LEFT); table2.addCell(hcel11);
			 * 
			 * PdfPCell hcel1;
			 * 
			 * hcel1 = new PdfPCell(new Phrase("", redFont));
			 * hcel1.setBorder(Rectangle.NO_BORDER); hcel1.setPaddingLeft(-15f);
			 * table2.addCell(hcel1);
			 * 
			 * hcel1 = new PdfPCell(new Phrase("" , redFont));
			 * hcel1.setBorder(Rectangle.NO_BORDER); hcel1.setPaddingLeft(-35f);
			 * table2.addCell(hcel1);
			 * 
			 * hcel1 = new PdfPCell(new Phrase("" , redFont));
			 * hcel1.setBorder(Rectangle.NO_BORDER); hcel1.setPaddingLeft(-25f);
			 * table2.addCell(hcel1);
			 * 
			 */
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

			hcell15 = new PdfPCell(new Phrase("Ward Pharmacy Receipt", redFont3));
			hcell15.setBorder(Rectangle.NO_BORDER);
			hcell15.setPaddingLeft(05);
			hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
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
			table1.setWidths(new float[] { 1.5f, 5.4f, 4.5f, 3f, 2f, 1.2f, 1.5f, 2f, 1.5f, 2.8f });

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
			hcell.setPaddingLeft(-15f);

			table1.addCell(hcell);

			hcell = new PdfPCell(new Phrase("Batch No", redFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			hcell.setBackgroundColor(BaseColor.GRAY);
			hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell.setPaddingLeft(20f);
			table1.addCell(hcell);

			hcell = new PdfPCell(new Phrase("Exp Date", redFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			hcell.setBackgroundColor(BaseColor.GRAY);
			hcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			hcell.setPaddingRight(-5f);
			table1.addCell(hcell);

			hcell = new PdfPCell(new Phrase("Qty", redFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			hcell.setBackgroundColor(BaseColor.GRAY);
			hcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			hcell.setPaddingRight(-18);
			table1.addCell(hcell);

			hcell = new PdfPCell(new Phrase("Disc(%)", redFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			hcell.setBackgroundColor(BaseColor.GRAY);
			hcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			hcell.setPaddingRight(-25f);
			table1.addCell(hcell);

			hcell = new PdfPCell(new Phrase("MRP", redFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			hcell.setBackgroundColor(BaseColor.GRAY);
			hcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			hcell.setPaddingRight(-10f);
			table1.addCell(hcell);

			hcell = new PdfPCell(new Phrase("GST", redFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			hcell.setBackgroundColor(BaseColor.GRAY);
			hcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			hcell.setPaddingRight(-10f);
			table1.addCell(hcell);

			hcell = new PdfPCell(new Phrase("Sale Value", redFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			hcell.setBackgroundColor(BaseColor.GRAY);
			hcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table1.addCell(hcell);
			int count = 0;

			PdfPTable table20 = new PdfPTable(10);
			table20.setWidths(new float[] { 1.5f, 5.4f, 4.5f, 3f, 2f, 1.2f, 1.5f, 2f, 1.5f, 2f });

			table20.setSpacingBefore(10);

			for (RefSales a : refSales) {

				MedicineDetails medicineDetails1 = medicineDetailsServiceImpl.findByName(a.getMedicineName());
				List<MedicineProcurement> medicineProcurement = medicineProcurementServiceImpl
						.findByBatchAndMedicine(a.getBatchNo(), medicineDetails1.getMedicineId());
				PdfPCell cell;

				cell = new PdfPCell(new Phrase(String.valueOf(count = count + 1), redFonts));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table20.addCell(cell);

				cell = new PdfPCell(new Phrase(a.getMedicineName(), redFonts));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setPaddingLeft(-1);
				// cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				// cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				table20.addCell(cell);

				cell = new PdfPCell(new Phrase(medicineDetails1.getManufacturer(), redFonts));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell.setPaddingLeft(-15f);
				table20.addCell(cell);

				cell = new PdfPCell(new Phrase(String.valueOf(a.getBatchNo()), redFonts));
				cell.setBorder(Rectangle.NO_BORDER);

				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell.setPaddingLeft(12);
				table20.addCell(cell);

				try {
					expdate = a.getExpDate().toString().substring(0, 10);
					SimpleDateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd");
					SimpleDateFormat toFormat = new SimpleDateFormat("dd-MM-yyyy");
					expdate = toFormat.format(fromFormat.parse(expdate));

				} catch (Exception e) {
					e.printStackTrace();
				}

				cell = new PdfPCell(new Phrase(expdate, redFonts));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell.setPaddingLeft(-10);
				table20.addCell(cell);

				cell = new PdfPCell(new Phrase(String.valueOf((a.getQuantity())), redFonts));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setPaddingLeft(8);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table20.addCell(cell);

				cell = new PdfPCell(new Phrase(String.valueOf((a.getDiscount())), redFonts));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setPaddingLeft(8);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setPaddingLeft(10f);
				table20.addCell(cell);

				cell = new PdfPCell(new Phrase(String.valueOf((a.getMrp())), redFonts));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell.setPaddingRight(10);
				table20.addCell(cell);

				cell = new PdfPCell(new Phrase(String.valueOf((a.getGst())), redFonts));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setPaddingLeft(-15);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table20.addCell(cell);

				cell = new PdfPCell(new Phrase(String.valueOf((a.getAmount())), redFonts));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setPaddingLeft(5);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
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
			table4.setWidths(new float[] { 5f, 1f, 5f, 8f, 1f, 3f });
			table4.setSpacingBefore(10);

			PdfPCell hcell2;
			hcell2 = new PdfPCell(new Phrase("", redFont));
			hcell2.setBorder(Rectangle.NO_BORDER);
			hcell2.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell2.setPaddingLeft(-50f);
			table4.addCell(hcell2);

			hcell2 = new PdfPCell(new Phrase("", redFont));
			hcell2.setBorder(Rectangle.NO_BORDER);
			hcell2.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell2.setPaddingLeft(-40f);
			table4.addCell(hcell2);

			hcell2 = new PdfPCell(new Phrase("", redFont));
			hcell2.setBorder(Rectangle.NO_BORDER);
			hcell2.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell2.setPaddingLeft(-50f);
			table4.addCell(hcell2);

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

			hcell2 = new PdfPCell(new Phrase(String.valueOf(Math.round(total * 100.0) / 100.0), redFont));
			hcell2.setBorder(Rectangle.NO_BORDER);
			hcell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
			hcell2.setPaddingRight(-40f);
			table4.addCell(hcell2);

			PdfPCell hcell04;
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

			hcell04 = new PdfPCell(new Phrase(String.valueOf(Math.round(total)), redFont));
			hcell04.setBorder(Rectangle.NO_BORDER);
			hcell04.setHorizontalAlignment(Element.ALIGN_RIGHT);
			hcell04.setPaddingRight(-40f);
			table4.addCell(hcell04);

			PdfPCell hcell4;
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

			hcell4 = new PdfPCell(new Phrase("Net Amount", redFont));
			hcell4.setBorder(Rectangle.NO_BORDER);
			hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell4.setPaddingLeft(85f);
			table4.addCell(hcell4);

			hcell4 = new PdfPCell(new Phrase(":", redFont));
			hcell4.setBorder(Rectangle.NO_BORDER);
			hcell4.setHorizontalAlignment(Element.ALIGN_RIGHT);
			hcell4.setPaddingRight(-30f);
			table4.addCell(hcell4);

			hcell4 = new PdfPCell(new Phrase(String.valueOf(Math.round(total)), redFont));
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

			hcell9 = new PdfPCell(new Phrase("", redFont));
			hcell9.setBorder(Rectangle.NO_BORDER);
			hcell9.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell9.setPaddingLeft(85f);
			table4.addCell(hcell9);

			hcell9 = new PdfPCell(new Phrase("", redFont));
			hcell9.setBorder(Rectangle.NO_BORDER);
			hcell9.setHorizontalAlignment(Element.ALIGN_RIGHT);
			hcell9.setPaddingRight(-30f);
			table4.addCell(hcell9);

			hcell9 = new PdfPCell(new Phrase("", redFont));
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
			hcell34 = new PdfPCell(new Phrase(sales.getPaymentType(), redFont2));
			hcell34.setBorder(Rectangle.NO_BORDER);
			hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell34.setPaddingLeft(10f);
			table13.addCell(hcell34);

			hcell34 = new PdfPCell(new Phrase(String.valueOf(Math.round(total)), redFont2));
			hcell34.setBorder(Rectangle.NO_BORDER);
			hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell34.setPaddingLeft(35f);
			table13.addCell(hcell34);

			hcell34 = new PdfPCell(new Phrase("", redFont1));
			hcell34.setBorder(Rectangle.NO_BORDER);
			hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell34.setPaddingLeft(40f);
			table13.addCell(hcell34);

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

			cell33.setFixedHeight(35f);
			cell33.setColspan(2);
			table13.setWidthPercentage(100f);
			cell33.addElement(table13);
			table.addCell(cell33);

			// for new row end

			PharmacyShopDetails pharmacyShopDetails = pharmacyShopDetailsRepository
					.findByShopLocation(sales.getLocation());

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

			PdfPCell hcell7;

			hcell7 = new PdfPCell(new Phrase("Instructions  : "
					+ "1) Returns are accepted within TEN(10)Days. \n                       2) Fridge Items once sold cannot be taken Back.",
					redFont));
			hcell7.setBorder(Rectangle.NO_BORDER);
			hcell7.setPaddingLeft(-50f);
			table5.addCell(hcell7);

			hcell7 = new PdfPCell(new Phrase("Pharmacist"));
			hcell7.setBorder(Rectangle.NO_BORDER);
			hcell7.setHorizontalAlignment(Element.ALIGN_RIGHT);
			hcell7.setPaddingTop(25f);
			table5.addCell(hcell7);

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
			salesPaymentPdf.setFileName(billNo + " Medicine Sales Ward Online Issue");
			salesPaymentPdf.setFileuri(uri);
			salesPaymentPdf.setPid(salesPaymentPdfServiceImpl.getNextId());
			salesPaymentPdf.setData(pdfByte);
			salesPaymentPdfServiceImpl.save(salesPaymentPdf);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return salesPaymentPdf;

	}

	public List<Sales> findByPatientRegistration(PatientRegistration patientRegistration) {
		return salesRepository.findByPatientRegistration(patientRegistration);
	}

	public List<Sales> findByBillNo(String id) {
		return salesRepository.findByBillNo(id);
	}

	/*
	 * Modified code for sales
	 */
	public List<Map<String, String>> findMedicineDetailsModified(List<Map<String, String>> medicine) {
		List<Map<String, String>> info = new ArrayList<>();

		List<Map<String, String>> med = medicine;
		for (int i = 0; i < med.size(); i++) {
			Map<String, String> lis = new HashMap<>();
			System.out.println(med.get(i).get("med"));
			System.out.println(med.get(i).get("quantity"));
			MedicineDetails medicineDetails = medicineDetailsRepository.findByName(med.get(i).get("med"));
			List<MedicineProcurement> medicineProcurement = medicineDetails.getMedicineProcurement();

			float cost = medicineProcurement.get(medicineProcurement.size() - 1).getMrp()
					* Float.parseFloat(med.get(i).get("quantity"));
			lis.put("total", String.valueOf(cost));
			lis.put("med", medicineDetails.getName());
			lis.put("batch", medicineProcurement.get(medicineProcurement.size() - 1).getBatch());
			lis.put("mrp", String.valueOf(medicineProcurement.get(medicineProcurement.size() - 1).getMrp()));
			info.add(lis);

		}

		return info;
	}

	public RefSalesIds findMedicineDetails(String medicine) {
		MedicineDetails medicineDetails = medicineDetailsRepository.findByName(medicine);
		refSalesIds.setBatch(medicineDetails.getBatchNo());
		List<MedicineProcurement> medicineProcurement = medicineDetails.getMedicineProcurement();
		for (MedicineProcurement medicineProcurementL : medicineProcurement) {
			System.out.println(medicineProcurementL);
		}
		refSalesIds.setExpDate(medicineProcurement.get(medicineProcurement.size() - 1).getExpDate());
		refSalesIds.setMrp(medicineProcurement.get(medicineProcurement.size() - 1).getMrp());
		return refSalesIds;
	}

	public List<Object> getBillIds() {
		List<Object> displayList = new ArrayList<>();
		Iterable<MedicineDetails> medicineDetails = medicineDetailsServiceImpl.findAll();
		for (MedicineDetails medicineDetailsInfo : medicineDetails) {
			List<MedicineProcurement> medicineProcurements = medicineProcurementServiceImpl
					.findOneApproved(medicineDetailsInfo);
           
			if (!medicineProcurements.isEmpty()) {
				RefSalesIds refSalesIds = new RefSalesIds();
				refSalesIds.setBillNo(salesServiceImpl.getNextBillNo());
				Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
				refSalesIds.setDate(timestamp.toString().substring(0, 10));
				refSalesIds.setMedName(medicineDetailsInfo.getName());
				refSalesIds.setBatch(medicineDetailsInfo.getBatchNo());
				 int procSize = medicineProcurements.size();
				refSalesIds.setExpDate(medicineProcurements.get(procSize - 1).getExpDate());
				refSalesIds.setMrp(medicineProcurements.get(procSize- 1).getMrp());
				refSalesIds.setGst(medicineProcurements.get(procSize- 1).getTax());
				displayList.add(refSalesIds);
			}
		}
		return displayList;
	}

	public List<Object> getBillId() {
		List<Object> displayList = new ArrayList<>();
		RefSalesIds refSalesIds = new RefSalesIds();
		refSalesIds.setBillNo(salesServiceImpl.getNextBillNo());
		Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
		refSalesIds.setDate(timestamp.toString().substring(0, 10));
		displayList.add(refSalesIds);

		return displayList;
	}

	public List<Sales> findByBatchAndMedicine(String batch, String medicine) {
		return salesRepository.findByBatchAndMedicine(batch, medicine);
	}

	public List<Sales> findByName(String medName) {
		return salesRepository.findByName(medName);
	}

	@Override
	public List<Sales> findByPaymentTypeAndPatientRegistration(String payment, PatientRegistration reg) {
		return salesRepository.findByPaymentTypeAndPatientRegistration(payment, reg);
	}

	// find due bill
	public List<Sales> findByPatientRegistrationAndPaymentType(PatientRegistration patientRegistration,
			String paymentType) {
		return salesRepository.findByPatientRegistrationAndPaymentType(patientRegistration, paymentType);
	}

	public List<Object> getPreviousDaySales() {

		List<Object> list = new ArrayList<Object>();

		List<Sales> salesList = salesRepository.getPreviousDaySales();
		for (Sales salesListInfo : salesList) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("SaleId", salesListInfo.getSaleNo());
			map.put("billNo", salesListInfo.getBillNo());
			map.put("regId", salesListInfo.getPatientRegistration().getRegId());
			map.put("umr", salesListInfo.getUmr());
			map.put("medName", salesListInfo.getMedicineName());
			map.put("count", String.valueOf(salesListInfo.getQuantity()));
			map.put("sellingPrice", String.valueOf(salesListInfo.getMrp()));
			map.put("paymentType", salesListInfo.getPaymentType());
			map.put("discount", String.valueOf(salesListInfo.getDiscount()));
			map.put("amount", String.valueOf(salesListInfo.getAmount()));
			map.put("totalAmount", String.valueOf(salesListInfo.getCostPrice()));
			map.put("patientType", salesListInfo.getPatientRegistration().getpType());
			map.put("date", String.valueOf(salesListInfo.getBillDate()));
			map.put("name", salesListInfo.getName());
			list.add(map);

		}

		return list;
	}

	public void updateMedDetails(Sales sales, Principal principal, String billNo) {

		User userSecurity = userServiceImpl.findByUserName(principal.getName());
		String createdBy = (userSecurity.getMiddleName() != null)
				? userSecurity.getFirstName() + " " + userSecurity.getMiddleName() + " " + userSecurity.getLastName()
				: userSecurity.getFirstName() + " " + userSecurity.getLastName();

		Sales salesPrev = null;
		MedicineDetails newMedicineDetails = null;
		MedicineDetails prevMedicineDetails = null;
		PatientRegistration patientRegistration = null;
		float newAmount = 0;
		String newSaleId = "";
		String newMedName = "";
		long newQuantity = 0;
		String newBatchNo = "";
		float newDiscount = 0;
		String newPaymentType = "";
		String newExpireDate = "";
		float newGst = 0;
		float newMrp = 0;
		String patientName = "";
		String umr = "";
		String regId = "";
		String location = null;

		List<Map<String, String>> addMedicine = sales.getAddMedDetails();
		for (Map<String, String> addMedicineInfo : addMedicine) {
			newAmount = Float.valueOf(addMedicineInfo.get("amount"));
			newSaleId = addMedicineInfo.get("saleId");
			newMedName = addMedicineInfo.get("medicineName");
			newQuantity = Long.valueOf(addMedicineInfo.get("quantity"));
			newBatchNo = addMedicineInfo.get("batchNo");
			newDiscount = Float.valueOf(addMedicineInfo.get("discount"));
			newPaymentType = addMedicineInfo.get("paymentMode");
			newExpireDate = addMedicineInfo.get("expiryDate");
			newGst = Float.valueOf(addMedicineInfo.get("gst"));
			newMrp = Float.valueOf(addMedicineInfo.get("mrp"));
			location = addMedicineInfo.get("location");

			float finalCash = 0; // final billing
			float finalCard = 0; // final billing
			float finalCheque = 0; // final billing
			float finalDue = 0; // final billing
			long netAmount = 0;
			long finalNetAmount = 0;

			salesPrev = salesRepository.findBySaleNo(newSaleId);
			prevMedicineDetails = salesPrev.getPatientSalesMedicineDetails();
			newMedicineDetails = medicineDetailsRepository.findByName(newMedName);
			salesPrev.setPatientSalesMedicineDetails(newMedicineDetails);
			salesPrev.setAmount(newAmount);
			salesPrev.setMedicineName(newMedName);
			salesPrev.setQuantity(newQuantity);
			salesPrev.setBatchNo(newBatchNo);
			salesPrev.setDiscount(newDiscount);
			salesPrev.setPaymentType(newPaymentType);
			salesPrev.setExpireDate(newExpireDate);
			salesPrev.setGst(newGst);
			salesPrev.setMrp(newMrp);
			salesPrev.setLocation(location);

			// Updating medicine quantity for previous medicine
			MedicineQuantity prevMedicineQuantityInfo = medicineQuantityServiceImpl
					.findByMedicineDetails(prevMedicineDetails);
			prevMedicineQuantityInfo.setBalance(prevMedicineQuantityInfo.getBalance() + salesPrev.getQuantity());
			prevMedicineQuantityInfo.setSold(prevMedicineQuantityInfo.getSold() - salesPrev.getQuantity());
			medicineQuantityRepository.save(prevMedicineQuantityInfo);

			// Updating medicine quantity for previous medicine
			MedicineQuantity newMedicineQuantityInfo = medicineQuantityServiceImpl
					.findByMedicineDetails(newMedicineDetails);
			newMedicineQuantityInfo.setBalance(newMedicineQuantityInfo.getBalance() + newQuantity);
			newMedicineQuantityInfo.setSold(newMedicineQuantityInfo.getSold() - newQuantity);
			medicineQuantityRepository.save(newMedicineQuantityInfo);

			if (salesPrev.getPatientRegistration() != null) {
				patientRegistration = salesPrev.getPatientRegistration();
				regId = patientRegistration.getRegId();
				umr = patientRegistration.getPatientDetails().getUmr();

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

				System.out.println(billNo);
				System.out.println(prevMedicineDetails);
				/*
				 * //PatientSales patientSales = patientSalesRepository
				 * .findBySalesBillNoAndPatientSalesMedicineDetails(billNo,
				 * prevMedicineDetails); System.out.
				 * println("patient sales--------------------------------------------" +
				 * patientSales); System.out.
				 * println("patient sales--------------------------------------------" +
				 * billNo); System.out.
				 * println("patient sales--------------------------------------------" +
				 * prevMedicineDetails); patientSales.setAmount(newAmount);
				 * patientSales.setBatchNo(newBatchNo); patientSales.setSalesBillNo(billNo);
				 * patientSales.setDiscount(newDiscount);
				 * patientSales.setExpireDate(newExpireDate); patientSales.setGst(newGst);
				 * patientSales.setMedicineName(newMedName);
				 * patientSales.setPatientSalesMedicineDetails(newMedicineDetails);
				 * patientSales.setQuantity(newQuantity);
				 * patientSales.setPaymentType(newPaymentType); patientSales.setMrp(newMrp);
				 */
				if (salesPrev.getPatientRegistration().getpType().equals(ConstantValues.INPATIENT)
						&& newPaymentType.equalsIgnoreCase("Advance")
						|| newPaymentType.equalsIgnoreCase(ConstantValues.DUE)) {
					// patientSales.setPaid(ConstantValues.NO);
					salesPrev.setPaid(ConstantValues.NO);

					ChargeBill chargeBillPrev = chargeBillServiceImpl.findBySaleId(salesPrev);
					chargeBillPrev.setMrp(newMrp);
					chargeBillPrev.setAmount(newAmount);
					chargeBillPrev.setDiscount(newDiscount);
					chargeBillPrev.setPaid(ConstantValues.NO);
					chargeBillPrev.setQuantity(newQuantity);
					chargeBillPrev.setNetAmount(newAmount);
					chargeBillPrev.setAmount(newQuantity * newMrp);
					chargeBillPrev.setPaymentType(newPaymentType);
					chargeBillPrev.setInsertedBy(userSecurity.getUserId());
					chargeBillRepository.save(chargeBillPrev);
				} else if (salesPrev.getPatientRegistration().getpType().equals(ConstantValues.INPATIENT)
						&& !newPaymentType.equalsIgnoreCase("Advance")
						|| !newPaymentType.equalsIgnoreCase(ConstantValues.DUE)) {

					// patientSales.setPaid(ConstantValues.YES);
					salesPrev.setPaid(ConstantValues.YES);

					ChargeBill chargeBillPrev = chargeBillServiceImpl.findBySaleId(salesPrev);
					chargeBillPrev.setMrp(newMrp);
					chargeBillPrev.setAmount(newAmount);
					chargeBillPrev.setDiscount(newDiscount);
					chargeBillPrev.setPaid(ConstantValues.YES);
					chargeBillPrev.setQuantity(newQuantity);
					chargeBillPrev.setNetAmount(newAmount);
					chargeBillPrev.setAmount(newQuantity * newMrp);
					chargeBillPrev.setPaymentType(newPaymentType);
					chargeBillPrev.setInsertedBy(userSecurity.getUserId());
					chargeBillRepository.save(chargeBillPrev);

				} else {
					salesPrev.setPaid(ConstantValues.YES);
					// patientSales.setPaid(ConstantValues.YES);
				}

				// patientSalesRepository.save(patientSales);
			}
			salesRepository.save(salesPrev);

			// Cash + Card

			if (newPaymentType.equalsIgnoreCase(ConstantValues.CASH)) {
				finalCash = finalNetAmount;
			} else if (newPaymentType.equalsIgnoreCase(ConstantValues.CARD)) {
				finalCard = finalNetAmount;
			} else if (newPaymentType.equalsIgnoreCase(ConstantValues.CHEQUE)) {
				finalCheque = finalNetAmount;
			} else if (newPaymentType.equalsIgnoreCase(ConstantValues.DUE)) {
				finalDue = finalNetAmount;
			}

			if (newPaymentType.equalsIgnoreCase(ConstantValues.CASH_PLUS_CARD)) {
				float cashAmount = 0;
				float cardAmount = 0;
				float chequeAmount = 0;
				CashPlusCard cashPlusCardLab = new CashPlusCard();
				cashPlusCardLab.setInsertedBy(userSecurity.getUserId());
				List<Map<String, String>> multiMode = sales.getMultimode();
				for (Map<String, String> multiModeInfo : multiMode) {
					if (multiModeInfo.get("mode").equalsIgnoreCase(ConstantValues.CASH)) {
						cashAmount = Float.parseFloat(multiModeInfo.get("amount"));
						finalCash = Float.parseFloat(multiModeInfo.get("amount"));
					} else if (multiModeInfo.get("mode").equalsIgnoreCase(ConstantValues.CARD)) {
						cardAmount = Float.parseFloat(multiModeInfo.get("amount"));
						finalCard = Float.parseFloat(multiModeInfo.get("amount"));
					} else if (multiModeInfo.get("mode").equalsIgnoreCase(ConstantValues.CHEQUE)) {
						chequeAmount = Float.parseFloat(multiModeInfo.get("amount"));
						finalCheque = Float.parseFloat(multiModeInfo.get("amount"));
					}

				}
				cashPlusCardLab.setInsertedDate(Timestamp.valueOf(LocalDateTime.now()));
				cashPlusCardLab.setDescription("Sales");
				cashPlusCardLab
						.setPatientRegistrationCashCard((patientRegistration != null) ? patientRegistration : null);
				cashPlusCardLab.setCardAmount(cardAmount);
				cashPlusCardLab.setCashAmount(cashAmount);
				cashPlusCardLab.setBillNo(sales.getBillNo());
				cashPlusCardLab.setChequeAmount(chequeAmount);
				cashPlusCardServiceImpl.save(cashPlusCardLab);

			}

			if (patientRegistration.getpType().equalsIgnoreCase(ConstantValues.INPATIENT)) {
				// Final Billing
				FinalBilling finalBilling = new FinalBilling();
				finalBilling.setBillNo(billNo);
				finalBilling.setBillType("Sales");
				finalBilling.setCardAmount(finalCard);
				finalBilling.setCashAmount(finalCash);
				finalBilling.setChequeAmount(finalCheque);
				finalBilling.setInsertedDate(Timestamp.valueOf(LocalDateTime.now()));
				finalBilling.setDueAmount(finalDue);
				finalBilling.setFinalAmountPaid(finalNetAmount);
				finalBilling.setFinalBillUser(userSecurity);
				finalBilling.setUpdatedBy(userSecurity.getUserId());
				finalBilling.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
				finalBilling.setName(patientName);
				finalBilling.setRegNo(regId);
				finalBilling.setPaymentType(newPaymentType);
				finalBilling.setTotalAmount(finalNetAmount);
				finalBilling.setUmrNo(umr);
				finalBillingServcieImpl.computeSave(finalBilling);
			}

		}

		PharmacyShopDetails pharmacyShopDetails = pharmacyShopDetailsRepository.findByShopLocation(location);

//------------------------------Pdf code---------------------------------

		// To find total amount of bill

		float total = 0.0f;
		List<Sales> listSales = findByBillNo(billNo);
		for (Sales listSalesInfo : listSales) {
			total += listSalesInfo.getAmount();
		}

		String roundOff = null;

		/*
		 * String myAd = "Plot No14,15,16 & 17,Nandi Co-op.Society," +
		 * "\n                             Main Road, Beside Navya Grand Hotel, \n                                       Miyapur,Hyderabad-49   \n                               "
		 * + "        Phone:040-23046789    " +
		 * "\n                           Email :udbhavahospitals@gmail.com";
		 */

		if (patientRegistration != null) {

			if (!patientRegistration.getpType().equals(ConstantValues.INPATIENT)) {

				byte[] pdfByte = null;
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

				Document document = new Document(PageSize.A4_LANDSCAPE);
				try {

					Resource fileResourcee = resourceLoader.getResource(ConstantValues.IMAGE_PNG_CLASSPATH);
					Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
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

					hcel = new PdfPCell(new Phrase(umr, redFont));
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

					hcel11 = new PdfPCell(new Phrase(regId, redFont));
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

					hcell15 = new PdfPCell(new Phrase("Pharmacy Receipt", redFont3));
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

					hcell = new PdfPCell(new Phrase("Disc(%)", redFont));
					hcell.setBorder(Rectangle.NO_BORDER);
					hcell.setBackgroundColor(BaseColor.GRAY);
					hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table1.addCell(hcell);

					hcell = new PdfPCell(new Phrase("MRP", redFont));
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

					for (Map<String, String> a : addMedicine) {

						MedicineDetails medicineDetails1 = medicineDetailsServiceImpl.findByName(a.get("medicineName"));
						List<MedicineProcurement> medicineProcurement = medicineProcurementServiceImpl
								.findByBatchAndMedicine(a.get("batchNo"), medicineDetails1.getMedicineId());
						PdfPCell cell;

						cell = new PdfPCell(new Phrase(String.valueOf(count = count + 1), redFont));
						cell.setBorder(Rectangle.NO_BORDER);
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table1.addCell(cell);

						cell = new PdfPCell(new Phrase(a.get("medicineName"), redFont));
						cell.setBorder(Rectangle.NO_BORDER);
						cell.setPaddingLeft(5);
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

						cell = new PdfPCell(new Phrase(String.valueOf(a.get("batchNo")), redFont));
						cell.setBorder(Rectangle.NO_BORDER);
						cell.setPaddingLeft(5);
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						// cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						table1.addCell(cell);

						// for convert db date to dmy format

						/*
						 * String expdate=medicineProcurement.get(medicineProcurement.size() -
						 * 1).getExpDate().toString().substring(0,10); SimpleDateFormat fromFormat = new
						 * SimpleDateFormat("yyyy-MM-dd"); SimpleDateFormat toFormat = new
						 * SimpleDateFormat("dd-MM-yyyy");
						 * expdate=toFormat.format(fromFormat.parse(expdate));
						 */

						try {
							expdate = a.get("expiryDate").toString().substring(0, 10);
							SimpleDateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd");
							SimpleDateFormat toFormat = new SimpleDateFormat("dd-MM-yyyy");
							expdate = toFormat.format(fromFormat.parse(expdate));

						} catch (Exception e) {
							Logger.error(e.getMessage());
						}

						cell = new PdfPCell(new Phrase(expdate, redFont));
						cell.setBorder(Rectangle.NO_BORDER);
						cell.setPaddingLeft(5);
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table1.addCell(cell);

						cell = new PdfPCell(new Phrase(String.valueOf(a.get("quantity")), redFont));
						cell.setBorder(Rectangle.NO_BORDER);
						cell.setPaddingLeft(5);
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table1.addCell(cell);
						cell = new PdfPCell(new Phrase(String.valueOf(a.get("discount")), redFont));
						cell.setBorder(Rectangle.NO_BORDER);
						cell.setPaddingLeft(5);
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table1.addCell(cell);

						cell = new PdfPCell(new Phrase(String.valueOf(a.get("mrp")), redFont));
						cell.setBorder(Rectangle.NO_BORDER);
						cell.setPaddingLeft(5);
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table1.addCell(cell);

						cell = new PdfPCell(new Phrase(String.valueOf(a.get("gst")), redFont));
						cell.setBorder(Rectangle.NO_BORDER);
						cell.setPaddingLeft(5);
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table1.addCell(cell);

						cell = new PdfPCell(new Phrase(String.valueOf(a.get("amount")), redFont));
						cell.setBorder(Rectangle.NO_BORDER);
						cell.setPaddingLeft(5);
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table1.addCell(cell);

					}
					cell3.setColspan(2);
					table1.setWidthPercentage(100f);
					cell3.addElement(table1);
					table.addCell(cell3);

					PdfPCell cell4 = new PdfPCell();

					PdfPTable table4 = new PdfPTable(6);
					table4.setWidths(new float[] { 5f, 1f, 5f, 8f, 1f, 3f });
					table4.setSpacingBefore(10);

					int ttl = (int) Math.round(total);
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

					hcell2 = new PdfPCell(new Phrase(String.valueOf(Math.round(total * 100.0) / 100.0), redFont));
					hcell2.setBorder(Rectangle.NO_BORDER);
					hcell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
					hcell2.setPaddingRight(-40f);
					table4.addCell(hcell2);

					PdfPCell hcell04;
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

					/*
					 * BigDecimal bg=new BigDecimal(total-Math.floor(total));
					 * bg=bg.setScale(2,RoundingMode.HALF_DOWN); float round=bg.floatValue();
					 * //float rd=Math.nextUp(1.0f-round); float rd=1.00f-round;
					 * 
					 * if(round<0.50) { hcell04 = new PdfPCell(new Phrase("-" +round, redFont)); }
					 * else { if(String.valueOf(rd).length()>=4) { hcell04 = new PdfPCell(new
					 * Phrase("+" +String.valueOf(rd).substring(0, 4) , redFont)); } else { hcell04
					 * = new PdfPCell(new Phrase("+" +String.valueOf(rd+"/"+round) , redFont));
					 * 
					 * 
					 * } }
					 */

					hcell04 = new PdfPCell(new Phrase(String.valueOf(Math.round(total)), redFont));
					hcell04.setBorder(Rectangle.NO_BORDER);
					hcell04.setHorizontalAlignment(Element.ALIGN_RIGHT);
					hcell04.setPaddingRight(-40f);
					table4.addCell(hcell04);

					PdfPCell hcell4;
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

					hcell4 = new PdfPCell(new Phrase("Net Amount", redFont));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell4.setPaddingLeft(85f);
					table4.addCell(hcell4);

					hcell4 = new PdfPCell(new Phrase(":", redFont));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setHorizontalAlignment(Element.ALIGN_RIGHT);
					hcell4.setPaddingRight(-30f);
					table4.addCell(hcell4);

					hcell4 = new PdfPCell(new Phrase(String.valueOf(Math.round(total)), redFont));
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

					hcell9 = new PdfPCell(new Phrase(String.valueOf(Math.round(total)), redFont));
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
					hcell34 = new PdfPCell(new Phrase(newPaymentType, redFont2));
					hcell34.setBorder(Rectangle.NO_BORDER);
					hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell34.setPaddingLeft(10f);
					table13.addCell(hcell34);

					hcell34 = new PdfPCell(new Phrase(String.valueOf(total), redFont2));
					hcell34.setBorder(Rectangle.NO_BORDER);
					hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell34.setPaddingLeft(35f);
					table13.addCell(hcell34);

					hcell34 = new PdfPCell(new Phrase("", redFont1));
					hcell34.setBorder(Rectangle.NO_BORDER);
					hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell34.setPaddingLeft(40f);
					table13.addCell(hcell34);

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

					cell33.setFixedHeight(35f);
					cell33.setColspan(2);
					table13.setWidthPercentage(100f);
					cell33.addElement(table13);
					table.addCell(cell33);

					// for new row end

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

					PdfPCell hcell7;

					hcell7 = new PdfPCell(new Phrase("Instructions  : "
							+ "1) Returns are accepted within TEN(10)Days. \n                       2) Fridge Items once sold cannot be taken Back.",
							redFont));
					hcell7.setBorder(Rectangle.NO_BORDER);
					hcell7.setPaddingLeft(-50f);
					table5.addCell(hcell7);

					hcell7 = new PdfPCell(new Phrase("Pharmacist"));
					hcell7.setBorder(Rectangle.NO_BORDER);
					hcell7.setHorizontalAlignment(Element.ALIGN_RIGHT);
					hcell7.setPaddingTop(25f);
					table5.addCell(hcell7);

					cell6.setFixedHeight(80f);
					cell6.setColspan(2);
					cell6.addElement(table5);
					table.addCell(cell6);

					document.add(table);

					document.close();
					System.out.println("finished");
					pdfByte = byteArrayOutputStream.toByteArray();

					SalesPaymentPdf salesPaymentPdfs = salesPaymentPdfServiceImpl.findByFileName(billNo);

					if (salesPaymentPdfs != null) {
						salesPaymentPdf = new SalesPaymentPdf();
						salesPaymentPdf.setFileName(billNo + "-" + regId + " Medicine Sales");
						salesPaymentPdf.setFileuri(salesPaymentPdfs.getFileuri());
						salesPaymentPdf.setPid(salesPaymentPdfs.getPid());
						salesPaymentPdf.setData(pdfByte);
						salesPaymentPdfServiceImpl.save(salesPaymentPdf);
					} else {

						String uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/sales/viewFile/")
								.path(salesPaymentPdfServiceImpl.getNextId()).toUriString();

						salesPaymentPdf = new SalesPaymentPdf();
						salesPaymentPdf.setFileName(billNo + "-" + regId + " Medicine Sales");
						salesPaymentPdf.setFileuri(uri);
						salesPaymentPdf.setPid(salesPaymentPdfServiceImpl.getNextId());
						salesPaymentPdf.setData(pdfByte);
						salesPaymentPdfServiceImpl.save(salesPaymentPdf);
					}

				} catch (Exception e) {
					Logger.error(e.getMessage());
					// e.printStackTrace();
				}
			}

			else if (patientRegistration.getpType().equals(ConstantValues.INPATIENT)
					&& newPaymentType.equalsIgnoreCase("Advance")) {
				byte[] pdfByte = null;
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

				Document document = new Document(PageSize.A4_LANDSCAPE);
				try {

					Resource fileResourcee = resourceLoader.getResource(ConstantValues.IMAGE_PNG_CLASSPATH);
					Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
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
					hcell96 = new PdfPCell(new Phrase("UDBHAVA PHARMACY", redFont1));
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

					hcel = new PdfPCell(new Phrase(umr, redFont));
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

					hcel11 = new PdfPCell(new Phrase(regId, redFont));
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

					cell0.setFixedHeight(100f);
					cell0.setColspan(2);
					cell0.addElement(table2);
					table.addCell(cell0);

					PdfPCell cell19 = new PdfPCell();

					PdfPTable table21 = new PdfPTable(3);
					table21.setWidths(new float[] { 4f, 8f, 5f });
					table21.setSpacingBefore(10);

					PdfPCell hcell15;
					hcell15 = new PdfPCell(new Phrase("", redFont));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setPaddingLeft(-70f);
					table21.addCell(hcell15);

					hcell15 = new PdfPCell(new Phrase("Pharmacy Advance Receipt", redFont3));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setPaddingLeft(10);
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

					hcell = new PdfPCell(new Phrase("Disc(%)", redFont));
					hcell.setBorder(Rectangle.NO_BORDER);
					hcell.setBackgroundColor(BaseColor.GRAY);
					hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table1.addCell(hcell);

					hcell = new PdfPCell(new Phrase("MRP", redFont));
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

					for (Map<String, String> a : addMedicine) {

						MedicineDetails medicineDetails1 = medicineDetailsServiceImpl.findByName(a.get("medicineName"));
						List<MedicineProcurement> medicineProcurement = medicineProcurementServiceImpl
								.findByBatchAndMedicine(a.get("batchNo"), medicineDetails1.getMedicineId());
						PdfPCell cell;

						cell = new PdfPCell(new Phrase(String.valueOf(count = count + 1), redFont));
						cell.setBorder(Rectangle.NO_BORDER);
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table1.addCell(cell);

						cell = new PdfPCell(new Phrase(a.get("medicineName"), redFont));
						cell.setBorder(Rectangle.NO_BORDER);
						cell.setPaddingLeft(5);
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

						cell = new PdfPCell(new Phrase(String.valueOf(a.get("batchNo")), redFont));
						cell.setBorder(Rectangle.NO_BORDER);
						cell.setPaddingLeft(5);
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						// cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						table1.addCell(cell);

						// for convert db date to dmy format

						/*
						 * String expdate=medicineProcurement.get(medicineProcurement.size() -
						 * 1).getExpDate().toString().substring(0,10); SimpleDateFormat fromFormat = new
						 * SimpleDateFormat("yyyy-MM-dd"); SimpleDateFormat toFormat = new
						 * SimpleDateFormat("dd-MM-yyyy");
						 * expdate=toFormat.format(fromFormat.parse(expdate));
						 */
						try {
							expdate = a.get("expiryDate").toString().substring(0, 10);
							SimpleDateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd");
							SimpleDateFormat toFormat = new SimpleDateFormat("dd-MM-yyyy");
							expdate = toFormat.format(fromFormat.parse(expdate));

						} catch (Exception e) {
							Logger.error(e.getMessage());
						}

						cell = new PdfPCell(new Phrase(expdate, redFont));
						cell.setBorder(Rectangle.NO_BORDER);
						cell.setPaddingLeft(5);
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table1.addCell(cell);

						cell = new PdfPCell(new Phrase(String.valueOf(a.get("quantity")), redFont));
						cell.setBorder(Rectangle.NO_BORDER);
						cell.setPaddingLeft(5);
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table1.addCell(cell);
						cell = new PdfPCell(new Phrase(String.valueOf((a.get("discount"))), redFont));
						cell.setBorder(Rectangle.NO_BORDER);
						cell.setPaddingLeft(5);
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table1.addCell(cell);

						cell = new PdfPCell(new Phrase(String.valueOf((a.get("mrp"))), redFont));
						cell.setBorder(Rectangle.NO_BORDER);
						cell.setPaddingLeft(5);
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table1.addCell(cell);

						cell = new PdfPCell(new Phrase(String.valueOf((a.get("gst"))), redFont));
						cell.setBorder(Rectangle.NO_BORDER);
						cell.setPaddingLeft(5);
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table1.addCell(cell);

						cell = new PdfPCell(new Phrase(String.valueOf((a.get("amount"))), redFont));
						cell.setBorder(Rectangle.NO_BORDER);
						cell.setPaddingLeft(5);
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table1.addCell(cell);

					}
					cell3.setColspan(2);
					table1.setWidthPercentage(100f);
					cell3.addElement(table1);
					table.addCell(cell3);

					PdfPCell cell4 = new PdfPCell();

					PdfPTable table4 = new PdfPTable(6);
					table4.setWidths(new float[] { 5f, 1f, 5f, 8f, 1f, 3f });
					table4.setSpacingBefore(10);

					int ttl = (int) Math.round(total);
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

					hcell2 = new PdfPCell(new Phrase("Total Sale Value ", redFont));
					hcell2.setBorder(Rectangle.NO_BORDER);
					hcell2.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell2.setPaddingLeft(85f);
					table4.addCell(hcell2);

					hcell2 = new PdfPCell(new Phrase(":", redFont));
					hcell2.setBorder(Rectangle.NO_BORDER);
					hcell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
					hcell2.setPaddingRight(-30f);
					table4.addCell(hcell2);

					hcell2 = new PdfPCell(new Phrase(String.valueOf(Math.round(total * 100.0) / 100.0), redFont));
					hcell2.setBorder(Rectangle.NO_BORDER);
					hcell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
					hcell2.setPaddingRight(-40f);
					table4.addCell(hcell2);

					PdfPCell hcell04;
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

					/*
					 * BigDecimal bg=new BigDecimal(total-Math.floor(total));
					 * bg=bg.setScale(2,RoundingMode.HALF_DOWN); float round=bg.floatValue();
					 * //float rd=Math.nextUp(1f-round); float rd=1.00f-round;
					 * 
					 * if(round<0.50) { hcell04 = new PdfPCell(new Phrase("-" +round , redFont)); }
					 * else {
					 * 
					 * if(String.valueOf(rd).length()>=4) { hcell04 = new PdfPCell(new Phrase("+"
					 * +String.valueOf(rd).substring(0, 4), redFont));} else { hcell04 = new
					 * PdfPCell(new Phrase("+" +String.valueOf(rd), redFont));
					 * 
					 * }
					 * 
					 * }
					 */
					hcell04 = new PdfPCell(new Phrase(String.valueOf(Math.round(total)), redFont));
					hcell04.setBorder(Rectangle.NO_BORDER);
					hcell04.setHorizontalAlignment(Element.ALIGN_RIGHT);
					hcell04.setPaddingRight(-40f);
					table4.addCell(hcell04);

					PdfPCell hcell4;
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

					hcell4 = new PdfPCell(new Phrase("Net Amount", redFont));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell4.setPaddingLeft(85f);
					table4.addCell(hcell4);

					hcell4 = new PdfPCell(new Phrase(":", redFont));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setHorizontalAlignment(Element.ALIGN_RIGHT);
					hcell4.setPaddingRight(-30f);
					table4.addCell(hcell4);

					hcell4 = new PdfPCell(new Phrase(String.valueOf(Math.round(total)), redFont));
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

					hcell9 = new PdfPCell(new Phrase(String.valueOf(Math.round(total)), redFont));
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
					hcell34 = new PdfPCell(new Phrase(newPaymentType, redFont2));
					hcell34.setBorder(Rectangle.NO_BORDER);
					hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell34.setPaddingLeft(10f);
					table13.addCell(hcell34);

					hcell34 = new PdfPCell(new Phrase(String.valueOf(total), redFont2));
					hcell34.setBorder(Rectangle.NO_BORDER);
					hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell34.setPaddingLeft(35f);
					table13.addCell(hcell34);

					hcell34 = new PdfPCell(new Phrase("", redFont1));
					hcell34.setBorder(Rectangle.NO_BORDER);
					hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell34.setPaddingLeft(40f);
					table13.addCell(hcell34);

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

					cell33.setFixedHeight(35f);
					cell33.setColspan(2);
					table13.setWidthPercentage(100f);
					cell33.addElement(table13);
					table.addCell(cell33);

					// for new row end

					/*
					 * PharmacyShopDetails pharmacyShopDetails = pharmacyShopDetailsRepository
					 * .findByShopLocation(sales.getLocation());
					 */
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

					PdfPCell hcell7;

					hcell7 = new PdfPCell(new Phrase("Instructions  : "
							+ "1) Returns are accepted within TEN(10)Days. \n                       2) Fridge Items once sold cannot be taken Back.",
							redFont));
					hcell7.setBorder(Rectangle.NO_BORDER);
					hcell7.setPaddingLeft(-50f);
					table5.addCell(hcell7);

					hcell7 = new PdfPCell(new Phrase("Pharmacist"));
					hcell7.setBorder(Rectangle.NO_BORDER);
					hcell7.setHorizontalAlignment(Element.ALIGN_RIGHT);
					hcell7.setPaddingTop(25f);
					table5.addCell(hcell7);

					cell6.setFixedHeight(80f);
					cell6.setColspan(2);
					cell6.addElement(table5);
					table.addCell(cell6);

					document.add(table);

					document.close();
					System.out.println("finished");
					pdfByte = byteArrayOutputStream.toByteArray();

					SalesPaymentPdf salesPaymentPdfs = salesPaymentPdfServiceImpl.findByFileName(billNo);

					if (salesPaymentPdfs != null) {
						salesPaymentPdf = new SalesPaymentPdf();
						salesPaymentPdf.setFileName(billNo + "-" + regId + " Medicine Sales");
						salesPaymentPdf.setFileuri(salesPaymentPdfs.getFileuri());
						salesPaymentPdf.setPid(salesPaymentPdfs.getPid());
						salesPaymentPdf.setData(pdfByte);
						salesPaymentPdfServiceImpl.save(salesPaymentPdf);
					} else {

						String uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/sales/viewFile/")
								.path(salesPaymentPdfServiceImpl.getNextId()).toUriString();

						salesPaymentPdf = new SalesPaymentPdf();
						salesPaymentPdf.setFileName(billNo + "-" + regId + " Medicine Sales");
						salesPaymentPdf.setFileuri(uri);
						salesPaymentPdf.setPid(salesPaymentPdfServiceImpl.getNextId());
						salesPaymentPdf.setData(pdfByte);
						salesPaymentPdfServiceImpl.save(salesPaymentPdf);
					}

				} catch (Exception e) {
					Logger.error(e.getMessage());
				}
			}

			else if (patientRegistration.getpType().equals(ConstantValues.INPATIENT)
					&& !newPaymentType.equalsIgnoreCase("Advance")) {
				byte[] pdfByte = null;
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

				Document document = new Document(PageSize.A4_LANDSCAPE);
				try {

					Resource fileResourcee = resourceLoader.getResource(ConstantValues.IMAGE_PNG_CLASSPATH);
					Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
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
					hcell96 = new PdfPCell(new Phrase("UDBHAVA PHARMACY", redFont1));
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

					hcel = new PdfPCell(new Phrase(umr, redFont));
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

					hcel11 = new PdfPCell(new Phrase(regId, redFont));
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

					cell0.setFixedHeight(100f);
					cell0.setColspan(2);
					cell0.addElement(table2);
					table.addCell(cell0);

					PdfPCell cell19 = new PdfPCell();

					PdfPTable table21 = new PdfPTable(3);
					table21.setWidths(new float[] { 4f, 8f, 5f });
					table21.setSpacingBefore(10);

					PdfPCell hcell15;
					hcell15 = new PdfPCell(new Phrase("", redFont));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setPaddingLeft(-70f);
					table21.addCell(hcell15);

					if (newPaymentType.equalsIgnoreCase(ConstantValues.DUE)) {
						hcell15 = new PdfPCell(new Phrase("Pharmacy Due Receipt", redFont3));
						hcell15.setBorder(Rectangle.NO_BORDER);
						hcell15.setPaddingLeft(35);
						hcell15.setHorizontalAlignment(Element.ALIGN_CENTER);
						table21.addCell(hcell15);
					} else {
						hcell15 = new PdfPCell(new Phrase("Pharmacy Receipt", redFont3));
						hcell15.setBorder(Rectangle.NO_BORDER);
						hcell15.setPaddingLeft(35);
						hcell15.setHorizontalAlignment(Element.ALIGN_CENTER);
						table21.addCell(hcell15);
					}

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

					hcell = new PdfPCell(new Phrase("Disc(%)", redFont));
					hcell.setBorder(Rectangle.NO_BORDER);
					hcell.setBackgroundColor(BaseColor.GRAY);
					hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table1.addCell(hcell);

					hcell = new PdfPCell(new Phrase("MRP", redFont));
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

					for (Map<String, String> a : addMedicine) {

						MedicineDetails medicineDetails1 = medicineDetailsServiceImpl.findByName(a.get("medicineName"));
						List<MedicineProcurement> medicineProcurement = medicineProcurementServiceImpl
								.findByBatchAndMedicine(a.get("batcheNo"), medicineDetails1.getMedicineId());
						PdfPCell cell;

						cell = new PdfPCell(new Phrase(String.valueOf(count = count + 1), redFont));
						cell.setBorder(Rectangle.NO_BORDER);
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table1.addCell(cell);

						cell = new PdfPCell(new Phrase(a.get("medicineName"), redFont));
						cell.setBorder(Rectangle.NO_BORDER);
						cell.setPaddingLeft(5);
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

						cell = new PdfPCell(new Phrase(String.valueOf(a.get("batchNo")), redFont));
						cell.setBorder(Rectangle.NO_BORDER);
						cell.setPaddingLeft(5);
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						// cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						table1.addCell(cell);

						// for convert db date to dmy format

						/*
						 * String expdate=medicineProcurement.get(medicineProcurement.size() -
						 * 1).getExpDate().toString().substring(0,10); SimpleDateFormat fromFormat = new
						 * SimpleDateFormat("yyyy-MM-dd"); SimpleDateFormat toFormat = new
						 * SimpleDateFormat("dd-MM-yyyy");
						 * expdate=toFormat.format(fromFormat.parse(expdate));
						 */

						try {
							expdate = a.get("expiryDate").toString().substring(0, 10);
							SimpleDateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd");
							SimpleDateFormat toFormat = new SimpleDateFormat("dd-MM-yyyy");
							expdate = toFormat.format(fromFormat.parse(expdate));

						} catch (Exception e) {
							Logger.error(e.getMessage());
							// e.printStackTrace();
						}

						// System.out.println("-------------------------expiryDate-----"+expdate);
						cell = new PdfPCell(new Phrase(expdate, redFont));
						cell.setBorder(Rectangle.NO_BORDER);
						cell.setPaddingLeft(5);
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table1.addCell(cell);

						cell = new PdfPCell(new Phrase(String.valueOf((a.get("quantity"))), redFont));
						cell.setBorder(Rectangle.NO_BORDER);
						cell.setPaddingLeft(5);
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table1.addCell(cell);
						cell = new PdfPCell(new Phrase(String.valueOf((a.get("discount"))), redFont));
						cell.setBorder(Rectangle.NO_BORDER);
						cell.setPaddingLeft(5);
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table1.addCell(cell);

						cell = new PdfPCell(new Phrase(String.valueOf((a.get("mrp"))), redFont));
						cell.setBorder(Rectangle.NO_BORDER);
						cell.setPaddingLeft(5);
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table1.addCell(cell);

						cell = new PdfPCell(new Phrase(String.valueOf((a.get("gst"))), redFont));
						cell.setBorder(Rectangle.NO_BORDER);
						cell.setPaddingLeft(5);
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table1.addCell(cell);

						cell = new PdfPCell(new Phrase(String.valueOf((a.get("amount"))), redFont));
						cell.setBorder(Rectangle.NO_BORDER);
						cell.setPaddingLeft(5);
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table1.addCell(cell);

					}
					cell3.setColspan(2);
					table1.setWidthPercentage(100f);
					cell3.addElement(table1);
					table.addCell(cell3);

					PdfPCell cell4 = new PdfPCell();

					PdfPTable table4 = new PdfPTable(6);
					table4.setWidths(new float[] { 5f, 1f, 5f, 8f, 1f, 3f });
					table4.setSpacingBefore(10);

					int ttl = (int) Math.round(total);
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

					hcell2 = new PdfPCell(new Phrase(String.valueOf(Math.round(total * 100.0) / 100.0), redFont));
					hcell2.setBorder(Rectangle.NO_BORDER);
					hcell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
					hcell2.setPaddingRight(-40f);
					table4.addCell(hcell2);

					PdfPCell hcell04;
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

					/*
					 * BigDecimal bg=new BigDecimal(total-Math.floor(total));
					 * bg=bg.setScale(2,RoundingMode.HALF_DOWN); float round=bg.floatValue();
					 * //float rd=Math.nextUp(1f-round); float rd=1.00f-round;
					 * 
					 * if(round<0.50) { hcell04 = new PdfPCell(new Phrase("-" +round , redFont)); }
					 * else {
					 * 
					 * if(String.valueOf(rd).length()>=4) { hcell04 = new PdfPCell(new Phrase("+"
					 * +String.valueOf(rd).substring(0,4) , redFont)); }
					 * 
					 * else { hcell04 = new PdfPCell(new Phrase("+" +String.valueOf(rd) , redFont));
					 * }
					 * 
					 * }
					 */
					hcell04 = new PdfPCell(new Phrase(String.valueOf(Math.round(total)), redFont));
					hcell04.setBorder(Rectangle.NO_BORDER);
					hcell04.setHorizontalAlignment(Element.ALIGN_RIGHT);
					hcell04.setPaddingRight(-40f);
					table4.addCell(hcell04);

					PdfPCell hcell4;
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

					hcell4 = new PdfPCell(new Phrase("Net Amount", redFont));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell4.setPaddingLeft(85f);
					table4.addCell(hcell4);

					hcell4 = new PdfPCell(new Phrase(":", redFont));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setHorizontalAlignment(Element.ALIGN_RIGHT);
					hcell4.setPaddingRight(-30f);
					table4.addCell(hcell4);

					hcell4 = new PdfPCell(new Phrase(String.valueOf(Math.round(total)), redFont));
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

					hcell9 = new PdfPCell(new Phrase(String.valueOf(Math.round(total)), redFont));
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
					hcell34 = new PdfPCell(new Phrase(newPaymentType, redFont2));
					hcell34.setBorder(Rectangle.NO_BORDER);
					hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell34.setPaddingLeft(10f);
					table13.addCell(hcell34);

					hcell34 = new PdfPCell(new Phrase(String.valueOf(total), redFont2));
					hcell34.setBorder(Rectangle.NO_BORDER);
					hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell34.setPaddingLeft(35f);
					table13.addCell(hcell34);

					hcell34 = new PdfPCell(new Phrase("", redFont1));
					hcell34.setBorder(Rectangle.NO_BORDER);
					hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell34.setPaddingLeft(40f);
					table13.addCell(hcell34);

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

					cell33.setFixedHeight(35f);
					cell33.setColspan(2);
					table13.setWidthPercentage(100f);
					cell33.addElement(table13);
					table.addCell(cell33);

					// for new row end
					/*
					 * PharmacyShopDetails pharmacyShopDetails = pharmacyShopDetailsRepository
					 * .findByShopLocation(sales.getLocation());
					 * 
					 */ PdfPCell cell01 = new PdfPCell();

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

					PdfPCell hcell7;

					hcell7 = new PdfPCell(new Phrase("Instructions  : "
							+ "1) Returns are accepted within TEN(10)Days. \n                       2) Fridge Items once sold cannot be taken Back.",
							redFont));
					hcell7.setBorder(Rectangle.NO_BORDER);
					hcell7.setPaddingLeft(-50f);
					table5.addCell(hcell7);

					hcell7 = new PdfPCell(new Phrase("Pharmacist"));
					hcell7.setBorder(Rectangle.NO_BORDER);
					hcell7.setHorizontalAlignment(Element.ALIGN_RIGHT);
					hcell7.setPaddingTop(25f);
					table5.addCell(hcell7);

					cell6.setFixedHeight(80f);
					cell6.setColspan(2);
					cell6.addElement(table5);
					table.addCell(cell6);

					document.add(table);

					document.close();
					System.out.println("finished");
					pdfByte = byteArrayOutputStream.toByteArray();
					SalesPaymentPdf salesPaymentPdfs = salesPaymentPdfServiceImpl.findByFileName(billNo);

					if (salesPaymentPdfs != null) {
						salesPaymentPdf = new SalesPaymentPdf();
						salesPaymentPdf.setFileName(billNo + "-" + regId + " Medicine Sales");
						salesPaymentPdf.setFileuri(salesPaymentPdfs.getFileuri());
						salesPaymentPdf.setPid(salesPaymentPdfs.getPid());
						salesPaymentPdf.setData(pdfByte);
						salesPaymentPdfServiceImpl.save(salesPaymentPdf);
					} else {

						String uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/sales/viewFile/")
								.path(salesPaymentPdfServiceImpl.getNextId()).toUriString();

						salesPaymentPdf = new SalesPaymentPdf();
						salesPaymentPdf.setFileName(billNo + "-" + regId + " Medicine Sales");
						salesPaymentPdf.setFileuri(uri);
						salesPaymentPdf.setPid(salesPaymentPdfServiceImpl.getNextId());
						salesPaymentPdf.setData(pdfByte);
						salesPaymentPdfServiceImpl.save(salesPaymentPdf);
					}

				} catch (Exception e) {
					Logger.error(e.getMessage());
					// e.printStackTrace();
				}
			}

		} else // for walk-ins
		{
			byte[] pdfByte = null;
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			Document document = new Document(PageSize.A4_LANDSCAPE);
			try {

				Resource fileResourcee = resourceLoader.getResource(ConstantValues.IMAGE_PNG_CLASSPATH);

				Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
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

				PdfPCell hcell18;
				hcell18 = new PdfPCell(new Phrase("Patient Name", redFont));
				hcell18.setBorder(Rectangle.NO_BORDER);
				hcell18.setPaddingLeft(-15f);
				table2.addCell(hcell18);

				hcell18 = new PdfPCell(new Phrase(":", redFont));
				hcell18.setBorder(Rectangle.NO_BORDER);
				hcell18.setPaddingLeft(-35f);
				table2.addCell(hcell18);

				hcell18 = new PdfPCell(new Phrase(salesPrev.getName(), redFont));
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

				hcell15 = new PdfPCell(new Phrase("Pharmacy Receipt", redFont3));
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

				hcell = new PdfPCell(new Phrase("Disc(%)", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("MRP", redFont));
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

				for (Map<String, String> a : addMedicine) {

					MedicineDetails medicineDetails1 = medicineDetailsServiceImpl.findByName(a.get("medicineName"));
					List<MedicineProcurement> medicineProcurement = medicineProcurementServiceImpl
							.findByBatchAndMedicine(a.get("batchNo"), medicineDetails1.getMedicineId());
					PdfPCell cell;

					cell = new PdfPCell(new Phrase(String.valueOf(count = count + 1), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table1.addCell(cell);

					cell = new PdfPCell(new Phrase(a.get("medicineName"), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
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

					cell = new PdfPCell(new Phrase(String.valueOf(a.get("batchNo")), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					// cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					table1.addCell(cell);

					// for convert db date to dmy format

					/*
					 * String expdate=medicineProcurement.get(medicineProcurement.size() -
					 * 1).getExpDate().toString().substring(0,10); SimpleDateFormat fromFormat = new
					 * SimpleDateFormat("yyyy-MM-dd"); SimpleDateFormat toFormat = new
					 * SimpleDateFormat("dd-MM-yyyy");
					 * expdate=toFormat.format(fromFormat.parse(expdate));
					 */

					try {
						expdate = a.get("expiryDate").toString().substring(0, 10);
						SimpleDateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd");
						SimpleDateFormat toFormat = new SimpleDateFormat("dd-MM-yyyy");
						expdate = toFormat.format(fromFormat.parse(expdate));

					} catch (Exception e) {
						Logger.error(e.getMessage());
					}

					cell = new PdfPCell(new Phrase(expdate, redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table1.addCell(cell);

					cell = new PdfPCell(new Phrase(String.valueOf((a.get("quantity"))), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table1.addCell(cell);
					cell = new PdfPCell(new Phrase(String.valueOf((a.get("discount"))), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table1.addCell(cell);

					cell = new PdfPCell(new Phrase(String.valueOf((a.get("mrp"))), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table1.addCell(cell);

					cell = new PdfPCell(new Phrase(String.valueOf((a.get("gst"))), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table1.addCell(cell);

					cell = new PdfPCell(new Phrase(String.valueOf((a.get("amount"))), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table1.addCell(cell);

				}
				cell3.setColspan(2);
				table1.setWidthPercentage(100f);
				cell3.addElement(table1);
				table.addCell(cell3);

				PdfPCell cell4 = new PdfPCell();

				PdfPTable table4 = new PdfPTable(6);
				table4.setWidths(new float[] { 5f, 1f, 5f, 8f, 1f, 3f });
				table4.setSpacingBefore(10);

				int ttl = (int) Math.round(total);
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

				hcell2 = new PdfPCell(new Phrase(String.valueOf(Math.round(total * 100.0) / 100.0), redFont));
				hcell2.setBorder(Rectangle.NO_BORDER);
				hcell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell2.setPaddingRight(-40f);
				table4.addCell(hcell2);

				PdfPCell hcell04;
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

				/*
				 * BigDecimal bg=new BigDecimal(total-Math.floor(total));
				 * bg=bg.setScale(2,RoundingMode.HALF_DOWN); float round=bg.floatValue();
				 * //float rd=Math.nextUp(1f-round); float rd=1.00f-round;
				 * 
				 * if(round<0.50) { hcell04 = new PdfPCell(new Phrase("-" +round , redFont)); }
				 * else {
				 * 
				 * 
				 * if(String.valueOf(rd).length()>=4) { hcell04 = new PdfPCell(new Phrase("+"
				 * +String.valueOf(rd).substring(0,4) , redFont)); }
				 * 
				 * else { hcell04 = new PdfPCell(new Phrase("+" +String.valueOf(rd) , redFont));
				 * 
				 * }
				 * 
				 * }
				 */
				hcell04 = new PdfPCell(new Phrase(String.valueOf(Math.round(total)), redFont));
				hcell04.setBorder(Rectangle.NO_BORDER);
				hcell04.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell04.setPaddingRight(-40f);
				table4.addCell(hcell04);

				PdfPCell hcell4;
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

				hcell4 = new PdfPCell(new Phrase("Net Amount", redFont));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell4.setPaddingLeft(85f);
				table4.addCell(hcell4);

				hcell4 = new PdfPCell(new Phrase(":", redFont));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell4.setPaddingRight(-30f);
				table4.addCell(hcell4);

				hcell4 = new PdfPCell(new Phrase(String.valueOf(Math.round(total)), redFont));
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

				hcell9 = new PdfPCell(new Phrase(String.valueOf(Math.round(total)), redFont));
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
				hcell34 = new PdfPCell(new Phrase(sales.getPaymentType(), redFont2));
				hcell34.setBorder(Rectangle.NO_BORDER);
				hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell34.setPaddingLeft(10f);
				table13.addCell(hcell34);

				hcell34 = new PdfPCell(new Phrase(String.valueOf(total), redFont2));
				hcell34.setBorder(Rectangle.NO_BORDER);
				hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell34.setPaddingLeft(35f);
				table13.addCell(hcell34);

				hcell34 = new PdfPCell(new Phrase("", redFont1));
				hcell34.setBorder(Rectangle.NO_BORDER);
				hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell34.setPaddingLeft(40f);
				table13.addCell(hcell34);

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

				cell33.setFixedHeight(35f);
				cell33.setColspan(2);
				table13.setWidthPercentage(100f);
				cell33.addElement(table13);
				table.addCell(cell33);

				// for new row end
				/*
				 * PharmacyShopDetails pharmacyShopDetails = pharmacyShopDetailsRepository
				 * .findByShopLocation(sales.getLocation());
				 */
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

				PdfPCell hcell7;

				hcell7 = new PdfPCell(new Phrase("Instructions  : "
						+ "1) Returns are accepted within TEN(10)Days. \n                       2) Fridge Items once sold cannot be taken Back.",
						redFont));
				hcell7.setBorder(Rectangle.NO_BORDER);
				hcell7.setPaddingLeft(-50f);
				table5.addCell(hcell7);

				hcell7 = new PdfPCell(new Phrase("Pharmacist", redFont1));
				hcell7.setBorder(Rectangle.NO_BORDER);
				hcell7.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell7.setPaddingTop(25f);
				table5.addCell(hcell7);

				cell6.setFixedHeight(80f);
				cell6.setColspan(2);
				cell6.addElement(table5);
				table.addCell(cell6);

				document.add(table);

				document.close();
				System.out.println("finished");
				pdfByte = byteArrayOutputStream.toByteArray();
				SalesPaymentPdf salesPaymentPdfs = salesPaymentPdfServiceImpl.findByFileName(billNo);

				if (salesPaymentPdfs != null) {
					salesPaymentPdf = new SalesPaymentPdf();
					salesPaymentPdf.setFileName(billNo + " " + "Sales");
					salesPaymentPdf.setFileuri(salesPaymentPdfs.getFileuri());
					salesPaymentPdf.setPid(salesPaymentPdfs.getPid());
					salesPaymentPdf.setData(pdfByte);
					salesPaymentPdfServiceImpl.save(salesPaymentPdf);
				} else {

					System.out.println("---------Coming to else condition---------------");
					String uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/sales/viewFile/")
							.path(salesPaymentPdfServiceImpl.getNextId()).toUriString();

					salesPaymentPdf = new SalesPaymentPdf();
					salesPaymentPdf.setFileName(billNo + " " + "Sales");
					salesPaymentPdf.setFileuri(uri);
					salesPaymentPdf.setPid(salesPaymentPdfServiceImpl.getNextId());
					salesPaymentPdf.setData(pdfByte);
					salesPaymentPdfServiceImpl.save(salesPaymentPdf);
				}

			} catch (Exception e) {
				Logger.error(e.getMessage());
			}
		}

//---------------------end--------------------------------

	}

	@Override

	public SalesPaymentPdf generateOutStandingSalesReport(String regId, Principal principal) {

		User userSecurity = userServiceImpl.findByUserName(principal.getName());
		String createdBy = (userSecurity.getMiddleName() != null)
				? userSecurity.getFirstName() + " " + userSecurity.getMiddleName() + " " + userSecurity.getLastName()
				: userSecurity.getFirstName() + " " + userSecurity.getLastName();

		PatientRegistration patientRegistartion = patientRegistrationServiceImpl.findByRegId(regId);
		List<Sales> listOfSales = salesServiceImpl.findByPatientRegistration(patientRegistartion);

		String patientName = listOfSales.get(0).getName();

		byte[] pdfByte = null;
		ByteArrayOutputStream byteArrayOutputStream1 = new ByteArrayOutputStream();

		try {

			Resource fileResourcee = resourceLoader.getResource(ConstantValues.IMAGE_PNG_CLASSPATH);

			Document document = new Document(PageSize.A4_LANDSCAPE);
			PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream1);

			document.open();
			PdfPTable table = new PdfPTable(2);

			Image img = Image.getInstance(hospitalLogo.getURL());
			img.scaleAbsolute(ConstantValues.IMAGE_ABSOLUTE_INTIAL_POSITION,
					ConstantValues.IMAGE_ABSOLUTE_FINAL_POSITION);
			table.setWidthPercentage(ConstantValues.TABLE_SET_WIDTH_PERECENTAGE);

			Phrase pq = new Phrase(
					new Chunk(img, ConstantValues.IMAGE_SET_INTIAL_POSITION, ConstantValues.IMAGE_SET_FINAL_POSITION));

			Font redFont2 = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
			Font redFont3 = new Font(Font.FontFamily.HELVETICA, 12, Font.UNDERLINE);
			Font redFont4 = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);

			Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
			Font redFont9 = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.NORMAL);

			Font redFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

			Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);

			/*
			 * String newAddress =
			 * "                                                    Plot No.14,15,16 &17,Nandi Co-op. Society,     \n                                                              Main Road,Beside Navya Grand Hotel,Miyapur,Hyderabad,TS                       \n                                                               Phone:040-23046789 | For Appointment Contact: 8019114481   \n                                                                             Email : udbhavahospitals@gmail.com"
			 * ;
			 */

			pq.add(new Chunk(ConstantValues.FINAL_DISCHARGE, redFont));

			PdfPCell cellp = new PdfPCell(pq);
			PdfPCell cell1 = new PdfPCell();
			cell1.setBorder(0);

			// cell1.setFixedHeight(107f);

			PdfPTable table351 = new PdfPTable(1);
			table351.setWidths(new float[] { 5f });
			table351.setSpacingBefore(10);
			table351.setWidthPercentage(100f);

			PdfPCell hcell351;
			hcell351 = new PdfPCell(new Phrase(ConstantValues.PHARMACY_NAME, redFont4));
			hcell351.setBorder(Rectangle.NO_BORDER);
			hcell351.setHorizontalAlignment(Element.ALIGN_CENTER);
			table351.addCell(hcell351);

			cell1.addElement(table351);
			cell1.addElement(pq);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setColspan(2);

			PdfPTable table35 = new PdfPTable(1);
			table35.setWidths(new float[] { 5f });
			table35.setSpacingBefore(10);
			table35.setWidthPercentage(100f);

			PdfPCell hcell35;
			hcell35 = new PdfPCell(new Phrase("INPATIENT PHARMACY BILL", headFont));
			hcell35.setBorder(Rectangle.NO_BORDER);
			hcell35.setHorizontalAlignment(Element.ALIGN_CENTER);
			table35.addCell(hcell35);

			cell1.addElement(table35);

			PdfPTable table182 = new PdfPTable(1);
			table182.setWidths(new float[] { 5f });
			table182.setSpacingBefore(10);
			table182.setWidthPercentage(100f);

			PdfPCell hcell072;
			hcell072 = new PdfPCell(new Phrase(
					"___________________________________________________________________________________________________________",
					headFont));
			hcell072.setBorder(Rectangle.NO_BORDER);
			hcell072.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table182.addCell(hcell072);

			cell1.addElement(table182);

			PdfPTable table3 = new PdfPTable(6);
			table3.setWidths(new float[] { 5f, 1f, 5f, 5f, 1f, 5f });
			table3.setSpacingBefore(10);

			PdfPCell hcell1;
			hcell1 = new PdfPCell(new Phrase("Reg No", redFont1));
			hcell1.setBorder(Rectangle.NO_BORDER);
			hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell1.setPaddingLeft(-50f);
			table3.addCell(hcell1);

			hcell1 = new PdfPCell(new Phrase(":", redFont1));
			hcell1.setBorder(Rectangle.NO_BORDER);
			hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell1.setPaddingLeft(-50f);
			table3.addCell(hcell1);

			hcell1 = new PdfPCell(new Phrase(regId, redFont1));
			hcell1.setBorder(Rectangle.NO_BORDER);
			hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell1.setPaddingLeft(-50f);
			table3.addCell(hcell1);

			PdfPCell hcell10;
			hcell10 = new PdfPCell(new Phrase("Umr No", redFont1));
			hcell10.setBorder(Rectangle.NO_BORDER);
			// hcell10.setPaddingLeft(40f);
			table3.addCell(hcell10);

			hcell10 = new PdfPCell(new Phrase(":", redFont1));
			hcell10.setBorder(Rectangle.NO_BORDER);
			// hcell10.setPaddingLeft(40f);
			table3.addCell(hcell10);

			hcell10 = new PdfPCell(new Phrase(patientRegistartion.getPatientDetails().getUmr(), redFont1));
			hcell10.setBorder(Rectangle.NO_BORDER);
			// hcell10.setPaddingLeft(40f);
			table3.addCell(hcell10);

			PdfPCell hcell2;
			hcell2 = new PdfPCell(new Phrase("Patient Name", redFont1));
			hcell2.setBorder(Rectangle.NO_BORDER);
			hcell2.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell2.setPaddingLeft(-50f);
			table3.addCell(hcell2);

			hcell2 = new PdfPCell(new Phrase(":", redFont1));
			hcell2.setBorder(Rectangle.NO_BORDER);
			hcell2.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell2.setPaddingLeft(-50f);
			table3.addCell(hcell2);

			hcell2 = new PdfPCell(new Phrase(patientName, redFont1));
			hcell2.setBorder(Rectangle.NO_BORDER);
			hcell2.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell2.setPaddingLeft(-50f);
			table3.addCell(hcell2);

			PdfPCell hcell20;
			hcell20 = new PdfPCell(new Phrase("Consultant", redFont1));
			hcell20.setBorder(Rectangle.NO_BORDER);
			// hcell20.setPaddingLeft(40f);
			table3.addCell(hcell20);

			hcell20 = new PdfPCell(new Phrase(":", redFont1));
			hcell20.setBorder(Rectangle.NO_BORDER);
			// hcell20.setPaddingLeft(40f);
			table3.addCell(hcell20);

			hcell20 = new PdfPCell(new Phrase(patientRegistartion.getPatientDetails().getConsultant(), redFont1));
			hcell20.setBorder(Rectangle.NO_BORDER);
			// hcell20.setPaddingLeft(40f);
			table3.addCell(hcell20);

			// Display a date in day, month, year format
			Date date = Calendar.getInstance().getTime();
			DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
			String today = formatter.format(date).toString();
			PdfPCell hcell21;
			hcell21 = new PdfPCell(new Phrase("Created By", redFont1));
			hcell21.setBorder(Rectangle.NO_BORDER);
			hcell21.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell21.setPaddingLeft(-50f);
			table3.addCell(hcell21);

			hcell21 = new PdfPCell(new Phrase(":", redFont1));
			hcell21.setBorder(Rectangle.NO_BORDER);
			hcell21.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell21.setPaddingLeft(-50f);
			table3.addCell(hcell21);

			hcell21 = new PdfPCell(new Phrase(createdBy, redFont1));
			hcell21.setBorder(Rectangle.NO_BORDER);
			hcell21.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell21.setPaddingLeft(-50f);
			table3.addCell(hcell21);

			PdfPCell hcell201;
			hcell201 = new PdfPCell(new Phrase("Created Date", redFont1));
			hcell201.setBorder(Rectangle.NO_BORDER);
			// hcell20.setPaddingLeft(40f);
			table3.addCell(hcell201);

			hcell201 = new PdfPCell(new Phrase(":", redFont1));
			hcell201.setBorder(Rectangle.NO_BORDER);
			// hcell20.setPaddingLeft(40f);
			table3.addCell(hcell201);

			hcell201 = new PdfPCell(new Phrase(today, redFont1));
			hcell201.setBorder(Rectangle.NO_BORDER);
			// hcell20.setPaddingLeft(40f);
			table3.addCell(hcell201);

			cell1.addElement(table3);

			PdfPTable table1 = new PdfPTable(7);
			table1.setWidths(new float[] { 1f, 4f, 3f, 5f, 2f, 2.5f, 2f });
			table1.setSpacingBefore(10);
			table1.setWidthPercentage(105f);

			PdfPCell hcell;

			hcell = new PdfPCell(new Phrase("S.No", headFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			hcell.setBackgroundColor(BaseColor.GRAY);
			hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
			table1.addCell(hcell);

			hcell = new PdfPCell(new Phrase("Bill Date", headFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			hcell.setBackgroundColor(BaseColor.GRAY);
			hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell.setPaddingLeft(5f);
			table1.addCell(hcell);

			hcell = new PdfPCell(new Phrase("Bill No", headFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			hcell.setBackgroundColor(BaseColor.GRAY);
			hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
			table1.addCell(hcell);

			hcell = new PdfPCell(new Phrase("Item Name", headFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			hcell.setBackgroundColor(BaseColor.GRAY);
			hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
			table1.addCell(hcell);

			hcell = new PdfPCell(new Phrase("Qty", headFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			hcell.setBackgroundColor(BaseColor.GRAY);
			hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
			table1.addCell(hcell);

			hcell = new PdfPCell(new Phrase("Amount", headFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			hcell.setBackgroundColor(BaseColor.GRAY);
			hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
			table1.addCell(hcell);

			hcell = new PdfPCell(new Phrase("Paid", headFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			hcell.setBackgroundColor(BaseColor.GRAY);
			hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell.setPaddingLeft(30);
			table1.addCell(hcell);

			table1.addCell(hcell);

			cell1.addElement(table1);

			PdfPTable table11 = new PdfPTable(7);
			table11.setWidths(new float[] { 1f, 4f, 3f, 5f, 2f, 2.5f, 2f });
			table11.setSpacingBefore(10);
			table11.setWidthPercentage(105f);

			int i = 0;

			float totalAmount = 0;
			float dueAmount = 0;

			PdfPCell cell;
			for (Sales sale : listOfSales) {

				if (sale.getAmount() > 0) {

					String from = sale.getBillDate().toString();
					Timestamp timestamp = Timestamp.valueOf(from);
					DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");

					Calendar calendar = Calendar.getInstance();
					calendar.setTimeInMillis(timestamp.getTime());

					String saleDate = dateFormat.format(calendar.getTime());

					cell = new PdfPCell(new Phrase(String.valueOf(i + 1), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					table11.addCell(cell);

					cell = new PdfPCell(new Phrase(saleDate, redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					table11.addCell(cell);

					cell = new PdfPCell(new Phrase(sale.getBillNo(), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					table11.addCell(cell);

					cell = new PdfPCell(new Phrase(sale.getMedicineName(), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					table11.addCell(cell);

					cell = new PdfPCell(new Phrase(String.valueOf(sale.getQuantity()), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					table11.addCell(cell);

					cell = new PdfPCell(new Phrase(String.valueOf(sale.getAmount()), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					table11.addCell(cell);

					cell = new PdfPCell(new Phrase(sale.getPaid(), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cell.setPaddingRight(10);
					table11.addCell(cell);

					i++;
					totalAmount += sale.getAmount();
					if (sale.getPaymentType().equalsIgnoreCase(ConstantValues.DUE)) {

						dueAmount += sale.getAmount();
					}

				}
			}

			float paidAmount = totalAmount - dueAmount;

			cell1.addElement(table11);

			PdfPTable table1821 = new PdfPTable(1);
			table1821.setWidths(new float[] { 5f });
			table1821.setSpacingBefore(10);
			table1821.setWidthPercentage(100f);

			PdfPCell hcell0721;
			hcell0721 = new PdfPCell(
					new Phrase("_________________________________________________________________________", headFont));
			hcell0721.setBorder(Rectangle.NO_BORDER);
			hcell0721.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table1821.addCell(hcell0721);

			cell1.addElement(table1821);

			PdfPTable table14 = new PdfPTable(5);
			table14.setWidths(new float[] { 2f, 1f, 10f, 5f, 5f });
			table14.setSpacingBefore(10);
			table14.setWidthPercentage(100f);

			PdfPCell hcell02;

			hcell02 = new PdfPCell(new Phrase("", headFont));
			hcell02.setBorder(Rectangle.NO_BORDER);
			hcell02.setHorizontalAlignment(Element.ALIGN_LEFT);
			table14.addCell(hcell02);

			hcell02 = new PdfPCell(new Phrase("", headFont));
			hcell02.setBorder(Rectangle.NO_BORDER);
			hcell02.setHorizontalAlignment(Element.ALIGN_LEFT);
			table14.addCell(hcell02);

			hcell02 = new PdfPCell(new Phrase("TOTAL AMOUNT", headFont));
			hcell02.setBorder(Rectangle.NO_BORDER);
			hcell02.setHorizontalAlignment(Element.ALIGN_RIGHT);
			hcell02.setPaddingRight(-56f);
			table14.addCell(hcell02);

			hcell02 = new PdfPCell(new Phrase(":", headFont));
			hcell02.setBorder(Rectangle.NO_BORDER);
			hcell02.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table14.addCell(hcell02);

			hcell02 = new PdfPCell(new Phrase(String.valueOf(Math.round(totalAmount)), headFont));
			hcell02.setBorder(Rectangle.NO_BORDER);
			hcell02.setHorizontalAlignment(Element.ALIGN_RIGHT);
			hcell02.setPaddingRight(50);
			table14.addCell(hcell02);

			PdfPCell hcell021;

			hcell021 = new PdfPCell(new Phrase("", headFont));
			hcell021.setBorder(Rectangle.NO_BORDER);
			hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
			table14.addCell(hcell021);

			hcell021 = new PdfPCell(new Phrase("", headFont));
			hcell021.setBorder(Rectangle.NO_BORDER);
			hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
			table14.addCell(hcell021);

			hcell021 = new PdfPCell(new Phrase("PAID AMOUNT", headFont));
			hcell021.setBorder(Rectangle.NO_BORDER);
			hcell021.setHorizontalAlignment(Element.ALIGN_RIGHT);
			hcell021.setPaddingRight(-59f);
			table14.addCell(hcell021);

			hcell021 = new PdfPCell(new Phrase(":", headFont));
			hcell021.setBorder(Rectangle.NO_BORDER);
			hcell021.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table14.addCell(hcell021);

			hcell021 = new PdfPCell(new Phrase(String.valueOf(Math.round(paidAmount)), headFont));
			hcell021.setBorder(Rectangle.NO_BORDER);
			hcell021.setHorizontalAlignment(Element.ALIGN_RIGHT);
			hcell021.setPaddingRight(50);
			table14.addCell(hcell021);

			PdfPCell hcell0211;

			hcell0211 = new PdfPCell(new Phrase("", headFont));
			hcell0211.setBorder(Rectangle.NO_BORDER);
			hcell0211.setHorizontalAlignment(Element.ALIGN_LEFT);
			table14.addCell(hcell0211);

			hcell0211 = new PdfPCell(new Phrase("", headFont));
			hcell0211.setBorder(Rectangle.NO_BORDER);
			hcell0211.setHorizontalAlignment(Element.ALIGN_LEFT);
			table14.addCell(hcell0211);

			hcell0211 = new PdfPCell(new Phrase("DUE AMOUNT", headFont));
			hcell0211.setBorder(Rectangle.NO_BORDER);
			hcell0211.setHorizontalAlignment(Element.ALIGN_RIGHT);
			hcell0211.setPaddingRight(-59f);
			table14.addCell(hcell0211);

			hcell0211 = new PdfPCell(new Phrase(":", headFont));
			hcell0211.setBorder(Rectangle.NO_BORDER);
			hcell0211.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table14.addCell(hcell0211);

			hcell0211 = new PdfPCell(new Phrase(String.valueOf(Math.round(dueAmount)), headFont));
			hcell0211.setBorder(Rectangle.NO_BORDER);
			hcell0211.setHorizontalAlignment(Element.ALIGN_RIGHT);
			hcell0211.setPaddingRight(50);
			table14.addCell(hcell0211);
			cell1.addElement(table14);

			table.addCell(cell1);
			document.add(table);

			document.close();

			System.out.println("finished");

			pdfByte = byteArrayOutputStream1.toByteArray();
			String uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/sales/viewFile/")
					.path(salesPaymentPdfServiceImpl.getNextId()).toUriString();

			salesPaymentPdf = salesPaymentPdfServiceImpl.getSalesPdf(regId + "" + "Ip Pharmacy Bill");
			if (salesPaymentPdf != null) {
				salesPaymentPdf.setData(pdfByte);
				salesPaymentPdfServiceImpl.save(salesPaymentPdf);

			} else {
				salesPaymentPdf = new SalesPaymentPdf();
				salesPaymentPdf.setFileName(regId + "" + "Ip Pharmacy Bill");
				salesPaymentPdf.setFileuri(uri);
				salesPaymentPdf.setPid(salesPaymentPdfServiceImpl.getNextId());
				salesPaymentPdf.setData(pdfByte);
				salesPaymentPdfServiceImpl.save(salesPaymentPdf);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return salesPaymentPdf;

	}

	public List<Map<String, String>> getmedicinesnames1() {
		List<Map<String, String>> displayList = new ArrayList<>();
		Iterable<MedicineDetails> medicineDetails = medicineDetailsServiceImpl.findAll();

		for (MedicineDetails medicineDetailsInfo : medicineDetails) {
			Map<String, String> map = new HashMap<>();
			map.put("medicinename", medicineDetailsInfo.getName());
			displayList.add(map);
		}
		return displayList;

	}

	@Override
	public Iterable<Sales> displaySalesReturnList(String days) {
		return salesRepository.findAll();
	}

	@Override
	public List<Object> getStockDetails(Map<String, String> stockDetails) {

		float procQuantity = 0;
		float salesQuantity = 0;
		float available = 0;
		String medicineName = stockDetails.get("medicineName");
		String batch = stockDetails.get("batch");
		String expDate = stockDetails.get("expDate").substring(0, 10);
		List<Object> list = new ArrayList<Object>();
		Map<String, String> map = new HashMap<String, String>();
		List<MedicineProcurement> medicineProcurementsList = medicineProcurementRepository
				.findStockAdjustments(medicineName, batch, expDate);
		
		
		for (MedicineProcurement medicineProcurementsInfo : medicineProcurementsList) {
			procQuantity += medicineProcurementsInfo.getDetailedQuantity();

		}

		List<Sales> salesList = salesRepository.findStockAdjustments(medicineName, batch, expDate);

		for (Sales salesInfo : salesList) {
			salesQuantity += salesInfo.getQuantity();
		}

		available = procQuantity - salesQuantity;
		map.put("medicineName", medicineName);
		map.put("detailedQty", String.valueOf(procQuantity));
		map.put("saleQty", String.valueOf(salesQuantity));
		map.put("closingQty", String.valueOf(available));
		list.add(map);
		procQuantity = 0;
		salesQuantity = 0;

		return list;
	}

	public String updateStockDetails(Map<String, String> stockDetails) {
		String medicineName = stockDetails.get("medicineName");
		String batch = stockDetails.get("batch");
		String expDate = stockDetails.get("expDate").substring(0, 10);
		String modifiedQty = stockDetails.get("modifiedQty");

		long modifiedQuantity = Long.parseLong(modifiedQty);

		List<MedicineProcurement> medicineProcurementsList = medicineProcurementRepository
				.findStockAdjustments(medicineName, batch, expDate);

		for (MedicineProcurement medicineProcurementsListInfo : medicineProcurementsList) {
			medicineProcurementsListInfo.setDetailedQuantity(modifiedQuantity);
			medicineProcurementRepository.save(medicineProcurementsListInfo);

		}
		return medicineProcurementsList.size() + "no of records updated successfully";
	}

	
	/* Excel for IP Pharmacy List*/
	public PatientPaymentPdf excelForIpPharmacyList(String regId) throws IOException {
	
		List<Sales> listOfSales = salesRepository.findByPatientRegistrationAndQuantity(regId);
		if(listOfSales.isEmpty()) {
			throw new RuntimeException("No medicine available for "+regId+" "+"number");
			
		}
		 String path = context.getRealPath("/");
		PatientPaymentPdf patientPaymentPdf = null;
			
			byte[] pdfBytes = null;
		    ByteArrayOutputStream out=null;
		    
		    String[] columns = {"S.No", "Bill No","Bill Date", "Patient Name", "Item Name","MRP","Purchase Qty","amount"};
		    try(
					Workbook workbook = new XSSFWorkbook();
					
			){
				
				out = new ByteArrayOutputStream();
				CreationHelper createHelper = workbook.getCreationHelper();
		 
				Sheet sheet = workbook.createSheet("IPPharmacyList");
		 
				org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
				headerFont.setBold(true);
				headerFont.setColor(IndexedColors.BLUE.getIndex());
		 
				CellStyle headerCellStyle = workbook.createCellStyle();
				headerCellStyle.setFont(headerFont);
				// Row for Header
				Row headerRow = sheet.createRow(0);
		 
				// Header
				for (int col = 0; col < columns.length; col++) {
					Cell cell = headerRow.createCell(col);
					cell.setCellValue(columns[col]);
					cell.setCellStyle(headerCellStyle);
				}
		    
				// CellStyle for Age
				CellStyle ageCellStyle = workbook.createCellStyle();
				ageCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("#"));	
				
				int rowIdx = 1;
				
				int i=1;
				for (Sales  saleInfo: listOfSales) {
					
					String billDate = String.valueOf(saleInfo.getBillDate().toString());
					SimpleDateFormat fromFormat = new SimpleDateFormat(ConstantValues.yyyy_MM_dd_HH_mm_ss); // yyyy-MM-dd
																											// HH:mm:ss
					SimpleDateFormat toFormat = new SimpleDateFormat(ConstantValues.dd_MM_yyyy_hh_mm_a); // dd-MM-yyyy hh:mm a
					try {
						billDate = toFormat.format(fromFormat.parse(billDate));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Row row = sheet.createRow(rowIdx++);
		 			row.createCell(0).setCellValue(i++);
					row.createCell(1).setCellValue( saleInfo.getBillNo());
					row.createCell(2).setCellValue(billDate);
					row.createCell(3).setCellValue(saleInfo.getName());
					row.createCell(4).setCellValue(saleInfo.getMedicineName());
					row.createCell(5).setCellValue(saleInfo.getMrp());
					row.createCell(6).setCellValue(saleInfo.getQuantity());
					row.createCell(7).setCellValue(String.valueOf(saleInfo.getActualAmount()));
		 
								
				}
		 
				workbook.write(out);
				
			    workbook.close();
		    }
		    pdfBytes=out.toByteArray();
			String uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/payment/excelviewFile/")
					.path(paymentPdfServiceImpl.getNextPdfId()).toUriString();

			patientPaymentPdf = new PatientPaymentPdf();
			patientPaymentPdf.setFileName("IPPharamcy List for "+regId+".xls");
			patientPaymentPdf.setRegId(regId);
			patientPaymentPdf.setFileuri(uri);
			patientPaymentPdf.setPid(paymentPdfServiceImpl.getNextPdfId());
			patientPaymentPdf.setData(pdfBytes);
			paymentPdfServiceImpl.save(patientPaymentPdf);
		  return patientPaymentPdf;	}

}
