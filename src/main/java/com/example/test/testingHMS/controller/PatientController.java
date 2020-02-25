
package com.example.test.testingHMS.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.test.testingHMS.Excel.ExcelView;
import com.example.test.testingHMS.Excel.GeneratePdfReport;
import com.example.test.testingHMS.MoneyToWords.NumberToWordsConverter;
import com.example.test.testingHMS.UpdatePdfs.PdfGenerator;
import com.example.test.testingHMS.bed.model.RoomBookingDetails;
import com.example.test.testingHMS.bed.model.RoomDetails;
import com.example.test.testingHMS.bed.serviceImpl.RoomBookingDetailsServiceImpl;
import com.example.test.testingHMS.bed.serviceImpl.RoomDetailsServiceImpl;
import com.example.test.testingHMS.bill.model.ChargeBill;
import com.example.test.testingHMS.bill.repository.ChargeBillRepository;
import com.example.test.testingHMS.finalBilling.model.FinalBilling;
import com.example.test.testingHMS.finalBilling.serviceImpl.FinalBillingServiceImpl;
import com.example.test.testingHMS.laboratory.model.LaboratoryRegistration;
import com.example.test.testingHMS.laboratory.model.NotesDetails;
import com.example.test.testingHMS.laboratory.model.NotesPdf;
import com.example.test.testingHMS.laboratory.model.ServicePdf;
import com.example.test.testingHMS.laboratory.repository.NotesDetailsRepository;
import com.example.test.testingHMS.laboratory.repository.NotesPdfRepository;
import com.example.test.testingHMS.laboratory.repository.ServicePdfRepository;
import com.example.test.testingHMS.laboratory.serviceImpl.LaboratoryRegistrationServiceImpl;
import com.example.test.testingHMS.nurse.model.PrescriptionDetails;
import com.example.test.testingHMS.nurse.repository.PrescriptionDetailsRepository;
import com.example.test.testingHMS.patient.dto.PatientDetailsDTO;
import com.example.test.testingHMS.patient.dto.PatientPaymentDTO;
import com.example.test.testingHMS.patient.dto.ReferralDetailsDTO;
import com.example.test.testingHMS.patient.idGenerator.RegGenerator;
import com.example.test.testingHMS.patient.model.MarketingQuestions;
import com.example.test.testingHMS.patient.model.PatientDetails;
import com.example.test.testingHMS.patient.model.PatientPayment;
import com.example.test.testingHMS.patient.model.PatientPaymentPdf;
import com.example.test.testingHMS.patient.model.PatientRegistration;
import com.example.test.testingHMS.patient.model.ReferralDetails;
import com.example.test.testingHMS.patient.repository.PatientDetailsRepository;
import com.example.test.testingHMS.patient.repository.PatientPaymentRepository;
import com.example.test.testingHMS.patient.repository.PatientRegistrationRepository;
import com.example.test.testingHMS.patient.repository.PaymentPdfRepository;
import com.example.test.testingHMS.patient.repository.PaymentRepository;
import com.example.test.testingHMS.patient.serviceImpl.PatientDetailsServiceImpl;
import com.example.test.testingHMS.patient.serviceImpl.PatientPaymentServiceImpl;
import com.example.test.testingHMS.patient.serviceImpl.PatientRegistrationServiceImpl;
import com.example.test.testingHMS.patient.serviceImpl.PaymentPdfServiceImpl;
import com.example.test.testingHMS.patient.serviceImpl.ReferralDetailsServiceImpl;
import com.example.test.testingHMS.pharmacist.model.Sales;
import com.example.test.testingHMS.pharmacist.model.SalesPaymentPdf;
import com.example.test.testingHMS.pharmacist.repository.SalesPaymentPdfRepository;
import com.example.test.testingHMS.pharmacist.serviceImpl.SalesServiceImpl;
import com.example.test.testingHMS.user.model.DoctorDetails;
import com.example.test.testingHMS.user.model.User;
import com.example.test.testingHMS.user.repository.UserRepository;
import com.example.test.testingHMS.user.serviceImpl.DoctorDetailsServiceImpl;
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
public class PatientController {
	public static Logger Logger = LoggerFactory.getLogger(PatientController.class);

	private static final String YEAR = "Y ";
	private static final String MONTH = "M ";
	private static final String DAYS = "D";

	@Value("${hospital.logo}")
	private Resource hospitalLogo;
	
	@Autowired
	PatientDetailsServiceImpl patientDetailsServiceImpl;
	
	@Autowired
	FinalBillingServiceImpl finalBillingServiceImpl;
	
	@Autowired
	PdfGenerator pdfGenerator;
	
	@Autowired
	PatientDetailsRepository patientDetailsRepository;

	@Autowired
	PaymentRepository paymentRepository;

	@Autowired
	NumberToWordsConverter numberToWordsConverter;

	@Autowired
	SalesPaymentPdfRepository salesPaymentPdfRepository;

	@Autowired
	LaboratoryRegistrationServiceImpl laboratoryRegistrationServiceImpl;

	@Autowired
	PatientRegistrationServiceImpl patientRegistrationServiceImpl;

	@Autowired
	RegGenerator regGenerator;

	@Autowired
	ServletContext context;

	@Autowired
	PaymentPdfRepository paymentPdfRepository;

	@Autowired
	NotesPdfRepository notesPdfRepository;
	@Autowired
	PrescriptionDetailsRepository prescriptionDetailsRepository;

	@Autowired
	SalesServiceImpl salesServiceImpl;

	@Autowired
	NotesDetailsRepository notesDetailsRepository;

	@Autowired
	ResourceLoader resourceLoader;

	@Autowired
	ServicePdfRepository servicePdfRepository;

	@Autowired
	RoomDetailsServiceImpl roomDetailsServiceImpl;

	@Autowired
	RoomBookingDetailsServiceImpl roomBookingDetailsServiceImpl;

	@Autowired
	ReferralDetailsServiceImpl referralDetailsServiceImpl;

	@Autowired
	PatientPaymentServiceImpl patientPaymentServiceImpl;

	@Autowired
	PatientRegistrationRepository patientRegistrationRepository;

	@Autowired
	UserServiceImpl userServiceImpl;

	@Autowired
	DoctorDetailsServiceImpl doctorDetailsServiceImpl;

	@Autowired
	PaymentPdfServiceImpl paymentPdfServiceImpl;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	PatientPaymentRepository patientPaymentRepository;
	
	@Autowired
	ChargeBillRepository chargeBillRepository;

	// Get values for drop down in patient registration
	@RequestMapping(value = "/patient/create", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Object> getInfo() {
		return patientDetailsServiceImpl.pageLoad();
	}

	// Creating the new Patient

	@RequestMapping(value = "/patient/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public PatientPaymentPdf saveInfo(@RequestBody PatientDetailsDTO patientDetailsDTO, Principal p) throws Exception {
		PatientDetails patientDetails = new PatientDetails();
		BeanUtils.copyProperties(patientDetailsDTO, patientDetails);
		return patientDetailsServiceImpl.saveInfo(patientDetails, p);

	}

	/*
	 * To add referral details
	 */
	@RequestMapping(value = "/patient/refdetails", method = RequestMethod.POST)
	public void addRefDetails(@RequestBody ReferralDetailsDTO referralDetailsDTO) {
		ReferralDetails referralDetails = new ReferralDetails();
		BeanUtils.copyProperties(referralDetailsDTO, referralDetails);
		referralDetailsServiceImpl.save(referralDetails);

	}

	
	/*
	 * 
	 * referral list
	 */
	@RequestMapping(value = "/patient/referrallist")
	public List<ReferralDetails> referralList() {

		return referralDetailsServiceImpl.findAll();
	}

	/*
	 * To get referral details
	 */
	@RequestMapping(value = "/patient/refdetails/{source}", method = RequestMethod.GET)
	public List<ReferralDetails> refDetails(@PathVariable String source) {
		return referralDetailsServiceImpl.findBySource(source);

	}

	/*
	 * To get referral details
	 */
	@RequestMapping(value = "/patient/refInfo/{refSource}/{refName}", method = RequestMethod.GET)
	public ReferralDetails refInfo(@PathVariable String refSource, @PathVariable String refName) {
		return referralDetailsServiceImpl.findBySourceAndRefName(refSource, refName);

	}

	/*
	 * To get room details
	 */
	@RequestMapping(value = "/patient/roomdetails", method = RequestMethod.GET)
	public List<RoomDetails> roomDetails() {
		List<RoomDetails> freeRoom = new ArrayList<>();
		// Empty Bed
		List<RoomDetails> roomDetails = roomDetailsServiceImpl.findAll();
		for (RoomDetails roomDetailsinfo : roomDetails) {
			RoomBookingDetails roomBookingDetailsInfo = roomBookingDetailsServiceImpl
					.getroomStatus(roomDetailsinfo.getBedId());
			if (roomBookingDetailsInfo == null) {
				roomDetailsinfo.setStatus("ALLOCATE");
				freeRoom.add(roomDetailsinfo);
			} else {

				roomDetailsinfo.setStatus("OCCUPIED");
				freeRoom.add(roomDetailsinfo);
			}
		}

		return freeRoom;
	}

	/*
	 * get particular floor
	 */
	@RequestMapping(value = "/patient/roomdetails/{floor}", method = RequestMethod.GET)
	public List<RoomDetails> getFloor(@PathVariable String floor) {

		List<String> containsRoom = new ArrayList<>();

		List<RoomDetails> freeRoom = new ArrayList<>();
		List<RoomDetails> roomDetails = roomDetailsServiceImpl.findByFloorNo(floor);
		for (RoomDetails roomDetailsinfo : roomDetails) {
			if (!containsRoom.contains(roomDetailsinfo.getRoomType())) {
				RoomBookingDetails roomBookingDetailsInfo = roomBookingDetailsServiceImpl
						.getroomStatus(roomDetailsinfo.getBedId());
				if (roomBookingDetailsInfo == null) {
					roomDetailsinfo.setStatus("ALLOCATE");
					freeRoom.add(roomDetailsinfo);
				} else {

					roomDetailsinfo.setStatus("OCCUPIED");
					freeRoom.add(roomDetailsinfo);
				}
				containsRoom.add(roomDetailsinfo.getRoomType());
			}
		}

		return freeRoom;
	}

	/*
	 * To search patient by mobile no
	 */
	@RequestMapping(value = "/patient/search/{mobile}", method = RequestMethod.GET)
	public PatientDetails searchByMobile(@PathVariable long mobile) {
		return patientDetailsServiceImpl.findByMobile(mobile);
	}

	
	/*
	 * Generates Pdf for update patient
	 */
	@RequestMapping(value = "/patient/updateAll/{umr}/{regId}", method = RequestMethod.PUT)
	public PatientPaymentPdf updateAll(@RequestBody PatientDetails patientDetails, @PathVariable String umr,@PathVariable String regId,Principal principal) {
		PatientDetails patientDetailsInfo = patientDetailsServiceImpl.getPatientByUmr(umr);
		long id = patientDetailsInfo.getPatientId();
		String age = patientDetailsInfo.getAge();
		String discharged = patientDetailsInfo.getDischarged();
		String umrPre = patientDetailsInfo.getUmr();
		MarketingQuestions marketingQuestions = patientDetailsInfo.getvMarketingQuestion();
		ReferralDetails referralDetails = patientDetailsInfo.getvRefferalDetails();
		
		PatientRegistration patientRegistration=patientRegistrationServiceImpl.findByRegId(regId);

		// Calculating age
		LocalDate todayLocal = LocalDate.now();
		LocalDate birthday = patientDetails.getDob().toLocalDateTime().toLocalDate();
		Period p = Period.between(birthday, todayLocal);
		int accurate_age = 0;

		if (p.getMonths() >= 5) {
			accurate_age = p.getYears() + 1;
		} else {
			accurate_age = p.getYears();
		}
		if (p.getYears() == 0) {
			if (p.getDays() == 0) {
				String lessThanOne = String
						.valueOf(p.getYears() + YEAR + p.getMonths() + MONTH + String.valueOf(1) + DAYS);
				age = String.valueOf(lessThanOne);
			} else {
				int days = p.getDays() + 1;
				String lessThanOne = String.valueOf(p.getYears() + YEAR + p.getMonths()) + MONTH
						+ String.valueOf(days + DAYS);
				age = String.valueOf(lessThanOne);
			}
		} else {
			int days = 0;
			if (p.getMonths() == 0) {
				days = p.getDays() + 1;
				age=
						String.valueOf(String.valueOf(p.getYears() + YEAR + p.getMonths()) + MONTH + days + DAYS);
			} else {
				days = p.getDays() + 1;
				age=
						String.valueOf(String.valueOf(p.getYears() + YEAR + p.getMonths()) + MONTH + days + DAYS);

			}
		}
		BeanUtils.copyProperties(patientDetails, patientDetailsInfo);
		patientDetailsInfo.setPatientId(id);
		patientDetailsInfo.setvMarketingQuestion(marketingQuestions);
		patientDetailsInfo.setAge(age);
		patientDetailsInfo.setDischarged(discharged);
		patientDetailsInfo.setvRefferalDetails(referralDetails);
		patientDetailsInfo.setUmr(umrPre);
		patientDetailsServiceImpl.save(patientDetailsInfo);
		
		// Blank prescription
		patientDetailsServiceImpl.createBlankPrescription(umr, regId,principal);
		if(!patientRegistrationServiceImpl.findByRegId(regId).getpType().equalsIgnoreCase(ConstantValues.OUTPATIENT))
		{	
			// Admission Slip
			patientDetailsServiceImpl.admissionSlipInfo(regId, principal);
		}
		
		// Sales pdf generator
		pdfGenerator.salesPdfGenerator(regId, principal);
		
		// Lab pdf generator
		pdfGenerator.labPdfGenerate(regId);
		
		//for final and discharge slip
		
		List<RoomBookingDetails> roomBookingDetails=patientRegistration.getRoomBookingDetails();
		if(!roomBookingDetails.isEmpty()) {
		if(roomBookingDetails.get(0).getStatus()==0) {
			
			pdfGenerator.ipFinalPdfGenerator(regId, principal);
			pdfGenerator.ipSettledPdfGenerator(regId, principal);
			
		}
		}
		// Consultation receipt
		 return patientDetailsServiceImpl.createConsultationReceipt(regId,principal);
		 
		 
		 
	}
	

	// Get patient details by umr
	@RequestMapping(value = "/patient/getOne/{umr}", method = RequestMethod.GET)
	public PatientDetails getPd(@PathVariable String umr) {
		return patientDetailsServiceImpl.getPatientByUmr(umr);
	}

	/*
	 * Room booking extension for patients
	 */

	@RequestMapping(value = "/patient/extend", method = RequestMethod.POST)
	public void extendRoom(@RequestBody Map<String, Object> info) {
		RoomBookingDetails roomBookingDetails = roomBookingDetailsServiceImpl.findByPatientRegistrationBooking(
				patientRegistrationServiceImpl.findByRegId(info.get("regId").toString()));
		String toDate  ;
		if(info.get("toDate")!=null) {
			
			 toDate = info.get("toDate").toString();
		}
		else {
		 toDate =Timestamp.valueOf(LocalDateTime.now()).toString();
		}
		if (!toDate.equals("")) {
			DateFormat formatterIST = new SimpleDateFormat("yyyy-MM-dd");
			formatterIST.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata")); // better than using IST
			Date date = null;
			try {
				date = formatterIST.parse(toDate);
			} catch (ParseException e) {
				Logger.error(e.getMessage());
			}
			String myDate = formatterIST.format(date);
			Timestamp timeTo = Timestamp.valueOf(myDate + " 00:00:00.000");
			roomBookingDetails.setToDate(timeTo);
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(timeTo.getTime());
			calendar.add(Calendar.DATE, -1);
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			roomBookingDetails.setTentativeDischargeDate(Timestamp.valueOf(dateFormat.format(calendar.getTime())));

		} else {
			roomBookingDetails.setToDate(null);
			roomBookingDetails.setTentativeDischargeDate(null);
		}
		RoomDetails roomDetails = roomDetailsServiceImpl.findByBedName(info.get("room").toString());
		roomBookingDetails.setRoomDetails(roomDetails);
		roomBookingDetails.setStatus(1);
		roomBookingDetails.setBedNo(info.get("room").toString());
		roomBookingDetailsServiceImpl.save(roomBookingDetails);
	}

	/*
	 * get all patient
	 */
	@RequestMapping(value = "/patient/getAll", method = RequestMethod.GET)
	public List<Object> getAllPd() {
		return patientDetailsServiceImpl.getAllPd();

	}

	// To get Pat info for edit consultanat,regfee,docfee

	@RequestMapping(value = "/patient/consultant/change/{regId}", method = RequestMethod.GET)
	public Map<String, String> getChangeConsultnt(@PathVariable String regId, Principal principal) {
		Map<String, String> displayInfo = new HashMap<>();
		PatientRegistration patientRegistration = patientRegistrationServiceImpl.findByRegId(regId);
		String consultantFee = "";
		String regFee = "";
		String patientType = "";
		Set<PatientPayment> patientPayments = patientRegistration.getPatientPayment();
		for (PatientPayment patientPaymentinfo : patientPayments) {
			if (patientPaymentinfo.getTypeOfCharge().equalsIgnoreCase("Reg Fees")) {
				regFee = String.valueOf(patientPaymentinfo.getAmount());
			} else if (patientPaymentinfo.getTypeOfCharge().equalsIgnoreCase("Doctor Fee")) {
				consultantFee = String.valueOf(patientPaymentinfo.getAmount());
			}

		}
		patientType = patientRegistration.getpType();
		if (patientType.equalsIgnoreCase(ConstantValues.INPATIENT)||patientType.equalsIgnoreCase(ConstantValues.DAYCARE)||patientType.equalsIgnoreCase(ConstantValues.EMERGENCY)) {
			RoomBookingDetails roomBookingDetails = roomBookingDetailsServiceImpl
					.findByPatientRegistrationBookingAndStatus(patientRegistration, 1);
			displayInfo.put("Consultant", patientRegistration.getPatientDetails().getConsultant());
			displayInfo.put("ConsultantFee", consultantFee);
			displayInfo.put("regFee", regFee);
			displayInfo.put("type", patientRegistration.getpType());
			displayInfo.put("room", roomBookingDetails.getRoomDetails().getRoomType());
		} else {
			displayInfo.put("Consultant", patientRegistration.getPatientDetails().getConsultant());
			displayInfo.put("ConsultantFee", consultantFee);
			displayInfo.put("regFee", regFee);
			displayInfo.put("type", patientRegistration.getpType());
			displayInfo.put("room", "Not Allocated");
		}
		return displayInfo;
	}

	// To change modeOfPayment of advance

	@RequestMapping(value = "/change", method = RequestMethod.POST)
	public void changeMOP() {
		String mop = "";
		boolean advnce = false;
		List<PatientPayment> patientPaymentList = patientPaymentServiceImpl.findAll();
		for (PatientPayment patientPaymentInfo : patientPaymentList) {
			PatientRegistration patientRegistration = patientPaymentInfo.getPatientRegistration();
			Set<PatientPayment> preg = patientPaymentServiceImpl.findByPatientRegistration(patientRegistration);
			for (PatientPayment pregList : preg) {

				if (pregList.getModeOfPaymant().equalsIgnoreCase("Advance")
						&& pregList.getTypeOfCharge().equalsIgnoreCase("Advance") && advnce == true) {
					pregList.setModeOfPaymant(mop);
					patientPaymentServiceImpl.save(pregList);
					advnce = false;

				}
				if (pregList.getTypeOfCharge().equals("Reg Fees")) {
					mop = pregList.getModeOfPaymant();
					advnce = true;
				} else if (pregList.getTypeOfCharge().equals("Doctor Fee")) {
					mop = pregList.getModeOfPaymant();
					advnce = true;
				}
			}
		}
	}


	
	// To change consultant

		@RequestMapping(value = "/patient/consultant/change", method = RequestMethod.POST)
		public PatientPaymentPdf changeConsultant(@RequestBody Map<String, String> info, Principal principal) {
			String regId = info.get("regId");
			String consultant = info.get("consultant");
			long consultantFee = Long.parseLong(info.get("consultantFee"));
			long regFee = Long.parseLong(info.get("regFee"));

			String[] docNameSplit = null;
			String docName = "";
			User user = null;
			String refBy = "";
			String patientName = "";
			String middleName = "";
			String umr = "";
			String patientType = "";
			Timestamp regDateTimestamp = null;
			String age = "";
			String gender = "";
			String regValidity = "";
			long phone = 0;
			String mop = "";
			String father = "";
			String admittedWard = "";
			String department = "";
			String regDate = "";
			String address = "";
			String state = "";
			String city = "";
			String createdBy = "";
			String printedDate = "";
			int advanceAmount = 0;
			int docAmount = 0;
			int regAmount = 0;
			String toc = "";
			int rectAmount = 0;
			String refNo=null;

			PatientRegistration patientRegistration = null;

			patientRegistration = patientRegistrationServiceImpl.findByRegId(regId);
			patientType = patientRegistration.getpType();
			umr = patientRegistration.getPatientDetails().getUmr();
			regDateTimestamp = patientRegistration.getRegDate();
			age = patientRegistration.getPatientDetails().getAge();
			gender = patientRegistration.getPatientDetails().getGender();
			phone = patientRegistration.getPatientDetails().getMobile();
			father = patientRegistration.getPatientDetails().getMotherName();
			department = patientRegistration.getVuserD().getDoctorDetails().getSpecilization();
			address = patientRegistration.getPatientDetails().getAddress();
			city = patientRegistration.getPatientDetails().getCity();
			state = patientRegistration.getPatientDetails().getState();
			if (patientRegistration.getPatientDetails().getvRefferalDetails() != null) {
				refBy = patientRegistration.getPatientDetails().getvRefferalDetails().getRefName();
			}
			User userCreated = userServiceImpl.findOneByUserId(patientRegistration.getCreatedBy());

			// Reg validity
			patientRegistration.setRegDate(patientRegistration.getRegDate());
			regDate = patientRegistration.getRegDate().toString().substring(0, 10);
			regDateTimestamp = patientRegistration.getRegDate();
			regValidity = LocalDate.parse(regDate).plusDays(7).toString();

			if (patientType.equalsIgnoreCase(ConstantValues.INPATIENT)||patientType.equalsIgnoreCase(ConstantValues.DAYCARE)||patientType.equalsIgnoreCase(ConstantValues.EMERGENCY)) {
				admittedWard = patientRegistration.getRoomBookingDetails()
						.get(patientRegistration.getRoomBookingDetails().size() - 1).getBedNo();
			}

			if (userCreated.getMiddleName() != null) {
				createdBy = userCreated.getFirstName() + ConstantValues.ONE_SPACE_STRING + userCreated.getMiddleName()
						+ ConstantValues.ONE_SPACE_STRING + userCreated.getLastName();
			} else {
				createdBy = userCreated.getFirstName() + ConstantValues.ONE_SPACE_STRING + userCreated.getLastName();
			}

			Date date = new Date(patientRegistration.getCreatedAt().getTime());
			SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
			printedDate = formatter.format(date).toString();

			if (patientRegistration.getPatientDetails().getMiddleName() != null) {
				patientName = patientRegistration.getPatientDetails().getTitle() + ". "
						+ patientRegistration.getPatientDetails().getFirstName() + ConstantValues.ONE_SPACE_STRING
						+ patientRegistration.getPatientDetails().getMiddleName() + ConstantValues.ONE_SPACE_STRING
						+ patientRegistration.getPatientDetails().getLastName();
			} else {
				patientName = patientRegistration.getPatientDetails().getTitle() + ". "
						+ patientRegistration.getPatientDetails().getFirstName() + ConstantValues.ONE_SPACE_STRING
						+ middleName + ConstantValues.ONE_SPACE_STRING
						+ patientRegistration.getPatientDetails().getLastName();
			}

			if (consultant.contains("-")) {
				docName = consultant;
				docNameSplit = docName.split("-");
				user = userServiceImpl.findOneByUserId(docNameSplit[1]);
				patientRegistration.setVuserD(user);
				patientRegistration.getPatientDetails().setConsultant(docNameSplit[0]);

			} else {
				patientRegistration.setVuserD(patientRegistration.getVuserD());

			}

			// Updating payment Fee
			PatientPayment patientPaymentsReg = patientPaymentServiceImpl.findPatientByRegFee(regId, "Reg Fees");
			if (patientPaymentsReg != null) {
				patientPaymentsReg.setAmount(regFee);
			}
			PatientPayment patientPaymentsDoc = patientPaymentServiceImpl.findPatientByRegFee(regId, "Doctor Fee");
			if (patientPaymentsDoc != null) {
				patientPaymentsDoc.setAmount(consultantFee);
			}

			patientRegistrationServiceImpl.save(patientRegistration);

			// Patient Payment
			String bill=null;
			Set<PatientPayment> patientPayments = patientRegistration.getPatientPayment();
			for (PatientPayment patientInfo : patientPayments) {
				if (patientInfo.getTypeOfCharge().equalsIgnoreCase("Reg Fees")) {
					regAmount = (int) patientInfo.getAmount();
					toc = patientInfo.getTypeOfCharge();
					mop = patientInfo.getModeOfPaymant();
					rectAmount += regAmount;
				} else if (patientInfo.getTypeOfCharge().equalsIgnoreCase("Doctor Fee")) {
					docAmount = (int) patientInfo.getAmount();
					mop = patientInfo.getModeOfPaymant();
					rectAmount += docAmount;
				} else if (patientInfo.getTypeOfCharge().equalsIgnoreCase("Advance")) {
					advanceAmount = (int) patientInfo.getAmount();
					mop = patientInfo.getModeOfPaymant();
					rectAmount += advanceAmount;
				}
				bill=patientInfo.getBillNo();
				refNo=patientInfo.getReferenceNumber();
			}

			if (toc.equals("")) {
				toc = "Reg Fees";
				regAmount = 0;
			}

			
			float totalAmount=regAmount+docAmount+advanceAmount;
			
			// Reg Date
			date = new Date(regDateTimestamp.getTime());
			formatter = new SimpleDateFormat("dd-MMM-yyyy");
			String today = formatter.format(date).toString();

			try {
				SimpleDateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat toFormat = new SimpleDateFormat("dd-MMM-yyyy");
				regValidity = toFormat.format(fromFormat.parse(regValidity));
			} catch (Exception e) {
				Logger.error(e.getMessage());
			}

			//for changing final billing table
			FinalBilling finalBilling=finalBillingServiceImpl.findByBillTypeAndBillNoAndRegNo("Patient Registration", bill, regId);
			if(mop.equalsIgnoreCase(ConstantValues.CASH))
	 		  {
	 		  	finalBilling.setCashAmount(Math.round(totalAmount));
	 		  }
	 		  else if(mop.equalsIgnoreCase(ConstantValues.CARD))
	 		  {
	 			  finalBilling.setCardAmount(Math.round(totalAmount));
	 		  }
	 		  else if(mop.equalsIgnoreCase(ConstantValues.CHEQUE))
	 		  {
	 			  finalBilling.setChequeAmount(Math.round(totalAmount));
	 		  }else if(mop.equalsIgnoreCase(ConstantValues.DUE)) {
	 			 finalBilling.setDueAmount(Math.round(totalAmount));
	 			  
	 		  }
			finalBilling.setFinalAmountPaid(Math.round(totalAmount));
			finalBilling.setTotalAmount(totalAmount);
			finalBillingServiceImpl.computeSave(finalBilling);
			
			
			
			
			String addrss=ConstantValues.ADVANCE_RECEIPT_ADDRESS;
				ByteArrayOutputStream byteArrayOutputStream = null;
			PatientPaymentPdf patientPaymentPdf = null;
			byte[] pdfBytes = null;
			if (patientType.equals(ConstantValues.INPATIENT)||patientType.equals(ConstantValues.DAYCARE)||patientType.equals(ConstantValues.EMERGENCY)) {

				byteArrayOutputStream = new ByteArrayOutputStream();

				try {
					Document document = new Document(PageSize.A4_LANDSCAPE);

					Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

					Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
					Font headFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
					PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);

					
					Image img = Image.getInstance(hospitalLogo.getURL());
					document.open();
					PdfPTable table = new PdfPTable(2);

					img.scaleAbsolute(ConstantValues.IMAGE_ABSOLUTE_INTIAL_POSITION, ConstantValues.IMAGE_ABSOLUTE_FINAL_POSITION);
					table.setWidthPercentage(ConstantValues.TABLE_SET_WIDTH_PERECENTAGE);

					Phrase pq = new Phrase(new Chunk(img, ConstantValues.IMAGE_SET_INTIAL_POSITION, ConstantValues.IMAGE_SET_FINAL_POSITION));
                 	pq.add(new Chunk(addrss, redFont));
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

					PdfPCell hcell1;
					hcell1 = new PdfPCell(new Phrase("UMR NO", redFont));
					hcell1.setBorder(Rectangle.NO_BORDER);
					hcell1.setPaddingLeft(-25f);
					hcell1.setPaddingTop(-5f);
					table2.addCell(hcell1);

					hcell1 = new PdfPCell(new Phrase(":", redFont));
					hcell1.setBorder(Rectangle.NO_BORDER);
					hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell1.setPaddingLeft(-10f);
					hcell1.setPaddingTop(-5f);
					table2.addCell(hcell1);

					hcell1 = new PdfPCell(new Phrase(umr, redFont));
					hcell1.setBorder(Rectangle.NO_BORDER);
					hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell1.setPaddingLeft(-15f);
					hcell1.setPaddingTop(-5f);
					table2.addCell(hcell1);

					hcell1 = new PdfPCell(new Phrase("", redFont));
					hcell1.setBorder(Rectangle.NO_BORDER);
					hcell1.setPaddingRight(34f);
					hcell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
					table2.addCell(hcell1);

					hcell1 = new PdfPCell(new Phrase("", redFont));
					hcell1.setBorder(Rectangle.NO_BORDER);
					hcell1.setPaddingRight(34f);
					hcell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
					table2.addCell(hcell1);

					hcell1 = new PdfPCell(new Phrase("", redFont));
					hcell1.setBorder(Rectangle.NO_BORDER);
					hcell1.setPaddingRight(34f);
					hcell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
					table2.addCell(hcell1);

					PdfPCell hcell4;
					hcell4 = new PdfPCell(new Phrase("Rect.No", redFont));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setPaddingLeft(-25f);
					// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					table2.addCell(hcell4);

					hcell4 = new PdfPCell(new Phrase(":", redFont));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell4.setPaddingLeft(-10f);
					table2.addCell(hcell4);

					hcell4 = new PdfPCell(new Phrase(regId, redFont));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell4.setPaddingLeft(-15f);
					table2.addCell(hcell4);

					hcell4 = new PdfPCell(new Phrase("Rect.Dt", redFont));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell1.setPaddingRight(-27.5f);
					table2.addCell(hcell4);

					hcell4 = new PdfPCell(new Phrase(":", redFont));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setHorizontalAlignment(Element.ALIGN_RIGHT);
					hcell4.setPaddingRight(-0.1f);
					table2.addCell(hcell4);

					hcell4 = new PdfPCell(new Phrase(today, redFont));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell4.setPaddingRight(-20.5f);
					table2.addCell(hcell4);

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

					hcell4 = new PdfPCell(new Phrase(":", redFont));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setHorizontalAlignment(Element.ALIGN_RIGHT);
					hcell4.setPaddingRight(-0.1f);
					table2.addCell(hcell4);

					hcell4 = new PdfPCell(new Phrase(String.valueOf(phone), redFont));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell4.setPaddingRight(-20.5f);
					table2.addCell(hcell4);

					cell3.setFixedHeight(110f);
					cell3.setColspan(2);
					cell3.addElement(table2);

					table.addCell(cell3);

					PdfPCell cell19 = new PdfPCell();

					PdfPTable table21 = new PdfPTable(1);
					table21.setWidths(new float[] { 4f });
					table21.setSpacingBefore(10);

					PdfPCell hcell19;
					hcell19 = new PdfPCell(new Phrase("Advance Receipt", headFont1));
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
					hcell = new PdfPCell(new Phrase("Father", redFont));
					hcell.setBorder(Rectangle.NO_BORDER);
					hcell.setPaddingLeft(-50f);
					table3.addCell(hcell);

					hcell = new PdfPCell(new Phrase(":", redFont));
					hcell.setBorder(Rectangle.NO_BORDER);
					hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell.setPaddingLeft(-80f);
					table3.addCell(hcell);

					hcell = new PdfPCell(new Phrase(father, redFont));
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

					hcell11 = new PdfPCell(new Phrase(today, redFont));
					hcell11.setBorder(Rectangle.NO_BORDER);
					hcell11.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell11.setPaddingLeft(-80f);
					table3.addCell(hcell11);

					String dpt = null;
					if (department != null) {
						dpt = department;
					} else {
						dpt = "";
					}
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

					hcell14 = new PdfPCell(new Phrase(docNameSplit[0], redFont));
					hcell14.setBorder(Rectangle.NO_BORDER);
					hcell14.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell14.setPaddingLeft(-80f);
					table3.addCell(hcell14);

					hcell14 = new PdfPCell(new Phrase("Consultation Fee", redFont));
					hcell14.setBorder(Rectangle.NO_BORDER);
					hcell14.setHorizontalAlignment(Element.ALIGN_LEFT);
					table3.addCell(hcell14);

					hcell14 = new PdfPCell(new Phrase(":", redFont));
					hcell14.setBorder(Rectangle.NO_BORDER);
					hcell14.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell14.setPaddingLeft(-15f);
					table3.addCell(hcell14);

					hcell14 = new PdfPCell(new Phrase(String.valueOf(docAmount), redFont));
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

					hcell16 = new PdfPCell(new Phrase("Advance Amount", redFont));
					hcell16.setBorder(Rectangle.NO_BORDER);
					hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
					table3.addCell(hcell16);

					hcell16 = new PdfPCell(new Phrase(":", redFont));
					hcell16.setBorder(Rectangle.NO_BORDER);
					hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell16.setPaddingLeft(-15f);
					table3.addCell(hcell16);

					hcell16 = new PdfPCell(new Phrase(String.valueOf(advanceAmount), redFont));
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

					if (toc.equalsIgnoreCase("Nursing Charges")) {
						hcell17 = new PdfPCell(new Phrase(toc, redFont));
						hcell17.setBorder(Rectangle.NO_BORDER);
						hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
						table3.addCell(hcell17);

						hcell17 = new PdfPCell(new Phrase(":", redFont));
						hcell17.setBorder(Rectangle.NO_BORDER);
						hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell17.setPaddingLeft(-15f);
						table3.addCell(hcell17);

						hcell17 = new PdfPCell(new Phrase("", redFont));
						hcell17.setBorder(Rectangle.NO_BORDER);
						hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell17.setPaddingLeft(-20f);
						table3.addCell(hcell17);
					} else if (toc.equalsIgnoreCase("Service Charges")) {
						hcell17 = new PdfPCell(new Phrase(toc, redFont));
						hcell17.setBorder(Rectangle.NO_BORDER);
						hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
						table3.addCell(hcell17);

						hcell17 = new PdfPCell(new Phrase(":", redFont));
						hcell17.setBorder(Rectangle.NO_BORDER);
						hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell17.setPaddingLeft(-15f);
						table3.addCell(hcell17);

						hcell17 = new PdfPCell(new Phrase("", redFont));
						hcell17.setBorder(Rectangle.NO_BORDER);
						hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell17.setPaddingLeft(-20f);
						table3.addCell(hcell17);
					} else if (toc.equalsIgnoreCase("Reg Fees")) {
						hcell17 = new PdfPCell(new Phrase(toc, redFont));
						hcell17.setBorder(Rectangle.NO_BORDER);
						hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
						table3.addCell(hcell17);

						hcell17 = new PdfPCell(new Phrase(":", redFont));
						hcell17.setBorder(Rectangle.NO_BORDER);
						hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell17.setPaddingLeft(-15f);
						table3.addCell(hcell17);

						hcell17 = new PdfPCell(new Phrase(String.valueOf(regAmount), redFont));
						hcell17.setBorder(Rectangle.NO_BORDER);
						hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell17.setPaddingLeft(-20f);
						table3.addCell(hcell17);
					} else if (toc.equalsIgnoreCase("Vaccination fees")) {
						hcell17 = new PdfPCell(new Phrase(toc, redFont));
						hcell17.setBorder(Rectangle.NO_BORDER);
						hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
						table3.addCell(hcell17);

						hcell17 = new PdfPCell(new Phrase(":", redFont));
						hcell17.setBorder(Rectangle.NO_BORDER);
						hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell17.setPaddingLeft(-15f);
						table3.addCell(hcell17);

						hcell17 = new PdfPCell(new Phrase("", redFont));
						hcell17.setBorder(Rectangle.NO_BORDER);
						hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell17.setPaddingLeft(-20f);
						table3.addCell(hcell17);
					}

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

					hcell18 = new PdfPCell(new Phrase("Rect.Amount", redFont));
					hcell18.setBorder(Rectangle.NO_BORDER);
					hcell18.setHorizontalAlignment(Element.ALIGN_LEFT);
					table3.addCell(hcell18);

					hcell18 = new PdfPCell(new Phrase(":", redFont));
					hcell18.setBorder(Rectangle.NO_BORDER);
					hcell18.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell18.setPaddingLeft(-15f);
					table3.addCell(hcell18);

					hcell18 = new PdfPCell(new Phrase(String.valueOf(rectAmount), redFont));
					hcell18.setBorder(Rectangle.NO_BORDER);
					hcell18.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell18.setPaddingLeft(-20f);
					table3.addCell(hcell18);

					PdfPCell hcell20;
					hcell20 = new PdfPCell(new Phrase("State", redFont));
					hcell20.setBorder(Rectangle.NO_BORDER);
					hcell20.setPaddingLeft(-50f);
					table3.addCell(hcell20);

					hcell20 = new PdfPCell(new Phrase(":", redFont));
					hcell20.setBorder(Rectangle.NO_BORDER);
					hcell20.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell20.setPaddingLeft(-80f);
					table3.addCell(hcell20);

					hcell20 = new PdfPCell(new Phrase(state, redFont));
					hcell20.setBorder(Rectangle.NO_BORDER);
					hcell20.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell20.setPaddingLeft(-80f);
					table3.addCell(hcell20);

					hcell20 = new PdfPCell(new Phrase("", redFont));
					hcell20.setBorder(Rectangle.NO_BORDER);
					hcell20.setHorizontalAlignment(Element.ALIGN_LEFT);
					table3.addCell(hcell20);

					hcell20 = new PdfPCell(new Phrase("", redFont));
					hcell20.setBorder(Rectangle.NO_BORDER);
					hcell20.setHorizontalAlignment(Element.ALIGN_LEFT);
					table3.addCell(hcell20);

					hcell20 = new PdfPCell(new Phrase("", redFont));
					hcell20.setBorder(Rectangle.NO_BORDER);
					hcell20.setHorizontalAlignment(Element.ALIGN_LEFT);
					table3.addCell(hcell20);

					
					PdfPCell hcell91;
					hcell91 = new PdfPCell(new Phrase("Payment Mode", redFont));
					hcell91.setBorder(Rectangle.NO_BORDER);
					hcell91.setPaddingTop(10f);
					hcell91.setPaddingLeft(-50f);
					table3.addCell(hcell91);

					hcell91 = new PdfPCell(new Phrase(":", redFont));
					hcell91.setBorder(Rectangle.NO_BORDER);
					hcell91.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell91.setPaddingTop(10f);
					hcell91.setPaddingLeft(-80f);
					table3.addCell(hcell91);

					hcell91 = new PdfPCell(new Phrase(mop, redFont));
					hcell91.setBorder(Rectangle.NO_BORDER);
					hcell91.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell91.setPaddingTop(10f);
					hcell91.setPaddingLeft(-80f);
					table3.addCell(hcell91);

					if(mop.equalsIgnoreCase("card") || mop.equalsIgnoreCase("debit card") || mop.equalsIgnoreCase("credit card") || mop.equalsIgnoreCase("cash+card"))
					{
					hcell91 = new PdfPCell(new Phrase("Reference No", redFont));
					hcell91.setBorder(Rectangle.NO_BORDER);
					hcell91.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell91.setPaddingTop(10f);
					table3.addCell(hcell91);

					hcell91 = new PdfPCell(new Phrase(":", redFont));
					hcell91.setBorder(Rectangle.NO_BORDER);
					hcell91.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell91.setPaddingTop(10f);
					hcell91.setPaddingLeft(-15f);
					table3.addCell(hcell91);

					hcell91 = new PdfPCell(new Phrase(refNo, redFont));
					hcell91.setBorder(Rectangle.NO_BORDER);
					hcell91.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell91.setPaddingTop(10f);
					hcell91.setPaddingLeft(-20f);
					table3.addCell(hcell91);
					}
					else 
					{
						hcell91 = new PdfPCell(new Phrase("", redFont));
						hcell91.setBorder(Rectangle.NO_BORDER);
						hcell91.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell91.setPaddingTop(10f);
						table3.addCell(hcell91);

						hcell91 = new PdfPCell(new Phrase("", redFont));
						hcell91.setBorder(Rectangle.NO_BORDER);
						hcell91.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell91.setPaddingTop(10f);
						hcell91.setPaddingLeft(-15f);
						table3.addCell(hcell91);

						hcell91 = new PdfPCell(new Phrase("", redFont));
						hcell91.setBorder(Rectangle.NO_BORDER);
						hcell91.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell91.setPaddingTop(10f);
						hcell91.setPaddingLeft(-20f);
						table3.addCell(hcell91);
					}

					
					PdfPTable table91 = new PdfPTable(1);
					table91.setWidths(new float[] { 5f });
					table91.setSpacingBefore(10);

					cell4.setFixedHeight(170f);
					cell4.setColspan(2);
					cell4.addElement(table3);

					PdfPCell hcell98;
					hcell98 = new PdfPCell(
							new Phrase(
									"Received with thanks from " + patientName + ", " + "A sum of Rs. " + rectAmount
											+ "\n\n" + "In Words Rupees " + numberToWordsConverter.convert(rectAmount),
									redFont));
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
					Font redFont3 = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
					hcell21 = new PdfPCell(new Phrase("*" + umr + "*", redFont3));
					hcell21.setBorder(Rectangle.NO_BORDER);
					hcell21.setPaddingLeft(-50f);
					table35.addCell(hcell21);

					hcell21 = new PdfPCell(new Phrase("*" + regId + "*", redFont3));
					hcell21.setBorder(Rectangle.NO_BORDER);
					hcell21.setHorizontalAlignment(Element.ALIGN_RIGHT);
					table35.addCell(hcell21);

					PdfPCell hcell12;
					hcell12 = new PdfPCell(new Phrase("Created By    : " + createdBy, redFont));
					hcell12.setBorder(Rectangle.NO_BORDER);
					hcell12.setPaddingTop(10f);
					hcell12.setPaddingLeft(-50f);
					table35.addCell(hcell12);

					hcell12 = new PdfPCell(new Phrase("Created Dt   :   " + printedDate, redFont));
					hcell12.setBorder(Rectangle.NO_BORDER);
					hcell12.setPaddingTop(10f);
					hcell12.setHorizontalAlignment(Element.ALIGN_RIGHT);
					table35.addCell(hcell12);

					PdfPCell hcell13;
					hcell13 = new PdfPCell(new Phrase("Printed By     : " + createdBy, redFont));
					hcell13.setBorder(Rectangle.NO_BORDER);
					hcell13.setPaddingLeft(-50f);
					table35.addCell(hcell13);

					hcell13 = new PdfPCell(new Phrase("Print Dt       :   " + printedDate, redFont));
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
					String uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/payment/viewFile/")
							.path(paymentPdfServiceImpl.getNextPdfId()).toUriString();

					patientPaymentPdf = new PatientPaymentPdf(regId + " Advance Reciept", uri,regId, pdfBytes,bill);
					patientPaymentPdf.setPid(paymentPdfServiceImpl.getNextPdfId());
					paymentPdfServiceImpl.save(patientPaymentPdf);
				} catch (Exception e) {
					Logger.error(e.getMessage());
				}

			}

			else
			// -----------------------------------------
			{
				try {
					byteArrayOutputStream = new ByteArrayOutputStream();
					Document document = new Document(PageSize.A4_LANDSCAPE);

					Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

					Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
					Font headFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
					PdfWriter r = PdfWriter.getInstance(document, byteArrayOutputStream);
					document.open();
					PdfPTable table = new PdfPTable(2);
					Resource fileResourcee = resourceLoader.getResource(
							ConstantValues.IMAGE_PNG_CLASSPATH);

				Image img = Image.getInstance(hospitalLogo.getURL());

					img.scaleAbsolute(ConstantValues.IMAGE_ABSOLUTE_INTIAL_POSITION, ConstantValues.IMAGE_ABSOLUTE_FINAL_POSITION);
					table.setWidthPercentage(ConstantValues.TABLE_SET_WIDTH_PERECENTAGE);

					Phrase pq = new Phrase(new Chunk(img, ConstantValues.IMAGE_SET_INTIAL_POSITION, ConstantValues.IMAGE_SET_FINAL_POSITION));

					pq.add(new Chunk(addrss, redFont));
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
					hcell96.setPaddingTop(10f);
					hcell96.setPaddingLeft(52f);

					table96.addCell(hcell96);
					cell1.addElement(table96);
					cell1.addElement(pq);
					cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
					table.addCell(cell1);
					// ----------------------------------
					PdfPCell cell3 = new PdfPCell();

					PdfPTable table99 = new PdfPTable(3);
					table99.setWidths(new float[] { 3f, 1f, 5f });
					table99.setSpacingBefore(10);

					PdfPCell hcell90;
					hcell90 = new PdfPCell(new Phrase("Patient", redFont));
					hcell90.setBorder(Rectangle.NO_BORDER);
					hcell90.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell90.setPaddingLeft(-25f);
					hcell90.setPaddingTop(15f);
					table99.addCell(hcell90);

					hcell90 = new PdfPCell(new Phrase(":", redFont));
					hcell90.setBorder(Rectangle.NO_BORDER);
					hcell90.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell90.setPaddingLeft(-50f);
					hcell90.setPaddingTop(15f);
					table99.addCell(hcell90);

					hcell90 = new PdfPCell(new Phrase(patientName, redFont));
					hcell90.setBorder(Rectangle.NO_BORDER);
					hcell90.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell90.setPaddingLeft(-65f);
					hcell90.setPaddingTop(15f);
					table99.addCell(hcell90);

					cell3.addElement(table99);
					// table.addCell(cell3);

					PdfPTable table2 = new PdfPTable(6);
					table2.setWidths(new float[] { 3f, 1f, 5f, 3f, 1f, 4f });
					table2.setSpacingBefore(10);

					PdfPCell hcell1;
					hcell1 = new PdfPCell(new Phrase("Age/Sex", redFont));
					hcell1.setBorder(Rectangle.NO_BORDER);
					hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell1.setPaddingLeft(-25f);
					hcell1.setPaddingTop(-5f);
					table2.addCell(hcell1);

					hcell1 = new PdfPCell(new Phrase(":", redFont));
					hcell1.setBorder(Rectangle.NO_BORDER);
					hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell1.setPaddingLeft(-15f);
					hcell1.setPaddingTop(-5f);
					table2.addCell(hcell1);

					hcell1 = new PdfPCell(new Phrase(age + "/" + gender, redFont));
					hcell1.setBorder(Rectangle.NO_BORDER);
					hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell1.setPaddingLeft(-20f);
					hcell1.setPaddingTop(-5f);
					table2.addCell(hcell1);

					hcell1 = new PdfPCell(new Phrase("UMR NO", redFont));
					hcell1.setBorder(Rectangle.NO_BORDER);
					hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell1.setPaddingRight(-27.5f);
					hcell1.setPaddingTop(-5f);
					table2.addCell(hcell1);

					hcell1 = new PdfPCell(new Phrase(":", redFont));
					hcell1.setBorder(Rectangle.NO_BORDER);
					hcell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
					hcell1.setPaddingTop(-5f);
					;
					table2.addCell(hcell1);

					hcell1 = new PdfPCell(new Phrase(umr, redFont));
					hcell1.setBorder(Rectangle.NO_BORDER);
					hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell1.setPaddingRight(-27.5f);
					hcell1.setPaddingTop(-5f);
					table2.addCell(hcell1);

					PdfPCell hcell4;
					hcell4 = new PdfPCell(new Phrase("Const.No", redFont));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell4.setPaddingLeft(-25f);
					table2.addCell(hcell4);

					hcell4 = new PdfPCell(new Phrase(":", redFont));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell4.setPaddingLeft(-15f);
					table2.addCell(hcell4);

					hcell4 = new PdfPCell(new Phrase(regId, redFont));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell4.setPaddingLeft(-20f);
					table2.addCell(hcell4);

					hcell4 = new PdfPCell(new Phrase("Const.Dt", redFont));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell4.setPaddingRight(-27.5f);
					table2.addCell(hcell4);

					hcell4 = new PdfPCell(new Phrase(":", redFont));
					hcell4.setBorder(Rectangle.NO_BORDER);
					hcell4.setHorizontalAlignment(Element.ALIGN_RIGHT);
					table2.addCell(hcell4);

					hcell4 = new PdfPCell(new Phrase(today, redFont));
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
					hcell15.setPaddingLeft(-15f);
					table2.addCell(hcell15);

					hcell15 = new PdfPCell(new Phrase(refBy, redFont));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingLeft(-20f);
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

					hcell15 = new PdfPCell(new Phrase(String.valueOf(phone), redFont));
					hcell15.setBorder(Rectangle.NO_BORDER);
					hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell15.setPaddingRight(-27.5f);
					table2.addCell(hcell15);

					cell3.setFixedHeight(115f);
					cell3.setColspan(2);
					cell3.addElement(table2);
					table.addCell(cell3);

					PdfPCell cell19 = new PdfPCell();

					PdfPTable table21 = new PdfPTable(1);
					table21.setWidths(new float[] { 4f });
					table21.setSpacingBefore(10);

					PdfPCell hcell19;
					hcell19 = new PdfPCell(new Phrase("Consultation Receipt", headFont1));
					hcell19.setBorder(Rectangle.NO_BORDER);
					hcell19.setHorizontalAlignment(Element.ALIGN_CENTER);
					table21.addCell(hcell19);

					cell19.setFixedHeight(20f);
					cell19.setColspan(2);
					cell19.addElement(table21);
					table.addCell(cell19);

					PdfPCell cell4 = new PdfPCell();

					PdfPTable table3 = new PdfPTable(6);
					table3.setWidths(new float[] { 4f, 1f, 5f, 5f, 1f, 6f });
					table3.setSpacingBefore(10);

					PdfPCell hcell;
					hcell = new PdfPCell(new Phrase("Consultant", redFont));
					hcell.setBorder(Rectangle.NO_BORDER);
					hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell.setPaddingLeft(-50f);
					table3.addCell(hcell);

					hcell = new PdfPCell(new Phrase(":", redFont));
					hcell.setBorder(Rectangle.NO_BORDER);
					hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell.setPaddingLeft(-65f);
					table3.addCell(hcell);

					hcell = new PdfPCell(new Phrase(docNameSplit[0], redFont));
					hcell.setBorder(Rectangle.NO_BORDER);
					hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell.setPaddingLeft(-80f);
					table3.addCell(hcell);

					String dpt = null;
					if (department != null) {
						dpt = department;
					} else {
						dpt = "";
					}
					hcell = new PdfPCell(new Phrase("Dept.Name", redFont));
					hcell.setBorder(Rectangle.NO_BORDER);
					hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell.setPaddingRight(27.5f);
					table3.addCell(hcell);

					hcell = new PdfPCell(new Phrase(":", redFont));
					hcell.setBorder(Rectangle.NO_BORDER);
					hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell.setPaddingLeft(-27f);
					table3.addCell(hcell);

					hcell = new PdfPCell(new Phrase(dpt, redFont));
					hcell.setBorder(Rectangle.NO_BORDER);
					hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell.setPaddingLeft(-40f);
					table3.addCell(hcell);

					PdfPCell hcell11;
					hcell11 = new PdfPCell(new Phrase("Visit Type", redFont));
					hcell11.setBorder(Rectangle.NO_BORDER);
					hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell11.setPaddingLeft(-50f);
					table3.addCell(hcell11);

					hcell11 = new PdfPCell(new Phrase(":", redFont));
					hcell11.setBorder(Rectangle.NO_BORDER);
					hcell11.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell11.setPaddingLeft(-65f);
					table3.addCell(hcell11);

					hcell11 = new PdfPCell(new Phrase(patientType, redFont));
					hcell11.setBorder(Rectangle.NO_BORDER);
					hcell11.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell11.setPaddingLeft(-80f);
					table3.addCell(hcell11);

					hcell11 = new PdfPCell(new Phrase("Consultant Fee", redFont));
					hcell11.setBorder(Rectangle.NO_BORDER);
					hcell11.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell11.setPaddingRight(27.5f);
					table3.addCell(hcell11);

					hcell11 = new PdfPCell(new Phrase(":", redFont));
					hcell11.setBorder(Rectangle.NO_BORDER);
					hcell11.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell11.setPaddingLeft(-27f);
					table3.addCell(hcell11);

					hcell11 = new PdfPCell(new Phrase(String.valueOf(docAmount), redFont));
					hcell11.setBorder(Rectangle.NO_BORDER);
					hcell11.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell11.setPaddingLeft(-40f);
					table3.addCell(hcell11);

					PdfPCell hcell14;
					hcell14 = new PdfPCell(new Phrase("Payment Mode", redFont));
					hcell14.setBorder(Rectangle.NO_BORDER);
					hcell14.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell14.setPaddingLeft(-50f);
					table3.addCell(hcell14);

					hcell14 = new PdfPCell(new Phrase(":", redFont));
					hcell14.setBorder(Rectangle.NO_BORDER);
					hcell14.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell14.setPaddingLeft(-65f);
					table3.addCell(hcell14);

					hcell14 = new PdfPCell(new Phrase(mop, redFont));
					hcell14.setBorder(Rectangle.NO_BORDER);
					hcell14.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell14.setPaddingLeft(-80f);
					table3.addCell(hcell14);

					if (toc.equalsIgnoreCase("Nursing Charges")) {
						hcell14 = new PdfPCell(new Phrase(toc, redFont));
						hcell14.setBorder(Rectangle.NO_BORDER);
						hcell14.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell14.setPaddingRight(27.5f);
						table3.addCell(hcell14);

						hcell14 = new PdfPCell(new Phrase(":", redFont));
						hcell14.setBorder(Rectangle.NO_BORDER);
						hcell14.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell14.setPaddingLeft(-27f);
						table3.addCell(hcell14);

						hcell14 = new PdfPCell(new Phrase("", redFont));
						hcell14.setBorder(Rectangle.NO_BORDER);
						hcell14.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell14.setPaddingLeft(-40f);
						table3.addCell(hcell14);
					} else if (toc.equalsIgnoreCase("Service Charges")) {
						hcell14 = new PdfPCell(new Phrase(toc, redFont));
						hcell14.setBorder(Rectangle.NO_BORDER);
						hcell14.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell14.setPaddingRight(27.5f);
						table3.addCell(hcell14);

						hcell14 = new PdfPCell(new Phrase(":", redFont));
						hcell14.setBorder(Rectangle.NO_BORDER);
						hcell14.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell14.setPaddingLeft(-27f);
						table3.addCell(hcell14);

						hcell14 = new PdfPCell(new Phrase("", redFont));
						hcell14.setBorder(Rectangle.NO_BORDER);
						hcell14.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell14.setPaddingLeft(-40f);
						table3.addCell(hcell14);
					} else if (toc.equalsIgnoreCase("Reg Fees")) {
						hcell14 = new PdfPCell(new Phrase(toc, redFont));
						hcell14.setBorder(Rectangle.NO_BORDER);
						hcell14.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell14.setPaddingRight(27.5f);
						table3.addCell(hcell14);

						hcell14 = new PdfPCell(new Phrase(":", redFont));
						hcell14.setBorder(Rectangle.NO_BORDER);
						hcell14.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell14.setPaddingLeft(-27f);
						table3.addCell(hcell14);

						hcell14 = new PdfPCell(new Phrase(String.valueOf(regAmount), redFont));
						hcell14.setBorder(Rectangle.NO_BORDER);
						hcell14.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell14.setPaddingLeft(-40f);
						table3.addCell(hcell14);
					} else if (toc.equalsIgnoreCase("Vaccination fees")) {
						hcell14 = new PdfPCell(new Phrase(toc, redFont));
						hcell14.setBorder(Rectangle.NO_BORDER);
						hcell14.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell14.setPaddingRight(27.5f);
						table3.addCell(hcell14);

						hcell14 = new PdfPCell(new Phrase(":", redFont));
						hcell14.setBorder(Rectangle.NO_BORDER);
						hcell14.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell14.setPaddingLeft(-27f);
						table3.addCell(hcell14);

						hcell14 = new PdfPCell(new Phrase("", redFont));
						hcell14.setBorder(Rectangle.NO_BORDER);
						hcell14.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell14.setPaddingLeft(-40f);
						table3.addCell(hcell14);
					}
					PdfPCell hcell16;

					if (mop == null) {
						hcell16 = new PdfPCell(new Phrase("Receipt No", redFont));
						hcell16.setBorder(Rectangle.NO_BORDER);
						hcell16.setPaddingLeft(-50f);
						hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
						table3.addCell(hcell16);

						hcell16 = new PdfPCell(new Phrase(":", redFont));
						hcell16.setBorder(Rectangle.NO_BORDER);
						hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell16.setPaddingLeft(-65f);
						table3.addCell(hcell16);

						hcell16 = new PdfPCell(new Phrase(regId, redFont));
						hcell16.setBorder(Rectangle.NO_BORDER);
						hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell16.setPaddingLeft(-80f);
						table3.addCell(hcell16);

						hcell16 = new PdfPCell(new Phrase("", redFont));
						hcell16.setBorder(Rectangle.NO_BORDER);
						hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell16.setPaddingLeft(-20f);
						table3.addCell(hcell16);

						hcell16 = new PdfPCell(new Phrase("", redFont));
						hcell16.setBorder(Rectangle.NO_BORDER);
						hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell16.setPaddingLeft(-27f);
						table3.addCell(hcell16);

						hcell16 = new PdfPCell(new Phrase("", redFont));
						hcell16.setBorder(Rectangle.NO_BORDER);
						hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell16.setPaddingLeft(-40f);
						table3.addCell(hcell16);

					} else {
						hcell16 = new PdfPCell(new Phrase("Receipt No", redFont));
						hcell16.setBorder(Rectangle.NO_BORDER);
						hcell16.setPaddingLeft(-50f);
						hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
						table3.addCell(hcell16);

						hcell16 = new PdfPCell(new Phrase(":", redFont));
						hcell16.setBorder(Rectangle.NO_BORDER);
						hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell16.setPaddingLeft(-65f);
						table3.addCell(hcell16);

						hcell16 = new PdfPCell(new Phrase(regId, redFont));
						hcell16.setBorder(Rectangle.NO_BORDER);
						hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell16.setPaddingLeft(-80f);
						table3.addCell(hcell16);

						hcell16 = new PdfPCell(new Phrase("Rect.Amount", redFont));
						hcell16.setBorder(Rectangle.NO_BORDER);
						hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell16.setPaddingRight(27.5f);
						table3.addCell(hcell16);

						hcell16 = new PdfPCell(new Phrase(":", redFont));
						hcell16.setBorder(Rectangle.NO_BORDER);
						hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell16.setPaddingLeft(-27f);
						table3.addCell(hcell16);

						hcell16 = new PdfPCell(new Phrase(String.valueOf(rectAmount), redFont));
						hcell16.setBorder(Rectangle.NO_BORDER);
						hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
						hcell16.setPaddingLeft(-40f);
						table3.addCell(hcell16);

					}

					PdfPTable table91 = new PdfPTable(1);
					table91.setWidths(new float[] { 5f });
					table91.setSpacingBefore(10);

					cell4.setFixedHeight(130f);
					cell4.setColspan(2);
					cell4.addElement(table3);

					PdfPCell hcell98;
					hcell98 = new PdfPCell(new Phrase(
							"Validity             : " + "2 Visits Before " + regValidity + "\n\nReceived with thanks from "
									+ patientName + ", " + "A sum of Rs." + String.valueOf(rectAmount) + "\n\n"
									+ "In Words Rupees " + numberToWordsConverter.convert(rectAmount),
							redFont));
					hcell98.setBorder(Rectangle.NO_BORDER);
					// hcell3.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell98.setPaddingLeft(-50f);
					hcell98.setPaddingTop(5);
					table91.addCell(hcell98);
					cell4.addElement(table91);

					table.addCell(cell4);

					PdfPCell cell5 = new PdfPCell();

					PdfPTable table35 = new PdfPTable(2);
					table35.setWidths(new float[] { 5f, 4f });
					table35.setSpacingBefore(10);

					PdfPCell hcell21;
					Font redFont3 = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
					hcell21 = new PdfPCell(new Phrase("*" + umr + "*", redFont3));
					hcell21.setBorder(Rectangle.NO_BORDER);
					// hcell3.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell21.setPaddingLeft(-50f);
					table35.addCell(hcell21);

					hcell21 = new PdfPCell(new Phrase("*" + regId + "*", redFont3));
					hcell21.setBorder(Rectangle.NO_BORDER);
					hcell21.setHorizontalAlignment(Element.ALIGN_RIGHT);
					table35.addCell(hcell21);

					PdfPCell hcell12;
					hcell12 = new PdfPCell(new Phrase("Created By    : " + createdBy, redFont));
					hcell12.setBorder(Rectangle.NO_BORDER);
					// hcell3.setHorizontalAlignment(Element.ALIGN_LEFT);
					hcell12.setPaddingTop(5f);
					hcell12.setPaddingLeft(-50f);
					table35.addCell(hcell12);

					hcell12 = new PdfPCell(new Phrase("Created Dt   :   " + printedDate, redFont));
					hcell12.setBorder(Rectangle.NO_BORDER);
					hcell12.setPaddingTop(5f);
					// hcell12.setPaddingRight(0f);
					hcell12.setHorizontalAlignment(Element.ALIGN_RIGHT);
					table35.addCell(hcell12);

					PdfPCell hcell13;
					hcell13 = new PdfPCell(new Phrase("Printed By     : " + createdBy, redFont));
					hcell13.setBorder(Rectangle.NO_BORDER);
					hcell13.setPaddingLeft(-50f);
					// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					table35.addCell(hcell13);

					hcell13 = new PdfPCell(new Phrase("Print Dt       :   " + printedDate, redFont));
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
					hcell23.setPaddingTop(15f);
					hcell23.setHorizontalAlignment(Element.ALIGN_RIGHT);
					table35.addCell(hcell23);

					cell5.setFixedHeight(95f);
					cell5.setColspan(2);
					cell5.addElement(table35);
					table.addCell(cell5);

					document.add(table);

					document.close();

					pdfBytes = byteArrayOutputStream.toByteArray();
					String uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/payment/viewFile/")
							.path(paymentPdfServiceImpl.getNextPdfId()).toUriString();

					patientPaymentPdf = new PatientPaymentPdf(regId + " Consultation Reciept", uri,regId, pdfBytes,bill);
					patientPaymentPdf.setPid(paymentPdfServiceImpl.getNextPdfId());
					paymentPdfServiceImpl.save(patientPaymentPdf);

				} catch (Exception e) {
					Logger.error(e.getMessage());
				}

			}

			return patientPaymentPdf;

		}

	
	
	/*
	 * Inactive patients
	 */

	@RequestMapping(value = "/patient/inactive/{umr}", method = RequestMethod.POST)
	public void inactivePatients(@PathVariable String umr) {
		PatientDetails patientDetails = patientDetailsServiceImpl.getPatientByUmr(umr);
		List<PatientRegistration> patientRegistrations = patientRegistrationServiceImpl
				.findByPatientDetails(patientDetails);
		patientDetails.setDeleted("Yes");
		patientDetails.setDeletedAt(Timestamp.valueOf(LocalDateTime.now()));
		for (PatientRegistration patientRegistrationInfo : patientRegistrations) {
			patientRegistrationInfo.setDeletedAt(Timestamp.valueOf(LocalDateTime.now()));
		}
		patientDetailsServiceImpl.save(patientDetails);
	}

	// Update the existing patient

	@RequestMapping(value = "/patient/update/{umr}", method = RequestMethod.PUT)
	public PatientDetails updatePatient(@RequestBody PatientDetails patientDetails, @PathVariable String umr) {
		return patientDetailsServiceImpl.updatePatient(patientDetails, umr);

	}

	@RequestMapping(value = "/patient/listbyconsultant", method = RequestMethod.POST)
	public List<Map<String, String>> outPatientDetailsByConsultant(@RequestBody Map<String, String> map) {
		return patientDetailsServiceImpl.outPatientDetailsByConsultant(map);

	}

	/*
	 * To get list of patient in OUTPATIENT LIST
	 */
	@RequestMapping(value = "/patient/consultant")
	public List<User> getConsultants() {

		return userServiceImpl.findByUserRole("DOCTOR");
	}

	@RequestMapping(value = "/patient/pdf/{regId}", method = RequestMethod.GET)
	public Map<String, Object> pdfPatient(@PathVariable String regId) {
		Map<String, Object> display = new HashMap<>();
		List<PatientPaymentPdf> patientPaymentPdfs = paymentPdfRepository.getAllReport(regId);
		List<PrescriptionDetails> prescriptionDetails = prescriptionDetailsRepository.getAllReport(regId);
		List<NotesPdf> notesDetails = notesPdfRepository.getAllReport(regId);
		List<ServicePdf> servicePdfs = servicePdfRepository.findByRegId(regId);
		List<SalesPaymentPdf> salesPaymentPdf = salesPaymentPdfRepository.getAllReport(regId);
		// display.put("name",patientPaymentPdfs.get(0).getFileuri());
		int i = 0;
		for (PatientPaymentPdf patientPaymentPdfInfo : patientPaymentPdfs) {
			String[] data = patientPaymentPdfInfo.getFileName().split(ConstantValues.ONE_SPACE_STRING);
			String id = null;
			String name = "";
			boolean status = true;
			for (String dataInfo : data) {
				if (status) {
					id = dataInfo;
					status = false;
				} else {
					name += dataInfo + ConstantValues.ONE_SPACE_STRING;
				}

			}

			if (display.containsKey(name)) {
				name += i;
				i++;
			}

			display.put(name, patientPaymentPdfInfo.getFileuri());
		}
		for (PrescriptionDetails prescriptionDetailsInfo : prescriptionDetails) {
			String[] data = prescriptionDetailsInfo.getFileNamee().split(ConstantValues.ONE_SPACE_STRING);
			String id = null;
			String name = "";
			boolean status = true;
			for (String dataInfo : data) {
				if (status) {
					id = dataInfo;
					status = false;
				} else {
					name += dataInfo + ConstantValues.ONE_SPACE_STRING;
				}

			}

			if (display.containsKey(name)) {
				name += i;
				i++;
			}

			display.put(name, prescriptionDetailsInfo.getFileDownloadUri());
		}
		for (SalesPaymentPdf salesPaymentPdfInfo : salesPaymentPdf) {
			String[] data = salesPaymentPdfInfo.getFileName().split(ConstantValues.ONE_SPACE_STRING);
			String id = null;
			String name = "";
			boolean status = true;
			for (String dataInfo : data) {
				if (status) {
					id = dataInfo;
					status = false;
				} else {
					name += dataInfo + ConstantValues.ONE_SPACE_STRING;
				}

			}

			if (display.containsKey(name)) {
				name += i;
				i++;
			}

			display.put(name, salesPaymentPdfInfo.getFileuri());
		}
		for (NotesPdf notesDetailsInfo : notesDetails) {
			display.put("Notes",notesDetailsInfo.getFileuri());
		}
		for (ServicePdf servicePdfsInfo : servicePdfs) {
			display.put(servicePdfsInfo.getFileName(), servicePdfsInfo.getFileuri());
		}

		// display.put("Prescription","http://localhost:8084/v1/doctor/prescription/"+regId);

		return display;
	}

	/*
	 * to get patient payment pdf
	 */
	@RequestMapping(value = "/payment/viewFile/{id}", method = RequestMethod.GET)
	public ResponseEntity<Resource> uriLink(@PathVariable String id) {

		PatientPaymentPdf patientPaymentPdf = paymentPdfServiceImpl.findById(id);

		return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/pdf"))
				.header(HttpHeaders.CONTENT_DISPOSITION,
						String.format("inline; filename=\"" + patientPaymentPdf.getFileName() + "\""))
				.body(new ByteArrayResource(patientPaymentPdf.getData()));

	}

	@RequestMapping(value = "/patient/xlPdf", method = RequestMethod.POST)
	public ResponseEntity<?> getXlPdf(@RequestBody Map<String, Object> info, Principal principal) {
		Map<String, Object> sendInfo = new HashMap<>();
		String format = info.get("reportName").toString();
		try {
			if (format.equalsIgnoreCase("Excel")) {

				sendInfo.put("fromDate", info.get("fromDate"));
				sendInfo.put("toDate", info.get("toDate"));
				return excelReport(sendInfo, principal);
			} else {

				sendInfo.put("fromDate", info.get("fromDate"));
				sendInfo.put("toDate", info.get("toDate"));
				return pdfReport(sendInfo, principal);
			}
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
		return null;

	}

	/*
	 * Download Excel
	 */
	@PostMapping(value = "/download/customers.xlsx")
	public ResponseEntity<?> excelReport(@RequestBody Map<String, Object> info, Principal p) throws IOException {
		Map<String, Object> info1 = info;

		List<PatientRegistration> customers = patientRegistrationRepository.findByDate(info1.get("fromDate"),
				info1.get("toDate"));
		for (PatientRegistration pr : customers) {
			System.out.println(
					"-----------------------------------------------------------------------" + pr.getRegDate());
		}

		String path = context.getRealPath("/");

		System.out.println("The path name is----" + path);

		ByteArrayInputStream in = ExcelView.customersToExcel(customers, path);
		// return IOUtils.toByteArray(in);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "attachment; filename=customers.xlsx");

		return ResponseEntity.ok(null);
	}

	// -------------------------for pdf-----------------------------
	@RequestMapping(value = "/pdfreport", method = RequestMethod.POST, produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<?> pdfReport(@RequestBody Map<String, Object> info, Principal p) throws IOException {
		Map<String, Object> info1 = info;

		// List<City> cities = (List<City>) cityService.findAll();
		List<PatientRegistration> cities = patientRegistrationRepository.findByDate(info1.get("fromDate"),
				info1.get("toDate"));

		String path = context.getRealPath("/");

		ByteArrayInputStream bis = GeneratePdfReport.citiesReport(cities, path);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "attachment; filename=citiesreport.pdf");

		return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF)
				.body(new InputStreamResource(bis));
	}

	/*
	 * Patient registration Page load info
	 */
	@RequestMapping(value = "/registration/patient", method = RequestMethod.GET)
	public RegGenerator newRegistrationId() {
		regGenerator = new RegGenerator(patientRegistrationServiceImpl.getNextRegId());
		return regGenerator;
	}

	/*
	 * Existing patient registration
	 */

	@RequestMapping(value = "/registration/patient/{umr}", method = RequestMethod.POST)
	public PatientPaymentPdf newRegistration(@RequestBody PatientRegistration patientRegistration,
			@PathVariable String umr, Principal p) {
		return patientDetailsServiceImpl.newRegistration(patientRegistration, umr, p);
	}

	// for getting latest registration for patient
	@RequestMapping(value = "/patient/latest/{uid}", method = RequestMethod.GET)
	public PatientRegistration latestRegistration(@PathVariable Long uid) {
		return patientRegistrationServiceImpl.findLatestReg(uid);
	}

	/*
	 * List of INPATIENT (ONLY FOR 2 DAYS, 7 DAYS, 15 DAYS, 30 DAYS)
	 */
	@RequestMapping(value = "/patient/patientDetails/{type}", method = RequestMethod.GET)
	public List<Map<String, String>> patientDetails(@PathVariable String type) {
		return patientDetailsServiceImpl.patientDetails(type);

	}

	/*
	 * List of OUTPATIENT (ONLY FOR 2 DAYS, 7 DAYS, 15 DAYS, 30 DAYS)
	 */
	@RequestMapping(value = "/patient/outPatient/{type}", method = RequestMethod.GET)
	public List<Map<String, String>> outpatientDetails(@PathVariable String type) {
		return patientDetailsServiceImpl.outPatientDetails(type);

	}

	// get separate list of discharge and non discharge patients
	@RequestMapping(value = "/patient/discharge/{dischargeType}", method = RequestMethod.GET)
	public List<Object> getDischargedAndNonDischargedPatients(@PathVariable("dischargeType") String dischargeType) {

		return patientDetailsServiceImpl.getDischargeAndNonDischargePatients(dischargeType);

	}

	/*
	 * to get patients other than OPD
	 */
	@RequestMapping(value = "/patient/inPatient", method = RequestMethod.GET)
	public List<Map<String, String>> inpatientDetails() {
		return patientDetailsServiceImpl.inPatientDetails();

	}

	/*
	 * doctor fee `````````` To send doctor fee
	 */
	@RequestMapping(value = "/patient/fee/docFee", method = RequestMethod.POST)
	public Map<String, String> docFee(@RequestBody Map<String, String> feeInfo) {
		int fee = 0;
		DoctorDetails doctorDetails = doctorDetailsServiceImpl.findByDrRegistrationo(feeInfo.get("userId"));
		
		
		
		if (feeInfo.get("patType").equalsIgnoreCase(ConstantValues.INPATIENT)||feeInfo.get("patType").equalsIgnoreCase(ConstantValues.EMERGENCY)||feeInfo.get("patType").equalsIgnoreCase(ConstantValues.DAYCARE)) {
			if(feeInfo.get("ward")==null) {
				
				throw new RuntimeException("PLEASE SELECT THE ROOM");
			}
			
			if (feeInfo.get("ward").equalsIgnoreCase("double sharing")) {
				fee = doctorDetails.getIpDs();
			} else if (feeInfo.get("ward").equalsIgnoreCase("General ward-male")) {
				fee = doctorDetails.getIpGenMale();
			} else if (feeInfo.get("ward").equalsIgnoreCase("General ward-female")) {
				fee = doctorDetails.getIpGenFemale();
			} else if (feeInfo.get("ward").equalsIgnoreCase("emergency")) {
				fee = doctorDetails.getIpEmergency();
			} else if (feeInfo.get("ward").equalsIgnoreCase("DayCare")) {
				fee = doctorDetails.getIpDayCare();
			} else if (feeInfo.get("ward").equalsIgnoreCase("Single Sharing")) {
				fee = doctorDetails.getIpSs();
			} else if (feeInfo.get("ward").equalsIgnoreCase("NICU")) {
				fee = doctorDetails.getIpNicu();
			} else if (feeInfo.get("ward").equalsIgnoreCase("Adult icu")) {
				fee = doctorDetails.getIpAicu();
			} else if (feeInfo.get("ward").equalsIgnoreCase("picu")) {
				fee = doctorDetails.getIpPicu();
			} else if (feeInfo.get("ward").equalsIgnoreCase("isolation")) {
				fee = doctorDetails.getIpIsolation();
			}

		} else {
			fee = doctorDetails.getOpFee();
		}

		Map<String, String> displayInfo = new HashMap<>();
		displayInfo.put("Fee", String.valueOf(fee));
		return displayInfo;
	}

	/*
	 * To pay advance amount for IP
	 */

	@RequestMapping(value = "/patient/advanceAmount/{regID}", method = RequestMethod.POST)
	public PatientPaymentPdf advanceAmount(@RequestBody PatientPaymentDTO patientPaymentDTO, @PathVariable String regID,
			Principal principal) {
		PatientPayment patientPayment=new PatientPayment();
		BeanUtils.copyProperties(patientPaymentDTO, patientPayment);
		
		return patientDetailsServiceImpl.advanceAmount(patientPayment, regID, principal);

	}

	/*
	 * Printing blank prescription
	 */
	@RequestMapping(value = "/patient/blank/{regId}", method = RequestMethod.POST)
	public PatientPaymentPdf blankPrescription(@PathVariable String regId) {
		return patientDetailsServiceImpl.blankPrescription(regId);
	}

	/*
	 * Get one patient for UI info(sales,lab)
	 */
	@RequestMapping(value = "/patient/{patId}", method = RequestMethod.GET)
	public Map<String, String> getOnePatient(@PathVariable String patId) {
		Map<String, String> info = new HashMap<>();
		PatientRegistration patientRegistration = patientRegistrationServiceImpl.findByRegId(patId);
		if(patientRegistration.isBlockedStatus())
		{
			throw new RuntimeException("Payment for this patinet is blocked !");
		}
		info.put("name", patientRegistration.getPatientDetails().getFirstName() + ConstantValues.ONE_SPACE_STRING
				+ patientRegistration.getPatientDetails().getLastName());
		info.put("mobile", String.valueOf(patientRegistration.getPatientDetails().getMobile()));
		info.put("type", patientRegistration.getpType());
		return info;
	}

	// for INPATIENT SALES for admin
	@RequestMapping(value = "/patient/adminSale/{regId}", method = RequestMethod.GET)
	public PatientPaymentPdf createAdmnWiseSale(@PathVariable String regId, Principal principal) {

		return patientDetailsServiceImpl.admnWiseSales(regId, principal);
	}

	// Get lab service registerd for patients
	@RequestMapping(value = "/patient/patientlab/{regId}")
	public PatientPaymentPdf getInpatientLabServices(@PathVariable("regId") String regId, Principal principal) {

		// User Security
		User userSecurity = userServiceImpl.findByUserName(principal.getName());
		String creatdBy = userSecurity.getFirstName() + ConstantValues.ONE_SPACE_STRING + userSecurity.getMiddleName()
				+ ConstantValues.ONE_SPACE_STRING + userSecurity.getLastName();

		PatientRegistration patientRegistration = patientRegistrationServiceImpl.findByRegId(regId);

		List<LaboratoryRegistration> laboratoryRegistration = patientRegistration.getLaboratoryRegistration();

		String admittedWard = null;
		float totalAmount = 0;

		List<RoomBookingDetails> roomBookingDetails = patientRegistration.getRoomBookingDetails();

		for (RoomBookingDetails roomBookingDetailsInfo : roomBookingDetails) {
			RoomDetails roomDetails = roomBookingDetailsInfo.getRoomDetails();
			admittedWard = roomDetails.getRoomType();
		}

		String newAddress =ConstantValues. PATIENT_SERVICE_SLIP;
		PatientPaymentPdf patientPaymentPdf = null;

		byte[] pdfBytes = null;
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		final Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.NORMAL, BaseColor.RED);
		final Font blueFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
		final Font font = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
		final Font font1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

		final Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
		final Font headFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

		Document document = new Document(PageSize.A4_LANDSCAPE);

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

			Paragraph p51 = new Paragraph(ConstantValues.HOSPITAL_NAME, headFont);
			p51.setAlignment(Element.ALIGN_CENTER);

			document.add(p51);
			Paragraph p52 = new Paragraph(newAddress, headFont1);
			p52.setAlignment(Element.ALIGN_CENTER);
			document.add(p52);

			// Display a date in day, month, year format
			Date date1 = Calendar.getInstance().getTime();
			DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
			String today = formatter.format(date1).toString();

			Font redFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

			Paragraph p9 = new Paragraph(" \n  Service Slip", blueFont);
			p9.setAlignment(Element.ALIGN_CENTER);
			document.add(p9);

			PdfPCell cell1 = new PdfPCell();
			PdfPTable table2 = new PdfPTable(6);
			table2.setWidths(new float[] { 5f, 1f, 5f, 5f, 1f, 5f });

			PdfPCell hcell4;
			hcell4 = new PdfPCell(new Phrase(" \nAdmission No", redFont1));
			hcell4.setBorder(Rectangle.NO_BORDER);
			hcell4.setPaddingLeft(-25f);
			// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			table2.addCell(hcell4);

			hcell4 = new PdfPCell(new Phrase(" \n:", redFont1));
			hcell4.setBorder(Rectangle.NO_BORDER);
			hcell4.setPaddingLeft(-25f);
			// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			table2.addCell(hcell4);

			hcell4 = new PdfPCell(new Phrase("\n" + patientRegistration.getRegId(), redFont1));
			hcell4.setBorder(Rectangle.NO_BORDER);
			hcell4.setPaddingLeft(-25f);
			// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			table2.addCell(hcell4);

			hcell4 = new PdfPCell(new Phrase(" \nUMR No", redFont1));
			hcell4.setBorder(Rectangle.NO_BORDER);
			// hcell4.setPaddingRight(5f);
			hcell4.setPaddingLeft(-20f);

			hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
			table2.addCell(hcell4);

			hcell4 = new PdfPCell(new Phrase(" \n:", redFont1));
			hcell4.setBorder(Rectangle.NO_BORDER);
			// hcell4.setPaddingRight(5f);
			hcell4.setPaddingLeft(-40f);
			hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
			table2.addCell(hcell4);

			hcell4 = new PdfPCell(new Phrase("\n" + patientRegistration.getPatientDetails().getUmr(), redFont1));
			hcell4.setBorder(Rectangle.NO_BORDER);
			// hcell4.setPaddingRight(5f);
			hcell4.setPaddingLeft(-40f);
			hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
			table2.addCell(hcell4);

			PdfPCell hcell15;
			hcell15 = new PdfPCell(new Phrase("P. Name", redFont1));
			hcell15.setBorder(Rectangle.NO_BORDER);
			hcell15.setPaddingLeft(-25f);
			// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			table2.addCell(hcell15);

			hcell15 = new PdfPCell(new Phrase(":", redFont1));
			hcell15.setBorder(Rectangle.NO_BORDER);
			hcell15.setPaddingLeft(-25f);
			// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			table2.addCell(hcell15);

			hcell15 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getTitle() + ". "
					+ patientRegistration.getPatientDetails().getFirstName() + ConstantValues.ONE_SPACE_STRING
					+ patientRegistration.getPatientDetails().getLastName(), redFont1));
			hcell15.setBorder(Rectangle.NO_BORDER);
			hcell15.setPaddingLeft(-25f);
			// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			table2.addCell(hcell15);

			hcell15 = new PdfPCell(new Phrase("Age/Sex", redFont1));
			hcell15.setBorder(Rectangle.NO_BORDER);
			// hcell15.setPaddingRight(5f);
			hcell15.setPaddingLeft(-20f);
			hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
			table2.addCell(hcell15);

			hcell15 = new PdfPCell(new Phrase(":", redFont1));
			hcell15.setBorder(Rectangle.NO_BORDER);
			// hcell15.setPaddingRight(5f);
			hcell15.setPaddingLeft(-40f);
			hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
			table2.addCell(hcell15);

			hcell15 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getAge() + "/"
					+ patientRegistration.getPatientDetails().getGender(), redFont1));
			hcell15.setBorder(Rectangle.NO_BORDER);
			// hcell15.setPaddingRight(5f);
			hcell15.setPaddingLeft(-40f);
			hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
			table2.addCell(hcell15);

			String aw = null;
			if (admittedWard == null) {
				aw = ConstantValues.ONE_SPACE_STRING;
			} else {
				aw = admittedWard;
			}

			PdfPCell hcell16;
			hcell16 = new PdfPCell(new Phrase("Ward", redFont1));

			hcell16.setBorder(Rectangle.NO_BORDER);
			hcell16.setPaddingLeft(-25f);
			// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);

			table2.addCell(hcell16);

			hcell16 = new PdfPCell(new Phrase(":", redFont1));

			hcell16.setBorder(Rectangle.NO_BORDER);
			hcell16.setPaddingLeft(-25f);
			// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);

			table2.addCell(hcell16);

			hcell16 = new PdfPCell(new Phrase(aw, redFont1));

			hcell16.setBorder(Rectangle.NO_BORDER);
			hcell16.setPaddingLeft(-25f);
			// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);

			table2.addCell(hcell16);

			String refBy = null;

			if (patientRegistration.getPatientDetails().getvRefferalDetails() == null) {
				refBy = ConstantValues.ONE_SPACE_STRING;
			} else {
				refBy = patientRegistration.getPatientDetails().getvRefferalDetails().getRefName();
			}

			hcell16 = new PdfPCell(new Phrase("Ref. By", redFont1));
			hcell16.setBorder(Rectangle.NO_BORDER);
			hcell16.setPaddingLeft(-20f);
			hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
			table2.addCell(hcell16);

			hcell16 = new PdfPCell(new Phrase(":", redFont1));
			hcell16.setBorder(Rectangle.NO_BORDER);
			hcell16.setPaddingLeft(-40f);
			hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
			table2.addCell(hcell16);

			hcell16 = new PdfPCell(new Phrase(refBy, redFont1));
			hcell16.setBorder(Rectangle.NO_BORDER);
			hcell16.setPaddingLeft(-40f);
			hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
			table2.addCell(hcell16);

			PdfPCell hcell17;
			hcell17 = new PdfPCell(new Phrase("History", redFont1));
			hcell17.setBorder(Rectangle.NO_BORDER);
			hcell17.setPaddingLeft(-25f);
			// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			table2.addCell(hcell17);

			hcell17 = new PdfPCell(new Phrase(":", redFont1));
			hcell17.setBorder(Rectangle.NO_BORDER);
			hcell17.setPaddingLeft(-25f);
			// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			table2.addCell(hcell17);

			hcell17 = new PdfPCell(new Phrase("  ", redFont1));
			hcell17.setBorder(Rectangle.NO_BORDER);
			hcell17.setPaddingLeft(-25f);
			// hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			table2.addCell(hcell17);

			hcell17 = new PdfPCell(new Phrase("Lab No", redFont1));
			hcell17.setBorder(Rectangle.NO_BORDER);
			hcell17.setPaddingLeft(-20f);
			hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
			table2.addCell(hcell17);

			hcell17 = new PdfPCell(new Phrase(":", redFont1));
			hcell17.setBorder(Rectangle.NO_BORDER);
			hcell17.setPaddingLeft(-40f);
			hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
			table2.addCell(hcell17);

			hcell17 = new PdfPCell(new Phrase(ConstantValues.ONE_SPACE_STRING, redFont1));
			hcell17.setBorder(Rectangle.NO_BORDER);
			hcell17.setPaddingLeft(-40f);
			hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
			table2.addCell(hcell17);

			PdfPCell hcell171;
			hcell171 = new PdfPCell(new Phrase("Indent No", redFont1));
			hcell171.setBorder(Rectangle.NO_BORDER);
			hcell171.setPaddingLeft(-25f);
			hcell171.setPaddingBottom(6f);
			// hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
			table2.addCell(hcell171);
			// table2.addCell(cell1);

			hcell171 = new PdfPCell(new Phrase(":", redFont1));
			hcell171.setBorder(Rectangle.NO_BORDER);
			hcell171.setPaddingLeft(-25f);
			hcell171.setPaddingBottom(5f);
			// hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
			table2.addCell(hcell171);

			hcell171 = new PdfPCell(new Phrase(ConstantValues.ONE_SPACE_STRING, redFont1));
			hcell171.setBorder(Rectangle.NO_BORDER);
			hcell171.setPaddingLeft(-25f);
			hcell171.setPaddingBottom(5f);
			// hcell17.setHorizontalAlignment(Element.ALIGN_LEFT);
			table2.addCell(hcell171);

			hcell171 = new PdfPCell(new Phrase(ConstantValues.ONE_SPACE_STRING, redFont1));
			hcell171.setBorder(Rectangle.NO_BORDER);
			hcell171.setPaddingLeft(-20f);
			hcell171.setPaddingBottom(5f);
			hcell171.setHorizontalAlignment(Element.ALIGN_LEFT);
			table2.addCell(hcell171);

			hcell171 = new PdfPCell(new Phrase(ConstantValues.ONE_SPACE_STRING, redFont1));
			hcell171.setBorder(Rectangle.NO_BORDER);
			hcell171.setPaddingLeft(-40f);
			hcell171.setPaddingBottom(3f);
			hcell171.setHorizontalAlignment(Element.ALIGN_LEFT);
			table2.addCell(hcell171);

			hcell171 = new PdfPCell(new Phrase(ConstantValues.ONE_SPACE_STRING, redFont1));
			hcell171.setBorder(Rectangle.NO_BORDER);
			hcell171.setPaddingLeft(-40f);
			hcell171.setPaddingBottom(3f);
			hcell171.setHorizontalAlignment(Element.ALIGN_LEFT);
			table2.addCell(hcell171);

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
			PdfPTable table3 = new PdfPTable(6);
			table3.setWidths(new float[] { 1.5f, 3f, 3f, 6f, 4f, 4f });
			// table2.setSpacingBefore(10);
			// cell3.setBorder(Rectangle.BOX);
			PdfPCell hcell2;
			hcell2 = new PdfPCell(new Phrase("S.No ", font));
			hcell2.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
			hcell2.setBackgroundColor(BaseColor.LIGHT_GRAY);
			hcell2.setPaddingBottom(5f);
			hcell2.setPaddingTop(3f);
			// hcell2.setPaddingLeft(-25f);
			hcell2.setHorizontalAlignment(Element.ALIGN_CENTER);

			table3.addCell(hcell2);

			hcell2 = new PdfPCell(new Phrase("Ser.Code", font));
			hcell2.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
			hcell2.setBackgroundColor(BaseColor.LIGHT_GRAY);
			hcell2.setPaddingBottom(5f);
			hcell2.setPaddingTop(3f);
			// hcell1.setPaddingRight(-40f);
			// hcell3.setPaddingLeft(90f);
			hcell2.setHorizontalAlignment(Element.ALIGN_CENTER);
			table3.addCell(hcell2);

			hcell2 = new PdfPCell(new Phrase("Dept. Name", font));
			hcell2.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
			hcell2.setBackgroundColor(BaseColor.LIGHT_GRAY);
			hcell2.setPaddingBottom(5f);
			hcell2.setPaddingTop(3f);
			// hcell5.setPaddingLeft(-25f);
			hcell2.setHorizontalAlignment(Element.ALIGN_CENTER);
			table3.addCell(hcell2);

			hcell2 = new PdfPCell(new Phrase("Ser.Name", font));
			hcell2.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
			hcell2.setBackgroundColor(BaseColor.LIGHT_GRAY);
			hcell2.setPaddingBottom(5f);
			// hcell2.setPaddingTop(3f);
			// hcell4.setPaddingRight(5f);
			// hcell6.setPaddingLeft(90f);
			hcell2.setHorizontalAlignment(Element.ALIGN_CENTER);
			table3.addCell(hcell2);

			hcell2 = new PdfPCell(new Phrase("Ser.Cost", font));
			hcell2.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
			hcell2.setBackgroundColor(BaseColor.LIGHT_GRAY);
			hcell2.setPaddingBottom(5f);
			hcell2.setPaddingTop(3f);
			// hcell4.setPaddingRight(5f);
			// hcell6.setPaddingLeft(90f);
			hcell2.setHorizontalAlignment(Element.ALIGN_CENTER);
			// hcell2.setPaddingLeft(30f);
			table3.addCell(hcell2);

			hcell2 = new PdfPCell(new Phrase("Ser.Date", font));
			hcell2.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
			hcell2.setBackgroundColor(BaseColor.LIGHT_GRAY);
			hcell2.setPaddingBottom(5f);
			hcell2.setPaddingTop(3f);
			hcell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
			hcell2.setPaddingRight(20f);
			table3.addCell(hcell2);

			table3.setWidthPercentage(100f);
			document.add(table3);

			cell3.setFixedHeight(107f);
			cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
			// cell1.setBorder(Rectangle.LEFT);
			// cell1.setBorder(Rectangle.RIGHT);
			cell3.setColspan(2);
			document.add(cell3);

			/*
			 * PdfPTable table16 = new PdfPTable(1); table16.setWidths(new int[] { 15 });
			 * 
			 * PdfPCell hcell64 = new PdfPCell(new Phrase(
			 * "__________________________________________________________________________________________________________________ "
			 * , font)); hcell64.setBorder(Rectangle.NO_BORDER);
			 * table16.setWidthPercentage(120f); // hcell64.setPaddingBottom(10); // hcell64
			 * .setFixedHeight(15); hcell64.setHorizontalAlignment(Element.ALIGN_CENTER);
			 * hcell64.setPaddingRight(10f); table16.addCell(hcell64);
			 * document.add(table16);
			 */

			int count = 0;
			String bill=null;
			for (LaboratoryRegistration lab : laboratoryRegistration) {
				bill = lab.getBillNo();
				Timestamp timestamp1 = lab.getEnteredDate();
				DateFormat dateFormat1 = new SimpleDateFormat("dd-MMM-yyyy  ");

				Calendar calendar1 = Calendar.getInstance();
				calendar1.setTimeInMillis(timestamp1.getTime());

				String to = dateFormat1.format(calendar1.getTime());

				PdfPCell cell4 = new PdfPCell();
				PdfPTable table5 = new PdfPTable(6);
				table5.setWidths(new float[] { 1.5f, 3f, 3f, 6f, 4f, 4f });
				// table2.setSpacingBefore(10);

				PdfPCell hcell5;
				hcell5 = new PdfPCell(new Phrase(String.valueOf(count = count + 1), font1));

				hcell5.setBorder(Rectangle.NO_BORDER);
				// hcell5.setPaddingLeft(-25f);
				hcell5.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell5.setPaddingLeft(-40f);
				table5.addCell(hcell5);

				hcell5 = new PdfPCell(new Phrase(lab.getLabServices().getServiceId(), font1));
				hcell5.setBorder(Rectangle.NO_BORDER);
				// hcell1.setPaddingRight(-40f);
				hcell5.setPaddingLeft(-60f);
				hcell5.setHorizontalAlignment(Element.ALIGN_CENTER);
				table5.addCell(hcell5);

				hcell5 = new PdfPCell(new Phrase(lab.getLabServices().getDepartment(), font1));
				hcell5.setBorder(Rectangle.NO_BORDER);
				// hcell5.setPaddingLeft(-25f);
				hcell5.setPaddingLeft(-20f);

				hcell5.setHorizontalAlignment(Element.ALIGN_CENTER);
				table5.addCell(hcell5);

				hcell5 = new PdfPCell(new Phrase(lab.getServiceName(), font1));
				hcell5.setBorder(Rectangle.NO_BORDER);
				// hcell4.setPaddingRight(5f);
				// hcell5.setPaddingLeft(10f);
				hcell5.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell5.setPaddingLeft(30f);
				table5.addCell(hcell5);

				hcell5 = new PdfPCell(new Phrase(String.valueOf(lab.getPrice()), font1));
				hcell5.setBorder(Rectangle.NO_BORDER);
				// hcell4.setPaddingRight(5f);
				hcell5.setPaddingLeft(30f);
				hcell5.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table5.addCell(hcell5);

				hcell5 = new PdfPCell(new Phrase(to, font1));
				hcell5.setBorder(Rectangle.NO_BORDER);
				// hcell5.setPaddingRight(-20f);
				// hcell5.setPaddingLeft(30f);
				hcell5.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell5.setPaddingRight(-40f);
				table5.addCell(hcell5);
				document.add(table5);

				cell4.setFixedHeight(107f);
				cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
				// cell4.setBorder(Rectangle.LEFT);
				// cell1.setBorder(Rectangle.RIGHT);
				cell4.setColspan(2);
				document.add(cell4);

				totalAmount += lab.getPrice();
			}

			PdfPCell cell31 = new PdfPCell();
			PdfPTable table31 = new PdfPTable(6);
			table31.setWidths(new float[] { 4f, 1.5f, 2f, 5f, 4f, 4f });
			// table2.setSpacingBefore(10);
			// cell3.setBorder(Rectangle.BOX);
			PdfPCell hcell21;
			hcell21 = new PdfPCell(new Phrase("TOTAL", font));
			hcell21.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
			hcell21.setPaddingBottom(5f);
			hcell21.setPaddingTop(3f);
			// hcell2.setPaddingLeft(-25f);
			hcell21.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell21.setPaddingLeft(60f);
			table31.addCell(hcell21);

			hcell21 = new PdfPCell(new Phrase("", font));
			hcell21.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
			hcell21.setPaddingBottom(5f);
			hcell21.setPaddingTop(3f);
			// hcell1.setPaddingRight(-40f);
			// hcell3.setPaddingLeft(90f);
			hcell21.setHorizontalAlignment(Element.ALIGN_CENTER);
			table31.addCell(hcell21);

			hcell21 = new PdfPCell(new Phrase("", font));
			hcell21.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
			hcell21.setPaddingBottom(5f);
			hcell21.setPaddingTop(3f);
			// hcell5.setPaddingLeft(-25f);
			hcell21.setHorizontalAlignment(Element.ALIGN_CENTER);
			table31.addCell(hcell21);

			hcell21 = new PdfPCell(new Phrase("", font));
			hcell21.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
			hcell21.setPaddingBottom(5f);
			// hcell2.setPaddingTop(3f);
			// hcell4.setPaddingRight(5f);
			// hcell6.setPaddingLeft(90f);
			hcell21.setHorizontalAlignment(Element.ALIGN_CENTER);
			table31.addCell(hcell21);

			hcell21 = new PdfPCell(new Phrase(String.valueOf(totalAmount), font));
			hcell21.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
			hcell21.setPaddingBottom(5f);
			hcell21.setPaddingTop(3f);
			hcell21.setHorizontalAlignment(Element.ALIGN_RIGHT);
			hcell21.setPaddingRight(30f);
			table31.addCell(hcell21);

			hcell21 = new PdfPCell(new Phrase("", font));
			hcell21.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
			hcell21.setPaddingBottom(5f);
			hcell21.setPaddingTop(3f);
			// hcell5.setPaddingLeft(-25f);
			hcell21.setHorizontalAlignment(Element.ALIGN_CENTER);
			table31.addCell(hcell21);

			table31.setWidthPercentage(100f);
			document.add(table31);

			cell31.setFixedHeight(107f);
			cell31.setHorizontalAlignment(Element.ALIGN_CENTER);
			// cell1.setBorder(Rectangle.LEFT);
			// cell1.setBorder(Rectangle.RIGHT);
			cell31.setColspan(2);
			document.add(cell31);

			Paragraph p17 = new Paragraph("\n \n \n \n \n \n \n \n \n \n \n", font1);
			p17.setAlignment(Element.ALIGN_CENTER);
			document.add(p17);

			Chunk cnd = new Chunk(new VerticalPositionMark());

			Paragraph p18 = new Paragraph("Created By : " + creatdBy, font);
			p18.add(cnd);
			p18.add("Create Date : " + today);

			document.add(p18);

			Paragraph p19 = new Paragraph("Printed By : " + creatdBy, font);
			p19.add(cnd);
			p19.add("Printed Date : " + today);

			document.add(p19);

			document.close();

			System.out.println("finished");

			pdfBytes = byteArrayOutputStream.toByteArray();
			String uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/payment/viewFile/")
					.path(paymentPdfServiceImpl.getNextPdfId()).toUriString();

			patientPaymentPdf = new PatientPaymentPdf("payment", uri,regId, pdfBytes,bill);
			patientPaymentPdf.setPid(paymentPdfServiceImpl.getNextPdfId());
			paymentPdfServiceImpl.save(patientPaymentPdf);

		} catch (Exception e) {
			Logger.error(e.getMessage());
		}

		return patientPaymentPdf;

	}

	/*
	 * Due Bill pdf for outpatient
	 */
	@RequestMapping(value = "/patient/due/{regId}", method = RequestMethod.GET)
	public PatientPaymentPdf duePdf(@PathVariable String regId, Principal principal) {

		User userSecurity = userServiceImpl.findByUserName(principal.getName());
		String createdBy = userSecurity.getFirstName() + ConstantValues.ONE_SPACE_STRING + userSecurity.getLastName();

		String today = null;

		PatientRegistration patientRegistration = patientRegistrationServiceImpl.findByRegId(regId);
		List allDue = new ArrayList<>();

		List<PatientPayment> paymentDue = patientPaymentServiceImpl.findByModeOfPaymantAndPatientRegistration("due",
				patientRegistration);
		for (PatientPayment paymentDueInfo : paymentDue) {
			allDue.add(paymentDueInfo);
		}

		List<Sales> salesDue = salesServiceImpl.findByPaymentTypeAndPatientRegistration("Due", patientRegistration);
		for (Sales salesDueInfo : salesDue) {
			allDue.add(salesDueInfo);
		}

		List<LaboratoryRegistration> laboratoryDue = laboratoryRegistrationServiceImpl
				.findByPaymentTypeAndLaboratoryPatientRegistration("Due", patientRegistration);
		for (LaboratoryRegistration laboratoryDueInfo : laboratoryDue) {
			allDue.add(laboratoryDueInfo);
		}

		allDue.add(paymentDue);
		allDue.add(salesDue);
		allDue.add(laboratoryDue);
		
		String adress=ConstantValues.DUEBILL_FOR_OUTPATIENT;
		/*String adress="     Plot No. 14,15,16 & 17,Nandi Co-op.Society,"
				+ "\n                               Main Road ,Beside Navya Grand Hotel,\n                                           Miyapur,Hyderabad-49   \n                               "
				+ "             Phone:040-23046789    "
				+ "\n                                Email :udbhavahospitals@gmail.com";

*/		PatientPaymentPdf patientPaymentPdf = null;
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

			pq.add(new Chunk(adress, redFont));
			PdfPCell cellp = new PdfPCell(pq);
			PdfPCell cell1 = new PdfPCell();

			// Display a date in day, month, year format
			Date dateInfo = Calendar.getInstance().getTime();
			DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
			today = formatter.format(dateInfo).toString();

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

			hcell90 = new PdfPCell(new Phrase(patientRegistration.getPatientDetails().getTitle() + ". "
					+ patientRegistration.getPatientDetails().getFirstName() + ConstantValues.ONE_SPACE_STRING
					+ patientRegistration.getPatientDetails().getMiddleName() + ConstantValues.ONE_SPACE_STRING
					+ patientRegistration.getPatientDetails().getLastName(), redFont));
			hcell90.setBorder(Rectangle.NO_BORDER);
			hcell90.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell90.setPaddingBottom(-7f);
			hcell90.setPaddingLeft(-85f);
			table99.addCell(hcell90);

			cell3.addElement(table99);

			PdfPTable table2 = new PdfPTable(6);
			table2.setWidths(new float[] { 3f, 1f, 4f, 3f, 1f, 4f });
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

			hcell4 = new PdfPCell(new Phrase("Bill No", redFont));
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

			hcell4 = new PdfPCell(new Phrase("", redFont));
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

			hcell15 = new PdfPCell(
					new Phrase(patientRegistration.getPatientDetails().getvRefferalDetails().getRefName(), redFont));
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
			hcell19 = new PdfPCell(new Phrase("Due Receipt", headFont1));
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
			table1.setWidths(new float[] { 1f, 5f, 3f, 1f, 2f, 2f, 2f });

			table1.setSpacingBefore(10);

			PdfPCell hcell;
			hcell = new PdfPCell(new Phrase("S.No", redFont));
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
			for (PatientPayment paymentDueInfo : paymentDue) {

				PdfPCell cell;

				cell = new PdfPCell(new Phrase(String.valueOf(count = count + 1), redFont));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table1.addCell(cell);

				cell = new PdfPCell(new Phrase(paymentDueInfo.getTypeOfCharge(), redFont));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setPaddingLeft(5);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				// cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				table1.addCell(cell);

				cell = new PdfPCell(new Phrase("", redFont));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setPaddingLeft(5);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				// cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				table1.addCell(cell);

				cell = new PdfPCell(new Phrase("", redFont));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setPaddingLeft(5);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table1.addCell(cell);

				cell = new PdfPCell(new Phrase(String.valueOf(paymentDueInfo.getAmount()), redFont));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setPaddingLeft(5);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table1.addCell(cell);

				cell = new PdfPCell(new Phrase(String.valueOf(""), redFont));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setPaddingLeft(5);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table1.addCell(cell);

				cell = new PdfPCell(new Phrase(String.valueOf(paymentDueInfo.getAmount()), redFont));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setPaddingLeft(5);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table1.addCell(cell);

				total += paymentDueInfo.getAmount();

			}

			for (Sales salesDueInfo : salesDue) {

				PdfPCell cell2;

				cell2 = new PdfPCell(new Phrase(String.valueOf(count = count + 1), redFont));
				cell2.setBorder(Rectangle.NO_BORDER);
				cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
				table1.addCell(cell2);

				cell2 = new PdfPCell(new Phrase("Medicine", redFont));
				cell2.setBorder(Rectangle.NO_BORDER);
				cell2.setPaddingLeft(5);
				cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell2.setHorizontalAlignment(Element.ALIGN_LEFT);
				// cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				table1.addCell(cell2);

				cell2 = new PdfPCell(new Phrase(salesDueInfo.getMedicineName(), redFont));
				cell2.setBorder(Rectangle.NO_BORDER);
				cell2.setPaddingLeft(5);
				cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell2.setHorizontalAlignment(Element.ALIGN_LEFT);
				// cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				table1.addCell(cell2);

				cell2 = new PdfPCell(
						new Phrase(String.valueOf(salesDueInfo.getMrp() * salesDueInfo.getQuantity()), redFont));
				cell2.setBorder(Rectangle.NO_BORDER);
				cell2.setPaddingLeft(5);
				cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
				table1.addCell(cell2);

				cell2 = new PdfPCell(new Phrase(String.valueOf(salesDueInfo.getQuantity()), redFont));
				cell2.setBorder(Rectangle.NO_BORDER);
				cell2.setPaddingLeft(5);
				cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
				table1.addCell(cell2);

				cell2 = new PdfPCell(new Phrase(String.valueOf(salesDueInfo.getDiscount()), redFont));
				cell2.setBorder(Rectangle.NO_BORDER);
				cell2.setPaddingLeft(5);
				cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
				table1.addCell(cell2);

				cell2 = new PdfPCell(new Phrase(String.valueOf(salesDueInfo.getAmount()), redFont));
				cell2.setBorder(Rectangle.NO_BORDER);
				cell2.setPaddingLeft(5);
				cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
				table1.addCell(cell2);

				total += salesDueInfo.getAmount();

			}

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

			/*
			 * hcell59 = new PdfPCell(new Phrase("(" + numberToWordsConverter.convert(total)
			 * + ")", redFont)); hcell59.setBorder(Rectangle.NO_BORDER);
			 * hcell59.setHorizontalAlignment(Element.ALIGN_LEFT);
			 * hcell59.setPaddingLeft(-35f); table37.addCell(hcell59);
			 */

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

			/*
			 * hcell60 = new PdfPCell(new Phrase("(" + numberToWordsConverter.convert(total)
			 * + ")", redFont)); hcell60.setBorder(Rectangle.NO_BORDER);
			 * hcell60.setHorizontalAlignment(Element.ALIGN_LEFT);
			 * hcell60.setPaddingLeft(-20f); table37.addCell(hcell60);
			 */

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
			cell31.setFixedHeight(170f);
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

			patientPaymentPdf = new PatientPaymentPdf(regId + " Lab Registration", uri,regId, pdfBytes,null);
			patientPaymentPdf.setPid(paymentPdfServiceImpl.getNextPdfId());
			paymentPdfServiceImpl.save(patientPaymentPdf);

		} catch (Exception e) {
			Logger.error(e.getMessage());
		}

		return patientPaymentPdf;

	}

	/*
	 * Admission Slip
	 */
	@RequestMapping(value = "/patient/slip/{regId}", method = RequestMethod.GET)
	public PatientPaymentPdf AdmissionInfo(@PathVariable("regId") String regId, Principal principal) {
		String blankValue = regId + " Admission Slip";
		PatientPaymentPdf patientPaymentPdf = paymentPdfServiceImpl.getBlankPdf(blankValue);

		return patientPaymentPdf;

	}

	private static final String name = null;
	private static final String age = null;

	@RequestMapping(value = "/checkJpa/{fromTime}/{toTime}")
	public Map<String, String> getJpa(@PathVariable Timestamp fromTime, @PathVariable Timestamp toTime) {

		List<PatientRegistration> patientRegistrations = patientRegistrationRepository
				.findByRegDateGreaterThanEqualAndRegDateLessThanEqual(fromTime, toTime);

		
		Function<PatientRegistration,String> d=(s) -> {
			
			return s.getPatientDetails().getFirstName();
			};
			
			Function<PatientRegistration,String> b=(s) -> {
				
				return s.getPatientDetails().getAge();
				};
		
		Map<String,String> disp=patientRegistrations.stream().collect(Collectors.toMap(d,b));
		
		return disp;
		
		// return

	}
	
		

	//total inpatient payment pdf
	@RequestMapping(value="/getDetailedReport/{regId}")
	public PatientPaymentPdf getDetailedReport(@PathVariable("regId") String regId,Principal principal)
	{
		
		String patientName=null;
		long phoneNo=0;
		String consultant=null;
		String umr=null;
		String billNo=null;
		String regDate=null;
		String age=null;
		String gender=null;
		String paymentMode=null;
		String createdBy=null;
		String printedBy=null;
		String address=null;
		String regdate=null;
		float totalAmount=0;
	
		List<PatientRegistration> patientRegistrations=patientRegistrationRepository.getAllByRegId(regId);
		for(PatientRegistration patient:patientRegistrations)
		{
			if(patient.getpType().equalsIgnoreCase("INPATIENT"))
			{
			patientName=(patient.getPatientDetails().getMiddleName()!=null?patient.getPatientDetails().getFirstName()+
					" "+patient.getPatientDetails().getMiddleName()+" "+patient.getPatientDetails().getLastName():patient.getPatientDetails().getFirstName()+""+patient.getPatientDetails().getLastName());
			
			umr=patient.getPatientDetails().getUmr();
			User userName=userRepository.findOneByUserId(patient.getVuserD().getUserId());
			consultant=userName.getFirstName()+" "+userName.getLastName();
			age=patient.getPatientDetails().getAge();
			gender=patient.getPatientDetails().getGender();
			phoneNo=patient.getPatientDetails().getMobile();
			regDate = patient.getRegDate().toString();
			SimpleDateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat toFormat = new SimpleDateFormat("dd-MM-yyyy");
			try {
				regdate = toFormat.format(fromFormat.parse(regDate));
			} catch (ParseException e) {
				
				e.printStackTrace();
			}
			}
		}
		
		List<PatientPayment> patientPayments=patientPaymentRepository.findByPatientRegistrationId(regId);
		List<ChargeBill> chargeList=chargeBillRepository.findByPatRegIds(regId);
		
	/*	List<ChargeBill> chargeBillListLab = chargeList.stream().filter((s) -> s.getLabId() != null)
				.collect(Collectors.toList());
		List<ChargeBill> chargeBillListService = chargeList.stream().filter((s) -> s.getServiceId() != null)
				.collect(Collectors.toList());
		List<ChargeBill> chargeBillListSale = chargeList.stream().filter((s) -> s.getSaleId() != null)
				.collect(Collectors.toList());
*/
		
		PatientPaymentPdf patientPaymentPdf = null;
	
	byte[] pdfBytes = null;
	ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

	Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
	Font redFontInfo = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.BOLD);
	Font blueFont = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.NORMAL);
	Font font = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

	/*
	 * PatientRegistration patientRegistration = patientRegistrationServiceImpl
	 * .findByRegId(prescription.get("regId"));
	 */
	
	
	// for  reciept
	
	String addrss=ConstantValues.ADVANCE_RECEIPT_ADDRESS;
		/*
		 * String addrss = " Plot No14,15,16 & 17,Nandi Co-op.Society," +
		 * "\n                                   Main Road, Beside Navya Grand Hotel, \n                                Miyapur,Hyderabad-49,Phone:040-23046789   \n                               "
		 * + "   For Appointment Contact:8019114481   " +
		 * "\n                                   Email :udbhavahospitals@gmail.com ";
		 * 
		 */			
			// Display a date in day, month, year format
			Date date = Calendar.getInstance().getTime();
			DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
			String today = formatter.format(date).toString();

			

	Document document = new Document();

	try {
		document = new Document(PageSize.A4_LANDSCAPE);

		redFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

		Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
		Font headFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
		PdfWriter r = PdfWriter.getInstance(document, byteArrayOutputStream);
		document.open();
		PdfPTable table = new PdfPTable(2);
		Resource fileResourcee = resourceLoader.getResource(
				ConstantValues.IMAGE_PNG_CLASSPATH);

	Image img = Image.getInstance(hospitalLogo.getURL());
		img.scaleAbsolute(ConstantValues.IMAGE_ABSOLUTE_INTIAL_POSITION, ConstantValues.IMAGE_ABSOLUTE_FINAL_POSITION);
		table.setWidthPercentage(ConstantValues.TABLE_SET_WIDTH_PERECENTAGE);

		Phrase pq = new Phrase(new Chunk(img, ConstantValues.IMAGE_SET_INTIAL_POSITION, ConstantValues.IMAGE_SET_FINAL_POSITION));
	pq.add(new Chunk(addrss, redFont));
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
		hcell96.setPaddingTop(10f);
		hcell96.setPaddingLeft(52f);

		table96.addCell(hcell96);
		cell1.addElement(table96);
		cell1.addElement(pq);
		cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell1);
//----------------------------------
		PdfPCell cell3 = new PdfPCell();

		PdfPTable table99 = new PdfPTable(3);
		table99.setWidths(new float[] { 3f, 1f, 5f });
		table99.setSpacingBefore(10);

		PdfPCell hcell90;
		hcell90 = new PdfPCell(new Phrase("PatientName", redFont));
		hcell90.setBorder(Rectangle.NO_BORDER);
		hcell90.setHorizontalAlignment(Element.ALIGN_LEFT);
		hcell90.setPaddingLeft(-25f);
		hcell90.setPaddingTop(15f);
		table99.addCell(hcell90);

		hcell90 = new PdfPCell(new Phrase(":", redFont));
		hcell90.setBorder(Rectangle.NO_BORDER);
		hcell90.setHorizontalAlignment(Element.ALIGN_LEFT);
		hcell90.setPaddingLeft(-45f);
		hcell90.setPaddingTop(15f);
		table99.addCell(hcell90);

		hcell90 = new PdfPCell(new Phrase(patientName, redFont));
		hcell90.setBorder(Rectangle.NO_BORDER);
		hcell90.setHorizontalAlignment(Element.ALIGN_LEFT);
		hcell90.setPaddingLeft(-65f);
		hcell90.setPaddingTop(15f);
		table99.addCell(hcell90);

		cell3.addElement(table99);
		// table.addCell(cell3);

		PdfPTable table2 = new PdfPTable(6);
		table2.setWidths(new float[] { 3f, 1f, 5f, 3f, 1f, 5f });
		table2.setSpacingBefore(10);

		PdfPCell hcell1;
		hcell1 = new PdfPCell(new Phrase("Age/Gender", redFont));
		hcell1.setBorder(Rectangle.NO_BORDER);
		hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
		hcell1.setPaddingLeft(-25f);
		hcell1.setPaddingTop(-5f);
		table2.addCell(hcell1);

		hcell1 = new PdfPCell(new Phrase(":", redFont));
		hcell1.setBorder(Rectangle.NO_BORDER);
		hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
		hcell1.setPaddingLeft(-12f);
		hcell1.setPaddingTop(-5f);
		table2.addCell(hcell1);

		hcell1 = new PdfPCell(new Phrase(age + "/" + gender, redFont));
		hcell1.setBorder(Rectangle.NO_BORDER);
		hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
		hcell1.setPaddingLeft(-15f);
		hcell1.setPaddingTop(-5f);
		table2.addCell(hcell1);

		hcell1 = new PdfPCell(new Phrase("Umr No", redFont));
		hcell1.setBorder(Rectangle.NO_BORDER);
		hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
		hcell1.setPaddingRight(-23f);
		hcell1.setPaddingTop(-5f);
		table2.addCell(hcell1);

		hcell1 = new PdfPCell(new Phrase(":", redFont));
		hcell1.setBorder(Rectangle.NO_BORDER);
		hcell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
		hcell1.setPaddingTop(-5f);
		;
		table2.addCell(hcell1);

		hcell1 = new PdfPCell(new Phrase(umr, redFont));
		hcell1.setBorder(Rectangle.NO_BORDER);
		hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
		hcell1.setPaddingRight(-23f);
		hcell1.setPaddingTop(-5f);
		table2.addCell(hcell1);

		PdfPCell hcell4;

		hcell4 = new PdfPCell(new Phrase("Const.No", redFont));
		hcell4.setBorder(Rectangle.NO_BORDER);
		hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
		hcell4.setPaddingLeft(-25f);
		table2.addCell(hcell4);

		hcell4 = new PdfPCell(new Phrase(":", redFont));
		hcell4.setBorder(Rectangle.NO_BORDER);
		hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
		hcell4.setPaddingLeft(-12f);
		table2.addCell(hcell4);

		hcell4 = new PdfPCell(new Phrase(regId, redFont));
		hcell4.setBorder(Rectangle.NO_BORDER);
		hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
		hcell4.setPaddingLeft(-15f);
		table2.addCell(hcell4);

		hcell4 = new PdfPCell(new Phrase("Const.Dt", redFont));
		hcell4.setBorder(Rectangle.NO_BORDER);
		hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
		hcell4.setPaddingRight(-23f);
		table2.addCell(hcell4);

		hcell4 = new PdfPCell(new Phrase(":", redFont));
		hcell4.setBorder(Rectangle.NO_BORDER);
		hcell4.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table2.addCell(hcell4);

		hcell4 = new PdfPCell(new Phrase(today, redFont));
		hcell4.setBorder(Rectangle.NO_BORDER);
		hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
		hcell4.setPaddingRight(-23f);
		table2.addCell(hcell4);

		

	/*	String ref = null;
		if (refBy != null) {
			ref = refBy;
		} else {
			ref = EMPTY_STRING;
		}*/
		PdfPCell hcell15;
		hcell15 = new PdfPCell(new Phrase("Phone No", redFont));
		hcell15.setBorder(Rectangle.NO_BORDER);
		hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
		hcell15.setPaddingLeft(-25f);
		table2.addCell(hcell15);

		hcell15 = new PdfPCell(new Phrase(":", redFont));
		hcell15.setBorder(Rectangle.NO_BORDER);
		hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
		hcell15.setPaddingLeft(-12f);
		table2.addCell(hcell15);

		hcell15 = new PdfPCell(new Phrase(String.valueOf(phoneNo), redFont));
		hcell15.setBorder(Rectangle.NO_BORDER);
		hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
		hcell15.setPaddingLeft(-15f);
		table2.addCell(hcell15);

		hcell15 = new PdfPCell(new Phrase("", redFont));
		hcell15.setBorder(Rectangle.NO_BORDER);
		hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
		hcell15.setPaddingRight(-23f);
		table2.addCell(hcell15);

		hcell15 = new PdfPCell(new Phrase("", redFont));
		hcell15.setBorder(Rectangle.NO_BORDER);
		hcell15.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table2.addCell(hcell15);

		hcell15 = new PdfPCell(new Phrase("", redFont));
		hcell15.setBorder(Rectangle.NO_BORDER);
		hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
		hcell15.setPaddingRight(-23f);
		table2.addCell(hcell15);
	
	
		
		cell3.setFixedHeight(115f);
		cell3.setColspan(2);
		cell3.addElement(table2);
		
		
		
		
		PdfPTable table90 = new PdfPTable(3);
		table90.setWidths(new float[] { 3f, 1f, 5f });
		table90.setSpacingBefore(10);

		PdfPCell hcell91;
		hcell91 = new PdfPCell(new Phrase("Consultant", redFont));
		hcell91.setBorder(Rectangle.NO_BORDER);
		hcell91.setHorizontalAlignment(Element.ALIGN_LEFT);
		hcell91.setPaddingLeft(-25f);
		hcell91.setPaddingTop(5f);
		table90.addCell(hcell91);

		hcell91 = new PdfPCell(new Phrase(":", redFont));
		hcell91.setBorder(Rectangle.NO_BORDER);
		hcell91.setHorizontalAlignment(Element.ALIGN_LEFT);
		hcell91.setPaddingLeft(-45f);
		hcell91.setPaddingTop(5f);
		table90.addCell(hcell91);

		hcell91 = new PdfPCell(new Phrase(consultant, redFont));
		hcell91.setBorder(Rectangle.NO_BORDER);
		hcell91.setHorizontalAlignment(Element.ALIGN_LEFT);
		hcell91.setPaddingLeft(-65f);
		hcell91.setPaddingTop(5f);
		table90.addCell(hcell91);
		
		cell3.addElement(table90);
		
		table.addCell(cell3);

		PdfPCell cell19 = new PdfPCell();

		PdfPTable table21 = new PdfPTable(1);
		table21.setWidths(new float[] { 4f });
		table21.setSpacingBefore(10);

		PdfPCell hcell19;

		hcell19 = new PdfPCell(new Phrase("Payment Details Reciept", headFont1));
		hcell19.setBorder(Rectangle.NO_BORDER);
		hcell19.setHorizontalAlignment(Element.ALIGN_CENTER);
		table21.addCell(hcell19);

		cell19.setFixedHeight(20f);
		cell19.setColspan(2);
		cell19.addElement(table21);
		table.addCell(cell19);

		PdfPCell cell4 = new PdfPCell();

		PdfPTable table3 = new PdfPTable(7);
		table3.setWidths(new float[] { 1.5f,3f,4f, 3f,3f, 3f, 3f });
		table3.setSpacingBefore(10);

		PdfPCell hcell191;

		hcell191 = new PdfPCell(new Phrase("Sr. No", headFont));
		hcell191.setBorder(Rectangle.NO_BORDER);
		hcell191.setHorizontalAlignment(Element.ALIGN_CENTER);
		table3.addCell(hcell191);

		hcell191 = new PdfPCell(new Phrase("Bill No", headFont));
		hcell191.setBorder(Rectangle.NO_BORDER);
		hcell191.setHorizontalAlignment(Element.ALIGN_CENTER);
		table3.addCell(hcell191);

	
		hcell191 = new PdfPCell(new Phrase("Payment Type", headFont));
		hcell191.setBorder(Rectangle.NO_BORDER);
		hcell191.setHorizontalAlignment(Element.ALIGN_CENTER);
		hcell191.setPaddingLeft(10f);
		table3.addCell(hcell191);

		hcell191 = new PdfPCell(new Phrase("Pay. Date", headFont));
		hcell191.setBorder(Rectangle.NO_BORDER);
		hcell191.setHorizontalAlignment(Element.ALIGN_CENTER);
		hcell191.setPaddingLeft(10f);
		table3.addCell(hcell191);
		
		hcell191 = new PdfPCell(new Phrase("Pay. Mode", headFont));
		hcell191.setBorder(Rectangle.NO_BORDER);
		hcell191.setHorizontalAlignment(Element.ALIGN_CENTER);
		hcell191.setPaddingLeft(10f);
		table3.addCell(hcell191);

		hcell191 = new PdfPCell(new Phrase("Discount", headFont));
		hcell191.setBorder(Rectangle.NO_BORDER);
		hcell191.setHorizontalAlignment(Element.ALIGN_CENTER);
		hcell191.setPaddingLeft(10f);
		table3.addCell(hcell191);

		hcell191 = new PdfPCell(new Phrase("Amount", headFont));
		hcell191.setBorder(Rectangle.NO_BORDER);
		hcell191.setHorizontalAlignment(Element.ALIGN_CENTER);
		hcell191.setPaddingLeft(20f);
		table3.addCell(hcell191);
		table3.setWidthPercentage(100f);
		cell4.setColspan(2);
		cell4.addElement(table3);
		
		PdfPCell cell34=new PdfPCell();
		
		PdfPTable table31 = new PdfPTable(7);
		table31.setWidths(new float[] { 1.5f,3.5f, 4f, 3f,3f, 3f, 3f });
		table31.setSpacingBefore(10);

		PdfPCell hcell1911;
		long count=0;
		if(!patientPayments.isEmpty())
		{
			for(PatientPayment payment:patientPayments)
			{
				
				
				String payDate = payment.getInsertedDate().toString();
				SimpleDateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat toFormat = new SimpleDateFormat("dd-MM-yyyy");
				String paydate = toFormat.format(fromFormat.parse(payDate));
				
				
		hcell1911 = new PdfPCell(new Phrase(String.valueOf(count+=1), redFont));
		hcell1911.setBorder(Rectangle.NO_BORDER);
		hcell1911.setHorizontalAlignment(Element.ALIGN_CENTER);
		table31.addCell(hcell1911);
		
		hcell1911 = new PdfPCell(new Phrase(payment.getBillNo(), redFont));
		hcell1911.setBorder(Rectangle.NO_BORDER);
		hcell1911.setHorizontalAlignment(Element.ALIGN_LEFT);
		hcell1911.setPaddingLeft(20f);
		table31.addCell(hcell1911);

	
		hcell1911 = new PdfPCell(new Phrase(payment.getTypeOfCharge(), redFont));
		hcell1911.setBorder(Rectangle.NO_BORDER);
		hcell1911.setHorizontalAlignment(Element.ALIGN_LEFT);
		hcell1911.setPaddingLeft(30f);
		table31.addCell(hcell1911);

		hcell1911 = new PdfPCell(new Phrase(paydate, redFont));
		hcell1911.setBorder(Rectangle.NO_BORDER);
		hcell1911.setHorizontalAlignment(Element.ALIGN_CENTER);
		table31.addCell(hcell1911);

		hcell1911 = new PdfPCell(new Phrase(payment.getModeOfPaymant(), redFont));
		hcell1911.setBorder(Rectangle.NO_BORDER);
		hcell1911.setHorizontalAlignment(Element.ALIGN_LEFT);
		hcell1911.setPaddingLeft(30f);
		table31.addCell(hcell1911);
		
		hcell1911 = new PdfPCell(new Phrase("0", redFont));
		hcell1911.setBorder(Rectangle.NO_BORDER);
		hcell1911.setHorizontalAlignment(Element.ALIGN_RIGHT);
		hcell1911.setPaddingRight(25f);
		table31.addCell(hcell1911);

		hcell1911 = new PdfPCell(new Phrase(String.valueOf(payment.getAmount()), redFont));
		hcell1911.setBorder(Rectangle.NO_BORDER);
		hcell1911.setHorizontalAlignment(Element.ALIGN_RIGHT);
		hcell1911.setPaddingRight(20f);
		table31.addCell(hcell1911);
		
		totalAmount+=payment.getAmount();
			}
		}
		
		
		if(!chargeList.isEmpty())
		{
			for(ChargeBill charge:chargeList)
			{
				
				
				String payDate = charge.getInsertedDate().toString();
				SimpleDateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat toFormat = new SimpleDateFormat("dd-MM-yyyy");
				String paydate = toFormat.format(fromFormat.parse(payDate));
				
				
		hcell1911 = new PdfPCell(new Phrase(String.valueOf(count+=1), redFont));
		hcell1911.setBorder(Rectangle.NO_BORDER);
		hcell1911.setHorizontalAlignment(Element.ALIGN_CENTER);
		table31.addCell(hcell1911);
		
		hcell1911 = new PdfPCell(new Phrase(charge.getBillNo(), redFont));
		hcell1911.setBorder(Rectangle.NO_BORDER);
		hcell1911.setHorizontalAlignment(Element.ALIGN_LEFT);
		hcell1911.setPaddingLeft(20f);
		table31.addCell(hcell1911);

		if(charge.getLabId()!=null)
		{
		hcell1911 = new PdfPCell(new Phrase("Lab", redFont));
		hcell1911.setBorder(Rectangle.NO_BORDER);
		hcell1911.setHorizontalAlignment(Element.ALIGN_LEFT);
		hcell1911.setPaddingLeft(30f);
		table31.addCell(hcell1911);
		}
		else if(charge.getSaleId()!=null)
		{
			hcell1911 = new PdfPCell(new Phrase("Sales", redFont));
			hcell1911.setBorder(Rectangle.NO_BORDER);
			hcell1911.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell1911.setPaddingLeft(30f);
			table31.addCell(hcell1911);
		}
		else if(charge.getServiceId()!=null)
		{
			hcell1911 = new PdfPCell(new Phrase("Other Services", redFont));
			hcell1911.setBorder(Rectangle.NO_BORDER);
			hcell1911.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell1911.setPaddingLeft(30f);
			table31.addCell(hcell1911);
		}

		hcell1911 = new PdfPCell(new Phrase(paydate, redFont));
		hcell1911.setBorder(Rectangle.NO_BORDER);
		hcell1911.setHorizontalAlignment(Element.ALIGN_CENTER);
		table31.addCell(hcell1911);

		hcell1911 = new PdfPCell(new Phrase(charge.getPaymentType(), redFont));
		hcell1911.setBorder(Rectangle.NO_BORDER);
		hcell1911.setHorizontalAlignment(Element.ALIGN_LEFT);
		hcell1911.setPaddingLeft(30f);
		table31.addCell(hcell1911);
		
		hcell1911 = new PdfPCell(new Phrase(String.valueOf(charge.getDiscount()), redFont));
		hcell1911.setBorder(Rectangle.NO_BORDER);
		hcell1911.setHorizontalAlignment(Element.ALIGN_RIGHT);
		hcell1911.setPaddingRight(25f);
		table31.addCell(hcell1911);

		hcell1911 = new PdfPCell(new Phrase(String.valueOf(charge.getNetAmount()), redFont));
		hcell1911.setBorder(Rectangle.NO_BORDER);
		hcell1911.setHorizontalAlignment(Element.ALIGN_RIGHT);
		hcell1911.setPaddingRight(20f);
		table31.addCell(hcell1911);
		
		totalAmount+=charge.getNetAmount();
			}
		}
		table31.setWidthPercentage(100f);
		cell34.setColspan(2);
		cell34.addElement(table31);
		
		table.addCell(cell4);
		table.addCell(cell34);
		
		
		
		
		PdfPCell cell10 = new PdfPCell();

		PdfPTable table4 = new PdfPTable(7);
		table4.setWidths(new float[] { 6f,3f,1f, 1f,3f, 3f, 3f });
		table4.setSpacingBefore(10);

		PdfPCell hcell192;

		hcell192 = new PdfPCell(new Phrase("Total Amount Paid:", headFont));
		hcell192.setBorder(Rectangle.NO_BORDER);
		hcell192.setHorizontalAlignment(Element.ALIGN_CENTER);
		hcell192.setPaddingLeft(10);
		table4.addCell(hcell192);

		hcell192 = new PdfPCell(new Phrase("", headFont));
		hcell192.setBorder(Rectangle.NO_BORDER);
		hcell192.setHorizontalAlignment(Element.ALIGN_CENTER);
		table4.addCell(hcell192);

	
		hcell192 = new PdfPCell(new Phrase("", headFont));
		hcell192.setBorder(Rectangle.NO_BORDER);
		hcell192.setHorizontalAlignment(Element.ALIGN_CENTER);
		hcell192.setPaddingLeft(10f);
		table4.addCell(hcell192);

		hcell192 = new PdfPCell(new Phrase("", headFont));
		hcell192.setBorder(Rectangle.NO_BORDER);
		hcell192.setHorizontalAlignment(Element.ALIGN_CENTER);
		hcell192.setPaddingLeft(10f);
		table4.addCell(hcell192);
		
		hcell192 = new PdfPCell(new Phrase("", headFont));
		hcell192.setBorder(Rectangle.NO_BORDER);
		hcell192.setHorizontalAlignment(Element.ALIGN_CENTER);
		hcell192.setPaddingLeft(10f);
		table4.addCell(hcell192);

		hcell192 = new PdfPCell(new Phrase("", headFont));
		hcell192.setBorder(Rectangle.NO_BORDER);
		hcell192.setHorizontalAlignment(Element.ALIGN_CENTER);
		hcell192.setPaddingLeft(10f);
		table4.addCell(hcell192);

		hcell192 = new PdfPCell(new Phrase(String.valueOf(totalAmount), headFont));
		hcell192.setBorder(Rectangle.NO_BORDER);
		hcell192.setHorizontalAlignment(Element.ALIGN_CENTER);
		hcell192.setPaddingLeft(20f);
		table4.addCell(hcell192);
		
		table4.setWidthPercentage(100f);
		cell10.setColspan(2);
		cell10.addElement(table4);
		cell4.setColspan(2);
		table.addCell(cell10);


		document.add(table);

		document.close();
		pdfBytes = byteArrayOutputStream.toByteArray();
		
		
		PatientPaymentPdf patientPaymentPdfs=paymentPdfRepository.getUltimatePdf(regId);
		if (patientPaymentPdfs != null) {
			patientPaymentPdf = new PatientPaymentPdf();
			patientPaymentPdf.setFileName(regId + " Patient Detailed Reciept");
			patientPaymentPdf.setFileuri(patientPaymentPdfs.getFileuri());
			patientPaymentPdf.setPid(patientPaymentPdfs.getPid());
			patientPaymentPdf.setData(pdfBytes);
			paymentPdfServiceImpl.save(patientPaymentPdf);
		} else {

			String uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/payment/viewFile/")
					.path(paymentPdfServiceImpl.getNextPdfId()).toUriString();

			patientPaymentPdf = new PatientPaymentPdf();
			patientPaymentPdf.setFileName(regId + " Patient Detailed Reciept");
			patientPaymentPdf.setFileuri(uri);
			patientPaymentPdf.setPid(paymentPdfServiceImpl.getNextPdfId());
			patientPaymentPdf.setData(pdfBytes);
			paymentPdfServiceImpl.save(patientPaymentPdf);
		}

	} catch (Exception e) {
		e.printStackTrace();
	}
	return patientPaymentPdf;
	}
		
		//TOTAL ADVANCE DETAILS INPATIENT
	@RequestMapping(value="/getDetailedAdvanceReport/{regId}")
	public PatientPaymentPdf getDetailedAdvanceReport(@PathVariable("regId") String regId,Principal principal)
	{
		
		String patientName=null;
		long phoneNo=0;
		String consultant=null;
		String umr=null;
		String billNo=null;
		String regDate=null;
		String age=null;
		String gender=null;
		String paymentMode=null;
		String createdBy=null;
		String printedBy=null;
		String address=null;
		String regdate=null;
		float totalAmount=0;
	
		List<PatientRegistration> patientRegistrations=patientRegistrationRepository.getAllByRegId(regId);
		for(PatientRegistration patient:patientRegistrations)
		{
			if(patient.getpType().equalsIgnoreCase("INPATIENT"))
			{
			patientName=(patient.getPatientDetails().getMiddleName()!=null?patient.getPatientDetails().getFirstName()+
					" "+patient.getPatientDetails().getMiddleName()+" "+patient.getPatientDetails().getLastName():patient.getPatientDetails().getFirstName()+""+patient.getPatientDetails().getLastName());
			
			umr=patient.getPatientDetails().getUmr();
			User userName=userRepository.findOneByUserId(patient.getVuserD().getUserId());
			consultant=userName.getFirstName()+" "+userName.getLastName();
			age=patient.getPatientDetails().getAge();
			gender=patient.getPatientDetails().getGender();
			phoneNo=patient.getPatientDetails().getMobile();
			regDate = patient.getRegDate().toString();
			SimpleDateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat toFormat = new SimpleDateFormat("dd-MM-yyyy");
			try {
				regdate = toFormat.format(fromFormat.parse(regDate));
			} catch (ParseException e) {
				
				e.printStackTrace();
			}
			}
		}
		
		List<PatientPayment> patientPayments=patientPaymentRepository.findByPatientRegistrationIdAdvance(regId);
		System.out.println("payment---------------------------------"+patientPayments);
		//List<ChargeBill> chargeList=chargeBillRepository.findByPatRegIds(regId);
		
	/*	List<ChargeBill> chargeBillListLab = chargeList.stream().filter((s) -> s.getLabId() != null)
				.collect(Collectors.toList());
		List<ChargeBill> chargeBillListService = chargeList.stream().filter((s) -> s.getServiceId() != null)
				.collect(Collectors.toList());
		List<ChargeBill> chargeBillListSale = chargeList.stream().filter((s) -> s.getSaleId() != null)
				.collect(Collectors.toList());
*/
		
		PatientPaymentPdf patientPaymentPdf = null;
	
	byte[] pdfBytes = null;
	ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

	Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
	Font redFontInfo = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.BOLD);
	Font blueFont = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.NORMAL);
	Font font = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

	/*
	 * PatientRegistration patientRegistration = patientRegistrationServiceImpl
	 * .findByRegId(prescription.get("regId"));
	 */
	
	
	// for  reciept
	String addrss=ConstantValues.ADVANCE_RECEIPT_ADDRESS;
	
		/*
		 * String addrss = " Plot No14,15,16 & 17,Nandi Co-op.Society," +
		 * "\n                                   Main Road, Beside Navya Grand Hotel, \n                                Miyapur,Hyderabad-49,Phone:040-23046789   \n                               "
		 * + "   For Appointment Contact:8019114481   " +
		 * "\n                                   Email :udbhavahospitals@gmail.com ";
		 */
			
			// Display a date in day, month, year format
			Date date = Calendar.getInstance().getTime();
			DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
			String today = formatter.format(date).toString();

			

	Document document = new Document();

	try {
		document = new Document(PageSize.A4_LANDSCAPE);

		redFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

		Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
		Font headFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
		PdfWriter r = PdfWriter.getInstance(document, byteArrayOutputStream);
		document.open();
		PdfPTable table = new PdfPTable(2);
		Resource fileResourcee = resourceLoader.getResource(ConstantValues.IMAGE_PNG_CLASSPATH);

	Image img = Image.getInstance(hospitalLogo.getURL());

		img.scaleAbsolute(ConstantValues.IMAGE_ABSOLUTE_INTIAL_POSITION, ConstantValues.IMAGE_ABSOLUTE_FINAL_POSITION);
		table.setWidthPercentage(ConstantValues.TABLE_SET_WIDTH_PERECENTAGE);

		Phrase pq = new Phrase(new Chunk(img, ConstantValues.IMAGE_SET_INTIAL_POSITION, ConstantValues.IMAGE_SET_FINAL_POSITION));
	pq.add(new Chunk(addrss, redFont));
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
		hcell96.setPaddingTop(10f);
		hcell96.setPaddingLeft(52f);

		table96.addCell(hcell96);
		cell1.addElement(table96);
		cell1.addElement(pq);
		cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell1);
//----------------------------------
		PdfPCell cell3 = new PdfPCell();

		PdfPTable table99 = new PdfPTable(3);
		table99.setWidths(new float[] { 3f, 1f, 5f });
		table99.setSpacingBefore(10);

		PdfPCell hcell90;
		hcell90 = new PdfPCell(new Phrase("PatientName", redFont));
		hcell90.setBorder(Rectangle.NO_BORDER);
		hcell90.setHorizontalAlignment(Element.ALIGN_LEFT);
		hcell90.setPaddingLeft(-25f);
		hcell90.setPaddingTop(15f);
		table99.addCell(hcell90);

		hcell90 = new PdfPCell(new Phrase(":", redFont));
		hcell90.setBorder(Rectangle.NO_BORDER);
		hcell90.setHorizontalAlignment(Element.ALIGN_LEFT);
		hcell90.setPaddingLeft(-45f);
		hcell90.setPaddingTop(15f);
		table99.addCell(hcell90);

		hcell90 = new PdfPCell(new Phrase(patientName, redFont));
		hcell90.setBorder(Rectangle.NO_BORDER);
		hcell90.setHorizontalAlignment(Element.ALIGN_LEFT);
		hcell90.setPaddingLeft(-65f);
		hcell90.setPaddingTop(15f);
		table99.addCell(hcell90);

		cell3.addElement(table99);
		// table.addCell(cell3);

		PdfPTable table2 = new PdfPTable(6);
		table2.setWidths(new float[] { 3f, 1f, 5f, 3f, 1f, 5f });
		table2.setSpacingBefore(10);

		PdfPCell hcell1;
		hcell1 = new PdfPCell(new Phrase("Age/Gender", redFont));
		hcell1.setBorder(Rectangle.NO_BORDER);
		hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
		hcell1.setPaddingLeft(-25f);
		hcell1.setPaddingTop(-5f);
		table2.addCell(hcell1);

		hcell1 = new PdfPCell(new Phrase(":", redFont));
		hcell1.setBorder(Rectangle.NO_BORDER);
		hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
		hcell1.setPaddingLeft(-12f);
		hcell1.setPaddingTop(-5f);
		table2.addCell(hcell1);

		hcell1 = new PdfPCell(new Phrase(age + "/" + gender, redFont));
		hcell1.setBorder(Rectangle.NO_BORDER);
		hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
		hcell1.setPaddingLeft(-15f);
		hcell1.setPaddingTop(-5f);
		table2.addCell(hcell1);

		hcell1 = new PdfPCell(new Phrase("Umr No", redFont));
		hcell1.setBorder(Rectangle.NO_BORDER);
		hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
		hcell1.setPaddingRight(-23f);
		hcell1.setPaddingTop(-5f);
		table2.addCell(hcell1);

		hcell1 = new PdfPCell(new Phrase(":", redFont));
		hcell1.setBorder(Rectangle.NO_BORDER);
		hcell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
		hcell1.setPaddingTop(-5f);
		;
		table2.addCell(hcell1);

		hcell1 = new PdfPCell(new Phrase(umr, redFont));
		hcell1.setBorder(Rectangle.NO_BORDER);
		hcell1.setHorizontalAlignment(Element.ALIGN_LEFT);
		hcell1.setPaddingRight(-23f);
		hcell1.setPaddingTop(-5f);
		table2.addCell(hcell1);

		PdfPCell hcell4;

		hcell4 = new PdfPCell(new Phrase("Const.No", redFont));
		hcell4.setBorder(Rectangle.NO_BORDER);
		hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
		hcell4.setPaddingLeft(-25f);
		table2.addCell(hcell4);

		hcell4 = new PdfPCell(new Phrase(":", redFont));
		hcell4.setBorder(Rectangle.NO_BORDER);
		hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
		hcell4.setPaddingLeft(-12f);
		table2.addCell(hcell4);

		hcell4 = new PdfPCell(new Phrase(regId, redFont));
		hcell4.setBorder(Rectangle.NO_BORDER);
		hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
		hcell4.setPaddingLeft(-15f);
		table2.addCell(hcell4);

		hcell4 = new PdfPCell(new Phrase("Const.Dt", redFont));
		hcell4.setBorder(Rectangle.NO_BORDER);
		hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
		hcell4.setPaddingRight(-23f);
		table2.addCell(hcell4);

		hcell4 = new PdfPCell(new Phrase(":", redFont));
		hcell4.setBorder(Rectangle.NO_BORDER);
		hcell4.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table2.addCell(hcell4);

		hcell4 = new PdfPCell(new Phrase(today, redFont));
		hcell4.setBorder(Rectangle.NO_BORDER);
		hcell4.setHorizontalAlignment(Element.ALIGN_LEFT);
		hcell4.setPaddingRight(-23f);
		table2.addCell(hcell4);

		

	/*	String ref = null;
		if (refBy != null) {
			ref = refBy;
		} else {
			ref = EMPTY_STRING;
		}*/
		PdfPCell hcell15;
		hcell15 = new PdfPCell(new Phrase("Phone No", redFont));
		hcell15.setBorder(Rectangle.NO_BORDER);
		hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
		hcell15.setPaddingLeft(-25f);
		table2.addCell(hcell15);

		hcell15 = new PdfPCell(new Phrase(":", redFont));
		hcell15.setBorder(Rectangle.NO_BORDER);
		hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
		hcell15.setPaddingLeft(-12f);
		table2.addCell(hcell15);

		hcell15 = new PdfPCell(new Phrase(String.valueOf(phoneNo), redFont));
		hcell15.setBorder(Rectangle.NO_BORDER);
		hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
		hcell15.setPaddingLeft(-15f);
		table2.addCell(hcell15);

		hcell15 = new PdfPCell(new Phrase("", redFont));
		hcell15.setBorder(Rectangle.NO_BORDER);
		hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
		hcell15.setPaddingRight(-23f);
		table2.addCell(hcell15);

		hcell15 = new PdfPCell(new Phrase("", redFont));
		hcell15.setBorder(Rectangle.NO_BORDER);
		hcell15.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table2.addCell(hcell15);

		hcell15 = new PdfPCell(new Phrase("", redFont));
		hcell15.setBorder(Rectangle.NO_BORDER);
		hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
		hcell15.setPaddingRight(-23f);
		table2.addCell(hcell15);
	
	
		
		cell3.setFixedHeight(115f);
		cell3.setColspan(2);
		cell3.addElement(table2);
		
		
		
		
		PdfPTable table90 = new PdfPTable(3);
		table90.setWidths(new float[] { 3f, 1f, 5f });
		table90.setSpacingBefore(10);

		PdfPCell hcell91;
		hcell91 = new PdfPCell(new Phrase("Consultant", redFont));
		hcell91.setBorder(Rectangle.NO_BORDER);
		hcell91.setHorizontalAlignment(Element.ALIGN_LEFT);
		hcell91.setPaddingLeft(-25f);
		hcell91.setPaddingTop(5f);
		table90.addCell(hcell91);

		hcell91 = new PdfPCell(new Phrase(":", redFont));
		hcell91.setBorder(Rectangle.NO_BORDER);
		hcell91.setHorizontalAlignment(Element.ALIGN_LEFT);
		hcell91.setPaddingLeft(-45f);
		hcell91.setPaddingTop(5f);
		table90.addCell(hcell91);

		hcell91 = new PdfPCell(new Phrase(consultant, redFont));
		hcell91.setBorder(Rectangle.NO_BORDER);
		hcell91.setHorizontalAlignment(Element.ALIGN_LEFT);
		hcell91.setPaddingLeft(-65f);
		hcell91.setPaddingTop(5f);
		table90.addCell(hcell91);
		
		cell3.addElement(table90);
		
		table.addCell(cell3);

		PdfPCell cell19 = new PdfPCell();

		PdfPTable table21 = new PdfPTable(1);
		table21.setWidths(new float[] { 4f });
		table21.setSpacingBefore(10);

		PdfPCell hcell19;

		hcell19 = new PdfPCell(new Phrase("Payment Details Reciept", headFont1));
		hcell19.setBorder(Rectangle.NO_BORDER);
		hcell19.setHorizontalAlignment(Element.ALIGN_CENTER);
		table21.addCell(hcell19);

		cell19.setFixedHeight(20f);
		cell19.setColspan(2);
		cell19.addElement(table21);
		table.addCell(cell19);

		PdfPCell cell4 = new PdfPCell();

		PdfPTable table3 = new PdfPTable(7);
		table3.setWidths(new float[] { 1.5f,3f,4f, 3f,3f, 3f, 3f });
		table3.setSpacingBefore(10);

		PdfPCell hcell191;

		hcell191 = new PdfPCell(new Phrase("Sr. No", headFont));
		hcell191.setBorder(Rectangle.NO_BORDER);
		hcell191.setHorizontalAlignment(Element.ALIGN_CENTER);
		table3.addCell(hcell191);

		hcell191 = new PdfPCell(new Phrase("Bill No", headFont));
		hcell191.setBorder(Rectangle.NO_BORDER);
		hcell191.setHorizontalAlignment(Element.ALIGN_CENTER);
		table3.addCell(hcell191);

	
		hcell191 = new PdfPCell(new Phrase("Payment Type", headFont));
		hcell191.setBorder(Rectangle.NO_BORDER);
		hcell191.setHorizontalAlignment(Element.ALIGN_CENTER);
		hcell191.setPaddingLeft(10f);
		table3.addCell(hcell191);

		hcell191 = new PdfPCell(new Phrase("Pay. Date", headFont));
		hcell191.setBorder(Rectangle.NO_BORDER);
		hcell191.setHorizontalAlignment(Element.ALIGN_CENTER);
		hcell191.setPaddingLeft(10f);
		table3.addCell(hcell191);
		
		hcell191 = new PdfPCell(new Phrase("Pay. Mode", headFont));
		hcell191.setBorder(Rectangle.NO_BORDER);
		hcell191.setHorizontalAlignment(Element.ALIGN_CENTER);
		hcell191.setPaddingLeft(10f);
		table3.addCell(hcell191);

		hcell191 = new PdfPCell(new Phrase("Discount", headFont));
		hcell191.setBorder(Rectangle.NO_BORDER);
		hcell191.setHorizontalAlignment(Element.ALIGN_CENTER);
		hcell191.setPaddingLeft(10f);
		table3.addCell(hcell191);

		hcell191 = new PdfPCell(new Phrase("Amount", headFont));
		hcell191.setBorder(Rectangle.NO_BORDER);
		hcell191.setHorizontalAlignment(Element.ALIGN_CENTER);
		hcell191.setPaddingLeft(20f);
		table3.addCell(hcell191);
		table3.setWidthPercentage(100f);
		cell4.setColspan(2);
		cell4.addElement(table3);
		
		PdfPCell cell34=new PdfPCell();
		
		PdfPTable table31 = new PdfPTable(7);
		table31.setWidths(new float[] { 1.5f,3.5f, 4f, 3f,3f, 3f, 3f });
		table31.setSpacingBefore(10);

		PdfPCell hcell1911;
		long count=0;
		if(!patientPayments.isEmpty())
		{
			for(PatientPayment payment:patientPayments)
			{
				
				
				String payDate = payment.getInsertedDate().toString();
				SimpleDateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat toFormat = new SimpleDateFormat("dd-MM-yyyy");
				String paydate = toFormat.format(fromFormat.parse(payDate));
				
				
		hcell1911 = new PdfPCell(new Phrase(String.valueOf(count+=1), redFont));
		hcell1911.setBorder(Rectangle.NO_BORDER);
		hcell1911.setHorizontalAlignment(Element.ALIGN_CENTER);
		table31.addCell(hcell1911);
		
		hcell1911 = new PdfPCell(new Phrase(payment.getBillNo(), redFont));
		hcell1911.setBorder(Rectangle.NO_BORDER);
		hcell1911.setHorizontalAlignment(Element.ALIGN_LEFT);
		hcell1911.setPaddingLeft(20f);
		table31.addCell(hcell1911);

	
		hcell1911 = new PdfPCell(new Phrase(payment.getTypeOfCharge(), redFont));
		hcell1911.setBorder(Rectangle.NO_BORDER);
		hcell1911.setHorizontalAlignment(Element.ALIGN_LEFT);
		hcell1911.setPaddingLeft(30f);
		table31.addCell(hcell1911);

		hcell1911 = new PdfPCell(new Phrase(paydate, redFont));
		hcell1911.setBorder(Rectangle.NO_BORDER);
		hcell1911.setHorizontalAlignment(Element.ALIGN_CENTER);
		table31.addCell(hcell1911);

		hcell1911 = new PdfPCell(new Phrase(payment.getModeOfPaymant(), redFont));
		hcell1911.setBorder(Rectangle.NO_BORDER);
		hcell1911.setHorizontalAlignment(Element.ALIGN_LEFT);
		hcell1911.setPaddingLeft(30f);
		table31.addCell(hcell1911);
		
		hcell1911 = new PdfPCell(new Phrase("0", redFont));
		hcell1911.setBorder(Rectangle.NO_BORDER);
		hcell1911.setHorizontalAlignment(Element.ALIGN_RIGHT);
		hcell1911.setPaddingRight(25f);
		table31.addCell(hcell1911);

		hcell1911 = new PdfPCell(new Phrase(String.valueOf(payment.getAmount()), redFont));
		hcell1911.setBorder(Rectangle.NO_BORDER);
		hcell1911.setHorizontalAlignment(Element.ALIGN_RIGHT);
		hcell1911.setPaddingRight(20f);
		table31.addCell(hcell1911);
		
		totalAmount+=payment.getAmount();
			}
		}
		
		
	/*	if(!chargeList.isEmpty())
		{
			for(ChargeBill charge:chargeList)
			{
				
				
				String payDate = charge.getInsertedDate().toString();
				SimpleDateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat toFormat = new SimpleDateFormat("dd-MM-yyyy");
				String payda	te = toFormat.format(fromFormat.parse(payDate));
				
				
		hcell1911 = new PdfPCell(new Phrase(String.valueOf(count+=1), redFont));
		hcell1911.setBorder(Rectangle.NO_BORDER);
		hcell1911.setHorizontalAlignment(Element.ALIGN_CENTER);
		table31.addCell(hcell1911);
		
		hcell1911 = new PdfPCell(new Phrase(charge.getBillNo(), redFont));
		hcell1911.setBorder(Rectangle.NO_BORDER);
		hcell1911.setHorizontalAlignment(Element.ALIGN_LEFT);
		hcell1911.setPaddingLeft(20f);
		table31.addCell(hcell1911);

		if(charge.getLabId()!=null)
		{
		hcell1911 = new PdfPCell(new Phrase("Lab", redFont));
		hcell1911.setBorder(Rectangle.NO_BORDER);
		hcell1911.setHorizontalAlignment(Element.ALIGN_LEFT);
		hcell1911.setPaddingLeft(30f);
		table31.addCell(hcell1911);
		}
		else if(charge.getSaleId()!=null)
		{
			hcell1911 = new PdfPCell(new Phrase("Sales", redFont));
			hcell1911.setBorder(Rectangle.NO_BORDER);
			hcell1911.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell1911.setPaddingLeft(30f);
			table31.addCell(hcell1911);
		}
		else if(charge.getServiceId()!=null)
		{
			hcell1911 = new PdfPCell(new Phrase("Other Services", redFont));
			hcell1911.setBorder(Rectangle.NO_BORDER);
			hcell1911.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell1911.setPaddingLeft(30f);
			table31.addCell(hcell1911);
		}

		hcell1911 = new PdfPCell(new Phrase(paydate, redFont));
		hcell1911.setBorder(Rectangle.NO_BORDER);
		hcell1911.setHorizontalAlignment(Element.ALIGN_CENTER);
		table31.addCell(hcell1911);

		hcell1911 = new PdfPCell(new Phrase(charge.getPaymentType(), redFont));
		hcell1911.setBorder(Rectangle.NO_BORDER);
		hcell1911.setHorizontalAlignment(Element.ALIGN_LEFT);
		hcell1911.setPaddingLeft(30f);
		table31.addCell(hcell1911);
		
		hcell1911 = new PdfPCell(new Phrase(String.valueOf(charge.getDiscount()), redFont));
		hcell1911.setBorder(Rectangle.NO_BORDER);
		hcell1911.setHorizontalAlignment(Element.ALIGN_RIGHT);
		hcell1911.setPaddingRight(25f);
		table31.addCell(hcell1911);

		hcell1911 = new PdfPCell(new Phrase(String.valueOf(charge.getNetAmount()), redFont));
		hcell1911.setBorder(Rectangle.NO_BORDER);
		hcell1911.setHorizontalAlignment(Element.ALIGN_RIGHT);
		hcell1911.setPaddingRight(20f);
		table31.addCell(hcell1911);
		
		totalAmount+=charge.getNetAmount();
			}
		}*/
		table31.setWidthPercentage(100f);
		cell34.setColspan(2);
		cell34.addElement(table31);
		
		table.addCell(cell4);
		table.addCell(cell34);
		
		
		
		
		PdfPCell cell10 = new PdfPCell();

		PdfPTable table4 = new PdfPTable(7);
		table4.setWidths(new float[] { 6f,3f,1f, 1f,3f, 3f, 3f });
		table4.setSpacingBefore(10);

		PdfPCell hcell192;

		hcell192 = new PdfPCell(new Phrase("Total Amount Paid:", headFont));
		hcell192.setBorder(Rectangle.NO_BORDER);
		hcell192.setHorizontalAlignment(Element.ALIGN_CENTER);
		hcell192.setPaddingLeft(10);
		table4.addCell(hcell192);

		hcell192 = new PdfPCell(new Phrase("", headFont));
		hcell192.setBorder(Rectangle.NO_BORDER);
		hcell192.setHorizontalAlignment(Element.ALIGN_CENTER);
		table4.addCell(hcell192);

	
		hcell192 = new PdfPCell(new Phrase("", headFont));
		hcell192.setBorder(Rectangle.NO_BORDER);
		hcell192.setHorizontalAlignment(Element.ALIGN_CENTER);
		hcell192.setPaddingLeft(10f);
		table4.addCell(hcell192);

		hcell192 = new PdfPCell(new Phrase("", headFont));
		hcell192.setBorder(Rectangle.NO_BORDER);
		hcell192.setHorizontalAlignment(Element.ALIGN_CENTER);
		hcell192.setPaddingLeft(10f);
		table4.addCell(hcell192);
		
		hcell192 = new PdfPCell(new Phrase("", headFont));
		hcell192.setBorder(Rectangle.NO_BORDER);
		hcell192.setHorizontalAlignment(Element.ALIGN_CENTER);
		hcell192.setPaddingLeft(10f);
		table4.addCell(hcell192);

		hcell192 = new PdfPCell(new Phrase("", headFont));
		hcell192.setBorder(Rectangle.NO_BORDER);
		hcell192.setHorizontalAlignment(Element.ALIGN_CENTER);
		hcell192.setPaddingLeft(10f);
		table4.addCell(hcell192);

		hcell192 = new PdfPCell(new Phrase(String.valueOf(totalAmount), headFont));
		hcell192.setBorder(Rectangle.NO_BORDER);
		hcell192.setHorizontalAlignment(Element.ALIGN_CENTER);
		hcell192.setPaddingLeft(20f);
		table4.addCell(hcell192);
		
		table4.setWidthPercentage(100f);
		cell10.setColspan(2);
		cell10.addElement(table4);
		cell4.setColspan(2);
		table.addCell(cell10);


		document.add(table);

		document.close();
	
		pdfBytes = byteArrayOutputStream.toByteArray();
		
		PatientPaymentPdf patientPaymentPdfs=paymentPdfRepository.getUltimatePdf(regId);
		if (patientPaymentPdfs != null) {
			patientPaymentPdf = new PatientPaymentPdf();
			patientPaymentPdf.setFileName(regId + " Patient Detailed Advance Reciept");
			patientPaymentPdf.setFileuri(patientPaymentPdfs.getFileuri());
			patientPaymentPdf.setPid(patientPaymentPdfs.getPid());
			patientPaymentPdf.setData(pdfBytes);
			paymentPdfServiceImpl.save(patientPaymentPdf);
		} else {

			String uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/payment/viewFile/")
					.path(paymentPdfServiceImpl.getNextPdfId()).toUriString();

			patientPaymentPdf = new PatientPaymentPdf();
			patientPaymentPdf.setFileName(billNo + "-" + regId + "Patient Detailed Advance Reciept");
			patientPaymentPdf.setFileuri(uri);
			patientPaymentPdf.setPid(paymentPdfServiceImpl.getNextPdfId());
			patientPaymentPdf.setData(pdfBytes);
			paymentPdfServiceImpl.save(patientPaymentPdf);
		}


	} catch (Exception e) {
		e.printStackTrace();
	}
	return patientPaymentPdf;
	}
		
		@RequestMapping(value="/patient/block/{regId}/{flag}",method=RequestMethod.POST)
		public void blockPatientPayment(@PathVariable("regId") String regId,@PathVariable("flag") boolean flag)
		{
			if(regId==null)
			{
				throw new RuntimeException("Please enter registration id !");
			}
			PatientRegistration patientRegistration=patientRegistrationServiceImpl.findByRegId(regId);
			patientRegistration.setBlockedStatus(flag);
			patientRegistrationServiceImpl.save(patientRegistration);
		}
		
		/*
		 *  This method is to give revists count for OP
		 */
		@RequestMapping(value="/patient/revisits/{regId}",method=RequestMethod.GET)
		public Map<String, String> revisitsGet(@PathVariable String regId)
		{
			PatientRegistration patientRegistration = patientRegistrationServiceImpl.findByRegId(regId);
			
			if(!patientRegistration.getpType().equalsIgnoreCase("OUTPATIENT"))
			{
				throw new RuntimeException("Only OUTPATIENTS ARE ALLOWED !");
			}
			
			HashMap<String,String> info=new HashMap<String,String>();
			
			// Calculating revisits
			LocalDate today = LocalDate.now();
			LocalDate regDaregDate = patientRegistration.getRegDate().toLocalDateTime().toLocalDate();
			Period period = Period.between(regDaregDate, today);
			
			System.out.println(period.getYears()+" "+period.getMonths()+" "+period.getDays());
			
			if(patientRegistration.getRevisits()==0)
			{
				throw new RuntimeException("Patient has finished his Free visits !");
			}
			if(period.getYears()==0 && period.getMonths()==0 && period.getDays()<=7 && patientRegistration.getRevisits()>0)
			{
				info.put("visitsLeft", String.valueOf(patientRegistration.getRevisits()));
				info.put("validTill", patientRegistration.getRegValidity());
			}
			else
			{
				throw new RuntimeException("Registration validity is expired !");
			}
			
			return info;
		}
		
		
		/*
		 * To print revisit blank prescription
		 */
		@RequestMapping(value="/patient/revisits/{regId}",method=RequestMethod.POST)
		public PatientPaymentPdf revisitsPrint(@PathVariable String regId,Principal principal)
		{
			PatientRegistration patientRegistration = patientRegistrationServiceImpl.findByRegId(regId);
			
			LocalDate today = LocalDate.now();
			LocalDate regDaregDate = patientRegistration.getRegDate().toLocalDateTime().toLocalDate();
			Period period = Period.between(regDaregDate, today);
			
			if(patientRegistration.getRevisits()==0)
			{
				throw new RuntimeException("Registration validity expired !");
			}
			if(period.getYears()==0 && period.getMonths()==0 && !(period.getDays()<=7))
			{
				throw new RuntimeException("Registration validity expired !");
			}
			
			String umr=patientRegistration.getPatientDetails().getUmr();
			
			patientRegistration.setRevisits(patientRegistration.getRevisits()-1);
			patientRegistrationServiceImpl.save(patientRegistration);
			return patientDetailsServiceImpl.revistBlankPrescription(umr, regId, true, principal);
			
		}
		
		/* 
		 * Existing patient list by umr 
		 */
		@RequestMapping(value = "/patient/oneExistingPatientbyUmr/{umr}")
		public List<Object> getOneExistingPatientByUmr(@PathVariable String umr) {
				return patientDetailsServiceImpl.getOneExistingPatientByUmr(umr);

		}
		
		
		/* 
		 * Existing patient list by mobile 
		 */

		@RequestMapping(value = "/patient/oneExistingPatientbyMobile/{mobile}")
		public List<Object> getOneExistingPatientByMobile(@PathVariable long mobile) {
			
			return patientDetailsServiceImpl.getOneExistingPatientByMobile(mobile);

		}
		
		/* 
		 * Existing patient list by name 
		 */

		@RequestMapping(value = "/patient/oneExistingPatientbyName/{name}")
		public List<Object> getOneExistingPatientByName(@PathVariable String name) {
			
			return patientDetailsServiceImpl.getOneExistingPatientByName(name);

		}
		
	/*
	 * for existing patient list filter with regId-----
	 */
		@RequestMapping(value = "/patient/oneExistingPatient/{regId}")
		public List<Object> getOneExistingPatient(@PathVariable String regId) {
			
			return patientDetailsServiceImpl.getOneExistingPatient(regId);

		}

		
		
		
		
		
        /*
* List of EMERCENCY,DAYCARE,VIP (ONLY FOR 2 DAYS, 7 DAYS, 15 DAYS, 30 DAYS)
*/
@RequestMapping(value = "/patient/otherpatientDetails/{type}", method = RequestMethod.GET)
public List<Map<String, String>> otherpatientDetails(@PathVariable String type) {
return patientDetailsServiceImpl.otherpatientDetails(type);

}



//findout the reg fee for patient
@RequestMapping(value="/patient/regfee/{umr}")
public List<Object> getRegFee(@PathVariable("umr") String umr){
	
	List<Object> list=new ArrayList<Object>();
	
	Map<String, String> map=new HashMap<String, String>();
			
	PatientDetails patientDetails=patientDetailsServiceImpl.findByUmr(umr);
	
	List<PatientPayment> patientPaymentList=new ArrayList<PatientPayment>();
	
	Set<PatientRegistration> patientRegistrations=patientDetails.getvPatientRegistration();
	for(PatientRegistration patientRegistrationsInfo:patientRegistrations) {
		String regId=patientRegistrationsInfo.getRegId();
		
		PatientPayment patientPayment=patientPaymentRepository.findPatientByRegFee(regId, "Reg Fees");
		
		if(patientPayment!=null) {
			patientPaymentList.add(patientPayment);
			
		}
		
	}
	
	int num=patientPaymentList.size();
	if(patientPaymentList.isEmpty()) {
		map.put("regFees", "50");
		
	}else {
		LocalDate todayLocal = LocalDate.now();
		LocalDate prevDate = patientPaymentList.get(num-1).getInsertedDate().toLocalDateTime().toLocalDate();
		Period p = Period.between(prevDate, todayLocal);
		System.out.println(p.getMonths());
		
		int days=(p.getYears()*365)+(p.getMonths()*30)+p.getDays();
		System.out.println(days);
		
		System.out.println(patientPaymentList.get(num-1).getPatientRegistration().getRegId());
		if(days>365) {
			map.put("regFees", "50");

		}else {
			map.put("regFees", "0");
		}
	}
	list.add(map);
	
	return list;
}



@RequestMapping(value = "/payment/excelviewFile/{id}", method = RequestMethod.GET)
public ResponseEntity<Resource> excelUriLink(@PathVariable String id) {

	PatientPaymentPdf patientPaymentPdf = paymentPdfServiceImpl.findById(id);

	return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/x-ms-excel"))
			.header(HttpHeaders.CONTENT_DISPOSITION,
					String.format("inline; filename=\"" + patientPaymentPdf.getFileName() + "\""))
			.body(new ByteArrayResource(patientPaymentPdf.getData()));

}

	/*
	 *  Excel List for Inpatient (ONLY FOR 2 DAYS, 7 DAYS, 15 DAYS, 30 DAYS)
	 */

@RequestMapping(value = "/patient/excel/inpatient/{type}", method = RequestMethod.GET)
public PatientPaymentPdf excelForInpatientDetails(@PathVariable String type) throws IOException {
	return patientDetailsServiceImpl.excelForInpatientDetails(type);

}


/*
 * Excel List for OUTPATIENT (ONLY FOR 2 DAYS, 7 DAYS, 15 DAYS, 30 DAYS)
 */
@RequestMapping(value = "/patient/excel/outPatient/{type}", method = RequestMethod.GET)
public PatientPaymentPdf excelOutpatientList(@PathVariable String type) throws IOException {
	return patientDetailsServiceImpl.excelOutpatientList(type);

}

	/*
	 *  cancellation of patient 
	 */

@RequestMapping(value="/patient/cancel",method = RequestMethod.POST)
public void cancellationOfPatient(@RequestBody Map<String, String> patient) {
	String regNo=patient.get("regNo");
	String reasonForCancel=patient.get("reasonForCancel");
	PatientRegistration patientRegistration=patientRegistrationServiceImpl.findByRegId(regNo);
	patientRegistration.setCancellationFlag(ConstantValues.YES);
	patientRegistration.setReasonForCanncellation(reasonForCancel);
	patientRegistration.setBlockedStatus(true);
	patientRegistrationServiceImpl.save(patientRegistration);
	
	
}


		
}
