package com.example.test.testingHMS.laboratory.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.test.testingHMS.laboratory.model.LabServices;

@Repository
public interface LabServicesRepository extends CrudRepository<LabServices,String>{
	

	LabServices findFirstByOrderByServiceIdDesc();
	
	List<LabServices> findAll();
	
	List<LabServices> findByServiceName(String name);
	
	List<LabServices> findByPatientType(String type);
	
	List<LabServices> findAllByPatientTypeAndServiceType(String type,String serviceType);
	
	
	LabServices findByServiceNameAndPatientType(String serviceName,String patName);
	
	@Query(value="SELECT * FROM second.v_lab_services_d where room_type=:roomType",nativeQuery=true)
	List<LabServices> findAccoringToPatient(@Param("roomType") String roomType);
	
	LabServices findByServiceId(String serviceId);
	
	@Query(value="select * from second.v_lab_services_d where service_type=:lab",nativeQuery=true)
	List<LabServices> findOnlyLab(@Param("lab") String lab);
	
	@Query(value="SELECT * FROM second.v_lab_services_d where patient_type=:type group by service_name;",nativeQuery=true)
	List<LabServices> servicesForInptient(@Param("type") String type);
	
	
	@Query(value="select * from second.v_lab_services_d where service_name=:name and patient_type=:type and room_type=:room",nativeQuery=true)
	LabServices findPriceByType(@Param("name") String name,@Param("type") String type,@Param("room") String room);
	
	
	@Query(value="select * from second.v_lab_services_d where patient_type='OUTPATIENT'",nativeQuery=true)
	List<LabServices> findOnlyOthers();



}
