package com.example.test.testingHMS.controller;


import java.security.Principal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.test.testingHMS.due.helper.DueHelper;
import com.example.test.testingHMS.due.helper.DueServiceImpl;
import com.example.test.testingHMS.pharmacist.model.SalesPaymentPdf;
import com.example.test.testingHMS.pharmacyShopDetails.repository.PharmacyShopDetailsRepository;
import com.example.test.testingHMS.user.model.User;
import com.example.test.testingHMS.user.repository.UserRepository;
import com.example.test.testingHMS.user.serviceImpl.UserServiceImpl;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;

@CrossOrigin(origins = "*", maxAge = 36000)
@RestController
@RequestMapping("/v1/due")
public class DueController {

	public static Logger Logger = LoggerFactory.getLogger(DueController.class);

	@Autowired
	DueServiceImpl dueServiceImpl;
	
	//filter by duetype
	@RequestMapping(value="/create",method=RequestMethod.POST)
	public List<Object> getDueBillsBasedOnType(@RequestBody Map<String, String> mapInfo)
	{
		return dueServiceImpl.getDueBillsBasedOnType(mapInfo);
	}

	//for getting all the due list
	@RequestMapping(value="/get/duelist",method=RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE)
	public List<Object> getInfo()
	{
		return dueServiceImpl.getInfo();
	}
	

	@RequestMapping(value = "/get/{umrNo}")
	public List<Object> getPatientDetails(@PathVariable("umrNo") String umrNo) 
	{
		return dueServiceImpl.getPatientDetails(umrNo);
	}


	@RequestMapping(value = "/duepay/{billNo}", method = RequestMethod.POST)
	public SalesPaymentPdf getPharmacySettlementPdf(@RequestBody DueHelper dueHelper,
			@PathVariable("billNo") String billNo, Principal principal) {
		
		return dueServiceImpl.payDue(dueHelper, billNo, principal);
	}

	
}

	
	

	



