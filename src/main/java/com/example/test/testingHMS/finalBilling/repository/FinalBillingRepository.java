package com.example.test.testingHMS.finalBilling.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.test.testingHMS.finalBilling.model.FinalBilling;

@Repository
public interface FinalBillingRepository extends CrudRepository<FinalBilling, Long>{
	
	void deleteByBillTypeAndBillNoAndRegNo(String billtype,String billNo,String regNo);

	FinalBilling save(FinalBilling finalBilling);

@Query(value="select * from second.v_finalbilling_f where bill_type=:billType order by bill_no desc",nativeQuery=true)
List<FinalBilling> findBillType(@Param("billType") String billType);
	
	@Query(value="select * from second.v_finalbilling_f where bill_type=:billType and bill_no=:billNo and updated_date like %:date%",nativeQuery=true)
	FinalBilling paymentUpdate(@Param("billType") String billType,@Param("billNo") String billNo,@Param("date") String date);
	
	@Query(value="select * from second.v_finalbilling_f where updated_date>=:fromDate and updated_date<=:toDate",nativeQuery=true)
	List<FinalBilling> findDailyStats(@Param("fromDate")Object fromDate,@Param("toDate") Object toDate);
	
	
	FinalBilling findByBillTypeAndBillNoAndRegNo(String billtype,String billNo,String regNo); //Finding sales

	@Query(value="select * from second.v_finalbilling_f where updated_date>=:fromDate and updated_date<=:toDate and user_id=:userName and bill_type='Ip Final Billing'",nativeQuery=true)
	List<FinalBilling> getIpBill(@Param("fromDate")Object fromDate,@Param("toDate") Object toDate,@Param("userName") String userName);


	List<FinalBilling> findAll();
	
	@Query(value="select * from second.v_finalbilling_f where updated_date>=:fromDate and updated_date<=:toDate and user_id=:userName and( bill_type='Osp Bill' or bill_type='Osp Due Settlememt') ",nativeQuery=true)
	List<FinalBilling> getOspBill(@Param("fromDate")Object fromDate,@Param("toDate") Object toDate,@Param("userName") String userName);
	
	
	@Query(value="select * from second.v_finalbilling_f where updated_date>=:fromDate and updated_date<=:toDate and user_id=:userName and bill_type='Patient Registration' AND reg_no in (select reg_id from second.v_patient_payment_f where type_of_charge='ADVANCE');",nativeQuery=true)
	List<FinalBilling> getPatBill(@Param("fromDate")Object fromDate,@Param("toDate") Object toDate,@Param("userName") String userName);
	
	
	@Query(value="select * from second.v_finalbilling_f where updated_date>=:fromDate and updated_date<=:toDate and user_id=:userName and (bill_type='Sales' or bill_type='Sales Due Settlement'or bill_type='Walkin Sales') and payment_type!='NA_WO' and bill_no=:billNo",nativeQuery=true)
	List<FinalBilling> getDueSalesBill(@Param("fromDate")Object fromDate,@Param("toDate") Object toDate,@Param("userName") String userName,@Param("billNo") String billNo);

	@Query(value="select * from second.v_finalbilling_f where updated_date>=:fromDate and updated_date<=:toDate and user_id=:userName and (bill_type='Laboratory Registration'or bill_type='Lab Due Settlement')",nativeQuery=true)
	List<FinalBilling> getLabBill(@Param("fromDate")Object fromDate,@Param("toDate") Object toDate,@Param("userName") String userName);
	
	


	@Query(value="select * from second.v_finalbilling_f where updated_date>=:fromDate and updated_date<=:toDate and user_id=:userName and (bill_type='Sales' or bill_type='Sales Due Settlement') and payment_type!='NA_WO'",nativeQuery=true)
	List<FinalBilling> getSalesBill(@Param("fromDate")Object fromDate,@Param("toDate") Object toDate,@Param("userName") String userName);
	
	@Query(value="select * from second.v_finalbilling_f where updated_date>=:fromDate and updated_date<=:toDate and user_id=:userName and (bill_type='Sales Return' or bill_type='Ip Sales Return')",nativeQuery=true)
	List<FinalBilling> getSalesReturnBill(@Param("fromDate")Object fromDate,@Param("toDate") Object toDate,@Param("userName") String userName);
	
	List<FinalBilling> findByRegNo(String regNo);
	
		List<FinalBilling> findByBillTypeAndBillNo(String billtype,String billNo);
	
	@Query(value="select * from second.v_finalbilling_f where (bill_type=:billType or bill_type=:dueBillType  )and due_status=:dueStatus order by updated_date desc",nativeQuery=true)
	List<FinalBilling> findDueBills(@Param("billType") String billType, @Param("dueBillType") String dueBillType,@Param("dueStatus")String dueStatus);
	
	@Query(value="select * from second.v_finalbilling_f where (bill_type=:billType or bill_type=:dueBillType)and bill_no=:billNo ",nativeQuery=true)
	List<FinalBilling> changeStatusDueBills(@Param("billType") String billType, @Param("dueBillType") String dueBillType,@Param("billNo")String billNo);
	
	@Query(value="select * from second.v_finalbilling_f where (bill_type=:billType or bill_type=:dueBillType)and bill_no=:billNo and updated_by=:userName and updated_date>=:fromDate and updated_date<=:toDate",nativeQuery=true)
	List<FinalBilling> changeStatusReturnDueBills(@Param("billType") String billType, @Param("dueBillType") String dueBillType,@Param("billNo")String billNo,@Param("userName") String userBy,@Param("fromDate")Object fromDate,@Param("toDate") Object toDate);
	
	
	
	@Query(value="select * from second.v_finalbilling_f where (bill_type=:billType or bill_type=:dueBillType)and bill_no=:billNo and updated_date like %:date% anduser_id=:userName ",nativeQuery=true)
	List<FinalBilling> forScroll(@Param("billType") String billType, @Param("dueBillType") String dueBillType,@Param("billNo")String billNo,@Param("date") String date,@Param("userName") String userName);
	
	@Query(value="select * from second.v_finalbilling_f where bill_type=:billType and  updated_date>=:fromDate and updated_date<=:toDate order by bill_no desc",nativeQuery=true )
	List<FinalBilling> forSales(@Param("billType") String billType,@Param("fromDate")Object fromDate,@Param("toDate") Object toDate);
	
}