package com.example.test.testingHMS.taskSchedular;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.test.testingHMS.bed.model.RoomBookingDetails;
import com.example.test.testingHMS.bed.serviceImpl.RoomBookingDetailsServiceImpl;
import com.example.test.testingHMS.bill.model.ChargeBill;
import com.example.test.testingHMS.bill.serviceImpl.ChargeBillServiceImpl;
import com.example.test.testingHMS.laboratory.model.LabServices;
import com.example.test.testingHMS.patient.model.PatientPayment;
import com.example.test.testingHMS.patient.model.PatientRegistration;
import com.example.test.testingHMS.patient.serviceImpl.PatientPaymentServiceImpl;
import com.example.test.testingHMS.patient.serviceImpl.PatientRegistrationServiceImpl;
import com.example.test.testingHMS.user.model.User;

@Component
public class TaskSchedular {
	
	final static Logger log=Logger.getLogger(TaskSchedular.class);
	static SimpleDateFormat format=null;
	static Timestamp today=null;
	static int costSoFar=0;
	static long costPerDay =0;
	static List<String> containsList=new ArrayList<>();
	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss.a");
	static boolean flag; 
	
	
	
	
	@Autowired
	PatientRegistrationServiceImpl patientRegistrationServiceImpl;
	
	@Autowired
	ChargeBillServiceImpl chargeBillServiceImpl; 
	
	@Autowired
	RoomBookingDetailsServiceImpl 	roomBookingDetailsServiceImpl;
	
	@Autowired
	PatientPaymentServiceImpl patientPaymentServiceImpl; 
	
	
	@Transactional
	//@Scheduled(fixedRate=300000) //15 min
	public void executeTask()
	{
		format=new SimpleDateFormat("HH:mm:ss");
		today=Timestamp.valueOf(LocalDateTime.now());
		
		
		String triggerHour=dateTimeFormatter.format(LocalDateTime.now());
    	int todayMin=Integer.parseInt(triggerHour.substring(3, 5));
    	int todayHour=Integer.parseInt(triggerHour.substring(0, 2));
    	
    	int min=Integer.parseInt("52");
    	String hr="23";
    	
    	if(hr.equals(String.valueOf(todayHour)) && todayMin>min)
    	{
    		if(!flag)
    		{
    		flag=true;
    		containsList=new ArrayList<>();
    		}
    		
    	}
    	else
    	{
    		flag=false;
    	}
		
		
  	List<PatientRegistration> patientList =	patientRegistrationServiceImpl.findOnlyInpatient();
		
    //	PatientRegistration patientListInfo =patientRegistrationServiceImpl.findByRegId("PR00000002");
		try
		{
		if(patientList!=null)
		{
				for(PatientRegistration patientListInfo:patientList)
				{
				
				if(patientListInfo.getTriggeredDate()!=null)
				{
					System.out.println("1----------------Entered triggered Function");
				if(!containsList.contains(patientListInfo.getRegId()))
				{
					System.out.println("2----------------Entered Contains list");
					String patIssuedDate=patientListInfo.getTriggeredDate().toString().substring(0,10);
					String todaysDate=Timestamp.valueOf(LocalDateTime.now()).toString().substring(0,10);
					if(!patIssuedDate.equals(todaysDate))
					{
						System.out.println("3----------------Entered patIssue date not equal to todays date");
						//Using issued_at field
						Timestamp issuedDate=	 patientListInfo.getTriggeredDate();
						
						Timestamp regDate=	 patientListInfo.getRegDate();
						
						SimpleDateFormat form=new SimpleDateFormat("yyyy-MM-dd");
						
						SimpleDateFormat timeFormat=new SimpleDateFormat("HH:mm:ss");
						
						// Calculating patient previous date using issue_date
						Calendar cal=Calendar.getInstance();
						cal.setTimeInMillis(issuedDate.getTime());
					//	cal.add(Calendar.DATE, 0);
						String prevDate= String.valueOf(form.format(cal.getTime()));
						
						//Calculating time for patient
						Date dateFor=new Date(issuedDate.getTime());
				    	String patTimeValue= timeFormat.format(dateFor);
						String patTimeHour=patTimeValue.substring(0,2);
						int patTimeMin=Integer.valueOf(patTimeValue.substring(3,5));
						
				    	//Calculating current time 
				    	dateFor=new Date(today.getTime());
				    	String todayTimeValue=timeFormat.format(dateFor);
				    	String todayTimeHour=todayTimeValue.substring(0,2);
				    	int todayTimeMin=Integer.valueOf(todayTimeValue.substring(3,5));
				    	
						//Calculating current previous date
						 cal=Calendar.getInstance();
						cal.setTimeInMillis(today.getTime());
						cal.add(Calendar.DATE, -1);
						String currentPrevDate= String.valueOf(form.format(cal.getTime()));
						
						//calculating period b/w issued_date and current date
						LocalDate patientDate=issuedDate.toLocalDateTime().toLocalDate();
						LocalDate todayDate=LocalDate.now();
						Period period=Period.between(patientDate, todayDate);
						
						
						//calculating period b/w issued_date and registeredDate
						LocalDate patientIssuedDate=issuedDate.toLocalDateTime().toLocalDate();
						LocalDate patientRegDate=regDate.toLocalDateTime().toLocalDate();
						Period regPeriod=Period.between(patientRegDate, patientIssuedDate);
						
						
						
						long fee=0;
						String roomType = "";
		
						
					
						if(prevDate.equalsIgnoreCase(currentPrevDate) && todayTimeHour.equalsIgnoreCase(patTimeHour) && todayTimeMin>patTimeMin)
						{

							RoomBookingDetails roomBookingDetails= roomBookingDetailsServiceImpl.findByPatientRegistrationBooking(patientListInfo);
							if(roomBookingDetails!=null)
							{
								 costSoFar=roomBookingDetails.getCostSoFar();
								 costPerDay = roomBookingDetails.getRoomDetails().getCostPerDay();
								 roomType=roomBookingDetails.getRoomDetails().getRoomType();
								roomBookingDetails.setCostSoFar(costSoFar+(int)costPerDay);
							}
						
							Set<PatientPayment> patientPayment=patientPaymentServiceImpl.findByPatientRegistration(patientListInfo);
							for(PatientPayment patientPaymentInfo:patientPayment)
							{
								
								if(patientPaymentInfo.getTypeOfCharge().equalsIgnoreCase("Doctor fee"))
								{
									
									User doctor=patientListInfo.getVuserD();
									if(roomType.equalsIgnoreCase("doublesharing"))
									{
										fee=doctor.getDoctorDetails().getIpDs();
										patientPaymentInfo.setAmount(patientPaymentInfo.getAmount()+fee);
										patientListInfo.setTriggeredDate(Timestamp.valueOf(LocalDateTime.now()));
										patientRegistrationServiceImpl.save(patientListInfo);
					
									}
									else if(roomType.equalsIgnoreCase("General ward-male"))
									{
										fee=doctor.getDoctorDetails().getIpGenMale();
										patientPaymentInfo.setAmount(patientPaymentInfo.getAmount()+fee);
										patientListInfo.setTriggeredDate(Timestamp.valueOf(LocalDateTime.now()));
										patientRegistrationServiceImpl.save(patientListInfo);
									}
									else if(roomType.equalsIgnoreCase("General ward-female"))
									{
										fee=doctor.getDoctorDetails().getIpGenFemale();
										patientPaymentInfo.setAmount(patientPaymentInfo.getAmount()+fee);
										patientListInfo.setTriggeredDate(Timestamp.valueOf(LocalDateTime.now()));
										patientRegistrationServiceImpl.save(patientListInfo);
									}
									else if(roomType.equalsIgnoreCase("emergency"))
									{
										fee=doctor.getDoctorDetails().getIpEmergency();
										patientPaymentInfo.setAmount(patientPaymentInfo.getAmount()+fee);
										patientListInfo.setTriggeredDate(Timestamp.valueOf(LocalDateTime.now()));
										patientRegistrationServiceImpl.save(patientListInfo);
									}
									else if(roomType.equalsIgnoreCase("DayCare"))
									{
										fee=doctor.getDoctorDetails().getIpDayCare();
										patientPaymentInfo.setAmount(patientPaymentInfo.getAmount()+fee);
										patientListInfo.setTriggeredDate(Timestamp.valueOf(LocalDateTime.now()));
										patientRegistrationServiceImpl.save(patientListInfo);
									}
									else if(roomType.equalsIgnoreCase("Single Sharing"))
									{
										fee=doctor.getDoctorDetails().getIpSs();
										patientPaymentInfo.setAmount(patientPaymentInfo.getAmount()+fee);
										patientListInfo.setTriggeredDate(Timestamp.valueOf(LocalDateTime.now()));
										patientRegistrationServiceImpl.save(patientListInfo);
									}
									else if(roomType.equalsIgnoreCase("NICU"))
									{
										fee=doctor.getDoctorDetails().getIpNicu();
										patientPaymentInfo.setAmount(patientPaymentInfo.getAmount()+fee);
										patientListInfo.setTriggeredDate(Timestamp.valueOf(LocalDateTime.now()));
										patientRegistrationServiceImpl.save(patientListInfo);
									}
									else if(roomType.equalsIgnoreCase("Adult icu"))
									{
										fee=doctor.getDoctorDetails().getIpAicu();
										patientPaymentInfo.setAmount(patientPaymentInfo.getAmount()+fee);
										patientListInfo.setTriggeredDate(Timestamp.valueOf(LocalDateTime.now()));
										patientRegistrationServiceImpl.save(patientListInfo);									
									}
									else if(roomType.equalsIgnoreCase("picu"))
									{
										fee=doctor.getDoctorDetails().getIpPicu();
										patientPaymentInfo.setAmount(patientPaymentInfo.getAmount()+fee);
										patientListInfo.setTriggeredDate(Timestamp.valueOf(LocalDateTime.now()));
										patientRegistrationServiceImpl.save(patientListInfo);									
									}
									else if(roomType.equalsIgnoreCase("isolation"))
									{
										fee=doctor.getDoctorDetails().getIpIsolation();
										patientPaymentInfo.setAmount(patientPaymentInfo.getAmount()+fee);
										patientListInfo.setTriggeredDate(Timestamp.valueOf(LocalDateTime.now()));
										patientRegistrationServiceImpl.save(patientListInfo);
									}
									
									
								}
							
								
							}
							
							List<ChargeBill> chargeBills=chargeBillServiceImpl.findByPatRegId(patientListInfo);
							
							for(ChargeBill chargeBillInfo:chargeBills)
							{
								LabServices labService=chargeBillInfo.getServiceId();
								if(labService!=null)
								{
									float prevAmount=chargeBillInfo.getAmount();
									long qty=chargeBillInfo.getQuantity();
									if(labService.getServiceName().equalsIgnoreCase("GENERAL WARD DMO CHARGES"))
									{
										chargeBillInfo.setAmount(prevAmount+labService.getCost());
										chargeBillInfo.setQuantity(qty++);
									}
									else if(labService.getServiceName().equalsIgnoreCase("GENERAL WARD NURSING CHARGES"))
									{
										chargeBillInfo.setAmount(prevAmount+labService.getCost());
										chargeBillInfo.setQuantity(qty++);
									}
								
									chargeBillServiceImpl.save(chargeBillInfo);
								}
								
							}
							containsList.add(patientListInfo.getRegId());
						}
						
						
					}
					else
					{
						containsList.add(patientListInfo.getRegId());
					}
					
				}
				
			
				}

					
				}
				
				
		}
		}
		catch(Exception e)
		{
			log.error(e.getMessage());
		}
		
	}

}
