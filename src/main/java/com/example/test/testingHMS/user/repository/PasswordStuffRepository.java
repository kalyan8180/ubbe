package com.example.test.testingHMS.user.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.test.testingHMS.user.model.PasswordStuff;
import com.example.test.testingHMS.user.model.User;

@Repository
public interface PasswordStuffRepository extends CrudRepository<PasswordStuff,Long>{
	
	PasswordStuff findFirstByOrderByPasswordIdDesc();
	
	PasswordStuff findByUser(User id);
	

}
