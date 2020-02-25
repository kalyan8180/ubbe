package com.example.test.testingHMS.finalBilling.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.test.testingHMS.finalBilling.model.FinalBilling;
import com.example.test.testingHMS.finalBilling.repository.FinalBillingRepository;
import com.example.test.testingHMS.finalBilling.service.FinalBillingService;

@Service
public class FinalBillingServiceImpl implements FinalBillingService {

	@Autowired
	FinalBillingRepository finalBillingRepository;
	
	@Override
	public void computeSave(FinalBilling finalBilling) {
		finalBillingRepository.save(finalBilling);
	}

	@Override
	public FinalBilling findByBillTypeAndBillNoAndRegNo(String billtype, String billNo, String regNo) {
		return finalBillingRepository.findByBillTypeAndBillNoAndRegNo(billtype, billNo, regNo);
	}

	@Override
	@Transactional
	public void deleteByBillTypeAndBillNoAndRegNo(String billtype, String billNo, String regNo) {
		finalBillingRepository.deleteByBillTypeAndBillNoAndRegNo(billtype, billNo, regNo);		
	}

	@Override
	public List<FinalBilling> findByRegNo(String regNo) {
		return finalBillingRepository.findByRegNo(regNo);
	}

	
	@Override
	public List<FinalBilling> findByBillTypeAndBillNo(String billtype, String billNo) {
		return finalBillingRepository.findByBillTypeAndBillNo(billtype, billNo);
	}

	@Override
	public List<FinalBilling> findDueBills(String billType, String dueBillType, String dueStatus) {
		
		return finalBillingRepository.findDueBills(billType, dueBillType, dueStatus);
	}



}
