package com.example.test.testingHMS.pharmacist.serviceImpl;

import java.sql.Timestamp;
import java.util.List;

import org.bouncycastle.jce.provider.JCEKeyGenerator.Salsa20;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.test.testingHMS.patient.model.PatientDetails;
import com.example.test.testingHMS.patient.serviceImpl.PatientDetailsServiceImpl;
import com.example.test.testingHMS.pharmacist.helper.RefSalesReturn;
import com.example.test.testingHMS.pharmacist.model.Location;
import com.example.test.testingHMS.pharmacist.model.Sales;
import com.example.test.testingHMS.pharmacist.model.SalesRefund;
import com.example.test.testingHMS.pharmacist.model.SalesReturn;
import com.example.test.testingHMS.pharmacist.repository.SalesRefundRepository;
import com.example.test.testingHMS.pharmacist.repository.SalesRepository;
import com.example.test.testingHMS.pharmacist.repository.SalesReturnRepository;
import com.example.test.testingHMS.pharmacist.service.SalesRefundService;
import com.example.test.testingHMS.pharmacist.service.SalesReturnService;
@Service
public class SalesRefundServiceImpl implements SalesRefundService {
	
	@Autowired
	SalesReturnRepository salesReturnRepository;
	
	@Autowired
	SalesServiceImpl salesServiceImpl;
	
	@Autowired
	SalesRepository salesRepository;
	
	@Autowired
	SalesRefundRepository salesRefundRepository;
	
	@Autowired
	MedicineDetailsServiceImpl medicineDetailsServiceImpl;
	
	@Autowired
	PatientDetailsServiceImpl patientDetailsServiceImpl;
	
	@Autowired
	LocationServiceImpl locationServiceImpl;

	@Override
	public String getNextReturnId() {
		SalesRefund salesRefund=salesRefundRepository.findFirstByOrderByReturnIdDesc();
		String nextId=null;
		if(salesRefund==null)
		{
			nextId="RS0000001";
		}
		else
		{
			int nextIntId=Integer.parseInt(salesRefund.getReturnId().substring(2));
			nextIntId+=1;
			nextId="RS"+String.format("%07d", nextIntId);
		}
		return nextId;
	}

	public SalesRefund findByReturnId(String id)
	{
		return salesRefundRepository.findByReturnId(id);
	}

	
	

}
