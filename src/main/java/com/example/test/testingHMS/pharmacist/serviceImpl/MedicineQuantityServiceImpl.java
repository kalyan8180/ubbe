package com.example.test.testingHMS.pharmacist.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.test.testingHMS.pharmacist.model.MedicineDetails;
import com.example.test.testingHMS.pharmacist.model.MedicineQuantity;
import com.example.test.testingHMS.pharmacist.repository.MedicineQuantityRepository;
import com.example.test.testingHMS.pharmacist.service.MedicineQuantityService;

@Service
public class MedicineQuantityServiceImpl implements MedicineQuantityService{
	
	@Autowired
	MedicineQuantityRepository medicineQuantityRepository ;

	@Override
	public MedicineQuantity findByMedicineDetails(MedicineDetails medicineDetails) {
		
		return medicineQuantityRepository.findByMedicineDetails(medicineDetails);
	}

	@Override
	public MedicineQuantity findByBatchIdAndMedicineDetails(String batchId, MedicineDetails medicineDetails) {
		return medicineQuantityRepository.findByBatchIdAndMedicineDetails(batchId, medicineDetails);
	}
	
	

}