package com.example.test.testingHMS.security.helper;

import org.springframework.security.crypto.password.PasswordEncoder;


import com.example.test.testingHMS.user.model.PasswordStuff;

public class PasswordEncodeUtil {
	
	
	public static void encryptedPasswordStuff(PasswordEncoder passwordEncoder,PasswordStuff passwordStuff) {
		passwordStuff.setPassword(passwordEncoder.encode(passwordStuff.getPassword()));
		passwordStuff.setConfirmPassword(passwordEncoder.encode(passwordStuff.getConfirmPassword()));
		//passwordStuff.setTxnPassword(passwordEncoder.encode(passwordStuff.getTxnPassword()));
		//passwordStuff.setConfirmTxnPassword(passwordEncoder.encode(passwordStuff.getConfirmTxnPassword()));
		
		
	}
}
