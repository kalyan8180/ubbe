package com.example.test.testingHMS.taskSchedular;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import net.sf.ehcache.CacheManager;



@Component
public class CacheTaskSchedular {
	
	@Autowired
	CacheManager cacheManager;


// 1sec 1000	
// 1min 60,000
// 1hr  3,600,000
// 3hr  10,800,000

	
	/*
	 *  It will remove all generated cache for
	 *  every 3 hour 		
	 */
 	@Scheduled(fixedRate=10800000) // 3 Hours
	public void executeTask()
	{
		String[] cacheNames = cacheManager.getCacheNames();

		for(String name:cacheNames)
		{
			cacheManager.getCache(name).removeAll();
		}
	
	}

}
