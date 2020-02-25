package com.example.test.testingHMS.osp.service;

import java.security.Principal;
import java.util.List;

import com.example.test.testingHMS.osp.model.OspService;
import com.example.test.testingHMS.pharmacist.model.SalesPaymentPdf;

public interface OspServiceService {
	
	public List<Object> pageRefrersh();

	SalesPaymentPdf chargeForOspService(OspService ospService, Principal principal);
	
	public List<Object> findAll();
	
	OspService findOneByBillNo(String id);
	
void deleteByMasterOspServiceId(String masterOspServiceId);
	
OspService findByMasterOspServiceId(String masterOspServiceId);


List<Object> getCancelServices(String billNo);


}
