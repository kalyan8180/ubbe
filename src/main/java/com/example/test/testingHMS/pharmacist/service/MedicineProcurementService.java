package com.example.test.testingHMS.pharmacist.service;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.data.repository.query.Param;

import com.example.test.testingHMS.pharmacist.model.MedicineDetails;
import com.example.test.testingHMS.pharmacist.model.MedicineProcurement;
import com.example.test.testingHMS.pharmacist.model.SalesPaymentPdf;

public interface MedicineProcurementService 
{
	String getNextMasterProcurementId();
	
	SalesPaymentPdf computeSave(MedicineProcurement medicineProcurement,Principal principal);
	
	SalesPaymentPdf updateSave(MedicineProcurement medicineProcurement,Principal principal);
	 
	String getNextProcurementId();
	

	//List<MedicineProcurement> findByProcurementId(String id);
	
	MedicineProcurement findMasterProcurementId(String procId,String medName,String batch);
	
	List<MedicineProcurement> findAll();
	
	List<MedicineProcurement> findByItemNameAndStatus(String name,String status);
	
	MedicineProcurement findByMasterProcurementId(String id);
	
	List<MedicineProcurement> findByMedicineProcurmentMedicineDetails(MedicineDetails id);
	
	public List<Object> getProcurementIds();
	
	List<MedicineProcurement> findOneApproved(MedicineDetails medId);
	
	/*
	 * get All procurement
	 */
	public List<Object> getAllProcurement();
	
	List<MedicineProcurement> findByItemName(String name);
	
	/*
	 * Approve procurement
	 */
	public SalesPaymentPdf approve(String procId,Principal principal);
	
	public MedicineDetails getManufacturer(String medName);
	
	public List<MedicineProcurement> findByBatchAndMedicine(String batch,String medicine);
	


	 

}
