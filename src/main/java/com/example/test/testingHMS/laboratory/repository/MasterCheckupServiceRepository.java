package com.example.test.testingHMS.laboratory.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.test.testingHMS.laboratory.model.MasterCheckupService;


@Repository
public interface MasterCheckupServiceRepository extends CrudRepository<MasterCheckupService, String> {
	
	MasterCheckupService findFirstByOrderByMasterCheckupIdDesc();
	
	List<MasterCheckupService> findAll();
	
	List<MasterCheckupService> findByCheckupId(String checkupId);
	
	List<MasterCheckupService> findByMasterServiceName(String masterServiceName);
	
	@Query(value="select * from second.v_master_checkup_service_d group by master_service_name",nativeQuery=true)
	List<MasterCheckupService> getAllServices();

}