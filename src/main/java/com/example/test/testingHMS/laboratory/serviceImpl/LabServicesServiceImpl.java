package com.example.test.testingHMS.laboratory.serviceImpl;

import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.test.testingHMS.bed.model.RoomDetails;
import com.example.test.testingHMS.bed.serviceImpl.RoomDetailsServiceImpl;
import com.example.test.testingHMS.laboratory.model.LabServiceRange;
import com.example.test.testingHMS.laboratory.model.LabServices;
import com.example.test.testingHMS.laboratory.repository.LabServiceRangeRepository;
import com.example.test.testingHMS.laboratory.repository.LabServicesRepository;
import com.example.test.testingHMS.laboratory.service.LabServicesService;
import com.example.test.testingHMS.user.model.User;
import com.example.test.testingHMS.user.serviceImpl.UserServiceImpl;

@Service
public class LabServicesServiceImpl implements LabServicesService{
	
	public static Logger Logger=LoggerFactory.getLogger(LabServicesServiceImpl.class);
	
	
	@Autowired
	LabServicesRepository patientLabServicesRepository;
	
	@Autowired
	RoomDetailsServiceImpl roomDetailsServiceImpl;
	
	@Autowired
	LabServiceRangeRepository labServiceRangeRepository;
	
	@Autowired
	UserServiceImpl userServiceImpl;
	
	@Autowired
	LabServicesServiceImpl labServicesServiceImpl;
	
	public List<LabServices> findByServiceName(String name)
	{
		return patientLabServicesRepository.findByServiceName(name);
	}

	
	public String getNexMasterId()
	{
		LabServices labServices=patientLabServicesRepository.findFirstByOrderByServiceIdDesc();
		String nextId=null;
		if(labServices==null)
		{
			nextId="MSER0000001";
		}
		else
		{
			int nextIntId=Integer.parseInt(labServices.getMasterServiceId().substring(4));
			nextIntId+=1;
			nextId="MSER"+String.format("%07d", nextIntId);
		}
		return nextId;
	}
	
	public LabServices findPriceByType(String name,String type,String room)
	{
		return patientLabServicesRepository.findPriceByType(name, type,room);
	}
	
	public List<LabServices> findAll()
	{
		return patientLabServicesRepository.findAll();
	}
	
	public String getNextId()
	{
		LabServices labServices=patientLabServicesRepository.findFirstByOrderByServiceIdDesc();
		String nextId=null;
		if(labServices==null)
		{
			nextId="SER000001";
		}
		else
		{
			int nextIntId=Integer.parseInt(labServices.getServiceId().substring(3));
			nextIntId+=1;
			nextId="SER"+String.format("%06d", nextIntId);
		}
		return nextId;
	}
	
	public Map<String, Object> pageRefresh(){
		Map<String, Object> map=new HashMap<>();
		map.put("srvicreId", getNexMasterId());
		List<RoomDetails> roomDetails=roomDetailsServiceImpl.findAll();
		List<Object> room=new ArrayList<>();
		
		List<String> wardName=new ArrayList<>();
		for(RoomDetails roomDetailsInfo:roomDetails){
			Map<String, String> disp=new HashMap<>();
			String ward=roomDetailsInfo.getRoomType();
			
			if(!wardName.contains(ward))
			{
				
				
				disp.put("roomType", ward);
				room.add(disp);
				wardName.add(ward);
			}
			
		}
		
		map.put("roomType", wardName);
		
		List<String> list=new ArrayList<>();
		list.add("INPATIENT");
		list.add("OUTPATIENT");
		list.add("OSP");
		map.put("patientType", list);
		return map;
	}
	
	@Transactional
	public void computeSave(LabServices labServices,Principal principal){
		
		
		//user Security
		
		User user=userServiceImpl.findByUserName(principal.getName());
		List<Map<String, String>> addService=labServices.getAddService();
		
		
		if(labServices.getServiceName().contains("/"))
		{
			throw new RuntimeException("Service name cann't contain '/'");
		}
		
		addService.forEach((s) -> 
		{
			if(s.get("roomType")==null || s.get("roomType").length()==0  )
			{
				throw new RuntimeException("");
			}
			
		});
		
		if(!patientLabServicesRepository.findByServiceName(labServices.getServiceName()).isEmpty())
		{
			throw new RuntimeException("Service Already Exists!");
		}
		
		
		labServices.setInsertedDate(Timestamp.valueOf(LocalDateTime.now()));
		labServices.setMasterServiceId(getNexMasterId());
		labServices.setUserLabService(user);
		for(Map<String, String> addServiceInfo:addService){
			labServices.setServiceId(getNextId());
			labServices.setRoomtype(addServiceInfo.get("roomType"));
			labServices.setPatientType(addServiceInfo.get("patientType"));
			labServices.setCost(Long.parseLong(addServiceInfo.get("cost")));
			patientLabServicesRepository.save(labServices);
			
		}
		
	}
	

	@Override
	public LabServices findByServiceId(String serviceId) {
		
		return patientLabServicesRepository.findByServiceId(serviceId);
	}
	
	
	
	public List<LabServiceRange> findMeasures(List<LabServices> labServices, int age, String gender){
		List<LabServiceRange> newLabserviceRange=null;
		
		LabServices labServicesInfo=labServicesServiceImpl.findByServiceId(labServices.get(0).getServiceId());
		
		
		if(age>=0 && age<=12 && gender.equalsIgnoreCase("CHILDREN")){
			newLabserviceRange=labServiceRangeRepository.findServices(labServicesInfo.getMasterServiceId(), 0, 12, "CHILDREN");
			}
		
		else if(age>=13 && age<=50 && gender.equalsIgnoreCase("MALE")){
			newLabserviceRange=labServiceRangeRepository.findServices(labServicesInfo.getMasterServiceId(), 13, 50, "Male");

			
		}
		else if(age>=13 && age<=50 && gender.equalsIgnoreCase("FEMALE")){
			newLabserviceRange=labServiceRangeRepository.findServices(labServicesInfo.getMasterServiceId(), 13, 50, "Female");
			}
		else if(age>=50 && age<=100 && gender.equalsIgnoreCase("MALE")){
			newLabserviceRange=labServiceRangeRepository.findServices(labServicesInfo.getMasterServiceId(), 50, 100, "Male");

			
		}
		else if(age>=50 && age<=100 && gender.equalsIgnoreCase("FEMALE")){
			newLabserviceRange=labServiceRangeRepository.findServices(labServicesInfo.getMasterServiceId(), 50, 100, "Female");

			
		}
		
		return newLabserviceRange;
		
	}
	
	public List<LabServiceRange> findNewMeasures(List<LabServices> labServices, int age, String gender,String ageType){
		List<LabServiceRange> labserviceRange=null;
		
		LabServices labServicesInfo=labServicesServiceImpl.findByServiceId(labServices.get(0).getServiceId());
		
		if(labServices.get(0).getServiceName().equalsIgnoreCase("LFT - LIVER FUNCTON TESTS") ||labServices.get(0).getServiceName().equalsIgnoreCase("SERUM BILIRUBIN") ){
		
		if(age>=0 && age<=4 && gender.equalsIgnoreCase("CHILDREN")&&ageType.equalsIgnoreCase("days") &&labServices.get(0).getServiceId()!=null){
			
			labserviceRange=labServiceRangeRepository.findNewServices(labServicesInfo.getMasterServiceId(), 0, 4, "CHILDREN", ageType);
			}
		
		else if(age>4 && age<=5 && gender.equalsIgnoreCase("CHILDREN")&&ageType.equalsIgnoreCase("days")&&labServices.get(0).getServiceId()!=null){
			labserviceRange=labServiceRangeRepository.findNewServices(labServicesInfo.getMasterServiceId(), 4, 5, "CHILDREN", "days");

			
		}
		else if(age>=6 && age<=30 && gender.equalsIgnoreCase("CHILDREN")&&ageType.equalsIgnoreCase("days")&&labServices.get(0).getServiceId()!=null){
			labserviceRange=labServiceRangeRepository.findNewServices(labServicesInfo.getMasterServiceId(), 6, 30, "CHILDREN", "days");

			}
		else if(age>=1 && age<=1800 && gender.equalsIgnoreCase("MALE")&&ageType.equalsIgnoreCase("months")&&labServices.get(0).getServiceId()!=null){
			labserviceRange=labServiceRangeRepository.findNewServices(labServicesInfo.getMasterServiceId(), 1, 1800, "MALE", "months");

			
		}
		else if(age>=1 && age<=1800 && gender.equalsIgnoreCase("FEMALE")&&ageType.equalsIgnoreCase("months")&&labServices.get(0).getServiceId()!=null){

			labserviceRange=labServiceRangeRepository.findNewServices(labServicesInfo.getMasterServiceId(), 1, 1800, "FEMALE", "months");
		}
		else if(age>=0 && age<=1800 && gender.equalsIgnoreCase("MALE")&&ageType.equalsIgnoreCase("months")&&labServices.get(0).getServiceId()!=null){
			labserviceRange=labServiceRangeRepository.findNewServices(labServicesInfo.getMasterServiceId(), 0, 1800, "MALE", "months");

			
		}
		else if(age>=0 && age<=1800 && gender.equalsIgnoreCase("FEMALE")&&ageType.equalsIgnoreCase("months")&&labServices.get(0).getServiceId()!=null){

			labserviceRange=labServiceRangeRepository.findNewServices(labServicesInfo.getMasterServiceId(), 0, 1800, "FEMALE", "months");
		}
		

		}else if(labServices.get(0).getServiceName().equalsIgnoreCase("COMPLETE BLOOD PICTURE")){
			
			if(age>=0 && age<=4 && gender.equalsIgnoreCase("CHILDREN")&&ageType.equalsIgnoreCase("days") &&labServices.get(0).getServiceId()!=null){
				
				labserviceRange=labServiceRangeRepository.findNewServices(labServicesInfo.getMasterServiceId(), 0, 4, "CHILDREN", ageType);
				}
			
			else if(age>=5 && age<=30 && gender.equalsIgnoreCase("CHILDREN")&&ageType.equalsIgnoreCase("days")&&labServices.get(0).getServiceId()!=null){
				labserviceRange=labServiceRangeRepository.findNewServices(labServicesInfo.getMasterServiceId(), 5, 30, "CHILDREN", "days");

				
			}
			else if(age>=1 && age<2 && gender.equalsIgnoreCase("CHILDREN")&&ageType.equalsIgnoreCase("months")&&labServices.get(0).getServiceId()!=null){
				labserviceRange=labServiceRangeRepository.findNewServices(labServicesInfo.getMasterServiceId(), 1, 2, "CHILDREN", "months");

				}
			else if(age>=2 && age<3 && gender.equalsIgnoreCase("CHILDREN")&&ageType.equalsIgnoreCase("months")&&labServices.get(0).getServiceId()!=null){
				labserviceRange=labServiceRangeRepository.findNewServices(labServicesInfo.getMasterServiceId(), 2, 3, "CHILDREN", "months");

				
			}
			else if(age>=3 && age<8 && gender.equalsIgnoreCase("CHILDREN")&&ageType.equalsIgnoreCase("months")&&labServices.get(0).getServiceId()!=null){

				labserviceRange=labServiceRangeRepository.findNewServices(labServicesInfo.getMasterServiceId(), 3, 8, "CHILDREN", "months");
			}
			else if(age>=8 && age<=24 && gender.equalsIgnoreCase("CHILDREN")&&ageType.equalsIgnoreCase("months")&&labServices.get(0).getServiceId()!=null){

				labserviceRange=labServiceRangeRepository.findNewServices(labServicesInfo.getMasterServiceId(), 8, 24, "CHILDREN", "months");
			}

			else if(age>=2 && age<6 && gender.equalsIgnoreCase("CHILDREN")&&ageType.equalsIgnoreCase("years")&&labServices.get(0).getServiceId()!=null){

				labserviceRange=labServiceRangeRepository.findNewServices(labServicesInfo.getMasterServiceId(), 2, 6, "CHILDREN", "years");
			}

			else if(age>=6 && age<=13 && gender.equalsIgnoreCase("CHILDREN")&&ageType.equalsIgnoreCase("years")&&labServices.get(0).getServiceId()!=null){

				labserviceRange=labServiceRangeRepository.findNewServices(labServicesInfo.getMasterServiceId(), 6, 13, "CHILDREN", "years");
			}

			else if(age>=13 && age<150 && gender.equalsIgnoreCase("FEMALE")&&ageType.equalsIgnoreCase("years")&&labServices.get(0).getServiceId()!=null){

				labserviceRange=labServiceRangeRepository.findNewServices(labServicesInfo.getMasterServiceId(), 13, 150, "Female", "years");
			}

			else if(age>=13 && age<150 && gender.equalsIgnoreCase("MALE")&&ageType.equalsIgnoreCase("years")&&labServices.get(0).getServiceId()!=null){

				labserviceRange=labServiceRangeRepository.findNewServices(labServicesInfo.getMasterServiceId(), 13, 150, "Male", "years");
			}


			

			
			
		}
		
		return labserviceRange;
		
	}
	
	public LabServices findByServiceNameAndPatientType(String serviceName,String patName)
	{
		return patientLabServicesRepository.findByServiceNameAndPatientType(serviceName, patName);
	}
	
	public List<LabServices> findOnlyLab(String lab)
	{
		return patientLabServicesRepository.findOnlyLab(lab);
	}
	
	public List<LabServices> findOnlyOthers()
	{
		return patientLabServicesRepository.findOnlyOthers();
	}
	
	@Override
	public List<LabServices> getOspServices(String patientType){
		
		return patientLabServicesRepository.findByPatientType(patientType);
	}


	@Override
	public List<LabServices> servicesForInptient(String type) {
		
		return patientLabServicesRepository.servicesForInptient(type);
	}
	
	
public Map<String, Object> getServicesForDropdown(){
		
		Map<String, Object> map=new HashMap<>();
		
		List<LabServices> labServices=patientLabServicesRepository.findAll();
		
		List<String> serviceName=new ArrayList<>();
		for(LabServices labServicesInfo:labServices ) {
			if(!serviceName.contains(labServicesInfo.getServiceName())) {
				serviceName.add(labServicesInfo.getServiceName());
				
			}
			
		}
		map.put("serviceName", serviceName);
		return map;
	}
	
	
	
	@Override
	public void updateService(LabServices labServices,Principal principal,String serviceName) {
		
		//security
		
		User user=userServiceImpl.findByUserName(principal.getName());
		String userId=user.getUserId();
		
		List<LabServices> labServicesInfo=patientLabServicesRepository.findByServiceName(serviceName);
		for(LabServices labs:labServicesInfo) {
			
			if(labServices.getDepartment()!=null) {
				labs.setDepartment(labServices.getDepartment());	
			}else {
				labs.setDepartment(labs.getDepartment());
				
			}
			
			if(labServices.getServiceType()!=null) {
				labs.setServiceType(labServices.getServiceType());	
			}else {
				labs.setServiceType(labs.getServiceType());
				
			}
		}
		
		
		
		List<Map<String, String>> addService=labServices.getAddService();
		
		for(Map<String, String> addServiceInfo:addService) {
			
			String serviceId=addServiceInfo.get("serviceId");
			
			LabServices lab=patientLabServicesRepository.findByServiceId(serviceId);
			lab.setServiceName(labServices.getServiceName());
			lab.setRoomtype(lab.getRoomtype());
			lab.setPatientType(addServiceInfo.get("patientType"));
			lab.setCost(Long.parseLong(addServiceInfo.get("cost")));
			lab.setUserLabService(user);
			lab.setCreatedBy(userId);
			patientLabServicesRepository.save(lab);
			
		}
	
		
	}
	
	
	
	
	
	
	@Override
	public List<Object> findServices(){
		List<Object> list=new ArrayList<>();
		
		String serviceName=null;
		
		List<LabServices> labServices=patientLabServicesRepository.findAll();
		List<String> serviceNameInfo=new ArrayList<>();
		for(LabServices labServicesInfo:labServices) {
			serviceName=labServicesInfo.getServiceName();
			if(!serviceNameInfo.contains(serviceName)) {
			
				Map<String, String> map=new HashMap<>();
				map.put("masterSeviceId", labServicesInfo.getMasterServiceId());
				map.put("serviceName", serviceName);
				map.put("department", labServicesInfo.getDepartment());
				map.put("serviceType", labServicesInfo.getServiceType());
				serviceNameInfo.add(serviceName);
				list.add(map);
			}
			
		}
		
		return list;
		
	}
	
	
}
