package com.example.test.testingHMS.ambulance.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.test.testingHMS.ambulance.model.AmbulanceServices;

@Repository
public interface AmbulanceServicesRepository extends CrudRepository<AmbulanceServices, Long>{
	
	
	
	
		@Query(value="select * from second.v_ambulance_service_d where status=1",nativeQuery=true)
		List<AmbulanceServices> getAmbulanceStatus();
		
		
		AmbulanceServices findByAmbulanceNO(String ambulanceNo);


}
