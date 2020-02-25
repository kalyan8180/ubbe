package com.example.test.testingHMS.laboratory.service;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import com.example.test.testingHMS.laboratory.model.MasterCheckUpRegistration;
import com.example.test.testingHMS.laboratory.model.MasterCheckupService;

public interface MasterCheckupServiceService {

	List<Object> pageRefresh();

	void computeSave(MasterCheckupService masterCheckupService, Principal principal);

	List<Object> getAll();
	
	List<MasterCheckupService> findByCheckupId(String checkupId);
	List<MasterCheckupService> findByMasterServiceName(String masterServiceName);


}