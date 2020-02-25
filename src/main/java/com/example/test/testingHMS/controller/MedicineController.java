package com.example.test.testingHMS.controller;

import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.test.testingHMS.pharmacist.dto.MedicianDetailsDto;
import com.example.test.testingHMS.pharmacist.helper.RefMedicineIds;
import com.example.test.testingHMS.pharmacist.model.MedicineDetails;
import com.example.test.testingHMS.pharmacist.repository.MedicineDetailsRepository;
import com.example.test.testingHMS.pharmacist.serviceImpl.MedicineDetailsServiceImpl;

@CrossOrigin(origins="*",maxAge=36000)
@RestController
@RequestMapping("/v1/pharmacist")
public class MedicineController {
	
	public static Logger Logger=LoggerFactory.getLogger(MedicineController.class);
	
	
	@Autowired
	MedicineDetailsServiceImpl  medicineDetailsServiceImpl;
	
	@Autowired
	RefMedicineIds refMedicineIds;
	
	@Autowired
	MedicineDetailsRepository medicineDetailsRepository;
	
	@RequestMapping(value="/medicine/create",method=RequestMethod.GET)
	public RefMedicineIds getMedicineId()
	{
		refMedicineIds.setMedicineId(medicineDetailsServiceImpl.getNextMedId());
		
		return refMedicineIds;
		
	}
	
	
	/*
	 * NOT using this method - Deprecated
	 */
	@RequestMapping(value="/medicine/create",method=RequestMethod.POST)
	public void createMedicine(@RequestBody MedicianDetailsDto medicianDetailsDto,Principal p)
	{
		MedicineDetails medicineDetails=new MedicineDetails();
		BeanUtils.copyProperties(medicianDetailsDto, medicineDetails);
		medicineDetails.setInsertedDate(Timestamp.valueOf(LocalDateTime.now()));
		medicineDetailsServiceImpl.computeSave(medicineDetails,p);
	}
	

	/*
	 * New Method to Add Multiple items
	 */
	@RequestMapping(value="/medicine/listcreate",method=RequestMethod.POST)
	public void createMedicineList(@RequestBody MedicianDetailsDto medicianDetailsDto,Principal p)
	{
		MedicineDetails medicineDetails=new MedicineDetails();
		BeanUtils.copyProperties(medicianDetailsDto, medicineDetails);
		medicineDetails.setInsertedDate(Timestamp.valueOf(LocalDateTime.now()));
		medicineDetailsServiceImpl.computeSaveList(medicineDetails,p);
		
		
	}
	
	@RequestMapping(value="/medicine/update",method=RequestMethod.PUT)
	public void Medicine(@RequestBody MedicineDetails medicineDetails,Principal p)
	{
		MedicineDetails medicineDetailsInfo=medicineDetailsRepository.findByMedicineId(medicineDetails.getMedicineId());
		medicineDetails.setMedicineId(medicineDetails.getMedicineId());
		medicineDetails.setInsertedBy(medicineDetailsInfo.getInsertedBy());
		medicineDetails.setInsertedDate(Timestamp.valueOf(LocalDateTime.now()));
		medicineDetailsRepository.save(medicineDetails);
	
	}
	
	@RequestMapping(value="/medicine/getAll",method=RequestMethod.GET)
	public List<MedicineDetails> getMeddicine()
	{
		return medicineDetailsServiceImpl.findAll();
	}
}
