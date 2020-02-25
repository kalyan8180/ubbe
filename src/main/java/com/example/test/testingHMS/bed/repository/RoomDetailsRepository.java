package com.example.test.testingHMS.bed.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.test.testingHMS.bed.model.RoomDetails;

@Repository
public interface RoomDetailsRepository extends CrudRepository<RoomDetails, String>{
	
	public List<RoomDetails> findAll();
	
	@Query(value="select * from second.v_room_details_d where floor_no=:floor and room_type=:ward",nativeQuery=true)
	public List<RoomDetails> getRooms(@Param("floor") String floor,@Param("ward") String ward);
	
	public RoomDetails findByBedName(String name);
	
	public List<RoomDetails> findByFloorNo(String name);
	
	

}
