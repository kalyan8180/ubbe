package com.example.test.testingHMS.patient.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.test.testingHMS.patient.model.PatientPayment;


@Repository
public interface PaymentRepository extends CrudRepository<PatientPayment,Long>
{
	@Query(value="SELECT * FROM second.v_patient_payment_f where reg_id=:regId and paid=:status",nativeQuery=true)
	List<PatientPayment> findByPatientRegistration(@Param("regId") String regId,@Param("status") String status);
	
	

}
