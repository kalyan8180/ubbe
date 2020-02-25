package com.example.test.testingHMS.security;

import com.example.test.testingHMS.user.model.User;
import com.example.test.testingHMS.user.service.UserPrincipal;
import com.example.test.testingHMS.user.service.UserService;
import com.example.test.testingHMS.user.serviceImpl.UserServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	
    @Autowired
    UserService userService;
    
    @Autowired
    UserServiceImpl userServiceImpl;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String usernameOrEmail)
            throws UsernameNotFoundException {
    	User user = null;
    			

		user=userServiceImpl.findByUserName(usernameOrEmail);
    /*
    	if(user==null)
    	{
    		user=userService.findOneByUserId(usernameOrEmail);
    		System.out.println(usernameOrEmail);
    	}
    */	
    	return UserPrincipal.create(user);
    }
}
