package com.example.test.testingHMS.patient.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.test.testingHMS.patient.model.PatientDetails;
import com.example.test.testingHMS.patient.model.PatientRegistration;
import com.example.test.testingHMS.user.model.User;



@Repository
public interface PatientRegistrationRepository extends CrudRepository<PatientRegistration,Long>

{
	@Query(value=	"select * from second.v_patient_registration_f where p_type  not like '%INPATIENT%' and p_type  not like '%OUTPATIENT%'  order by reg_date desc ",nativeQuery=true)
	List<PatientRegistration> findExceptInpatientandOutpatient();
	
	
	@Query(value="select * from second.v_patient_registration_f where p_type='INPATIENT' order by reg_date asc ",nativeQuery=true)
	List<PatientRegistration> expectOutPatientAllDays();
	
	@Query(value="select * from second.v_patient_registration_f where p_type='OUTPATIENT' order by reg_date asc",nativeQuery=true)
	List<PatientRegistration> allOutPatientDays();
	
	List<PatientRegistration> getAllByRegId(String id);
	
	@Query(value="select * from second.v_patient_registration_f where reg_date like %:year%",nativeQuery=true)
	List<PatientRegistration> getpatAccordingToYear(@Param("year") String year);
	
	
	@Query(value="select * from second.v_patient_registration_f where year(reg_date)=:year and month(reg_date)=:month and user_id=:userId",nativeQuery=true)
	List<PatientRegistration> getpatAccordingToYearMYAndUserId(@Param("month") String month,@Param("year") String year,@Param("userId") String userId);
	
	@Query(value="select * from second.v_patient_registration_f where year(reg_date)=:year and month(reg_date)=:month ",nativeQuery=true)
	List<PatientRegistration> getpatAccordingToYearandMonth(@Param("month") String month,@Param("year") String year);

	
	@Query(value="select * from second.v_patient_registration_f where reg_date>=(SELECT min(reg_date) FROM second.v_patient_registration_f) and reg_date<=(SELECT MAX(reg_date) FROM second.v_patient_registration_f)",nativeQuery=true)
	List<PatientRegistration> getPatientWiseIpCount();

	@Query(value="select * from second.v_patient_registration_f where reg_date>=(SELECT min(reg_date) FROM second.v_patient_registration_f) and reg_date<=(SELECT MAX(reg_date) FROM second.v_patient_registration_f) and user_id=:userId",nativeQuery=true)
	List<PatientRegistration> getPatientWiseOpCount(@Param("userId")String userId);

@Query(value="select * from second.v_patient_registration_f where reg_date like %:year% and user_id=:userId",nativeQuery=true)
	List<PatientRegistration> getpatAccordingToYearAndUserId(@Param("year") String year,@Param("userId") String userId);
	
	PatientRegistration findFirstByOrderByRegIdDesc();
	
	List<PatientRegistration> findByPatientDetails(PatientDetails patientDetails);
	
	//Inner join for patient Registration/details
	@Query(value="SELECT * FROM second.v_patient_registration_f "
			+ "INNER JOIN second.v_patient_details_d "
			+ "ON second.v_patient_registration_f.p_id=second.v_patient_details_d.patient_id "
			+ "where  second.v_patient_registration_f.p_type='INPATIENT' "
			+ "and second.v_patient_details_d.discharged not like '%Yes%';",nativeQuery=true)
	List<PatientRegistration> findOnlyInpatient();
	
	@Query(value="select * from second.v_patient_registration_f where year(reg_date)=:year and month(reg_date)=:month and user_id=:userId",nativeQuery=true)
	List<PatientRegistration> getPatientCountMonthWise(@Param("year") String year,@Param("month") String month,@Param("userId") String userId);
	
	@Query(value="select * from second.v_patient_registration_f where p_id=:pid",nativeQuery=true)
	List<PatientRegistration> getRegids(@Param("pid") Long pid );
	
	@Query(value="SELECT * FROM second.v_patient_registration_f where user_id=:userId " ,nativeQuery=true)
	List<PatientRegistration> findPatientCount(@Param("userId") String userId);
	
	@Query(value="select * from second.v_patient_registration_f where reg_date>=:fromDate AND reg_date<=:toDate",nativeQuery=true)
	List<PatientRegistration> findByDate(@Param("fromDate") Object fromDate, @Param("toDate") Object toDate);
	
	
	@Query(value="select * from second.v_patient_registration_f "
			+ "where reg_date = (SELECT MAX(reg_date) FROM second.v_patient_registration_f WHERE second.v_patient_registration_f.p_id = :pid) and second.v_patient_registration_f.p_id =:pid",nativeQuery=true)
	PatientRegistration findLatestReg(@Param("pid") Long pid);
	
	PatientRegistration findByRegId(String id);
	

	@Query(value="select * from second.v_patient_registration_f where p_reg_id like %:prefix% order by p_reg_id desc limit 1",nativeQuery=true)
	PatientRegistration findByRegIdIpOp(@Param("prefix") String prefix);
	
	@Query(value="SELECT * FROM second.v_patient_registration_f where user_id=:userId and reg_date like %:date% and p_type=:pType" ,nativeQuery=true)
	List<PatientRegistration> findPatientListByConsultant(@Param("userId") String userId,@Param("date") String date,@Param("pType") String pType);
	
	List<PatientRegistration> findByPType( String pType);
	
	List<PatientRegistration> findAll();
	
	List<PatientRegistration> findByVuserD(User user);
	

	@Query(value="select * from second.v_patient_registration_f where reg_date>=:time and p_type=:type",nativeQuery=true)
	List<PatientRegistration> findPatient(@Param("time") String time,@Param("type") String type);
	
	@Query(value="select * from second.v_patient_registration_f where reg_date>=:time and p_type <> :type",nativeQuery=true)
	List<PatientRegistration> findOutPatient(@Param("time") String time,@Param("type") String type);
	
	@Query(value="select * from second.v_patient_registration_f where p_type not like '%OUTPATIENT%'",nativeQuery=true)
	List<PatientRegistration> expectOutPatient();
	
	@Query(value="select * from second.v_patient_registration_f where p_type='INPATIENT' and created_at>=:twoDayBack and created_at<=:today order by reg_date asc",nativeQuery=true)
	List<PatientRegistration> expectOutPatientTwoDays(@Param("twoDayBack") String twoDayBack,@Param("today") String today);

	@Query(value="select * from second.v_patient_registration_f where p_type='OUTPATIENT' and created_at>=:twoDayBack and created_at<=:today order by reg_date asc",nativeQuery=true)
	List<PatientRegistration> onlyOutPatientTwoDays(@Param("twoDayBack") String twoDayBack,@Param("today") String today);

	
	
	@Query(value="SELECT * FROM second.v_patient_registration_f where p_id=:pId and reg_date like %:date%",nativeQuery=true)
	PatientRegistration patientAlredyExists(@Param("pId") long pId,@Param("date") String date);
	
	@Query(value="select * from second.v_patient_registration_f where reg_date>=:fromDate AND reg_date<=:toDate AND created_by=:uId",nativeQuery=true)
	List<PatientRegistration> findUserWiseIpOpDetailed(@Param("fromDate") Object fromDate, @Param("toDate") Object toDate, @Param("uId") String uId);
	
	@Query(value="select * from second.v_patient_registration_f where user_id=:userId",nativeQuery=true)
	List<PatientRegistration> findNewPatient(@Param("userId") String userId);
	
	
	// For whatsapp msg
	@Query(value="select * from second.v_patient_registration_f where reg_date like %:date% and p_type <>'OUTPATIENT' and p_type <>'INPATIENT' and user_id=:userId",nativeQuery=true)
	List<PatientRegistration> getPatientWiseOtherCount(@Param("userId")String userId,@Param("date") String date);
	// For whatsapp msg
    @Query(value="select * from second.v_patient_registration_f where reg_date like %:date%  and p_type='OUTPATIENT' and user_id=:userId",nativeQuery=true)
	List<PatientRegistration> getPatientWiseopCount(@Param("userId")String userId,@Param("date") String date);
    // For whatsapp msg
    @Query(value="select * from second.v_patient_registration_f where reg_date like %:date% and p_type='INPATIENT' and user_id=:userId",nativeQuery=true)
	List<PatientRegistration> getPatientWiseipCount(@Param("userId")String userId,@Param("date") String date);
    
    List<PatientRegistration> findByRegDateGreaterThanEqualAndRegDateLessThanEqual(Timestamp t,Timestamp o);
    
    @Query(value="select * from second.v_patient_registration_f where p_type  not like '%INPATIENT%' and p_type  not like '%OUTPATIENT%' and created_at>=:twoDayBack and created_at<=:today order by reg_date desc",nativeQuery=true)
   	List<PatientRegistration> onlyOtherPatientTwoDays(@Param("twoDayBack") String twoDayBack,@Param("today") String today);

   	
   	@Query(value="select * from second.v_patient_registration_f where p_type  not like '%INPATIENT%' and p_type  not like '%OUTPATIENT%' order by reg_date asc",nativeQuery=true)
   	List<PatientRegistration> allOtherPatientDays();
   	
   	//for sms of stats
   	
   	
   	
}
