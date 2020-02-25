package com.example.test.testingHMS.pharmacist.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.test.testingHMS.pharmacist.model.SalesPaymentPdf;
@Repository
public interface SalesPaymentPdfRepository extends CrudRepository<SalesPaymentPdf,String> 
{
	SalesPaymentPdf findFirstByOrderByPidDesc();
	
	SalesPaymentPdf findByPid(String id);
	
	@Query(value="SELECT * FROM second.sales_payment_pdf where file_name like %:billNo% and file_name like '%sale%'" ,nativeQuery=true)
	SalesPaymentPdf getNewSalesPdf(@Param("billNo") String billNo);
	
	@Query(value="SELECT * FROM second.sales_payment_pdf where file_name like %:regId%  and file_name not like '%Approximate Bill%'",nativeQuery=true)
	List<SalesPaymentPdf> getAllReport(@Param("regId") String regId);	

	  @Query(value="SELECT * FROM second.sales_payment_pdf where file_name like %:billNo% order by sid desc",nativeQuery=true)
		SalesPaymentPdf findByFileName(@Param("billNo") String billNo);

	@Query(value="SELECT * FROM second.sales_payment_pdf where file_name like %:procId%",nativeQuery=true)
	SalesPaymentPdf getProcurementPdf(@Param("procId") String procId);
	
	@Query(value="SELECT * FROM second.sales_payment_pdf where file_name like %:ospId% and file_name not like '%Osp due Bill%'",nativeQuery=true)
	SalesPaymentPdf getOspPdf(@Param("ospId") String ospId);
	
	@Query(value="SELECT * FROM second.sales_payment_pdf where file_name like %:billNo%",nativeQuery=true)
	SalesPaymentPdf getSalesPdf(@Param("billNo") String billNo);
	
	@Query(value="SELECT * FROM second.sales_payment_pdf where file_name like %:billNo% and file_name like '%return%'",nativeQuery=true)
	SalesPaymentPdf getReturnPaymentPdf(@Param("billNo") String billNo);
			
	@Query(value="SELECT * FROM second.sales_payment_pdf where file_name like %:regId% and file_name like '%Final Advance Reciept%'",nativeQuery=true)
	SalesPaymentPdf getFinalAdvancePdf(@Param("regId") String regId);
	
	
	@Query(value="SELECT * FROM second.sales_payment_pdf where file_name like %:billNo% and file_name like '%return%'",nativeQuery=true)
	List<SalesPaymentPdf> getReturnPaymentPdfList(@Param("billNo") String billNo);

	

}
