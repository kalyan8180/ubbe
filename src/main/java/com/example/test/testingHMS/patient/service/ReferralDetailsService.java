package com.example.test.testingHMS.patient.service;

import java.util.List;
import java.util.Optional;

import com.example.test.testingHMS.patient.model.ReferralDetails;



public interface ReferralDetailsService 
{	
	public ReferralDetails save(ReferralDetails referralDetails);
	
	public Optional<ReferralDetails> findById(Long id);
	
	public void delte(Long id);
	
	public ReferralDetails update(ReferralDetails referralDetails);
	
	public List<ReferralDetails> findAll();
	
	public List<ReferralDetails> findBySource(String name);
	
	public List<ReferralDetails> findDistinct();
	
	ReferralDetails findByRefId(Long refId);
	
	public ReferralDetails findBySourceAndRefName(String source,String refName);
	
}
