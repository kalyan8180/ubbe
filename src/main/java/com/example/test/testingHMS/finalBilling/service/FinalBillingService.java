package com.example.test.testingHMS.finalBilling.service;

import java.util.List;

import org.springframework.data.repository.query.Param;

import com.example.test.testingHMS.finalBilling.model.FinalBilling;

public interface FinalBillingService {
	
	void computeSave(FinalBilling finalBilling);
	
	void deleteByBillTypeAndBillNoAndRegNo(String billtype,String billNo,String regNo);
	
	FinalBilling findByBillTypeAndBillNoAndRegNo(String billtype,String billNo,String regNo);
	
	List<FinalBilling> findByRegNo(String regNo);
	
	List<FinalBilling> findDueBills( String billType,String dueBillType,String dueStatus);
	
	List<FinalBilling> findByBillTypeAndBillNo(String billtype,String billNo);
	

}