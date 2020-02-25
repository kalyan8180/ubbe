package com.example.test.testingHMS.patient.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.test.testingHMS.patient.model.CashPlusCard;

@Repository
public interface CashPlusCardRepository extends CrudRepository<CashPlusCard, Long> {


	CashPlusCard save(CashPlusCard cashPlusCard);
	

	CashPlusCard findByBillNo(String billNo);
	@Query(value="select * from second.v_cash_card_reg_d where inserted_date>=:fromDate AND inserted_date<=:toDate AND inserted_by=:uId and description like '%Sales%'",nativeQuery=true)
	List<CashPlusCard> findTheSalesCashPlusCardAmt(@Param("fromDate") Object fromDate,@Param("toDate") Object toDate,@Param("uId") String uId);
	


	@Query(value="select * from second.v_cash_card_reg_d where bill_no=:billNo and description like %:desc%",nativeQuery=true)
	CashPlusCard findByBillNoAndDescription(@Param("billNo") String billNo, @Param("desc") String desc);
	
	@Query(value="select * from second.v_cash_card_reg_d where inserted_date>=:fromDate" + 
			" AND inserted_date<=:toDate AND inserted_by=:uId AND description like '%Registration%' " + 
			"union " + 
			"select * from second.v_cash_card_reg_d where inserted_date>=:fromDate" + 
			" AND inserted_date<=:toDate AND inserted_by=:uId and description like '%Lab%'" + 
			" union " + 
			" select * from second.v_cash_card_reg_d where inserted_date>=:fromDate" + 
			" AND inserted_date<=:toDate AND inserted_by=:uId and description like '%Patient Advance%'" + 
			" union" + 
			" select * from second.v_cash_card_reg_d where inserted_date>=:fromDate" + 
			" AND inserted_date<=:toDate AND inserted_by=:uId and description like '%OspBill%'" + 
			" ",nativeQuery=true)
	List<CashPlusCard> findByBillNoAndDescriptions(@Param("fromDate") Object fromDate,@Param("toDate") Object toDate,@Param("uId") String uId);

}
