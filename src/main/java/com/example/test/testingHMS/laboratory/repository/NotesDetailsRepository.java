package com.example.test.testingHMS.laboratory.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.test.testingHMS.laboratory.model.NotesDetails;
import com.example.test.testingHMS.patient.model.PatientRegistration;


@Repository
public interface NotesDetailsRepository extends CrudRepository<NotesDetails,String>{

	NotesDetails findFirstByOrderByNoteIdDesc();
	
	NotesDetails findByPatientRegistrationNotes(PatientRegistration patientRegistration);
	
	@Query(value="select * from second.v_note_f where p_reg_id=:regId",nativeQuery=true)
	List<NotesDetails> getAllReport(@Param("regId") String regId);
	
	
}
