package com.example.test.testingHMS.taskSchedular;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.test.testingHMS.finalBilling.model.FinalBilling;
import com.example.test.testingHMS.finalBilling.repository.FinalBillingRepository;
import com.example.test.testingHMS.laboratory.model.LaboratoryRegistration;
import com.example.test.testingHMS.laboratory.repository.LaboratoryRegistrationRepository;
import com.example.test.testingHMS.osp.model.OspService;
import com.example.test.testingHMS.osp.repository.OspServiceRepository;
import com.example.test.testingHMS.patient.model.PatientPayment;
import com.example.test.testingHMS.patient.model.PatientRegistration;
import com.example.test.testingHMS.patient.repository.PatientPaymentRepository;
import com.example.test.testingHMS.patient.repository.PatientRegistrationRepository;
import com.example.test.testingHMS.pharmacist.model.Sales;
import com.example.test.testingHMS.pharmacist.repository.SalesRepository;
import com.example.test.testingHMS.user.model.User;
import com.example.test.testingHMS.user.serviceImpl.UserServiceImpl;
import com.example.test.testingHMS.utils.ConstantValues;
import com.example.test.testingHMS.voucher.model.Voucher;
import com.example.test.testingHMS.voucher.repository.VoucherRepository;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010
.account.Message;

@Component
public class WhatsAppMessageSchedular {public static final Logger log = LoggerFactory.getLogger(WhatsAppMessageSchedular.class);
static Timestamp today = null;
public static final String ACCOUNT_SID = "AC704746cd9fd9179c1f740bb71b2f5da1";
public static final String AUTH_TOKEN = "bc7d9ae59b9c5c449721751feafaeeb7";
static Message message = null;
static float totalopamt;
static float totalipamt;
static float totalotheramt;
static float totalsaleamt;
static float totallabamt;
static int totalip;
static int totalop;
static int totallabservices;
static int totalother;
static String id;
static float saleAmt;
static float returnAmt;

static float ospAmt;
static float totalOspAmt;
static float voucherAmt;
static float totalvoucherAmt;
static float  totalCashAmt;
static float totalCardAmt;

//new
static float totalsaledueamtcash;
static float totalsaledueamtcard;
static float totallabdueamtcash;
static float totallabdueamtcard;
static float totalospdueamtcash;
static float totalospdueamtcard;
static float totalsalescash;
static float totalsalescard;
static float totalpatientcash;
static float totalpatientcard;
static float totallabocash;
static float totallabocard;
static float totalospcash;
static float totalospcard;
static float totalipcard;
static float totalipcash;



@Autowired
UserServiceImpl userServiceImpl;

@Autowired
PatientRegistrationRepository patientRegistrationRepository;

@Autowired
LaboratoryRegistrationRepository laboratoryRegistrationRepository;

@Autowired
PatientPaymentRepository patientPaymentRepository;

@Autowired
SalesRepository salesRepository;

@Autowired
OspServiceRepository ospServiceRepository;

@Autowired
VoucherRepository voucherRepository;

@Autowired
FinalBillingRepository finalBillingRepository;

//Cron expression
//is represented
//by six fields:
//
//(*     *      *    *            *       *)
//second,minute,hour,day of month,month,day(s) of week
//
//	* "0 0 * * * *" = the top of every hour of every day.
//	* "*/10 * * * * *" = every ten seconds.
//	* "0 0 8-10 * * *" = 8, 9 and 10 o'clock of every day.
//	* "0 0 8,10 * * *" = 8 and 10 o'clock of every day.
//	* "0 0/30 8-10 * * *" = 8:00, 8:30, 9:00, 9:30 and 10 o'clock every day.
//	* "0 0 9-17 * * MON-FRI" = on the hour nine-to-five weekdays
//	* "0 0 0 25 12 ?" = every Christmas Day at midnight
//
//	(*) means match any
//
//	*/X means "every X"
//
//	? ("no specific value") 
//
//	useful when you need to specify something in one of the two fields in which the character is allowed,
//	but not the other. For emple, iff I want my trigger to fire on a particular day of the month (say, the 10th), 
//	but I don't care what day of the week that happens to be, I would put "10" in the day-of-month field and "?" in the day-of-week field.

@Scheduled(cron = "0 00 21 * * ?")

public void test() {
	executeTask();
}

@Transactional
public void executeTask() {

	totalopamt = 0;
	totalipamt = 0;
	totalotheramt = 0;
	totalsaleamt = 0;
	totallabamt = 0;
	totalip = 0;
	totalop = 0;
	totallabservices = 0;
	totalother = 0;
	float saleamt = 0;
	id = null;
	ospAmt=0;
	totalOspAmt=0;
	voucherAmt=0;
	totalvoucherAmt=0;
	 totalCashAmt=0;
	 totalCardAmt=0;

	 
	 //new
	 
	  totalsaledueamtcash=0;
		 totalsaledueamtcard=0;
		 totallabdueamtcash=0;
		 totallabdueamtcard=0;
		  totalospdueamtcash=0;
		 totalospdueamtcard=0;
		  totalsalescash=0;
		 totalsalescard=0;
		 totalpatientcash=0;
		 totalpatientcard=0;
		 totallabocash=0;
		  totallabocard=0;
		 totalospcash=0;
		totalospcard=0;
		 totalipcard=0;
		 totalipcash=0;

	
	String todayDay= Timestamp.valueOf(LocalDateTime.now()).toString().substring(0, 10);
	String prevDay=LocalDate.parse(todayDay).plusDays(-1).toString();
	
	Timestamp todayDate=Timestamp.valueOf(todayDay+" "+"21:00:00");
	Timestamp prevDate=Timestamp.valueOf(prevDay+" "+"20:59:59");
	
		
		Date date = Calendar.getInstance().getTime();
	DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	String today = formatter.format(date).substring(0, 10);
	// String today="2019-04-02";
	System.out.println("TODAYDATE -----------------------" + today);
	
	
	Date dateTime = Calendar.getInstance().getTime();
	DateFormat formatterTime = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
	String todayTime = formatterTime.format(dateTime).toString();

	
	Iterable<User> user = userServiceImpl.findByRole("DOCTOR");


		float opamt = 0;
		float ipamt = 0;
		float otheramt = 0;
		float labamt = 0;
		int ip = 0;
		int op = 0;
		int labservices = 0;
		int other = 0;
		float totalSale=0;
				
		List<PatientRegistration> OutPatientRegistration = patientRegistrationRepository.onlyOutPatientTwoDays(prevDate.toString(), todayDate.toString());
				
		System.out.println("PATIENT REGISTERATION ENTERING.............");
		op = OutPatientRegistration.size();
		System.out.println("-----outpatientlist" + op);
		
		for (PatientRegistration reg : OutPatientRegistration) {
			System.out.println("shilpi");
			System.out.println(reg.getRegId());
			Set<PatientPayment> pay = reg.getPatientPayment();
					
			if (!pay.isEmpty()) {
				for (PatientPayment payment : pay) {
					opamt += payment.getAmount();
				}
			}
		}
		
		System.out.println("-----outpatientlistAmount" + opamt);
		
		List<PatientRegistration> InpatientRegistration = patientRegistrationRepository.expectOutPatientTwoDays(prevDate.toString(), todayDate.toString());
				
		ip = InpatientRegistration.size();
		System.out.println("-----Inpatientlist" + ip);
		for (PatientRegistration reg : InpatientRegistration) {
			System.out.println("shilpi");
			System.out.println(reg.getRegId());
			Set<PatientPayment> pay = reg.getPatientPayment();
					
			if (!pay.isEmpty()) {
				for (PatientPayment payment : pay) {
					ipamt += payment.getAmount();
				}
			}

		}
		
		System.out.println("-----InpatientlistAmt" + ipamt);

		List<PatientRegistration> patientRegistrationother = patientRegistrationRepository.onlyOtherPatientTwoDays(prevDate.toString(), todayDate.toString());
				
		other = patientRegistrationother.size();
		System.out.println("-----otherpatientlist" + other);
		for (PatientRegistration reg : patientRegistrationother) {
			System.out.println("shilpi");
			System.out.println(reg.getRegId());
			Set<PatientPayment> pay = reg.getPatientPayment();
					
			if (!pay.isEmpty()) {
				for (PatientPayment payment : pay) {

					otheramt += payment.getAmount();
				}
			}

		}
		System.out.println("-----otherpatientlistAmt" + otheramt);
		
		
		List<FinalBilling> finalBillings=finalBillingRepository.findDailyStats(prevDate, todayDate);//totallabamt
		
		for(FinalBilling finalBillingsInfo:finalBillings) {
			totalCashAmt+=finalBillingsInfo.getCashAmount();
			totalCardAmt+=finalBillingsInfo.getCardAmount();
			
			if(finalBillingsInfo.getBillType().equalsIgnoreCase("Laboratory Registration")) {
				totallabamt+=(finalBillingsInfo.getCashAmount()+finalBillingsInfo.getCardAmount()+finalBillingsInfo.getChequeAmount());
			}
			if(finalBillingsInfo.getBillType().equalsIgnoreCase("Sales")||finalBillingsInfo.getBillType().equalsIgnoreCase(ConstantValues.SALES_DUE)) {
				saleamt+=(finalBillingsInfo.getCashAmount()+finalBillingsInfo.getCardAmount()+finalBillingsInfo.getChequeAmount());
			}if(finalBillingsInfo.getBillType().equalsIgnoreCase("Sales Return")||finalBillingsInfo.getBillType().equalsIgnoreCase("Ip Sales Return")) {
				returnAmt+=(finalBillingsInfo.getCashAmount()+finalBillingsInfo.getCardAmount()+finalBillingsInfo.getChequeAmount());
			}

			
		}
		
		totalsaleamt=saleamt-returnAmt;
		

		/*
		 * List<LaboratoryRegistration> patientRegistrationlabservicescount =
		 * laboratoryRegistrationRepository.labDailyStats(prevDate, todayDate);
		 * 
		 * labservices = patientRegistrationlabservicescount.size();
		 * System.out.println("-----LabserviceslistCount" + labservices);
		 * 
		 * for (LaboratoryRegistration labreg : patientRegistrationlabservicescount) {
		 * System.out.println("shilpi"); System.out.println(labreg.getRefferedById());
		 * System.out.println(labreg.getReg_id());
		 * System.out.println(labreg.getLaboratoryPatientRegistration().getRegId());
		 * 
		 * System.out.println("checklabamt..."); System.out.println(labreg.getPrice());
		 * labamt += labreg.getPrice(); System.out.println("-----LabAmtinloop" +
		 * labamt);
		 * 
		 * }
		 */		
		
		System.out.println("-----LabAmt" + labamt);
		System.out.println("-----outpatientlist" + op);
		System.out.println("-----inpatient list" + ip);
		System.out.println("-----otherpatientlist" + other);
		System.out.println("-----LabserviceslistCount" + labservices);
		totalop += op;
		totalip += ip;
		totalother += other;
		totallabservices += labservices;
		totalipamt += ipamt;
		totalopamt += opamt;
		totalotheramt += otheramt;
		System.out.println("ERRORPoint");
		System.out.println(labamt);
		System.out.println(totallabamt);
		totallabamt += labamt;
		System.out.println(totallabamt);

	

		/*
		 * List<Sales> sales = salesRepository.findTheDailyStats(prevDate, todayDate);
		 * 
		 * if (!sales.isEmpty()) { for (Sales sale : sales) { saleamt +=
		 * sale.getAmount(); }
		 * 
		 * } System.out.println("-----SaleAmt" + saleamt); totalsaleamt += saleamt;
		 * 
		 * int toatSale=(int) (totalsaleamt);
		 */
	List<OspService> ospServices=ospServiceRepository.findDailyStats(prevDate, todayDate);
	if(!ospServices.isEmpty()) {
		
		for(OspService ospServicesInfo:ospServices) {
			ospAmt+=ospServicesInfo.getNetAmount();
			
		}
	}
	
	totalOspAmt+=ospAmt;
	List<Voucher> vouchers=voucherRepository.findDailyStats(prevDate, todayDate);
	if(!vouchers.isEmpty()) {
		for(Voucher vouchersInfo:vouchers ) {
			voucherAmt+=vouchersInfo.getVoucherAmount();
		}
		
	}
	totalvoucherAmt+=voucherAmt;
	
	
	System.out.println("-----finalcount----");
	System.out.println("-----outpatientlist" + totalop);
	System.out.println("-----outpatientlistAmt" + totalopamt);
	System.out.println("-----inpatient list" + totalip);
	System.out.println("-----inpatient listAmt" + totalipamt);
	System.out.println("-----otherpatientlist" + totalother);
	System.out.println("-----otherpatientlistAmt" + totalotheramt);
	System.out.println("-----LabserviceslistCount" + totallabservices);
	System.out.println("-----LabserviceslistAmt" + totallabamt);
	System.out.println("-----totalSaleAmt" + totalsaleamt);
	System.out.println("-----totalOspAmt" + totalOspAmt);
	System.out.println("-----totalvoucherAmt" + totalvoucherAmt);
	
	
	//new code
	for(FinalBilling finalBillingsInfo:finalBillings) {
	
		if(finalBillingsInfo.getBillType().equalsIgnoreCase("Sales Due Settlement")) {
			totalsaledueamtcash+=(finalBillingsInfo.getCashAmount());
			totalsaledueamtcard+=(finalBillingsInfo.getCardAmount());
			
		}
		
		if(finalBillingsInfo.getBillType().equalsIgnoreCase("Lab Due Settlement")) {
			totallabdueamtcash+=(finalBillingsInfo.getCashAmount());
			totallabdueamtcard+=(finalBillingsInfo.getCardAmount());
			
		}
		
		if(finalBillingsInfo.getBillType().equalsIgnoreCase("Osp Due Settlememt")) {
			totalospdueamtcash+=(finalBillingsInfo.getCashAmount());
			totalospdueamtcard+=(finalBillingsInfo.getCardAmount());
			
		}
		
		if(finalBillingsInfo.getBillType().equalsIgnoreCase("Sales")) {
			totalsalescash+=(finalBillingsInfo.getCashAmount());
			totalsalescard+=(finalBillingsInfo.getCardAmount());
			
		}
	
		if(finalBillingsInfo.getBillType().equalsIgnoreCase("Patient Registration")) {
			totalpatientcash+=(finalBillingsInfo.getCashAmount());
			totalpatientcard+=(finalBillingsInfo.getCardAmount());
			
		}
		
		if(finalBillingsInfo.getBillType().equalsIgnoreCase("Laboratory Registration")) {
			totallabocash+=(finalBillingsInfo.getCashAmount());
			totallabocard+=(finalBillingsInfo.getCardAmount());
			
		}
		
		
		if(finalBillingsInfo.getBillType().equalsIgnoreCase("Osp Bill")) {
			totalospcash+=(finalBillingsInfo.getCashAmount());
			totalospcard+=(finalBillingsInfo.getCardAmount());
			
		}
		
		if(finalBillingsInfo.getBillType().equalsIgnoreCase("Ip Final Billing")) {
			totalipcash+=(finalBillingsInfo.getCashAmount());
			totalipcard+=(finalBillingsInfo.getCardAmount());
			
		}
	
		
	}
	
	
	totalCardAmt=totalsaledueamtcard+totallabdueamtcard+totalospdueamtcard+totalsalescard+totalpatientcard+totallabocard+totalospcard+totalipcard;
	totalCashAmt=totalsaledueamtcash+totallabdueamtcash+totalospdueamtcash+totalsalescash+totalpatientcash+totallabocash+totalospcash+totalipcash;
	
	
	
	
//	Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
	List<String> phno = Arrays.asList("9380175628","9703601903","7899242777","9248004412");
	
	phno.forEach((s) -> {
	
	// for sms new registration
			try {

				String smsTodayTime=todayTime.replace(" ", "%20");
				String msg = 	"Hi%20Dr%20," + "%20" + "Here%20are%20the%20statistics%20as"+"%20%20%20%20%20"+"on" + smsTodayTime + "%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20" + "O/P%20Patients%20-%20" + totalop
						+ "/" + "Rs" + totalopamt + "%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20" + "I/P%20Patients%20-%20" + totalip + "/" + "Rs" + totalipamt
						+ "%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20" + "Lab%20Services%20value%20-%20" + "Rs" + totallabamt + "%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20"
					+ "Pharmacy%20Sales%20-%20" + "Rs" + totalsaleamt + "%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20"+"OSP%20Value%20-%20" + "Rs" + totalOspAmt+"%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20"+"Voucher%20Payments%20-%20" + "Rs" + totalvoucherAmt+"%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20" + "OTHER%20Patients%20-%20" + totalother
					+ "/" + "Rs" + totalotheramt+"%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20"+"Total%20Cash%20Value%20-%20" + "Rs" + totalCashAmt+"%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20"+
					"Total%20Card%20Value%20-%20" + "Rs" + totalCardAmt;
				
				URL url = new URL("http://203.212.70.200/smpp/sendsms?username=udbavaapi&password=udbavaapi123&to="+s+"&udh=0&from=UDBAVA&text="+msg);
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
					System.out.println("response is sent to this" + response);
				}
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
	
	});
	/*phno.forEach((s) -> {
		message = Message.creator(new com.twilio.type.PhoneNumber("whatsapp:" + "+" + s),
				new com.twilio.type.PhoneNumber("whatsapp:+14155238886"),
				"Hi Dr ," + "\n" + "Here are the statistics as"+"\n"+" on" + todayTime + "\n" + "O/P Patients - " + totalop
						+ "/" + "Rs" + totalopamt + "\n" + "I/P Patients - " + totalip + "/" + "Rs" + totalipamt
						+ "\n" + "Lab Services - " + totallabservices + "/" + "Rs" + totallabamt + "\n"
					+ "Pharmacy Sales - " + "Rs" + toatSale + "\n"+"OSP Value - " + "Rs" + totalOspAmt+"\n"+"Voucher Payments - " + "Rs" + totalvoucherAmt+"\n" + "OTHER Patients - " + totalother
					+ "/" + "Rs" + totalotheramt).create();
						
				
					
	});*/

}}