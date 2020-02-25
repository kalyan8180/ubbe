package com.example.test.testingHMS.patient.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.test.testingHMS.patient.model.ReferralDetails;
import com.example.test.testingHMS.patient.repository.ReferralDetailsRepository;
import com.example.test.testingHMS.patient.service.ReferralDetailsService;


@Service
public class ReferralDetailsServiceImpl implements ReferralDetailsService 
{
	@Autowired
	ReferralDetailsRepository repo;
	
	public ReferralDetails save(ReferralDetails referralDetails)
	{
		return repo.save(referralDetails);
	}
	
	public Optional<ReferralDetails> findById(Long id)
	{
		return repo.findById(id);
	}
	
	public void delte(Long id)
	{
		 repo.deleteById(id);
	}
	
	public ReferralDetails update(ReferralDetails referralDetails)
	{
		return repo.save(referralDetails);
	}

	@Override
	public List<ReferralDetails> findAll() {
		return repo.findAll();
	}
	
	public List<ReferralDetails> findBySource(String name)
	{
		return repo.findBySource(name);
	}
	
	public ReferralDetails findBySourceAndRefName(String source,String refName)
	{
		return repo.findBySourceAndRefName(source, refName);
	}
	
	public List<ReferralDetails> findDistinct()
	{
		return repo.findDistinct();
	}

	@Override
	public ReferralDetails findByRefId(Long refId) {
		return repo.findByRefId(refId);
	}
}
