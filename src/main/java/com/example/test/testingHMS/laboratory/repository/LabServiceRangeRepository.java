package com.example.test.testingHMS.laboratory.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.test.testingHMS.laboratory.model.LabServiceRange;

@Repository
public interface LabServiceRangeRepository extends CrudRepository<LabServiceRange,String>{

	
	@Query(value="SELECT * FROM second.v_lab_services_measure_f where service_id=:serviceId and age_limit_min =:ageLimitMin and age_limit_max  >=:ageLimitMax and gender= :gender ",nativeQuery=true)
	List<LabServiceRange> findServices(@Param("serviceId") String serviceId,@Param("ageLimitMin") long ageLimitMin,@Param("ageLimitMax") long ageLimitMax,@Param("gender") String gender);

	@Query(value="SELECT * FROM second.v_lab_services_measure_f where service_id=:serviceId and age_limit_min =:ageLimitMin and age_limit_max  =:ageLimitMax and gender= :gender and age_type=:ageType ",nativeQuery=true)
	List<LabServiceRange> findNewServices(@Param("serviceId") String serviceId,@Param("ageLimitMin") long ageLimitMin,@Param("ageLimitMax") long ageLimitMax,@Param("gender") String gender,@Param("ageType") String ageType);
	
}
