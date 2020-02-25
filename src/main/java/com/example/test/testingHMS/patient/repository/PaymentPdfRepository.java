package com.example.test.testingHMS.patient.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.test.testingHMS.patient.model.PatientPaymentPdf;


@Repository
public interface PaymentPdfRepository extends CrudRepository<PatientPaymentPdf,String>
{
	PatientPaymentPdf findFirstByOrderByPidDesc();
	
	PatientPaymentPdf findByPid(String id);
	
	PatientPaymentPdf findByRegIdAndBillNo(String regId, String billno);
	
	@Query(value="SELECT * FROM second.patient_payment_pdf where file_name like :regId%",nativeQuery=true)
	List<PatientPaymentPdf> getAllReport(@Param("regId") String regId);
	
	@Query(value="SELECT * FROM second.patient_payment_pdf where file_name like %:regId%",nativeQuery=true)
	PatientPaymentPdf getBlankPdf(@Param("regId") String regId);
	
	@Query(value="SELECT * FROM second.patient_payment_pdf where file_name like %:regId%  and (file_name like '%Patient Detailed Reciept%' or file_name like '%Patient Detailed Advance Reciept%')",nativeQuery=true)
	PatientPaymentPdf getUltimatePdf(@Param("regId") String regId);

}
