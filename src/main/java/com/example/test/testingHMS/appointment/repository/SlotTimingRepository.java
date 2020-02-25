package com.example.test.testingHMS.appointment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.test.testingHMS.appointment.model.SlotTiming;

@Repository
public interface SlotTimingRepository extends CrudRepository<SlotTiming, Integer> {
	public List<SlotTiming> findBySlot(String slot);
	
	public SlotTiming findByFromTimeAndToTime(String fromTime,String toTime);
	

	
	@Query(value = "select * from second.v_doctor_appointment_f ad, second.v_slottimings_d sd where doctor_name=:doctorName  AND shift=:shift AND appointment_date like %:appointmentDate% AND ad.slot_id<>sd.slot_id AND ad.shift=sd.slot", nativeQuery = true)
	List<SlotTiming> getNoAppointments(@Param("doctorName") String doctorName, @Param("shift") String shift,
			@Param("appointmentDate") String appointmentDate);
	
	
	@Query(value = "select *from second.v_slottimings_d where slot_id not in(select ad.slot_id from second.v_doctor_appointment_f ad, second.v_slottimings_d sd where doctor_name=:doctorName  AND shift=:shift AND appointment_date like %:appointmentDate% AND ad.slot_id=sd.slot_id AND ad.shift=sd.slot) AND slot=:shift ", nativeQuery = true)
	List<SlotTiming> getnonAppointments(@Param("doctorName") String doctorName, @Param("shift") String shift,@Param("appointmentDate") String appointmentDate);
	
	
	
	


}