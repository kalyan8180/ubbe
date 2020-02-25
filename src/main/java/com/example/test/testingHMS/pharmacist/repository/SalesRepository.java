package com.example.test.testingHMS.pharmacist.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.test.testingHMS.patient.model.PatientRegistration;
import com.example.test.testingHMS.pharmacist.model.Sales;

@Repository
public interface SalesRepository extends CrudRepository<Sales,String> 
{
	Sales findFirstByOrderBySaleNoDesc();
	
	@Query(value="select * from second.v_sales_f order by bill_no desc",nativeQuery=true)
	 List<Sales> findAllSales();
	
	List<Sales> findByBillNo(String id);
	
	@Query(value="SELECT distinct bill_no FROM second.v_sales_f where p_reg_id = :regId", nativeQuery=true)
	List<String> findDistinctBill(@Param("regId") String regId);
	
	
	List<Sales> findByBillNoAndPatientRegistration(String id, PatientRegistration patientRegistration);
	
	
	@Query(value="select * from second.v_sales_f where bill_date>=:fromdate  AND bill_date <= :todate AND medicine_name=:medName order by bill_date desc" ,nativeQuery=true)
	List<Sales> findpatientStockDetails(@Param("fromdate") Object fromdate, @Param("todate") Object todate, @Param("medName") String medName);
	
	
	@Query(value="select * from second.v_sales_f where updated_date >=:fromdate  AND updated_date <= :todate AND bill_no=:billNo ",nativeQuery=true)
	List<Sales> findByBillNoAndUpdatedDate(@Param("fromdate")Object fromdate,@Param("todate")Object todate,@Param("billNo")Object billNo);
	
	
	List<Sales> findByPaymentType(String  paymentType );
	
	@Query(value="select * from second.v_sales_f where payment_type=:paymentType order by bill_date desc",nativeQuery=true)
	List<Sales> findDuePaymentsByDesc(@Param("paymentType") String paymentType);
	
	@Query(value="select * from second.v_sales_f where bill_no=:billno and medicine_name=:name and batch_no=:batch",nativeQuery=true)
	Sales findOneBill(@Param("billno") String billno,@Param("name") String name,@Param("batch") String batch);
	
	List<Sales> findByPatientRegistration(PatientRegistration patientRegistration);
	
	@Query(value="SELECT * FROM second.v_sales_f where p_reg_id=:pRegId and quantity!=0 order by bill_date desc",nativeQuery=true)
	List<Sales> findByPatientRegistrationAndQuantity(@Param("pRegId") String pRegId);
	
	Sales findBySaleNo(String saleId);
	
	// For whatsApp Message
	@Query(value="SELECT * FROM second.v_sales_f WHERE bill_date like %:date% and payment_type!='NA_WO'",nativeQuery=true)
	List<Sales> findPerDayPharmacySalesValue(@Param("date") String date);
	
	@Query(value="select * from second.v_sales_f where updated_date>=:fromdate  AND updated_date <= :todate and payment_type!='NA_WO'",nativeQuery=true)
	List<Sales> findTheDailyStats(@Param("fromdate") Object fromdate,@Param("todate") Object todate);

	
	
	@Query(value="select * from second.v_sales_f where bill_date BETWEEN CURDATE() - INTERVAL 1 DAY AND CURDATE()",nativeQuery=true)
	List<Sales> getPreviousDaySales();
	
	@Query(value="select * from second.v_sales_f where bill_date>=:fromdate  AND bill_date <= :todate AND sold_by=:soldBy and payment_type not like '%Due%'",nativeQuery=true)
	List<Sales> findTheUserWiseShiftAndNotDue(@Param("fromdate") Object fromdate,@Param("todate") Object todate,@Param("soldBy") Object soldBy);
	
	
	
	@Query(value="select * from second.v_sales_f where updated_date >=:fromdate  AND updated_date <= :todate AND updated_by=:soldBy and payment_type!='NA_WO'",nativeQuery=true)
	List<Sales> findTheUserWiseShift(@Param("fromdate") Object fromdate,@Param("todate") Object todate,@Param("soldBy") Object soldBy);

	@Query(value="select * from second.v_sales_f where updated_date>=:fromdate  AND updated_date <= :todate",nativeQuery=true)
	List<Sales> findTheUserWiseDetails(@Param("fromdate") Object fromdate,@Param("todate") Object todate);
	
	@Query(value="select * from second.v_sales_f where bill_date>=:fromdate  AND bill_date <= :todate AND medicine_name=:medName AND batch_no=:batch",nativeQuery=true)
	List<Sales> findExpiryDetails(@Param("fromdate") Object fromdate, @Param("todate") Object todate, @Param("medName") String medName,@Param("batch") String batch);

	List<Sales> findByPaymentTypeAndUmr(String paymentType,String umrNo);

	@Query(value="select * from second.v_sales_f where bill_date like %:salesDate%",nativeQuery=true)
	List<Sales> getBillDate(@Param("salesDate") String salesDate);
	

	@Query(value="select * from second.v_sales_f where medicine_name=:medName",nativeQuery=true)
	List<Sales> findByName(@Param("medName") String medName);

	List<Sales> findByMedicineName(String medName);

	@Query(value="select * from second.v_sales_f where batch_no=:batch and medicine_id=:medicine",nativeQuery=true)
	List<Sales> findByBatchAndMedicine(@Param("batch") String batch,@Param("medicine") String medicine);
	
  @Query(value="select * from second.v_sales_f where bill_date>=:fromdate  AND bill_date <= :todate AND medicine_name=:medName",nativeQuery=true)
	List<Sales> findStockDetails(@Param("fromdate") Object fromdate, @Param("todate") Object todate, @Param("medName") String medName);
	
	 List<Sales> findByBillNoAndPaymentType(String billno,String payType);
	 
	// Find due Bill
	List<Sales> findByPatientRegistrationAndPaymentType(PatientRegistration patientRegistration,String paymentType);
	
	
	 List<Sales> findByPaymentTypeAndPatientRegistration(String payment,PatientRegistration reg);
	//List<Sales> findByName(String name);
	 
	 
	 @Query(value="select * from second.v_sales_f where bill_date>=:fromdate  AND bill_date <= :todate AND sold_by=:soldBy and payment_type not like '%Due%' and payment_type='Cash+Card'",nativeQuery=true)
		List<Sales> findByPaymentType(@Param("fromdate") Object fromdate,@Param("todate") Object todate,@Param("soldBy") Object soldBy);

	 @Query(value="select * from second.v_sales_f where medicine_name=:medName and batch_no=:batch and expire_date like %:expDate%",nativeQuery=true)
	 List<Sales> findStockAdjustments(@Param("medName") String medName,@Param("batch") String batch,@Param("expDate") String expDate);
	 
}
