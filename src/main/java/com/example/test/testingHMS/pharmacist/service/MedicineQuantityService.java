package com.example.test.testingHMS.pharmacist.service;

import com.example.test.testingHMS.pharmacist.model.MedicineDetails;
import com.example.test.testingHMS.pharmacist.model.MedicineQuantity;

public interface MedicineQuantityService {
	
	public MedicineQuantity findByMedicineDetails(MedicineDetails medicineDetails);
	
	public MedicineQuantity findByBatchIdAndMedicineDetails(String batchId,MedicineDetails medicineDetails);

}
