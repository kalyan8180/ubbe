
package com.example.test.testingHMS.patient.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.test.testingHMS.patient.model.PatientDetails;


@Repository
public interface PatientDetailsRepository extends CrudRepository<PatientDetails,Long>
{
	PatientDetails findFirstByOrderByPatientIdDesc();
	
	PatientDetails findByUmr(String umr);
	
	List<PatientDetails> findAll();
	 
	PatientDetails findByMobile(long mobile);
	 
	PatientDetails findByPatientId(long id);
	
	List<PatientDetails> findAllByOrderByPatientIdDesc();
	
	@Query(value="select * from second.v_patient_details_d where mobile=:mobile and first_name=:fname and last_name=:lname",nativeQuery=true)
	List<PatientDetails> patientAlreadyExists(
			@Param("mobile") String mobile,
			@Param("fname") String fname,
			@Param("lname") String lname);
	
	
	List<PatientDetails> findOneByPatientId(long id);
	
	List<PatientDetails> findByFirstName(String firstName);
	
	@Query(value="select * from second.v_patient_details_d where first_name like %:fname%",nativeQuery=true)
	List<PatientDetails> findbyName(@Param("fname") String fname);
	
	@Query(value="select * from second.v_patient_details_d where mobile=:mobile",nativeQuery=true)
	List<PatientDetails> findByMobileNo(@Param("mobile") long mobile);

}
