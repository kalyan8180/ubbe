package com.example.test.testingHMS.laboratory.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.test.testingHMS.laboratory.model.PatientServiceDetails;

@Repository
public interface PatientServiceDetailsRepository extends CrudRepository<PatientServiceDetails,String>{
	
	PatientServiceDetails findFirstByOrderByPatServiceIdDesc();
	
	PatientServiceDetails findByPatientService(String id);
	
	@Query(value="select * from second.v_patient_services_details_f where p_reg_id=:regId and service_id=:serviceId",nativeQuery=true)
	List<PatientServiceDetails> findByPatientServiceAndPatientLabService(@Param("regId") String regId,@Param("serviceId") String serviceId);


}
