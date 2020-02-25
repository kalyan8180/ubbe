package com.example.test.testingHMS.voucher.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.test.testingHMS.voucher.model.Voucher;

@Repository
public interface VoucherRepository extends CrudRepository<Voucher,String>{
	Voucher findFirstByOrderByPaymentNoDesc();
	
	@Query(value="select * from second.v_voucher_details where payment_date>=:fromDate AND payment_date<=:toDate AND user_id=:uId",nativeQuery=true)
	List<Voucher> findUserWiseIpOpDetailed(@Param("fromDate") Object fromDate, @Param("toDate") Object toDate, @Param("uId") String uId);

	Voucher findByPaymentNo(String id);
	
	@Query(value="select * from second.v_voucher_details where payment_date>=:twoDayBack and  payment_date<=:today order by payment_no desc",nativeQuery=true)
	List<Voucher> voucherTwoDays(@Param("twoDayBack") String twoDayBack,@Param("today") String today);
	
	
//	List<Voucher> findAllByOrderByPaymentNoDesc();
	
	List<Voucher> findAll();
	
	
	@Query(value="select * from second.v_voucher_details order by payment_no desc",nativeQuery=true)
	List<Voucher>findAllByOrderByPaymentNoDesc();
	
	@Query(value="select * from second.v_voucher_details where payment_date like %:date%",nativeQuery=true)
	List<Voucher> findPerDayVoucherValue(@Param("date") String date);
	
	@Query(value="select * from second.v_voucher_details where payment_date>=:fromDate and  payment_date<=:toDate",nativeQuery=true)
	List<Voucher> findDailyStats(@Param("fromDate") Object fromDate,@Param("toDate") Object toDate);

	
}