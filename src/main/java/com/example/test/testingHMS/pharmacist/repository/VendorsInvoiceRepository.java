package com.example.test.testingHMS.pharmacist.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.test.testingHMS.pharmacist.model.MedicineProcurement;
import com.example.test.testingHMS.pharmacist.model.VendorsInvoice;

@Repository
public interface VendorsInvoiceRepository extends CrudRepository<VendorsInvoice,String> 
{
	VendorsInvoice findFirstByOrderByInvoiceIdDesc();
	
	List<VendorsInvoice> findByVendorInvoiceMedicineProcurement(String id);
	
	@Query(value="select invoice_id from second.v_vendor_invoice where procurement_id=:pid",nativeQuery=true)
	List<String> findOneInvoice(@Param("pid") String id);
	
	@Query(value="select sum(paid_amount) from second.v_vendor_invoice where procurement_id=:pid",nativeQuery=true)
	long findSumOfPaidAmount(@Param("pid") String pid);
	
	
}
