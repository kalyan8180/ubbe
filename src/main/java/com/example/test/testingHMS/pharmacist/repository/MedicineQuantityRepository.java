package com.example.test.testingHMS.pharmacist.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.example.test.testingHMS.pharmacist.model.MedicineDetails;
import com.example.test.testingHMS.pharmacist.model.MedicineQuantity;

public interface MedicineQuantityRepository extends CrudRepository<MedicineQuantity, Long>{
	
	public MedicineQuantity findByMedicineDetails(MedicineDetails medicineDetails);
	
	@Query(value="select * from second.v_medicine_quantity where balance<=100 order by balance, med_name",nativeQuery=true)
	public List<MedicineQuantity> findAll();
	public List<MedicineQuantity> findBymedName(String medName);
	
	public MedicineQuantity findByBatchIdAndMedicineDetails(String batchId,MedicineDetails medicineDetails);
}
