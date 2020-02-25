package com.example.test.testingHMS.bed.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.test.testingHMS.bed.model.RoomBookingDetails;
import com.example.test.testingHMS.patient.model.PatientRegistration;

@Repository
public interface RoomBookingDetailsRepository extends CrudRepository<RoomBookingDetails, String>{


	RoomBookingDetails findFirstByOrderByBookingIdDesc();
	
	@Query(value="select * from second.v_room_booking_details_f where bed_id=:id and status=1",nativeQuery=true)
	RoomBookingDetails getroomStatus(@Param("id") String id);
	
	RoomBookingDetails findByPatientRegistrationBooking(PatientRegistration patientRegistration);
	
	RoomBookingDetails findByPatientRegistrationBookingAndStatus(PatientRegistration patientRegistration,int status);
	
}
