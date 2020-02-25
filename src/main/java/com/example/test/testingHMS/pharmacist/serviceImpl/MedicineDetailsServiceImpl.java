package com.example.test.testingHMS.pharmacist.serviceImpl;

import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.test.testingHMS.pharmacist.helper.RefMedicine;
import com.example.test.testingHMS.pharmacist.model.MedicineDetails;
import com.example.test.testingHMS.pharmacist.repository.MedicineDetailsRepository;
import com.example.test.testingHMS.pharmacist.service.MedicineDetailsService;
import com.example.test.testingHMS.user.model.User;
import com.example.test.testingHMS.user.serviceImpl.UserServiceImpl;
@Service
public class MedicineDetailsServiceImpl implements MedicineDetailsService 
{
	
	public static final String KEY = "cacheKey";
	
	@Autowired
	MedicineDetailsRepository medicineDetailsRepository;
	
	@Autowired
	UserServiceImpl userServiceImpl;

	@Override
	public void computeSave(MedicineDetails medicineDetails,Principal p) {
		
		if(medicineDetailsRepository.findByName(medicineDetails.getName())!=null)
		{
			throw new RuntimeException("Medicine Already Exists ! "+medicineDetails.getName());
		}
		if(medicineDetails.getDrugType()==null)
		{
			throw new RuntimeException("Drug Type Cann't Be Null !");
		}

		medicineDetails.setMedicineId(getNextMedId());
		medicineDetails.setInsertedBy(p.getName());
		 medicineDetailsRepository.save(medicineDetails);
	}
	
	
	@Transactional
	//@CachePut(value="itemCache")
	@CacheEvict(value="itemCache",allEntries = true)
	public void computeSaveList(MedicineDetails medicineDetails,Principal p) {
		
		User userSecurity=userServiceImpl.findByUserName(p.getName());
		
		List<RefMedicine> medicineDetailss = medicineDetails.getRefMedicine();
		for (RefMedicine nextInfo : medicineDetailss) {
			
			if(medicineDetailsRepository.findByName(nextInfo.getName())!=null)
			{
				throw new RuntimeException("Medicine Already Exists ! "+nextInfo.getName());
			}

		medicineDetails.setMedicineId(getNextMedId());
		medicineDetails.setInsertedBy(userSecurity.getUserId());
		medicineDetails.setInsertedDate(Timestamp.valueOf(LocalDateTime.now()));
		medicineDetails.setDrugType(nextInfo.getDrugType());
		medicineDetails.setItemLevel(nextInfo.getItemLevel());
		medicineDetails.setVendorPackage(nextInfo.getVendorPackage());
		
		medicineDetails.setBatchNo(nextInfo.getBatchNo());
		medicineDetails.setBrand(nextInfo.getBrand());
		medicineDetails.setManufacturer(nextInfo.getManufacturer());
		medicineDetails.setMaxPurchaseQuantity(nextInfo.getMaxPurchaseQuantity());
		medicineDetails.setMinPurchaseQuantity(nextInfo.getMinPurchaseQuantity());
		medicineDetails.setName(nextInfo.getName());
		medicineDetails.setQuantityPerDay(nextInfo.getQuantityPerDay());
		medicineDetails.setSaleUnits(nextInfo.getSaleUnits());
		medicineDetails.setStrengthUnits(nextInfo.getStrengthUnits());
		medicineDetailsRepository.save(medicineDetails);
		}

	}


	@Override
	public String getNextMedId() {
		MedicineDetails medicineDetails=medicineDetailsRepository.findFirstByOrderByMedicineIdDesc();
		String nextId=null;
		if(medicineDetails==null)
		{
			nextId="MED0000001";
		}
		else
		{
			int lastId=Integer.parseInt(medicineDetails.getMedicineId().substring(3));
			lastId+=1;
			nextId="MED"+String.format("%07d", lastId);
			
		}
		return nextId;
	}
	
	
	@Override
	@Transactional(readOnly=true)
    @Cacheable(value="itemCache",key="#root.target.KEY")
	public List<MedicineDetails> findAll()
	{
		return medicineDetailsRepository.findAll();
	}
	
	public MedicineDetails findByName(String name)
	{
		return medicineDetailsRepository.findByName(name);
	}

	
}
