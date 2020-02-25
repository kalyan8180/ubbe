package com.example.test.testingHMS.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.test.testingHMS.pharmacist.model.SalesRefund;
import com.example.test.testingHMS.pharmacist.repository.SalesRefundRepository;
import com.example.test.testingHMS.pharmacist.serviceImpl.SalesRefundServiceImpl;

@CrossOrigin(origins="*",maxAge=36000)
@RestController
@RequestMapping("/v1/sales")
public class SalesRefundController 
{
	
	public static Logger Logger=LoggerFactory.getLogger(SalesRefundController.class);
	
	@Autowired
	SalesRefundRepository salesRefundRepository;
	
	@Autowired
	SalesRefundServiceImpl salesRefundServiceImpl;
	
	@RequestMapping(value="/refund/create",method=RequestMethod.GET)
	public List<SalesRefund> getAllRefund()
	{
		return salesRefundRepository.findAllByOrderByReturnIdDesc();
	}

	@RequestMapping(value="/refund/create",method=RequestMethod.PUT)
	public void approveList(@RequestBody List<SalesRefund> salesRefunds)
	{
		for(SalesRefund salesRefundsList:salesRefunds)
		{
			
			SalesRefund salesRefund=salesRefundServiceImpl.findByReturnId(salesRefundsList.getReturnId());
			salesRefundsList.setSalesRefundPatientRegistration(salesRefund.getSalesRefundPatientRegistration());
			salesRefundsList.setSalesRefundUser(salesRefund.getSalesRefundUser());
			salesRefundsList.setStatus("Approved");
			salesRefundRepository.save(salesRefundsList);
		}
		
		
	}
	
	
}
