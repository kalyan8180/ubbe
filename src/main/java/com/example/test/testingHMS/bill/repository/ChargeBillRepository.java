package com.example.test.testingHMS.bill.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.test.testingHMS.bill.model.ChargeBill;
import com.example.test.testingHMS.laboratory.model.LabServices;
import com.example.test.testingHMS.laboratory.model.LaboratoryRegistration;
import com.example.test.testingHMS.patient.model.PatientRegistration;
import com.example.test.testingHMS.pharmacist.model.Sales;

@Repository
public interface ChargeBillRepository extends CrudRepository<ChargeBill,String> {
	
	@Query(value="select * FROM second.v_charge_bill_f WHERE pat_reg_id IN (SELECT second.v_patient_registration_f.p_reg_id  FROM second.v_patient_registration_f WHERE second.v_patient_registration_f.p_type='inpatient')",nativeQuery=true)
	List<ChargeBill> findOnlyIp();
	
	ChargeBill findFirstByOrderByChargeBillIdDesc();
	
	ChargeBill findByLabId(LaboratoryRegistration laboratoryRegistration);
	
	@Query(value="SELECT * FROM second.v_charge_bill_f where service_id is not null;",nativeQuery=true)
	List<ChargeBill> getAllServices();
	
	@Query(value="SELECT * FROM second.v_charge_bill_f where Email is not null;",nativeQuery=true)
	List<ChargeBill> getAllServicesNull();
	
	ChargeBill findBySaleId(Sales sale);
	
	ChargeBill findByServiceIdAndPaymentType(LabServices service,String paymentType);
	
	ChargeBill findBySaleIdAndPaymentType(Sales sales,String paymentType);
	
	ChargeBill findByLabIdAndPaymentType(LaboratoryRegistration lab,String paymentType);
	
	
	ChargeBill findByChargeBillId(String id);
	
	List<ChargeBill> findByPatRegIdAndNetAmountNot(PatientRegistration patientRegistration,float amt);
	
	List<ChargeBill> findByPatRegId(PatientRegistration patientRegistration);
	
	// discharge and not-discharge patient
	@Query(value="SELECT * FROM second.v_charge_bill_f where pat_reg_id=:regId and dicharged_date is not null",nativeQuery=true)
	List<ChargeBill> findByPatRegIdAndDischarged(@Param("regId") String regId);
	
	// discharge and not-discharge patient
	@Query(value = "SELECT * FROM second.v_charge_bill_f where pat_reg_id=:regId and dicharged_date is null", nativeQuery = true)
	List<ChargeBill> findByPatRegIdAndNotDischarged(@Param("regId") String regId);

	
	@Query(value="SELECT * FROM second.v_charge_bill_f where pat_reg_id=:regId",nativeQuery=true)
	List<ChargeBill> findByPatRegIds(@Param("regId") String regId);
	
	
	@Query(value="SELECT * FROM second.v_charge_bill_f where pat_reg_id=:regId and net_amount!=0",nativeQuery=true)
	List<ChargeBill> findByPatRegIdsIp(@Param("regId") String regId);
	
	@Query(value="SELECT * FROM second.v_charge_bill_f where pat_reg_id=:regId and service_id is not null",nativeQuery=true)
	List<ChargeBill> findByPatRegIdsAndServiceId(@Param("regId") String regId);
	
	List<ChargeBill> findByPatRegIdAndPaid(PatientRegistration patientRegistration,String paid);
	
	@Query(value="SELECT * FROM second.v_charge_bill_f where pat_reg_id=:regId and paid=:status",nativeQuery=true)
	List<ChargeBill> findByPatRegIdStatus(@Param("regId") String regId,@Param("status") String status);
	
	@Query(value="select * from second.v_charge_bill_f where pat_reg_id=:regId and paid='NO' and lab_id is NOT NULL",nativeQuery=true)
	List<ChargeBill> findAllLab(@Param("regId") String regId);
	
	@Query(value="select max(bill_no) from second.v_charge_bill_f",nativeQuery=true)
	String findMaxBill();
	
	@Query(value="select * from second.v_charge_bill_f where pat_reg_id=:regId and payment_type='due' and sale_id is not null",nativeQuery=true)
	List<ChargeBill> findDueBill(@Param("regId") String regId);
	
	List<ChargeBill> findByPatRegIdAndIpSettledFlag(PatientRegistration patientRegistration,String ipSettledFlag);
	

}
