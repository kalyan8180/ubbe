package com.example.test.testingHMS.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.test.testingHMS.voucher.dto.VoucherDto;
import com.example.test.testingHMS.voucher.model.Voucher;
import com.example.test.testingHMS.voucher.model.VoucherPdf;
import com.example.test.testingHMS.voucher.serviceImpl.VoucherPdfServiceImpl;
import com.example.test.testingHMS.voucher.serviceImpl.VoucherServiceImpl;

@CrossOrigin(origins="*",maxAge=360000)
@RequestMapping("/v1")
@RestController
public class VoucherController {
	
	public static Logger Logger=LoggerFactory.getLogger(VoucherController.class);
	
	@Value("${hospital.logo}")
	private Resource hospitalLogo;
	
	@Autowired
	VoucherServiceImpl voucherServiceImpl;
	
	@Autowired
	VoucherPdfServiceImpl voucherPdfServiceImpl;
	
	@RequestMapping(value="/voucher/create",method=RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE)
	public List<Object> getinfo()
	{	
		return voucherServiceImpl.pageLoad();
	}
	
	@RequestMapping(value="/voucher/create",method=RequestMethod.POST)
	public VoucherPdf createVendor(@RequestBody VoucherDto voucherDto,Principal principal)
	{
		Voucher voucher = new Voucher();
		BeanUtils.copyProperties(voucherDto, voucher);
		return voucherServiceImpl.computeSave(voucher,principal);
		
		
	}

	// * List of Voucher (ONLY FOR 2 DAYS, 7 Days,15 Days,30 Days)

	@RequestMapping(value = "/Voucher/{type}", method = RequestMethod.GET)
	public List<Map<String, String>> voucherDetails(@PathVariable String type) {
		return voucherServiceImpl.voucherDetails(type);

	}
	
	@RequestMapping(value="/pdf/{paymentNo}", method = RequestMethod.GET)
	public Map<String, Object> getPdf(@PathVariable("paymentNo") String paymentNo){
		
		Map<String, Object> map=new HashMap<>();
		
		
		VoucherPdf voucherPdf=voucherPdfServiceImpl.getVoucherPdf(paymentNo);
		
		map.put("voucherNo", voucherPdf.getFileuri());
		
		
		return map;
	}
	
	@RequestMapping(value="/voucher/viewFile/{id}",method=RequestMethod.GET)
	public ResponseEntity<Resource> uriLink(@PathVariable String id)
	{
		
		VoucherPdf voucherPdf=voucherPdfServiceImpl.findById(id);
		
		
		 return ResponseEntity.ok()
	        		.contentType(MediaType.parseMediaType("application/pdf"))
	                .header(HttpHeaders.CONTENT_DISPOSITION,String.format("inline; filename=\"" + voucherPdf.getFileName() + "\""))
	                .body(new ByteArrayResource(voucherPdf.getData()));
		
	}

	/*
	 * get all patient
	 */
	@RequestMapping(value = "/voucher/getAll", method = RequestMethod.GET)
	public List<Map<String, String>> getAllvc()
	{
		return voucherServiceImpl.getAllvc();
		
	}

		@RequestMapping(value = "/voucher/updatevoucher/{id}", method = RequestMethod.PUT)
		public  VoucherPdf updateUser(@RequestBody VoucherDto voucherdto, @PathVariable String id,Principal  principal ) {

			// Voucher voucher=new Voucher();
			// BeanUtils.copyProperties(voucherdto, voucher);

			return voucherServiceImpl.updateVoucher(voucherdto, id,principal);
		}
	
	
}