package com.example.test.testingHMS.pharmacist.service;

import com.example.test.testingHMS.pharmacist.model.SalesRefund;
import com.example.test.testingHMS.pharmacist.model.SalesReturn;

public interface SalesRefundService 
{
	String getNextReturnId();
	
	SalesRefund findByReturnId(String id);
	
}
