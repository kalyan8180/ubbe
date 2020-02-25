package com.example.test.testingHMS.pharmacist.serviceImpl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.test.testingHMS.pharmacist.model.Vendors;
import com.example.test.testingHMS.pharmacist.repository.VendorsRepository;
import com.example.test.testingHMS.pharmacist.service.VendorsService;
@Service
public class VendorsServiceImpl implements VendorsService 
{
	@Autowired
	VendorsRepository vendorsRepository;
	

	public void computeSave(Vendors vendors) 
	{
		Vendors existingVendor=vendorsRepository.findByVendorName(vendors.getVendorName());
		if(existingVendor!=null)
		{
			throw new RuntimeException("Vendor Name Already Exists !");
		}
		
		vendors.setVendorId(getNextVendorId());
		//vendors.setRegNo(getNextRegId());
		Timestamp timestamp=Timestamp.valueOf(LocalDateTime.now());
		System.out.println(timestamp);
		vendors.setRegisteredDate(timestamp);
		vendors.setActive("YES");
		// After security this values will be inserted
		//vendors.setInsertedBy(insertedBy);
		//vendors.setVendorUser(vendorUser);
		vendors.setInsertedDate(timestamp);
		
		vendorsRepository.save(vendors);
		
	}
	
	public void computeUpdate(Vendors vendors) 
	{
		Timestamp timestamp=Timestamp.valueOf(LocalDateTime.now());
		System.out.println(timestamp);
		vendors.setRegisteredDate(timestamp);
		
		Vendors vendorsInfo=findByVendorId(vendors.getVendorId());
		if(vendorsInfo==null)
		{
			System.out.println("Its null");
		}
		else
		{
			System.out.println("Not nulk" +vendorsInfo.getInsertedDate()+vendorsInfo.getRegNo());
		}
		vendors.setInsertedDate(vendorsInfo.getInsertedDate());
		vendors.setRegNo(vendorsInfo.getRegNo());
		
		
		// After security this values will be inserted
		//vendors.setInsertedBy(insertedBy);
		//vendors.setVendorUser(vendorUser);
		vendors.setModifiedDate(timestamp);
		
		vendorsRepository.save(vendors);
	
	}
	
	public String getNextVendorId()
	{
		Vendors vendors=vendorsRepository.findFirstByOrderByVendorIdDesc();
		String nextId=null;
		if(vendors==null)
		{
			nextId="VEN0000001";
		}
		else
		{
			int lastIntId=Integer.parseInt(vendors.getVendorId().substring(3));
			lastIntId+=1;
			nextId="VEN"+String.format("%07d",lastIntId);
		}
		return nextId;
	}
	
	/*public String getNextRegId()
	{
		Vendors vendors=vendorsRepository.findFirstByOrderByVendorIdDesc();
		String nextId=null;
		if(vendors==null)
		{
			nextId="REG0000001";
		}
		else
		{
			String lastId=vendors.getRegNo();
			int lastIntId=Integer.parseInt(lastId.substring(3));
			lastIntId+=1;
			nextId="REG"+String.format("%07d",lastIntId);
		}
		return nextId;
	}
	*/
	public List<Vendors> findAll()
	{
		return vendorsRepository.findAll();
	}

	public Vendors findByVendorName(String name)
	{
		return vendorsRepository.findByVendorName(name);
	}

	public Vendors findByVendorId(String id)
	{
		return vendorsRepository.findByVendorId(id);
	}
public List<Vendors> findAllOrderByVendorId(){
		
		
		return vendorsRepository.findAllByOrderByVendorIdDesc();
	}
	

}
