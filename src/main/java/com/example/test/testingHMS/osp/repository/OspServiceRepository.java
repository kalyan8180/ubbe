package com.example.test.testingHMS.osp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.test.testingHMS.osp.model.OspService;

@Repository
public interface OspServiceRepository extends CrudRepository<OspService, String>{
	
OspService findFirstByOrderByMasterOspServiceIdDesc();


List<OspService> findByBillNo(String id);

@Query(value="select * from second.v_ospservice_f where payment_type=:paymentType order by bill_no desc",nativeQuery=true)
List<OspService> findOspDueByDesc(@Param("paymentType") String paymentType);

List<OspService> findByPaymentType(String paymentType);
	
	//List<OspService> findByOspServiceId(String ospServiceId);
	
	@Query(value="select * from second.v_ospservice_f where osp_service_id=:ospServiceId",nativeQuery=true)
	List<OspService> findServices(@Param("ospServiceId") String ospServiceId);
	
	@Query(value="select * from second.v_ospservice_f where updated_date>=:fromDate AND updated_date<=:toDate AND updated_by=:uId",nativeQuery=true)
	List<OspService> findByUserDetails(@Param("fromDate") Object fromDate,@Param("toDate") Object toDate,@Param("uId") String uId);

	
	List<OspService> findByOrderByMasterOspServiceIdDesc();
	
	@Query(value="select * from second.v_ospservice_f where entered_date>=:twoDayBack and  entered_date<=:today order by osp_service_id desc",nativeQuery=true)
	List<OspService> ospTwoDays(@Param("twoDayBack") String twoDayBack,@Param("today") String today);

	OspService findOneByBillNo(String id);
	
	@Query(value="select * from second.v_ospservice_f where entered_date like %:date%",nativeQuery=true)
	List<OspService> findPerDayOspValue(@Param("date") String date);
	
	void deleteByMasterOspServiceId(String masterOspServiceId);
	
	OspService findByMasterOspServiceId(String masterOspServiceId);
	
	@Query(value="select * from second.v_ospservice_f where entered_date>=:fromDate AND entered_date<=:toDate",nativeQuery=true)
	List<OspService> findDailyStats(@Param("fromDate") Object fromDate,@Param("toDate") Object toDate);


}