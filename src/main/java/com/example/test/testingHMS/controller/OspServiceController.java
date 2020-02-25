package com.example.test.testingHMS.controller;

import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
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

import com.example.test.testingHMS.finalBilling.model.FinalBilling;
import com.example.test.testingHMS.finalBilling.serviceImpl.FinalBillingServiceImpl;
import com.example.test.testingHMS.osp.model.OspService;
import com.example.test.testingHMS.osp.serviceImpl.OspServiceServiceImpl;
import com.example.test.testingHMS.ospDto.OspServiceDto;
import com.example.test.testingHMS.pharmacist.model.SalesPaymentPdf;
import com.example.test.testingHMS.pharmacist.serviceImpl.SalesPaymentPdfServiceImpl;
import com.example.test.testingHMS.user.model.User;
import com.example.test.testingHMS.user.serviceImpl.UserServiceImpl;
import com.example.test.testingHMS.utils.ConstantValues;

@CrossOrigin(origins = "*", maxAge = 36000)
@RestController
@RequestMapping("/v1/osp")

public class OspServiceController {
	
	public static Logger Logger=LoggerFactory.getLogger(OspServiceController.class);
	
	
	@Autowired
	OspServiceServiceImpl ospServiceServiceImpl;
	
	@Autowired
	SalesPaymentPdfServiceImpl salesPaymentPdfServiceImpl;
	
	
	@Autowired
	UserServiceImpl userServiceImpl;
	
	@Autowired
	FinalBillingServiceImpl finalBillingServcieImpl;
	
	
	@RequestMapping(value="/create")
	List<Object> pageRefresh(){
		
		return ospServiceServiceImpl.pageRefrersh();
	}
	
	
	// * List of ospfilter (ONLY FOR 2 DAYS, 7 Days,) 
	 
		@RequestMapping(value="/ospfilter/{type}",method=RequestMethod.GET)
		public List<Map<String, String>> ospDetails(@PathVariable String type)
		{
			return ospServiceServiceImpl.ospDetails(type);
			
		}
	
	@RequestMapping(value="/getcost",method=RequestMethod.POST)
	
	public Map<String, String> getServiceCost(@RequestBody Map<String, String> map){
		
		String name=map.get("serviceName");
		return ospServiceServiceImpl.getOspServiceCost(name, "OSP");
	}
	
	@RequestMapping(value="/findAll")
	public List<Object> findAll(){
		
		return ospServiceServiceImpl.findAll();
	}
	
	@RequestMapping(value="/create",method=RequestMethod.POST)
	public SalesPaymentPdf computeOspService(@RequestBody OspServiceDto ospServiceDto,Principal principal){
		
		OspService ospService=new OspService();
		BeanUtils.copyProperties(ospServiceDto, ospService);
		
		
		return ospServiceServiceImpl.chargeForOspService(ospService, principal);
		
	}
	
	
	@RequestMapping(value="/pdf/{ospServiceId}")
	public Map<String, Object> getPdf(@PathVariable("ospServiceId") String ospServiceId){
		
		Map<String, Object> map=new HashMap<>();
		
		
		SalesPaymentPdf salesPaymentPdf=salesPaymentPdfServiceImpl.getOspPdf(ospServiceId);
		
		map.put("ospBill", salesPaymentPdf.getFileuri());
		
		
		return map;
	}
	
	/*
	 *  deletion of OSP services 
	 */
	@RequestMapping(value="delete/service/{masterOspServiceId}",method=RequestMethod.DELETE)
	public void deleteOspService(@PathVariable("masterOspServiceId") String masterOspServiceId,Principal principal) {
		
		//for user security
		User user=userServiceImpl.findByUserName(principal.getName());
		
		OspService ospService=ospServiceServiceImpl.findByMasterOspServiceId(masterOspServiceId);
		String paymentType=ospService.getPaymentType();
		
		 FinalBilling finalBilling=new FinalBilling();
		 finalBilling.setBillNo(ospService.getBillNo());
		 finalBilling.setBillType("osp cancellation");
		 if(paymentType.equalsIgnoreCase(ConstantValues.DUE)) {
			 finalBilling.setPaymentType(ConstantValues.DUE);

			 finalBilling.setDueAmount(-ospService.getNetAmount());  
		 }else if(!paymentType.equalsIgnoreCase(ConstantValues.DUE)) {
			 finalBilling.setCashAmount(-ospService.getNetAmount());
			 finalBilling.setPaymentType(ConstantValues.CASH);
		 }
		 finalBilling.setUpdatedBy(user.getUserId());
		 finalBilling.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
		 finalBilling.setInsertedDate(Timestamp.valueOf(LocalDateTime.now()));
		 finalBilling.setFinalAmountPaid(Math.round(-ospService.getNetAmount()));
		 finalBilling.setFinalBillUser(user);
		 finalBilling.setName(ospService.getPatientName());	
		 finalBilling.setRegNo( null);
		 finalBilling.setMobile(ospService.getMobile());
		 finalBilling.setTotalAmount(-ospService.getNetAmount());
		 finalBilling.setUmrNo(null);
		 finalBillingServcieImpl.computeSave(finalBilling);
		ospServiceServiceImpl.deleteByMasterOspServiceId(masterOspServiceId);
	}
	
	@RequestMapping(value="/cancel/find/{billNo}")
	public List<Object> getCancelSErvices(@PathVariable("billNo") String billNo){
		
		return ospServiceServiceImpl.getCancelServices(billNo);
	}

	

}