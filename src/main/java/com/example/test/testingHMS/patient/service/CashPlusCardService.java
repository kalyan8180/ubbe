package com.example.test.testingHMS.patient.service;

import com.example.test.testingHMS.patient.model.CashPlusCard;

public interface CashPlusCardService {
	
	CashPlusCard save(CashPlusCard cashPlusCard);
	
	CashPlusCard findByBillNo(String billNo);

}
