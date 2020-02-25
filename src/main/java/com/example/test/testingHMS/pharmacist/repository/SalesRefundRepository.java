package com.example.test.testingHMS.pharmacist.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.test.testingHMS.pharmacist.model.SalesRefund;
@Repository
public interface SalesRefundRepository extends CrudRepository<SalesRefund,String> 
{
	SalesRefund findFirstByOrderByReturnIdDesc();
	
	List<SalesRefund> findAllByOrderByReturnIdDesc();
	
	@Query(value="select * from second.v_sales_refund where status='Not-Approved'",nativeQuery=true)
	List<SalesRefund> findNotApproved();
	
	SalesRefund findByReturnId(String id);

}
