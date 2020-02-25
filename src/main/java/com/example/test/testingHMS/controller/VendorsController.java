package com.example.test.testingHMS.controller;

import java.util.List;

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

import com.example.test.testingHMS.pharmacist.dto.VendorsDto;
import com.example.test.testingHMS.pharmacist.helper.RefVendorIds;
import com.example.test.testingHMS.pharmacist.model.Vendors;
import com.example.test.testingHMS.pharmacist.serviceImpl.VendorsServiceImpl;

@CrossOrigin(origins="*",maxAge=36000)
@RestController
@RequestMapping("/v1/pharmacist")
public class VendorsController {
	
	public static Logger Logger=LoggerFactory.getLogger(VendorsController.class);
	
	
	@Autowired
	VendorsServiceImpl vendorsServiceImpl;
	
	@Autowired
	RefVendorIds refVendorIds;
	
	@RequestMapping(value="/vendor/create",method=RequestMethod.GET)
	public RefVendorIds getVendorIds()
	{
		refVendorIds.setVendorId(vendorsServiceImpl.getNextVendorId());
		return refVendorIds;
	}
	
	
	@RequestMapping(value="/vendor/create",method=RequestMethod.POST)
	public void createVendor(@RequestBody VendorsDto vendorsDto)
	{
		Vendors vendors=new Vendors();
		BeanUtils.copyProperties(vendorsDto, vendors);
		vendorsServiceImpl.computeSave(vendors);
		
		
	}
	
	
	@RequestMapping(value="/vendor/update/{id}",method=RequestMethod.PUT)
	public void updateVendor(@RequestBody VendorsDto vendorsDto,@PathVariable String id)
	{
		Vendors vendors=new Vendors();
		BeanUtils.copyProperties(vendorsDto, vendors);
		vendors.setVendorId(id);
		vendorsServiceImpl.computeUpdate(vendors);
	}
	
	@RequestMapping(value="/vendor/getAll",method=RequestMethod.GET)
	public List<Vendors> getVendors()
	{
		return vendorsServiceImpl.findAllOrderByVendorId();
	}
}
