package com.example.test.testingHMS.controller;

import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.test.testingHMS.patient.model.PatientPaymentPdf;
import com.example.test.testingHMS.patient.model.PatientRegistration;
import com.example.test.testingHMS.pharmacist.dto.SalesReturnDto;
import com.example.test.testingHMS.pharmacist.helper.RefSalesIds;
import com.example.test.testingHMS.pharmacist.model.Location;
import com.example.test.testingHMS.pharmacist.model.Sales;
import com.example.test.testingHMS.pharmacist.model.SalesPaymentPdf;
import com.example.test.testingHMS.pharmacist.model.SalesReturn;
import com.example.test.testingHMS.pharmacist.repository.MedicineDetailsRepository;
import com.example.test.testingHMS.pharmacist.serviceImpl.SalesPaymentPdfServiceImpl;
import com.example.test.testingHMS.pharmacist.serviceImpl.SalesReturnServiceImpl;
import com.example.test.testingHMS.pharmacist.serviceImpl.SalesServiceImpl;
import com.example.test.testingHMS.utils.ConstantValues;

@CrossOrigin(origins="*",maxAge=36000)
@RestController
@RequestMapping("/v1/sales")
public class SalesReturnController 
{
	public static Logger Logger=LoggerFactory.getLogger(SalesReturnController.class);
	
	
	@Autowired
	RefSalesIds refSalesIds;
	
	@Autowired
	MedicineDetailsRepository medicineDetailsRepository;
	
	@Autowired
	SalesServiceImpl salesServiceImpl;
	
	@Autowired
	SalesReturnServiceImpl salesReturnServiceImpl;
	
	@Autowired
	SalesPaymentPdfServiceImpl salesPaymentPdfServiceImpl;
	
	
	@RequestMapping(value="/return/find/{bill}",method=RequestMethod.GET)
	public List<Sales> findBill(@PathVariable String bill)
	{
		List<Sales> sales=salesServiceImpl.findByBillNo(bill);
		for(Sales s:sales)
		{
			
			Date dateAdv = s.getBillDate();
			DateFormat formatterAdv = new SimpleDateFormat("dd-MMM-yyyy");
			String advDate = formatterAdv.format(dateAdv).toString();

			Location location=s.getPatientSaleslocation();
			PatientRegistration patientRegistration= s.getPatientRegistration();
			if(patientRegistration!=null)
			{
				if(patientRegistration.isBlockedStatus())
				{
					throw new RuntimeException("Payment for this patinet is blocked !");
				}
				s.setRegId(patientRegistration.getRegId());
			}
			s.setLocation(location.getLocationName());
			s.setDate(advDate);
			
		}
		return sales;
		
	}
	
	
	
	
	@RequestMapping(value="/return/create",method=RequestMethod.POST)
	public SalesPaymentPdf createRefund(@RequestBody SalesReturnDto dto,Principal principal) 
	{
		SalesReturn salesReturn=new SalesReturn();
		BeanUtils.copyProperties(dto, salesReturn);
		return salesReturnServiceImpl.computeSave(salesReturn,principal);
		
	}
	
	/*
	 * To get payement type of sales
	 */
	@RequestMapping(value="/paymentType/{billNo}",method=RequestMethod.GET)
	public Map<String, String> getType(@PathVariable String billNo)
	{
		List<Sales> sales=salesServiceImpl.findByBillNo(billNo);
		Map<String,String> info=new HashMap<>();
		info.put("Type", sales.get(0).getPaymentType());
		return info;
		
	}
	
	/*
	 *  to display the sales return list
	 */
	
	@RequestMapping(value="/return/list/{days}")
	public List<Object> displayList(@PathVariable String days){
		
		
		return salesReturnServiceImpl.displaySalesReturnList(days);
	}

	/*
	 * To get Sales Return list Pdf 
	 */
	@RequestMapping(value="/return/pdf/{billNo}")
	public Map<String, Object> getSalesPdf(@PathVariable("billNo") String billNo){
		
		List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();
		List<SalesPaymentPdf> salesPaymentPdf=salesPaymentPdfServiceImpl.getReturnPaymentPdfList(billNo);
		Map<String, Object> display=new HashMap<String, Object>();
		int i=0;
		for (SalesPaymentPdf salesPaymentPdfInfo : salesPaymentPdf) {
			String[] data = salesPaymentPdfInfo.getFileName().split(ConstantValues.ONE_SPACE_STRING);
			String id = null;
			String name = "";
			boolean status = true;
			for (String dataInfo : data) {
				if (status) {
					id = dataInfo;
					status = false;
				} else {
					name += dataInfo + ConstantValues.ONE_SPACE_STRING;
				}

			}

			if (display.containsKey(name)) {
				name += i;
				i++;
			}

			display.put(name, salesPaymentPdfInfo.getFileuri());
		}
				
	return display;
	
	}
	
	
	@RequestMapping(value="/ippharmacy/return/{regId}",method=RequestMethod.POST)
	public SalesPaymentPdf createIpPharmacyReturn(@RequestBody SalesReturnDto dto,@PathVariable String regId,Principal principal) 
	{
		SalesReturn salesReturn=new SalesReturn();
		BeanUtils.copyProperties(dto, salesReturn);
		return salesReturnServiceImpl.createIpPharmacyReturn(salesReturn,regId,principal);
		
	}


	
}
