package com.example.test.testingHMS.UpdatePdfs;

import java.io.ByteArrayOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.Principal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.test.testingHMS.MoneyToWords.NumberToWordsConverter;
import com.example.test.testingHMS.bed.model.RoomBookingDetails;
import com.example.test.testingHMS.bed.model.RoomDetails;
import com.example.test.testingHMS.bill.dto.ChargeBillDto;
import com.example.test.testingHMS.bill.model.ChargeBill;
import com.example.test.testingHMS.bill.repository.ChargeBillRepository;
import com.example.test.testingHMS.bill.serviceImpl.ChargeBillServiceImpl;
import com.example.test.testingHMS.finalBilling.model.FinalBilling;
import com.example.test.testingHMS.finalBilling.repository.FinalBillingRepository;
import com.example.test.testingHMS.finalBilling.serviceImpl.FinalBillingServiceImpl;
import com.example.test.testingHMS.laboratory.helper.RefLaboratoryRegistration;
import com.example.test.testingHMS.laboratory.model.LaboratoryRegistration;
import com.example.test.testingHMS.laboratory.repository.LaboratoryRegistrationRepository;
import com.example.test.testingHMS.laboratory.serviceImpl.LaboratoryRegistrationServiceImpl;
import com.example.test.testingHMS.patient.model.CashPlusCard;
import com.example.test.testingHMS.patient.model.PatientDetails;
import com.example.test.testingHMS.patient.model.PatientPayment;
import com.example.test.testingHMS.patient.model.PatientPaymentPdf;
import com.example.test.testingHMS.patient.model.PatientRegistration;
import com.example.test.testingHMS.patient.repository.CashPlusCardRepository;
import com.example.test.testingHMS.patient.serviceImpl.CashPlusCardServiceImpl;
import com.example.test.testingHMS.patient.serviceImpl.PatientRegistrationServiceImpl;
import com.example.test.testingHMS.patient.serviceImpl.PaymentPdfServiceImpl;
import com.example.test.testingHMS.pharmacist.helper.RefSales;
import com.example.test.testingHMS.pharmacist.helper.RefSalesIds;
import com.example.test.testingHMS.pharmacist.model.MedicineDetails;
import com.example.test.testingHMS.pharmacist.model.MedicineProcurement;
import com.example.test.testingHMS.pharmacist.model.Sales;
import com.example.test.testingHMS.pharmacist.model.SalesPaymentPdf;
import com.example.test.testingHMS.pharmacist.model.SalesReturn;
import com.example.test.testingHMS.pharmacist.repository.MedicineDetailsRepository;
import com.example.test.testingHMS.pharmacist.repository.MedicineProcurementRepository;
import com.example.test.testingHMS.pharmacist.repository.MedicineQuantityRepository;
import com.example.test.testingHMS.pharmacist.repository.SalesRepository;
import com.example.test.testingHMS.pharmacist.repository.SalesReturnRepository;
import com.example.test.testingHMS.pharmacist.serviceImpl.LocationServiceImpl;
import com.example.test.testingHMS.pharmacist.serviceImpl.MedicineDetailsServiceImpl;
import com.example.test.testingHMS.pharmacist.serviceImpl.MedicineProcurementServiceImpl;
import com.example.test.testingHMS.pharmacist.serviceImpl.MedicineQuantityServiceImpl;
import com.example.test.testingHMS.pharmacist.serviceImpl.SalesPaymentPdfServiceImpl;
import com.example.test.testingHMS.pharmacist.serviceImpl.SalesServiceImpl;
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
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;

@CrossOrigin(origins = "*", maxAge = 36000)
@RestController
@RequestMapping("/v1")
@Component
public class PdfGenerator {
	private static final String TYPE_OF_CHARGE_SETTLED_AMOUNT = "SETTLED AMOUNT";
	private static final String COLON = ":";
	private static final String EMPTY_STRING = "";

	@Value("${hospital.logo}")
	private Resource hospitalLogo;
	
	@Autowired
	SalesRepository salesRepository;

	@Autowired
	LaboratoryRegistrationServiceImpl laboratoryRegistrationServiceImpl;

	@Autowired
	LaboratoryRegistrationRepository laboratoryRegistrationRepository;

	@Autowired
	SalesReturnRepository salesReturnRepository;

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
	PaymentPdfServiceImpl paymentPdfServiceImpl;

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
	CashPlusCardRepository cashPlusCardRepository;

	@Autowired
	MedicineQuantityRepository medicineQuantityRepository;

	@Autowired
	FinalBillingRepository finalBillingRepository;

	/**
	 * This method regenerate all the sales pdf for patient
	 * 
	 * @param regIde
	 * @param principal
	 */
	public void salesPdfGenerator(@PathVariable String regIde, Principal principal) {

		String regId = null;
		SalesPaymentPdf salesPaymentPdf = null;
		String billNo = "";
		String patientName = "";
		String umr = "";
		String expdate = null;
		String refNo = null;

		PatientRegistration patientRegistrationFirst = patientRegistrationServiceImpl.findByRegId(regIde);

		List<String> findDistinctBill = salesRepository.findDistinctBill(regIde);

		for (String distinctBill : findDistinctBill) {

			List<Sales> salesList = salesRepository.findByBillNoAndPatientRegistration(distinctBill,
					patientRegistrationFirst);

			Sales salesFor = salesList.get(0);

			if (salesFor.getPaymentType() == null) {
				throw new RuntimeException(ConstantValues.ENTER_PAYMENT_TYPE_ERROR_MSG);
			}

			String paymentType = salesFor.getPaymentType();

			if (regIde != null) {

				umr = patientRegistrationFirst.getPatientDetails().getUmr();

				if (patientRegistrationFirst.getPatientDetails().getMiddleName() != null) {
					patientName = patientRegistrationFirst.getPatientDetails().getTitle() + ". "
							+ patientRegistrationFirst.getPatientDetails().getFirstName() + " "
							+ patientRegistrationFirst.getPatientDetails().getMiddleName() + " "
							+ patientRegistrationFirst.getPatientDetails().getLastName();

				} else {
					patientName = patientRegistrationFirst.getPatientDetails().getTitle() + ". "
							+ patientRegistrationFirst.getPatientDetails().getFirstName() + " "
							+ patientRegistrationFirst.getPatientDetails().getLastName();

				}
			}

			// CreatedBy (Security)
			User userSecurity = salesFor.getPatientSalesUser();
			String createdBy = userSecurity.getFirstName() + " " + userSecurity.getLastName();

			refNo = (salesFor.getReferenceNumber() != null) ? salesFor.getReferenceNumber() : "";

			float total = 0;
			float cardAmount = 0;
			float cashAmount = 0;
			ArrayList<RefSales> refSales = new ArrayList<>();
			for (Sales salesInfo : salesList) {
				RefSales refSalesValue = new RefSales();
				float amount = salesInfo.getAmount();
				long quantity = salesInfo.getQuantity();
				if (salesInfo.getAmount() != salesInfo.getActualAmount()) {
					List<SalesReturn> listOfReturnedMedicines = salesReturnRepository
							.findByBillNoAndMedicineName(distinctBill, salesInfo.getMedicineName());
					for (SalesReturn listOfReturns : listOfReturnedMedicines) {
						quantity += listOfReturns.getQuantity();
						amount += listOfReturns.getAmount();
					}
				}
				refSalesValue.setAmount(amount);
				refSalesValue.setBatchNo(salesInfo.getBatchNo());
				refSalesValue.setDiscount(salesInfo.getDiscount());
				refSalesValue.setExpDate(salesInfo.getExpireDate());
				refSalesValue.setGst(salesInfo.getGst());
				refSalesValue.setMedicineName(salesInfo.getMedicineName());
				refSalesValue.setMrp(salesInfo.getMrp());
				refSalesValue.setPaymentType(salesInfo.getPaymentType());
				refSalesValue.setQuantity(quantity);

				total += salesInfo.getActualAmount();

				if (salesInfo.getPaymentType().equalsIgnoreCase("CASH+CARD")) {
					CashPlusCard cashPlusCardValue = cashPlusCardRepository.findByBillNoAndDescription(distinctBill,
							"Sales");
					cardAmount = cashPlusCardValue.getCardAmount();
					cashAmount = cashPlusCardValue.getCashAmount();
				}

				refSales.add(refSalesValue);
			}
			String roundOff = null;

			FinalBilling finalsales = finalBillingRepository.findByBillTypeAndBillNoAndRegNo("Sales", distinctBill,
					regId);

			float finalCash = finalsales.getCashAmount();
			float finalCard = finalsales.getCardAmount();
			float finalDue = finalsales.getDueAmount();

			/*
			 * String myAd = "     Plot No14,15,16 & 17,Nandi Co-op.Society," +
			 * "\n                                   Main Road, Beside Navya Grand Hotel, \n                                Miyapur,Hyderabad-49,Phone:040-23046789   \n                               "
			 * + "   For Appointment Contact:8019114481   " +
			 * "\n                                   Email :udbhavahospitals@gmail.com ";
			 */
			if (patientRegistrationFirst != null) {
				if (!patientRegistrationFirst.getpType().equals(ConstantValues.INPATIENT)) {
					byte[] pdfByte = null;
					ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

					Document document = new Document(PageSize.A4_LANDSCAPE);
					try {

						Resource fileResourcee = resourceLoader.getResource(
								ConstantValues.IMAGE_PNG_CLASSPATH);
						Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
						Font redFonts = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.NORMAL);
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

						hcell1 = new PdfPCell(new Phrase(salesFor.getBillNo(), redFont));
						hcell1.setBorder(Rectangle.NO_BORDER);
						hcell1.setPaddingLeft(-25f);
						table2.addCell(hcell1);

						// Display a date in day, month, year format
						Date date = salesFor.getBillDate();
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

						hcel = new PdfPCell(new Phrase(patientRegistrationFirst.getPatientDetails().getUmr(), redFont));
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

						hcel11 = new PdfPCell(new Phrase(patientRegistrationFirst.getRegId(), redFont));
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
								new Phrase(patientRegistrationFirst.getPatientDetails().getConsultant(), redFont));
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

							MedicineDetails medicineDetails1 = medicineDetailsServiceImpl
									.findByName(a.getMedicineName());
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

						hcell2 = new PdfPCell(
								new Phrase(numberToWordsConverter.convert(ttl) + " Rupees Only", redFont));
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

						hcell9 = new PdfPCell(new Phrase(String.valueOf(Math.round(total)), redFont));
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
						hcell34 = new PdfPCell(new Phrase(salesFor.getPaymentType(), redFont2));
						hcell34.setBorder(Rectangle.NO_BORDER);
						hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell34.setPaddingLeft(10f);
						table13.addCell(hcell34);

						hcell34 = new PdfPCell(new Phrase(String.valueOf(Math.round(total)), redFont2));
						hcell34.setBorder(Rectangle.NO_BORDER);
						hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell34.setPaddingLeft(35f);
						table13.addCell(hcell34);
						if (salesFor.getPaymentType().equalsIgnoreCase("card")
								|| salesFor.getPaymentType().equalsIgnoreCase("cash+card")) {
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
								.findByShopLocation(salesFor.getPatientSaleslocation().getLocationName());

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
						 salesPaymentPdf = salesPaymentPdfServiceImpl
								.getSalesPdf(distinctBill + "-" + regIde + " Medicine Sales");
						if (salesPaymentPdf != null) {
							salesPaymentPdf.setData(pdfByte);
							salesPaymentPdfServiceImpl.save(salesPaymentPdf);

						} else {
							salesPaymentPdf = new SalesPaymentPdf();
							salesPaymentPdf.setFileName(distinctBill + "-" + regIde + " Medicine Sales");
							salesPaymentPdf.setFileuri(uri);
							salesPaymentPdf.setPid(salesPaymentPdfServiceImpl.getNextId());
							salesPaymentPdf.setData(pdfByte);
							salesPaymentPdfServiceImpl.save(salesPaymentPdf);

						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				else if (patientRegistrationFirst.getpType().equals(ConstantValues.INPATIENT)
						&& salesFor.getPaymentType().equalsIgnoreCase("Advance")) {
					byte[] pdfByte = null;
					ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

					Document document = new Document(PageSize.A4_LANDSCAPE);
					try {

						Resource fileResourcee = resourceLoader.getResource(

ConstantValues.IMAGE_PNG_CLASSPATH);
						Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
						Font redFonts = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.NORMAL);
						Font redFont2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
						Font redFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
						Font redFont3 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
						PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);

						document.open();
						PdfPTable table = new PdfPTable(2);

						Image img = Image.getInstance(hospitalLogo.getURL());
						// img.setWidthPercentage(20);
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

						hcell1 = new PdfPCell(new Phrase(salesFor.getBillNo(), redFont));
						hcell1.setBorder(Rectangle.NO_BORDER);
						hcell1.setPaddingLeft(-25f);
						table2.addCell(hcell1);

						// Display a date in day, month, year format
						Date date = salesFor.getBillDate();
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

						hcel = new PdfPCell(new Phrase(patientRegistrationFirst.getPatientDetails().getUmr(), redFont));
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

						hcel11 = new PdfPCell(new Phrase(patientRegistrationFirst.getRegId(), redFont));
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
								new Phrase(patientRegistrationFirst.getPatientDetails().getConsultant(), redFont));
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

							MedicineDetails medicineDetails1 = medicineDetailsServiceImpl
									.findByName(a.getMedicineName());
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

						hcell2 = new PdfPCell(
								new Phrase(numberToWordsConverter.convert(ttl) + " Rupees Only", redFont));
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

						hcell9 = new PdfPCell(new Phrase(String.valueOf(Math.round(total)), redFont));
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
						hcell34 = new PdfPCell(new Phrase(salesFor.getPaymentType(), redFont2));
						hcell34.setBorder(Rectangle.NO_BORDER);
						hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell34.setPaddingLeft(10f);
						table13.addCell(hcell34);

						hcell34 = new PdfPCell(new Phrase(String.valueOf(Math.round(total)), redFont2));
						hcell34.setBorder(Rectangle.NO_BORDER);
						hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell34.setPaddingLeft(35f);
						table13.addCell(hcell34);

						if (salesFor.getPaymentType().equalsIgnoreCase("card")
								|| salesFor.getPaymentType().equalsIgnoreCase("cash+card")) {
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
								.findByShopLocation(salesFor.getPatientSaleslocation().getLocationName());

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

						 salesPaymentPdf = salesPaymentPdfServiceImpl
								.getSalesPdf(distinctBill + "-" + regIde + " Medicine Sales");
						if (salesPaymentPdf != null) {
							salesPaymentPdf.setData(pdfByte);
							salesPaymentPdfServiceImpl.save(salesPaymentPdf);

						} else {
							salesPaymentPdf = new SalesPaymentPdf();
							salesPaymentPdf.setFileName(distinctBill + "-" + regIde + " Medicine Sales");
							salesPaymentPdf.setFileuri(uri);
							salesPaymentPdf.setPid(salesPaymentPdfServiceImpl.getNextId());
							salesPaymentPdf.setData(pdfByte);
							salesPaymentPdfServiceImpl.save(salesPaymentPdf);

						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				else if (patientRegistrationFirst.getpType().equals(ConstantValues.INPATIENT)
						&& !salesFor.getPaymentType().equalsIgnoreCase("Advance")) {
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

						hcell1 = new PdfPCell(new Phrase(salesFor.getBillNo(), redFont));
						hcell1.setBorder(Rectangle.NO_BORDER);
						hcell1.setPaddingLeft(-25f);
						table2.addCell(hcell1);

						// Display a date in day, month, year format
						Date date = salesFor.getBillDate();
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

						hcel = new PdfPCell(new Phrase(patientRegistrationFirst.getPatientDetails().getUmr(), redFont));
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

						hcel11 = new PdfPCell(new Phrase(patientRegistrationFirst.getRegId(), redFont));
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
								new Phrase(patientRegistrationFirst.getPatientDetails().getConsultant(), redFont));
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

						if (salesFor.getPaymentType().equalsIgnoreCase(ConstantValues.DUE)) {
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

							MedicineDetails medicineDetails1 = medicineDetailsServiceImpl
									.findByName(a.getMedicineName());
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

						hcell2 = new PdfPCell(
								new Phrase(numberToWordsConverter.convert(ttl) + " Rupees Only", redFont));
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

						hcell9 = new PdfPCell(new Phrase(String.valueOf(Math.round(total)), redFont));
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
						hcell34 = new PdfPCell(new Phrase(salesFor.getPaymentType(), redFont2));
						hcell34.setBorder(Rectangle.NO_BORDER);
						hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell34.setPaddingLeft(10f);
						table13.addCell(hcell34);

						hcell34 = new PdfPCell(new Phrase(String.valueOf(Math.round(total)), redFont2));
						hcell34.setBorder(Rectangle.NO_BORDER);
						hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell34.setPaddingLeft(35f);
						table13.addCell(hcell34);

						if (salesFor.getPaymentType().equalsIgnoreCase("card")
								|| salesFor.getPaymentType().equalsIgnoreCase("cash+card")) {
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
								.findByShopLocation(salesFor.getPatientSaleslocation().getLocationName());

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
						System.out.println("finished" + patientName + "m");
						pdfByte = byteArrayOutputStream.toByteArray();
						String uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/sales/viewFile/")
								.path(salesPaymentPdfServiceImpl.getNextId()).toUriString();

						 salesPaymentPdf = salesPaymentPdfServiceImpl
								.getSalesPdf(distinctBill + "-" + regIde + " Medicine Sales");
						if (salesPaymentPdf != null) {
							salesPaymentPdf.setData(pdfByte);
							salesPaymentPdfServiceImpl.save(salesPaymentPdf);

						} else {
							salesPaymentPdf = new SalesPaymentPdf();
							salesPaymentPdf.setFileName(distinctBill + "-" + regIde + " Medicine Sales");
							salesPaymentPdf.setFileuri(uri);
							salesPaymentPdf.setPid(salesPaymentPdfServiceImpl.getNextId());
							salesPaymentPdf.setData(pdfByte);
							salesPaymentPdfServiceImpl.save(salesPaymentPdf);

						}

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

					Resource fileResourcee = resourceLoader.getResource(
							ConstantValues.IMAGE_PNG_CLASSPATH);

					Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
					Font redFonts = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.NORMAL);
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

					hcell1 = new PdfPCell(new Phrase(salesFor.getBillNo(), redFont));
					hcell1.setBorder(Rectangle.NO_BORDER);
					hcell1.setPaddingLeft(-25f);
					table2.addCell(hcell1);

					// Display a date in day, month, year format
					Date date = salesFor.getBillDate();
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

					hcell18 = new PdfPCell(new Phrase(salesFor.getName(), redFont));
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

					hcell9 = new PdfPCell(new Phrase(String.valueOf(Math.round(total)), redFont));
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
					hcell34 = new PdfPCell(new Phrase(salesFor.getPaymentType(), redFont2));
					hcell34.setBorder(Rectangle.NO_BORDER);
					hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell34.setPaddingLeft(10f);
					table13.addCell(hcell34);

					hcell34 = new PdfPCell(new Phrase(String.valueOf(Math.round(total)), redFont2));
					hcell34.setBorder(Rectangle.NO_BORDER);
					hcell34.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell34.setPaddingLeft(35f);
					table13.addCell(hcell34);

					if (salesFor.getPaymentType().equalsIgnoreCase("card")
							|| salesFor.getPaymentType().equalsIgnoreCase("cash+card")) {
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

					// locationServiceImpl.findByLocationName(salesFor.getPatientSaleslocation())
					PharmacyShopDetails pharmacyShopDetails = pharmacyShopDetailsRepository
							.findByShopLocation(salesFor.getPatientSaleslocation().getLocationName());

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
					System.out.println("finished" + patientName + "m");
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
		}

	}

//	@RequestMapping(value="/labPdf/{regIde}")
	public List<String> labPdfGenerate(String regIde) {

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
		String patientName = null;

		String refNo = null;
		String firstName = null;
		String lastName = null;
		String bill = null;

		List<String> distinctBill = laboratoryRegistrationRepository.findDistinctBill(regIde);
		List<String> listOfUrls = new ArrayList<>();

		for (String oneBill : distinctBill) {

			PatientRegistration patientRegistration = patientRegistrationServiceImpl.findByRegId(regIde);

			List<LaboratoryRegistration> laboratoryRegistrationList = laboratoryRegistrationRepository
					.findByLaboratoryPatientRegistrationAndBillNo(patientRegistration, oneBill);

			LaboratoryRegistration laboratoryRegistration = laboratoryRegistrationList.get(0);
			bill = laboratoryRegistration.getBillNo();

			// createdBy (Security)
			User userSecurity = userServiceImpl
					.findByUserName(laboratoryRegistration.getUserLaboratoryRegistration().getUserName());
			String createdBy = userSecurity.getFirstName() + " " + userSecurity.getLastName();

			String paymentType = laboratoryRegistration.getPaymentType();

			if (paymentType.equalsIgnoreCase("Card") || paymentType.equalsIgnoreCase("Credit Card")
					|| paymentType.equalsIgnoreCase("Debit Card") || paymentType.equalsIgnoreCase("Cash+Card")) {

				refNo = laboratoryRegistration.getReferenceNumber();
			}

			float cardAmount = 0;
			float cashAmount = 0;
			if (paymentType.equalsIgnoreCase("Cash+Card")) {
				CashPlusCard cashPlusCardLab = cashPlusCardRepository
						.findByBillNoAndDescription(laboratoryRegistration.getBillNo(), "Lab");
				cardAmount = cashPlusCardLab.getCardAmount();
				cashAmount = cashPlusCardLab.getCashAmount();
			}

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
			regId = laboratoryRegistration.getReg_id();
			mobile = patientRegistration.getPatientDetails().getMobile();
			docName = patientRegistration.getPatientDetails().getConsultant();
			docNameList = docName.split(" ");
			User user = userServiceImpl.findOneByUserId(patientRegistration.getVuserD().getUserId());

			List<RefLaboratoryRegistration> refLaboratoryRegistrations = new ArrayList<RefLaboratoryRegistration>();

			for (LaboratoryRegistration refLab : laboratoryRegistrationList) {
				RefLaboratoryRegistration refLaboratoryRegistrationEdit = new RefLaboratoryRegistration();
				refLaboratoryRegistrationEdit.setServiceName(refLab.getServiceName());
				refLaboratoryRegistrationEdit.setQuantity(refLab.getQuantity());
				refLaboratoryRegistrationEdit.setDiscount(refLab.getDiscount());
				refLaboratoryRegistrationEdit.setAmount(refLab.getPrice());

				refLaboratoryRegistrations.add(refLaboratoryRegistrationEdit);

			}

			paymentMode = laboratoryRegistration.getPaymentType();

			if (patientRegistration.getpType().equalsIgnoreCase(ConstantValues.INPATIENT)
					|| patientRegistration.getpType().equalsIgnoreCase(ConstantValues.DAYCARE)
					|| patientRegistration.getpType().equalsIgnoreCase(ConstantValues.EMERGENCY)) {
				List<RoomBookingDetails> roomBookingDetails = patientRegistration.getRoomBookingDetails();
				roomType = roomBookingDetails.get(0).getRoomDetails().getRoomType();
			} else {
				roomType = "NA";
			}

			for (RefLaboratoryRegistration refLaboratoryRegistrationList : refLaboratoryRegistrations) {

				// Discount Number
				amount = refLaboratoryRegistrationList.getAmount(); // total amount
				netAmount = refLaboratoryRegistrationList.getAmount() * refLaboratoryRegistrationList.getQuantity();
				discount = refLaboratoryRegistrationList.getDiscount(); // discount
				netAmount = netAmount - (discount); // total amount after applying discount

				paymentMode = laboratoryRegistration.getPaymentType();

			}

			FinalBilling finallab = finalBillingRepository.findByBillTypeAndBillNoAndRegNo("Laboratory Registration",
					laboratoryRegistration.getBillNo(), regIde);

			float finalCard = finallab.getCardAmount();
			float finalCash = finallab.getCashAmount();
			float finalDue = finallab.getDueAmount();

			// for other receipts leaving service slip
			/*
			 * String addr="        Plot No14,15,16 & 17,Nandi Co-op.Society," +
			 * "\n                                   Main Road, Beside Navya Grand Hotel, \n                                Miyapur,Hyderabad-49,Phone:040-23046789   \n                               "
			 * + "   For Appointment Contact:8019114481   " +
			 * "\n                                   Email :udbhavahospitals@gmail.com ";
			 */

			float totalAmount = 0;

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

					Resource fileResource = resourceLoader.getResource(
							ConstantValues.IMAGE_PNG_CLASSPATH);

					PdfPCell cell2 = new PdfPCell();

					PdfPTable table = new PdfPTable(2);

					Image img = Image.getInstance(hospitalLogo.getURL());
					img.scaleAbsolute(ConstantValues.IMAGE_ABSOLUTE_INTIAL_POSITION, ConstantValues.IMAGE_ABSOLUTE_FINAL_POSITION);
					table.setWidthPercentage(ConstantValues.TABLE_SET_WIDTH_PERECENTAGE);

					Phrase pq = new Phrase(new Chunk(img, ConstantValues.IMAGE_SET_INTIAL_POSITION, ConstantValues.IMAGE_SET_FINAL_POSITION));
                 	document.add(pq);

					Paragraph p51 = new Paragraph("UDBHAVA HOSPITALS", headFont);
					p51.setAlignment(Element.ALIGN_CENTER);

					document.add(p51);

					String newAddress = "        Plot No.14,15,16 &17,Nandi Co-op. Society,     \n                         Main Road,Beside Navya Grand Hotel,Miyapur,Hyderabad,TS                       \n          Phone:040-23046789 | Appointment Contact: 8019114481   \n    Email : udbhavahospitals@gmail.com";

					Paragraph p52 = new Paragraph(newAddress, headFont1);
					p52.setAlignment(Element.ALIGN_CENTER);
					document.add(p52);

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

					 patientPaymentPdf = paymentPdfServiceImpl.findByRegIdAndBillNo(regIde, oneBill);
					if (patientPaymentPdf != null) {
						patientPaymentPdf.setData(pdfBytes);
						paymentPdfServiceImpl.save(patientPaymentPdf);
						listOfUrls.add(patientPaymentPdf.getFileuri());
					} else {
						patientPaymentPdf = new PatientPaymentPdf(regIde + " Lab Registration", uri, regId, pdfBytes,
								bill);
						patientPaymentPdf.setPid(paymentPdfServiceImpl.getNextPdfId());
						paymentPdfServiceImpl.save(patientPaymentPdf);
						listOfUrls.add(patientPaymentPdf.getFileuri());
					}
				} catch (Exception e) {

				}
			}

			else if (patientRegistration.getpType().equals("INPATIENT")
					&& laboratoryRegistration.getPaymentType().equalsIgnoreCase("Due")
					&& !laboratoryRegistration.getPaymentType().equalsIgnoreCase("Insurance")
					&& !laboratoryRegistration.getPaymentType().equalsIgnoreCase("Paid In KPHB")) {

				List<LaboratoryRegistration> laboratoryRegistrationInfor = laboratoryRegistrationServiceImpl.findBill(
						laboratoryRegistration.getLaboratoryPatientRegistration().getRegId(),
						laboratoryRegistration.getInvoiceNo());

				patientPaymentPdf = null;
				byte[] pdfBytes = null;
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

				Document document = new Document(PageSize.A4_LANDSCAPE);

				try {

					Resource fileResource = resourceLoader.getResource(
							ConstantValues.IMAGE_PNG_CLASSPATH);
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
					Timestamp dateInfo = laboratoryRegistration.getEnteredDate();

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

					hcell91 = new PdfPCell(
							new Phrase(patientRegistration.getPatientDetails().getConsultant(), redFont));
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

						cell = new PdfPCell(
								new Phrase(String.valueOf(laboratoryRegistrationInfo.getQuantity()), redFont));
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

						cell = new PdfPCell(
								new Phrase(String.valueOf(laboratoryRegistrationInfo.getDiscount()), redFont));
						cell.setBorder(Rectangle.NO_BORDER);
						cell.setPaddingLeft(5);
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table1.addCell(cell);

						cell = new PdfPCell(
								new Phrase(String.valueOf(laboratoryRegistrationInfo.getNetAmount()), redFont));
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

					hcell56 = new PdfPCell(new Phrase(String.valueOf(total), redFont));
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

					hcell57 = new PdfPCell(new Phrase(String.valueOf(total), redFont));
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

					hcell58 = new PdfPCell(new Phrase(String.valueOf(total), redFont));
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

					hcell59 = new PdfPCell(
							new Phrase("(" + numberToWordsConverter.convert((long) total) + ")", redFont));
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

					hcell60 = new PdfPCell(
							new Phrase("(" + numberToWordsConverter.convert((long) total) + ")", redFont));
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

					 patientPaymentPdf = paymentPdfServiceImpl.findByRegIdAndBillNo(regIde, oneBill);
					if (patientPaymentPdf != null) {
						patientPaymentPdf.setData(pdfBytes);
						paymentPdfServiceImpl.save(patientPaymentPdf);
						listOfUrls.add(patientPaymentPdf.getFileuri());
					} else {
						patientPaymentPdf = new PatientPaymentPdf(regIde + " Lab Registration", uri, regId, pdfBytes,
								bill);
						patientPaymentPdf.setPid(paymentPdfServiceImpl.getNextPdfId());
						paymentPdfServiceImpl.save(patientPaymentPdf);
						listOfUrls.add(patientPaymentPdf.getFileuri());
					}
				} catch (Exception e) {

				}

			} else if (patientRegistration.getpType().equals("INPATIENT")
					&& !laboratoryRegistration.getPaymentType().equalsIgnoreCase("Insurance")
					&& !laboratoryRegistration.getPaymentType().equalsIgnoreCase("Paid In KPHB")) {

				List<LaboratoryRegistration> laboratoryRegistrationInfor = laboratoryRegistrationServiceImpl.findBill(
						laboratoryRegistration.getLaboratoryPatientRegistration().getRegId(),
						laboratoryRegistration.getInvoiceNo());

				patientPaymentPdf = null;
				byte[] pdfBytes = null;
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

				Document document = new Document(PageSize.A4_LANDSCAPE);

				try {

					Resource fileResource = resourceLoader.getResource(
							ConstantValues.IMAGE_PNG_CLASSPATH);
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
					Timestamp dateInfo = laboratoryRegistration.getEnteredDate();
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

					hcell91 = new PdfPCell(
							new Phrase(patientRegistration.getPatientDetails().getConsultant(), redFont));
					hcell91.setBorder(Rectangle.NO_BORDER);
					hcell91.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell91.setPaddingTop(-5f);
					hcell91.setPaddingLeft(-85f);
					table98.addCell(hcell91);

					cell3.addElement(table98);

					PdfPTable table97 = new PdfPTable(1);
					table97.setWidths(new float[] { 5f });
					table97.setSpacingBefore(10);

					PdfPCell hcell97;
					hcell97 = new PdfPCell(new Phrase(
							"*" + "OBN0094995" + "*" + "  " + "==> Scan This BarCode To Take Report At KIOSK",
							headFont1));
					hcell97.setBorder(Rectangle.NO_BORDER);
					// hcell3.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell97.setPaddingBottom(-10f);
					hcell97.setPaddingLeft(-35f);

					table97.addCell(hcell97);
					cell3.addElement(table97);

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

						cell = new PdfPCell(
								new Phrase(String.valueOf(laboratoryRegistrationInfo.getQuantity()), redFont));
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

						cell = new PdfPCell(
								new Phrase(String.valueOf(laboratoryRegistrationInfo.getDiscount()), redFont));
						cell.setBorder(Rectangle.NO_BORDER);
						cell.setPaddingLeft(5);
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table1.addCell(cell);

						cell = new PdfPCell(
								new Phrase(String.valueOf(laboratoryRegistrationInfo.getNetAmount()), redFont));
						cell.setBorder(Rectangle.NO_BORDER);
						cell.setPaddingLeft(5);
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table1.addCell(cell);

						total += laboratoryRegistrationInfo.getNetAmount();

					}
					total = Math.round(total);

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

					hcell56 = new PdfPCell(new Phrase(String.valueOf(total), redFont));
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

					hcell58 = new PdfPCell(new Phrase(String.valueOf(total), redFont));
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

					hcell60 = new PdfPCell(new Phrase("(" + numberToWordsConverter.convert((long) total) + ")", redFont));
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
					 patientPaymentPdf = paymentPdfServiceImpl.findByRegIdAndBillNo(regIde, oneBill);
					if (patientPaymentPdf != null) {
						patientPaymentPdf.setData(pdfBytes);
						paymentPdfServiceImpl.save(patientPaymentPdf);
						listOfUrls.add(patientPaymentPdf.getFileuri());
					} else {
						patientPaymentPdf = new PatientPaymentPdf(regIde + " Lab Registration", uri, regId, pdfBytes,
								bill);
						patientPaymentPdf.setPid(paymentPdfServiceImpl.getNextPdfId());
						paymentPdfServiceImpl.save(patientPaymentPdf);
						listOfUrls.add(patientPaymentPdf.getFileuri());
					}
				} catch (Exception e) {

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
					Timestamp dateInfo = laboratoryRegistration.getEnteredDate();
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

					hcell91 = new PdfPCell(
							new Phrase(patientRegistration.getPatientDetails().getConsultant(), redFont));
					hcell91.setBorder(Rectangle.NO_BORDER);
					hcell91.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell91.setPaddingTop(-5f);
					hcell91.setPaddingLeft(-85f);
					table98.addCell(hcell91);

					cell3.addElement(table98);

					PdfPTable table97 = new PdfPTable(1);
					table97.setWidths(new float[] { 5f });
					table97.setSpacingBefore(10);
					PdfPCell hcell97;
					hcell97 = new PdfPCell(new Phrase(
							"*" + "OBN0094995" + "*" + "  " + "==> Scan This BarCode To Take Report At KIOSK",
							headFont1));
					hcell97.setBorder(Rectangle.NO_BORDER);
					// hcell3.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell97.setPaddingBottom(-10f);
					hcell97.setPaddingLeft(-35f);
					table97.addCell(hcell97);
					cell3.addElement(table97);

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

						cell = new PdfPCell(
								new Phrase(String.valueOf(laboratoryRegistrationInfo.getQuantity()), redFont));
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

						cell = new PdfPCell(
								new Phrase(String.valueOf(laboratoryRegistrationInfo.getDiscount()), redFont));
						cell.setBorder(Rectangle.NO_BORDER);
						cell.setPaddingLeft(5);
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table1.addCell(cell);

						cell = new PdfPCell(
								new Phrase(String.valueOf(laboratoryRegistrationInfo.getNetAmount()), redFont));
						cell.setBorder(Rectangle.NO_BORDER);
						cell.setPaddingLeft(5);
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table1.addCell(cell);

						total += laboratoryRegistrationInfo.getNetAmount();

					}
					total = Math.round(total);
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

					hcell56 = new PdfPCell(new Phrase(String.valueOf(total), redFont));
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

					hcell57 = new PdfPCell(new Phrase(String.valueOf(total), redFont));
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

					hcell58 = new PdfPCell(new Phrase(String.valueOf(total), redFont));
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

					hcell59 = new PdfPCell(
							new Phrase("(" + numberToWordsConverter.convert((long) total) + ")", redFont));
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

					hcell60 = new PdfPCell(
							new Phrase("(" + numberToWordsConverter.convert((long) total) + ")", redFont));
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

					 patientPaymentPdf = paymentPdfServiceImpl.findByRegIdAndBillNo(regIde, oneBill);
					if (patientPaymentPdf != null) {
						patientPaymentPdf.setData(pdfBytes);
						paymentPdfServiceImpl.save(patientPaymentPdf);
						listOfUrls.add(patientPaymentPdf.getFileuri());
					} else {
						patientPaymentPdf = new PatientPaymentPdf(regIde + " Lab Registration", uri, regId, pdfBytes,
								bill);
						patientPaymentPdf.setPid(paymentPdfServiceImpl.getNextPdfId());
						paymentPdfServiceImpl.save(patientPaymentPdf);
						listOfUrls.add(patientPaymentPdf.getFileuri());
					}
				} catch (Exception e) {

				}

			} else {

				List<LaboratoryRegistration> laboratoryRegistrationInfor = laboratoryRegistrationServiceImpl
						.findBill(laboratoryRegistration.getReg_id(), laboratoryRegistration.getInvoiceNo());

				patientPaymentPdf = null;
				byte[] pdfBytes = null;
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

				Document document = new Document(PageSize.A4_LANDSCAPE);

				try {

					Resource fileResource = resourceLoader.getResource(
							ConstantValues.IMAGE_PNG_CLASSPATH);
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
					Timestamp dateInfo = laboratoryRegistration.getEnteredDate();
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

					hcell91 = new PdfPCell(
							new Phrase(patientRegistration.getPatientDetails().getConsultant(), redFont));
					hcell91.setBorder(Rectangle.NO_BORDER);
					hcell91.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell91.setPaddingTop(-5f);
					hcell91.setPaddingLeft(-85f);
					table98.addCell(hcell91);

					cell3.addElement(table98);

					PdfPTable table97 = new PdfPTable(1);
					table97.setWidths(new float[] { 5f });
					table97.setSpacingBefore(10);

					PdfPCell hcell97;
					hcell97 = new PdfPCell(new Phrase(
							"*" + "OBN0094995" + "*" + "  " + "==> Scan This BarCode To Take Report At KIOSK",
							headFont1));
					hcell97.setBorder(Rectangle.NO_BORDER);
					// hcell3.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell97.setPaddingBottom(-10f);
					hcell97.setPaddingLeft(-35f);

					table97.addCell(hcell97);
					cell3.addElement(table97);

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

						cell = new PdfPCell(
								new Phrase(String.valueOf(laboratoryRegistrationInfo.getQuantity()), redFont));
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

						cell = new PdfPCell(
								new Phrase(String.valueOf(laboratoryRegistrationInfo.getDiscount()), redFont));
						cell.setBorder(Rectangle.NO_BORDER);
						cell.setPaddingLeft(5);
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table1.addCell(cell);

						cell = new PdfPCell(
								new Phrase(String.valueOf(laboratoryRegistrationInfo.getNetAmount()), redFont));
						cell.setBorder(Rectangle.NO_BORDER);
						cell.setPaddingLeft(5);
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table1.addCell(cell);

						total += laboratoryRegistrationInfo.getNetAmount();

					}
					total = Math.round(total);

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

					hcell56 = new PdfPCell(new Phrase(String.valueOf(total), redFont));
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

					hcell58 = new PdfPCell(new Phrase(String.valueOf(total), redFont));
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

					hcell60 = new PdfPCell(new Phrase("(" + numberToWordsConverter.convert((long) total) + ")", redFont));
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

					 patientPaymentPdf = paymentPdfServiceImpl.findByRegIdAndBillNo(regIde, oneBill);
					if (patientPaymentPdf != null) {
						patientPaymentPdf.setData(pdfBytes);
						paymentPdfServiceImpl.save(patientPaymentPdf);
						listOfUrls.add(patientPaymentPdf.getFileuri());
					} else {
						patientPaymentPdf = new PatientPaymentPdf(regIde + " Lab Registration", uri, regId, pdfBytes,
								bill);
						patientPaymentPdf.setPid(paymentPdfServiceImpl.getNextPdfId());
						paymentPdfServiceImpl.save(patientPaymentPdf);
						listOfUrls.add(patientPaymentPdf.getFileuri());
					}
				} catch (Exception e) {

				}

			}

		}
		return listOfUrls;

	}

	// @RequestMapping(value = "/ipfinalpdf/{regIde}")
	public String ipFinalPdfGenerator(@PathVariable("regIde") String regIde, Principal principal) {
		String EMPTY_SPACE = " ";
		String consultant = null;
		String refBy = null;
		String paid = null;
		String paymentType = null;
		float totaPaidAmount = 0;
		float amount = 0;
		float totalRecieptAmt = 0;
		float salesnetAmount = 0;
		long mobile = 0;
		float settledAmt = 0;
		float advanceAmt = 0;

		// for finding net,paid and due amount
		float paidCash = 0;
		float paidCard = 0;
		float paidCheque = 0;
		float paidDue = 0;
		float totalnewNetAmt = 0;
		float returnAmount = 0;
		float discAmount = 0;
		float returnAmt = 0;
		List<FinalBilling> finalBillingAmount = finalBillingServcieImpl.findByRegNo(regIde);
		for (FinalBilling finalBillingAmountInfo : finalBillingAmount) {
			String billType = finalBillingAmountInfo.getBillType();
			System.out.println(billType);
			if (billType.equalsIgnoreCase("Sales") || billType.equalsIgnoreCase("Laboratory Registration")) {
				totalnewNetAmt += finalBillingAmountInfo.getFinalAmountPaid();
				paidCash += finalBillingAmountInfo.getCashAmount();
				paidCard += finalBillingAmountInfo.getCardAmount();
				paidCheque += finalBillingAmountInfo.getChequeAmount();
			}

			if (billType.equalsIgnoreCase("Sales Return")) {
				returnAmt += (finalBillingAmountInfo.getCashAmount() + finalBillingAmountInfo.getCardAmount()
						+ finalBillingAmountInfo.getChequeAmount());
			}
		}

		totaPaidAmount = paidCash + paidCard + paidCheque - returnAmt;
		System.out.println(totaPaidAmount);
		System.out.println(totalnewNetAmt);

		// createdBy Security
		User userSecurity = userServiceImpl.findByUserName(principal.getName());
		PatientRegistration patientRegistration = patientRegistrationServiceImpl.findByRegId(regIde);
		mobile = patientRegistration.getPatientDetails().getMobile();
		List<ChargeBill> chargeBillListq = chargeBillServiceImpl.findByPatRegId(patientRegistration);
		ChargeBill chargeFor = chargeBillListq.get(0);
		Date date = chargeFor.getDichargedDate();
		DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
		String today = formatter.format(date).toString();

		// udser details
		String createdBy = chargeFor.getUpdatedBy();
		User userInfo = userServiceImpl.findOneByUserId(createdBy);
		String createdName = null;
		createdName = (userInfo.getMiddleName() != null)
				? (userInfo.getFirstName() + EMPTY_SPACE + userInfo.getMiddleName() + EMPTY_SPACE
						+ userInfo.getLastName())
				: (userInfo.getFirstName() + EMPTY_SPACE + userInfo.getLastName());

		// for patientdetails
		String regNo = regIde;
		PatientDetails patientDetails = patientRegistration.getPatientDetails();
		String name = patientDetails.getTitle() + patientDetails.getFirstName() + patientDetails.getLastName();
		String umr = patientDetails.getUmr();
		consultant = patientDetails.getConsultant();
		long refByMobile = 0;
		if (patientDetails.getvRefferalDetails() != null) {
			refBy = patientDetails.getvRefferalDetails().getRefName();
			refByMobile = patientDetails.getvRefferalDetails().getRefPhone();
		} else {
			refBy = ConstantValues.EMPTY_STRING;

		}
		long consultantMob = patientRegistration.getVuserD().getPersonalContactNumber();

		// for department
		String dpt = null;

		if (patientRegistration.getVuserD().getDoctorDetails() != null) {
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

		// for advance
		Set<PatientPayment> patientPayment = patientRegistration.getPatientPayment();

		for (PatientPayment patientPaymentInfo : patientPayment) {
			if (patientPaymentInfo.getTypeOfCharge().equalsIgnoreCase("ADVANCE")) {
				advanceAmt += patientPaymentInfo.getAmount();
			}

			if (patientPaymentInfo.getTypeOfCharge().equalsIgnoreCase("SETTLED AMOUNT")) {
				settledAmt += patientPaymentInfo.getAmount();

			}

		}

		System.out.println(advanceAmt);
		System.out.println(settledAmt);

		String pdfBill = null;
		String regId = regIde;

		List<ChargeBill> chargeBillList = chargeBillServiceImpl
				.findByPatRegId(patientRegistrationServiceImpl.findByRegId(regIde));

		List<ChargeBill> chargeBillListLab = chargeBillList.stream().filter((s) -> s.getLabId() != null)
				.collect(Collectors.toList());
		List<ChargeBill> chargeBillListService = chargeBillList.stream().filter((s) -> s.getServiceId() != null)
				.collect(Collectors.toList());
		List<ChargeBill> chargeBillListSale = chargeBillList.stream().filter((s) -> s.getSaleId() != null)
				.collect(Collectors.toList());

		String patientName = patientRegistration.getPatientDetails().getFirstName() + "%20"
				+ patientRegistration.getPatientDetails().getLastName();
		long mob = patientRegistration.getPatientDetails().getMobile();

		pdfBill = chargeBillListq.get(0).getBillNo();

		Date date5 = Calendar.getInstance().getTime();
		DateFormat formatter5 = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss");
		String today5 = formatter5.format(date5).toString();

		String pName = patientDetails.getFirstName() + " " + patientDetails.getLastName();
		/*
		 * String newAddress =
		 * "                                                    Plot No.14,15,16 &17,Nandi Co-op. Society,     \n                                                              Main Road,Beside Navya Grand Hotel,Miyapur,Hyderabad,TS                       \n                                                               Phone:040-23046789 | For Appointment Contact: 8019114481   \n                                                                             Email : udbhavahospitals@gmail.com"
		 * ;
		 */
		Font redFont2 = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
		Font redFont3 = new Font(Font.FontFamily.HELVETICA, 12, Font.UNDERLINE);
		Font redFont4 = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);

		Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
		Font redFont9 = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.NORMAL);

		Font redFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

		Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);

		String billNoo = null;

		// final bill-------------------------------

		byte[] pdfByte = null;
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		try {

			Resource fileResourcee = resourceLoader.getResource(
					ConstantValues.IMAGE_PNG_CLASSPATH);

			Document document = new Document(PageSize.A4_LANDSCAPE);
			PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);

			document.open();
			PdfPTable table = new PdfPTable(2);

			Image img = Image.getInstance(hospitalLogo.getURL());

			img.scaleAbsolute(ConstantValues.IMAGE_ABSOLUTE_INTIAL_POSITION, ConstantValues.IMAGE_ABSOLUTE_FINAL_POSITION);
			table.setWidthPercentage(ConstantValues.TABLE_SET_WIDTH_PERECENTAGE);

			Phrase pq = new Phrase(new Chunk(img, ConstantValues.IMAGE_SET_INTIAL_POSITION, ConstantValues.IMAGE_SET_FINAL_POSITION));

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
			hcell351 = new PdfPCell(new Phrase(ConstantValues.HOSPITAL_NAME, redFont4));
			hcell351.setBorder(Rectangle.NO_BORDER);
			hcell351.setHorizontalAlignment(Element.ALIGN_CENTER);
			table351.addCell(hcell351);

			cell1.addElement(table351);
			cell1.addElement(pq);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setColspan(2);

			// table.addCell(cell1);

			/*
			 * PdfPCell cell35 = new PdfPCell(); cell35.setBorder(Rectangle.NO_BORDER);
			 */

			PdfPTable table35 = new PdfPTable(1);
			table35.setWidths(new float[] { 5f });
			table35.setSpacingBefore(10);
			table35.setWidthPercentage(100f);

			PdfPCell hcell35;
			hcell35 = new PdfPCell(new Phrase("INPATIENT FINAL BILL", headFont));
			hcell35.setBorder(Rectangle.NO_BORDER);
			hcell35.setHorizontalAlignment(Element.ALIGN_CENTER);
			table35.addCell(hcell35);

			/*
			 * cell35.setColspan(2); cell35.addElement(table35); table.addCell(cell35);
			 */

			cell1.addElement(table35);

			// PdfPCell cell19 = new PdfPCell();

			PdfPTable table3 = new PdfPTable(6);
			table3.setWidths(new float[] { 5f, 1f, 5f, 5f, 1f, 5f });
			table3.setSpacingBefore(10);

			PdfPCell hcell1;
			hcell1 = new PdfPCell(new Phrase("Bill Date", redFont1));
			hcell1.setBorder(Rectangle.NO_BORDER);
			hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell1.setPaddingLeft(-50f);
			table3.addCell(hcell1);

			hcell1 = new PdfPCell(new Phrase(":", redFont1));
			hcell1.setBorder(Rectangle.NO_BORDER);
			hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell1.setPaddingLeft(-50f);
			table3.addCell(hcell1);

			hcell1 = new PdfPCell(new Phrase(today, redFont1));
			hcell1.setBorder(Rectangle.NO_BORDER);
			hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell1.setPaddingLeft(-50f);
			table3.addCell(hcell1);

			PdfPCell hcell10;
			hcell10 = new PdfPCell(new Phrase("Bill No", redFont1));
			hcell10.setBorder(Rectangle.NO_BORDER);
			// hcell10.setPaddingLeft(40f);
			table3.addCell(hcell10);

			hcell10 = new PdfPCell(new Phrase(":", redFont1));
			hcell10.setBorder(Rectangle.NO_BORDER);
			// hcell10.setPaddingLeft(40f);
			table3.addCell(hcell10);

			hcell10 = new PdfPCell(new Phrase(pdfBill, redFont1));
			hcell10.setBorder(Rectangle.NO_BORDER);
			// hcell10.setPaddingLeft(40f);
			table3.addCell(hcell10);

			PdfPCell hcell2;
			hcell2 = new PdfPCell(new Phrase("Admission No", redFont1));
			hcell2.setBorder(Rectangle.NO_BORDER);
			hcell2.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell2.setPaddingLeft(-50f);
			table3.addCell(hcell2);

			hcell2 = new PdfPCell(new Phrase(":", redFont1));
			hcell2.setBorder(Rectangle.NO_BORDER);
			hcell2.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell2.setPaddingLeft(-50f);
			table3.addCell(hcell2);

			hcell2 = new PdfPCell(new Phrase(patientRegistration.getRegId(), redFont1));
			hcell2.setBorder(Rectangle.NO_BORDER);
			hcell2.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell2.setPaddingLeft(-50f);
			table3.addCell(hcell2);

			PdfPCell hcell20;
			hcell20 = new PdfPCell(new Phrase("UMR No", redFont1));
			hcell20.setBorder(Rectangle.NO_BORDER);
			// hcell20.setPaddingLeft(40f);
			table3.addCell(hcell20);

			hcell20 = new PdfPCell(new Phrase(":", redFont1));
			hcell20.setBorder(Rectangle.NO_BORDER);
			// hcell20.setPaddingLeft(40f);
			table3.addCell(hcell20);

			hcell20 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getUmr(), redFont1));
			hcell20.setBorder(Rectangle.NO_BORDER);
			// hcell20.setPaddingLeft(40f);
			table3.addCell(hcell20);

			PdfPCell hcell3;
			hcell3 = new PdfPCell(new Phrase("Patient Name", redFont1));
			hcell3.setBorder(Rectangle.NO_BORDER);
			hcell3.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell3.setPaddingLeft(-50f);
			table3.addCell(hcell3);

			hcell3 = new PdfPCell(new Phrase(":", redFont1));
			hcell3.setBorder(Rectangle.NO_BORDER);
			hcell3.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell3.setPaddingLeft(-50f);
			table3.addCell(hcell3);

			hcell3 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getTitle() + " "
					+ patientRegistration.getPatientDetails().getFirstName() + " "
					+ patientRegistration.getPatientDetails().getLastName(), redFont1));
			hcell3.setBorder(Rectangle.NO_BORDER);
			hcell3.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell3.setPaddingLeft(-50f);
			table3.addCell(hcell3);

			PdfPCell hcell30;
			hcell30 = new PdfPCell(new Phrase("S-W-B-D/O", redFont1));
			hcell30.setBorder(Rectangle.NO_BORDER);
			table3.addCell(hcell30);

			hcell30 = new PdfPCell(new Phrase(":", redFont1));
			hcell30.setBorder(Rectangle.NO_BORDER);
			table3.addCell(hcell30);

			hcell30 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getMotherName(), redFont1));// mother
																													// name
																													// if
																													// father
																													// name
			hcell30.setBorder(Rectangle.NO_BORDER);
			table3.addCell(hcell30);

			// for DOA
			Timestamp timestamp2 = patientRegistration.getDateOfJoining();
			DateFormat dateFormat2 = new SimpleDateFormat("dd-MMM-yyyy hh:mm aa ");

			Calendar calendar2 = Calendar.getInstance();
			calendar2.setTimeInMillis(timestamp2.getTime());

			String doa = dateFormat2.format(calendar2.getTime());

			PdfPCell hcell4;
			hcell4 = new PdfPCell(new Phrase("DOA", redFont1));
			hcell4.setBorder(Rectangle.NO_BORDER);
			hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell4.setPaddingLeft(-50f);
			table3.addCell(hcell4);

			hcell4 = new PdfPCell(new Phrase(":", redFont1));
			hcell4.setBorder(Rectangle.NO_BORDER);
			hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell4.setPaddingLeft(-50f);
			table3.addCell(hcell4);

			hcell4 = new PdfPCell(new Phrase(String.valueOf(doa), redFont1));
			hcell4.setBorder(Rectangle.NO_BORDER);
			hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell4.setPaddingLeft(-50f);
			table3.addCell(hcell4);

			PdfPCell hcell40;
			hcell40 = new PdfPCell(new Phrase("Admitted Ward", redFont1));
			hcell40.setBorder(Rectangle.NO_BORDER);
			table3.addCell(hcell40);

			hcell40 = new PdfPCell(new Phrase(":", redFont1));
			hcell40.setBorder(Rectangle.NO_BORDER);
			table3.addCell(hcell40);

			hcell40 = new PdfPCell(new Phrase(admittedWard, redFont1));
			hcell40.setBorder(Rectangle.NO_BORDER);
			table3.addCell(hcell40);

			PdfPCell hcell5;
			hcell5 = new PdfPCell(new Phrase("Consultant", redFont1));
			hcell5.setBorder(Rectangle.NO_BORDER);
			hcell5.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell5.setPaddingLeft(-50f);
			table3.addCell(hcell5);

			hcell5 = new PdfPCell(new Phrase(":", redFont1));
			hcell5.setBorder(Rectangle.NO_BORDER);
			hcell5.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell5.setPaddingLeft(-50f);
			table3.addCell(hcell5);

			hcell5 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getConsultant(), redFont1));
			hcell5.setBorder(Rectangle.NO_BORDER);
			hcell5.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell5.setPaddingLeft(-50f);
			table3.addCell(hcell5);

			PdfPCell hcell50;
			hcell50 = new PdfPCell(new Phrase("Department", redFont1));
			hcell50.setBorder(Rectangle.NO_BORDER);
			// hcell50.setPaddingLeft(40f);
			table3.addCell(hcell50);

			hcell50 = new PdfPCell(new Phrase(":", redFont1));
			hcell50.setBorder(Rectangle.NO_BORDER);
			// hcell50.setPaddingLeft(40f);
			table3.addCell(hcell50);

			hcell50 = new PdfPCell(new Phrase(dpt, redFont1));
			hcell50.setBorder(Rectangle.NO_BORDER);
			// hcell50.setPaddingLeft(40f);
			table3.addCell(hcell50);

			PdfPCell hcell6;
			hcell6 = new PdfPCell(new Phrase("Registration No", redFont1));
			hcell6.setBorder(Rectangle.NO_BORDER);
			hcell6.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell6.setPaddingLeft(-50f);
			table3.addCell(hcell6);

			hcell6 = new PdfPCell(new Phrase(":", redFont1));
			hcell6.setBorder(Rectangle.NO_BORDER);
			hcell6.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell6.setPaddingLeft(-50f);
			table3.addCell(hcell6);

			hcell6 = new PdfPCell(new Phrase(patientRegistration.getRegId(), redFont1));
			hcell6.setBorder(Rectangle.NO_BORDER);
			hcell6.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell6.setPaddingLeft(-50f);
			table3.addCell(hcell6);

			PdfPCell hcell60;
			hcell60 = new PdfPCell(new Phrase("Age/Sex", redFont1));
			hcell60.setBorder(Rectangle.NO_BORDER);
			// hcell60.setPaddingLeft(40f);
			table3.addCell(hcell60);

			hcell60 = new PdfPCell(new Phrase(":", redFont1));
			hcell60.setBorder(Rectangle.NO_BORDER);
			// hcell60.setPaddingLeft(40f);
			table3.addCell(hcell60);

			hcell60 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getAge() + "/"
					+ patientRegistration.getPatientDetails().getGender(), redFont1));
			hcell60.setBorder(Rectangle.NO_BORDER);
			// hcell60.setPaddingLeft(40f);
			table3.addCell(hcell60);

			PdfPCell hcell7;
			hcell7 = new PdfPCell(new Phrase("Address", redFont1));
			hcell7.setBorder(Rectangle.NO_BORDER);
			hcell7.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell7.setPaddingLeft(-50f);
			table3.addCell(hcell7);

			hcell7 = new PdfPCell(new Phrase(":", redFont1));
			hcell7.setBorder(Rectangle.NO_BORDER);
			hcell7.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell7.setPaddingLeft(-50f);
			table3.addCell(hcell7);

			hcell7 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getAddress(), redFont1));
			hcell7.setBorder(Rectangle.NO_BORDER);
			hcell7.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell7.setPaddingLeft(-50f);
			table3.addCell(hcell7);

			String refName = null;
			if (patientRegistration.getPatientDetails().getvRefferalDetails() == null) {
				refName = "";
			} else {
				refName = patientRegistration.getPatientDetails().getvRefferalDetails().getRefName();
			}

			PdfPCell hcell70;
			hcell70 = new PdfPCell(new Phrase("Referal Name", redFont1));
			hcell70.setBorder(Rectangle.NO_BORDER);
			// hcell70.setPaddingLeft(40f);
			table3.addCell(hcell70);

			hcell70 = new PdfPCell(new Phrase(":", redFont1));
			hcell70.setBorder(Rectangle.NO_BORDER);
			// hcell70.setPaddingLeft(40f);
			table3.addCell(hcell70);

			hcell70 = new PdfPCell(new Phrase(refName, redFont1));
			hcell70.setBorder(Rectangle.NO_BORDER);
			// hcell70.setPaddingLeft(40f);
			table3.addCell(hcell70);

			PdfPCell hcell701;
			hcell701 = new PdfPCell(new Phrase("Discharge Date", redFont1));
			hcell701.setBorder(Rectangle.NO_BORDER);
			hcell701.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell701.setPaddingLeft(-50f);

			// hcell70.setPaddingLeft(40f);
			table3.addCell(hcell701);

			hcell701 = new PdfPCell(new Phrase(":", redFont1));
			hcell701.setBorder(Rectangle.NO_BORDER);
			hcell701.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell701.setPaddingLeft(-50f);

			// hcell70.setPaddingLeft(40f);
			table3.addCell(hcell701);

			hcell701 = new PdfPCell(new Phrase(today, redFont1));
			hcell701.setBorder(Rectangle.NO_BORDER);
			hcell701.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell701.setPaddingLeft(-50f);

			// hcell70.setPaddingLeft(40f);
			table3.addCell(hcell701);

			String procedureName = null;
			if (patientRegistration.getProcedureName() == null) {
				procedureName = "";
			} else {
				procedureName = patientRegistration.getProcedureName();
			}

			PdfPCell hcell7011;
			hcell7011 = new PdfPCell(new Phrase("Procedure", redFont1));
			hcell7011.setBorder(Rectangle.NO_BORDER);
			// hcell7011.setHorizontalAlignment(Element.ALIGN_LEFT);
			// hcell7011.setPaddingLeft(-50f);
			// hcell70.setPaddingLeft(40f);
			table3.addCell(hcell7011);

			hcell7011 = new PdfPCell(new Phrase(":", redFont1));
			hcell7011.setBorder(Rectangle.NO_BORDER);
			// hcell7011.setHorizontalAlignment(Element.ALIGN_LEFT);
			// hcell7011.setPaddingLeft(-50f);
			// hcell70.setPaddingLeft(40f);
			table3.addCell(hcell7011);

			hcell7011 = new PdfPCell(new Phrase(procedureName, redFont1));
			hcell7011.setBorder(Rectangle.NO_BORDER);
			// hcell7011.setHorizontalAlignment(Element.ALIGN_LEFT);S
			// hcell7011.setPaddingLeft(-50f);
			// hcell70.setPaddingLeft(40f);
			table3.addCell(hcell7011);

			/*
			 * cell19.setColspan(2); cell19.addElement(table3); table.addCell(cell19);
			 */
			cell1.addElement(table3);

			/*
			 * PdfPCell cell20 = new PdfPCell(); cell20.setBorder(Rectangle.NO_BORDER);
			 * cell20.setPaddingTop(10f);
			 */

			PdfPTable table1 = new PdfPTable(7);
			table1.setWidths(new float[] { 4f, 10f, 3f, 2.5f, 3f, 3f, 3f });
			table1.setSpacingBefore(10);
			table1.setWidthPercentage(105f);

			PdfPCell hcell;

			hcell = new PdfPCell(new Phrase("Service Code", headFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			hcell.setBackgroundColor(BaseColor.GRAY);
			hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
			table1.addCell(hcell);

			hcell = new PdfPCell(new Phrase("Service/Investigation", headFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			hcell.setBackgroundColor(BaseColor.GRAY);
			hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
			table1.addCell(hcell);

			hcell = new PdfPCell(new Phrase("Date", headFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			hcell.setBackgroundColor(BaseColor.GRAY);
			hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell.setPaddingLeft(7);
			table1.addCell(hcell);

			hcell = new PdfPCell(new Phrase("Qty", headFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			hcell.setBackgroundColor(BaseColor.GRAY);
			hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell.setPaddingLeft(18);
			table1.addCell(hcell);

			hcell = new PdfPCell(new Phrase("Rate", headFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			hcell.setBackgroundColor(BaseColor.GRAY);
			hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell.setPaddingLeft(15);
			table1.addCell(hcell);

			hcell = new PdfPCell(new Phrase("Disc", headFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			hcell.setBackgroundColor(BaseColor.GRAY);
			hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell.setPaddingLeft(15);
			table1.addCell(hcell);

			hcell = new PdfPCell(new Phrase("Amt", headFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			hcell.setBackgroundColor(BaseColor.GRAY);
			hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell.setPaddingLeft(30);
			table1.addCell(hcell);

			table1.addCell(hcell);

			/*
			 * cell20.setColspan(2); cell20.addElement(table1); table.addCell(cell20);
			 */
			cell1.addElement(table1);

			/*
			 * PdfPCell cell21 = new PdfPCell(); cell21.setBorder(Rectangle.NO_BORDER);
			 */

			// Calculating total
			float total = 0;
			float totalMed = 0;
			float totalServiceAmt = 0;
			float totalAccmAmt = 0;
			float totalEqAmt = 0;
			float totalconAmt = 0;
			for (ChargeBill chargeBillInfo : chargeBillListLab) {
				if (chargeBillInfo.getLabId() != null) {

					if (chargeBillInfo.getNetAmount() != 0) {
						chargeBillInfo.setServiceName(chargeBillInfo.getLabId().getServiceName());

						total += chargeBillInfo.getNetAmount();
					}

				}
			}

			for (ChargeBill chargeMedicine : chargeBillListSale) {
				if (chargeMedicine.getSaleId() != null) {

					if (chargeMedicine.getNetAmount() != 0) {
						totalMed += chargeMedicine.getNetAmount();
					}

				}

			}

			for (ChargeBill chargeBillInfo : chargeBillListService) {

				if (chargeBillInfo.getServiceId() != null) {
					if (chargeBillInfo.getServiceId().getServiceType().equalsIgnoreCase("OTHER")) {
						if (chargeBillInfo.getNetAmount() != 0) {

							totalServiceAmt += chargeBillInfo.getNetAmount();
						}
					}
					if (chargeBillInfo.getServiceId().getServiceType().equalsIgnoreCase("Lab")) {
						totalServiceAmt += chargeBillInfo.getNetAmount();

					}
				}
			}

			for (ChargeBill chargeBillInfo1 : chargeBillListService) {

				if (chargeBillInfo1.getServiceId() != null) {
					if (chargeBillInfo1.getServiceId().getServiceType().equalsIgnoreCase("WARD CHARGES")) {
						if (chargeBillInfo1.getNetAmount() != 0) {
							totalAccmAmt += chargeBillInfo1.getNetAmount();
						}
					}
				}
			}

			for (ChargeBill chargeBillInfo1 : chargeBillListService) {

				if (chargeBillInfo1.getServiceId() != null) {
					if (chargeBillInfo1.getServiceId().getServiceType().equalsIgnoreCase("EQUIPMENT CHARGES")) {
						if (chargeBillInfo1.getNetAmount() != 0) {
							totalEqAmt += chargeBillInfo1.getNetAmount();
						}
					}
				}
			}

			for (ChargeBill chargeBillInfo1 : chargeBillListService) {

				if (chargeBillInfo1.getServiceId() != null) {
					if (chargeBillInfo1.getServiceId().getServiceType().equalsIgnoreCase("CONSULTATION CHARGES")) {
						if (chargeBillInfo1.getNetAmount() != 0) {
							totalconAmt += chargeBillInfo1.getNetAmount();
						}
					}
				}
			}

			PdfPTable table11 = new PdfPTable(7);
			table11.setWidths(new float[] { 4f, 10f, 3f, 2.5f, 3f, 3f, 3f });
			table11.setSpacingBefore(10);
			table11.setWidthPercentage(105f);

			PdfPCell hcell01;
			if (!chargeBillListLab.isEmpty()) {
				hcell01 = new PdfPCell(new Phrase("LAB CHARGES", headFont));
				hcell01.setBorder(Rectangle.NO_BORDER);
				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
				table11.addCell(hcell01);

				hcell01 = new PdfPCell(new Phrase("", headFont));
				hcell01.setBorder(Rectangle.NO_BORDER);
				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
				table11.addCell(hcell01);

				hcell01 = new PdfPCell(new Phrase("", headFont));
				hcell01.setBorder(Rectangle.NO_BORDER);
				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
				table11.addCell(hcell01);

				hcell01 = new PdfPCell(new Phrase("", headFont));
				hcell01.setBorder(Rectangle.NO_BORDER);
				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
				table11.addCell(hcell01);

				hcell01 = new PdfPCell(new Phrase("", headFont));
				hcell01.setBorder(Rectangle.NO_BORDER);
				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
				table11.addCell(hcell01);

				hcell01 = new PdfPCell(new Phrase("", headFont));
				hcell01.setBorder(Rectangle.NO_BORDER);
				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
				table11.addCell(hcell01);

				hcell01 = new PdfPCell(new Phrase(String.valueOf(total), headFont));
				hcell01.setBorder(Rectangle.NO_BORDER);
				hcell01.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell01.setPaddingRight(10);
				table11.addCell(hcell01);

				PdfPCell cell;
				for (ChargeBill chargeBillInfo : chargeBillListLab) {

					if (chargeBillInfo.getLabId() != null)

					{
						if (chargeBillInfo.getAmount() != 0) {

							chargeBillInfo.setServiceName(chargeBillInfo.getLabId().getServiceName());

							String from = chargeBillInfo.getLabId().getEnteredDate().toString();
							Timestamp timestamp = Timestamp.valueOf(from);
							DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");

							Calendar calendar = Calendar.getInstance();
							calendar.setTimeInMillis(timestamp.getTime());

							String labDate = dateFormat.format(calendar.getTime());

							cell = new PdfPCell(new Phrase(
									String.valueOf(chargeBillInfo.getLabId().getLabServices().getServiceId()),
									redFont));
							cell.setBorder(Rectangle.NO_BORDER);
							cell.setHorizontalAlignment(Element.ALIGN_LEFT);
							table11.addCell(cell);

							cell = new PdfPCell(new Phrase(chargeBillInfo.getServiceName(), redFont));
							cell.setBorder(Rectangle.NO_BORDER);
							cell.setHorizontalAlignment(Element.ALIGN_LEFT);
							table11.addCell(cell);

							cell = new PdfPCell(new Phrase(labDate, redFont));
							cell.setBorder(Rectangle.NO_BORDER);
							cell.setHorizontalAlignment(Element.ALIGN_LEFT);
							table11.addCell(cell);

							cell = new PdfPCell(new Phrase(String.valueOf(chargeBillInfo.getQuantity()), redFont));
							cell.setBorder(Rectangle.NO_BORDER);
							cell.setHorizontalAlignment(Element.ALIGN_CENTER);
							table11.addCell(cell);

							cell = new PdfPCell(new Phrase(String.valueOf(chargeBillInfo.getMrp()), redFont));
							cell.setBorder(Rectangle.NO_BORDER);
							cell.setHorizontalAlignment(Element.ALIGN_CENTER);
							table11.addCell(cell);

							cell = new PdfPCell(new Phrase(String.valueOf(chargeBillInfo.getDiscount()), redFont));
							cell.setBorder(Rectangle.NO_BORDER);
							cell.setHorizontalAlignment(Element.ALIGN_CENTER);
							table11.addCell(cell);

							cell = new PdfPCell(new Phrase(String.valueOf(chargeBillInfo.getNetAmount()), redFont));
							cell.setBorder(Rectangle.NO_BORDER);
							cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
							cell.setPaddingRight(10);
							table11.addCell(cell);

							// total += chargeBillInfo.getNetAmount();
						}
					}

				}

			} else {
				hcell01 = new PdfPCell(new Phrase("", headFont));
				hcell01.setBorder(Rectangle.NO_BORDER);
				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
				table11.addCell(hcell01);

				hcell01 = new PdfPCell(new Phrase("", headFont));
				hcell01.setBorder(Rectangle.NO_BORDER);
				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
				table11.addCell(hcell01);

				hcell01 = new PdfPCell(new Phrase("", headFont));
				hcell01.setBorder(Rectangle.NO_BORDER);
				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
				table11.addCell(hcell01);

				hcell01 = new PdfPCell(new Phrase("", headFont));
				hcell01.setBorder(Rectangle.NO_BORDER);
				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
				table11.addCell(hcell01);

				hcell01 = new PdfPCell(new Phrase("", headFont));
				hcell01.setBorder(Rectangle.NO_BORDER);
				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
				table11.addCell(hcell01);

				hcell01 = new PdfPCell(new Phrase("", headFont));
				hcell01.setBorder(Rectangle.NO_BORDER);
				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
				table11.addCell(hcell01);

				hcell01 = new PdfPCell(new Phrase("", headFont));
				hcell01.setBorder(Rectangle.NO_BORDER);
				hcell01.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell01.setPaddingRight(10);
				table11.addCell(hcell01);

			}

			int count = 0;
			/*
			 * cell21.setColspan(2); cell21.addElement(table11); table.addCell(cell21);
			 */

			cell1.addElement(table11);

			/*
			 * PdfPCell cell22 = new PdfPCell(); cell22.setBorder(Rectangle.NO_BORDER);
			 */

			PdfPTable table12 = new PdfPTable(7);
			table12.setWidths(new float[] { 7f, 10f, 3f, 2.5f, 3f, 3f, 3f });
			table12.setSpacingBefore(10);
			table12.setWidthPercentage(105f);

			PdfPTable table22 = new PdfPTable(7);
			table22.setWidths(new float[] { 4f, 10f, 3f, 2.5f, 3f, 3f, 3f });
			table22.setSpacingBefore(10);
			table22.setWidthPercentage(105f);

			PdfPCell hcell011;
			if (!chargeBillListSale.isEmpty()) {
				hcell011 = new PdfPCell(new Phrase("MEDICINE CHARGES", headFont));
				hcell011.setBorder(Rectangle.NO_BORDER);
				hcell011.setHorizontalAlignment(Element.ALIGN_LEFT);
				table12.addCell(hcell011);

				hcell011 = new PdfPCell(new Phrase("", headFont));
				hcell011.setBorder(Rectangle.NO_BORDER);
				hcell011.setHorizontalAlignment(Element.ALIGN_LEFT);
				table12.addCell(hcell011);

				hcell011 = new PdfPCell(new Phrase("", headFont));
				hcell011.setBorder(Rectangle.NO_BORDER);
				hcell011.setHorizontalAlignment(Element.ALIGN_LEFT);
				table12.addCell(hcell011);

				hcell011 = new PdfPCell(new Phrase("", headFont));
				hcell011.setBorder(Rectangle.NO_BORDER);
				hcell011.setHorizontalAlignment(Element.ALIGN_LEFT);
				table12.addCell(hcell011);

				hcell011 = new PdfPCell(new Phrase("", headFont));
				hcell011.setBorder(Rectangle.NO_BORDER);
				hcell011.setHorizontalAlignment(Element.ALIGN_LEFT);
				table12.addCell(hcell011);

				hcell011 = new PdfPCell(new Phrase("", headFont));
				hcell011.setBorder(Rectangle.NO_BORDER);
				hcell011.setHorizontalAlignment(Element.ALIGN_LEFT);
				table12.addCell(hcell011);

				hcell011 = new PdfPCell(new Phrase(String.valueOf(Math.round(totalMed)), headFont));
				hcell011.setBorder(Rectangle.NO_BORDER);
				hcell011.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell011.setPaddingRight(10);
				table12.addCell(hcell011);

				// charge bill
				for (ChargeBill chargeMedicine : chargeBillListSale) {
					if (chargeMedicine.getSaleId() != null) {

						if (chargeMedicine.getAmount() != 0) {

							/*
							 * salestAmount += chargeMedicine.getSaleId().getCostPrice(); salesnetAmount +=
							 * chargeMedicine.getNetAmount(); salesDiscount =
							 * chargeMedicine.getSaleId().getDiscount(); salesQuantity +=
							 * chargeMedicine.getSaleId().getQuantity();
							 */

							salesnetAmount += chargeMedicine.getNetAmount();
							String from = chargeMedicine.getSaleId().getBillDate().toString();
							Timestamp timestamp = Timestamp.valueOf(from);
							DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");

							Calendar calendar = Calendar.getInstance();
							calendar.setTimeInMillis(timestamp.getTime());

							String medicineDate = dateFormat.format(calendar.getTime());

							PdfPCell hcell0;

							hcell0 = new PdfPCell(new Phrase(String.valueOf(
									chargeMedicine.getSaleId().getPatientSalesMedicineDetails().getMedicineId()),
									redFont1));
							hcell0.setBorder(Rectangle.NO_BORDER);
							hcell0.setHorizontalAlignment(Element.ALIGN_LEFT);
							table22.addCell(hcell0);

							hcell0 = new PdfPCell(
									new Phrase(String.valueOf(chargeMedicine.getSaleId().getMedicineName()), redFont1));
							hcell0.setBorder(Rectangle.NO_BORDER);
							hcell0.setHorizontalAlignment(Element.ALIGN_LEFT);
							table22.addCell(hcell0);

							hcell0 = new PdfPCell(new Phrase(medicineDate, redFont1));
							hcell0.setBorder(Rectangle.NO_BORDER);
							hcell0.setHorizontalAlignment(Element.ALIGN_LEFT);
							table22.addCell(hcell0);

							hcell0 = new PdfPCell(
									new Phrase(String.valueOf(chargeMedicine.getSaleId().getQuantity()), redFont1));
							hcell0.setBorder(Rectangle.NO_BORDER);
							hcell0.setHorizontalAlignment(Element.ALIGN_CENTER);
							table22.addCell(hcell0);

							hcell0 = new PdfPCell(
									new Phrase(String.valueOf(chargeMedicine.getSaleId().getMrp()), redFont1));
							hcell0.setBorder(Rectangle.NO_BORDER);
							hcell0.setHorizontalAlignment(Element.ALIGN_CENTER);
							table22.addCell(hcell0);

							hcell0 = new PdfPCell(new Phrase(
									String.valueOf(Math.round(chargeMedicine.getSaleId().getDiscount())), redFont1));
							hcell0.setBorder(Rectangle.NO_BORDER);
							hcell0.setHorizontalAlignment(Element.ALIGN_CENTER);
							table22.addCell(hcell0);

							hcell0 = new PdfPCell(new Phrase(String.valueOf(chargeMedicine.getNetAmount()), redFont1));
							hcell0.setBorder(Rectangle.NO_BORDER);
							hcell0.setHorizontalAlignment(Element.ALIGN_RIGHT);
							hcell0.setPaddingRight(10);
							table22.addCell(hcell0);

						}

					}
				}

			} else {
				hcell011 = new PdfPCell(new Phrase("", headFont));
				hcell011.setBorder(Rectangle.NO_BORDER);
				hcell011.setHorizontalAlignment(Element.ALIGN_LEFT);
				table12.addCell(hcell011);

				hcell011 = new PdfPCell(new Phrase("", headFont));
				hcell011.setBorder(Rectangle.NO_BORDER);
				hcell011.setHorizontalAlignment(Element.ALIGN_LEFT);
				table12.addCell(hcell011);

				hcell011 = new PdfPCell(new Phrase("", headFont));
				hcell011.setBorder(Rectangle.NO_BORDER);
				hcell011.setHorizontalAlignment(Element.ALIGN_LEFT);
				table12.addCell(hcell011);

				hcell011 = new PdfPCell(new Phrase("", headFont));
				hcell011.setBorder(Rectangle.NO_BORDER);
				hcell011.setHorizontalAlignment(Element.ALIGN_LEFT);
				table12.addCell(hcell011);

				hcell011 = new PdfPCell(new Phrase("", headFont));
				hcell011.setBorder(Rectangle.NO_BORDER);
				hcell011.setHorizontalAlignment(Element.ALIGN_LEFT);
				table12.addCell(hcell011);

				hcell011 = new PdfPCell(new Phrase("", headFont));
				hcell011.setBorder(Rectangle.NO_BORDER);
				hcell011.setHorizontalAlignment(Element.ALIGN_LEFT);
				table12.addCell(hcell011);

				hcell011 = new PdfPCell(new Phrase("", headFont));
				hcell011.setBorder(Rectangle.NO_BORDER);
				hcell011.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell011.setPaddingRight(10);
				table12.addCell(hcell011);

			}

			cell1.addElement(table12);

			cell1.addElement(table22);

			/*
			 * PdfPCell cell23 = new PdfPCell(); cell23.setBorder(Rectangle.NO_BORDER);
			 */

			PdfPTable table28 = new PdfPTable(7);
			table28.setWidths(new float[] { 7f, 10f, 3f, 2.5f, 3f, 3f, 3f });
			table28.setSpacingBefore(10);
			table28.setWidthPercentage(105f);

			PdfPTable table13 = new PdfPTable(7);
			table13.setWidths(new float[] { 4f, 10f, 3f, 2.5f, 3f, 3f, 3f });
			table13.setSpacingBefore(10);
			table13.setWidthPercentage(105f);

			PdfPCell hcell0111;
			if (!chargeBillListService.isEmpty()) {
				if (chargeBillListService.stream()
						.filter((s) -> s.getServiceId().getServiceType().equalsIgnoreCase("OTHER")).count() > 0) {
					hcell0111 = new PdfPCell(new Phrase("SERVICE CHARGES", headFont));
					hcell0111.setBorder(Rectangle.NO_BORDER);
					hcell0111.setHorizontalAlignment(Element.ALIGN_LEFT);
					table28.addCell(hcell0111);

					hcell0111 = new PdfPCell(new Phrase("", headFont));
					hcell0111.setBorder(Rectangle.NO_BORDER);
					hcell0111.setHorizontalAlignment(Element.ALIGN_CENTER);
					table28.addCell(hcell0111);

					hcell0111 = new PdfPCell(new Phrase("", headFont));
					hcell0111.setBorder(Rectangle.NO_BORDER);
					hcell0111.setHorizontalAlignment(Element.ALIGN_CENTER);
					table28.addCell(hcell0111);
					hcell0111.setHorizontalAlignment(Element.ALIGN_CENTER);

					hcell0111 = new PdfPCell(new Phrase("", headFont));
					hcell0111.setBorder(Rectangle.NO_BORDER);
					hcell0111.setHorizontalAlignment(Element.ALIGN_CENTER);
					table28.addCell(hcell0111);

					hcell0111 = new PdfPCell(new Phrase("", headFont));
					hcell0111.setBorder(Rectangle.NO_BORDER);
					table28.addCell(hcell0111);

					hcell0111 = new PdfPCell(new Phrase("", headFont));
					hcell0111.setBorder(Rectangle.NO_BORDER);
					table28.addCell(hcell0111);

					hcell0111 = new PdfPCell(new Phrase(String.valueOf(totalServiceAmt), headFont));
					hcell0111.setBorder(Rectangle.NO_BORDER);
					hcell0111.setHorizontalAlignment(Element.ALIGN_RIGHT);
					hcell0111.setPaddingRight(10);
					table28.addCell(hcell0111);

					for (ChargeBill chargeBillInfo : chargeBillListService) {

						if (chargeBillInfo.getServiceId() != null
								&& chargeBillInfo.getServiceId().getServiceType().equalsIgnoreCase("OTHER")
								|| chargeBillInfo.getServiceId().getServiceType().equalsIgnoreCase("LAB")) {

							if (chargeBillInfo.getAmount() != 0) {

								String from = chargeBillInfo.getInsertedDate().toString();
								Timestamp timestamp = Timestamp.valueOf(from);
								DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");

								Calendar calendar = Calendar.getInstance();
								calendar.setTimeInMillis(timestamp.getTime());

								String serviceDate = dateFormat.format(calendar.getTime());

								PdfPCell cell11;
								chargeBillInfo.setServiceName(chargeBillInfo.getServiceId().getServiceName());

								cell11 = new PdfPCell(new Phrase(
										String.valueOf(chargeBillInfo.getServiceId().getServiceId()), redFont));
								cell11.setBorder(Rectangle.NO_BORDER);
								cell11.setHorizontalAlignment(Element.ALIGN_LEFT);
								table13.addCell(cell11);

								cell11 = new PdfPCell(new Phrase(chargeBillInfo.getServiceName(), redFont));
								cell11.setBorder(Rectangle.NO_BORDER);
								cell11.setHorizontalAlignment(Element.ALIGN_LEFT);
								table13.addCell(cell11);

								cell11 = new PdfPCell(new Phrase(serviceDate, redFont));
								cell11.setBorder(Rectangle.NO_BORDER);
								cell11.setHorizontalAlignment(Element.ALIGN_LEFT);
								table13.addCell(cell11);

								cell11 = new PdfPCell(
										new Phrase(String.valueOf(chargeBillInfo.getQuantity()), redFont));
								cell11.setBorder(Rectangle.NO_BORDER);
								cell11.setHorizontalAlignment(Element.ALIGN_CENTER);
								table13.addCell(cell11);

								cell11 = new PdfPCell(new Phrase(String.valueOf(chargeBillInfo.getMrp()), redFont));
								cell11.setBorder(Rectangle.NO_BORDER);
								cell11.setHorizontalAlignment(Element.ALIGN_CENTER);
								table13.addCell(cell11);

								cell11 = new PdfPCell(
										new Phrase(String.valueOf(chargeBillInfo.getDiscount()), redFont));
								cell11.setBorder(Rectangle.NO_BORDER);
								cell11.setHorizontalAlignment(Element.ALIGN_CENTER);
								table13.addCell(cell11);

								cell11 = new PdfPCell(
										new Phrase(String.valueOf(chargeBillInfo.getNetAmount()), redFont));
								cell11.setBorder(Rectangle.NO_BORDER);
								cell11.setHorizontalAlignment(Element.ALIGN_RIGHT);
								cell11.setPaddingRight(10);

								table13.addCell(cell11);

								// total += chargeBillInfo.getNetAmount();

							}
						}

						else {
							chargeBillInfo.setServiceName("NOT APPLICABLE");
						}

					}

				}
			} else {
				hcell0111 = new PdfPCell(new Phrase("", headFont));
				hcell0111.setBorder(Rectangle.NO_BORDER);
				hcell0111.setHorizontalAlignment(Element.ALIGN_LEFT);
				table28.addCell(hcell0111);

				hcell0111 = new PdfPCell(new Phrase("", headFont));
				hcell0111.setBorder(Rectangle.NO_BORDER);
				hcell0111.setHorizontalAlignment(Element.ALIGN_CENTER);
				table28.addCell(hcell0111);

				hcell0111 = new PdfPCell(new Phrase("", headFont));
				hcell0111.setBorder(Rectangle.NO_BORDER);
				hcell0111.setHorizontalAlignment(Element.ALIGN_CENTER);
				table28.addCell(hcell0111);
				hcell0111.setHorizontalAlignment(Element.ALIGN_CENTER);

				hcell0111 = new PdfPCell(new Phrase("", headFont));
				hcell0111.setBorder(Rectangle.NO_BORDER);
				hcell0111.setHorizontalAlignment(Element.ALIGN_CENTER);
				table28.addCell(hcell0111);

				hcell0111 = new PdfPCell(new Phrase("", headFont));
				hcell0111.setBorder(Rectangle.NO_BORDER);
				table28.addCell(hcell0111);

				hcell0111 = new PdfPCell(new Phrase("", headFont));
				hcell0111.setBorder(Rectangle.NO_BORDER);
				table28.addCell(hcell0111);

				hcell0111 = new PdfPCell(new Phrase("", headFont));
				hcell0111.setBorder(Rectangle.NO_BORDER);
				hcell0111.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell0111.setPaddingRight(10);
				table28.addCell(hcell0111);

			}

			/*
			 * cell23.setColspan(2); cell23.addElement(table13); table.addCell(cell23);
			 */

			cell1.addElement(table28);
			cell1.addElement(table13);

			// ----------------------------------
			PdfPTable table131 = new PdfPTable(7);
			table131.setWidths(new float[] { 10f, 7f, 3f, 2.5f, 3f, 3f, 3f });
			table131.setSpacingBefore(10);
			table131.setWidthPercentage(105f);

			PdfPTable table281 = new PdfPTable(7);
			table281.setWidths(new float[] { 4f, 10f, 3f, 2.5f, 3f, 3f, 3f });
			table281.setSpacingBefore(10);
			table281.setWidthPercentage(105f);

			PdfPCell hcell01111;
			if (!chargeBillListService.isEmpty()) {
				if (chargeBillListService.stream()
						.filter((s) -> s.getServiceId().getServiceType().equalsIgnoreCase("WARD CHARGES"))
						.count() > 0) {
					hcell01111 = new PdfPCell(new Phrase("WARD CHARGES", headFont));
					hcell01111.setBorder(Rectangle.NO_BORDER);
					hcell01111.setHorizontalAlignment(Element.ALIGN_LEFT);
					table131.addCell(hcell01111);

					hcell01111 = new PdfPCell(new Phrase("", headFont));
					hcell01111.setBorder(Rectangle.NO_BORDER);
					hcell01111.setHorizontalAlignment(Element.ALIGN_CENTER);
					table131.addCell(hcell01111);

					hcell01111 = new PdfPCell(new Phrase("", headFont));
					hcell01111.setBorder(Rectangle.NO_BORDER);
					hcell01111.setHorizontalAlignment(Element.ALIGN_CENTER);
					table131.addCell(hcell01111);
					hcell01111.setHorizontalAlignment(Element.ALIGN_CENTER);

					hcell01111 = new PdfPCell(new Phrase("", headFont));
					hcell01111.setBorder(Rectangle.NO_BORDER);
					hcell01111.setHorizontalAlignment(Element.ALIGN_CENTER);
					table131.addCell(hcell01111);

					hcell01111 = new PdfPCell(new Phrase("", headFont));
					hcell01111.setBorder(Rectangle.NO_BORDER);
					table131.addCell(hcell01111);

					hcell01111 = new PdfPCell(new Phrase("", headFont));
					hcell01111.setBorder(Rectangle.NO_BORDER);
					table131.addCell(hcell01111);

					hcell01111 = new PdfPCell(new Phrase(String.valueOf(totalAccmAmt), headFont));
					hcell01111.setBorder(Rectangle.NO_BORDER);
					hcell01111.setHorizontalAlignment(Element.ALIGN_RIGHT);
					hcell01111.setPaddingRight(10);
					table131.addCell(hcell01111);

					for (ChargeBill chargeBillInfo3 : chargeBillListService) {

						if (chargeBillInfo3.getServiceId() != null) {
							if (chargeBillInfo3.getServiceId().getServiceType().equalsIgnoreCase("WARD CHARGES")) {
								if (chargeBillInfo3.getNetAmount() != 0) {

									String from = chargeBillInfo3.getInsertedDate().toString();
									Timestamp timestamp = Timestamp.valueOf(from);
									DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");

									Calendar calendar = Calendar.getInstance();
									calendar.setTimeInMillis(timestamp.getTime());

									String serviceDate = dateFormat.format(calendar.getTime());

									PdfPCell cell11;
									chargeBillInfo3.setServiceName(chargeBillInfo3.getServiceId().getServiceName());

									cell11 = new PdfPCell(new Phrase(
											String.valueOf(chargeBillInfo3.getServiceId().getServiceId()), redFont9));
									cell11.setBorder(Rectangle.NO_BORDER);
									cell11.setHorizontalAlignment(Element.ALIGN_LEFT);
									table281.addCell(cell11);

									cell11 = new PdfPCell(new Phrase(chargeBillInfo3.getServiceName(), redFont9));
									cell11.setBorder(Rectangle.NO_BORDER);
									cell11.setHorizontalAlignment(Element.ALIGN_LEFT);
									table281.addCell(cell11);

									cell11 = new PdfPCell(new Phrase(serviceDate, redFont9));
									cell11.setBorder(Rectangle.NO_BORDER);
									cell11.setHorizontalAlignment(Element.ALIGN_LEFT);
									table281.addCell(cell11);

									cell11 = new PdfPCell(
											new Phrase(String.valueOf(chargeBillInfo3.getQuantity()), redFont9));
									cell11.setBorder(Rectangle.NO_BORDER);
									cell11.setHorizontalAlignment(Element.ALIGN_CENTER);
									table281.addCell(cell11);

									cell11 = new PdfPCell(
											new Phrase(String.valueOf(chargeBillInfo3.getMrp()), redFont9));
									cell11.setBorder(Rectangle.NO_BORDER);
									cell11.setHorizontalAlignment(Element.ALIGN_CENTER);
									table281.addCell(cell11);

									cell11 = new PdfPCell(
											new Phrase(String.valueOf(chargeBillInfo3.getDiscount()), redFont9));
									cell11.setBorder(Rectangle.NO_BORDER);
									cell11.setHorizontalAlignment(Element.ALIGN_CENTER);
									table281.addCell(cell11);

									cell11 = new PdfPCell(
											new Phrase(String.valueOf(chargeBillInfo3.getNetAmount()), redFont9));
									cell11.setBorder(Rectangle.NO_BORDER);
									cell11.setHorizontalAlignment(Element.ALIGN_RIGHT);
									cell11.setPaddingRight(10);

									table281.addCell(cell11);

									// total += chargeBillInfo3.getNetAmount();
								}
							}
						} else {
							chargeBillInfo3.setServiceName("NOT APPLICABLE");
						}

					}
				}

			} else {
				hcell01111 = new PdfPCell(new Phrase("", headFont));
				hcell01111.setBorder(Rectangle.NO_BORDER);
				hcell01111.setHorizontalAlignment(Element.ALIGN_LEFT);
				table131.addCell(hcell01111);

				hcell01111 = new PdfPCell(new Phrase("", headFont));
				hcell01111.setBorder(Rectangle.NO_BORDER);
				hcell01111.setHorizontalAlignment(Element.ALIGN_CENTER);
				table131.addCell(hcell01111);

				hcell01111 = new PdfPCell(new Phrase("", headFont));
				hcell01111.setBorder(Rectangle.NO_BORDER);
				hcell01111.setHorizontalAlignment(Element.ALIGN_CENTER);
				table131.addCell(hcell01111);
				hcell01111.setHorizontalAlignment(Element.ALIGN_CENTER);

				hcell01111 = new PdfPCell(new Phrase("", headFont));
				hcell01111.setBorder(Rectangle.NO_BORDER);
				hcell01111.setHorizontalAlignment(Element.ALIGN_CENTER);
				table131.addCell(hcell01111);

				hcell01111 = new PdfPCell(new Phrase("", headFont));
				hcell01111.setBorder(Rectangle.NO_BORDER);
				table131.addCell(hcell01111);

				hcell01111 = new PdfPCell(new Phrase("", headFont));
				hcell01111.setBorder(Rectangle.NO_BORDER);
				table131.addCell(hcell01111);

				hcell01111 = new PdfPCell(new Phrase("", headFont));
				hcell01111.setBorder(Rectangle.NO_BORDER);
				hcell01111.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell01111.setPaddingRight(10);
				table131.addCell(hcell01111);

			}

			/*
			 * cell23.setColspan(2); cell23.addElement(table13); table.addCell(cell23);
			 */

			cell1.addElement(table131);
			cell1.addElement(table281);

			PdfPTable table132 = new PdfPTable(7);
			table132.setWidths(new float[] { 10f, 7f, 3f, 2.5f, 3f, 3f, 3f });
			table132.setSpacingBefore(10);
			table132.setWidthPercentage(105f);

			PdfPTable table282 = new PdfPTable(7);
			table282.setWidths(new float[] { 4f, 10f, 3f, 2.5f, 3f, 3f, 3f });
			table282.setSpacingBefore(10);
			table282.setWidthPercentage(105f);

			PdfPCell hcell01112;
			if (!chargeBillListService.isEmpty()) {
				if (chargeBillListService.stream()
						.filter((s) -> s.getServiceId().getServiceType().equalsIgnoreCase("EQUIPMENT CHARGES"))
						.count() > 0) {
					hcell01112 = new PdfPCell(new Phrase("EQUIPMENT CHARGES", headFont));
					hcell01112.setBorder(Rectangle.NO_BORDER);
					hcell01112.setHorizontalAlignment(Element.ALIGN_LEFT);
					table132.addCell(hcell01112);

					hcell01112 = new PdfPCell(new Phrase("", headFont));
					hcell01112.setBorder(Rectangle.NO_BORDER);
					hcell01112.setHorizontalAlignment(Element.ALIGN_CENTER);
					table132.addCell(hcell01112);

					hcell01112 = new PdfPCell(new Phrase("", headFont));
					hcell01112.setBorder(Rectangle.NO_BORDER);
					hcell01112.setHorizontalAlignment(Element.ALIGN_CENTER);
					table132.addCell(hcell01112);
					hcell01112.setHorizontalAlignment(Element.ALIGN_CENTER);

					hcell01112 = new PdfPCell(new Phrase("", headFont));
					hcell01112.setBorder(Rectangle.NO_BORDER);
					hcell01112.setHorizontalAlignment(Element.ALIGN_CENTER);
					table132.addCell(hcell01112);

					hcell01112 = new PdfPCell(new Phrase("", headFont));
					hcell01112.setBorder(Rectangle.NO_BORDER);
					table132.addCell(hcell01112);

					hcell01112 = new PdfPCell(new Phrase("", headFont));
					hcell01112.setBorder(Rectangle.NO_BORDER);
					table132.addCell(hcell01112);

					hcell01112 = new PdfPCell(new Phrase(String.valueOf(totalEqAmt), headFont));
					hcell01112.setBorder(Rectangle.NO_BORDER);
					hcell01112.setHorizontalAlignment(Element.ALIGN_RIGHT);
					hcell01112.setPaddingRight(10);
					table132.addCell(hcell01112);

					for (ChargeBill chargeBillInfo3 : chargeBillListService) {

						if (chargeBillInfo3.getServiceId() != null) {
							if (chargeBillInfo3.getServiceId().getServiceType().equalsIgnoreCase("EQUIPMENT CHARGES")) {
								if (chargeBillInfo3.getNetAmount() != 0) {

									String from = chargeBillInfo3.getInsertedDate().toString();
									Timestamp timestamp = Timestamp.valueOf(from);
									DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");

									Calendar calendar = Calendar.getInstance();
									calendar.setTimeInMillis(timestamp.getTime());

									String serviceDate = dateFormat.format(calendar.getTime());

									PdfPCell cell11;
									chargeBillInfo3.setServiceName(chargeBillInfo3.getServiceId().getServiceName());

									cell11 = new PdfPCell(new Phrase(
											String.valueOf(chargeBillInfo3.getServiceId().getServiceId()), redFont9));
									cell11.setBorder(Rectangle.NO_BORDER);
									cell11.setHorizontalAlignment(Element.ALIGN_LEFT);
									table282.addCell(cell11);

									cell11 = new PdfPCell(new Phrase(chargeBillInfo3.getServiceName(), redFont9));
									cell11.setBorder(Rectangle.NO_BORDER);
									cell11.setHorizontalAlignment(Element.ALIGN_LEFT);
									table282.addCell(cell11);

									cell11 = new PdfPCell(new Phrase(serviceDate, redFont9));
									cell11.setBorder(Rectangle.NO_BORDER);
									cell11.setHorizontalAlignment(Element.ALIGN_LEFT);
									table282.addCell(cell11);

									cell11 = new PdfPCell(
											new Phrase(String.valueOf(chargeBillInfo3.getQuantity()), redFont9));
									cell11.setBorder(Rectangle.NO_BORDER);
									cell11.setHorizontalAlignment(Element.ALIGN_CENTER);
									table282.addCell(cell11);

									cell11 = new PdfPCell(
											new Phrase(String.valueOf(chargeBillInfo3.getMrp()), redFont9));
									cell11.setBorder(Rectangle.NO_BORDER);
									cell11.setHorizontalAlignment(Element.ALIGN_CENTER);
									table282.addCell(cell11);

									cell11 = new PdfPCell(
											new Phrase(String.valueOf(chargeBillInfo3.getDiscount()), redFont9));
									cell11.setBorder(Rectangle.NO_BORDER);
									cell11.setHorizontalAlignment(Element.ALIGN_CENTER);
									table282.addCell(cell11);

									cell11 = new PdfPCell(
											new Phrase(String.valueOf(chargeBillInfo3.getNetAmount()), redFont9));
									cell11.setBorder(Rectangle.NO_BORDER);
									cell11.setHorizontalAlignment(Element.ALIGN_RIGHT);
									cell11.setPaddingRight(10);

									table282.addCell(cell11);

									// total += chargeBillInfo3.getNetAmount();
								}
							}
						} else {
							chargeBillInfo3.setServiceName("NOT APPLICABLE");
						}

					}

				}
			} else {
				hcell01112 = new PdfPCell(new Phrase("", headFont));
				hcell01112.setBorder(Rectangle.NO_BORDER);
				hcell01112.setHorizontalAlignment(Element.ALIGN_LEFT);
				table132.addCell(hcell01112);

				hcell01112 = new PdfPCell(new Phrase("", headFont));
				hcell01112.setBorder(Rectangle.NO_BORDER);
				hcell01112.setHorizontalAlignment(Element.ALIGN_CENTER);
				table132.addCell(hcell01112);

				hcell01112 = new PdfPCell(new Phrase("", headFont));
				hcell01112.setBorder(Rectangle.NO_BORDER);
				hcell01112.setHorizontalAlignment(Element.ALIGN_CENTER);
				table132.addCell(hcell01112);
				hcell01112.setHorizontalAlignment(Element.ALIGN_CENTER);

				hcell01112 = new PdfPCell(new Phrase("", headFont));
				hcell01112.setBorder(Rectangle.NO_BORDER);
				hcell01112.setHorizontalAlignment(Element.ALIGN_CENTER);
				table132.addCell(hcell01112);

				hcell01112 = new PdfPCell(new Phrase("", headFont));
				hcell01112.setBorder(Rectangle.NO_BORDER);
				table132.addCell(hcell01112);

				hcell01112 = new PdfPCell(new Phrase("", headFont));
				hcell01112.setBorder(Rectangle.NO_BORDER);
				table132.addCell(hcell01112);

				hcell01112 = new PdfPCell(new Phrase("", headFont));
				hcell01112.setBorder(Rectangle.NO_BORDER);
				hcell01112.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell01112.setPaddingRight(10);
				table132.addCell(hcell01112);

			}
			// -----------------------------------------

			/*
			 * cell23.setColspan(2); cell23.addElement(table13); table.addCell(cell23);
			 */

			cell1.addElement(table132);
			cell1.addElement(table282);

			PdfPTable table121 = new PdfPTable(7);
			table121.setWidths(new float[] { 10f, 7f, 3f, 2.5f, 3f, 3f, 3f });
			table121.setSpacingBefore(10);
			table121.setWidthPercentage(105f);

			PdfPTable table21 = new PdfPTable(7);
			table21.setWidths(new float[] { 4f, 10f, 3f, 2.5f, 3f, 3f, 3f });
			table21.setSpacingBefore(10);
			table21.setWidthPercentage(105f);

			PdfPCell hcell21;
			if (!chargeBillListService.isEmpty()) {
				if (chargeBillListService.stream()
						.filter((s) -> s.getServiceId().getServiceType().equalsIgnoreCase("CONSULTATION CHARGES"))
						.count() > 0) {
					hcell21 = new PdfPCell(new Phrase("CONSULTATION CHARGES", headFont));
					hcell21.setBorder(Rectangle.NO_BORDER);
					hcell21.setHorizontalAlignment(Element.ALIGN_LEFT);
					table121.addCell(hcell21);

					hcell21 = new PdfPCell(new Phrase("", headFont));
					hcell21.setBorder(Rectangle.NO_BORDER);
					hcell21.setHorizontalAlignment(Element.ALIGN_CENTER);
					table121.addCell(hcell21);

					hcell21 = new PdfPCell(new Phrase("", headFont));
					hcell21.setBorder(Rectangle.NO_BORDER);
					hcell21.setHorizontalAlignment(Element.ALIGN_CENTER);
					table121.addCell(hcell21);
					hcell21.setHorizontalAlignment(Element.ALIGN_CENTER);

					hcell21 = new PdfPCell(new Phrase("", headFont));
					hcell21.setBorder(Rectangle.NO_BORDER);
					hcell21.setHorizontalAlignment(Element.ALIGN_CENTER);
					table121.addCell(hcell21);

					hcell21 = new PdfPCell(new Phrase("", headFont));
					hcell21.setBorder(Rectangle.NO_BORDER);
					table121.addCell(hcell21);

					hcell21 = new PdfPCell(new Phrase("", headFont));
					hcell21.setBorder(Rectangle.NO_BORDER);
					table121.addCell(hcell21);

					hcell21 = new PdfPCell(new Phrase(String.valueOf(totalconAmt), headFont));
					hcell21.setBorder(Rectangle.NO_BORDER);
					hcell21.setHorizontalAlignment(Element.ALIGN_RIGHT);
					hcell21.setPaddingRight(10);
					table121.addCell(hcell21);

					for (ChargeBill chargeBillInfo3 : chargeBillListService) {

						if (chargeBillInfo3.getServiceId() != null) {
							if (chargeBillInfo3.getServiceId().getServiceType()
									.equalsIgnoreCase("CONSULTATION CHARGES")) {
								if (chargeBillInfo3.getNetAmount() != 0) {

									String from = chargeBillInfo3.getInsertedDate().toString();
									Timestamp timestamp = Timestamp.valueOf(from);
									DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");

									Calendar calendar = Calendar.getInstance();
									calendar.setTimeInMillis(timestamp.getTime());

									String serviceDate = dateFormat.format(calendar.getTime());

									PdfPCell cell11;
									chargeBillInfo3.setServiceName(chargeBillInfo3.getServiceId().getServiceName());

									cell11 = new PdfPCell(new Phrase(
											String.valueOf(chargeBillInfo3.getServiceId().getServiceId()), redFont9));
									cell11.setBorder(Rectangle.NO_BORDER);
									cell11.setHorizontalAlignment(Element.ALIGN_LEFT);
									table21.addCell(cell11);

									cell11 = new PdfPCell(new Phrase(chargeBillInfo3.getServiceName(), redFont9));
									cell11.setBorder(Rectangle.NO_BORDER);
									cell11.setHorizontalAlignment(Element.ALIGN_LEFT);
									table21.addCell(cell11);

									cell11 = new PdfPCell(new Phrase(serviceDate, redFont9));
									cell11.setBorder(Rectangle.NO_BORDER);
									cell11.setHorizontalAlignment(Element.ALIGN_LEFT);
									table21.addCell(cell11);

									cell11 = new PdfPCell(
											new Phrase(String.valueOf(chargeBillInfo3.getQuantity()), redFont9));
									cell11.setBorder(Rectangle.NO_BORDER);
									cell11.setHorizontalAlignment(Element.ALIGN_CENTER);
									table21.addCell(cell11);

									cell11 = new PdfPCell(
											new Phrase(String.valueOf(chargeBillInfo3.getMrp()), redFont9));
									cell11.setBorder(Rectangle.NO_BORDER);
									cell11.setHorizontalAlignment(Element.ALIGN_CENTER);
									table21.addCell(cell11);

									cell11 = new PdfPCell(
											new Phrase(String.valueOf(chargeBillInfo3.getDiscount()), redFont9));
									cell11.setBorder(Rectangle.NO_BORDER);
									cell11.setHorizontalAlignment(Element.ALIGN_CENTER);
									table21.addCell(cell11);

									cell11 = new PdfPCell(
											new Phrase(String.valueOf(chargeBillInfo3.getNetAmount()), redFont9));
									cell11.setBorder(Rectangle.NO_BORDER);
									cell11.setHorizontalAlignment(Element.ALIGN_RIGHT);
									cell11.setPaddingRight(10);

									table21.addCell(cell11);

									// total += chargeBillInfo3.getNetAmount();
								}
							}
						} else {
							chargeBillInfo3.setServiceName("NOT APPLICABLE");
						}

					}

				}
			} else {
				hcell21 = new PdfPCell(new Phrase("", headFont));
				hcell21.setBorder(Rectangle.NO_BORDER);
				hcell21.setHorizontalAlignment(Element.ALIGN_LEFT);
				table121.addCell(hcell21);

				hcell21 = new PdfPCell(new Phrase("", headFont));
				hcell21.setBorder(Rectangle.NO_BORDER);
				hcell21.setHorizontalAlignment(Element.ALIGN_CENTER);
				table121.addCell(hcell21);

				hcell21 = new PdfPCell(new Phrase("", headFont));
				hcell21.setBorder(Rectangle.NO_BORDER);
				hcell21.setHorizontalAlignment(Element.ALIGN_CENTER);
				table121.addCell(hcell21);
				hcell21.setHorizontalAlignment(Element.ALIGN_CENTER);

				hcell21 = new PdfPCell(new Phrase("", headFont));
				hcell21.setBorder(Rectangle.NO_BORDER);
				hcell21.setHorizontalAlignment(Element.ALIGN_CENTER);
				table121.addCell(hcell21);

				hcell21 = new PdfPCell(new Phrase("", headFont));
				hcell21.setBorder(Rectangle.NO_BORDER);
				table121.addCell(hcell21);

				hcell21 = new PdfPCell(new Phrase("", headFont));
				hcell21.setBorder(Rectangle.NO_BORDER);
				table121.addCell(hcell21);

				hcell21 = new PdfPCell(new Phrase("", headFont));
				hcell21.setBorder(Rectangle.NO_BORDER);
				hcell21.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell21.setPaddingRight(10);
				table121.addCell(hcell21);

			}

			// -----------------------------------------

			/*
			 * cell23.setColspan(2); cell23.addElement(table13); table.addCell(cell23);
			 */

			cell1.addElement(table121);
			cell1.addElement(table21);

			PdfPTable table182 = new PdfPTable(1);
			table182.setWidths(new float[] { 5f });
			table182.setSpacingBefore(10);
			table182.setWidthPercentage(100f);

			PdfPCell hcell072;
			hcell072 = new PdfPCell(
					new Phrase("_________________________________________________________________________", headFont));
			hcell072.setBorder(Rectangle.NO_BORDER);
			hcell072.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table182.addCell(hcell072);

			float totalAmount = 0;
			totalAmount = total + totalMed + totalServiceAmt + totalAccmAmt + totalEqAmt + totalconAmt;

			/*
			 * PdfPCell cell24 = new PdfPCell(); cell24.setBorder(Rectangle.NO_BORDER);
			 */
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

			hcell02 = new PdfPCell(new Phrase("NET AMOUNT", headFont));
			hcell02.setBorder(Rectangle.NO_BORDER);
			hcell02.setHorizontalAlignment(Element.ALIGN_RIGHT);
			hcell02.setPaddingRight(-51f);
			table14.addCell(hcell02);

			hcell02 = new PdfPCell(new Phrase(":", headFont));
			hcell02.setBorder(Rectangle.NO_BORDER);
			hcell02.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table14.addCell(hcell02);

			hcell02 = new PdfPCell(new Phrase(String.valueOf(Math.round(totalAmount)), headFont));
			hcell02.setBorder(Rectangle.NO_BORDER);
			hcell02.setHorizontalAlignment(Element.ALIGN_RIGHT);
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
			hcell021.setPaddingRight(-55f);
			table14.addCell(hcell021);

			hcell021 = new PdfPCell(new Phrase(":", headFont));
			hcell021.setBorder(Rectangle.NO_BORDER);
			hcell021.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table14.addCell(hcell021);

			hcell021 = new PdfPCell(new Phrase(String.valueOf(Math.round(totaPaidAmount)), headFont));
			hcell021.setBorder(Rectangle.NO_BORDER);
			hcell021.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table14.addCell(hcell021);

			PdfPCell hcell03;

			hcell03 = new PdfPCell(new Phrase("", headFont));
			hcell03.setBorder(Rectangle.NO_BORDER);
			hcell03.setHorizontalAlignment(Element.ALIGN_LEFT);
			table14.addCell(hcell03);

			hcell03 = new PdfPCell(new Phrase("", headFont));
			hcell03.setBorder(Rectangle.NO_BORDER);
			hcell03.setHorizontalAlignment(Element.ALIGN_LEFT);
			table14.addCell(hcell03);

			hcell03 = new PdfPCell(new Phrase("ADVANCE AMOUNT", headFont));
			hcell03.setBorder(Rectangle.NO_BORDER);
			hcell03.setHorizontalAlignment(Element.ALIGN_RIGHT);
			hcell03.setPaddingRight(-81f);
			table14.addCell(hcell03);

			hcell03 = new PdfPCell(new Phrase(":", headFont));
			hcell03.setBorder(Rectangle.NO_BORDER);
			hcell03.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table14.addCell(hcell03);

			hcell03 = new PdfPCell(new Phrase(String.valueOf(advanceAmt), headFont));
			hcell03.setBorder(Rectangle.NO_BORDER);
			hcell03.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table14.addCell(hcell03);

			PdfPCell hcell031;

			hcell031 = new PdfPCell(new Phrase("", headFont));
			hcell031.setBorder(Rectangle.NO_BORDER);
			hcell031.setHorizontalAlignment(Element.ALIGN_LEFT);
			table14.addCell(hcell031);

			hcell031 = new PdfPCell(new Phrase("", headFont));
			hcell031.setBorder(Rectangle.NO_BORDER);
			hcell031.setHorizontalAlignment(Element.ALIGN_LEFT);
			table14.addCell(hcell031);

			hcell031 = new PdfPCell(new Phrase(TYPE_OF_CHARGE_SETTLED_AMOUNT, headFont));
			hcell031.setBorder(Rectangle.NO_BORDER);
			hcell031.setHorizontalAlignment(Element.ALIGN_RIGHT);
			hcell031.setPaddingRight(-77f);
			table14.addCell(hcell031);

			hcell031 = new PdfPCell(new Phrase(":", headFont));
			hcell031.setBorder(Rectangle.NO_BORDER);
			hcell031.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table14.addCell(hcell031);

			hcell031 = new PdfPCell(new Phrase(String.valueOf(settledAmt), headFont));
			hcell031.setBorder(Rectangle.NO_BORDER);
			hcell031.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table14.addCell(hcell031);

			PdfPCell hcell0311;

			hcell0311 = new PdfPCell(new Phrase("", headFont));
			hcell0311.setBorder(Rectangle.NO_BORDER);
			hcell0311.setHorizontalAlignment(Element.ALIGN_LEFT);
			table14.addCell(hcell0311);

			hcell0311 = new PdfPCell(new Phrase("", headFont));
			hcell0311.setBorder(Rectangle.NO_BORDER);
			hcell0311.setHorizontalAlignment(Element.ALIGN_LEFT);
			table14.addCell(hcell0311);

			hcell0311 = new PdfPCell(new Phrase("DISCOUNT AMOUNT", headFont));
			hcell0311.setBorder(Rectangle.NO_BORDER);
			hcell0311.setHorizontalAlignment(Element.ALIGN_RIGHT);
			hcell0311.setPaddingRight(-83f);
			table14.addCell(hcell0311);

			hcell0311 = new PdfPCell(new Phrase(":", headFont));
			hcell0311.setBorder(Rectangle.NO_BORDER);
			hcell0311.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table14.addCell(hcell0311);

			hcell0311 = new PdfPCell(new Phrase(String.valueOf(discAmount), headFont));
			hcell0311.setBorder(Rectangle.NO_BORDER);
			hcell0311.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table14.addCell(hcell0311);

			PdfPCell hcell03111;

			hcell03111 = new PdfPCell(new Phrase("", headFont));
			hcell03111.setBorder(Rectangle.NO_BORDER);
			hcell03111.setHorizontalAlignment(Element.ALIGN_LEFT);
			table14.addCell(hcell03111);

			hcell03111 = new PdfPCell(new Phrase("", headFont));
			hcell03111.setBorder(Rectangle.NO_BORDER);
			hcell03111.setHorizontalAlignment(Element.ALIGN_LEFT);
			table14.addCell(hcell03111);

			hcell03111 = new PdfPCell(new Phrase("RETURN AMOUNT", headFont));
			hcell03111.setBorder(Rectangle.NO_BORDER);
			hcell03111.setHorizontalAlignment(Element.ALIGN_RIGHT);
			hcell03111.setPaddingRight(-72f);
			table14.addCell(hcell03111);

			hcell03111 = new PdfPCell(new Phrase(":", headFont));
			hcell03111.setBorder(Rectangle.NO_BORDER);
			hcell03111.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table14.addCell(hcell03111);

			hcell03111 = new PdfPCell(new Phrase(String.valueOf(returnAmount), headFont));
			hcell03111.setBorder(Rectangle.NO_BORDER);
			hcell03111.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table14.addCell(hcell03111);

			// float dueAmt = total - patientRegistration.getAdvanceAmount()+salesnetAmount;
			float dueAmt = totalAmount - discAmount + returnAmount - totaPaidAmount - advanceAmt - settledAmt;

			PdfPCell hcell04;

			hcell04 = new PdfPCell(new Phrase("", headFont));
			hcell04.setBorder(Rectangle.NO_BORDER);
			hcell04.setHorizontalAlignment(Element.ALIGN_LEFT);
			table14.addCell(hcell04);

			hcell04 = new PdfPCell(new Phrase("", headFont));
			hcell04.setBorder(Rectangle.NO_BORDER);
			hcell04.setHorizontalAlignment(Element.ALIGN_LEFT);
			table14.addCell(hcell04);

			hcell04 = new PdfPCell(new Phrase("DUE AMOUNT", headFont));
			hcell04.setBorder(Rectangle.NO_BORDER);
			hcell04.setHorizontalAlignment(Element.ALIGN_RIGHT);
			hcell04.setPaddingRight(-51f);
			table14.addCell(hcell04);

			hcell04 = new PdfPCell(new Phrase(":", headFont));
			hcell04.setBorder(Rectangle.NO_BORDER);
			hcell04.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table14.addCell(hcell04);

			hcell04 = new PdfPCell(new Phrase(String.valueOf(Math.round(dueAmt)), headFont));
			hcell04.setBorder(Rectangle.NO_BORDER);
			hcell04.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table14.addCell(hcell04);

			/*
			 * cell24.setColspan(2); cell24.addElement(table182);
			 * cell24.addElement(table14); table.addCell(cell24);
			 */

			cell1.addElement(table182);
			cell1.addElement(table14);

			/*
			 * PdfPCell cell25 = new PdfPCell(); cell25.setBorder(Rectangle.NO_BORDER);
			 */

			PdfPTable table15 = new PdfPTable(1);
			table15.setWidths(new float[] { 5f });
			table15.setSpacingBefore(10);
			table15.setWidthPercentage(100f);

			PdfPCell hcell05;
			hcell05 = new PdfPCell(new Phrase("\n \nADVANCE RECEIPT DETAILS", headFont));
			hcell05.setBorder(Rectangle.NO_BORDER);
			table15.addCell(hcell05);

			/*
			 * cell25.setColspan(2); cell25.addElement(table15); table.addCell(cell25);
			 */

			cell1.addElement(table15);

			/*
			 * PdfPCell cell26 = new PdfPCell(); cell26.setBorder(Rectangle.NO_BORDER);
			 */

			PdfPTable table16 = new PdfPTable(6);
			table16.setWidths(new float[] { 5f, 6f, 5f, 6f, 4f, 4f });
			table16.setSpacingBefore(10);
			table16.setWidthPercentage(100f);

			PdfPCell hcell06;
			hcell06 = new PdfPCell(new Phrase("Receipt No", headFont));
			hcell06.setBorder(Rectangle.NO_BORDER);
			hcell06.setBackgroundColor(BaseColor.GRAY);
			table16.addCell(hcell06);

			hcell06 = new PdfPCell(new Phrase("Receipt Date", headFont));
			hcell06.setBorder(Rectangle.NO_BORDER);
			hcell06.setBackgroundColor(BaseColor.GRAY);
			table16.addCell(hcell06);

			hcell06 = new PdfPCell(new Phrase("Receipt Name", headFont));
			hcell06.setBorder(Rectangle.NO_BORDER);
			hcell06.setBackgroundColor(BaseColor.GRAY);
			table16.addCell(hcell06);

			hcell06 = new PdfPCell(new Phrase("Mode Of Payment", headFont));
			hcell06.setBorder(Rectangle.NO_BORDER);
			hcell06.setBackgroundColor(BaseColor.GRAY);
			table16.addCell(hcell06);

			hcell06 = new PdfPCell(new Phrase("Receipt Amt", headFont));
			hcell06.setBorder(Rectangle.NO_BORDER);
			hcell06.setBackgroundColor(BaseColor.GRAY);
			table16.addCell(hcell06);

			hcell06 = new PdfPCell(new Phrase("Remarks", headFont));
			hcell06.setBorder(Rectangle.NO_BORDER);
			hcell06.setBackgroundColor(BaseColor.GRAY);
			table16.addCell(hcell06);

			for (PatientPayment patientPaymentInfo : patientPayment) {

				Timestamp timestamp = patientPaymentInfo.getInsertedDate();
				DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa ");
				Calendar calendar = Calendar.getInstance();
				calendar.setTimeInMillis(timestamp.getTime());
				String from = dateFormat.format(calendar.getTime());

				if (patientPaymentInfo.getTypeOfCharge().equalsIgnoreCase("ADVANCE")
						|| patientPaymentInfo.getTypeOfCharge().equalsIgnoreCase(TYPE_OF_CHARGE_SETTLED_AMOUNT)) {

					if (patientPaymentInfo.getAmount() != 0) {
						PdfPCell cell11;

						cell11 = new PdfPCell(new Phrase(String.valueOf(patientPaymentInfo.getBillNo()), redFont));
						cell11.setBorder(Rectangle.NO_BORDER);
						cell11.setHorizontalAlignment(Element.ALIGN_LEFT);
						table16.addCell(cell11);

						cell11 = new PdfPCell(new Phrase(from, redFont));
						cell11.setBorder(Rectangle.NO_BORDER);
						cell11.setHorizontalAlignment(Element.ALIGN_LEFT);
						table16.addCell(cell11);

						cell11 = new PdfPCell(new Phrase(patientPaymentInfo.getTypeOfCharge(), redFont));
						cell11.setBorder(Rectangle.NO_BORDER);
						cell11.setHorizontalAlignment(Element.ALIGN_LEFT);
						table16.addCell(cell11);

						cell11 = new PdfPCell(
								new Phrase(String.valueOf(patientPaymentInfo.getModeOfPaymant()), redFont));
						cell11.setBorder(Rectangle.NO_BORDER);
						cell11.setHorizontalAlignment(Element.ALIGN_LEFT);
						table16.addCell(cell11);

						cell11 = new PdfPCell(new Phrase(String.valueOf(patientPaymentInfo.getAmount()), redFont));
						cell11.setBorder(Rectangle.NO_BORDER);
						cell11.setHorizontalAlignment(Element.ALIGN_RIGHT);
						cell11.setPaddingRight(23f);
						table16.addCell(cell11);

						cell11 = new PdfPCell(new Phrase("", redFont));
						cell11.setBorder(Rectangle.NO_BORDER);
						cell11.setHorizontalAlignment(Element.ALIGN_RIGHT);
						table16.addCell(cell11);

						totalRecieptAmt = totalRecieptAmt + patientPaymentInfo.getAmount();
					}

				}

			}
			/*
			 * cell26.setColspan(2); cell26.addElement(table16); table.addCell(cell26);
			 */
			cell1.addElement(table16);

			PdfPTable table18 = new PdfPTable(1);
			table18.setWidths(new float[] { 5f });
			table18.setSpacingBefore(10);
			table18.setWidthPercentage(100f);

			PdfPCell hcell071;
			hcell071 = new PdfPCell(
					new Phrase("_________________________________________________________________________", headFont));
			hcell071.setBorder(Rectangle.NO_BORDER);
			hcell071.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table18.addCell(hcell071);

			/*
			 * PdfPCell cell27 = new PdfPCell(); cell27.setBorder(Rectangle.NO_BORDER);
			 */

			PdfPTable table17 = new PdfPTable(6);
			table17.setWidths(new float[] { 5f, 6f, 5f, 6f, 4f, 4f });
			table17.setSpacingBefore(10);
			table17.setWidthPercentage(100f);

			PdfPCell hcell07;
			hcell07 = new PdfPCell(new Phrase("", headFont));
			hcell07.setBorder(Rectangle.NO_BORDER);
			table17.addCell(hcell07);

			hcell07 = new PdfPCell(new Phrase("", headFont));
			hcell07.setBorder(Rectangle.NO_BORDER);
			table17.addCell(hcell07);

			hcell07 = new PdfPCell(new Phrase("Total : ", headFont));
			hcell07.setBorder(Rectangle.NO_BORDER);
			table17.addCell(hcell07);

			hcell07 = new PdfPCell(new Phrase("", headFont));
			hcell07.setBorder(Rectangle.NO_BORDER);
			table17.addCell(hcell07);

			hcell07 = new PdfPCell(new Phrase(String.valueOf(totalRecieptAmt), headFont));
			hcell07.setBorder(Rectangle.NO_BORDER);
			hcell07.setHorizontalAlignment(Element.ALIGN_CENTER);
			table17.addCell(hcell07);

			hcell07 = new PdfPCell(new Phrase("", headFont));
			hcell07.setBorder(Rectangle.NO_BORDER);
			table17.addCell(hcell07);

			/*
			 * cell27.setColspan(2); cell27.addElement(table18); cell27.addElement(table17);
			 * table.addCell(cell27);
			 */
			cell1.addElement(table18);
			cell1.addElement(table17);

			/*
			 * PdfPCell cell28 = new PdfPCell(); cell28.setBorder(Rectangle.NO_BORDER);
			 */

			PdfPTable table181 = new PdfPTable(2);
			table181.setWidths(new float[] { 8f, 8f });
			table181.setSpacingBefore(10);
			table181.setWidthPercentage(100f);

			PdfPCell hcell08;
			hcell08 = new PdfPCell(new Phrase("Total Received Amount In Words : ", headFont));
			hcell08.setBorder(Rectangle.NO_BORDER);
			hcell08.setPaddingTop(10f);
			table181.addCell(hcell08);

			hcell08 = new PdfPCell(
					new Phrase(numberToWordsConverter.convert(Math.round(totalAmount)) + " Rupees Only", redFont1));
			hcell08.setBorder(Rectangle.NO_BORDER);
			hcell08.setPaddingTop(10f);
			hcell08.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell08.setPaddingLeft(-50f);
			table181.addCell(hcell08);

			PdfPCell hcell081;
			hcell081 = new PdfPCell(new Phrase("Net Amount In Words : ", headFont));
			hcell081.setBorder(Rectangle.NO_BORDER);
			hcell081.setPaddingTop(10f);
			table181.addCell(hcell081);

			hcell081 = new PdfPCell(
					new Phrase(numberToWordsConverter.convert(Math.round(totalAmount)) + " Rupees Only", redFont1));

			hcell081.setBorder(Rectangle.NO_BORDER);
			hcell081.setPaddingTop(10f);
			hcell081.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell081.setPaddingLeft(-50f);
			table181.addCell(hcell081);

			/*
			 * cell28.setColspan(2); cell28.addElement(table181); table.addCell(cell28);
			 */

			cell1.addElement(table181);

			/*
			 * PdfPCell cell30 = new PdfPCell(); cell30.setBorder(Rectangle.NO_BORDER);
			 */
			PdfPTable table20 = new PdfPTable(1);
			table20.setWidths(new float[] { 8f });
			table20.setSpacingBefore(10);
			table20.setWidthPercentage(100f);

			PdfPCell hcell09;
			hcell09 = new PdfPCell(new Phrase("(Authorized Signature)", headFont));
			hcell09.setBorder(Rectangle.NO_BORDER);
			hcell09.setHorizontalAlignment(Element.ALIGN_RIGHT);
			hcell09.setPaddingTop(20f);
			table20.addCell(hcell09);

			/*
			 * cell30.setColspan(2); cell30.addElement(table20); table.addCell(cell30);
			 */

			cell1.addElement(table20);

			/*
			 * PdfPCell cell29 = new PdfPCell(); cell29.setBorder(Rectangle.NO_BORDER);
			 */

			PdfPTable table33 = new PdfPTable(4);
			table33.setWidthPercentage(100.0f);
			table33.setWidths(new int[] { 3, 4, 3, 4 });
			table33.setSpacingBefore(10);

			PdfPCell hcell16;
			hcell16 = new PdfPCell(new Phrase("Created By                    :  ", headFont));
			hcell16.setBorder(Rectangle.NO_BORDER);
			hcell16.setPaddingLeft(-10f);
			table33.addCell(hcell16);

			hcell16 = new PdfPCell(new Phrase(createdName, redFont1));
			hcell16.setBorder(Rectangle.NO_BORDER);
			// hcell16.setPaddingRight(-70f);
			hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
			table33.addCell(hcell16);

			hcell16 = new PdfPCell(new Phrase("Created Dt                   : ", headFont));
			hcell16.setBorder(Rectangle.NO_BORDER);
			hcell16.setPaddingLeft(-20f);
			table33.addCell(hcell16);

			hcell16 = new PdfPCell(new Phrase(today, redFont1));
			hcell16.setBorder(Rectangle.NO_BORDER);
			// hcell16.setPaddingRight(-70f);
			hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
			table33.addCell(hcell16);

			PdfPCell hcell161;
			hcell161 = new PdfPCell(new Phrase("Printed By                     :  ", headFont));
			hcell161.setBorder(Rectangle.NO_BORDER);
			hcell161.setPaddingLeft(-10f);
			table33.addCell(hcell161);

			hcell161 = new PdfPCell(new Phrase(createdName, redFont1));
			hcell161.setBorder(Rectangle.NO_BORDER);
			// hcell161.setPaddingRight(-70f);
			hcell161.setHorizontalAlignment(Element.ALIGN_LEFT);
			table33.addCell(hcell161);

			hcell161 = new PdfPCell(new Phrase("Printed Dt                    : ", headFont));
			hcell161.setBorder(Rectangle.NO_BORDER);
			hcell161.setPaddingLeft(-20f);
			table33.addCell(hcell161);

			hcell161 = new PdfPCell(new Phrase(today, redFont1));
			hcell161.setBorder(Rectangle.NO_BORDER);
			// hcell161.setPaddingRight(-70f);
			hcell161.setHorizontalAlignment(Element.ALIGN_LEFT);
			table33.addCell(hcell161);

			/*
			 * cell29.setColspan(2); cell29.addElement(table33); table.addCell(cell29);
			 */

			cell1.addElement(table33);
			table.addCell(cell1);

			document.add(table);

			document.close();

			System.out.println("finished");

			pdfByte = byteArrayOutputStream.toByteArray();
			String uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/sales/viewFile/")
					.path(salesPaymentPdfServiceImpl.getNextId()).toUriString();
			 salesPaymentPdf = salesPaymentPdfServiceImpl
					.getSalesPdf(pdfBill + "-" + regIde + "Ip Final Bill");
			if (salesPaymentPdf != null) {
				salesPaymentPdf.setData(pdfByte);
				salesPaymentPdfServiceImpl.save(salesPaymentPdf);

			} else {
				salesPaymentPdf = new SalesPaymentPdf();
				salesPaymentPdf.setFileName(pdfBill + "-" + regIde + "Ip Final Bill");
				salesPaymentPdf.setFileuri(uri);
				salesPaymentPdf.setPid(salesPaymentPdfServiceImpl.getNextId());
				salesPaymentPdf.setData(pdfByte);
				salesPaymentPdfServiceImpl.save(salesPaymentPdf);

			}
			System.out.println(salesPaymentPdf);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "ip final bill suuccessfully";

	}

	// @RequestMapping(value = "/ipPdf/{regIde}")
	public void ipSettledPdfGenerator(@PathVariable("regIde") String regIde, Principal principal) {

		String patientName = null;
		String EMPTY_SPACE = " ";
		String umr = null;
		String consultant = null;
		float amount = 0;
		String paymentNextBillNo = null;
		String paymentType = null;
		String refNo = null;
		String finalDate = null;
		PatientRegistration patientRegistration = patientRegistrationServiceImpl.findByRegId(regIde);

		List<ChargeBill> chargeBillListq = chargeBillServiceImpl.findByPatRegId(patientRegistration);
		ChargeBill chargeFor = chargeBillListq.get(0);
		Date date = chargeFor.getDichargedDate();
		DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
		String today = formatter.format(date).toString();

		Date dateAdv = chargeFor.getDichargedDate();
		DateFormat formatterAdv = new SimpleDateFormat("dd-MMM-yyyy");
		String advDate = formatterAdv.format(dateAdv).toString();

		// udser details
		String createdBy = chargeFor.getUpdatedBy();
		User userInfo = userServiceImpl.findOneByUserId(createdBy);
		String createdName = null;
		createdName = (userInfo.getMiddleName() != null)
				? (userInfo.getFirstName() + EMPTY_SPACE + userInfo.getMiddleName() + EMPTY_SPACE
						+ userInfo.getLastName())
				: (userInfo.getFirstName() + EMPTY_SPACE + userInfo.getLastName());

		Date date2 = patientRegistration.getDateOfJoining();
		DateFormat formatter2 = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
		String admissionDate = formatter2.format(date2).toString();

		byte[] pdfBytes = null;
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

		if (patientRegistration.getVuserD().getDoctorDetails() != null) {
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

		// for advance
		Set<PatientPayment> patientPayment = patientRegistration.getPatientPayment();

		for (PatientPayment patientPaymentInfo : patientPayment) {
			if (patientPaymentInfo.getTypeOfCharge().equalsIgnoreCase("SETTLED AMOUNT")) {
				amount += patientPaymentInfo.getAmount();
				paymentNextBillNo = patientPaymentInfo.getBillNo();
				paymentType = patientPaymentInfo.getModeOfPaymant();
				refNo = patientPaymentInfo.getReferenceNumber();
				finalDate = patientPaymentInfo.getInsertedDate().toString().substring(0, 15);
			}

		}

		// for final billing
		FinalBilling finalBilling = finalBillingRepository.paymentUpdate("Ip Final Billing", paymentNextBillNo,
				finalDate);

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

		Font redFont2 = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
		Font redFont3 = new Font(Font.FontFamily.HELVETICA, 12, Font.UNDERLINE);
		Font redFont4 = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
		Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
		Font redFont9 = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.NORMAL);
		Font redFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
		Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);

		SalesPaymentPdf salesPaymentPdf = null;
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

			hcell4 = new PdfPCell(new Phrase(paymentNextBillNo, redFont));
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
			hcell19 = new PdfPCell(new Phrase("Final Amount Reciept", headFont1));
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

			hcell14 = new PdfPCell(new Phrase("Final Settled.Amt", redFont));
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
			if (finalBilling.getCardAmount() != 0) {
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

				hcell20 = new PdfPCell(new Phrase(refNo, redFont));
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
			if (finalBilling.getCashAmount() != 0) {
				hcell161 = new PdfPCell(new Phrase("Cash Amt", redFont));
				hcell161.setBorder(Rectangle.NO_BORDER);
				hcell161.setPaddingTop(10f);
				hcell161.setPaddingLeft(-50f);
				table3.addCell(hcell161);

				hcell161 = new PdfPCell(new Phrase(COLON, redFont));
				hcell161.setBorder(Rectangle.NO_BORDER);
				hcell161.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell161.setPaddingTop(10f);
				hcell161.setPaddingLeft(-65f);
				table3.addCell(hcell161);

				hcell161 = new PdfPCell(new Phrase(String.valueOf(finalBilling.getCashAmount()), redFont));
				hcell161.setBorder(Rectangle.NO_BORDER);
				hcell161.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell161.setPaddingTop(10f);
				hcell161.setPaddingLeft(-80f);
				table3.addCell(hcell161);

			} else {
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

			if (finalBilling.getCardAmount() != 0) {
				hcell161 = new PdfPCell(new Phrase("Card Amt", redFont));
				hcell161.setBorder(Rectangle.NO_BORDER);
				hcell161.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell161.setPaddingTop(10f);
				table3.addCell(hcell161);

				hcell161 = new PdfPCell(new Phrase(COLON, redFont));
				hcell161.setBorder(Rectangle.NO_BORDER);
				hcell161.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell161.setPaddingTop(10f);
				hcell161.setPaddingLeft(-27f);
				table3.addCell(hcell161);

				hcell161 = new PdfPCell(new Phrase(String.valueOf(finalBilling.getCardAmount()), redFont));
				hcell161.setBorder(Rectangle.NO_BORDER);
				hcell161.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell161.setPaddingTop(10f);
				hcell161.setPaddingLeft(-40f);
				table3.addCell(hcell161);
			} else {
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

			if (finalBilling.getDueAmount() != 0) {
				hcell1611 = new PdfPCell(new Phrase("due Amt", redFont));
				hcell1611.setBorder(Rectangle.NO_BORDER);
				hcell1611.setPaddingTop(10f);
				hcell1611.setPaddingLeft(-50f);
				table3.addCell(hcell1611);

				hcell1611 = new PdfPCell(new Phrase(COLON, redFont));
				hcell1611.setBorder(Rectangle.NO_BORDER);
				hcell1611.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1611.setPaddingTop(10f);
				hcell1611.setPaddingLeft(-65f);
				table3.addCell(hcell1611);

				hcell1611 = new PdfPCell(new Phrase(String.valueOf(finalBilling.getDueAmount()), redFont));
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

			pdfBytes = byteArrayOutputStream.toByteArray();
			String uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/sales/viewFile/")
					.path(salesPaymentPdfServiceImpl.getNextId()).toUriString();
			salesPaymentPdf = salesPaymentPdfServiceImpl.getSalesPdf(regIde + "-" + "Final Advance Reciept");
			if (salesPaymentPdf != null) {
				salesPaymentPdf.setData(pdfBytes);
				salesPaymentPdfServiceImpl.save(salesPaymentPdf);

			} else {
				salesPaymentPdf = new SalesPaymentPdf();
				salesPaymentPdf.setFileName(regIde + "-" + "Final Advance Reciept");
				salesPaymentPdf.setFileuri(uri);
				salesPaymentPdf.setPid(salesPaymentPdfServiceImpl.getNextId());
				salesPaymentPdf.setData(pdfBytes);
				salesPaymentPdfServiceImpl.save(salesPaymentPdf);

			}

		} catch (Exception e) {
		}

	}

}
