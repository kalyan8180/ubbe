package com.example.test.testingHMS.patient.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.example.test.testingHMS.patient.model.PatientDetails;
import com.example.test.testingHMS.patient.model.PatientRegistration;
import com.example.test.testingHMS.patient.repository.PatientRegistrationRepository;
import com.example.test.testingHMS.patient.service.PatientRegistrationService;
import com.example.test.testingHMS.user.model.User;


@Service
public class PatientRegistrationServiceImpl implements PatientRegistrationService 
{
	@Autowired
	PatientRegistrationRepository repo;
	
	@Override
	public PatientRegistration save(PatientRegistration patientRegistration)
	{
		return repo.save(patientRegistration);
	}
	
	@Override
	public Optional<PatientRegistration> findById(Long id)
	{
		return repo.findById(id);
	}
	
	@Override
	public void delte(Long id)
	{
		 repo.deleteById(id);
	}
	
	@Override
	public PatientRegistration update(PatientRegistration patientRegistration)
	{
		return repo.save(patientRegistration);
	}

	@Override
	public String getNextRegId() {
		PatientRegistration pr= repo.findFirstByOrderByRegIdDesc();
		String nextReg=null;
		if(pr==null)
		{
			nextReg="PR00000001";
		}
		else
		{
			String lastUmr=pr.getRegId();
			
			int regIntId=Integer.parseInt(lastUmr.substring(3));
			regIntId+=1;
			nextReg="PR"+String.format("%08d",regIntId );
		}
		return nextReg;
	}
	
	public String getNextRegIdIpOp(String prefix) {
		PatientRegistration pr=null;
		String nextReg=null;
		String lastUmr=null;
		int regIntId=0;
		
		if(prefix.equalsIgnoreCase("INPATIENT"))
		{
			pr= repo.findByRegIdIpOp("IP");
			if(pr==null)
			{
				nextReg="IP00000001";
			}
			else
			{
				lastUmr=pr.getRegId();
				
				regIntId=Integer.parseInt(lastUmr.substring(3));
				regIntId+=1;
				nextReg="IP"+String.format("%08d",regIntId );
			}
			return nextReg;
		}
		else if(prefix.equalsIgnoreCase("OUTPATIENT"))
		{
			pr= repo.findByRegIdIpOp("OP");
			if(pr==null)
			{
				nextReg="OP00000001";
			}
			else
			{
				lastUmr=pr.getRegId();
				
				regIntId=Integer.parseInt(lastUmr.substring(3));
				regIntId+=1;
				nextReg="OP"+String.format("%08d",regIntId );
			}
			return nextReg;
		}else if(prefix.equalsIgnoreCase("DAY CARE"))
		{
			pr= repo.findByRegIdIpOp("DC");
			if(pr==null)
			{
				nextReg="DC00000001";
			}
			else
			{
				lastUmr=pr.getRegId();
				
				regIntId=Integer.parseInt(lastUmr.substring(3));
				regIntId+=1;
				nextReg="DC"+String.format("%08d",regIntId );
			}
			return nextReg;
		}else if(prefix.equalsIgnoreCase("EMERGENCY"))
		{
			pr= repo.findByRegIdIpOp("EM");
			if(pr==null)
			{
				nextReg="EM00000001";
			}
			else
			{
				lastUmr=pr.getRegId();
				
				regIntId=Integer.parseInt(lastUmr.substring(3));
				regIntId+=1;
				nextReg="EM"+String.format("%08d",regIntId );
			}
			return nextReg;
		}else if(prefix.equalsIgnoreCase("VIP"))
		{
			pr= repo.findByRegIdIpOp("VIP");
			if(pr==null)
			{
				nextReg="VIP00000001";
			}
			else
			{
				lastUmr=pr.getRegId();
				
				regIntId=Integer.parseInt(lastUmr.substring(3));
				regIntId+=1;
				nextReg="VIP"+String.format("%08d",regIntId );
			}
			return nextReg;
		}
		
		else
		{
			pr= repo.findByRegIdIpOp("PR");
			 nextReg=null;
			if(pr==null)
			{
				nextReg="PR00000001";
			}
			else
			{
				 lastUmr=pr.getRegId();
				
				 regIntId=Integer.parseInt(lastUmr.substring(3));
				regIntId+=1;
				nextReg="PR"+String.format("%08d",regIntId );
			}
			return nextReg;
		}
	}
	
	public List<PatientRegistration> findByVuserD(User user)
	{
		return repo.findByVuserD(user);
	}
	
	public List<PatientRegistration> findByPatientDetails(PatientDetails patientDetails)
	{
		return repo.findByPatientDetails(patientDetails);
	}
	
	public PatientRegistration findLatestReg(Long uid)
	{
		return repo.findLatestReg(uid);
	}
	
	public PatientRegistration findByRegId(String id)
	{
		return repo.findByRegId(id);
	}
	
	public List<PatientRegistration> findAll()
	{
		return repo.findAll();
	}
	
	public List<PatientRegistration> findByPType(String pType) {
		
		return repo.findByPType(pType);
	}

	@Override
	public List<PatientRegistration> findPatient(String time,String type) {
		return repo.findPatient(time,type);
	}

	@Override
	public List<PatientRegistration> findOutPatient(String time, String type) {
		return repo.findOutPatient(time, type);
	}

	@Override
	public List<PatientRegistration> expectOutPatient() {
		return repo.expectOutPatient();
	}
	
	public PatientRegistration patientAlredyExists(long pId,String date)
	{
		return repo.patientAlredyExists(pId, date);
	}
	
	public 	List<PatientRegistration> findOnlyInpatient()
	{
		return repo.findOnlyInpatient();
	}

	@Override
	public List<PatientRegistration> expectOutPatientTwoDays(String twoDayBack, String today) {
		return repo.expectOutPatientTwoDays(twoDayBack, today);
	}

	@Override
	public List<PatientRegistration> onlyOutPatientTwoDays(String twoDayBack, String today) {
		return repo.onlyOutPatientTwoDays(twoDayBack, today);
	}

	@Override
	public List<PatientRegistration> expectOutPatientAllDays() {
		// 
		return repo.expectOutPatientAllDays();
	}

	
	
	@Override
	public List<PatientRegistration> allOutPatientDays() {
		return repo.allOutPatientDays();
	}
	
	@Override
	public List<PatientRegistration> allOtherPatientDays() {
		return repo.allOtherPatientDays();
	}
	
	
	
	@Override
	public List<PatientRegistration> onlyOtherPatientTwoDays(String twoDayBack, String today) {
		return repo.onlyOtherPatientTwoDays(twoDayBack, today);
	}

}
