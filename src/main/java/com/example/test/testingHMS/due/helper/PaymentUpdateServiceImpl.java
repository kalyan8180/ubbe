package com.example.test.testingHMS.due.helper;

import java.security.Principal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.test.testingHMS.bill.model.ChargeBill;
import com.example.test.testingHMS.bill.repository.ChargeBillRepository;
import com.example.test.testingHMS.bill.serviceImpl.ChargeBillServiceImpl;
import com.example.test.testingHMS.finalBilling.model.FinalBilling;
import com.example.test.testingHMS.finalBilling.repository.FinalBillingRepository;
import com.example.test.testingHMS.finalBilling.serviceImpl.FinalBillingServiceImpl;
import com.example.test.testingHMS.laboratory.model.LaboratoryRegistration;
import com.example.test.testingHMS.laboratory.repository.LaboratoryRegistrationRepository;
import com.example.test.testingHMS.osp.model.OspService;
import com.example.test.testingHMS.osp.repository.OspServiceRepository;
import com.example.test.testingHMS.patient.Helper.MultiplePayment;
import com.example.test.testingHMS.patient.model.PatientPayment;
import com.example.test.testingHMS.patient.model.PatientRegistration;
import com.example.test.testingHMS.patient.repository.PatientPaymentRepository;
import com.example.test.testingHMS.pharmacist.model.Sales;
import com.example.test.testingHMS.pharmacist.model.SalesReturn;
import com.example.test.testingHMS.pharmacist.repository.SalesRepository;
import com.example.test.testingHMS.pharmacist.repository.SalesReturnRepository;
import com.example.test.testingHMS.user.model.User;
import com.example.test.testingHMS.user.serviceImpl.UserServiceImpl;
import com.example.test.testingHMS.utils.ConstantValues;


@Component
public class PaymentUpdateServiceImpl {
	@Autowired
	UserServiceImpl userServiceImpl;

	@Autowired
	SalesRepository salesRepository;

	@Autowired
	OspServiceRepository ospServiceRepository;

	@Autowired
	SalesReturnRepository salesReturnRepository;

	@Autowired
	ChargeBillServiceImpl chargeBillServiceImpl;

	@Autowired
	FinalBillingServiceImpl finalBillingServcieImpl;

	@Autowired
	FinalBillingRepository finalBillingRepository;

	@Autowired
	PatientPaymentRepository patientPaymentRepository;

	/*
	 * @Autowired PatientSalesRepository patientSalesRepository;
	 */
	@Autowired
	ChargeBillRepository chargeBillRepository;

	@Autowired
	LaboratoryRegistrationRepository laboratoryRegistrationRepository;

	public List<Object> getperticularbill(Map<String, String> mapInfo) {

		String dueType = mapInfo.get("billType");
		String billNo = mapInfo.get("billNo");
		List<Object> Allllist = new ArrayList<>();

		if (dueType.equalsIgnoreCase("Osp Bill")) {

			List<FinalBilling> finalBillingUpdate = finalBillingServcieImpl.findByBillTypeAndBillNo("Osp Bill", billNo);

			// Map<String, String> saleMap = new HashMap<>();
			for (FinalBilling finalbill : finalBillingUpdate) {
				Map<String, String> saleMap = new HashMap<>();
				saleMap.put("regNo", finalbill.getRegNo());
				saleMap.put("billNo", finalbill.getBillNo());
				saleMap.put("cash", String.valueOf(finalbill.getCashAmount()));
				saleMap.put("card", String.valueOf(finalbill.getCardAmount()));
				saleMap.put("cheque", String.valueOf(finalbill.getChequeAmount()));
				saleMap.put("due", String.valueOf(finalbill.getDueAmount()));
				saleMap.put("date", finalbill.getUpdatedDate().toString());
				saleMap.put("finalAmount", String.valueOf(finalbill.getFinalAmountPaid()));
				saleMap.put("totalAmount", String.valueOf(finalbill.getTotalAmount()));
				Allllist.add(saleMap);
			}

		}

		else if (dueType.equalsIgnoreCase("Patient Registration")) {

			List<FinalBilling> finalBillingUpdate = finalBillingServcieImpl
					.findByBillTypeAndBillNo("Patient Registration", billNo);

			// Map<String, String> saleMap = new HashMap<>();
			for (FinalBilling finalbill : finalBillingUpdate) {
				Map<String, String> saleMap = new HashMap<>();
				saleMap.put("regNo", finalbill.getRegNo());
				saleMap.put("billNo", finalbill.getBillNo());
				saleMap.put("cash", String.valueOf(finalbill.getCashAmount()));
				saleMap.put("card", String.valueOf(finalbill.getCardAmount()));
				saleMap.put("cheque", String.valueOf(finalbill.getChequeAmount()));
				saleMap.put("due", String.valueOf(finalbill.getDueAmount()));
				saleMap.put("finalAmount", String.valueOf(finalbill.getFinalAmountPaid()));
				saleMap.put("totalAmount", String.valueOf(finalbill.getTotalAmount()));
				saleMap.put("date", finalbill.getUpdatedDate().toString());
				Allllist.add(saleMap);
			}

		} else if (dueType.equalsIgnoreCase("Sales")) {

			List<FinalBilling> finalBillingUpdate = finalBillingServcieImpl.findByBillTypeAndBillNo("Sales", billNo);

			// Map<String, String> saleMap = new HashMap<>();
			for (FinalBilling finalbill : finalBillingUpdate) {
				Map<String, String> saleMap = new HashMap<>();
				saleMap.put("regNo", finalbill.getRegNo());
				saleMap.put("billNo", finalbill.getBillNo());
				saleMap.put("cash", String.valueOf(finalbill.getCashAmount()));
				saleMap.put("card", String.valueOf(finalbill.getCardAmount()));
				saleMap.put("cheque", String.valueOf(finalbill.getChequeAmount()));
				saleMap.put("due", String.valueOf(finalbill.getDueAmount()));
				saleMap.put("finalAmount", String.valueOf(finalbill.getFinalAmountPaid()));
				saleMap.put("totalAmount", String.valueOf(finalbill.getTotalAmount()));
				saleMap.put("date", finalbill.getUpdatedDate().toString());
				Allllist.add(saleMap);
			}

		} else if (dueType.equalsIgnoreCase("Laboratory Registration")) {

			List<FinalBilling> finalBillingUpdate = finalBillingServcieImpl
					.findByBillTypeAndBillNo("Laboratory Registration", billNo);

			// Map<String, String> saleMap = new HashMap<>();
			for (FinalBilling finalbill : finalBillingUpdate) {
				Map<String, String> saleMap = new HashMap<>();
				saleMap.put("regNo", finalbill.getRegNo());
				saleMap.put("billNo", finalbill.getBillNo());
				saleMap.put("cash", String.valueOf(finalbill.getCashAmount()));
				saleMap.put("card", String.valueOf(finalbill.getCardAmount()));
				saleMap.put("cheque", String.valueOf(finalbill.getChequeAmount()));
				saleMap.put("due", String.valueOf(finalbill.getDueAmount()));
				saleMap.put("finalAmount", String.valueOf(finalbill.getFinalAmountPaid()));
				saleMap.put("totalAmount", String.valueOf(finalbill.getTotalAmount()));
				saleMap.put("date", finalbill.getUpdatedDate().toString());
				Allllist.add(saleMap);
			}

		} else if (dueType.equalsIgnoreCase("IP Advance Payment")) {

			List<FinalBilling> finalBillingUpdate = finalBillingServcieImpl
					.findByBillTypeAndBillNo("IP Advance Payment", billNo);

			// Map<String, String> saleMap = new HashMap<>();
			for (FinalBilling finalbill : finalBillingUpdate) {
				Map<String, String> saleMap = new HashMap<>();
				saleMap.put("regNo", finalbill.getRegNo());
				saleMap.put("billNo", finalbill.getBillNo());
				saleMap.put("cash", String.valueOf(finalbill.getCashAmount()));
				saleMap.put("card", String.valueOf(finalbill.getCardAmount()));
				saleMap.put("cheque", String.valueOf(finalbill.getChequeAmount()));
				saleMap.put("due", String.valueOf(finalbill.getDueAmount()));
				saleMap.put("finalAmount", String.valueOf(finalbill.getFinalAmountPaid()));
				saleMap.put("totalAmount", String.valueOf(finalbill.getTotalAmount()));
				saleMap.put("date", finalbill.getUpdatedDate().toString());
				Allllist.add(saleMap);
			}
		} else if (dueType.equalsIgnoreCase("Ip Final Billing")) {

			List<FinalBilling> finalBillingUpdate = finalBillingServcieImpl.findByBillTypeAndBillNo("Ip Final Billing",
					billNo);

			// Map<String, String> saleMap = new HashMap<>();
			for (FinalBilling finalbill : finalBillingUpdate) {
				Map<String, String> saleMap = new HashMap<>();
				saleMap.put("regNo", finalbill.getRegNo());
				saleMap.put("billNo", finalbill.getBillNo());
				saleMap.put("cash", String.valueOf(finalbill.getCashAmount()));
				saleMap.put("card", String.valueOf(finalbill.getCardAmount()));
				saleMap.put("cheque", String.valueOf(finalbill.getChequeAmount()));
				saleMap.put("due", String.valueOf(finalbill.getDueAmount()));
				saleMap.put("finalAmount", String.valueOf(finalbill.getFinalAmountPaid()));
				saleMap.put("totalAmount", String.valueOf(finalbill.getTotalAmount()));
				saleMap.put("date", finalbill.getUpdatedDate().toString());
				Allllist.add(saleMap);
			}

		} else if (dueType.equalsIgnoreCase(ConstantValues.SALES_DUE)) {

			List<FinalBilling> finalBillingUpdate = finalBillingServcieImpl
					.findByBillTypeAndBillNo(ConstantValues.SALES_DUE, billNo);

			// Map<String, String> saleMap = new HashMap<>();
			for (FinalBilling finalbill : finalBillingUpdate) {
				Map<String, String> saleMap = new HashMap<>();
				saleMap.put("regNo", finalbill.getRegNo());
				saleMap.put("billNo", finalbill.getBillNo());
				saleMap.put("cash", String.valueOf(finalbill.getCashAmount()));
				saleMap.put("card", String.valueOf(finalbill.getCardAmount()));
				saleMap.put("cheque", String.valueOf(finalbill.getChequeAmount()));
				saleMap.put("due", String.valueOf(finalbill.getDueAmount()));
				saleMap.put("finalAmount", String.valueOf(finalbill.getFinalAmountPaid()));
				saleMap.put("totalAmount", String.valueOf(finalbill.getTotalAmount()));
				saleMap.put("date", finalbill.getUpdatedDate().toString());
				Allllist.add(saleMap);
			}

		}

		else if (dueType.equalsIgnoreCase(ConstantValues.LAB_DUE)) {

			List<FinalBilling> finalBillingUpdate = finalBillingServcieImpl
					.findByBillTypeAndBillNo(ConstantValues.LAB_DUE, billNo);

			// Map<String, String> saleMap = new HashMap<>();
			for (FinalBilling finalbill : finalBillingUpdate) {
				Map<String, String> saleMap = new HashMap<>();
				saleMap.put("regNo", finalbill.getRegNo());
				saleMap.put("billNo", finalbill.getBillNo());
				saleMap.put("cash", String.valueOf(finalbill.getCashAmount()));
				saleMap.put("card", String.valueOf(finalbill.getCardAmount()));
				saleMap.put("cheque", String.valueOf(finalbill.getChequeAmount()));
				saleMap.put("due", String.valueOf(finalbill.getDueAmount()));
				saleMap.put("finalAmount", String.valueOf(finalbill.getFinalAmountPaid()));
				saleMap.put("totalAmount", String.valueOf(finalbill.getTotalAmount()));
				saleMap.put("date", finalbill.getUpdatedDate().toString());
				Allllist.add(saleMap);
			}

		}

		else if (dueType.equalsIgnoreCase(ConstantValues.OSP_DUE)) {

			List<FinalBilling> finalBillingUpdate = finalBillingServcieImpl
					.findByBillTypeAndBillNo(ConstantValues.OSP_DUE, billNo);

			// Map<String, String> saleMap = new HashMap<>();
			for (FinalBilling finalbill : finalBillingUpdate) {
				Map<String, String> saleMap = new HashMap<>();
				saleMap.put("regNo", finalbill.getRegNo());
				saleMap.put("billNo", finalbill.getBillNo());
				saleMap.put("cash", String.valueOf(finalbill.getCashAmount()));
				saleMap.put("card", String.valueOf(finalbill.getCardAmount()));
				saleMap.put("cheque", String.valueOf(finalbill.getChequeAmount()));
				saleMap.put("due", String.valueOf(finalbill.getDueAmount()));
				saleMap.put("finalAmount", String.valueOf(finalbill.getFinalAmountPaid()));
				saleMap.put("totalAmount", String.valueOf(finalbill.getTotalAmount()));
				saleMap.put("date", finalbill.getUpdatedDate().toString());
				Allllist.add(saleMap);
			}

		} else if (dueType.equalsIgnoreCase("Ip Sales Return")) {

			List<FinalBilling> finalBillingUpdate = finalBillingServcieImpl.findByBillTypeAndBillNo("Ip Sales Return",
					billNo);

			// Map<String, String> saleMap = new HashMap<>();
			for (FinalBilling finalbill : finalBillingUpdate) {
				Map<String, String> saleMap = new HashMap<>();
				saleMap.put("regNo", finalbill.getRegNo());
				saleMap.put("billNo", finalbill.getBillNo());
				saleMap.put("cash", String.valueOf(finalbill.getCashAmount()));
				saleMap.put("card", String.valueOf(finalbill.getCardAmount()));
				saleMap.put("cheque", String.valueOf(finalbill.getChequeAmount()));
				saleMap.put("due", String.valueOf(finalbill.getDueAmount()));
				saleMap.put("finalAmount", String.valueOf(finalbill.getFinalAmountPaid()));
				saleMap.put("totalAmount", String.valueOf(finalbill.getTotalAmount()));
				saleMap.put("date", finalbill.getUpdatedDate().toString());
				Allllist.add(saleMap);
			}

		} else if (dueType.equalsIgnoreCase("Sales Return")) {

			List<FinalBilling> finalBillingUpdate = finalBillingServcieImpl.findByBillTypeAndBillNo("Sales Return",
					billNo);

			// Map<String, String> saleMap = new HashMap<>();
			for (FinalBilling finalbill : finalBillingUpdate) {
				Map<String, String> saleMap = new HashMap<>();
				saleMap.put("regNo", finalbill.getRegNo());
				saleMap.put("billNo", finalbill.getBillNo());
				saleMap.put("cash", String.valueOf(finalbill.getCashAmount()));
				saleMap.put("card", String.valueOf(finalbill.getCardAmount()));
				saleMap.put("cheque", String.valueOf(finalbill.getChequeAmount()));
				saleMap.put("due", String.valueOf(finalbill.getDueAmount()));
				saleMap.put("finalAmount", String.valueOf(finalbill.getFinalAmountPaid()));
				saleMap.put("totalAmount", String.valueOf(finalbill.getTotalAmount()));
				saleMap.put("date", finalbill.getUpdatedDate().toString());
				Allllist.add(saleMap);
			}
		}
		return Allllist;
	}
	
	
	
	
	@Transactional
	public void updateAll( DueHelper dueHelper,  String billNo,
			Principal principal) {

		float finalCash = 0; // final billing
		float finalCard = 0; // final billing
		float finalCheque = 0; // final billing
		float finalDue = 0; // final billing
		float finalNetAmount = 0;
		float totalAmount = 0;
		float discount = 0;

		String payCash = null;
		String payCard = null;
		String payDue = null;
		String payCheque = null;
		String paymentType = null;
		String paid = null;
		String referenceNumber = null;

		User userSecurity = userServiceImpl.findByUserName(principal.getName());
		String createdBy = userSecurity.getFirstName() + " " + userSecurity.getLastName();
		String createdid = userSecurity.getUserId();

		
		String date = dueHelper.getDate().toString().substring(0, 15);
		List<MultiplePayment> multiplePayment = dueHelper.getMultiplePayment();

		for (MultiplePayment multiplePaymentInfo : multiplePayment) {

			if (multiplePaymentInfo.getPayType().equalsIgnoreCase(ConstantValues.CARD)
					|| multiplePaymentInfo.getPayType().equalsIgnoreCase("Credit Card")
					|| multiplePaymentInfo.getPayType().equalsIgnoreCase("Debit Card")
					|| multiplePaymentInfo.getPayType().equalsIgnoreCase(ConstantValues.CASH_PLUS_CARD)) {

				referenceNumber = dueHelper.getReferenceNumber();
			}

			if (multiplePaymentInfo.getPayType().equalsIgnoreCase(ConstantValues.CASH)) {
				finalCash = multiplePaymentInfo.getAmount();
				System.out.println(finalCash);
				payCash = ConstantValues.CASH;
			} else if (multiplePaymentInfo.getPayType().equalsIgnoreCase(ConstantValues.CARD)) {
				finalCard = multiplePaymentInfo.getAmount();
				payCard = ConstantValues.CARD;
			} else if (multiplePaymentInfo.getPayType().equalsIgnoreCase(ConstantValues.CHEQUE)) {
				finalCheque = multiplePaymentInfo.getAmount();
				payCheque = ConstantValues.CHEQUE;
			} else if (multiplePaymentInfo.getPayType().equalsIgnoreCase(ConstantValues.DUE)) {
				finalDue = multiplePaymentInfo.getAmount();
				payDue = ConstantValues.DUE;
			}

		}

		// for paytype and paid condition
		if (payDue != null && payCash == null && payCard == null) {
			paid = ConstantValues.NO;
			paymentType = ConstantValues.DUE;
		} else if (payCash != null && payDue == null && payCard == null) {
			paid = ConstantValues.YES;
			paymentType = ConstantValues.CASH;

		} else if (payCard != null && payDue == null && payCash == null) {
			paid = ConstantValues.YES;
			paymentType = ConstantValues.CARD;
		} else {
			paid = ConstantValues.PARTLY_PAID;
			paymentType = ConstantValues.MULTIPLE_PAYMENT;

		}

		System.out.println(paymentType);
		String modeOfPayment = paymentType;
		String dueType = dueHelper.getDueFor();
		System.out.println(dueType);
		if (dueType.equalsIgnoreCase("Osp Bill")) {

			List<OspService> ospinfo = ospServiceRepository.findByBillNo(billNo);

			for (OspService osp : ospinfo) {

				osp.setPaymentType(modeOfPayment);
				osp.setPaid(paid);
				// osp.setNetAmount(netAmount);
				ospServiceRepository.save(osp);
			}

			List<FinalBilling> finalBillingUpdate = finalBillingServcieImpl.findByBillTypeAndBillNo("Osp Bill", billNo);

			for (FinalBilling finalBilling : finalBillingUpdate) {
				
				if(finalDue!=0) {
					 finalBilling.setDueStatus(ConstantValues.YES);
				 }else {
					 finalBilling.setDueStatus(ConstantValues.NO);
				 }
				finalBilling.setCardAmount(Math.round(finalCard));
				finalBilling.setCashAmount(Math.round(finalCash));
				finalBilling.setChequeAmount(Math.round(finalCheque));
				finalBilling.setDueAmount(finalDue);
				finalBilling.setPaymentType(modeOfPayment);

				finalBillingServcieImpl.computeSave(finalBilling);

			}
		} else if (dueType.equalsIgnoreCase("Patient Registration")) {

			List<PatientPayment> patientpayment = patientPaymentRepository.findByBillNo(billNo);

			PatientRegistration pregid = null;
			for (PatientPayment patient : patientpayment) {
				pregid = patient.getPatientRegistration();
				patient.setModeOfPaymant(modeOfPayment);
				patient.setPaid(paid);
				// osp.setNetAmount(netAmount);
				patientPaymentRepository.save(patient);
			}

			List<FinalBilling> finalBillingUpdate = finalBillingServcieImpl
					.findByBillTypeAndBillNo("Patient Registration", billNo);

			for (FinalBilling finalBilling : finalBillingUpdate) {
				if(finalDue!=0) {
					 finalBilling.setDueStatus(ConstantValues.YES);
				 }else {
					 finalBilling.setDueStatus(ConstantValues.NO);
				 }
				finalBilling.setCardAmount(Math.round(finalCard));
				finalBilling.setCashAmount(Math.round(finalCash));
				finalBilling.setChequeAmount(Math.round(finalCheque));
				finalBilling.setDueAmount(finalDue);
				finalBilling.setPaymentType(modeOfPayment);

				finalBillingServcieImpl.computeSave(finalBilling);

			}

		} else if (dueType.equalsIgnoreCase("Sales")) {

			List<Sales> sales = salesRepository.findByBillNo(billNo);

			PatientRegistration patientRegistration = null;
			for (Sales sale : sales) {
				patientRegistration = sale.getPatientRegistration();

				sale.setPaymentType(modeOfPayment);
				sale.setPaid(paid);
				salesRepository.save(sale);

				if (patientRegistration != null
						&& (patientRegistration.getPatientType().getpType().equalsIgnoreCase(ConstantValues.INPATIENT)||
								patientRegistration.getPatientType().getpType().equalsIgnoreCase(ConstantValues.EMERGENCY)||
								patientRegistration.getPatientType().getpType().equalsIgnoreCase(ConstantValues.DAYCARE))) {
					ChargeBill chargebilles = chargeBillRepository.findBySaleId(sale);
					chargebilles.setPaymentType(modeOfPayment);
					chargebilles.setPaid(paid);
					chargeBillRepository.save(chargebilles);
				}

			}

			/*
			 * if (patientRegistration != null) {
			 * 
			 * List<PatientSales> patientsales =
			 * patientSalesRepository.findBySalesBillNo(billNo); for (PatientSales
			 * patientsale : patientsales) { patientsale.setPaymentType(modeOfPayment);
			 * patientsale.setPaid(paid); patientSalesRepository.save(patientsale);
			 * 
			 * }
			 * 
			 * }
			 */
			List<FinalBilling> finalBillingUpdate = finalBillingServcieImpl.findByBillTypeAndBillNo("Sales", billNo);

			for (FinalBilling finalBilling : finalBillingUpdate) {
				if(finalDue!=0) {
					 finalBilling.setDueStatus(ConstantValues.YES);
				 }else {
					 finalBilling.setDueStatus(ConstantValues.NO);
				 }
				finalBilling.setCardAmount(Math.round(finalCard));
				finalBilling.setCashAmount(Math.round(finalCash));
				finalBilling.setChequeAmount(Math.round(finalCheque));
				finalBilling.setDueAmount(finalDue);
				finalBilling.setPaymentType(modeOfPayment);

				finalBillingServcieImpl.computeSave(finalBilling);

			}

		} else if (dueType.equalsIgnoreCase("Laboratory Registration")) {

			List<LaboratoryRegistration> labes = laboratoryRegistrationRepository.findByBillNo(billNo);

			PatientRegistration patientRegistration = null;
			for (LaboratoryRegistration lab : labes) {
				patientRegistration = lab.getLaboratoryPatientRegistration();
				System.out.println(modeOfPayment);
				lab.setPaymentType(modeOfPayment);
				lab.setPaid(paid);
				laboratoryRegistrationRepository.save(lab);

				if (patientRegistration != null
						&&( patientRegistration.getPatientType().getpType().equalsIgnoreCase(ConstantValues.INPATIENT)||
								patientRegistration.getPatientType().getpType().equalsIgnoreCase(ConstantValues.DAYCARE)||
								patientRegistration.getPatientType().getpType().equalsIgnoreCase(ConstantValues.EMERGENCY))) {
					ChargeBill chargeBillLab = (lab.getLabServices().getServiceType().equalsIgnoreCase("Lab"))
							? chargeBillServiceImpl.findByLabId(lab)
							: chargeBillServiceImpl.findByServiceId(lab.getLabServices());

					if (chargeBillLab != null) {
						chargeBillLab.setPaid(paid);
						chargeBillLab.setPaymentType(modeOfPayment);
						chargeBillRepository.save(chargeBillLab);
					}
				}

			}

			List<FinalBilling> finalBillingUpdate = finalBillingServcieImpl
					.findByBillTypeAndBillNo("Laboratory Registration", billNo);

			for (FinalBilling finalBilling : finalBillingUpdate) {
				if(finalDue!=0) {
					 finalBilling.setDueStatus(ConstantValues.YES);
				 }else {
					 finalBilling.setDueStatus(ConstantValues.NO);
				 }
				finalBilling.setCardAmount(Math.round(finalCard));
				finalBilling.setCashAmount(Math.round(finalCash));
				finalBilling.setChequeAmount(Math.round(finalCheque));
				finalBilling.setDueAmount(finalDue);
				finalBilling.setPaymentType(modeOfPayment);

				finalBillingServcieImpl.computeSave(finalBilling);

			}

		} else if (dueType.equalsIgnoreCase("IP Advance Payment")) {

			List<PatientPayment> patientpayment = patientPaymentRepository.findByBillNo(billNo);

			for (PatientPayment patient : patientpayment) {
				patient.setModeOfPaymant(modeOfPayment);
				patient.setPaid(paid);
				patientPaymentRepository.save(patient);
			}

			List<FinalBilling> finalBillingUpdate = finalBillingServcieImpl
					.findByBillTypeAndBillNo("IP Advance Payment", billNo);

			for (FinalBilling finalBilling : finalBillingUpdate) {
				finalBilling.setCardAmount(Math.round(finalCard));
				finalBilling.setCashAmount(Math.round(finalCash));
				finalBilling.setChequeAmount(Math.round(finalCheque));
				finalBilling.setDueAmount(finalDue);
				finalBilling.setPaymentType(modeOfPayment);

				finalBillingServcieImpl.computeSave(finalBilling);

			}

		} else if (dueType.equalsIgnoreCase("Ip Final Billing")) {

			FinalBilling finalBilling = finalBillingRepository.paymentUpdate("Ip Final Billing", billNo, date);
			if(finalDue!=0) {
				 finalBilling.setDueStatus(ConstantValues.YES);
			 }else {
				 finalBilling.setDueStatus(ConstantValues.NO);
			 }

				finalBilling.setCardAmount(Math.round(finalCard));
				finalBilling.setCashAmount(Math.round(finalCash));
				finalBilling.setChequeAmount(Math.round(finalCheque));
				finalBilling.setDueAmount(finalDue);
				finalBilling.setPaymentType(modeOfPayment);

				finalBillingServcieImpl.computeSave(finalBilling);


		} else if (dueType.equalsIgnoreCase(ConstantValues.SALES_DUE)) {

			List<Sales> sales = salesRepository.findByBillNo(billNo);

			PatientRegistration patientRegistration = null;
			for (Sales sale : sales) {
				patientRegistration = sale.getPatientRegistration();

				sale.setPaymentType(modeOfPayment);
				sale.setPaid(paid);
				salesRepository.save(sale);

				if (patientRegistration != null
						&& (patientRegistration.getPatientType().getpType().equalsIgnoreCase(ConstantValues.INPATIENT)||
								patientRegistration.getPatientType().getpType().equalsIgnoreCase(ConstantValues.DAYCARE)||
								patientRegistration.getPatientType().getpType().equalsIgnoreCase(ConstantValues.EMERGENCY))) {
					ChargeBill chargebilles = chargeBillRepository.findBySaleId(sale);
					chargebilles.setPaymentType(modeOfPayment);
					chargebilles.setPaid(paid);
					chargeBillRepository.save(chargebilles);
				}

			}

			/*
			 * if (patientRegistration != null) {
			 * 
			 * List<PatientSales> patientsales =
			 * patientSalesRepository.findBySalesBillNo(billNo); for (PatientSales
			 * patientsale : patientsales) { patientsale.setPaymentType(modeOfPayment);
			 * patientsale.setPaid(paid); patientSalesRepository.save(patientsale);
			 * 
			 * }
			 * 
			 * }
			 */
			FinalBilling finalBilling = finalBillingRepository.paymentUpdate(ConstantValues.SALES_DUE, billNo, date);
			if(finalDue!=0) {
				 finalBilling.setDueStatus(ConstantValues.YES);
			 }else {
				 finalBilling.setDueStatus(ConstantValues.NO);
			 }
			
			finalBilling.setCardAmount(Math.round(finalCard));
			finalBilling.setCashAmount(Math.round(finalCash));
			finalBilling.setChequeAmount(Math.round(finalCheque));
			finalBilling.setDueAmount(finalDue);
			finalBilling.setPaymentType(modeOfPayment);

			finalBillingServcieImpl.computeSave(finalBilling);
		} else if (dueType.equalsIgnoreCase(ConstantValues.LAB_DUE)) {

			List<LaboratoryRegistration> labes = laboratoryRegistrationRepository.findByBillNo(billNo);

			PatientRegistration patientRegistration = null;
			for (LaboratoryRegistration lab : labes) {
				patientRegistration = lab.getLaboratoryPatientRegistration();
				System.out.println(modeOfPayment);
				lab.setPaymentType(modeOfPayment);
				lab.setPaid(paid);
				laboratoryRegistrationRepository.save(lab);

				if (patientRegistration != null
						&& (patientRegistration.getPatientType().getpType().equalsIgnoreCase(ConstantValues.INPATIENT)||
								patientRegistration.getPatientType().getpType().equalsIgnoreCase(ConstantValues.EMERGENCY)||
								patientRegistration.getPatientType().getpType().equalsIgnoreCase(ConstantValues.DAYCARE))) {
					ChargeBill chargeBillLab = (lab.getLabServices().getServiceType().equalsIgnoreCase("Lab"))
							? chargeBillServiceImpl.findByLabId(lab)
							: chargeBillServiceImpl.findByServiceId(lab.getLabServices());

					if (chargeBillLab != null) {
						chargeBillLab.setPaid(paid);
						chargeBillLab.setPaymentType(modeOfPayment);
						chargeBillRepository.save(chargeBillLab);
					}
				}

			}
			FinalBilling finalBilling = finalBillingRepository.paymentUpdate(ConstantValues.LAB_DUE, billNo, date);
			if(finalDue!=0) {
				 finalBilling.setDueStatus(ConstantValues.YES);
			 }else {
				 finalBilling.setDueStatus(ConstantValues.NO);
			 }
			finalBilling.setCardAmount(Math.round(finalCard));
			finalBilling.setCashAmount(Math.round(finalCash));
			finalBilling.setChequeAmount(Math.round(finalCheque));
			finalBilling.setDueAmount(finalDue);
			finalBilling.setPaymentType(modeOfPayment);

			finalBillingServcieImpl.computeSave(finalBilling);
		}

		else if (dueType.equalsIgnoreCase(ConstantValues.OSP_DUE)) {

			List<OspService> ospinfo = ospServiceRepository.findByBillNo(billNo);

			for (OspService osp : ospinfo) {

				osp.setPaymentType(modeOfPayment);
				osp.setPaid(paid);
				ospServiceRepository.save(osp);
			}

			FinalBilling finalBilling = finalBillingRepository.paymentUpdate(ConstantValues.OSP_DUE, billNo, date);
			if(finalDue!=0) {
				 finalBilling.setDueStatus(ConstantValues.YES);
			 }else {
				 finalBilling.setDueStatus(ConstantValues.NO);
			 }
			finalBilling.setCardAmount(Math.round(finalCard));
			finalBilling.setCashAmount(Math.round(finalCash));
			finalBilling.setChequeAmount(Math.round(finalCheque));
			finalBilling.setDueAmount(finalDue);
			finalBilling.setPaymentType(modeOfPayment);

			finalBillingServcieImpl.computeSave(finalBilling);

		} else if (dueType.equalsIgnoreCase("Sales Return")) {
			List<SalesReturn> salesReturns = salesReturnRepository.payUpdate(billNo, date);

			for (SalesReturn salesReturnsInfo : salesReturns) {
				salesReturnsInfo.setPaymentType(modeOfPayment);
				salesReturnRepository.save(salesReturnsInfo);

			}

			FinalBilling finalBilling = finalBillingRepository.paymentUpdate("Sales Return", billNo, date);
			if(finalDue!=0) {
				 finalBilling.setDueStatus(ConstantValues.YES);
			 }else {
				 finalBilling.setDueStatus(ConstantValues.NO);
			 }
			finalBilling.setCardAmount(Math.round(finalCard));
			finalBilling.setCashAmount(Math.round(finalCash));
			finalBilling.setChequeAmount(Math.round(finalCheque));
			finalBilling.setDueAmount(finalDue);
			finalBilling.setPaymentType(modeOfPayment);

			finalBillingServcieImpl.computeSave(finalBilling);

		} else if (dueType.equalsIgnoreCase("Ip Sales Return")) {
			List<SalesReturn> salesReturns = salesReturnRepository.payUpdate(billNo, date);

			for (SalesReturn salesReturnsInfo : salesReturns) {
				salesReturnsInfo.setPaymentType(modeOfPayment);
				salesReturnRepository.save(salesReturnsInfo);

			}

			FinalBilling finalBilling = finalBillingRepository.paymentUpdate("Ip Sales Return", billNo, date);
			if(finalDue!=0) {
				 finalBilling.setDueStatus(ConstantValues.YES);
			 }else {
				 finalBilling.setDueStatus(ConstantValues.NO);
			 }
			finalBilling.setCardAmount(Math.round(finalCard));
			finalBilling.setCashAmount(Math.round(finalCash));
			finalBilling.setChequeAmount(Math.round(finalCheque));
			finalBilling.setDueAmount(finalDue);
			finalBilling.setPaymentType(modeOfPayment);

			finalBillingServcieImpl.computeSave(finalBilling);

		}

	}

	
	

}
