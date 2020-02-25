package com.example.test.testingHMS.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.test.testingHMS.due.helper.DueHelper;
import com.example.test.testingHMS.due.helper.PaymentUpdateServiceImpl;

@CrossOrigin(origins = "*", maxAge = 36000)
@RestController
@RequestMapping("/v1")
public class PaymentUpdateController {
	
	@Autowired
	PaymentUpdateServiceImpl paymentUpdateServiceImpl;

	
	// filter by billno
	@RequestMapping(value = "/billno/billtype", method = RequestMethod.POST)
	public List<Object> getperticularbill(@RequestBody Map<String, String> mapInfo) {

		return paymentUpdateServiceImpl.getperticularbill(mapInfo);
	}
	
	/*
	 *  update of all payment types like cash to card vice versa 
	 */
	
	@RequestMapping(value = "/update/payment/{billNo}", method = RequestMethod.PUT)
	public void updateAll( @RequestBody DueHelper dueHelper,
	@PathVariable("billNo") String billNo, Principal principal) {
		paymentUpdateServiceImpl.updateAll(dueHelper, billNo, principal);
	}
}