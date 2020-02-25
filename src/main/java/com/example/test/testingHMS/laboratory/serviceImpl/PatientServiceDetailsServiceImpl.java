package com.example.test.testingHMS.laboratory.serviceImpl;

import java.io.ByteArrayOutputStream;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.test.testingHMS.bed.model.RoomBookingDetails;
import com.example.test.testingHMS.bed.model.RoomDetails;
import com.example.test.testingHMS.laboratory.helper.MyFooter;
import com.example.test.testingHMS.laboratory.helper.RefMeasureDetails;
import com.example.test.testingHMS.laboratory.model.LabServiceRange;
import com.example.test.testingHMS.laboratory.model.LabServices;
import com.example.test.testingHMS.laboratory.model.LaboratoryRegistration;
import com.example.test.testingHMS.laboratory.model.PatientServiceDetails;
import com.example.test.testingHMS.laboratory.model.ServicePdf;
import com.example.test.testingHMS.laboratory.repository.PatientServiceDetailsRepository;
import com.example.test.testingHMS.laboratory.service.PatientServiceDetailsService;
import com.example.test.testingHMS.patient.model.PatientRegistration;
import com.example.test.testingHMS.patient.serviceImpl.PatientRegistrationServiceImpl;
import com.example.test.testingHMS.user.model.User;
import com.example.test.testingHMS.user.serviceImpl.UserServiceImpl;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;

@Service
public class PatientServiceDetailsServiceImpl implements PatientServiceDetailsService {
	
	public static Logger Logger=LoggerFactory.getLogger(PatientServiceDetailsServiceImpl.class);
	
	
	@Autowired
	PatientServiceDetailsRepository patientServiceDetailsRepository;

	@Autowired
	LabServicesServiceImpl labServicesServiceImpl;

	@Autowired
	ResourceLoader resourceLoader;

	@Autowired
	ServicePdfServiceImpl servicePdfServiceImpl;

	@Autowired
	UserServiceImpl userServiceImpl;

	@Autowired
	PatientServiceDetailsServiceImpl patientServiceDetailsServiceImpl;

	@Autowired
	PatientRegistrationServiceImpl patientRegistrationServiceImpl;

	public String getNextId() {
		PatientServiceDetails patientServiceDetails = patientServiceDetailsRepository
				.findFirstByOrderByPatServiceIdDesc();
		String nextId = null;
		if (patientServiceDetails == null) {
			nextId = "PSD0000001";
		} else {
			int nextIntId = Integer.parseInt(patientServiceDetails.getPatServiceId().substring(3));
			nextIntId += 1;
			nextId = "PSD" + String.format("%07d", nextIntId);

		}
		return nextId;
	}

	public void save(PatientServiceDetails patientServiceDetails) {
		patientServiceDetailsRepository.save(patientServiceDetails);
	}

	public PatientServiceDetails findByRegId(String id) {
		return patientServiceDetailsRepository.findByPatientService(id);
	}

	

	public ServicePdf saveInfo(PatientServiceDetails patientServiceDetails, Principal principal) {
		String regId = null;

		List<LabServiceRange> labServiceRange = null;

		PatientRegistration patientRegistration = patientRegistrationServiceImpl
				.findByRegId(patientServiceDetails.getRegId());
		regId = patientServiceDetails.getRegId();
		patientServiceDetails.setPatientService(patientRegistration);
		System.out.println(patientServiceDetails.getServiceName());
		System.out.println(patientServiceDetails.getRegId());
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
		LabServices labServices = labServicesServiceImpl.findPriceByType(patientServiceDetails.getServiceName(),
				patientRegistration.getpType(),
				roomType);
		if (labServices == null) {
			System.out.println("ITS NULL");
		} else {
			System.out.println("ITS NOT NULL " + labServices.getServiceId());
		}

		patientServiceDetails.setPatientLabService(labServices);
		patientServiceDetails.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));

		
		// createdBy (Security)
		User userSecurity = userServiceImpl.findByUserName(principal.getName());
		String createdBy = userSecurity.getFirstName() + " " + userSecurity.getMiddleName() + " "
				+ userSecurity.getLastName();
		patientServiceDetails.setCreatedBy(createdBy);

		// for admission date

		String admisionDate = patientRegistration.getRegDate().toString();
		Timestamp timestamp = Timestamp.valueOf(admisionDate);
		DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa ");

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timestamp.getTime());

		String pdfadmissionDate = dateFormat.format(calendar.getTime());

		// for pdf

		List<LaboratoryRegistration> laboratoryRegistrationsInfo = null;

		List<RefMeasureDetails> refMeasureDetails = patientServiceDetails.getRefMeasureDetails();

		if (!refMeasureDetails.isEmpty()) {
			for (RefMeasureDetails refMeasureDetailsInfo : refMeasureDetails) {
				patientServiceDetails.setPatServiceId(patientServiceDetailsServiceImpl.getNextId());
				List<LabServices> li = new ArrayList<>();
				li.add(labServices);
				String newAge = patientRegistration.getPatientDetails().getAge();
				String ageType = null;
				int age = 0;
				LocalDate today = LocalDate.now();
				LocalDate birthday = patientRegistration.getPatientDetails().getDob().toLocalDateTime().toLocalDate();
				Period p = Period.between(birthday, today);
				System.out.println("Days" + p.getDays() + "months" + p.getMonths() + "years" + p.getYears());
				// System.out.println("service
				// id"+labServices.get(0).getServiceId());

				// String
				// newAge=patientRegistration.getPatientDetails().getAge();
				String gender = null;

				if (patientServiceDetails.getServiceName().equalsIgnoreCase("LFT - LIVER FUNCTON TESTS")
						|| patientServiceDetails.getServiceName().equalsIgnoreCase("SERUM BILIRUBIN")) {

					if (p.getDays() >= 0 && p.getMonths() == 0 && p.getYears() == 0) {

						age = p.getDays();
						ageType = "days";
						gender = "CHILDREN";
					} else if (p.getDays() >= 0 && p.getMonths() > 0 && p.getYears() >= 0) {

						age = p.getMonths();
						ageType = "months";
						gender = patientRegistration.getPatientDetails().getGender();
					}

					laboratoryRegistrationsInfo = patientRegistration.getLaboratoryRegistration();

					labServiceRange = labServicesServiceImpl.findNewMeasures(li, age, gender, ageType);

				} else if (patientServiceDetails.getServiceName().equalsIgnoreCase("COMPLETE BLOOD PICTURE")) {
					if (p.getDays() >= 0 && p.getMonths() == 0 && p.getYears() == 0) {

						age = p.getDays();
						ageType = "days";
						gender = "CHILDREN";
					} else if (p.getDays() >= 0 && p.getMonths() >= 0 && p.getYears() < 2) {

						age = p.getMonths();
						ageType = "months";
						gender = "CHILDREN";
					} else if (p.getDays() >= 0 && p.getMonths() >= 0 && p.getYears() > 2 && p.getYears() < 13) {

						age = p.getYears();
						ageType = "years";
						gender = "CHILDREN";
					} else if (p.getDays() >= 0 && p.getMonths() >= 0 && p.getYears() > 13) {
						age = p.getYears();
						ageType = "years";
						gender = patientRegistration.getPatientDetails().getGender();

					}

					laboratoryRegistrationsInfo = patientRegistration.getLaboratoryRegistration();

					labServiceRange = labServicesServiceImpl.findNewMeasures(li, age, gender, ageType);

				} else {

					newAge = patientRegistration.getPatientDetails().getAge();
					/*
					 * if(newAge.contains("/")) {
					 * newAge=newAge.substring(newAge.indexOf("M")+2,newAge.
					 * indexOf("Y")); }
					 */

					float age1 = p.getYears();
					int labAge = 0;
					gender = null;

					if (age1 <= 12) {
						labAge = (int) age1;
						gender = "CHILDREN";
					} else

					{
						labAge = (int) age1;
						gender = patientRegistration.getPatientDetails().getGender();

					}

					laboratoryRegistrationsInfo = patientRegistration.getLaboratoryRegistration();

					labServiceRange = labServicesServiceImpl.findMeasures(li, labAge, gender);

				}

				for (LabServiceRange labServiceRangeInfo : labServiceRange) {

					if (labServiceRangeInfo.getMeasureName().equals(refMeasureDetailsInfo.getMeasure())) {
						if (labServiceRangeInfo.getMinRange() != null && labServiceRangeInfo.getMaxRange() != null) {
							refMeasureDetailsInfo.setDimension(labServiceRangeInfo.getParameter());
							refMeasureDetailsInfo.setGender(labServiceRangeInfo.getGender());
							refMeasureDetailsInfo.setRange("[" + labServiceRangeInfo.getMinRange() + "-"
									+ labServiceRangeInfo.getMaxRange() + "]");
							refMeasureDetailsInfo.setMethod(labServiceRangeInfo.getMethod());
							patientServiceDetails.setPatientLabServiceRange(labServiceRangeInfo);
							patientServiceDetails.setActualValue(refMeasureDetailsInfo.getValue());
						} else if (labServiceRangeInfo.getMinRange() != null
								|| labServiceRangeInfo.getMaxRange() != null) {
							refMeasureDetailsInfo.setDimension(labServiceRangeInfo.getParameter());
							refMeasureDetailsInfo.setGender(labServiceRangeInfo.getGender());
							if (labServiceRangeInfo.getMinRange() == null) {
								refMeasureDetailsInfo.setRange("[" + labServiceRangeInfo.getMaxRange() + "]");
							} else {
								refMeasureDetailsInfo.setRange("[" + labServiceRangeInfo.getMinRange() + "]");
							}
							refMeasureDetailsInfo.setMethod(labServiceRangeInfo.getMethod());
							patientServiceDetails.setPatientLabServiceRange(labServiceRangeInfo);
							patientServiceDetails.setActualValue(refMeasureDetailsInfo.getValue());
						} else {
							refMeasureDetailsInfo.setDimension(labServiceRangeInfo.getParameter());
							refMeasureDetailsInfo.setGender(labServiceRangeInfo.getGender());
							refMeasureDetailsInfo.setRange(" ");
							refMeasureDetailsInfo.setMethod(labServiceRangeInfo.getMethod());
							patientServiceDetails.setPatientLabServiceRange(labServiceRangeInfo);
							patientServiceDetails.setActualValue(refMeasureDetailsInfo.getValue());
						}
					}
				}
				patientServiceDetailsServiceImpl.save(patientServiceDetails);

			}

		}

		Date date = new Date(laboratoryRegistrationsInfo.get(0).getEnteredDate().getTime());
		DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
		String bill = formatter.format(date).toString();

		String admittedWard = null;
		String roomName = null;

		List<RoomBookingDetails> roomBookingDetails = patientRegistration.getRoomBookingDetails();
		if (!roomBookingDetails.isEmpty()) {
			for (RoomBookingDetails roomBookingDetailsInfo : roomBookingDetails) {
				RoomDetails roomDetails = roomBookingDetailsInfo.getRoomDetails();
				admittedWard = roomDetails.getRoomType();
				roomName = roomDetails.getRoomName();
			}
		}

		// PDF

		Resource fileResourcee = resourceLoader.getResource("classpath:udbhava.png");

		Image img = null;
		try {
			img = Image.getInstance(fileResourcee.getFile().getAbsolutePath());
		} catch (Exception e1) {
			Logger.error(e1.getMessage());
		}
		img.scaleAbsolute(100, 100);
		Phrase pq = new Phrase(new Chunk(img, 0, -90));

		ServicePdf servicePdf = new ServicePdf();
		if (patientServiceDetails.getServiceName().equalsIgnoreCase("SERUM ELECTROLYTES")) {
			byte[] pdfBytes = null;
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			final Font blueFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
			final Font font = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
			final Font font2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD | Font.UNDERLINE);
			final Font font1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

			final Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
			final Font headFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);

			Document document = new Document();

			try {
				PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);
				MyFooter event = new MyFooter(principal, userServiceImpl);

				writer.setPageEvent(event);

				document.open();

				document.add(pq);

				Paragraph p51 = new Paragraph("UDBHAVA HOSPITALS", headFont);
				p51.setAlignment(Element.ALIGN_CENTER);

				document.add(p51);

				// Display a date in day, month, year format
				date = Calendar.getInstance().getTime();
				formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
				String today = formatter.format(date).toString();

				Font redFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

				Paragraph p9 = new Paragraph("\n \n \n   DEPARTMENT OF BIOCHEMISTRY", blueFont);
				p9.add("\n ");
				p9.setAlignment(Element.ALIGN_CENTER);
				document.add(p9);

				PdfPTable table2 = new PdfPTable(2);
				
				PdfPCell cell1 = new PdfPCell();
				table2.setWidthPercentage(100f);	
				cell1.setBorder(Rectangle.BOTTOM|Rectangle.TOP);
				
				PdfPTable table21 = new PdfPTable(6);
				table21.setWidths(new float[] { 5f,1f,5f,5f,1f,6.5f});
				 table21.setSpacingBefore(10);
				

				PdfPCell hcell1;
				hcell1 = new PdfPCell(
						new Phrase("Patient Name", redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(
						new Phrase(":", redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(
						new Phrase(patientRegistration.getPatientDetails().getTitle() +". "+ patientRegistration.getPatientDetails().getFirstName()
								+ " " + patientRegistration.getPatientDetails().getLastName(), redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase("Age/Gender", redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase(":", redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getAge()
						+ "/" + patientRegistration.getPatientDetails().getGender(), redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				
				
				PdfPCell hcell4;
				hcell4 = new PdfPCell(
						new Phrase("Admission No", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(
						new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(
						new Phrase(patientRegistration.getRegId(), redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				


				if(admittedWard!=null)
				{
				hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(admittedWard, redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				}
				else
				{
					hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					
					hcell4 = new PdfPCell(new Phrase(":", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					hcell4 = new PdfPCell(new Phrase("", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);

				}



				PdfPCell hcell15;
				hcell15 = new PdfPCell(
						new Phrase("Admission Date", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				hcell15 = new PdfPCell(
						new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				hcell15 = new PdfPCell(
						new Phrase(pdfadmissionDate, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				
				if(roomName!=null)
				{
				hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(roomName, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				}
				else
				{
					hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase(":", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase("", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
				}
				PdfPCell hcell16;
				hcell16 = new PdfPCell(new Phrase(
						"Bill No/UMR No", redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(
						":", redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getUmr(), redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);

				hcell16 = new PdfPCell(new Phrase("Bill Date", redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(":", redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(bill, redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);

				PdfPCell hcell17;
				hcell17 = new PdfPCell(new Phrase(
						"Consultant",
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(
						":",
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(
						patientRegistration.getPatientDetails().getConsultant(),
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);

				hcell17 = new PdfPCell(new Phrase("Report Date", redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(":", redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(today, redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				cell1.setColspan(2);
				cell1.addElement(table21);
				table2.addCell(cell1);
             //  table2.addCell(cell1);
               document.add(table2);

				Paragraph p12 = new Paragraph("   SERUM ELECTROLYTES ", blueFont);
				p12.setAlignment(Element.ALIGN_CENTER);
				document.add(p12);

				cell1.setFixedHeight(107f);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);

				cell1.setColspan(2);

				document.add(cell1);

				PdfPCell cell3 = new PdfPCell();
				PdfPTable table3 = new PdfPTable(4);
				table3.setWidths(new float[] { 4f, 2f, 4.5f, 2f });

				PdfPCell hcell2;
				hcell2 = new PdfPCell(new Phrase("\n PARAMETER ", font2));
				hcell2.setBorder(Rectangle.NO_BORDER);
				hcell2.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell2.setPaddingLeft(-50f);
				table3.addCell(hcell2);

				hcell2 = new PdfPCell(new Phrase(" \n RESULTS", font2));
				hcell2.setBorder(Rectangle.NO_BORDER);
				hcell2.setHorizontalAlignment(Element.ALIGN_CENTER);
				table3.addCell(hcell2);

				hcell2 = new PdfPCell(new Phrase(" \n NORMAL VALUES", font2));
				hcell2.setBorder(Rectangle.NO_BORDER);
				hcell2.setHorizontalAlignment(Element.ALIGN_CENTER);
				table3.addCell(hcell2);

				hcell2 = new PdfPCell(new Phrase(" \n METHOD", font2));
				hcell2.setBorder(Rectangle.NO_BORDER);

				hcell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table3.addCell(hcell2);

				Paragraph p13 = new Paragraph("   SERUM ELECTROLYTES ", font);
				p13.setAlignment(Element.ALIGN_LEFT);

				document.add(table3);
				document.add(p13);

				cell3.setFixedHeight(107f);
				cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell3.setColspan(2);
				document.add(cell3);

				for (RefMeasureDetails refMeasureDetailsInfo : refMeasureDetails) {

					if (refMeasureDetailsInfo.getMeasure().equalsIgnoreCase("Sodium")
							|| refMeasureDetailsInfo.getMeasure().equalsIgnoreCase("Potassium")
							|| refMeasureDetailsInfo.getMeasure().equalsIgnoreCase("Chloride")) {

						PdfPCell cell4 = new PdfPCell();
						PdfPTable table5 = new PdfPTable(4);
						table5.setWidths(new float[] { 4f, 2f, 4.5f, 2f });

						PdfPCell hcell5;
						hcell5 = new PdfPCell(new Phrase(refMeasureDetailsInfo.getMeasure(), font1));

						hcell5.setBorder(Rectangle.NO_BORDER);
						hcell5.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell5.setPaddingLeft(-50f);
						table5.addCell(hcell5);
						if (refMeasureDetailsInfo.getDimension() != null) {
							hcell5 = new PdfPCell(new Phrase(String.valueOf(
									refMeasureDetailsInfo.getValue() + " " + refMeasureDetailsInfo.getDimension()),
									font1));
							hcell5.setBorder(Rectangle.NO_BORDER);

							hcell5.setHorizontalAlignment(Element.ALIGN_CENTER);
							table5.addCell(hcell5);
						} else {

							hcell5 = new PdfPCell(new Phrase(String.valueOf(refMeasureDetailsInfo.getValue()), font1));
							hcell5.setBorder(Rectangle.NO_BORDER);

							hcell5.setHorizontalAlignment(Element.ALIGN_CENTER);
							table5.addCell(hcell5);
						}

						if (refMeasureDetailsInfo.getDimension() != null) {

							hcell5 = new PdfPCell(new Phrase(
									refMeasureDetailsInfo.getRange() + " " + refMeasureDetailsInfo.getDimension(),
									font1));
							hcell5.setBorder(Rectangle.NO_BORDER);
							hcell5.setHorizontalAlignment(Element.ALIGN_CENTER);
							table5.addCell(hcell5);
						} else {
							hcell5 = new PdfPCell(new Phrase(refMeasureDetailsInfo.getRange(), font1));
							hcell5.setBorder(Rectangle.NO_BORDER);
							hcell5.setHorizontalAlignment(Element.ALIGN_CENTER);
							table5.addCell(hcell5);
						}

						hcell5 = new PdfPCell(new Phrase(refMeasureDetailsInfo.getMethod(), font1));
						hcell5.setBorder(Rectangle.NO_BORDER);
						hcell5.setHorizontalAlignment(Element.ALIGN_RIGHT);
						table5.addCell(hcell5);

						document.add(table5);

						cell4.setFixedHeight(107f);
						cell4.setHorizontalAlignment(Element.ALIGN_CENTER);

						cell4.setColspan(2);
						document.add(cell4);

					}

				}

				if (patientServiceDetails.getComment().isEmpty()) {
					Paragraph p27 = new Paragraph("\n Findings:", font);
					document.add(p27);
					Paragraph p28 = new Paragraph("N/A", font1);
					document.add(p28);

				} else {
					Paragraph p27 = new Paragraph("\n Findings:", font);

					Paragraph p28 = new Paragraph(patientServiceDetails.getComment(), font1);
					document.add(p27);
					document.add(p28);
				}

				Paragraph p16 = new Paragraph("\n \n \n \n ******End Of Report****** ", font1);
				p16.setAlignment(Element.ALIGN_CENTER);

				document.add(p16);

				Chunk cnd = new Chunk(new VerticalPositionMark());
				Paragraph p19 = new Paragraph(patientRegistration.getPatientDetails().getConsultant(), font);
				p19.setAlignment(Element.ALIGN_RIGHT);
				document.add(p19);

				Paragraph p18 = new Paragraph("LAB INCHARGE", font);
				p18.add(cnd);

				p18.add("(" + patientRegistration.getVuserD().getDoctorDetails().getSpecilization() + ")");
				document.add(p18);

				document.close();

				System.out.println("finished");

				pdfBytes = byteArrayOutputStream.toByteArray();

				List<ServicePdf> servicePdfs = servicePdfServiceImpl.findByRegAndMeasureName(regId,
						patientServiceDetails.getServiceName());
				if (!servicePdfs.isEmpty()) {
					servicePdf = new ServicePdf();
					servicePdf.setSid(servicePdfs.get(0).getSid());
					servicePdf.setData(pdfBytes);
					servicePdf.setFileName(servicePdfs.get(0).getFileName());
					servicePdf.setFileuri(servicePdfs.get(0).getFileuri());
					servicePdf.setRegId(servicePdfs.get(0).getRegId());
					servicePdfServiceImpl.save(servicePdf);

				} else {
					String uri = ServletUriComponentsBuilder.fromCurrentContextPath()
							.path("/v1/lab/servicePdf/viewFile/").path(servicePdfServiceImpl.getNextLabId())
							.toUriString();

					servicePdf = new ServicePdf();
					servicePdf.setSid(servicePdfServiceImpl.getNextLabId());
					servicePdf.setData(pdfBytes);
					servicePdf.setFileName(patientServiceDetails.getServiceName());
					servicePdf.setFileuri(uri);
					servicePdf.setRegId(patientServiceDetails.getRegId());
					servicePdfServiceImpl.save(servicePdf);
				}
			} catch (Exception e) {
				Logger.error(e.getMessage());
			}
		} else if (patientServiceDetails.getServiceName().equalsIgnoreCase("CALCIUM SERUM")) {

			byte[] pdfBytes = null;
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			final Font blueFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
			final Font blueFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD | Font.UNDERLINE);
			final Font font = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
			final Font font1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
			final Font font2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.UNDERLINE | Font.BOLD);
			final Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
			final Font headFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);

			Document document = new Document();

			try {
				PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);
				MyFooter event = new MyFooter(principal, userServiceImpl);
				writer.setPageEvent(event);

				document.open();

				document.add(pq);

				Paragraph p51 = new Paragraph("UDBHAVA HOSPITALS", headFont);
				p51.setAlignment(Element.ALIGN_CENTER);
				p51.add("\n \n \n");
				document.add(p51);

				// Display a date in day, month, year format
				date = Calendar.getInstance().getTime();
				formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
				String today = formatter.format(date).toString();

				Font redFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

				Paragraph p9 = new Paragraph("BIOCHEMISTRY DEPARTMENT", blueFont1);
				p9.setAlignment(Element.ALIGN_CENTER);
				document.add(p9);

				Paragraph p90 = new Paragraph("\n ", blueFont);
				p90.setAlignment(Element.ALIGN_CENTER);
				document.add(p90);

				PdfPTable table2 = new PdfPTable(2);
				
				PdfPCell cell1 = new PdfPCell();
				table2.setWidthPercentage(100f);	
				cell1.setBorder(Rectangle.BOTTOM|Rectangle.TOP);
				
				PdfPTable table21 = new PdfPTable(6);
				table21.setWidths(new float[] { 5f,1f,5f,5f,1f,6.5f});
				 table21.setSpacingBefore(10);
				
				
				PdfPCell hcell1;
				hcell1 = new PdfPCell(
						new Phrase("Patient Name", redFont1));
				
				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);
				
				
				hcell1 = new PdfPCell(
						new Phrase(":", redFont1));
				
				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);
				
				
				hcell1 = new PdfPCell(
						new Phrase(patientRegistration.getPatientDetails().getTitle() +". "+ patientRegistration.getPatientDetails().getFirstName()
					+ " " + patientRegistration.getPatientDetails().getLastName(), redFont1));
				
				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);
				
				hcell1 = new PdfPCell(new Phrase("Age/Gender", redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);
				
				hcell1 = new PdfPCell(new Phrase(":", redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);
				
				
				hcell1 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getAge()
						+ "/" + patientRegistration.getPatientDetails().getGender(), redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);
				
				
				
				PdfPCell hcell4;
				hcell4 = new PdfPCell(
						new Phrase("Admission No", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(
						new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(
						new Phrase(patientRegistration.getRegId(), redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				
				
				
				if(admittedWard!=null)
				{
				hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);
				
				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);
				
				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(admittedWard, redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);
				
				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				}
				else
				{
					hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);
				
				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				
				hcell4 = new PdfPCell(new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);
				
				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase("", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);
				
					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
				
				}
				
				
				
				PdfPCell hcell15;
				hcell15 = new PdfPCell(
						new Phrase("Admission Date", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(
						new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(
						new Phrase(pdfadmissionDate, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);
				
				
				if(roomName!=null)
				{
				hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(roomName, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				}
				else
				{
					hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase("", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
				}
				PdfPCell hcell16;
				hcell16 = new PdfPCell(new Phrase(
						"Bill No/UMR No", redFont1));
				
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(
						":", redFont1));
				
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getUmr(), redFont1));
				
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase("Bill Date", redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(":", redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(bill, redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);
				
				PdfPCell hcell17;
				hcell17 = new PdfPCell(new Phrase(
						"Consultant",
						redFont1));
				
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(
						":",
						redFont1));
				
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(
						patientRegistration.getPatientDetails().getConsultant(),
						redFont1));
				
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase("Report Date", redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(":", redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(today, redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				cell1.setColspan(2);
				cell1.addElement(table21);
				table2.addCell(cell1);
				//  table2.addCell(cell1);
				document.add(table2);

				Paragraph p12 = new Paragraph("  CALCIUM SERUM ", blueFont);
				p12.setAlignment(Element.ALIGN_CENTER);
				document.add(p12);

				cell1.setFixedHeight(107f);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setColspan(2);

				document.add(cell1);

				PdfPCell cell3 = new PdfPCell();
				PdfPTable table3 = new PdfPTable(4);
				table3.setWidths(new float[] { 4f, 2f, 4f, 3f });

				PdfPCell hcell2;
				hcell2 = new PdfPCell(new Phrase("\n PARAMETER ", font2));
				hcell2.setBorder(Rectangle.NO_BORDER);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell2.setPaddingLeft(-50f);

				table3.addCell(hcell2);

				hcell2 = new PdfPCell(new Phrase(" \n RESULT", font2));
				hcell2.setBorder(Rectangle.NO_BORDER);
				hcell2.setHorizontalAlignment(Element.ALIGN_CENTER);
				table3.addCell(hcell2);

				hcell2 = new PdfPCell(new Phrase(" \n NORMAL RANGE", font2));
				hcell2.setBorder(Rectangle.NO_BORDER);
				hcell2.setHorizontalAlignment(Element.ALIGN_CENTER);
				table3.addCell(hcell2);

				hcell2 = new PdfPCell(new Phrase(" \n METHOD", font2));
				hcell2.setBorder(Rectangle.NO_BORDER);
				hcell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table3.addCell(hcell2);

				document.add(table3);

				for (RefMeasureDetails refMeasureDetailsInfoPdf : refMeasureDetails) {

					PdfPCell cell4 = new PdfPCell();
					PdfPTable table5 = new PdfPTable(4);
					table5.setWidths(new float[] { 4f, 2f, 4f, 3f });

					PdfPCell hcell5;
					hcell5 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getMeasure(), font1));

					hcell5.setBorder(Rectangle.NO_BORDER);

					hcell5.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell5.setPaddingLeft(-50f);
					table5.addCell(hcell5);
					if (refMeasureDetailsInfoPdf.getDimension() != null) {
						hcell5 = new PdfPCell(new Phrase(String.valueOf(
								refMeasureDetailsInfoPdf.getValue() + "" + refMeasureDetailsInfoPdf.getDimension()),
								font1));
						hcell5.setBorder(Rectangle.NO_BORDER);
						hcell5.setHorizontalAlignment(Element.ALIGN_CENTER);
						table5.addCell(hcell5);
					} else {
						hcell5 = new PdfPCell(new Phrase(String.valueOf(refMeasureDetailsInfoPdf.getValue()), font1));
						hcell5.setBorder(Rectangle.NO_BORDER);
						hcell5.setHorizontalAlignment(Element.ALIGN_CENTER);
						table5.addCell(hcell5);
					}

					if (refMeasureDetailsInfoPdf.getDimension() != null) {
						hcell5 = new PdfPCell(new Phrase(
								refMeasureDetailsInfoPdf.getRange() + "" + refMeasureDetailsInfoPdf.getDimension(),
								font1));
						hcell5.setBorder(Rectangle.NO_BORDER);
						hcell5.setHorizontalAlignment(Element.ALIGN_CENTER);
						table5.addCell(hcell5);
					} else {
						hcell5 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getRange(), font1));
						hcell5.setBorder(Rectangle.NO_BORDER);
						hcell5.setHorizontalAlignment(Element.ALIGN_CENTER);
						table5.addCell(hcell5);

					}
					System.out.println(refMeasureDetailsInfoPdf.getRange());

					hcell5 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getMethod(), font1));
					hcell5.setBorder(Rectangle.NO_BORDER);
					hcell5.setHorizontalAlignment(Element.ALIGN_RIGHT);
					hcell5.setPaddingRight(10f);
					table5.addCell(hcell5);

					document.add(table5);

					cell4.setFixedHeight(107f);
					cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell4.setColspan(2);
					document.add(cell4);

				}

				cell3.setFixedHeight(107f);
				cell3.setHorizontalAlignment(Element.ALIGN_CENTER);

				cell3.setColspan(2);
				document.add(cell3);

				if (patientServiceDetails.getComment().isEmpty()) {
					Paragraph p27 = new Paragraph("\n Findings:", font);
					document.add(p27);
					Paragraph p28 = new Paragraph("N/A", font1);
					document.add(p28);

				} else {
					Paragraph p27 = new Paragraph("\n Findings:", font);

					Paragraph p28 = new Paragraph(patientServiceDetails.getComment(), font1);
					document.add(p27);
					document.add(p28);
				}
				Paragraph p16 = new Paragraph("\n \n \n \n *******End Of Report****** ", font1);
				p16.setAlignment(Element.ALIGN_CENTER);

				document.add(p16);

				Chunk cnd = new Chunk(new VerticalPositionMark());
				Paragraph p19 = new Paragraph(patientRegistration.getPatientDetails().getConsultant(), font1);
				p19.setAlignment(Element.ALIGN_RIGHT);
				document.add(p19);

				Paragraph p18 = new Paragraph("LAB INCHARGE:", font);
				p18.add(cnd);

				p18.add("(" + patientRegistration.getVuserD().getDoctorDetails().getSpecilization() + ")");
				document.add(p18);

				document.close();
				System.out.println("finished");

				pdfBytes = byteArrayOutputStream.toByteArray();
				List<ServicePdf> servicePdfs = servicePdfServiceImpl.findByRegAndMeasureName(regId,
						patientServiceDetails.getServiceName());
				if (!servicePdfs.isEmpty()) {
					servicePdf = new ServicePdf();
					servicePdf.setSid(servicePdfs.get(0).getSid());
					servicePdf.setData(pdfBytes);
					servicePdf.setFileName(servicePdfs.get(0).getFileName());
					servicePdf.setFileuri(servicePdfs.get(0).getFileuri());
					servicePdf.setRegId(servicePdfs.get(0).getRegId());
					servicePdfServiceImpl.save(servicePdf);

				} else {
					String uri = ServletUriComponentsBuilder.fromCurrentContextPath()
							.path("/v1/lab/servicePdf/viewFile/").path(servicePdfServiceImpl.getNextLabId())
							.toUriString();

					servicePdf = new ServicePdf();
					servicePdf.setSid(servicePdfServiceImpl.getNextLabId());
					servicePdf.setData(pdfBytes);
					servicePdf.setFileName(patientServiceDetails.getServiceName());
					servicePdf.setFileuri(uri);
					servicePdf.setRegId(patientServiceDetails.getRegId());
					servicePdfServiceImpl.save(servicePdf);
				}

			} catch (Exception e) {
				Logger.error(e.getMessage());
			}
		} else if (patientServiceDetails.getServiceName().equalsIgnoreCase("MAGNESIUM-SERUM")) {

			byte[] pdfBytes = null;
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			final Font blueFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
			final Font blueFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD | Font.UNDERLINE);
			final Font font = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
			final Font font1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
			final Font font2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD | Font.UNDERLINE);

			final Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
			final Font headFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);

			Document document = new Document();

			try {
				PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);
				MyFooter event = new MyFooter(principal, userServiceImpl);
				writer.setPageEvent(event);
				document.open();
				document.add(pq);

				Paragraph p51 = new Paragraph("UDBHAVA HOSPITALS", headFont);
				p51.setAlignment(Element.ALIGN_CENTER);
				p51.add("\n \n ");
				document.add(p51);

				// Display a date in day, month, year format
				date = Calendar.getInstance().getTime();
				formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
				String today = formatter.format(date).toString();

				Font redFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

				Paragraph p9 = new Paragraph("  BIOCHEMISTRY DEPARTMENT", blueFont1);
				p9.setAlignment(Element.ALIGN_CENTER);
				document.add(p9);

				Paragraph p90 = new Paragraph("\n", blueFont);
				p90.setAlignment(Element.ALIGN_CENTER);
				document.add(p90);

				PdfPTable table2 = new PdfPTable(2);
				
				PdfPCell cell1 = new PdfPCell();
				table2.setWidthPercentage(100f);	
				cell1.setBorder(Rectangle.BOTTOM|Rectangle.TOP);
				
				PdfPTable table21 = new PdfPTable(6);
				table21.setWidths(new float[] { 5f,1f,5f,5f,1f,6.5f});
				 table21.setSpacingBefore(10);
				

				PdfPCell hcell1;
				hcell1 = new PdfPCell(
						new Phrase("Patient Name", redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(
						new Phrase(":", redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(
						new Phrase(patientRegistration.getPatientDetails().getTitle() +". "+ patientRegistration.getPatientDetails().getFirstName()
								+ " " + patientRegistration.getPatientDetails().getLastName(), redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase("Age/Gender", redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase(":", redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getAge()
						+ "/" + patientRegistration.getPatientDetails().getGender(), redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				
				
				PdfPCell hcell4;
				hcell4 = new PdfPCell(
						new Phrase("Admission No", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(
						new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(
						new Phrase(patientRegistration.getRegId(), redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				


				if(admittedWard!=null)
				{
				hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(admittedWard, redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				}
				else
				{
					hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					
					hcell4 = new PdfPCell(new Phrase(":", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					hcell4 = new PdfPCell(new Phrase("", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);

				}



				PdfPCell hcell15;
				hcell15 = new PdfPCell(
						new Phrase("Admission Date", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				hcell15 = new PdfPCell(
						new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				hcell15 = new PdfPCell(
						new Phrase(pdfadmissionDate, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				
				if(roomName!=null)
				{
				hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(roomName, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				}
				else
				{
					hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase(":", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase("", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
				}
				PdfPCell hcell16;
				hcell16 = new PdfPCell(new Phrase(
						"Bill No/UMR No", redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(
						":", redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getUmr(), redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);

				hcell16 = new PdfPCell(new Phrase("Bill Date", redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(":", redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(bill, redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);

				PdfPCell hcell17;
				hcell17 = new PdfPCell(new Phrase(
						"Consultant",
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(
						":",
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(
						patientRegistration.getPatientDetails().getConsultant(),
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);

				hcell17 = new PdfPCell(new Phrase("Report Date", redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(":", redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(today, redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				cell1.setColspan(2);
				cell1.addElement(table21);
				table2.addCell(cell1);
             //  table2.addCell(cell1);
               document.add(table2);

				Paragraph p12 = new Paragraph("  MAGNESIUM-SERUM ", blueFont);
				p12.setAlignment(Element.ALIGN_CENTER);
				document.add(p12);

				cell1.setFixedHeight(107f);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setColspan(2);

				document.add(cell1);

				PdfPCell cell3 = new PdfPCell();
				PdfPTable table3 = new PdfPTable(3);
				table3.setWidths(new float[] { 4f, 2f, 4f });

				PdfPCell hcell2;
				hcell2 = new PdfPCell(new Phrase("\n PARAMETER ", font2));
				hcell2.setBorder(Rectangle.NO_BORDER);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell2.setPaddingLeft(-50f);

				table3.addCell(hcell2);

				hcell2 = new PdfPCell(new Phrase(" \n RESULT", font2));
				hcell2.setBorder(Rectangle.NO_BORDER);
				hcell2.setHorizontalAlignment(Element.ALIGN_CENTER);
				table3.addCell(hcell2);

				hcell2 = new PdfPCell(new Phrase(" \n NORMAL RANGE", font2));
				hcell2.setBorder(Rectangle.NO_BORDER);
				hcell2.setHorizontalAlignment(Element.ALIGN_CENTER);
				table3.addCell(hcell2);

				hcell2 = new PdfPCell(new Phrase(" \n METHOD", font));
				hcell2.setBorder(Rectangle.NO_BORDER);
				hcell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table3.addCell(hcell2);

				document.add(table3);

				for (RefMeasureDetails refMeasureDetailsInfoPdf : refMeasureDetails) {

					if (refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("SERUM MAGNESIUM")) {
						PdfPCell cell4 = new PdfPCell();
						PdfPTable table5 = new PdfPTable(3);
						table5.setWidths(new float[] { 4f, 2f, 4f });

						PdfPCell hcell5;
						hcell5 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getMeasure(), font1));

						hcell5.setBorder(Rectangle.NO_BORDER);

						hcell5.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell5.setPaddingLeft(-50f);
						table5.addCell(hcell5);

						if (refMeasureDetailsInfoPdf.getDimension() != null) {
							hcell5 = new PdfPCell(new Phrase(String.valueOf(
									refMeasureDetailsInfoPdf.getValue() + "" + refMeasureDetailsInfoPdf.getDimension()),
									font1));
							hcell5.setBorder(Rectangle.NO_BORDER);
							hcell5.setHorizontalAlignment(Element.ALIGN_CENTER);
							table5.addCell(hcell5);
						} else {
							hcell5 = new PdfPCell(
									new Phrase(String.valueOf(refMeasureDetailsInfoPdf.getValue()), font1));
							hcell5.setBorder(Rectangle.NO_BORDER);
							hcell5.setHorizontalAlignment(Element.ALIGN_CENTER);
							table5.addCell(hcell5);
						}
						if (refMeasureDetailsInfoPdf.getDimension() != null) {
							hcell5 = new PdfPCell(new Phrase(
									refMeasureDetailsInfoPdf.getRange() + "" + refMeasureDetailsInfoPdf.getDimension(),
									font1));
							hcell5.setBorder(Rectangle.NO_BORDER);
							hcell5.setHorizontalAlignment(Element.ALIGN_CENTER);
							table5.addCell(hcell5);
						} else {
							hcell5 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getRange(), font1));
							hcell5.setBorder(Rectangle.NO_BORDER);
							hcell5.setHorizontalAlignment(Element.ALIGN_CENTER);
							table5.addCell(hcell5);
						}
						System.out.println(refMeasureDetailsInfoPdf.getRange());

						document.add(table5);

						cell4.setFixedHeight(107f);
						cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell4.setColspan(2);
						document.add(cell4);

					}
				}
				cell3.setFixedHeight(107f);
				cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell3.setColspan(2);
				document.add(cell3);

				if (patientServiceDetails.getComment().isEmpty()) {
					Paragraph p27 = new Paragraph("\n Findings:", font);
					document.add(p27);
					Paragraph p28 = new Paragraph("N/A", font1);
					document.add(p28);

				} else {
					Paragraph p27 = new Paragraph("\n Findings:", font);

					Paragraph p28 = new Paragraph(patientServiceDetails.getComment(), font1);
					document.add(p27);
					document.add(p28);
				}
				Paragraph p16 = new Paragraph("\n \n \n \n *******End Of Report****** ", font1);
				p16.setAlignment(Element.ALIGN_CENTER);

				document.add(p16);

				Chunk cnd = new Chunk(new VerticalPositionMark());
				Paragraph p19 = new Paragraph(patientRegistration.getPatientDetails().getConsultant(), font1);
				p19.setAlignment(Element.ALIGN_RIGHT);
				document.add(p19);

				Paragraph p18 = new Paragraph("LAB INCHARGE", font);
				p18.add(cnd);

				p18.add("(" + patientRegistration.getVuserD().getDoctorDetails().getSpecilization() + ")");
				document.add(p18);

				document.close();
				System.out.println("finished");

				pdfBytes = byteArrayOutputStream.toByteArray();
				List<ServicePdf> servicePdfs = servicePdfServiceImpl.findByRegAndMeasureName(regId,
						patientServiceDetails.getServiceName());
				if (!servicePdfs.isEmpty()) {
					servicePdf = new ServicePdf();
					servicePdf.setSid(servicePdfs.get(0).getSid());
					servicePdf.setData(pdfBytes);
					servicePdf.setFileName(servicePdfs.get(0).getFileName());
					servicePdf.setFileuri(servicePdfs.get(0).getFileuri());
					servicePdf.setRegId(servicePdfs.get(0).getRegId());
					servicePdfServiceImpl.save(servicePdf);

				} else {
					String uri = ServletUriComponentsBuilder.fromCurrentContextPath()
							.path("/v1/lab/servicePdf/viewFile/").path(servicePdfServiceImpl.getNextLabId())
							.toUriString();

					servicePdf = new ServicePdf();
					servicePdf.setSid(servicePdfServiceImpl.getNextLabId());
					servicePdf.setData(pdfBytes);
					servicePdf.setFileName(patientServiceDetails.getServiceName());
					servicePdf.setFileuri(uri);
					servicePdf.setRegId(patientServiceDetails.getRegId());
					servicePdfServiceImpl.save(servicePdf);
				}

			} catch (Exception e) {
				Logger.error(e.getMessage());
			}

		} else if (patientServiceDetails.getServiceName().equalsIgnoreCase("COMPLETE STOOL EXAMINATION")) {

			byte[] pdfBytes = null;
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			final Font blueFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
			final Font font = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
			final Font font2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD | Font.UNDERLINE);
			final Font font1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

			final Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
			final Font headFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);

			Document document = new Document();

			try {
				PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);
				MyFooter event = new MyFooter(principal, userServiceImpl);
				writer.setPageEvent(event);

				document.open();
				document.add(pq);

				Paragraph p51 = new Paragraph("UDBHAVA HOSPITALS", headFont);
				p51.add("\n \n \n");
				p51.setAlignment(Element.ALIGN_CENTER);
				document.add(p51);

				// Display a date in day, month, year format
				date = Calendar.getInstance().getTime();
				formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
				String today = formatter.format(date).toString();

				Font redFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

				Paragraph p9 = new Paragraph(" MICROBIOLOGY DEPARTMENT", blueFont);
				p9.setAlignment(Element.ALIGN_CENTER);
				document.add(p9);

				Paragraph p90 = new Paragraph("\n", blueFont);
				p90.setAlignment(Element.ALIGN_CENTER);
				document.add(p90);

				PdfPTable table2 = new PdfPTable(2);
				
				PdfPCell cell1 = new PdfPCell();
				table2.setWidthPercentage(100f);	
				cell1.setBorder(Rectangle.BOTTOM|Rectangle.TOP);
				
				PdfPTable table21 = new PdfPTable(6);
				table21.setWidths(new float[] { 5f,1f,5f,5f,1f,6.5f});
				 table21.setSpacingBefore(10);
				

				PdfPCell hcell1;
				hcell1 = new PdfPCell(
						new Phrase("Patient Name", redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(
						new Phrase(":", redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(
						new Phrase(patientRegistration.getPatientDetails().getTitle() +". "+ patientRegistration.getPatientDetails().getFirstName()
								+ " " + patientRegistration.getPatientDetails().getLastName(), redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase("Age/Gender", redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase(":", redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getAge()
						+ "/" + patientRegistration.getPatientDetails().getGender(), redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				
				
				PdfPCell hcell4;
				hcell4 = new PdfPCell(
						new Phrase("Admission No", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(
						new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(
						new Phrase(patientRegistration.getRegId(), redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				


				if(admittedWard!=null)
				{
				hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(admittedWard, redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				}
				else
				{
					hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					
					hcell4 = new PdfPCell(new Phrase(":", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					hcell4 = new PdfPCell(new Phrase("", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);

				}



				PdfPCell hcell15;
				hcell15 = new PdfPCell(
						new Phrase("Admission Date", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				hcell15 = new PdfPCell(
						new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				hcell15 = new PdfPCell(
						new Phrase(pdfadmissionDate, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				
				if(roomName!=null)
				{
				hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(roomName, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				}
				else
				{
					hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase(":", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase("", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
				}
				PdfPCell hcell16;
				hcell16 = new PdfPCell(new Phrase(
						"Bill No/UMR No", redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(
						":", redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getUmr(), redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);

				hcell16 = new PdfPCell(new Phrase("Bill Date", redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(":", redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(bill, redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);

				PdfPCell hcell17;
				hcell17 = new PdfPCell(new Phrase(
						"Consultant",
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(
						":",
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(
						patientRegistration.getPatientDetails().getConsultant(),
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);

				hcell17 = new PdfPCell(new Phrase("Report Date", redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(":", redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(today, redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				cell1.setColspan(2);
				cell1.addElement(table21);
				table2.addCell(cell1);
             //  table2.addCell(cell1);
               document.add(table2);
				Paragraph p12 = new Paragraph(" COMPLETE STOOL EXAMINATION  ", blueFont);
				p12.setAlignment(Element.ALIGN_CENTER);
				document.add(p12);

				cell1.setFixedHeight(107f);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setColspan(2);

				document.add(cell1);

				PdfPCell cell3 = new PdfPCell();
				PdfPTable table3 = new PdfPTable(3);
				table3.setWidths(new float[] { 4f, 4f, 4f });

				PdfPCell hcell2;
				hcell2 = new PdfPCell(new Phrase("\n  PARAMETER ", font2));
				hcell2.setBorder(Rectangle.NO_BORDER);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell2.setPaddingLeft(-50f);

				table3.addCell(hcell2);

				hcell2 = new PdfPCell(new Phrase(" \n RESULT", font2));
				hcell2.setBorder(Rectangle.NO_BORDER);
				hcell2.setHorizontalAlignment(Element.ALIGN_CENTER);
				table3.addCell(hcell2);

				hcell2 = new PdfPCell(new Phrase(" \n METHOD", font2));
				hcell2.setBorder(Rectangle.NO_BORDER);
				hcell2.setHorizontalAlignment(Element.ALIGN_CENTER);
				table3.addCell(hcell2);

				document.add(table3);

				Paragraph p17 = new Paragraph("");
				p17.setAlignment(Element.ALIGN_LEFT);
				p17.add("\n");
				document.add(p17);

				Paragraph p15 = new Paragraph("PHYSICAL EXAMINATION", font2);
				p15.setAlignment(Element.ALIGN_LEFT);

				document.add(p15);
				Paragraph p116 = new Paragraph("");
				p116.setAlignment(Element.ALIGN_LEFT);
				p116.add("\n");
				document.add(p116);

				PdfPCell cell4 = new PdfPCell();
				PdfPTable table5 = new PdfPTable(3);
				table5.setWidths(new float[] { 4f, 4f, 4f });
				for (RefMeasureDetails refMeasureDetailsInfoPdf : refMeasureDetails) {

					if (refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("COLOUR")
							|| refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("APPEARANCE")
							|| refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("REACTION")
							|| refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("MUCOUS")
							|| refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("BLOOD")

					) {

						PdfPCell hcell5;
						hcell5 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getMeasure(), font1));

						hcell5.setBorder(Rectangle.NO_BORDER);

						hcell5.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell5.setPaddingLeft(-50f);
						table5.addCell(hcell5);

						if (refMeasureDetailsInfoPdf.getDimension() != null) {
							hcell5 = new PdfPCell(new Phrase(String.valueOf(
									refMeasureDetailsInfoPdf.getValue() + refMeasureDetailsInfoPdf.getDimension()),
									font1));
							hcell5.setBorder(Rectangle.NO_BORDER);
							hcell5.setHorizontalAlignment(Element.ALIGN_LEFT);
							hcell5.setPaddingLeft(50f);
							table5.addCell(hcell5);
						} else {
							hcell5 = new PdfPCell(
									new Phrase(String.valueOf(":  " + refMeasureDetailsInfoPdf.getValue()), font1));
							hcell5.setBorder(Rectangle.NO_BORDER);
							hcell5.setHorizontalAlignment(Element.ALIGN_LEFT);
							hcell5.setPaddingLeft(50f);
							table5.addCell(hcell5);

						}
						System.out.println(refMeasureDetailsInfoPdf.getRange());
						hcell5 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getMethod(), font1));
						hcell5.setBorder(Rectangle.NO_BORDER);
						hcell5.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell5.setPaddingLeft(50f);
						table5.addCell(hcell5);

					}

				}

				document.add(table5);

				Paragraph p011 = new Paragraph("MICROSCOPIC EXAMINATION ", font2);
				document.add(p011);
				Paragraph p16 = new Paragraph("");
				p16.setAlignment(Element.ALIGN_LEFT);
				p16.add("\n");
				document.add(p16);

				PdfPTable table6 = new PdfPTable(3);
				table6.setWidths(new float[] { 4f, 4f, 4f });
				for (RefMeasureDetails refMeasureDetailsInfoPdf : refMeasureDetails) {
					if (refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("OVA")
							|| refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("CYSTS")
							|| refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("PUS CELLS")
							|| refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("Epithelial Cells")
							|| refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("FAT GLOBULES")
							|| refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("STARCH")
							|| refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("VEGTETABLE CELLS/FIBERS")
							|| refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("BACTERIA")
							|| refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("OTHERS")) {

						PdfPCell hcell6;
						hcell6 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getMeasure(), font1));

						hcell6.setBorder(Rectangle.NO_BORDER);

						hcell6.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell6.setPaddingLeft(-50f);
						table6.addCell(hcell6);

						hcell6 = new PdfPCell(
								new Phrase(String.valueOf(":  " + refMeasureDetailsInfoPdf.getValue()), font1));
						hcell6.setBorder(Rectangle.NO_BORDER);
						hcell6.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell6.setPaddingLeft(50f);
						table6.addCell(hcell6);

						System.out.println(refMeasureDetailsInfoPdf.getRange());
						hcell6 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getMethod(), font1));
						hcell6.setBorder(Rectangle.NO_BORDER);
						hcell6.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell6.setPaddingLeft(50f);
						table6.addCell(hcell6);

					}

				}

				document.add(table6);

				if (patientServiceDetails.getComment().isEmpty()) {
					Paragraph p27 = new Paragraph("\n Findings:", font);
					document.add(p27);
					Paragraph p28 = new Paragraph("N/A", font1);
					document.add(p28);

				} else {
					Paragraph p27 = new Paragraph("\n Findings:", font);

					Paragraph p28 = new Paragraph(patientServiceDetails.getComment(), font1);
					document.add(p27);
					document.add(p28);
				}
				Paragraph p27 = new Paragraph(" *******End Of Report****** ", font1);
				p27.setAlignment(Element.ALIGN_CENTER);

				document.add(p27);

				Chunk cnd = new Chunk(new VerticalPositionMark());
				Paragraph p19 = new Paragraph(patientRegistration.getPatientDetails().getConsultant(), font1);
				p19.setAlignment(Element.ALIGN_RIGHT);
				document.add(p19);

				Paragraph p18 = new Paragraph("LAB INCHARGE", font);
				p18.add(cnd);

				p18.add("(" + patientRegistration.getVuserD().getDoctorDetails().getSpecilization() + ")");
				document.add(p18);

				document.close();
				System.out.println("finished");

				pdfBytes = byteArrayOutputStream.toByteArray();
				List<ServicePdf> servicePdfs = servicePdfServiceImpl.findByRegAndMeasureName(regId,
						patientServiceDetails.getServiceName());
				if (!servicePdfs.isEmpty()) {
					servicePdf = new ServicePdf();
					servicePdf.setSid(servicePdfs.get(0).getSid());
					servicePdf.setData(pdfBytes);
					servicePdf.setFileName(servicePdfs.get(0).getFileName());
					servicePdf.setFileuri(servicePdfs.get(0).getFileuri());
					servicePdf.setRegId(servicePdfs.get(0).getRegId());
					servicePdfServiceImpl.save(servicePdf);

				} else {
					String uri = ServletUriComponentsBuilder.fromCurrentContextPath()
							.path("/v1/lab/servicePdf/viewFile/").path(servicePdfServiceImpl.getNextLabId())
							.toUriString();

					servicePdf = new ServicePdf();
					servicePdf.setSid(servicePdfServiceImpl.getNextLabId());
					servicePdf.setData(pdfBytes);
					servicePdf.setFileName(patientServiceDetails.getServiceName());
					servicePdf.setFileuri(uri);
					servicePdf.setRegId(patientServiceDetails.getRegId());
					servicePdfServiceImpl.save(servicePdf);
				}

			} catch (Exception e) {
				Logger.error(e.getMessage());
			}

		} else if (patientServiceDetails.getServiceName().equalsIgnoreCase("LFT - LIVER FUNCTON TESTS")) {

			byte[] pdfBytes = null;
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			final Font blueFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
			final Font font = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
			final Font font1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
			final Font font2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.UNDERLINE | Font.BOLD);

			final Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
			final Font headFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);

			Document document = new Document();

			try {
				PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);
				MyFooter event = new MyFooter(principal, userServiceImpl);
				writer.setPageEvent(event);

				document.open();

				document.add(pq);

				Paragraph p51 = new Paragraph("UDBHAVA HOSPITALS", headFont);
				p51.add("\n \n \n \n");
				p51.setAlignment(Element.ALIGN_CENTER);

				document.add(p51);

				// Display a date in day, month, year format
				date = Calendar.getInstance().getTime();
				formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
				String today = formatter.format(date).toString();

				Font redFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

				PdfPTable table2 = new PdfPTable(2);
				
				PdfPCell cell1 = new PdfPCell();
				table2.setWidthPercentage(100f);	
				cell1.setBorder(Rectangle.BOTTOM|Rectangle.TOP);
				
				PdfPTable table21 = new PdfPTable(6);
				table21.setWidths(new float[] { 5f,1f,5f,5f,1f,6.5f});
				 table21.setSpacingBefore(10);
				

				PdfPCell hcell1;
				hcell1 = new PdfPCell(
						new Phrase("Patient Name", redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(
						new Phrase(":", redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(
						new Phrase(patientRegistration.getPatientDetails().getTitle() +". "+ patientRegistration.getPatientDetails().getFirstName()
								+ " " + patientRegistration.getPatientDetails().getLastName(), redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase("Age/Gender", redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase(":", redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getAge()
						+ "/" + patientRegistration.getPatientDetails().getGender(), redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				
				
				PdfPCell hcell4;
				hcell4 = new PdfPCell(
						new Phrase("Admission No", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(
						new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(
						new Phrase(patientRegistration.getRegId(), redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				


				if(admittedWard!=null)
				{
				hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(admittedWard, redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				}
				else
				{
					hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					
					hcell4 = new PdfPCell(new Phrase(":", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					hcell4 = new PdfPCell(new Phrase("", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);

				}



				PdfPCell hcell15;
				hcell15 = new PdfPCell(
						new Phrase("Admission Date", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				hcell15 = new PdfPCell(
						new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				hcell15 = new PdfPCell(
						new Phrase(pdfadmissionDate, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				
				if(roomName!=null)
				{
				hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(roomName, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				}
				else
				{
					hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase(":", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase("", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
				}
				PdfPCell hcell16;
				hcell16 = new PdfPCell(new Phrase(
						"Bill No/UMR No", redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(
						":", redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getUmr(), redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);

				hcell16 = new PdfPCell(new Phrase("Bill Date", redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(":", redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(bill, redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);

				PdfPCell hcell17;
				hcell17 = new PdfPCell(new Phrase(
						"Consultant",
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(
						":",
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(
						patientRegistration.getPatientDetails().getConsultant(),
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);

				hcell17 = new PdfPCell(new Phrase("Report Date", redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(":", redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(today, redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				cell1.setColspan(2);
				cell1.addElement(table21);
				table2.addCell(cell1);
             //  table2.addCell(cell1);
               document.add(table2);

				Paragraph p12 = new Paragraph("  LFT-LIVER FUNCTON TEST  ", blueFont);
				p12.setAlignment(Element.ALIGN_CENTER);
				document.add(p12);

				cell1.setFixedHeight(107f);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				// cell1.setBorder(Rectangle.LEFT);
				// cell1.setBorder(Rectangle.RIGHT);
				cell1.setColspan(2);

				document.add(cell1);

				PdfPCell cell3 = new PdfPCell();
				PdfPTable table3 = new PdfPTable(3);
				table3.setWidths(new float[] { 4f, 2f, 4.5f, });
				// table2.setSpacingBefore(10);

				PdfPCell hcell2;
				hcell2 = new PdfPCell(new Phrase("\n INVESTIGATION ", font2));
				hcell2.setBorder(Rectangle.NO_BORDER);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell2.setPaddingLeft(-50f);

				table3.addCell(hcell2);

				hcell2 = new PdfPCell(new Phrase(" \n RESULT", font2));
				hcell2.setBorder(Rectangle.NO_BORDER);
				// hcell1.setPaddingRight(-40f);
				// hcell3.setPaddingLeft(90f);
				hcell2.setHorizontalAlignment(Element.ALIGN_LEFT);
				table3.addCell(hcell2);

				hcell2 = new PdfPCell(new Phrase(" \n NORMAL RANGE", font2));
				hcell2.setBorder(Rectangle.NO_BORDER);
				// hcell5.setPaddingLeft(-25f);
				hcell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table3.addCell(hcell2);

				// document.add(cell3);
				document.add(table3);

				for (RefMeasureDetails refMeasureDetailsInfoPdf : refMeasureDetails) {

					PdfPCell cell4 = new PdfPCell();
					PdfPTable table5 = new PdfPTable(3);
					table5.setWidths(new float[] { 4f, 2f, 4.5f });
					// table2.setSpacingBefore(10);

					PdfPCell hcell5;
					hcell5 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getMeasure(), font1));

					hcell5.setBorder(Rectangle.NO_BORDER);

					hcell5.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell5.setPaddingLeft(-50f);
					table5.addCell(hcell5);

					if (refMeasureDetailsInfoPdf.getDimension() != null) {
						hcell5 = new PdfPCell(new Phrase(String.valueOf(": " + refMeasureDetailsInfoPdf.getValue() + " "
								+ refMeasureDetailsInfoPdf.getDimension()), font1));
						hcell5.setBorder(Rectangle.NO_BORDER);
						hcell5.setHorizontalAlignment(Element.ALIGN_LEFT);
						table5.addCell(hcell5);
					} else {
						hcell5 = new PdfPCell(
								new Phrase(String.valueOf(": " + refMeasureDetailsInfoPdf.getValue()), font1));
						hcell5.setBorder(Rectangle.NO_BORDER);
						hcell5.setHorizontalAlignment(Element.ALIGN_LEFT);
						table5.addCell(hcell5);
					}

					if (refMeasureDetailsInfoPdf.getDimension() != null) {
						hcell5 = new PdfPCell(new Phrase(
								refMeasureDetailsInfoPdf.getRange() + "" + refMeasureDetailsInfoPdf.getDimension(),
								font1));
						hcell5.setBorder(Rectangle.NO_BORDER);
						hcell5.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell5.setPaddingLeft(100f);
						table5.addCell(hcell5);
					} else {
						hcell5 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getRange(), font1));
						hcell5.setBorder(Rectangle.NO_BORDER);
						hcell5.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell5.setPaddingLeft(100f);
						table5.addCell(hcell5);
					}
					System.out.println(refMeasureDetailsInfoPdf.getRange());

					document.add(table5);

					cell4.setFixedHeight(107f);
					cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell4.setColspan(2);
					document.add(cell4);

				}

				cell3.setFixedHeight(107f);
				cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell3.setColspan(2);
				document.add(cell3);

				if (patientServiceDetails.getComment().isEmpty()) {
					Paragraph p27 = new Paragraph("\n Findings:", font);
					document.add(p27);
					Paragraph p28 = new Paragraph("N/A", font1);
					document.add(p28);

				} else {
					Paragraph p27 = new Paragraph("\n Findings:", font);

					Paragraph p28 = new Paragraph(patientServiceDetails.getComment(), font1);
					document.add(p27);
					document.add(p28);
				}

				Paragraph p16 = new Paragraph(" \n \n *******End Of Report****** ", font1);
				p16.setAlignment(Element.ALIGN_CENTER);

				document.add(p16);
				Paragraph p101 = new Paragraph();
				document.add(p101);

				Paragraph p17 = new Paragraph("\n \n  ", font1);
				p17.setAlignment(Element.ALIGN_CENTER);
				document.add(p17);

				Chunk cnd = new Chunk(new VerticalPositionMark());
				Paragraph p19 = new Paragraph(patientRegistration.getPatientDetails().getConsultant(), font1);
				p19.setAlignment(Element.ALIGN_RIGHT);
				document.add(p19);

				Paragraph p18 = new Paragraph("LAB INCHARGE", font);
				p18.add(cnd);

				p18.add("(" + patientRegistration.getVuserD().getDoctorDetails().getSpecilization() + ")");
				document.add(p18);

				document.close();
				System.out.println("finished");

				pdfBytes = byteArrayOutputStream.toByteArray();
				List<ServicePdf> servicePdfs = servicePdfServiceImpl.findByRegAndMeasureName(regId,
						patientServiceDetails.getServiceName());
				if (!servicePdfs.isEmpty()) {
					servicePdf = new ServicePdf();
					servicePdf.setSid(servicePdfs.get(0).getSid());
					servicePdf.setData(pdfBytes);
					servicePdf.setFileName(servicePdfs.get(0).getFileName());
					servicePdf.setFileuri(servicePdfs.get(0).getFileuri());
					servicePdf.setRegId(servicePdfs.get(0).getRegId());
					servicePdfServiceImpl.save(servicePdf);

				} else {
					String uri = ServletUriComponentsBuilder.fromCurrentContextPath()
							.path("/v1/lab/servicePdf/viewFile/").path(servicePdfServiceImpl.getNextLabId())
							.toUriString();

					servicePdf = new ServicePdf();
					servicePdf.setSid(servicePdfServiceImpl.getNextLabId());
					servicePdf.setData(pdfBytes);
					servicePdf.setFileName(patientServiceDetails.getServiceName());
					servicePdf.setFileuri(uri);
					servicePdf.setRegId(patientServiceDetails.getRegId());
					servicePdfServiceImpl.save(servicePdf);
				}

			} catch (Exception e) {
				Logger.error(e.getMessage());
			}
		} else if (patientServiceDetails.getServiceName().equalsIgnoreCase("RENAL FUNCTION TEST(RFT)")) {

			byte[] pdfBytes = null;
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			final Font blueFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
			final Font font = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
			final Font font1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
			final Font font2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD | Font.UNDERLINE);

			final Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
			final Font headFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);

			Document document = new Document();

			try {
				PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);
				MyFooter event = new MyFooter(principal, userServiceImpl);
				writer.setPageEvent(event);

				document.open();

				document.add(pq);

				Paragraph p51 = new Paragraph("UDBHAVA HOSPITALS", headFont);
				p51.add("\n \n");
				p51.setAlignment(Element.ALIGN_CENTER);

				document.add(p51);

				// Display a date in day, month, year format
				date = Calendar.getInstance().getTime();
				formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
				String today = formatter.format(date).toString();

				Font redFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

				Paragraph p9 = new Paragraph("\n  BIOCHEMISTRY", blueFont);
				p9.setAlignment(Element.ALIGN_CENTER);
				document.add(p9);

				Paragraph p90 = new Paragraph("\n", blueFont);
				p90.setAlignment(Element.ALIGN_CENTER);
				document.add(p90);
				PdfPTable table2 = new PdfPTable(2);
				
				PdfPCell cell1 = new PdfPCell();
				table2.setWidthPercentage(100f);	
				cell1.setBorder(Rectangle.BOTTOM|Rectangle.TOP);
				
				PdfPTable table21 = new PdfPTable(6);
				table21.setWidths(new float[] { 5f,1f,5f,5f,1f,6.5f});
				 table21.setSpacingBefore(10);
				

				PdfPCell hcell1;
				hcell1 = new PdfPCell(
						new Phrase("Patient Name", redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(
						new Phrase(":", redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(
						new Phrase(patientRegistration.getPatientDetails().getTitle() +". "+ patientRegistration.getPatientDetails().getFirstName()
								+ " " + patientRegistration.getPatientDetails().getLastName(), redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase("Age/Gender", redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase(":", redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getAge()
						+ "/" + patientRegistration.getPatientDetails().getGender(), redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				
				
				PdfPCell hcell4;
				hcell4 = new PdfPCell(
						new Phrase("Admission No", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(
						new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(
						new Phrase(patientRegistration.getRegId(), redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				


				if(admittedWard!=null)
				{
				hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(admittedWard, redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				}
				else
				{
					hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					
					hcell4 = new PdfPCell(new Phrase(":", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					hcell4 = new PdfPCell(new Phrase("", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);

				}



				PdfPCell hcell15;
				hcell15 = new PdfPCell(
						new Phrase("Admission Date", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				hcell15 = new PdfPCell(
						new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				hcell15 = new PdfPCell(
						new Phrase(pdfadmissionDate, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				
				if(roomName!=null)
				{
				hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(roomName, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				}
				else
				{
					hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase(":", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase("", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
				}
				PdfPCell hcell16;
				hcell16 = new PdfPCell(new Phrase(
						"Bill No/UMR No", redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(
						":", redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getUmr(), redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);

				hcell16 = new PdfPCell(new Phrase("Bill Date", redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(":", redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(bill, redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);

				PdfPCell hcell17;
				hcell17 = new PdfPCell(new Phrase(
						"Consultant",
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(
						":",
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(
						patientRegistration.getPatientDetails().getConsultant(),
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);

				hcell17 = new PdfPCell(new Phrase("Report Date", redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(":", redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(today, redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				cell1.setColspan(2);
				cell1.addElement(table21);
				table2.addCell(cell1);
             //  table2.addCell(cell1);
               document.add(table2);
				Paragraph p12 = new Paragraph("RENAL FUNCTION TEST(RFT)", blueFont);
				p12.setAlignment(Element.ALIGN_CENTER);
				document.add(p12);

				cell1.setFixedHeight(107f);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setColspan(2);

				document.add(cell1);

				PdfPCell cell3 = new PdfPCell();
				PdfPTable table3 = new PdfPTable(4);
				table3.setWidths(new float[] { 4f, 2f, 4.5f, 2f });

				PdfPCell hcell2;
				hcell2 = new PdfPCell(new Phrase("\n PARAMETER ", font2));

				hcell2.setBorder(Rectangle.NO_BORDER);
				hcell2.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell2.setPaddingLeft(-50f);
				table3.addCell(hcell2);

				hcell2 = new PdfPCell(new Phrase(" \n RESULTS", font2));
				hcell2.setBorder(Rectangle.NO_BORDER);

				hcell2.setHorizontalAlignment(Element.ALIGN_LEFT);
				table3.addCell(hcell2);

				hcell2 = new PdfPCell(new Phrase(" \n NORMAL VALUES", font2));
				hcell2.setBorder(Rectangle.NO_BORDER);
				hcell2.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell2.setPaddingLeft(15f);
				table3.addCell(hcell2);

				hcell2 = new PdfPCell(new Phrase(" \n METHOD", font2));
				hcell2.setBorder(Rectangle.NO_BORDER);

				hcell2.setHorizontalAlignment(Element.ALIGN_LEFT);
				table3.addCell(hcell2);

				document.add(table3);

				cell3.setFixedHeight(107f);
				cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell3.setColspan(2);
				document.add(cell3);

				for (RefMeasureDetails refMeasureDetailsInfoPdf : refMeasureDetails) {
					if (refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("Random Blood Sugar")
							|| refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("Corresponding Urine Sugar")
							|| refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("Blood Urea")
							|| refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("Serum Creatinine")) {

						PdfPCell cell4 = new PdfPCell();
						PdfPTable table5 = new PdfPTable(4);
						table5.setWidths(new float[] { 4f, 2f, 4.5f, 2f });

						PdfPCell hcell5;
						hcell5 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getMeasure(), font1));
						hcell5.setBorder(Rectangle.NO_BORDER);
						hcell5.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell5.setPaddingLeft(-50f);
						table5.addCell(hcell5);

						if (refMeasureDetailsInfoPdf.getDimension() != null) {
							hcell5 = new PdfPCell(new Phrase(":  " + String.valueOf(refMeasureDetailsInfoPdf.getValue()
									+ " " + refMeasureDetailsInfoPdf.getDimension()), font1));
							hcell5.setBorder(Rectangle.NO_BORDER);

							hcell5.setHorizontalAlignment(Element.ALIGN_LEFT);
							table5.addCell(hcell5);
						} else {
							hcell5 = new PdfPCell(
									new Phrase(":  " + String.valueOf(refMeasureDetailsInfoPdf.getValue()), font1));
							hcell5.setBorder(Rectangle.NO_BORDER);

							hcell5.setHorizontalAlignment(Element.ALIGN_LEFT);
							table5.addCell(hcell5);

						}
						if (refMeasureDetailsInfoPdf.getDimension() != null) {
							hcell5 = new PdfPCell(new Phrase(
									refMeasureDetailsInfoPdf.getRange() + "" + refMeasureDetailsInfoPdf.getDimension(),
									font1));
							hcell5.setBorder(Rectangle.NO_BORDER);
							hcell5.setHorizontalAlignment(Element.ALIGN_LEFT);
							hcell5.setPaddingLeft(15f);
							table5.addCell(hcell5);
						} else {
							hcell5 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getRange(), font1));
							hcell5.setBorder(Rectangle.NO_BORDER);
							hcell5.setHorizontalAlignment(Element.ALIGN_LEFT);
							hcell5.setPaddingLeft(15f);
							table5.addCell(hcell5);
						}
						hcell5 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getMethod(), font1));
						hcell5.setBorder(Rectangle.NO_BORDER);

						hcell5.setHorizontalAlignment(Element.ALIGN_LEFT);
						table5.addCell(hcell5);

						document.add(table5);

						cell4.setFixedHeight(107f);
						cell4.setHorizontalAlignment(Element.ALIGN_CENTER);

						cell4.setColspan(2);
						document.add(cell4);

					}

				}
				Paragraph p13 = new Paragraph("SERUM ELECTROLYTES ", font2);
				p13.setAlignment(Element.ALIGN_LEFT);
				document.add(p13);

				PdfPCell cell4 = new PdfPCell();
				PdfPTable table6 = new PdfPTable(4);
				table6.setWidths(new float[] { 4f, 2f, 4.5f, 2f });

				for (RefMeasureDetails refMeasureDetailsInfoPdf : refMeasureDetails) {
					if (refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("Sodium")
							|| refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("Potassium")
							|| refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("chloride")) {

						PdfPCell hcell5;
						hcell5 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getMeasure(), font1));
						hcell5.setBorder(Rectangle.NO_BORDER);
						hcell5.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell5.setPaddingLeft(-50f);
						table6.addCell(hcell5);

						if (refMeasureDetailsInfoPdf.getDimension() != null) {
							hcell5 = new PdfPCell(new Phrase(":  " + String.valueOf(refMeasureDetailsInfoPdf.getValue()
									+ " " + refMeasureDetailsInfoPdf.getDimension()), font1));
							hcell5.setBorder(Rectangle.NO_BORDER);
							// hcell5.setPaddingLeft(-33f);
							hcell5.setHorizontalAlignment(Element.ALIGN_LEFT);
							table6.addCell(hcell5);
						} else {
							hcell5 = new PdfPCell(
									new Phrase(":  " + String.valueOf(refMeasureDetailsInfoPdf.getValue()), font1));
							hcell5.setBorder(Rectangle.NO_BORDER);
							// hcell5.setPaddingLeft(-33f);
							hcell5.setHorizontalAlignment(Element.ALIGN_LEFT);
							table6.addCell(hcell5);

						}
						if (refMeasureDetailsInfoPdf.getDimension() != null) {
							hcell5 = new PdfPCell(new Phrase(
									refMeasureDetailsInfoPdf.getRange() + "" + refMeasureDetailsInfoPdf.getDimension(),
									font1));
							hcell5.setBorder(Rectangle.NO_BORDER);
							// hcell5.setPaddingLeft(-53f);
							hcell5.setHorizontalAlignment(Element.ALIGN_LEFT);
							hcell5.setPaddingLeft(15f);
							table6.addCell(hcell5);
						} else {
							hcell5 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getRange(), font1));
							hcell5.setBorder(Rectangle.NO_BORDER);
							// hcell5.setPaddingLeft(-53f);
							hcell5.setHorizontalAlignment(Element.ALIGN_LEFT);
							hcell5.setPaddingLeft(15f);
							table6.addCell(hcell5);

						}
						hcell5 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getMethod(), font1));
						hcell5.setBorder(Rectangle.NO_BORDER);
						hcell5.setHorizontalAlignment(Element.ALIGN_LEFT);
						table6.addCell(hcell5);

					}
				}

				cell4.setFixedHeight(107f);
				cell4.setColspan(2);

				document.add(table6);
				document.add(cell4);

				if (patientServiceDetails.getComment().isEmpty()) {
					Paragraph p27 = new Paragraph("\n Findings:", font);
					document.add(p27);
					Paragraph p28 = new Paragraph("N/A", font1);
					document.add(p28);

				} else {
					Paragraph p27 = new Paragraph("\n Findings:", font);

					Paragraph p28 = new Paragraph(patientServiceDetails.getComment(), font1);
					document.add(p27);
					document.add(p28);
				}
				Paragraph p16 = new Paragraph("\n \n \n \n  *******End Of Report****** ", font1);
				p16.setAlignment(Element.ALIGN_CENTER);

				// document.add(cell3);
				document.add(p16);

				Chunk cnd = new Chunk(new VerticalPositionMark());

				Paragraph p18 = new Paragraph("\n \n  ", font);
				p18.add(cnd);
				p18.add(patientRegistration.getPatientDetails().getConsultant());
				document.add(p18);

				Paragraph p19 = new Paragraph("LAB INCHARGE", font);
				p19.add(cnd);

				p19.add("(" + patientRegistration.getVuserD().getDoctorDetails().getSpecilization() + ")");
				document.add(p19);

				document.close();
				System.out.println("finished");

				pdfBytes = byteArrayOutputStream.toByteArray();
				List<ServicePdf> servicePdfs = servicePdfServiceImpl.findByRegAndMeasureName(regId,
						patientServiceDetails.getServiceName());
				if (!servicePdfs.isEmpty()) {
					servicePdf = new ServicePdf();
					servicePdf.setSid(servicePdfs.get(0).getSid());
					servicePdf.setData(pdfBytes);
					servicePdf.setFileName(servicePdfs.get(0).getFileName());
					servicePdf.setFileuri(servicePdfs.get(0).getFileuri());
					servicePdf.setRegId(servicePdfs.get(0).getRegId());
					servicePdfServiceImpl.save(servicePdf);

				} else {
					String uri = ServletUriComponentsBuilder.fromCurrentContextPath()
							.path("/v1/lab/servicePdf/viewFile/").path(servicePdfServiceImpl.getNextLabId())
							.toUriString();

					servicePdf = new ServicePdf();
					servicePdf.setSid(servicePdfServiceImpl.getNextLabId());
					servicePdf.setData(pdfBytes);
					servicePdf.setFileName(patientServiceDetails.getServiceName());
					servicePdf.setFileuri(uri);
					servicePdf.setRegId(patientServiceDetails.getRegId());
					servicePdfServiceImpl.save(servicePdf);
				}

			} catch (Exception e) {
				Logger.error(e.getMessage());
			}

		} else if (patientServiceDetails.getServiceName().equalsIgnoreCase("ULTRA SOUND WHOLE ABDOMEN")) {

			byte[] pdfBytes = null;
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			final Font font = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
			final Font font2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD | Font.UNDERLINE);
			final Font redFont2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.ITALIC);
			final Font font1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
			final Font bluefont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
			final Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
			final Font headFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);

			Document document = new Document();

			try {
				PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);
				MyFooter event = new MyFooter(principal, userServiceImpl);
				writer.setPageEvent(event);

				document.open();

				document.add(pq);

				Paragraph p51 = new Paragraph("UDBHAVA HOSPITALS", headFont);
				p51.add("\n \n \n \n");
				p51.setAlignment(Element.ALIGN_CENTER);

				document.add(p51);

				// Display a date in day, month, year format
				date = Calendar.getInstance().getTime();
				formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
				String today = formatter.format(date).toString();

				Font redFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

				PdfPTable table2 = new PdfPTable(2);
				
				PdfPCell cell1 = new PdfPCell();
				table2.setWidthPercentage(100f);	
				cell1.setBorder(Rectangle.BOTTOM|Rectangle.TOP);
				
				PdfPTable table21 = new PdfPTable(6);
				table21.setWidths(new float[] { 5f,1f,5f,5f,1f,6.5f});
				 table21.setSpacingBefore(10);
				

				PdfPCell hcell1;
				hcell1 = new PdfPCell(
						new Phrase("Patient Name", redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(
						new Phrase(":", redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(
						new Phrase(patientRegistration.getPatientDetails().getTitle() +". "+ patientRegistration.getPatientDetails().getFirstName()
								+ " " + patientRegistration.getPatientDetails().getLastName(), redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase("Age/Gender", redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase(":", redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getAge()
						+ "/" + patientRegistration.getPatientDetails().getGender(), redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				
				
				PdfPCell hcell4;
				hcell4 = new PdfPCell(
						new Phrase("Admission No", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(
						new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(
						new Phrase(patientRegistration.getRegId(), redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				


				if(admittedWard!=null)
				{
				hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(admittedWard, redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				}
				else
				{
					hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					
					hcell4 = new PdfPCell(new Phrase(":", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					hcell4 = new PdfPCell(new Phrase("", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);

				}



				PdfPCell hcell15;
				hcell15 = new PdfPCell(
						new Phrase("Admission Date", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				hcell15 = new PdfPCell(
						new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				hcell15 = new PdfPCell(
						new Phrase(pdfadmissionDate, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				
				if(roomName!=null)
				{
				hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(roomName, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				}
				else
				{
					hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase(":", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase("", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
				}
				PdfPCell hcell16;
				hcell16 = new PdfPCell(new Phrase(
						"Bill No/UMR No", redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(
						":", redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getUmr(), redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);

				hcell16 = new PdfPCell(new Phrase("Bill Date", redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(":", redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(bill, redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);

				PdfPCell hcell17;
				hcell17 = new PdfPCell(new Phrase(
						"Consultant",
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(
						":",
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(
						patientRegistration.getPatientDetails().getConsultant(),
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);

				hcell17 = new PdfPCell(new Phrase("Report Date", redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(":", redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(today, redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				cell1.setColspan(2);
				cell1.addElement(table21);
				table2.addCell(cell1);
             //  table2.addCell(cell1);
               document.add(table2);

				Paragraph p11 = new Paragraph("ULTRA SOUND WHOLE ABDOMEN", font2);
				p11.setAlignment(Element.ALIGN_CENTER);
				p11.add("\n");
				p11.add("\n");

				document.add(p11);
				PdfPTable table3 = new PdfPTable(2);
				table3.setWidths(new float[] { 3f, 9f });
				table3.setWidthPercentage(120f);

				for (RefMeasureDetails refMeasureDetailsInfoPdf : refMeasureDetails) {
					PdfPCell hcell02;
					hcell02 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getMeasure(), font));
					hcell02.setBorder(Rectangle.NO_BORDER);
					hcell02.setPaddingLeft(60f);
					hcell02.setHorizontalAlignment(Element.ALIGN_LEFT);
					table3.addCell(hcell02);

					if (refMeasureDetailsInfoPdf.getDimension() != null) {
						hcell02 = new PdfPCell(new Phrase(
								": " + refMeasureDetailsInfoPdf.getValue() + refMeasureDetailsInfoPdf.getDimension(),
								redFont1));
						hcell02.setBorder(Rectangle.NO_BORDER);
						hcell02.setPaddingLeft(40f);
						hcell02.setHorizontalAlignment(Element.ALIGN_LEFT);
						table3.addCell(hcell02);
					} else {
						hcell02 = new PdfPCell(new Phrase(": " + refMeasureDetailsInfoPdf.getValue(), redFont1));
						hcell02.setBorder(Rectangle.NO_BORDER);
						hcell02.setPaddingLeft(40f);
						hcell02.setHorizontalAlignment(Element.ALIGN_LEFT);
						table3.addCell(hcell02);

					}
				}

				document.add(table3);

				if (patientServiceDetails.getComment().isEmpty()) {
					Paragraph p27 = new Paragraph("\n Findings:", font);
					document.add(p27);
					Paragraph p28 = new Paragraph("N/A", font1);
					document.add(p28);

				} else {
					Paragraph p27 = new Paragraph("\n Findings: ", font);

					Paragraph p28 = new Paragraph(patientServiceDetails.getComment(), font1);
					document.add(p27);
					document.add(p28);
				}

				Chunk cnd = new Chunk(new VerticalPositionMark());

				Paragraph p18 = new Paragraph("\n \n  ", font);
				p18.add(cnd);
				p18.add(patientRegistration.getPatientDetails().getConsultant());
				document.add(p18);

				Paragraph p19 = new Paragraph("LAB INCHARGE", font);
				p19.add(cnd);

				p19.add("(" + patientRegistration.getVuserD().getDoctorDetails().getSpecilization() + ")");
				document.add(p19);

				Paragraph p109 = new Paragraph(" \n Suggest Clinical Correlation", font1);
				p109.setAlignment(Element.ALIGN_CENTER);
				document.add(p109);

				document.close();

				System.out.println("finished");

				pdfBytes = byteArrayOutputStream.toByteArray();
				List<ServicePdf> servicePdfs = servicePdfServiceImpl.findByRegAndMeasureName(regId,
						patientServiceDetails.getServiceName());
				if (!servicePdfs.isEmpty()) {
					servicePdf = new ServicePdf();
					servicePdf.setSid(servicePdfs.get(0).getSid());
					servicePdf.setData(pdfBytes);
					servicePdf.setFileName(servicePdfs.get(0).getFileName());
					servicePdf.setFileuri(servicePdfs.get(0).getFileuri());
					servicePdf.setRegId(servicePdfs.get(0).getRegId());
					servicePdfServiceImpl.save(servicePdf);

				} else {
					String uri = ServletUriComponentsBuilder.fromCurrentContextPath()
							.path("/v1/lab/servicePdf/viewFile/").path(servicePdfServiceImpl.getNextLabId())
							.toUriString();

					servicePdf = new ServicePdf();
					servicePdf.setSid(servicePdfServiceImpl.getNextLabId());
					servicePdf.setData(pdfBytes);
					servicePdf.setFileName(patientServiceDetails.getServiceName());
					servicePdf.setFileuri(uri);
					servicePdf.setRegId(patientServiceDetails.getRegId());
					servicePdfServiceImpl.save(servicePdf);
				}

			} catch (Exception e) {
				Logger.error(e.getMessage());
			}

		} else if (patientServiceDetails.getServiceName().equalsIgnoreCase("2D ECHO")) {

			byte[] pdfBytes = null;
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			final Font font = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
			final Font fonts = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD | Font.UNDERLINE);

			final Font redFont2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.ITALIC);
			final Font font1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

			final Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
			final Font headFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);

			Document document = new Document();

			try {
				PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);
				MyFooter event = new MyFooter(principal, userServiceImpl);
				writer.setPageEvent(event);

				document.open();

				document.add(pq);

				Paragraph p51 = new Paragraph("UDBHAVA HOSPITALS", headFont);
				p51.setAlignment(Element.ALIGN_CENTER);
				p51.add("\n \n	\n \n");
				document.add(p51);

				// Display a date in day, month, year format
				date = Calendar.getInstance().getTime();
				formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
				String today = formatter.format(date).toString();

				Font redFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
				PdfPTable table2 = new PdfPTable(2);
				
				PdfPCell cell1 = new PdfPCell();
				table2.setWidthPercentage(100f);	
				cell1.setBorder(Rectangle.BOTTOM|Rectangle.TOP);
				
				PdfPTable table21 = new PdfPTable(6);
				table21.setWidths(new float[] { 5f,1f,5f,5f,1f,6.5f});
				 table21.setSpacingBefore(10);
				

				PdfPCell hcell1;
				hcell1 = new PdfPCell(
						new Phrase("Patient Name", redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(
						new Phrase(":", redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(
						new Phrase(patientRegistration.getPatientDetails().getTitle() +". "+ patientRegistration.getPatientDetails().getFirstName()
								+ " " + patientRegistration.getPatientDetails().getLastName(), redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase("Age/Gender", redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase(":", redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getAge()
						+ "/" + patientRegistration.getPatientDetails().getGender(), redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				
				
				PdfPCell hcell4;
				hcell4 = new PdfPCell(
						new Phrase("Admission No", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(
						new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(
						new Phrase(patientRegistration.getRegId(), redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				


				if(admittedWard!=null)
				{
				hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(admittedWard, redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				}
				else
				{
					hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					
					hcell4 = new PdfPCell(new Phrase(":", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					hcell4 = new PdfPCell(new Phrase("", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);

				}



				PdfPCell hcell15;
				hcell15 = new PdfPCell(
						new Phrase("Admission Date", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				hcell15 = new PdfPCell(
						new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				hcell15 = new PdfPCell(
						new Phrase(pdfadmissionDate, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				
				if(roomName!=null)
				{
				hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(roomName, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				}
				else
				{
					hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase(":", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase("", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
				}
				PdfPCell hcell16;
				hcell16 = new PdfPCell(new Phrase(
						"Bill No/UMR No", redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(
						":", redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getUmr(), redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);

				hcell16 = new PdfPCell(new Phrase("Bill Date", redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(":", redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(bill, redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);

				PdfPCell hcell17;
				hcell17 = new PdfPCell(new Phrase(
						"Consultant",
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(
						":",
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(
						patientRegistration.getPatientDetails().getConsultant(),
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);

				hcell17 = new PdfPCell(new Phrase("Report Date", redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(":", redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(today, redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				cell1.setColspan(2);
				cell1.addElement(table21);
				table2.addCell(cell1);
             //  table2.addCell(cell1);
               document.add(table2);
               
				Paragraph p11 = new Paragraph("2D ECHO REPORT", fonts);
				p11.setAlignment(Element.ALIGN_CENTER);
				document.add(p11);

				if (patientServiceDetails.getComment().isEmpty()) {
					Paragraph p27 = new Paragraph("\n Findings:", font);
					document.add(p27);
					Paragraph p28 = new Paragraph("N/A", font1);
					document.add(p28);

				} else {
					Paragraph p27 = new Paragraph("\n ", font);

					Paragraph p28 = new Paragraph(patientServiceDetails.getComment(), font1);
					document.add(p27);
					document.add(p28);
				}

				Chunk cnd = new Chunk(new VerticalPositionMark());
				Paragraph p19 = new Paragraph(patientRegistration.getPatientDetails().getConsultant(), font);
				p19.setAlignment(Element.ALIGN_RIGHT);
				document.add(p19);

				Paragraph p18 = new Paragraph("LAB INCHARGE", font);
				p18.add(cnd);

				p18.add("(" + patientRegistration.getVuserD().getDoctorDetails().getSpecilization() + ")");
				document.add(p18);
				document.close();

				System.out.println("finished");

				pdfBytes = byteArrayOutputStream.toByteArray();
				List<ServicePdf> servicePdfs = servicePdfServiceImpl.findByRegAndMeasureName(regId,
						patientServiceDetails.getServiceName());
				if (!servicePdfs.isEmpty()) {
					servicePdf = new ServicePdf();
					servicePdf.setSid(servicePdfs.get(0).getSid());
					servicePdf.setData(pdfBytes);
					servicePdf.setFileName(servicePdfs.get(0).getFileName());
					servicePdf.setFileuri(servicePdfs.get(0).getFileuri());
					servicePdf.setRegId(servicePdfs.get(0).getRegId());
					servicePdfServiceImpl.save(servicePdf);

				} else {
					String uri = ServletUriComponentsBuilder.fromCurrentContextPath()
							.path("/v1/lab/servicePdf/viewFile/").path(servicePdfServiceImpl.getNextLabId())
							.toUriString();

					servicePdf = new ServicePdf();
					servicePdf.setSid(servicePdfServiceImpl.getNextLabId());
					servicePdf.setData(pdfBytes);
					servicePdf.setFileName(patientServiceDetails.getServiceName());
					servicePdf.setFileuri(uri);
					servicePdf.setRegId(patientServiceDetails.getRegId());
					servicePdfServiceImpl.save(servicePdf);
				}

			} catch (Exception e) {
				Logger.error(e.getMessage());
			}

		}

		else if (patientServiceDetails.getServiceName().equalsIgnoreCase("COMPLETE BLOOD PICTURE")) {

			byte[] pdfBytes = null;
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			final Font font = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
			final Font font2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.UNDERLINE | Font.BOLD);
			final Font font1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
			final Font bluefont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
			final Font bluefont1 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.UNDERLINE | Font.BOLD);

			final Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
			final Font headFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);

			Document document = new Document();

			try {
				PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);
				MyFooter event = new MyFooter(principal, userServiceImpl);
				writer.setPageEvent(event);

				document.open();

				document.add(pq);

				Paragraph p51 = new Paragraph("UDBHAVA HOSPITALS", headFont);
				p51.add("\n \n \n \n");
				p51.setAlignment(Element.ALIGN_CENTER);
				document.add(p51);

				// Display a date in day, month, year format
				date = Calendar.getInstance().getTime();
				formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
				String today = formatter.format(date).toString();

				Font redFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

				Paragraph p50 = new Paragraph("\n \n", headFont);

				p50.setAlignment(Element.ALIGN_CENTER);
				document.add(p50);

				PdfPTable table2 = new PdfPTable(2);
				
				PdfPCell cell1 = new PdfPCell();
				table2.setWidthPercentage(100f);	
				cell1.setBorder(Rectangle.BOTTOM|Rectangle.TOP);
				
				PdfPTable table21 = new PdfPTable(6);
				table21.setWidths(new float[] { 5f,1f,5f,5f,1f,6.5f});
				 table21.setSpacingBefore(10);
				

				PdfPCell hcell1;
				hcell1 = new PdfPCell(
						new Phrase("Patient Name", redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(
						new Phrase(":", redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(
						new Phrase(patientRegistration.getPatientDetails().getTitle() +". "+ patientRegistration.getPatientDetails().getFirstName()
								+ " " + patientRegistration.getPatientDetails().getLastName(), redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase("Age/Gender", redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase(":", redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getAge()
						+ "/" + patientRegistration.getPatientDetails().getGender(), redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				
				
				PdfPCell hcell4;
				hcell4 = new PdfPCell(
						new Phrase("Admission No", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(
						new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(
						new Phrase(patientRegistration.getRegId(), redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				


				if(admittedWard!=null)
				{
				hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(admittedWard, redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				}
				else
				{
					hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					
					hcell4 = new PdfPCell(new Phrase(":", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					hcell4 = new PdfPCell(new Phrase("", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);

				}



				PdfPCell hcell15;
				hcell15 = new PdfPCell(
						new Phrase("Admission Date", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				hcell15 = new PdfPCell(
						new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				hcell15 = new PdfPCell(
						new Phrase(pdfadmissionDate, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				
				if(roomName!=null)
				{
				hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(roomName, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				}
				else
				{
					hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase(":", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase("", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
				}
				PdfPCell hcell16;
				hcell16 = new PdfPCell(new Phrase(
						"Bill No/UMR No", redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(
						":", redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getUmr(), redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);

				hcell16 = new PdfPCell(new Phrase("Bill Date", redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(":", redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(bill, redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);

				PdfPCell hcell17;
				hcell17 = new PdfPCell(new Phrase(
						"Consultant",
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(
						":",
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(
						patientRegistration.getPatientDetails().getConsultant(),
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);

				hcell17 = new PdfPCell(new Phrase("Report Date", redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(":", redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(today, redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				cell1.setColspan(2);
				cell1.addElement(table21);
				table2.addCell(cell1);
             //  table2.addCell(cell1);
               document.add(table2);

				Paragraph p11 = new Paragraph("DEPARTMENT OF HAEMATOLOGY", bluefont1);
				p11.setAlignment(Element.ALIGN_CENTER);

				document.add(p11);

				Paragraph p304 = new Paragraph("\n \n");
				document.add(p304);

				Paragraph p12 = new Paragraph("COMPLETE BLOOD PICTURE", bluefont1);
				p12.setAlignment(Element.ALIGN_LEFT);

				document.add(p12);
				Paragraph p102 = new Paragraph("\n");
				document.add(p102);
				// *******************

				PdfPTable table3 = new PdfPTable(3);
				table3.setWidths(new float[] { 5f, 7f, 7f });

				PdfPCell hcell01;
				hcell01 = new PdfPCell(new Phrase("INVESTIGATION", font2));
				hcell01.setBorder(Rectangle.NO_BORDER);
				hcell01.setPaddingLeft(-50f);
				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);

				table3.addCell(hcell01);

				hcell01 = new PdfPCell(new Phrase("RESULT", font2));
				hcell01.setBorder(Rectangle.NO_BORDER);

				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
				table3.addCell(hcell01);

				hcell01 = new PdfPCell(new Phrase("NORMAL RANGE", font2));
				hcell01.setBorder(Rectangle.NO_BORDER);
				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
				table3.addCell(hcell01);

				for (RefMeasureDetails refMeasureDetailsInfoPdf : refMeasureDetails) {
					if (refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("Haemoglobin")
							|| refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("Total RBC count")
							|| refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("Total WBC count"))

					{
						PdfPCell hcell02;
						hcell02 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getMeasure(), redFont1));
						hcell02.setBorder(Rectangle.NO_BORDER);
						hcell02.setPaddingLeft(-50f);
						hcell02.setHorizontalAlignment(Element.ALIGN_LEFT);
						table3.addCell(hcell02);

						if (refMeasureDetailsInfoPdf.getDimension() != null) {
							hcell02 = new PdfPCell(new Phrase(": " + refMeasureDetailsInfoPdf.getValue()
									+ refMeasureDetailsInfoPdf.getDimension(), redFont1));
							hcell02.setBorder(Rectangle.NO_BORDER);
							hcell02.setHorizontalAlignment(Element.ALIGN_LEFT);
							table3.addCell(hcell02);
						} else {
							hcell02 = new PdfPCell(new Phrase(": " + refMeasureDetailsInfoPdf.getValue(), redFont1));
							hcell02.setBorder(Rectangle.NO_BORDER);
							hcell02.setHorizontalAlignment(Element.ALIGN_LEFT);
							table3.addCell(hcell02);

						}

						if (refMeasureDetailsInfoPdf.getDimension() != null) {
							hcell02 = new PdfPCell(new Phrase(
									refMeasureDetailsInfoPdf.getRange() + " " + refMeasureDetailsInfoPdf.getDimension(),
									redFont1));
							hcell02.setBorder(Rectangle.NO_BORDER);
							hcell02.setHorizontalAlignment(Element.ALIGN_LEFT);
							table3.addCell(hcell02);
						} else {
							hcell02 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getRange(), redFont1));
							hcell02.setBorder(Rectangle.NO_BORDER);
							hcell02.setHorizontalAlignment(Element.ALIGN_LEFT);
							table3.addCell(hcell02);
						}
					}

				}
				document.add(table3);

				PdfPTable table4 = new PdfPTable(3);
				table4.setWidths(new float[] { 5f, 7f, 7f });

				PdfPCell hcell03;
				hcell03 = new PdfPCell(new Phrase("DIFFERENTAL COUNT", font2));
				hcell03.setBorder(Rectangle.NO_BORDER);
				hcell03.setPaddingTop(25f);
				hcell03.setPaddingLeft(-50f);
				hcell03.setHorizontalAlignment(Element.ALIGN_LEFT);

				table4.addCell(hcell03);

				hcell03 = new PdfPCell(new Phrase("", headFont));
				hcell03.setBorder(Rectangle.NO_BORDER);
				// hcell1.setPaddingRight(-40f);
				// hcell01.setPaddingTop(50f);
				// hcell01.setPaddingLeft(80f);
				hcell03.setHorizontalAlignment(Element.ALIGN_CENTER);
				table4.addCell(hcell03);

				hcell03 = new PdfPCell(new Phrase("", font));
				hcell03.setBorder(Rectangle.NO_BORDER);
				// hcell1.setPaddingRight(-40f);
				hcell03.setPaddingTop(25f);
				// hcell01.setPaddingLeft(80f);
				hcell03.setHorizontalAlignment(Element.ALIGN_LEFT);
				table4.addCell(hcell03);

				for (RefMeasureDetails refMeasureDetailsInfoPdf : refMeasureDetails) {
					if (refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("Neuthrophils")
							|| refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("Lymphocytes")
							|| refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("Eosinophils")
							|| refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("Monocytes")
							|| refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("Basophils")
							|| refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("Platelet Count")) {

						PdfPCell hcell04;
						hcell04 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getMeasure(), redFont1));
						hcell04.setBorder(Rectangle.NO_BORDER);
						hcell04.setPaddingLeft(-50f);
						hcell04.setHorizontalAlignment(Element.ALIGN_LEFT);
						table4.addCell(hcell04);

						if (refMeasureDetailsInfoPdf.getDimension() != null) {
							hcell04 = new PdfPCell(new Phrase(": " + refMeasureDetailsInfoPdf.getValue()
									+ refMeasureDetailsInfoPdf.getDimension(), redFont1));
							hcell04.setBorder(Rectangle.NO_BORDER);
							// hcell4.setPaddingRight(5f);
							hcell04.setHorizontalAlignment(Element.ALIGN_LEFT);
							// hcell02.setPaddingLeft(80f);

							// hcell4.setHorizontalAlignment(Element.ALIGN_RIGHT);
							table4.addCell(hcell04);
						} else {
							hcell04 = new PdfPCell(new Phrase(": " + refMeasureDetailsInfoPdf.getValue(), redFont1));
							hcell04.setBorder(Rectangle.NO_BORDER);
							// hcell4.setPaddingRight(5f);
							hcell04.setHorizontalAlignment(Element.ALIGN_LEFT);
							// hcell02.setPaddingLeft(80f);

							// hcell4.setHorizontalAlignment(Element.ALIGN_RIGHT);
							table4.addCell(hcell04);

						}
						if (refMeasureDetailsInfoPdf.getDimension() != null) {
							hcell04 = new PdfPCell(new Phrase(
									refMeasureDetailsInfoPdf.getRange() + " " + refMeasureDetailsInfoPdf.getDimension(),
									redFont1));
							hcell04.setBorder(Rectangle.NO_BORDER);
							// hcell4.setPaddingRight(5f);
							// hcell02.setPaddingLeft(80f);
							hcell04.setHorizontalAlignment(Element.ALIGN_LEFT);
							table4.addCell(hcell04);
						} else {
							hcell04 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getRange(), redFont1));
							hcell04.setBorder(Rectangle.NO_BORDER);
							// hcell4.setPaddingRight(5f);
							// hcell02.setPaddingLeft(80f);
							hcell04.setHorizontalAlignment(Element.ALIGN_LEFT);
							table4.addCell(hcell04);

						}
					}
				}
				document.add(table4);

				PdfPTable table5 = new PdfPTable(2);
				table5.setWidths(new float[] { 5f, 7f });

				PdfPTable table6 = new PdfPTable(2);
				table6.setWidths(new float[] { 5f, 7f });

				PdfPCell hcell103;
				hcell103 = new PdfPCell(new Phrase("PERIPHERAL SMEAR", font2));
				hcell103.setBorder(Rectangle.NO_BORDER);
				hcell103.setPaddingTop(25f);
				hcell103.setPaddingLeft(-50f);
				hcell103.setHorizontalAlignment(Element.ALIGN_LEFT);
				table5.addCell(hcell103);

				hcell103 = new PdfPCell(new Phrase("", font));
				hcell103.setBorder(Rectangle.NO_BORDER);
				hcell103.setPaddingTop(25f);
				hcell103.setPaddingLeft(-50f);
				hcell103.setHorizontalAlignment(Element.ALIGN_LEFT);
				table5.addCell(hcell103);

				document.add(table6);

				for (RefMeasureDetails refMeasureDetailsInfoPdf : refMeasureDetails) {
					if (refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("R.B.C")
							|| refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("W.B.C")
							|| refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("Platelets")) {
						PdfPCell hcell04;
						hcell04 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getMeasure(), redFont1));
						hcell04.setBorder(Rectangle.NO_BORDER);
						hcell04.setPaddingLeft(-50f);
						hcell04.setHorizontalAlignment(Element.ALIGN_LEFT);
						table5.addCell(hcell04);

						if (refMeasureDetailsInfoPdf.getDimension() != null) {
							hcell04 = new PdfPCell(new Phrase(": " + refMeasureDetailsInfoPdf.getValue()
									+ refMeasureDetailsInfoPdf.getDimension(), redFont1));
							hcell04.setBorder(Rectangle.NO_BORDER);
							// hcell4.setPaddingRight(5f);
							hcell04.setHorizontalAlignment(Element.ALIGN_LEFT);
							hcell04.setPaddingLeft(-63f);

							// hcell4.setHorizontalAlignment(Element.ALIGN_RIGHT);
							table5.addCell(hcell04);
						} else {
							hcell04 = new PdfPCell(new Phrase(": " + refMeasureDetailsInfoPdf.getValue(), redFont1));
							hcell04.setBorder(Rectangle.NO_BORDER);
							// hcell4.setPaddingRight(5f);
							hcell04.setHorizontalAlignment(Element.ALIGN_LEFT);
							hcell04.setPaddingLeft(-63f);

							// hcell4.setHorizontalAlignment(Element.ALIGN_RIGHT);
							table5.addCell(hcell04);

						}

					}
				}
				document.add(table5);

				Font redFont2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.ITALIC);
				Paragraph p6 = new Paragraph("\n (Automated cellcounter microscopy)", redFont2);
				p6.setAlignment(Element.ALIGN_LEFT);
				p6.add("\n");
				// p6.add("\n");
				document.add(p6);

				if (patientServiceDetails.getComment().isEmpty()) {
					Paragraph p27 = new Paragraph("\n Findings:", font);
					document.add(p27);
					Paragraph p28 = new Paragraph("N/A", font1);
					document.add(p28);

				} else {
					Paragraph p27 = new Paragraph("\n Findings:", font);

					Paragraph p28 = new Paragraph(patientServiceDetails.getComment(), font1);
					document.add(p27);
					document.add(p28);
				}

				Paragraph p7 = new Paragraph("*Please correlate with clinical findings if necessary discuss", redFont2);
				p7.setAlignment(Element.ALIGN_CENTER);
				p7.add("\n");
				document.add(p7);

				Chunk cnd = new Chunk(new VerticalPositionMark());
				Paragraph p19 = new Paragraph(patientRegistration.getPatientDetails().getConsultant(), font1);
				p19.setAlignment(Element.ALIGN_RIGHT);
				document.add(p19);

				Paragraph p18 = new Paragraph("LAB INCHARGE", font);
				p18.add(cnd);

				p18.add("(" + patientRegistration.getVuserD().getDoctorDetails().getSpecilization() + ")");
				document.add(p18);
				Paragraph p202 = new Paragraph();

				document.add(p202);

				document.close();

				System.out.println("finished");

				pdfBytes = byteArrayOutputStream.toByteArray();
				List<ServicePdf> servicePdfs = servicePdfServiceImpl.findByRegAndMeasureName(regId,
						patientServiceDetails.getServiceName());
				if (!servicePdfs.isEmpty()) {
					servicePdf = new ServicePdf();
					servicePdf.setSid(servicePdfs.get(0).getSid());
					servicePdf.setData(pdfBytes);
					servicePdf.setFileName(servicePdfs.get(0).getFileName());
					servicePdf.setFileuri(servicePdfs.get(0).getFileuri());
					servicePdf.setRegId(servicePdfs.get(0).getRegId());
					servicePdfServiceImpl.save(servicePdf);

				} else {
					String uri = ServletUriComponentsBuilder.fromCurrentContextPath()
							.path("/v1/lab/servicePdf/viewFile/").path(servicePdfServiceImpl.getNextLabId())
							.toUriString();

					servicePdf = new ServicePdf();
					servicePdf.setSid(servicePdfServiceImpl.getNextLabId());
					servicePdf.setData(pdfBytes);
					servicePdf.setFileName(patientServiceDetails.getServiceName());
					servicePdf.setFileuri(uri);
					servicePdf.setRegId(patientServiceDetails.getRegId());
					servicePdfServiceImpl.save(servicePdf);
				}

			} catch (Exception e) {
				Logger.error(e.getMessage());
			}
		} 
		else if (patientServiceDetails.getServiceName().equalsIgnoreCase("COMPLETE URINE EXAMINATION")) {

			byte[] pdfBytes = null;
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			final Font font = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);

			final Font font2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD | Font.UNDERLINE);
			final Font font1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

			final Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
			final Font headFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);

			Document document = new Document();

			try {
				PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);

				MyFooter event = new MyFooter(principal, userServiceImpl);
				writer.setPageEvent(event);

				document.open();

				document.add(pq);

				Paragraph p51 = new Paragraph("UDBHAVA HOSPITALS", headFont);
				p51.add("\n \n \n");
				p51.setAlignment(Element.ALIGN_CENTER);
				document.add(p51);

				// table2.setSpacingBefore(10);
				Font redFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

				date = Calendar.getInstance().getTime();
				formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
				String today = formatter.format(date).toString();

				PdfPTable table2 = new PdfPTable(2);
				
				PdfPCell cell1 = new PdfPCell();
				table2.setWidthPercentage(100f);	
				cell1.setBorder(Rectangle.BOTTOM|Rectangle.TOP);
				
				PdfPTable table21 = new PdfPTable(6);
				table21.setWidths(new float[] { 5f,1f,5f,5f,1f,6.5f});
				 table21.setSpacingBefore(10);
				

				PdfPCell hcell1;
				hcell1 = new PdfPCell(
						new Phrase("Patient Name", redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(
						new Phrase(":", redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(
						new Phrase(patientRegistration.getPatientDetails().getTitle() +". "+ patientRegistration.getPatientDetails().getFirstName()
								+ " " + patientRegistration.getPatientDetails().getLastName(), redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase("Age/Gender", redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase(":", redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getAge()
						+ "/" + patientRegistration.getPatientDetails().getGender(), redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				
				
				PdfPCell hcell4;
				hcell4 = new PdfPCell(
						new Phrase("Admission No", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(
						new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(
						new Phrase(patientRegistration.getRegId(), redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				


				if(admittedWard!=null)
				{
				hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(admittedWard, redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				}
				else
				{
					hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					
					hcell4 = new PdfPCell(new Phrase(":", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					hcell4 = new PdfPCell(new Phrase("", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);

				}



				PdfPCell hcell15;
				hcell15 = new PdfPCell(
						new Phrase("Admission Date", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				hcell15 = new PdfPCell(
						new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				hcell15 = new PdfPCell(
						new Phrase(pdfadmissionDate, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				
				if(roomName!=null)
				{
				hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(roomName, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				}
				else
				{
					hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase(":", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase("", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
				}
				PdfPCell hcell16;
				hcell16 = new PdfPCell(new Phrase(
						"Bill No/UMR No", redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(
						":", redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getUmr(), redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);

				hcell16 = new PdfPCell(new Phrase("Bill Date", redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(":", redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(bill, redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);

				PdfPCell hcell17;
				hcell17 = new PdfPCell(new Phrase(
						"Consultant",
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(
						":",
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(
						patientRegistration.getPatientDetails().getConsultant(),
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);

				hcell17 = new PdfPCell(new Phrase("Report Date", redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(":", redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(today, redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				cell1.setColspan(2);
				cell1.addElement(table21);
				table2.addCell(cell1);
             //  table2.addCell(cell1);
               document.add(table2);
               
				Paragraph p11 = new Paragraph("DEPARTMENT OF PATHOLOGY", font2);
				p11.setAlignment(Element.ALIGN_CENTER);
				document.add(p11);

				Paragraph p304 = new Paragraph("\n");
				document.add(p304);

				Paragraph p13 = new Paragraph("COMPLETE URINE EXAMINATION", font2);
				p13.setAlignment(Element.ALIGN_CENTER);
				document.add(p13);

				Paragraph p303 = new Paragraph("\n");
				document.add(p303);

				PdfPTable table0 = new PdfPTable(2);
				table0.setWidths(new float[] { 4f, 4f });

				PdfPCell hcell01;
				hcell01 = new PdfPCell(new Phrase("INVESTIGATION", font2));
				hcell01.setBorder(Rectangle.NO_BORDER);
				// hcell01.setPaddingTop(250f);
				hcell01.setPaddingLeft(-50f);
				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);

				table0.addCell(hcell01);

				hcell01 = new PdfPCell(new Phrase("RESULTS", font2));
				hcell01.setBorder(Rectangle.NO_BORDER);
				// hcell1.setPaddingRight(-40f);
				// hcell01.setPaddingTop(50f);
				hcell01.setPaddingRight(150f);
				hcell01.setHorizontalAlignment(Element.ALIGN_CENTER);
				table0.addCell(hcell01);
				document.add(table0);

				Paragraph p14 = new Paragraph("PHYSICAL EXAMINATION", font2);
				p14.setAlignment(Element.ALIGN_LEFT);
				document.add(p14);
				/*
				 * Paragraph p302 = new Paragraph("\n"); document.add(p302);
				 */

				PdfPTable table01 = new PdfPTable(2);
				table01.setWidths(new float[] { 4f, 4f });

				for (RefMeasureDetails refMeasureDetailsInfoPdf : refMeasureDetails) {
					if (refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("Colour")
							|| refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("Appearance"))

					{
						PdfPCell hcell001;
						hcell001 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getMeasure(), redFont1));
						hcell001.setBorder(Rectangle.NO_BORDER);
						hcell001.setPaddingLeft(-50f);
						hcell001.setHorizontalAlignment(Element.ALIGN_LEFT);
						table01.addCell(hcell001);

						if (refMeasureDetailsInfoPdf.getDimension() != null) {
							hcell001 = new PdfPCell(new Phrase(": " + refMeasureDetailsInfoPdf.getValue() + " "
									+ refMeasureDetailsInfoPdf.getDimension(), redFont1));
							hcell001.setBorder(Rectangle.NO_BORDER);
							// hcell4.setPaddingRight(5f);
							hcell001.setPaddingRight(150f);
							hcell001.setHorizontalAlignment(Element.ALIGN_LEFT);
							table01.addCell(hcell001);
						} else {
							hcell001 = new PdfPCell(new Phrase(": " + refMeasureDetailsInfoPdf.getValue(), redFont1));
							hcell001.setBorder(Rectangle.NO_BORDER);
							// hcell4.setPaddingRight(5f);
							hcell001.setPaddingRight(150f);
							hcell001.setHorizontalAlignment(Element.ALIGN_LEFT);
							table01.addCell(hcell001);
						}
					}
				}
				document.add(table01);
				/*
				 * Paragraph p17 = new Paragraph(""); p17.setAlignment(Element.ALIGN_LEFT);
				 * p17.add("\n"); document.add(p17);
				 */
				Paragraph p15 = new Paragraph("CHEMICAL EXAMINATION", font2);
				p15.setAlignment(Element.ALIGN_LEFT);
				document.add(p15);

				/*
				 * Paragraph p301 = new Paragraph("\n"); document.add(p301);
				 */

				PdfPTable table02 = new PdfPTable(2);
				table02.setWidths(new float[] { 4f, 4f });
				for (RefMeasureDetails refMeasureDetailsInfoPdf : refMeasureDetails) {

					if (refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("Ph")
							|| refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("Specific Gravity")
							|| refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("Reaction")
							|| refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("Albumin")
							|| refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("Sugar")
							|| refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("Bile Salts")
							|| refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("Bile Pigments")

					) {

						PdfPCell hcell001;
						hcell001 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getMeasure(), redFont1));
						hcell001.setBorder(Rectangle.NO_BORDER);
						hcell001.setPaddingLeft(-50f);
						hcell001.setHorizontalAlignment(Element.ALIGN_LEFT);
						table02.addCell(hcell001);

						if (refMeasureDetailsInfoPdf.getDimension() != null) {
							hcell001 = new PdfPCell(new Phrase(": " + refMeasureDetailsInfoPdf.getValue() + " "
									+ refMeasureDetailsInfoPdf.getDimension(), redFont1));
							hcell001.setBorder(Rectangle.NO_BORDER);
							// hcell4.setPaddingRight(5f);
							hcell001.setPaddingRight(150f);
							hcell001.setHorizontalAlignment(Element.ALIGN_LEFT);
							table02.addCell(hcell001);
						} else {
							hcell001 = new PdfPCell(new Phrase(": " + refMeasureDetailsInfoPdf.getValue(), redFont1));
							hcell001.setBorder(Rectangle.NO_BORDER);
							// hcell4.setPaddingRight(5f);
							hcell001.setPaddingRight(150f);
							hcell001.setHorizontalAlignment(Element.ALIGN_LEFT);
							table02.addCell(hcell001);
						}
					}
				}
				document.add(table02);

				Paragraph p011 = new Paragraph("", font2);
				p011.add("MICROSCOPIC EXAMINATION ");
				document.add(p011);
				Paragraph p16 = new Paragraph("");
				p16.setAlignment(Element.ALIGN_LEFT);
				p16.add("\n");
				document.add(p16);

				PdfPTable table3 = new PdfPTable(2);
				table3.setWidths(new float[] { 4f, 4f });

				for (RefMeasureDetails refMeasureDetailsInfoPdf : refMeasureDetails) {
					if (refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("Pus Cells")
							|| refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("Epithelial Cells")
							|| refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("RBC")
							|| refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("Crystals")
							|| refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("Casts")
							|| refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("Amorpous Material")
							|| refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("Others")) {

						PdfPCell hcell021;
						hcell021 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getMeasure(), redFont1));
						hcell021.setBorder(Rectangle.NO_BORDER);
						hcell021.setPaddingLeft(-50f);
						hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
						table3.addCell(hcell021);

						if (refMeasureDetailsInfoPdf.getDimension() != null) {
							hcell021 = new PdfPCell(new Phrase(": " + refMeasureDetailsInfoPdf.getValue() + " "
									+ refMeasureDetailsInfoPdf.getDimension(), redFont1));
							hcell021.setBorder(Rectangle.NO_BORDER);
							// hcell4.setPaddingRight(5f);
							hcell021.setPaddingRight(150f);
							hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
							table3.addCell(hcell021);
						} else {
							hcell021 = new PdfPCell(new Phrase(": " + refMeasureDetailsInfoPdf.getValue(), redFont1));
							hcell021.setBorder(Rectangle.NO_BORDER);
							// hcell4.setPaddingRight(5f);
							hcell021.setPaddingRight(150f);
							hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
							table3.addCell(hcell021);
						}
					}

				}

				document.add(table3);
				/*
				 * Paragraph p01 = new Paragraph();
				 * 
				 * p01.add("\n"); p01.add("\n"); document.add(p01);
				 */
				if (patientServiceDetails.getComment().isEmpty()) {
					Paragraph p27 = new Paragraph("Findings:", font);
					document.add(p27);
					Paragraph p28 = new Paragraph("N/A", redFont1);
					document.add(p28);

				} else {
					Paragraph p27 = new Paragraph("Findings:", font);

					Paragraph p28 = new Paragraph(patientServiceDetails.getComment(), redFont1);
					document.add(p27);
					document.add(p28);
				}

				Paragraph p7 = new Paragraph("*Please correlate with clinical findings if necessary discuss", redFont1);
				p7.setAlignment(Element.ALIGN_CENTER);

				document.add(p7);
				Chunk cnd = new Chunk(new VerticalPositionMark());
				Paragraph p19 = new Paragraph(patientRegistration.getPatientDetails().getConsultant(), font);
				p19.setAlignment(Element.ALIGN_RIGHT);
				document.add(p19);

				Paragraph p18 = new Paragraph("LAB INCHARGE", font);
				p18.add(cnd);

				p18.add("(" + patientRegistration.getVuserD().getDoctorDetails().getSpecilization() + ")");
				document.add(p18);

				// ************************

				document.close();
				System.out.println("finished");

				pdfBytes = byteArrayOutputStream.toByteArray();
				List<ServicePdf> servicePdfs = servicePdfServiceImpl.findByRegAndMeasureName(regId,
						patientServiceDetails.getServiceName());
				if (!servicePdfs.isEmpty()) {
					servicePdf = new ServicePdf();
					servicePdf.setSid(servicePdfs.get(0).getSid());
					servicePdf.setData(pdfBytes);
					servicePdf.setFileName(servicePdfs.get(0).getFileName());
					servicePdf.setFileuri(servicePdfs.get(0).getFileuri());
					servicePdf.setRegId(servicePdfs.get(0).getRegId());
					servicePdfServiceImpl.save(servicePdf);

				} else {
					String uri = ServletUriComponentsBuilder.fromCurrentContextPath()
							.path("/v1/lab/servicePdf/viewFile/").path(servicePdfServiceImpl.getNextLabId())
							.toUriString();

					servicePdf = new ServicePdf();
					servicePdf.setSid(servicePdfServiceImpl.getNextLabId());
					servicePdf.setData(pdfBytes);
					servicePdf.setFileName(patientServiceDetails.getServiceName());
					servicePdf.setFileuri(uri);
					servicePdf.setRegId(patientServiceDetails.getRegId());
					servicePdfServiceImpl.save(servicePdf);
				}

			} catch (Exception e) {
				Logger.error(e.getMessage());
			}

		}
		// remaining pdfs from here
		else if (patientServiceDetails.getServiceName().equalsIgnoreCase("WIDAL TEST")) {

			byte[] pdfBytes = null;
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			Document document = new Document();

			try {
				PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);

				MyFooter event = new MyFooter(principal, userServiceImpl);
				writer.setPageEvent(event);

				Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
				Font redFont3 = new Font(Font.FontFamily.TIMES_ROMAN, 17, Font.BOLD);
				Font redFont2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.ITALIC);
				Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
				Font headFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD | Font.UNDERLINE);

				document.open();

				document.add(pq);

				Paragraph p51 = new Paragraph("UDBHAVA HOSPITALS", redFont);
				p51.add("\n \n \n \n");
				p51.setAlignment(Element.ALIGN_CENTER);
				document.add(p51);
				Font redFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

				date = Calendar.getInstance().getTime();
				formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
				String today = formatter.format(date).toString();

				PdfPTable table2 = new PdfPTable(2);
				
				PdfPCell cell1 = new PdfPCell();
				table2.setWidthPercentage(100f);	
				cell1.setBorder(Rectangle.BOTTOM|Rectangle.TOP);
				
				PdfPTable table21 = new PdfPTable(6);
				table21.setWidths(new float[] { 5f,1f,5f,5f,1f,6.5f});
				 table21.setSpacingBefore(10);
				

				PdfPCell hcell1;
				hcell1 = new PdfPCell(
						new Phrase("Patient Name", redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(
						new Phrase(":", redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(
						new Phrase(patientRegistration.getPatientDetails().getTitle() +". "+ patientRegistration.getPatientDetails().getFirstName()
								+ " " + patientRegistration.getPatientDetails().getLastName(), redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase("Age/Gender", redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase(":", redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getAge()
						+ "/" + patientRegistration.getPatientDetails().getGender(), redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				
				
				PdfPCell hcell4;
				hcell4 = new PdfPCell(
						new Phrase("Admission No", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(
						new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(
						new Phrase(patientRegistration.getRegId(), redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				


				if(admittedWard!=null)
				{
				hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(admittedWard, redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				}
				else
				{
					hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					
					hcell4 = new PdfPCell(new Phrase(":", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					hcell4 = new PdfPCell(new Phrase("", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);

				}



				PdfPCell hcell15;
				hcell15 = new PdfPCell(
						new Phrase("Admission Date", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				hcell15 = new PdfPCell(
						new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				hcell15 = new PdfPCell(
						new Phrase(pdfadmissionDate, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				
				if(roomName!=null)
				{
				hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(roomName, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				}
				else
				{
					hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase(":", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase("", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
				}
				PdfPCell hcell16;
				hcell16 = new PdfPCell(new Phrase(
						"Bill No/UMR No", redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(
						":", redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getUmr(), redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);

				hcell16 = new PdfPCell(new Phrase("Bill Date", redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(":", redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(bill, redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);

				PdfPCell hcell17;
				hcell17 = new PdfPCell(new Phrase(
						"Consultant",
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(
						":",
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(
						patientRegistration.getPatientDetails().getConsultant(),
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);

				hcell17 = new PdfPCell(new Phrase("Report Date", redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(":", redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(today, redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				cell1.setColspan(2);
				cell1.addElement(table21);
				table2.addCell(cell1);
             //  table2.addCell(cell1);
               document.add(table2);
               
				Paragraph p11 = new Paragraph("DEPARTMENT OF SEROLOGY", headFont1);
				p11.setAlignment(Element.ALIGN_CENTER);
				document.add(p11);

				Paragraph p101 = new Paragraph("\n \n", headFont);
				p101.setAlignment(Element.ALIGN_CENTER);
				document.add(p101);

				PdfPTable table3 = new PdfPTable(2);
				table3.setWidths(new float[] { 5f, 9f });

				PdfPCell hcell01;
				hcell01 = new PdfPCell(new Phrase("\n INVESTIGATION", headFont1));
				hcell01.setBorder(Rectangle.NO_BORDER);
				// hcell01.setPaddingTop(250f);
				hcell01.setPaddingLeft(-50f);
				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);

				table3.addCell(hcell01);

				hcell01 = new PdfPCell(new Phrase("\n RESULT", headFont1));
				hcell01.setBorder(Rectangle.NO_BORDER);
				hcell01.setPaddingLeft(70f);
				// hcell1.setPaddingRight(-40f);
				// hcell01.setPaddingTop(50f);
				// hcell01.setPaddingLeft(80f);
				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
				table3.addCell(hcell01);

				PdfPTable table4 = new PdfPTable(3);
				table4.setWidths(new float[] { 5f, 9f, 7f });

				PdfPCell hcell02;
				hcell02 = new PdfPCell(new Phrase("\n", headFont));
				hcell02.setBorder(Rectangle.NO_BORDER);
				// hcell01.setPaddingTop(250f);
				hcell02.setPaddingLeft(-50f);
				hcell02.setHorizontalAlignment(Element.ALIGN_LEFT);

				table4.addCell(hcell02);

				hcell02 = new PdfPCell(new Phrase("WIDAL TEST", headFont1));
				hcell02.setBorder(Rectangle.NO_BORDER);
				// hcell1.setPaddingRight(-40f);
				// hcell01.setPaddingTop(50f);
				// hcell01.setPaddingLeft(80f);
				hcell02.setHorizontalAlignment(Element.ALIGN_CENTER);
				table4.addCell(hcell02);

				hcell02 = new PdfPCell(new Phrase("", headFont1));
				hcell02.setBorder(Rectangle.NO_BORDER);
				// hcell1.setPaddingRight(-40f);
				// hcell01.setPaddingTop(50f);
				// hcell01.setPaddingLeft(80f);
				hcell02.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table4.addCell(hcell02);

				document.add(table4);

				// List<SalesReciept>
				// a1=salesRecieptServiceImpl.findByBillNo(billNo);

				for (RefMeasureDetails refMeasureDetailsInfoPdf : refMeasureDetails) {

					PdfPCell hcell021;
					hcell021 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getMeasure(), redFont1));
					hcell021.setBorder(Rectangle.NO_BORDER);
					hcell021.setPaddingLeft(-50f);
					hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
					table3.addCell(hcell021);

					if (refMeasureDetailsInfoPdf.getDimension() != null) {
						hcell021 = new PdfPCell(new Phrase(": " + refMeasureDetailsInfoPdf.getValue() + " "
								+ refMeasureDetailsInfoPdf.getDimension(), redFont1));
						hcell021.setBorder(Rectangle.NO_BORDER);
						// hcell4.setPaddingRight(5f);
						hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell021.setPaddingLeft(50f);
						table3.addCell(hcell021);
					} else {
						hcell021 = new PdfPCell(new Phrase(": " + refMeasureDetailsInfoPdf.getValue(), redFont1));
						hcell021.setBorder(Rectangle.NO_BORDER);
						// hcell4.setPaddingRight(5f);
						hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell021.setPaddingLeft(50f);
						table3.addCell(hcell021);
					}

				}

				document.add(table3);

				if (patientServiceDetails.getComment().isEmpty()) {
					Paragraph p27 = new Paragraph("\n Findings:", headFont);
					document.add(p27);
					Paragraph p28 = new Paragraph("N/A", redFont2);
					document.add(p28);

				} else {
					Paragraph p27 = new Paragraph("\n Findings:", headFont);

					Paragraph p28 = new Paragraph(patientServiceDetails.getComment(), redFont2);
					document.add(p27);
					document.add(p28);
				}

				Paragraph p01 = new Paragraph();

				p01.add("\n");
				p01.add("\n");
				document.add(p01);

				Paragraph p7 = new Paragraph("*Please correlate with clinical findings if necessary discuss", redFont2);
				p7.setAlignment(Element.ALIGN_CENTER);

				p7.add("\n");
				p7.add("\n");
				document.add(p7);

				Chunk cnd = new Chunk(new VerticalPositionMark());
				Paragraph p19 = new Paragraph(patientRegistration.getPatientDetails().getConsultant(), headFont);
				p19.setAlignment(Element.ALIGN_RIGHT);
				document.add(p19);

				Paragraph p18 = new Paragraph("LAB INCHARGE", headFont);
				p18.add(cnd);

				p18.add("(" + patientRegistration.getVuserD().getDoctorDetails().getSpecilization() + ")");
				document.add(p18);

				// ************************

				document.close();

				System.out.println("finished");

				pdfBytes = byteArrayOutputStream.toByteArray();
				List<ServicePdf> servicePdfs = servicePdfServiceImpl.findByRegAndMeasureName(regId,
						patientServiceDetails.getServiceName());
				if (!servicePdfs.isEmpty()) {
					servicePdf = new ServicePdf();
					servicePdf.setSid(servicePdfs.get(0).getSid());
					servicePdf.setData(pdfBytes);
					servicePdf.setFileName(servicePdfs.get(0).getFileName());
					servicePdf.setFileuri(servicePdfs.get(0).getFileuri());
					servicePdf.setRegId(servicePdfs.get(0).getRegId());
					servicePdfServiceImpl.save(servicePdf);

				} else {
					String uri = ServletUriComponentsBuilder.fromCurrentContextPath()
							.path("/v1/lab/servicePdf/viewFile/").path(servicePdfServiceImpl.getNextLabId())
							.toUriString();

					servicePdf = new ServicePdf();
					servicePdf.setSid(servicePdfServiceImpl.getNextLabId());
					servicePdf.setData(pdfBytes);
					servicePdf.setFileName(patientServiceDetails.getServiceName());
					servicePdf.setFileuri(uri);
					servicePdf.setRegId(patientServiceDetails.getRegId());
					servicePdfServiceImpl.save(servicePdf);
				}

			} catch (Exception e) {
				Logger.error(e.getMessage());
			}

		}

		else if (patientServiceDetails.getServiceName().equalsIgnoreCase("C - REACTIVE PROTEIN (CRP)")) {
			byte[] pdfBytes = null;
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			Document document = new Document();

			try {
				PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);

				MyFooter event = new MyFooter(principal, userServiceImpl);
				writer.setPageEvent(event);

				Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
				Font redFont3 = new Font(Font.FontFamily.TIMES_ROMAN, 17, Font.BOLD);
				Font redFont2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.ITALIC);
				Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
				Font headFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.UNDERLINE | Font.BOLD);

				document.open();

				document.add(pq);

				Paragraph p51 = new Paragraph("UDBHAVA HOSPITALS", redFont);
				p51.add("\n \n \n");
				p51.setAlignment(Element.ALIGN_CENTER);
				document.add(p51);

				Font redFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

				date = Calendar.getInstance().getTime();
				formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
				String today = formatter.format(date).toString();

				PdfPTable table2 = new PdfPTable(2);
				
				PdfPCell cell1 = new PdfPCell();
				table2.setWidthPercentage(100f);	
				cell1.setBorder(Rectangle.BOTTOM|Rectangle.TOP);
				
				PdfPTable table21 = new PdfPTable(6);
				table21.setWidths(new float[] { 5f,1f,5f,5f,1f,6.5f});
				 table21.setSpacingBefore(10);
				

				PdfPCell hcell1;
				hcell1 = new PdfPCell(
						new Phrase("Patient Name", redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(
						new Phrase(":", redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(
						new Phrase(patientRegistration.getPatientDetails().getTitle() +". "+ patientRegistration.getPatientDetails().getFirstName()
								+ " " + patientRegistration.getPatientDetails().getLastName(), redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase("Age/Gender", redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase(":", redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getAge()
						+ "/" + patientRegistration.getPatientDetails().getGender(), redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				
				
				PdfPCell hcell4;
				hcell4 = new PdfPCell(
						new Phrase("Admission No", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(
						new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(
						new Phrase(patientRegistration.getRegId(), redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				


				if(admittedWard!=null)
				{
				hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(admittedWard, redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				}
				else
				{
					hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					
					hcell4 = new PdfPCell(new Phrase(":", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					hcell4 = new PdfPCell(new Phrase("", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);

				}



				PdfPCell hcell15;
				hcell15 = new PdfPCell(
						new Phrase("Admission Date", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				hcell15 = new PdfPCell(
						new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				hcell15 = new PdfPCell(
						new Phrase(pdfadmissionDate, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				
				if(roomName!=null)
				{
				hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(roomName, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				}
				else
				{
					hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase(":", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase("", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
				}
				PdfPCell hcell16;
				hcell16 = new PdfPCell(new Phrase(
						"Bill No/UMR No", redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(
						":", redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getUmr(), redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);

				hcell16 = new PdfPCell(new Phrase("Bill Date", redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(":", redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(bill, redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);

				PdfPCell hcell17;
				hcell17 = new PdfPCell(new Phrase(
						"Consultant",
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(
						":",
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(
						patientRegistration.getPatientDetails().getConsultant(),
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);

				hcell17 = new PdfPCell(new Phrase("Report Date", redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(":", redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(today, redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				cell1.setColspan(2);
				cell1.addElement(table21);
				table2.addCell(cell1);
             //  table2.addCell(cell1);
               document.add(table2);

				Paragraph p11 = new Paragraph("DEPARTMENT OF SEROLOGY", headFont1);
				p11.setAlignment(Element.ALIGN_CENTER);
				document.add(p11);

				Paragraph p301 = new Paragraph();
				p301.add("\n");
				p301.add("\n");
				document.add(p301);

				PdfPTable table3 = new PdfPTable(3);
				table3.setWidths(new float[] { 5f, 9f, 7f });

				PdfPCell hcell01;
				hcell01 = new PdfPCell(new Phrase("INVESTIGATION ", headFont1));
				hcell01.setBorder(Rectangle.NO_BORDER);
				hcell01.setPaddingLeft(-50f);
				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);

				table3.addCell(hcell01);

				hcell01 = new PdfPCell(new Phrase("RESULT ", headFont1));
				hcell01.setBorder(Rectangle.NO_BORDER);

				hcell01.setHorizontalAlignment(Element.ALIGN_CENTER);
				table3.addCell(hcell01);

				hcell01 = new PdfPCell(new Phrase("NORMAL RANGE ", headFont1));
				hcell01.setBorder(Rectangle.NO_BORDER);

				hcell01.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table3.addCell(hcell01);

				for (RefMeasureDetails refMeasureDetailsInfoPdf : refMeasureDetails) {

					PdfPCell hcell021;
					hcell021 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getMeasure(), redFont1));
					hcell021.setBorder(Rectangle.NO_BORDER);
					hcell021.setPaddingLeft(-50f);
					hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
					table3.addCell(hcell021);

					if (refMeasureDetailsInfoPdf.getDimension() != null) {
						hcell021 = new PdfPCell(new Phrase(": " + refMeasureDetailsInfoPdf.getValue() + ""
								+ refMeasureDetailsInfoPdf.getDimension(), redFont1));
						hcell021.setBorder(Rectangle.NO_BORDER);
						hcell021.setHorizontalAlignment(Element.ALIGN_CENTER);
						table3.addCell(hcell021);
					} else {
						hcell021 = new PdfPCell(new Phrase(": " + refMeasureDetailsInfoPdf.getValue(), redFont1));
						hcell021.setBorder(Rectangle.NO_BORDER);
						hcell021.setHorizontalAlignment(Element.ALIGN_CENTER);
						table3.addCell(hcell021);
					}
					if (refMeasureDetailsInfoPdf.getDimension() != null) {
						hcell021 = new PdfPCell(new Phrase(
								refMeasureDetailsInfoPdf.getRange() + "" + refMeasureDetailsInfoPdf.getDimension(),
								redFont1));
						hcell021.setBorder(Rectangle.NO_BORDER);
						hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell021.setPaddingLeft(55f);
						table3.addCell(hcell021);
					} else {
						hcell021 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getRange(), redFont1));
						hcell021.setBorder(Rectangle.NO_BORDER);
						hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell021.setPaddingLeft(55f);
						table3.addCell(hcell021);
					}
				}
				document.add(table3);
				Paragraph p01 = new Paragraph();

				p01.add("\n");
				p01.add("\n");
				document.add(p01);
				if (patientServiceDetails.getComment().isEmpty()) {
					Paragraph p27 = new Paragraph("\n Findings:", headFont);
					document.add(p27);
					Paragraph p28 = new Paragraph("N/A", redFont2);
					document.add(p28);

				} else {
					Paragraph p27 = new Paragraph("\n Findings:", headFont);

					Paragraph p28 = new Paragraph(patientServiceDetails.getComment(), redFont2);
					document.add(p27);
					document.add(p28);
				}

				Paragraph p7 = new Paragraph("*Please correlate with clinical findings if necessary discuss", redFont2);
				p7.setAlignment(Element.ALIGN_CENTER);

				p7.add("\n \n \n \n");
				document.add(p7);

				Chunk cnd = new Chunk(new VerticalPositionMark());
				Paragraph p19 = new Paragraph(patientRegistration.getPatientDetails().getConsultant(), headFont);
				p19.setAlignment(Element.ALIGN_RIGHT);
				document.add(p19);

				Paragraph p18 = new Paragraph("LAB INCHARGE", headFont);
				p18.add(cnd);

				p18.add("(" + patientRegistration.getVuserD().getDoctorDetails().getSpecilization() + ")");
				document.add(p18);

				document.close();
				pdfBytes = byteArrayOutputStream.toByteArray();
				List<ServicePdf> servicePdfs = servicePdfServiceImpl.findByRegAndMeasureName(regId,
						patientServiceDetails.getServiceName());
				if (!servicePdfs.isEmpty()) {
					servicePdf = new ServicePdf();
					servicePdf.setSid(servicePdfs.get(0).getSid());
					servicePdf.setData(pdfBytes);
					servicePdf.setFileName(servicePdfs.get(0).getFileName());
					servicePdf.setFileuri(servicePdfs.get(0).getFileuri());
					servicePdf.setRegId(servicePdfs.get(0).getRegId());
					servicePdfServiceImpl.save(servicePdf);

				} else {
					String uri = ServletUriComponentsBuilder.fromCurrentContextPath()
							.path("/v1/lab/servicePdf/viewFile/").path(servicePdfServiceImpl.getNextLabId())
							.toUriString();

					servicePdf = new ServicePdf();
					servicePdf.setSid(servicePdfServiceImpl.getNextLabId());
					servicePdf.setData(pdfBytes);
					servicePdf.setFileName(patientServiceDetails.getServiceName());
					servicePdf.setFileuri(uri);
					servicePdf.setRegId(patientServiceDetails.getRegId());
					servicePdfServiceImpl.save(servicePdf);
				}

			} catch (Exception e) {
				//Logger.error(e.getMessage());
				e.printStackTrace();
			}

		} else if (patientServiceDetails.getServiceName().equalsIgnoreCase("MALARIA PARASITE PF&PV")) {

			byte[] pdfBytes = null;
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			Document document = new Document();

			try {
				PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);

				MyFooter event = new MyFooter(principal, userServiceImpl);
				writer.setPageEvent(event);

				Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
				Font redFont3 = new Font(Font.FontFamily.TIMES_ROMAN, 17, Font.BOLD);
				Font redFont2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.ITALIC);
				Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
				Font headFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD | Font.UNDERLINE);

				document.open();

				document.add(pq);

				Paragraph p51 = new Paragraph("UDBHAVA HOSPITALS", redFont);
				p51.add("\n \n \n");
				p51.setAlignment(Element.ALIGN_CENTER);
				document.add(p51);

				Font redFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

				date = Calendar.getInstance().getTime();
				formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
				String today = formatter.format(date).toString();

PdfPTable table2 = new PdfPTable(2);
				
				PdfPCell cell1 = new PdfPCell();
				table2.setWidthPercentage(100f);	
				cell1.setBorder(Rectangle.BOTTOM|Rectangle.TOP);
				
				PdfPTable table21 = new PdfPTable(2);
				table21.setWidths(new float[] { 5f, 7.5f });
				 table21.setSpacingBefore(10);
				

				PdfPCell hcell1;
				hcell1 = new PdfPCell(
						new Phrase("Patient Name            : " +patientRegistration.getPatientDetails().getTitle() +". "+ patientRegistration.getPatientDetails().getFirstName()
								+ " " + patientRegistration.getPatientDetails().getLastName(), redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase("Age/Gender    : " + patientRegistration.getPatientDetails().getAge()
						+ "/" + patientRegistration.getPatientDetails().getGender(), redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(80f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				PdfPCell hcell4;
				hcell4 = new PdfPCell(
						new Phrase("Admission No           : " + patientRegistration.getRegId(), redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);

				if(admittedWard!=null)
				{
				hcell4 = new PdfPCell(new Phrase("Ward Name    : " + admittedWard, redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(80f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				}
				else
				{
					hcell4 = new PdfPCell(new Phrase("Ward Name    : " + " ", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(80f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);

				}



				PdfPCell hcell15;
				hcell15 = new PdfPCell(
						new Phrase("Admission Date        : " + pdfadmissionDate, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				if(roomName!=null)
				{
				hcell15 = new PdfPCell(new Phrase("Room Name   : " + roomName, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(80f);
				table21.addCell(hcell15);
				}
				else
				{
					hcell15 = new PdfPCell(new Phrase("Room Name   : " + " ", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(80f);
					table21.addCell(hcell15);
					
				}
				PdfPCell hcell16;
				hcell16 = new PdfPCell(new Phrase(
						"Bill No/UMR No      : " + patientRegistration.getPatientDetails().getUmr(), redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);

				hcell16 = new PdfPCell(new Phrase("Bill Date         : " + bill, redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(80f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);

				PdfPCell hcell17;
				hcell17 = new PdfPCell(new Phrase(
						"Consultant                 : " + patientRegistration.getPatientDetails().getConsultant(),
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);

				hcell17 = new PdfPCell(new Phrase("Report Date    : " + today, redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(80f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				cell1.setColspan(2);
				cell1.addElement(table21);
				table2.addCell(cell1);
             //  table2.addCell(cell1);
               document.add(table2);

				Paragraph p11 = new Paragraph("DEPARTMENT OF PATHOLOGY", headFont1);
				p11.setAlignment(Element.ALIGN_CENTER);
				document.add(p11);

				Paragraph p201 = new Paragraph();
				p201.add("\n");
				p201.add("\n");
				document.add(p201);

				PdfPTable table3 = new PdfPTable(2);
				table3.setWidths(new float[] { 5f, 9f });

				PdfPCell hcell01;
				hcell01 = new PdfPCell(new Phrase("INVESTIGATION", headFont1));
				hcell01.setBorder(Rectangle.NO_BORDER);
				hcell01.setPaddingLeft(-50f);
				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);

				table3.addCell(hcell01);

				hcell01 = new PdfPCell(new Phrase("RESULT", headFont1));
				hcell01.setBorder(Rectangle.NO_BORDER);
				hcell01.setHorizontalAlignment(Element.ALIGN_CENTER);
				table3.addCell(hcell01);

				for (RefMeasureDetails refMeasureDetailsInfoPdf : refMeasureDetails) {

					PdfPCell hcell021;
					hcell021 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getMeasure(), redFont1));
					hcell021.setBorder(Rectangle.NO_BORDER);
					hcell021.setPaddingLeft(-50f);
					hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
					table3.addCell(hcell021);

					if (refMeasureDetailsInfoPdf.getDimension() != null) {
						hcell021 = new PdfPCell(new Phrase(": " + refMeasureDetailsInfoPdf.getValue() + " "
								+ refMeasureDetailsInfoPdf.getDimension(), redFont1));
						hcell021.setBorder(Rectangle.NO_BORDER);
						hcell021.setHorizontalAlignment(Element.ALIGN_CENTER);
						table3.addCell(hcell021);
					} else {
						hcell021 = new PdfPCell(new Phrase(": " + refMeasureDetailsInfoPdf.getValue(), redFont1));
						hcell021.setBorder(Rectangle.NO_BORDER);
						hcell021.setHorizontalAlignment(Element.ALIGN_CENTER);
						table3.addCell(hcell021);
					}
				}
				document.add(table3);

				Paragraph p01 = new Paragraph();

				p01.add("\n");
				p01.add("\n");
				document.add(p01);

				if (patientServiceDetails.getComment().isEmpty()) {
					Paragraph p27 = new Paragraph("\n Findings:", headFont);
					document.add(p27);
					Paragraph p28 = new Paragraph("N/A", redFont2);
					document.add(p28);

				} else {
					Paragraph p27 = new Paragraph("\n Findings:", headFont);

					Paragraph p28 = new Paragraph(patientServiceDetails.getComment(), redFont2);
					document.add(p27);
					document.add(p28);
				}

				Paragraph p7 = new Paragraph("*Please correlate with clinical findings if necessary discuss", redFont2);
				p7.setAlignment(Element.ALIGN_CENTER);

				p7.add("\n");
				document.add(p7);

				Chunk cnd = new Chunk(new VerticalPositionMark());
				Paragraph p19 = new Paragraph(patientRegistration.getPatientDetails().getConsultant(), headFont);
				p19.setAlignment(Element.ALIGN_RIGHT);
				document.add(p19);

				Paragraph p18 = new Paragraph("LAB INCHARGE", headFont);
				p18.add(cnd);

				p18.add("(" + patientRegistration.getVuserD().getDoctorDetails().getSpecilization() + ")");
				document.add(p18);
				document.close();
				pdfBytes = byteArrayOutputStream.toByteArray();
				String uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/lab/servicePdf/viewFile/")
						.path(servicePdfServiceImpl.getNextLabId()).toUriString();

				servicePdf = new ServicePdf();
				servicePdf.setSid(servicePdfServiceImpl.getNextLabId());
				servicePdf.setData(pdfBytes);
				servicePdf.setFileName(patientServiceDetails.getServiceName());
				servicePdf.setFileuri(uri);
				servicePdf.setRegId(patientServiceDetails.getRegId());
				servicePdfServiceImpl.save(servicePdf);

			} catch (Exception e) {
				Logger.error(e.getMessage());
			}
		}
		else if (patientServiceDetails.getServiceName().equalsIgnoreCase("PROTHROMBIN TIME")) {

			byte[] pdfBytes = null;
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			Document document = new Document();

			try {
				PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);
				MyFooter event = new MyFooter(principal, userServiceImpl);
				writer.setPageEvent(event);

				Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
				Font redFont3 = new Font(Font.FontFamily.TIMES_ROMAN, 17, Font.BOLD);
				Font redFont2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.ITALIC);
				Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
				Font headFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD | Font.UNDERLINE);
				Font headFont2 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD | Font.UNDERLINE);

				document.open();

				document.add(pq);
				Paragraph p51 = new Paragraph("UDBHAVA HOSPITALS", redFont);
				p51.add("\n \n \n");
				p51.setAlignment(Element.ALIGN_CENTER);
				document.add(p51);
				Font redFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

				date = Calendar.getInstance().getTime();
				formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
				String today = formatter.format(date).toString();

PdfPTable table2 = new PdfPTable(2);
				
				PdfPCell cell1 = new PdfPCell();
				table2.setWidthPercentage(100f);	
				cell1.setBorder(Rectangle.BOTTOM|Rectangle.TOP);
				
				PdfPTable table21 = new PdfPTable(2);
				table21.setWidths(new float[] { 5f, 7.5f });
				 table21.setSpacingBefore(10);
				

				PdfPCell hcell1;
				hcell1 = new PdfPCell(
						new Phrase("Patient Name            : " +patientRegistration.getPatientDetails().getTitle() +". "+ patientRegistration.getPatientDetails().getFirstName()
								+ " " + patientRegistration.getPatientDetails().getLastName(), redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase("Age/Gender    : " + patientRegistration.getPatientDetails().getAge()
						+ "/" + patientRegistration.getPatientDetails().getGender(), redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(80f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				PdfPCell hcell4;
				hcell4 = new PdfPCell(
						new Phrase("Admission No           : " + patientRegistration.getRegId(), redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);

				if(admittedWard!=null)
				{
				hcell4 = new PdfPCell(new Phrase("Ward Name    : " + admittedWard, redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(80f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				}
				else
				{
					hcell4 = new PdfPCell(new Phrase("Ward Name    : " + " ", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(80f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);

				}



				PdfPCell hcell15;
				hcell15 = new PdfPCell(
						new Phrase("Admission Date        : " + pdfadmissionDate, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				if(roomName!=null)
				{
				hcell15 = new PdfPCell(new Phrase("Room Name   : " + roomName, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(80f);
				table21.addCell(hcell15);
				}
				else
				{
					hcell15 = new PdfPCell(new Phrase("Room Name   : " + " ", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(80f);
					table21.addCell(hcell15);
					
				}
				PdfPCell hcell16;
				hcell16 = new PdfPCell(new Phrase(
						"Bill No/UMR No      : " + patientRegistration.getPatientDetails().getUmr(), redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);

				hcell16 = new PdfPCell(new Phrase("Bill Date         : " + bill, redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(80f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);

				PdfPCell hcell17;
				hcell17 = new PdfPCell(new Phrase(
						"Consultant                 : " + patientRegistration.getPatientDetails().getConsultant(),
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);

				hcell17 = new PdfPCell(new Phrase("Report Date    : " + today, redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(80f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				cell1.setColspan(2);
				cell1.addElement(table21);
				table2.addCell(cell1);
             //  table2.addCell(cell1);
               document.add(table2);

				Paragraph p11 = new Paragraph("DEPARTMENT OF BIOCHEMISTRY", headFont2);
				p11.setAlignment(Element.ALIGN_CENTER);
				document.add(p11);

				Paragraph p101 = new Paragraph("\n", headFont);
				p101.setAlignment(Element.ALIGN_CENTER);
				document.add(p101);

				PdfPTable table3 = new PdfPTable(3);
				table3.setWidths(new float[] { 9f, 6f, 9f });

				PdfPCell hcell63;
				hcell63 = new PdfPCell(new Phrase("", headFont1));
				hcell63.setBorder(Rectangle.NO_BORDER);
				hcell63.setPaddingLeft(-50f);
				hcell63.setHorizontalAlignment(Element.ALIGN_LEFT);

				table3.addCell(hcell63);

				hcell63 = new PdfPCell(new Phrase("PROTHROMBIN TIME", headFont1));
				hcell63.setBorder(Rectangle.NO_BORDER);
				hcell63.setPaddingTop(20f);
				hcell63.setPaddingLeft(-20f);
				hcell63.setHorizontalAlignment(Element.ALIGN_LEFT);
				table3.addCell(hcell63);

				hcell63 = new PdfPCell(new Phrase("", headFont));
				hcell63.setBorder(Rectangle.NO_BORDER);
				hcell63.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table3.addCell(hcell63);

				PdfPCell hcell01;
				hcell01 = new PdfPCell(new Phrase("\n INVESTIGATION", headFont1));
				hcell01.setBorder(Rectangle.NO_BORDER);
				hcell01.setPaddingLeft(-50f);
				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);

				table3.addCell(hcell01);

				hcell01 = new PdfPCell(new Phrase("\n RESULT", headFont1));
				hcell01.setBorder(Rectangle.NO_BORDER);

				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell01.setPaddingLeft(20f);
				table3.addCell(hcell01);

				hcell01 = new PdfPCell(new Phrase("\n NORMAL RANGE", headFont1));
				hcell01.setBorder(Rectangle.NO_BORDER);
				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell01.setPaddingLeft(20f);
				// hcell01.setPaddingLeft(65f);
				table3.addCell(hcell01);

				for (RefMeasureDetails refMeasureDetailsInfoPdf : refMeasureDetails) {

					PdfPCell hcell021;
					hcell021 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getMeasure(), redFont1));
					hcell021.setBorder(Rectangle.NO_BORDER);
					hcell021.setPaddingLeft(-50f);
					hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
					table3.addCell(hcell021);

					if (refMeasureDetailsInfoPdf.getDimension() != null) {
						hcell021 = new PdfPCell(new Phrase(": " + refMeasureDetailsInfoPdf.getValue() + " "
								+ refMeasureDetailsInfoPdf.getDimension(), redFont1));
						hcell021.setBorder(Rectangle.NO_BORDER);
						hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell021.setPaddingLeft(20f);
						table3.addCell(hcell021);
					} else {
						hcell021 = new PdfPCell(new Phrase(": " + refMeasureDetailsInfoPdf.getValue(), redFont1));
						hcell021.setBorder(Rectangle.NO_BORDER);
						hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell021.setPaddingLeft(20f);
						table3.addCell(hcell021);
					}

					if (refMeasureDetailsInfoPdf.getDimension() != null) {
						hcell021 = new PdfPCell(new Phrase(
								refMeasureDetailsInfoPdf.getRange() ,
								redFont1));
						hcell021.setBorder(Rectangle.NO_BORDER);
						hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell021.setPaddingLeft(20f);
						table3.addCell(hcell021);

					} else {
						hcell021 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getRange(), redFont1));
						hcell021.setBorder(Rectangle.NO_BORDER);
						hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell021.setPaddingLeft(20f);
						table3.addCell(hcell021);

					}
				}
				document.add(table3);

				Paragraph p01 = new Paragraph();

				p01.add("\n");
				document.add(p01);

				if (patientServiceDetails.getComment().isEmpty()) {
					Paragraph p27 = new Paragraph("\n Findings:", headFont);
					document.add(p27);
					Paragraph p28 = new Paragraph("N/A", redFont2);
					document.add(p28);

				} else {
					Paragraph p27 = new Paragraph("\n Findings:", headFont);

					Paragraph p28 = new Paragraph(patientServiceDetails.getComment(), redFont2);
					document.add(p27);
					document.add(p28);
				}

				Paragraph p7 = new Paragraph("*Please correlate with clinical findings if necessary discuss", redFont2);
				p7.setAlignment(Element.ALIGN_CENTER);

				// FixText("Add Your Text",400,700,writer,14);
				document.add(p7);

				Chunk cnd = new Chunk(new VerticalPositionMark());
				Paragraph p19 = new Paragraph(patientRegistration.getPatientDetails().getConsultant(), headFont);
				p19.setAlignment(Element.ALIGN_RIGHT);
				document.add(p19);

				Paragraph p18 = new Paragraph("LAB INCHARGE", headFont);
				p18.add(cnd);

				p18.add("(" + patientRegistration.getVuserD().getDoctorDetails().getSpecilization() + ")");
				document.add(p18);

				// ************************

				document.close();

				pdfBytes = byteArrayOutputStream.toByteArray();
				List<ServicePdf> servicePdfs = servicePdfServiceImpl.findByRegAndMeasureName(regId,
						patientServiceDetails.getServiceName());
				if (!servicePdfs.isEmpty()) {
					servicePdf = new ServicePdf();
					servicePdf.setSid(servicePdfs.get(0).getSid());
					servicePdf.setData(pdfBytes);
					servicePdf.setFileName(servicePdfs.get(0).getFileName());
					servicePdf.setFileuri(servicePdfs.get(0).getFileuri());
					servicePdf.setRegId(servicePdfs.get(0).getRegId());
					servicePdfServiceImpl.save(servicePdf);

				} else {
					String uri = ServletUriComponentsBuilder.fromCurrentContextPath()
							.path("/v1/lab/servicePdf/viewFile/").path(servicePdfServiceImpl.getNextLabId())
							.toUriString();

					servicePdf = new ServicePdf();
					servicePdf.setSid(servicePdfServiceImpl.getNextLabId());
					servicePdf.setData(pdfBytes);
					servicePdf.setFileName(patientServiceDetails.getServiceName());
					servicePdf.setFileuri(uri);
					servicePdf.setRegId(patientServiceDetails.getRegId());
					servicePdfServiceImpl.save(servicePdf);
				}

			} catch (Exception e) {
				//Logger.error(e.getMessage());
				e.printStackTrace();
			}
		}
		else if (patientServiceDetails.getServiceName().equalsIgnoreCase("SERUM BILIRUBIN")) {

			byte[] pdfBytes = null;
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			Document document = new Document();

			try {
				PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);

				MyFooter event = new MyFooter(principal, userServiceImpl);
				writer.setPageEvent(event);

				Font font2 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD | Font.UNDERLINE);

				Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
				Font redFont3 = new Font(Font.FontFamily.TIMES_ROMAN, 17, Font.BOLD);
				Font redFont2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.ITALIC);
				Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
				Font headFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD | Font.UNDERLINE);

				// Display a date in day, month, year format
				date = Calendar.getInstance().getTime();
				formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
				String today = formatter.format(date).toString();

				Font redFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

				document.open();

				document.add(pq);

				Paragraph p51 = new Paragraph("UDBHAVA HOSPITALS", redFont);
				p51.add("\n \n \n");
				p51.setAlignment(Element.ALIGN_CENTER);
				document.add(p51);

				PdfPTable table2 = new PdfPTable(2);
				
				PdfPCell cell1 = new PdfPCell();
				table2.setWidthPercentage(100f);	
				cell1.setBorder(Rectangle.BOTTOM|Rectangle.TOP);
				
				PdfPTable table21 = new PdfPTable(6);
				table21.setWidths(new float[] { 5f,1f,5f,5f,1f,6.5f});
				 table21.setSpacingBefore(10);
				

				PdfPCell hcell1;
				hcell1 = new PdfPCell(
						new Phrase("Patient Name", redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(
						new Phrase(":", redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(
						new Phrase(patientRegistration.getPatientDetails().getTitle() +". "+ patientRegistration.getPatientDetails().getFirstName()
								+ " " + patientRegistration.getPatientDetails().getLastName(), redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase("Age/Gender", redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase(":", redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getAge()
						+ "/" + patientRegistration.getPatientDetails().getGender(), redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				
				
				PdfPCell hcell4;
				hcell4 = new PdfPCell(
						new Phrase("Admission No", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(
						new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(
						new Phrase(patientRegistration.getRegId(), redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				


				if(admittedWard!=null)
				{
				hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(admittedWard, redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				}
				else
				{
					hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					
					hcell4 = new PdfPCell(new Phrase(":", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					hcell4 = new PdfPCell(new Phrase("", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);

				}



				PdfPCell hcell15;
				hcell15 = new PdfPCell(
						new Phrase("Admission Date", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				hcell15 = new PdfPCell(
						new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				hcell15 = new PdfPCell(
						new Phrase(pdfadmissionDate, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				
				if(roomName!=null)
				{
				hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(roomName, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				}
				else
				{
					hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase(":", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase("", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
				}
				PdfPCell hcell16;
				hcell16 = new PdfPCell(new Phrase(
						"Bill No/UMR No", redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(
						":", redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getUmr(), redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);

				hcell16 = new PdfPCell(new Phrase("Bill Date", redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(":", redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(bill, redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);

				PdfPCell hcell17;
				hcell17 = new PdfPCell(new Phrase(
						"Consultant",
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(
						":",
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(
						patientRegistration.getPatientDetails().getConsultant(),
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);

				hcell17 = new PdfPCell(new Phrase("Report Date", redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(":", redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(today, redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				cell1.setColspan(2);
				cell1.addElement(table21);
				table2.addCell(cell1);
             //  table2.addCell(cell1);
               document.add(table2);

				Paragraph p11 = new Paragraph("DEPARTMENT OF BIOCHEMISTRY", font2);
				p11.setAlignment(Element.ALIGN_CENTER);

				document.add(p11);

				Paragraph p110 = new Paragraph("\n \n", redFont1);
				p110.setAlignment(Element.ALIGN_CENTER);

				document.add(p110);

				PdfPTable table3 = new PdfPTable(3);
				table3.setWidths(new float[] { 5f, 9f, 7f });

				PdfPCell hcell01;
				hcell01 = new PdfPCell(new Phrase("INVESTIGATION", headFont1));
				hcell01.setBorder(Rectangle.NO_BORDER);
				hcell01.setPaddingLeft(-50f);
				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);

				table3.addCell(hcell01);

				hcell01 = new PdfPCell(new Phrase("RESULT", headFont1));
				hcell01.setBorder(Rectangle.NO_BORDER);

				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell01.setPaddingLeft(20f);
				table3.addCell(hcell01);

				hcell01 = new PdfPCell(new Phrase("NORMAL RANGE", headFont1));
				hcell01.setBorder(Rectangle.NO_BORDER);

				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
				table3.addCell(hcell01);

				for (RefMeasureDetails refMeasureDetailsInfoPdf : refMeasureDetails) {

					PdfPCell hcell021;
					hcell021 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getMeasure(), redFont1));
					hcell021.setBorder(Rectangle.NO_BORDER);
					hcell021.setPaddingLeft(-50f);
					hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
					table3.addCell(hcell021);

					if (refMeasureDetailsInfoPdf.getDimension() != null) {
						hcell021 = new PdfPCell(new Phrase(": " + refMeasureDetailsInfoPdf.getValue() + " "
								+ refMeasureDetailsInfoPdf.getDimension(), redFont1));
						hcell021.setBorder(Rectangle.NO_BORDER);
						hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell021.setPaddingLeft(20f);
						table3.addCell(hcell021);
					} else {
						hcell021 = new PdfPCell(new Phrase(": " + refMeasureDetailsInfoPdf.getValue(), redFont1));
						hcell021.setBorder(Rectangle.NO_BORDER);
						hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell021.setPaddingLeft(20f);
						table3.addCell(hcell021);

					}
					if (refMeasureDetailsInfoPdf.getDimension() != null) {
						hcell021 = new PdfPCell(new Phrase(
								refMeasureDetailsInfoPdf.getRange() + " " + refMeasureDetailsInfoPdf.getDimension(),
								redFont1));
						hcell021.setBorder(Rectangle.NO_BORDER);
						// hcell4.setPaddingRight(5f);
						hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell021.setPaddingLeft(10f);

						table3.addCell(hcell021);
					} else {
						hcell021 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getRange(), redFont1));
						hcell021.setBorder(Rectangle.NO_BORDER);
						// hcell4.setPaddingRight(5f);
						hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell021.setPaddingLeft(10f);

						table3.addCell(hcell021);

					}
				}
				document.add(table3);

				Paragraph p01 = new Paragraph();
				p01.add("\n");
				p01.add("\n");
				document.add(p01);

				if (patientServiceDetails.getComment().isEmpty()) {
					Paragraph p27 = new Paragraph("\n Findings:", headFont);
					document.add(p27);
					Paragraph p28 = new Paragraph("N/A", redFont2);
					document.add(p28);

				} else {
					Paragraph p27 = new Paragraph("\n Findings:", headFont);

					Paragraph p28 = new Paragraph(patientServiceDetails.getComment(), redFont2);
					document.add(p27);
					document.add(p28);
				}

				Paragraph p7 = new Paragraph("*Please correlate with clinical findings if necessary discuss", redFont2);
				p7.setAlignment(Element.ALIGN_CENTER);

				p7.add("\n");
				p7.add("\n");
				document.add(p7);

				Chunk cnd = new Chunk(new VerticalPositionMark());
				Paragraph p19 = new Paragraph(patientRegistration.getPatientDetails().getConsultant(), headFont);
				p19.setAlignment(Element.ALIGN_RIGHT);
				document.add(p19);

				Paragraph p18 = new Paragraph("LAB INCHARGE", headFont);
				p18.add(cnd);

				p18.add("(" + patientRegistration.getVuserD().getDoctorDetails().getSpecilization() + ")");
				document.add(p18);

				// ************************

				document.close();

				pdfBytes = byteArrayOutputStream.toByteArray();
				List<ServicePdf> servicePdfs = servicePdfServiceImpl.findByRegAndMeasureName(regId,
						patientServiceDetails.getServiceName());
				if (!servicePdfs.isEmpty()) {
					servicePdf = new ServicePdf();
					servicePdf.setSid(servicePdfs.get(0).getSid());
					servicePdf.setData(pdfBytes);
					servicePdf.setFileName(servicePdfs.get(0).getFileName());
					servicePdf.setFileuri(servicePdfs.get(0).getFileuri());
					servicePdf.setRegId(servicePdfs.get(0).getRegId());
					servicePdfServiceImpl.save(servicePdf);

				} else {
					String uri = ServletUriComponentsBuilder.fromCurrentContextPath()
							.path("/v1/lab/servicePdf/viewFile/").path(servicePdfServiceImpl.getNextLabId())
							.toUriString();

					servicePdf = new ServicePdf();
					servicePdf.setSid(servicePdfServiceImpl.getNextLabId());
					servicePdf.setData(pdfBytes);
					servicePdf.setFileName(patientServiceDetails.getServiceName());
					servicePdf.setFileuri(uri);
					servicePdf.setRegId(patientServiceDetails.getRegId());
					servicePdfServiceImpl.save(servicePdf);
				}

			} catch (Exception e) {
				Logger.error(e.getMessage());
			}
		} 
		else if (patientServiceDetails.getServiceName().equalsIgnoreCase("DENGUE SEROLOGY")) {

				byte[] pdfBytes = null;
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

				Document document = new Document();

				try {
					PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);

					MyFooter event = new MyFooter(principal,userServiceImpl);
					writer.setPageEvent(event);


					Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
					Font blueFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
					Font blueFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.UNDERLINE|Font.BOLD);
					Font redFont3 = new Font(Font.FontFamily.TIMES_ROMAN, 17, Font.BOLD);
					Font redFont2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.ITALIC);
					Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);

					document.open();

				

					document.add(pq);

					Paragraph p51 = new Paragraph("UDBHAVA HOSPITALS", redFont);
					p51.add("\n \n \n");
					p51.setAlignment(Element.ALIGN_CENTER);
					document.add(p51);
					
					Font redFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

					date = Calendar.getInstance().getTime();
					formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
					String today = formatter.format(date).toString();

					
					PdfPTable table2 = new PdfPTable(2);
					
					PdfPCell cell1 = new PdfPCell();
					table2.setWidthPercentage(100f);	
					cell1.setBorder(Rectangle.BOTTOM|Rectangle.TOP);
					
					PdfPTable table21 = new PdfPTable(6);
					table21.setWidths(new float[] { 5f,1f,5f,5f,1f,6.5f});
					 table21.setSpacingBefore(10);
					

					PdfPCell hcell1;
					hcell1 = new PdfPCell(
							new Phrase("Patient Name", redFont1));

					hcell1.setBorder(Rectangle.NO_BORDER);
					// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell1.setPaddingLeft(-50f);
					table21.addCell(hcell1);

					
					hcell1 = new PdfPCell(
							new Phrase(":", redFont1));

					hcell1.setBorder(Rectangle.NO_BORDER);
					// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell1.setPaddingLeft(-50f);
					table21.addCell(hcell1);

					
					hcell1 = new PdfPCell(
							new Phrase(patientRegistration.getPatientDetails().getTitle() +". "+ patientRegistration.getPatientDetails().getFirstName()
									+ " " + patientRegistration.getPatientDetails().getLastName(), redFont1));

					hcell1.setBorder(Rectangle.NO_BORDER);
					// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell1.setPaddingLeft(-50f);
					table21.addCell(hcell1);

					hcell1 = new PdfPCell(new Phrase("Age/Gender", redFont1));
					hcell1.setBorder(Rectangle.NO_BORDER);
					hcell1.setPaddingLeft(10f);
					hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell1);

					hcell1 = new PdfPCell(new Phrase(":", redFont1));
					hcell1.setBorder(Rectangle.NO_BORDER);
					hcell1.setPaddingLeft(10f);
					hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell1);

					
					hcell1 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getAge()
							+ "/" + patientRegistration.getPatientDetails().getGender(), redFont1));
					hcell1.setBorder(Rectangle.NO_BORDER);
					hcell1.setPaddingLeft(10f);
					hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell1);

					
					
					PdfPCell hcell4;
					hcell4 = new PdfPCell(
							new Phrase("Admission No", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(-50f);
					table21.addCell(hcell4);
					
					hcell4 = new PdfPCell(
							new Phrase(":", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(-50f);
					table21.addCell(hcell4);
					
					hcell4 = new PdfPCell(
							new Phrase(patientRegistration.getRegId(), redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(-50f);
					table21.addCell(hcell4);
					
					if(admittedWard!=null)
					{
					hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					hcell4 = new PdfPCell(new Phrase(":", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					hcell4 = new PdfPCell(new Phrase(admittedWard, redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					}
					else
					{
						hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
						hcell4.setBorder(Rectangle.NO_BORDER);
						hcell4.setPaddingLeft(10f);

						hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
						table21.addCell(hcell4);
						
						
						hcell4 = new PdfPCell(new Phrase(":", redFont1));
						hcell4.setBorder(Rectangle.NO_BORDER);
						hcell4.setPaddingLeft(10f);

						hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
						table21.addCell(hcell4);
						
						hcell4 = new PdfPCell(new Phrase("", redFont1));
						hcell4.setBorder(Rectangle.NO_BORDER);
						hcell4.setPaddingLeft(10f);

						hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
						table21.addCell(hcell4);

					}

					PdfPCell hcell15;
					hcell15 = new PdfPCell(
							new Phrase("Admission Date", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setPaddingLeft(-50f);
					table21.addCell(hcell15);

					hcell15 = new PdfPCell(
							new Phrase(":", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setPaddingLeft(-50f);
					table21.addCell(hcell15);

					hcell15 = new PdfPCell(
							new Phrase(pdfadmissionDate, redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setPaddingLeft(-50f);
					table21.addCell(hcell15);
					
					if(roomName!=null)
					{
					hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase(":", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase(roomName, redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					}
					else
					{
						hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
						hcell15.setBorder(Rectangle.NO_BORDER);
						hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell15.setPaddingLeft(10f);
						table21.addCell(hcell15);
						
						hcell15 = new PdfPCell(new Phrase(":", redFont1));
						hcell15.setBorder(Rectangle.NO_BORDER);
						hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell15.setPaddingLeft(10f);
						table21.addCell(hcell15);
						
						hcell15 = new PdfPCell(new Phrase("", redFont1));
						hcell15.setBorder(Rectangle.NO_BORDER);
						hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell15.setPaddingLeft(10f);
						table21.addCell(hcell15);
						
					}
					PdfPCell hcell16;
					hcell16 = new PdfPCell(new Phrase(
							"Bill No/UMR No", redFont1));

					hcell16.setBorder(Rectangle.NO_BORDER);
					hcell16.setPaddingLeft(-50f);
					table21.addCell(hcell16);
					
					hcell16 = new PdfPCell(new Phrase(
							":", redFont1));

					hcell16.setBorder(Rectangle.NO_BORDER);
					hcell16.setPaddingLeft(-50f);
					table21.addCell(hcell16);
					
					hcell16 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getUmr(), redFont1));

					hcell16.setBorder(Rectangle.NO_BORDER);
					hcell16.setPaddingLeft(-50f);
					table21.addCell(hcell16);

					hcell16 = new PdfPCell(new Phrase("Bill Date", redFont1));
					hcell16.setBorder(Rectangle.NO_BORDER);
					hcell16.setPaddingLeft(10f);
					hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell16);
					
					hcell16 = new PdfPCell(new Phrase(":", redFont1));
					hcell16.setBorder(Rectangle.NO_BORDER);
					hcell16.setPaddingLeft(10f);
					hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell16);
					
					hcell16 = new PdfPCell(new Phrase(bill, redFont1));
					hcell16.setBorder(Rectangle.NO_BORDER);
					hcell16.setPaddingLeft(10f);
					hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell16);

					PdfPCell hcell17;
					hcell17 = new PdfPCell(new Phrase(
							"Consultant",
							redFont1));

					hcell17.setBorder(Rectangle.NO_BORDER);
					hcell17.setPaddingLeft(-50f);
					table21.addCell(hcell17);
					
					hcell17 = new PdfPCell(new Phrase(
							":",
							redFont1));

					hcell17.setBorder(Rectangle.NO_BORDER);
					hcell17.setPaddingLeft(-50f);
					table21.addCell(hcell17);
					
					hcell17 = new PdfPCell(new Phrase(
							patientRegistration.getPatientDetails().getConsultant(),
							redFont1));

					hcell17.setBorder(Rectangle.NO_BORDER);
					hcell17.setPaddingLeft(-50f);
					table21.addCell(hcell17);

					hcell17 = new PdfPCell(new Phrase("Report Date", redFont1));
					hcell17.setBorder(Rectangle.NO_BORDER);
					hcell17.setPaddingLeft(10f);
					hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell17);
					
					hcell17 = new PdfPCell(new Phrase(":", redFont1));
					hcell17.setBorder(Rectangle.NO_BORDER);
					hcell17.setPaddingLeft(10f);
					hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell17);
					
					hcell17 = new PdfPCell(new Phrase(today, redFont1));
					hcell17.setBorder(Rectangle.NO_BORDER);
					hcell17.setPaddingLeft(10f);
					hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell17);
					
					cell1.setColspan(2);
					cell1.addElement(table21);
					table2.addCell(cell1);
	             //  table2.addCell(cell1);
	               document.add(table2);
	               
					Paragraph p11 = new Paragraph("DENGUE SEROLOGY",blueFont);
					p11.setAlignment(Element.ALIGN_CENTER);
					p11.add("\n");
					p11.add("\n");
					document.add(p11);

					PdfPTable table3 = new PdfPTable(3);
					table3.setWidths(new float[] { 5f, 5f,6f});

					PdfPCell hcell01;
					hcell01 = new PdfPCell(new Phrase("INVESTIGATION", blueFont1));
					hcell01.setBorder(Rectangle.NO_BORDER);
					// hcell01.setPaddingTop(250f);
					hcell01.setPaddingLeft(-50f);
					hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);

					table3.addCell(hcell01);

					hcell01 = new PdfPCell(new Phrase("RESULT", blueFont1));
					hcell01.setBorder(Rectangle.NO_BORDER);
					hcell01.setPaddingRight(-40f);
					// hcell01.setPaddingTop(50f);
					// hcell01.setPaddingLeft(80f);
					hcell01.setHorizontalAlignment(Element.ALIGN_CENTER);
					table3.addCell(hcell01);
					
					hcell01 = new PdfPCell(new Phrase("METHOD", blueFont1));
					hcell01.setBorder(Rectangle.NO_BORDER);
					hcell01.setPaddingRight(-40f);
					// hcell01.setPaddingTop(50f);
					// hcell01.setPaddingLeft(80f);
					hcell01.setHorizontalAlignment(Element.ALIGN_CENTER);
					table3.addCell(hcell01);

					for (RefMeasureDetails refMeasureDetailsInfoPdf : refMeasureDetails) {

						if (refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("Dengue -1gG")
								|| refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("Dengue -1gM")) {

							PdfPCell hcell021;
							hcell021 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getMeasure(), redFont1));
							hcell021.setBorder(Rectangle.NO_BORDER);
							hcell021.setPaddingLeft(-40f);
							hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
							table3.addCell(hcell021);
							
							
							if (refMeasureDetailsInfoPdf.getDimension() != null) {
								hcell021 = new PdfPCell(new Phrase(": " + refMeasureDetailsInfoPdf.getValue() + " "
										+ refMeasureDetailsInfoPdf.getDimension(), redFont1));
								hcell021.setBorder(Rectangle.NO_BORDER);
								// hcell4.setPaddingRight(5f);
								hcell021.setHorizontalAlignment(Element.ALIGN_CENTER);
								hcell021.setPaddingLeft(30f);

								table3.addCell(hcell021);
							} else {
								hcell021 = new PdfPCell(new Phrase(": " + refMeasureDetailsInfoPdf.getValue(), redFont1));
								hcell021.setBorder(Rectangle.NO_BORDER);
								// hcell4.setPaddingRight(5f);
								hcell021.setHorizontalAlignment(Element.ALIGN_CENTER);
								hcell021.setPaddingLeft(45f);

								table3.addCell(hcell021);
							}
							

							hcell021 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getMethod(), redFont1));
							hcell021.setBorder(Rectangle.NO_BORDER);
							hcell021.setPaddingLeft(72f);
							hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
							table3.addCell(hcell021);
							
						}
					}
				
					
					document.add(table3);

					Paragraph p01 = new Paragraph();
					
					p01.add("\n");
					p01.add("\n");
					document.add(p01);

					if (patientServiceDetails.getComment().isEmpty()) {
						Paragraph p27 = new Paragraph("\n Findings:", headFont);
						document.add(p27);
						Paragraph p28 = new Paragraph("N/A", redFont2);
						document.add(p28);

					} else {
						Paragraph p27 = new Paragraph("\n Findings:", headFont);

						Paragraph p28 = new Paragraph(patientServiceDetails.getComment(), redFont2);
						document.add(p27);
						document.add(p28);
					}
					Paragraph p7 = new Paragraph("*Please correlate with clinical findings if necessary discuss", redFont2);
					p7.setAlignment(Element.ALIGN_CENTER);

				
					p7.add("\n \n \n");
					p7.add("\n");
					document.add(p7);

					Chunk cnd = new Chunk(new VerticalPositionMark());
					Paragraph p19 = new Paragraph(patientRegistration.getPatientDetails().getConsultant(), headFont);
					p19.setAlignment(Element.ALIGN_RIGHT);
					document.add(p19);

					Paragraph p18 = new Paragraph("LAB INCHARGE", headFont);
					p18.add(cnd);

					p18.add("(" + patientRegistration.getVuserD().getDoctorDetails().getSpecilization() + ")");
					document.add(p18);

					// ************************

					document.close();

					pdfBytes = byteArrayOutputStream.toByteArray();
					List<ServicePdf> servicePdfs=servicePdfServiceImpl.findByRegAndMeasureName(regId,patientServiceDetails.getServiceName());
					if(!servicePdfs.isEmpty())
					{
						servicePdf = new ServicePdf();
						servicePdf.setSid(servicePdfs.get(0).getSid());
						servicePdf.setData(pdfBytes);
						servicePdf.setFileName(servicePdfs.get(0).getFileName());
						servicePdf.setFileuri(servicePdfs.get(0).getFileuri());
						servicePdf.setRegId(servicePdfs.get(0).getRegId());
						servicePdfServiceImpl.save(servicePdf);

					}
					else
					{
					String uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/lab/servicePdf/viewFile/")
							.path(servicePdfServiceImpl.getNextLabId()).toUriString();

					servicePdf = new ServicePdf();
					servicePdf.setSid(servicePdfServiceImpl.getNextLabId());
					servicePdf.setData(pdfBytes);
					servicePdf.setFileName(patientServiceDetails.getServiceName());
					servicePdf.setFileuri(uri);
					servicePdf.setRegId(patientServiceDetails.getRegId());
					servicePdfServiceImpl.save(servicePdf);
					}


				} catch (Exception e) {
					Logger.error(e.getMessage());
				}

			}

			else if (patientServiceDetails.getServiceName().equalsIgnoreCase("HBs Ag")) {

				byte[] pdfBytes = null;
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

				Document document = new Document();

				try {
					PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);

					MyFooter event = new MyFooter(principal,userServiceImpl);
					writer.setPageEvent(event);


					Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
					Font redFont3 = new Font(Font.FontFamily.TIMES_ROMAN, 17, Font.BOLD);
					Font redFont2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.ITALIC);
					Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
					
					Font headFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD|Font.UNDERLINE);
					
					Font headFont2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD|Font.UNDERLINE);

					document.open();

					document.add(pq);

					Paragraph p51 = new Paragraph("UDBHAVA HOSPITALS", redFont);
					p51.add("\n \n \n \n");
					p51.setAlignment(Element.ALIGN_CENTER);
					document.add(p51);
					
					
					Font redFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

					date = Calendar.getInstance().getTime();
					formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
					String today = formatter.format(date).toString();
					PdfPTable table2 = new PdfPTable(2);
					
					PdfPCell cell1 = new PdfPCell();
					table2.setWidthPercentage(100f);	
					cell1.setBorder(Rectangle.BOTTOM|Rectangle.TOP);
					
					PdfPTable table21 = new PdfPTable(6);
					table21.setWidths(new float[] { 5f,1f,5f,5f,1f,6.5f});
					 table21.setSpacingBefore(10);
					

					PdfPCell hcell1;
					hcell1 = new PdfPCell(
							new Phrase("Patient Name", redFont1));

					hcell1.setBorder(Rectangle.NO_BORDER);
					// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell1.setPaddingLeft(-50f);
					table21.addCell(hcell1);

					
					hcell1 = new PdfPCell(
							new Phrase(":", redFont1));

					hcell1.setBorder(Rectangle.NO_BORDER);
					// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell1.setPaddingLeft(-50f);
					table21.addCell(hcell1);

					
					hcell1 = new PdfPCell(
							new Phrase(patientRegistration.getPatientDetails().getTitle() +". "+ patientRegistration.getPatientDetails().getFirstName()
									+ " " + patientRegistration.getPatientDetails().getLastName(), redFont1));

					hcell1.setBorder(Rectangle.NO_BORDER);
					// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell1.setPaddingLeft(-50f);
					table21.addCell(hcell1);

					hcell1 = new PdfPCell(new Phrase("Age/Gender", redFont1));
					hcell1.setBorder(Rectangle.NO_BORDER);
					hcell1.setPaddingLeft(10f);
					hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell1);

					hcell1 = new PdfPCell(new Phrase(":", redFont1));
					hcell1.setBorder(Rectangle.NO_BORDER);
					hcell1.setPaddingLeft(10f);
					hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell1);

					
					hcell1 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getAge()
							+ "/" + patientRegistration.getPatientDetails().getGender(), redFont1));
					hcell1.setBorder(Rectangle.NO_BORDER);
					hcell1.setPaddingLeft(10f);
					hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell1);

					
					
					PdfPCell hcell4;
					hcell4 = new PdfPCell(
							new Phrase("Admission No", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(-50f);
					table21.addCell(hcell4);
					
					hcell4 = new PdfPCell(
							new Phrase(":", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(-50f);
					table21.addCell(hcell4);
					
					hcell4 = new PdfPCell(
							new Phrase(patientRegistration.getRegId(), redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(-50f);
					table21.addCell(hcell4);
					
					


					if(admittedWard!=null)
					{
					hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					hcell4 = new PdfPCell(new Phrase(":", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					hcell4 = new PdfPCell(new Phrase(admittedWard, redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					}
					else
					{
						hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
						hcell4.setBorder(Rectangle.NO_BORDER);
						hcell4.setPaddingLeft(10f);

						hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
						table21.addCell(hcell4);
						
						
						hcell4 = new PdfPCell(new Phrase(":", redFont1));
						hcell4.setBorder(Rectangle.NO_BORDER);
						hcell4.setPaddingLeft(10f);

						hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
						table21.addCell(hcell4);
						
						hcell4 = new PdfPCell(new Phrase("", redFont1));
						hcell4.setBorder(Rectangle.NO_BORDER);
						hcell4.setPaddingLeft(10f);

						hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
						table21.addCell(hcell4);

					}



					PdfPCell hcell15;
					hcell15 = new PdfPCell(
							new Phrase("Admission Date", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setPaddingLeft(-50f);
					table21.addCell(hcell15);

					hcell15 = new PdfPCell(
							new Phrase(":", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setPaddingLeft(-50f);
					table21.addCell(hcell15);

					hcell15 = new PdfPCell(
							new Phrase(pdfadmissionDate, redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setPaddingLeft(-50f);
					table21.addCell(hcell15);

					
					if(roomName!=null)
					{
					hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase(":", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase(roomName, redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					}
					else
					{
						hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
						hcell15.setBorder(Rectangle.NO_BORDER);
						hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell15.setPaddingLeft(10f);
						table21.addCell(hcell15);
						
						hcell15 = new PdfPCell(new Phrase(":", redFont1));
						hcell15.setBorder(Rectangle.NO_BORDER);
						hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell15.setPaddingLeft(10f);
						table21.addCell(hcell15);
						
						hcell15 = new PdfPCell(new Phrase("", redFont1));
						hcell15.setBorder(Rectangle.NO_BORDER);
						hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell15.setPaddingLeft(10f);
						table21.addCell(hcell15);
						
					}
					PdfPCell hcell16;
					hcell16 = new PdfPCell(new Phrase(
							"Bill No/UMR No", redFont1));

					hcell16.setBorder(Rectangle.NO_BORDER);
					hcell16.setPaddingLeft(-50f);
					table21.addCell(hcell16);
					
					hcell16 = new PdfPCell(new Phrase(
							":", redFont1));

					hcell16.setBorder(Rectangle.NO_BORDER);
					hcell16.setPaddingLeft(-50f);
					table21.addCell(hcell16);
					
					hcell16 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getUmr(), redFont1));

					hcell16.setBorder(Rectangle.NO_BORDER);
					hcell16.setPaddingLeft(-50f);
					table21.addCell(hcell16);

					hcell16 = new PdfPCell(new Phrase("Bill Date", redFont1));
					hcell16.setBorder(Rectangle.NO_BORDER);
					hcell16.setPaddingLeft(10f);
					hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell16);
					
					hcell16 = new PdfPCell(new Phrase(":", redFont1));
					hcell16.setBorder(Rectangle.NO_BORDER);
					hcell16.setPaddingLeft(10f);
					hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell16);
					
					hcell16 = new PdfPCell(new Phrase(bill, redFont1));
					hcell16.setBorder(Rectangle.NO_BORDER);
					hcell16.setPaddingLeft(10f);
					hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell16);

					PdfPCell hcell17;
					hcell17 = new PdfPCell(new Phrase(
							"Consultant",
							redFont1));

					hcell17.setBorder(Rectangle.NO_BORDER);
					hcell17.setPaddingLeft(-50f);
					table21.addCell(hcell17);
					
					hcell17 = new PdfPCell(new Phrase(
							":",
							redFont1));

					hcell17.setBorder(Rectangle.NO_BORDER);
					hcell17.setPaddingLeft(-50f);
					table21.addCell(hcell17);
					
					hcell17 = new PdfPCell(new Phrase(
							patientRegistration.getPatientDetails().getConsultant(),
							redFont1));

					hcell17.setBorder(Rectangle.NO_BORDER);
					hcell17.setPaddingLeft(-50f);
					table21.addCell(hcell17);

					hcell17 = new PdfPCell(new Phrase("Report Date", redFont1));
					hcell17.setBorder(Rectangle.NO_BORDER);
					hcell17.setPaddingLeft(10f);
					hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell17);
					
					hcell17 = new PdfPCell(new Phrase(":", redFont1));
					hcell17.setBorder(Rectangle.NO_BORDER);
					hcell17.setPaddingLeft(10f);
					hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell17);
					
					hcell17 = new PdfPCell(new Phrase(today, redFont1));
					hcell17.setBorder(Rectangle.NO_BORDER);
					hcell17.setPaddingLeft(10f);
					hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell17);
					
					cell1.setColspan(2);
					cell1.addElement(table21);
					table2.addCell(cell1);
	             //  table2.addCell(cell1);
	               document.add(table2);

					
					
					
					Paragraph p11 = new Paragraph("DEPARTMENT OF SEROLOGY",headFont1);
					p11.setAlignment(Element.ALIGN_CENTER);
									document.add(p11);

					PdfPTable table3 = new PdfPTable(2);
					table3.setWidths(new float[] { 5f, 6f });

					PdfPCell hcell01;
					hcell01 = new PdfPCell(new Phrase("\n INVESTIGATION", headFont2));
					hcell01.setBorder(Rectangle.NO_BORDER);
					// hcell01.setPaddingTop(250f);
					hcell01.setPaddingLeft(-50f);
					hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);

					table3.addCell(hcell01);

					hcell01 = new PdfPCell(new Phrase("\n RESULT", headFont2));
					hcell01.setBorder(Rectangle.NO_BORDER);
					hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
					 hcell01.setPaddingLeft(5f);
					table3.addCell(hcell01);

				
					for (RefMeasureDetails refMeasureDetailsInfoPdf : refMeasureDetails) {

						
						PdfPCell hcell021;
						hcell021 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getMeasure(), redFont1));
						hcell021.setBorder(Rectangle.NO_BORDER);
						hcell021.setPaddingLeft(-50f);
						hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
						table3.addCell(hcell021);

						if (refMeasureDetailsInfoPdf.getDimension() != null) {
							hcell021 = new PdfPCell(new Phrase(": " + refMeasureDetailsInfoPdf.getValue() + " "
									+ refMeasureDetailsInfoPdf.getDimension(), redFont1));
							hcell021.setBorder(Rectangle.NO_BORDER);
							// hcell4.setPaddingRight(5f);
							hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
							hcell021.setPaddingLeft(5f);

							table3.addCell(hcell021);
						} else {
							hcell021 = new PdfPCell(new Phrase(": " + refMeasureDetailsInfoPdf.getValue(), redFont1));
							hcell021.setBorder(Rectangle.NO_BORDER);
							// hcell4.setPaddingRight(5f);
							hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
							hcell021.setPaddingLeft(5f);

							table3.addCell(hcell021);
						}
						
						System.out.println(refMeasureDetailsInfoPdf.getMethod());
					
						PdfPCell hcell0211;
						hcell0211 = new PdfPCell(new Phrase("\n ("+refMeasureDetailsInfoPdf.getMethod()+")", redFont1));
						hcell0211.setBorder(Rectangle.NO_BORDER);
						hcell0211.setPaddingLeft(-50f);
						hcell0211.setHorizontalAlignment(Element.ALIGN_LEFT);
						table3.addCell(hcell0211);

					
						hcell0211 = new PdfPCell(new Phrase("", redFont));
						hcell0211.setBorder(Rectangle.NO_BORDER);
						hcell0211.setPaddingLeft(-50f);
						hcell0211.setHorizontalAlignment(Element.ALIGN_LEFT);
						table3.addCell(hcell0211);

					}
					
						

					document.add(table3);

					Paragraph p01 = new Paragraph();
					p01.add("\n");
					p01.add("\n");
					document.add(p01);

					if (patientServiceDetails.getComment().isEmpty()) {
						Paragraph p27 = new Paragraph("\n Findings:", headFont);
						document.add(p27);
						Paragraph p28 = new Paragraph("N/A", redFont2);
						document.add(p28);

					} else {
						Paragraph p27 = new Paragraph("\n Findings:", headFont);

						Paragraph p28 = new Paragraph(patientServiceDetails.getComment(), redFont2);
						document.add(p27);
						document.add(p28);
					}
					Paragraph p7 = new Paragraph("*Please correlate with clinical findings if necessary discuss", redFont2);
					p7.setAlignment(Element.ALIGN_CENTER);

				
					p7.add("\n");
					p7.add("\n");
					document.add(p7);

					Chunk cnd = new Chunk(new VerticalPositionMark());
					Paragraph p19 = new Paragraph(patientRegistration.getPatientDetails().getConsultant(), headFont);
					p19.setAlignment(Element.ALIGN_RIGHT);
					document.add(p19);

					Paragraph p18 = new Paragraph("LAB INCHARGE", headFont);
					p18.add(cnd);

					p18.add("(" + patientRegistration.getVuserD().getDoctorDetails().getSpecilization() + ")");
					document.add(p18);

					// ************************

					document.close();

					pdfBytes = byteArrayOutputStream.toByteArray();
					List<ServicePdf> servicePdfs=servicePdfServiceImpl.findByRegAndMeasureName(regId,patientServiceDetails.getServiceName());
					if(!servicePdfs.isEmpty())
					{
						servicePdf = new ServicePdf();
						servicePdf.setSid(servicePdfs.get(0).getSid());
						servicePdf.setData(pdfBytes);
						servicePdf.setFileName(servicePdfs.get(0).getFileName());
						servicePdf.setFileuri(servicePdfs.get(0).getFileuri());
						servicePdf.setRegId(servicePdfs.get(0).getRegId());
						servicePdfServiceImpl.save(servicePdf);

					}
					else
					{
					String uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/lab/servicePdf/viewFile/")
							.path(servicePdfServiceImpl.getNextLabId()).toUriString();

					servicePdf = new ServicePdf();
					servicePdf.setSid(servicePdfServiceImpl.getNextLabId());
					servicePdf.setData(pdfBytes);
					servicePdf.setFileName(patientServiceDetails.getServiceName());
					servicePdf.setFileuri(uri);
					servicePdf.setRegId(patientServiceDetails.getRegId());
					servicePdfServiceImpl.save(servicePdf);
					}


				} catch (Exception e) {
					Logger.error(e.getMessage());
				}

			}
		
		
		// new
			// pdfs---------------------------------------------------------------------------

		return servicePdf;
	}


	public ServicePdf saveInfo1(PatientServiceDetails patientServiceDetails,Principal principal)
	{
		String regId = null;

		List<LabServiceRange> labServiceRange = null;
		
		PatientRegistration patientRegistration = patientRegistrationServiceImpl
				.findByRegId(patientServiceDetails.getRegId());
		regId = patientServiceDetails.getRegId();
		patientServiceDetails.setPatientService(patientRegistration);
		System.out.println(patientServiceDetails.getServiceName());
		System.out.println(patientServiceDetails.getRegId());
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
		LabServices labServices = labServicesServiceImpl.findPriceByType(patientServiceDetails.getServiceName(),
				patientRegistration.getpType(),
				roomType);
		if (labServices == null) {
			System.out.println("ITS NULL");
		} else {
			System.out.println("ITS NOT NULL " + labServices.getServiceId());
		}

		patientServiceDetails.setPatientLabService(labServices);
		patientServiceDetails.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));

		// createdBy (Security)
		User userSecurity = userServiceImpl.findByUserName(principal.getName());
		String createdBy = userSecurity.getFirstName() + " " + userSecurity.getLastName();
		patientServiceDetails.setCreatedBy(createdBy);
		
		//for admission date
				String admisionDate =patientRegistration.getRegDate().toString();
			Timestamp timestamp = Timestamp.valueOf(admisionDate);
			DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa ");

			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(timestamp.getTime());

			String pdfadmissionDate = dateFormat.format(calendar.getTime());

		// for pdf
		List<LaboratoryRegistration> laboratoryRegistrationsInfo = null;
		List<RefMeasureDetails> refMeasureDetails = patientServiceDetails.getRefMeasureDetails();

		if (!refMeasureDetails.isEmpty()) {
			for (RefMeasureDetails refMeasureDetailsInfo : refMeasureDetails) {
				patientServiceDetails.setPatServiceId(patientServiceDetailsServiceImpl.getNextId());
				List<LabServices> li = new ArrayList<>();
				li.add(labServices);
				String newAge = patientRegistration.getPatientDetails().getAge();
				String ageType = null;
				int age = 0;
				LocalDate today = LocalDate.now();
				LocalDate birthday = patientRegistration.getPatientDetails().getDob().toLocalDateTime().toLocalDate();
				Period p = Period.between(birthday, today);
				System.out.println("Days" + p.getDays() + "months" + p.getMonths() + "years" + p.getYears());
				// System.out.println("service
				// id"+labServices.get(0).getServiceId());

				// String
				// newAge=patientRegistration.getPatientDetails().getAge();
				String gender = null;

				if (patientServiceDetails.getServiceName().equalsIgnoreCase("LFT - LIVER FUNCTON TESTS")
						|| patientServiceDetails.getServiceName().equalsIgnoreCase("SERUM BILIRUBIN")) {

					if (p.getDays() >= 0 && p.getMonths() == 0 && p.getYears() == 0) {

						age = p.getDays();
						ageType = "days";
						gender = "CHILDREN";
					} else if (p.getDays() >= 0 && p.getMonths() > 0 && p.getYears() >= 0) {

						age = p.getMonths();
						ageType = "months";
						gender = patientRegistration.getPatientDetails().getGender();
					} 

					laboratoryRegistrationsInfo = patientRegistration.getLaboratoryRegistration();

					labServiceRange = labServicesServiceImpl.findNewMeasures(li, age, gender, ageType);

				} else if (patientServiceDetails.getServiceName().equalsIgnoreCase("COMPLETE BLOOD PICTURE")) {
					if (p.getDays() >= 0 && p.getMonths() == 0 && p.getYears() == 0) {

						age = p.getDays();
						ageType = "days";
						gender = "CHILDREN";
					} else if (p.getDays() >= 0 && p.getMonths() >= 0 && p.getYears() < 2) {

						age = p.getMonths();
						ageType = "months";
						gender = "CHILDREN";
					} else if (p.getDays() >= 0 && p.getMonths() >= 0 && p.getYears() > 2 && p.getYears() < 13) {

						age = p.getYears();
						ageType = "years";
						gender = "CHILDREN";
					} else if (p.getDays() >= 0 && p.getMonths() >= 0 && p.getYears() > 13) {
						age = p.getYears();
						ageType = "years";
						gender = patientRegistration.getPatientDetails().getGender();

					}

					laboratoryRegistrationsInfo = patientRegistration.getLaboratoryRegistration();

					labServiceRange = labServicesServiceImpl.findNewMeasures(li, age, gender, ageType);

				} else {

					newAge = patientRegistration.getPatientDetails().getAge();

					float age1 = p.getYears();
					int labAge = 0;
					gender = null;

					if (age1 <= 12) {
						labAge = (int) age1;
						gender = "CHILDREN";
					} else

					{
						labAge = (int) age1;
						gender = patientRegistration.getPatientDetails().getGender();

					}

					laboratoryRegistrationsInfo = patientRegistration.getLaboratoryRegistration();

					labServiceRange = labServicesServiceImpl.findMeasures(li, labAge, gender);

				}

				for (LabServiceRange labServiceRangeInfo : labServiceRange) {

					if (labServiceRangeInfo.getMeasureName().equals(refMeasureDetailsInfo.getMeasure())) {
						if(labServiceRangeInfo.getMinRange()!=null && labServiceRangeInfo.getMaxRange()!=null)
						{
						refMeasureDetailsInfo.setDimension(labServiceRangeInfo.getParameter());
						refMeasureDetailsInfo.setGender(labServiceRangeInfo.getGender());
						refMeasureDetailsInfo.setRange("[" + labServiceRangeInfo.getMinRange() + "-"
								+ labServiceRangeInfo.getMaxRange() + "]");
						refMeasureDetailsInfo.setMethod(labServiceRangeInfo.getMethod());
						patientServiceDetails.setPatientLabServiceRange(labServiceRangeInfo);
						patientServiceDetails.setActualValue(refMeasureDetailsInfo.getValue());
					}
						else if(labServiceRangeInfo.getMinRange()!=null ||labServiceRangeInfo.getMaxRange()!=null )
						{
							refMeasureDetailsInfo.setDimension(labServiceRangeInfo.getParameter());
							refMeasureDetailsInfo.setGender(labServiceRangeInfo.getGender());
							if(labServiceRangeInfo.getMinRange()==null)
							{
							refMeasureDetailsInfo.setRange("["+ labServiceRangeInfo.getMaxRange() + "]");
							}else
							{
								refMeasureDetailsInfo.setRange("[" + labServiceRangeInfo.getMinRange()+ "]");
							}
							refMeasureDetailsInfo.setMethod(labServiceRangeInfo.getMethod());
							patientServiceDetails.setPatientLabServiceRange(labServiceRangeInfo);
							patientServiceDetails.setActualValue(refMeasureDetailsInfo.getValue());
						}
						else
						{
							refMeasureDetailsInfo.setDimension(labServiceRangeInfo.getParameter());
							refMeasureDetailsInfo.setGender(labServiceRangeInfo.getGender());
							refMeasureDetailsInfo.setRange(" ");
							refMeasureDetailsInfo.setMethod(labServiceRangeInfo.getMethod());
							patientServiceDetails.setPatientLabServiceRange(labServiceRangeInfo);
							patientServiceDetails.setActualValue(refMeasureDetailsInfo.getValue());
						}
					}
				}
				patientServiceDetailsServiceImpl.save(patientServiceDetails);

			}

		}
		Date date = new Date(laboratoryRegistrationsInfo.get(0).getEnteredDate().getTime());
		DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
		String bill = formatter.format(date).toString();

		
		String admittedWard=null;
		String roomName=null;

		List<RoomBookingDetails> roomBookingDetails=patientRegistration.getRoomBookingDetails();
		if(!roomBookingDetails.isEmpty())
		{
		for(RoomBookingDetails roomBookingDetailsInfo:roomBookingDetails){
			RoomDetails roomDetails =roomBookingDetailsInfo.getRoomDetails();
			admittedWard=roomDetails.getRoomType();
			roomName=roomDetails.getRoomName();
		}}

		// PDF
		
		Resource fileResourcee = resourceLoader.getResource("classpath:udbhava.png");

		Image img=null;
		try {
			img = Image.getInstance(fileResourcee.getFile().getAbsolutePath());
		} catch (Exception e1) {
			Logger.error(e1.getMessage());
		}
		img.scaleAbsolute(100, 100);
		Phrase pq = new Phrase(new Chunk(img, 0, -90));

		ServicePdf servicePdf = new ServicePdf();

		
		
		if (patientServiceDetails.getServiceName().equalsIgnoreCase("HCV I & II")) {

			byte[] pdfBytes = null;
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			Document document = new Document();

			try {
				PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);

				MyFooter event = new MyFooter(principal,userServiceImpl);
				writer.setPageEvent(event);


				Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
				Font redFont3 = new Font(Font.FontFamily.TIMES_ROMAN, 17, Font.BOLD);
				Font redFont2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.ITALIC);
				Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);

				document.open();

				

				document.add(pq);

				Paragraph p51 = new Paragraph("UDBHAVA HOSPITALS", redFont);
				p51.add("\n \n \n \n");
				p51.setAlignment(Element.ALIGN_CENTER);
				document.add(p51);
				

				PdfPTable table2 = new PdfPTable(2);
				table2.setWidths(new float[] { 7f, 7f });
				Font redFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

				date = Calendar.getInstance().getTime();
				formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
				String today = formatter.format(date).toString();

				PdfPCell cell1 = new PdfPCell();
				table2.setWidthPercentage(100f);	
				cell1.setBorder(Rectangle.BOTTOM|Rectangle.TOP);
				
				PdfPTable table21 = new PdfPTable(6);
				table21.setWidths(new float[] { 5f,1f,5f,5f,1f,6.5f});
				 table21.setSpacingBefore(10);
				

				PdfPCell hcell1;
				hcell1 = new PdfPCell(
						new Phrase("Patient Name", redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);
				
				hcell1 = new PdfPCell(
						new Phrase(":", redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);
				
				hcell1 = new PdfPCell(
						new Phrase(patientRegistration.getPatientDetails().getTitle() +". "+ patientRegistration.getPatientDetails().getFirstName()
								+ " " + patientRegistration.getPatientDetails().getLastName(), redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase("Age/Gender", redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase(":", redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);
				
				hcell1 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getAge()
						+ "/" + patientRegistration.getPatientDetails().getGender(), redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				PdfPCell hcell4;
				hcell4 = new PdfPCell(
						new Phrase("Admission No", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(
						new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(
						new Phrase(patientRegistration.getRegId(), redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				if(admittedWard!=null)
				{
				hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(admittedWard, redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				}
				else
				{
					hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					
					hcell4 = new PdfPCell(new Phrase(":", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					hcell4 = new PdfPCell(new Phrase("", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);

				}
				PdfPCell hcell15;
				hcell15 = new PdfPCell(
						new Phrase("Admission Date", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				hcell15 = new PdfPCell(
						new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				hcell15 = new PdfPCell(
						new Phrase(pdfadmissionDate, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);
				if(roomName!=null)
				{
				hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(roomName, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				}
				else
				{
					hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase(":", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase("", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
				}
				PdfPCell hcell16;
				hcell16 = new PdfPCell(new Phrase(
						"Bill No/UMR No", redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(
						":", redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getUmr(), redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);

				hcell16 = new PdfPCell(new Phrase("Bill Date", redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(":", redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(bill, redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);

				PdfPCell hcell17;
				hcell17 = new PdfPCell(new Phrase(
						"Consultant",
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(
						":",
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(
						patientRegistration.getPatientDetails().getConsultant(),
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);

				hcell17 = new PdfPCell(new Phrase("Report Date", redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(":", redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(today, redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				cell1.setColspan(2);
				cell1.addElement(table21);
				table2.addCell(cell1);
             //  table2.addCell(cell1);
               document.add(table2);


				Paragraph p11 = new Paragraph("SEROLOGY REPORT",headFont);
				p11.setAlignment(Element.ALIGN_CENTER);
				p11.add("\n");
				p11.add("\n");
				document.add(p11);

				PdfPTable table3 = new PdfPTable(2);
				table3.setWidths(new float[] { 5f, 6f});

				PdfPCell hcell01;
				hcell01 = new PdfPCell(new Phrase("TEST", headFont));
				hcell01.setBorder(Rectangle.NO_BORDER);
				hcell01.setPaddingLeft(-50f);
				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
				table3.addCell(hcell01);

				hcell01 = new PdfPCell(new Phrase("RESULT", headFont));
				hcell01.setBorder(Rectangle.NO_BORDER);
				hcell01.setPaddingRight(-40f);
				hcell01.setHorizontalAlignment(Element.ALIGN_CENTER);
				table3.addCell(hcell01);


				for (RefMeasureDetails refMeasureDetailsInfoPdf : refMeasureDetails) {

					if (refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("HCV")) {

						PdfPCell hcell021;
						hcell021 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getMeasure(), redFont1));
						hcell021.setBorder(Rectangle.NO_BORDER);
						hcell021.setPaddingLeft(-50f);
						hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
						table3.addCell(hcell021);

						if (refMeasureDetailsInfoPdf.getDimension() != null) {
							hcell021 = new PdfPCell(new Phrase(": " + refMeasureDetailsInfoPdf.getValue() + " "
									+ refMeasureDetailsInfoPdf.getDimension(), redFont1));
							hcell021.setBorder(Rectangle.NO_BORDER);
							hcell021.setHorizontalAlignment(Element.ALIGN_CENTER);
							hcell021.setPaddingLeft(30f);

							table3.addCell(hcell021);
						} else {
							hcell021 = new PdfPCell(new Phrase(": " + refMeasureDetailsInfoPdf.getValue(), redFont1));
							hcell021.setBorder(Rectangle.NO_BORDER);
							hcell021.setHorizontalAlignment(Element.ALIGN_CENTER);
							hcell021.setPaddingLeft(45f);
							table3.addCell(hcell021);
						}
						PdfPCell hcell0211;
						System.out.println(refMeasureDetailsInfoPdf.getMethod());
						if (refMeasureDetailsInfoPdf.getMethod() != null) {
							
							hcell0211 = new PdfPCell(
									new Phrase(" \n Method :  "+refMeasureDetailsInfoPdf.getMethod(), headFont));
							hcell0211.setBorder(Rectangle.NO_BORDER);
							hcell0211.setPaddingLeft(-50f);
							hcell0211.setHorizontalAlignment(Element.ALIGN_LEFT);
							table3.addCell(hcell0211);
							
							hcell0211 = new PdfPCell(
									new Phrase("", redFont1));
							hcell0211.setBorder(Rectangle.NO_BORDER);
							hcell0211.setPaddingLeft(-70f);
							hcell0211.setHorizontalAlignment(Element.ALIGN_LEFT);
							table3.addCell(hcell0211);
						}
						else
						{
							hcell0211 = new PdfPCell(
									new Phrase("", redFont1));
							hcell0211.setBorder(Rectangle.NO_BORDER);
							hcell0211.setPaddingLeft(-50f);
							hcell0211.setHorizontalAlignment(Element.ALIGN_LEFT);
							table3.addCell(hcell0211);

							
							hcell0211 = new PdfPCell(
									new Phrase("", redFont1));
							hcell0211.setBorder(Rectangle.NO_BORDER);
							hcell0211.setPaddingLeft(30f);
							hcell0211.setHorizontalAlignment(Element.ALIGN_LEFT);
							table3.addCell(hcell0211);

						}
					}
				}

				document.add(table3);

				Paragraph p01 = new Paragraph();
				
				p01.add("\n");
				p01.add("\n");
				document.add(p01);

				if (patientServiceDetails.getComment().isEmpty()) {
					Paragraph p27 = new Paragraph();
					p27.add("\n findings:" + "\n" + "N/A");
					document.add(p27);
				} else {
					Paragraph p27 = new Paragraph();
					p27.add("\n findings:" + "\n" + patientServiceDetails.getComment());
					document.add(p27);
				}

				Paragraph p7 = new Paragraph("*Suggested clinical correlation, if necessary kindly discuss", redFont2);
				p7.setAlignment(Element.ALIGN_CENTER);

				p7.add("\n");
				p7.add("\n");
				document.add(p7);

				Chunk cnd = new Chunk(new VerticalPositionMark());
				Paragraph p19 = new Paragraph(patientRegistration.getPatientDetails().getConsultant(), headFont);
				p19.setAlignment(Element.ALIGN_RIGHT);
				document.add(p19);

				Paragraph p18 = new Paragraph("LAB INCHARGE", headFont);
				p18.add(cnd);

				p18.add("(" + patientRegistration.getVuserD().getDoctorDetails().getSpecilization() + ")");
				document.add(p18);

				document.close();

				pdfBytes = byteArrayOutputStream.toByteArray();
				List<ServicePdf> servicePdfs=servicePdfServiceImpl.findByRegAndMeasureName(regId,patientServiceDetails.getServiceName());
				if(!servicePdfs.isEmpty())
				{
					servicePdf = new ServicePdf();
					servicePdf.setSid(servicePdfs.get(0).getSid());
					servicePdf.setData(pdfBytes);
					servicePdf.setFileName(servicePdfs.get(0).getFileName());
					servicePdf.setFileuri(servicePdfs.get(0).getFileuri());
					servicePdf.setRegId(servicePdfs.get(0).getRegId());
					servicePdfServiceImpl.save(servicePdf);

				}
				else
				{
				String uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/lab/servicePdf/viewFile/")
						.path(servicePdfServiceImpl.getNextLabId()).toUriString();

				servicePdf = new ServicePdf();
				servicePdf.setSid(servicePdfServiceImpl.getNextLabId());
				servicePdf.setData(pdfBytes);
				servicePdf.setFileName(patientServiceDetails.getServiceName());
				servicePdf.setFileuri(uri);
				servicePdf.setRegId(patientServiceDetails.getRegId());
				servicePdfServiceImpl.save(servicePdf);
				}


			} catch (Exception e) {
				Logger.error(e.getMessage());
			}

		}		
		else if (patientServiceDetails.getServiceName().equalsIgnoreCase("ESR")) {

			byte[] pdfBytes = null;
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			Document document = new Document();

			try {
				PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);

				MyFooter event = new MyFooter(principal,userServiceImpl);
				writer.setPageEvent(event);


				Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
				Font redFont3 = new Font(Font.FontFamily.TIMES_ROMAN, 17, Font.BOLD);
				Font redFont2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.ITALIC);
				Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
				Font headFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD|Font.UNDERLINE);
				Font headFont2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD|Font.UNDERLINE);

				
				document.open();


				document.add(pq);

				Paragraph p51 = new Paragraph("UDBHAVA HOSPITALS", redFont);
				p51.add("\n \n \n \n");
				p51.setAlignment(Element.ALIGN_CENTER);
				document.add(p51);
				Font redFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

				date = Calendar.getInstance().getTime();
				formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
				String today = formatter.format(date).toString();

				PdfPTable table2 = new PdfPTable(2);
				
				PdfPCell cell1 = new PdfPCell();
				table2.setWidthPercentage(100f);	
				cell1.setBorder(Rectangle.BOTTOM|Rectangle.TOP);
				
				PdfPTable table21 = new PdfPTable(6);
				table21.setWidths(new float[] { 5f,1f,5f,5f,1f,6.5f});
				 table21.setSpacingBefore(10);
				

				PdfPCell hcell1;
				hcell1 = new PdfPCell(
						new Phrase("Patient Name", redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(
						new Phrase(":", redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(
						new Phrase(patientRegistration.getPatientDetails().getTitle() +". "+ patientRegistration.getPatientDetails().getFirstName()
								+ " " + patientRegistration.getPatientDetails().getLastName(), redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase("Age/Gender", redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase(":", redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getAge()
						+ "/" + patientRegistration.getPatientDetails().getGender(), redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				
				
				PdfPCell hcell4;
				hcell4 = new PdfPCell(
						new Phrase("Admission No", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(
						new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(
						new Phrase(patientRegistration.getRegId(), redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				


				if(admittedWard!=null)
				{
				hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(admittedWard, redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				}
				else
				{
					hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					
					hcell4 = new PdfPCell(new Phrase(":", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					hcell4 = new PdfPCell(new Phrase("", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);

				}



				PdfPCell hcell15;
				hcell15 = new PdfPCell(
						new Phrase("Admission Date", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				hcell15 = new PdfPCell(
						new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				hcell15 = new PdfPCell(
						new Phrase(pdfadmissionDate, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				
				if(roomName!=null)
				{
				hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(roomName, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				}
				else
				{
					hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase(":", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase("", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
				}
				PdfPCell hcell16;
				hcell16 = new PdfPCell(new Phrase(
						"Bill No/UMR No", redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(
						":", redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getUmr(), redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);

				hcell16 = new PdfPCell(new Phrase("Bill Date", redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(":", redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(bill, redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);

				PdfPCell hcell17;
				hcell17 = new PdfPCell(new Phrase(
						"Consultant",
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(
						":",
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(
						patientRegistration.getPatientDetails().getConsultant(),
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);

				hcell17 = new PdfPCell(new Phrase("Report Date", redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(":", redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(today, redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				cell1.setColspan(2);
				cell1.addElement(table21);
				table2.addCell(cell1);
             //  table2.addCell(cell1);
               document.add(table2);

				Paragraph p11 = new Paragraph("DEPARTMENT OF HAEMATOLOGY",headFont1);
				p11.setAlignment(Element.ALIGN_CENTER);
				document.add(p11);

				PdfPTable table3 = new PdfPTable(3);
				table3.setWidths(new float[] { 5f, 6f, 9f });

				PdfPCell hcell01;
				hcell01 = new PdfPCell(new Phrase("\n INVESTIGATION", headFont2));
				hcell01.setBorder(Rectangle.NO_BORDER);
				// hcell01.setPaddingTop(250f);
				hcell01.setPaddingLeft(-50f);
				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);

				table3.addCell(hcell01);

				hcell01 = new PdfPCell(new Phrase("\n RESULT", headFont2));
				hcell01.setBorder(Rectangle.NO_BORDER);
				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
				 hcell01.setPaddingLeft(30f);
				table3.addCell(hcell01);

				hcell01 = new PdfPCell(new Phrase("\n NORMAL RANGE", headFont2));
				hcell01.setBorder(Rectangle.NO_BORDER);
				// hcell1.setPaddingRight(-40f);
				// hcell01.setPaddingTop(50f);

				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell01.setPaddingLeft(50f);
				table3.addCell(hcell01);

				for (RefMeasureDetails refMeasureDetailsInfoPdf : refMeasureDetails) {

					PdfPCell hcell021;
					hcell021 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getMeasure(), redFont1));
					hcell021.setBorder(Rectangle.NO_BORDER);
					hcell021.setPaddingLeft(-50f);
					hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
					table3.addCell(hcell021);

					if(refMeasureDetailsInfoPdf.getDimension()!=null)
					{
					hcell021 = new PdfPCell(new Phrase(
							": " + refMeasureDetailsInfoPdf.getValue() + " " + refMeasureDetailsInfoPdf.getDimension(),
							redFont1));
					hcell021.setBorder(Rectangle.NO_BORDER);
					// hcell4.setPaddingRight(5f);
					hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell021.setPaddingLeft(30f);

					table3.addCell(hcell021);
					}
					else
					{
						hcell021 = new PdfPCell(new Phrase(
								": " + refMeasureDetailsInfoPdf.getValue(),
								redFont1));
						hcell021.setBorder(Rectangle.NO_BORDER);
						// hcell4.setPaddingRight(5f);
						hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell021.setPaddingLeft(30f);

						table3.addCell(hcell021);

					}
					if(refMeasureDetailsInfoPdf.getDimension()!=null) {
						
					hcell021 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getRange()+" "+refMeasureDetailsInfoPdf.getDimension(), redFont1));
					hcell021.setBorder(Rectangle.NO_BORDER);
					// hcell4.setPaddingRight(5f);

					hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell021.setPaddingLeft(50f);
					table3.addCell(hcell021);
					}
					else
					{
						hcell021 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getRange(), redFont1));
						hcell021.setBorder(Rectangle.NO_BORDER);
						// hcell4.setPaddingRight(5f);

						hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell021.setPaddingLeft(50f);
						table3.addCell(hcell021);

					}
					System.out.println(refMeasureDetailsInfoPdf.getMethod());
				}

				document.add(table3);

				Paragraph p01 = new Paragraph();
				
				p01.add("\n");
				p01.add("\n");
				document.add(p01);

				if (patientServiceDetails.getComment().isEmpty()) {
					Paragraph p27 = new Paragraph("\n Findings:", headFont);
					document.add(p27);
					Paragraph p28 = new Paragraph("N/A", redFont2);
					document.add(p28);

				} else {
					Paragraph p27 = new Paragraph("\n Findings:", headFont);

					Paragraph p28 = new Paragraph(patientServiceDetails.getComment(), redFont2);
					document.add(p27);
					document.add(p28);
				}
				
				Paragraph p7 = new Paragraph("*Please correlate with clinical findings if necessary discuss", redFont2);
				p7.setAlignment(Element.ALIGN_CENTER);

				
				p7.add("\n");
				p7.add("\n");
				document.add(p7);

				Chunk cnd = new Chunk(new VerticalPositionMark());
				Paragraph p19 = new Paragraph(patientRegistration.getPatientDetails().getConsultant(), headFont);
				p19.setAlignment(Element.ALIGN_RIGHT);
				document.add(p19);

				Paragraph p18 = new Paragraph("LAB INCHARGE", headFont);
				p18.add(cnd);

				p18.add("(" + patientRegistration.getVuserD().getDoctorDetails().getSpecilization() + ")");
				document.add(p18);

				// ************************

				document.close();

				pdfBytes = byteArrayOutputStream.toByteArray();
				List<ServicePdf> servicePdfs=servicePdfServiceImpl.findByRegAndMeasureName(regId,patientServiceDetails.getServiceName());
				if(!servicePdfs.isEmpty())
				{
					servicePdf = new ServicePdf();
					servicePdf.setSid(servicePdfs.get(0).getSid());
					servicePdf.setData(pdfBytes);
					servicePdf.setFileName(servicePdfs.get(0).getFileName());
					servicePdf.setFileuri(servicePdfs.get(0).getFileuri());
					servicePdf.setRegId(servicePdfs.get(0).getRegId());
					servicePdfServiceImpl.save(servicePdf);

				}
				else
				{
				String uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/lab/servicePdf/viewFile/")
						.path(servicePdfServiceImpl.getNextLabId()).toUriString();

				servicePdf = new ServicePdf();
				servicePdf.setSid(servicePdfServiceImpl.getNextLabId());
				servicePdf.setData(pdfBytes);
				servicePdf.setFileName(patientServiceDetails.getServiceName());
				servicePdf.setFileuri(uri);
				servicePdf.setRegId(patientServiceDetails.getRegId());
				servicePdfServiceImpl.save(servicePdf);
				}


			} catch (Exception e) {
				Logger.error(e.getMessage());
			}

		}

		else if (patientServiceDetails.getServiceName().equalsIgnoreCase("TROPONIN - 1")) {

			byte[] pdfBytes = null;
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			Document document = new Document();

			try {
				PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);

				MyFooter event = new MyFooter(principal,userServiceImpl);
				writer.setPageEvent(event);


				Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
				Font redFont3 = new Font(Font.FontFamily.TIMES_ROMAN, 17, Font.BOLD);
				Font redFont2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.ITALIC);
				Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
				Font headFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD|Font.UNDERLINE);
				Font headFont2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD|Font.UNDERLINE);

				document.open();

				

				document.add(pq);

				Paragraph p51 = new Paragraph("UDBHAVA HOSPITALS", redFont);
				p51.add("\n \n \n \n");
				p51.setAlignment(Element.ALIGN_CENTER);
				document.add(p51);
				
				Font redFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

				date = Calendar.getInstance().getTime();
				formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
				String today = formatter.format(date).toString();

				PdfPTable table2 = new PdfPTable(2);
				
				PdfPCell cell1 = new PdfPCell();
				table2.setWidthPercentage(100f);	
				cell1.setBorder(Rectangle.BOTTOM|Rectangle.TOP);
				
				PdfPTable table21 = new PdfPTable(6);
				table21.setWidths(new float[] { 5f,1f,5f,5f,1f,6.5f});
				 table21.setSpacingBefore(10);
				

				PdfPCell hcell1;
				hcell1 = new PdfPCell(
						new Phrase("Patient Name", redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(
						new Phrase(":", redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(
						new Phrase(patientRegistration.getPatientDetails().getTitle() +". "+ patientRegistration.getPatientDetails().getFirstName()
								+ " " + patientRegistration.getPatientDetails().getLastName(), redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase("Age/Gender", redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase(":", redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getAge()
						+ "/" + patientRegistration.getPatientDetails().getGender(), redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				
				
				PdfPCell hcell4;
				hcell4 = new PdfPCell(
						new Phrase("Admission No", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(
						new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(
						new Phrase(patientRegistration.getRegId(), redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				


				if(admittedWard!=null)
				{
				hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(admittedWard, redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				}
				else
				{
					hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					
					hcell4 = new PdfPCell(new Phrase(":", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					hcell4 = new PdfPCell(new Phrase("", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);

				}



				PdfPCell hcell15;
				hcell15 = new PdfPCell(
						new Phrase("Admission Date", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				hcell15 = new PdfPCell(
						new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				hcell15 = new PdfPCell(
						new Phrase(pdfadmissionDate, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				
				if(roomName!=null)
				{
				hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(roomName, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				}
				else
				{
					hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase(":", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase("", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
				}
				PdfPCell hcell16;
				hcell16 = new PdfPCell(new Phrase(
						"Bill No/UMR No", redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(
						":", redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getUmr(), redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);

				hcell16 = new PdfPCell(new Phrase("Bill Date", redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(":", redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(bill, redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);

				PdfPCell hcell17;
				hcell17 = new PdfPCell(new Phrase(
						"Consultant",
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(
						":",
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(
						patientRegistration.getPatientDetails().getConsultant(),
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);

				hcell17 = new PdfPCell(new Phrase("Report Date", redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(":", redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(today, redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				cell1.setColspan(2);
				cell1.addElement(table21);
				table2.addCell(cell1);
             //  table2.addCell(cell1);
               document.add(table2);

				
				
				Paragraph p11 = new Paragraph("DEPARTMENT OF BIOCHEMISTRY",headFont1);
				p11.setAlignment(Element.ALIGN_CENTER);
								document.add(p11);

				PdfPTable table3 = new PdfPTable(2);
				table3.setWidths(new float[] { 5f, 6f });

				PdfPCell hcell01;
				hcell01 = new PdfPCell(new Phrase("\n INVESTIGATION", headFont2));
				hcell01.setBorder(Rectangle.NO_BORDER);
				// hcell01.setPaddingTop(250f);
				hcell01.setPaddingLeft(-50f);
				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);

				table3.addCell(hcell01);

				hcell01 = new PdfPCell(new Phrase("\n RESULT", headFont2));
				hcell01.setBorder(Rectangle.NO_BORDER);
			//	hcell01.setPaddingRight(-40f);
				// hcell01.setPaddingTop(50f);
				// hcell01.setPaddingLeft(80f);
				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
				table3.addCell(hcell01);

				for (RefMeasureDetails refMeasureDetailsInfoPdf : refMeasureDetails) {

					if(refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("Troponin -1"))
					{
					PdfPCell hcell021;
					hcell021 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getMeasure(), redFont1));
					hcell021.setBorder(Rectangle.NO_BORDER);
					hcell021.setPaddingLeft(-50f);
					hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
					table3.addCell(hcell021);

					if (refMeasureDetailsInfoPdf.getDimension() != null) {
						hcell021 = new PdfPCell(new Phrase(": " + refMeasureDetailsInfoPdf.getValue() + " "
								+ refMeasureDetailsInfoPdf.getDimension(), redFont1));
						hcell021.setBorder(Rectangle.NO_BORDER);
						// hcell4.setPaddingRight(5f);
						hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell021.setPaddingLeft(0f);
						table3.addCell(hcell021);
						
					} else {
						hcell021 = new PdfPCell(new Phrase(": " + refMeasureDetailsInfoPdf.getValue(), redFont1));
						hcell021.setBorder(Rectangle.NO_BORDER);
						// hcell4.setPaddingRight(5f);
						hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell021.setPaddingLeft(0f);
						table3.addCell(hcell021);
					}
					
					System.out.println(refMeasureDetailsInfoPdf.getMethod());
					
						PdfPCell hcell0211;
						hcell0211 = new PdfPCell(
								new Phrase("\n method : " + refMeasureDetailsInfoPdf.getMethod(), redFont1));
						hcell0211.setBorder(Rectangle.NO_BORDER);
						hcell0211.setPaddingLeft(-50f);
						hcell0211.setHorizontalAlignment(Element.ALIGN_LEFT);
						table3.addCell(hcell0211);
						
						
						hcell0211 = new PdfPCell(
								new Phrase("", redFont1));
						hcell0211.setBorder(Rectangle.NO_BORDER);
						hcell0211.setPaddingLeft(-50f);
						hcell0211.setHorizontalAlignment(Element.ALIGN_LEFT);
						table3.addCell(hcell0211);
						
						hcell0211 = new PdfPCell(
								new Phrase("" , redFont1));
						hcell0211.setBorder(Rectangle.NO_BORDER);
						hcell0211.setPaddingLeft(-50f);
						hcell0211.setHorizontalAlignment(Element.ALIGN_LEFT);
						table3.addCell(hcell0211);

					}
				}

				document.add(table3);

				Paragraph p01 = new Paragraph();
				
				p01.add("\n");
				p01.add("\n");
				document.add(p01);

				if (patientServiceDetails.getComment().isEmpty()) {
					Paragraph p27 = new Paragraph("\n Findings:", headFont);
					document.add(p27);
					Paragraph p28 = new Paragraph("N/A", redFont2);
					document.add(p28);

				} else {
					Paragraph p27 = new Paragraph("\n Findings:", headFont);

					Paragraph p28 = new Paragraph(patientServiceDetails.getComment(), redFont2);
					document.add(p27);
					document.add(p28);
				}
				
				Paragraph p7 = new Paragraph("*Please correlate with clinical findings if necessary discuss", redFont2);
				p7.setAlignment(Element.ALIGN_CENTER);

				
				p7.add("\n");
				p7.add("\n");
				document.add(p7);

				Chunk cnd = new Chunk(new VerticalPositionMark());
				Paragraph p19 = new Paragraph(patientRegistration.getPatientDetails().getConsultant(), headFont);
				p19.setAlignment(Element.ALIGN_RIGHT);
				document.add(p19);

				Paragraph p18 = new Paragraph("LAB INCHARGE", headFont);
				p18.add(cnd);

				p18.add("(" + patientRegistration.getVuserD().getDoctorDetails().getSpecilization() + ")");
				document.add(p18);

				// ************************

				document.close();

				pdfBytes = byteArrayOutputStream.toByteArray();
				List<ServicePdf> servicePdfs=servicePdfServiceImpl.findByRegAndMeasureName(regId,patientServiceDetails.getServiceName());
				if(!servicePdfs.isEmpty())
				{
					servicePdf = new ServicePdf();
					servicePdf.setSid(servicePdfs.get(0).getSid());
					servicePdf.setData(pdfBytes);
					servicePdf.setFileName(servicePdfs.get(0).getFileName());
					servicePdf.setFileuri(servicePdfs.get(0).getFileuri());
					servicePdf.setRegId(servicePdfs.get(0).getRegId());
					servicePdfServiceImpl.save(servicePdf);

				}
				else
				{
				String uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/lab/servicePdf/viewFile/")
						.path(servicePdfServiceImpl.getNextLabId()).toUriString();

				servicePdf = new ServicePdf();
				servicePdf.setSid(servicePdfServiceImpl.getNextLabId());
				servicePdf.setData(pdfBytes);
				servicePdf.setFileName(patientServiceDetails.getServiceName());
				servicePdf.setFileuri(uri);
				servicePdf.setRegId(patientServiceDetails.getRegId());
				servicePdfServiceImpl.save(servicePdf);
				}


			} catch (Exception e) {
				Logger.error(e.getMessage());
			}

		}

		else if (patientServiceDetails.getServiceName().equalsIgnoreCase("STOOL FOR OCCULT BLOOD")) {

			byte[] pdfBytes = null;
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			Document document = new Document();

			try {
				PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);

				MyFooter event = new MyFooter(principal,userServiceImpl);
				writer.setPageEvent(event);

				Font blueFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
				Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
				Font redFont3 = new Font(Font.FontFamily.TIMES_ROMAN, 17, Font.BOLD);
				Font redFont2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.ITALIC);
				Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
				Font headFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD|Font.UNDERLINE);
				Font headFont2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD|Font.UNDERLINE);

				document.open();

				
				document.add(pq);

				Paragraph p51 = new Paragraph("UDBHAVA HOSPITALS", redFont);
				p51.add("\n \n \n \n");
				p51.setAlignment(Element.ALIGN_CENTER);
				document.add(p51);
		
				Font redFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

				date = Calendar.getInstance().getTime();
				formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
				String today = formatter.format(date).toString();

				
				PdfPTable table2 = new PdfPTable(2);
				
				PdfPCell cell1 = new PdfPCell();
				table2.setWidthPercentage(100f);	
				cell1.setBorder(Rectangle.BOTTOM|Rectangle.TOP);
				
				PdfPTable table21 = new PdfPTable(6);
				table21.setWidths(new float[] { 5f,1f,5f,5f,1f,6.5f});
				 table21.setSpacingBefore(10);
				

				PdfPCell hcell1;
				hcell1 = new PdfPCell(
						new Phrase("Patient Name", redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(
						new Phrase(":", redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(
						new Phrase(patientRegistration.getPatientDetails().getTitle() +". "+ patientRegistration.getPatientDetails().getFirstName()
								+ " " + patientRegistration.getPatientDetails().getLastName(), redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase("Age/Gender", redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase(":", redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getAge()
						+ "/" + patientRegistration.getPatientDetails().getGender(), redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				
				
				PdfPCell hcell4;
				hcell4 = new PdfPCell(
						new Phrase("Admission No", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(
						new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(
						new Phrase(patientRegistration.getRegId(), redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				


				if(admittedWard!=null)
				{
				hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(admittedWard, redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				}
				else
				{
					hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					
					hcell4 = new PdfPCell(new Phrase(":", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					hcell4 = new PdfPCell(new Phrase("", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);

				}



				PdfPCell hcell15;
				hcell15 = new PdfPCell(
						new Phrase("Admission Date", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				hcell15 = new PdfPCell(
						new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				hcell15 = new PdfPCell(
						new Phrase(pdfadmissionDate, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				
				if(roomName!=null)
				{
				hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(roomName, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				}
				else
				{
					hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase(":", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase("", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
				}
				PdfPCell hcell16;
				hcell16 = new PdfPCell(new Phrase(
						"Bill No/UMR No", redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(
						":", redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getUmr(), redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);

				hcell16 = new PdfPCell(new Phrase("Bill Date", redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(":", redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(bill, redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);

				PdfPCell hcell17;
				hcell17 = new PdfPCell(new Phrase(
						"Consultant",
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(
						":",
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(
						patientRegistration.getPatientDetails().getConsultant(),
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);

				hcell17 = new PdfPCell(new Phrase("Report Date", redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(":", redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(today, redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				cell1.setColspan(2);
				cell1.addElement(table21);
				table2.addCell(cell1);
             //  table2.addCell(cell1);
               document.add(table2);

				Paragraph p11 = new Paragraph("DEPARTMENT OF CLINICAL PATHOLOGY",headFont1);
				p11.setAlignment(Element.ALIGN_CENTER);
				document.add(p11);

				PdfPTable table3 = new PdfPTable(2);
				table3.setWidths(new float[] { 7f, 7f });

				PdfPCell hcell01;
				hcell01 = new PdfPCell(new Phrase("\n INVESTIGATION", headFont2));
				hcell01.setBorder(Rectangle.NO_BORDER);
				// hcell01.setPaddingTop(250f);
				hcell01.setPaddingLeft(-50f);
				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);

				table3.addCell(hcell01);

				PdfPCell hcell011;
				hcell011 = new PdfPCell(new Phrase("\n RESULT", headFont2));
				hcell011.setBorder(Rectangle.NO_BORDER);
				hcell011.setPaddingRight(-40f);
				// hcell01.setPaddingTop(50f);
				// hcell01.setPaddingLeft(80f);
				hcell011.setHorizontalAlignment(Element.ALIGN_LEFT);
				table3.addCell(hcell011);

				for (RefMeasureDetails refMeasureDetailsInfoPdf : refMeasureDetails) {

					hcell01 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getMeasure(), redFont1));
					hcell01.setBorder(Rectangle.NO_BORDER);
					hcell01.setPaddingLeft(-50f);
					hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
					table3.addCell(hcell01);

					if (refMeasureDetailsInfoPdf.getDimension() != null) {
						hcell011 = new PdfPCell(new Phrase(": " + refMeasureDetailsInfoPdf.getValue() + ""
								+ refMeasureDetailsInfoPdf.getDimension(), redFont1));
						hcell011.setBorder(Rectangle.NO_BORDER);
						// hcell4.setPaddingRight(5f);
						hcell011.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell011.setPaddingLeft(1f);

						table3.addCell(hcell011);
					} else {
						hcell011 = new PdfPCell(new Phrase(": " + refMeasureDetailsInfoPdf.getValue(), redFont1));
						hcell011.setBorder(Rectangle.NO_BORDER);
						// hcell4.setPaddingRight(5f);
						hcell011.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell011.setPaddingLeft(1f);

						table3.addCell(hcell011);

					}

					if (refMeasureDetailsInfoPdf.getMethod() != null) {
						PdfPCell hcell0211;
						hcell0211 = new PdfPCell(
								new Phrase("\n Method : "+refMeasureDetailsInfoPdf.getMethod() , headFont));
						hcell0211.setBorder(Rectangle.NO_BORDER);
						hcell0211.setPaddingLeft(-50f);
						hcell0211.setHorizontalAlignment(Element.ALIGN_LEFT);
						table3.addCell(hcell0211);
						
						hcell0211 = new PdfPCell(
								new Phrase("", redFont1));
						hcell0211.setBorder(Rectangle.NO_BORDER);
						hcell0211.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell0211.setPaddingLeft(-90f);
						table3.addCell(hcell0211);

					}
				}

				document.add(table3);

				Paragraph p01 = new Paragraph();
				
				p01.add("\n");
				p01.add("\n");
				document.add(p01);

				if (patientServiceDetails.getComment().isEmpty()) {
					Paragraph p27 = new Paragraph("\n Findings:", headFont);
					document.add(p27);
					Paragraph p28 = new Paragraph("N/A", redFont2);
					document.add(p28);

				} else {
					Paragraph p27 = new Paragraph("\n Findings:", headFont);

					Paragraph p28 = new Paragraph(patientServiceDetails.getComment(), redFont2);
					document.add(p27);
					document.add(p28);
				}
				

				Paragraph p7 = new Paragraph("*Suggested clinical correlation, if necessary kindly discuss", redFont2);
				p7.setAlignment(Element.ALIGN_CENTER);

				
				p7.add("\n");
				p7.add("\n");
				document.add(p7);

				Chunk cnd = new Chunk(new VerticalPositionMark());
				Paragraph p19 = new Paragraph(patientRegistration.getPatientDetails().getConsultant(), headFont);
				p19.setAlignment(Element.ALIGN_RIGHT);
				document.add(p19);

				Paragraph p18 = new Paragraph("LAB INCHARGE", headFont);
				p18.add(cnd);

				p18.add("(" + patientRegistration.getVuserD().getDoctorDetails().getSpecilization() + ")");
				document.add(p18);

				// ************************

				document.close();

				pdfBytes = byteArrayOutputStream.toByteArray();
				List<ServicePdf> servicePdfs=servicePdfServiceImpl.findByRegAndMeasureName(regId,patientServiceDetails.getServiceName());
				if(!servicePdfs.isEmpty())
				{
					servicePdf = new ServicePdf();
					servicePdf.setSid(servicePdfs.get(0).getSid());
					servicePdf.setData(pdfBytes);
					servicePdf.setFileName(servicePdfs.get(0).getFileName());
					servicePdf.setFileuri(servicePdfs.get(0).getFileuri());
					servicePdf.setRegId(servicePdfs.get(0).getRegId());
					servicePdfServiceImpl.save(servicePdf);

				}
				else
				{
				String uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/lab/servicePdf/viewFile/")
						.path(servicePdfServiceImpl.getNextLabId()).toUriString();

				servicePdf = new ServicePdf();
				servicePdf.setSid(servicePdfServiceImpl.getNextLabId());
				servicePdf.setData(pdfBytes);
				servicePdf.setFileName(patientServiceDetails.getServiceName());
				servicePdf.setFileuri(uri);
				servicePdf.setRegId(patientServiceDetails.getRegId());
				servicePdfServiceImpl.save(servicePdf);
				}

			} catch (Exception e) {
				Logger.error(e.getMessage());
			}

		}

		else if (patientServiceDetails.getServiceName().equalsIgnoreCase("FASTING BLOOD SUGAR")) {

			byte[] pdfBytes = null;
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			Document document = new Document();

			try {
				PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream); 

				MyFooter event = new MyFooter(principal,userServiceImpl);
				writer.setPageEvent(event);


				Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
				Font redFont3 = new Font(Font.FontFamily.TIMES_ROMAN, 17, Font.BOLD);
				Font redFont2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.ITALIC);
				Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
				Font headFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD|Font.UNDERLINE);
				Font headFont2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD|Font.UNDERLINE);

				document.open();

				document.add(pq);

				Paragraph p51 = new Paragraph("UDBHAVA HOSPITALS", redFont);
				p51.add("\n \n \n \n");
				p51.setAlignment(Element.ALIGN_CENTER);
				document.add(p51);
	
				Font redFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

				date = Calendar.getInstance().getTime();
				formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
				String today = formatter.format(date).toString();

				
				PdfPTable table2 = new PdfPTable(2);
				
				PdfPCell cell1 = new PdfPCell();
				table2.setWidthPercentage(100f);	
				cell1.setBorder(Rectangle.BOTTOM|Rectangle.TOP);
				
				PdfPTable table21 = new PdfPTable(6);
				table21.setWidths(new float[] { 5f,1f,5f,5f,1f,6.5f});
				 table21.setSpacingBefore(10);
				

				PdfPCell hcell1;
				hcell1 = new PdfPCell(
						new Phrase("Patient Name", redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(
						new Phrase(":", redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(
						new Phrase(patientRegistration.getPatientDetails().getTitle() +". "+ patientRegistration.getPatientDetails().getFirstName()
								+ " " + patientRegistration.getPatientDetails().getLastName(), redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase("Age/Gender", redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase(":", redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getAge()
						+ "/" + patientRegistration.getPatientDetails().getGender(), redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				
				
				PdfPCell hcell4;
				hcell4 = new PdfPCell(
						new Phrase("Admission No", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(
						new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(
						new Phrase(patientRegistration.getRegId(), redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				


				if(admittedWard!=null)
				{
				hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(admittedWard, redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				}
				else
				{
					hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					
					hcell4 = new PdfPCell(new Phrase(":", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					hcell4 = new PdfPCell(new Phrase("", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);

				}



				PdfPCell hcell15;
				hcell15 = new PdfPCell(
						new Phrase("Admission Date", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				hcell15 = new PdfPCell(
						new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				hcell15 = new PdfPCell(
						new Phrase(pdfadmissionDate, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				
				if(roomName!=null)
				{
				hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(roomName, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				}
				else
				{
					hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase(":", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase("", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
				}
				PdfPCell hcell16;
				hcell16 = new PdfPCell(new Phrase(
						"Bill No/UMR No", redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(
						":", redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getUmr(), redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);

				hcell16 = new PdfPCell(new Phrase("Bill Date", redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(":", redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(bill, redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);

				PdfPCell hcell17;
				hcell17 = new PdfPCell(new Phrase(
						"Consultant",
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(
						":",
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(
						patientRegistration.getPatientDetails().getConsultant(),
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);

				hcell17 = new PdfPCell(new Phrase("Report Date", redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(":", redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(today, redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				cell1.setColspan(2);
				cell1.addElement(table21);
				table2.addCell(cell1);
             //  table2.addCell(cell1);
               document.add(table2);

				Paragraph p11 = new Paragraph("BIOCHEMISTRY REPORT",headFont1);
				p11.setAlignment(Element.ALIGN_CENTER);
				document.add(p11);

				PdfPTable table3 = new PdfPTable(3);
				table3.setWidths(new float[] { 6f, 6f, 9f });

				PdfPCell hcell01;
				hcell01 = new PdfPCell(new Phrase("\nINVESTIGATION", headFont2));
				hcell01.setBorder(Rectangle.NO_BORDER);
				// hcell01.setPaddingTop(250f);
				hcell01.setPaddingLeft(-50f);
				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);

				table3.addCell(hcell01);

				hcell01 = new PdfPCell(new Phrase("\nRESULT", headFont2));
				hcell01.setBorder(Rectangle.NO_BORDER);
				// hcell1.setPaddingRight(-40f);
				// hcell01.setPaddingTop(50f);
				// hcell01.setPaddingLeft(80f);
				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell01.setPaddingLeft(50f);
				table3.addCell(hcell01);

				hcell01 = new PdfPCell(new Phrase("\nNORMAL RANGE", headFont2));
				hcell01.setBorder(Rectangle.NO_BORDER);
				// hcell1.setPaddingRight(-40f);
				// hcell01.setPaddingTop(50f);

				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell01.setPaddingLeft(85f);
				table3.addCell(hcell01);

				for (RefMeasureDetails refMeasureDetailsInfoPdf : refMeasureDetails) {

					PdfPCell hcell021;
					hcell021 = new PdfPCell(new Phrase("\n" + refMeasureDetailsInfoPdf.getMeasure(), redFont1));
					hcell021.setBorder(Rectangle.NO_BORDER);
					hcell021.setPaddingLeft(-50f);
					hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
					table3.addCell(hcell021);

					if (refMeasureDetailsInfoPdf.getDimension() != null) {
						hcell021 = new PdfPCell(new Phrase("\n : " + refMeasureDetailsInfoPdf.getValue() + " "
								+ refMeasureDetailsInfoPdf.getDimension(), redFont1));
						hcell021.setBorder(Rectangle.NO_BORDER);
						// hcell4.setPaddingRight(5f);
						hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell021.setPaddingLeft(45f);
						table3.addCell(hcell021);
					} else {
						hcell021 = new PdfPCell(new Phrase("\n : " + refMeasureDetailsInfoPdf.getValue(), redFont1));
						hcell021.setBorder(Rectangle.NO_BORDER);
						// hcell4.setPaddingRight(5f);
						hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell021.setPaddingLeft(45f);
						table3.addCell(hcell021);
					}

								if(refMeasureDetailsInfoPdf.getDimension()!=null) {	
					hcell021 = new PdfPCell(new Phrase("\n" + refMeasureDetailsInfoPdf.getRange()+ ""+refMeasureDetailsInfoPdf.getDimension(), redFont1));
					hcell021.setBorder(Rectangle.NO_BORDER);
					// hcell4.setPaddingRight(5f);
					hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell021.setPaddingLeft(89f);
					table3.addCell(hcell021);
								}
								else
								{
									hcell021 = new PdfPCell(new Phrase("\n" + refMeasureDetailsInfoPdf.getRange(), redFont1));
									hcell021.setBorder(Rectangle.NO_BORDER);
									// hcell4.setPaddingRight(5f);
									hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
									hcell021.setPaddingLeft(89f);
									table3.addCell(hcell021);
								}
				}
				document.add(table3);

				Paragraph p01 = new Paragraph();

				p01.add("\n");
				p01.add("\n");
				document.add(p01);

				if (patientServiceDetails.getComment().isEmpty()) {
					Paragraph p27 = new Paragraph("\n Findings:", headFont);
					document.add(p27);
					Paragraph p28 = new Paragraph("N/A", redFont2);
					document.add(p28);

				} else {
					Paragraph p27 = new Paragraph("\n Findings:", headFont);

					Paragraph p28 = new Paragraph(patientServiceDetails.getComment(), redFont1);
					document.add(p27);
					document.add(p28);
				}
				

				Paragraph p7 = new Paragraph("*Suggested Clinical correlation", redFont2);
				p7.setAlignment(Element.ALIGN_CENTER);

				
				p7.add("\n");
				p7.add("\n");
				document.add(p7);

				Chunk cnd = new Chunk(new VerticalPositionMark());
				Paragraph p19 = new Paragraph(patientRegistration.getPatientDetails().getConsultant(), headFont);
				p19.setAlignment(Element.ALIGN_RIGHT);
				document.add(p19);

				Paragraph p18 = new Paragraph("LAB INCHARGE", headFont);
				p18.add(cnd);

				p18.add("(" + patientRegistration.getVuserD().getDoctorDetails().getSpecilization() + ")");
				document.add(p18);

				// **********************

				document.close();

				pdfBytes = byteArrayOutputStream.toByteArray();
				List<ServicePdf> servicePdfs=servicePdfServiceImpl.findByRegAndMeasureName(regId,patientServiceDetails.getServiceName());
				if(!servicePdfs.isEmpty())
				{
					servicePdf = new ServicePdf();
					servicePdf.setSid(servicePdfs.get(0).getSid());
					servicePdf.setData(pdfBytes);
					servicePdf.setFileName(servicePdfs.get(0).getFileName());
					servicePdf.setFileuri(servicePdfs.get(0).getFileuri());
					servicePdf.setRegId(servicePdfs.get(0).getRegId());
					servicePdfServiceImpl.save(servicePdf);

				}
				else
				{
				String uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/lab/servicePdf/viewFile/")
						.path(servicePdfServiceImpl.getNextLabId()).toUriString();

				servicePdf = new ServicePdf();
				servicePdf.setSid(servicePdfServiceImpl.getNextLabId());
				servicePdf.setData(pdfBytes);
				servicePdf.setFileName(patientServiceDetails.getServiceName());
				servicePdf.setFileuri(uri);
				servicePdf.setRegId(patientServiceDetails.getRegId());
				servicePdfServiceImpl.save(servicePdf);
				}


			} catch (Exception e) {
				Logger.error(e.getMessage());
			}
		}

		else if (patientServiceDetails.getServiceName().equalsIgnoreCase("SEMEN ANALYSIS")) {

			byte[] pdfBytes = null;
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			Document document = new Document();

			try {
				PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);

				MyFooter event = new MyFooter(principal,userServiceImpl);
				writer.setPageEvent(event);


				Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
				Font redFont3 = new Font(Font.FontFamily.TIMES_ROMAN, 17, Font.BOLD);
				Font redFont2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.ITALIC);
				Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
				Font headFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD|Font.UNDERLINE);
				Font headFont2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD|Font.UNDERLINE);

				document.open();

				

				document.add(pq);

				Paragraph p51 = new Paragraph("UDBHAVA HOSPITALS", redFont);
				p51.add("\n \n \n");
				p51.setAlignment(Element.ALIGN_CENTER);
				document.add(p51);
				
				Font redFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

				date = Calendar.getInstance().getTime();
				formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
				String today = formatter.format(date).toString();

				PdfPTable table2 = new PdfPTable(2);
				
				PdfPCell cell1 = new PdfPCell();
				table2.setWidthPercentage(100f);	
				cell1.setBorder(Rectangle.BOTTOM|Rectangle.TOP);
				
				PdfPTable table21 = new PdfPTable(6);
				table21.setWidths(new float[] { 5f,1f,5f,5f,1f,6.5f});
				 table21.setSpacingBefore(10);
				

				PdfPCell hcell1;
				hcell1 = new PdfPCell(
						new Phrase("Patient Name", redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);
				
				hcell1 = new PdfPCell(
						new Phrase(":", redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);
				
				hcell1 = new PdfPCell(
						new Phrase(patientRegistration.getPatientDetails().getTitle() +". "+ patientRegistration.getPatientDetails().getFirstName()
								+ " " + patientRegistration.getPatientDetails().getLastName(), redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase("Age/Gender", redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase(":", redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);
				
				hcell1 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getAge()
						+ "/" + patientRegistration.getPatientDetails().getGender(), redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				PdfPCell hcell4;
				hcell4 = new PdfPCell(
						new Phrase("Admission No", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(
						new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(
						new Phrase(patientRegistration.getRegId(), redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				if(admittedWard!=null)
				{
				hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(admittedWard, redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				}
				else
				{
					hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					
					hcell4 = new PdfPCell(new Phrase(":", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					hcell4 = new PdfPCell(new Phrase("", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);

				}
				PdfPCell hcell15;
				hcell15 = new PdfPCell(
						new Phrase("Admission Date", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				hcell15 = new PdfPCell(
						new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				hcell15 = new PdfPCell(
						new Phrase(pdfadmissionDate, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);
				if(roomName!=null)
				{
				hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(roomName, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				}
				else
				{
					hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase(":", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase("", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
				}
				PdfPCell hcell16;
				hcell16 = new PdfPCell(new Phrase(
						"Bill No/UMR No", redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(
						":", redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getUmr(), redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);

				hcell16 = new PdfPCell(new Phrase("Bill Date", redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(":", redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(bill, redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);

				PdfPCell hcell17;
				hcell17 = new PdfPCell(new Phrase(
						"Consultant",
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(
						":",
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(
						patientRegistration.getPatientDetails().getConsultant(),
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);

				hcell17 = new PdfPCell(new Phrase("Report Date", redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(":", redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(today, redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				cell1.setColspan(2);
				cell1.addElement(table21);
				table2.addCell(cell1);
             //  table2.addCell(cell1);
               document.add(table2);

				Paragraph p11 = new Paragraph("SEMEN ANALYSIS",headFont1);
				p11.setAlignment(Element.ALIGN_CENTER);
				document.add(p11);

				Paragraph p204 = new Paragraph("\n ", headFont);
				p204.setAlignment(Element.ALIGN_LEFT);
				document.add(p204);

				
				PdfPTable table3 = new PdfPTable(2);
				table3.setWidths(new float[] { 7f, 7f });
				
				
				for (RefMeasureDetails refMeasureDetailsInfoPdf : refMeasureDetails) {
					if (refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("PLACE OF COLLECTION")
							|| refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("METHOD OF COLLECTION")
							|| refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("TIME OF COLLECTION")
							|| refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("LIQUEF ACTION TIME")
							|| refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("PROCESSING TIME")) {
						PdfPCell hcell01;
						hcell01 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getMeasure(), redFont1));
						hcell01.setBorder(Rectangle.NO_BORDER);
						hcell01.setPaddingLeft(-50f);
						hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
						table3.addCell(hcell01);

						PdfPCell hcell011;
						if (refMeasureDetailsInfoPdf.getDimension() != null) {

							hcell011 = new PdfPCell(new Phrase(": " + refMeasureDetailsInfoPdf.getValue() + ""
									+ refMeasureDetailsInfoPdf.getDimension(), redFont1));
							hcell011.setBorder(Rectangle.NO_BORDER);
							// hcell4.setPaddingRight(5f);
							hcell011.setHorizontalAlignment(Element.ALIGN_LEFT);
							hcell011.setPaddingLeft(1f);

							table3.addCell(hcell011);
						} else {
							hcell011 = new PdfPCell(new Phrase(": " + refMeasureDetailsInfoPdf.getValue(), redFont1));
							hcell011.setBorder(Rectangle.NO_BORDER);
							// hcell4.setPaddingRight(5f);
							hcell011.setHorizontalAlignment(Element.ALIGN_LEFT);
							hcell011.setPaddingLeft(1f);
							table3.addCell(hcell011);
						}

						
					}
				}
				document.add(table3);
				Paragraph p21 = new Paragraph("", headFont2);
				p21.add("PHYSICAL EXAMINATION");
				p21.setAlignment(Element.ALIGN_LEFT);
				document.add(p21);

				/*Paragraph p203 = new Paragraph("\n ", headFont);
				p203.setAlignment(Element.ALIGN_LEFT);
				document.add(p203);
*/
				
				PdfPTable table31 = new PdfPTable(2);
				table31.setWidths(new float[] { 7f, 7f });
				for (RefMeasureDetails refMeasureDetailsInfoPdf : refMeasureDetails) {
					if (refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("VOLUME")
							|| refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("COLOUR")
							|| refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("REACTION")
							|| refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("VISCOSITY")) {
						PdfPCell hcell01;
						hcell01 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getMeasure(), redFont1));
						hcell01.setBorder(Rectangle.NO_BORDER);
						hcell01.setPaddingLeft(-50f);
						hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
						table31.addCell(hcell01);

						PdfPCell hcell011;
						if (refMeasureDetailsInfoPdf.getDimension() != null) {
							hcell011 = new PdfPCell(new Phrase(": " + refMeasureDetailsInfoPdf.getValue() + " "
									+ refMeasureDetailsInfoPdf.getDimension(), redFont1));
							hcell011.setBorder(Rectangle.NO_BORDER);
							// hcell4.setPaddingRight(5f);
							hcell011.setHorizontalAlignment(Element.ALIGN_LEFT);
							hcell011.setPaddingLeft(1f);

							table31.addCell(hcell011);
						} else {
							hcell011 = new PdfPCell(new Phrase(": " + refMeasureDetailsInfoPdf.getValue(), redFont1));
							hcell011.setBorder(Rectangle.NO_BORDER);
							// hcell4.setPaddingRight(5f);
							hcell011.setHorizontalAlignment(Element.ALIGN_LEFT);
							hcell011.setPaddingLeft(1f);

							table31.addCell(hcell011);
						}

											}
				}
				document.add(table31);
				Paragraph p22 = new Paragraph("", headFont2);
				p22.add("MICROSCOPIC EXAMINATION");
				p22.setAlignment(Element.ALIGN_LEFT);
				document.add(p22);
//LIQUEF ACTION TIME
		
				PdfPTable table311 = new PdfPTable(2);
				table311.setWidths(new float[] { 7f, 7f });
				for (RefMeasureDetails refMeasureDetailsInfoPdf : refMeasureDetails) {
					if (refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("TOTAL SPEM COUNT")
							|| refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("MORHOLOGY")
							
							|| refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("OTHERS")) {
						PdfPCell hcell01;
						hcell01 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getMeasure(), redFont1));
						hcell01.setBorder(Rectangle.NO_BORDER);
						hcell01.setPaddingLeft(-50f);
						hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
						table311.addCell(hcell01);

						PdfPCell hcell011;

						if (refMeasureDetailsInfoPdf.getDimension() != null) {
							hcell011 = new PdfPCell(new Phrase(": " + refMeasureDetailsInfoPdf.getValue() + " "
									+ refMeasureDetailsInfoPdf.getDimension(), redFont1));
							hcell011.setBorder(Rectangle.NO_BORDER);
							// hcell4.setPaddingRight(5f);
							hcell011.setHorizontalAlignment(Element.ALIGN_LEFT);
							hcell011.setPaddingLeft(1f);

							table311.addCell(hcell011);
						} else {
							hcell011 = new PdfPCell(new Phrase(": " + refMeasureDetailsInfoPdf.getValue(), redFont1));
							hcell011.setBorder(Rectangle.NO_BORDER);
							// hcell4.setPaddingRight(5f);
							hcell011.setHorizontalAlignment(Element.ALIGN_LEFT);
							hcell011.setPaddingLeft(1f);

							table311.addCell(hcell011);
						}

						
					}
				}
				
				document.add(table311);
				
				Paragraph p220 = new Paragraph("", headFont);
				p220.add("MOTILITY");
				p220.setAlignment(Element.ALIGN_LEFT);
				document.add(p220);
				
				
				
				PdfPTable table312 = new PdfPTable(2);
				table312.setWidths(new float[] { 7f, 7f });

				for (RefMeasureDetails refMeasureDetailsInfoPdf : refMeasureDetails) {
					if (refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("Rapid")
							|| refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("Progressive")
							
							) {
						PdfPCell hcell01;
						hcell01 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getMeasure(), redFont1));
						hcell01.setBorder(Rectangle.NO_BORDER);
						hcell01.setPaddingLeft(-30f);
						hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
						table312.addCell(hcell01);

						PdfPCell hcell011;

						if (refMeasureDetailsInfoPdf.getDimension() != null) {
							hcell011 = new PdfPCell(new Phrase(": " + refMeasureDetailsInfoPdf.getValue() + " "
									+ refMeasureDetailsInfoPdf.getDimension(), redFont1));
							hcell011.setBorder(Rectangle.NO_BORDER);
							// hcell4.setPaddingRight(5f);
							hcell011.setHorizontalAlignment(Element.ALIGN_LEFT);
							hcell011.setPaddingLeft(1f);

							table312.addCell(hcell011);
						} else {
							hcell011 = new PdfPCell(new Phrase(": " + refMeasureDetailsInfoPdf.getValue(), redFont1));
							hcell011.setBorder(Rectangle.NO_BORDER);
							// hcell4.setPaddingRight(5f);
							hcell011.setHorizontalAlignment(Element.ALIGN_LEFT);
							hcell011.setPaddingLeft(1f);

							table312.addCell(hcell011);
						}

						
					}
				}
				
document.add(table312);



Paragraph p212 = new Paragraph("", headFont);
p212.add("OTHERS");
p212.setAlignment(Element.ALIGN_LEFT);
document.add(p212);



PdfPTable table313 = new PdfPTable(2);
table313.setWidths(new float[] { 7f, 7f });

				for (RefMeasureDetails refMeasureDetailsInfoPdf : refMeasureDetails) {
					if (refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("Pus Cells")
							|| refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("Epitholial Cells")
							|| refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("RBC")
							|| refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("Spermato go nia")) {
						PdfPCell hcell01;
						hcell01 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getMeasure(), redFont1));
						hcell01.setBorder(Rectangle.NO_BORDER);
						hcell01.setPaddingLeft(-30f);
						hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
						table313.addCell(hcell01);

						PdfPCell hcell011;

						if (refMeasureDetailsInfoPdf.getDimension() != null) {

							hcell011 = new PdfPCell(new Phrase(": " + refMeasureDetailsInfoPdf.getValue() + " "
									+ refMeasureDetailsInfoPdf.getDimension(), redFont1));
							hcell011.setBorder(Rectangle.NO_BORDER);
							// hcell4.setPaddingRight(5f);
							hcell011.setHorizontalAlignment(Element.ALIGN_LEFT);
							hcell011.setPaddingLeft(1f);

							table313.addCell(hcell011);
						} else {
							hcell011 = new PdfPCell(new Phrase(": " + refMeasureDetailsInfoPdf.getValue(), redFont1));
							hcell011.setBorder(Rectangle.NO_BORDER);
							// hcell4.setPaddingRight(5f);
							hcell011.setHorizontalAlignment(Element.ALIGN_LEFT);
							hcell011.setPaddingLeft(1f);

							table313.addCell(hcell011);
						}

						
					}
				}

				document.add(table313);
				
				
				
				
				
				

PdfPTable table314 = new PdfPTable(2);
table314.setWidths(new float[] { 7f, 7f });

				for (RefMeasureDetails refMeasureDetailsInfoPdf : refMeasureDetails) {
					if (refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("IMPRESSION")
							) {
						PdfPCell hcell01;
						hcell01 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getMeasure(), redFont1));
						hcell01.setBorder(Rectangle.NO_BORDER);
						hcell01.setPaddingLeft(-50f);
						hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
						table314.addCell(hcell01);

						PdfPCell hcell011;

						if (refMeasureDetailsInfoPdf.getDimension() != null) {

							hcell011 = new PdfPCell(new Phrase(": " + refMeasureDetailsInfoPdf.getValue() + " "
									+ refMeasureDetailsInfoPdf.getDimension(), redFont1));
							hcell011.setBorder(Rectangle.NO_BORDER);
							// hcell4.setPaddingRight(5f);
							hcell011.setHorizontalAlignment(Element.ALIGN_LEFT);
							hcell011.setPaddingLeft(1f);

							table314.addCell(hcell011);
						} else {
							hcell011 = new PdfPCell(new Phrase(": " + refMeasureDetailsInfoPdf.getValue(), redFont1));
							hcell011.setBorder(Rectangle.NO_BORDER);
							// hcell4.setPaddingRight(5f);
							hcell011.setHorizontalAlignment(Element.ALIGN_LEFT);
							hcell011.setPaddingLeft(1f);

							table314.addCell(hcell011);
						}

						
					}
				}

				document.add(table314);


				Paragraph p01 = new Paragraph();
				
				p01.add("\n");
				p01.add("\n");
				document.add(p01);

				if (patientServiceDetails.getComment().isEmpty()) {
					Paragraph p27 = new Paragraph("\n Findings:", headFont);
					document.add(p27);
					Paragraph p28 = new Paragraph("N/A", redFont2);
					document.add(p28);

				} else {
					Paragraph p27 = new Paragraph("\n Findings:", headFont);

					Paragraph p28 = new Paragraph(patientServiceDetails.getComment(), redFont2);
					document.add(p27);
					document.add(p28);
				}
				
				Paragraph p7 = new Paragraph("*Suggested clinical ", redFont2);
				p7.setAlignment(Element.ALIGN_CENTER);

				
				p7.add("\n");
				p7.add("\n");
				document.add(p7);

				Chunk cnd = new Chunk(new VerticalPositionMark());
				Paragraph p19 = new Paragraph(patientRegistration.getPatientDetails().getConsultant(), headFont);
				p19.setAlignment(Element.ALIGN_RIGHT);
				document.add(p19);

				Paragraph p18 = new Paragraph("LAB INCHARGE", headFont);
				p18.add(cnd);

				p18.add("(" + patientRegistration.getVuserD().getDoctorDetails().getSpecilization() + ")");
				document.add(p18);

				// ************************

				document.close();

				pdfBytes = byteArrayOutputStream.toByteArray();
				List<ServicePdf> servicePdfs=servicePdfServiceImpl.findByRegAndMeasureName(regId,patientServiceDetails.getServiceName());
				if(!servicePdfs.isEmpty())
				{
					servicePdf = new ServicePdf();
					servicePdf.setSid(servicePdfs.get(0).getSid());
					servicePdf.setData(pdfBytes);
					servicePdf.setFileName(servicePdfs.get(0).getFileName());
					servicePdf.setFileuri(servicePdfs.get(0).getFileuri());
					servicePdf.setRegId(servicePdfs.get(0).getRegId());
					servicePdfServiceImpl.save(servicePdf);

				}
				else
				{
				String uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/lab/servicePdf/viewFile/")
						.path(servicePdfServiceImpl.getNextLabId()).toUriString();

				servicePdf = new ServicePdf();
				servicePdf.setSid(servicePdfServiceImpl.getNextLabId());
				servicePdf.setData(pdfBytes);
				servicePdf.setFileName(patientServiceDetails.getServiceName());
				servicePdf.setFileuri(uri);
				servicePdf.setRegId(patientServiceDetails.getRegId());
				servicePdfServiceImpl.save(servicePdf);
				}


			} catch (Exception e) {
				Logger.error(e.getMessage());
			}

		}
		else if (patientServiceDetails.getServiceName().equalsIgnoreCase("CPKMB")) {

			byte[] pdfBytes = null;
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			Document document = new Document();

			try {
				PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);

				MyFooter event = new MyFooter(principal,userServiceImpl);
				writer.setPageEvent(event);

				Font blueFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
				Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
				Font redFont3 = new Font(Font.FontFamily.TIMES_ROMAN, 17, Font.BOLD);
				Font redFont2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.ITALIC);
				Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
				Font headFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD|Font.UNDERLINE);
				Font headFont2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD|Font.UNDERLINE);

				document.open();

				

				document.add(pq);

				Paragraph p51 = new Paragraph("UDBHAVA HOSPITALS", redFont);
				p51.add("\n \n \n \n");
				p51.setAlignment(Element.ALIGN_CENTER);
				document.add(p51);
				
				
				
				Font redFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

				date = Calendar.getInstance().getTime();
				formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
				String today = formatter.format(date).toString();
				PdfPTable table2 = new PdfPTable(2);
				
				PdfPCell cell1 = new PdfPCell();
				table2.setWidthPercentage(100f);	
				cell1.setBorder(Rectangle.BOTTOM|Rectangle.TOP);
				
				PdfPTable table21 = new PdfPTable(6);
				table21.setWidths(new float[] { 5f,1f,5f,5f,1f,6.5f});
				 table21.setSpacingBefore(10);
				

				PdfPCell hcell1;
				hcell1 = new PdfPCell(
						new Phrase("Patient Name", redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);
				
				hcell1 = new PdfPCell(
						new Phrase(":", redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);
				
				hcell1 = new PdfPCell(
						new Phrase(patientRegistration.getPatientDetails().getTitle() +". "+ patientRegistration.getPatientDetails().getFirstName()
								+ " " + patientRegistration.getPatientDetails().getLastName(), redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase("Age/Gender", redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase(":", redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);
				
				hcell1 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getAge()
						+ "/" + patientRegistration.getPatientDetails().getGender(), redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				PdfPCell hcell4;
				hcell4 = new PdfPCell(
						new Phrase("Admission No", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(
						new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(
						new Phrase(patientRegistration.getRegId(), redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				if(admittedWard!=null)
				{
				hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(admittedWard, redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				}
				else
				{
					hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					
					hcell4 = new PdfPCell(new Phrase(":", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					hcell4 = new PdfPCell(new Phrase("", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);

				}
				PdfPCell hcell15;
				hcell15 = new PdfPCell(
						new Phrase("Admission Date", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				hcell15 = new PdfPCell(
						new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				hcell15 = new PdfPCell(
						new Phrase(pdfadmissionDate, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);
				if(roomName!=null)
				{
				hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(roomName, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				}
				else
				{
					hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase(":", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase("", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
				}
				PdfPCell hcell16;
				hcell16 = new PdfPCell(new Phrase(
						"Bill No/UMR No", redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(
						":", redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getUmr(), redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);

				hcell16 = new PdfPCell(new Phrase("Bill Date", redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(":", redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(bill, redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);

				PdfPCell hcell17;
				hcell17 = new PdfPCell(new Phrase(
						"Consultant",
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(
						":",
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(
						patientRegistration.getPatientDetails().getConsultant(),
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);

				hcell17 = new PdfPCell(new Phrase("Report Date", redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(":", redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(today, redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				cell1.setColspan(2);
				cell1.addElement(table21);
				table2.addCell(cell1);
             //  table2.addCell(cell1);
               document.add(table2);


				
				Paragraph p11 = new Paragraph("C P K M B",headFont1);
				p11.setAlignment(Element.ALIGN_CENTER);
								document.add(p11);
								
								
								Paragraph p101 = new Paragraph("\n",headFont);
								p101.setAlignment(Element.ALIGN_CENTER);
												document.add(p101);


				PdfPTable table3 = new PdfPTable(3);
				table3.setWidths(new float[] { 5f, 6f, 9f });

				PdfPCell hcell01;
				hcell01 = new PdfPCell(new Phrase("INVESTIGATION", headFont2));
				hcell01.setBorder(Rectangle.NO_BORDER);
				// hcell01.setPaddingTop(250f);
				hcell01.setPaddingLeft(-50f);
				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);

				table3.addCell(hcell01);

				hcell01 = new PdfPCell(new Phrase("RESULT", headFont2));
				hcell01.setBorder(Rectangle.NO_BORDER);
				hcell01.setPaddingRight(-40f);
				// hcell01.setPaddingTop(50f);
				// hcell01.setPaddingLeft(80f);
				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell01.setPaddingLeft(40f);
				table3.addCell(hcell01);

				hcell01 = new PdfPCell(new Phrase("NORMAL RANGE", headFont2));
				hcell01.setBorder(Rectangle.NO_BORDER);
				// hcell1.setPaddingRight(-40f);
				// hcell01.setPaddingTop(50f);

				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell01.setPaddingLeft(85f);
				table3.addCell(hcell01);

				for (RefMeasureDetails refMeasureDetailsInfoPdf : refMeasureDetails) {

					if (refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("BLOOD FOR CPK_MB")) {

						PdfPCell hcell021;
						hcell021 = new PdfPCell(new Phrase("\n" + refMeasureDetailsInfoPdf.getMeasure(), redFont1));
						hcell021.setBorder(Rectangle.NO_BORDER);
						hcell021.setPaddingLeft(-50f);
						hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
						table3.addCell(hcell021);

						if (refMeasureDetailsInfoPdf.getDimension() != null) {
							hcell021 = new PdfPCell(new Phrase("\n : " + refMeasureDetailsInfoPdf.getValue() + " "
									+ refMeasureDetailsInfoPdf.getDimension(), redFont1));
							hcell021.setBorder(Rectangle.NO_BORDER);
							// hcell4.setPaddingRight(5f);
							hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
							hcell021.setPaddingLeft(35f);

							table3.addCell(hcell021);
						} else {
							hcell021 = new PdfPCell(
									new Phrase("\n : " + refMeasureDetailsInfoPdf.getValue(), redFont1));
							hcell021.setBorder(Rectangle.NO_BORDER);
							// hcell4.setPaddingRight(5f);
							hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
							hcell021.setPaddingLeft(35f);

							table3.addCell(hcell021);
						}
						if(refMeasureDetailsInfoPdf.getDimension()!=null)
						{
						hcell021 = new PdfPCell(new Phrase("\n" + refMeasureDetailsInfoPdf.getRange()+" "+refMeasureDetailsInfoPdf.getDimension(), redFont1));
						hcell021.setBorder(Rectangle.NO_BORDER);
						// hcell4.setPaddingRight(5f);

						hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell021.setPaddingLeft(89f);
						table3.addCell(hcell021);
						System.out.println(refMeasureDetailsInfoPdf.getMethod());
						}
						else
						{
							hcell021 = new PdfPCell(new Phrase("\n" + refMeasureDetailsInfoPdf.getRange(), redFont1));
							hcell021.setBorder(Rectangle.NO_BORDER);
							// hcell4.setPaddingRight(5f);

							hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
							hcell021.setPaddingLeft(89f);
							table3.addCell(hcell021);

						}
						if (refMeasureDetailsInfoPdf.getMethod() != null) {
							PdfPCell hcell0211;
							hcell0211 = new PdfPCell(
									new Phrase("method :" + refMeasureDetailsInfoPdf.getMethod(), redFont1));
							hcell0211.setBorder(Rectangle.NO_BORDER);
							hcell0211.setPaddingLeft(-50f);
							hcell0211.setHorizontalAlignment(Element.ALIGN_LEFT);
							table3.addCell(hcell0211);

						}
					}
				}

				document.add(table3);

				Paragraph p01 = new Paragraph();
			
				p01.add("\n");
				p01.add("\n");
				document.add(p01);

				if (patientServiceDetails.getComment().isEmpty()) {
					Paragraph p27 = new Paragraph("\n Findings:", headFont);
					document.add(p27);
					Paragraph p28 = new Paragraph("N/A", redFont2);
					document.add(p28);

				} else {
					Paragraph p27 = new Paragraph("\n Findings:", headFont);

					Paragraph p28 = new Paragraph(patientServiceDetails.getComment(), redFont2);
					document.add(p27);
					document.add(p28);
				}
				Paragraph p7 = new Paragraph("*Please correlate with clinical findings if necessary discuss", redFont2);
				p7.setAlignment(Element.ALIGN_CENTER);

				
				p7.add("\n");
				p7.add("\n");
				document.add(p7);

				Chunk cnd = new Chunk(new VerticalPositionMark());
				Paragraph p19 = new Paragraph(patientRegistration.getPatientDetails().getConsultant(), headFont);
				p19.setAlignment(Element.ALIGN_RIGHT);
				document.add(p19);

				Paragraph p18 = new Paragraph("LAB INCHARGE", headFont);
				p18.add(cnd);

				p18.add("(" + patientRegistration.getVuserD().getDoctorDetails().getSpecilization() + ")");
				document.add(p18);

				// ************************

				document.close();

				pdfBytes = byteArrayOutputStream.toByteArray();
				List<ServicePdf> servicePdfs=servicePdfServiceImpl.findByRegAndMeasureName(regId,patientServiceDetails.getServiceName());
				if(!servicePdfs.isEmpty())
				{
					servicePdf = new ServicePdf();
					servicePdf.setSid(servicePdfs.get(0).getSid());
					servicePdf.setData(pdfBytes);
					servicePdf.setFileName(servicePdfs.get(0).getFileName());
					servicePdf.setFileuri(servicePdfs.get(0).getFileuri());
					servicePdf.setRegId(servicePdfs.get(0).getRegId());
					servicePdfServiceImpl.save(servicePdf);

				}
				else
				{
				String uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/lab/servicePdf/viewFile/")
						.path(servicePdfServiceImpl.getNextLabId()).toUriString();

				servicePdf = new ServicePdf();
				servicePdf.setSid(servicePdfServiceImpl.getNextLabId());
				servicePdf.setData(pdfBytes);
				servicePdf.setFileName(patientServiceDetails.getServiceName());
				servicePdf.setFileuri(uri);
				servicePdf.setRegId(patientServiceDetails.getRegId());
				servicePdfServiceImpl.save(servicePdf);
				}

			} catch (Exception e) {
				Logger.error(e.getMessage());
			}

		}

		else if (patientServiceDetails.getServiceName().equalsIgnoreCase("GLUCOSE TOLERANCE TEST(GTT)")) {

			byte[] pdfBytes = null;
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			Document document = new Document();

			try {
				PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);

				MyFooter event = new MyFooter(principal,userServiceImpl);
				writer.setPageEvent(event);


				Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
				Font redFont3 = new Font(Font.FontFamily.TIMES_ROMAN, 17, Font.BOLD);
				Font redFont2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.ITALIC);
				Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
				Font headFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD|Font.UNDERLINE);
				Font headFont2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD|Font.UNDERLINE);

				
				document.open();

			

				document.add(pq);

				Paragraph p51 = new Paragraph("UDBHAVA HOSPITALS", redFont);
				p51.add("\n \n \n \n");
				p51.setAlignment(Element.ALIGN_CENTER);
				document.add(p51);
			
				Font redFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

				date = Calendar.getInstance().getTime();
				formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
				String today = formatter.format(date).toString();

				
				PdfPTable table2 = new PdfPTable(2);
				
				PdfPCell cell1 = new PdfPCell();
				table2.setWidthPercentage(100f);	
				cell1.setBorder(Rectangle.BOTTOM|Rectangle.TOP);
				
				PdfPTable table21 = new PdfPTable(6);
				table21.setWidths(new float[] { 5f,1f,5f,5f,1f,6.5f});
				 table21.setSpacingBefore(10);
				

				PdfPCell hcell1;
				hcell1 = new PdfPCell(
						new Phrase("Patient Name", redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);
				
				hcell1 = new PdfPCell(
						new Phrase(":", redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);
				
				hcell1 = new PdfPCell(
						new Phrase(patientRegistration.getPatientDetails().getTitle() +". "+ patientRegistration.getPatientDetails().getFirstName()
								+ " " + patientRegistration.getPatientDetails().getLastName(), redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase("Age/Gender", redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase(":", redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);
				
				hcell1 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getAge()
						+ "/" + patientRegistration.getPatientDetails().getGender(), redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				PdfPCell hcell4;
				hcell4 = new PdfPCell(
						new Phrase("Admission No", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(
						new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(
						new Phrase(patientRegistration.getRegId(), redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				if(admittedWard!=null)
				{
				hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(admittedWard, redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				}
				else
				{
					hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					
					hcell4 = new PdfPCell(new Phrase(":", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					hcell4 = new PdfPCell(new Phrase("", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);

				}
				PdfPCell hcell15;
				hcell15 = new PdfPCell(
						new Phrase("Admission Date", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				hcell15 = new PdfPCell(
						new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				hcell15 = new PdfPCell(
						new Phrase(pdfadmissionDate, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);
				if(roomName!=null)
				{
				hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(roomName, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				}
				else
				{
					hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase(":", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase("", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
				}
				PdfPCell hcell16;
				hcell16 = new PdfPCell(new Phrase(
						"Bill No/UMR No", redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(
						":", redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getUmr(), redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);

				hcell16 = new PdfPCell(new Phrase("Bill Date", redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(":", redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(bill, redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);

				PdfPCell hcell17;
				hcell17 = new PdfPCell(new Phrase(
						"Consultant",
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(
						":",
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(
						patientRegistration.getPatientDetails().getConsultant(),
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);

				hcell17 = new PdfPCell(new Phrase("Report Date", redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(":", redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(today, redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				cell1.setColspan(2);
				cell1.addElement(table21);
				table2.addCell(cell1);
             //  table2.addCell(cell1);
               document.add(table2);


               Paragraph p11 = new Paragraph("BIOCHEMISTRY REPORT",
						headFont1);
				p11.setAlignment(Element.ALIGN_CENTER);
				document.add(p11);

				Paragraph p102 = new Paragraph("\n",
						headFont);
				p102.setAlignment(Element.ALIGN_CENTER);
				document.add(p102);

				
				Paragraph p12 = new Paragraph("GLUCOSE TOLERANCE TEST",
						headFont2);
				p12.setAlignment(Element.ALIGN_CENTER);
				document.add(p12);

				PdfPTable table3 = new PdfPTable(3);
				table3.setWidths(new float[] { 6f, 6f, 9f });
				
				
				PdfPCell hcell01;
				hcell01 = new PdfPCell(new Phrase("\n INVESTIGATION ", headFont2));
				hcell01.setBorder(Rectangle.NO_BORDER);
				// hcell01.setPaddingTop(250f);
				hcell01.setPaddingLeft(-50f);
				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);

				table3.addCell(hcell01);

				hcell01 = new PdfPCell(new Phrase("\n RESULT ", headFont2));
				hcell01.setBorder(Rectangle.NO_BORDER);
				// hcell01.setPaddingTop(50f);
				// hcell01.setPaddingLeft(80f);
				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell01.setPaddingLeft(35f);
				table3.addCell(hcell01);

				hcell01 = new PdfPCell(new Phrase("\n NORMAL RANGE ", headFont2));
				hcell01.setBorder(Rectangle.NO_BORDER);
				// hcell1.setPaddingRight(-40f);
				// hcell01.setPaddingTop(50f);

				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell01.setPaddingLeft(85f);
				table3.addCell(hcell01);

				//for measure
				String measure=null;
				
				for (RefMeasureDetails refMeasureDetailsInfoPdf : refMeasureDetails) {

					if (refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("FASTING BLOOD SUGAR")
							|| refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("FASTING BLOOD SUGAR-CORRESPONDING URINE SUGAR")) {

						PdfPCell hcell021;
						
						if(refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("FASTING BLOOD SUGAR-CORRESPONDING URINE SUGAR")) {
							
							measure=refMeasureDetailsInfoPdf.getMeasure().substring(20);
						hcell021 = new PdfPCell(new Phrase("\n" + measure, redFont1));
						hcell021.setBorder(Rectangle.NO_BORDER);
						hcell021.setPaddingLeft(-50f);
						hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
						table3.addCell(hcell021);
						}
						else
						{
							hcell021 = new PdfPCell(new Phrase("\n" + refMeasureDetailsInfoPdf.getMeasure(), redFont1));
							hcell021.setBorder(Rectangle.NO_BORDER);
							hcell021.setPaddingLeft(-50f);
							hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
							table3.addCell(hcell021);
						}

						if (refMeasureDetailsInfoPdf.getDimension() != null) {
							hcell021 = new PdfPCell(new Phrase("\n : " + refMeasureDetailsInfoPdf.getValue() + " "
									+ refMeasureDetailsInfoPdf.getDimension(), redFont1));
							hcell021.setBorder(Rectangle.NO_BORDER);
							hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
							hcell021.setPaddingLeft(30f);
							table3.addCell(hcell021);
						} else {
							hcell021 = new PdfPCell(
									new Phrase("\n : " + refMeasureDetailsInfoPdf.getValue(), redFont1));
							hcell021.setBorder(Rectangle.NO_BORDER);
							hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
							hcell021.setPaddingLeft(30f);
							table3.addCell(hcell021);
						}
						if(refMeasureDetailsInfoPdf.getDimension()!=null)
						{
							hcell021 = new PdfPCell(new Phrase("\n" + refMeasureDetailsInfoPdf.getRange()+" "+refMeasureDetailsInfoPdf.getDimension(), redFont1));
						hcell021.setBorder(Rectangle.NO_BORDER);
						hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell021.setPaddingLeft(89f);
						table3.addCell(hcell021);
						}
						else
						{
							hcell021 = new PdfPCell(new Phrase("\n" + refMeasureDetailsInfoPdf.getRange(), redFont1));
							hcell021.setBorder(Rectangle.NO_BORDER);
							hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
							hcell021.setPaddingLeft(89f);
							table3.addCell(hcell021);	
						}
						System.out.println(refMeasureDetailsInfoPdf.getMethod());
						
					}

				}
				document.add(table3);
				
				Paragraph p03=new Paragraph("\n Glucose After Orally Given",headFont); 
				p03.add("\n \n");
				document.add(p03);
				
				PdfPTable table31 = new PdfPTable(3);
				table31.setWidths(new float[] { 6f, 6f, 9f });
				
				
				for (RefMeasureDetails refMeasureDetailsInfoPdf1 : refMeasureDetails) {
					if (refMeasureDetailsInfoPdf1.getMeasure().equalsIgnoreCase("1st HOUR BLOOD SUGAR")
							|| refMeasureDetailsInfoPdf1.getMeasure().equalsIgnoreCase("2nd HOUR BLOOD GLOCOUSE")
							|| refMeasureDetailsInfoPdf1.getMeasure().equalsIgnoreCase("3rd HOUR BLOOD GLOCOUSE")
							|| refMeasureDetailsInfoPdf1.getMeasure().equalsIgnoreCase("CORRESPONDING URINE SUGAR")) {

						PdfPCell hcell021;
						hcell021 = new PdfPCell(new Phrase("\n" + refMeasureDetailsInfoPdf1.getMeasure(), redFont1));
						hcell021.setBorder(Rectangle.NO_BORDER);
						hcell021.setPaddingLeft(-50f);
						hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
						table31.addCell(hcell021);

						if (refMeasureDetailsInfoPdf1.getDimension() != null) {
							hcell021 = new PdfPCell(new Phrase("\n : " + refMeasureDetailsInfoPdf1.getValue() + " "
									+ refMeasureDetailsInfoPdf1.getDimension(), redFont1));
							hcell021.setBorder(Rectangle.NO_BORDER);
							hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
							hcell021.setPaddingLeft(30f);
							table31.addCell(hcell021);
						} else {
							hcell021 = new PdfPCell(
									new Phrase("\n : " + refMeasureDetailsInfoPdf1.getValue(), redFont1));
							hcell021.setBorder(Rectangle.NO_BORDER);
							hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
							hcell021.setPaddingLeft(30f);
							table31.addCell(hcell021);
						}
						if(refMeasureDetailsInfoPdf1.getDimension()!=null)
						{
						hcell021 = new PdfPCell(new Phrase("\n" + refMeasureDetailsInfoPdf1.getRange()+" "+refMeasureDetailsInfoPdf1.getDimension(), redFont1));
						hcell021.setBorder(Rectangle.NO_BORDER);
						hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell021.setPaddingLeft(89f);
						table31.addCell(hcell021);
						}
						else
						{
							hcell021 = new PdfPCell(new Phrase("\n" + refMeasureDetailsInfoPdf1.getRange(), redFont1));
							hcell021.setBorder(Rectangle.NO_BORDER);
							hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
							hcell021.setPaddingLeft(89f);
							table31.addCell(hcell021);

						}
						System.out.println(refMeasureDetailsInfoPdf1.getMethod());
						
					}

				}

				document.add(table31);

				Paragraph p01 = new Paragraph();

				p01.add("\n");
				p01.add("\n");

				document.add(p01);

				if (patientServiceDetails.getComment().isEmpty()) {
					Paragraph p27 = new Paragraph("\n Findings:", headFont);
					document.add(p27);
					Paragraph p28 = new Paragraph("N/A", redFont2);
					document.add(p28);

				} else {
					Paragraph p27 = new Paragraph("\n Findings:", headFont);

					Paragraph p28 = new Paragraph(patientServiceDetails.getComment(), redFont1);
					document.add(p27);
					document.add(p28);
				}

				Paragraph p7 = new Paragraph("*Please correlate with clinical findings if necessary discuss", redFont2);
				p7.setAlignment(Element.ALIGN_CENTER);

				p7.add("\n");
				p7.add("\n");
				document.add(p7);

				Chunk cnd = new Chunk(new VerticalPositionMark());
				Paragraph p19 = new Paragraph(patientRegistration.getPatientDetails().getConsultant(), headFont);
				p19.setAlignment(Element.ALIGN_RIGHT);
				document.add(p19);

				Paragraph p18 = new Paragraph("LAB INCHARGE", headFont);
				p18.add(cnd);

				p18.add("(" + patientRegistration.getVuserD().getDoctorDetails().getSpecilization() + ")");
				document.add(p18);

				// ************************

				document.close();

				pdfBytes = byteArrayOutputStream.toByteArray();
				List<ServicePdf> servicePdfs=servicePdfServiceImpl.findByRegAndMeasureName(regId,patientServiceDetails.getServiceName());
				if(!servicePdfs.isEmpty())
				{
					servicePdf = new ServicePdf();
					servicePdf.setSid(servicePdfs.get(0).getSid());
					servicePdf.setData(pdfBytes);
					servicePdf.setFileName(servicePdfs.get(0).getFileName());
					servicePdf.setFileuri(servicePdfs.get(0).getFileuri());
					servicePdf.setRegId(servicePdfs.get(0).getRegId());
					servicePdfServiceImpl.save(servicePdf);

				}
				else
				{
				String uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/lab/servicePdf/viewFile/")
						.path(servicePdfServiceImpl.getNextLabId()).toUriString();

				servicePdf = new ServicePdf();
				servicePdf.setSid(servicePdfServiceImpl.getNextLabId());
				servicePdf.setData(pdfBytes);
				servicePdf.setFileName(patientServiceDetails.getServiceName());
				servicePdf.setFileuri(uri);
				servicePdf.setRegId(patientServiceDetails.getRegId());
				servicePdfServiceImpl.save(servicePdf);
				}

			} catch (Exception e) {
				Logger.error(e.getMessage());
			}

		}
		else if (patientServiceDetails.getServiceName().equalsIgnoreCase("STOOL FOR REDUCING SUBSTANCES")) {

			byte[] pdfBytes = null;
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			Document document = new Document();

			try {
				PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);

				MyFooter event = new MyFooter(principal,userServiceImpl);
				writer.setPageEvent(event);


				Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
				Font redFont3 = new Font(Font.FontFamily.TIMES_ROMAN, 17, Font.BOLD);
				Font redFont2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.ITALIC);
				Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
				Font headFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD|Font.UNDERLINE);
				Font headFont2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD|Font.UNDERLINE);

				document.open();


				document.add(pq);

				Paragraph p51 = new Paragraph("UDBHAVA HOSPITALS", redFont);
				p51.add("\n \n \n \n");
				p51.setAlignment(Element.ALIGN_CENTER);
				document.add(p51);
				
				Font redFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

				date = Calendar.getInstance().getTime();
				formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
				String today = formatter.format(date).toString();
				PdfPTable table2 = new PdfPTable(2);
				
				PdfPCell cell1 = new PdfPCell();
				table2.setWidthPercentage(100f);	
				cell1.setBorder(Rectangle.BOTTOM|Rectangle.TOP);
				
				PdfPTable table21 = new PdfPTable(6);
				table21.setWidths(new float[] { 5f,1f,5f,5f,1f,6.5f});
				 table21.setSpacingBefore(10);
				

				PdfPCell hcell1;
				hcell1 = new PdfPCell(
						new Phrase("Patient Name", redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);
				
				hcell1 = new PdfPCell(
						new Phrase(":", redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);
				
				hcell1 = new PdfPCell(
						new Phrase(patientRegistration.getPatientDetails().getTitle() +". "+ patientRegistration.getPatientDetails().getFirstName()
								+ " " + patientRegistration.getPatientDetails().getLastName(), redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase("Age/Gender", redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase(":", redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);
				
				hcell1 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getAge()
						+ "/" + patientRegistration.getPatientDetails().getGender(), redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				PdfPCell hcell4;
				hcell4 = new PdfPCell(
						new Phrase("Admission No", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(
						new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(
						new Phrase(patientRegistration.getRegId(), redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				if(admittedWard!=null)
				{
				hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(admittedWard, redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				}
				else
				{
					hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					
					hcell4 = new PdfPCell(new Phrase(":", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					hcell4 = new PdfPCell(new Phrase("", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);

				}
				PdfPCell hcell15;
				hcell15 = new PdfPCell(
						new Phrase("Admission Date", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				hcell15 = new PdfPCell(
						new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				hcell15 = new PdfPCell(
						new Phrase(pdfadmissionDate, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);
				if(roomName!=null)
				{
				hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(roomName, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				}
				else
				{
					hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase(":", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase("", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
				}
				PdfPCell hcell16;
				hcell16 = new PdfPCell(new Phrase(
						"Bill No/UMR No", redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(
						":", redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getUmr(), redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);

				hcell16 = new PdfPCell(new Phrase("Bill Date", redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(":", redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(bill, redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);

				PdfPCell hcell17;
				hcell17 = new PdfPCell(new Phrase(
						"Consultant",
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(
						":",
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(
						patientRegistration.getPatientDetails().getConsultant(),
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);

				hcell17 = new PdfPCell(new Phrase("Report Date", redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(":", redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(today, redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				cell1.setColspan(2);
				cell1.addElement(table21);
				table2.addCell(cell1);
             //  table2.addCell(cell1);
               document.add(table2);

				Paragraph p11 = new Paragraph("DEPARTMENT OF CLINICAL PATHOLOGY", headFont1);
				p11.setAlignment(Element.ALIGN_CENTER);
				document.add(p11);

				PdfPTable table3 = new PdfPTable(2);
				table3.setWidths(new float[] { 7f, 6f });

				PdfPCell hcell01;
				hcell01 = new PdfPCell(new Phrase("\n INVESTIGATION", headFont2));
				hcell01.setBorder(Rectangle.NO_BORDER);
				// hcell01.setPaddingTop(250f);
				hcell01.setPaddingLeft(-50f);
				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);

				table3.addCell(hcell01);

				hcell01 = new PdfPCell(new Phrase("\n RESULT", headFont2));
				hcell01.setBorder(Rectangle.NO_BORDER);
				hcell01.setPaddingRight(-40f);
				// hcell01.setPaddingTop(50f);
				// hcell01.setPaddingLeft(80f);
				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
				table3.addCell(hcell01);

				

				for (RefMeasureDetails refMeasureDetailsInfoPdf : refMeasureDetails) {

					if (refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("STOOL FOR REDUCING SUBSTANCES")) {

						PdfPCell hcell021;
						hcell021 = new PdfPCell(new Phrase("\n" + refMeasureDetailsInfoPdf.getMeasure(), redFont1));
						hcell021.setBorder(Rectangle.NO_BORDER);
						hcell021.setPaddingLeft(-50f);
						hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
						table3.addCell(hcell021);

						if (refMeasureDetailsInfoPdf.getDimension() != null) {
							hcell021 = new PdfPCell(new Phrase("\n : " + refMeasureDetailsInfoPdf.getValue() + " "
									+ refMeasureDetailsInfoPdf.getDimension(), redFont1));
							hcell021.setBorder(Rectangle.NO_BORDER);
							// hcell4.setPaddingRight(5f);
							hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
							hcell021.setPaddingLeft(0f);

							table3.addCell(hcell021);
						} else {
							hcell021 = new PdfPCell(
									new Phrase("\n : " + refMeasureDetailsInfoPdf.getValue(), redFont1));
							hcell021.setBorder(Rectangle.NO_BORDER);
							// hcell4.setPaddingRight(5f);
							hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
							hcell021.setPaddingLeft(0f);

							table3.addCell(hcell021);
						}
						
						
					}
				}

				document.add(table3);

				Paragraph p01 = new Paragraph();
				
				p01.add("\n");
				p01.add("\n");
				document.add(p01);

				if (patientServiceDetails.getComment().isEmpty()) {
					Paragraph p27 = new Paragraph("\n Findings:", headFont);
					document.add(p27);
					Paragraph p28 = new Paragraph("N/A", redFont2);
					document.add(p28);

				} else {
					Paragraph p27 = new Paragraph("\n Findings:", headFont);

					Paragraph p28 = new Paragraph(patientServiceDetails.getComment(), redFont2);
					document.add(p27);
					document.add(p28);
				}
				Paragraph p7 = new Paragraph("*Please correlate with clinical findings if necessary discuss", redFont2);
				p7.setAlignment(Element.ALIGN_CENTER);

				p7.add("\n");
				p7.add("\n");
				document.add(p7);

				Chunk cnd = new Chunk(new VerticalPositionMark());
				Paragraph p19 = new Paragraph(patientRegistration.getPatientDetails().getConsultant(), headFont);
				p19.setAlignment(Element.ALIGN_RIGHT);
				document.add(p19);

				Paragraph p18 = new Paragraph("LAB INCHARGE", headFont);
				p18.add(cnd);

				p18.add("(" + patientRegistration.getVuserD().getDoctorDetails().getSpecilization() + ")");
				document.add(p18);

				document.close();

				pdfBytes = byteArrayOutputStream.toByteArray();
				List<ServicePdf> servicePdfs=servicePdfServiceImpl.findByRegAndMeasureName(regId,patientServiceDetails.getServiceName());
				if(!servicePdfs.isEmpty())
				{
					servicePdf = new ServicePdf();
					servicePdf.setSid(servicePdfs.get(0).getSid());
					servicePdf.setData(pdfBytes);
					servicePdf.setFileName(servicePdfs.get(0).getFileName());
					servicePdf.setFileuri(servicePdfs.get(0).getFileuri());
					servicePdf.setRegId(servicePdfs.get(0).getRegId());
					servicePdfServiceImpl.save(servicePdf);

				}
				else
				{
				String uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/lab/servicePdf/viewFile/")
						.path(servicePdfServiceImpl.getNextLabId()).toUriString();

				servicePdf = new ServicePdf();
				servicePdf.setSid(servicePdfServiceImpl.getNextLabId());
				servicePdf.setData(pdfBytes);
				servicePdf.setFileName(patientServiceDetails.getServiceName());
				servicePdf.setFileuri(uri);
				servicePdf.setRegId(patientServiceDetails.getRegId());
				servicePdfServiceImpl.save(servicePdf);
				}


			} catch (Exception e) {
				Logger.error(e.getMessage());
			}

		}

		else if (patientServiceDetails.getServiceName().equalsIgnoreCase("VDRL")) {

			byte[] pdfBytes = null;
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			Document document = new Document();

			try {
				PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);

				MyFooter event = new MyFooter(principal,userServiceImpl);
				writer.setPageEvent(event);


				Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
				Font redFont3 = new Font(Font.FontFamily.TIMES_ROMAN, 17, Font.BOLD);
				Font redFont2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.ITALIC);
				Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
				Font headFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD|Font.UNDERLINE);
				Font headFont2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD|Font.UNDERLINE);

				document.open();

				

				document.add(pq);

				Paragraph p51 = new Paragraph("UDBHAVA HOSPITALS", redFont);
				p51.add("\n \n \n \n");
				p51.setAlignment(Element.ALIGN_CENTER);
				document.add(p51);
				
				Font redFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

				date = Calendar.getInstance().getTime();
				formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
				String today = formatter.format(date).toString();


				PdfPTable table2 = new PdfPTable(2);
				
				PdfPCell cell1 = new PdfPCell();
				table2.setWidthPercentage(100f);	
				cell1.setBorder(Rectangle.BOTTOM|Rectangle.TOP);
				
				PdfPTable table21 = new PdfPTable(6);
				table21.setWidths(new float[] { 5f,1f,5f,5f,1f,6.5f});
				 table21.setSpacingBefore(10);
				

				PdfPCell hcell1;
				hcell1 = new PdfPCell(
						new Phrase("Patient Name", redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);
				
				hcell1 = new PdfPCell(
						new Phrase(":", redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);
				
				hcell1 = new PdfPCell(
						new Phrase(patientRegistration.getPatientDetails().getTitle() +". "+ patientRegistration.getPatientDetails().getFirstName()
								+ " " + patientRegistration.getPatientDetails().getLastName(), redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase("Age/Gender", redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase(":", redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);
				
				hcell1 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getAge()
						+ "/" + patientRegistration.getPatientDetails().getGender(), redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				PdfPCell hcell4;
				hcell4 = new PdfPCell(
						new Phrase("Admission No", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(
						new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(
						new Phrase(patientRegistration.getRegId(), redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				if(admittedWard!=null)
				{
				hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(admittedWard, redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				}
				else
				{
					hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					
					hcell4 = new PdfPCell(new Phrase(":", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					hcell4 = new PdfPCell(new Phrase("", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);

				}
				PdfPCell hcell15;
				hcell15 = new PdfPCell(
						new Phrase("Admission Date", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				hcell15 = new PdfPCell(
						new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				hcell15 = new PdfPCell(
						new Phrase(pdfadmissionDate, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);
				if(roomName!=null)
				{
				hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(roomName, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				}
				else
				{
					hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase(":", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase("", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
				}
				PdfPCell hcell16;
				hcell16 = new PdfPCell(new Phrase(
						"Bill No/UMR No", redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(
						":", redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getUmr(), redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);

				hcell16 = new PdfPCell(new Phrase("Bill Date", redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(":", redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(bill, redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);

				PdfPCell hcell17;
				hcell17 = new PdfPCell(new Phrase(
						"Consultant",
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(
						":",
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(
						patientRegistration.getPatientDetails().getConsultant(),
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);

				hcell17 = new PdfPCell(new Phrase("Report Date", redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(":", redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(today, redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				cell1.setColspan(2);
				cell1.addElement(table21);
				table2.addCell(cell1);
             //  table2.addCell(cell1);
               document.add(table2);


               Paragraph p11 = new Paragraph("SEROLOGY REPORT",headFont1);
				p11.setAlignment(Element.ALIGN_CENTER);
				document.add(p11);

				PdfPTable table3 = new PdfPTable(2);
				table3.setWidths(new float[] { 5f, 6f });

				PdfPCell hcell01;
				hcell01 = new PdfPCell(new Phrase("\n TEST", headFont2));
				hcell01.setBorder(Rectangle.NO_BORDER);
				hcell01.setPaddingLeft(-40f);
				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
				table3.addCell(hcell01);

				hcell01 = new PdfPCell(new Phrase("\n RESULT", headFont2));
				hcell01.setBorder(Rectangle.NO_BORDER);
				hcell01.setPaddingRight(-40f);
				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
				table3.addCell(hcell01);

				for (RefMeasureDetails refMeasureDetailsInfoPdf : refMeasureDetails) {

					if (refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("VDRL")) {
						PdfPCell hcell021;
						hcell021 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getMeasure(), redFont1));
						hcell021.setBorder(Rectangle.NO_BORDER);
						hcell021.setPaddingLeft(-40f);
						hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
						table3.addCell(hcell021);

						if (refMeasureDetailsInfoPdf.getDimension() != null) {
							hcell021 = new PdfPCell(new Phrase(": " + refMeasureDetailsInfoPdf.getValue() + " "
									+ refMeasureDetailsInfoPdf.getDimension(), redFont1));
							hcell021.setBorder(Rectangle.NO_BORDER);
							hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
							// hcell021.setPaddingLeft(30f);

							table3.addCell(hcell021);
						} else {
							hcell021 = new PdfPCell(new Phrase(": " + refMeasureDetailsInfoPdf.getValue(), redFont1));
							hcell021.setBorder(Rectangle.NO_BORDER);
							hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
							// hcell021.setPaddingLeft(45f);
							table3.addCell(hcell021);
						}
						
						
						PdfPCell hcell0211;
						hcell0211 = new PdfPCell(new Phrase("\n ("+refMeasureDetailsInfoPdf.getMethod()+")", redFont1));
						hcell0211.setBorder(Rectangle.NO_BORDER);
						//hcell0211.setPaddingRight(-40f);
						hcell0211.setPaddingLeft(-40f);
						hcell0211.setHorizontalAlignment(Element.ALIGN_LEFT);
						table3.addCell(hcell0211);
						
						
						hcell0211 = new PdfPCell(new Phrase("", redFont1));
						hcell0211.setBorder(Rectangle.NO_BORDER);
						//hcell0211.setPaddingRight(-40f);
						hcell0211.setHorizontalAlignment(Element.ALIGN_LEFT);
						table3.addCell(hcell0211);
						System.out.println(refMeasureDetailsInfoPdf.getMethod());

						System.out.println(refMeasureDetailsInfoPdf.getMethod());

					}
				}
				document.add(table3);

				Paragraph p01 = new Paragraph();

				p01.add("\n");
				p01.add("\n");
				document.add(p01);

				if (patientServiceDetails.getComment().isEmpty()) {
					Paragraph p27 = new Paragraph("\n Findings:", headFont);
					document.add(p27);
					Paragraph p28 = new Paragraph("N/A", redFont2);
					document.add(p28);

				} else {
					Paragraph p27 = new Paragraph("\n Findings:", headFont);

					Paragraph p28 = new Paragraph(patientServiceDetails.getComment(), redFont1);
					document.add(p27);
					document.add(p28);
				}

				Paragraph p7 = new Paragraph("*Suggested clinical correlation, if necessary kindly discuss", redFont2);
				p7.setAlignment(Element.ALIGN_CENTER);

				
				p7.add("\n");
				p7.add("\n");
				document.add(p7);

				Chunk cnd = new Chunk(new VerticalPositionMark());
				Paragraph p19 = new Paragraph(patientRegistration.getPatientDetails().getConsultant(), headFont);
				p19.setAlignment(Element.ALIGN_RIGHT);
				document.add(p19);

				Paragraph p18 = new Paragraph("LAB INCHARGE", headFont);
				p18.add(cnd);

				p18.add("(" + patientRegistration.getVuserD().getDoctorDetails().getSpecilization() + ")");
				document.add(p18);

				document.close();

				pdfBytes = byteArrayOutputStream.toByteArray();
				List<ServicePdf> servicePdfs=servicePdfServiceImpl.findByRegAndMeasureName(regId,patientServiceDetails.getServiceName());
				if(!servicePdfs.isEmpty())
				{
					servicePdf = new ServicePdf();
					servicePdf.setSid(servicePdfs.get(0).getSid());
					servicePdf.setData(pdfBytes);
					servicePdf.setFileName(servicePdfs.get(0).getFileName());
					servicePdf.setFileuri(servicePdfs.get(0).getFileuri());
					servicePdf.setRegId(servicePdfs.get(0).getRegId());
					servicePdfServiceImpl.save(servicePdf);

				}
				else
				{
				String uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/lab/servicePdf/viewFile/")
						.path(servicePdfServiceImpl.getNextLabId()).toUriString();

				servicePdf = new ServicePdf();
				servicePdf.setSid(servicePdfServiceImpl.getNextLabId());
				servicePdf.setData(pdfBytes);
				servicePdf.setFileName(patientServiceDetails.getServiceName());
				servicePdf.setFileuri(uri);
				servicePdf.setRegId(patientServiceDetails.getRegId());
				servicePdfServiceImpl.save(servicePdf);
				}

			} catch (Exception e) {
				Logger.error(e.getMessage());
			}

		}

		else if (patientServiceDetails.getServiceName().equalsIgnoreCase("HCV")) {

			byte[] pdfBytes = null;
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			Document document = new Document();

			try {
				PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);

				MyFooter event = new MyFooter(principal,userServiceImpl);
				writer.setPageEvent(event);


				Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
				Font redFont3 = new Font(Font.FontFamily.TIMES_ROMAN, 17, Font.BOLD);
				Font redFont2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.ITALIC);
				Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);

				document.open();

				

				document.add(pq);

				Paragraph p51 = new Paragraph("UDBHAVA HOSPITALS", redFont);
				p51.setAlignment(Element.ALIGN_CENTER);
				document.add(p51);
				

				PdfPTable table2 = new PdfPTable(2);
				table2.setWidths(new float[] { 7f, 7f });
				Font redFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

				date = Calendar.getInstance().getTime();
				formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
				String today = formatter.format(date).toString();

				PdfPCell cell1 = new PdfPCell();
				table2.setWidthPercentage(100f);	
				cell1.setBorder(Rectangle.BOTTOM|Rectangle.TOP);
				
				PdfPTable table21 = new PdfPTable(6);
				table21.setWidths(new float[] { 5f,1f,5f,5f,1f,6.5f});
				 table21.setSpacingBefore(10);
				

				PdfPCell hcell1;
				hcell1 = new PdfPCell(
						new Phrase("Patient Name", redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);
				
				hcell1 = new PdfPCell(
						new Phrase(":", redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);
				
				hcell1 = new PdfPCell(
						new Phrase(patientRegistration.getPatientDetails().getTitle() +". "+ patientRegistration.getPatientDetails().getFirstName()
								+ " " + patientRegistration.getPatientDetails().getLastName(), redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase("Age/Gender", redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase(":", redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);
				
				hcell1 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getAge()
						+ "/" + patientRegistration.getPatientDetails().getGender(), redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				PdfPCell hcell4;
				hcell4 = new PdfPCell(
						new Phrase("Admission No", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(
						new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(
						new Phrase(patientRegistration.getRegId(), redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				if(admittedWard!=null)
				{
				hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(admittedWard, redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				}
				else
				{
					hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					
					hcell4 = new PdfPCell(new Phrase(":", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					hcell4 = new PdfPCell(new Phrase("", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);

				}
				PdfPCell hcell15;
				hcell15 = new PdfPCell(
						new Phrase("Admission Date", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				hcell15 = new PdfPCell(
						new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				hcell15 = new PdfPCell(
						new Phrase(pdfadmissionDate, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);
				if(roomName!=null)
				{
				hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(roomName, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				}
				else
				{
					hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase(":", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase("", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
				}
				PdfPCell hcell16;
				hcell16 = new PdfPCell(new Phrase(
						"Bill No/UMR No", redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(
						":", redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getUmr(), redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);

				hcell16 = new PdfPCell(new Phrase("Bill Date", redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(":", redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(bill, redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);

				PdfPCell hcell17;
				hcell17 = new PdfPCell(new Phrase(
						"Consultant",
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(
						":",
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(
						patientRegistration.getPatientDetails().getConsultant(),
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);

				hcell17 = new PdfPCell(new Phrase("Report Date", redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(":", redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(today, redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				cell1.setColspan(2);
				cell1.addElement(table21);
				table2.addCell(cell1);
             //  table2.addCell(cell1);
               document.add(table2);


				
				Paragraph p11 = new Paragraph("SEROLOGY REPORT");
				p11.setAlignment(Element.ALIGN_CENTER);
				p11.add("\n");
				p11.add("\n");
				document.add(p11);

				PdfPTable table3 = new PdfPTable(3);
				table3.setWidths(new float[] { 5f, 6f, 6f });

				PdfPCell hcell01;
				hcell01 = new PdfPCell(new Phrase("TEST", headFont));
				hcell01.setBorder(Rectangle.NO_BORDER);
				hcell01.setPaddingLeft(-50f);
				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
				table3.addCell(hcell01);

				hcell01 = new PdfPCell(new Phrase("RESULT", headFont));
				hcell01.setBorder(Rectangle.NO_BORDER);
				hcell01.setPaddingRight(-40f);
				hcell01.setHorizontalAlignment(Element.ALIGN_CENTER);
				table3.addCell(hcell01);

				hcell01 = new PdfPCell(new Phrase("Method", headFont));
				hcell01.setBorder(Rectangle.NO_BORDER);
				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell01.setPaddingLeft(85f);
				table3.addCell(hcell01);

				for (RefMeasureDetails refMeasureDetailsInfoPdf : refMeasureDetails) {

					if (refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("HCV")) {

						PdfPCell hcell021;
						hcell021 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getMeasure(), redFont1));
						hcell021.setBorder(Rectangle.NO_BORDER);
						hcell021.setPaddingLeft(-50f);
						hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
						table3.addCell(hcell021);

						if (refMeasureDetailsInfoPdf.getDimension() != null) {
							hcell021 = new PdfPCell(new Phrase(": " + refMeasureDetailsInfoPdf.getValue() + " "
									+ refMeasureDetailsInfoPdf.getDimension(), redFont1));
							hcell021.setBorder(Rectangle.NO_BORDER);
							hcell021.setHorizontalAlignment(Element.ALIGN_CENTER);
							hcell021.setPaddingLeft(30f);

							table3.addCell(hcell021);
						} else {
							hcell021 = new PdfPCell(new Phrase(": " + refMeasureDetailsInfoPdf.getValue(), redFont1));
							hcell021.setBorder(Rectangle.NO_BORDER);
							hcell021.setHorizontalAlignment(Element.ALIGN_CENTER);
							hcell021.setPaddingLeft(45f);
							table3.addCell(hcell021);
						}

						System.out.println(refMeasureDetailsInfoPdf.getMethod());
						if (refMeasureDetailsInfoPdf.getMethod() != null) {
							PdfPCell hcell0211;
							hcell0211 = new PdfPCell(
									new Phrase("method :" + refMeasureDetailsInfoPdf.getMethod(), redFont1));
							hcell0211.setBorder(Rectangle.NO_BORDER);
							hcell0211.setPaddingLeft(-50f);
							hcell0211.setHorizontalAlignment(Element.ALIGN_LEFT);
							table3.addCell(hcell0211);
							
							hcell0211 = new PdfPCell(
									new Phrase("method :" + refMeasureDetailsInfoPdf.getMethod(), redFont1));
							hcell0211.setBorder(Rectangle.NO_BORDER);
							hcell0211.setPaddingLeft(-50f);
							hcell0211.setHorizontalAlignment(Element.ALIGN_LEFT);
							table3.addCell(hcell0211);
							

						}
					}
				}

				document.add(table3);

				Paragraph p01 = new Paragraph();
				
				p01.add("\n");
				p01.add("\n");
				document.add(p01);

				if (patientServiceDetails.getComment().isEmpty()) {
					Paragraph p27 = new Paragraph();
					p27.add("\n findings:" + "\n" + "N/A");
					document.add(p27);
				} else {
					Paragraph p27 = new Paragraph();
					p27.add("\n findings:" + "\n" + patientServiceDetails.getComment());
					document.add(p27);
				}

				Paragraph p7 = new Paragraph("*Suggested clinical correlation, if necessary kindly discuss", redFont2);
				p7.setAlignment(Element.ALIGN_CENTER);

				p7.add("\n");
				p7.add("\n");
				document.add(p7);

				Chunk cnd = new Chunk(new VerticalPositionMark());
				Paragraph p19 = new Paragraph(patientRegistration.getPatientDetails().getConsultant(), headFont);
				p19.setAlignment(Element.ALIGN_RIGHT);
				document.add(p19);

				Paragraph p18 = new Paragraph("LAB INCHARGE", headFont);
				p18.add(cnd);

				p18.add("(" + patientRegistration.getVuserD().getDoctorDetails().getSpecilization() + ")");
				document.add(p18);

				document.close();

				pdfBytes = byteArrayOutputStream.toByteArray();
				List<ServicePdf> servicePdfs=servicePdfServiceImpl.findByRegAndMeasureName(regId,patientServiceDetails.getServiceName());
				if(!servicePdfs.isEmpty())
				{
					servicePdf = new ServicePdf();
					servicePdf.setSid(servicePdfs.get(0).getSid());
					servicePdf.setData(pdfBytes);
					servicePdf.setFileName(servicePdfs.get(0).getFileName());
					servicePdf.setFileuri(servicePdfs.get(0).getFileuri());
					servicePdf.setRegId(servicePdfs.get(0).getRegId());
					servicePdfServiceImpl.save(servicePdf);

				}
				else
				{
				String uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/lab/servicePdf/viewFile/")
						.path(servicePdfServiceImpl.getNextLabId()).toUriString();

				servicePdf = new ServicePdf();
				servicePdf.setSid(servicePdfServiceImpl.getNextLabId());
				servicePdf.setData(pdfBytes);
				servicePdf.setFileName(patientServiceDetails.getServiceName());
				servicePdf.setFileuri(uri);
				servicePdf.setRegId(patientServiceDetails.getRegId());
				servicePdfServiceImpl.save(servicePdf);
				}


			} catch (Exception e) {
				Logger.error(e.getMessage());
			}

		}
		
		
		return servicePdf;
	}

	
	

	public ServicePdf saveInfoOthers(PatientServiceDetails patientServiceDetails,Principal principal)
	{
		String regId = null;

		List<LabServiceRange> labServiceRange = null;
		
		PatientRegistration patientRegistration = patientRegistrationServiceImpl
				.findByRegId(patientServiceDetails.getRegId());
		regId = patientServiceDetails.getRegId();
		patientServiceDetails.setPatientService(patientRegistration);
		System.out.println(patientServiceDetails.getServiceName());
		System.out.println(patientServiceDetails.getRegId());
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
		LabServices labServices = labServicesServiceImpl.findPriceByType(patientServiceDetails.getServiceName(),
				patientRegistration.getpType(),
				roomType);
		if (labServices == null) {
			System.out.println("ITS NULL");
		} else {
			System.out.println("ITS NOT NULL " + labServices.getServiceId());
		}

		patientServiceDetails.setPatientLabService(labServices);
		patientServiceDetails.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));

		// createdBy (Security)
		User userSecurity = userServiceImpl.findByUserName(principal.getName());
		String createdBy = userSecurity.getFirstName() + " " + userSecurity.getLastName();
		patientServiceDetails.setCreatedBy(createdBy);
		
		//for admission date
				String admisionDate =patientRegistration.getRegDate().toString();
			Timestamp timestamp = Timestamp.valueOf(admisionDate);
			DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa ");

			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(timestamp.getTime());

			String pdfadmissionDate = dateFormat.format(calendar.getTime());

		// for pdf
		List<LaboratoryRegistration> laboratoryRegistrationsInfo = null;
		List<RefMeasureDetails> refMeasureDetails = patientServiceDetails.getRefMeasureDetails();

		if (!refMeasureDetails.isEmpty()) {
			for (RefMeasureDetails refMeasureDetailsInfo : refMeasureDetails) {
				patientServiceDetails.setPatServiceId(patientServiceDetailsServiceImpl.getNextId());
				List<LabServices> li = new ArrayList<>();
				li.add(labServices);
				String newAge = patientRegistration.getPatientDetails().getAge();
				String ageType = null;
				int age = 0;
				LocalDate today = LocalDate.now();
				LocalDate birthday = patientRegistration.getPatientDetails().getDob().toLocalDateTime().toLocalDate();
				Period p = Period.between(birthday, today);
				System.out.println("Days" + p.getDays() + "months" + p.getMonths() + "years" + p.getYears());
				// System.out.println("service
				// id"+labServices.get(0).getServiceId());

				// String
				// newAge=patientRegistration.getPatientDetails().getAge();
				String gender = null;

				if (patientServiceDetails.getServiceName().equalsIgnoreCase("LFT - LIVER FUNCTON TESTS")
						|| patientServiceDetails.getServiceName().equalsIgnoreCase("SERUM BILIRUBIN")) {

					if (p.getDays() >= 0 && p.getMonths() == 0 && p.getYears() == 0) {

						age = p.getDays();
						ageType = "days";
						gender = "CHILDREN";
					} else if (p.getDays() >= 0 && p.getMonths() > 0 && p.getYears() >= 0) {

						age = p.getMonths();
						ageType = "months";
						gender = patientRegistration.getPatientDetails().getGender();
					} 

					laboratoryRegistrationsInfo = patientRegistration.getLaboratoryRegistration();

					labServiceRange = labServicesServiceImpl.findNewMeasures(li, age, gender, ageType);

				} else if (patientServiceDetails.getServiceName().equalsIgnoreCase("COMPLETE BLOOD PICTURE")) {
					if (p.getDays() >= 0 && p.getMonths() == 0 && p.getYears() == 0) {

						age = p.getDays();
						ageType = "days";
						gender = "CHILDREN";
					} else if (p.getDays() >= 0 && p.getMonths() >= 0 && p.getYears() < 2) {

						age = p.getMonths();
						ageType = "months";
						gender = "CHILDREN";
					} else if (p.getDays() >= 0 && p.getMonths() >= 0 && p.getYears() > 2 && p.getYears() < 13) {

						age = p.getYears();
						ageType = "years";
						gender = "CHILDREN";
					} else if (p.getDays() >= 0 && p.getMonths() >= 0 && p.getYears() > 13) {
						age = p.getYears();
						ageType = "years";
						gender = patientRegistration.getPatientDetails().getGender();

					}

					laboratoryRegistrationsInfo = patientRegistration.getLaboratoryRegistration();

					labServiceRange = labServicesServiceImpl.findNewMeasures(li, age, gender, ageType);

				} else {

					newAge = patientRegistration.getPatientDetails().getAge();

					float age1 = p.getYears();
					int labAge = 0;
					gender = null;

					if (age1 <= 12) {
						labAge = (int) age1;
						gender = "CHILDREN";
					} else

					{
						labAge = (int) age1;
						gender = patientRegistration.getPatientDetails().getGender();

					}

					laboratoryRegistrationsInfo = patientRegistration.getLaboratoryRegistration();

					labServiceRange = labServicesServiceImpl.findMeasures(li, labAge, gender);

				}

				for (LabServiceRange labServiceRangeInfo : labServiceRange) {

					if (labServiceRangeInfo.getMeasureName().equals(refMeasureDetailsInfo.getMeasure())) {
						if(labServiceRangeInfo.getMinRange()!=null && labServiceRangeInfo.getMaxRange()!=null)
						{
						refMeasureDetailsInfo.setDimension(labServiceRangeInfo.getParameter());
						refMeasureDetailsInfo.setGender(labServiceRangeInfo.getGender());
						refMeasureDetailsInfo.setRange("[" + labServiceRangeInfo.getMinRange() + "-"
								+ labServiceRangeInfo.getMaxRange() + "]");
						refMeasureDetailsInfo.setMethod(labServiceRangeInfo.getMethod());
						patientServiceDetails.setPatientLabServiceRange(labServiceRangeInfo);
						patientServiceDetails.setActualValue(refMeasureDetailsInfo.getValue());
					}
						else if(labServiceRangeInfo.getMinRange()!=null ||labServiceRangeInfo.getMaxRange()!=null )
						{
							refMeasureDetailsInfo.setDimension(labServiceRangeInfo.getParameter());
							refMeasureDetailsInfo.setGender(labServiceRangeInfo.getGender());
							if(labServiceRangeInfo.getMinRange()==null)
							{
							refMeasureDetailsInfo.setRange("["+ labServiceRangeInfo.getMaxRange() + "]");
							}else
							{
								refMeasureDetailsInfo.setRange("[" + labServiceRangeInfo.getMinRange()+ "]");
							}
							refMeasureDetailsInfo.setMethod(labServiceRangeInfo.getMethod());
							patientServiceDetails.setPatientLabServiceRange(labServiceRangeInfo);
							patientServiceDetails.setActualValue(refMeasureDetailsInfo.getValue());
						}
						else
						{
							refMeasureDetailsInfo.setDimension(labServiceRangeInfo.getParameter());
							refMeasureDetailsInfo.setGender(labServiceRangeInfo.getGender());
							refMeasureDetailsInfo.setRange(" ");
							refMeasureDetailsInfo.setMethod(labServiceRangeInfo.getMethod());
							patientServiceDetails.setPatientLabServiceRange(labServiceRangeInfo);
							patientServiceDetails.setActualValue(refMeasureDetailsInfo.getValue());
						}
					}
				}
				patientServiceDetailsServiceImpl.save(patientServiceDetails);

			}

		}
		Date date = new Date(laboratoryRegistrationsInfo.get(0).getEnteredDate().getTime());
		DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
		String bill = formatter.format(date).toString();

		
		String admittedWard=null;
		String roomName=null;

		List<RoomBookingDetails> roomBookingDetails=patientRegistration.getRoomBookingDetails();
		if(!roomBookingDetails.isEmpty())
		{
		for(RoomBookingDetails roomBookingDetailsInfo:roomBookingDetails){
			RoomDetails roomDetails =roomBookingDetailsInfo.getRoomDetails();
			admittedWard=roomDetails.getRoomType();
			roomName=roomDetails.getRoomName();
		}}

		// PDF
		
		Resource fileResourcee = resourceLoader.getResource("classpath:udbhava.png");

		Image img=null;
		try {
			img = Image.getInstance(fileResourcee.getFile().getAbsolutePath());
		} catch (Exception e1) {
			Logger.error(e1.getMessage());
		}
		img.scaleAbsolute(100, 100);
		Phrase pq = new Phrase(new Chunk(img, 0, -90));

		ServicePdf servicePdf = new ServicePdf();

		
		 if (patientServiceDetails.getServiceName().equalsIgnoreCase("HIV")) {

			byte[] pdfBytes = null;
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			Document document = new Document();

			try {
				PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);

				MyFooter event = new MyFooter(principal,userServiceImpl);
				writer.setPageEvent(event);


				Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
				Font redFont3 = new Font(Font.FontFamily.TIMES_ROMAN, 17, Font.BOLD);
				Font redFont2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.ITALIC);
				Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
				Font headFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD|Font.UNDERLINE);
				Font headFont2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD|Font.UNDERLINE);

				document.open();

				
				document.add(pq);

				Paragraph p51 = new Paragraph("UDBHAVA HOSPITALS", redFont);
				p51.add("\n \n \n  \n");
				p51.setAlignment(Element.ALIGN_CENTER);
				document.add(p51);
				
				Font redFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

				date = Calendar.getInstance().getTime();
				formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
				String today = formatter.format(date).toString();

				
				PdfPTable table2 = new PdfPTable(2);
				
				PdfPCell cell1 = new PdfPCell();
				table2.setWidthPercentage(100f);	
				cell1.setBorder(Rectangle.BOTTOM|Rectangle.TOP);
				
				PdfPTable table21 = new PdfPTable(6);
				table21.setWidths(new float[] { 5f,1f,5f,5f,1f,6.5f});
				 table21.setSpacingBefore(10);
				

				PdfPCell hcell1;
				hcell1 = new PdfPCell(
						new Phrase("Patient Name", redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(
						new Phrase(":", redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(
						new Phrase(patientRegistration.getPatientDetails().getTitle() +". "+ patientRegistration.getPatientDetails().getFirstName()
								+ " " + patientRegistration.getPatientDetails().getLastName(), redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase("Age/Gender", redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase(":", redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getAge()
						+ "/" + patientRegistration.getPatientDetails().getGender(), redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				
				
				PdfPCell hcell4;
				hcell4 = new PdfPCell(
						new Phrase("Admission No", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(
						new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(
						new Phrase(patientRegistration.getRegId(), redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				


				if(admittedWard!=null)
				{
				hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(admittedWard, redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				}
				else
				{
					hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					
					hcell4 = new PdfPCell(new Phrase(":", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					hcell4 = new PdfPCell(new Phrase("", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);

				}



				PdfPCell hcell15;
				hcell15 = new PdfPCell(
						new Phrase("Admission Date", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				hcell15 = new PdfPCell(
						new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				hcell15 = new PdfPCell(
						new Phrase(pdfadmissionDate, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				
				if(roomName!=null)
				{
				hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(roomName, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				}
				else
				{
					hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase(":", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase("", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
				}
				PdfPCell hcell16;
				hcell16 = new PdfPCell(new Phrase(
						"Bill No/UMR No", redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(
						":", redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getUmr(), redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);

				hcell16 = new PdfPCell(new Phrase("Bill Date", redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(":", redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(bill, redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);

				PdfPCell hcell17;
				hcell17 = new PdfPCell(new Phrase(
						"Consultant",
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(
						":",
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(
						patientRegistration.getPatientDetails().getConsultant(),
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);

				hcell17 = new PdfPCell(new Phrase("Report Date", redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(":", redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(today, redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				cell1.setColspan(2);
				cell1.addElement(table21);
				table2.addCell(cell1);
             //  table2.addCell(cell1);
               document.add(table2);


				Paragraph p11 = new Paragraph("SEROLOGY REPORT",headFont1);
				p11.setAlignment(Element.ALIGN_CENTER);
				document.add(p11);

				PdfPTable table3 = new PdfPTable(2);
				table3.setWidths(new float[] { 6f, 6f });

				PdfPCell hcell01;
				hcell01 = new PdfPCell(new Phrase("\n INVESTIGATION", headFont2));
				hcell01.setBorder(Rectangle.NO_BORDER);
				hcell01.setPaddingLeft(-50f);
				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
				table3.addCell(hcell01);

				hcell01 = new PdfPCell(new Phrase("\n REPORT", headFont2));
				hcell01.setBorder(Rectangle.NO_BORDER);
				hcell01.setPaddingRight(-40f);
				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
				table3.addCell(hcell01);

				for (RefMeasureDetails refMeasureDetailsInfoPdf : refMeasureDetails) {

					if (refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("HIV I")
							|| refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("HIV II")) {
						PdfPCell hcell021;
						hcell021 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getMeasure(), redFont1));
						hcell021.setBorder(Rectangle.NO_BORDER);
						hcell021.setPaddingLeft(-50f);
						hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
						table3.addCell(hcell021);

						if (refMeasureDetailsInfoPdf.getDimension() != null) {
							hcell021 = new PdfPCell(new Phrase(": " + refMeasureDetailsInfoPdf.getValue() + " "
									+ refMeasureDetailsInfoPdf.getDimension(), redFont1));
							hcell021.setBorder(Rectangle.NO_BORDER);
							hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
							// hcell021.setPaddingLeft(30f);
							table3.addCell(hcell021);
						} else {
							hcell021 = new PdfPCell(new Phrase(": " + refMeasureDetailsInfoPdf.getValue(), redFont1));
							hcell021.setBorder(Rectangle.NO_BORDER);
							hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
							/// hcell021.setPaddingLeft(45f);
							table3.addCell(hcell021);
						}
					}
				}
						for (RefMeasureDetails refMeasureDetailsInfoPdf : refMeasureDetails) {
							if(refMeasureDetailsInfoPdf.getMethod()!=null) {
						PdfPCell hcell0211;
						hcell0211 = new PdfPCell(new Phrase("\n ("+refMeasureDetailsInfoPdf.getMethod()+")", redFont1));
						hcell0211.setBorder(Rectangle.NO_BORDER);
						//hcell0211.setPaddingRight(-40f);
						hcell0211.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell0211.setPaddingLeft(-55f);
						table3.addCell(hcell0211);
						
						
						hcell0211 = new PdfPCell(new Phrase("", redFont1));
						hcell0211.setBorder(Rectangle.NO_BORDER);
						//hcell0211.setPaddingRight(-40f);
						hcell0211.setHorizontalAlignment(Element.ALIGN_LEFT);
						table3.addCell(hcell0211);
						System.out.println(refMeasureDetailsInfoPdf.getMethod());
						System.out.println(refMeasureDetailsInfoPdf.getMethod());
						}
						}
				

				document.add(table3);

				Paragraph p01 = new Paragraph();
				p01.add("\n");
				p01.add("\n");
				document.add(p01);

				if (patientServiceDetails.getComment().isEmpty()) {
					Paragraph p27 = new Paragraph("\n Findings:", headFont);
					document.add(p27);
					Paragraph p28 = new Paragraph("N/A", redFont2);
					document.add(p28);

				} else {
					Paragraph p27 = new Paragraph("\n Findings:", headFont);

					Paragraph p28 = new Paragraph(patientServiceDetails.getComment(), redFont1);
					document.add(p27);
					document.add(p28);
				}
				Paragraph p7 = new Paragraph("*Suggested clinical correlation", redFont2);
				p7.setAlignment(Element.ALIGN_CENTER);
				p7.add("\n");
				p7.add("\n");
				document.add(p7);

				Chunk cnd = new Chunk(new VerticalPositionMark());
				Paragraph p19 = new Paragraph(patientRegistration.getPatientDetails().getConsultant(), headFont);
				p19.setAlignment(Element.ALIGN_RIGHT);
				document.add(p19);

				Paragraph p18 = new Paragraph("LAB INCHARGE", headFont);
				p18.add(cnd);

				p18.add("(" + patientRegistration.getVuserD().getDoctorDetails().getSpecilization() + ")");
				document.add(p18);

				document.close();

				pdfBytes = byteArrayOutputStream.toByteArray();
				List<ServicePdf> servicePdfs=servicePdfServiceImpl.findByRegAndMeasureName(regId,patientServiceDetails.getServiceName());
				if(!servicePdfs.isEmpty())
				{
					servicePdf = new ServicePdf();
					servicePdf.setSid(servicePdfs.get(0).getSid());
					servicePdf.setData(pdfBytes);
					servicePdf.setFileName(servicePdfs.get(0).getFileName());
					servicePdf.setFileuri(servicePdfs.get(0).getFileuri());
					servicePdf.setRegId(servicePdfs.get(0).getRegId());
					servicePdfServiceImpl.save(servicePdf);

				}
				else
				{
				String uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/lab/servicePdf/viewFile/")
						.path(servicePdfServiceImpl.getNextLabId()).toUriString();

				servicePdf = new ServicePdf();
				servicePdf.setSid(servicePdfServiceImpl.getNextLabId());
				servicePdf.setData(pdfBytes);
				servicePdf.setFileName(patientServiceDetails.getServiceName());
				servicePdf.setFileuri(uri);
				servicePdf.setRegId(patientServiceDetails.getRegId());
				servicePdfServiceImpl.save(servicePdf);
				}

			} catch (Exception e) {
				Logger.error(e.getMessage());
			}

		}

		else if (patientServiceDetails.getServiceName().equalsIgnoreCase("SERUM URIC ACID")) {

			byte[] pdfBytes = null;
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			Document document = new Document();

			try {
				PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);

				MyFooter event = new MyFooter(principal,userServiceImpl);
				writer.setPageEvent(event);


				Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
				Font redFont3 = new Font(Font.FontFamily.TIMES_ROMAN, 17, Font.BOLD);
				Font redFont2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.ITALIC);
				Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
				Font headFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD|Font.UNDERLINE);
				Font headFont2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD|Font.UNDERLINE);

				document.open();


				document.add(pq);

				Paragraph p51 = new Paragraph("UDBHAVA HOSPITALS", redFont);
				p51.add("\n \n \n \n");
				p51.setAlignment(Element.ALIGN_CENTER);
				document.add(p51);
				Font redFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

				date = Calendar.getInstance().getTime();
				formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
				String today = formatter.format(date).toString();

				
				PdfPTable table2 = new PdfPTable(2);
				
				PdfPCell cell1 = new PdfPCell();
				table2.setWidthPercentage(100f);	
				cell1.setBorder(Rectangle.BOTTOM|Rectangle.TOP);
				
				PdfPTable table21 = new PdfPTable(6);
				table21.setWidths(new float[] { 5f,1f,5f,5f,1f,6.5f});
				 table21.setSpacingBefore(10);
				

				PdfPCell hcell1;
				hcell1 = new PdfPCell(
						new Phrase("Patient Name", redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(
						new Phrase(":", redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(
						new Phrase(patientRegistration.getPatientDetails().getTitle() +". "+ patientRegistration.getPatientDetails().getFirstName()
								+ " " + patientRegistration.getPatientDetails().getLastName(), redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase("Age/Gender", redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase(":", redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getAge()
						+ "/" + patientRegistration.getPatientDetails().getGender(), redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				
				
				PdfPCell hcell4;
				hcell4 = new PdfPCell(
						new Phrase("Admission No", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(
						new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(
						new Phrase(patientRegistration.getRegId(), redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				


				if(admittedWard!=null)
				{
				hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(admittedWard, redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				}
				else
				{
					hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					
					hcell4 = new PdfPCell(new Phrase(":", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					hcell4 = new PdfPCell(new Phrase("", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);

				}



				PdfPCell hcell15;
				hcell15 = new PdfPCell(
						new Phrase("Admission Date", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				hcell15 = new PdfPCell(
						new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				hcell15 = new PdfPCell(
						new Phrase(pdfadmissionDate, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				
				if(roomName!=null)
				{
				hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(roomName, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				}
				else
				{
					hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase(":", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase("", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
				}
				PdfPCell hcell16;
				hcell16 = new PdfPCell(new Phrase(
						"Bill No/UMR No", redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(
						":", redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getUmr(), redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);

				hcell16 = new PdfPCell(new Phrase("Bill Date", redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(":", redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(bill, redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);

				PdfPCell hcell17;
				hcell17 = new PdfPCell(new Phrase(
						"Consultant",
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(
						":",
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(
						patientRegistration.getPatientDetails().getConsultant(),
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);

				hcell17 = new PdfPCell(new Phrase("Report Date", redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(":", redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(today, redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				cell1.setColspan(2);
				cell1.addElement(table21);
				table2.addCell(cell1);
             //  table2.addCell(cell1);
               document.add(table2);


				Paragraph p11 = new Paragraph("BIOCHEMISTRY REPORT",headFont1);
				p11.setAlignment(Element.ALIGN_CENTER);
				document.add(p11);

				PdfPTable table3 = new PdfPTable(3);
				table3.setWidths(new float[] { 5f, 4f, 6f });

				PdfPCell hcell01;
				hcell01 = new PdfPCell(new Phrase("\n INVESTIGATION", headFont2));
				hcell01.setBorder(Rectangle.NO_BORDER);
				hcell01.setPaddingLeft(-50f);
				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
				table3.addCell(hcell01);

				hcell01 = new PdfPCell(new Phrase("\n RESULT", headFont2));
				hcell01.setBorder(Rectangle.NO_BORDER);
				//hcell01.setPaddingRight(-50f);
				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell01.setPaddingLeft(0f);
				table3.addCell(hcell01);

				hcell01 = new PdfPCell(new Phrase("\n NORMAL RANGE", headFont2));
				hcell01.setBorder(Rectangle.NO_BORDER);
				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell01.setPaddingLeft(10f);
				table3.addCell(hcell01);

				for (RefMeasureDetails refMeasureDetailsInfoPdf : refMeasureDetails) {

					if (refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("SERUM URIC ACID")) {

						PdfPCell hcell021;
						hcell021 = new PdfPCell(new Phrase("\n" + refMeasureDetailsInfoPdf.getMeasure(), redFont1));
						hcell021.setBorder(Rectangle.NO_BORDER);
						hcell021.setPaddingLeft(-50f);
						hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
						table3.addCell(hcell021);

						if (refMeasureDetailsInfoPdf.getDimension() != null) {
							hcell021 = new PdfPCell(new Phrase("\n : " + refMeasureDetailsInfoPdf.getValue() + " "
									+ refMeasureDetailsInfoPdf.getDimension(), redFont1));
							hcell021.setBorder(Rectangle.NO_BORDER);
							hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
							 hcell021.setPaddingLeft(0f);
							table3.addCell(hcell021);
						} else {
							hcell021 = new PdfPCell(
									new Phrase("\n : " + refMeasureDetailsInfoPdf.getValue(), redFont1));
							hcell021.setBorder(Rectangle.NO_BORDER);
							hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
							hcell021.setPaddingLeft(0f);
							table3.addCell(hcell021);
						}

						//System.out.println(refMeasureDetailsInfoPdf.getMethod());
						if(refMeasureDetailsInfoPdf.getDimension()!=null)
						{
						hcell021 = new PdfPCell(new Phrase("\n" + refMeasureDetailsInfoPdf.getRange() + " "
								+ refMeasureDetailsInfoPdf.getDimension(), redFont1));
						hcell021.setBorder(Rectangle.NO_BORDER);
						hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell021.setPaddingLeft(10f);
						table3.addCell(hcell021);
						}
						else
						{
							hcell021 = new PdfPCell(new Phrase("\n" + refMeasureDetailsInfoPdf.getRange(), redFont1));
							hcell021.setBorder(Rectangle.NO_BORDER);
							hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
							hcell021.setPaddingLeft(10f);
							table3.addCell(hcell021);
						}
					}
				}

				document.add(table3);

				Paragraph p01 = new Paragraph();
				
				p01.add("\n");
				p01.add("\n");
				document.add(p01);

				if (patientServiceDetails.getComment().isEmpty()) {
					Paragraph p27 = new Paragraph("\n Findings:", headFont);
					document.add(p27);
					Paragraph p28 = new Paragraph("N/A", redFont2);
					document.add(p28);

				} else {
					Paragraph p27 = new Paragraph("\n Findings:", headFont);

					Paragraph p28 = new Paragraph(patientServiceDetails.getComment(), redFont2);
					document.add(p27);
					document.add(p28);
				}
				Paragraph p7 = new Paragraph("*Suggested clinical correlation, if necessary kindly discuss", redFont2);
				p7.setAlignment(Element.ALIGN_CENTER);

				
				p7.add("\n");
				p7.add("\n");

				document.add(p7);

				Chunk cnd = new Chunk(new VerticalPositionMark());
				Paragraph p19 = new Paragraph(patientRegistration.getPatientDetails().getConsultant(), headFont);
				p19.setAlignment(Element.ALIGN_RIGHT);
				document.add(p19);

				Paragraph p18 = new Paragraph("LAB INCHARGE", headFont);
				p18.add(cnd);

				p18.add("(" + patientRegistration.getVuserD().getDoctorDetails().getSpecilization() + ")");
				document.add(p18);

				document.close();
				pdfBytes = byteArrayOutputStream.toByteArray();
				List<ServicePdf> servicePdfs=servicePdfServiceImpl.findByRegAndMeasureName(regId,patientServiceDetails.getServiceName());
				if(!servicePdfs.isEmpty())
				{
					servicePdf = new ServicePdf();
					servicePdf.setSid(servicePdfs.get(0).getSid());
					servicePdf.setData(pdfBytes);
					servicePdf.setFileName(servicePdfs.get(0).getFileName());
					servicePdf.setFileuri(servicePdfs.get(0).getFileuri());
					servicePdf.setRegId(servicePdfs.get(0).getRegId());
					servicePdfServiceImpl.save(servicePdf);

				}
				else
				{
				String uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/lab/servicePdf/viewFile/")
						.path(servicePdfServiceImpl.getNextLabId()).toUriString();

				servicePdf = new ServicePdf();
				servicePdf.setSid(servicePdfServiceImpl.getNextLabId());
				servicePdf.setData(pdfBytes);
				servicePdf.setFileName(patientServiceDetails.getServiceName());
				servicePdf.setFileuri(uri);
				servicePdf.setRegId(patientServiceDetails.getRegId());
				servicePdfServiceImpl.save(servicePdf);
				}


			} catch (Exception e) {
				Logger.error(e.getMessage());
			}

		}
		else if (patientServiceDetails.getServiceName().equalsIgnoreCase("LIPID PROFILE")){

			byte[] pdfBytes = null;
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			Document document = new Document();

			try {
				PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);

				MyFooter event = new MyFooter(principal,userServiceImpl);
				writer.setPageEvent(event);


				Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
				Font redFont3 = new Font(Font.FontFamily.TIMES_ROMAN, 17, Font.BOLD);
				Font redFont2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.ITALIC);
				Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
				Font headFont1= new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.UNDERLINE|Font.BOLD);

				document.open();

				


				document.add(pq);

				Paragraph p51 = new Paragraph("UDBHAVA HOSPITALS", redFont);
				p51.add("\n \n \n");
				p51.setAlignment(Element.ALIGN_CENTER);
				document.add(p51);
				
				/*Paragraph p101 = new Paragraph(
						"_____________________________________________________________________________");
				document.add(p101);*/
				
				Font redFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

				 date = Calendar.getInstance().getTime();
				 formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
				String today = formatter.format(date).toString();

				
				PdfPTable table2 = new PdfPTable(2);
				
				PdfPCell cell1 = new PdfPCell();
				table2.setWidthPercentage(100f);	
				cell1.setBorder(Rectangle.BOTTOM|Rectangle.TOP);
				
				PdfPTable table21 = new PdfPTable(6);
				table21.setWidths(new float[] { 5f,1f,5f,5f,1f,6.5f});
				 table21.setSpacingBefore(10);
				

				PdfPCell hcell1;
				hcell1 = new PdfPCell(
						new Phrase("Patient Name", redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(
						new Phrase(":", redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(
						new Phrase(patientRegistration.getPatientDetails().getTitle() +". "+ patientRegistration.getPatientDetails().getFirstName()
								+ " " + patientRegistration.getPatientDetails().getLastName(), redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase("Age/Gender", redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase(":", redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getAge()
						+ "/" + patientRegistration.getPatientDetails().getGender(), redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				
				
				PdfPCell hcell4;
				hcell4 = new PdfPCell(
						new Phrase("Admission No", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(
						new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(
						new Phrase(patientRegistration.getRegId(), redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				


				if(admittedWard!=null)
				{
				hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(admittedWard, redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				}
				else
				{
					hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					
					hcell4 = new PdfPCell(new Phrase(":", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					hcell4 = new PdfPCell(new Phrase("", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);

				}



				PdfPCell hcell15;
				hcell15 = new PdfPCell(
						new Phrase("Admission Date", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				hcell15 = new PdfPCell(
						new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				hcell15 = new PdfPCell(
						new Phrase(pdfadmissionDate, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				
				if(roomName!=null)
				{
				hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(roomName, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				}
				else
				{
					hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase(":", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase("", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
				}
				PdfPCell hcell16;
				hcell16 = new PdfPCell(new Phrase(
						"Bill No/UMR No", redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(
						":", redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getUmr(), redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);

				hcell16 = new PdfPCell(new Phrase("Bill Date", redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(":", redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(bill, redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);

				PdfPCell hcell17;
				hcell17 = new PdfPCell(new Phrase(
						"Consultant",
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(
						":",
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(
						patientRegistration.getPatientDetails().getConsultant(),
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);

				hcell17 = new PdfPCell(new Phrase("Report Date", redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(":", redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(today, redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				cell1.setColspan(2);
				cell1.addElement(table21);
				table2.addCell(cell1);
             //  table2.addCell(cell1);
               document.add(table2);

               
               
        		/*
				table21.addCell(cell1);
				document.add(table21);*/
/*
				Paragraph p10 = new Paragraph(
						"_____________________________________________________________________________");
				document.add(p10);*/
Paragraph p201=new Paragraph("\n");
document.add(p201);
				Paragraph p11 = new Paragraph(" DEPARTMENT OF BIOCHEMISTRY",headFont1);
				p11.setAlignment(Element.ALIGN_CENTER);
				
				document.add(p11);
				
				Paragraph p202=new Paragraph("\n");
				document.add(p202);

				PdfPTable table3 = new PdfPTable(3);
				table3.setWidths(new float[] { 5f, 6f, 10f });

				PdfPCell hcell01;
				hcell01 = new PdfPCell(new Phrase("INVESTIGATION", headFont1));
				hcell01.setBorder(Rectangle.NO_BORDER);
				hcell01.setPaddingLeft(-50f);
				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
				table3.addCell(hcell01);

				hcell01 = new PdfPCell(new Phrase("RESULT", headFont1));
				hcell01.setBorder(Rectangle.NO_BORDER);
				 hcell01.setPaddingRight(-40f);
				hcell01.setHorizontalAlignment(Element.ALIGN_CENTER);
				table3.addCell(hcell01);

				hcell01 = new PdfPCell(new Phrase("NORMAL RANGE", headFont1));
				hcell01.setBorder(Rectangle.NO_BORDER);
				hcell01.setHorizontalAlignment(Element.ALIGN_CENTER);
				hcell01.setPaddingLeft(50f);
				table3.addCell(hcell01);


				PdfPCell hcell011;
				hcell011 = new PdfPCell(new Phrase("\n", headFont));
				hcell011.setBorder(Rectangle.NO_BORDER);
				hcell011.setPaddingLeft(-50f);
				hcell011.setHorizontalAlignment(Element.ALIGN_LEFT);
				table3.addCell(hcell011);

				hcell011 = new PdfPCell(new Phrase("\n LIPID PROFILE", headFont1));
				hcell011.setBorder(Rectangle.NO_BORDER);
				 hcell011.setPaddingRight(-40f);
				hcell011.setHorizontalAlignment(Element.ALIGN_CENTER);
				table3.addCell(hcell011);

				hcell011 = new PdfPCell(new Phrase("\n", headFont));
				hcell011.setBorder(Rectangle.NO_BORDER);
				hcell011.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell011.setPaddingLeft(85f);
				table3.addCell(hcell011);

				
				

				for (RefMeasureDetails refMeasureDetailsInfoPdf : refMeasureDetails) {

						PdfPCell hcell021;
						hcell021 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getMeasure(), redFont1));
						hcell021.setBorder(Rectangle.NO_BORDER);
						hcell021.setPaddingLeft(-50f);
						hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
						table3.addCell(hcell021);

						if(refMeasureDetailsInfoPdf.getDimension()!=null)
						{
							hcell021 = new PdfPCell(new Phrase(":   " + refMeasureDetailsInfoPdf.getValue() + " " + refMeasureDetailsInfoPdf.getDimension(),redFont1));
							hcell021.setBorder(Rectangle.NO_BORDER);
							hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
							hcell021.setPaddingLeft(55f);
							table3.addCell(hcell021);
						}
						else
						{
							hcell021 = new PdfPCell(new Phrase(":   " + refMeasureDetailsInfoPdf.getValue(),redFont1));
							hcell021.setBorder(Rectangle.NO_BORDER);
							hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
							hcell021.setPaddingLeft(55f);
							table3.addCell(hcell021);
						}
						
						if(refMeasureDetailsInfoPdf.getDimension()!=null)
						{
						hcell021 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getRange()+" "+refMeasureDetailsInfoPdf.getDimension(), redFont1));
						hcell021.setBorder(Rectangle.NO_BORDER);
						hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell021.setPaddingLeft(80f);
						table3.addCell(hcell021);
						}
						else
						{
							hcell021 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getRange(), redFont1));
							hcell021.setBorder(Rectangle.NO_BORDER);
							hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
							hcell021.setPaddingLeft(80f);
							table3.addCell(hcell021);
						}
						System.out.println(refMeasureDetailsInfoPdf.getMethod());
											}
				
				
				document.add(table3);

				Paragraph p01 = new Paragraph();
				
				p01.add("\n");
				p01.add("\n");
				
				document.add(p01);


				if (patientServiceDetails.getComment().isEmpty()) {
					Paragraph p27 = new Paragraph("\n Findings:", headFont);
					document.add(p27);
					Paragraph p28 = new Paragraph("N/A", redFont2);
					document.add(p28);

				} else {
					Paragraph p27 = new Paragraph("\n Findings:", headFont);

					Paragraph p28 = new Paragraph(patientServiceDetails.getComment(), redFont2);
					document.add(p27);
					document.add(p28);
				}
				
				Paragraph p7 = new Paragraph("*Please correlate with clinical findings if necessary discuss", redFont2);
				p7.setAlignment(Element.ALIGN_CENTER);

				p7.add("\n");
				p7.add("\n");
				document.add(p7);

				Chunk cnd = new Chunk(new VerticalPositionMark());
				Paragraph p19 = new Paragraph(patientRegistration.getPatientDetails().getConsultant(), headFont);
				p19.setAlignment(Element.ALIGN_RIGHT);
				document.add(p19);

				Paragraph p18 = new Paragraph("LAB INCHARGE", headFont);
				p18.add(cnd);

				p18.add("(" + patientRegistration.getVuserD().getDoctorDetails().getSpecilization() + ")");
				document.add(p18);
				
			
				// ************************

				document.close();

				pdfBytes = byteArrayOutputStream.toByteArray();
				List<ServicePdf> servicePdfs=servicePdfServiceImpl.findByRegAndMeasureName(regId,patientServiceDetails.getServiceName());
				if(!servicePdfs.isEmpty())
				{
					servicePdf = new ServicePdf();
					servicePdf.setSid(servicePdfs.get(0).getSid());
					servicePdf.setData(pdfBytes);
					servicePdf.setFileName(servicePdfs.get(0).getFileName());
					servicePdf.setFileuri(servicePdfs.get(0).getFileuri());
					servicePdf.setRegId(servicePdfs.get(0).getRegId());
					servicePdfServiceImpl.save(servicePdf);

				}
				else
				{
				String uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/lab/servicePdf/viewFile/")
						.path(servicePdfServiceImpl.getNextLabId()).toUriString();

				servicePdf = new ServicePdf();
				servicePdf.setSid(servicePdfServiceImpl.getNextLabId());
				servicePdf.setData(pdfBytes);
				servicePdf.setFileName(patientServiceDetails.getServiceName());
				servicePdf.setFileuri(uri);
				servicePdf.setRegId(patientServiceDetails.getRegId());
				servicePdfServiceImpl.save(servicePdf);
				}


			} catch (Exception e) {
				Logger.error(e.getMessage());
			}

		}
		
		else if (patientServiceDetails.getServiceName().equalsIgnoreCase("URINE FOR MICROALBUMIN")) {

			byte[] pdfBytes = null;
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			Document document = new Document();

			try {
				PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);

				MyFooter event = new MyFooter(principal,userServiceImpl);
				writer.setPageEvent(event);

				Font blueFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
				Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
				Font redFont3 = new Font(Font.FontFamily.TIMES_ROMAN, 17, Font.BOLD);
				Font redFont2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.ITALIC);
				Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
				Font headFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD|Font.UNDERLINE);
				Font headFont2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD|Font.UNDERLINE);

				document.open();

				document.add(pq);

				Paragraph p51 = new Paragraph("UDBHAVA HOSPITALS", redFont);
				p51.add("\n \n \n \n");
				p51.setAlignment(Element.ALIGN_CENTER);
				document.add(p51);

				Font redFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

				date = Calendar.getInstance().getTime();
				formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
				String today = formatter.format(date).toString();

				PdfPTable table2 = new PdfPTable(2);
				
				PdfPCell cell1 = new PdfPCell();
				table2.setWidthPercentage(100f);	
				cell1.setBorder(Rectangle.BOTTOM|Rectangle.TOP);
				
				PdfPTable table21 = new PdfPTable(6);
				table21.setWidths(new float[] { 5f,1f,5f,5f,1f,6.5f});
				 table21.setSpacingBefore(10);
				

				PdfPCell hcell1;
				hcell1 = new PdfPCell(
						new Phrase("Patient Name", redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(
						new Phrase(":", redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(
						new Phrase(patientRegistration.getPatientDetails().getTitle() +". "+ patientRegistration.getPatientDetails().getFirstName()
								+ " " + patientRegistration.getPatientDetails().getLastName(), redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase("Age/Gender", redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase(":", redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getAge()
						+ "/" + patientRegistration.getPatientDetails().getGender(), redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				
				
				PdfPCell hcell4;
				hcell4 = new PdfPCell(
						new Phrase("Admission No", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(
						new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(
						new Phrase(patientRegistration.getRegId(), redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				


				if(admittedWard!=null)
				{
				hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(admittedWard, redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				}
				else
				{
					hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					
					hcell4 = new PdfPCell(new Phrase(":", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					hcell4 = new PdfPCell(new Phrase("", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);

				}



				PdfPCell hcell15;
				hcell15 = new PdfPCell(
						new Phrase("Admission Date", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				hcell15 = new PdfPCell(
						new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				hcell15 = new PdfPCell(
						new Phrase(pdfadmissionDate, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				
				if(roomName!=null)
				{
				hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(roomName, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				}
				else
				{
					hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase(":", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase("", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
				}
				PdfPCell hcell16;
				hcell16 = new PdfPCell(new Phrase(
						"Bill No/UMR No", redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(
						":", redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getUmr(), redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);

				hcell16 = new PdfPCell(new Phrase("Bill Date", redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(":", redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(bill, redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);

				PdfPCell hcell17;
				hcell17 = new PdfPCell(new Phrase(
						"Consultant",
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(
						":",
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(
						patientRegistration.getPatientDetails().getConsultant(),
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);

				hcell17 = new PdfPCell(new Phrase("Report Date", redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(":", redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(today, redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				cell1.setColspan(2);
				cell1.addElement(table21);
				table2.addCell(cell1);
             //  table2.addCell(cell1);
               document.add(table2);


				Paragraph p11 = new Paragraph("BIOCHEMISTRY REPORT",headFont1);
				p11.setAlignment(Element.ALIGN_CENTER);
				document.add(p11);

				PdfPTable table3 = new PdfPTable(2);
				table3.setWidths(new float[] { 5f, 6f });

				PdfPCell hcell01;
				hcell01 = new PdfPCell(new Phrase("\n INVESTIGATION", headFont2));
				hcell01.setBorder(Rectangle.NO_BORDER);
				hcell01.setPaddingLeft(-40f);
				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
				table3.addCell(hcell01);

				hcell01 = new PdfPCell(new Phrase("\n RESULT", headFont2));
				hcell01.setBorder(Rectangle.NO_BORDER);
				hcell01.setPaddingRight(-40f);
				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
				table3.addCell(hcell01);

				for (RefMeasureDetails refMeasureDetailsInfoPdf : refMeasureDetails) {

					if (refMeasureDetailsInfoPdf.getMeasure().equalsIgnoreCase("Urine for Microalbumin")) {
						PdfPCell hcell021;
						hcell021 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getMeasure(), redFont1));
						hcell021.setBorder(Rectangle.NO_BORDER);
						hcell021.setPaddingLeft(-40f);
						hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
						table3.addCell(hcell021);

						if (refMeasureDetailsInfoPdf.getDimension() != null) {
							hcell021 = new PdfPCell(new Phrase(": " + refMeasureDetailsInfoPdf.getValue() + ""
									+ refMeasureDetailsInfoPdf.getDimension(), redFont1));
							hcell021.setBorder(Rectangle.NO_BORDER);
							hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
							// hcell021.setPaddingLeft(30f);

							table3.addCell(hcell021);
						} else {
							hcell021 = new PdfPCell(new Phrase(": " + refMeasureDetailsInfoPdf.getValue(), redFont1));
							hcell021.setBorder(Rectangle.NO_BORDER);
							hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
							// hcell021.setPaddingLeft(45f);
							table3.addCell(hcell021);
						}
						
						
						PdfPCell hcell0211;
						hcell0211 = new PdfPCell(new Phrase("\n ("+refMeasureDetailsInfoPdf.getMethod()+")", redFont1));
						hcell0211.setBorder(Rectangle.NO_BORDER);
						//hcell0211.setPaddingRight(-40f);
						hcell0211.setPaddingLeft(-40f);
						hcell0211.setHorizontalAlignment(Element.ALIGN_LEFT);
						table3.addCell(hcell0211);
						
						
						hcell0211 = new PdfPCell(new Phrase("", redFont1));
						hcell0211.setBorder(Rectangle.NO_BORDER);
						//hcell0211.setPaddingRight(-40f);
						hcell0211.setHorizontalAlignment(Element.ALIGN_LEFT);
						table3.addCell(hcell0211);
						System.out.println(refMeasureDetailsInfoPdf.getMethod());

						System.out.println(refMeasureDetailsInfoPdf.getMethod());

					}
				}
				document.add(table3);

				Paragraph p01 = new Paragraph();

				p01.add("\n");
				p01.add("\n");
				document.add(p01);

				if (patientServiceDetails.getComment().isEmpty()) {
					Paragraph p27 = new Paragraph("\n Findings:", headFont);
					document.add(p27);
					Paragraph p28 = new Paragraph("N/A", redFont2);
					document.add(p28);

				} else {
					Paragraph p27 = new Paragraph("\n Findings:", headFont);

					Paragraph p28 = new Paragraph(patientServiceDetails.getComment(), redFont2);
					document.add(p27);
					document.add(p28);
				}

				Paragraph p7 = new Paragraph("*Suggested clinical correlation, if necessary kindly discuss", redFont2);
				p7.setAlignment(Element.ALIGN_CENTER);

				
				p7.add("\n");
				p7.add("\n");
				document.add(p7);

				Chunk cnd = new Chunk(new VerticalPositionMark());
				Paragraph p19 = new Paragraph(patientRegistration.getPatientDetails().getConsultant(), headFont);
				p19.setAlignment(Element.ALIGN_RIGHT);
				document.add(p19);

				Paragraph p18 = new Paragraph("LAB INCHARGE", headFont);
				p18.add(cnd);

				p18.add("(" + patientRegistration.getVuserD().getDoctorDetails().getSpecilization() + ")");
				document.add(p18);

				document.close();

				pdfBytes = byteArrayOutputStream.toByteArray();
				List<ServicePdf> servicePdfs=servicePdfServiceImpl.findByRegAndMeasureName(regId,patientServiceDetails.getServiceName());
				if(!servicePdfs.isEmpty())
				{
					servicePdf = new ServicePdf();
					servicePdf.setSid(servicePdfs.get(0).getSid());
					servicePdf.setData(pdfBytes);
					servicePdf.setFileName(servicePdfs.get(0).getFileName());
					servicePdf.setFileuri(servicePdfs.get(0).getFileuri());
					servicePdf.setRegId(servicePdfs.get(0).getRegId());
					servicePdfServiceImpl.save(servicePdf);

				}
				else
				{
				String uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/lab/servicePdf/viewFile/")
						.path(servicePdfServiceImpl.getNextLabId()).toUriString();

				servicePdf = new ServicePdf();
				servicePdf.setSid(servicePdfServiceImpl.getNextLabId());
				servicePdf.setData(pdfBytes);
				servicePdf.setFileName(patientServiceDetails.getServiceName());
				servicePdf.setFileuri(uri);
				servicePdf.setRegId(patientServiceDetails.getRegId());
				servicePdfServiceImpl.save(servicePdf);
				}

			} catch (Exception e) {
				Logger.error(e.getMessage());
			}

		}
		 
		else if (patientServiceDetails.getServiceName().equalsIgnoreCase("SERUM CREATININE")){

			byte[] pdfBytes = null;
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			Document document = new Document();

			try {
				PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);

				MyFooter event = new MyFooter(principal,userServiceImpl);
				writer.setPageEvent(event);
				Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
				Font redFont3 = new Font(Font.FontFamily.TIMES_ROMAN, 17, Font.BOLD);
				Font redFont2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.ITALIC);
				Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
				Font headFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.UNDERLINE|Font.BOLD);
				document.open();
				document.add(pq);

				Paragraph p51 = new Paragraph("UDBHAVA HOSPITALS", redFont);
				p51.add("\n");
				p51.add("\n \n ");
				
				p51.setAlignment(Element.ALIGN_CENTER);
				document.add(p51);
				
				Font redFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

				 date = Calendar.getInstance().getTime();
				 formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
				String today = formatter.format(date).toString();

				PdfPTable table2 = new PdfPTable(2);
				
				PdfPCell cell1 = new PdfPCell();
				table2.setWidthPercentage(100f);	
				cell1.setBorder(Rectangle.BOTTOM|Rectangle.TOP);
				
				PdfPTable table21 = new PdfPTable(6);
				table21.setWidths(new float[] { 5f,1f,5f,5f,1f,6.5f});
				 table21.setSpacingBefore(10);
				

				PdfPCell hcell1;
				hcell1 = new PdfPCell(
						new Phrase("Patient Name", redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(
						new Phrase(":", redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(
						new Phrase(patientRegistration.getPatientDetails().getTitle() +". "+ patientRegistration.getPatientDetails().getFirstName()
								+ " " + patientRegistration.getPatientDetails().getLastName(), redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase("Age/Gender", redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase(":", redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getAge()
						+ "/" + patientRegistration.getPatientDetails().getGender(), redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				
				
				PdfPCell hcell4;
				hcell4 = new PdfPCell(
						new Phrase("Admission No", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(
						new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(
						new Phrase(patientRegistration.getRegId(), redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				


				if(admittedWard!=null)
				{
				hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(admittedWard, redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				}
				else
				{
					hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					
					hcell4 = new PdfPCell(new Phrase(":", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					hcell4 = new PdfPCell(new Phrase("", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);

				}



				PdfPCell hcell15;
				hcell15 = new PdfPCell(
						new Phrase("Admission Date", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				hcell15 = new PdfPCell(
						new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				hcell15 = new PdfPCell(
						new Phrase(pdfadmissionDate, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				
				if(roomName!=null)
				{
				hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(roomName, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				}
				else
				{
					hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase(":", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase("", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
				}
				PdfPCell hcell16;
				hcell16 = new PdfPCell(new Phrase(
						"Bill No/UMR No", redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(
						":", redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getUmr(), redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);

				hcell16 = new PdfPCell(new Phrase("Bill Date", redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(":", redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(bill, redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);

				PdfPCell hcell17;
				hcell17 = new PdfPCell(new Phrase(
						"Consultant",
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(
						":",
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(
						patientRegistration.getPatientDetails().getConsultant(),
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);

				hcell17 = new PdfPCell(new Phrase("Report Date", redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(":", redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(today, redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				cell1.setColspan(2);
				cell1.addElement(table21);
				table2.addCell(cell1);
             //  table2.addCell(cell1);
               document.add(table2);

				

				
				Paragraph p11 = new Paragraph("DEPARTMENT OF BIO CHEMISTRY",headFont1);
				p11.setAlignment(Element.ALIGN_CENTER);
								document.add(p11);
								
								Paragraph p305=new Paragraph("\n ");
								document.add(p305);

				PdfPTable table3 = new PdfPTable(3);
				table3.setWidths(new float[] { 5f, 6f, 9f });

				PdfPCell hcell01;
				hcell01 = new PdfPCell(new Phrase("INVESTIGATION", headFont1));
				hcell01.setBorder(Rectangle.NO_BORDER);
				hcell01.setPaddingLeft(-50f);
				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
				table3.addCell(hcell01);

				hcell01 = new PdfPCell(new Phrase("RESULT", headFont1));
				hcell01.setBorder(Rectangle.NO_BORDER);
				 hcell01.setPaddingRight(-40f);
				hcell01.setHorizontalAlignment(Element.ALIGN_CENTER);
				table3.addCell(hcell01);

				hcell01 = new PdfPCell(new Phrase("NORMAL RANGE", headFont1));
				hcell01.setBorder(Rectangle.NO_BORDER);
				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell01.setPaddingLeft(85f);
				table3.addCell(hcell01);

				for (RefMeasureDetails refMeasureDetailsInfoPdf : refMeasureDetails) {

						PdfPCell hcell021;
						hcell021 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getMeasure(), redFont1));
						hcell021.setBorder(Rectangle.NO_BORDER);
						hcell021.setPaddingLeft(-50f);
						
						hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
						table3.addCell(hcell021);

						if(refMeasureDetailsInfoPdf.getDimension()!=null)
						{
							hcell021 = new PdfPCell(new Phrase(": " + refMeasureDetailsInfoPdf.getValue() + " " + refMeasureDetailsInfoPdf.getDimension(),redFont1));
							hcell021.setBorder(Rectangle.NO_BORDER);
							hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
							hcell021.setPaddingLeft(60f);
							table3.addCell(hcell021);
						}
						else
						{
							hcell021 = new PdfPCell(new Phrase(": " + refMeasureDetailsInfoPdf.getValue(),redFont1));
							hcell021.setBorder(Rectangle.NO_BORDER);
							hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
							hcell021.setPaddingLeft(60f);
							table3.addCell(hcell021);
						}
						if(refMeasureDetailsInfoPdf.getDimension()!=null) {
						hcell021 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getRange()+"  "+refMeasureDetailsInfoPdf.getDimension(), redFont1));
						hcell021.setBorder(Rectangle.NO_BORDER);
						hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell021.setPaddingLeft(89f);
						table3.addCell(hcell021);
						}
						else
						{
							hcell021 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getRange(), redFont1));
							hcell021.setBorder(Rectangle.NO_BORDER);
							hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
							hcell021.setPaddingLeft(89f);
							table3.addCell(hcell021);
						}
											}
				
				document.add(table3);
				Paragraph p01 = new Paragraph();
				p01.add("\n");
				p01.add("\n");
				document.add(p01);
				if(patientServiceDetails.getComment().isEmpty())
				{
					Paragraph p27 = new Paragraph("\n Findings:", headFont);
					document.add(p27);
					Paragraph p28 = new Paragraph("N/A", redFont1);
					document.add(p28);
				}else
				{
					Paragraph p27 = new Paragraph("\n Findings:", headFont);

					Paragraph p28 = new Paragraph(patientServiceDetails.getComment(), redFont1);
					document.add(p27);
					document.add(p28);
				}
				Paragraph p7 = new Paragraph("*Please correlate with clinical findings if necessary discuss", redFont2);
				p7.setAlignment(Element.ALIGN_CENTER);

				p7.add("\n");
				p7.add("\n");
				document.add(p7);

				Chunk cnd = new Chunk(new VerticalPositionMark());
				Paragraph p19 = new Paragraph(patientRegistration.getPatientDetails().getConsultant(), headFont);
				p19.setAlignment(Element.ALIGN_RIGHT);
				document.add(p19);

				Paragraph p18 = new Paragraph("LAB INCHARGE", headFont);
				p18.add(cnd);

				p18.add("(" + patientRegistration.getVuserD().getDoctorDetails().getSpecilization() + ")");
				document.add(p18);
			
				document.close();

				pdfBytes = byteArrayOutputStream.toByteArray();
				List<ServicePdf> servicePdfs=servicePdfServiceImpl.findByRegAndMeasureName(regId,patientServiceDetails.getServiceName());
				if(!servicePdfs.isEmpty())
				{
					servicePdf = new ServicePdf();
					servicePdf.setSid(servicePdfs.get(0).getSid());
					servicePdf.setData(pdfBytes);
					servicePdf.setFileName(servicePdfs.get(0).getFileName());
					servicePdf.setFileuri(servicePdfs.get(0).getFileuri());
					servicePdf.setRegId(servicePdfs.get(0).getRegId());
					servicePdfServiceImpl.save(servicePdf);

				}
				else
				{
				String uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/lab/servicePdf/viewFile/")
						.path(servicePdfServiceImpl.getNextLabId()).toUriString();

				servicePdf = new ServicePdf();
				servicePdf.setSid(servicePdfServiceImpl.getNextLabId());
				servicePdf.setData(pdfBytes);
				servicePdf.setFileName(patientServiceDetails.getServiceName());
				servicePdf.setFileuri(uri);
				servicePdf.setRegId(patientServiceDetails.getRegId());
				servicePdfServiceImpl.save(servicePdf);
				}


			} catch (Exception e) {
				Logger.error(e.getMessage());
			}
		}
		else if (patientServiceDetails.getServiceName().equalsIgnoreCase("BLOOD UREA")){

			byte[] pdfBytes = null;
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			Document document = new Document();

			try {
				PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);

				MyFooter event = new MyFooter(principal,userServiceImpl);
				writer.setPageEvent(event);

				Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
				Font redFont3 = new Font(Font.FontFamily.TIMES_ROMAN, 17, Font.BOLD);
				Font redFont2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.ITALIC);
				Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
				Font headFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD|Font.UNDERLINE);
				Font headFont2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD|Font.UNDERLINE);
				
				
				document.open();
				document.add(pq);

				Paragraph p51 = new Paragraph("UDBHAVA HOSPITALS", redFont);
				p51.add("\n");
				p51.add("\n \n \n");
				
				p51.setAlignment(Element.ALIGN_CENTER);
				document.add(p51);
			
				Font redFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

				 date = Calendar.getInstance().getTime();
				 formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
				String today = formatter.format(date).toString();

				PdfPTable table2 = new PdfPTable(2);
				
				PdfPCell cell1 = new PdfPCell();
				table2.setWidthPercentage(100f);	
				cell1.setBorder(Rectangle.BOTTOM|Rectangle.TOP);
				
				PdfPTable table21 = new PdfPTable(6);
				table21.setWidths(new float[] { 5f,1f,5f,5f,1f,6.5f});
				 table21.setSpacingBefore(10);
				

				PdfPCell hcell1;
				hcell1 = new PdfPCell(
						new Phrase("Patient Name", redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(
						new Phrase(":", redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(
						new Phrase(patientRegistration.getPatientDetails().getTitle() +". "+ patientRegistration.getPatientDetails().getFirstName()
								+ " " + patientRegistration.getPatientDetails().getLastName(), redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase("Age/Gender", redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase(":", redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getAge()
						+ "/" + patientRegistration.getPatientDetails().getGender(), redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				
				
				PdfPCell hcell4;
				hcell4 = new PdfPCell(
						new Phrase("Admission No", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(
						new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(
						new Phrase(patientRegistration.getRegId(), redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				


				if(admittedWard!=null)
				{
				hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(admittedWard, redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				}
				else
				{
					hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					
					hcell4 = new PdfPCell(new Phrase(":", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					hcell4 = new PdfPCell(new Phrase("", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);

				}



				PdfPCell hcell15;
				hcell15 = new PdfPCell(
						new Phrase("Admission Date", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				hcell15 = new PdfPCell(
						new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				hcell15 = new PdfPCell(
						new Phrase(pdfadmissionDate, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				
				if(roomName!=null)
				{
				hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(roomName, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				}
				else
				{
					hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase(":", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase("", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
				}
				PdfPCell hcell16;
				hcell16 = new PdfPCell(new Phrase(
						"Bill No/UMR No", redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(
						":", redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getUmr(), redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);

				hcell16 = new PdfPCell(new Phrase("Bill Date", redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(":", redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(bill, redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);

				PdfPCell hcell17;
				hcell17 = new PdfPCell(new Phrase(
						"Consultant",
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(
						":",
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(
						patientRegistration.getPatientDetails().getConsultant(),
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);

				hcell17 = new PdfPCell(new Phrase("Report Date", redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(":", redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(today, redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				cell1.setColspan(2);
				cell1.addElement(table21);
				table2.addCell(cell1);
             //  table2.addCell(cell1);
               document.add(table2);


				Paragraph p11 = new Paragraph("BIOCHEMISTRY",headFont1);
				p11.setAlignment(Element.ALIGN_CENTER);
				document.add(p11);

				Paragraph p101 = new Paragraph("\n",headFont);
				p101.setAlignment(Element.ALIGN_CENTER);
				document.add(p101);

				
				PdfPTable table3 = new PdfPTable(3);
				table3.setWidths(new float[] { 5f, 6f, 9f });

				PdfPCell hcell01;
				hcell01 = new PdfPCell(new Phrase("INVESTIGATION", headFont2));
				hcell01.setBorder(Rectangle.NO_BORDER);
				hcell01.setPaddingLeft(-50f);
				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
				table3.addCell(hcell01);

				hcell01 = new PdfPCell(new Phrase("RESULT", headFont2));
				hcell01.setBorder(Rectangle.NO_BORDER);
				 hcell01.setPaddingRight(-40f);
				hcell01.setHorizontalAlignment(Element.ALIGN_CENTER);
				table3.addCell(hcell01);

				hcell01 = new PdfPCell(new Phrase("NORMAL RANGE", headFont2));
				hcell01.setBorder(Rectangle.NO_BORDER);
				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell01.setPaddingLeft(85f);
				table3.addCell(hcell01);

				for (RefMeasureDetails refMeasureDetailsInfoPdf : refMeasureDetails) {

						PdfPCell hcell021;
						hcell021 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getMeasure(), redFont1));
						hcell021.setBorder(Rectangle.NO_BORDER);
						hcell021.setPaddingLeft(-50f);
						
						hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
						table3.addCell(hcell021);

						if(refMeasureDetailsInfoPdf.getDimension()!=null)
						{
							hcell021 = new PdfPCell(new Phrase(": " + refMeasureDetailsInfoPdf.getValue() + " " + refMeasureDetailsInfoPdf.getDimension(),redFont1));
							hcell021.setBorder(Rectangle.NO_BORDER);
							hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
							hcell021.setPaddingLeft(60f);
							table3.addCell(hcell021);
						}
						else
						{
							hcell021 = new PdfPCell(new Phrase(": " + refMeasureDetailsInfoPdf.getValue(),redFont1));
							hcell021.setBorder(Rectangle.NO_BORDER);
							hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
							hcell021.setPaddingLeft(50f);
							table3.addCell(hcell021);
						}
						if(refMeasureDetailsInfoPdf.getDimension()!=null) {
						hcell021 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getRange()+" "+refMeasureDetailsInfoPdf.getDimension(), redFont1));
						hcell021.setBorder(Rectangle.NO_BORDER);
						hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell021.setPaddingLeft(89f);
						table3.addCell(hcell021);
						}
						else
						{
							hcell021 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getRange(), redFont1));
							hcell021.setBorder(Rectangle.NO_BORDER);
							hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
							hcell021.setPaddingLeft(89f);
							table3.addCell(hcell021);
						}
						System.out.println(refMeasureDetailsInfoPdf.getMethod());
											}
				
				
				document.add(table3);

				Paragraph p01 = new Paragraph();
				
				p01.add("\n");
				p01.add("\n");
				
				document.add(p01);


				if(patientServiceDetails.getComment().isEmpty())
				{
					Paragraph p27 = new Paragraph("\n Findings:", headFont);
					document.add(p27);
					Paragraph p28 = new Paragraph("N/A", redFont2);
					document.add(p28);

				}else
				{
					Paragraph p27 = new Paragraph("\n Findings:", headFont);

					Paragraph p28 = new Paragraph(patientServiceDetails.getComment(), redFont2);
					document.add(p27);
					document.add(p28);
				}
				Paragraph p7 = new Paragraph("*Please correlate with clinical findings if necessary discuss", redFont2);
				p7.setAlignment(Element.ALIGN_CENTER);

				p7.add("\n \n");
				document.add(p7);

				Chunk cnd = new Chunk(new VerticalPositionMark());
				Paragraph p19 = new Paragraph(patientRegistration.getPatientDetails().getConsultant(), headFont);
				p19.setAlignment(Element.ALIGN_RIGHT);
				document.add(p19);

				Paragraph p18 = new Paragraph("LAB INCHARGE", headFont);
				p18.add(cnd);

				p18.add("(" + patientRegistration.getVuserD().getDoctorDetails().getSpecilization() + ")");
				document.add(p18);
				document.close();

				pdfBytes = byteArrayOutputStream.toByteArray();
				List<ServicePdf> servicePdfs=servicePdfServiceImpl.findByRegAndMeasureName(regId,patientServiceDetails.getServiceName());
				if(!servicePdfs.isEmpty())
				{
					servicePdf = new ServicePdf();
					servicePdf.setSid(servicePdfs.get(0).getSid());
					servicePdf.setData(pdfBytes);
					servicePdf.setFileName(servicePdfs.get(0).getFileName());
					servicePdf.setFileuri(servicePdfs.get(0).getFileuri());
					servicePdf.setRegId(servicePdfs.get(0).getRegId());
					servicePdfServiceImpl.save(servicePdf);

				}
				else
				{
				String uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/lab/servicePdf/viewFile/")
						.path(servicePdfServiceImpl.getNextLabId()).toUriString();

				servicePdf = new ServicePdf();
				servicePdf.setSid(servicePdfServiceImpl.getNextLabId());
				servicePdf.setData(pdfBytes);
				servicePdf.setFileName(patientServiceDetails.getServiceName());
				servicePdf.setFileuri(uri);
				servicePdf.setRegId(patientServiceDetails.getRegId());
				servicePdfServiceImpl.save(servicePdf);
				}


			} catch (Exception e) {
				Logger.error(e.getMessage());
			}
		}
		else if (patientServiceDetails.getServiceName().equalsIgnoreCase("BLOOD GROUP AND Rh TYPE")){

			byte[] pdfBytes = null;
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			Document document = new Document();

			try {
				PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);

				MyFooter event = new MyFooter(principal,userServiceImpl);
				writer.setPageEvent(event);

				Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
				Font redFont3 = new Font(Font.FontFamily.TIMES_ROMAN, 17, Font.BOLD);
				Font redFont2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.ITALIC);
				Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
				Font headFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD|Font.UNDERLINE);
				Font headFont2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD|Font.UNDERLINE);

				
				document.open();
				document.add(pq);
				Paragraph p51 = new Paragraph("UDBHAVA HOSPITALS", redFont);
				p51.add("\n");
				p51.add("\n \n \n");
				
				p51.setAlignment(Element.ALIGN_CENTER);
				document.add(p51);
				Font redFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

				 date = Calendar.getInstance().getTime();
				 formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
				String today = formatter.format(date).toString();

				PdfPTable table2 = new PdfPTable(2);
				
				PdfPCell cell1 = new PdfPCell();
				table2.setWidthPercentage(100f);	
				cell1.setBorder(Rectangle.BOTTOM|Rectangle.TOP);
				
				PdfPTable table21 = new PdfPTable(6);
				table21.setWidths(new float[] { 5f,1f,5f,5f,1f,6.5f});
				 table21.setSpacingBefore(10);
				

				PdfPCell hcell1;
				hcell1 = new PdfPCell(
						new Phrase("Patient Name", redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(
						new Phrase(":", redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(
						new Phrase(patientRegistration.getPatientDetails().getTitle() +". "+ patientRegistration.getPatientDetails().getFirstName()
								+ " " + patientRegistration.getPatientDetails().getLastName(), redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase("Age/Gender", redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase(":", redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getAge()
						+ "/" + patientRegistration.getPatientDetails().getGender(), redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				
				
				PdfPCell hcell4;
				hcell4 = new PdfPCell(
						new Phrase("Admission No", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(
						new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(
						new Phrase(patientRegistration.getRegId(), redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				


				if(admittedWard!=null)
				{
				hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(admittedWard, redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				}
				else
				{
					hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					
					hcell4 = new PdfPCell(new Phrase(":", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					hcell4 = new PdfPCell(new Phrase("", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);

				}



				PdfPCell hcell15;
				hcell15 = new PdfPCell(
						new Phrase("Admission Date", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				hcell15 = new PdfPCell(
						new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				hcell15 = new PdfPCell(
						new Phrase(pdfadmissionDate, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				
				if(roomName!=null)
				{
				hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(roomName, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				}
				else
				{
					hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase(":", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase("", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
				}
				PdfPCell hcell16;
				hcell16 = new PdfPCell(new Phrase(
						"Bill No/UMR No", redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(
						":", redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getUmr(), redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);

				hcell16 = new PdfPCell(new Phrase("Bill Date", redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(":", redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(bill, redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);

				PdfPCell hcell17;
				hcell17 = new PdfPCell(new Phrase(
						"Consultant",
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(
						":",
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(
						patientRegistration.getPatientDetails().getConsultant(),
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);

				hcell17 = new PdfPCell(new Phrase("Report Date", redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(":", redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(today, redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				cell1.setColspan(2);
				cell1.addElement(table21);
				table2.addCell(cell1);
             //  table2.addCell(cell1);
               document.add(table2);


				Paragraph p11 = new Paragraph(" DEPARTMENT OF HAEMOTOLOGY",headFont1);
				p11.setAlignment(Element.ALIGN_CENTER);
				document.add(p11);

				Paragraph p101 = new Paragraph("\n",headFont);
				p101.setAlignment(Element.ALIGN_CENTER);
				document.add(p101);

				
				PdfPTable table3 = new PdfPTable(2);
				table3.setWidths(new float[] { 5f, 6f });

				PdfPCell hcell01;
				hcell01 = new PdfPCell(new Phrase("INVESTIGATION", headFont2));
				hcell01.setBorder(Rectangle.NO_BORDER);
				hcell01.setPaddingLeft(-50f);
				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
				table3.addCell(hcell01);
		hcell01 = new PdfPCell(new Phrase("RESULT", headFont2));
				hcell01.setBorder(Rectangle.NO_BORDER);
				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell01.setPaddingLeft(10f);
				table3.addCell(hcell01);
				
				for (RefMeasureDetails refMeasureDetailsInfoPdf : refMeasureDetails) {

						PdfPCell hcell021;
						hcell021 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getMeasure(), redFont1));
						hcell021.setBorder(Rectangle.NO_BORDER);
						hcell021.setPaddingLeft(-50f);
						
						hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
						table3.addCell(hcell021);

						if(refMeasureDetailsInfoPdf.getDimension()!=null)
						{
							hcell021 = new PdfPCell(new Phrase(": " + refMeasureDetailsInfoPdf.getValue() + " " + refMeasureDetailsInfoPdf.getDimension(),redFont1));
							hcell021.setBorder(Rectangle.NO_BORDER);
							hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
							hcell021.setPaddingLeft(10f);
							table3.addCell(hcell021);
						}
						else
						{
							hcell021 = new PdfPCell(new Phrase(": " + refMeasureDetailsInfoPdf.getValue(),redFont1));
							hcell021.setBorder(Rectangle.NO_BORDER);
							hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
							hcell021.setPaddingLeft(10f);
							table3.addCell(hcell021);
						}
									}
				
				
				document.add(table3);

				Paragraph p01 = new Paragraph();
				
				p01.add("\n");
				p01.add("\n");
				
				document.add(p01);


				if(patientServiceDetails.getComment().isEmpty())
				{
					Paragraph p27 = new Paragraph("\n Findings:", headFont);
					document.add(p27);
					Paragraph p28 = new Paragraph("N/A", redFont2);
					document.add(p28);

				}else
				{
					Paragraph p27 = new Paragraph("\n Findings:", headFont);

					Paragraph p28 = new Paragraph(patientServiceDetails.getComment(), redFont2);
					document.add(p27);
					document.add(p28);
				}
				
				
				Paragraph p7 = new Paragraph("*Please correlate with clinical findings if necessary discuss", redFont2);
				p7.setAlignment(Element.ALIGN_CENTER);

				p7.add("\n");
				p7.add("\n");
				document.add(p7);

				Chunk cnd = new Chunk(new VerticalPositionMark());
				Paragraph p19 = new Paragraph(patientRegistration.getPatientDetails().getConsultant(), headFont);
				p19.setAlignment(Element.ALIGN_RIGHT);
				document.add(p19);

				Paragraph p18 = new Paragraph("LAB INCHARGE", headFont);
				p18.add(cnd);

				p18.add("(" + patientRegistration.getVuserD().getDoctorDetails().getSpecilization() + ")");
				document.add(p18);

			
				// ************************

				document.close();

				pdfBytes = byteArrayOutputStream.toByteArray();
				List<ServicePdf> servicePdfs=servicePdfServiceImpl.findByRegAndMeasureName(regId,patientServiceDetails.getServiceName());
				if(!servicePdfs.isEmpty())
				{
					servicePdf = new ServicePdf();
					servicePdf.setSid(servicePdfs.get(0).getSid());
					servicePdf.setData(pdfBytes);
					servicePdf.setFileName(servicePdfs.get(0).getFileName());
					servicePdf.setFileuri(servicePdfs.get(0).getFileuri());
					servicePdf.setRegId(servicePdfs.get(0).getRegId());
					servicePdfServiceImpl.save(servicePdf);

				}
				else
				{
				String uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/lab/servicePdf/viewFile/")
						.path(servicePdfServiceImpl.getNextLabId()).toUriString();

				servicePdf = new ServicePdf();
				servicePdf.setSid(servicePdfServiceImpl.getNextLabId());
				servicePdf.setData(pdfBytes);
				servicePdf.setFileName(patientServiceDetails.getServiceName());
				servicePdf.setFileuri(uri);
				servicePdf.setRegId(patientServiceDetails.getRegId());
				servicePdfServiceImpl.save(servicePdf);
				}


			} catch (Exception e) {
				Logger.error(e.getMessage());
			}
		}		
		else if (patientServiceDetails.getServiceName().equalsIgnoreCase("HIV I & II")){

			byte[] pdfBytes = null;
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			Document document = new Document();

			try {
				PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);

				MyFooter event = new MyFooter(principal,userServiceImpl);
				writer.setPageEvent(event);


				Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
				Font redFont3 = new Font(Font.FontFamily.TIMES_ROMAN, 17, Font.BOLD);
				Font redFont2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.ITALIC);
				Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
				Font headFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD|Font.UNDERLINE);
				Font headFont2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD|Font.UNDERLINE);

				document.open();

				document.add(pq);

				Paragraph p51 = new Paragraph("UDBHAVA HOSPITALS", redFont);
				p51.add("\n \n");
				p51.add("\n \n");
				
				p51.setAlignment(Element.ALIGN_CENTER);
				document.add(p51);
			
				Font redFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

				 date = Calendar.getInstance().getTime();
				 formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
				String today = formatter.format(date).toString();


				PdfPTable table2 = new PdfPTable(2);
				
				PdfPCell cell1 = new PdfPCell();
				table2.setWidthPercentage(100f);	
				cell1.setBorder(Rectangle.BOTTOM|Rectangle.TOP);
				
				PdfPTable table21 = new PdfPTable(6);
				table21.setWidths(new float[] { 5f,1f,5f,5f,1f,6.5f});
				 table21.setSpacingBefore(10);
				

				PdfPCell hcell1;
				hcell1 = new PdfPCell(
						new Phrase("Patient Name", redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(
						new Phrase(":", redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(
						new Phrase(patientRegistration.getPatientDetails().getTitle() +". "+ patientRegistration.getPatientDetails().getFirstName()
								+ " " + patientRegistration.getPatientDetails().getLastName(), redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase("Age/Gender", redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase(":", redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				
				hcell1 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getAge()
						+ "/" + patientRegistration.getPatientDetails().getGender(), redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(10f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				
				
				PdfPCell hcell4;
				hcell4 = new PdfPCell(
						new Phrase("Admission No", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(
						new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(
						new Phrase(patientRegistration.getRegId(), redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);
				
				


				if(admittedWard!=null)
				{
				hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(":", redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				hcell4 = new PdfPCell(new Phrase(admittedWard, redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(10f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				}
				else
				{
					hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					
					hcell4 = new PdfPCell(new Phrase(":", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					hcell4 = new PdfPCell(new Phrase("", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);

				}



				PdfPCell hcell15;
				hcell15 = new PdfPCell(
						new Phrase("Admission Date", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				hcell15 = new PdfPCell(
						new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				hcell15 = new PdfPCell(
						new Phrase(pdfadmissionDate, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				
				if(roomName!=null)
				{
				hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(":", redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				
				hcell15 = new PdfPCell(new Phrase(roomName, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(10f);
				table21.addCell(hcell15);
				}
				else
				{
					hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase(":", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase("", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
				}
				PdfPCell hcell16;
				hcell16 = new PdfPCell(new Phrase(
						"Bill No/UMR No", redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(
						":", redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getUmr(), redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);

				hcell16 = new PdfPCell(new Phrase("Bill Date", redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(":", redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);
				
				hcell16 = new PdfPCell(new Phrase(bill, redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(10f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);

				PdfPCell hcell17;
				hcell17 = new PdfPCell(new Phrase(
						"Consultant",
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(
						":",
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(
						patientRegistration.getPatientDetails().getConsultant(),
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);

				hcell17 = new PdfPCell(new Phrase("Report Date", redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(":", redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				hcell17 = new PdfPCell(new Phrase(today, redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(10f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				cell1.setColspan(2);
				cell1.addElement(table21);
				table2.addCell(cell1);
             //  table2.addCell(cell1);
               document.add(table2);


				Paragraph p11 = new Paragraph("SEROLOGYREPORT", headFont1);
				p11.setAlignment(Element.ALIGN_CENTER);
				document.add(p11);

				PdfPTable table3 = new PdfPTable(2);
				table3.setWidths(new float[] { 5f, 6f });

				PdfPCell hcell01;
				hcell01 = new PdfPCell(new Phrase("\n INVESTIGATION", headFont2));
				hcell01.setBorder(Rectangle.NO_BORDER);
				hcell01.setPaddingLeft(-50f);
				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
				table3.addCell(hcell01);
		hcell01 = new PdfPCell(new Phrase("\n RESULT", headFont2));
				hcell01.setBorder(Rectangle.NO_BORDER);
				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell01.setPaddingLeft(10f);
				table3.addCell(hcell01);
				
				

				for (RefMeasureDetails refMeasureDetailsInfoPdf : refMeasureDetails) {

						PdfPCell hcell021;
						hcell021 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getMeasure(), redFont1));
						hcell021.setBorder(Rectangle.NO_BORDER);
						hcell021.setPaddingLeft(-50f);
						
						hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
						table3.addCell(hcell021);

						if(refMeasureDetailsInfoPdf.getDimension()!=null)
						{
							hcell021 = new PdfPCell(new Phrase(": " + refMeasureDetailsInfoPdf.getValue() + " " + refMeasureDetailsInfoPdf.getDimension(),redFont1));
							hcell021.setBorder(Rectangle.NO_BORDER);
							hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
							hcell021.setPaddingLeft(10f);
							table3.addCell(hcell021);
						}
						else
						{
							hcell021 = new PdfPCell(new Phrase(": " + refMeasureDetailsInfoPdf.getValue(),redFont1));
							hcell021.setBorder(Rectangle.NO_BORDER);
							hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
							hcell021.setPaddingLeft(10f);
							table3.addCell(hcell021);
						}
						PdfPCell hcell011;
						hcell011 = new PdfPCell(new Phrase("\nMethod  :", headFont));
						hcell011.setBorder(Rectangle.NO_BORDER);
						hcell011.setPaddingLeft(-50f);
						hcell011.setHorizontalAlignment(Element.ALIGN_LEFT);
						table3.addCell(hcell011);
			        	hcell011 = new PdfPCell(new Phrase("\n"+refMeasureDetailsInfoPdf.getMethod(), redFont1));
						hcell011.setBorder(Rectangle.NO_BORDER);
						
						hcell011.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell011.setPaddingLeft(-190f);
						table3.addCell(hcell011);
								}
				
				document.add(table3);

				Paragraph p01 = new Paragraph();
				
				p01.add("\n");
				p01.add("\n");
				
				document.add(p01);


				if(patientServiceDetails.getComment().isEmpty())
				{
					Paragraph p27 = new Paragraph("\n Findings:", headFont);
					document.add(p27);
					Paragraph p28 = new Paragraph("N/A", redFont2);
					document.add(p28);

				}else
				{
					Paragraph p27 = new Paragraph("\n Findings:", headFont);

					Paragraph p28 = new Paragraph(patientServiceDetails.getComment(), redFont1);
					document.add(p27);
					document.add(p28);
				}
				
				
				Paragraph p7 = new Paragraph("*Please correlate with clinical findings if necessary discuss", redFont2);
				p7.setAlignment(Element.ALIGN_CENTER);

				p7.add("\n");
				p7.add("\n");
				document.add(p7);

				Chunk cnd = new Chunk(new VerticalPositionMark());
				Paragraph p19 = new Paragraph(patientRegistration.getPatientDetails().getConsultant(), headFont);
				p19.setAlignment(Element.ALIGN_RIGHT);
				document.add(p19);

				Paragraph p18 = new Paragraph("LAB INCHARGE", headFont);
				p18.add(cnd);

				p18.add("(" + patientRegistration.getVuserD().getDoctorDetails().getSpecilization() + ")");
				document.add(p18);

			
				// ************************

				document.close();

				pdfBytes = byteArrayOutputStream.toByteArray();
				List<ServicePdf> servicePdfs=servicePdfServiceImpl.findByRegAndMeasureName(regId,patientServiceDetails.getServiceName());
				if(!servicePdfs.isEmpty())
				{
					servicePdf = new ServicePdf();
					servicePdf.setSid(servicePdfs.get(0).getSid());
					servicePdf.setData(pdfBytes);
					servicePdf.setFileName(servicePdfs.get(0).getFileName());
					servicePdf.setFileuri(servicePdfs.get(0).getFileuri());
					servicePdf.setRegId(servicePdfs.get(0).getRegId());
					servicePdfServiceImpl.save(servicePdf);

				}
				else
				{
				String uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/lab/servicePdf/viewFile/")
						.path(servicePdfServiceImpl.getNextLabId()).toUriString();

				servicePdf = new ServicePdf();
				servicePdf.setSid(servicePdfServiceImpl.getNextLabId());
				servicePdf.setData(pdfBytes);
				servicePdf.setFileName(patientServiceDetails.getServiceName());
				servicePdf.setFileuri(uri);
				servicePdf.setRegId(patientServiceDetails.getRegId());
				servicePdfServiceImpl.save(servicePdf);
				}

			} catch (Exception e) {
				Logger.error(e.getMessage());
			}

	}		 
		else if (patientServiceDetails.getServiceName().equalsIgnoreCase("RH TYPE")){

			byte[] pdfBytes = null;
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			Document document = new Document();

			try {
				PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);

				MyFooter event = new MyFooter(principal,userServiceImpl);
				writer.setPageEvent(event);


				Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
				Font redFont3 = new Font(Font.FontFamily.TIMES_ROMAN, 17, Font.BOLD);
				Font redFont2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.ITALIC);
				Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
				Font headFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD|Font.UNDERLINE);
				Font headFont2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD|Font.UNDERLINE);

				
				document.open();

				document.add(pq);

				Paragraph p51 = new Paragraph("UDBHAVA HOSPITALS", redFont);
				p51.add("\n");
				p51.add("\n \n \n");
				
				p51.setAlignment(Element.ALIGN_CENTER);
				document.add(p51);
			
				Font redFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

				 date = Calendar.getInstance().getTime();
				 formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
				String today = formatter.format(date).toString();
				PdfPTable table2 = new PdfPTable(2);
				
				PdfPCell cell1 = new PdfPCell();
				table2.setWidthPercentage(100f);	
				cell1.setBorder(Rectangle.BOTTOM|Rectangle.TOP);
				
				PdfPTable table21 = new PdfPTable(2);
				table21.setWidths(new float[] { 5f, 7.5f });
				 table21.setSpacingBefore(10);
				

				PdfPCell hcell1;
				hcell1 = new PdfPCell(
						new Phrase("Patient Name            : " +patientRegistration.getPatientDetails().getTitle() +". "+ patientRegistration.getPatientDetails().getFirstName()
								+ " " + patientRegistration.getPatientDetails().getLastName(), redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase("Age/Gender    : " + patientRegistration.getPatientDetails().getAge()
						+ "/" + patientRegistration.getPatientDetails().getGender(), redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(80f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				PdfPCell hcell4;
				hcell4 = new PdfPCell(
						new Phrase("Admission No           : " + patientRegistration.getRegId(), redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);

				if(admittedWard!=null)
				{
				hcell4 = new PdfPCell(new Phrase("Ward Name    : " + admittedWard, redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(80f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				}
				else
				{
					hcell4 = new PdfPCell(new Phrase("Ward Name    : " + " ", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(80f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);

				}



				PdfPCell hcell15;
				hcell15 = new PdfPCell(
						new Phrase("Admission Date        : " + pdfadmissionDate, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				if(roomName!=null)
				{
				hcell15 = new PdfPCell(new Phrase("Room Name   : " + roomName, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(80f);
				table21.addCell(hcell15);
				}
				else
				{
					hcell15 = new PdfPCell(new Phrase("Room Name   : " + " ", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(80f);
					table21.addCell(hcell15);
					
				}
				PdfPCell hcell16;
				hcell16 = new PdfPCell(new Phrase(
						"Bill No/UMR No      : " + patientRegistration.getPatientDetails().getUmr(), redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);

				hcell16 = new PdfPCell(new Phrase("Bill Date         : " + bill, redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(80f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);

				PdfPCell hcell17;
				hcell17 = new PdfPCell(new Phrase(
						"Consultant                 : " + patientRegistration.getPatientDetails().getConsultant(),
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);

				hcell17 = new PdfPCell(new Phrase("Report Date    : " + today, redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(80f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				cell1.setColspan(2);
				cell1.addElement(table21);
				table2.addCell(cell1);
             //  table2.addCell(cell1);
               document.add(table2);

               Paragraph p11 = new Paragraph("DEPARTMENT OF HAEMATOLOGY",headFont1);
				p11.setAlignment(Element.ALIGN_CENTER);
				document.add(p11);

				PdfPTable table3 = new PdfPTable(2);
				table3.setWidths(new float[] { 5f, 6f });

				PdfPCell hcell01;
				hcell01 = new PdfPCell(new Phrase("\n INVESTIGATION", headFont2));
				hcell01.setBorder(Rectangle.NO_BORDER);
				hcell01.setPaddingLeft(-50f);
				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
				table3.addCell(hcell01);
		hcell01 = new PdfPCell(new Phrase("\n RESULT", headFont2));
				hcell01.setBorder(Rectangle.NO_BORDER);
				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell01.setPaddingLeft(10f);
				table3.addCell(hcell01);
				
				

				for (RefMeasureDetails refMeasureDetailsInfoPdf : refMeasureDetails) {

						PdfPCell hcell021;
						hcell021 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getMeasure(), redFont1));
						hcell021.setBorder(Rectangle.NO_BORDER);
						hcell021.setPaddingLeft(-50f);
						
						hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
						table3.addCell(hcell021);

						if(refMeasureDetailsInfoPdf.getDimension()!=null)
						{
							hcell021 = new PdfPCell(new Phrase(": " + refMeasureDetailsInfoPdf.getValue() + " " + refMeasureDetailsInfoPdf.getDimension(),redFont1));
							hcell021.setBorder(Rectangle.NO_BORDER);
							hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
							hcell021.setPaddingLeft(10f);
							table3.addCell(hcell021);
						}
						else
						{
							hcell021 = new PdfPCell(new Phrase(": " + refMeasureDetailsInfoPdf.getValue(),redFont1));
							hcell021.setBorder(Rectangle.NO_BORDER);
							hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
							hcell021.setPaddingLeft(10f);
							table3.addCell(hcell021);
						}
									}
				
				
				document.add(table3);

				Paragraph p01 = new Paragraph();
				
				p01.add("\n");
				p01.add("\n");
				
				document.add(p01);


				if(patientServiceDetails.getComment().isEmpty())
				{
					Paragraph p27 = new Paragraph("\n Findings:", headFont);
					document.add(p27);
					Paragraph p28 = new Paragraph("N/A", redFont2);
					document.add(p28);

				}else
				{
					Paragraph p27 = new Paragraph("\n Findings:", headFont);

					Paragraph p28 = new Paragraph(patientServiceDetails.getComment(), redFont2);
					document.add(p27);
					document.add(p28);
				}
				
				
				Paragraph p7 = new Paragraph("*Please correlate with clinical findings if necessary discuss", redFont2);
				p7.setAlignment(Element.ALIGN_CENTER);

				p7.add("\n");
				p7.add("\n");
				document.add(p7);

				Chunk cnd = new Chunk(new VerticalPositionMark());
				Paragraph p19 = new Paragraph(patientRegistration.getPatientDetails().getConsultant(), headFont);
				p19.setAlignment(Element.ALIGN_RIGHT);
				document.add(p19);

				Paragraph p18 = new Paragraph("LAB INCHARGE", headFont);
				p18.add(cnd);

				p18.add("(" + patientRegistration.getVuserD().getDoctorDetails().getSpecilization() + ")");
				document.add(p18);

			
				// ************************

				document.close();

				pdfBytes = byteArrayOutputStream.toByteArray();
				List<ServicePdf> servicePdfs=servicePdfServiceImpl.findByRegAndMeasureName(regId,patientServiceDetails.getServiceName());
				if(!servicePdfs.isEmpty())
				{
					servicePdf = new ServicePdf();
					servicePdf.setSid(servicePdfs.get(0).getSid());
					servicePdf.setData(pdfBytes);
					servicePdf.setFileName(servicePdfs.get(0).getFileName());
					servicePdf.setFileuri(servicePdfs.get(0).getFileuri());
					servicePdf.setRegId(servicePdfs.get(0).getRegId());
					servicePdfServiceImpl.save(servicePdf);

				}
				else
				{
				String uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/lab/servicePdf/viewFile/")
						.path(servicePdfServiceImpl.getNextLabId()).toUriString();

				servicePdf = new ServicePdf();
				servicePdf.setSid(servicePdfServiceImpl.getNextLabId());
				servicePdf.setData(pdfBytes);
				servicePdf.setFileName(patientServiceDetails.getServiceName());
				servicePdf.setFileuri(uri);
				servicePdf.setRegId(patientServiceDetails.getRegId());
				servicePdfServiceImpl.save(servicePdf);
				}

			} catch (Exception e) {
				Logger.error(e.getMessage());
			}
		}
		 
		else if (patientServiceDetails.getServiceName().equalsIgnoreCase("DENGUE NS1 - ANTIGEN")){

			byte[] pdfBytes = null;
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			Document document = new Document();

			try {
				PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);

				MyFooter event = new MyFooter(principal,userServiceImpl);
				writer.setPageEvent(event);


				Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
				Font redFont3 = new Font(Font.FontFamily.TIMES_ROMAN, 17, Font.BOLD);
				Font redFont2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.ITALIC);
				Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
				Font headFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD|Font.UNDERLINE);
				Font headFont2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD|Font.UNDERLINE);

				
				document.open();

				document.add(pq);

				Paragraph p51 = new Paragraph("UDBHAVA HOSPITALS", redFont);
				p51.add("\n");
				p51.add("\n \n \n");
				
				p51.setAlignment(Element.ALIGN_CENTER);
				document.add(p51);
			
				Font redFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

				 date = Calendar.getInstance().getTime();
				 formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
				String today = formatter.format(date).toString();
PdfPTable table2 = new PdfPTable(2);
				
				PdfPCell cell1 = new PdfPCell();
				table2.setWidthPercentage(100f);	
				cell1.setBorder(Rectangle.BOTTOM|Rectangle.TOP);
				
				PdfPTable table21 = new PdfPTable(2);
				table21.setWidths(new float[] { 5f, 7.5f });
				 table21.setSpacingBefore(10);
				

				PdfPCell hcell1;
				hcell1 = new PdfPCell(
						new Phrase("Patient Name            : " +patientRegistration.getPatientDetails().getTitle() +". "+ patientRegistration.getPatientDetails().getFirstName()
								+ " " + patientRegistration.getPatientDetails().getLastName(), redFont1));

				hcell1.setBorder(Rectangle.NO_BORDER);
				// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell1.setPaddingLeft(-50f);
				table21.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase("Age/Gender    : " + patientRegistration.getPatientDetails().getAge()
						+ "/" + patientRegistration.getPatientDetails().getGender(), redFont1));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(80f);
				hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell1);

				PdfPCell hcell4;
				hcell4 = new PdfPCell(
						new Phrase("Admission No           : " + patientRegistration.getRegId(), redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-50f);
				table21.addCell(hcell4);

				if(admittedWard!=null)
				{
				hcell4 = new PdfPCell(new Phrase("Ward Name    : " + admittedWard, redFont1));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(80f);

				hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell4);
				
				}
				else
				{
					hcell4 = new PdfPCell(new Phrase("Ward Name    : " + " ", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(80f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);

				}



				PdfPCell hcell15;
				hcell15 = new PdfPCell(
						new Phrase("Admission Date        : " + pdfadmissionDate, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-50f);
				table21.addCell(hcell15);

				if(roomName!=null)
				{
				hcell15 = new PdfPCell(new Phrase("Room Name   : " + roomName, redFont1));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell15.setPaddingLeft(80f);
				table21.addCell(hcell15);
				}
				else
				{
					hcell15 = new PdfPCell(new Phrase("Room Name   : " + " ", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(80f);
					table21.addCell(hcell15);
					
				}
				PdfPCell hcell16;
				hcell16 = new PdfPCell(new Phrase(
						"Bill No/UMR No      : " + patientRegistration.getPatientDetails().getUmr(), redFont1));

				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-50f);
				table21.addCell(hcell16);

				hcell16 = new PdfPCell(new Phrase("Bill Date         : " + bill, redFont1));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(80f);
				hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell16);

				PdfPCell hcell17;
				hcell17 = new PdfPCell(new Phrase(
						"Consultant                 : " + patientRegistration.getPatientDetails().getConsultant(),
						redFont1));

				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-50f);
				table21.addCell(hcell17);

				hcell17 = new PdfPCell(new Phrase("Report Date    : " + today, redFont1));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(80f);
				hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
				table21.addCell(hcell17);
				
				cell1.setColspan(2);
				cell1.addElement(table21);
				table2.addCell(cell1);
             //  table2.addCell(cell1);
               document.add(table2);

               Paragraph p11 = new Paragraph("DENGUE SEROLOGY", headFont1);
				p11.setAlignment(Element.ALIGN_CENTER);
				document.add(p11);

				
				
				PdfPTable table3 = new PdfPTable(2);
				table3.setWidths(new float[] { 5f, 6f });

				PdfPCell hcell01;
				hcell01 = new PdfPCell(new Phrase("\n INVESTIGATION", headFont2));
				hcell01.setBorder(Rectangle.NO_BORDER);
				hcell01.setPaddingLeft(-50f);
				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
				table3.addCell(hcell01);
		hcell01 = new PdfPCell(new Phrase("\n RESULT", headFont2));
				hcell01.setBorder(Rectangle.NO_BORDER);
				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell01.setPaddingLeft(10f);
				table3.addCell(hcell01);
				
				

				for (RefMeasureDetails refMeasureDetailsInfoPdf : refMeasureDetails) {

						PdfPCell hcell021;
						hcell021 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getMeasure(), redFont1));
						hcell021.setBorder(Rectangle.NO_BORDER);
						hcell021.setPaddingLeft(-50f);
						
						hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
						table3.addCell(hcell021);

						if(refMeasureDetailsInfoPdf.getDimension()!=null)
						{
							hcell021 = new PdfPCell(new Phrase(": " + refMeasureDetailsInfoPdf.getValue() + " " + refMeasureDetailsInfoPdf.getDimension(),redFont1));
							hcell021.setBorder(Rectangle.NO_BORDER);
							hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
							hcell021.setPaddingLeft(10f);
							table3.addCell(hcell021);
						}
						else
						{
							hcell021 = new PdfPCell(new Phrase(": " + refMeasureDetailsInfoPdf.getValue(),redFont1));
							hcell021.setBorder(Rectangle.NO_BORDER);
							hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
							hcell021.setPaddingLeft(10f);
							table3.addCell(hcell021);
						}
						PdfPCell hcell011;
						hcell011 = new PdfPCell(new Phrase("\nMethod  :", headFont));
						hcell011.setBorder(Rectangle.NO_BORDER);
						hcell011.setPaddingLeft(-50f);
						hcell011.setHorizontalAlignment(Element.ALIGN_LEFT);
						table3.addCell(hcell011);
			        	hcell011 = new PdfPCell(new Phrase("\n"+refMeasureDetailsInfoPdf.getMethod(), redFont1));
						hcell011.setBorder(Rectangle.NO_BORDER);
						
						hcell011.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell011.setPaddingLeft(-190f);
						table3.addCell(hcell011);
								}
				
				document.add(table3);

				Paragraph p01 = new Paragraph();
				
				p01.add("\n");
				p01.add("\n");
				
				document.add(p01);


				if(patientServiceDetails.getComment().isEmpty())
				{
					Paragraph p27 = new Paragraph("\n Findings:", headFont);
					document.add(p27);
					Paragraph p28 = new Paragraph("N/A", redFont2);
					document.add(p28);

				}else
				{
					Paragraph p27 = new Paragraph("\n Findings:", headFont);

					Paragraph p28 = new Paragraph(patientServiceDetails.getComment(), redFont2);
					document.add(p27);
					document.add(p28);
				}
				
				
				Paragraph p7 = new Paragraph("*Please correlate with clinical findings if necessary discuss", redFont2);
				p7.setAlignment(Element.ALIGN_CENTER);

				p7.add("\n");
				p7.add("\n");
				document.add(p7);

				Chunk cnd = new Chunk(new VerticalPositionMark());
				Paragraph p19 = new Paragraph(patientRegistration.getPatientDetails().getConsultant(), headFont);
				p19.setAlignment(Element.ALIGN_RIGHT);
				document.add(p19);

				Paragraph p18 = new Paragraph("LAB INCHARGE", headFont);
				p18.add(cnd);

				p18.add("(" + patientRegistration.getVuserD().getDoctorDetails().getSpecilization() + ")");
				document.add(p18);

			
				// ************************

				document.close();

				pdfBytes = byteArrayOutputStream.toByteArray();
				List<ServicePdf> servicePdfs=servicePdfServiceImpl.findByRegAndMeasureName(regId,patientServiceDetails.getServiceName());
				if(!servicePdfs.isEmpty())
				{
					servicePdf = new ServicePdf();
					servicePdf.setSid(servicePdfs.get(0).getSid());
					servicePdf.setData(pdfBytes);
					servicePdf.setFileName(servicePdfs.get(0).getFileName());
					servicePdf.setFileuri(servicePdfs.get(0).getFileuri());
					servicePdf.setRegId(servicePdfs.get(0).getRegId());
					servicePdfServiceImpl.save(servicePdf);

				}
				else
				{
				String uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/lab/servicePdf/viewFile/")
						.path(servicePdfServiceImpl.getNextLabId()).toUriString();

				servicePdf = new ServicePdf();
				servicePdf.setSid(servicePdfServiceImpl.getNextLabId());
				servicePdf.setData(pdfBytes);
				servicePdf.setFileName(patientServiceDetails.getServiceName());
				servicePdf.setFileuri(uri);
				servicePdf.setRegId(patientServiceDetails.getRegId());
				servicePdfServiceImpl.save(servicePdf);
				}


			} catch (Exception e) {
				Logger.error(e.getMessage());
			}

	}		 
		 else if (patientServiceDetails.getServiceName().equalsIgnoreCase("TB-DB-IB")) {
				byte[] pdfBytes = null;
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

				Document document = new Document();

				
				try {
					PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);

					MyFooter event = new MyFooter(principal,userServiceImpl);
					writer.setPageEvent(event);

					Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
					Font redFont3 = new Font(Font.FontFamily.TIMES_ROMAN, 17, Font.BOLD);
					Font redFont2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.ITALIC);
					Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);

					document.open();

					
					document.add(pq);

					Paragraph p51 = new Paragraph("UDBHAVA HOSPITALS", redFont);
					p51.add("\n \n \n");
					p51.setAlignment(Element.ALIGN_CENTER);
					document.add(p51);
					
					
					PdfPTable table2 = new PdfPTable(2);
					table2.setWidths(new float[] { 7f, 7f });
					Font redFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

					date = Calendar.getInstance().getTime();
					formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
					String today = formatter.format(date).toString();

					PdfPCell cell1 = new PdfPCell();
					table2.setWidthPercentage(100f);	
					cell1.setBorder(Rectangle.BOTTOM|Rectangle.TOP);
					
					PdfPTable table21 = new PdfPTable(6);
					table21.setWidths(new float[] { 5f,1f,5f,5f,1f,6.5f});
					 table21.setSpacingBefore(10);
					

					PdfPCell hcell1;
					hcell1 = new PdfPCell(
							new Phrase("Patient Name", redFont1));

					hcell1.setBorder(Rectangle.NO_BORDER);
					// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell1.setPaddingLeft(-50f);
					table21.addCell(hcell1);

					
					hcell1 = new PdfPCell(
							new Phrase(":", redFont1));

					hcell1.setBorder(Rectangle.NO_BORDER);
					// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell1.setPaddingLeft(-50f);
					table21.addCell(hcell1);

					
					hcell1 = new PdfPCell(
							new Phrase(patientRegistration.getPatientDetails().getTitle() +". "+ patientRegistration.getPatientDetails().getFirstName()
									+ " " + patientRegistration.getPatientDetails().getLastName(), redFont1));

					hcell1.setBorder(Rectangle.NO_BORDER);
					// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell1.setPaddingLeft(-50f);
					table21.addCell(hcell1);

					hcell1 = new PdfPCell(new Phrase("Age/Gender", redFont1));
					hcell1.setBorder(Rectangle.NO_BORDER);
					hcell1.setPaddingLeft(10f);
					hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell1);

					hcell1 = new PdfPCell(new Phrase(":", redFont1));
					hcell1.setBorder(Rectangle.NO_BORDER);
					hcell1.setPaddingLeft(10f);
					hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell1);

					
					hcell1 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getAge()
							+ "/" + patientRegistration.getPatientDetails().getGender(), redFont1));
					hcell1.setBorder(Rectangle.NO_BORDER);
					hcell1.setPaddingLeft(10f);
					hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell1);

					
					
					PdfPCell hcell4;
					hcell4 = new PdfPCell(
							new Phrase("Admission No", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(-50f);
					table21.addCell(hcell4);
					
					hcell4 = new PdfPCell(
							new Phrase(":", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(-50f);
					table21.addCell(hcell4);
					
					hcell4 = new PdfPCell(
							new Phrase(patientRegistration.getRegId(), redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(-50f);
					table21.addCell(hcell4);
					
					


					if(admittedWard!=null)
					{
					hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					hcell4 = new PdfPCell(new Phrase(":", redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					hcell4 = new PdfPCell(new Phrase(admittedWard, redFont1));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(10f);

					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell4);
					
					}
					else
					{
						hcell4 = new PdfPCell(new Phrase("Ward Name", redFont1));
						hcell4.setBorder(Rectangle.NO_BORDER);
						hcell4.setPaddingLeft(10f);

						hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
						table21.addCell(hcell4);
						
						
						hcell4 = new PdfPCell(new Phrase(":", redFont1));
						hcell4.setBorder(Rectangle.NO_BORDER);
						hcell4.setPaddingLeft(10f);

						hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
						table21.addCell(hcell4);
						
						hcell4 = new PdfPCell(new Phrase("", redFont1));
						hcell4.setBorder(Rectangle.NO_BORDER);
						hcell4.setPaddingLeft(10f);

						hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
						table21.addCell(hcell4);

					}



					PdfPCell hcell15;
					hcell15 = new PdfPCell(
							new Phrase("Admission Date", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setPaddingLeft(-50f);
					table21.addCell(hcell15);

					hcell15 = new PdfPCell(
							new Phrase(":", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setPaddingLeft(-50f);
					table21.addCell(hcell15);

					hcell15 = new PdfPCell(
							new Phrase(pdfadmissionDate, redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setPaddingLeft(-50f);
					table21.addCell(hcell15);

					
					if(roomName!=null)
					{
					hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase(":", redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					
					hcell15 = new PdfPCell(new Phrase(roomName, redFont1));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(10f);
					table21.addCell(hcell15);
					}
					else
					{
						hcell15 = new PdfPCell(new Phrase("Room Name", redFont1));
						hcell15.setBorder(Rectangle.NO_BORDER);
						hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell15.setPaddingLeft(10f);
						table21.addCell(hcell15);
						
						hcell15 = new PdfPCell(new Phrase(":", redFont1));
						hcell15.setBorder(Rectangle.NO_BORDER);
						hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell15.setPaddingLeft(10f);
						table21.addCell(hcell15);
						
						hcell15 = new PdfPCell(new Phrase("", redFont1));
						hcell15.setBorder(Rectangle.NO_BORDER);
						hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell15.setPaddingLeft(10f);
						table21.addCell(hcell15);
						
					}
					PdfPCell hcell16;
					hcell16 = new PdfPCell(new Phrase(
							"Bill No/UMR No", redFont1));

					hcell16.setBorder(Rectangle.NO_BORDER);
					hcell16.setPaddingLeft(-50f);
					table21.addCell(hcell16);
					
					hcell16 = new PdfPCell(new Phrase(
							":", redFont1));

					hcell16.setBorder(Rectangle.NO_BORDER);
					hcell16.setPaddingLeft(-50f);
					table21.addCell(hcell16);
					
					hcell16 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getUmr(), redFont1));

					hcell16.setBorder(Rectangle.NO_BORDER);
					hcell16.setPaddingLeft(-50f);
					table21.addCell(hcell16);

					hcell16 = new PdfPCell(new Phrase("Bill Date", redFont1));
					hcell16.setBorder(Rectangle.NO_BORDER);
					hcell16.setPaddingLeft(10f);
					hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell16);
					
					hcell16 = new PdfPCell(new Phrase(":", redFont1));
					hcell16.setBorder(Rectangle.NO_BORDER);
					hcell16.setPaddingLeft(10f);
					hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell16);
					
					hcell16 = new PdfPCell(new Phrase(bill, redFont1));
					hcell16.setBorder(Rectangle.NO_BORDER);
					hcell16.setPaddingLeft(10f);
					hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell16);

					PdfPCell hcell17;
					hcell17 = new PdfPCell(new Phrase(
							"Consultant",
							redFont1));

					hcell17.setBorder(Rectangle.NO_BORDER);
					hcell17.setPaddingLeft(-50f);
					table21.addCell(hcell17);
					
					hcell17 = new PdfPCell(new Phrase(
							":",
							redFont1));

					hcell17.setBorder(Rectangle.NO_BORDER);
					hcell17.setPaddingLeft(-50f);
					table21.addCell(hcell17);
					
					hcell17 = new PdfPCell(new Phrase(
							patientRegistration.getPatientDetails().getConsultant(),
							redFont1));

					hcell17.setBorder(Rectangle.NO_BORDER);
					hcell17.setPaddingLeft(-50f);
					table21.addCell(hcell17);

					hcell17 = new PdfPCell(new Phrase("Report Date", redFont1));
					hcell17.setBorder(Rectangle.NO_BORDER);
					hcell17.setPaddingLeft(10f);
					hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell17);
					
					hcell17 = new PdfPCell(new Phrase(":", redFont1));
					hcell17.setBorder(Rectangle.NO_BORDER);
					hcell17.setPaddingLeft(10f);
					hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell17);
					
					hcell17 = new PdfPCell(new Phrase(today, redFont1));
					hcell17.setBorder(Rectangle.NO_BORDER);
					hcell17.setPaddingLeft(10f);
					hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
					table21.addCell(hcell17);
					
					cell1.setColspan(2);
					cell1.addElement(table21);
					table2.addCell(cell1);
	             //  table2.addCell(cell1);
	               document.add(table2);


					Paragraph p11 = new Paragraph("DEPARTMENT OF SEROLOGY");
					p11.setAlignment(Element.ALIGN_CENTER);
					p11.add("\n");
					p11.add("\n");
					document.add(p11);

					PdfPTable table3 = new PdfPTable(3);
					table3.setWidths(new float[] { 5f, 9f, 7f });

					PdfPCell hcell01;
					hcell01 = new PdfPCell(new Phrase("INVESTIGATION", headFont));
					hcell01.setBorder(Rectangle.NO_BORDER);
					hcell01.setPaddingLeft(-50f);
					hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);

					table3.addCell(hcell01);

					hcell01 = new PdfPCell(new Phrase("RESULT", headFont));
					hcell01.setBorder(Rectangle.NO_BORDER);

					hcell01.setHorizontalAlignment(Element.ALIGN_CENTER);
					table3.addCell(hcell01);

					hcell01 = new PdfPCell(new Phrase("NORMAL RANGE", headFont));
					hcell01.setBorder(Rectangle.NO_BORDER);

					hcell01.setHorizontalAlignment(Element.ALIGN_RIGHT);
					table3.addCell(hcell01);

					for (RefMeasureDetails refMeasureDetailsInfoPdf : refMeasureDetails) {

						PdfPCell hcell021;
						hcell021 = new PdfPCell(new Phrase(refMeasureDetailsInfoPdf.getMeasure(), redFont1));
						hcell021.setBorder(Rectangle.NO_BORDER);
						hcell021.setPaddingLeft(-50f);
						hcell021.setHorizontalAlignment(Element.ALIGN_LEFT);
						table3.addCell(hcell021);

						if(refMeasureDetailsInfoPdf.getDimension()!=null)
						{
						hcell021 = new PdfPCell(new Phrase(
								": " + refMeasureDetailsInfoPdf.getValue() + " " + refMeasureDetailsInfoPdf.getDimension(),
								redFont1));
						hcell021.setBorder(Rectangle.NO_BORDER);
						hcell021.setHorizontalAlignment(Element.ALIGN_CENTER);
						table3.addCell(hcell021);
						}
						else
						{
							hcell021 = new PdfPCell(new Phrase(
									": " + refMeasureDetailsInfoPdf.getValue(),
									redFont1));
							hcell021.setBorder(Rectangle.NO_BORDER);
							hcell021.setHorizontalAlignment(Element.ALIGN_CENTER);
							table3.addCell(hcell021);	
						}
						if(refMeasureDetailsInfoPdf.getDimension()!=null)
						{
						hcell021 = new PdfPCell(new Phrase(
								refMeasureDetailsInfoPdf.getRange() + " " + refMeasureDetailsInfoPdf.getDimension(),
								redFont1));
						hcell021.setBorder(Rectangle.NO_BORDER);
						hcell021.setHorizontalAlignment(Element.ALIGN_RIGHT);
						table3.addCell(hcell021);
					}
						else
						{
							hcell021 = new PdfPCell(new Phrase(
									refMeasureDetailsInfoPdf.getRange(),
									redFont1));
							hcell021.setBorder(Rectangle.NO_BORDER);
							hcell021.setHorizontalAlignment(Element.ALIGN_RIGHT);
							table3.addCell(hcell021);
						}
					}
					document.add(table3);
					Paragraph p01 = new Paragraph();
				
					p01.add("\n");
					p01.add("\n");
					document.add(p01);
					if (patientServiceDetails.getComment().isEmpty()) {
						Paragraph p27 = new Paragraph("\n Findings:", headFont);
						document.add(p27);
						Paragraph p28 = new Paragraph("N/A", redFont2);
						document.add(p28);

					} else {
						Paragraph p27 = new Paragraph("\n Findings:", headFont);

						Paragraph p28 = new Paragraph(patientServiceDetails.getComment(), redFont2);
						document.add(p27);
						document.add(p28);
					}

					Paragraph p7 = new Paragraph("*Please correlate with clinical findings if necessary discuss", redFont2);
					p7.setAlignment(Element.ALIGN_CENTER);

					
					p7.add("\n");
					p7.add("\n");
					document.add(p7);

					Chunk cnd = new Chunk(new VerticalPositionMark());
					Paragraph p19 = new Paragraph(patientRegistration.getPatientDetails().getConsultant(), headFont);
					p19.setAlignment(Element.ALIGN_RIGHT);
					document.add(p19);

					Paragraph p18 = new Paragraph("LAB INCHARGE", headFont);
					p18.add(cnd);

					p18.add("(" + patientRegistration.getVuserD().getDoctorDetails().getSpecilization() + ")");
					document.add(p18);
					document.close();
					pdfBytes = byteArrayOutputStream.toByteArray();
					List<ServicePdf> servicePdfs=servicePdfServiceImpl.findByRegAndMeasureName(regId,patientServiceDetails.getServiceName());
					if(!servicePdfs.isEmpty())
					{
						servicePdf = new ServicePdf();
						servicePdf.setSid(servicePdfs.get(0).getSid());
						servicePdf.setData(pdfBytes);
						servicePdf.setFileName(servicePdfs.get(0).getFileName());
						servicePdf.setFileuri(servicePdfs.get(0).getFileuri());
						servicePdf.setRegId(servicePdfs.get(0).getRegId());
						servicePdfServiceImpl.save(servicePdf);

					}
					else
					{
					String uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/lab/servicePdf/viewFile/")
							.path(servicePdfServiceImpl.getNextLabId()).toUriString();

					servicePdf = new ServicePdf();
					servicePdf.setSid(servicePdfServiceImpl.getNextLabId());
					servicePdf.setData(pdfBytes);
					servicePdf.setFileName(patientServiceDetails.getServiceName());
					servicePdf.setFileuri(uri);
					servicePdf.setRegId(patientServiceDetails.getRegId());
					servicePdfServiceImpl.save(servicePdf);
					}


				} catch (Exception e) {
					Logger.error(e.getMessage());
				}

			}
		
		return servicePdf;
	}

	
	
	
	public List<PatientServiceDetails> findByPatientServiceAndPatientLabService(String regId, String serviceId) {
		return patientServiceDetailsRepository.findByPatientServiceAndPatientLabService(regId, serviceId);
	}

}
