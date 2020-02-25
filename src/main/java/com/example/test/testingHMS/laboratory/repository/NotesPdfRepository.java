package com.example.test.testingHMS.laboratory.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.test.testingHMS.laboratory.model.LaboratoryRegistration;
import com.example.test.testingHMS.laboratory.model.NotesPdf;
import com.example.test.testingHMS.patient.model.PatientPaymentPdf;

@Repository
public interface NotesPdfRepository extends CrudRepository<NotesPdf,String>{

	NotesPdf findFirstByOrderByNidDesc();
	
	NotesPdf findByNid(String id);
	
	NotesPdf findByRegId(String id);
	
	@Query(value="SELECT * FROM second.notes_pdf where file_name like :regId%",nativeQuery=true)
	List<NotesPdf> getAllReport(@Param("regId") String regId);
	
	
	
}
