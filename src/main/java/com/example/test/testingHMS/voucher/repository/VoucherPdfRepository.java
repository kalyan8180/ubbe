package com.example.test.testingHMS.voucher.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.test.testingHMS.voucher.model.VoucherPdf;

@Repository
public interface VoucherPdfRepository extends CrudRepository<VoucherPdf, String>{

	VoucherPdf findFirstByOrderByVidDesc();

	VoucherPdf findByVid(String id);
	
	VoucherPdf save(VoucherPdf pdf);
	
	@Query(value="SELECT * FROM second.voucher_pdf where file_name like %:paymentNo%",nativeQuery=true)
	VoucherPdf getVoucherPdf(@Param("paymentNo") String paymentNo);


}
