package com.example.test.testingHMS.laboratory.service;

import java.security.Principal;

import com.example.test.testingHMS.laboratory.model.MasterCheckUpRegistration;
import com.example.test.testingHMS.pharmacist.model.SalesPaymentPdf;

public interface MasterCheckUpRegistrationService {


	SalesPaymentPdf registerMastercheckupServices(MasterCheckUpRegistration masterCheckUpRegistration,
			Principal principal);

}
