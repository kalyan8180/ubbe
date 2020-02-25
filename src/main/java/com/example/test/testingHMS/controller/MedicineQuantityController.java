package com.example.test.testingHMS.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.test.testingHMS.pharmacist.model.MedicineQuantity;
import com.example.test.testingHMS.pharmacist.repository.MedicineDetailsRepository;
import com.example.test.testingHMS.pharmacist.repository.MedicineQuantityRepository;
import com.example.test.testingHMS.pharmacist.serviceImpl.MedicineQuantityServiceImpl;

@CrossOrigin(origins="*",maxAge=36000)
@RestController
@RequestMapping("/v1/quantity")
public class MedicineQuantityController {
	
	public static Logger Logger=LoggerFactory.getLogger(MedicineQuantityController.class);
	

	@Autowired
	MedicineQuantityServiceImpl medicineQuantityServiceImpl;
	
	@Autowired
	MedicineQuantityRepository medicineQuantityRepository;

	@Autowired
	MedicineDetailsRepository medicineDetailsRepository;
	
			
	@GetMapping(value = "/value")
	public List<Object> getMedicineQuantity() {

		List<Object> list = new ArrayList<>();
		

		List<MedicineQuantity> medicineQuantity = medicineQuantityRepository.findAll();
		for (MedicineQuantity med : medicineQuantity) {
			Map<Object, Object> map = new HashMap<>();
			//System.out.println(med);
		    map.put("medicineName", med.getMedName());
			
			map.put("balance", med.getBalance());
			
			list.add(map);

		}
		return list;
	}
	
}
