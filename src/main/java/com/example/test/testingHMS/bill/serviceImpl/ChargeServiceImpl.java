/*package com.example.test.testingHMS.bill.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.test.testingHMS.bill.model.Charge;
import com.example.test.testingHMS.bill.repository.ChargeRepository;
import com.example.test.testingHMS.bill.service.ChargeService;

@Service
public class ChargeServiceImpl implements ChargeService {
	
	@Autowired
	ChargeRepository chargeRepository;
	
	@Override
	public String getNextId() {
		Charge charge=chargeRepository.findFirstByOrderByChargeIdDesc();
		String nextId=null;
		if(charge==null)
		{
			nextId="CH0000001";
		}
		else
		{
			int nextIntId=Integer.parseInt(charge.getChargeId().substring(2));
			nextIntId+=1;
			nextId="CH"+String.format("%07d", nextIntId);
		}
		return nextId;
	}

	public Charge findByName(String name)
	{
		return chargeRepository.findByName(name);
	}
	
	public  List<Charge> findAll()
	{
		return chargeRepository.findAll();
	}

}
*/