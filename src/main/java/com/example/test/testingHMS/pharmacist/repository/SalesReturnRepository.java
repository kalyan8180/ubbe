package com.example.test.testingHMS.pharmacist.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.test.testingHMS.patient.model.PatientRegistration;
import com.example.test.testingHMS.pharmacist.model.SalesReturn;
@Repository
public interface SalesReturnRepository extends CrudRepository<SalesReturn,String> 
{
	
	@Query(value="select * from second.v_sales_return order by date desc",nativeQuery=true)
	 List<SalesReturn> findAllReturns();
	
	List<SalesReturn> findBySalesReturnPatientRegistration(PatientRegistration patientRegistration);
	
	SalesReturn findFirstByOrderBySaleNoDesc();
		
	SalesReturn findFirstByOrderByMasterSaleNoDesc();
	
	List<SalesReturn> findByMasterSaleNo(String id);
	
	List<SalesReturn> findByBillNoAndMedicineName(String billNo,String medName);
	
	List<SalesReturn> findByMasterSaleNoAndBillNo(String masterSaleNo,String billNo);
	@Query(value="select * from second.v_sales_return where bill_no=:billNo and date like %:date%",nativeQuery=true)
    List<SalesReturn> payUpdate(@Param("billNo") String billNo,@Param("date") String date);
	
	
	@Query(value="select * from second.v_sales_return where date>=:fromdate  AND date<=:todate AND raised_by=:soldBy and payment_type!='NA_WO'",nativeQuery=true)
	List<SalesReturn> findTheUserWiseDetails1(@Param("fromdate") Object fromdate,@Param("todate") Object todate,@Param("soldBy") Object soldBy);

	@Query(value="select * from second.v_sales_return where date>=:fromdate  AND date<=:todate",nativeQuery=true)
	List<SalesReturn> findTheUserWiseDetails(@Param("fromdate") Timestamp fromdate,@Param("todate") Timestamp todate);

	@Query(value="select * from second.v_sales_return where date>=:fromdate  AND date <= :todate order by date desc",nativeQuery=true)
	List<SalesReturn> findTheReturnList(@Param("fromdate") String fromdate,@Param("todate") String todate);
	
	@Query(value="select * from second.v_sales_return where bill_no=:bill1 and date>=:fromdate  AND date <= :todate",nativeQuery=true)
	List<SalesReturn> getByBill(@Param("bill1") String bill1,@Param("fromdate") Object fromdate,@Param("todate") Object todate);
	
	List<SalesReturn> findByBillNo(String billNo);
	
	@Query(value="select * from  second.v_sales_return where date>=:fromdate  AND date <= :todate AND raised_by=:soldBy ",nativeQuery=true)
	List<SalesReturn> findTheUserWiseShiftReturns(@Param("fromdate") Object fromdate,@Param("todate") Object todate,@Param("soldBy") Object soldBy);

	//List<SalesReturn> findByName(String name);

	@Query(value="select * from second.v_sales_return where medicine_name=:medName",nativeQuery=true)
	List<SalesReturn> findByName(@Param("medName") String medName);
	
	@Query(value="select * from second.v_sales_return where date like %:returnDate%",nativeQuery=true)
	List<SalesReturn> getBillReturnDate(@Param("returnDate") String returnDate);
	
	 @Query(value="select * from second.v_sales_return where date>=:fromDate AND date<=:toDate AND medicine_name=:medName",nativeQuery=true)
		List<SalesReturn> getStockDetails(@Param("fromDate") Object fromDate,@Param("toDate") Object toDate,@Param("medName") Object medName);

}
