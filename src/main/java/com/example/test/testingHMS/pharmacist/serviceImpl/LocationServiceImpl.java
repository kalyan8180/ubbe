package com.example.test.testingHMS.pharmacist.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.test.testingHMS.pharmacist.model.Location;
import com.example.test.testingHMS.pharmacist.repository.LocationRepository;
import com.example.test.testingHMS.pharmacist.service.LocationService;
@Service
public class LocationServiceImpl implements LocationService 
{
	@Autowired
	LocationRepository locationRepository;
	

	public Location findByLocationName(String name)
	{
		return locationRepository.findByLocationName(name);
	}


}
