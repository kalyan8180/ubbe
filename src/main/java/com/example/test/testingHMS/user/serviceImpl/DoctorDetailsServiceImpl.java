package com.example.test.testingHMS.user.serviceImpl;

import java.io.ByteArrayOutputStream;
import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.test.testingHMS.doctor.dto.RefDoctorDetails;
import com.example.test.testingHMS.doctor.model.MyFooter;
import com.example.test.testingHMS.doctor.model.RefPrescription;
import com.example.test.testingHMS.laboratory.model.NotesDetails;
import com.example.test.testingHMS.laboratory.model.NotesPdf;
import com.example.test.testingHMS.laboratory.model.ServicePdf;
import com.example.test.testingHMS.laboratory.repository.NotesDetailsRepository;
import com.example.test.testingHMS.laboratory.repository.NotesPdfRepository;
import com.example.test.testingHMS.laboratory.serviceImpl.NotesDetailsServiceImpl;
import com.example.test.testingHMS.laboratory.serviceImpl.NotesPdfServiceImpl;
import com.example.test.testingHMS.laboratory.serviceImpl.ServicePdfServiceImpl;
import com.example.test.testingHMS.nurse.model.PrescriptionDetails;
import com.example.test.testingHMS.nurse.repository.PrescriptionDetailsRepository;
import com.example.test.testingHMS.nurse.serviceImpl.PrescriptionDetailsServiceImpl;
import com.example.test.testingHMS.patient.model.PatientRegistration;
import com.example.test.testingHMS.patient.serviceImpl.PatientRegistrationServiceImpl;
import com.example.test.testingHMS.pharmacist.serviceImpl.SalesReturnServiceImpl;
import com.example.test.testingHMS.user.model.DoctorDetails;
import com.example.test.testingHMS.user.model.User;
import com.example.test.testingHMS.user.repository.DoctorDetailsRepository;
import com.example.test.testingHMS.user.service.DoctorDetailsService;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;

@Service
public class DoctorDetailsServiceImpl implements DoctorDetailsService {
	
	private static final Logger Logger=LoggerFactory.getLogger(DoctorDetailsServiceImpl.class);
	
	
	@Autowired
	DoctorDetailsRepository repo;

	@Autowired
	PrescriptionDetails prescriptionDetails;
	
	@Autowired
	NotesPdf notesPdf;
	
	@Autowired
	ServicePdfServiceImpl servicePdfServiceImpl;
	
	@Autowired
	NotesPdfRepository notesPdfRepository;
	
	@Autowired
	NotesPdfServiceImpl notesPdfServiceImpl;
	
	@Autowired
	PatientRegistrationServiceImpl patientRegistrationServiceImpl;
	
	@Autowired
	RefPrescription refPrescription;
	
	@Autowired
	NotesDetailsRepository notesDetailsRepository;
	
	@Autowired
	NotesDetailsServiceImpl notesDetailsServiceImpl;
	
	@Autowired
	UserServiceImpl userServiceImpl;
	
	
	@Autowired
	PrescriptionDetailsServiceImpl prescriptionDetailsServiceImpl;

	@Autowired
	PrescriptionDetailsRepository prescriptionDetailsRepository;
	
	
	@Autowired
	RefDoctorDetails refDoctorDetails;

	
	public String getNextId() {
		DoctorDetails docLast = repo.findFirstByOrderByDoctorIdDesc();
		String docNextId = null;
		if (docLast == null) {
			docNextId ="DOC0000001";
		} else {
			String docLastId = docLast.getDoctorId();
			int docIntId = Integer.parseInt(docLastId.substring(3));
			docIntId += 1;
			docNextId = "DOC" + String.format("%07d", docIntId);
		}
		return docNextId;

	}
	
	public List<RefDoctorDetails> getAll(Principal principal)
	{
		List<RefDoctorDetails> refDoctorDetailsList=new ArrayList<>();
		User userSecurity=userServiceImpl.findByUserName(principal.getName());
		List<PatientRegistration> patientRegistration=patientRegistrationServiceImpl.findByVuserD(userSecurity);
		for(PatientRegistration patientRegistrationList:patientRegistration) 
		{
			RefDoctorDetails refDoctorDetailsInfo=new RefDoctorDetails();
			refDoctorDetailsInfo.setRegNo(patientRegistrationList.getRegId());
			refDoctorDetailsInfo.setDoj(patientRegistrationList.getDateOfJoining().toString().substring(0, 10));
			NotesPdf notesPdf=notesPdfServiceImpl.findByRegId(patientRegistrationList.getRegId());
			List<ServicePdf> servicePdf=servicePdfServiceImpl.findByRegId(patientRegistrationList.getRegId());
			List<Map<String,String>> urlInfo=new ArrayList<>();
			
			if(notesPdf!=null)
			{
				refDoctorDetailsInfo.setNotes(notesPdf.getFileuri());
			}
			if(servicePdf!=null)
			{
				for(ServicePdf servicePdfInfo:servicePdf)
				{
					Map<String,String> url=new HashMap<>();
					url.put(servicePdfInfo.getFileName(),servicePdfInfo.getFileuri());
					urlInfo.add(url);
				}
				refDoctorDetailsInfo.setReport(urlInfo);
				
			}
			refDoctorDetailsInfo.setPatientName(patientRegistrationList.getPatientDetails().getFirstName()+" "+patientRegistrationList.getPatientDetails().getLastName());
			PrescriptionDetails prescriptionDetails=prescriptionDetailsServiceImpl.findByRegId(patientRegistrationList.getRegId());
			if(prescriptionDetails!=null)
			{
				refDoctorDetailsInfo.setPrescription(prescriptionDetails.getFileDownloadUri());
			}
			refDoctorDetailsList.add(refDoctorDetailsInfo);
		}
		return refDoctorDetailsList;

	}
	
	public NotesPdf createNotes(NotesDetails notesDetails)
	{
		PatientRegistration patientRegistration =patientRegistrationServiceImpl.findByRegId(notesDetails.getRegId());
		NotesDetails notesDetailsInfo=notesDetailsServiceImpl.findByPatientRegistrationNotes(patientRegistration);

		
		
		  if(notesDetailsInfo!=null) {
		  notesDetails.setNoteId(notesDetailsInfo.getNoteId()); String
		  allNotes=notesDetailsInfo.getNotes(); String
		  timestamp=Timestamp.valueOf(LocalDateTime.now()).toString().substring(0, 10);
		  notesDetails.setPharmacyNotes(notesDetailsInfo.getPharmacyNotes());
		  allNotes+="\n"+timestamp+"\n"+notesDetails.getWriteNotes()+"\n";
		  notesDetails.setNotes(allNotes);
		  notesDetails.setWriteNotes(timestamp+"\n"+notesDetails.getWriteNotes()+"\n");
		  notesDetails.setPatientRegistrationNotes(patientRegistration); } else {
		  notesDetails.setNoteId(notesDetailsServiceImpl.getNextNoteId());
		  notesDetails.setNotes(Timestamp.valueOf(LocalDateTime.now()).toString()+"\n"+
		  notesDetails.getWriteNotes()+"\n");
		  notesDetails.setWriteNotes(Timestamp.valueOf(LocalDateTime.now()).toString()+
		  "\n"+notesDetails.getWriteNotes()+"\n");
		  notesDetails.setPatientRegistrationNotes(patientRegistration); }
		  notesDetailsRepository.save(notesDetails);
		 
		 	
		final Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.NORMAL, BaseColor.RED);
		final Font blueFont = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.NORMAL);

		NotesPdf notesPdf=null;
		byte[] pdfBytes=null;
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		 
			Document document= new Document();
			try {
				
				PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);
				MyFooter event = new MyFooter();
		        writer.setPageEvent(event);

				
				document.open();
				
				Chunk cnd = new Chunk();
				Paragraph p2 = new Paragraph();
				p2.add(notesDetails.getNotes());
				document.add(p2);
				
				document.close();
				
				pdfBytes = byteArrayOutputStream.toByteArray();
				
				String uri=ServletUriComponentsBuilder.fromCurrentContextPath()
		                .path("/v1/doctor/viewFile/")
		                .path(notesPdfServiceImpl.getNextLabId())
		                .toUriString();
				
				notesPdf=new NotesPdf();
				NotesPdf notesPdfExists=notesPdfServiceImpl.findByRegId(notesDetails.getRegId());
				if(notesPdfExists!=null)
				{	notesPdf.setRegId(notesPdfExists.getRegId());
					notesPdf.setNid(notesPdfExists.getNid());
					notesPdf.setFileName(notesPdfExists.getFileName());
					notesPdf.setFileuri(notesPdfExists.getFileuri());
					notesPdf.setData(pdfBytes);
				}
				else
				{
					notesPdf.setRegId(notesDetails.getRegId());
					notesPdf.setNid(notesPdfServiceImpl.getNextLabId());
					notesPdf.setFileName(notesDetails.getRegId()+" Notes");
					notesPdf.setFileuri(uri);
					notesPdf.setData(pdfBytes);
				}
				
				notesPdfRepository.save(notesPdf);
				
				
			}
			catch(Exception e)
			{
				Logger.error(e.getMessage());
			}
			
			return notesPdf;
	}
	
	public void createPrescriptionNotes(NotesDetails notesDetails)
	{
		PatientRegistration patientRegistration =patientRegistrationServiceImpl.findByRegId(notesDetails.getRegId());
		NotesDetails notesDetailsInfo=notesDetailsServiceImpl.findByPatientRegistrationNotes(patientRegistration);

		if(notesDetailsInfo!=null)
		{
			notesDetails.setNoteId(notesDetailsInfo.getNoteId());
			notesDetails.setNotes(notesDetailsInfo.getNotes());
			notesDetails.setWriteNotes(notesDetailsInfo.getWriteNotes());
			
			
			String timestamp=Timestamp.valueOf(LocalDateTime.now()).toString();
			

			if(notesDetailsInfo.getPharmacyNotes()!=null)
			{
				notesDetails.setPharmacyNotes(notesDetailsInfo.getPharmacyNotes()+"\n"+timestamp+"\n"+notesDetails.getPharmacyNotes()+"\n");
			}
			else
			{
				notesDetails.setPharmacyNotes(timestamp+"\n"+notesDetails.getPharmacyNotes()+"\n");
			}
			notesDetails.setPatientRegistrationNotes(patientRegistration);
		}
		else
		{
			notesDetails.setNoteId(notesDetailsServiceImpl.getNextNoteId());
			notesDetails.setPharmacyNotes(Timestamp.valueOf(LocalDateTime.now()).toString()+"\n"+notesDetails.getPharmacyNotes()+"\n");
			notesDetails.setPatientRegistrationNotes(patientRegistration);
		}
		notesDetailsRepository.save(notesDetails);

	}
	
	public NotesPdf getNotes(String regId)
	{
		/*
		 * final Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.NORMAL,
		 * BaseColor.RED); final Font blueFont = new Font(Font.FontFamily.TIMES_ROMAN,
		 * 16, Font.NORMAL);
		 * 
		 * NotesPdf notesPdf=null; byte[] pdfBytes=null; ByteArrayOutputStream
		 * byteArrayOutputStream = new ByteArrayOutputStream();
		 */
		PatientRegistration patientRegistration =patientRegistrationServiceImpl.findByRegId(regId);
		NotesDetails notesDetailsInfo=notesDetailsServiceImpl.findByPatientRegistrationNotes(patientRegistration);
		final Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.NORMAL, BaseColor.RED);
		final Font blueFont = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.NORMAL);

		NotesPdf notesPdf=null;
		byte[] pdfBytes=null;
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		 
			Document document= new Document();
			try {
				
				PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);
				MyFooter event = new MyFooter();
		        writer.setPageEvent(event);

				
				document.open();
				
				Chunk cnd = new Chunk();
				Paragraph p2 = new Paragraph();
				p2.add(notesDetailsInfo.getNotes());
				document.add(p2);
				
				document.close();
				
				pdfBytes = byteArrayOutputStream.toByteArray();
				
				String uri=ServletUriComponentsBuilder.fromCurrentContextPath()
		                .path("/v1/doctor/viewFile/")
		                .path(notesPdfServiceImpl.getNextLabId())
		                .toUriString();
				
				notesPdf=new NotesPdf();
				NotesPdf notesPdfExists=notesPdfServiceImpl.findByRegId(regId);
				if(notesPdfExists!=null)
				{	notesPdf.setRegId(notesPdfExists.getRegId());
					notesPdf.setNid(notesPdfExists.getNid());
					notesPdf.setFileName(notesPdfExists.getFileName());
					notesPdf.setFileuri(notesPdfExists.getFileuri());
					notesPdf.setData(pdfBytes);
				}
				else
				{
					notesPdf.setRegId(regId);
					notesPdf.setNid(notesPdfServiceImpl.getNextLabId());
					notesPdf.setFileName(regId+" Notes");
					notesPdf.setFileuri(uri);
					notesPdf.setData(pdfBytes);
				}
				
				notesPdfRepository.save(notesPdf);
				
				
			}
			catch(Exception e)
			{
				Logger.error(e.getMessage());
			}
			
			return notesPdf;
			
	}
	
	public PrescriptionDetails create(RefPrescription refPrescription)
	{
		byte[] pdfBytes=null;
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		
		final Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.NORMAL, BaseColor.RED);
		final Font blueFont = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.NORMAL);
		final Font font = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

		PatientRegistration patientRegistration=patientRegistrationServiceImpl.findByRegId(refPrescription.getRegId());
		
		
		Document document = new Document();
		try {

			PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);

			MyFooter event = new MyFooter();
			writer.setPageEvent(event);

			document.open();

			Paragraph p1 = new Paragraph(
					"_____________________________________________________________________________");
			document.add(p1);

			Paragraph p8 = new Paragraph();
			p8.add(new Paragraph("OUT-PATIENT RECORD", blueFont));
			p8.setAlignment(Element.ALIGN_CENTER);
			document.add(p8);

			Paragraph p9 = new Paragraph(
					"_____________________________________________________________________________");
			document.add(p9);

			
			Chunk cnd = new Chunk(new VerticalPositionMark());
			Paragraph p2 = new Paragraph();
			String name= patientRegistration.getPatientDetails().getTitle()+". "+patientRegistration.getPatientDetails().getFirstName()+" "+patientRegistration.getPatientDetails().getLastName();
			p2.add("Patient Name   :" +name);
			p2.add(cnd);
			
			p2.add("REG NO :" + refPrescription.getRegId());
			document.add(p2);

			
			
			Paragraph p3 = new Paragraph();
			p3.add("Mobile No        :" + patientRegistration.getPatientDetails().getMobile());
			p3.add(cnd);
			p3.add("UMR NO :" + patientRegistration.getPatientDetails().getUmr());
			
			document.add(p3);

			
			
			
			Paragraph p4 = new Paragraph();
			p4.add("Gender            :" + patientRegistration.getPatientDetails().getGender());
			p4.add(cnd);
			
			String date=patientRegistration.getDateOfJoining().toString().substring(0, 10);
			p4.add("Date Of joining :" + date);
			
			document.add(p4);

			Paragraph p10 = new Paragraph(
					"_____________________________________________________________________________");
			document.add(p10);
			Paragraph p11 = new Paragraph();
			p11.add(new Paragraph("CONSULATATION DETAILS", blueFont));
			document.add(p11);
			document.add(new Paragraph("\n"));
			Paragraph p12 = new Paragraph();
			p12.add(new Paragraph("Present Illness",blueFont));
			document.add(p12);
			Paragraph p23 = new Paragraph();
			p23.add(refPrescription.getPresentillness());
			p23.setFont(font);
			document.add(p23);
			document.add(new Paragraph("\n"));

			Paragraph p13 = new Paragraph();
			p13.add(new Paragraph("Physical Examination",blueFont));
			document.add(p13);
			Paragraph p24 = new Paragraph();
			p24.add(refPrescription.getPhysicalExamination());
			p24.setFont(font);
			document.add(p24);
			document.add(new Paragraph("\n"));

			Paragraph p16 = new Paragraph();
			p16.add(new Paragraph("INVESTIGATION ADVICED", blueFont));
			document.add(p16);

			Paragraph p25 = new Paragraph();
			p25.add(refPrescription.getInvestigationAdviced());
			p25.setFont(font);
			document.add(p25);
			document.add(new Paragraph("\n"));

			Paragraph p28 = new Paragraph();
			p28.add(new Paragraph("MedicationName&Dosage", blueFont));
			document.add(p28);

			Paragraph p29 = new Paragraph();
			p29.add(refPrescription.getMedicationNameDosage());
			p29.setFont(font);
			document.add(p29);
			document.add(new Paragraph("\n"));

			Paragraph p21 = new Paragraph();
			p21.add(new Paragraph("PATIENT INSTRUCTION", blueFont));
			document.add(p21);
			Paragraph p26 = new Paragraph();
			p26.add(refPrescription.getPatientInstruction());
			p26.setFont(font);
			document.add(p26);

			document.add(new Paragraph("\n"));
			Paragraph p22 = new Paragraph();
			p22.add(new Paragraph("RECOMMENDATION", blueFont));
			
			document.add(p22);
			Paragraph p27 = new Paragraph();
			p27.add(refPrescription.getRecommendation());
			p27.setFont(font);
			document.add(p27);
			document.add(new Paragraph("\n"));
			Paragraph p30 = new Paragraph();
			p30.add(new Paragraph("DOCTOR NAME", blueFont));
			
			document.add(p30);
			Paragraph p32 = new Paragraph();
			//p32.add(user.getFirstName()+" "+user.getLastName());
			p32.setFont(font);
			document.add(p32);

			document.close();

			pdfBytes = byteArrayOutputStream.toByteArray();
			String uri=ServletUriComponentsBuilder.fromCurrentContextPath()
	                .path("/v1/nurse/viewFile/")
	                .path(prescriptionDetailsServiceImpl.generatePrescriptionId())
	                .toUriString();
			
			
			
			
			
			
			
			PrescriptionDetails prescriptionDetailsPrev=prescriptionDetailsServiceImpl.findByPatientRegistration(patientRegistration);
			if(prescriptionDetailsPrev!=null)
			{
				prescriptionDetailsPrev.setFileName(pdfBytes);
				prescriptionDetailsRepository.save(prescriptionDetailsPrev);
				return prescriptionDetailsPrev;
				
				
			}
			else
			{
			prescriptionDetails.setPrescriptionId(prescriptionDetailsServiceImpl.generatePrescriptionId());
			prescriptionDetails.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
		//	prescriptionDetails.setDoctorName(user.getFirstName()+" "+user.getLastName());
			prescriptionDetails.setFileDownloadUri(uri);
			prescriptionDetails.setFileNamee(refPrescription.getRegId()+" Prescription");
			prescriptionDetails.setFileName(pdfBytes);
			prescriptionDetails.setFileType("application/pdf");
			prescriptionDetails.setPatientRegistration(patientRegistration);
			prescriptionDetails.setRegId(patientRegistration.getRegId());
			prescriptionDetailsRepository.save(prescriptionDetails);
			return prescriptionDetails;
			}
			//After security
			//	prescriptionDetails.setUserDetails(userDetails);
			
			
	
	}
		catch(Exception e)
		{
			Logger.error(e.getMessage());
		}
		return null;

	}
	
	public List<DoctorDetails> findBySpecilization(String specialization)
	{
		return repo.findBySpecilization(specialization);
	}
	
	public 	DoctorDetails findByDrRegistrationo(String regNo)
	{
		return repo.findByDrRegistrationo(regNo);
	}
	
	
	
}
